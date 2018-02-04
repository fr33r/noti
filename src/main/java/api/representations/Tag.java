package api.representations;

public class Tag {

	private final String name;
	
	public Tag() {
		this.name = null;
	}
	
	public Tag(final String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
}
