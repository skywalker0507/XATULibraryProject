package com.liuqiang.xatulibrary.common;

import android.graphics.Bitmap;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuqiang on 15-9-4.
 */
public class DoubanBook extends DataSupport{
    private int id;
    private String Bookname;
    private String author;
    private String ISBN_number;
    private String numRaters;
    private String average;
    private int allPages;
    private int freePages;
    private String begin;
    private String end;
    private String publisher;
    private String pubdate;
    private String summary;
    private String price;
    private Bitmap cover;
    private String url;
    private List<String> tags=new ArrayList<>();
    private List<Times> times=new ArrayList<>();
    public void setId(int id) {
        this.id = id;
    }

    public List<Times> getTimes() {
        return times;
    }

    public void setTimes(List<Times> times) {
        this.times = times;
    }

    public int getId() {
        return id;
    }

    public void setBookname(String bookname) {
        Bookname = bookname;
    }

    public String getBookname() {
        return Bookname;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    public void setISBN_number(String ISBN_number) {
        this.ISBN_number = ISBN_number;
    }

    public String getISBN_number() {
        return ISBN_number;
    }

    public void setNumRaters(String numRaters) {
        this.numRaters = numRaters;
    }

    public void setAverage(String average) {
        this.average = average;
    }

    public void setAllPages(int allPages) {
        this.allPages = allPages;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setPubdate(String pubdate) {
        this.pubdate = pubdate;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setCover(Bitmap cover) {
        this.cover = cover;
    }

    public void setFreePages(int freePages) {
        this.freePages = freePages;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public void setEnd(String end) {
        this.end = end;
    }


    public String getNumRaters() {
        return numRaters;
    }

    public String getAverage() {
        return average;
    }

    public int getAllPages() {
        return allPages;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getPubdate() {
        return pubdate;
    }

    public String getSummary() {
        return summary;
    }

    public String getPrice() {
        return price;
    }

    public List<String> getTags() {
        return tags;
    }

    public Bitmap getCover() {
        return cover;
    }

    public int getFreePages() {
        return freePages;
    }

    public String getBegin() {
        return begin;
    }

    public String getEnd() {
        return end;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}


