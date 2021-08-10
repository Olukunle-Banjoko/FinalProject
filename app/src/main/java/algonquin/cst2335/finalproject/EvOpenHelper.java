package algonquin.cst2335.finalproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class EvOpenHelper extends SQLiteOpenHelper {
    public static final String name = "FavDatabase"; //String filename that holds the data
    public static final int version = 1; // data columns of your database, increment if you change columns in database
    public static final String TABLE_NAME = "Favorites";
    public static final String locationTitle = "Stations";
    public static final String address = "Address";
    public static final String distance = "Distance";

    public EvOpenHelper( Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create table " + TABLE_NAME + "(_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + locationTitle + " TEXT,"
                + address + " TEXT,"
                + distance + " REAL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
