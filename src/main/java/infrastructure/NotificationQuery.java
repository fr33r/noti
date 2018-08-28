package infrastructure;

import domain.Notification;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Represents a {@link Query} that retrieves a {@link Notification} collection matching its
 * criteria.
 *
 * @author Jon Freer
 */
public final class NotificationQuery extends Query<Notification> {

  private final NotificationDataMapper notificationDataMapper;

  /**
   * Constructs a new {@link NotificationQuery}.
   *
   * @param notificationDataMapper The data mapper responsible for mapping the {@link Notification}
   *     domain objects to the database.
   */
  public NotificationQuery(NotificationDataMapper notificationDataMapper) {
    super();

    this.notificationDataMapper = notificationDataMapper;
  }

  @Override
  public List<DataMap> getDataMaps() {
    List<DataMap> dataMaps = new ArrayList<>();
    dataMaps.add(new NotificationMetadata().getDataMap());
    dataMaps.add(new MessageMetadata().getDataMap());
    return dataMaps;
  }

  /**
   * {@inheritDoc}
   *
   * @return The {@link Notification} collection matching the {@link Query}.
   */
  @Override
  public Set<Notification> execute() {

    QueryExpression expression = this.getQueryExpression();
    String condition = expression != null ? expression.interpret() : null;
    String orderBy =
        this.getOrderByExpression() != null ? this.getOrderByExpression().interpret() : null;
    String skip = this.getSkipExpression() != null ? this.getSkipExpression().interpret() : null;
    String limit = this.getLimitExpression() != null ? this.getLimitExpression().interpret() : null;

    return this.notificationDataMapper.find(
        condition, orderBy, skip, limit, this.getQueryArguments());
  }
}
