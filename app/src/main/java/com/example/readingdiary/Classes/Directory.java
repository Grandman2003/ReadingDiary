package com.example.readingdiary.Classes;


// класс для директорий
public class Directory implements Note {
    private long id;
    private String directory;
    private final int type = 1;
    public Directory(long id, String directory){
        this.id = id;
        this.directory = directory;
    }

    public String getDirectory() {
        return directory;
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
