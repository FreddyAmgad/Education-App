package com.example.FreeEdu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class TeacherHomeActivity extends AppCompatActivity {
    // Initialize Firebase Auth
    private FirebaseAuth mAuth;
    private Context context;

    public void getAllQuestions(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Questions")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        final LinearLayout lm = (LinearLayout) findViewById(R.id.question_history_list);
                        lm.removeAllViews();
                        if (task.isSuccessful()) {
                            int i = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map question = document.getData();
                                LinearLayout ll = new LinearLayout(context);
                                ll.setId(i);
                                ll.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent k = new Intent(TeacherHomeActivity.this, QuestionDetailActivity.class);
                                        Bundle b = new Bundle();
                                        b.putString("questionId", document.getId());
                                        k.putExtras(b);
                                        startActivity(k);
                                    }
                                });
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                ll.setLayoutParams(params);
                                ll.setOrientation(LinearLayout.VERTICAL);

                                // Create Title
                                TextView title = new TextView(context);
                                title.setText("Question:");
                                title.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                title.setTextSize(16);
                                title.setGravity(Gravity.LEFT);
                                ll.addView(title);
                                //Create Content
                                TextView content = new TextView(context);
                                content.setText(question.get("content").toString());
                                content.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                content.setTextSize(18);
                                content.setGravity(Gravity.CENTER);
                                content.setPadding(10,20,0,20);
                                ll.addView(content);

                                // Create Date
                                TextView date = new TextView(context);
                                date.setText(question.get("createAt").toString());
                                date.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                date.setTextSize(14);
                                date.setGravity(Gravity.RIGHT);
                                ll.addView(date);

                                lm.addView(ll);
                                ll.setBackgroundColor(Color.WHITE);
                                params.setMargins(0,0,0,40);
                                ll.setBackground(ContextCompat.getDrawable(TeacherHomeActivity.this, R.drawable.your_rounded_shape));
                                i++;
                            }
                        }
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_home);
        context = this; // Set the current activity as the context
        getAllQuestions();
    }

    public void handleLogout(View view){
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        Intent k;
        k = new Intent(TeacherHomeActivity.this, LandingActivity.class);
        startActivity(k);
    }

    public void handleGoProfile(View view){
        Intent k = new Intent(TeacherHomeActivity.this, ProfileActivity.class);
        startActivity(k);
    }
    @Override
    public void onResume() {
        super.onResume();
        getAllQuestions();
    }

}