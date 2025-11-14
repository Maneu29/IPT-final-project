package com.example.iptproject; // Make sure your package name is correct

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView; // Import ImageView
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable; // Import Nullable
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class RegisterActivity extends AppCompatActivity {

    // Declare views
    EditText editTextEmail, editTextPassword;
    Button btnRegister;
    TextView btnLoginNow; // A text to go back to the login screen
    ImageView googleSignInButton; // For the Google sign-in icon
    ImageView showPasswordButton; // The variable for the show password button

    // Declare Firebase Auth
    private FirebaseAuth mAuth;

    // Declare Google Sign-In client
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001; // Request code for Google Sign-In

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        editTextEmail = findViewById(R.id.editTextTextEmailAddress);
        editTextPassword = findViewById(R.id.editTextTextPassword);
        btnRegister = findViewById(R.id.button3);
        btnLoginNow = findViewById(R.id.LoginTextBtn);
        googleSignInButton = findViewById(R.id.googleIcon);
        showPasswordButton = findViewById(R.id.showPasswordButton); // Initialize the button

        // --- Google Sign-In Configuration ---
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set listener for Google Sign-In button
        googleSignInButton.setOnClickListener(v -> {
            signInWithGoogle();
        });

        // Set listener to go back to login page
        btnLoginNow.setOnClickListener(v -> {
            finish();
        });

        // Set OnClickListener for the main Register button
        btnRegister.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            // Input Validation
            if (email.isEmpty()) {
                editTextEmail.setError("Email is required");
                editTextEmail.requestFocus();
                return;
            }
            if (password.isEmpty()) {
                editTextPassword.setError("Password is required");
                editTextPassword.requestFocus();
                return;
            }
            if (password.length() < 6) {
                editTextPassword.setError("Password must be at least 6 characters");
                editTextPassword.requestFocus();
                return;
            }

            // Firebase User Creation Logic
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Account Created Successfully.", Toast.LENGTH_SHORT).show();
                            finish(); // Go back to login screen
                        } else {
                            Toast.makeText(RegisterActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });
        
        // Setup the password visibility toggle
        setupPasswordVisibilityToggle();
    }

    // --- Google Sign-In Methods ---

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
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w("GoogleSignIn", "Google sign in failed", e);
                Toast.makeText(this, "Google sign in failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Google Registration Successful.", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();
                        // Navigate to your main activity
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish(); // Finish all activities in the stack
                    } else {
                        Toast.makeText(RegisterActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupPasswordVisibilityToggle() {
        showPasswordButton.setOnClickListener(v -> {
            if (editTextPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                showPasswordButton.setImageResource(R.drawable.baseline_visibility_24);
            } else {
                editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                showPasswordButton.setImageResource(R.drawable.baseline_visibility_off_24);
            }
            editTextPassword.setSelection(editTextPassword.length());
        });
    }
}
