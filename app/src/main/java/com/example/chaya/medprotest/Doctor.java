package com.example.chaya.medprotest;

/**
 * Created by ChayaSandamali on 9/14/2014.
 */
/* This is the data modelling class for the doctor
* */
public class Doctor {
    private String username, name, medicalCenter, specArea, telNo;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMedicalCenter() {
        return medicalCenter;
    }

    public void setMedicalCenter(String medicalCenter) {
        this.medicalCenter = medicalCenter;
    }

    public String getSpecArea() {
        return specArea;
    }

    public void setSpecArea(String specArea) {
        this.specArea = specArea;
    }

    public String getTelNo() {
        return telNo;
    }

    public void setTelNo(String telNo) {
        this.telNo = telNo;
    }
}
