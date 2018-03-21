package api.representations;

import api.representations.Audience;
import api.representations.Notification;
import api.representations.Representation;
import api.representations.Target;
import api.representations.siren.SirenEntityRepresentation;

import java.net.URISyntaxException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import siren.Action;
import siren.EmbeddedLinkSubEntity;
import siren.Entity;
import siren.Field;
import siren.FieldType;
import siren.HttpMethod;
import siren.Link;
import siren.Relation;
import siren.factories.ActionBuilderFactory;
import siren.factories.EmbeddedLinkSubEntityBuilderFactory;
import siren.factories.EntityBuilderFactory;
import siren.factories.FieldBuilderFactory;
import siren.factories.LinkBuilderFactory;

public final class SirenRepresentationFactory extends RepresentationFactory {

	private final LinkBuilderFactory linkBuilderFactory;
	private final EntityBuilderFactory entityBuilderFactory;
	private final ActionBuilderFactory actionBuilderFactory;
	private final FieldBuilderFactory fieldBuilderFactory;
	private final EmbeddedLinkSubEntityBuilderFactory embeddedLinkSubEntityBuilderFactory;

	public SirenRepresentationFactory() {
		super(new MediaType("application", "vnd.siren+json"));
		this.linkBuilderFactory = new LinkBuilderFactory();
		this.entityBuilderFactory = new EntityBuilderFactory();
		this.actionBuilderFactory = new ActionBuilderFactory();
		this.fieldBuilderFactory = new FieldBuilderFactory();
		this.embeddedLinkSubEntityBuilderFactory =
			new EmbeddedLinkSubEntityBuilderFactory();
	}

	@Override
	public Representation createNotificationRepresentation(UriInfo uriInfo, Notification notification) {
		Link.Builder linkBuilder = this.linkBuilderFactory.create();
		
		Link self = null;
		try {
			self =
				linkBuilder
					.rel(Relation.SELF)
					.href(uriInfo.getRequestUri())
					.build();
		} catch (URISyntaxException x){}

		Action.Builder actionBuilder = this.actionBuilderFactory.create();
		Action delete =
			actionBuilder
				.name("delete-notification")
				.title("Delete Notification")
				.method(HttpMethod.DELETE)
				.href(uriInfo.getRequestUri())
				.build();

		EmbeddedLinkSubEntity.Builder embeddedLinkSubEntityBuilder =
			this.embeddedLinkSubEntityBuilderFactory.create();

		Entity.Builder entityBuilder = this.entityBuilderFactory.create();
		EmbeddedLinkSubEntity targetCollectionSubEntity = null;
		EmbeddedLinkSubEntity audienceCollectionSubEntity = null;

		try {
			//create target collection entity.
			targetCollectionSubEntity =
				embeddedLinkSubEntityBuilder
					.klasses("target", "collection")
					.title("Target Collection")
					.rel(Relation.COLLECTION)
					.href(
						UriBuilder
							.fromUri(uriInfo.getRequestUri())
							.replacePath("/targets/")
							.build()
					)
					.build();
		} catch (URISyntaxException x){}

		embeddedLinkSubEntityBuilder.clear();

		try {
			//create audience collection entity.
			audienceCollectionSubEntity =
				embeddedLinkSubEntityBuilder
					.klasses("audience", "collection")
					.title("Audience Collection")
					.rel(Relation.COLLECTION)
					.href(
						UriBuilder
							.fromUri(uriInfo.getRequestUri())
							.replacePath("/audiences/")
							.build()
					)
					.build();
		} catch (URISyntaxException x){}

		Entity entity =
			entityBuilder
				.klass("notification")
				.property("uuid", notification.getUUID())
				.property("content", notification.getContent())
				.property("sendAt", notification.getSendAt())
				.property("sentAt", notification.getSentAt())
				.property("status", notification.getStatus().toString())
				.link(self)
				.actions(delete)
				.subEntities(audienceCollectionSubEntity, targetCollectionSubEntity)
				.build();

		return new api.representations.siren.SirenEntityRepresentation(entity);
	}

