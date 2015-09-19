package com.liuqiang.xatulibrary.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.liuqiang.xatulibrary.common.DoubanBook;

import java.util.ArrayList;
import java.util.List;

public class DBManager {
    private Context context;
    private BookDBHelper bookDBHelper;
    private SQLiteDatabase dbReader;
    private SQLiteDatabase dbWriter;
    private static DBManager instance;

    public DBManager(Context context) {
        this.context = context;
        bookDBHelper = new BookDBHelper(context, "Library.db", null, 1);
        // 创建and/or打开一个数据库
        dbReader = bookDBHelper.getReadableDatabase();
        dbWriter = bookDBHelper.getWritableDatabase();
    }

    public DBManager() {
        bookDBHelper = new BookDBHelper(context, "Library.db", null, 1);
        // 创建and/or打开一个数据库
        dbReader = bookDBHelper.getReadableDatabase();
        dbWriter = bookDBHelper.getWritableDatabase();
    }

    //getInstance单例
    public static synchronized DBManager getInstance(Context context) {
        if (instance == null) {
            instance = new DBManager(context);
        }
        return instance;
    }

    public void addToDB(String name, String author, String isbn, int all, int free,
                        String begin, String end, String url,int isFull) {
        //  组装数据
        if (!checkNameExists(name)) {
            ContentValues cv = new ContentValues();
            cv.put("name", name);
            cv.put("author", author);
            cv.put("isbn", isbn);
            cv.put("allpages", all);
            cv.put("freepages", free);
            cv.put("begin", begin);
            cv.put("end", end);
            cv.put("url", url);
            cv.put("isFull",isFull);
            dbWriter.insert("MyBook", null, cv);
        }
    }


