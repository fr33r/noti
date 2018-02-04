import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.uber.jaeger.dropwizard.Configuration;
import com.uber.jaeger.dropwizard.JaegerFeature;

import api.filters.CacheControlFilter;
import api.filters.ConditionalGetFilter;
import api.filters.ConditionalPutFilter;
import api.filters.VaryFilter;
import api.resources.NotificationResource;
import application.services.NotificationService;
import configuration.DatabaseConfiguration;
import configuration.NotiConfiguration;
import configuration.SMSConfiguration;
import domain.EntitySQLFactory;
import domain.Notification;
import domain.NotificationFactory;
import domain.NotificationSQLFactory;
import domain.Target;
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
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import mappers.Mapper;
import mappers.NotificationMapper;


public class Noti extends Application<NotiConfiguration> {

	@Override
	public String getName(){
		return "noti";
	}

	@Override
	public void initialize(Bootstrap<NotiConfiguration> bootstrap) {
		// strapping...
	}

	private void initializeTracing(Configuration jaegerConfiguration, Environment environment) {
		JaegerFeature jaegerFeature = new JaegerFeature(jaegerConfiguration);
		environment.jersey().register(jaegerFeature);
	}

	private void initializeGraphiteReporter(NotiConfiguration configuration, Environment environment) {
		MetricRegistry graphiteMetricRegistry = new MetricRegistry();
		environment.jersey().register(new AbstractBinder() {

			@Override
			protected void configure() {
				this.bind(graphiteMetricRegistry).to(MetricRegistry.class);
			}		
		});
		
		final Graphite graphite = new Graphite(new InetSocketAddress("localhost", 2003));
		final GraphiteReporter reporter =
			GraphiteReporter.forRegistry(graphiteMetricRegistry)
				//.prefixedWith("web1.example.com")
				.convertRatesTo(TimeUnit.SECONDS)
				.convertDurationsTo(TimeUnit.MILLISECONDS)
				.filter(MetricFilter.ALL)
				.build(graphite);
		reporter.start(1, TimeUnit.SECONDS);
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
	}
	
	private void initializeFilters(NotiConfiguration configuration, Environment environment) {
		environment.jersey().register(CacheControlFilter.class);
		environment.jersey().register(ConditionalGetFilter.class);
		environment.jersey().register(ConditionalPutFilter.class);
		environment.jersey().register(VaryFilter.class);
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
				this.bind(NotificationMapper.class)
					.to(new TypeLiteral<Mapper<domain.Notification, api.representations.Notification>>(){});
			}		
		});
		
		environment.jersey().register(new AbstractBinder() {

			@Override
			protected void configure() {
				this.bindAsContract(NotificationFactory.class);
			}		
		});
	}

	public static void main(String[] args) throws Exception {
		new Noti().run(args);
	}
}
