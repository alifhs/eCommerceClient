package com.example.quickdeals;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickdeals.Model.Products;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.MyViewHolder> {

    private List<Products>products;
    Context context;

    public ProductsAdapter(List<Products> products, Context context){
        this.products = products;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder,final int position) {

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ProductDetailsActivity.class);

                intent.putExtra("pid", products.get(position).getPid() );



                context.startActivity(intent);


            }
        });


        holder.setProductName(products.get(position).getPname());
        holder.setProductImage(products.get(position).getImage());
        holder.setPrice(products.get(position).getPrice() +"bdt");
        holder.setDescription(products.get(position).getDescription());

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        View view;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setProductName(String productName){
            TextView textView = view.findViewById(R.id.product_name);
            textView.setText(productName);
        }
        public void setProductImage(String image){
            ImageView imageView = view.findViewById(R.id.product_image);
            Picasso.get().load(image).into(imageView);
        }
        public void setPrice(String price){
            TextView textView = view.findViewById(R.id.product_price);
            textView.setText(price);
        }

        public void setDescription(String description){
            TextView textView = view.findViewById(R.id.product_description);
            textView.setText(description);
        }
    }
}
