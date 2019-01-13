package infrastructure;

/**
 * Terminal expression representing a database column name.
 *
 * <p>See Interpreter pattern.
 *
 * @author Jon Freer
 */
public class ColumnExpression extends TerminalQueryExpression {

  private final String columnName;
  private final String referenceAlias;
  private final String columnAlias;

  /**
   * Constructs a new {@link ColumnExpression}.
   *
   * @param columnName The name of the database column.
   */
  public ColumnExpression(String columnName) {
    this(null, columnName, null);
  }

  /**
   * Constructs a new {@link ColumnExpression}.
   *
   * @param referenceAlias The table alias that this column is referencing.
   * @param columnName The database column name.
   */
  public ColumnExpression(String referenceAlias, String columnName) {
    this(referenceAlias, columnName, null);
  }

  /**
   * Constructs a new {@link ColumnExpression}.
   *
   * @param referenceAlias The table alias that this column is referencing.
   * @param columnName The database column name.
   * @param columnAlias The alias of the database column.
   */
  public ColumnExpression(String referenceAlias, String columnName, String columnAlias) {
    super();
    this.referenceAlias = referenceAlias;
    this.columnName = columnName;
    this.columnAlias = columnAlias;
  }

  /**
   * {@inheritDoc}
   *
   * @return {@inheritDoc}
   */
  @Override
  public String interpret() {
    StringBuilder sb = new StringBuilder();
    if (this.referenceAlias != null) {
      sb.append(this.referenceAlias).append(".");
    }

    sb.append(this.columnName);

    if (this.columnAlias != null) {
      sb.append(" AS ").append(this.columnAlias);
    }
    return sb.toString();
  }
}
