package com.samvel.matchinggame;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference rootDatabaseRef;

    // Timer

    private long startTimeInMillis;
    private TextView mTextViewCountDown;
    private CountDownTimer mCountDownTimer;
    private boolean isVisible = false;
    private long mTimeLeftInMillis;

    // Game params

    public static String userName = "-1";

    int n = 12; // Game size
    int id, width, height, nBestScore, tick, playedGames;
    int clicked = 0, lastClicked = -1, allChecked = 0, i = 0, nScore = 0, stepCount = 0;
    String sizes, scores, steps, times, p1Text, p2Text;
    String currentSize = "3x4";
    GridLayout gridLayout;
    LinearLayout timer;
    ImageView singleplayer, multiplayer, settings;
    Button btn_3x4, btn_5x6, btn_4x5, playAgain, pauseResume, setTimer, cancelTimer, playTimer, playGame, playMul, sec30, sec45, sec60;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    AlertDialog endDialog, gameDialog, timeDialog, twoPlayerDialog;
    TextView endText, score, logo;
    EditText p1Edit, p2Edit;
    LinearLayout l1, l2;

    boolean isAlive = false; // Check if thread is alive
    boolean[] checkIsImageOpen = new boolean[n]; // Check if image is opened
    boolean isOnPause = true;

    ArrayList<Integer> scoreList = new ArrayList<>();
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        rootDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users");

        BottomNavigationView bottomNavBar = findViewById(R.id.bottomNavigationView);

        bottomNavBar.getMenu().getItem(1).setOnMenuItemClickListener(item -> {
            changeActivity(ReviewsActivity.class);
            bottomNavBar.getMenu().getItem(1).setChecked(true);
            return true;
        });
        bottomNavBar.getMenu().getItem(2).setOnMenuItemClickListener(item -> {
            changeActivity(ScoresActivity.class);
            bottomNavBar.getMenu().getItem(2).setChecked(true);
            return true;
        });

        //StyleableToast.makeText(MainActivity.this, "Well done!", Toast.LENGTH_LONG, R.style.mytoast).show();

        // Getting highest score

        // Display sizes

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;

        mTextViewCountDown = findViewById(R.id.text_view_countdown);
        timer = findViewById(R.id.timer);
        logo = findViewById(R.id.logo);

        gridLayout = findViewById(R.id.gridLayout);


        int action_btn_color = Color.rgb(226, 209, 166);
        int action_btn_color_light = Color.rgb(255,236,189);
        int btn_color = Color.rgb(148,192,192);
        int btn_color_pressed = Color.rgb(209,239,239);

        // Alert Layout

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View theEnd = getLayoutInflater().inflate(R.layout.the_end, null);
        View gameD = getLayoutInflater().inflate(R.layout.game_alert, null);
        View timerAlert = getLayoutInflater().inflate(R.layout.timer_alert, null);
        View mulAlert = getLayoutInflater().inflate(R.layout.two_player_alert, null);


        singleplayer = findViewById(R.id.startGame);
        multiplayer = findViewById(R.id.multiplayer);
        settings = findViewById(R.id.settings);

        pauseResume = findViewById(R.id.pauseAndResume);
        pauseResume.setBackgroundColor(action_btn_color_light);
        pauseResume.setTextColor(Color.BLACK);

        // End dialog settings

        playAgain = theEnd.findViewById(R.id.playAgain);
        endText = theEnd.findViewById(R.id.endText);
        score = theEnd.findViewById(R.id.score);

        // Game dialog settings

        btn_3x4 = gameD.findViewById(R.id.btn_3x4);
        btn_4x5 = gameD.findViewById(R.id.btn_4x5);
        btn_5x6 = gameD.findViewById(R.id.btn_5x6);
        setTimer = gameD.findViewById(R.id.setTimer);
        playGame = gameD.findViewById(R.id.startGame);

        // Timer layout settings

        sec30 = timerAlert.findViewById(R.id.sec30);
        sec45 = timerAlert.findViewById(R.id.sec45);
        sec60 = timerAlert.findViewById(R.id.sec60);
        playTimer = timerAlert.findViewById(R.id.startGame);
        cancelTimer = timerAlert.findViewById(R.id.cancelTimer);

        playAgain.setBackgroundColor(action_btn_color);
        playAgain.setTextColor(Color.BLACK);

        // Multiplayer dialog settings

        p1Edit = mulAlert.findViewById(R.id.player1);
        p2Edit = mulAlert.findViewById(R.id.player2);

        playMul = mulAlert.findViewById(R.id.startGame);

        l1 = mulAlert.findViewById(R.id.linearLayout2);
        l2 = mulAlert.findViewById(R.id.linearLayout3);

        // Button settings

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(16, 8, 16, 8);

        btn_3x4.setTextColor(Color.BLACK);
        btn_4x5.setTextColor(Color.BLACK);
        btn_5x6.setTextColor(Color.BLACK);
        sec30.setTextColor(Color.BLACK);
        sec45.setTextColor(Color.BLACK);
        sec60.setTextColor(Color.BLACK);

        btn_3x4.setBackgroundColor(btn_color_pressed);
        btn_4x5.setBackgroundColor(btn_color);
        btn_5x6.setBackgroundColor(btn_color);
        sec30.setBackgroundColor(btn_color_pressed);
        sec45.setBackgroundColor(btn_color);
        sec60.setBackgroundColor(btn_color);

        btn_3x4.setLayoutParams(new LinearLayout.LayoutParams(width / 4, height / 16));
        btn_4x5.setLayoutParams(new LinearLayout.LayoutParams(width / 4, height / 16));
        btn_5x6.setLayoutParams(new LinearLayout.LayoutParams(width / 4, height / 16));

        layoutParams.setMargins(10, 8, 10, 8);

        sec30.setLayoutParams(new LinearLayout.LayoutParams(width / 4, height / 16));
        sec45.setLayoutParams(new LinearLayout.LayoutParams(width / 4, height / 16));
        sec60.setLayoutParams(new LinearLayout.LayoutParams(width / 4, height / 16));

        btn_3x4.setLayoutParams(layoutParams);
        btn_4x5.setLayoutParams(layoutParams);
        btn_5x6.setLayoutParams(layoutParams);

        sec30.setLayoutParams(layoutParams);
        sec45.setLayoutParams(layoutParams);
        sec60.setLayoutParams(layoutParams);


        // End dialog

        builder.setView(gameD);
        gameDialog = builder.create();
        gameDialog.setCancelable(true);

        builder.setView(theEnd);
        endDialog = builder.create();
        endDialog.setCancelable(false);

        builder.setView(timerAlert);
        timeDialog = builder.create();
        timeDialog.setCancelable(false);

        builder.setView(mulAlert);
        twoPlayerDialog = builder.create();
        twoPlayerDialog.setCancelable(true);

        if (userName == "-1") settings.setVisibility(View.INVISIBLE);
        else settings.setVisibility(View.VISIBLE);

        settings.setOnClickListener(v -> {
            changeActivity(SettingsActivity.class);
        });

        btn_3x4.setOnClickListener(v -> {
            n = 12;
            currentSize = "3x4";
            btn_3x4.setBackgroundColor(btn_color_pressed);
            btn_4x5.setBackgroundColor(btn_color);
            btn_5x6.setBackgroundColor(btn_color);
        });

        btn_4x5.setOnClickListener(v -> {
            n = 20;
            currentSize = "4x5";
            btn_3x4.setBackgroundColor(btn_color);
            btn_4x5.setBackgroundColor(btn_color_pressed);
            btn_5x6.setBackgroundColor(btn_color);
        });

        btn_5x6.setOnClickListener(v -> {
            n = 30;
            currentSize = "5x6";
            btn_3x4.setBackgroundColor(btn_color);
            btn_4x5.setBackgroundColor(btn_color);
            btn_5x6.setBackgroundColor(btn_color_pressed);
        });

        sec30.setOnClickListener(v -> {
            i = 0;
            sec30.setBackgroundColor(btn_color_pressed);
            sec45.setBackgroundColor(btn_color);
            sec60.setBackgroundColor(btn_color);
        });
        sec45.setOnClickListener(v -> {
            i = 1;
            sec30.setBackgroundColor(btn_color);
            sec45.setBackgroundColor(btn_color_pressed);
            sec60.setBackgroundColor(btn_color);
        });
        sec60.setOnClickListener(v -> {
            i = 2;
            sec30.setBackgroundColor(btn_color);
            sec45.setBackgroundColor(btn_color);
            sec60.setBackgroundColor(btn_color_pressed);
        });

        playMul.setOnClickListener(v -> {
            boolean f1 = true, f2 = true;
            p1Text = p1Edit.getText().toString().trim();
            p2Text = p2Edit.getText().toString().trim();
            if (p1Text.isEmpty()) {
                f1 = false;
                p1Edit.setError("Please input Player 1 name");
            }
            if (p2Text.isEmpty()) {
                f2 = false;
                p2Edit.setError("Please input Player 2 name");
            }
            if (f1 && f2) {
                MultiplayerActivity.p1Text = p1Text;
                MultiplayerActivity.p2Text = p2Text;
                changeActivity(MultiplayerActivity.class);
            }
        });

        singleplayer.setOnClickListener(v -> {
            Methods.clickSound(this);
            singleplayer.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
            gameDialog.show();
        });

        setTimer.setOnClickListener(v -> {
            gameDialog.cancel();
            timeDialog.show();
        });

        cancelTimer.setOnClickListener(v -> {
            timeDialog.cancel();
            gameDialog.show();
        });

        playGame.setOnClickListener(v -> {
            gameDialog.cancel();
            imageNumbers = new ArrayList<>();
            resetAll();
            generate();
            isVisible = false;
            isClickable = new ArrayList<>(Arrays.asList(new Boolean[n]));
            isClickableTrack = new ArrayList<>(Arrays.asList(new Boolean[n]));
            Collections.fill(isClickable, Boolean.TRUE);
            singleplayer.setVisibility(View.INVISIBLE);
            multiplayer.setVisibility(View.INVISIBLE);
            logo.setVisibility(View.INVISIBLE);
            if (!userName.equals("-1")) {
                getFirebaseData();
            }
        });

        playTimer.setOnClickListener(v -> {
            timeDialog.cancel();
            imageNumbers = new ArrayList<>();
            isVisible = true;
            resetAll();
            generate();
            isClickable = new ArrayList<>(Arrays.asList(new Boolean[n]));
            isClickableTrack = new ArrayList<>(Arrays.asList(new Boolean[n]));
            Collections.fill(isClickable, Boolean.TRUE);
            singleplayer.setVisibility(View.INVISIBLE);
            multiplayer.setVisibility(View.INVISIBLE);
            pauseResume.setVisibility(View.VISIBLE);
            mTextViewCountDown.setVisibility(View.VISIBLE);
            pauseResume.setText("Pause");
            setTimer(i);
            logo.setVisibility(View.INVISIBLE);
            if (!userName.equals("-1")) {
                getFirebaseData();
            }
        });

        multiplayer.setOnClickListener(v -> {
            Methods.clickSound(this);
            multiplayer.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
            twoPlayerDialog.show();
        });

        pauseResume.setOnClickListener(v -> {
            if (isOnPause) {
                pauseTimer();
                pauseResume.setText("Resume");
                isOnPause = false;
            } else {
                startTimer();
                pauseResume.setText("Pause");
                isOnPause = true;
            }
        });

        playAgain.setOnClickListener(v -> {
            endDialog.cancel();
            resetAll();
            resetTimer();
            gridLayout.removeAllViews();
            singleplayer.setVisibility(View.VISIBLE);
            multiplayer.setVisibility(View.VISIBLE);
            pauseResume.setVisibility(View.INVISIBLE);
            logo.setVisibility(View.VISIBLE);
            mTextViewCountDown.setVisibility(View.INVISIBLE);
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
                    else if (Objects.equals(dataSnapshot.getKey(), "games"))
                        playedGames = Integer.parseInt(value);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public void finish() {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    // ValueAnimator, shuffle images if 3 false attempts
    @SuppressLint({"NonConstantResourceId", "UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    public void onClick(View view) {
        if (isAlive) return;
        if (!isOnPause) return;

        id = view.getId(); // Get current image id

        if (!checkIsImageOpen[id] && isClickable.get(id)) { // if("image is not opened" and "is image clickable")
            Animation pressAnimation = AnimationUtils.loadAnimation(this, R.anim.press);
            Animation pullAnimation = AnimationUtils.loadAnimation(this, R.anim.pull);
            pressAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    setImage(id); // Change image using id
                    imageButtons.get(id).startAnimation(pullAnimation);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            imageButtons.get(id).startAnimation(pressAnimation);

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
                FlipThread flipThread = new FlipThread();
                flipThread.start();
                isAlive = true;

            } else {
                ImageThread imageThread = new ImageThread();
                ImageView[] imageViews = new ImageView[n];
                for (int i = 0; i < n; i++) {
                    imageViews[i] = imageButtons.get(i);
                }
                imageThread.sendImage(imageViews);
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
            if (nScore > nBestScore) nBestScore = nScore;
            endText.setText("You Win!");
            score.setText("Your score:" + nScore);
            endDialog.show();
            if (isVisible) pauseTimer();
            if (!userName.equals("-1")) saveFirebaseScore();
        }
    }


    private void saveFirebaseScore() {

        String current = "";
        playedGames++;

        if (nScore < 0) nScore = 0;
        scores += nScore + "-";
        sizes += currentSize + "-";
        steps += stepCount + "-";
        times += tick + "-";

        for (int i = 0; i < scores.length(); i++) {
            char c = scores.charAt(i);
            if (c == '-') {
                scoreList.add(Integer.valueOf(current));
                current = "";
            } else current += c;
        }

        Collections.sort(scoreList);
        nBestScore = scoreList.get(scoreList.size() - 1);

        rootDatabaseRef.child(userName).child("games").setValue(playedGames);
        rootDatabaseRef.child(userName).child("score").setValue(scores);
        rootDatabaseRef.child(userName).child("size").setValue(sizes);
        rootDatabaseRef.child(userName).child("step").setValue(steps);
        rootDatabaseRef.child(userName).child("time").setValue(times);
        rootDatabaseRef.child(userName).child("bestScore").setValue(nBestScore);

        Log.e("msg", "All right");
    }

    static void randomize(ArrayList<Integer> arr) {
        Random r = new Random();
        for (int i = arr.toArray().length - 1; i > 0; i--) {
            int j = r.nextInt(i + 1);
            int temp = arr.get(i);
            arr.set(i, arr.get(j));
            arr.set(j, temp);
        }
    }

    void setImage(int i) {
        ImageView currentImage = imageButtons.get(i);
        int currentImageId = imageNumbers.get(i);
        currentImage.setImageResource(links.get(currentImageId));
    }

    void resetAll() {
        tick = 0;
        nScore = 0;
        checkIsImageOpen = new boolean[n];
        mTextViewCountDown.setText(String.format(Locale.getDefault(), "%02d:%02d", 0, 0));
        randomize(imageNumbers);
        for (int i = 0; i < imageButtons.toArray().length; i++) {
            ImageView cur = imageButtons.get(i);
            cur.setImageResource(R.drawable.code);
        }
        Collections.fill(isClickable, Boolean.TRUE);
        clicked = 0;
        lastClicked = -1;
        allChecked = 0;
    }

    private void generate() {
        for (int i = 0; i < n / 2; i++) {
            imageNumbers.add(i);
        }
        for (int i = 0; i < n / 2; i++) {
            imageNumbers.add(i);
        }
        randomize(imageNumbers);

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
            param.height = height / (column * 2) - 70;
            param.width = width / column - 40 - width / 60;
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
        }

    }

    private void startTimer() {
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
                playedGames++;
                if (allChecked == n) {
                    score.setText("Your score: " + nScore);
                    endText.setText("You Win!");
                } else {
                    score.setText("Your score: 0");
                    endText.setText("You Lose");
                }
            }
        }.start();
    }

    private void pauseTimer() {
        mCountDownTimer.cancel();
    }

    private void resetTimer() {
        mTimeLeftInMillis = startTimeInMillis;
        updateCountDownText();
    }

    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        mTextViewCountDown.setText(timeLeftFormatted);
    }

    private void setTimer(int i) {
        if (i == -1) startTimeInMillis = 5000;
        else if (i == 0) startTimeInMillis = 30000;
        else if (i == 1) startTimeInMillis = 45000;
        else if (i == 2) startTimeInMillis = 60000;

        mTimeLeftInMillis = startTimeInMillis;
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        mTextViewCountDown.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));

        startTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isVisible && isOnPause) {
            pauseTimer();
            pauseResume.setText("Resume");
            isOnPause = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isVisible && !isOnPause) {
            startTimer();
            pauseResume.setText("Pause");
            isOnPause = true;
        }
    }

    private void changeActivity(Class class_) {
        startActivity(new Intent(this, class_));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        this.finish();
    }


    class FlipThread extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            imageButtons.get(id).startAnimation(AnimationUtils.loadAnimation(
                    getApplicationContext(), R.anim.zoom_out
            ));
            imageButtons.get(lastClicked).startAnimation(AnimationUtils.loadAnimation(
                    getApplicationContext(), R.anim.zoom_out
            ));
            isAlive = false;
        }
    }

    class ImageThread extends Thread {

        ImageView lastImage;
        ImageView currentImage;

        @Override
        public void run() {
            try {
                Thread.sleep(350);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Animation pressAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.press);
            Animation pullAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.pull);
            pressAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    currentImage.setImageResource(R.drawable.code);
                    lastImage.setImageResource(R.drawable.code);
                    imageButtons.get(id).startAnimation(pullAnimation);
                    imageButtons.get(lastClicked).startAnimation(pullAnimation);
                    isAlive = false;
                }
                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            imageButtons.get(id).startAnimation(pressAnimation);
            imageButtons.get(lastClicked).startAnimation(pressAnimation);

            for (int i = 0; i < n; i++) isClickable.set(i, isClickableTrack.get(i));
            checkIsImageOpen[id] = false;
            checkIsImageOpen[lastClicked] = false;
        }
        public void sendImage(ImageView[] imageViews) {
            lastImage = imageViews[lastClicked];
            currentImage = imageViews[id];
        }
    }
}