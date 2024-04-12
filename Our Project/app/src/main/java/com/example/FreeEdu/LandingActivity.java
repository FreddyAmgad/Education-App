package com.example.FreeEdu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.View;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.Map;

public class LandingActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing);
        context = this; // Set the current activity as the context
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if(currentUser != null){
            DocumentReference docRef = db.collection("Users").document(currentUser.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map userInfo = document.getData();
                            Intent k;
                            if(userInfo.get("type").toString().equals("STUDENT")){
                                k = new Intent(LandingActivity.this, StudentHomeActivity.class);
                            }
                            else{
                                k = new Intent(LandingActivity.this, TeacherHomeActivity.class);
                            }
                            startActivity(k);
                        }
                    }
                }
            });
        }
    }

    public void handleGoLogin(View view){
        Intent k = new Intent(LandingActivity.this, LoginActivity.class);
        startActivity(k);
    }
    public void handleGoSignup(View view){
        Intent k = new Intent(LandingActivity.this, SignupActivity.class);
        startActivity(k);
    }
}