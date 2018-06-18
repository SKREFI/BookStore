package com.skrefi.bookstore.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.skrefi.bookstore.data.BookContract.BookEntry.COLUMN_BOOK_NAME;
import static com.skrefi.bookstore.data.BookContract.BookEntry.COLUMN_BOOK_PHONE;
import static com.skrefi.bookstore.data.BookContract.BookEntry.COLUMN_BOOK_PRICE;
import static com.skrefi.bookstore.data.BookContract.BookEntry.COLUMN_BOOK_QUANTITY;
import static com.skrefi.bookstore.data.BookContract.BookEntry.COLUMN_BOOK_SUPPLIER;
import static com.skrefi.bookstore.data.BookContract.BookEntry.TABLE_NAME;
import static com.skrefi.bookstore.data.BookContract.BookEntry._ID;

public class BookDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = BookDbHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "books.db";
    private static final int DATABASE_VERSION = 1;

    public BookDbHelper(Context c){
        super(c,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_BOOK_NAME + " TEXT NOT NULL, " +
                COLUMN_BOOK_PRICE + " INTEGER NOT NULL, " +
                COLUMN_BOOK_QUANTITY + " INTEGER NOT NULL DEFAULT 0, " +
                COLUMN_BOOK_SUPPLIER + " TEXT DEFAULT \"N/A\", " +
                COLUMN_BOOK_PHONE + " TEXT DEFAULT \"N/A\");";

        db.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    //This is called when the database needs to be upgraded.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        // The database is still at version 1, so there's nothing to do be done here.
    }

}
