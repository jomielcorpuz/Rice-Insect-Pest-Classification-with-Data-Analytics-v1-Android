package com.example.riceinsectpest;


public class SavePurok {

    String province,municipality,brgy,purok, purokID;

    public SavePurok() {

    }

    public SavePurok(String province, String municipality, String brgy, String purok, String purokID) {
        this.province = province;
        this.municipality = municipality;
        this.brgy = brgy;
        this.purok = purok;
        this.purokID = purokID;

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

    public String getPurok() {
        return purok;
    }

    public void setPurok(String purok) {
        this.purok = purok;
    }

    public String getPurokID() {
        return purokID;
    }

    public void setPurokID(String purokID) {
        this.purokID = purokID;
    }



}
