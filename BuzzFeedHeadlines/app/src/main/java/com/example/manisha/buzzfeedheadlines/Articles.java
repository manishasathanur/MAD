package com.example.manisha.buzzfeedheadlines;

public class Articles {
    String title,publishedAt, urlToImage,description;

    public Articles(String title, String publishedAt, String urlToImage, String description) {
        this.title = title;
        this.publishedAt = publishedAt;
        this.urlToImage = urlToImage;
        this.description = description;
    }

    public Articles(){
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Articles{" +
                "title='" + title + '\'' +
                ", publishedAt='" + publishedAt + '\'' +
                ", urlToImage='" + urlToImage + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
