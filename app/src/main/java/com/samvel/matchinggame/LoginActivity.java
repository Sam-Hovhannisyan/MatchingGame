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
import java.util.SortedMap;

public class LoginActivity extends AppCompatActivity {

    ArrayList<String> user_id, user_username, user_email, user_password, user_bestscore;
    MyDatabaseHelper myDB;
    TextView btn;
    EditText inputUsername, inputPassword;
    Button btnlogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btn = findViewById(R.id.textViewSignUp);
        btnlogin = findViewById(R.id.btnlogin);

        inputUsername = findViewById(R.id.inputUsername);
        inputPassword = findViewById(R.id.inputPassword);

        myDB = new MyDatabaseHelper(LoginActivity.this);
        user_id = new ArrayList<>();
        user_username = new ArrayList<>();
        user_email = new ArrayList<>();
        user_password = new ArrayList<>();
        user_bestscore = new ArrayList<>();

        getData();

        btn.setOnClickListener(view -> {
            startActivity(new Intent(this, RegisterActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            this.finish();
        });
        btnlogin.setOnClickListener(view -> {
            if (isEmpty(inputUsername)) inputUsername.setError("You must enter username to register!");
            else if(inputUsername.getText().toString().equals("admin")){
                startActivity(new Intent(this, ShowDatabase.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                this.finish();
            }
            else if (isEmpty(inputPassword)) inputPassword.setError("You must enter password to register!");
            else if (inputPassword.getText().toString().equals(user_password.get(user_username.indexOf(inputUsername.getText().toString())))){
                MainActivity.username = inputUsername.getText().toString();
                Toast.makeText(this, "All Right!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                this.finish();
            }
            else{
                Toast.makeText(this, "Input Error", Toast.LENGTH_SHORT).show();
            }

        });
    }

    void getData(){
        Cursor cursor = myDB.readAllData();
        if (cursor.getCount() == 0){
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        }
        else{
            while (cursor.moveToNext()){
                user_id.add(cursor.getString(0));
                user_username.add(cursor.getString(1));
                user_email.add(cursor.getString(2));
                user_password.add(cursor.getString(3));
                user_bestscore.add(cursor.getString(4));
            }
        }
    }

    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

}
