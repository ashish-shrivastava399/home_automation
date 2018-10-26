package com.iot.app.home;

import com.iot.app.R;

import java.util.ArrayList;
import java.util.List;

public class LangData {
    private String name, code;
    private int imageId;

    public LangData(String name, String code, int imageId) {
        this.name = name;
        this.code = code;
        this.imageId = imageId;
    }

    public static List<LangData> getDefaultList() {
        List<LangData> defaultList = new ArrayList<>();
        defaultList.add(new LangData("English", "en", R.drawable.flag_usa));
        defaultList.add(new LangData("Українська", "uk", R.drawable.flag_ukraine));
        defaultList.add(new LangData("русский", "ru", R.drawable.flag_russia));
        defaultList.add(new LangData("हिंदी", "hi", R.drawable.flag_india));
        return defaultList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imgId) {
        this.imageId = imgId;
    }
}

