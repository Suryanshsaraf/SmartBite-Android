package com.smartbite.ui.login;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.smartbite.ui.customer.CustomerDashboard;
import com.smartbite.ui.delivery.DeliveryDashboard;
import com.smartbite.utils.Constants;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private RadioButton customerRadio;
    private View progressBar;
    private UserDAO userDAO;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        userDAO = new UserDAO(dbHelper);
        prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);

        if (isUserLoggedIn()) {
            redirectToDashboard();
            finish();
            return;
        }

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        customerRadio = findViewById(R.id.customerRadio);
        progressBar = findViewById(R.id.progressBar);

        findViewById(R.id.signupText).setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        });
    }

    private void setupClickListeners() {
        findViewById(R.id.loginButton).setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        emailInput.setError(null);
        passwordInput.setError(null);

        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String role = customerRadio.isChecked() ? Constants.ROLE_CUSTOMER : Constants.ROLE_DELIVERY;

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

        progressBar.setVisibility(View.VISIBLE);

        User user = userDAO.getUserByCredentials(email, password);
        if (user != null && user.getRole().equals(role)) {
            saveUserInfo(user);
            Toast.makeText(this, R.string.msg_login_success, Toast.LENGTH_SHORT).show();
            
            if (user.isCustomer()) {
                startActivity(new Intent(this, CustomerDashboard.class));
            } else {
                startActivity(new Intent(this, DeliveryDashboard.class));
            }
            finish();
        } else {
            Toast.makeText(this, R.string.error_login_failed, Toast.LENGTH_SHORT).show();
        }

        progressBar.setVisibility(View.GONE);
    }

    private void saveUserInfo(User user) {
        SharedPreferences.Editor editor = prefs.edit();
        // Correctly save the user ID as a long
        editor.putLong(Constants.KEY_USER_ID, user.getId());
        editor.putString(Constants.KEY_USER_ROLE, user.getRole());
        editor.putString(Constants.KEY_USERNAME, user.getUsername());
        editor.apply();
    }

    private boolean isUserLoggedIn() {
        return prefs.contains(Constants.KEY_USER_ID);
    }

    private void redirectToDashboard() {
        String userRole = prefs.getString(Constants.KEY_USER_ROLE, "");
        if (Constants.ROLE_CUSTOMER.equals(userRole)) {
            startActivity(new Intent(this, CustomerDashboard.class));
        } else if (Constants.ROLE_DELIVERY.equals(userRole)) {
            startActivity(new Intent(this, DeliveryDashboard.class));
        }
    }
}