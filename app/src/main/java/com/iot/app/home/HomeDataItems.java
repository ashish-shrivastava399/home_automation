package com.iot.app.home;

public class HomeDataItems {
    String title;
    boolean state;
    int imgIDOn, imgIDOff;

    public HomeDataItems(String title, boolean state, int imgIDOn, int imgIDOff) {
        this.title = title;
        this.state = state;
        this.imgIDOn = imgIDOn;
        this.imgIDOff = imgIDOff;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public int getImgIDOn() {
        return imgIDOn;
    }

    public void setImgIDOn(int imgIDOn) {
        this.imgIDOn = imgIDOn;
    }

    public int getImgIDOff() {
        return imgIDOff;
    }

    public void setImgIDOff(int imgIDOff) {
        this.imgIDOff = imgIDOff;
    }
}
