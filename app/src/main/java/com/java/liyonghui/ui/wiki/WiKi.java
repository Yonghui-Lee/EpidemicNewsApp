package com.java.liyonghui.ui.wiki;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class WiKi {
    private String hot;
    private String label;
    private String url;
    private String enwiki;
    private String baidu;
    private String zhwiki;
    private ArrayList<String> properties;
    private Bitmap image;
    private ArrayList<Relation> relations = new ArrayList<>();
    public WiKi(){}
    public WiKi(String _hot,String _label,String _url,String _enwiki,String _baidu,String _zhwiki,
                ArrayList<String> properties, Bitmap _image)
    {
        this.hot = _hot;
        this.label = _label;
        this.url = _url;
        this.enwiki = _enwiki;
        this.baidu =_baidu;
        this.zhwiki = _zhwiki;
        this.properties = properties;
        this.image = _image;
    }

    public void add_relation(Relation R){
        relations.add(R);
    }

    public ArrayList<Relation> getRelations() {
        return relations;
    }

    public Bitmap getImage(){
        return image;
    }
    public ArrayList<String> getProperties(){
        return properties;
    }
    public String return_value(String kind){
        switch (kind) {
            case "hot":
                return hot;
            case "label":
                return label;
            case "url":
                return url;
            case "enwiki":
                return enwiki;
            case "baidu":
                return baidu;
            case "zhwiki":
                return zhwiki;
            default:
                return "";
        }
    }
}