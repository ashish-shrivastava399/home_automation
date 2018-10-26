package com.iot.app.home;

public class UserDetailsAdapterDataList {
    private String heading, data;
    private int iconId;

    public UserDetailsAdapterDataList(int iconId, String heading, String data) {
        this.heading = heading;
        this.data = data;
        this.iconId = iconId;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }
}
