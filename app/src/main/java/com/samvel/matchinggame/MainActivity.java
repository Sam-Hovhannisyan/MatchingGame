package com.samvel.matchinggame;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.service.controls.actions.FloatAction;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Intent switchActivityIntent;
    MyDatabaseHelper myDB;

    // Timer

    private long startTimeInMillis;
    private TextView mTextViewCountDown;
    private CountDownTimer mCountDownTimer;
    private boolean isVisible;
    private long mTimeLeftInMillis;

    // Game params

    private int user_id;
    public static String username = "";
    int n = 12; // Game size
    int id, width, nBestScore, tick;
    int clicked = 0, lastClicked = -1, allChecked = 0, i = 0, nScore = 0, stepCount = 0;
    String sizes = "", savings = "", steps = "";
    String currentSize = "3x4";
    String times;
    GridLayout gridLayout, layoutTime;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switcher;
    LinearLayout timer;
    Button btn_3x4, btn_5x6, btn_4x5, startGame, playAgain, pauseResume, sec30, sec45, sec60;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    AlertDialog endDialog;
    TextView endText, score, bestScore;
    RecyclerView recyclerView;

    boolean isStartClicked = false; // Check if start button is clicked
    boolean isAlive = false; // Check if thread is alive
    boolean[] checkIsImageOpen = new boolean[n]; // Check if image is opened
    boolean isOnPause = true;

    ArrayList<String> user_usernames = new ArrayList<>();
    ArrayList<Integer> scores = new ArrayList<>();
    ArrayList<Boolean> isClickable = new ArrayList<>(); // Is button clickable or not clickable
    ArrayList<Boolean> isClickableTrack = new ArrayList<>();
    ArrayList<Integer> imageNumbers = new ArrayList<>();
    ArrayList<ImageView> imageButtons = new ArrayList<>();
    ArrayList<Integer> links = new ArrayList<>(Arrays.asList(
            R.drawable.camera,
            R.drawable.circles,
            R.drawable.flags,
            R.drawable.flash,
            R.drawable.microphone,
            R.drawable.trophy,
            R.drawable.calendar,
            R.drawable.train,
            R.drawable.ruler,
            R.drawable.flower,
            R.drawable.palette,
            R.drawable.skate,
            R.drawable.speaker,
            R.drawable.hourglass,
            R.drawable.radio
    ));

    @SuppressLint({"SetTextI18n", "NonConstantResourceId", "MissingInflatedId"})
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDB = new MyDatabaseHelper(MainActivity.this);

        BottomNavigationView bottomNavBar = findViewById(R.id.bottomNavigationView);

        bottomNavBar.getMenu().getItem(1).setOnMenuItemClickListener(item -> {
            SwitchActivities(1);
            bottomNavBar.getMenu().getItem(1).setChecked(true);
            return true;
        });
        bottomNavBar.getMenu().getItem(2).setOnMenuItemClickListener(item -> {
            SwitchActivities(2);
            bottomNavBar.getMenu().getItem(2).setChecked(true);
            return true;
        });


        // Getting highest score

        Cursor cursor = myDB.readAllData();

        if(cursor.getCount() > 0){
            while (cursor.moveToNext()){
                user_usernames.add(cursor.getString(1));
            }
        }

        user_id = user_usernames.indexOf(username);

        Toast.makeText(this, user_id + "", Toast.LENGTH_SHORT).show();

        SharedPreferences prefs = getSharedPreferences("High_Score", MODE_PRIVATE);
        nBestScore = prefs.getInt("best-score-" + user_id, 0);

        SharedPreferences getPrefs = getSharedPreferences("Prefs", MODE_PRIVATE);
        savings = getPrefs.getString("scores_", "");
        sizes = getPrefs.getString("size_", "");
        steps = getPrefs.getString("step_", "");
        times = getPrefs.getString("time_", "");

        // Display sizes

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;

        mTextViewCountDown = findViewById(R.id.text_view_countdown);
        timer = findViewById(R.id.timer);

        gridLayout = findViewById(R.id.gridLayout);

        int btn_color = Color.rgb(38, 70, 83);

        // Alert Layout

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View theEnd = getLayoutInflater().inflate(R.layout.the_end, null);

        switcher = findViewById(R.id.switcher);
        switcher.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                layoutTime.setVisibility(View.VISIBLE);
                isVisible = true;
            } else {
                layoutTime.setVisibility(View.INVISIBLE);
                isVisible = false;
            }
        });

        startGame = findViewById(R.id.startGame);
        startGame.setBackgroundColor(btn_color);
        startGame.setTextColor(Color.WHITE);

        pauseResume = findViewById(R.id.pauseAndResume);
        pauseResume.setBackgroundColor(btn_color);
        pauseResume.setTextColor(Color.WHITE);

        playAgain = theEnd.findViewById(R.id.playAgain);
        endText = theEnd.findViewById(R.id.endText);
        score = theEnd.findViewById(R.id.score);
        bestScore = theEnd.findViewById(R.id.bestScore);

        layoutTime = findViewById(R.id.layoutTime);
        startGame.setBackgroundColor(btn_color);
        playAgain.setBackgroundColor(btn_color);
        playAgain.setTextColor(Color.WHITE);

        btn_3x4 = findViewById(R.id.btn_3x4);
        btn_4x5 = findViewById(R.id.btn_4x5);
        btn_5x6 = findViewById(R.id.btn_5x6);
        sec30 = findViewById(R.id.sec30);
        sec45 = findViewById(R.id.sec45);
        sec60 = findViewById(R.id.sec60);

        btn_3x4.setTextColor(Color.WHITE);
        btn_4x5.setTextColor(Color.WHITE);
        btn_5x6.setTextColor(Color.WHITE);
        sec30.setTextColor(Color.WHITE);
        sec45.setTextColor(Color.WHITE);
        sec60.setTextColor(Color.WHITE);

        btn_3x4.setBackgroundColor(btn_color);
        btn_4x5.setBackgroundColor(btn_color);
        btn_5x6.setBackgroundColor(btn_color);
        sec30.setBackgroundColor(btn_color);
        sec45.setBackgroundColor(btn_color);
        sec60.setBackgroundColor(btn_color);


        // End dialog

        builder.setView(theEnd);
        endDialog = builder.create();
        endDialog.setCancelable(false);

        btn_3x4.setOnClickListener(v -> {
            n = 12;
            //startDialog.cancel();
            startGame.setVisibility(View.VISIBLE);
            currentSize = "3x4";
        });

        btn_4x5.setOnClickListener(v -> {
            n = 20;
            startGame.setVisibility(View.VISIBLE);
            currentSize = "4x5";
        });

        btn_5x6.setOnClickListener(v -> {
            n = 30;
            startGame.setVisibility(View.VISIBLE);
            currentSize = "3x4";
        });

        sec30.setOnClickListener(v -> i = 0);
        sec45.setOnClickListener(v -> i = 1);
        sec60.setOnClickListener(v -> i = 2);


        startGame.setOnClickListener(v -> {
            isStartClicked = true;
            imageNumbers = new ArrayList<>();
            ResetAll();
            Generate();
            isClickable = new ArrayList<>(Arrays.asList(new Boolean[n]));
            isClickableTrack = new ArrayList<>(Arrays.asList(new Boolean[n]));
            Collections.fill(isClickable, Boolean.TRUE);
            startGame.setVisibility(View.INVISIBLE);
            timer.setVisibility(View.INVISIBLE);
            if (isVisible) {
                layoutTime.setVisibility(View.INVISIBLE);
                pauseResume.setVisibility(View.VISIBLE);
                mTextViewCountDown.setVisibility(View.VISIBLE);
                pauseResume.setText("Pause");
                SetTimer(i);
                StartTimer();
            }
        });

        pauseResume.setOnClickListener(v -> {
            if(isOnPause){
                PauseTimer();
                pauseResume.setText("Resume");
                isStartClicked = false;
                isOnPause = false;
            }
            else{
                StartTimer();
                pauseResume.setText("Pause");
                isStartClicked = true;
                isOnPause = true;
            }
        });

        playAgain.setOnClickListener(v -> {
            endDialog.cancel();
            //startDialog.show();
            ResetAll();
            ResetTimer();
            isStartClicked = false;
            gridLayout.removeAllViews();
            startGame.setVisibility(View.VISIBLE);
            pauseResume.setVisibility(View.INVISIBLE);
            timer.setVisibility(View.VISIBLE);
            if (isVisible) layoutTime.setVisibility(View.VISIBLE);
            mTextViewCountDown.setVisibility(View.INVISIBLE);
        });
    }

    private void SwitchActivities(int i) {
        if (i == 1) {
            switchActivityIntent = new Intent(this, ReviewsActivity.class);
        }
        else if(i == 2) {
            switchActivityIntent = new Intent(this, ScoresActivity.class);
        }
        startActivity(switchActivityIntent);
        this.finish();
    }

    @Override
    public void finish() {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    // ValueAnimator, shuffle images if 3 false attempts
    @SuppressLint({"NonConstantResourceId", "UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    public void onClick(View view) {
        if (!isStartClicked) return;
        if (isAlive) return;

        id = view.getId(); // Get current image id

        if (!checkIsImageOpen[id] && isClickable.get(id)) { // if("image is not opened" and "is image clickable")
            SetImage(id); // Change image using id
            checkIsImageOpen[id] = true;
            if (clicked == 0) {
                lastClicked = id;
            }
            clicked++;
            stepCount++;
        }
        if (clicked == 2) {
            if (Objects.equals(imageNumbers.get(id), imageNumbers.get(lastClicked)) && isClickable.get(id) && isClickable.get(lastClicked)) {
                isClickable.set(id, false);
                isClickable.set(lastClicked, false);
                allChecked += 2;
                imageButtons.get(id).startAnimation(AnimationUtils.loadAnimation(
                        getApplicationContext(), R.anim.zoom_out
                ));
                imageButtons.get(lastClicked).startAnimation(AnimationUtils.loadAnimation(
                        getApplicationContext(), R.anim.zoom_out
                ));
            } else {
                ImageThread imageThread = new ImageThread();
                ImageView[] imageViews = new ImageView[n];
                for (int i = 0; i < n; i++) {
                    imageViews[i] = imageButtons.get(i);
                }
                imageThread.SendImage(imageViews);
                for (int i = 0; i < n; i++) isClickableTrack.set(i, isClickable.get(i));
                Collections.fill(isClickable, false);
                imageThread.start();
                isAlive = true;
            }
            clicked = 0;
        }
        if (allChecked == n) {
            int k = 0;
            if (!isVisible) k = 100;
            nScore = 200 * (i + 1) - (int) (startTimeInMillis / 400000) - tick - stepCount - k;
            Toast.makeText(this, "" + nScore, Toast.LENGTH_SHORT).show();
            if (nScore > nBestScore) nBestScore = nScore;
            scores.add(nScore);
            endText.setText("You Win!");
            bestScore.setText("Best score:" + nBestScore);
            score.setText("Your score:" + nScore);
            endDialog.show();
            if (isVisible) PauseTimer();
            SaveScore();
        }
    }

    private void SaveScore() {
        if (nScore < 0) nScore = 0;
        savings += nScore + "-";
        sizes += currentSize + "-";
        steps += stepCount + "-";
        times += tick + "-";
        SharedPreferences.Editor savePrefs = getSharedPreferences("Prefs", MODE_PRIVATE).edit();
        savePrefs.putString("scores_", savings);
        savePrefs.putString("size_", sizes);
        savePrefs.putString("step_", steps);
        savePrefs.putString("time_", times);
        savePrefs.apply();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Saving highest score
        SharedPreferences.Editor editor = getSharedPreferences("High_Score", MODE_PRIVATE).edit();
        editor.putInt("best-score", nBestScore);
        editor.apply();
    }

    static void Randomize(ArrayList<Integer> arr) {
        // Creating a object for Random class
        Random r = new Random();

        // Start from the last element and swap one by one. We don't
        // need to run for the first element that's why i >

        for (int i = arr.toArray().length - 1; i > 0; i--) {

            // Pick a random index from 0 to i
            int j = r.nextInt(i + 1);

            // Swap arr[i] with the element at random index
            int temp = arr.get(i);
            arr.set(i, arr.get(j));
            arr.set(j, temp);
        }
    }

    void SetImage(int i) {
        ImageView currentImage = imageButtons.get(i);
        int currentImageId = imageNumbers.get(i);
        currentImage.setImageResource(links.get(currentImageId));
    }

    void ResetAll() {
        tick = 0;
        nScore = 0;
        checkIsImageOpen = new boolean[n];
        mTextViewCountDown.setText(String.format(Locale.getDefault(), "%02d:%02d", 0, 0));
        Randomize(imageNumbers);
        for (int i = 0; i < imageButtons.toArray().length; i++) {
            ImageView cur = imageButtons.get(i);
            cur.setImageResource(R.drawable.code);
        }
        Collections.fill(isClickable, Boolean.TRUE);
        clicked = 0;
        lastClicked = -1;
        allChecked = 0;
    }

    private void Generate() {
        for (int i = 0; i < n / 2; i++) {
            imageNumbers.add(i);
        }
        for (int i = 0; i < n / 2; i++) {
            imageNumbers.add(i);
        }
        Randomize(imageNumbers);

        imageButtons = new ArrayList<>(n);
        gridLayout.removeAllViews();

        int column = (int) Math.round(Math.sqrt(n));
        int row = n / column;
        gridLayout.setColumnCount(column);
        gridLayout.setRowCount(row);

        for (int i = 0, c = 0, r = 0; i < n; i++, c++) {
            if (c == column) {
                c = 0;
                r++;
            }
            ImageView oImageView = new ImageView(this);
            oImageView.setId(i);
            oImageView.setImageResource(R.drawable.code);
            oImageView.setOnClickListener(this);
            GridLayout.LayoutParams param = new GridLayout.LayoutParams();
            param.height = width / column - 40;
            param.width = width / column - 40;
            param.rightMargin = 20;
            param.topMargin = 20;
            param.bottomMargin = 20;
            param.leftMargin = 20;
            param.setGravity(Gravity.CENTER);
            param.columnSpec = GridLayout.spec(c);
            param.rowSpec = GridLayout.spec(r);
            oImageView.setLayoutParams(param);
            gridLayout.addView(oImageView);
            imageButtons.add(i, oImageView);
            //oImageView.startAnimation(rotation);
        }

    }

    private void StartTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
                tick++;
            }
            @SuppressLint("SetTextI18n")
            @Override
            public void onFinish() {
                endDialog.show();
                if (allChecked == n) {
                    score.setText("Your score: " + nScore);
                    endText.setText("You Win!");
                } else {
                    score.setText("Your score: 0");
                    bestScore.setText("Best score: " + nBestScore);
                    endText.setText("You Lose");
                }
            }
        }.start();
    }

    private void PauseTimer() {
        mCountDownTimer.cancel();
    }

    private void ResetTimer() {
        mTimeLeftInMillis = startTimeInMillis;
        updateCountDownText();
    }

    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        mTextViewCountDown.setText(timeLeftFormatted);
    }

    private void SetTimer(int i) {
        if(i == -1) startTimeInMillis = 5000;
        else if (i == 0) startTimeInMillis = 30000;
        else if (i == 1) startTimeInMillis = 45000;
        else if (i == 2) startTimeInMillis = 60000;

        mTimeLeftInMillis = startTimeInMillis;
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        mTextViewCountDown.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
    }

    class ImageThread extends Thread    {

        ImageView lastImage;
        ImageView currentImage;

        @Override
        public void run() {
            try {
                Thread.sleep(350);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            currentImage.setImageResource(R.drawable.code);
            lastImage.setImageResource(R.drawable.code);
            for (int i = 0; i < n; i++) isClickable.set(i, isClickableTrack.get(i));
            checkIsImageOpen[id] = false;
            checkIsImageOpen[lastClicked] = false;
            isAlive = false;
        }

        public void SendImage(ImageView[] imageViews) {
            lastImage = imageViews[lastClicked];
            currentImage = imageViews[id];
        }
    }

}