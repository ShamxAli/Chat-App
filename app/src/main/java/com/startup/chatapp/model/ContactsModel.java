package com.startup.chatapp.model;

import java.util.Objects;


public class ContactsModel {

    private String contactName;
    private String contactNumber;
    private String uid;



    /*Constructor*/
   public ContactsModel(String name, String number, String uid) {
        contactName = name;
        contactNumber = number;
       this.uid = uid;
    }

    /*Getters Setters */
    public String getContactName() {
        return contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getUid() {
        return uid;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    /*For remove duplication*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContactsModel that = (ContactsModel) o;
        return Objects.equals(contactNumber, that.contactNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash( contactNumber);
    }



}
