package com.miranda.luis.appsupport;

/**
 * Created by luisafm on 13/11/15.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class helpBD extends SQLiteOpenHelper {
    String tableMessage = "CREATE TABLE messages(id INTEGER PRIMARY KEY AUTOINCREMENT, uuid TEXT, payload TEXT, fromm TEXT, too TEXT, priority INTEGER, size INT, date TEXT, checksum INTEGER, status INTEGER );";
    String tableiFilter = "CREATE TABLE ifilters(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, company TEXT, status INTEGER, location TEXT, change TEXT);";



    public helpBD(Context context, String name, CursorFactory factory,
                      int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(tableiFilter);
        db.execSQL(tableMessage);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}