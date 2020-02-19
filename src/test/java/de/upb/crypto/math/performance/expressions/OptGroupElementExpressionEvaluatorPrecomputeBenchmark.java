package de.upb.crypto.math.performance.expressions;

import com.github.noconnor.junitperf.JUnitPerfRule;
import com.github.noconnor.junitperf.JUnitPerfTest;
import de.upb.crypto.math.expressions.ValueBundle;
import de.upb.crypto.math.expressions.evaluator.OptGroupElementExpressionEvaluator;
import de.upb.crypto.math.expressions.group.GroupElementExpression;
import de.upb.crypto.math.factory.BilinearGroup;
import de.upb.crypto.math.factory.BilinearGroupFactory;
import de.upb.crypto.math.interfaces.structures.GroupElement;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

@Ignore
public class OptGroupElementExpressionEvaluatorPrecomputeBenchmark {

    @Rule
    public JUnitPerfRule perfTestRule = new JUnitPerfRule();
    private final int perfDuration = 10_000;
    private final int warmupDuration = 4_000;

    private static GroupElementExpression testExpr;
    private static GroupElementExpression precomputedTestExpr;

    private static GroupElement result;

    private static ValueBundle valueBundle;


    @BeforeClass
    public static void setupPerfTest() {
        BilinearGroupFactory fac = new BilinearGroupFactory(60);
        fac.setRequirements(BilinearGroup.Type.TYPE_3);
        BilinearGroup bilGroup = fac.createBilinearGroup();
        int numPairing = 10;
        testExpr = ExpressionGenerator.genPairingWithMultiExpOutside(bilGroup.getBilinearMap(), numPairing,
                true);
        valueBundle = new ValueBundle();
        for (int i = 0; i < numPairing; ++i) {
            valueBundle.put("x" + i, bilGroup.getGT().getZn().getUniformlyRandomElement());
        }
        // Do precomputation beforehand
        OptGroupElementExpressionEvaluator evaluator = new OptGroupElementExpressionEvaluator();
        precomputedTestExpr = evaluator.precompute(testExpr);
        result = testExpr.substitute(valueBundle).evaluateNaive();
    }

    @Test
    @JUnitPerfTest(durationMs = perfDuration, warmUpMs = warmupDuration)
    public void testEvaluation() {
        OptGroupElementExpressionEvaluator evaluator = new OptGroupElementExpressionEvaluator();
        assert evaluator.evaluate(testExpr.substitute(valueBundle)).equals(result);
    }

    @Test
    @JUnitPerfTest(durationMs = perfDuration, warmUpMs = warmupDuration)
    public void testPrecomputedEvaluation() {
        OptGroupElementExpressionEvaluator evaluator = new OptGroupElementExpressionEvaluator();
        assert evaluator.evaluate(precomputedTestExpr.substitute(valueBundle)).equals(result);
    }
}