package com.samvel.matchinggame;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import java.util.stream.IntStream;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

public class ScoresActivity extends AppCompatActivity {

    Intent switchActivityIntent;
    private DatabaseReference rootDatabaseRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String userName;
    Button logOut;
    TextView available;
    TableView tableView;
    String[] headers = {"Username", "Games", "Best Score"};
    String[][] data;
    static int i = 1;
    HashMap<String, Integer> dict;
    ArrayList<Integer> user_bestScores = new ArrayList<>();
    ArrayList<Integer> user_games = new ArrayList<>();
    ArrayList<String> user_usernames = new ArrayList<>();

    @SuppressLint({"MissingInflatedId", "ResourceAsColor", "Range"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        rootDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        dict = new HashMap<>();

        try {
            userName = mUser.getDisplayName();
        } catch (Exception e) {
            userName = "-1";
        }

        tableView = findViewById(R.id.tableView);
        logOut = findViewById(R.id.logout);
        available = findViewById(R.id.available);
        if (i == 0) {
            available.setVisibility(View.VISIBLE);
            tableView.setHeaderVisible(false);
        } else {
            getFirebaseData();
        }
        logOut.setOnClickListener(view -> {
            mAuth.signOut();
            startActivity(new Intent(this, RegisterActivity.class));
            this.finish();
        });

        BottomNavigationView bottomNavBar = findViewById(R.id.bottomNavigationView);
        bottomNavBar.getMenu().getItem(2).setChecked(true);
        bottomNavBar.getMenu().getItem(1).setOnMenuItemClickListener(item -> {
            switchActivities(1);
            bottomNavBar.getMenu().getItem(1).setChecked(true);
            return true;
        });
        bottomNavBar.getMenu().getItem(0).setOnMenuItemClickListener(item -> {
            switchActivities(0);
            bottomNavBar.getMenu().getItem(0).setChecked(true);
            return true;
        });
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

    private void showData() {
        int size = user_usernames.size();
        data = new String[size][3];
        ArrayList<String> uNames = new ArrayList<>();
        ArrayList<Integer> uScores = new ArrayList<>();
        if (size > 0) {
            for (Map.Entry<String, Integer> entry : dict.entrySet()) {
                String key = entry.getKey();
                Integer val = entry.getValue();
                uNames.add(key);
                uScores.add(val);
            }
            try {
                for (int i = 0; i < size; i++) {
                    data[size - i - 1][0] = uNames.get(i);
                    data[size - i - 1][1] = user_games.get(user_usernames.indexOf(uNames.get(i))).toString();
                    data[size - i - 1][2] = uScores.get(i).toString();
                }
            } catch (Exception e) {
                Log.e("hech", "bana");
            }
        }
        tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(this, headers));
        tableView.setDataAdapter(new SimpleTableDataAdapter(this, data));
    }

    private void switchActivities(int i) {
        if (i == 1) {
            switchActivityIntent = new Intent(this, ReviewsActivity.class);
        } else if (i == 0) {
            switchActivityIntent = new Intent(this, MainActivity.class);
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
