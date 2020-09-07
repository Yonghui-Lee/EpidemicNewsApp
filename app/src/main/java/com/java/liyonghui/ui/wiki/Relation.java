package com.java.liyonghui.ui.wiki;

public class Relation {
    private String relation;
    private String url;
    private String label;
    private String forward;
    public Relation(){}
    public Relation(String _relation,String _url,String _label,String _forward){
        relation = _relation;
        url = _url;
        label = _label;
        forward = _forward;
    }
    public String getUrl()
    {
        return url;
    }
    public String getRelation()
    {
        return relation;
    }
    public String getLabel()
    {
        return label;
    }
    public String getForward()
    {
        return forward;
    }
}