package com.java.liyonghui.channel;

public class Channel {

    private String name;
    private boolean Selected;
    public Channel() {
    }

    public Channel(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
    public boolean getSelected() {
        return this.Selected;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setSelected(boolean Selected) {
        this.Selected = Selected;
    }
}
