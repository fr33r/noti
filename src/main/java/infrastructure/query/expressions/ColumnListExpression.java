package infrastructure.query.expressions;

import java.util.ArrayList;
import java.util.List;

/**
 * Nonterminal expression representing a collection of column expressions.
 *
 * <p>See Interpreter pattern.
 *
 * @author Jon Freer
 */
public class ColumnListExpression extends QueryExpression {

  public final ColumnExpression[] expressions;

  /**
   * Constructs a new {@link ColumnListExpression}.
   *
   * @param expressions The collection of column expressions.
   */
  public ColumnListExpression(ColumnExpression... expressions) {
    super();
    this.expressions = expressions;
  }

  /**
   * {@inheritDoc}
   *
   * @return {@inheritDoc}
   */
  @Override
  public String interpret() {
    List<String> interpretations = new ArrayList<>();
    for (QueryExpression q : expressions) {
      interpretations.add(q.interpret());
    }
    return String.join(", ", interpretations);
  }
}
