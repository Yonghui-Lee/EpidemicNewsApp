package com.java.liyonghui.ui.data;

import com.bin.david.form.annotation.SmartColumn;
import com.bin.david.form.annotation.SmartTable;
import com.orm.SugarRecord;

@SmartTable(name="用户信息列表")
public class EpidemicData extends SugarRecord {
    @SmartColumn(id =1,name = "地区")
    private String location;
    private String category;
    @SmartColumn(id =2,name = "确诊")
    private int confirmed;
    @SmartColumn(id =3,name = "治愈")
    private int cured;
    @SmartColumn(id =4,name = "死亡")
    private int dead;
    public EpidemicData(){

    }
    public EpidemicData(String location, String category, int confirmed, int cured, int dead){
        this.location = location;
        this.category = category;
        this.confirmed = confirmed;
        this.cured = cured;
        this.dead = dead;
    }
    public String getLocation(){return location;};
    public String getCategory(){return category;};
    public int getConfirmed(){return confirmed;};
    public int getCured(){return cured;};
    public int getDead(){return dead;};

    public void setLocation(String location) {
        this.location = location;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setConfirmed(int confirmed) {
        this.confirmed = confirmed;
    }

    public void setCured(int cured) {
        this.cured = cured;
    }

    public void setDead(int dead) {
        this.dead = dead;
    }
}
