package org.daylight.pathweaver.api.validation.verifier;

public class MustBeIntegerInRange implements Verifier<Integer> {
    private final Integer floor;
    private final Integer ceiling;

    public MustBeIntegerInRange(Integer floor, Integer ceiling) {
        this.floor = floor;
        this.ceiling = ceiling;
    }

    @Override
    public VerifierResult verify(Integer obj) {
        if(obj == null) {
            return new VerifierResult(false);
        }

        return new VerifierResult(floor <= obj && obj <= ceiling);
    }
}
