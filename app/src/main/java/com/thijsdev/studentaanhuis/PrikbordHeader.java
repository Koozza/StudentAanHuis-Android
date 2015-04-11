package com.thijsdev.studentaanhuis;

public class PrikbordHeader {
    private String title;
    private int id;
    private boolean isMessage;

    public PrikbordHeader(int id, String title, Boolean isMessage) {
        this.id = id;
        this.title = title;
        this.isMessage = isMessage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isMessage() {
        return isMessage;
    }

    public void setMessage(boolean isMessage) {
        this.isMessage = isMessage;
    }
}
