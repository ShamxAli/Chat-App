package com.startup.chatapp.model;

import java.util.Objects;


public class ContactsModel {

    private String contactName;
    private String contactNumber;


    /*Constructor*/
   public ContactsModel(String name, String number) {
        contactName = name;
        contactNumber = number;
        Integer sklcsndk;
    }


    /*For remove duplication*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContactsModel that = (ContactsModel) o;
        return Objects.equals(contactName, that.contactName) &&
                Objects.equals(contactNumber, that.contactNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contactName, contactNumber);
    }

    /*Getters*/
    public String getContactName() {
        return contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

}
