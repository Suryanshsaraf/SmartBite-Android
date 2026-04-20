package com.smartbite.ui.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.smartbite.R;
import com.smartbite.database.DatabaseHelper;
import com.smartbite.database.UserDAO;
import com.smartbite.models.User;
import com.smartbite.utils.Constants;

/**
 * SignupActivity handles new user registration
 * Validates input fields and creates new user account
 */
public class SignupActivity extends AppCompatActivity {
    private TextInputEditText usernameInput;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private RadioButton customerRadio;
    private View progressBar;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize database
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        userDAO = new UserDAO(dbHelper);

        // Initialize views
        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        usernameInput = findViewById(R.id.usernameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        customerRadio = findViewById(R.id.customerRadio);
        progressBar = findViewById(R.id.progressBar);

        // Set up login text click
        findViewById(R.id.loginText).setOnClickListener(v -> finish());
    }

    private void setupClickListeners() {
        findViewById(R.id.signupButton).setOnClickListener(v -> attemptSignup());
    }

    private void attemptSignup() {
        // Reset errors
        usernameInput.setError(null);
        emailInput.setError(null);
        passwordInput.setError(null);

        // Get values
        String username = usernameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String role = customerRadio.isChecked() ? Constants.ROLE_CUSTOMER : Constants.ROLE_DELIVERY;

        // Validate fields
        if (TextUtils.isEmpty(username)) {
            usernameInput.setError(getString(R.string.error_required));
            usernameInput.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            emailInput.setError(getString(R.string.error_required));
            emailInput.requestFocus();
            return;
        }

        if (!email.matches(Constants.EMAIL_PATTERN)) {
            emailInput.setError(getString(R.string.error_invalid_email));
            emailInput.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordInput.setError(getString(R.string.error_required));
            passwordInput.requestFocus();
            return;
        }

        if (password.length() < Constants.MIN_PASSWORD_LENGTH) {
            passwordInput.setError(getString(R.string.error_password_length));
            passwordInput.requestFocus();
            return;
        }

        // Show progress
        progressBar.setVisibility(View.VISIBLE);

        // Check if email already exists
        if (userDAO.isEmailExists(email)) {
            emailInput.setError("Email already registered");
            emailInput.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }

        // Create new user
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);

        // Insert user
        long userId = userDAO.insert(user);
        if (userId > 0) {
            Toast.makeText(this, R.string.msg_signup_success, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
        }

        progressBar.setVisibility(View.GONE);
    }
}