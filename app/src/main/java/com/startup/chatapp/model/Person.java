package com.startup.chatapp.model;

public class Person {


    private String uid;
    private String phoneNumber;

    public Person(String uid, String phoneNumber, String token) {
        this.uid = uid;
        this.phoneNumber = phoneNumber;
        this.token = token;
    }

    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Person() {
    }



    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


}
