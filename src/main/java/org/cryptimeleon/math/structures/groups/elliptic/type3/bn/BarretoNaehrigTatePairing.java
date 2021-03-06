package org.cryptimeleon.math.structures.groups.elliptic.type3.bn;

import org.cryptimeleon.math.structures.groups.elliptic.AbstractPairing;
import org.cryptimeleon.math.structures.groups.elliptic.PairingSourceGroupElement;
import org.cryptimeleon.math.structures.groups.elliptic.PairingTargetGroupElementImpl;
import org.cryptimeleon.math.structures.rings.FieldElement;
import org.cryptimeleon.math.structures.rings.extfield.ExtensionField;
import org.cryptimeleon.math.structures.rings.extfield.ExtensionFieldElement;

import java.math.BigInteger;

/**
 * Tate-pairing specific implementation of BN based pairings.
 */
class BarretoNaehrigTatePairing extends AbstractPairing {
    BigInteger lambda2, lambda1, lambda0;

    /**
     * Construct Tate pairing \(\mathbb{G}_1 \times \mathbb{G}_2 \rightarrow \mathbb{G}_T\).
     */
    public BarretoNaehrigTatePairing(BarretoNaehrigGroup1Impl g1, BarretoNaehrigGroup2Impl g2, BarretoNaehrigTargetGroupImpl gT, BigInteger u) {
        super(g1, g2, gT);
        lambda2 = u.pow(2).multiply(BigInteger.valueOf(6)).add(BigInteger.ONE);
        lambda1 = u.pow(3).multiply(BigInteger.valueOf(-36))
                .add(u.pow(2).multiply(BigInteger.valueOf(-18)))
                .add(u.multiply(BigInteger.valueOf(-12)))
                .add(BigInteger.ONE);
        lambda0 = u.pow(3).multiply(BigInteger.valueOf(-36))
                .add(u.pow(2).multiply(BigInteger.valueOf(-30)))
                .add(u.multiply(BigInteger.valueOf(-18)))
                .add(BigInteger.valueOf(-2));
    }

    /**
     * TODO (rh): Write javadoc for this. I had some notes somewhere about how exactly this works
     *  and especially the line parameterization format.
     *
     * @param line parameterization of the line
     */
    @Override
    protected ExtensionFieldElement evaluateLine(FieldElement[] line, PairingSourceGroupElement P, PairingSourceGroupElement Q) {
        ExtensionField targetField = (ExtensionField) gT.getFieldOfDefinition();
        ExtensionField extField = (ExtensionField) Q.getFieldOfDefinition();

        /*
         * G2 is a subgroup sextic twist E':y^2=x^3-b/v with xi^6=v from E'->E, phi:(x,y)->(x xi^2,y xi^3). GT is
         * defined over degree 6 extension field defined by X^6-v.
         *
         * Hence,
         * l_P(phi(xq,yq))=a_0(yq xi^3-yp) - a_1(xq xi^2 - xp) = (a_1 xp - a_0 yp) + 0 xi + (- a_1 xq) xi^2 + a_0 yq
         * xi^3 + 0 xi^4 + 0 xi^5.
         *
         * Here, non-vertical lines are parameterize by [a_0,a_1]=[1,lambda_P] where lambda_P is the slope through P and
         * vertical lines are parameterized by [a_0,a_1]=[0,1].
         */
        if (!P.isNormalized() || !Q.isNormalized()) {
            throw new IllegalArgumentException("Currently, only affine points are supported.");
        }

        FieldElement[] coefficients = new FieldElement[4];
        coefficients[0] = extField.createElement(P.getX().mul(line[1]).sub(P.getY().mul(line[0])));

        coefficients[1] = extField.getZeroElement();
        coefficients[2] = extField.createElement(line[1]).mul(Q.getX()).neg();

        coefficients[3] = Q.getY().mul(extField.createElement(line[0]));

        return targetField.createElement(coefficients);
    }

    @Override
    protected ExtensionFieldElement pair(PairingSourceGroupElement P, PairingSourceGroupElement Q) {
        ExtensionFieldElement result = this.miller(P, Q, g1.size());
        /*this might happen, if P and Q are from same subgroup. In this case, we get neutral element for Tate pairing.*/
        if (result.isZero()) {
            return result.getStructure().getOneElement();
        } else {
            return result;
        }

    }

    @Override
    public PairingTargetGroupElementImpl exponentiate(FieldElement f) {
        FieldElement result;

        if (lambda2 != null) {
            //https://eprint.iacr.org/2008/490.pdf section 3
            result = f.applyFrobenius(6).div(f);
            result = result.applyFrobenius(2).mul(result);

            ////https://eprint.iacr.org/2008/490.pdf section 5 (the "hard part" mentioned in section 3)
            FieldElement resultFrob1 = result.applyFrobenius();
            FieldElement resultFrob2 = resultFrob1.applyFrobenius();
            FieldElement resultFrob3 = resultFrob2.applyFrobenius();
            result = resultFrob3.mul(resultFrob2.pow(lambda2)).mul(resultFrob1.pow(lambda1)).mul(result.pow(lambda0));
        } else {
            result = f.pow(gT.getCofactor());
        }

        return gT.getElement((ExtensionFieldElement) result);
    }

    @Override
    public String toString() {
        return "Tate Pairing G1xG2->Gt of Type 3";
    }

    @Override
    public boolean isSymmetric() {
        return false;
    }


}
