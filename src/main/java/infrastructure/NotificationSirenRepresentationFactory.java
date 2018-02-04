/*package noti.infrastructure;

import noti.domain.Notification;
import noti.infrastructure.SirenRepresentation;
import siren.factories.BuilderFactory;
import siren.factories.
import siren.LinkBuilder;
import siren.ActionBuilder;
import siren.EntityBuilder;

public class NotificationSirenRepresentationFactory implements SirenRepresentationFactory<Notification>{

	private EntityBuilderFactory entityBuilderFactory;
	private LinkBuilderFactory linkBuilderFactory;
	private ActionBuilderFactory actionBuilderFactory;

	public NotificationSirenRepresentationFactory(
		BuilderFactory<Entity.Builder> entityBuilderFactory,
		BuilderFactory<Link.Builder> linkBuilderFactory,
		BuilderFactory<Action.Builder> actionBuilderFactory
	){
		this.entityBuilderFactory = entityBuilderFactory;
		this.linkBuilderFactory = linkBuilderFactory;
		this.actionBuilderFactory = actionBuilderFactory;
	}
	//potentially these methods could turn into the factory method pattern. (define them on an abstract class and override them when desired).
	public Link createLink(URI href, String relation, String type, String title){ 
		return this.linkBuilderFactory
			.create()
			.href(href)
			.rel(relation)
			.type(type)
			.title(title)
			.build();
	}

	public Link createSelfLink(URI href, String type, String title) {
		return this.createLink(href, Relation.SELF, type, title);
	}

	public EmbeddedLinkSubEntity createEmbeddedLinkSubEntity(String klass, String href, String title, String relation){
		EmbeddedLinkSubEntity subEntity = 
			embeddedLinkSubEntityBuilderFactory
				.create()
				.klass(klass)
				.href(href)
				.title(title)
				.rel(relation)
				.build();
		return subEntity;
	} 

	public Entity create(Notification notification){

		Entity.Builder entityBuilder = this.entityBuilderFactory.create();
		Link.Builder linkBuilder = this.linkBuilderFactory.create();
		Action.Builder actionBuilder = this.actionBuilderFactory.create();
		EmbeddedLinkSubEntity.Builder embeddedLinkSubEntityBuilder = this.embeddedLinkSubEntityBuilderFactory.create();
		EmbeddedRepresentationSubEntity.Builder embeddedRepresentationSubEntityBuilder = 
			this.embeddedRepresentationSubEntityBuilderFactory.create();

		Link selfLink = 
			this.createSelfLink(String.format("http://freer.ddns.net/api/notifications/%s", notification.getId()), "application/json", "self"); 

		entityBuilder = entityBuilder
			.links(selfLink)
			.property("uuid", notification.getId())
			.property("message", notification.getMessage())
			.property("method", notification.getMethod())
			.property("sendAt", notification.getSendAt())
			.property("sentAt", notification.getSentAt())
			.property("status", notification.getStatus());

		if(notification instanceof TargetNotification){
			TargetNotification targetNotification = (TargetNotification)notification;
			for (Target target : targetNotification.getTargets()) {
				this.createEmbeddedLinkSubEntity("target", String.format("http://freer.ddns.net/api/target/%s", target.getId()), "target", Relation.ITEM);
				entityBuilder.subEntity(subEntity);
			}
		} else if(notification instanceof TagNotification){
			TagNotification tagNotification = (TagNotification)notification;
			entityBuilder.property("tags", tagNotification.getTags());
		}

		return entityBuilder.build();
	}
}*/
