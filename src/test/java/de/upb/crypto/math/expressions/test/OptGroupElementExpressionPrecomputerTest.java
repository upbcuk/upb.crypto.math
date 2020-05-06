package de.upb.crypto.math.expressions.test;

import de.upb.crypto.math.expressions.Expression;
import de.upb.crypto.math.expressions.ValueBundle;
import de.upb.crypto.math.expressions.VariableExpression;
import de.upb.crypto.math.expressions.bool.*;
import de.upb.crypto.math.expressions.evaluator.ExponentExpressionAnalyzer;
import de.upb.crypto.math.expressions.evaluator.OptGroupElementExpressionEvaluator;
import de.upb.crypto.math.expressions.evaluator.OptGroupElementExpressionPrecomputer;
import de.upb.crypto.math.expressions.evaluator.trs.ExprRule;
import de.upb.crypto.math.expressions.evaluator.trs.RuleApplicator;
import de.upb.crypto.math.expressions.evaluator.trs.group.*;
import de.upb.crypto.math.expressions.exponent.ExponentConstantExpr;
import de.upb.crypto.math.expressions.exponent.ExponentMulExpr;
import de.upb.crypto.math.expressions.exponent.ExponentVariableExpr;
import de.upb.crypto.math.expressions.group.*;
import de.upb.crypto.math.factory.BilinearGroup;
import de.upb.crypto.math.factory.BilinearGroupFactory;
import de.upb.crypto.math.interfaces.structures.Group;
import de.upb.crypto.math.structures.zn.Zp;
import org.junit.Test;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class OptGroupElementExpressionPrecomputerTest {

    @Test
    public void testRewriteTermsSimpleExpSwapRule() {
        Zp zp = new Zp(BigInteger.valueOf(101));
        Group unitGroup = zp.asUnitGroup();

        GroupPowExpr expr = new GroupPowExpr(
                new GroupPowExpr(
                        new GroupElementConstantExpr(unitGroup.getUniformlyRandomNonNeutral()),
                        new ExponentVariableExpr("x")),
                new ExponentConstantExpr(BigInteger.valueOf(2))
        );

        List<ExprRule> rules = new LinkedList<>();
        rules.add(new ExpSwapRule());
        rules.add(new PairingGtExpRule());
        GroupElementExpression newExpr = new OptGroupElementExpressionPrecomputer()
                .rewriteGroupTermsTopDown(expr, new RuleApplicator(rules));
        ValueBundle valueBundle = new ValueBundle();
        valueBundle.put("x", BigInteger.valueOf(3));
        assert expr.substitute(valueBundle).evaluateNaive().equals(newExpr.substitute(valueBundle).evaluateNaive());
    }

    @Test
    public void testRewriteTermsPairing() {
        BilinearGroupFactory fac = new BilinearGroupFactory(60);
        fac.setDebugMode(true);
        fac.setRequirements(BilinearGroup.Type.TYPE_3);
        BilinearGroup group = fac.createBilinearGroup();

        // (e(g_1^a, g_2^b)^x)^2
        GroupPowExpr expr = new GroupPowExpr(
                new GroupPowExpr(
                        new PairingExpr(
                                group.getBilinearMap(),
                                group.getG1().getUniformlyRandomNonNeutral().expr().pow(new ExponentVariableExpr("a")),
                                group.getG2().getUniformlyRandomNonNeutral().expr().pow(new ExponentVariableExpr("b"))
                        ),
                        new ExponentVariableExpr("x")
                ),
                new ExponentConstantExpr(BigInteger.valueOf(2))
        );

        List<ExprRule> rules = new LinkedList<>();
        rules.add(new ExpSwapRule());
        rules.add(new PairingGtExpRule());
        rules.add(new PairingMoveLeftVarsOutsideRule());
        rules.add(new PairingMoveRightVarsOutsideRule());
        rules.add(new MergeNestedVarExpRule());
        // newExpr == e(g_1^2, g_2)^{abx}
        GroupElementExpression newExpr = (GroupElementExpression) new OptGroupElementExpressionPrecomputer()
                .rewriteTerms(expr, new RuleApplicator(rules));
        assert newExpr instanceof GroupPowExpr;
        GroupPowExpr powExpr = (GroupPowExpr) newExpr;
        assert powExpr.getExponent().getVariables().stream().map(VariableExpression::getName).sorted().reduce("", (s1, s2) -> s1+s2).equals("abx");
        assert powExpr.getBase() instanceof  PairingExpr;
        ValueBundle valueBundle = new ValueBundle();
        valueBundle.put("x", BigInteger.valueOf(3));
        valueBundle.put("a", BigInteger.valueOf(4));
        valueBundle.put("b", BigInteger.valueOf(5));
        assert expr.substitute(valueBundle).evaluateNaive().equals(newExpr.substitute(valueBundle).evaluateNaive());
    }

    @Test
    public void testRewriteTermsExpOptimization() {
        Zp zp = new Zp(BigInteger.valueOf(101));
        Group unitGroup = zp.asUnitGroup();

        // (g^{2*x})^{y*3}
        GroupElementExpression expr = new GroupPowExpr(
                new GroupPowExpr(
                        unitGroup.getUniformlyRandomNonNeutral().expr(),
                        new ExponentMulExpr(
                                new ExponentConstantExpr(BigInteger.valueOf(2)),
                                new ExponentVariableExpr("x")
                        )
                ),
                new ExponentMulExpr(
                        new ExponentVariableExpr("y"),
                        new ExponentConstantExpr(BigInteger.valueOf(3))
                )
        );

        GroupElementExpression newExpr = (GroupElementExpression) new OptGroupElementExpressionPrecomputer().rewriteTerms(expr);
        assert newExpr instanceof GroupPowExpr;
        GroupPowExpr powExpr1 = (GroupPowExpr) newExpr;
        assert powExpr1.getBase() instanceof GroupPowExpr;
        assert !ExponentExpressionAnalyzer.containsTypeExpr(((GroupPowExpr) powExpr1.getBase()).getExponent(),
                ExponentVariableExpr.class);
        assert ExponentExpressionAnalyzer.containsTypeExpr(powExpr1.getExponent(),
                ExponentVariableExpr.class);
        ValueBundle valueBundle = new ValueBundle();
        valueBundle.put("x", BigInteger.valueOf(3));
        valueBundle.put("y", BigInteger.valueOf(2));
        assert expr.substitute(valueBundle).evaluateNaive().equals(newExpr.substitute(valueBundle).evaluateNaive());
    }

    @Test
    public void testVariableInBase() {
        Zp zp = new Zp(BigInteger.valueOf(101));

        // x^e
        GroupElementExpression expr = new GroupPowExpr(
                new GroupVariableExpr("x"),
                new ExponentConstantExpr(zp.getUniformlyRandomElement())
        );
        GroupElementExpression newExpr = new OptGroupElementExpressionEvaluator().precompute(expr);
    }

    @Test
    public void testMarkMergeable() {
        Zp zp = new Zp(BigInteger.valueOf(101));
        Group unitGroup = zp.asUnitGroup();

        BoolAndExpr andExpr1 = new BoolAndExpr(
                new GroupEqualityExpr(new GroupVariableExpr("x"), new GroupEmptyExpr(unitGroup)),
                new GroupEqualityExpr(new GroupVariableExpr("y"), new GroupEmptyExpr(unitGroup))
        );
        BoolAndExpr andExpr2 = new BoolAndExpr(
                new BoolVariableExpr("z"),
                new GroupEqualityExpr(
                        unitGroup.getNeutralElement().expr(),
                        unitGroup.getNeutralElement().expr()
                )
        );

        BooleanExpression expr = new BoolOrExpr(
                andExpr1,
                new BoolNotExpr(
                    andExpr2
                )
        );
        Map<Expression, Boolean> exprToMergeable = new HashMap<>();
        new OptGroupElementExpressionPrecomputer().markMergeableExprs(expr, exprToMergeable);
        assert exprToMergeable.get(andExpr1);
        assert !exprToMergeable.get(andExpr2);
        assert !exprToMergeable.get(expr);
    }

    @Test
    public void testMergeANDs() {
        Zp zp = new Zp(new BigInteger("170141183460469231731687303715884105727"));
        Group unitGroup = zp.asUnitGroup();

        BoolAndExpr expr = new BoolAndExpr(
                new GroupEqualityExpr(
                        unitGroup.getUniformlyRandomNonNeutral().expr(), unitGroup.getUniformlyRandomNonNeutral().expr()
                ),
                new BoolAndExpr(
                        new GroupEqualityExpr(
                                unitGroup.getUniformlyRandomNonNeutral().expr(), new GroupEmptyExpr(unitGroup)
                        ),
                        new GroupEqualityExpr(
                                unitGroup.getUniformlyRandomNonNeutral().expr(), new GroupVariableExpr("y")
                        )
                )
        );

        Map<Expression, Boolean> exprToMergeable = new HashMap<>();
        exprToMergeable.put(expr, true);
        BooleanExpression newExpr = (BooleanExpression) new OptGroupElementExpressionPrecomputer()
                .traverseMergeANDs(expr, exprToMergeable);
        assert newExpr instanceof GroupEqualityExpr;
        ValueBundle valueBundle = new ValueBundle();
        valueBundle.put("y", unitGroup.getUniformlyRandomNonNeutral());
        // Since this is a probabilistic simplification, it could theoretically fail the test.
        assert expr.substitute(valueBundle).evaluate() == newExpr.substitute(valueBundle).evaluate();
    }
}