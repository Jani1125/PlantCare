package hu.nje.plantcare;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import hu.nje.plantcare.ui.GoogleSignInFragment;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Animáció betöltése
        ImageView imageView3 = findViewById(R.id.imageView3);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        imageView3.startAnimation(fadeIn);

        // Ellenőrizzük, hogy a felhasználó be van-e jelentkezve
        new Handler().postDelayed(() -> {
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            if (account != null) {
                // Ha már be van jelentkezve, akkor a főoldalra megy
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                // Ha nincs bejelentkezve, akkor a GoogleSignInFragment-et jelenítjük meg
                GoogleSignInFragment fragment = new GoogleSignInFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment); // Itt használjuk a fragment_containert
                transaction.commit();
            }
        }, 3000); // 3 másodperc késleltetés
    }
}
