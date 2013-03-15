package org.daylight.pathweaver.api.validation.verifier;


import org.daylight.pathweaver.api.validation.expectation.ValidationResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MustHaveLengthVerifier implements Verifier {
    private int length;

    public MustHaveLengthVerifier(int length) {
        this.length = length;
    }

    @Override
    public VerifierResult verify(Object obj) {
        if (obj instanceof String){
            return new VerifierResult(((String)obj).length() <= length);
        }

        if (obj instanceof Collection){
            return new VerifierResult(((Collection)obj).size() <= length);
        }

        if (obj instanceof Object[]){
            return new VerifierResult(((Object[])obj).length <= length);
        }

        List<ValidationResult> results = new ArrayList();
        results.add(new ValidationResult(false, "Inappropriate type for length comparison."));
        return new VerifierResult(false, results);
    }
}