package com.example.servingwebcontent;

import scala.actors.threadpool.TimeUnit;

public class SearchResult {
    String title;
    String URL;
    String citacao;

    public SearchResult(String title, String URL, String citacao){
        this.title = title;
        this.URL = URL;
        this.citacao = citacao;
    }

    public String getTitle() {
        return title;
    }
    public String getURL() {
        return URL;
    }
    public String getCitacao() {
        return citacao;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setURL(String uRL) {
        URL = uRL;
    }
    public void setCitacao(String citacao) {
        this.citacao = citacao;
    }

    

}
