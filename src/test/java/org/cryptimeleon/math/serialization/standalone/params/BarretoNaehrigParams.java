package org.cryptimeleon.math.serialization.standalone.params;

import org.cryptimeleon.math.structures.groups.elliptic.type3.bn.BarretoNaehrigBilinearGroup;
import org.cryptimeleon.math.structures.groups.elliptic.type3.bn.BarretoNaehrigBilinearGroupImpl;
import org.cryptimeleon.math.serialization.standalone.StandaloneTestParams;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BarretoNaehrigParams {
    public static Collection<StandaloneTestParams> get() {
        BarretoNaehrigBilinearGroupImpl bnGroup = new BarretoNaehrigBilinearGroupImpl(100);

        List<StandaloneTestParams> toReturn = new ArrayList<>();
        toReturn.add(new StandaloneTestParams(bnGroup.getG1()));
        toReturn.add(new StandaloneTestParams(bnGroup.getG2()));
        toReturn.add(new StandaloneTestParams(bnGroup.getHashIntoG1()));
        toReturn.add(new StandaloneTestParams(bnGroup));
        toReturn.add(new StandaloneTestParams(bnGroup.getGT()));
        toReturn.add(new StandaloneTestParams(new BarretoNaehrigBilinearGroup(100)));
        return toReturn;
    }
}