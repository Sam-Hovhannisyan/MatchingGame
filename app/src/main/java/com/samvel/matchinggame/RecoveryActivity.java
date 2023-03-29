package com.samvel.matchinggame;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

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

            String mEmail = email.getText().toString().trim();

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
                        email.setError("Something went wrong or incorrect account");
                    }
                });
            }
        });

        returnReg.setOnClickListener(view -> {
            Methods.clickSound(this);
            returnReg.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
            changeActivity(RegisterActivity.class);
        });

        returnLogin.setOnClickListener(view -> {
            Methods.clickSound(this);
            returnLogin.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
            changeActivity(LoginActivity.class);
        });

    }

    private void changeActivity(Class class_) {
        startActivity(new Intent(this, class_));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        changeActivity(LoginActivity.class);
    }


    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    private boolean checkAccountEmailExistInFirebase(String email) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final boolean[] b = new boolean[1];

        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()){

                b[0] = !task.getResult().getSignInMethods().isEmpty();
            }
            else {
                this.email.setError("Incorrect account");
            }
        });

        return b[0];
    }

}
