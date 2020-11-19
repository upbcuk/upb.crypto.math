package de.upb.crypto.math.interfaces.structures.group.impl;

import de.upb.crypto.math.interfaces.structures.Ring;
import de.upb.crypto.math.interfaces.structures.RingElement;
import de.upb.crypto.math.serialization.RepresentableRepresentation;
import de.upb.crypto.math.serialization.Representation;

/**
 * Common base class for ring subgroups (additive/unit groups).
 */
public abstract class RingGroupImpl implements GroupImpl {
    protected final Ring ring;

    /**
     * Construct a ring group from a given ring.
     *
     * @param ring the ring to wrap
     */
    public RingGroupImpl(Ring ring) {
        this.ring = ring;
    }

    public RingGroupImpl(Representation repr) {
        ring = (Ring) repr.repr().recreateRepresentable();
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass().equals(this.getClass()) && ((RingGroupImpl) obj).ring.equals(this.ring);
    }

    @Override
    public int hashCode() {
        return ring.hashCode();
    }

    @Override
    public Representation getRepresentation() {
        return new RepresentableRepresentation(ring);
    }

    public Ring getRing() {
        return ring;
    }

    public abstract RingGroupElementImpl getElement(RingElement ringElement);

    /**
     * Common base class of ring subgroup elements
     */
    public abstract class RingGroupElementImpl implements GroupElementImpl {
        protected final RingElement element;

        public RingGroupElementImpl(RingElement e) {
            element = e;
        }

        @Override
        public boolean equals(Object obj) {
            return //element.equals(obj) || //this is deliberately not in because the equals()-induced relation would not be symmetric anymore with it.
                    obj instanceof RingGroupElementImpl && element.equals(((RingGroupElementImpl) obj).element);
        }

        @Override
        public int hashCode() {
            return element.hashCode();
        }

        /**
         * Projects the element of this group back to its ring
         *
         * @return the same element, interpreted as a ring element
         */
        public RingElement projectToRing() {
            return element;
        }

        @Override
        public Representation getRepresentation() {
            return element.getRepresentation();
        }

        @Override
        public String toString() {
            return element.toString();
        }
    }

    @Override
    public String toString() {
        return ring.toString();
    }
}
