package org.daylight.pathweaver.api.validation.verifier;

public class MustBeInArray implements Verifier {
    private Object[] values;

    public MustBeInArray(Object[] values) {
        try {
            this.values = values.clone();
        } catch (Exception ex) {
            this.values = new Object[values.length];
        }
    }

    @Override
    public VerifierResult verify(Object obj) {
        boolean isInEnumSet = false;
        for (Object value : values) {
            if (value.equals(obj)) {
                isInEnumSet = true;
                break;
            }
        }
        return new VerifierResult(isInEnumSet);
    }
}
