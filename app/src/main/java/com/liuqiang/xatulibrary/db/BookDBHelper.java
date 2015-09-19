package com.liuqiang.xatulibrary.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by XP on 2015/2/15.
 */
public class BookDBHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "Book";
    public static final int VERSION = 1;
    public static final String CREATE_BOOK = "create table MyBook ("
            + "id integer primary key autoincrement,"
            + "name text not null,"
            + "author text not null,"
            + "isbn text ,"
            + "allpages integer,"
            + "freepages integer,"
            + "begin text,"
            + "url text,"
            + "isFull integer,"
            + "end text)";

    private Context context;

    //    public BookDBHelper(Context context) {
//        super(context, TABLE_NAME, null, VERSION);
//    }
    public BookDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int v) {
        super(context, name, factory, v);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_BOOK);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }
}
