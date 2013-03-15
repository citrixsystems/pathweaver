package org.daylight.pathweaver.service.domain.service;


import org.daylight.pathweaver.service.domain.entity.Node;
import org.daylight.pathweaver.service.domain.exception.BadRequestException;

import java.util.Set;

public interface BlacklistService {

    void verifyNoBlacklistNodes(Set<Node> nodes) throws BadRequestException;

}
