package api.filters;

import infrastructure.RepresentationMetadata;
import infrastructure.RepresentationMetadataService;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;

@Provider
public class ConditionalPutFilter extends RequestFilter {

  private RepresentationMetadataService representationMetadataService;
  private final Tracer tracer;
  private final Logger logger;

  @Inject
  public ConditionalPutFilter(
      RepresentationMetadataService representationMetadataService,
      Tracer tracer,
      @Named("api.filters.ConditionalPutFilter") Logger logger) {
    this.representationMetadataService = representationMetadataService;
    this.tracer = tracer;
    this.logger = logger;
  }

  public void filter(RequestContext requestContext) throws IOException {
    Span span =
        this.tracer
            .buildSpan("ConditionalPutFilter#filter")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {

      // gaurd: do not proceed if the HTTP method is not PUT.
      if (!requestContext.methodIs("PUT")) return;

      // gaurd: do not proceed if there is not value for the Content-Type HTTP header.
      MediaType contentType = requestContext.getMediaType();
      if (contentType == null) {
        this.logger.info("Unable to perform conditional request without 'Content-Type' header.");
        return;
      }

      URI contentLocation = requestContext.getRequestUri();
      Locale language = requestContext.getLanguage();
      List<String> encodings = requestContext.getEncodings();
      String encodingString = encodings == null ? null : String.join(",", encodings);
      RepresentationMetadata representationMetadata =
          this.representationMetadataService.get(
              new URI(contentLocation.getPath()), language, encodingString, contentType);

      if (representationMetadata != null) {

        this.logger.info("Discovered representation metadata match.");
        this.logger.debug(representationMetadata.toString());

        ResponseBuilder responseBuilder =
            requestContext
                .getRequest()
                .evaluatePreconditions(
                    representationMetadata.getLastModified(),
                    representationMetadata.getEntityTag());

        if (responseBuilder != null) {
          requestContext.abortWith(responseBuilder.build());
        }
      } else {
        this.logger.info("No match for representation metadata found.");
      }
    } catch (Exception x) {
      this.logger.error("Encountered an issue when retrieving representation metadata.", x);
    } finally {
      span.finish();
    }
  }
}
