package com.bharani.manabu;

public class ContactsModel {
    String name, status, profile_img, uid;

    public ContactsModel() {
    }

    public ContactsModel(String name, String status, String profile_img, String uid) {
        this.name = name;
        this.status = status;
        this.profile_img = profile_img;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProfile_img() {
        return profile_img;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
