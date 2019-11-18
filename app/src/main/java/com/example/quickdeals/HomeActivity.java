package com.example.quickdeals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quickdeals.Model.Products;
import com.example.quickdeals.Prevalent.Prevalent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Button logoutButton;
    private FloatingActionButton floatingActionButton;
    private Toolbar toolbar;
   private DrawerLayout drawerLayout;
   private NavigationView navigationView;
    private DatabaseReference productRef;
  private   List<Products> arrayList;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        floatingActionButton = findViewById(R.id.fab_cart);
        toolbar = findViewById(R.id.nav_bar_toolbar);
        drawerLayout = findViewById(R.id.drawer_home);
        navigationView = findViewById(R.id.navView);
        Paper.init(this);
        arrayList = new ArrayList<>();
        productRef = FirebaseDatabase.getInstance().getReference().child("Products");

        final ProductsAdapter adapter = new ProductsAdapter(arrayList, this);

        recyclerView = findViewById(R.id.show_products_recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);


        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot products: dataSnapshot.getChildren()){
                    arrayList.add(products.getValue(Products.class));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(HomeActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

//        Log.i("hello", "how u doing?");

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");

        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView userNameTextView = headerView.findViewById(R.id.user_profile_name);
        userNameTextView.setText(Prevalent.currentOnlineUsers.getName());
        ImageView profileImgView = headerView.findViewById(R.id.user_profile_image);

        Picasso.get().load(Prevalent.currentOnlineUsers.getImage()).into(profileImgView);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_closed);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(HomeActivity.this, "I'm floating", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){
            case R.id.nav_cart:
                Toast.makeText(this, "Cart", Toast.LENGTH_SHORT).show();
                Intent intentCart = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intentCart);
                break;

            case R.id.nav_categories:
                Toast.makeText(this, "categories", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_logout:

                Paper.book().destroy();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                break;

            case R.id.nav_settings:
                Intent intent1 = new Intent(HomeActivity.this, SettingsActivity.class);
                    startActivity(intent1);


        }

        return false;
    }
}
