package domain;

public class Tag extends ValueObject implements Cloneable{

	private final String name;

	public Tag(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || this.getClass() != obj.getClass()) { return false; }
		return this.equals((Tag)obj);
	}

	public boolean equals(Tag tag) {
		if (tag == null || this.getClass() != tag.getClass()) { return false; }
		return this.name.equals(tag.name);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder
			.append("[")
			.append("name=").append(this.name)
			.append("]");
		return builder.toString();
	}

	@Override
	public Object clone() {

		Tag tag = null;

		try {
			tag = (Tag)super.clone();
		} catch (CloneNotSupportedException ex) {
			//not possible.
		}

		return tag;
	}
}
