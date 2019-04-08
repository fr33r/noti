package infrastructure;

import domain.EntitySQLFactory;
import domain.Template;
import infrastructure.query.Query;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a {@link Template} collection.
 *
 * @author Jon Freer
 */
public final class TemplateRepository extends SQLRepository implements Repository<Template, UUID> {

  private final EntitySQLFactory<Template, UUID> templateFactory;
  private final Tracer tracer;

  /**
   * Constructs a new {@link TemplateRepository}.
   *
   * @param unitOfWork The unit of work that this repository will contribute to.
   * @param templateFactory The factory that reconstitutes {@link Template} entities.
   * @param tracer The tracer conforming to the OpenTracing standard utilized for instrumentation.
   */
  public TemplateRepository(
      UnitOfWork unitOfWork, EntitySQLFactory<Template, UUID> templateFactory, Tracer tracer) {
    super(unitOfWork);

    this.templateFactory = templateFactory;
    this.tracer = tracer;
  }

  /**
   * Retrieves the template matching the provided {@link Query}.
   *
   * @param query The {@link Query} to match against.
   * @return The collection template matching the provided {@link Query}.
   */
  @Override
  public Set<Template> get(Query<Template> query) {
    String className = TemplateRepository.class.getName();
    String spanName = String.format("%s#get(query)", className);
    Span span = this.tracer.buildSpan(spanName).asChildOf(this.tracer.activeSpan()).start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      return query.execute();
    } finally {
      span.finish();
    }
  }

  /**
   * Retrieves the {@link Template} identified by the universally unique identifier provided.
   *
   * @param uuid The universally unique identifier of the {@link Template} to retrieve.
   * @return The {@link Template} with the universally unique identifier provided.
   */
  @Override
  public Template get(UUID uuid) {
    String className = TemplateRepository.class.getName();
    String spanName = String.format("%s#get(uuid)", className);
    Span span = this.tracer.buildSpan(spanName).asChildOf(this.tracer.activeSpan()).start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      DataMapper dm = this.getUnitOfWork().dataMappers().get(Template.class);
      return (Template) dm.find(uuid);
    } finally {
      span.finish();
    }
  }

  /**
   * Places the {@link Template} provided into the repository. In the event the {@link Template}
   * provided already exists in the repository, the prexisting one will be replaced with the one
   * provided.
   *
   * @param template The template to put into the repository.
   */
  @Override
  public void put(Template template) {
    String className = TemplateRepository.class.getName();
    String spanName = String.format("%s#put", className);
    Span span = this.tracer.buildSpan(spanName).asChildOf(this.tracer.activeSpan()).start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      Template existingTemplate = this.get(template.getId());
      if (existingTemplate == null) {
        this.add(template);
      } else {
        this.getUnitOfWork().alter(template);
      }
    } finally {
      span.finish();
    }
  }

  /**
   * Adds the provided {@link Template} into the repository.
   *
   * @param template The template to add into the repository.
   */
  @Override
  public void add(Template template) {
    String className = TemplateRepository.class.getName();
    String spanName = String.format("%s#add", className);
    Span span = this.tracer.buildSpan(spanName).asChildOf(this.tracer.activeSpan()).start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      this.getUnitOfWork().add(template);
    } finally {
      span.finish();
    }
  }

  /**
   * Removes the {@link Template} with the universally unique identifier provided.
   *
   * @param uuid The universally unique identifier of the {@link Template} to retrieve.
   */
  @Override
  public void remove(UUID uuid) {
    String className = TemplateRepository.class.getName();
    String spanName = String.format("%s#remove", className);
    Span span = this.tracer.buildSpan(spanName).asChildOf(this.tracer.activeSpan()).start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      Template template = this.get(uuid);
      if (template != null) {
        this.getUnitOfWork().remove(template);
      }
    } finally {
      span.finish();
    }
  }

  /**
   * Retrieves the number of notifications within the repository.
   *
   * @return The number of notifications within the repository.
   */
  @Override
  public int size() {
    String className = TemplateRepository.class.getName();
    String spanName = String.format("%s#size", className);
    final Span span = this.tracer.buildSpan(spanName).asChildOf(this.tracer.activeSpan()).start();
    try (final Scope scope = this.tracer.scopeManager().activate(span, false)) {
      DataMapper dm = this.getUnitOfWork().dataMappers().get(Template.class);
      return dm.count();
    } finally {
      span.finish();
    }
  }
}
