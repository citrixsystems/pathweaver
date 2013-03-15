package org.daylight.pathweaver.api.validation.validator;

import org.daylight.pathweaver.api.validation.result.ExpectationResult;
import org.daylight.pathweaver.api.validation.result.ValidatorResult;

import java.util.LinkedList;
import java.util.List;

public final class ValidatorUtilities {

    public static ValidatorResult removeEmptyMessages(ValidatorResult result) {
        List<ExpectationResult> filteredResults = new LinkedList<ExpectationResult>();

        for (ExpectationResult expectationResult : result.getValidationResults()) {
            if (expectationResult.getMessage() != null) {
                filteredResults.add(expectationResult);
            }
        }

        return new ValidatorResult(filteredResults);
    }
}
