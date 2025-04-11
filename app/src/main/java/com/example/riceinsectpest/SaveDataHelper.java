package com.example.riceinsectpest;

public class SaveDataHelper {

    String img, disease, treatment, area, date, saveID,latlang;


    public SaveDataHelper() {

    }

    public SaveDataHelper(String img, String disease, String treatment, String area, String date, String saveID, String latlang) {
        this.img = img;
        this.disease = disease;
        this.treatment = treatment;
        this.area = area;
        this.date = date;
        this.saveID = saveID;
        this.latlang = latlang;
    }

    public String getLatlang() {
        return latlang;
    }

    public void setLatlang(String latlang) {
        this.latlang = latlang;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSaveID() {
        return saveID;
    }

    public void setSaveID(String saveID) {
        this.saveID = saveID;
    }


}
