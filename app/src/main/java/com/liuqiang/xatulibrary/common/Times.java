package com.liuqiang.xatulibrary.common;

import org.litepal.crud.DataSupport;

/**
 * Created by liuqiang on 15-9-16.
 */
public class Times extends DataSupport {

    private int id;
    private DoubanBook doubanBook;
    private String date;
    private int pages;

    public DoubanBook getDoubanBook() {
        return doubanBook;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDoubanBook(DoubanBook doubanBook) {
        this.doubanBook = doubanBook;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    private String note;

}
