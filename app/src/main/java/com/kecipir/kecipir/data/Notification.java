package com.kecipir.kecipir.data;

/**
 * Created by Kecipir-Dev on 10/10/2016.
 */

public class Notification {

    private String title;
    private String url;
    private String description;
    private String postDate;
    private String imgLink;


    public Notification(){}

    public Notification(String title, String url, String postDate) {
        this.title = title;
        this.url = url;
        this.postDate = postDate;
    }

    public Notification(String title, String url, String description, String postDate, String imgLink) {
        this.title = title;
        this.url = url;
        this.description = description;
        this.postDate = postDate;
        this.imgLink = imgLink;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getImgLink() {
        return imgLink;
    }

    public void setImgLink(String imgLink) {
        this.imgLink = imgLink;
    }
}
