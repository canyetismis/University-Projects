package com.example.canyetismis.coursework3;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class MyContentProvider extends ContentProvider {
    DBHelper dbHelper;

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MyProviderContract.AUTHORITY, "people", 1);
        uriMatcher.addURI(MyProviderContract.AUTHORITY, "people/#", 2);
    }

    @Override
    public boolean onCreate() {
        Log.d("g53mdp", "contentprovider oncreate");
        this.dbHelper = new DBHelper(this.getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        if (uri.getLastPathSegment()==null)
        {
            return "vnd.android.cursor.dir/MyProvider.data.text";
        }
        else
        {
            return "vnd.android.cursor.item/MyProvider.data.text";
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String tableName;

        switch(uriMatcher.match(uri)) {
            case 1:
                tableName = "myList";
                break;
            default:
                tableName = "myList";
                break;
        }

        long id = db.insert(tableName, null, values);
        Uri nu = ContentUris.withAppendedId(uri, id);
        Log.d("g53mdp", nu.toString());
        getContext().getContentResolver().notifyChange(nu, null);
        return nu;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[]
            selectionArgs, String sortOrder) {

        return dbHelper.getReadableDatabase().query(MyProviderContract.TABLE_NAME, projection, selection, selectionArgs, null, null,
                sortOrder);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[]
            selectionArgs) {
        return dbHelper.getWritableDatabase().update(MyProviderContract.TABLE_NAME, values, selection, null);
    }
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return dbHelper.getWritableDatabase().delete(MyProviderContract.TABLE_NAME, selection, null);
    }
}
