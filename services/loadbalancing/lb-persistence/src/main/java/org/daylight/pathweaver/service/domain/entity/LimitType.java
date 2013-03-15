package org.daylight.pathweaver.service.domain.entity;

import javax.persistence.*;
import java.io.Serializable;

@javax.persistence.Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name = "vendor",
        discriminatorType = DiscriminatorType.STRING
)
@DiscriminatorValue("CORE")
@Table(name = "limit_type")
public class LimitType implements Serializable {
    private final static long serialVersionUID = 532512316L;

    @Id
    @Column(name = "name", unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountLimitType name;

    @Column(name = "default_value", nullable = false)
    private int defaultValue;

    @Column(name = "description", nullable = false)
    private String description;

    public AccountLimitType getName() {
        return name;
    }

    public void setName(AccountLimitType name) {
        this.name = name;
    }

    public int getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(int defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        String format = "{name=\"%s\" description=\"%s\" defaultValue=\"%s\" }";
        String tname = (this.name == null) ? "null" : this.name.toString();
        String tdescription = (this.description == null) ? "null" : this.description;
        String tdefaultValue = String.format("%d", this.defaultValue);
        return String.format(format, tname, tdescription, tdefaultValue);
    }
}