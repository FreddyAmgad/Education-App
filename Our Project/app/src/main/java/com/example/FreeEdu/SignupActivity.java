package com.example.FreeEdu;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;


public class SignupActivity extends AppCompatActivity {
    // Initialize Firebase Auth
    private FirebaseAuth mAuth;
    private Context context;
    private String userType = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        context = this; // Set the current activity as the context
    }

    public void handleUserTypeChange(View view){
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_btn_teacher:
                if (checked){
                    userType = "TEACHER";
                    Log.i("User Type","TEACHER");
                }
                break;
            case R.id.radio_btn_student:
                if (checked){
                    userType = "STUDENT";
                    Log.i("User Type","STUDENT");
                }
                break;
        }
    }


    public void handleSignUp(View view) {
        if (userType.equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // Do something when the user clicks the OK button
                    dialog.cancel();
                }
            });
            builder.setTitle("Sign Up");
            builder.setMessage("Please select whether you are a student or a teacher.");
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }
        EditText emailInput = (EditText) findViewById(R.id.signupEmailInput);
        if (emailInput.getText().toString().equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // Do something when the user clicks the OK button
                    dialog.cancel();
                }
            });
            builder.setTitle("Sign Up");
            builder.setMessage("Please enter email.");
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }
        String email = emailInput.getText().toString();

        EditText passwordInput = (EditText) findViewById(R.id.signupPasswordInput);
        if (passwordInput.getText().toString().equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // Do something when the user clicks the OK button
                    dialog.cancel();
                }
            });
            builder.setTitle("Sign Up");
            builder.setMessage("Please enter password.");
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }
        String password = passwordInput.getText().toString();
        EditText confirmPasswordInput = (EditText) findViewById(R.id.signupConfirmPasswordInput);
        if (confirmPasswordInput.getText().toString().equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // Do something when the user clicks the OK button
                    dialog.cancel();
                }
            });
            builder.setTitle("Sign Up");
            builder.setMessage("Please enter confirm password.");
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }
        String confirmPassword = passwordInput.getText().toString();
        if (password.equals(confirmPassword)) {
            mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser currentUser = mAuth.getCurrentUser();

                                Map<String, Object> userInfo = new HashMap<>();
                                userInfo.put("email", currentUser.getEmail());
                                userInfo.put("type", userType);
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                db.collection("Users").document(currentUser.getUid()).set(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // Sign in success, update UI with the signed-in user's information
                                                Toast toast = Toast.makeText(context, "Sign Up Success!", Toast.LENGTH_LONG);
                                                toast.show();
                                                //go to home page
                                                Intent k;
                                                if (userType.equals("STUDENT")) {
                                                    k = new Intent(SignupActivity.this, StudentHomeActivity.class);
                                                } else {
                                                    k = new Intent(SignupActivity.this, TeacherHomeActivity.class);
                                                }
                                                startActivity(k);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        // Do something when the user clicks the OK button
                                                        dialog.cancel();
                                                    }
                                                });
                                                builder.setTitle("Signup");
                                                builder.setMessage("Create account failed!");
                                                AlertDialog dialog = builder.create();
                                                dialog.show();
                                                return;
                                            }
                                        });

                            } else {
                                // If sign in fails, display a message to the user.
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // Do something when the user clicks the OK button
                                        dialog.cancel();
                                    }
                                });
                                builder.setTitle("Signup");
                                builder.setMessage("Create account failed!");
                                AlertDialog dialog = builder.create();
                                dialog.show();
                                return;
                            }
                        }
                    });

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // Do something when the user clicks the OK button
                    dialog.cancel();
                }
            });
            builder.setTitle("Sign Up");
            builder.setMessage("Please make sure the password and confirm password are same.");
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }

    }
}