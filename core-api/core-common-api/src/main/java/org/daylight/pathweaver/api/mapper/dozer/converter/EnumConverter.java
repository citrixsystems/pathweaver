package org.daylight.pathweaver.api.mapper.dozer.converter;

import org.dozer.CustomConverter;
import org.daylight.pathweaver.core.api.v1.IpVersion;
import org.daylight.pathweaver.service.domain.entity.*;
import org.daylight.pathweaver.service.domain.exception.NoMappableConstantException;

public class EnumConverter implements CustomConverter {

    @Override
    public Object convert(Object existingDestinationFieldValue, Object sourceFieldValue, Class destinationClass, Class sourceClass) {
        if (sourceFieldValue == null) {
            return null;
        }

        if (sourceFieldValue instanceof IpVersion && destinationClass == org.daylight.pathweaver.service.domain.entity.IpVersion.class) {
            return org.daylight.pathweaver.service.domain.entity.IpVersion.fromDataType((IpVersion) sourceFieldValue);
        }

        throw new NoMappableConstantException("Cannot map source type: " + sourceClass.getName());
    }
}