    //  读取数据
    public List<DoubanBook> readFromDB() {
        List<DoubanBook> books = new ArrayList<>();
        Cursor cursor = dbReader.query("MyBook", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                DoubanBook book = new DoubanBook();
                book.setBookname(cursor.getString(cursor.getColumnIndex("name")));
                book.setAuthor(cursor.getString(cursor.getColumnIndex("author")));
                book.setISBN_number(cursor.getString(cursor.getColumnIndex("isbn")));
                book.setBegin(cursor.getString(cursor.getColumnIndex("begin")));
                book.setEnd(cursor.getString(cursor.getColumnIndex("end")));
                book.setAllPages(cursor.getInt(cursor.getColumnIndex("allpages")));
                book.setFreePages(cursor.getInt(cursor.getColumnIndex("freepages")));
                book.setUrl(cursor.getString(cursor.getColumnIndex("url")));
//                    Log.e("name", book.getBookNumber());
                books.add(book);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return books;
    }

    public int getCount() {
        int count = 0;
        Cursor c = dbReader.rawQuery("select count(*) from MyBook", null);
        if (c != null && c.moveToFirst()) {
            count = c.getInt(0);
        }
        c.close();

        return count;
    }

    //  更新数据
    public void updateBookInfo(int bookID, String name, String author, String isbn, int all, int free,
                               String begin, String end, String url,int isFull) {
        ContentValues cv = new ContentValues();
        cv.put("id", bookID);
        cv.put("name", name);
        cv.put("author", author);
        cv.put("isbn", isbn);
        cv.put("allpages", all);
        cv.put("freepages", free);
        cv.put("begin", begin);
        cv.put("end", end);
        cv.put("url", url);
        cv.put("isFull",isFull);
        dbWriter.update("MyBook", cv, "_id = ?", new String[]{bookID + ""});
    }

    public void updateBookInfo(String type, String content, int ID) {
        ContentValues cv = new ContentValues();
        cv.put(type, content);
        dbWriter.update("MyBook", cv, "id = ?", new String[]{ID + ""});
    }

    public void updateBookInfo(String type, int content, int ID) {
        ContentValues cv = new ContentValues();
        cv.put(type, content);
        dbWriter.update("MyBook", cv, "id = ?", new String[]{ID + ""});
    }

    public void updateBookInfoByURL(String url,String type, String content) {
        ContentValues cv = new ContentValues();
        cv.put(type, content);
        dbWriter.update("MyBook", cv, "url = ?", new String[]{url});
    }
    public void updateBookInfoByISBN(String isbn,String type, int content) {
        ContentValues cv = new ContentValues();
        cv.put(type, content);
        dbWriter.update("MyBook", cv, "isbn = ?", new String[]{isbn});
    }

    //  删除数据
    public void deleteBookInfo(int bookID) {
        dbWriter.delete("MyBook", "_id = ?", new String[]{bookID + ""});
    }

    // 根据id查询数据
    public DoubanBook readBookInfo(int bookID) {
        Cursor cursor = dbReader.rawQuery("SELECT * FROM MyBook WHERE id = ?", new String[]{bookID + ""});
        cursor.moveToFirst();
        DoubanBook book = new DoubanBook();
        book.setId(cursor.getInt(cursor.getColumnIndex("id")));
        book.setBookname(cursor.getString(cursor.getColumnIndex("name")));
        book.setAuthor(cursor.getString(cursor.getColumnIndex("author")));
        book.setISBN_number(cursor.getString(cursor.getColumnIndex("isbn")));
        book.setAllPages(cursor.getInt(cursor.getColumnIndex("allpages")));
        book.setFreePages(cursor.getInt(cursor.getColumnIndex("freepages")));
        book.setBegin(cursor.getString(cursor.getColumnIndex("begin")));
        book.setEnd(cursor.getString(cursor.getColumnIndex("end")));
        book.setUrl(cursor.getString(cursor.getColumnIndex("url")));
        cursor.close();
        return book;
    }

    public String readBookInfo(int bookID, String type) {
        Cursor cursor = dbReader.rawQuery("SELECT * FROM MyBook WHERE id = ?", new String[]{bookID + ""});
        cursor.moveToFirst();
        String content = cursor.getString(cursor.getColumnIndex(type));
        return content;
    }

    public String getISBNByURL(String url) {
        Cursor cursor = dbReader.rawQuery("SELECT * FROM MyBook WHERE url = ?", new String[]{url});
        if (cursor.getCount()==0){
            return "";
        }else {
            cursor.moveToFirst();
            String ISBN = cursor.getString(cursor.getColumnIndex("isbn"));
            return ISBN;
        }
    }

    public boolean checkNameExists(String name) {
        boolean result = false;
        Cursor cursor = dbReader.query("MyBook", null, "name=?", new String[]{name}, null, null, null);
        if (cursor.moveToNext()) {
            result = true;
            Log.e("exist","exist");
        } else {
            result = false;
            Log.e("not exist","not exist");
        }

        cursor.close();

        return result;
    }

    public boolean checkISBNExists(String name) {
        boolean result = false;
        Cursor cursor = dbReader.rawQuery("SELECT * FROM MyBook WHERE name = ?", new String[]{name});
        if (cursor.moveToFirst()){
            String isbn=cursor.getString(cursor.getColumnIndex("isbn"));
            if (isbn.isEmpty()){
                result= false;
            }else {
                result= true;
            }
        }

        cursor.close();

        return result;
    }

    public boolean checkPageExists(String isbn) {
        boolean result = false;
        Cursor cursor = dbReader.rawQuery("SELECT * FROM MyBook WHERE isbn = ?", new String[]{isbn});
        if (cursor.moveToFirst()){
            int allpages=cursor.getInt(cursor.getColumnIndex("allpages"));
            if (allpages==0){
                result= false;
            }else {
                result= true;
            }
        }

        cursor.close();

        return result;
    }

    public boolean checkIsFull(String isbn) {
        boolean result = false;
        Cursor cursor = dbReader.rawQuery("SELECT * FROM MyBook WHERE isFull = ?", new String[]{isbn});
        if (cursor.moveToFirst()){
            int isFull=cursor.getInt(cursor.getColumnIndex("allpages"));
            if (isFull==0){
                result= false;
            }else {
                result= true;
            }
        }

        cursor.close();

        return result;
    }
}


