package com.smartbite;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import com.smartbite.ui.login.LoginActivity;

/**
 * MainActivity displays a splash screen and initializes app resources
 */
public class MainActivity extends AppCompatActivity {
    // Splash screen duration
    private static final long SPLASH_DELAY = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hide action bar for splash screen
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Delay to show splash screen
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Start login activity
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish(); // Close splash screen
        }, SPLASH_DELAY);
    }
}