package de.upb.crypto.math.pairings.type3.bn;

import de.upb.crypto.math.interfaces.structures.FieldElement;

/**
 * Element of G1.
 */
public class BarretoNaehrigGroup1ElementImpl extends BarretoNaehrigSourceGroupElementImpl {

    /**
     * Construct point on given curve with given x- and y-coordinates.
     *
     * @param curve curve of point
     * @param x x-coordinate of point
     * @param y y-coordinate of point
     */
    BarretoNaehrigGroup1ElementImpl(BarretoNaehrigGroup1Impl curve, FieldElement x, FieldElement y) {
        super(curve, x, y);
    }

    BarretoNaehrigGroup1ElementImpl(BarretoNaehrigGroup1Impl curve) {
        super(curve);
    }
}
