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

    TextView playButton, howToPlay, offlineButton,signUp, logIn;
    MediaPlayer mediaPlayer;
    private FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mediaPlayer = MediaPlayer.create(this, R.raw.music);
        mediaPlayer.setLooping(true);
        //mediaPlayer.start();


        mAuth = FirebaseAuth.getInstance();

        playButton = findViewById(R.id.playButton);
        howToPlay = findViewById(R.id.howToPlay);
        offlineButton = findViewById(R.id.playOfflineButton);

        signUp = findViewById(R.id.googleSignUp);
        logIn = findViewById(R.id.googleLogIn);

        playButton.setOnClickListener(view -> {
            Methods.clickSound(this);
            playButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
            if (mAuth.getCurrentUser() != null){
                changeActivity(MainActivity.class);
            }
            else{
                new AlertDialog.Builder(this).setMessage("If you want to play online you have to log in").setPositiveButton("Log in", (dialogInterface, i) -> {
                    changeActivity(LoginActivity.class);
                }).setNegativeButton("Cancel", null).show();
            }
        });

        howToPlay.setOnClickListener(view -> {
            Methods.clickSound(this);
            howToPlay.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
            changeActivity(RulesActivity.class);
        });

        offlineButton.setOnClickListener(view -> {
            Methods.clickSound(this);
            offlineButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
            MainActivity.userName = "-1";
            try {
                mAuth.signOut();
            } catch (Exception e) {
                Log.e("Sign out", "not signed in account");
            }
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
