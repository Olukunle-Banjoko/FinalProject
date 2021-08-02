package algonquin.cst2335.finalproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyOpenHelper extends SQLiteOpenHelper {

    public static final String name= "SoccerDB";
    public static final int version=1;
    public static final String TABLE_NAME= "News";
    public static final String col_title= "Title";
    public static final String col_desc = "NewsDesc";
    public static final String col_date = "NewsDate";
    public static final String col_imageURL = "ImageLink";
    public static final String col_newsURL = "NewsLink";

    public MyOpenHelper(Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create table " + TABLE_NAME + "(_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + col_title + " TEXT,"
                + col_desc + " TEXT,"
                + col_date + " TEXT,"
                + col_imageURL + " TEXT,"
                + col_newsURL + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_NAME);
        onCreate(db);
    }
}

