package com.example.quickdeals;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickdeals.Model.CartModel;
import com.example.quickdeals.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {

    private List<CartModel> cart;
    Context context;
    int calculateTotal = 0;
    TextView totalAmount;

    public CartAdapter(List<CartModel>cart, Context context, TextView totalAmount){
        this.cart = cart;
        this.context = context;
        this.totalAmount = totalAmount;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.setProductName(cart.get(position).getPname());
        holder.setPrice( "price = "+cart.get(position).getPrice() +" bdt");
        holder.setQuantity("Quantity = "+cart.get(position).getQuantity());

        int singleProductPrice = Integer.valueOf(cart.get(position).getPrice())*Integer.valueOf(cart.get(position).getQuantity());
        calculateTotal += singleProductPrice;
        Prevalent.totalPricePrev = calculateTotal;
        totalAmount.setText("Total Price = "+String.valueOf(calculateTotal) +" bdt");




        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options [] = new CharSequence[]{

                        "Edit",
                        "Delete"
                };

                AlertDialog.Builder  builder = new AlertDialog.Builder(context);
                builder.setTitle("Cart Options :");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0){
                            Intent intent = new Intent(context, ProductDetailsActivity.class);
                            intent.putExtra("pid",cart.get(position).getPid() );
                            context.startActivity(intent);
                        }
                        if (which == 1){
                            final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
                            cartListRef.child("User View").child(Prevalent.currentOnlineUsers.getPhone())
                                    .child("Products").child(cart.get(position).getPid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(context, "Item removed successfully", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(context, HomeActivity.class);
                                            context.startActivity(intent);
                                        }
                                }
                            });
                        }

                    }
                });

                builder.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return cart.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        View view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setProductName(String productName){
            TextView textView = view.findViewById(R.id.cartRec_product_name);
            textView.setText(productName);
        }

        public void setPrice(String price){
            TextView textView = view.findViewById(R.id.cartRec_product_price);
            textView.setText(price);
        }

        public void setQuantity(String description){
            TextView textView = view.findViewById(R.id.cartRec_product_quantity);
            textView.setText(description);
        }
    }
}
