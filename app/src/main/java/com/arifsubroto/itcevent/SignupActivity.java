package com.arifsubroto.itcevent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.arifsubroto.itcevent.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class SignupActivity extends BaseActivity implements View.OnClickListener  {

    private EditText nameField;
    private EditText emailField;
    private EditText prodiField;
    private EditText angkatanField;
    private EditText passwordField;
    private EditText confirmPasswordField;
    private Button signUpButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName("member").build();
                    user.updateProfile(profileUpdates);
                    Intent intent = new Intent(SignupActivity.this, AdminHomeActivity.class);
                    startActivity(intent);
                }
            }
        };

        nameField = (EditText)findViewById(R.id.nameField);
        emailField = (EditText)findViewById(R.id.emailField);
        prodiField= (EditText)findViewById(R.id.prodiField);
        angkatanField = (EditText)findViewById(R.id.angkatanField);
        passwordField = (EditText)findViewById(R.id.passwordField);
        confirmPasswordField = (EditText)findViewById(R.id.confirmPasswordField);
        signUpButton = (Button) findViewById(R.id.signupButton);

        signUpButton.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener); //firebaseAuth is of class FirebaseAuth
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void signUp() {
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        String name = nameField.getText().toString();
        String prodi = prodiField.getText().toString();
        String angkatan = angkatanField.getText().toString();
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            //Toast.makeText(SignInActivity.this, "Sign Up Failed", Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(emailField.getText().toString())) {
            emailField.setError("Required");
            result = false;
        } else {
            emailField.setError(null);
        }

        String pass = passwordField.getText().toString();
        String cpass = confirmPasswordField.getText().toString();

        if (TextUtils.isEmpty(pass)) {
            passwordField.setError("Required");
            result = false;
        } else {
            passwordField.setError(null);
        }

        if (TextUtils.isEmpty(cpass)) {
            confirmPasswordField.setError("Required");
            result = false;
        } else if(!pass.equals(cpass)){
            confirmPasswordField.setError("Didn't match");
            passwordField.setError("Didn't match");
            result = false;
        } else {
            confirmPasswordField.setError(null);
        }

        return result;
    }

    private void onAuthSuccess(FirebaseUser user) {
        String name = nameField.getText().toString();
        String prodi = prodiField.getText().toString();
        String angkatan = angkatanField.getText().toString();

        // Write new user
        writeNewUser(user.getUid(), name, prodi, angkatan);

        // Go to UserActivity
        startActivity(new Intent(getApplicationContext(), UserHomeActivity.class));
        finish();
    }

    private void writeNewUser(String userId, String name, String prodi, String angkatan) {
        User user = new User(name, prodi, angkatan);

        mDatabase.child("users").child(userId).setValue(user);
    }



    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.signupButton) {
            signUp();
        }
    }
}

