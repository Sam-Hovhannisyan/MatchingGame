package com.samvel.matchinggame;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

public class ScoresActivity extends AppCompatActivity {

    Intent switchActivityIntent;
    Button logOut;
    TextView available;
    TableView tableView;
    int i = 1;

    @SuppressLint({"MissingInflatedId", "ResourceAsColor"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        tableView = findViewById(R.id.tableView);
        logOut = findViewById(R.id.logout);
        available = findViewById(R.id.available);
        if (i == 0) {
            available.setVisibility(View.VISIBLE);
        }

        String[] headers = {"ID", "Name", "Surname", "Age"};
        String[][] data = {{"1", "Joe", "Dona", "65"}, {"2", "Mark", "Dawn", "21"}};

        tableView.setHeaderAdapter(new SimpleTableHeaderAdapter(this, headers));
        tableView.setDataAdapter(new SimpleTableDataAdapter(this, data));

        logOut.setOnClickListener(view -> {
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
