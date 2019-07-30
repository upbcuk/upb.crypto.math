package de.upb.crypto.math.expressions.exponent;

import de.upb.crypto.math.expressions.Expression;
import de.upb.crypto.math.structures.integers.IntegerElement;
import de.upb.crypto.math.structures.zn.Zn;

import java.math.BigInteger;
import java.util.Map;
import java.util.function.Consumer;

public class ExponentLiteralExpr implements ExponentExpr {
    protected BigInteger exponent;

    public ExponentLiteralExpr(BigInteger exponent) {
        this.exponent = exponent;
    }

    public ExponentLiteralExpr(Zn.ZnElement exponent) {
        this.exponent = exponent.getInteger();
    }

    @Override
    public BigInteger evaluate() {
        return exponent;
    }

    @Override
    public Zn.ZnElement evaluateZn(Zn zn) {
        return zn.valueOf(exponent);
    }

    @Override
    public ExponentLiteralExpr substitute(Map<String, ? extends Expression> substitutions) {
        return this;
    }

    @Override
    public void treeWalk(Consumer<Expression> visitor) {
        visitor.accept(this);
    }
}
