package com.example.FreeEdu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.Map;

public class QuestionHistoryActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Context context;

    public void getHistory(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Questions")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        mAuth = FirebaseAuth.getInstance();
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        final LinearLayout lm = (LinearLayout) findViewById(R.id.questions_history_list);
                        lm.removeAllViews();
                        if (task.isSuccessful()) {
                            int i = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map question = document.getData();
                                if(question.get("studentId").toString().equals(currentUser.getUid())){
                                    LinearLayout ll = new LinearLayout(context);
                                    ll.setId(i);
                                    ll.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent k = new Intent(QuestionHistoryActivity.this, QuestionDetailActivity.class);
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

                                    // Create Edit button
                                    Button edit_btn = new Button(context);
                                    edit_btn.setText("Edit");
                                    edit_btn.setTextColor(context.getResources().getColorStateList(R.color.white));
                                    date.setTextSize(14);
                                    title.setGravity(Gravity.LEFT);
                                    edit_btn.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    edit_btn.setBackgroundTintList(context.getResources().getColorStateList(R.color.highlightBlue));
                                    edit_btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent k = new Intent(QuestionHistoryActivity.this, EditQuestionActivity.class);
                                            Bundle b = new Bundle();
                                            b.putString("questionId", document.getId());
                                            k.putExtras(b);
                                            startActivity(k);
                                        }
                                    });
                                    ll.addView(edit_btn);

                                    lm.addView(ll);
                                    ll.setBackgroundColor(Color.WHITE);
                                    params.setMargins(0,0,0,40);
                                    ll.setBackground(ContextCompat.getDrawable(QuestionHistoryActivity.this, R.drawable.your_rounded_shape));
                                    i++;
                                }
                            }
                        }
                    }
                });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_history);
        context = this; // Set the current activity as the context
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        getHistory();
    }

    @Override
    public void onResume() {
        super.onResume();
        getHistory();
    }
}