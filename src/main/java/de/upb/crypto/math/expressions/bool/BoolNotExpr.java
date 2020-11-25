package de.upb.crypto.math.expressions.bool;

import de.upb.crypto.math.expressions.Expression;
import de.upb.crypto.math.expressions.VariableExpression;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A {@link BooleanExpression} representing the Boolean NOT of a Boolean expression.
 */
public class BoolNotExpr implements BooleanExpression {
    private final BooleanExpression child;

    public BoolNotExpr(BooleanExpression child) {
        this.child = child;
    }

    /**
     * Retrieves the Boolean expression to which this Boolean NOT is applied.
     */
    public BooleanExpression getChild() {
        return child;
    }

    @Override
    public BooleanExpression substitute(Function<VariableExpression, ? extends Expression> substitutions) {
        return child.substitute(substitutions).not();
    }

    @Override
    public Boolean evaluate(Function<VariableExpression, ? extends Expression> substitutions) {
        return !child.evaluate(substitutions);
    }

    @Override
    public void forEachChild(Consumer<Expression> action) {
        action.accept(child);
    }
}
