package api.filters;

import infrastructure.RepresentationMetadata;
import infrastructure.RepresentationMetadataService;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

public abstract class MetadataFilter implements ContainerResponseFilter {

  private final RepresentationMetadataService representationMetadataService;
  private final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

  @Inject
  public MetadataFilter(RepresentationMetadataService representationMetadataService) {
    this.representationMetadataService = representationMetadataService;
  }

  @Override
  public abstract void filter(
      ContainerRequestContext requestContext, ContainerResponseContext responseContext);

  public boolean isPostRequest(ContainerRequestContext requestContext) {
    return this.requestMethodEquals(requestContext, "POST");
  }

  public boolean isGetRequest(ContainerRequestContext requestContext) {
    return this.requestMethodEquals(requestContext, "GET");
  }

  public boolean isPutRequest(ContainerRequestContext requestContext) {
    return this.requestMethodEquals(requestContext, "PUT");
  }

  public boolean isDeleteRequest(ContainerRequestContext requestContext) {
    return this.requestMethodEquals(requestContext, "DELETE");
  }

  public boolean isPatchRequest(ContainerRequestContext requestContext) {
    return this.requestMethodEquals(requestContext, "PATCH");
  }

  public boolean isHeadRequest(ContainerRequestContext requestContext) {
    return this.requestMethodEquals(requestContext, "HEAD");
  }

  public boolean requestMethodEquals(ContainerRequestContext requestContext, String method) {
    return requestContext.getRequest().getMethod().equalsIgnoreCase(method);
  }

  public boolean isErrorResponse(ContainerResponseContext responseContext) {
    return responseContext.getStatus() >= 400;
  }

  public boolean isNotModifiedResponse(ContainerResponseContext responseContext) {
    return this.responseStatusCodeEquals(responseContext, 304);
  }

  public boolean isPreconditionFailedResponse(ContainerResponseContext responseContext) {
    return this.responseStatusCodeEquals(responseContext, 412);
  }

  public boolean responseStatusCodeEquals(
      ContainerResponseContext responseContext, int statusCode) {
    return responseContext.getStatus() == statusCode;
  }

  public URI getRequestUri(ContainerRequestContext requestContext) {
    return requestContext.getUriInfo().getRequestUri();
  }

  public MediaType getResponseMediaType(ContainerResponseContext responseContext) {
    return responseContext.getMediaType();
  }

  public MediaType getRequestMediaType(ContainerRequestContext requestContext) {
    return requestContext.getMediaType();
  }

  public List<MediaType> getRequestAcceptableMediaTypes(ContainerRequestContext requestContext) {
    return requestContext.getAcceptableMediaTypes();
  }

  public List<Locale> getRequestAcceptableLanguages(ContainerRequestContext requestContext) {
    return requestContext.getAcceptableLanguages();
  }

  public Locale getRequestLanguage(ContainerRequestContext requestContext) {
    return requestContext.getLanguage();
  }

  public Locale getResponseLanguage(ContainerResponseContext responseContext) {
    return responseContext.getLanguage();
  }

  public List<String> getRequestAcceptableEncodings(ContainerRequestContext requestContext) {
    return requestContext.getHeaders().get(HttpHeaders.ACCEPT_ENCODING);
  }

  public String getRequestEncodingAsString(ContainerRequestContext requestContext) {
    return requestContext.getHeaderString(HttpHeaders.CONTENT_ENCODING);
  }

  public String getResponseEncoding(ContainerResponseContext responseContext) {
    return responseContext.getHeaderString(HttpHeaders.CONTENT_ENCODING);
  }

  RepresentationMetadataService getRepresentationMetadataService() {
    return this.representationMetadataService;
  }

  RepresentationMetadata getMetadata(
      URI location, MediaType mediaType, Locale language, String encoding) {
    RepresentationMetadata metadata =
        this.representationMetadataService.get(location, language, encoding, mediaType);
    return metadata;
  }

  /**
   * Removes all representation metadata associated with a URI.
   *
   * @param location The URI that identifies the representations to be deleted.
   */
  void deleteMetadata(URI location) {
    this.representationMetadataService.removeAll(location);
  }

  /*
   * GET
   *
   * 'put' (replace) the metadata based on the response, because the representation will be in the response.
   *
   * POST
   *
   * can't participate in caching activities - there is no guarentee that the request or response has the representation.
   *
   * PUT
   *
   * 'put' (replace) the metadata based on the request, because the representation will be in the request.
   *
   * DELETE (DONE)
   *
   * remove all resource metadata.
   */

  /** Stores the resource metadata provided. */
  void putMetadata(
      URI location,
      MediaType mediaType,
      Locale language,
      String encoding,
      Date lastModified,
      EntityTag entityTag) {
    this.representationMetadataService.put(
        new RepresentationMetadata(
            location, mediaType, language, encoding, lastModified, entityTag));

    // responseContext.getHeaders().add(HttpHeaders.LAST_MODIFIED, lastModified);
    // responseContext.getHeaders().add(HttpHeaders.ETAG, entityTag);
  }
}
