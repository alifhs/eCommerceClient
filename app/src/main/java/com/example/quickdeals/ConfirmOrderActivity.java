package com.example.quickdeals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.quickdeals.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmOrderActivity extends AppCompatActivity {

    EditText shippingName, shippingNumber, shippingAddress, ShippingCity;
    Button orderNowButton;

    private String totalPrice = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

       totalPrice =  getIntent().getStringExtra("totalPrice");

        Toast.makeText(this, totalPrice+" bdt", Toast.LENGTH_SHORT).show();

        shippingName = findViewById(R.id.shipping_userName);
        shippingNumber  = findViewById(R.id.shipping_phoneNumber);
        shippingAddress = findViewById(R.id.shipping_Address);
        ShippingCity =  findViewById(R.id.shipping_city);

        orderNowButton = findViewById(R.id.shipping_order_now_button);

        orderNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckFields();
            }
        });

    }

    private void CheckFields() {

        if (TextUtils.isEmpty(shippingName.getText().toString())){
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(shippingNumber.getText().toString())){
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(shippingAddress.getText().toString())){
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(ShippingCity.getText().toString())){
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
        } else {
            confirmOrder();
        }
    }

    private void confirmOrder() {
        String saveCurrentTime, saveCurrentDate;
        Calendar calDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calDate.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calDate.getTime());

        DatabaseReference orderReference = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(Prevalent.currentOnlineUsers.getPhone());
        HashMap<String, Object> orderHashmap = new HashMap<>();

        orderHashmap.put("totalAmount", totalPrice);
        orderHashmap.put("name", shippingName.getText().toString());
        orderHashmap.put("phone", Prevalent.currentOnlineUsers.getPhone());
        orderHashmap.put("UpdatedPhone", shippingNumber.getText().toString());
        orderHashmap.put("date", saveCurrentDate);
        orderHashmap.put("address", shippingAddress.getText().toString());
        orderHashmap.put("city", ShippingCity.getText().toString());
        orderHashmap.put("time", saveCurrentTime);
        orderHashmap.put("state", "not shipped");

        orderReference.updateChildren(orderHashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference().child("Cart List")
                            .child("User View").child(Prevalent.currentOnlineUsers.getPhone())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ConfirmOrderActivity.this, "your order has been placed successfully", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(ConfirmOrderActivity.this, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }
                    });
                }
            }
        });

    }
}
