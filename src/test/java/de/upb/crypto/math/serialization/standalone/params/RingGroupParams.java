package de.upb.crypto.math.serialization.standalone.params;

import de.upb.crypto.math.structures.groups.RingAdditiveGroupImpl;
import de.upb.crypto.math.structures.groups.RingUnitGroupImpl;
import de.upb.crypto.math.serialization.standalone.StandaloneTestParams;
import de.upb.crypto.math.structures.rings.zn.Zp;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

public class RingGroupParams {
    public static List<StandaloneTestParams> get() {
        Zp zp = new Zp(BigInteger.valueOf(17));
        return Arrays.asList(
                new StandaloneTestParams(zp.asUnitGroup()),
                new StandaloneTestParams(zp.asAdditiveGroup()),
                new StandaloneTestParams(new RingUnitGroupImpl(zp)),
                new StandaloneTestParams(new RingAdditiveGroupImpl(zp))
        );
    }
}
