package org.daylight.pathweaver.api.validation.verifier;

public interface Verifier <T> {
    public VerifierResult verify(T obj);
}
