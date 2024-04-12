package com.example.FreeEdu;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StudentHomeActivity extends AppCompatActivity {
    // Initialize Firebase Auth
    private FirebaseAuth mAuth;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_home);
        context = this; // Set the current activity as the context
    }

    public void handleLogout(View view){
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        Intent k;
        k = new Intent(StudentHomeActivity.this, LandingActivity.class);
        startActivity(k);
    }

    public void handleAskQuestion(View view){
        EditText questionInput =  (EditText)findViewById(R.id.ask_question);
        if (questionInput.getText().toString().equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // Do something when the user clicks the OK button
                    dialog.cancel();
                }
            });
            builder.setTitle("Ask Question");
            builder.setMessage("Please enter the content of the question.");
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }
        String question = questionInput.getText().toString();
        // Create a Date object with the current date and time
        Date currentDate = new Date();

        // Create a SimpleDateFormat object with the desired date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        // Format the date into a string
        String formattedDate = dateFormat.format(currentDate);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Map<String, Object> data = new HashMap<>();
        data.put("content", question);
        data.put("studentId", currentUser.getUid());
        data.put("createAt", formattedDate);
        data.put("answers", new ArrayList<>());
        db.collection("Questions").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast toast = Toast.makeText(context, "Post question success!", Toast.LENGTH_LONG);
                        toast.show();
                        questionInput.setText("");
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
                        builder.setTitle("Ask Question");
                        builder.setMessage("Post question failed!");
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        return;
                    }
                });
    }

    public void handleGoProfile(View view){
        Intent k = new Intent(StudentHomeActivity.this, ProfileActivity.class);
        startActivity(k);
    }

    public void handleGoHistory(View view){
        Intent k = new Intent(StudentHomeActivity.this, QuestionHistoryActivity.class);
        startActivity(k);
    }
}