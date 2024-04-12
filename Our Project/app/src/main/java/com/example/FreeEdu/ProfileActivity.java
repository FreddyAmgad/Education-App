package com.example.FreeEdu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.FreeEdu.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        context = this; // Set the current activity as the context
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        TextView email = (TextView) findViewById(R.id.profile_email_info);
        email.setText(currentUser.getEmail());
    }

    public void handleGoEdit(View view){
        Intent k = new Intent(ProfileActivity.this, EditProfileActivity.class);
        startActivity(k);
    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        TextView email = (TextView) findViewById(R.id.profile_email_info);
        email.setText(currentUser.getEmail());
    }

}