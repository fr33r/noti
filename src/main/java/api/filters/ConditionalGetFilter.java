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
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;

@Provider
public class ConditionalGetFilter extends RequestFilter {

  private final RepresentationMetadataService representationMetadataService;
  private final Tracer tracer;
  private final Logger logger;

  @Inject
  public ConditionalGetFilter(
      RepresentationMetadataService representationMetadataService, Tracer tracer, Logger logger) {
    this.representationMetadataService = representationMetadataService;
    this.tracer = tracer;
    this.logger = logger;
  }

  public void filter(RequestContext requestContext) throws IOException {
    Span span =
        this.tracer
            .buildSpan("ConditionalGetFilter#filter")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {

      // gaurd: do not proceed if HTTP method is not GET.
      if (!requestContext.methodIs("GET")) return;

      // gaurd: do not proceed if no values were provided for the Accept HTTP header.
      List<MediaType> acceptableMediaTypes = requestContext.getAcceptableMediaTypes();
      if (acceptableMediaTypes.size() < 1) {
        this.logger.info("Unable to perform conditional request without 'Accept' header.");
        return;
      }

      URI contentLocation = requestContext.getRequestUri();
      List<Locale> acceptableLanguages = requestContext.getAcceptableLanguages();
      List<String> acceptableEncodings = requestContext.getAcceptableEncodings();

      // search for a match for all values of accept; use first match.
      // gotta think about how to perform this search...
      // its possible for there to be multiple values for the following:
      //
      // - Accept
      // - Accept-Language
      // - Accept-Encoding
      //
      // SELECT
      //  *
      // FROM
      //  REPRESENTATION_METADATA
      // WHERE
      //  CONTENT_LOCATION = ?
      //  AND CONTENT_TYPE IN (?)
      //  AND CONTENT_LANGUAGE IN (?)
      //  AND CONTENT_ENCODING IN (?);
      RepresentationMetadata representationMetadata = null;

	  this.logger.debug(String.format("Content Location: %s", contentLocation));
	  this.logger.debug(String.format("Acceptable Languages: %s", acceptableLanguages));
	  this.logger.debug(String.format("Acceptable Encodings: %s", acceptableEncodings));
	  this.logger.debug(String.format("Acceptable Media Types: %s", acceptableMediaTypes));
      List<RepresentationMetadata> matches =
          this.representationMetadataService.match(
              contentLocation, acceptableMediaTypes, acceptableLanguages, acceptableEncodings);

      // naively takes the first match, but should validate that the route
      // matching alogrithm within jersey matches this logic. i want to make sure
      // that i don't retreive the wrong metadata.
      // NOTE - i am not even sure if jersey supports any different routing based on
      // language or encoding. if it doesn't we should be good here.
      this.logger.info(
          String.format("Discovered %d representation metadata match(es).", matches.size()));
      if (matches.size() > 0) {
        representationMetadata = matches.get(0);
      }

      if (representationMetadata != null) {
        ResponseBuilder responseBuilder =
            requestContext
                .getRequest()
                .evaluatePreconditions(
                    representationMetadata.getLastModified(),
                    representationMetadata.getEntityTag());

        if (responseBuilder != null) {
          // https://tools.ietf.org/html/rfc7232#section-4.1
          responseBuilder.header(
              HttpHeaders.CONTENT_TYPE, representationMetadata.getContentType().toString());
          responseBuilder.tag(representationMetadata.getEntityTag());
          responseBuilder.lastModified(representationMetadata.getLastModified());
          Response response = responseBuilder.build();

          requestContext.abortWith(response);
        }
      }
    } catch (Exception x) {
      this.logger.error("Encountered an issue when persisting representation metadata.", x);
    } finally {
      span.finish();
    }
  }
}
