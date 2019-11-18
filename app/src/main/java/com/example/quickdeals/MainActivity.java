package com.example.quickdeals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.quickdeals.Model.Users;
import com.example.quickdeals.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button joinNowButton, loginButton;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Paper.init(this);

       progressDialog = new ProgressDialog(this);

        joinNowButton = findViewById(R.id.join_now_button);
        loginButton = findViewById(R.id.login_main_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        joinNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);            }
        });

        String userphonekey = Paper.book().read(Prevalent.UserPhoneKey);
        String userpasskey = Paper.book().read(Prevalent.UserPasswordKey);

        if (userphonekey != "" && userpasskey != ""){

            if (!TextUtils.isEmpty(userpasskey) && !TextUtils.isEmpty(userphonekey)){

                progressDialog.setTitle("Logging in to your Account");
                progressDialog.setMessage("Please wait a moment");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                checkAccess(userphonekey, userpasskey);
            }
        }


    }

    private void checkAccess(final String phone,final String pass) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Users").child(phone).exists()){

                    Users usersdata = dataSnapshot.child("Users").child(phone).getValue(Users.class);
                    if (usersdata.getPhone().equals(phone)){
                        if (usersdata.getPassword().equals(pass)){
                            Toast.makeText(MainActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            Prevalent.currentOnlineUsers = usersdata;
                            startActivity(intent);

                        }

                        else {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                        }
                    }

                }else {
                    Toast.makeText(MainActivity.this, "Account with " + phone + " doesn't exists", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
