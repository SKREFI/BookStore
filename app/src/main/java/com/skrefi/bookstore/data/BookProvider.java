package com.skrefi.bookstore.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.skrefi.bookstore.data.BookContract.BookEntry.COLUMN_BOOK_NAME;
import static com.skrefi.bookstore.data.BookContract.BookEntry.COLUMN_BOOK_PHONE;
import static com.skrefi.bookstore.data.BookContract.BookEntry.COLUMN_BOOK_PRICE;
import static com.skrefi.bookstore.data.BookContract.BookEntry.COLUMN_BOOK_QUANTITY;
import static com.skrefi.bookstore.data.BookContract.BookEntry.COLUMN_BOOK_SUPPLIER;
import static com.skrefi.bookstore.data.BookContract.BookEntry.CONTENT_LIST_TYPE;
import static com.skrefi.bookstore.data.BookContract.BookEntry.CONTENT_ITEM_TYPE;
import static com.skrefi.bookstore.data.BookContract.BookEntry.TABLE_NAME;
import static com.skrefi.bookstore.data.BookContract.BookEntry._ID;
import static com.skrefi.bookstore.data.BookContract.CONTENT_AUTHORITY;

public class BookProvider extends ContentProvider {

    public static final String LOG_TAG = BookProvider.class.getSimpleName();
    private static final int BOOKS = 100;
    private static final int BOOK_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(CONTENT_AUTHORITY, TABLE_NAME, BOOKS);
        sUriMatcher.addURI(CONTENT_AUTHORITY, TABLE_NAME + "/#", BOOK_ID);
    }

    private BookDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new BookDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor = null;

        int match = sUriMatcher.match(uri);

        switch (match) {
            case BOOKS:
                cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case BOOK_ID:
                selection = _ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return CONTENT_LIST_TYPE;
            case BOOK_ID:
                return CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertBook(Uri uri, ContentValues values) {
        /**
         *  Check if the data is valid
         */
        String name = values.getAsString(COLUMN_BOOK_NAME);
        if (name == null) throw new IllegalArgumentException("Book requires a name");

        String supplier = values.getAsString(COLUMN_BOOK_SUPPLIER);
        if (supplier == null) throw new IllegalArgumentException("Book requires a supplier");

        String phone = values.getAsString(COLUMN_BOOK_PHONE);
        if (phone == null)
            throw new IllegalArgumentException("Book requires a supplier phone number");

        Integer price = values.getAsInteger(COLUMN_BOOK_PRICE);
        if (price < 0 || price == null)
            throw new IllegalArgumentException("The book's price is invalid");

        Integer quantity = values.getAsInteger(COLUMN_BOOK_QUANTITY);
        if (quantity < 0 || quantity == null)
            throw new IllegalArgumentException("The book's quantity is invalid");

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long id = db.insert(TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri,id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match){
            case BOOKS:
                rowsDeleted = db.delete(TABLE_NAME,selection,selectionArgs);
                break;
            case BOOK_ID:
                selection = _ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Delation is not supported for " + uri);
        }

        if(rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case BOOKS:
                return updateBook(uri, values,selection,selectionArgs);
            case BOOK_ID:
                selection = _ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateBook(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs){
        String name = values.getAsString(COLUMN_BOOK_NAME);
        if (name == null) throw new IllegalArgumentException("Book requires a name");

        String supplier = values.getAsString(COLUMN_BOOK_SUPPLIER);
        if (supplier == null) throw new IllegalArgumentException("Book requires a supplier");

        String phone = values.getAsString(COLUMN_BOOK_PHONE);
        if (phone == null)
            throw new IllegalArgumentException("Book requires a supplier phone number");

        Integer price = values.getAsInteger(COLUMN_BOOK_PRICE);
        if (price < 0 || price == null)
            throw new IllegalArgumentException("The book's price is invalid");

        Integer quantity = values.getAsInteger(COLUMN_BOOK_QUANTITY);
        if (quantity < 0 || quantity == null)
            throw new IllegalArgumentException("The book's quantity is invalid");

        if(values.size() == 0) return 0;

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int rowsUpdated = db.update(TABLE_NAME,values,selection,selectionArgs);

        if(rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return rowsUpdated;
    }
}
