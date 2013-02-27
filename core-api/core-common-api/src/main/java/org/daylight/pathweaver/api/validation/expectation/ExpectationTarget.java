/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.daylight.pathweaver.api.validation.expectation;

import org.daylight.pathweaver.api.validation.exception.ValidationChainExecutionException;
import org.daylight.pathweaver.api.validation.result.ExpectationResult;
import org.daylight.pathweaver.api.validation.result.ExpectationResultBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ExpectationTarget<T> {

    private final Method targetMethod;
    private final List<Expectation> expectations;

    public ExpectationTarget(Method targetMethod) {
        this.targetMethod = targetMethod;
        this.expectations = new LinkedList<Expectation>();
    }

    public synchronized void addExpectation(Expectation ex) {
        expectations.add(ex);
    }

    public synchronized SelfValidationResult isSatisfied() {
        final StringBuilder strBuff = new StringBuilder();
        boolean valid = true;

        Collections.sort(expectations);

        for (Expectation ex : expectations) {
            if (!ex.hasVerifierSet()) {
                strBuff.append("\nIncomplete: ").append("Expectation #").append(ex.getExpectationId()).append(" - ").append(getTargetName());
                valid = false;
            }
        }

        return new SelfValidationResult(strBuff, valid);
    }

    public String getTargetName() {
        return targetMethod == null ? "Root Object" : targetMethod.getName();
    }

    public boolean targets(Method target) {
        return target == targetMethod;
    }

    public synchronized List<ExpectationResult> validate(T target, Object context) {
        //TODO: Inline? Too complex?
        final Object objectToValidateAgainst = targetMethod == null ? target : invokeMethod(targetMethod, target);

        return validateObject(expectations, objectToValidateAgainst, context);
    }

    private List<ExpectationResult> validateObject(List<Expectation> expectations, Object objectBeingValidated, Object context) throws ValidationChainExecutionException {
        final ExpectationResultBuilder resultBuilder = new ExpectationResultBuilder(getTargetName());
        final List<ExpectationResult> gatheredResults = new LinkedList<ExpectationResult>();

        for (Expectation expectation : expectations) {
            final List<ValidationResult> validationResults = expectation.validate(objectBeingValidated, context);

            for (ValidationResult validationResult : validationResults) {
                if (!validationResult.expectationWasMet()) {
                    resultBuilder.setMessage(validationResult.getMessage());
                    resultBuilder.setPassed(false);

                    gatheredResults.add(resultBuilder.toResult());
                    
                }
            }
        }

        return gatheredResults;
    }

    private Object invokeMethod(Method m, T object) throws ValidationChainExecutionException {
        try {
            return m.invoke(object);
        } catch (IllegalAccessException iae) {
            System.out.println("Fatal exception encountered during validation. Please verify your JVM security model.");
            throw new ValidationChainExecutionException("Fatal exception encountered during validation. Please verify your JVM security model.", iae);
        } catch (IllegalArgumentException iae) {
            System.out.println("This shouldn't happen but if it does, you lose. Please report this as a bug.");
            System.out.println("Object class is: " + object.getClass().getName());
            System.out.println("method we are trying to call: " + m.getName());
            throw new ValidationChainExecutionException("This shouldn't happen but if it does, you lose. Please report this as a bug.", iae);
        } catch (InvocationTargetException ite) {
            System.out.println("encountered during validation chain execution. Pump cause for more details.");
            final Throwable t = ite.getTargetException();
            System.out.println(t.toString());
            throw new ValidationChainExecutionException("Exception \""
                    + t.getMessage()
                    + "\" encountered during validation chain execution. Pump cause for more details.", t);
        }
    }
}
