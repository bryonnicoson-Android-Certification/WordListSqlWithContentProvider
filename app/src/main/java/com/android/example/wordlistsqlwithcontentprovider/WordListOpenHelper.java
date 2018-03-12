package com.android.example.wordlistsqlwithcontentprovider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.android.example.wordlistsqlwithcontentprovider.Contract.WordList.*;
import static com.android.example.wordlistsqlwithcontentprovider.Contract.*;

/**
 * Created by bryon on 3/10/18.
 */

public class WordListOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = WordListOpenHelper.class.getSimpleName();

    // database, table
    private static final int DATABASE_VERSION = 1;

    private static final String[] COLUMNS = {KEY_ID, KEY_WORD};

    // sql query to create table
    private static final String WORD_LIST_TABLE_CREATE =
            "CREATE TABLE " + WORD_LIST_TABLE + " (" + KEY_ID + " INTEGER PRIMARY KEY, " +
                    KEY_WORD + " TEXT );";

    private SQLiteDatabase mWritableDB;
    private SQLiteDatabase mReadableDB;

    public WordListOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "Construct WordListOpenHelper");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate");
        db.execSQL(WORD_LIST_TABLE_CREATE);
        fillDatabaseWithData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(WordListOpenHelper.class.getName(), "Upgrading database from version " +
        oldVersion + " to " + newVersion + ", which will destroy all old data.");
        db.execSQL("DROP TABLE IF EXISTS " + WORD_LIST_TABLE);
    }

    private void fillDatabaseWithData(SQLiteDatabase db) {
        // create some data
        String[] words = {"Android", "Adapter", "ListView", "AsyncTask", "Android Studio",
                "SQLiteDatabase", "SQLOpenHelper", "Data model", "ViewHolder", "Android Performance",
                "OnClickListener"};

        // create a container for the data
        ContentValues values = new ContentValues();

        // put column/value pairs in the container and insert into db
        for (int i = 0; i < words.length; i++) {
            values.put(KEY_WORD, words[i]);
            db.insert(WORD_LIST_TABLE, null, values);
        }
    }

    public WordItem query(int position) {
        String query = "SELECT * FROM " + WORD_LIST_TABLE + " ORDER BY " + KEY_WORD +
                " ASC LIMIT " + position + ", 1";
        Cursor cursor = null;
        WordItem entry = new WordItem();
        try {
            if (mReadableDB == null) {mReadableDB = getReadableDatabase();}
            cursor = mReadableDB.rawQuery(query, null);
            cursor.moveToFirst();
            entry.setmId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            entry.setmWord(cursor.getString(cursor.getColumnIndex(KEY_WORD)));
        } catch (Exception e) {
            Log.d(TAG, "QUERY EXCEPTION! " + e.getMessage());
        } finally {
            cursor.close();
            return entry;
        }
    }

    public long insert(String word){
        long newId = 0;
        ContentValues values = new ContentValues();
        values.put(KEY_WORD, word);
        try {
            if (mWritableDB == null){
                mWritableDB = getWritableDatabase();
            }
            newId = mWritableDB.insert(WORD_LIST_TABLE, null, values);
        } catch (Exception e) {
            Log.d(TAG, "INSERT EXCEPTION! " + e.getMessage());
        }
        return newId;
    }

    public long count(){
        if (mReadableDB == null) {
            mReadableDB = getReadableDatabase();
        }
        return DatabaseUtils.queryNumEntries(mReadableDB, WORD_LIST_TABLE);
    }

    public int delete(int id){
        int deleted = 0;
        try {
            if (mWritableDB == null) {
                mWritableDB = getWritableDatabase();
            }
            deleted = mWritableDB.delete(WORD_LIST_TABLE, KEY_ID + " = ? ",
                    new String[]{String.valueOf(id)});
        } catch (Exception e) {
            Log.d(TAG, "DELETE EXCEPTION! " + e.getMessage());
        }
        return deleted;
    }

    public int update(int id, String word){
        int mNumberOfRowsUpdated = -1;
        try {
            if (mWritableDB == null) {
                mWritableDB = getWritableDatabase();
            }
            ContentValues values = new ContentValues();
            values.put(KEY_WORD, word);
            mNumberOfRowsUpdated = mWritableDB.update(WORD_LIST_TABLE, // table to change
                    values,                                            // new values to insert
                    KEY_ID + " = ?",                       // selection criteria
                    new String[]{String.valueOf(id)});                 // selection args
        } catch (Exception e){
            Log.d(TAG, "UPDATE EXCEPTION! " + e.getMessage());
        }
        return mNumberOfRowsUpdated;
    }

    public Cursor search(String searchString) {
        String[] columns = new String[]{KEY_WORD};
        searchString = "%" + searchString + "%";
        String where = KEY_WORD + " LIKE ?";
        String[] whereArgs = new String[]{searchString};

        Cursor cursor = null;

        try {
            if (mReadableDB == null) mReadableDB = getReadableDatabase();
            cursor = mReadableDB.query(WORD_LIST_TABLE, columns, where, whereArgs, null, null, null);
        } catch (Exception e) {
            Log.d(TAG, "SEARCH EXCEPTION! " + e.getMessage());
        }

        return cursor;
    }
}
