package com.skrefi.bookstore;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static com.skrefi.bookstore.data.BookContract.BookEntry.COLUMN_BOOK_NAME;
import static com.skrefi.bookstore.data.BookContract.BookEntry.COLUMN_BOOK_PRICE;
import static com.skrefi.bookstore.data.BookContract.BookEntry.COLUMN_BOOK_QUANTITY;
import static com.skrefi.bookstore.data.BookContract.BookEntry.CONTENT_URI;
import static com.skrefi.bookstore.data.BookContract.COLUMN_KEY;

public class BookCursorAdapter extends CursorAdapter {

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView tvName = (TextView) view.findViewById(R.id.name);
        TextView tvPrice = (TextView) view.findViewById(R.id.price);
        TextView tvQuantity = (TextView) view.findViewById(R.id.tvQuantity);
        ImageView ivSellBtn = (ImageView) view.findViewById(R.id.sell_btn);

        tvName.setText(cursor.getString(cursor.getColumnIndex(COLUMN_BOOK_NAME)));
        tvPrice.setText("$" + cursor.getString(cursor.getColumnIndex(COLUMN_BOOK_PRICE)));
        tvQuantity.setText(cursor.getString(cursor.getColumnIndex(COLUMN_BOOK_QUANTITY)));

        final int quantity = cursor.getInt(cursor.getColumnIndex(COLUMN_BOOK_QUANTITY));
        final int bookId = cursor.getInt(cursor.getColumnIndex(COLUMN_KEY));

        ivSellBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 0) {
                    int newQuantity = quantity - 1;
                    Uri quantityUri = ContentUris.withAppendedId(CONTENT_URI, bookId);
                    ContentValues values = new ContentValues();
                    values.put(COLUMN_BOOK_QUANTITY, newQuantity);

                    int rowUpdated = context.getContentResolver().update(quantityUri, values, null, null);
                    if (!(rowUpdated > 0)) {
                        Toast.makeText(context, "Error while proceeding", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context,"Book sold",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context,"Not enough books in stock",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
