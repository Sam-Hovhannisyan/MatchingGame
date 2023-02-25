package com.samvel.matchinggame;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;

public class ReviewsActivity extends AppCompatActivity {

    Intent switchActivityIntent;
    MyDatabaseHelper myDB;
    private String current = "";
    private int user_id;

    TextView reviewsNotFound;
    ListView simpleListView;
    // array objects
    String[] reviewList;
    ArrayList<String > scoreList = new ArrayList<>();
    ArrayList<String> sizeList = new ArrayList<>();
    ArrayList<String> stepList = new ArrayList<>();
    ArrayList<String> timeList = new ArrayList<>();
    ArrayList<String> user_score, user_size, user_step, user_time;
    String sizes = "", savings = "", steps = "", times = "";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        reviewsNotFound = findViewById(R.id.reviewsNotFound);
        myDB = new MyDatabaseHelper(ReviewsActivity.this);

        user_score = new ArrayList<>();
        user_step = new ArrayList<>();
        user_size = new ArrayList<>();
        user_time = new ArrayList<>();

        user_id = MainActivity.user_id;

        if (user_id == -1){
            reviewsNotFound.setText("Is not available");
        }
        else {
            getData();

            for (int i = 0; i < savings.length(); i++) {
                char c = savings.charAt(i);
                if (c == '-') {
                    scoreList.add(current);
                    current = "";
                } else current += c;
            }
            for (int i = 0; i < sizes.length(); i++) {
                char c = sizes.charAt(i);
                if (c == '-') {
                    sizeList.add(current);
                    current = "";
                } else current += c;
            }
            for (int i = 0; i < steps.length(); i++) {
                char c = steps.charAt(i);
                if (c == '-') {
                    stepList.add(current);
                    current = "";
                } else current += c;
            }
            for (int i = 0; i < times.length(); i++) {
                char c = times.charAt(i);
                if (c == '-') {
                    timeList.add(current);
                    current = "";
                } else current += c;
            }

            if (scoreList.isEmpty()) reviewsNotFound.setText("Reviews not found");
            else reviewsNotFound.setVisibility(View.INVISIBLE);
            simpleListView = findViewById(R.id.simpleListView);
            simpleListView.setDivider(null);
            simpleListView.setDividerHeight(0);
            simpleListView.setEnabled(false);

            if (scoreList.size() > 10) reviewList = new String[10];
            else reviewList = new String[scoreList.size()];

            for (int i = 0; i < reviewList.length; i++) {
                int id = scoreList.size() - i - 1;
                reviewList[i] = "Score:" + scoreList.get(id)
                        + ", Table size:" + sizeList.get(id)
                        + ", Steps:" + stepList.get(id)
                        + ", Time:" + timeList.get(id);
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                    R.layout.item_view, R.id.itemTextView, reviewList);
            simpleListView.setAdapter(arrayAdapter);
        }

        // Navigation bar

        BottomNavigationView bottomNavBar = findViewById(R.id.bottomNavigationView);

        bottomNavBar.getMenu().getItem(1).setChecked(true);
        bottomNavBar.getMenu().getItem(0).setOnMenuItemClickListener(item -> {
            switchActivities(0);
            bottomNavBar.getMenu().getItem(0).setChecked(true);
            return true;
        });
        bottomNavBar.getMenu().getItem(2).setOnMenuItemClickListener(item -> {
            switchActivities(2);
            bottomNavBar.getMenu().getItem(2).setChecked(true);
            return true;
        });
    }

    private void getData() {
        Cursor cursor = myDB.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                user_score.add(cursor.getString(5));
                user_size.add(cursor.getString(6));
                user_step.add(cursor.getString(7));
                user_time.add(cursor.getString(8));
            }
        }

        setData();

        Toast.makeText(this, "" + user_id, Toast.LENGTH_SHORT).show();
    }

    private void setData() {
        savings = user_score.get(user_id);
        sizes = user_size.get(user_id);
        steps = user_step.get(user_id);
        times = user_time.get(user_id);
    }


    private void switchActivities(int i) {
        if (i == 0) {
            switchActivityIntent = new Intent(this, MainActivity.class);
        }
        else if(i == 2) {
            switchActivityIntent = new Intent(this, ScoresActivity.class);
        }
        startActivity(switchActivityIntent);
        this.finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
