package de.upb.crypto.math.expressions.group;

import de.upb.crypto.math.expressions.Expression;
import de.upb.crypto.math.expressions.Substitution;
import de.upb.crypto.math.expressions.exponent.ExponentExpr;
import de.upb.crypto.math.interfaces.mappings.BilinearMap;
import de.upb.crypto.math.interfaces.structures.GroupElement;
import de.upb.crypto.math.structures.zn.Zn;

import java.math.BigInteger;
import java.util.function.Consumer;

public class PairingExpr extends AbstractGroupElementExpression {
    protected GroupElementExpression lhs, rhs;
    protected BilinearMap map;

    public PairingExpr(BilinearMap map, GroupElementExpression lhs, GroupElementExpression rhs) {
        super(map.getGT());
        this.map = map;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public BilinearMap getMap() {
        return map;
    }

    public GroupElementExpression getLhs() {
        return lhs;
    }

    public GroupElementExpression getRhs() {
        return rhs;
    }

    @Override
    public void forEachChild(Consumer<Expression> action) {
        action.accept(lhs);
        action.accept(rhs);
    }

    @Override
    public GroupElement evaluate(Substitution substitutions) {
        return map.apply(lhs.evaluate(substitutions), rhs.evaluate(substitutions));
    }

    @Override
    public PairingExpr substitute(Substitution substitutions) {
        return new PairingExpr(map, lhs.substitute(substitutions), rhs.substitute(substitutions));
    }

    @Override
    public GroupOpExpr linearize() throws IllegalArgumentException {
        boolean lhsHasVariables = lhs.containsVariables();
        boolean rhsHasVariables = rhs.containsVariables();

        if (lhsHasVariables && rhsHasVariables)
            throw new IllegalArgumentException("Expression is not linear (it's of the form e(g,h), where both g and h depend on variables)");

        if (!lhsHasVariables && !rhsHasVariables)
            return new GroupOpExpr(this, new GroupEmptyExpr(map.getGT()));

        if (lhsHasVariables) { //hence rhs doesn't
            GroupOpExpr lhsLinearized = lhs.linearize();
            return new GroupOpExpr(new PairingExpr(map, lhsLinearized.getLhs(), rhs), new PairingExpr(map, lhsLinearized.getRhs(), rhs));
        } else { //lhs is constant, rhs isn't
            GroupOpExpr rhsLinearized = rhs.linearize();
            return new GroupOpExpr(new PairingExpr(map, lhs, rhsLinearized.getLhs()), new PairingExpr(map, lhs, rhsLinearized.getRhs()));
        }
    }

    @Override
    public GroupOpExpr flatten(ExponentExpr exponent) {
        if (exponent.containsVariables() || lhs.containsVariables() || rhs.containsVariables()) {
            return new GroupOpExpr(new GroupEmptyExpr(map.getGT()), new PairingExpr(map, lhs.flatten(exponent), rhs.flatten()).pow(exponent));
        }
        else {
            BigInteger groupSize = getGroupOrderIfKnown();
            BigInteger exponentVal = groupSize == null ? exponent.evaluate() : exponent.evaluate(new Zn(groupSize)).getInteger();
            return new GroupOpExpr(evaluate().pow(exponentVal).expr(), new GroupEmptyExpr(map.getGT()));
        }
    }
}
