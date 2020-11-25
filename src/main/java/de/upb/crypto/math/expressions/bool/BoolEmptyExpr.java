package de.upb.crypto.math.expressions.bool;

import de.upb.crypto.math.expressions.Expression;
import de.upb.crypto.math.expressions.VariableExpression;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A {@link BooleanExpression} representing an empty Boolean expression which cannot be evaluated on its own.
 * <p>
 * This class is useful when first creating a new Boolean expression, i.e. as en empty scaffolding.
 * It will "disappear" once combined (via AND or OR) with other Boolean expressions.
 */
public class BoolEmptyExpr implements BooleanExpression {

    @Override
    public BooleanExpression substitute(Function<VariableExpression, ? extends Expression> substitutions) {
        return this;
    }

    @Override
    public Boolean evaluate() {
        throw new IllegalArgumentException("Cannot evaluate an empty expression.");
    }

    @Override
    public Boolean evaluate(Function<VariableExpression, ? extends Expression> substitutions) {
        return evaluate();
    }

    @Override
    public void forEachChild(Consumer<Expression> action) {
        //Nothing to do
    }

    @Override
    public BooleanExpression or(BooleanExpression rhs) {
        return rhs;
    }

    @Override
    public BooleanExpression and(BooleanExpression rhs) {
        return rhs;
    }

    @Override
    public BooleanExpression not() {
        return this;
    }
}
