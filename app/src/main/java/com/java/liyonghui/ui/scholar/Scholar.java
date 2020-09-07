package com.java.liyonghui.ui.scholar;

import android.graphics.Bitmap;

public class  Scholar{
    private Bitmap avatar;
    private double activity;
    private double citations;
    private double diversity;
    private double gindex;
    private double hindex;
    private double pubs;
    private double sociability;
    private String name;
    private String name_zh;
    private String address;
    private String affiliation;
    private String affiliation_zh;
    private String bio;
    private String edu;
    private String email;
    private String homepage;
    private String note;
    private String position;
    private String work;
    private boolean is_passedaway;
    public Scholar(){}
    public Scholar(Bitmap avatar, double activity, double citations, double diversity, double gindex, double hindex,
                   double pubs, double sociability, String name, String name_zh, String address,
                   String affiliation, String affiliation_zh, String bio, String edu, String email,
                   String homepage, String note, String position, String work, boolean is_passedaway)
    {
        this.avatar = avatar;
        this.activity = activity;
        this.citations = citations;
        this.diversity = diversity;
        this.gindex = gindex;
        this.hindex = hindex;
        this.pubs = pubs;
        this.sociability = sociability;
        this.name = name;
        this.name_zh = name_zh;
        this.address = address;
        this.affiliation = affiliation;
        this.affiliation_zh = affiliation_zh;
        this.bio = bio;
        this.edu = edu;
        this.email = email;
        this.homepage = homepage;
        this.note = note;
        this.position = position;
        this.work = work;
        this.is_passedaway = is_passedaway;
    }
    public double getActivity() {
        return activity;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public String getAffiliation_zh() {
        return affiliation_zh;
    }

    public String getAddress() {
        return address;
    }

    public Bitmap getAvatar() {
        return avatar;
    }

    public String getBio() {
        return bio;
    }

    public double getCitations() {
        return citations;
    }

    public double getDiversity() {
        return diversity;
    }

    public String getEdu() {
        return edu;
    }

    public String getEmail() {
        return email;
    }

    public double getGindex() {
        return gindex;
    }

    public double getHindex() {
        return hindex;
    }

    public String getHomepage() {
        return homepage;
    }

    public String getName() {
        return name;
    }

    public String getName_zh() {
        return name_zh;
    }

    public String getNote() {
        return note;
    }

    public String getPosition() {
        return position;
    }

    public double getSociability() {
        return sociability;
    }

    public String getWork() {
        return work;
    }

    public double getPubs() {
        return pubs;
    }

    public boolean getIs_passedaway() {
        return is_passedaway;
    }
}