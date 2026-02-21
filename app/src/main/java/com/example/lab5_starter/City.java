package com.example.lab5_starter;

import java.io.Serializable;

// Movie object
public class City implements Serializable {

    // attributes
    private String docid;
    private String name;
    private String province;

    // constructor
    public City() {}

    public City(String name, String province) {
        this.name = name;
        this.province = province;
    }

    public City(String docid, String name, String province) {
        this.docid = docid;
        this.name = name;
        this.province = province;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDocid() { return docid; }
    public void setDocid(String docid) { this.docid = docid; }
}
