package com.liuqiang.xatulibrary.common;

import android.content.Context;
import android.content.SharedPreferences;

import org.litepal.LitePalApplication;


public class UserData {

    public static Context getAppContext() {
        return LitePalApplication.getContext();
    }
/*
    public static String getBookMessage() {
        SharedPreferences pref = getAppContext().getSharedPreferences(
                "user", 0);
        return pref.getString("books", "");
    }

    public static boolean setBookMessage(String s) {
        SharedPreferences pref = getAppContext().getSharedPreferences(
                "user", 0);
        return pref.edit().putString("books", s).commit();
    }*/

    public static String getUserName() {
        SharedPreferences pref = getAppContext().getSharedPreferences(
                "user", 0);
        return pref.getString("login", "");
    }

    public static boolean setUserName(String s) {
        SharedPreferences pref = getAppContext().getSharedPreferences(
                "user", 0);
        return pref.edit().putString("login", s).commit();
    }

    public static String getPassWord() {
        SharedPreferences pref = getAppContext().getSharedPreferences(
                "user", 0);
        return pref.getString("password", "");
    }

    public static boolean setPassWord(String s) {
        SharedPreferences pref = getAppContext().getSharedPreferences(
                "user", 0);
        return pref.edit().putString("password", s).commit();
    }

    public static String getUserCookie() {
        SharedPreferences pref = getAppContext().getSharedPreferences(
                "user", 0);
        return pref.getString("cookie", "");
    }

    public static boolean setUserCookie(String s) {
        SharedPreferences pref = getAppContext().getSharedPreferences(
                "user", 0);
        return pref.edit().putString("cookie", s).commit();
    }

    public static String getBorrowNumber() {
        SharedPreferences pref = getAppContext().getSharedPreferences(
                "user", 0);
        return pref.getString("number", "");
    }

    public static boolean setBorrowNumber(String s) {
        SharedPreferences pref = getAppContext().getSharedPreferences(
                "user", 0);
        return pref.edit().putString("number", s).commit();
    }


    public static int getEarlyDay() {
        SharedPreferences pref = getAppContext().getSharedPreferences(
                "user", 0);
        return pref.getInt("day", 3);
    }

    public static boolean setEarlyDay(int s) {
        SharedPreferences pref = getAppContext().getSharedPreferences(
                "user", 0);
        return pref.edit().putInt("day", s).commit();
    }

 /*   public static boolean setAutoloading(Boolean s){
        SharedPreferences pref = getAppContext().getSharedPreferences(
                "user", 0);
        return pref.edit().putBoolean("autoloading", s).commit();
    }*/


   /* public static boolean isAutoloading() {
        SharedPreferences pref = getAppContext().getSharedPreferences(
                "user", 0);
        return pref.getBoolean("autoloading", false);
    }*/

    public static boolean setLoginSucess(Boolean s) {
        SharedPreferences pref = getAppContext().getSharedPreferences(
                "user", 0);
        return pref.edit().putBoolean("loginsucess", s).commit();

    }

    public static boolean isLoginSucess() {
        SharedPreferences pref = getAppContext().getSharedPreferences(
                "user", 0);
        return pref.getBoolean("loginsucess", false);
    }

    public static boolean setRememberPassword(Boolean s) {
        SharedPreferences pref = getAppContext().getSharedPreferences(
                "user", 0);
        return pref.edit().putBoolean("rememberpassword", s).commit();
    }

    public static boolean isRememberPassword() {
        SharedPreferences pref = getAppContext().getSharedPreferences(
                "user", 0);
        return pref.getBoolean("rememberpassword", false);
    }

    public static void clearData() {
        SharedPreferences pref = getAppContext().getSharedPreferences(
                "user", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear().commit();

    }

    public static String getUrl() {
        SharedPreferences pref = getAppContext().getSharedPreferences(
                "user", 0);
        return pref.getString("url", "");
    }

    public static boolean setUrl(String s) {
        SharedPreferences pref = getAppContext().getSharedPreferences(
                "user", 0);
        return pref.edit().putString("url", s).commit();
    }

    public static boolean setFirstLoad(Boolean s){
        SharedPreferences pref = getAppContext().getSharedPreferences(
                "user", 0);
        return pref.edit().putBoolean("firstLoad", s).commit();
    }

    public static boolean isFirstLoad(){
        SharedPreferences pref = getAppContext().getSharedPreferences(
                "user", 0);
        return pref.getBoolean("firstLoad", true);
    }

   /* public static boolean setISBN(String s){
        SharedPreferences pref = getAppContext().getSharedPreferences(
                "isbn", 0);
        return pref.edit().putString("isbn", s).commit();
    }*/
/*

    public static String getISBN() {
        SharedPreferences pref = getAppContext().getSharedPreferences(
                "isbn", 0);
        return pref.getString("isbn", "");
    }
*/


}
