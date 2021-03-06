package org.cryptimeleon.math.structures.groups.lazy;

import org.cryptimeleon.math.structures.groups.GroupElementImpl;

/**
 * Represents the result of generating a group element unformly at random.
 */
class RandomGroupElement extends LazyGroupElement {
    private GroupElementImpl value = null;

    public RandomGroupElement(LazyGroup group) {
        super(group);
    }

    @Override
    protected synchronized void computeConcreteValue() {
        if (value == null)
            value = group.impl.getUniformlyRandomElement();

        setConcreteValue(value);
    }
}
