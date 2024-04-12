package com.example.FreeEdu;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.FreeEdu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import org.checkerframework.checker.nullness.qual.NonNull;

public class EditProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        context = this; // Set the current activity as the context
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        EditText email = (EditText) findViewById(R.id.edit_email);
        email.setText(currentUser.getEmail());
    }


    public void editProfile(View view){
        EditText emailInput =  (EditText)findViewById(R.id.edit_email);
        if (emailInput.getText().toString().equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // Do something when the user clicks the OK button
                    dialog.cancel();
                }
            });
            builder.setTitle("Edit Email");
            builder.setMessage("Please enter the email.");
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }
        String email = emailInput.getText().toString();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.updateEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast toast = Toast.makeText(context, "Edit email success!", Toast.LENGTH_LONG);
                            toast.show();
                        }
                        else{
                            Toast toast = Toast.makeText(context, "Edit email Failed! Please try log out and log in.", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                });
    }
}