package com.example.formApi.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Users {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String country;
    private String gender;
    private Boolean feature;
    private Boolean agreecond;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Boolean getFeature() {
        return feature;
    }

    public void setFeature(Boolean feature) {
        this.feature = feature;
    }

    public Boolean getAgreecond() {
        return agreecond;
    }

    public void setAgreecond(Boolean agreecond) {
        this.agreecond = agreecond;
    }

    public Users() {
        super();
        this.id = id;
        this.name = name;
        this.email = email;
        this.country = country;
        this.gender = gender;
        this.feature = feature;
        this.agreecond = agreecond;
    }



}
