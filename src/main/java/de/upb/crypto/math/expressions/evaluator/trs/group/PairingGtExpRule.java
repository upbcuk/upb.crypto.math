package de.upb.crypto.math.expressions.evaluator.trs.group;

import de.upb.crypto.math.expressions.Expression;
import de.upb.crypto.math.expressions.evaluator.trs.ExprRule;
import de.upb.crypto.math.expressions.exponent.ExponentVariableExpr;
import de.upb.crypto.math.expressions.group.GroupPowExpr;
import de.upb.crypto.math.expressions.group.PairingExpr;

import static de.upb.crypto.math.expressions.evaluator.ExponentExpressionAnalyzer.containsTypeExpr;

/**
 * Rule that moves the exponent from an exponentiation with a pairing as its base to group 1 of the pairing,
 * e.g. e(g_1, g_2)^2 -> e(g_1^2, g_2). Exponentiation in group 1 is much cheaper than in the target group.
 * It does not move variables however, as then the pairing itself cannot be pre-evaluated which is very important.
 */
public class PairingGtExpRule implements ExprRule {

    @Override
    public boolean isApplicable(Expression expr) {
        if (!(expr instanceof GroupPowExpr))
            return false;

        GroupPowExpr powExpr = (GroupPowExpr) expr;

        return powExpr.getBase() instanceof PairingExpr
                && !containsTypeExpr(powExpr.getExponent(), ExponentVariableExpr.class);
    }

    @Override
    public Expression apply(Expression expr) {
        GroupPowExpr powExpr = (GroupPowExpr) expr;
        PairingExpr pairingExpr = (PairingExpr) powExpr.getBase();

        return new PairingExpr(
                pairingExpr.getMap(),
                new GroupPowExpr(pairingExpr.getLhs(), powExpr.getExponent()),
                pairingExpr.getRhs()
        );
    }
}