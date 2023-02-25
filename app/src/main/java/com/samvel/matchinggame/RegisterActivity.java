package com.samvel.matchinggame;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    TextView alreadyHaveAccount, playOffline, errorText;
    MyDatabaseHelper myDB;
    EditText inputUsername, inputEmail, inputPassword, inputConformPassword;
    Button btnRegister;
    ArrayList<String> user_username = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Other actions

        alreadyHaveAccount = findViewById(R.id.alreadyHaveAccount);
        playOffline = findViewById(R.id.playOffline);
        errorText = findViewById(R.id.errorText);
        myDB = new MyDatabaseHelper(RegisterActivity.this);

        getUsernames();

        // Inputs

        inputUsername = findViewById(R.id.inputUsername);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputConformPassword = findViewById(R.id.inputConformPassword);

        // Buttons

        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(view -> {
            /*if (inputUsername.getText().toString().equals("load")) {
                myDB.addUser("aaa".trim(), "aa@aa.com".trim(), "aa".trim(), "0".trim(), "a".trim());
            }
            else*/if (!checkDataEntered()) {
                errorText.setVisibility(View.VISIBLE);
            } else if (!inputPassword.getText().toString().equals(inputConformPassword.getText().toString())) {
                inputConformPassword.setError("Conform password doesn't match");
            } else if(user_username.contains(inputUsername.getText().toString())){
                inputUsername.setError("This username is already exist!");
            }
            else {
                errorText.setVisibility(View.INVISIBLE);
                MyDatabaseHelper myDB = new MyDatabaseHelper(RegisterActivity.this);
                myDB.addUser(inputUsername.getText().toString().trim(),
                        inputEmail.getText().toString().trim(),
                        inputPassword.getText().toString().trim(), "0", "", "", "", "");
                startActivity(new Intent(this, LoginActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                this.finish();
            }
        });

        playOffline.setOnClickListener(view -> {
            ScoresActivity.i = 0;
            MainActivity.user_id = -1;
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            this.finish();
        });

        alreadyHaveAccount.setOnClickListener(view -> {
            startActivity(new Intent(this, LoginActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            this.finish();
        });

    }

    void getUsernames(){
        Cursor cursor = myDB.readAllData();
        if (cursor.getCount() > 0){
            while (cursor.moveToNext()){
                user_username.add(cursor.getString(1));
            }
        }
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
