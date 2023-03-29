package com.samvel.matchinggame;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    ArrayList<String> fEmail, fPassword;
    private DatabaseReference rootDatabaseRef;
    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z\\d._%+-]+@[A-Z\\d.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    TextView signUpBtn, forgotPassword;
    EditText inputEmail, inputPassword;
    Button logInBtn;
    ImageView backToMenu;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signUpBtn = findViewById(R.id.textViewSignUp);
        logInBtn = findViewById(R.id.btnlogin);
        forgotPassword = findViewById(R.id.forgotPassword);
        backToMenu = findViewById(R.id.backToMenu);

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputCode);

        rootDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users");
        fEmail = new ArrayList<>();
        fPassword = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        progressDialog = new ProgressDialog(this, R.style.Custom);

        getFirebaseData();

        signUpBtn.setOnClickListener(view -> {
            Methods.clickSound(this);
            signUpBtn.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
            changeActivity(RegisterActivity.class);
        });

        backToMenu.setOnClickListener(view -> {
            Methods.clickSound(this);
            backToMenu.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
            changeActivity(MenuActivity.class);
        });

        logInBtn.setOnClickListener(view -> {
            if (inputEmail.getText().toString().equals("admin")) {
                changeActivity(ShowDatabase.class);
            } else{
                PerformAuth();
            }

        });

        forgotPassword.setOnClickListener(view -> {
            Methods.clickSound(this);
            forgotPassword.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
            changeActivity(RecoveryActivity.class);
        });
    }

    @Override
    public void onBackPressed() {
        changeActivity(RegisterActivity.class);
    }

    private void PerformAuth() {
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString();

        if(!validate(email)){
            inputEmail.setError("Enter email properly");
        }
        else if (password.isEmpty() || password.length() < 6){
            inputPassword.setError("Password length must be more than 6 symbols");
        }
        else{
            progressDialog.setMessage("Please wait while log in completes...");
            progressDialog.setTitle("Log in");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                progressDialog.dismiss();
                if (task.isSuccessful()){
                    if (mAuth.getCurrentUser().isEmailVerified()){
                        changeActivity(MainActivity.class);
                        rootDatabaseRef.child(mAuth.getCurrentUser().getDisplayName()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                    String value = dataSnapshot.getValue().toString();
                                    if (Objects.equals(dataSnapshot.getKey(), "bestScore")) SettingsActivity.bestScoreInt = Integer.parseInt(value);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        try {
                            mUser = mAuth.getCurrentUser();
                            MainActivity.userName = mUser.getDisplayName();
                        }
                        catch (Exception e){
                            Log.e("Sth went wrong", "line 108");
                        }
                    }
                    else {
                        mAuth.getCurrentUser().sendEmailVerification();
                        inputEmail.setError("Please verify your email");
                    }
                } else {
                    if (isNetworkConnected()) inputPassword.setError("Incorrect password!");
                    else inputPassword.setError("Please connect to the internet");
                }
            });

        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.matches();
    }
    private void changeActivity(Class class_){
        startActivity(new Intent(this, class_));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        //this.finish();
    }

    private void getFirebaseData() {
        rootDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    try {
                        fEmail.add(dataSnapshot.child("email").getValue().toString());
                        fPassword.add(dataSnapshot.child("password").getValue().toString());
                    } catch (Exception e) {
                        Log.e("msg", "get data error");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
