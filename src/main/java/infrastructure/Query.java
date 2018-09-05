package infrastructure;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * Represents a generic query.
 *
 * <p>Implemented using the Query Object pattern.
 */
public abstract class Query<T> {

  private Stack<QueryExpression> expression;
  private QueryExpression orderByExpression;
  private QueryExpression limitExpression;
  private QueryExpression skipExpression;
  private List<QueryArgument> args;
  private List<DataMap> dataMaps;
  private int index;

  public static class QueryArgument<T> {

    private final int index;
    private final T value;
    private final int type;

    public QueryArgument(int index, T value, int type) {
      this.index = index;
      this.value = value;
      this.type = type;
    }

    public int getIndex() {
      return this.index;
    }

    public T getValue() {
      return this.value;
    }

    public int getType() {
      return this.type;
    }
  }

  /** Constructs a new {@link Query}. */
  public Query() {
    this.expression = new Stack<>();
    this.dataMaps = this.getDataMaps();
    this.args = new ArrayList<>();
    this.index = 0;
  }

  /**
   * Retrieves the {@link DataMap} utilized to map domain object fields to their underying
   * representations.
   *
   * @return The {@link DataMap}.
   */
  protected abstract List<DataMap> getDataMaps();

  /**
   * Constructs a {@link StringExpression} within the {@link Query}.
   *
   * @param string The string used to construct the {@link StringExpression}.
   * @return The {@link StringExpression}.
   */
  public QueryExpression string(String string) {
    this.args.add(new QueryArgument<String>(++this.index, string, Types.VARCHAR));
    QueryExpression ex = new StringExpression(string);
    ex.usePlaceholders(true);
    return ex;
  }

  /**
   * Constructs a {@link BooleanLiteralExpression} within the {@link Query}.
   *
   * @param bool The boolean literal used to construct the {@link BooleanLiteralExpression}.
   * @return The {@link BooleanLiteralExpression}.
   */
  public QueryExpression bool(boolean bool) {
    this.args.add(new QueryArgument<Boolean>(++this.index, bool, Types.BOOLEAN));
    QueryExpression ex = new BooleanLiteralExpression(bool);
    ex.usePlaceholders(true);
    return ex;
  }

  /**
   * Constructs an {@link IntegerExpression} within the {@link Query}.
   *
   * @param integer The integer used to construct the {@link IntegerExpression}.
   * @return The {@link IntegerExpression}.
   */
  public QueryExpression integer(int integer) {
    this.args.add(new QueryArgument<Integer>(++this.index, integer, Types.INTEGER));
    QueryExpression ex = new IntegerExpression(integer);
    ex.usePlaceholders(true);
    return ex;
  }

  /**
   * Constructs a {@link FloatExpression} within the {@link Query}.
   *
   * @param floatingPoint The float used to construct the {@link FloatExpression}.
   * @return The {@link FloatExpression}.
   */
  public QueryExpression floatingPoint(float floatingPoint) {
    this.args.add(new QueryArgument<Float>(++this.index, floatingPoint, Types.FLOAT));
    QueryExpression ex = new FloatExpression(floatingPoint);
    ex.usePlaceholders(true);
    return ex;
  }

  /**
   * Constructs the corresponding {@link ColumnExpression} for the provided domain object field
   * name.
   *
   * @param name The domain object field name.
   * @return The {@link ColumnExpression}.
   */
  public QueryExpression field(String name) {
    for (DataMap map : this.dataMaps) {
      String columnName = map.getColumnNameForField(name);
      if (columnName != null) {
        return new ColumnExpression(map.getTableAlias(), columnName);
      }
    }
    throw new IllegalArgumentException(
        String.format("no mapping with field name '%s' found.", name));
  }

  /**
   * Constructs an {@link EqualToExpression} between the two given operands.
   *
   * @param first The first operand.
   * @param second The second operand.
   * @return The {@link EqualToExpression}.
   */
  public QueryExpression equalTo(QueryExpression first, QueryExpression second) {
    return new EqualToExpression(first, second);
  }

  /**
   * Constructs a {@link LessThanExpression} between the two given operands.
   *
   * @param first The first operand.
   * @param second The second operand.
   * @return The {@link LessThanExpression}.
   */
  public QueryExpression lessThan(QueryExpression first, QueryExpression second) {
    return new LessThanExpression(first, second);
  }

  /**
   * Constructs a {@link GreaterThanExpression} between the two given operands.
   *
   * @param first The first operand.
   * @param second The second operand.
   * @return The {@link GreaterThanExpression}.
   */
  public QueryExpression greaterThan(QueryExpression first, QueryExpression second) {
    return new GreaterThanExpression(first, second);
  }

  /**
   * Constructs a {@link LessThanOrEqualToExpression} between the two given operands.
   *
   * @param first The first operand.
   * @param second The second operand.
   * @return The {@link LessThanOrEqualToExpression}.
   */
  public QueryExpression lessThanOrEqualTo(QueryExpression first, QueryExpression second) {
    return new LessThanOrEqualToExpression(first, second);
  }

