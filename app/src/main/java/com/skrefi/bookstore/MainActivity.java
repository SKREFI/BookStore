package com.skrefi.bookstore;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import static com.skrefi.bookstore.data.BookContract.BookEntry.COLUMN_BOOK_NAME;
import static com.skrefi.bookstore.data.BookContract.BookEntry.COLUMN_BOOK_PHONE;
import static com.skrefi.bookstore.data.BookContract.BookEntry.COLUMN_BOOK_PRICE;
import static com.skrefi.bookstore.data.BookContract.BookEntry.COLUMN_BOOK_QUANTITY;
import static com.skrefi.bookstore.data.BookContract.BookEntry.COLUMN_BOOK_SUPPLIER;
import static com.skrefi.bookstore.data.BookContract.BookEntry.CONTENT_URI;
import static com.skrefi.bookstore.data.BookContract.BookEntry._ID;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int BOOK_LOADER = 0;

    BookCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.edit_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, EditorActivity.class));
            }
        });

        ListView bookListVIew = (ListView) findViewById(R.id.list);
        View emptyView = findViewById(R.id.empty_view);
        bookListVIew.setEmptyView(emptyView);

        mCursorAdapter = new BookCursorAdapter(this, null);
        bookListVIew.setAdapter(mCursorAdapter);

        bookListVIew.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);

                Uri currentBookUri = ContentUris.withAppendedId(CONTENT_URI, id);

                intent.setData(currentBookUri);

                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(BOOK_LOADER, null, this);
    }

    private void insertBook() {
        ContentValues values = new ContentValues();
        values.put(COLUMN_BOOK_NAME, "The Witcher");
        values.put(COLUMN_BOOK_PRICE, "$20");
        values.put(COLUMN_BOOK_QUANTITY, "76");
        values.put(COLUMN_BOOK_SUPPLIER, "YourOnlineBookStore");
        values.put(COLUMN_BOOK_PHONE, "077xxxxxxx");

        Uri newUri = getContentResolver().insert(CONTENT_URI, values);
    }

    private void deleteAllPets() {
        int rowsDelted = getContentResolver().delete(CONTENT_URI, null, null);
        Log.v("MainActivity", rowsDelted + " rwos deleted from the database");
    }

    public boolean onCreateOptionMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                _ID,
                COLUMN_BOOK_NAME,
                COLUMN_BOOK_PRICE};

        return new CursorLoader(this,
                CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
