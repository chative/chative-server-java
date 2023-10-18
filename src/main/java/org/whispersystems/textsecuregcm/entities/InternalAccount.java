package org.whispersystems.textsecuregcm.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class InternalAccount {

    @NotNull
    @JsonProperty
    private String number;

    @NotNull
    @JsonProperty
    private String name;


    @JsonProperty
    private String timeZone;

    @JsonProperty
    private String department;

    @JsonProperty
    private String superior;

    public InternalAccount(String number, String name,  String timeZone, String department, String superior) {
        this.number = number;
        this.name = name;
        this.timeZone = timeZone;
        this.department = department;
        this.superior = superior;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getSuperior() {
        return superior;
    }

    public void setSuperior(String superior) {
        this.superior = superior;
    }
}
