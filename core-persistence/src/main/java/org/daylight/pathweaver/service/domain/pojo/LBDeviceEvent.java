package org.daylight.pathweaver.service.domain.pojo;

import java.io.Serializable;

public class LBDeviceEvent
    implements Serializable
{

    private final static long serialVersionUID = 532512316L;

    private String eventType;

    private String paramLine;


    public String getEventType() {
        return eventType;
    }

    public void setEventType(String value) {
        this.eventType = value;
    }

    public String getParamLine() {
        return paramLine;
    }

    public void setParamLine(String value) {
        this.paramLine = value;
    }

}
