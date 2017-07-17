package com.bignerdranch.coyne.udacitymoviesearch;

/**
 * Created by Patrick Coyne on 7/17/2017.
 */

public class Trailer {

    private String title;
    private String key;
    private String site;

    public Trailer(String title, String key, String site) {
        this.title = title;
        this.key = key;
        this.site = site;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }
}
