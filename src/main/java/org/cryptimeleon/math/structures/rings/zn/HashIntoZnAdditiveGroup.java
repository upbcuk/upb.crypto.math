package org.cryptimeleon.math.structures.rings.zn;

import org.cryptimeleon.math.serialization.Representation;
import org.cryptimeleon.math.structures.groups.HashIntoGroup;
import org.cryptimeleon.math.structures.groups.RingGroup;

import java.math.BigInteger;
import java.util.Objects;

/**
 * Hashes into the additive subgroup of {@link Zn}.
 *
 * @see HashIntoZn
 */
public class HashIntoZnAdditiveGroup implements HashIntoGroup {
    /**
     * The {@link HashIntoZn} underlying this hash.
     */
    protected HashIntoZn znHash;

    /**
     * The target additive group.
     */
    protected RingGroup structure;

    /**
     * Initializes this hash to the {@code Zn} based on the given {@code n}.
     */
    public HashIntoZnAdditiveGroup(BigInteger n) {
        this(new HashIntoZn(n));
    }

    /**
     * Initializes this hash to the additive subgroup of the given {@code Zn}.
     */
    public HashIntoZnAdditiveGroup(Zn ring) {
        this(new HashIntoZn(ring.n));
    }

    /**
     * Recreates hash function from representation.
     */
    public HashIntoZnAdditiveGroup(Representation repr) {
        this(new HashIntoZn(repr));
    }

    /**
     * Initializes this hash based on an existing {@code HashIntoZn}.
     */
    public HashIntoZnAdditiveGroup(HashIntoZn hashIntoZn) {
        znHash = hashIntoZn;
        structure = znHash.getTargetStructure().asAdditiveGroup();
    }

    @Override
    public RingGroup.RingGroupElement hash(byte[] x) {
        return structure.getElement(znHash.hash(x));
    }

    @Override
    public Representation getRepresentation() {
        return znHash.getRepresentation();
    }


    @Override
    public int hashCode() {
        return Objects.hash(znHash, structure);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        HashIntoZnAdditiveGroup other = (HashIntoZnAdditiveGroup) obj;
        return Objects.equals(znHash, other.znHash);
    }
}
