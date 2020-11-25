package de.upb.crypto.math.factory;

/**
 * Requirements for a bilinear group.
 * <p>
 * This class is indented to use for the configuration of the {@link BilinearGroupFactory}. A user sets up an object
 * with the settings they need the bilinear group to fulfill and register the config using
 * {@link BilinearGroupFactory#setRequirements(BilinearGroupRequirement)}. The configuration are the basis for the
 * selecting a suitable bilinear group to create.
 *
 * @author Denis Diemert
 */
public class BilinearGroupRequirement {
    /**
     * The desired type of the {@link BilinearGroup}. For the possible values, consider {@link BilinearGroup.Type}.
     */
    private BilinearGroup.Type type;
    /**
     * If set to true, the resulting factory will be able to supply a hash function byte[] -> G1. (If set to false, it
     * may or may not support this)
     */
    private boolean hashIntoG1Needed = false;
    /**
     * If set to true, the resulting factory will be able to supply a hash function byte[] -> G2. (If set to false, it
     * may or may not support this)
     */
    private boolean hashIntoG2Needed = false;
    /**
     * If set to true, the resulting factory will be able to supply a hash function byte[] -> GT. (If set to false, it
     * may or may not support this)
     */
    private boolean hashIntoGTNeeded = false;
    /**
     * If set to 1, the resulting {@link BilinearGroup} will consist of (G1, G2, GT) of prime order. Else, if set to a
     * value > 1, the group order is a composite number with {@code cardinalityNumPrimeFactors} prime factors.
     */
    private int numPrimeFactorsOfSize;

    /**
     * Standard constructor to set all requirements by hand.
     *
     * @param type                       the desired type of the resulting bilinear group
     * @param hashIntoG1Needed           true enforces that the resulting bilinear group provides a mapping from
     *                                   {@code byte[]} to \(\mathbb{G}_1\). When set to false,
     *                                   it may still be supported.
     * @param hashIntoG2Needed           true enforces that the resulting bilinear group provides a mapping from
     *                                   {@code byte[]} to \(\mathbb{G}_2\). When set to false,
     *                                   it may still be supported.
     * @param hashIntoGTNeeded           true enforces that the resulting bilinear group provides a mapping from
     *                                   {@code byte[]} to \(\mathbb{G}_1\). When set to false,
     *                                   it may still be supported.
     * @param numPrimeFactorsOfSize desired number of prime factors of the size of the resulting groups
     */
    public BilinearGroupRequirement(BilinearGroup.Type type, boolean hashIntoG1Needed, boolean hashIntoG2Needed,
                                    boolean hashIntoGTNeeded, int numPrimeFactorsOfSize) {
        this.type = type;
        this.hashIntoG1Needed = hashIntoG1Needed;
        this.hashIntoG2Needed = hashIntoG2Needed;
        this.hashIntoGTNeeded = hashIntoGTNeeded;
        this.numPrimeFactorsOfSize = numPrimeFactorsOfSize;
    }

    /**
     * Constructor for prime order groups, i.e. the size factors into a single prime factor.
     *
     * @param type             the desired type of the resulting bilinear group
     * @param hashIntoG1Needed           true enforces that the resulting bilinear group provides a mapping from
     *                                   {@code byte[]} to \(\mathbb{G}_1\). When set to false,
     *                                   it may still be supported.
     * @param hashIntoG2Needed           true enforces that the resulting bilinear group provides a mapping from
     *                                   {@code byte[]} to \(\mathbb{G}_2\). When set to false,
     *                                   it may still be supported.
     * @param hashIntoGTNeeded           true enforces that the resulting bilinear group provides a mapping from
     *                                   {@code byte[]} to \(\mathbb{G}_1\). When set to false,
     *                                   it may still be supported.
     */
    public BilinearGroupRequirement(BilinearGroup.Type type, boolean hashIntoG1Needed, boolean hashIntoG2Needed,
                                    boolean hashIntoGTNeeded) {
        this(type, hashIntoG1Needed, hashIntoG2Needed, hashIntoGTNeeded, 1);
    }

    /**
     * Constructor for prime order groups without any requirements for hashing.
     *
     * @param type the desired type of the resulting bilinear group
     */
    public BilinearGroupRequirement(BilinearGroup.Type type) {
        this(type, false, false, false);
    }

    /**
     * Constructor for composite order groups without any requirements for hashing.
     *
     * @param type the desired type of the resulting bilinear group
     * @param numPrimeFactorsOfSize number of prime factors of the resulting groups
     */
    public BilinearGroupRequirement(BilinearGroup.Type type, int numPrimeFactorsOfSize) {
        this(type, false, false, false, numPrimeFactorsOfSize);
    }

    public BilinearGroup.Type getType() {
        return type;
    }

    public boolean isHashIntoG1Needed() {
        return hashIntoG1Needed;
    }

    public boolean isHashIntoG2Needed() {
        return hashIntoG2Needed;
    }

    public boolean isHashIntoGTNeeded() {
        return hashIntoGTNeeded;
    }

    /**
     * Returns how many prime factors the group size shall have.
     */
    public int getNumPrimeFactorsOfSize() {
        return numPrimeFactorsOfSize;
    }
}
