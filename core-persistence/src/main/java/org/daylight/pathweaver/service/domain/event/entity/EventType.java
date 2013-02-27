package org.daylight.pathweaver.service.domain.event.entity;

import java.io.Serializable;

public enum EventType implements Serializable {
    CREATE_LOADBALANCER,
    UPDATE_LOADBALANCER,
    DELETE_LOADBALANCER,
    CREATE_NODE,
    UPDATE_NODE,
    DELETE_NODE,
    ADD_VIRTUAL_IP,
    DELETE_VIRTUAL_IP,
    DELETE_NETWORK_ITEM,
    CREATE_ACCESS_LIST,
    UPDATE_ACCESS_LIST,
    DELETE_ACCESS_LIST,
    SET_CONNECTION_THROTTLE,
    DELETE_CONNECTION_THROTTLE,
    SET_HEALTH_MONITOR,
    DELETE_HEALTH_MONITOR,
    SET_SESSION_PERSISTENCE,
    DELETE_SESSION_PERSISTENCE,
    UPDATE_CONNECTION_LOGGING,
    UPDATE_ERROR_PAGE,
    DELETE_ERROR_PAGE;
    
    private final static long serialVersionUID = 532512316L;
    }   