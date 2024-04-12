package com.example.FreeEdu;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Map;


public class LoginActivity extends AppCompatActivity {
    // Initialize Firebase Auth
    private FirebaseAuth mAuth;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        context = this; // Set the current activity as the context
    }

    public void handleLogin(View view) {
        EditText emailInput = (EditText)findViewById(R.id.emailInput);
        if (emailInput.getText().toString().equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // Do something when the user clicks the OK button
                    dialog.cancel();
                }
            });
            builder.setTitle("Login");
            builder.setMessage("Please enter email.");
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }
        String email = emailInput.getText().toString();
        EditText passwordInput = (EditText)findViewById(R.id.passwordInput);
        if (passwordInput.getText().toString().equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // Do something when the user clicks the OK button
                    dialog.cancel();
                }
            });
            builder.setTitle("Login");
            builder.setMessage("Please enter password.");
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }
        String password = passwordInput.getText().toString();
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast toast = Toast.makeText(context, "Login Success!", Toast.LENGTH_LONG);
                            toast.show();
                            //go to home page
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
                                                    k = new Intent(LoginActivity.this, StudentHomeActivity.class);
                                                }
                                                else{
                                                    k = new Intent(LoginActivity.this, TeacherHomeActivity.class);
                                                }
                                                startActivity(k);
                                            }
                                        }
                                    }
                                });
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // Do something when the user clicks the OK button
                                    dialog.cancel();
                                }
                            });
                            builder.setTitle("Login Failed");
                            builder.setMessage("Password or Email does not match.");
                            AlertDialog dialog = builder.create();
                            dialog.show();
                            return;
                        }
                    }
                });
    }

}