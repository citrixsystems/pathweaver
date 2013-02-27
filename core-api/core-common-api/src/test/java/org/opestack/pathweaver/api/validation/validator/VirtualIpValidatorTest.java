package org.opestack.pathweaver.api.validation.validator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.daylight.pathweaver.api.validation.result.ValidatorResult;
import org.daylight.pathweaver.api.validation.validator.VirtualIpValidator;
import org.daylight.pathweaver.api.validation.validator.builder.VirtualIpValidatorBuilder;
import org.daylight.pathweaver.core.api.v1.IpVersion;
import org.daylight.pathweaver.core.api.v1.VipType;
import org.daylight.pathweaver.core.api.v1.VirtualIp;
import org.daylight.pathweaver.service.domain.stub.StubFactory;

import static org.daylight.pathweaver.api.validation.context.HttpRequestType.POST;

@RunWith(Enclosed.class)
public class VirtualIpValidatorTest {

    public static class WhenValidatingPostContext {
        private VirtualIp virtualIp;
        private VirtualIpValidator validator;

        @Before
        public void standUp() {
            validator = new VirtualIpValidator(new VirtualIpValidatorBuilder());
        }

        @Test
        public void shouldFailValidationWhenSpecifyingAddress() {
            virtualIp = new VirtualIp();
            virtualIp.setAddress("1.1.1.1");
            ValidatorResult result = validator.validate(virtualIp, POST);
            Assert.assertFalse(result.passedValidation());
        }

        @Test
        public void shouldPassValidationWhenGivenASharedVip() {
            virtualIp = StubFactory.createSharedDataModelVipForPost();
            ValidatorResult result = validator.validate(virtualIp, POST);
            Assert.assertTrue(result.passedValidation());
        }

        @Test
        public void shouldFailValidationWhenGivenASharedVipWithIpVersion() {
            virtualIp = StubFactory.createSharedDataModelVipForPost();
            virtualIp.setIpVersion(IpVersion.IPV4);
            ValidatorResult result = validator.validate(virtualIp, POST);
            Assert.assertFalse(result.passedValidation());
        }

        @Test
        public void shouldFailValidationWhenGivenASharedVipWithType() {
            virtualIp = StubFactory.createSharedDataModelVipForPost();
            virtualIp.setType(VipType.PUBLIC);
            ValidatorResult result = validator.validate(virtualIp, POST);
            Assert.assertFalse(result.passedValidation());
        }

        @Test
        public void shouldPassValidationWhenGivenAVipWithOnlyIpVersion() {
            virtualIp = StubFactory.createDataModelVipWithIpVersionForPost();
            ValidatorResult result = validator.validate(virtualIp, POST);
            Assert.assertTrue(result.passedValidation());
        }

        @Test
        public void shouldPassValidationWhenGivenAVipWithOnlyType() {
            virtualIp = StubFactory.createDataModelVipWithVipTypeForPost();
            ValidatorResult result = validator.validate(virtualIp, POST);
            Assert.assertTrue(result.passedValidation());
        }

        @Test
        public void shouldPassValidationWhenGivenAVipWithIpVersionAndTypeSet() {
            virtualIp = new VirtualIp();
            virtualIp.setIpVersion(IpVersion.IPV4);
            virtualIp.setType(VipType.PUBLIC);
            ValidatorResult result = validator.validate(virtualIp, POST);
            Assert.assertTrue(result.passedValidation());
        }
    }
}
