package com.android.example.wordlistsqlwithcontentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.android.example.wordlistsqlwithcontentprovider.Contract.ALL_ITEMS;
import static com.android.example.wordlistsqlwithcontentprovider.Contract.CONTENT_URI;
import static com.android.example.wordlistsqlwithcontentprovider.Contract.MULTIPLE_RECORDS_MIME_TYPE;
import static com.android.example.wordlistsqlwithcontentprovider.Contract.SINGLE_RECORD_MIME_TYPE;
import static java.lang.Integer.parseInt;

/**
 * Created by bryon on 3/12/18.
 */

public class WordListContentProvider extends ContentProvider {

    private static final String TAG = WordListContentProvider.class.getSimpleName();
    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private WordListOpenHelper mDB;

    // UriMatcher constants
    private static final int URI_ALL_ITEMS_CODE = 10;
    private static final int URI_ONE_ITEM_CODE = 20;
    private static final int URI_COUNT_CODE = 30;

    @Override
    public boolean onCreate() {
        mDB = new WordListOpenHelper(getContext());
        initializeUriMatching();
        return true;
    }

    private void initializeUriMatching() {
        sUriMatcher.addURI(Contract.AUTHORITY, Contract.CONTENT_PATH, URI_ALL_ITEMS_CODE);
        sUriMatcher.addURI(Contract.AUTHORITY, Contract.CONTENT_PATH + "/#", URI_ONE_ITEM_CODE);
        sUriMatcher.addURI(Contract.AUTHORITY, Contract.CONTENT_PATH + "/" + Contract.COUNT, URI_COUNT_CODE);
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {

        Cursor cursor = null;

        switch (sUriMatcher.match(uri)) {

            case URI_ALL_ITEMS_CODE:
                cursor = mDB.query(ALL_ITEMS);
                break;

            case URI_ONE_ITEM_CODE:
                cursor = mDB.query(parseInt(uri.getLastPathSegment()));
                break;

            case URI_COUNT_CODE:
                cursor = mDB.count();
                break;

            case UriMatcher.NO_MATCH:
                //todo: error handling
                Log.d(TAG, "NO MATCH FOR THIS URI IN SCHEME: " + uri);
                break;

            default:
                Log.d(TAG, "INVALID URI - URI NOT RECOGNIZED: " + uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case URI_ALL_ITEMS_CODE:
                return MULTIPLE_RECORDS_MIME_TYPE;
            case URI_ONE_ITEM_CODE:
                return SINGLE_RECORD_MIME_TYPE;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long id = mDB.insert(values);
        return Uri.parse(CONTENT_URI + "/" + id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return mDB.delete(parseInt(selectionArgs[0]));
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return mDB.update(parseInt(selectionArgs[0]), values.getAsString("word"));
    }
}
