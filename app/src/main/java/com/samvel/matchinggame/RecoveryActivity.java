package com.samvel.matchinggame;

import android.annotation.SuppressLint;
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

import java.security.SecureRandom;
import java.util.ArrayList;

public class RecoveryActivity extends AppCompatActivity {

    TextView returnReg, returnLogin;
    EditText username, email, code;
    Button getCode;
    ArrayList<String> user_username, user_email;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery);

        //myDB = new MyDatabaseHelper(RecoveryActivity.this);
        user_username = new ArrayList<>();
        user_email = new ArrayList<>();

        returnLogin = findViewById(R.id.textReturnLogin);
        returnReg = findViewById(R.id.textReturn);
        getCode = findViewById(R.id.btnGetCode);
        username = findViewById(R.id.inputUsername);
        email = findViewById(R.id.inputEmail);
        code = findViewById(R.id.inputCode);

        getData();

        getCode.setOnClickListener(view -> {
            if (isEmpty(username)) username.setError("Username mustn't be empty");
            else if (!user_username.contains(username.getText().toString())) username.setError("Username is not found");
            if (isEmpty(email)) email.setError("Email mustn't be empty");
            else if (!user_email.contains(email.getText().toString())) email.setError("Email is not found");
            else{
                String mEmail = email.getText().toString();
                String mSubject = "Recovery Code";
                String mMessage = generateRandomPassword(8);

                Toast.makeText(this, mMessage, Toast.LENGTH_SHORT).show();

                getCode.setText("Reset my password");
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

    void getData() {/*
        Cursor cursor = myDB.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                user_username.add(cursor.getString(1));
                user_email.add(cursor.getString(2));
            }
        }*/
    }

    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    private static String generateRandomPassword(int len) {
        // ASCII range – alphanumeric (0-9, a-z, A-Z)
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";

        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        // each iteration of the loop randomly chooses a character from the given
        // ASCII range and appends it to the `StringBuilder` instance

        for (int i = 0; i < len; i++)
        {
            int randomIndex = random.nextInt(chars.length());
            sb.append(chars.charAt(randomIndex));
        }

        return sb.toString();
    }
}
