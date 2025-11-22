package com.example.iptproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 2800; // 2.8 seconds
    private static final String PREFS_NAME = "app_prefs";
    private static final String KEY_FIRST_LAUNCH = "is_first_launch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView logo = findViewById(R.id.imageView);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        logo.startAnimation(fadeIn);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            boolean isFirstLaunch = prefs.getBoolean(KEY_FIRST_LAUNCH, true);

            Intent intent;
            if (isFirstLaunch) {
                // First time launch -> show onboarding
                prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply();
                intent = new Intent(MainActivity.this, Entry1Activity.class);
            } else if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                // Not first time & already logged in -> go straight to Homepage
                intent = new Intent(MainActivity.this, HomepageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            } else {
                // Not first time & not logged in -> go to login
                intent = new Intent(MainActivity.this, login.class);
            }

            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }, SPLASH_DELAY);
    }
}
