package com.samvel.matchinggame;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ScoresActivity extends AppCompatActivity {

    private DatabaseReference rootDatabaseRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String userName;
    ImageView logOut, backToMenu;
    TextView available;
    TableLayout tableLayout;
    String[] headers = {"Username", "Games", "Best Score"};
    HashMap<String, Integer> dict;
    ArrayList<Integer> user_bestScores = new ArrayList<>();
    ArrayList<Integer> user_games = new ArrayList<>();
    ArrayList<String> user_usernames = new ArrayList<>();

    @SuppressLint({"MissingInflatedId", "ResourceAsColor", "Range"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        rootDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        dict = new HashMap<>();

        try {
            userName = mUser.getDisplayName();
        } catch (Exception e) {
            userName = "-1";
        }

        tableLayout = findViewById(R.id.tableLayout);
        logOut = findViewById(R.id.logout);
        backToMenu = findViewById(R.id.backToMenu);
        available = findViewById(R.id.available);

        if(isNetworkConnected()) getFirebaseData();
        else available.setVisibility(View.VISIBLE);

        backToMenu.setOnClickListener(view -> {
            Methods.clickSound(this);
            backToMenu.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
            changeActivity(MenuActivity.class);
        });

        logOut.setOnClickListener(view -> {
            mAuth.signOut();
            Methods.clickSound(this);
            logOut.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
            changeActivity(MenuActivity.class);
        });

        BottomNavigationView bottomNavBar = findViewById(R.id.bottomNavigationView);
        bottomNavBar.getMenu().getItem(2).setChecked(true);
        bottomNavBar.getMenu().getItem(1).setOnMenuItemClickListener(item -> {
            changeActivity(ReviewsActivity.class);
            bottomNavBar.getMenu().getItem(1).setChecked(true);
            return true;
        });
        bottomNavBar.getMenu().getItem(0).setOnMenuItemClickListener(item -> {
            changeActivity(MainActivity.class);
            bottomNavBar.getMenu().getItem(0).setChecked(true);
            return true;
        });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private void getFirebaseData() {
        rootDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    user_usernames.add(dataSnapshot.getKey());
                    user_games.add(Integer.valueOf(dataSnapshot.child("games").getValue().toString()));
                    user_bestScores.add(Integer.valueOf(dataSnapshot.child("bestScore").getValue().toString()));
                }
                for (int j = 0; j < user_usernames.size(); j++) {
                    dict.put(user_usernames.get(j), user_bestScores.get(j));
                }
                dict = sortDictionary(dict);
                Log.e("this", dict + "");
                showData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private HashMap<String, Integer> sortDictionary(HashMap<String, Integer> map) {
        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        ArrayList<Integer> list = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            list.add(entry.getValue());
        }
        Collections.sort(list);
        for (int num : list) {
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                if (entry.getValue().equals(num)) {
                    sortedMap.put(entry.getKey(), num);
                }
            }
        }

        return sortedMap;
    }

    private void changeActivity(Class class_) {
        startActivity(new Intent(this, class_));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        this.finish();
    }


    private void showData() {
        int size = user_usernames.size();
        ArrayList<String> uNames = new ArrayList<>();
        ArrayList<Integer> uScores = new ArrayList<>();
        if (size > 0) {
            for (Map.Entry<String, Integer> entry : dict.entrySet()) {
                String key = entry.getKey();
                Integer val = entry.getValue();
                uNames.add(key);
                uScores.add(val);
            }
        }

        Log.e("Test 170", String.valueOf(uScores));
        // Define the number of rows and columns
        int numRows = size + 1;
        int numCols = 3;

        // Create the rows and cells dynamically
        for (int i = 0; i < numRows; i++) {
            TableRow row = new TableRow(this);
            for (int j = 0; j < numCols; j++) {
                TextView cell = new TextView(this);
                cell.setTextColor(Color.WHITE);
                cell.setTextSize(20);
                cell.setPadding(20, 20, 20, 20); // Set cell padding
                if (i == 0) {
                    cell.setBackgroundColor(Color.argb(100, 240,234,214));
                    cell.setText(headers[j]);
                } else {
                    try {
                        if (j == 0) cell.setText(uNames.get(numRows - i - 1));
                        else if (j == 1)
                            cell.setText(user_games.get(user_usernames.indexOf(uNames.get(numRows - i - 1))).toString());
                        else if (j == 2) cell.setText(uScores.get(numRows - i - 1).toString());
                    }catch(Exception e){
                        Log.e("Havayi","ban");
                    }

                }
                row.addView(cell);
            }
            tableLayout.addView(row);
        }
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
