package com.samvel.matchinggame;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;
import io.github.muddz.styleabletoast.StyleableToast;

public class RegisterActivity extends AppCompatActivity {

    TextView logIn;
    ImageView backToMenu;
    private DatabaseReference rootDatabaseRef;
    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    EditText inputUsername, inputEmail, inputPassword, inputConformPassword;
    Button btnRegister;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z\\d._%+-]+@[A-Z\\d.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Other actions

        logIn = findViewById(R.id.textViewLogIn);
        backToMenu = findViewById(R.id.backToMenu);
        rootDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        progressDialog = new ProgressDialog(this, R.style.Custom);

        try {
            mAuth.signOut();
        } catch (Exception e) {
            Log.e("Sign out", "not signed in account");
        }

        // Inputs

        inputUsername = findViewById(R.id.inputUsername);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputCode);
        inputConformPassword = findViewById(R.id.inputConformPassword);

        // Buttons

        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(view -> PerformAuth());

        backToMenu.setOnClickListener(view -> {
            Methods.clickSound(this);
            backToMenu.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
            changeActivity(MenuActivity.class);
        });

        logIn.setOnClickListener(view -> {
            Methods.clickSound(this);
            logIn.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
            changeActivity(LoginActivity.class);
        });

    }

    private void PerformAuth() {
        String username = inputUsername.getText().toString();
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString();
        String conformPassword = inputConformPassword.getText().toString();

        rootDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("users").hasChild(username)) {
                    inputUsername.setError("This username is already used");
                } else {
                    if (!validate(email)) {
                        inputEmail.setError("Enter email properly");
                    } else if (password.isEmpty() || password.length() < 6) {
                        inputPassword.setError("Password length must be more than 6 symbols");
                    } else if (!password.equals(conformPassword)) {
                        inputConformPassword.setError("Conform password doesn't match");
                    } else {
                        progressDialog.setMessage("Please wait while registration completes...");
                        progressDialog.setTitle("Registration");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();
                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        writeNewUser(username, email);
                                        StyleableToast.makeText(RegisterActivity.this, "Please check your email", Toast.LENGTH_LONG, R.style.mytoast).show();
                                        changeActivity(LoginActivity.class);
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(username)
                                                .build();
                                        user.updateProfile(profileUpdates);
                                    } else {
                                        mAuth.getCurrentUser().delete();
                                        inputEmail.setError("Verification error");
                                    }
                                });
                            } else {

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

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.matches();
    }

    private void writeNewUser(String username, String email) {
        User user = new User(email, "0", "", "", "", "", 0);
        rootDatabaseRef.child("users").child(username).setValue(user);
    }

    private void changeActivity(Class class_) {
        startActivity(new Intent(this, class_));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        this.finish();
    }
}
