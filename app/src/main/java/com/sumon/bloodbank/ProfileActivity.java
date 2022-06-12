package com.sumon.bloodbank;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView type, name, email, idNumber, phoneNumber, bloodGroup;
    private CircleImageView profileImg;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        type = findViewById(R.id.profileType);
        name = findViewById(R.id.profileName);
        email = findViewById(R.id.profileEmail);
        idNumber = findViewById(R.id.profileIdNumber);
        phoneNumber = findViewById(R.id.profilePhoneNumber);
        bloodGroup = findViewById(R.id.profileBloodGroup);
        profileImg = findViewById(R.id.profileImg);
        backButton = findViewById(R.id.backButton);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    type.setText(snapshot.child("type").getValue().toString());
                    name.setText(snapshot.child("name").getValue().toString());
                    idNumber.setText(snapshot.child("idNumber").getValue().toString());
                    phoneNumber.setText(snapshot.child("phoneNumber").getValue().toString());
                    bloodGroup.setText(snapshot.child("bloodGroup").getValue().toString());
                    email.setText(snapshot.child("email").getValue().toString());

                    Glide.with(getApplicationContext()).load(snapshot.child("profilePictureUrl").getValue().toString()).into(profileImg);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }
}