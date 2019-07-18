package de.upb.crypto.math.swante.profiling;

import de.upb.crypto.math.factory.BilinearGroup;
import de.upb.crypto.math.factory.BilinearGroupRequirement;
import de.upb.crypto.math.interfaces.mappings.BilinearMap;
import de.upb.crypto.math.interfaces.structures.GroupElement;
import de.upb.crypto.math.interfaces.structures.PowProductExpression;
import de.upb.crypto.math.pairings.bn.*;
import de.upb.crypto.math.pairings.generic.AbstractPairing;
import de.upb.crypto.math.structures.ec.AbstractEllipticCurvePoint;
import de.upb.crypto.math.structures.zn.Zp;
import de.upb.crypto.math.swante.*;
import de.upb.crypto.math.swante.powproducts.MyArrayPowProductWithFixedBases;
import de.upb.crypto.math.swante.powproducts.MyFastPowProductWithoutCaching;
import de.upb.crypto.math.swante.powproducts.MySimpleInterleavingPowProduct;
import de.upb.crypto.math.swante.powproducts.MySimultaneousSlidingWindowPowProduct;

import java.math.BigInteger;

import static de.upb.crypto.math.swante.misc.myAssert;
import static de.upb.crypto.math.swante.misc.pln;

public class ThesisPairings {
    public static void main(String[] args) {
        pln("=========================");
        if (args.length == 0) {
            args = "256 10 100 Tate".split(" ");
        }
        pln(args);
        int bitLength = Integer.parseInt(args[0]);
        AbstractPairing pairing = null;
        if (args[3].equals("Ate")) { // Ate
            pairing = MyBarretoNaehrigAtePairing.createAtePairing(bitLength);
        } else { // Tate
            BarretoNaehrigProvider bnProvider = new BarretoNaehrigProvider();
            BilinearMap bnMap = bnProvider.provideBilinearGroup(bitLength, new BilinearGroupRequirement(BilinearGroup.Type.TYPE_3)).getBilinearMap();
            pairing = new BarretoNaehrigTatePairing(((BarretoNaehrigSourceGroup) bnMap.getG1()), ((BarretoNaehrigSourceGroup) bnMap.getG2()), ((BarretoNaehrigTargetGroup) bnMap.getGT()));
        }
        int numPoints = Integer.parseInt(args[1]);
        int numIterations = Integer.parseInt(args[2]);
        BarretoNaehrigGroup1Element[] A = new BarretoNaehrigGroup1Element[numPoints];
        BarretoNaehrigGroup2Element[] B = new BarretoNaehrigGroup2Element[numPoints];
        for (int i = 0; i < numPoints; i++) {
            A[i] = (BarretoNaehrigGroup1Element) pairing.getG1().getUniformlyRandomNonNeutral();
            B[i] = (BarretoNaehrigGroup2Element) pairing.getUnitRandomElementFromG2Group();
        }
        double startMillis = System.nanoTime() / 1.0e6;
        for (int iter = -numIterations; iter < numIterations; iter++) {
            if (iter < 0) { // start timing only after warmup phase
                startMillis = System.nanoTime() / 1.0e6;
            }
            for (int i = 0; i < numPoints; i++) {
                pairing.apply(A[i], B[i]);
            }
        }
        double elapsedMillis = System.nanoTime() / 1.0e6 - startMillis;
        pln("Result: " + elapsedMillis);
    }
}
