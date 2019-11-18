package com.example.quickdeals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.quickdeals.Model.Products;
import com.example.quickdeals.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {

    FloatingActionButton floatingActionButton;
    ElegantNumberButton elegantNumberButton;
    ImageView imageView;

    TextView product_description, product_price, product_name;

    String productId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productId = getIntent().getStringExtra("pid");

        floatingActionButton = findViewById(R.id.add_product_details_cart);
        elegantNumberButton = findViewById(R.id.elegant_number_button);
        imageView = findViewById(R.id.product_image_details);
        product_description = findViewById(R.id.product_description_details);
        product_price = findViewById(R.id.product_price_details);
        product_name  =  findViewById(R.id.product_name_details);
        
        getProductDetails(productId);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProductToCartList();
            }
        });

    }

    private void addProductToCartList() {

        String saveCurrentTime, saveCurrentDate;
        Calendar calDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calDate.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calDate.getTime());

      final   DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

       final HashMap<String, Object> cartHashMap = new HashMap<>();
        cartHashMap.put("pid", productId);
        cartHashMap.put("pname", product_name.getText().toString());
        cartHashMap.put("price", product_price.getText().toString());
        cartHashMap.put("date", saveCurrentDate);
        cartHashMap.put("time", saveCurrentTime);
        cartHashMap.put("quantity", elegantNumberButton.getNumber());
        cartHashMap.put("discount", "");

        cartListRef.child("User View").child(Prevalent.currentOnlineUsers.getPhone())
                .child("Products").child(productId).updateChildren(cartHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    cartListRef.child("Admin View").child(Prevalent.currentOnlineUsers.getPhone())
                            .child("Products").child(productId).updateChildren(cartHashMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){
                                        Toast.makeText(ProductDetailsActivity.this, "Added to the cart successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(ProductDetailsActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                }
            }
        });

    }

    private void getProductDetails(final String productId) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Products");
        databaseReference.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    Products products = dataSnapshot.getValue(Products.class);
                    product_name.setText(products.getPname());
                    product_description.setText(products.getDescription());
                    product_price.setText(products.getPrice());
                    Picasso.get().load(products.getImage()).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
