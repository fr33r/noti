import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import io.opentracing.util.GlobalTracer;
import io.opentracing.Tracer;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.google.common.collect.ImmutableList;
import com.uber.jaeger.context.TracingUtils;
import com.uber.jaeger.dropwizard.Configuration;
import com.uber.jaeger.dropwizard.JaegerFeature;

import api.representations.RepresentationFactory;
import api.representations.JSONRepresentationFactory;
import api.representations.SirenRepresentationFactory;
import api.representations.XMLRepresentationFactory;
import api.filters.CacheControlFilter;
import api.filters.ConditionalGetFilter;
import api.filters.ConditionalPutFilter;
import api.filters.MetadataDeleteFilter;
import api.filters.MetadataGetFilter;
import api.filters.MetadataPostFilter;
import api.filters.MetadataPutFilter;
import api.filters.VaryFilter;
import api.resources.AudienceResource;
import api.resources.NotificationResource;
import api.resources.TargetResource;

import application.services.AudienceService;
import application.services.NotificationService;
import application.services.TargetService;

import configuration.DatabaseConfiguration;
import configuration.NotiConfiguration;
import configuration.SMSConfiguration;

import domain.Audience;
import domain.AudienceSQLFactory;
import domain.AudienceFactory;
import domain.EntitySQLFactory;
import domain.Notification;
import domain.NotificationFactory;
import domain.NotificationSQLFactory;
import domain.Target;
import domain.TargetFactory;
import domain.TargetSQLFactory;

import infrastructure.EntityTagService;
import infrastructure.MySQLUnitOfWorkFactory;
import infrastructure.RepositoryFactory;
import infrastructure.ResourceMetadataService;
import infrastructure.SQLRepositoryFactory;
import infrastructure.SQLUnitOfWorkFactory;
import infrastructure.ShortMessageService;
import infrastructure.TwilioShortMessageService;

import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.metrics.MetricsFactory;
import io.dropwizard.metrics.ReporterFactory;
import io.dropwizard.metrics.graphite.GraphiteReporterFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.util.Duration;

public class Noti extends Application<NotiConfiguration> {

	@Override
	public String getName(){
		return "noti";
	}

	@Override
	public void initialize(Bootstrap<NotiConfiguration> bootstrap) {
		bootstrap.setConfigurationSourceProvider(
			new SubstitutingSourceProvider(
				bootstrap.getConfigurationSourceProvider(),
				new EnvironmentVariableSubstitutor()
			)
		);
	}

	private void initializeTracing(Configuration jaegerConfiguration, Environment environment) {
		JaegerFeature jaegerFeature = new JaegerFeature(jaegerConfiguration);
		environment.jersey().register(jaegerFeature);

		Tracer tracer = jaegerConfiguration.getTracer();
		GlobalTracer.register(tracer);
		TracingUtils.setTracer(tracer);
		environment.jersey().register(new AbstractBinder() {

			@Override
			protected void configure() {
				this.bind(tracer).to(Tracer.class);
			}
		});
	}

	private void initializeGraphiteReporter(NotiConfiguration configuration, Environment environment) {
		MetricRegistry graphiteMetricRegistry = new MetricRegistry();
		environment.jersey().register(new AbstractBinder() {

			@Override
			protected void configure() {
				this.bind(graphiteMetricRegistry).to(MetricRegistry.class);
			}
		});

		// read metrics configuration values.
		MetricsFactory metricsFactory = configuration.getMetricsFactory();
		ImmutableList<ReporterFactory> reporterFactories = metricsFactory.getReporters();
		GraphiteReporterFactory graphiteReporterFactory =
			(GraphiteReporterFactory)reporterFactories.get(0);
		String graphiteHost = graphiteReporterFactory.getHost();
		int graphitePort = graphiteReporterFactory.getPort();
		TimeUnit rateTimeUnit = graphiteReporterFactory.getRateUnit();
		TimeUnit durationTimeUnit = graphiteReporterFactory.getDurationUnit();
		String prefix = graphiteReporterFactory.getPrefix();
		Duration metricsFrequency = metricsFactory.getFrequency();
		Optional<Duration> graphiteReporterFrequency =
			graphiteReporterFactory.getFrequency();
		Duration frequency =
			graphiteReporterFrequency.isPresent() ? graphiteReporterFrequency.get() : metricsFrequency;

		// build reporter.
		final Graphite graphite = new Graphite(new InetSocketAddress(graphiteHost, graphitePort));
		final GraphiteReporter reporter =
			GraphiteReporter.forRegistry(graphiteMetricRegistry)
				.prefixedWith(prefix)
				.convertRatesTo(rateTimeUnit)
				.convertDurationsTo(durationTimeUnit)
				.filter(MetricFilter.ALL)
				.build(graphite);
		reporter.start(frequency.getQuantity(), frequency.getUnit());

		// wire up jaeger to metrics registry.
		configuration.getJaegerConfiguration().setMetricRegistry(graphiteMetricRegistry);
	}

