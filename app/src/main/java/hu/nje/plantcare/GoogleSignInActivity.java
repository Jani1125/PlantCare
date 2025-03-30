package hu.nje.plantcare;

import com.google.android.gms.common.SignInButton;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

public class GoogleSignInActivity extends AppCompatActivity {

    // Request code for Google Sign-In
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_sign_in);

        // Google Sign-In beállítása
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        final GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Google Sign-In gomb eseménykezelője
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(view -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    // Eredmény kezelése a Google bejelentkezésből
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Ha a Google Sign-In aktivitásról jöttünk vissza
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    // Bejelentkezés eredményének kezelése
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            // A bejelentkezés sikeres volt
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            updateUI(account);
        } catch (ApiException e) {
            // A bejelentkezés hibát eredményezett
            Log.w("GoogleSignInActivity", "Google sign in failed", e);
            updateUI(null);
        }
    }

    // UI frissítése sikeres bejelentkezés esetén
    private void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            // Ha sikeres a bejelentkezés, megjelenítjük a felhasználó nevét
            String welcomeMessage = "Welcome, " + account.getDisplayName() + "!";
            Toast.makeText(this, welcomeMessage, Toast.LENGTH_LONG).show();

            // Itt navigálhatsz a főképernyőre, vagy bárhova, ahová szeretnéd
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();  // Befejezi a GoogleSignInActivity-t
        } else {
            // Ha a bejelentkezés nem sikerült
            Snackbar.make(findViewById(android.R.id.content), "Sign in failed", Snackbar.LENGTH_SHORT).show();
        }
    }
}
