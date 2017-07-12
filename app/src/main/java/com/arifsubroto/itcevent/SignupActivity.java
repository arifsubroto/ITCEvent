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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends BaseActivity implements View.OnClickListener  {

    private EditText nameField;
    private EditText emailField;
    private EditText passwordField;
    private EditText confirmPasswordField;
    private Button signUpButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        nameField = (EditText)findViewById(R.id.nameField);
        emailField = (EditText)findViewById(R.id.emailField);
        passwordField = (EditText)findViewById(R.id.passwordField);
        confirmPasswordField = (EditText)findViewById(R.id.confirmPasswordField);
        signUpButton = (Button) findViewById(R.id.signupButton);

        signUpButton.setOnClickListener(this);
    }

    private void signUp() {
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        String name = nameField.getText().toString();
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();
        String confirmPassword = confirmPasswordField.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            //onAuthSuccess(task.getResult().getUser());
                            Toast.makeText(getApplicationContext(), "Sign Up Success", Toast.LENGTH_SHORT).show();
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
            passwordField.setError("Required");
            result = false;
        } else if(!pass.equals(cpass)){
            confirmPasswordField.setError("Didn't match");
            result = false;
        } else {
            confirmPasswordField.setError(null);
        }

        return result;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.signupButton) {
            signUp();
        }
    }
}
