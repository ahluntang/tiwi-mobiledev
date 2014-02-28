package be.ugent.oomo.labo_2.database;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by elias on 04/11/13.
 */
public class DatabaseContract {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 13;
    public static final String DATABASE_NAME = "Labo_2.db";

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public DatabaseContract() {}

    public static void onCreate(SQLiteDatabase db) {
        Message.onCreate(db);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Message.onUpgrade(db, oldVersion, newVersion);
    }

    /* Inner class that defines the table contents */
    public static abstract class Message implements BaseColumns {

        // Database table
        public static final String TABLE_NAME = "message";
        public static final String COLUMN_NAME_CONTACT = "sender";
        public static final String COLUMN_NAME_MESSAGE = "message";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_SEND_FROM_APP = "send_from_app";
        
        // Database creation SQL statement
        private static final String DATABASE_CREATE = "CREATE TABLE " +
                TABLE_NAME +
                " ( " +
                    _ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_NAME_CONTACT + " TEXT NOT NULL, " + 
                    COLUMN_NAME_MESSAGE + " TEXT, " +
                    COLUMN_NAME_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    COLUMN_NAME_SEND_FROM_APP + " INTEGER DEFAULT 0" +
                " )";

        public static void onCreate(SQLiteDatabase database) {
            database.execSQL(DATABASE_CREATE);
        }

        public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
            Log.w(Message.class.getName(), "Upgrading database from version "
                    + oldVersion + " to " + newVersion
                    + ", which will destroy all old data");
            database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(database);
        }
    }
}
