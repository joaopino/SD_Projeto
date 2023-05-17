package com.example.servingwebcontent;

import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true) // This indicates that any properties not bound in this type should be
public class HackerNewsItemRecord {
    private Integer id;
    private Boolean deleted;
    private String type;
    private String by;
    private Long time;
    private String text;
    private Boolean dead;
    private String parent;
    private Integer poll;
    private List kids;
    private String url;
    private Integer score;
    private String title;
    private List parts;
    private Integer descendants;

    public String getText() {
        return text;
    }

    public String getTitle() {
        return title;
    }

    public Object getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }
}

