package api.representations.siren;

import api.representations.Representation;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import siren.Action;
import siren.Entity;
import siren.EntityBase;
import siren.Link;

//this class use object adapter pattern.
//mapping between Entity and Representation interfaces.
public final class SirenEntityRepresentation extends Representation {

	private final Entity sirenEntity;

	public SirenEntityRepresentation() {
		super(new MediaType("application", "vnd.siren+json"));
		this.sirenEntity = null;
	}
	
	public SirenEntityRepresentation(Entity sirenEntity) {
		super(new MediaType("application", "vnd.siren+json"));
		this.sirenEntity = sirenEntity;
	}

	@JsonProperty("class")
	public List<String> getKlass() {
		return this.sirenEntity.getKlass();
	}

	public String getTitle() {
		return this.sirenEntity.getTitle();
	}

	public Map<String, Object> getProperties() {
		return this.sirenEntity.getProperties();
	}

	public List<Action> getActions() {
		return this.sirenEntity.getActions();
	}

	public List<Link> getLinks() {
		return this.sirenEntity.getLinks();
	}

	public List<EntityBase> getEntities() {
		return this.sirenEntity.getEntities();
	}
}