package com.iot.app.home;

public class AccountData {

    private int imageId;
    private String name, desc;

    public AccountData(int imageId, String name, String desc) {
        this.imageId = imageId;
        this.name = name;
        this.desc = desc;
    }

    public AccountData(int imageId, String name) {
        this.imageId = imageId;
        this.name = name;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}