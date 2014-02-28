package be.ugent.oomo.labo_2.contentprovider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import be.ugent.oomo.labo_2.database.DatabaseContract;
import be.ugent.oomo.labo_2.database.DbHelper;


/**
 * Created by elias on 04/11/13.
 */
public class MessageContentProvider extends ContentProvider {

	// database
    private DbHelper database;

    // used for the UriMacher
    private static final int MESSAGES = 10;
    private static final int MESSAGE_ID = 20;

    private static final String AUTHORITY = "be.ugent.oomo.labo_2.contentprovider.message";

    private static final String MESSAGES_PATH = "messages";
    public static final Uri CONTENT_MESSAGE_URI = Uri.parse("content://" + AUTHORITY + "/" + MESSAGES_PATH);

    public static final String CONTENT_MESSAGES_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/messages";
    public static final String CONTENT_MESSAGES_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/message";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, MESSAGES_PATH, MESSAGES);
        sURIMatcher.addURI(AUTHORITY, MESSAGES_PATH + "/#", MESSAGE_ID);
    }

    @Override
    public boolean onCreate() {
        database = new DbHelper(getContext());
        return database != null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,String[] selectionArgs, String sortOrder) {

    	String groupBy = DatabaseContract.Message._ID;
        // Uisng SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case MESSAGES: // no break
                // Set the table
                queryBuilder.setTables(DatabaseContract.Message.TABLE_NAME);
                break;
            case MESSAGE_ID:
                // Set the table
                queryBuilder.setTables(DatabaseContract.Message.TABLE_NAME);
                // adding the ID to the original query
                queryBuilder.appendWhere(DatabaseContract.Message._ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, groupBy, null, sortOrder);
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        long id = 0;
        Uri returnUri = null;
        switch (uriType) {
            case MESSAGES:
                id = sqlDB.insert(DatabaseContract.Message.TABLE_NAME, null, values);
                returnUri = Uri.parse(MESSAGES_PATH + "/" + id);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted = 0;
        String id = null;
        switch (uriType) {
            case MESSAGES:
                rowsDeleted = sqlDB.delete(DatabaseContract.Message.TABLE_NAME, selection, selectionArgs);
                break;
            case MESSAGE_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(DatabaseContract.Message.TABLE_NAME, DatabaseContract.Message._ID + "=" + id, null);
                } else {
                    rowsDeleted = sqlDB.delete(DatabaseContract.Message.TABLE_NAME, DatabaseContract.Message._ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated = 0;
        String id = null;
        switch (uriType) {
            case MESSAGES:
                rowsUpdated = sqlDB.update(DatabaseContract.Message.TABLE_NAME, values, selection, selectionArgs);
                break;
            case MESSAGE_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(DatabaseContract.Message.TABLE_NAME, values, DatabaseContract.Message._ID + "=" + id, null);
                } else {
                    rowsUpdated = sqlDB.update(DatabaseContract.Message.TABLE_NAME, values, DatabaseContract.Message._ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        getContext().getContentResolver().notifyChange(CONTENT_MESSAGE_URI, null);
        return rowsUpdated;
    }

    @Override
    public String getType(Uri uri) {
        int uriType = sURIMatcher.match(uri);
        String type = null;
        switch (uriType) {
            case MESSAGES:
                type = CONTENT_MESSAGES_TYPE;
                break;
            case MESSAGE_ID:
                type = CONTENT_MESSAGES_ITEM_TYPE;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return type;
    }
}
