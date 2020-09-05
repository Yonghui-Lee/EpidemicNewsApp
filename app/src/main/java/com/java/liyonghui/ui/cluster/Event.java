package com.java.liyonghui.ui.cluster;

public class Event {

    private String name;
    private String time;
    public  Event() {
    }

    public  Event(String name, String time) {
        this.name = name;
        this.time = time;
    }

    public String getName() {
        return this.name;
    }
    public String getTime() {
        return this.time;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setTime(String time) {
        this.time = time;
    }
}
