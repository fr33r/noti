package infrastructure;

import domain.EntitySQLFactory;
import domain.Target;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a {@link Target} collection.
 *
 * @author Jon Freer
 */
public final class TargetRepository extends SQLRepository implements Repository<Target, UUID> {

  private final EntitySQLFactory<Target, UUID> targetFactory;
  private final Tracer tracer;

  /**
   * Constructs a new {@link TargetRepository}.
   *
   * @param unitOfWork The unit of work that this repository will contribute to.
   * @param targetFactory The factory that reconstitutes {@link Target} entities.
   * @param tracer The tracer conforming to the OpenTracing standard utilized for instrumentation.
   */
  public TargetRepository(
      UnitOfWork unitOfWork, EntitySQLFactory<Target, UUID> targetFactory, Tracer tracer) {
    super(unitOfWork);

    this.targetFactory = targetFactory;
    this.tracer = tracer;
  }

  /**
   * Retrieves the target matching the provided {@link Query}.
   *
   * @param query The {@link Query} to match against.
   * @return The collection target matching the provided {@link Query}.
   */
  @Override
  public Set<Target> get(Query<Target> query) {
    String className = TargetRepository.class.getName();
    String spanName = String.format("%s#get(query)", className);
    Span span = this.tracer.buildSpan(spanName).asChildOf(this.tracer.activeSpan()).start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      return query.execute();
    } finally {
      span.finish();
    }
  }

  /**
   * Retrieves the {@link Target} identified by the universally unique identifier provided.
   *
   * @param uuid The universally unique identifier of the {@link Target} to retrieve.
   * @return The {@link Target} with the universally unique identifier provided.
   */
  @Override
  public Target get(UUID uuid) {
    String className = TargetRepository.class.getName();
    String spanName = String.format("%s#get(uuid)", className);
    Span span = this.tracer.buildSpan(spanName).asChildOf(this.tracer.activeSpan()).start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      DataMapper dm = this.getUnitOfWork().dataMappers().get(Target.class);
      return (Target) dm.find(uuid);
    } finally {
      span.finish();
    }
  }

  /**
   * Places the {@link Target} provided into the repository. In the event the {@link Target}
   * provided already exists in the repository, the prexisting one will be replaced with the one
   * provided.
   *
   * @param target The target to put into the repository.
   */
  @Override
  public void put(Target target) {
    String className = TargetRepository.class.getName();
    String spanName = String.format("%s#put", className);
    Span span = this.tracer.buildSpan(spanName).asChildOf(this.tracer.activeSpan()).start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      Target existingTarget = this.get(target.getId());
      if (existingTarget == null) {
        this.add(target);
      } else {
        this.getUnitOfWork().alter(target);
      }
    } finally {
      span.finish();
    }
  }

  /**
   * Adds the provided {@link Target} into the repository.
   *
   * @param target The target to add into the repository.
   */
  @Override
  public void add(Target target) {
    String className = TargetRepository.class.getName();
    String spanName = String.format("%s#add", className);
    Span span = this.tracer.buildSpan(spanName).asChildOf(this.tracer.activeSpan()).start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      this.getUnitOfWork().add(target);
    } finally {
      span.finish();
    }
  }

  /**
   * Removes the {@link Target} with the universally unique identifier provided.
   *
   * @param uuid The universally unique identifier of the {@link Target} to retrieve.
   */
  @Override
  public void remove(UUID uuid) {
    String className = TargetRepository.class.getName();
    String spanName = String.format("%s#remove", className);
    Span span = this.tracer.buildSpan(spanName).asChildOf(this.tracer.activeSpan()).start();
    try (Scope scope = this.tracer.scopeManager().activate(span, false)) {
      Target target = this.get(uuid);
      if (target != null) {
        this.getUnitOfWork().remove(target);
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
    String className = TargetRepository.class.getName();
    String spanName = String.format("%s#size", className);
    final Span span = this.tracer.buildSpan(spanName).asChildOf(this.tracer.activeSpan()).start();
    try (final Scope scope = this.tracer.scopeManager().activate(span, false)) {
      DataMapper dm = this.getUnitOfWork().dataMappers().get(Target.class);
      return dm.count();
    } finally {
      span.finish();
    }
  }
}
