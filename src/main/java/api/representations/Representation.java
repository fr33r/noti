package api.representations;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.ws.rs.core.MediaType;

public class Representation {

	private final MediaType mediaType;

	public Representation(final MediaType mediaType) {
		this.mediaType = mediaType;
	}

	@JsonIgnore
	public MediaType getMediaType() {
		return this.mediaType;
	}
}
