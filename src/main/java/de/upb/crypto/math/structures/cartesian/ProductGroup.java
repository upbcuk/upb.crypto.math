package de.upb.crypto.math.structures.cartesian;

import de.upb.crypto.math.interfaces.structures.Group;
import de.upb.crypto.math.interfaces.structures.GroupElement;
import de.upb.crypto.math.serialization.Representation;
import de.upb.crypto.math.serialization.annotations.v2.ReprUtil;
import de.upb.crypto.math.serialization.annotations.v2.Represented;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Optional;

public class ProductGroup implements Group {
    @Represented
    protected Group[] groups;

    public ProductGroup(Group[] groups) {
        this.groups = groups;
    }

    public ProductGroup(Representation repr) {
        new ReprUtil(this).deserialize(repr);
    }

    @Override
    public GroupElement getNeutralElement() {
        return new ProductGroupElement(Arrays.stream(groups).map(Group::getNeutralElement).toArray(GroupElement[]::new));
    }

    @Override
    public BigInteger size() throws UnsupportedOperationException {
        return Arrays.stream(groups).map(Group::size).reduce(BigInteger.ZERO, (s, s2) -> s == null || s2 == null ? null : s.multiply(s2));
    }

    @Override
    public GroupElement getUniformlyRandomElement() throws UnsupportedOperationException {
        return new ProductGroupElement(Arrays.stream(groups).map(Group::getUniformlyRandomElement).toArray(GroupElement[]::new));
    }

    @Override
    public GroupElement getElement(Representation repr) {
        return new ProductGroupElement(repr);
    }

    @Override
    public Optional<Integer> getUniqueByteLength() {
        Optional<Integer> result = Optional.of(0);
        for (Group group : groups) {
            Optional<Integer> ubl = group.getUniqueByteLength();
            if (ubl.isPresent())
                result.map(s -> s + ubl.get());
            else
                result = Optional.empty();
        }

        return result;
    }

    @Override
    public boolean isCommutative() {
        return Arrays.stream(groups).allMatch(Group::isCommutative);
    }

    @Override
    public int estimateCostOfInvert() {
        return (int) Arrays.stream(groups).mapToInt(Group::estimateCostOfInvert).average().orElseGet(() -> 100);
    }

    @Override
    public Representation getRepresentation() {
        return ReprUtil.serialize(this);
    }

    public static ProductGroupElement valueOf(GroupElement... elems) {
        return new ProductGroupElement(elems);
    }
}