	private void initializeDatabase(DatabaseConfiguration databaseConfiguration, Environment environment) throws FlywayException {

		final String username = databaseConfiguration.getUser();
		final String password = databaseConfiguration.getPassword();
		final String host = databaseConfiguration.getHost();
		final int port = databaseConfiguration.getPort();
		final String databaseName = databaseConfiguration.getName();
		final boolean useLegacyDatetimeCode = databaseConfiguration.getUseLegacyDatetimeCode();
		final boolean useSSL = databaseConfiguration.getUseSSL();
		final String urlf = "jdbc:mysql://%s:%s/%s?useLegacyDatetimeCode=%b&useSSL=%b";
		final String url =
			String.format(urlf, host, port, databaseName, useLegacyDatetimeCode, useSSL);

		Flyway flyway = new Flyway();
		flyway.setInstalledBy(username);
		flyway.setDataSource(url, username, password);
		try {
			flyway.migrate();
		} catch (FlywayException ex) {
			flyway.repair();
			flyway.migrate();
		}

		environment.jersey().register(new AbstractBinder() {

			@Override
			protected void configure() {
				this.bind(url).to(String.class).named("JDBC_URL");
				this.bind(username).to(String.class).named("JDBC_USERNAME");
				this.bind(password).to(String.class).named("JDBC_PASSWORD");
			}
		});
	}

	private void initializeShortMessageService(SMSConfiguration smsConfiguration, Environment environment){
		final String smsAccountSID = smsConfiguration.getAccountSID();
		final String smsAuthToken = smsConfiguration.getAuthToken();

		environment.jersey().register(new AbstractBinder() {

			@Override
			protected void configure() {
				this.bind(smsAccountSID).to(String.class).named("SMS_ACCOUNT_SID");
				this.bind(smsAuthToken).to(String.class).named("SMS_AUTH_TOKEN");
			}
		});
		environment.jersey().register(new AbstractBinder() {

			@Override
			protected void configure() {
				this.bind(TwilioShortMessageService.class).to(ShortMessageService.class);
			}
		});
	}

	private void initializeResources(NotiConfiguration configuration, Environment environment){
		environment.jersey().register(NotificationResource.class);
		environment.jersey().register(AudienceResource.class);
		environment.jersey().register(TargetResource.class);
	}

	private void initializeFilters(NotiConfiguration configuration, Environment environment) {
		environment.jersey().register(CacheControlFilter.class);
		environment.jersey().register(ConditionalGetFilter.class);
		environment.jersey().register(ConditionalPutFilter.class);
		environment.jersey().register(VaryFilter.class);
		environment.jersey().register(MetadataGetFilter.class);
		environment.jersey().register(MetadataDeleteFilter.class);
		environment.jersey().register(MetadataPostFilter.class);
		environment.jersey().register(MetadataPutFilter.class);
	}

