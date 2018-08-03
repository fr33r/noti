import api.representations.RepresentationFactory;
import api.representations.json.JSONRepresentationFactory;
import api.representations.siren.SirenRepresentationFactory;
import api.representations.xml.XMLRepresentationFactory;
import configuration.NotiConfiguration;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public final class RepresentationModule extends NotiModule {

  private static final String JSON_REPRESENTATION_FACTORY = "JSONRepresentationFactory";
  private static final String XML_REPRESENTATION_FACTORY = "XMLRepresentationFactory";
  private static final String SIREN_REPRESENTATION_FACTORY = "SirenRepresentationFactory";

  public RepresentationModule(NotiConfiguration configuration, Environment environment) {
    super(configuration, environment);
  }

  @Override
  public void configure() {

    // register representation factories with environment.
    this.getEnvironment()
        .jersey()
        .register(
            new AbstractBinder() {
              @Override
              protected void configure() {
                this.bind(JSONRepresentationFactory.class)
                    .named(JSON_REPRESENTATION_FACTORY)
                    .to(RepresentationFactory.class);
                this.bind(XMLRepresentationFactory.class)
                    .named(XML_REPRESENTATION_FACTORY)
                    .to(RepresentationFactory.class);
                this.bind(SirenRepresentationFactory.class)
                    .named(SIREN_REPRESENTATION_FACTORY)
                    .to(RepresentationFactory.class);
              }
            });
  }
}
