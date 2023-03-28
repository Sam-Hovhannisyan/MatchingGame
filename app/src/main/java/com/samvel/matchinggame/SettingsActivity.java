package com.samvel.matchinggame;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.samvel.matchinggame.databinding.ActivitySettingsBinding;

import java.io.IOException;
import java.io.InputStream;

import io.github.muddz.styleabletoast.StyleableToast;

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding binding;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    Uri imageUri;
    public static int bestScoreInt = 0;
    public static final int KITKAT_VALUE = 1002;
    private static final int MAX_IMAGE_SIZE = 1536 * 1536;
    private DatabaseReference rootDatabaseRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    ImageView backToMenu, profileImage;
    TextView username, bestScore;
    String usernameText;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        backToMenu = findViewById(R.id.backToMenu);
        storageReference = FirebaseStorage.getInstance().getReference();
        rootDatabaseRef = FirebaseDatabase.getInstance().getReference();


        binding.changeImage.setOnClickListener(view -> selectImage());

        binding.saveChanges.setOnClickListener(view -> {
            try {
                saveImage();
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Please wait while data is saving...");
                progressDialog.setTitle("Changes saving");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                new Handler().postDelayed(() -> progressDialog.dismiss(), 2000);
            } catch (Exception e) {
                StyleableToast.makeText(this, "Changes are not found", Toast.LENGTH_LONG, R.style.mytoast).show();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        usernameText = mAuth.getCurrentUser().getDisplayName();

        username = findViewById(R.id.username);
        bestScore = findViewById(R.id.bestscore);

        profileImage = findViewById(R.id.profileImage);
        // mUser.

        username.setText(mUser.getDisplayName());
        bestScore.setText(bestScoreInt + "");

        loadImage();

        binding.deleteAccount.setOnClickListener(view -> new AlertDialog.Builder(this).setMessage("Do you want to delete your account?").setPositiveButton("Yes", (dialogInterface, i) -> mAuth.getCurrentUser().delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                StyleableToast.makeText(this, "Account was deleted successfully", Toast.LENGTH_LONG, R.style.mytoast).show();
                rootDatabaseRef.child("users").child(usernameText).removeValue();
                changeActivity(MenuActivity.class);
            }
        })).setNegativeButton("No", null).show());

        binding.changePassword.setOnClickListener(view -> mAuth.sendPasswordResetEmail(mAuth.getCurrentUser().getEmail()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                StyleableToast.makeText(this, "Please check your email", Toast.LENGTH_LONG, R.style.mytoast).show();
            }
        }));

        backToMenu.setOnClickListener(view -> {
            Methods.clickSound(this);
            backToMenu.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
            changeActivity(MainActivity.class);
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == KITKAT_VALUE && data != null && data.getData() != null) {
                imageUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                int imageSize = inputStream.available();
                inputStream.close();

                if (imageSize > MAX_IMAGE_SIZE) {
                    StyleableToast.makeText(this, "Image is too large", Toast.LENGTH_LONG, R.style.mytoast).show();
                    imageUri = null;
                } else {
                binding.profileImage.setImageURI(imageUri);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, KITKAT_VALUE);

    }

    private void changeActivity(Class class_) {
        startActivity(new Intent(this, class_));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        this.finish();
    }

    private void saveImage() {
        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setPhotoUri(imageUri).build();
        if (imageUri == null) {
            progressDialog.dismiss();
            StyleableToast.makeText(this, "Changes are not found", Toast.LENGTH_LONG, R.style.mytoast).show();
            return;
        }
        FirebaseAuth.getInstance().getCurrentUser().updateProfile(userProfileChangeRequest).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                StyleableToast.makeText(this, "Changes are saved successfully", Toast.LENGTH_LONG, R.style.mytoast).show();
            }
        });
    }

    private void loadImage() {
        profileImage = findViewById(R.id.profileImage);
        Glide.with(this).load(mAuth.getCurrentUser().getPhotoUrl()).into(profileImage);
        Log.e("look2", mAuth.getCurrentUser().getPhotoUrl() + "");
    }

}