package org.cryptimeleon.math.expressions.bool;

import org.cryptimeleon.math.expressions.Expression;
import org.cryptimeleon.math.expressions.Substitution;

import java.util.function.Consumer;

/**
 * A {@link BooleanExpression} representing the Boolean OR of two {@code BooleanExpression} instances.
 */
public class BoolOrExpr implements BooleanExpression {
    /**
     * The Boolean expression on the left hand side of this Boolean OR.
     */
    protected final BooleanExpression lhs;

    /**
     * The Boolean expression on the right hand side of this Boolean OR.
     */
    protected final BooleanExpression rhs;

    public BoolOrExpr(BooleanExpression lhs, BooleanExpression rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    /**
     * Retrieves the Boolean expression on the left hand side of this Boolean OR.
     */
    public BooleanExpression getLhs() {
        return lhs;
    }

    /**
     * Retrieves the Boolean expression on the right hand side of this Boolean OR.
     */
    public BooleanExpression getRhs() {
        return rhs;
    }

    @Override
    public BooleanExpression substitute(Substitution substitutions) {
        return lhs.substitute(substitutions).or(rhs.substitute(substitutions));
    }

    @Override
    public Boolean evaluate(Substitution substitutions) {
        return lhs.evaluate(substitutions) || rhs.evaluate(substitutions);
    }

    @Override
    public LazyBoolEvaluationResult evaluateLazy(Substitution substitutions) {
        LazyBoolEvaluationResult lhs = this.lhs.evaluateLazy(substitutions);
        LazyBoolEvaluationResult rhs = this.rhs.evaluateLazy(substitutions);
        if (lhs.isResultKnown())
            return lhs.getResult() ? LazyBoolEvaluationResult.TRUE : rhs;
        if (rhs.isResultKnown())
            return rhs.getResult() ? LazyBoolEvaluationResult.TRUE : lhs;
        return new LazyBoolEvaluationResult() {
            @Override
            public boolean getResult() {
                return lhs.getResult() || rhs.getResult();
            }

            @Override
            boolean isResultKnown() {
                return false;
            }
        };
    }

    @Override
    public void forEachChild(Consumer<Expression> action) {
        action.accept(lhs);
        action.accept(rhs);
    }
}
