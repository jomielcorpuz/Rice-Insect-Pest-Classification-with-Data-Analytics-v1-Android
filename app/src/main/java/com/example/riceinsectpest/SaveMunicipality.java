package com.example.riceinsectpest;


public class SaveMunicipality {

    String province,municipality,munID;

    public SaveMunicipality() {

    }

    public SaveMunicipality(String province, String municipality, String munID) {
        this.province = province;
        this.municipality = municipality;
        this.munID = munID;

    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public String getMunID() {
        return munID;
    }

    public void setMunID(String munID) {
        this.munID = munID;
    }



}
