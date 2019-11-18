package com.example.quickdeals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quickdeals.Model.CartModel;
import com.example.quickdeals.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Button proceedNextButton;
    TextView totalAmount, shippingMessage;
    private ArrayList<CartModel>arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.cart_list_recyclerView);
        proceedNextButton = findViewById(R.id.cartList_NextButton);
        totalAmount = findViewById(R.id.total_price_cartList);
        recyclerView.setHasFixedSize(true);
        arrayList = new ArrayList<>();
        shippingMessage = findViewById(R.id.shipping_message);



        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        final CartAdapter adapter = new CartAdapter(arrayList, this, totalAmount);
        recyclerView.setAdapter(adapter);

        DatabaseReference cartReference = FirebaseDatabase.getInstance().getReference().child("Cart List");
            cartReference.child("User View").child(Prevalent.currentOnlineUsers.getPhone()).child("Products")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                                arrayList.add(dataSnapshot1.getValue(CartModel.class));
                            }
                            adapter.notifyDataSetChanged();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                            Toast.makeText(CartActivity.this, "database error occurred", Toast.LENGTH_SHORT).show();
                        }
                    });

            proceedNextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CartActivity.this, ConfirmOrderActivity.class);
                    intent.putExtra("totalPrice", String.valueOf(Prevalent.totalPricePrev));
                    startActivity(intent);
                    finish();

                }
            });

//            for (CartModel x : arrayList){
//                Log.i("value of x = ", x.getPrice());
//
//            }
        CheckOrderState();


    }

    private void CheckOrderState(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(Prevalent.currentOnlineUsers.getPhone());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String shippingState = dataSnapshot.child("state").getValue().toString();
                    String userName = dataSnapshot.child("name").getValue().toString();

                    if (shippingState.equals("shipped")){
                        totalAmount.setText("Package is being shipped");
                        recyclerView.setVisibility(View.GONE);
                        shippingMessage.setVisibility(View.VISIBLE);
                        shippingMessage.setText("wait while we are shipping your products");
                        proceedNextButton.setVisibility(View.GONE);

                    } else if (shippingState.equals("not shipped")){

                        totalAmount.setText("Your order not confirmed yet");
                        recyclerView.setVisibility(View.GONE);
                        shippingMessage.setVisibility(View.VISIBLE);
                        proceedNextButton.setVisibility(View.GONE);

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
