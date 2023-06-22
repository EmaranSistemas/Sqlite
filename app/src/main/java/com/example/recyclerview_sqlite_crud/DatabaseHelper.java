package com.example.recyclerview_sqlite_crud;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "goods.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_PRODUCT = "product";
    private static final String COLUMN_ID = "Id";
    private static final String COLUMN_PRODUCT_NAME = "ProductName";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_PRODUCT + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PRODUCT_NAME + " TEXT)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTableQuery = "DROP TABLE IF EXISTS " + TABLE_PRODUCT;
        db.execSQL(dropTableQuery);
        onCreate(db);
    }

    public void insertProduct(Product product) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, product.getProductName());


        long id = db.insert(TABLE_PRODUCT, null, values);
        if (id != -1) {
            product.setId((int) id);
        }

        db.close();
    }

    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_PRODUCT,
                null,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String productName = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_NAME));


                Product product = new Product();
                product.setId(id);
                product.setProductName(productName);


                productList.add(product);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return productList;
    }

    public boolean updateProduct(Product product) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, product.getProductName());

        int rowsAffected = db.update(TABLE_PRODUCT, values, COLUMN_ID + "=?", new String[]{String.valueOf(product.getId())});
        db.close();

        // Notify the adapter about the updated product

        return rowsAffected > 0;
    }

    public boolean deleteProduct(int productId) {
        SQLiteDatabase db = getWritableDatabase();
        int rowsAffected = db.delete(TABLE_PRODUCT, COLUMN_ID + "=?", new String[]{String.valueOf(productId)});
        db.close();
        return rowsAffected > 0;
    }

    public Product getProduct(int productId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCT, null, COLUMN_ID + "=?", new String[]{String.valueOf(productId)}, null, null, null);
        Product product = null;
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            String productName = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_NAME));
            product = new Product(id, productName);
        }
        cursor.close();
        db.close();
        return product;
    }
}
