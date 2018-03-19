package tn.octave.coloc;

import java.util.Date;

/**
 * Created by squall on 18/03/2018.
 */

public class Personne {

    //String uid;
    String fullName;
    String birth;
    String gender;
    String status;
    String mail;
    String phone;
    String picture;
    String password;


    public Personne() {
        this.fullName = fullName;
        this.birth = birth;
        this.gender = gender;
        this.status = status;
        this.mail = mail;
        this.phone = phone;
        this.picture = picture;
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
