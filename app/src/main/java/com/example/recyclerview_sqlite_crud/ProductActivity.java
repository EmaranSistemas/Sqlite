package com.example.recyclerview_sqlite_crud;

import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProductActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private DatabaseHelper databaseHelper = new DatabaseHelper(this);

    //insert
    private Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(productList, this, databaseHelper);
        recyclerView.setAdapter(productAdapter);
//khai bao moi chay duoc k thi null object
        databaseHelper = new DatabaseHelper(this);
        loadProductData();

        //insert
        addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInsertDialog();
            }
        });
    }

    private void showInsertDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_insert_product);

        final EditText productNameEditText = dialog.findViewById(R.id.productNameEditText);
        Button insertButton = dialog.findViewById(R.id.insertButton);

        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productName = productNameEditText.getText().toString().trim();


                if (productName.isEmpty()) {
                    Toast.makeText(ProductActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                    return;
                }



                Product product = new Product();
                product.setProductName(productName);

                databaseHelper.insertProduct(product);
                loadProductData();
                Toast.makeText(ProductActivity.this, "Product added successfully", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });

        dialog.show();
    }
    private void loadProductData() {

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM product";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("Id"));
                String productName = cursor.getString(cursor.getColumnIndex("ProductName"));

                Product product = new Product();
                product.setId(id);
                product.setProductName(productName);

                productList.add(product);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        // Add sample products
        productList.add(new Product(1, "Product 1"));
        productList.add(new Product(2, "Product 2"));
        productList.add(new Product(3, "Product 3"));
        // Add more sample products as needed

        productList.clear();
        // Load data from the database and append to the product list
        productList.addAll(databaseHelper.getAllProducts());

        productAdapter.notifyDataSetChanged();
    }
}
