package com.example.riceinsectpest;


public class SavePlantation {

    String plantationName,address, longAddress, latAddress, plantationID;

    public SavePlantation() {

    }

    public SavePlantation(String plantationName, String address,String longAddress, String latAddress, String plantationID) {
        this.plantationName = plantationName;
        this.address = address;
        this.plantationID = plantationID;
        this.longAddress = longAddress;
        this.latAddress = latAddress;

    }

    public String getPlantationName() {
        return plantationName;
    }

    public void setPlantationName(String plantationName) {
        this.plantationName = plantationName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLongAddress() {
        return longAddress;
    }

    public void setLongAddress(String longAddress) {
        this.longAddress = longAddress;
    }

    public String getLatAddress() {
        return latAddress;
    }

    public void setLatAddress(String latAddress) {
        this.latAddress = latAddress;
    }

    public String getPlantationID() {
        return plantationID;
    }

    public void setPlantationID(String plantationID) {
        this.plantationID = plantationID;
    }



}