	@Override
	public Representation createAudienceRepresentation(UriInfo uriInfo, Audience audience) {
		Link.Builder linkBuilder = this.linkBuilderFactory.create();
		
		Link self = null;
		try {
			self =
				linkBuilder
					.rel(Relation.SELF)
					.href(uriInfo.getRequestUri())
					.build();
		} catch (URISyntaxException x){}

		Action.Builder actionBuilder = this.actionBuilderFactory.create();
		Action delete =
			actionBuilder
				.name("delete-audience")
				.title("Delete Audience")
				.method(HttpMethod.DELETE)
				.href(uriInfo.getRequestUri())
				.build();

		actionBuilder.clear();

		Field.Builder<String> stringFieldBuilder = this.fieldBuilderFactory.create();
		Field<String> uuidField =
			stringFieldBuilder
				.name("uuid")
				.title("Universally Unique Identifier")
				.type(FieldType.TEXT)
				.build();

		stringFieldBuilder.clear();

		Field<String> nameField =
			stringFieldBuilder
				.name("name")
				.title("Name")
				.type(FieldType.TEXT)
				.build();

		Action replace =
			actionBuilder
				.name("replace-audience")
				.title("Replace Audience")
				.method(HttpMethod.PUT)
				.href(uriInfo.getRequestUri())
				.fields(uuidField, nameField)
				.type(MediaType.APPLICATION_JSON)
				.build();

		EmbeddedLinkSubEntity.Builder embeddedLinkSubEntityBuilder =
			this.embeddedLinkSubEntityBuilderFactory.create();

		Entity.Builder entityBuilder = this.entityBuilderFactory.create();

		for(Target member : audience.getMembers()) {
			try {
				EmbeddedLinkSubEntity targetSubEntity =
					embeddedLinkSubEntityBuilder
						.klass("target")
						.title("Member")
						.rel(Relation.ITEM)
						.href(
							UriBuilder
								.fromUri(uriInfo.getRequestUri())
								.replacePath("/targets/{uuid}/")
								.build(member.getUUID())
						)
						.build();
				entityBuilder.subEntity(targetSubEntity);
				embeddedLinkSubEntityBuilder.clear();
			} catch (URISyntaxException x){}
		}

		Entity entity =
			entityBuilder
				.klass("audience")
				.property("uuid", audience.getUUID())
				.property("name", audience.getName())
				.link(self)
				.actions(delete, replace)
				.build();

		return new api.representations.siren.SirenEntityRepresentation(entity);
	}

	@Override
	public Representation createTargetRepresentation(UriInfo uriInfo, Target target) {
		Link.Builder linkBuilder = this.linkBuilderFactory.create();
		
		Link self = null;
		try {
			self =
				linkBuilder
					.rel(Relation.SELF)
					.href(uriInfo.getRequestUri())
					.build();
		} catch (URISyntaxException x){}

		Action.Builder actionBuilder = this.actionBuilderFactory.create();
		Action delete =
			actionBuilder
				.name("delete-target")
				.title("Delete Target")
				.method(HttpMethod.DELETE)
				.href(uriInfo.getRequestUri())
				.build();

		actionBuilder.clear();

		Field.Builder<String> stringFieldBuilder = this.fieldBuilderFactory.create();
		Field<String> uuidField =
			stringFieldBuilder
				.name("uuid")
				.title("Universally Unique Identifier")
				.type(FieldType.TEXT)
				.build();

		stringFieldBuilder.clear();

		Field<String> nameField =
			stringFieldBuilder
				.name("name")
				.title("Name")
				.type(FieldType.TEXT)
				.build();

		stringFieldBuilder.clear();

		Field<String> phoneNumberField =
			stringFieldBuilder
				.name("phoneNumber")
				.title("Phone Number")
				.type(FieldType.TEL)
				.build();

		Action replace =
			actionBuilder
				.name("replace-target")
				.title("Replace Target")
				.method(HttpMethod.PUT)
				.href(uriInfo.getRequestUri())
				.fields(uuidField, nameField, phoneNumberField)
				.type(MediaType.APPLICATION_JSON)
				.build();

		Entity.Builder entityBuilder = this.entityBuilderFactory.create();
		Entity entity =
			entityBuilder
				.klass("target")
				.property("uuid", target.getUUID())
				.property("phoneNumber", target.getPhoneNumber())
				.property("name", target.getName())
				.link(self)
				.actions(delete, replace)
				.build();

		// not sure what to do here yet...adapter pattern?
		return new api.representations.siren.SirenEntityRepresentation(entity);
	}
}
