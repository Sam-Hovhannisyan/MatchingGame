package com.samvel.matchinggame;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

public class ReviewsActivity extends AppCompatActivity {

    Intent switchActivityIntent;
    private DatabaseReference rootDatabaseRef;
    private String current = "";
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String userName;

    TextView reviewsNotFound;
    ListView simpleListView;
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

        scoreList = new ArrayList<>();
        sizeList = new ArrayList<>();
        stepList = new ArrayList<>();
        timeList = new ArrayList<>();

        try {
            userName = mUser.getDisplayName();
        } catch (Exception e) {
            userName = "-1";
        }

        simpleListView = findViewById(R.id.simpleListView);
        simpleListView.setDivider(null);
        simpleListView.setDividerHeight(0);
        simpleListView.setEnabled(false);

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
                }else{

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
        if (scoreList.size() > 10) reviewList = new String[10];
        else reviewList = new String[scoreList.size()];
        try {
            for (int i = 0; i < reviewList.length; i++) {
                int id = scoreList.size() - i - 1;
                reviewList[i] = "Score:" + scoreList.get(id)
                        + ", Table size:" + sizeList.get(id)
                        + ",Steps:" + stepList.get(id)
                        + ",Time:" + timeList.get(id);
                //Log.e("step list - ", reviewList[i]);
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                    R.layout.item_view, R.id.itemTextView, reviewList);
            simpleListView.setAdapter(arrayAdapter);
        } catch (Exception e) {
            Log.e("hav", "ban");
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
        moveTaskToBack(false);
    }
}
