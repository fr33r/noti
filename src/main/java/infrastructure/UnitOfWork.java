package infrastructure;

import domain.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class UnitOfWork {

  private final List<Entity> added;
  private final List<Entity> removed;
  private final List<Entity> altered;

  public UnitOfWork() {
    this.added = new ArrayList<>();
    this.removed = new ArrayList<>();
    this.altered = new ArrayList<>();
  }

  public void add(Entity entity) {
    if (!this.added.contains(entity)) {
      this.added.add(entity);
    }
  }

  List<Entity> added() {
    List<Entity> copy = new ArrayList<>();
    for (Entity entity : this.added) {
      copy.add(entity);
    }
    return copy;
  }

  public void remove(Entity entity) {
    if (!this.removed.contains(entity)) {
      this.removed.add(entity);
    }
  }

  List<Entity> removed() {
    List<Entity> copy = new ArrayList<>();
    for (Entity entity : this.removed) {
      copy.add(entity);
    }
    return copy;
  }

  public void alter(Entity entity) {
    if (!this.altered.contains(entity)) {
      this.altered.add(entity);
    }
  }

  List<Entity> altered() {
    List<Entity> copy = new ArrayList<>();
    for (Entity entity : this.altered) {
      copy.add(entity);
    }
    return copy;
  }

  public abstract void save();

  public abstract Map<Class, DataMapper> dataMappers();
}
