package com.startup.chatapp.model;

public class Person {


    private String uid;
    private String phoneNumber;

    public Person() {
    }

    public Person(String uid, String phoneNumber) {

        this.uid = uid;
        this.phoneNumber = phoneNumber;
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
