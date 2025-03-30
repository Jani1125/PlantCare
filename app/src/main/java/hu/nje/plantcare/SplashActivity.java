package hu.nje.plantcare;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Animáció betöltése
        Button logoButton = findViewById(R.id.logoButton);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        logoButton.startAnimation(fadeIn);

        // 3 másodperc után átlép a főképernyőre
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, GoogleSignInActivity.class);
            startActivity(intent);
            finish();
        }, 3000);
    }
}
