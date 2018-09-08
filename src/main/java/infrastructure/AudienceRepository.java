package infrastructure;

import domain.Audience;
import domain.EntitySQLFactory;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a {@link Audience} collection.
 *
 * @author Jon Freer
 */
public final class AudienceRepository extends SQLRepository implements Repository<Audience, UUID> {

  private final EntitySQLFactory<Audience, UUID> audienceFactory;
  private final AudienceDataMapper audienceDataMapper;
  private final Tracer tracer;

  /**
   * Constructs an instance of {@link AudienceRepository}.
   *
   * @param unitOfWork The unit of work that this repository will contribue to.
   * @param audienceFactory The factory that reconstitutes {@link Audience} entities.
   * @param audienceDataMapper The data mapper that maps {@link Audience} entities to the database.
   * @param tracer The tracer conforming to the OpenTracing standard utilized for instrumentation.
   */
  public AudienceRepository(
      SQLUnitOfWork unitOfWork,
      EntitySQLFactory<Audience, UUID> audienceFactory,
      AudienceDataMapper audienceDataMapper,
      Tracer tracer) {
    super(unitOfWork);

    this.audienceFactory = audienceFactory;
    this.audienceDataMapper = audienceDataMapper;
    this.tracer = tracer;
  }

  /**
   * Retrieves the audiences matching the provided {@link Query}.
   *
   * @param query The {@link Query} to match against.
   * @return The collection audiences matching the provided {@link Query}.
   */
  @Override
  public Set<Audience> get(Query<Audience> query) {
    final Span span =
        this.tracer
            .buildSpan("AudienceRepostory#get(query)")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (final Scope scope = this.tracer.scopeManager().activate(span, false)) {
      return query.execute();
    } finally {
      span.finish();
    }
  }

  /**
   * Retrieves the {@link Audience} identified by the universally unique identifier provided.
   *
   * @param uuid The universally unique identifier of the {@link Audience} to retrieve.
   * @return The {@link Audience} with the universally unique identifier provided.
   */
  @Override
  public Audience get(final UUID uuid) {
    final Span span =
        this.tracer
            .buildSpan("AudienceRepository#get(uuid)")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (final Scope scope = this.tracer.scopeManager().activate(span, false)) {
      return this.audienceDataMapper.find(uuid);
    } finally {
      span.finish();
    }
  }

  /**
   * Places the {@link Audience} provided into the repository. In the event the {@link Audience}
   * provided already exists in the repository, the prexisting one will be replaced with the one
   * provided.
   *
   * @param audience The audience to put into the repository.
   */
  @Override
  public void put(final Audience audience) {
    final Span span =
        this.tracer.buildSpan("AudienceRepository#put").asChildOf(this.tracer.activeSpan()).start();
    try (final Scope scope = this.tracer.scopeManager().activate(span, false)) {
      Audience existingAudience = this.get(audience.getId());
      if (existingAudience == null) {
        this.add(audience);
      } else {
        this.audienceDataMapper.update(audience);
      }
    } finally {
      span.finish();
    }
  }

  /**
   * Adds the provided {@link Audience} into the repository.
   *
   * @param audience The audience to add into the repository.
   */
  @Override
  public void add(final Audience audience) {
    final Span span =
        this.tracer.buildSpan("AudienceRepository#add").asChildOf(this.tracer.activeSpan()).start();
    try (final Scope scope = this.tracer.scopeManager().activate(span, false)) {
      this.audienceDataMapper.insert(audience);
    } finally {
      span.finish();
    }
  }

  /**
   * Removes the {@link Audience} with the universally unique identifier provided.
   *
   * @param uuid The universally unique identifier of the {@link Audience} to retrieve.
   */
  @Override
  public void remove(final UUID uuid) {
    final Span span =
        this.tracer.buildSpan("AudienceRepository#add").asChildOf(this.tracer.activeSpan()).start();
    try (final Scope scope = this.tracer.scopeManager().activate(span, false)) {
      this.audienceDataMapper.delete(uuid);
    } finally {
      span.finish();
    }
  }

  /**
   * Retrieves the number of audiences within the repository.
   *
   * @return The number of audiences within the repository.
   */
  @Override
  public int size() {
    final Span span =
        this.tracer
            .buildSpan("AudienceRepository#size")
            .asChildOf(this.tracer.activeSpan())
            .start();
    try (final Scope scope = this.tracer.scopeManager().activate(span, false)) {
      return this.audienceDataMapper.count();
    } finally {
      span.finish();
    }
  }
}
