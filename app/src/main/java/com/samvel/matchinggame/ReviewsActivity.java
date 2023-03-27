package com.samvel.matchinggame;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import de.codecrafters.tableview.TableView;

public class ReviewsActivity extends AppCompatActivity {

    Intent switchActivityIntent;
    private DatabaseReference rootDatabaseRef;
    private String current = "";
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String userName;

    TextView reviewsNotFound;
    TableLayout tableLayout;
    String[] headers = {"Size", "Score", "Steps", "Time"};
    // array objects
    String[] reviewList;
    ArrayList<String> scoreList, sizeList, stepList, timeList;
    String sizes = "", scores = "", steps = "", times = "";

    @SuppressLint({"SetTextI18n", "MissingInflatedId"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        reviewsNotFound = findViewById(R.id.reviewsNotFound);
        rootDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users");

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        tableLayout = findViewById(R.id.tableLayout);

        scoreList = new ArrayList<>();
        sizeList = new ArrayList<>();
        stepList = new ArrayList<>();
        timeList = new ArrayList<>();

        try {
            userName = mUser.getDisplayName();
        } catch (Exception e) {
            userName = "-1";
        }

        if (!userName.equals("-1")) {
            getFirebaseData();
            Log.e("success", "yeah");
        } else {
            reviewsNotFound.setText("Is not available");
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

    private void getFirebaseData() {
        rootDatabaseRef.child(userName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Log.e("info", dataSnapshot.getKey());
                    String value = dataSnapshot.getValue().toString();
                    if (Objects.equals(dataSnapshot.getKey(), "score")) scores = value;
                    else if (Objects.equals(dataSnapshot.getKey(), "size")) sizes = value;
                    else if (Objects.equals(dataSnapshot.getKey(), "step")) steps = value;
                    else if (Objects.equals(dataSnapshot.getKey(), "time")) times = value;

                    if (value.equals(scores)) {
                        for (int i = 0; i < scores.length(); i++) {
                            char c = scores.charAt(i);
                            if (c == '-') {
                                scoreList.add(current);
                                current = "";
                            } else current += c;
                        }
                    } else if (value.equals(sizes)) {
                        for (int i = 0; i < sizes.length(); i++) {
                            char c = sizes.charAt(i);
                            if (c == '-') {
                                sizeList.add(current);
                                current = "";
                            } else current += c;
                        }
                    } else if (value.equals(steps)) {
                        for (int i = 0; i < steps.length(); i++) {
                            char c = steps.charAt(i);
                            if (c == '-') {
                                stepList.add(current);
                                current = "";
                            } else current += c;
                        }
                    } else if (value.equals(times)) {
                        for (int i = 0; i < times.length(); i++) {
                            char c = times.charAt(i);
                            if (c == '-') {
                                timeList.add(current);
                                current = "";
                            } else current += c;
                        }
                    }
                }
                if (scoreList.size() == 0) {
                    reviewsNotFound.setVisibility(View.VISIBLE);
                } else {

                    showData();
                    reviewsNotFound.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showData() {
        int numRows = scoreList.size() + 1;
        int numCols = 4;
        for (int i = 0; i < numRows; i++) {
            TableRow row = new TableRow(this);
            for (int j = 0; j < numCols; j++) {
                TextView cell = new TextView(this);
                cell.setTextColor(Color.WHITE);
                cell.setTextSize(20);
                cell.setPadding(30, 20, 30, 20); // Set cell padding
                if (i == 0) {
                    cell.setBackgroundColor(Color.argb(100, 240, 234, 214));
                    cell.setText(headers[j]);
                } else {
                    try {
                        if (j == 0) cell.setText(sizeList.get(numRows - i - 1));
                        else if (j == 1) cell.setText(scoreList.get(numRows - i - 1));
                        else if (j == 2) cell.setText(stepList.get(numRows - i - 1));
                        else cell.setText(timeList.get(numRows - i - 1));
                    }
                    catch (Exception e){
                        Log.e("shat havayi", "ban");
                    }

                }
                row.addView(cell);
            }
            tableLayout.addView(row);
            if (i == 10) break;
        }
    }

    private void switchActivities(int i) {
        if (i == 0) {
            switchActivityIntent = new Intent(this, MainActivity.class);
        } else if (i == 2) {
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

    @Override
    public void onBackPressed() {
        //moveTaskToBack(false);
    }
}
