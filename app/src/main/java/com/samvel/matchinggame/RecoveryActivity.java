package com.samvel.matchinggame;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.security.SecureRandom;
import java.util.ArrayList;

public class RecoveryActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    TextView returnReg, returnLogin;
    EditText email;
    Button resetPassword;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        returnLogin = findViewById(R.id.textReturnLogin);
        returnReg = findViewById(R.id.textReturn);
        resetPassword = findViewById(R.id.btnResetPassword);
        email = findViewById(R.id.inputEmail);

        resetPassword.setOnClickListener(view -> {

            String mEmail = email.getText().toString();

            if (isEmpty(email)) email.setError("Email mustn't be empty");
            else if (checkAccountEmailExistInFirebase(mEmail)) email.setError("Email is not found");
            else{
                progressDialog.setMessage("Sending recovery link...");
                progressDialog.setTitle("Password recovery");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                mAuth.sendPasswordResetEmail(mEmail).addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()){
                        startActivity(new Intent(this, LoginActivity.class));
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        this.finish();
                    }
                    else {
                        email.setError("Something went wrong");
                    }
                });
            }
        });

        returnReg.setOnClickListener(view -> {
            startActivity(new Intent(this, RegisterActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            this.finish();
        });

        returnLogin.setOnClickListener(view -> {
            startActivity(new Intent(this, LoginActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            this.finish();
        });

    }


    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    private boolean checkAccountEmailExistInFirebase(String email) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final boolean[] b = new boolean[1];
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> b[0] = !task.getResult().getSignInMethods().isEmpty());
        return b[0];
    }

}
