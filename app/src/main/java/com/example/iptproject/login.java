package com.example.iptproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class login extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button btnLogin, btnForgot;
    private TextView btnGoToRegister;
    private ImageButton showPasswordButton;
    private ImageView googleSignInButton;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        editTextEmail = findViewById(R.id.editTextTextEmailAddress);
        editTextPassword = findViewById(R.id.editTextTextPassword);
        btnLogin = findViewById(R.id.Loginbtn2);
        btnGoToRegister = findViewById(R.id.RegisterTextbtn);
        showPasswordButton = findViewById(R.id.showPasswordButton);
        btnForgot = findViewById(R.id.forgotButton);
        googleSignInButton = findViewById(R.id.googleIcon);

        // Google Sign-In setup
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Click listeners
        googleSignInButton.setOnClickListener(v -> signInWithGoogle());
        btnGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(login.this, RegisterActivity.class));
            // Optional: finish(); if you don't want to come back here with back button
        });
        btnForgot.setOnClickListener(v -> Toast.makeText(this, "Forgot password coming soon", Toast.LENGTH_SHORT).show());
        setupPasswordVisibilityToggle();

        // Email/Password Login
        btnLogin.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(login.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            goToHomepage(); // This clears the entire back stack
                        } else {
                            String message = task.getException() != null ?
                                    task.getException().getMessage() : "Unknown error";
                            Toast.makeText(login.this, "Login Failed: " + message, Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }

    // Toggle password visibility
    private void setupPasswordVisibilityToggle() {
        showPasswordButton.setOnClickListener(v -> {
            if (editTextPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                // Show password
                editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                showPasswordButton.setImageResource(R.drawable.baseline_visibility_24);
            } else {
                // Hide password
                editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                showPasswordButton.setImageResource(R.drawable.baseline_visibility_off_24);
            }
            // Move cursor to end
            editTextPassword.setSelection(editTextPassword.getText().length());
        });
    }

    // Google Sign-In
    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account.getIdToken());
                }
            } catch (ApiException e) {
                Toast.makeText(this, "Google Sign-In Failed: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(login.this, "Google Sign-In Successful", Toast.LENGTH_SHORT).show();
                        goToHomepage();
                    } else {
                        Toast.makeText(login.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // CRITICAL: This clears onboarding/login from back stack
    private void goToHomepage() {
        Intent intent = new Intent(login.this, HomepageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}