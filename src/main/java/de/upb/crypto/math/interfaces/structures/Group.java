package de.upb.crypto.math.interfaces.structures;

import de.upb.crypto.math.expressions.Expression;
import de.upb.crypto.math.expressions.bool.BooleanExpression;
import de.upb.crypto.math.expressions.group.GroupElementExpression;
import de.upb.crypto.math.expressions.group.GroupEmptyExpr;
import de.upb.crypto.math.expressions.group.GroupPowExpr;
import de.upb.crypto.math.serialization.Representation;
import de.upb.crypto.math.serialization.annotations.v2.RepresentationRestorer;
import de.upb.crypto.math.structures.cartesian.GroupElementVector;
import de.upb.crypto.math.structures.cartesian.RingElementVector;
import de.upb.crypto.math.structures.cartesian.Vector;
import de.upb.crypto.math.structures.zn.Zn;

import java.lang.reflect.Type;
import java.math.BigInteger;

/**
 * A Group. Operations are defined on its elements.
 */
public interface Group extends Structure, RepresentationRestorer {
    /**
     * Returns the neutral element for this group
     */
    GroupElement getNeutralElement();

    @Override
    GroupElement getUniformlyRandomElement() throws UnsupportedOperationException;

    @Override
    default GroupElementVector getUniformlyRandomElements(int n) throws UnsupportedOperationException {
        return GroupElementVector.generate(this::getUniformlyRandomElement, n);
    }

    default GroupElement getUniformlyRandomNonNeutral() {
        GroupElement result;
        do {
            result = getUniformlyRandomElement();
        } while (result.isNeutralElement());
        return result;
    }

    default GroupElementVector getUniformlyRandomNonNeutrals(int n) {
        return GroupElementVector.generate(this::getUniformlyRandomNonNeutral, n);
    }

    @Override
    GroupElement getElement(Representation repr);

    /**
     * Recreates a GroupElementVector containing group elements from this Group
     * @param repr a representation of a GroupElementVector (obtained via GroupElementVector::getRepresentation).
     */
    default GroupElementVector getVector(Representation repr) {
        return GroupElementVector.fromStream(repr.list().stream().map(this::getElement));
    }

    /**
     * Returns any generator of this group if the group is cyclic and it's feasible to compute a generator.
     * Repeated calls may or may not always supply the same generator again (i.e. the output is not guaranteed to be random)!
     *
     * @throws UnsupportedOperationException if the group doesn't know or have a generator
     */
    default GroupElement getGenerator() throws UnsupportedOperationException {
        if (hasPrimeSize())
            return getUniformlyRandomNonNeutral();
        throw new UnsupportedOperationException("Can't compute generator for group: " + this);
    }

    /**
     * Returns true if this group is known to be commutative.
     */
    boolean isCommutative();

    /**
     * Returns a GroupElementExpression containing the neutral group element.
     */
    default GroupElementExpression expr() {
        return new GroupEmptyExpr(this);
    }

    @Override
    default Object recreateFromRepresentation(Type type, Representation repr) {
        if (type instanceof Class && GroupElement.class.isAssignableFrom((Class) type))
            return getElement(repr);
        if (type instanceof Class && GroupElementVector.class.isAssignableFrom((Class) type))
            return getVector(repr);

        throw new IllegalArgumentException("Group cannot recreate type "+type.getTypeName()+" from representation");
    }

    /**
     * Returns Zn, where n = size()
     */
    default Zn getZn() {
        BigInteger size = size();
        if (size == null)
            throw new IllegalArgumentException("Infinitely large group - cannot output corresponding Zn");

        return new Zn(size);
    }

    /**
     * Returns a random integer between 0 and size()-1.
     */
    default Zn.ZnElement getUniformlyRandomExponent() {
        return getZn().getUniformlyRandomElement();
    }

    /**
     * Returns n random integers between 0 and size()-1.
     */
    default RingElementVector getUniformlyRandomExponents(int n) {
        return RingElementVector.generate(this::getUniformlyRandomExponent, n);
    }

    /**
     * Returns a random integer invertible mod size().
     */
    default Zn.ZnElement getUniformlyRandomUnitExponent() {
        return getZn().getUniformlyRandomUnit();
    }

    /**
     * Returns n random integers invertible mod size().
     */
    default RingElementVector getUniformlyRandomUnitExponents(int n) {
        return RingElementVector.generate(this::getUniformlyRandomUnitExponent, n);
    }

    /**
     * Returns a random integer between 1 and size()-1.
     */
    default Zn.ZnElement getUniformlyRandomNonzeroExponent() {
        return getZn().getUniformlyRandomNonzeroElement();
    }

    /**
     * Returns n random integers between 1 and size()-1.
     */
    default RingElementVector getUniformlyRandomNonzeroExponents(int n) {
        return RingElementVector.generate(this::getUniformlyRandomNonzeroExponent, n);
    }
}

