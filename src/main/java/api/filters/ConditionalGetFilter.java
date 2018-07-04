package api.filters;

import infrastructure.RepresentationMetadata;
import infrastructure.RepresentationMetadataService;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.Provider;

@Provider
public class ConditionalGetFilter extends RequestFilter {

  private final RepresentationMetadataService representationMetadataService;
  private final Tracer tracer;

  @Inject
  public ConditionalGetFilter(
      RepresentationMetadataService representationMetadataService, Tracer tracer) {
    this.representationMetadataService = representationMetadataService;
    this.tracer = tracer;
  }

  public void filter(RequestContext requestContext) throws IOException {
    Span span =
        this.tracer
            .buildSpan("ConditionalGetFilter#filter")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      Request request = requestContext.getRequest();

      if (request.getMethod().equalsIgnoreCase("GET")) {

        List<MediaType> acceptableMediaTypes = requestContext.getAcceptableMediaTypes();
        if (acceptableMediaTypes.size() < 1) {
          return;
        }

        URI contentLocation = requestContext.getUriInfo().getRequestUri();
        List<Locale> acceptableLanguages = requestContext.getAcceptableLanguages();
        String acceptEncodingHeaderValue =
            requestContext.getHeaderString(HttpHeaders.CONTENT_LOCATION);
        List<String> acceptableEncodings = new ArrayList<>();

        if (acceptEncodingHeaderValue != null) {
          String[] parts = acceptEncodingHeaderValue.split(",");
          for (String part : parts) {
            acceptableEncodings.add(part);
          }
        }

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

        List<RepresentationMetadata> matches =
            this.representationMetadataService.match(
                contentLocation, acceptableMediaTypes, acceptableLanguages, acceptableEncodings);

        // naively takes the first match, but should validate that the route
        // matching alogrithm within jersey matches this logic. i want to make sure
        // that i don't retreive the wrong metadata.
        // NOTE - i am not even sure if jersey supports any different routing based on
        // language or encoding. if it doesn't we should be good here.
        if (matches.size() > 0) {
          representationMetadata = matches.get(0);
        }

        if (representationMetadata != null) {
          ResponseBuilder responseBuilder =
              request.evaluatePreconditions(
                  representationMetadata.getLastModified(), representationMetadata.getEntityTag());

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
      }
    } finally {
      span.finish();
    }
  }
}
