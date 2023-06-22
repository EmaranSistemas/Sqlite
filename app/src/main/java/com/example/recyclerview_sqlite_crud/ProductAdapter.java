package com.example.recyclerview_sqlite_crud;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> productList;
    private Context context;

    private DatabaseHelper databaseHelper;

    public ProductAdapter(List<Product> productList, Context context, DatabaseHelper databaseHelper) {
        this.productList = productList;
        this.context = context;
        this.databaseHelper = databaseHelper;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        final Product product = productList.get(position);
        holder.bind(product);


        //long press
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    showOptionsDialog(adapterPosition);
                    return true;
                }
                return false;
            }
        });


    }

    //option delete or update
    private void showOptionsDialog(final int position) {
        final CharSequence[] options = {"Update", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Option");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                // Update option clicked
                updateProduct(position);
            } else if (which == 1) {
                // Delete option clicked
                showDeleteConfirmationDialog(position);
            }
        });
        builder.show();
    }

    //go to update page
    private void updateProduct(int position) {
        final Product product = productList.get(position);

        // Create an Intent to navigate to the UpdateProductActivity
        Intent intent = new Intent(context, UpdateProductActivity.class);
        intent.putExtra("productId", product.getId());
        intent.putExtra("position", position);

        context.startActivity(intent);
    }

    //dialog delete confirm
    private void showDeleteConfirmationDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Product");
        builder.setMessage("Are you sure you want to delete this product?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Get the clicked product
                Product product = productList.get(position);
                // Remove the product from the database
                boolean d = databaseHelper.deleteProduct(product.getId());
                if(d){
                    Log.d("lol", "thanh cong");
                    productList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, productList.size());
                }else{
                    Log.d("lol", "khong thanh cong");
                }
//
//                // Remove the product from the list
//                productList.remove(position);
//
//                // Notify the adapter that an item has been removed
//                notifyItemRemoved(position);
//                notifyItemRangeChanged(position, productList.size());
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }




    @Override
    public int getItemCount() {
        return productList.size();
    }


    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        private TextView productNameTextView;
        private TextView descriptionTextView;
        private TextView priceTextView;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
        }

        public void bind(Product product) {
            productNameTextView.setText(product.getProductName());
        }
    }

    //refresh data

}
