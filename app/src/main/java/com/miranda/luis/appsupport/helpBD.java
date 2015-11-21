package com.miranda.luis.appsupport;

/**
 * Created by luisafm on 13/11/15.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class helpBD extends SQLiteOpenHelper {

    String tableiFilter = "CREATE TABLE ifilters(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT unique, company TEXT, location TEXT);";
    String tablechangeiFilter = "CREATE TABLE changeifilters(id INTEGER PRIMARY KEY AUTOINCREMENT, ifilter_id INTEGER, status INTEGER, lastchange INTEGER);";


    String tableMessage = "CREATE TABLE messages(id INTEGER PRIMARY KEY AUTOINCREMENT, uuid TEXT unique, payload TEXT, fromm TEXT, too TEXT, priority INTEGER, size INT, date TEXT, checksum INTEGER, status INTEGER );";
    String tableUser = "CREATE TABLE users(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT unique, email TEXT, position TEXT, status INTEGER);";



    public helpBD(Context context, String name, CursorFactory factory,
                      int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(tableiFilter);
        db.execSQL(tablechangeiFilter);
        db.execSQL(tableMessage);
        db.execSQL(tableUser);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}