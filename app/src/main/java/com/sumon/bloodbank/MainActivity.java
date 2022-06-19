package com.sumon.bloodbank;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sumon.bloodbank.Adapter.UserAdapter;
import com.sumon.bloodbank.Model.Notification;
import com.sumon.bloodbank.Model.NotificationDto;
import com.sumon.bloodbank.Model.User;
import com.sumon.bloodbank.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView nav_View;

    private CircleImageView nav_profile_img;
    private TextView nav_fullName, nav_email, nav_bloodGroup, nav_type;

    private DatabaseReference userREf;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private List<User> userList;
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Blood Bank");



        drawerLayout = findViewById(R.id.drawerLayout);
        nav_View = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        nav_View.setNavigationItemSelectedListener(this);

        progressBar = findViewById(R.id.progressbar);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(MainActivity.this, userList);
        recyclerView.setAdapter(userAdapter);


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String type = snapshot.child("type").getValue().toString();

                if (type.equals("donor")){

                    readRecipients();

                }
                else {
                    readDonors();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        nav_profile_img = nav_View.getHeaderView(0).findViewById(R.id.nav_user_img);
        nav_fullName = nav_View.getHeaderView(0).findViewById(R.id.nav_user_fullName);
        nav_email = nav_View.getHeaderView(0).findViewById(R.id.nav_user_email);
        nav_bloodGroup = nav_View.getHeaderView(0).findViewById(R.id.nav_user_bloodGroup);
        nav_type = nav_View.getHeaderView(0).findViewById(R.id.nav_user_type);

        userREf = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());


        userREf.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    String name = snapshot.child("name").getValue().toString();
                    nav_fullName.setText(name);

                    String email = snapshot.child("email").getValue().toString();
                    String phone = snapshot.child("phoneNumber").getValue().toString();
                    nav_email.setText(email);

                    String bloodGroup = snapshot.child("bloodGroup").getValue().toString();
                    nav_bloodGroup.setText(bloodGroup);

                    String type = snapshot.child("type").getValue().toString();
                    nav_type.setText(type);

                    if (snapshot.hasChild("profilePictureUrl")){

                        String imgUrl = snapshot.child("profilePictureUrl").getValue().toString();
                        Glide.with(getApplicationContext()).load(imgUrl).into(nav_profile_img);

                    }
                    else {
                        nav_profile_img.setImageResource(R.drawable.profile);
                    }

                    FirebaseMessaging.getInstance().subscribeToTopic(phone);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });


    }

    private void readDonors() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = reference.orderByChild("type").equalTo("donor");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                userList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    User user = dataSnapshot.getValue(User.class);
                    userList.add(user);
                }

                userAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                if (userList.isEmpty()){

                    Toast.makeText(MainActivity.this, "No recipients", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void readRecipients() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = reference.orderByChild("type").equalTo("recipient");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                userList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    User user = dataSnapshot.getValue(User.class);
                    userList.add(user);
                }

                userAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                if (userList.isEmpty()){

                    Toast.makeText(MainActivity.this, "No donors", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.compatible:
                Intent intent = new Intent(MainActivity.this, CategorySelectedActivity.class);
                intent.putExtra("group","Compatible with me");
                startActivity(intent);
                break;

            case R.id.aPositive:
                Intent intent1 = new Intent(MainActivity.this, CategorySelectedActivity.class);
                intent1.putExtra("group","A+");
                startActivity(intent1);

                sendNotification("01726455545");

                break;

            case R.id.aNegative:
                Intent intent2 = new Intent(MainActivity.this, CategorySelectedActivity.class);
                intent2.putExtra("group","A-");
                startActivity(intent2);
                break;

            case R.id.abPositive:
                Intent intent3 = new Intent(MainActivity.this, CategorySelectedActivity.class);
                intent3.putExtra("group","AB+");
                startActivity(intent3);
                break;

            case R.id.abNegative:
                Intent intent4 = new Intent(MainActivity.this, CategorySelectedActivity.class);
                intent4.putExtra("group","AB-");
                startActivity(intent4);
                break;

            case R.id.bPositive:
                Intent intent5 = new Intent(MainActivity.this, CategorySelectedActivity.class);
                intent5.putExtra("group","B+");
                startActivity(intent5);
                break;

            case R.id.bNegative:
                Intent intent6 = new Intent(MainActivity.this, CategorySelectedActivity.class);
                intent6.putExtra("group","B-");
                startActivity(intent6);
                break;

            case R.id.oPositive:
                Intent intent7 = new Intent(MainActivity.this, CategorySelectedActivity.class);
                intent7.putExtra("group","O+");
                startActivity(intent7);
                break;

            case R.id.oNegative:
                Intent intent8 = new Intent(MainActivity.this, CategorySelectedActivity.class);
                intent8.putExtra("group","O-");
                startActivity(intent8);
                break;

            case R.id.profile:
                Intent intent9 = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent9);
                break;

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent10 = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent10);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void sendNotification(String topic){

        NotificationDto notificationDto = new NotificationDto(
                new Notification("Blood Needed","Blood need for Bolod"),
                topic
        );

        RetrofitClient.getApiInterface().sendNotification(notificationDto).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toast.makeText(getApplicationContext(),"Notification Success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(),t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}