package com.samvel.matchinggame;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.samvel.matchinggame.databinding.ActivityMainBinding;
import com.samvel.matchinggame.databinding.ActivitySettingsBinding;

import java.io.File;
import java.io.IOException;

import io.github.muddz.styleabletoast.StyleableToast;

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding binding;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    Uri imageUri;
    private DatabaseReference rootDatabaseRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    ImageView backToMenu, profileImage;
    TextView username, bestScore;
    String usernameText, emailText, bestScoreText;
    Button saveChanges;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        backToMenu = findViewById(R.id.backToMenu);
        storageReference = FirebaseStorage.getInstance().getReference();
        rootDatabaseRef = FirebaseDatabase.getInstance().getReference();

        loadImage();

        binding.changeImage.setOnClickListener(view -> {
            selectImage();
        });

        binding.saveChanges.setOnClickListener(view -> {
            uploadImage();
        });

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        usernameText = mAuth.getCurrentUser().getDisplayName();

        username = findViewById(R.id.username);
        bestScore = findViewById(R.id.bestscore);

        profileImage = findViewById(R.id.profileImage);
        // mUser.

        username.setText(mUser.getDisplayName());
        bestScore.setText(mUser.getProviderId());

        binding.deleteAccount.setOnClickListener(view -> {
            mAuth.getCurrentUser().delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    StyleableToast.makeText(this, "Account was deleted successfully", Toast.LENGTH_LONG, R.style.mytoast).show();
                    rootDatabaseRef.child("users").child(usernameText).removeValue();
                    changeActivity(MenuActivity.class);
                }
            });
        });

        binding.changePassword.setOnClickListener(view -> {
            mAuth.sendPasswordResetEmail(mAuth.getCurrentUser().getEmail()).addOnCompleteListener(task -> {
               if (task.isSuccessful()){
                   StyleableToast.makeText(this, "Please check your email", Toast.LENGTH_LONG, R.style.mytoast).show();
               }
            });
        });

        backToMenu.setOnClickListener(view -> {
            Methods.clickSound(this);
            backToMenu.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce));
            changeActivity(MainActivity.class);
        });
    }

    private void uploadImage() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Saving changes...");
        progressDialog.show();

        storageReference = FirebaseStorage.getInstance().getReference("profile_photos/" + username.getText().toString());
        storageReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            Toast.makeText(this, "Changes are successfully saved", Toast.LENGTH_SHORT).show();
            if (progressDialog.isShowing()) progressDialog.dismiss();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Changes are failed to save", Toast.LENGTH_SHORT).show();
            if (progressDialog.isShowing()) progressDialog.dismiss();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && data != null && data.getData() != null) {
            imageUri = data.getData();
            binding.profileImage.setImageURI(imageUri);
        }
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 100);
    }

    private void changeActivity(Class class_) {
        startActivity(new Intent(this, class_));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        this.finish();
    }

    private void loadImage() {
        usernameText = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("profile_photos/" + usernameText);

        File localFile;
        try {
             localFile = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        storageRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(this, "success!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(exception -> {
                    Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show();
                });
        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
        profileImage = findViewById(R.id.profileImage);
        profileImage.setImageBitmap(bitmap);

    }
}