  /**
   * Constructs a {@link GreaterThanOrEqualToExpression} between the two given operands.
   *
   * @param first The first operand.
   * @param second The second operand.
   * @return The {@link GreaterThanOrEqualToExpression}.
   */
  public QueryExpression greaterThanOrEqualTo(QueryExpression first, QueryExpression second) {
    return new GreaterThanOrEqualToExpression(first, second);
  }

  /**
   * Constructs a {@link NotEqualToExpression} between the two given operands.
   *
   * @param first The first operand.
   * @param second The second operand.
   * @return The {@link NotEqualToExpression}.
   */
  public QueryExpression notEqualTo(QueryExpression first, QueryExpression second) {
    return new NotEqualToExpression(first, second);
  }

  /**
   * Adds a {@link QueryExpression} within the {@link Query}.
   *
   * @param expression The expression to add to the {@link Query}.
   */
  public void add(QueryExpression expression) {
    this.expression.push(expression);
  }

  /**
   * Constructs an {@link AndExpression} within the {@link Query}. The logical {@code AND} is
   * performed between the previous expression and the operand provided.
   *
   * @param operand The right-side operand of the {@link AndExpression}.
   */
  public void and(QueryExpression operand) {
    this.add(new AndExpression(this.expression.pop(), operand));
  }

  /**
   * Constructs an {@link OrExpression} within the {@link Query}. The logical {@code OR} is
   * performed between the previous expression and the operand provided.
   *
   * @param operand The right-side operand of the {@link OrExpression}.
   */
  public void or(QueryExpression operand) {
    this.add(new OrExpression(this.expression.pop(), operand));
  }

  /**
   * Constructs the {@link IntegerExpression} utilized to express the number of results skip.
   *
   * @param amount The number of results to skip.
   */
  public void skip(int amount) {
    this.skip(new Integer(amount));
  }

  /**
   * Constructs the {@link IntegerExpression} utilized to express the number of results skip.
   *
   * @param amount The number of results to skip.
   */
  public void skip(Integer amount) {
    this.args.add(new QueryArgument<Integer>(++this.index, amount, Types.INTEGER));
    QueryExpression ex = new IntegerExpression(amount);
    ex.usePlaceholders(true);
    this.skipExpression = ex;
  }

  /**
   * Constructs the {@link IntegerExpression} utilized to express the number of results retrieve.
   *
   * @param amount The number of results to retrieve.
   */
  public void limit(int amount) {
    this.limit(new Integer(amount));
  }

  /**
   * Constructs the {@link IntegerExpression} utilized to express the number of results retrieve.
   *
   * @param amount The number of results to retrieve.
   */
  public void limit(Integer amount) {
    this.args.add(new QueryArgument<Integer>(++this.index, amount, Types.INTEGER));
    QueryExpression ex = new IntegerExpression(amount);
    ex.usePlaceholders(true);
    this.limitExpression = ex;
  }

  /**
   * Constructs the {@link OrderByExpression} utilized to express the an ascending sort order of the
   * results retrieved.
   *
   * @param expressions The expressions to sort by.
   */
  public void ascending(QueryExpression... expressions) {
    this.orderByExpression =
        new OrderByExpression(new ColumnListExpression(expressions), new AscendingExpression());
  }

  /**
   * Constructs the {@link OrderByExpression} utilized to express the an descending sort order of
   * the results retrieved.
   *
   * @param expressions The expressions to sort by.
   */
  public void descending(QueryExpression... expressions) {
    this.orderByExpression =
        new OrderByExpression(new ColumnListExpression(expressions), new DescendingExpression());
  }

  /**
   * Retrieves the underlying conditional {@link QueryExpression} of the {@link Query}.
   *
   * @return The underlying conditional {@link QueryExpression}.
   */
  protected QueryExpression getQueryExpression() {
    return !this.expression.isEmpty() ? this.expression.pop() : null;
  }

  /**
   * Retrieves the underlying skip {@link QueryExpression} of the {@link Query}.
   *
   * @return The underlying skip {@link QueryExpression}.
   */
  protected QueryExpression getSkipExpression() {
    return this.skipExpression;
  }

  /**
   * Retrieves the underlying limit {@link QueryExpression} of the {@link Query}.
   *
   * @return The underlying limit {@link QueryExpression}.
   */
  protected QueryExpression getLimitExpression() {
    return this.limitExpression;
  }

  /**
   * Retrieves the underlying sort order {@link QueryExpression} of the {@link Query}.
   *
   * @return The underlying sort order {@link QueryExpression}.
   */
  protected QueryExpression getOrderByExpression() {
    return this.orderByExpression;
  }

  /**
   * Retrieves the list of arguments utilized to resolve placeholders within the {@link Query}.
   *
   * @return The {@link QueryArgument} collection.
   */
  protected List<QueryArgument> getQueryArguments() {
    return this.args;
  }

  /**
   * Executes the {@link Query}.
   *
   * @return A collection of results satisfying the {@link Query}.
   */
  public abstract Set<T> execute();
}
