package org.opestack.pathweaver.api.validation.validator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.daylight.pathweaver.api.validation.result.ValidatorResult;
import org.daylight.pathweaver.api.validation.validator.SessionPersistenceValidator;
import org.daylight.pathweaver.api.validation.validator.builder.SessionPersistenceValidatorBuilder;
import org.daylight.pathweaver.core.api.v1.SessionPersistence;
import org.daylight.pathweaver.datamodel.CorePersistenceType;
import org.daylight.pathweaver.service.domain.stub.StubFactory;

import static org.daylight.pathweaver.api.validation.context.HttpRequestType.PUT;

@RunWith(Enclosed.class)
public class SessionPersistenceValidatorTest {
    public static class WhenValidatingPutContext {
        private SessionPersistence sessionPersistence;
        private SessionPersistenceValidator validator;

        @Before
        public void standUp() {
            validator = new SessionPersistenceValidator(new SessionPersistenceValidatorBuilder(new CorePersistenceType()));
            sessionPersistence = StubFactory.createHydratedDataModelSessionPersistence();
        }

        @Test
        public void shouldPassValidationWhenGivenAValidPersistenceObject() {
            ValidatorResult result = validator.validate(sessionPersistence, PUT);
            Assert.assertTrue(result.passedValidation());
        }

        @Test
        public void shouldFailValidationWhenPassingNull() {
            ValidatorResult result = validator.validate(null, PUT);
            Assert.assertFalse(result.passedValidation());
        }

        @Test
        public void shouldFailValidationWhenNoAttributesSet() {
            ValidatorResult result = validator.validate(new SessionPersistence(), PUT);
            Assert.assertFalse(result.passedValidation());
        }

        @Test
        public void shouldAcceptAllCorePersistenceTypes() {
            for (String persistenceType : CorePersistenceType.values()) {
                sessionPersistence.setPersistenceType(persistenceType);
                ValidatorResult result = validator.validate(sessionPersistence, PUT);
                Assert.assertTrue(result.passedValidation());
            }
        }

        @Test
        public void shouldRejectInvalidPersistenceType() {
            sessionPersistence.setPersistenceType("BOGUS_PERSISTENCE_TYPE");
            ValidatorResult result = validator.validate(sessionPersistence, PUT);
            Assert.assertFalse(result.passedValidation());
        }
    }
}
