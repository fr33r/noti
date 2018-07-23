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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Variant;
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

      // debug.
      this.logger.debug(String.format("Content Location: %s", contentLocation));
      this.logger.debug(String.format("Acceptable Languages: %s", acceptableLanguages));
      this.logger.debug(String.format("Acceptable Encodings: %s", acceptableEncodings));
      this.logger.debug(String.format("Acceptable Media Types: %s", acceptableMediaTypes));

      // get all representation metadata by content location.
      List<RepresentationMetadata> representationMetadata =
          this.representationMetadataService.getAll(new URI(contentLocation.getPath()));
      this.logger.info(
          String.format(
              "Retrieved %d metadata entries for '%s'.",
              representationMetadata.size(), contentLocation.getPath()));

      List<MediaType> metadataMediaTypes = new ArrayList<>();
      List<Locale> metadataLanguages = new ArrayList<>();
      List<String> metadataEncodings = new ArrayList<>();

      for (RepresentationMetadata metadata : representationMetadata) {
        metadataMediaTypes.add(metadata.getContentType());

        if (metadata.getContentLanguage() == null) {
          metadataLanguages.add(metadata.getContentLanguage());
        }

        if (metadata.getContentEncoding() == null) {
          metadataEncodings.add(metadata.getContentEncoding());
        }
      }

      Variant.VariantListBuilder vb = Variant.VariantListBuilder.newInstance();
      List<Variant> variants =
          vb.mediaTypes(metadataMediaTypes.toArray(new MediaType[metadataMediaTypes.size()]))
              .languages(metadataLanguages.toArray(new Locale[metadataLanguages.size()]))
              .encodings(metadataEncodings.toArray(new String[metadataEncodings.size()]))
              .add()
              .build();
      this.logger.info(String.format("Generated %d varient combinations.", variants.size()));
      this.logger.info(variants.toString());

      RepresentationMetadata match = null;
      Variant optimal = null;
      while (variants.size() > 0) {

        // determine the most optimal variant.
        optimal = requestContext.getRequest().selectVariant(variants);
        if (optimal == null) {
          this.logger.info("No optimal variants available.");
          break;
        }

        // check if the most optimal variant is present in representation metadata.
        for (RepresentationMetadata metadata : representationMetadata) {
          boolean hasSameMediaType =
              optimal.getMediaType() == null && metadata.getContentType() == null
                  || optimal.getMediaType().equals(metadata.getContentType());
          boolean hasSameLanguage =
              optimal.getLanguage() == null && metadata.getContentLanguage() == null
                  || optimal.getLanguage().equals(metadata.getContentLanguage());
          boolean hasSameEncoding =
              optimal.getEncoding() == null && metadata.getContentEncoding() == null
                  || optimal.getEncoding().equals(metadata.getContentEncoding());

          if (hasSameMediaType && hasSameLanguage && hasSameEncoding) {
            match = metadata;
            break;
          }
        }

        // if so, we are done.
        if (match != null) {
          this.logger.info("Discovered representation metadata match.");
          this.logger.info(match.toString());
          break;
        }

        variants.remove(optimal);
      }

      if (match != null) {
        ResponseBuilder responseBuilder =
            requestContext
                .getRequest()
                .evaluatePreconditions(match.getLastModified(), match.getEntityTag());

        if (responseBuilder != null) {
          // https://tools.ietf.org/html/rfc7232#section-4.1
          responseBuilder.header(HttpHeaders.CONTENT_TYPE, match.getContentType().toString());
          responseBuilder.tag(match.getEntityTag());
          responseBuilder.lastModified(match.getLastModified());
          responseBuilder.variants(optimal);
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
