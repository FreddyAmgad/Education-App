package com.example.FreeEdu;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.FreeEdu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class QuestionDetailActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Context context;

    private String questionId;

    private Integer answersLength;

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
                            TextView contentTextView =  (TextView) findViewById(R.id.question_detail_content);
                            contentTextView.setText(question.get("content").toString());

                            //set answers
                            final LinearLayout lm = (LinearLayout) findViewById(R.id.question_detail_answer_list);
                            ArrayList<Map> answerArr = (ArrayList) question.get("answers");
                            if(answerArr != null){
                                for (int counter = 0; counter < answerArr.size(); counter++) {
                                    LinearLayout ll = new LinearLayout(context);
                                    ll.setId(counter);
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    ll.setLayoutParams(params);
                                    ll.setOrientation(LinearLayout.VERTICAL);
                                    // Create Title
                                    TextView title = new TextView(context);
                                    title.setText("Teacher" + (counter+1) + ":");
                                    title.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    title.setTextSize(16);
                                    title.setGravity(Gravity.LEFT);
                                    ll.addView(title);
                                    // Create Date
                                    TextView date = new TextView(context);
                                    date.setText(answerArr.get(counter).get("createAt").toString());
                                    date.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    date.setTextSize(14);
                                    date.setGravity(Gravity.RIGHT);
                                    ll.addView(date);
                                    //Create Content
                                    TextView content = new TextView(context);
                                    content.setText(answerArr.get(counter).get("content").toString());
                                    content.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    content.setTextSize(18);
                                    content.setGravity(Gravity.CENTER);
                                    content.setPadding(10,20,0,20);
                                    ll.addView(content);
                                    lm.addView(ll);
                                    ll.setBackgroundColor(Color.WHITE);
                                    params.setMargins(0,0,0,40);
                                    ll.setBackground(ContextCompat.getDrawable(QuestionDetailActivity.this, R.drawable.your_rounded_shape));
                                    answersLength++;
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_detail);
        context = this; // Set the current activity as the context
        Bundle b = getIntent().getExtras();
        questionId = "";
        answersLength=0;
        if(b != null){
            questionId = b.getString("questionId");
            getQuestionInfoById();
        }
    }

    public void postAnswer(View view){
        EditText answerInput =  (EditText)findViewById(R.id.editTextAnswers);
        if (answerInput.getText().toString().equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // Do something when the user clicks the OK button
                    dialog.cancel();
                }
            });
            builder.setTitle("Post Answer");
            builder.setMessage("Please enter the content of the answer.");
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }
        String answer = answerInput.getText().toString();
        // Create a Date object with the current date and time
        Date currentDate = new Date();

        // Create a SimpleDateFormat object with the desired date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        // Format the date into a string
        String formattedDate = dateFormat.format(currentDate);

        //start upsert data
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> aData = new HashMap<>();
        aData.put("content", answer);
        aData.put("teacherId", currentUser.getUid());
        aData.put("createAt", formattedDate);
        ArrayList arrData = new ArrayList();
        arrData.add(aData);
        data.put("answers",arrData);
        db.collection("Questions").document(questionId).update("answers",FieldValue.arrayUnion(aData)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Sign in success, update UI with the signed-in user's information
                        final LinearLayout lm = (LinearLayout) findViewById(R.id.question_detail_answer_list);
                        LinearLayout ll = new LinearLayout(context);
                        ll.setId(answersLength);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        ll.setLayoutParams(params);
                        ll.setOrientation(LinearLayout.VERTICAL);
                        // Create Title
                        TextView title = new TextView(context);
                        title.setText("Teacher" + (answersLength+1) + ":");
                        title.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        title.setTextSize(16);
                        title.setGravity(Gravity.LEFT);
                        ll.addView(title);
                        // Create Date
                        TextView date = new TextView(context);
                        date.setText(formattedDate);
                        date.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        date.setTextSize(14);
                        date.setGravity(Gravity.RIGHT);
                        ll.addView(date);
                        //Create Content
                        TextView content = new TextView(context);
                        content.setText(answer);
                        content.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        content.setTextSize(18);
                        content.setGravity(Gravity.CENTER);
                        content.setPadding(10,20,0,20);
                        ll.addView(content);
                        lm.addView(ll);
                        ll.setBackgroundColor(Color.WHITE);
                        params.setMargins(0,0,0,40);
                        ll.setBackground(ContextCompat.getDrawable(QuestionDetailActivity.this, R.drawable.your_rounded_shape));
                        Toast toast = Toast.makeText(context, "Post answer success!", Toast.LENGTH_LONG);
                        toast.show();
                        answerInput.setText("");
                        answersLength++;
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