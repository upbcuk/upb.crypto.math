package de.upb.crypto.math.pairings.type1.supersingular;

import de.upb.crypto.math.pairings.generic.ExtensionFieldElement;
import de.upb.crypto.math.pairings.generic.PairingTargetGroupElementImpl;
import de.upb.crypto.math.pairings.generic.PairingTargetGroupImpl;

/**
 * @see PairingTargetGroupElementImpl
 */
public class SupersingularTargetGroupElementImpl extends PairingTargetGroupElementImpl {

    public SupersingularTargetGroupElementImpl(PairingTargetGroupImpl g, ExtensionFieldElement fe) {
        super(g, fe);
    }
}