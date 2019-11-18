package com.example.quickdeals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText userName, phoneNumber, password;
    private Button registerButton;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userName = findViewById(R.id.register_name_input);
        phoneNumber = findViewById(R.id.register_phone_number_input);
        password = findViewById(R.id.register_password);
        registerButton = findViewById(R.id.register_Button);

        progressDialog = new ProgressDialog(this);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
            }
        });



    }

    private void CreateNewAccount() {

        String name  = userName.getText().toString();
        String phone = phoneNumber.getText().toString();
        String pass = password.getText().toString();

        if (TextUtils.isEmpty(name)){
            Toast.makeText(this, "name field is required", Toast.LENGTH_SHORT).show();
        }
       else if (TextUtils.isEmpty(phone)){
            Toast.makeText(this, "phone number is required", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(pass)){
            Toast.makeText(this, "password  field is required", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.setTitle("Creating New Account");
            progressDialog.setMessage("Please wait a moment");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            ValidatePhoneNumber(name, phone, pass);
        }
    }

    private void ValidatePhoneNumber(final String name, final String phone, final String pass) {

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!(dataSnapshot.child("Users").child(phone).exists())){

                        HashMap<String, Object> hashMap =  new HashMap<>();
                        hashMap.put("phone", phone);
                        hashMap.put("password", pass);
                        hashMap.put("name",name);
                        databaseReference.child("Users").child(phone).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(RegisterActivity.this, "Your account has been created", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                            else {
                                                progressDialog.dismiss();
                                                Toast.makeText(RegisterActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                                            }
                            }
                        });

                    }
                    else {
                        progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "A user with this "+phone+" number already exists in database", Toast.LENGTH_SHORT).show();


                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();

            }
        });
    }
}
