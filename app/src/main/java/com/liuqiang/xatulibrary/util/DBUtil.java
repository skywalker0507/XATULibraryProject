package com.liuqiang.xatulibrary.util;

import android.util.Log;

import com.liuqiang.xatulibrary.common.DoubanBook;
import com.liuqiang.xatulibrary.common.Times;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by liuqiang on 15-9-16.
 */
public class DBUtil {
    public DBUtil(){

    }

    public boolean checkNameExists(String name) {
        boolean result = false;
        List<DoubanBook> libraries = DataSupport.where("name=?", name).find(DoubanBook.class);
        if (libraries.size()>0) {
            result = true;
            Log.e("exist", "exist in library");
        } else {
            result = false;
            Log.e("not exist","not exist in library");
        }

        return result;
    }

    public boolean checkDateExists(DoubanBook doubanBook,String date) {
        long id=doubanBook.getId();
        List<Times> list= DataSupport.where("library_id = ?", String.valueOf(id)).find(Times.class);
        boolean result=false;
        if (list.size()>0){
            Log.e("ok","ok"+list.size());
            for (int i=0;i<list.size();i++){
                String time=list.get(i).getDate();
                Log.e("time1",time);
                Log.e("time2",date);
                if (date.equals(time)){
                    result=true;
                    Log.e("exist","exist in date");
                    break;
                }
            }
        }else {
            result=false;
            Log.e("exist","not exist in date");
        }
        return result;
    }

    public boolean checkISBNExists(String isbn) {
        boolean result = false;
        List<DoubanBook> libraries = DataSupport.where("isbn_number=?", isbn).find(DoubanBook.class);
        if (libraries.size()>0) {
            result = true;
            Log.e("exist", "exist in library");
        } else {
            result = false;
            Log.e("not exist","not exist in library");
        }

        return result;
    }
}
