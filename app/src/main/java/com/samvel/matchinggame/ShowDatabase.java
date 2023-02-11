package com.samvel.matchinggame;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.util.ArrayList;

public class ShowDatabase extends AppCompatActivity {

    ArrayList<String> user_id, user_username, user_email, user_password, user_bestscore;
    MyDatabaseHelper myDB;
    CustomAdapter customAdapter;
    RecyclerView recyclerView;
    Button goBack;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        recyclerView = findViewById(R.id.recyclerView);
        goBack = findViewById(R.id.goBack);

        goBack.setOnClickListener(view -> {
            startActivity(new Intent(this, RegisterActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            this.finish();
        });

        myDB = new MyDatabaseHelper(ShowDatabase.this);
        user_id = new ArrayList<>();
        user_username = new ArrayList<>();
        user_email = new ArrayList<>();
        user_password = new ArrayList<>();
        user_bestscore = new ArrayList<>();

        storeDataInArrays();

        customAdapter = new CustomAdapter(ShowDatabase.this, user_id, user_username, user_email, user_password, user_bestscore);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(ShowDatabase.this));
    }

    void storeDataInArrays (){
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
}
