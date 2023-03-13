package com.samvel.matchinggame;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    ArrayList<String> fEmail, fPassword;
    private DatabaseReference rootDatabaseRef;
    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    TextView signUpBtn, forgotPassword;
    EditText inputEmail, inputPassword;
    Button logInBtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signUpBtn = findViewById(R.id.textViewSignUp);
        logInBtn = findViewById(R.id.btnlogin);
        forgotPassword = findViewById(R.id.forgotPassword);

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputCode);

        rootDatabaseRef = FirebaseDatabase.getInstance().getReference();
        fEmail = new ArrayList<>();
        fPassword = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        progressDialog = new ProgressDialog(this);

        getFirebaseData();

        signUpBtn.setOnClickListener(view -> {
            changeActivity(RegisterActivity.class);
        });

        logInBtn.setOnClickListener(view -> {
            if (inputEmail.getText().toString().equals("admin")) {
                changeActivity(ShowDatabase.class);
            } else{
                PerforAuth();
            }

        });

        forgotPassword.setOnClickListener(view -> {
            changeActivity(RecoveryActivity.class);
        });
    }


    private void PerforAuth() {
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
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    changeActivity(MainActivity.class);
                    try {
                        mUser = mAuth.getCurrentUser();
                        MainActivity.userName = mUser.getDisplayName();
                        //ReviewsActivity.userName = mUser.getDisplayName();
                    }
                    catch (Exception e){
                        Log.e("Sth went wrong", "line 108");
                    }

//                    MainActivity.user_id = fEmail.indexOf(inputEmail.getText().toString());
//                    ReviewsActivity.user_id = fEmail.indexOf(inputEmail.getText().toString());
                    ScoresActivity.i = 1;
                    //Log.e("username", mUser.getDisplayName());
                } else {
                    progressDialog.dismiss();
                    inputPassword.setError("Incorrect password!");
                }
            });

        }
    }

    private static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.matches();
    }
    private void changeActivity(Class class_){
        startActivity(new Intent(this, class_));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        this.finish();
    }


    private void getFirebaseData() {
        rootDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    try {
                        fEmail.add(dataSnapshot.child("email").getValue().toString());
                        fPassword.add(dataSnapshot.child("password").getValue().toString());
                        Log.e("log-in", "all right");
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

    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

}
