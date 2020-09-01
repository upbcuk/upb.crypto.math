package de.upb.crypto.math.standalone.test;

import de.upb.crypto.math.structures.groups.count.CountingGroup;

public class CountingGroupParams {

    public static StandaloneTestParams get() {
        return new StandaloneTestParams(new CountingGroup("STest", 100000));
    }
}
