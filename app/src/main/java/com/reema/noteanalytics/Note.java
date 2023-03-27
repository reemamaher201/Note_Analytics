package com.reema.noteanalytics;

import java.io.Serializable;

public class Note implements Serializable {
    String id;
    String name;
     String category;


    private  Note(){}
    Note(String id, String name, String category) {
        this.id = id;
        this.name = name;
        this.category = category;

    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


}