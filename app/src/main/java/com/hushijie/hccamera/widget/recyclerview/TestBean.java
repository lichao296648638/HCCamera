package com.hushijie.hccamera.widget.recyclerview;

/**
 * Created by zkywalker on 2016/12/25.
 * package:org.zky.zky.recyclerview
 */

public class TestBean {
    private String name;
    private String url;


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

    public TestBean(String name, String url) {
        this.name = name;
        this.url = url;
    }
}
