package infrastructure;

import java.util.Date;
import java.net.URI;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;

/**
 * Represents metadata about a single REST resource.
 *
 * @author jonfreer
 * @since 03/26/2017
 */
public class ResourceMetadata {

    private final URI uri;
    private final MediaType contentType;
    private final Date lastModified;
    private final EntityTag entityTag;
    private final String nodeName;
    private Long revision;
    
    /**
     * Constructs a ResourceMetadata instance, provided a URI,
     * last modified date and time, and an entity tag (also referred to as an ETag).
     *
	 * @param uri The URI of the resource.
     * @param lastModified The date and time that the resource identified by the URI
     *                     was modified.
     * @param entityTag The entity tag of the resource identified by the URI.
     */
    public ResourceMetadata(
		URI uri,
		MediaType contentType,
		String nodeName,
		Long revision,
		Date lastModified,
		EntityTag entityTag
    ){

		if(uri == null) {
			throw new IllegalArgumentException("The constructor argument 'uri' cannot be null.");
		}

		if(contentType == null){
			throw new IllegalArgumentException("The constructor argument 'contentType' cannot be null.");
		}

		if(nodeName == null){
			throw new IllegalArgumentException("The constructor argument 'nodeName' cannot be null.");
		}

		if(revision == null){
			throw new IllegalArgumentException("The constructor argument 'revision' cannot be null.");
		}

		if(lastModified == null){
			throw new IllegalArgumentException("The constructor argument 'lastModified' cannot be null.");
		}

		if(entityTag == null){
			throw new IllegalArgumentException("The constructor argument 'entityTag' cannot be null.");
		}

		this.uri = uri;
		this.contentType = contentType;
		this.lastModified = (Date)lastModified.clone();
		this.entityTag = entityTag;
		this.nodeName = nodeName;
		this.revision = revision;
	}

	/**
     * Retrieves the URI identifying the resource.
     *
	 * @return The URI identifying the resource.
     */
	public URI getUri(){
			return this.uri;
	}

    /**
     * Retrieves the content type of the resource representation.
     *
	 * @return The date and time that the resource was
     * last modified.
     */
	public MediaType getContentType(){
		return this.contentType;
	}

    /**
     * Retrieves the "node name" assigned to the resource representation.
     * As per RFC7232, the best strong validators are based on the usage of
     * unique node name and revision identifiers to identify representation changes.
     *
	 * @return The node name assigned to this resource representation.
     */
	public String getNodeName() {
		return this.nodeName;
	}

    /**
     * Retrieves the revision associated with this representation.
     * Each time a resource representation is changed, a unique revision
     * is assigned.
     *
     * @return The current revision of the resource representation.
     */
	public Long getRevision() {
		return this.revision;
	}

    /**
     * Retrieves the date and time that the resource
     * was last modified.
     *
	 * @return The date and time that the resource was
     * last modified.
     */
	public Date getLastModified(){
		return this.lastModified == null ? null : (Date)this.lastModified.clone();
	}

    /**
     * Retrieves the entity tag (also referred to as an ETag)
     * for the resource representation.
     * @return The entity tag for the resource.
     */
	public EntityTag getEntityTag(){
		return this.entityTag;
	}

	public void incrementRevision() {
		this.revision++;
	}

    /**
     * Determines if a provided object is semantically
     * equivalent to the calling ResourceMetadata instance.
     * @param object An instance of Object to be compared with
     *               the calling ResourceMetadata instance.
     * @return true if the Object instance provided is an instance
     * of ResourceMetadata and is semantically equivalent to the calling
     * ResourceMetadata instance; false otherwise.
     */
	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}

		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		ResourceMetadata other = (ResourceMetadata) obj;
		boolean hasSameEntityTag = this.entityTag.equals(other.entityTag);
		boolean hasSameContentType = this.contentType.equals(other.contentType);
		boolean hasSameNodeName = this.nodeName.equals(other.nodeName);
		boolean hasSameRevision = this.revision.equals(other.revision);
		boolean hasSameLastModified = this.lastModified.equals(other.lastModified);
		boolean hasSameUri = this.uri.equals(other.uri);

		return
			hasSameEntityTag &&
			hasSameContentType &&
			hasSameLastModified &&
			hasSameUri &&
			hasSameNodeName &&
			hasSameRevision;
	}

    /**
     * Generates a hashcode representative of the current state of the
     * calling ResourceMetadata instance.
     * @return The hashcode of the calling ResourceMetadata instance.
     */
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.entityTag.hashCode();
		result = prime * result + this.contentType.hashCode();
		result = prime * result + this.lastModified.hashCode();
		result = prime * result + this.uri.hashCode();
		result = prime * result + this.nodeName.hashCode();
		result = prime * result + this.revision.hashCode();
		return result;
	}

    /**
     * Creates a string representation of the calling ResourceMetadata instance.
     * @return A string representation of the calling ResourceMetadata instance.
     */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ResourceMetadata [uri=");
		builder.append(uri);
		builder.append(", contentType=");
		builder.append(contentType);
		builder.append(", lastModified=");
		builder.append(lastModified);
		builder.append(", entityTag=");
		builder.append(entityTag);
		builder.append(", nodeName=");
		builder.append(nodeName);
		builder.append(", revision=");
		builder.append(revision);
		builder.append("]");
		return builder.toString();
	}
}
