package com.malikfoundation.purworejodisasterreporter;

/**
 * Created by malikfoundation on 8/26/2016.
 */
public class DataModel {
    private String bencana;
    private String deskripsi;
    private String level;
    private double latitude;
    private double longitude;
    private String datetime;
    private String image;

    public String getBencana() {
        return bencana;
    }

    public void setBencana(String bencana) {
        this.bencana = bencana;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getImage() {
        return "https://188.166.190.247/" + image;
    }

//    public String getImage() {
//        return "http://192.168.100.40/skripsi/" + image;
//    }

    public void setImage(String image) {
        this.image = image;
    }
}