	@Override
	public void run(NotiConfiguration configuration, Environment environment) throws Exception {
		this.initializeResources(configuration, environment);
		this.initializeFilters(configuration, environment);
		this.initializeTracing(configuration.getJaegerConfiguration(), environment);
		this.initializeShortMessageService(configuration.getSMSConfiguration(), environment);
		this.initializeDatabase(configuration.getDatabaseConfiguration(), environment);
		this.initializeGraphiteReporter(configuration, environment);

		environment.jersey().register(new AbstractBinder() {

			@Override
			protected void configure() {
				this.bind(NotificationService.class).to(application.NotificationService.class);
			}
		});

		environment.jersey().register(new AbstractBinder() {

			@Override
			protected void configure() {
				this.bind(TargetService.class).to(application.TargetService.class);
			}
		});

		environment.jersey().register(new AbstractBinder() {

			@Override
			protected void configure() {
				this.bind(AudienceService.class).to(application.AudienceService.class);
			}
		});

		environment.jersey().register(new AbstractBinder() {

			@Override
			protected void configure() {
				this.bind(SQLRepositoryFactory.class).to(RepositoryFactory.class);
			}
		});

		environment.jersey().register(new AbstractBinder() {

			@Override
			protected void configure() {
				this.bind(NotificationSQLFactory.class)
					.named("NotificationSQLFactory")
					.to(new TypeLiteral<EntitySQLFactory<Notification, UUID>>(){});

				this.bind(TargetSQLFactory.class)
					.named("TargetSQLFactory")
					.to(new TypeLiteral<EntitySQLFactory<Target, UUID>>(){});

				this.bind(AudienceSQLFactory.class)
					.named("AudienceSQLFactory")
					.to(new TypeLiteral<EntitySQLFactory<Audience, UUID>>(){});
			}
		});

		environment.jersey().register(new AbstractBinder() {

			@Override
			protected void configure() {
				this.bind(MySQLUnitOfWorkFactory.class).to(SQLUnitOfWorkFactory.class);
			}
		});

		environment.jersey().register(new AbstractBinder() {

			@Override
			protected void configure() {
				this.bind(infrastructure.services.ResourceMetadataService.class).to(ResourceMetadataService.class);
			}
		});

		environment.jersey().register(new AbstractBinder() {

			@Override
			protected void configure() {
				this.bind(infrastructure.services.EntityTagService.class).to(EntityTagService.class);
			}
		});

		environment.jersey().register(new AbstractBinder() {

			@Override
			protected void configure() {
				this.bindAsContract(application.NotificationFactory.class);
			}
		});

		environment.jersey().register(new AbstractBinder() {

			@Override
			protected void configure() {
				this.bindAsContract(application.TargetFactory.class);
			}
		});

		environment.jersey().register(new AbstractBinder() {

			@Override
			protected void configure() {
				this.bindAsContract(application.AudienceFactory.class);
			}
		});

		environment.jersey().register(new AbstractBinder() {

			@Override
			protected void configure() {
				this.bindAsContract(NotificationFactory.class);
			}
		});

		environment.jersey().register(new AbstractBinder() {

			@Override
			protected void configure() {
				this.bindAsContract(TargetFactory.class);
			}
		});

		environment.jersey().register(new AbstractBinder() {

			@Override
			protected void configure() {
				this.bindAsContract(AudienceFactory.class);
			}
		});

		environment.jersey().register(new AbstractBinder() {

			@Override
			protected void configure() {
				this.bind(JSONRepresentationFactory.class)
					.named("JSONRepresentationFactory")
					.to(RepresentationFactory.class);
			}
		});

		environment.jersey().register(new AbstractBinder() {

			@Override
			protected void configure() {
				this.bind(XMLRepresentationFactory.class)
					.named("XMLRepresentationFactory")
					.to(RepresentationFactory.class);
			}
		});

		environment.jersey().register(new AbstractBinder() {

			@Override
			protected void configure() {
				this.bind(SirenRepresentationFactory.class)
					.named("SirenRepresentationFactory")
					.to(RepresentationFactory.class);
			}
		});

	}

	public static void main(String[] args) throws Exception {
		new Noti().run(args);
	}
}
