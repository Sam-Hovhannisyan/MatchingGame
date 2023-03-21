package com.samvel.matchinggame;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MenuActivity extends AppCompatActivity {

    TextView playButton, settingsButton, offlineButton;
    private FirebaseAuth mAuth;
    GridLayout signUp, logIn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mAuth = FirebaseAuth.getInstance();

        try {
            mAuth.signOut();
        } catch (Exception e) {
            Log.e("Sign out", "not signed in account");
        }

        playButton = findViewById(R.id.playButton);
        settingsButton = findViewById(R.id.settingsButton);
        offlineButton = findViewById(R.id.playOfflineButton);

        signUp = findViewById(R.id.googleSignUp);
        logIn = findViewById(R.id.googleLogIn);

        playButton.setOnClickListener(view -> {
            Methods.clickSound(this);
            playButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
            new AlertDialog.Builder(this).setMessage("If you want to play online you have to log in").setPositiveButton("Log in", (dialogInterface, i) -> {
                changeActivity(LoginActivity.class);
            }).setNegativeButton("Cancel", null).show();
        });

        settingsButton.setOnClickListener(view -> {
            Methods.clickSound(this);
            settingsButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
        });

        offlineButton.setOnClickListener(view -> {
            Methods.clickSound(this);
            offlineButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
            ScoresActivity.i = 0;
            MainActivity.userName = "-1";
            changeActivity(MainActivity.class);
        });

        signUp.setOnClickListener(view -> {
            Methods.clickSound(this);
            signUp.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
            changeActivity(RegisterActivity.class);
        });

        logIn.setOnClickListener(view -> {
            Methods.clickSound(this);
            logIn.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
            changeActivity(LoginActivity.class);
        });
    }

    private void changeActivity(Class class_) {
        startActivity(new Intent(this, class_));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        this.finish();
    }


}
