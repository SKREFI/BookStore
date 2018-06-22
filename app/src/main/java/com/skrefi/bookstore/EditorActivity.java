package com.skrefi.bookstore;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.skrefi.bookstore.data.BookContract;

import static com.skrefi.bookstore.data.BookContract.BookEntry.COLUMN_BOOK_NAME;
import static com.skrefi.bookstore.data.BookContract.BookEntry.COLUMN_BOOK_PHONE;
import static com.skrefi.bookstore.data.BookContract.BookEntry.COLUMN_BOOK_PRICE;
import static com.skrefi.bookstore.data.BookContract.BookEntry.COLUMN_BOOK_QUANTITY;
import static com.skrefi.bookstore.data.BookContract.BookEntry.COLUMN_BOOK_SUPPLIER;
import static com.skrefi.bookstore.data.BookContract.BookEntry._ID;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_BOOK_LOADER = 0;
    private Uri mCurrentBookUri;
    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mSupplierEditText;
    private EditText mPhoneEditText;

    private boolean mBookHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEVent) {
            mBookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        if (mCurrentBookUri == null) {
            setTitle("Add a book!");
            invalidateOptionsMenu();
        } else {
            setTitle("Edit Book");

            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        mNameEditText = (EditText) findViewById(R.id.edit_book_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_book_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_book_qunatity);
        mSupplierEditText = (EditText) findViewById(R.id.edit_book_supplier);
        mPhoneEditText = (EditText) findViewById(R.id.edit_book_phone);
    }

    private void saveBook() {
        /** Comprimed in the content values
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String supplierString = mSupplierEditText.getText().toString().trim();
        String phoneString = mPhoneEditText.getText().toString().trim();
         */

        ContentValues values = new ContentValues();
        values.put(COLUMN_BOOK_NAME, mNameEditText.getText().toString().trim());
        values.put(COLUMN_BOOK_PRICE, mPriceEditText.getText().toString().trim());
        values.put(COLUMN_BOOK_QUANTITY, mQuantityEditText.getText().toString().trim());
        values.put(COLUMN_BOOK_SUPPLIER, mSupplierEditText.getText().toString().trim());
        values.put(COLUMN_BOOK_PHONE, mPhoneEditText.getText().toString().trim());

        if (mCurrentBookUri == null) {
            Uri newUri = getContentResolver().insert(BookContract.BookEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, "Error with saving book", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Book Saved", Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, "Error with updating book", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Book updated", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveBook();
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        if(!mBookHasChanged){
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                _ID,
                COLUMN_BOOK_NAME,
                COLUMN_BOOK_PRICE,
                COLUMN_BOOK_QUANTITY,
                COLUMN_BOOK_SUPPLIER,
                COLUMN_BOOK_PHONE
        };

        return new CursorLoader(this,
                mCurrentBookUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(cursor == null || cursor.getCount() < 1){
            return;
        }

        if(cursor.moveToFirst()){
            /** I comprimed all when I set the text for the edit text

            int nameColumnIndex = cursor.getColumnIndex(COLUMN_BOOK_NAME);
            int priceColumnIndex = cursor.getColumnIndex(COLUMN_BOOK_PRICE);
            int qunatityColumnIndex = cursor.getColumnIndex(COLUMN_BOOK_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(COLUMN_BOOK_SUPPLIER);
            int phoneColumnIndex = cursor.getColumnIndex(COLUMN_BOOK_PHONE);

            String name = cursor.getString(nameColumnIndex);
            String price = cursor.getString(priceColumnIndex);
            String quantity = cursor.getString(qunatityColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            String phone = cursor.getString(phoneColumnIndex);
             */

            mNameEditText.setText(cursor.getString(cursor.getColumnIndex(COLUMN_BOOK_NAME)));
            mPriceEditText.setText(cursor.getString(cursor.getColumnIndex(COLUMN_BOOK_PRICE)));
            mQuantityEditText.setText(cursor.getString(cursor.getColumnIndex(COLUMN_BOOK_QUANTITY)));
            mSupplierEditText.setText(cursor.getString(cursor.getColumnIndex(COLUMN_BOOK_SUPPLIER)));
            mPhoneEditText.setText(cursor.getString(cursor.getColumnIndex(COLUMN_BOOK_PHONE)));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierEditText.setText("");
        mPhoneEditText.setText("");
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard your changes and quit editing?");
        builder.setPositiveButton("Discard",discardButtonClickListener);
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if(dialog != null){
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this book?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteBook();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog != null){
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteBook(){
        if(mCurrentBookUri != null){
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);

            if(rowsDeleted == 0){
                Toast.makeText(this,"Error with dealeting book",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"Book deleted",Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
}
