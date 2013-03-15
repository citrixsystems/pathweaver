package org.daylight.pathweaver.api.validation;

import org.daylight.pathweaver.api.validation.expectation.ExpectationTarget;
import org.daylight.pathweaver.api.validation.result.ExpectationResult;
import org.daylight.pathweaver.api.validation.result.ExpectationResultBuilder;
import org.daylight.pathweaver.api.validation.result.ValidatorResult;

import java.util.LinkedList;
import java.util.List;

public class ValidatorImpl<ObjectTypeToValidate> implements Validator<ObjectTypeToValidate> {

    private final List<ExpectationTarget<ObjectTypeToValidate>> validationTargetList;

    public ValidatorImpl(List<ExpectationTarget<ObjectTypeToValidate>> validationTargetList) {
        this.validationTargetList = validationTargetList;
    }

    @Override
    public synchronized ValidatorResult validate(ObjectTypeToValidate object, Object context) {
        if (object == null) {
            return generateEmptyResult();
        }

        final List<ExpectationResult> gatheredResults = new LinkedList<ExpectationResult>();

        for (ExpectationTarget<ObjectTypeToValidate> target : validationTargetList) {
            gatheredResults.addAll(target.validate(object, context));
        }

        return new ValidatorResult(gatheredResults);
    }

    /**
     * Used for silent failure in the result of a null root element
     */
    private static ValidatorResult generateEmptyResult() {
        final List<ExpectationResult> gatheredResults = new LinkedList<ExpectationResult>();
        final ExpectationResultBuilder resultBuilder = new ExpectationResultBuilder("Root");

        resultBuilder.setMessage("");
        resultBuilder.setPassed(true);

        gatheredResults.add(resultBuilder.toResult());

        return new ValidatorResult(gatheredResults);
    }
}
