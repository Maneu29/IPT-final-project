package com.example.iptproject; // Make sure your package name is correct

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

public class login extends AppCompatActivity {

    // Declare views
    EditText editTextEmail, editTextPassword;
    Button btnLogin, btnForgot;
    TextView btnGoToRegister;
    ImageButton showPasswordButton;
    ImageView googleSignInButton; // For the Google sign-in icon

    // Declare Firebase Auth
    private FirebaseAuth mAuth;

    // Declare Google Sign-In client
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001; // Request code for Google Sign-In

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize views from your login.xml
        editTextEmail = findViewById(R.id.editTextTextEmailAddress);
        editTextPassword = findViewById(R.id.editTextTextPassword);
        btnLogin = findViewById(R.id.Loginbtn2);
        btnGoToRegister = findViewById(R.id.RegisterTextbtn);
        showPasswordButton = findViewById(R.id.showPasswordButton);
        btnForgot = findViewById(R.id.forgotButton);
        googleSignInButton = findViewById(R.id.googleIcon); // The clickable Google icon

        // --- Google Sign-In Configuration ---
        // Configure Google Sign-In. This is necessary to request the user's ID token.
        // R.string.default_web_client_id is generated from your google-services.json file.
        // Ensure you have this file in your app directory and it's configured correctly in Firebase.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set OnClickListener for the Google Sign-In button
        googleSignInButton.setOnClickListener(v -> {
            signInWithGoogle();
        });


        setupPasswordVisibilityToggle();

        // Set OnClickListener for the "Register" text
        btnGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
        });

        // Set OnClickListener for the "Forgot?" button (Optional: Implement password reset)
        btnForgot.setOnClickListener(v -> {
            Toast.makeText(login.this, "Forgot Password functionality not implemented yet.", Toast.LENGTH_SHORT).show();
        });


        // Set OnClickListener for the Login button
        btnLogin.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

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

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Login Successful.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), HomepageActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(login.this, "Authentication failed. Check credentials.", Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
    
    // --- Google Sign-In Methods ---

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
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
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(login.this, "Google Sign-In Successful.", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();
                        // Navigate to your main activity
                        Intent intent = new Intent(getApplicationContext(), HomepageActivity.class);
                        startActivity(intent);
                        finish(); // Finish login activity
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(login.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
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
