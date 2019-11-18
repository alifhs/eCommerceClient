package com.example.quickdeals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.quickdeals.Model.Users;
import com.example.quickdeals.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

  private   EditText loginPhone, loginPass;
  private   Button loginButton;
  private   ProgressDialog progressDialog;
  private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginPhone  = findViewById(R.id.login_phone_number_input);
        loginPass = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_Button);
        progressDialog = new ProgressDialog(this);
        checkBox = findViewById(R.id.rememberMeCheckbox);
        Paper.init(this);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUSer();
            }
        });
    }

    private void loginUSer() {

        String phone = loginPhone.getText().toString();
        String pass = loginPass.getText().toString();

        if (TextUtils.isEmpty(phone)){
            Toast.makeText(this, "phone number is required", Toast.LENGTH_SHORT).show();
        }
       else if (TextUtils.isEmpty(pass)){
            Toast.makeText(this, "password is required", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.setTitle("Logging in to your Account");
            progressDialog.setMessage("Please wait a moment");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
                
            ValidateUserData(phone, pass);
        }
    }

    private void ValidateUserData(final String phone, final String pass) {

        if (checkBox.isChecked()){
            Paper.book().write(Prevalent.UserPhoneKey, phone);
            Paper.book().write(Prevalent.UserPasswordKey, pass);
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Users").child(phone).exists()){

                    Users usersdata = dataSnapshot.child("Users").child(phone).getValue(Users.class);
                    if (usersdata.getPhone().equals(phone)){
                        if (usersdata.getPassword().equals(pass)){
                            Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                            Prevalent.currentOnlineUsers = usersdata;

                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                        }

                        else {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                        }
                    }

                }else {
                    Toast.makeText(LoginActivity.this, "Account with " + phone + " doesn't exists", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
