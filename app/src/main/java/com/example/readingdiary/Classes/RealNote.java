package com.example.readingdiary.Classes;

// класс для записей
public class RealNote implements Note {
    private String path;
    private String author;
    private String title;
    private long id;
    private final int type = 0;
    private double rating;
    String coverPath;



    public RealNote(long id, String path, String author, String title, double rating, String coverPath){
        this.id = id;
        this.path = path;
        this.title = title;
        this.author = author;
        this.rating = rating;
        this.coverPath=coverPath;
    }
    public RealNote(long id, String path, String author, String title, double rating){
        this.id = id;
        this.path = path;
        this.title = title;
        this.author = author;
        this.rating = rating;
        this.coverPath="";
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

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    @Override
    public int getItemType() {
        return type;
    }

    @Override
    public long getID() {
        return id;
    }


}

