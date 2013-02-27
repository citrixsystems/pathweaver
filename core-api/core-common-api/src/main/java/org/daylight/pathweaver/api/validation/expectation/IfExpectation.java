package org.daylight.pathweaver.api.validation.expectation;

public interface IfExpectation extends EmptyExpectation {
    OngoingExpectation<ThenExpectation> if_();
}
