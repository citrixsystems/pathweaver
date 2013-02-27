package org.daylight.pathweaver.api.validation.verifier;

import java.util.Collection;

public class HaveSizeOfAtMost implements Verifier {

    private final int num;

    public HaveSizeOfAtMost(int num) {
        this.num = num;
    }

    @Override
    public VerifierResult verify(Object obj) {
        boolean verifiedCorrectly = false;

        if (obj instanceof Collection) {
            int countNonNullObjects = 0;

            for (Object object : (Collection) obj) {
                if (object != null) {
                    countNonNullObjects++;
                }
            }

            verifiedCorrectly = countNonNullObjects <= num;
        } else if (obj instanceof String) {
            verifiedCorrectly = ((String) obj).length() <= num;
        }

        return new VerifierResult(verifiedCorrectly);
    }
}
