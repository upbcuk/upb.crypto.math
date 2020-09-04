package de.upb.crypto.math.pairings.debug;

import de.upb.crypto.math.factory.BilinearGroup;
import de.upb.crypto.math.interfaces.mappings.BilinearMap;
import de.upb.crypto.math.interfaces.structures.Group;
import de.upb.crypto.math.interfaces.structures.GroupElement;
import de.upb.crypto.math.serialization.Representation;
import de.upb.crypto.math.serialization.annotations.v2.ReprUtil;
import de.upb.crypto.math.serialization.annotations.v2.Represented;
import de.upb.crypto.math.structures.groups.lazy.LazyBilinearMap;
import de.upb.crypto.math.structures.groups.lazy.LazyGroup;
import de.upb.crypto.math.structures.groups.lazy.LazyGroupElement;
import de.upb.crypto.math.structures.zn.Zn;

import java.math.BigInteger;

public class CountingBilinearMap implements BilinearMap {

    @Represented
    LazyBilinearMap totalBilMap;
    @Represented
    LazyBilinearMap expMultiExpBilMap;

    public CountingBilinearMap(LazyBilinearMap totalBilMap, LazyBilinearMap expMultiExpBilMap) {
        this.totalBilMap = totalBilMap;
        this.expMultiExpBilMap = expMultiExpBilMap;
    }

    public CountingBilinearMap(Representation repr) {
        ReprUtil.deserialize(this, repr);
    }

    @Override
    public Group getG1() {
        return new CountingGroup((LazyGroup) totalBilMap.getG1(), (LazyGroup) expMultiExpBilMap.getG1());
    }

    @Override
    public Group getG2() {
        return new CountingGroup((LazyGroup) totalBilMap.getG2(), (LazyGroup) expMultiExpBilMap.getG2());
    }

    @Override
    public Group getGT() {
        return new CountingGroup((LazyGroup) totalBilMap.getGT(), (LazyGroup) expMultiExpBilMap.getGT());
    }

    @Override
    public GroupElement apply(GroupElement g1, GroupElement g2, BigInteger exponent) {
        // TODO: Add pairing counting
        CountingGroupElement g1Cast = (CountingGroupElement) g1;
        CountingGroupElement g2Cast = (CountingGroupElement) g2;
        return new CountingGroupElement(
                (CountingGroup) getGT(),
                (LazyGroupElement) totalBilMap.apply(g1Cast.elemTotal, g2Cast.elemTotal, exponent),
                (LazyGroupElement) expMultiExpBilMap.apply(g1Cast.elemExpMultiExp, g2Cast.elemExpMultiExp, exponent)
        );
    }

    @Override
    public boolean isSymmetric() {
        return totalBilMap.isSymmetric() && expMultiExpBilMap.isSymmetric();
    }
}
