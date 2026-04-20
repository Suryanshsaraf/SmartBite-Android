package com.smartbite.ui.profile;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.smartbite.R;
import com.smartbite.database.DatabaseHelper;
import com.smartbite.database.UserDAO;
import com.smartbite.models.User;
import com.smartbite.utils.Constants;

public class ProfileActivity extends AppCompatActivity {

    private TextInputEditText usernameInput;
    private TextInputEditText emailInput;
    private TextInputEditText phoneInput;
    private TextInputEditText addressInput;
    private MaterialButton saveButton;
    private ProgressBar progressBar;
    private UserDAO userDAO;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize UserDAO
        userDAO = new UserDAO(DatabaseHelper.getInstance(this));

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize views
        initViews();

        // Load user data
        loadUserData();

        // Setup save button click listener
        saveButton.setOnClickListener(v -> saveProfile());
    }

    private void initViews() {
        usernameInput = findViewById(R.id.usernameInput);
        emailInput = findViewById(R.id.emailInput);
        phoneInput = findViewById(R.id.phoneInput);
        addressInput = findViewById(R.id.addressInput);
        saveButton = findViewById(R.id.saveButton);
        progressBar = findViewById(R.id.progressBar);
    }

    private void loadUserData() {
        showLoading(true);

        // Get current user ID from shared preferences
        long userId = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE)
                .getLong(Constants.KEY_USER_ID, -1);

        if (userId == -1) {
            Toast.makeText(this, "Please log in to view your profile", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentUser = userDAO.getUserById(userId);
        if (currentUser != null) {
            usernameInput.setText(currentUser.getUsername());
            emailInput.setText(currentUser.getEmail());
            phoneInput.setText(currentUser.getPhone());
            addressInput.setText(currentUser.getAddress());
        }

        showLoading(false);
    }

    private void saveProfile() {
        if (!validateInputs()) {
            return;
        }

        showLoading(true);

        // Update user object
        currentUser.setPhone(phoneInput.getText().toString().trim());
        currentUser.setAddress(addressInput.getText().toString().trim());

        // Update in database
        boolean success = userDAO.updateUser(currentUser);

        if (success) {
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
            showLoading(false);
        }
    }

    private boolean validateInputs() {
        String phone = phoneInput.getText().toString().trim();
        String address = addressInput.getText().toString().trim();

        if (phone.isEmpty()) {
            phoneInput.setError("Required");
            return false;
        }

        if (address.isEmpty()) {
            addressInput.setError("Required");
            return false;
        }

        return true;
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        saveButton.setEnabled(!show);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}