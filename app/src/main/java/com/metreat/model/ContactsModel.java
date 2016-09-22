package com.metreat.model;

/**
 * Created by vinove on 9/8/16.
 */
public class ContactsModel implements Comparable<ContactsModel>{
    String Image, Name,phoneNumber;

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public int compareTo(ContactsModel another) {
        return this.Name.compareTo(another.getName());
    }
}
