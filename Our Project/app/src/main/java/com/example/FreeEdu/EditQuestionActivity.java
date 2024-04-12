package com.example.FreeEdu;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditQuestionActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Context context;
    private String questionId;

    private void getQuestionInfoById(){
        if(questionId != null){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("Questions").document(questionId);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map question = document.getData();
                            //set question content
                            EditText contentTextView =  (EditText) findViewById(R.id.edit_question_content);
                            contentTextView.setText(question.get("content").toString());
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_question);
        context = this; // Set the current activity as the context
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Bundle b = getIntent().getExtras();
        questionId = "";
        if(b != null){
            questionId = b.getString("questionId");
            getQuestionInfoById();
        }
    }

    public void handleEditQuestion(View view){
        EditText answerInput =  (EditText)findViewById(R.id.edit_question_content);
        if (answerInput.getText().toString().equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // Do something when the user clicks the OK button
                    dialog.cancel();
                }
            });
            builder.setTitle("Edit Question");
            builder.setMessage("Please enter the content of the question.");
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }
        String question = answerInput.getText().toString();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put("content",question);
        db.collection("Questions").document(questionId).set(data, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast toast = Toast.makeText(context, "Edit question success!", Toast.LENGTH_LONG);
                        toast.show();
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
}