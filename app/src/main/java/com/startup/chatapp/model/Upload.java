package com.startup.chatapp.model;

import java.io.Serializable;

public class Upload implements Serializable {

    String name;
    String url;


    String key;

    public Upload() {

    }


    public Upload(String url) {
        this.url = url;
    }

    public Upload(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
