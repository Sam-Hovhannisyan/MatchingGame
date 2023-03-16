package com.samvel.matchinggame;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    TextView logIn, playOffline;
    private DatabaseReference rootDatabaseRef;
    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    EditText inputUsername, inputEmail, inputPassword, inputConformPassword;
    Button btnRegister;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Other actions

        logIn = findViewById(R.id.textViewLogIn);
        playOffline = findViewById(R.id.playOffline);
        rootDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        progressDialog = new ProgressDialog(this);

        try {
            mAuth.signOut();
        }catch (Exception e){
            Log.e("Sign out", "not signed in account");
        }

        // Inputs

        inputUsername = findViewById(R.id.inputUsername);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputCode);
        inputConformPassword = findViewById(R.id.inputConformPassword);

        // Buttons

        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(view -> {
            PerforAuth();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        });

        playOffline.setOnClickListener(view -> {
            ScoresActivity.i = 0;
            MainActivity.userName = "-1";
            changeActivity(MainActivity.class);
        });

        logIn.setOnClickListener(view -> {
            changeActivity(LoginActivity.class);
        });

    }

    private void PerforAuth() {
        String username = inputUsername.getText().toString();
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString();
        String conformPassword = inputConformPassword.getText().toString();

        rootDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(username)) {
                    inputUsername.setError("This username is already used");
                }
                else{
                    if(!validate(email)){
                        inputEmail.setError("Enter email properly");
                    }
                    else if (password.isEmpty() || password.length() < 6){
                        inputPassword.setError("Password length must be more than 6 symbols");
                    } else if (!password.equals(conformPassword)) {
                        inputConformPassword.setError("Conform password doesn't match");
                    }
                    else{
                        progressDialog.setMessage("Please wait while registration completes...");
                        progressDialog.setTitle("Registration");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();
                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                            progressDialog.dismiss();
                            if (task.isSuccessful()){
                                mUser.sendEmailVerification().addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()){
                                        writeNewUser(username, email);
                                        changeActivity(LoginActivity.class);
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(username)
                                                .build();
                                        user.updateProfile(profileUpdates);
                                    }
                                    else {
                                        inputEmail.setError("Verification error");
                                    }
                                });
                            }
                            else{
                                inputEmail.setError("This email is used");
                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.matches();
    }

    private void writeNewUser(String username, String email) {
        User user = new User(email, "0", "", "", "", "", 0);
        rootDatabaseRef.child(username).setValue(user);
    }

    private void changeActivity(Class class_){
        startActivity(new Intent(this, class_));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        this.finish();
    }

    boolean isEmail(EditText text) {
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    boolean checkDataEntered() {
        boolean f1 = true;
        boolean f2 = true;
        boolean f3 = true;
        boolean f4 = true;
        if (isEmpty(inputUsername)) {
            inputUsername.setError("You must enter username to register!");
            f1 = false;
        }

        if (isEmpty(inputPassword)) {
            inputPassword.setError("Password is required!");
            f2 = false;
        }

        if (isEmpty(inputConformPassword)) {
            inputConformPassword.setError("Conform Password is required!");
            f4 = false;
        }

        if (!isEmail(inputEmail)) {
            inputEmail.setError("Enter valid email!");
            f3 = false;
        }

        return f1 && f2 && f3 && f4;
    }
}
