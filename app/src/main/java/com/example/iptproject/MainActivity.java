package com.example.iptproject;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Logo fade-in animation
        ImageView logo = findViewById(R.id.imageView);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        logo.startAnimation(fadeIn);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent;
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                // Already logged in → go straight to Homepage
                intent = new Intent(MainActivity.this, HomepageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            } else {
                // Not logged in → show onboarding
                intent = new Intent(MainActivity.this, Entry1Activity.class);
            }

            startActivity(intent);

            // ONLY apply transition when NOT using CLEAR_TASK (safe!)
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

            finish();
        }, SPLASH_DELAY);
    }
}