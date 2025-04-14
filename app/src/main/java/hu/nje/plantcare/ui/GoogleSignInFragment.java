package hu.nje.plantcare.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import hu.nje.plantcare.MainActivity;
import hu.nje.plantcare.R;

public class GoogleSignInFragment extends Fragment {

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient googleSignInClient;

    public GoogleSignInFragment() {
        // Üres publikus konstruktor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_google_sign_in, container, false);

        // Google Sign-In beállítása
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Web Client ID
                .requestEmail() // Email cím kérése
                .build();

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        // Google Sign-In gomb beállítása
        SignInButton signInButton = view.findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(v -> signIn());

        return view;
    }

    private void signIn() {
        // Indítjuk a Google Sign-In folyamatot
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // Eredmény feldolgozása
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            updateUI(account);
        } catch (ApiException e) {
            // Hibakezelés a különböző hibakódokhoz
            Log.w("GoogleSignInFragment", "Google sign in failed, status code: " + e.getStatusCode());
            handleSignInError(e.getStatusCode());
            updateUI(null);
        }
    }

    private void handleSignInError(int statusCode) {
        switch (statusCode) {
            case GoogleSignInStatusCodes.SIGN_IN_CANCELLED:
                Snackbar.make(requireView(), "Sign-in cancelled", Snackbar.LENGTH_SHORT).show();
                break;
            case GoogleSignInStatusCodes.SIGN_IN_FAILED:
                Snackbar.make(requireView(), "Sign-in failed. Try again later.", Snackbar.LENGTH_SHORT).show();
                break;
            default:
                Snackbar.make(requireView(), "Unknown error: " + statusCode, Snackbar.LENGTH_SHORT).show();
                break;
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            // Üdvözlés és továbblépés a fő képernyőre
            String welcomeMessage = "Welcome, " + account.getDisplayName() + "!";
            Toast.makeText(requireContext(), welcomeMessage, Toast.LENGTH_LONG).show();

            // Tovább az alkalmazás fő képernyőjére
            Intent intent = new Intent(requireActivity(), MainActivity.class);
            startActivity(intent);
            requireActivity().finish(); // Bezárjuk az aktuális Activity-t
        } else {
            Snackbar.make(requireView(), "Sign in failed", Snackbar.LENGTH_SHORT).show();
        }
    }
}