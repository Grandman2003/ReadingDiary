package com.example.readingdiary.Classes;

public class VariousNotes {
    private String text;
    private String path;
    private long time;
    private boolean changed;
    public VariousNotes(String text, String path, long time, boolean changed){
        this.text = text;
        this.path = path;
        this.changed = changed;
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }
}
