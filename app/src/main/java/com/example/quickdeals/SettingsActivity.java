package com.example.quickdeals;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quickdeals.Prevalent.Prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import io.paperdb.Paper;

public class SettingsActivity extends AppCompatActivity {

    ImageView imageView;
    TextView update_textView, close_textView, change_image;
    EditText editName, editPhone, editAddress;
    private Uri imageUri;
    private String myUrl ="";
    private StorageReference storageReferenceProfilePic;
    private String cheker = "";

    private StorageTask uploadTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        storageReferenceProfilePic  = FirebaseStorage.getInstance().getReference().child("Profile Pictures");
        imageView = findViewById(R.id.setting_profile_image);
        update_textView = findViewById(R.id.update_settings);
        close_textView = findViewById(R.id.close_settings);
        change_image = findViewById(R.id.change_profile_image_button);
        editAddress = findViewById(R.id.update_address_settings);
        editName = findViewById(R.id.update_name_settings);
        editPhone = findViewById(R.id.update_phone_settings);

        userInfoDisplay(editPhone,editName, imageView, editPhone);

        close_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        update_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cheker.equals("clicked")){
                        userInfoSaved();
                }else {
                    updateOnlyUserInfo();
                }
            }
        });

        change_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cheker = "clicked";
                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(SettingsActivity.this);
            }
        });
    }

    private void updateOnlyUserInfo() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", editName.getText().toString());
        hashMap.put("address", editAddress.getText().toString());
        hashMap.put("phoneOrder", editPhone.getText().toString());

        reference.child(Prevalent.currentOnlineUsers.getPhone()).updateChildren(hashMap);



        Intent intent = new Intent(SettingsActivity.this, HomeActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null){
            CropImage.ActivityResult activityResult = CropImage.getActivityResult(data);
            imageUri = activityResult.getUri();
            imageView.setImageURI(imageUri);

        } else {
            Toast.makeText(this, "Error occurred", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
            startActivity(intent);
            finish();
        }

    }

    private void userInfoSaved() {
        if (TextUtils.isEmpty(editName.getText().toString())){
            Toast.makeText(this, "you must insert all these fields", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(editAddress.getText().toString())){
            Toast.makeText(this, "you must insert all these fields", Toast.LENGTH_SHORT).show();


        }else if (TextUtils.isEmpty(editPhone.getText().toString())){
            Toast.makeText(this, "you must insert all these fields", Toast.LENGTH_SHORT).show();


        } else if (cheker.equals("clicked")){
            uploadImage();
        }
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");

        progressDialog.setMessage("Please wait .....");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        if (imageUri != null){
            final StorageReference fileRef = storageReferenceProfilePic.
                    child(Prevalent.currentOnlineUsers.getPhone()+".jpg");
            uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if (! task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener <Uri>() {
                @Override
                public void onComplete(@NonNull Task <Uri> task) {

                    if (task.isSuccessful()){
                        Uri DownloadUri = task.getResult();
                        myUrl = DownloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("name", editName.getText().toString());
                        hashMap.put("address", editAddress.getText().toString());
                        hashMap.put("phoneOrder", editPhone.getText().toString());
                        hashMap.put("image", myUrl);

                        reference.child(Prevalent.currentOnlineUsers.getPhone()).updateChildren(hashMap);
                        progressDialog.dismiss();

                        Intent intent = new Intent(SettingsActivity.this, HomeActivity.class);

                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
//                        finish();


                    } else {
                        progressDialog.dismiss();
                    }
                }
            });
        } else{
            Toast.makeText(this, "Image is not selected", Toast.LENGTH_SHORT).show();

        }
    }

    private void userInfoDisplay(final EditText editPhone, final EditText editName, final ImageView imageView, EditText editPhone1) {

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().
                child("Users").child(Prevalent.currentOnlineUsers.getPhone());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    if (dataSnapshot.child("image").exists()){
                        String image = dataSnapshot.child("image").getValue().toString();
                        String name = dataSnapshot.child("name").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(imageView);
                        editPhone.setText(phone);
                        editName.setText(name);
                        editAddress.setText(address);


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
