package org.opestack.pathweaver.api.mapper.dozer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.daylight.pathweaver.core.api.v1.SessionPersistence;
import org.daylight.pathweaver.datamodel.CorePersistenceType;
import org.daylight.pathweaver.service.domain.stub.StubFactory;

@RunWith(Enclosed.class)
public class SessionPersistenceMappingTest {

    public static class WhenMappingFromApiToDomain extends MappingBase {
        private org.daylight.pathweaver.core.api.v1.SessionPersistence apiSessionPersistence;
        private org.daylight.pathweaver.service.domain.entity.SessionPersistence domainSessionPersistence;

        @Before
        public void setUp() throws Exception {
            apiSessionPersistence = StubFactory.createHydratedDataModelSessionPersistence();
            domainSessionPersistence = mapper.map(apiSessionPersistence, org.daylight.pathweaver.service.domain.entity.SessionPersistence.class);
        }

        @Test
        public void shouldNotFailWhenApiPersistenceIsEmpty() {
            apiSessionPersistence = new org.daylight.pathweaver.core.api.v1.SessionPersistence();
            try {
                domainSessionPersistence = mapper.map(apiSessionPersistence, org.daylight.pathweaver.service.domain.entity.SessionPersistence.class);
            } catch (Exception e) {
                Assert.fail("Empty API session persistence caused this exception");
            }
        }

        @Test
        public void shouldMapAllAttributes() {
            Assert.assertEquals(apiSessionPersistence.getPersistenceType(), domainSessionPersistence.getPersistenceType());
        }

        @Test
        public void shouldMapAttributesToNullWhenApiPersistenceIsEmpty() {
            apiSessionPersistence = new SessionPersistence();
            domainSessionPersistence = mapper.map(apiSessionPersistence, org.daylight.pathweaver.service.domain.entity.SessionPersistence.class);

            Assert.assertNull(apiSessionPersistence.getPersistenceType());
        }
    }

    public static class WhenMappingFromDomainToApi extends MappingBase {
        private org.daylight.pathweaver.service.domain.entity.SessionPersistence domainSessionPersistence;
        private org.daylight.pathweaver.core.api.v1.SessionPersistence apiSessionPersistence;

        @Before
        public void setUp() throws Exception {
            domainSessionPersistence = StubFactory.createHydratedDomainSessionPersistence();
            apiSessionPersistence = mapper.map(domainSessionPersistence, org.daylight.pathweaver.core.api.v1.SessionPersistence.class);
        }

        @Test
        public void shouldNotFailWhenDomainPersistenceIsEmpty() {
            domainSessionPersistence = new org.daylight.pathweaver.service.domain.entity.SessionPersistence();
            try {
                apiSessionPersistence = mapper.map(domainSessionPersistence, org.daylight.pathweaver.core.api.v1.SessionPersistence.class);
            } catch (Exception e) {
                Assert.fail("Empty domain session persistence caused this exception");
            }
        }

        @Test
        public void shouldMapAllAttributes() {
            Assert.assertEquals(domainSessionPersistence.getPersistenceType(), apiSessionPersistence.getPersistenceType());
        }

        @Test
        public void shouldMapAttributesToDefaultsWhenNoAttributesSet() {
            domainSessionPersistence = new org.daylight.pathweaver.service.domain.entity.SessionPersistence();
            apiSessionPersistence = mapper.map(domainSessionPersistence, org.daylight.pathweaver.core.api.v1.SessionPersistence.class);

            Assert.assertEquals(CorePersistenceType.HTTP_COOKIE, apiSessionPersistence.getPersistenceType());
        }

        @Test
        public void shouldNotMapExtensionAttributes() {
            Assert.assertTrue(apiSessionPersistence.getOtherAttributes().isEmpty());
        }
    }
}

