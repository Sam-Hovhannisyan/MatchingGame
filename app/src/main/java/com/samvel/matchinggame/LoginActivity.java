package com.samvel.matchinggame;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    ArrayList<String> user_username, user_password;
    MyDatabaseHelper myDB;
    TextView signUpBtn, forgotPassword;
    EditText inputUsername, inputPassword;
    Button logInBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signUpBtn = findViewById(R.id.textViewSignUp);
        logInBtn = findViewById(R.id.btnlogin);
        forgotPassword = findViewById(R.id.forgotPassword);

        inputUsername = findViewById(R.id.inputUsername);
        inputPassword = findViewById(R.id.inputCode);

        myDB = new MyDatabaseHelper(LoginActivity.this);
        user_username = new ArrayList<>();
        user_password = new ArrayList<>();

        getData();

        signUpBtn.setOnClickListener(view -> {
            startActivity(new Intent(this, RegisterActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            this.finish();
        });

        logInBtn.setOnClickListener(view -> {
            if (isEmpty(inputUsername))
                inputUsername.setError("You must enter username to sign in!");
            else if (inputUsername.getText().toString().equals("admin")) {
                startActivity(new Intent(this, ShowDatabase.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                this.finish();
            } else if (!user_username.contains(inputUsername.getText().toString())) {
                inputUsername.setError("Username doesn't found!");
            } else if (isEmpty(inputPassword))
                inputPassword.setError("You must enter password to sign in!");
            else if (inputPassword.getText().toString().equals(user_password.get(user_username.indexOf(inputUsername.getText().toString())))) {
                MainActivity.user_id = user_username.indexOf(inputUsername.getText().toString());
                ScoresActivity.i = 1;
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                this.finish();
            } else {
                Toast.makeText(this, "Input Error", Toast.LENGTH_SHORT).show();
            }

        });

        forgotPassword.setOnClickListener(view -> {
            startActivity(new Intent(this, RecoveryActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            this.finish();
        });
    }

    void getData() {
        Cursor cursor = myDB.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                user_username.add(cursor.getString(1));
                user_password.add(cursor.getString(3));
            }
        }
    }

    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

}
