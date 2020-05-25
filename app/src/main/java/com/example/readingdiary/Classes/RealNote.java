package com.example.readingdiary.Classes;

import android.net.Uri;

// класс для записей
public class RealNote implements Note {
    private String path;
    private String author;
    private String title;
    private String id;
    private final int type = 0;
    private double rating;
    private boolean isPrivate;
    private Uri coverUri;



    public RealNote(String id, String path, String author, String title, double rating, boolean isPrivate, Uri coverUri){
        this.id = id;
        this.path = path;
        this.title = title;
        this.author = author;
        this.rating = rating;
        this.coverUri = coverUri;
        this.isPrivate = isPrivate;

    }
    public RealNote(String id, String path, String author, String title, double rating, boolean isPrivate){
        this.id = id;
        this.path = path;
        this.title = title;
        this.author = author;
        this.rating = rating;
        this.isPrivate = isPrivate;
//        this.coverPath="";
    }


    public String getPath() {
        return path;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public boolean changePrivate(){
        isPrivate = !isPrivate;
        return isPrivate;
    }

    public Uri getCoverUri() {
        return coverUri;
    }

    public void setCoverPath(Uri coverUri) {
        this.coverUri = coverUri;
    }

    @Override
    public int getItemType() {
        return type;
    }

    @Override
    public String getID() {
        return id;
    }


}

