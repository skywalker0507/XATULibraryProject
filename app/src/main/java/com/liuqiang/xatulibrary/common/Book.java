package com.liuqiang.xatulibrary.common;

import java.io.Serializable;

/**
 * Created by liuqiang on 15-7-26.
 */
public class Book implements Serializable {
    private String Bookname;
    private String author;
    private String maskNumber;
    private String beginTime;
    private String endTime;
    private String times;
    private String url;
    private String number_all;
    private String number_free;
    private String ISBN_number;
    private String bookNumber;
    private String check;



    public void setBookname(String bookname) {
        Bookname = bookname;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setMaskNumber(String maskNumber) {
        this.maskNumber = maskNumber;
    }


    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public String getBookname() {
        return Bookname;
    }

    public String getMaskNumber() {
        return maskNumber;
    }


    public String getBeginTime() {
        return beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getAuthor() {
        return author;
    }

    public String getUrl() {
        return url;
    }

    public String getTimes() {
        return times;
    }

    public void setNumber_free(String number_free) {
        this.number_free = number_free;
    }

    public String getNumber_free() {
        return number_free;
    }

    public void setNumber_all(String number_all) {
        this.number_all = number_all;
    }

    public String getNumber_all() {
        return number_all;
    }

    public void setISBN_number(String ISBN_number) {
        this.ISBN_number = ISBN_number;
    }

    public String getISBN_number() {
        return ISBN_number;
    }

    public void setBookNumber(String bookNumber) {
        this.bookNumber = bookNumber;
    }

    public String getBookNumber() {
        return bookNumber;
    }

    public void setCheck(String check) {
        this.check = check;
    }

    public String getCheck() {
        return check;
    }
}
