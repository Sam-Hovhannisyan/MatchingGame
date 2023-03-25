package com.samvel.matchinggame;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;

public class MultiplayerActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView backToMenu;
    TextView player1, player2, winText;
    AlertDialog mul_end;
    Button playAgain, exit;
    static String p1Text, p2Text;
    int n = 30; // Game size
    int turn = 0, p1guessed = 0, p2guessed = 0;
    int id, width, height, tick, activeColor, color;
    int clicked = 0, lastClicked = -1, allChecked = 0;
    boolean isAlive = false; // Check if thread is alive
    boolean[] checkIsImageOpen = new boolean[n]; // Check if image is opened
    GridLayout gridLayout;
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
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);

        backToMenu = findViewById(R.id.backToMenu);
        gridLayout = findViewById(R.id.gridLayout);

        player1 = findViewById(R.id.player1);
        player2 = findViewById(R.id.player2);

        activeColor = Color.rgb(255,75,68);
        color = Color.WHITE;

        // Alert Dialog

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View mulAlert = getLayoutInflater().inflate(R.layout.multiplayer_end, null);

        playAgain = mulAlert.findViewById(R.id.playAgain);
        exit = mulAlert.findViewById(R.id.exit);
        winText = mulAlert.findViewById(R.id.endText);

        builder.setView(mulAlert);
        mul_end = builder.create();
        mul_end.setCancelable(false);

        playAgain.setOnClickListener(view -> {
            mul_end.cancel();
            startGame();
        });

        exit.setOnClickListener(view -> {
            mul_end.cancel();
            changeActivity(MainActivity.class);
        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;

        startGame();

        backToMenu.setOnClickListener(view -> {
            Methods.clickSound(this);
            backToMenu.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
            changeActivity(MenuActivity.class);
        });
    }

    private void startGame(){
        imageNumbers = new ArrayList<>();
        resetAll();
        generate();
        isClickable = new ArrayList<>(Arrays.asList(new Boolean[n]));
        isClickableTrack = new ArrayList<>(Arrays.asList(new Boolean[n]));
        Collections.fill(isClickable, Boolean.TRUE);
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

    private void resetAll() {
        tick = 0;
        turn = 0;
        p1guessed = 0;
        p2guessed = 0;
        player1.setText(p1Text + ": 0");
        player2.setText(p2Text + ": 0");
        player1.setTextColor(activeColor);
        player1.setTextSize(25);
        player2.setTextColor(color);
        player2.setTextSize(20);
        checkIsImageOpen = new boolean[n];
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

    private static void randomize(ArrayList<Integer> arr) {
        Random r = new Random();
        for (int i = arr.toArray().length - 1; i > 0; i--) {
            int j = r.nextInt(i + 1);
            int temp = arr.get(i);
            arr.set(i, arr.get(j));
            arr.set(j, temp);
        }
    }
    private void changeActivity(Class class_) {
        startActivity(new Intent(this, class_));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        this.finish();
    }

    @Override
    public void onClick(View view) {
        if (isAlive) return;

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
        }
        if (clicked == 2) {
            if (Objects.equals(imageNumbers.get(id), imageNumbers.get(lastClicked)) && isClickable.get(id) && isClickable.get(lastClicked)) {
                isClickable.set(id, false);
                isClickable.set(lastClicked, false);
                allChecked += 2;
                FlipThread flipThread = new FlipThread();
                if (turn == 0) p1guessed++;
                else p2guessed++;
                player1.setText(p1Text + ": " + p1guessed);
                player2.setText(p2Text + ": " + p2guessed);
                flipThread.start();
                isAlive = true;

            } else {
                MultiplayerActivity.ImageThread imageThread = new ImageThread();
                ImageView[] imageViews = new ImageView[n];
                for (int i = 0; i < n; i++) {
                    imageViews[i] = imageButtons.get(i);
                }
                imageThread.sendImage(imageViews);
                for (int i = 0; i < n; i++) isClickableTrack.set(i, isClickable.get(i));
                Collections.fill(isClickable, false);
                imageThread.start();
                if (turn == 0) {
                    player1.setTextColor(color);
                    player2.setTextColor(activeColor);
                    player1.setTextSize(20);
                    player2.setTextSize(25);
                    turn = 1;
                }
                else {
                    turn = 0;
                    player2.setTextColor(color);
                    player1.setTextColor(activeColor);
                    player1.setTextSize(25);
                    player2.setTextSize(20);
                }
                isAlive = true;
            }
            clicked = 0;
        }
        if (allChecked == n) {
            if (p1guessed > p2guessed) winText.setText(p1Text + " win!");
            else if (p1guessed < p2guessed) winText.setText(p2Text + " win!");
            else winText.setText("Draw");
            mul_end.show();
            Toast.makeText(this, "you win", Toast.LENGTH_SHORT).show();
        }
    }

    void setImage(int i) {
        ImageView currentImage = imageButtons.get(i);
        int currentImageId = imageNumbers.get(i);
        currentImage.setImageResource(links.get(currentImageId));
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
            Animation pressAnimation = AnimationUtils.loadAnimation(MultiplayerActivity.this, R.anim.press);
            Animation pullAnimation = AnimationUtils.loadAnimation(MultiplayerActivity.this, R.anim.pull);
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
