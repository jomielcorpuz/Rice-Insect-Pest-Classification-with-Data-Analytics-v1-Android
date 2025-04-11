package com.example.riceinsectpest;


public class SaveBarangay {

    String province,municipality,brgy, brgyID;

    public SaveBarangay() {

    }

    public SaveBarangay(String province, String municipality, String brgy, String brgyID) {
        this.province = province;
        this.municipality = municipality;
        this.brgy = brgy;
        this.brgyID = brgyID;

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

    public String getBrgy() {
        return brgy;
    }

    public void setBrgy(String brgy) {
        this.brgy = brgy;
    }

    public String getBrgyID() {
        return brgyID;
    }

    public void setBrgyID(String brgyID) {
        this.brgyID = brgyID;
    }



}
