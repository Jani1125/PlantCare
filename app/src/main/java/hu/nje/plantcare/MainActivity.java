package hu.nje.plantcare;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import hu.nje.plantcare.adapters.MenuAdapter;
import hu.nje.plantcare.database.*;

public class MainActivity extends AppCompatActivity {

    private RecyclerView menuRecyclerView;
    private MenuAdapter menuAdapter;
    private List<String> menuItems;

    private LinearLayout menuLayout;
    private boolean isMenuVisible = false;

    //////////// API kulcs és az API URL alapja  /////////////////////////
    private static final String API_KEY = "sk-MzET67d004cc29a259082";
    private static final String baseUrl = "https://perenual.com/api/v2/species/details/";

    /// //////////////////////////////////////////////////////////////////


    /////// UI elemek //////////////////////////////
    private TextView data;
    private TextView infoText;
    private ImageView profileImageView;

    /// /////////////////////////////////////////////

    // Az adatok tárolására használt StringBuilder
    StringBuilder details = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Menüelemek inicializálása
        menuItems = new ArrayList<>();
        menuItems.add("Home");
        menuItems.add("Search");
        menuItems.add("Favourite plants");
        menuItems.add("Own plants");
        menuItems.add("Plant scanner");
        menuItems.add("Settings");

        // RecyclerView beállítása
        menuRecyclerView = findViewById(R.id.menuRecyclerView);
        menuRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Menü adapter beállítása
        menuAdapter = new MenuAdapter(this, menuItems, item -> {
            if ("Home".equals(item)) {
                // MainActivity indítása újra
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else if ("Search".equals(item)) {
                // Search fragment betöltése
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new SearchFragment())
                        .addToBackStack(null)
                        .commit();
            } else if ("Favourite plants".equals(item)) {
                // Favourite plants fragment betöltése
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new FavPlantFragment())
                        .addToBackStack(null)
                        .commit();
            } else if ("Own plants".equals(item)) {
            // Own plants fragment betöltése
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new OwnPlantFragment())
                    .addToBackStack(null)
                    .commit();
            } /*else if ("Plant scanner".equals(item)) {
                // Plant scanner fragment betöltése
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new PlantScannerFragment())
                        .addToBackStack(null)
                        .commit();
            }*/ else if ("Settings".equals(item)) {
                // Settings fragment betöltése
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new SettingsFragment())
                        .addToBackStack(null)
                        .commit();
            }
            menuRecyclerView.setVisibility(View.GONE);  // Menüpontok elrejtése
        });

        // RecyclerView adapter beállítása
        menuRecyclerView.setAdapter(menuAdapter);

        // Hamburger menü ikon kattintás kezelése
        ImageView hamMenuIcon = findViewById(R.id.hamMenuIcon);
        hamMenuIcon.setOnClickListener(v -> {
            if (menuRecyclerView.getVisibility() == View.GONE) {
                menuRecyclerView.setVisibility(View.VISIBLE);
            } else {
                menuRecyclerView.setVisibility(View.GONE);
            }
        });


        profileImageView = findViewById(R.id.profileImageView);

        // 🔄 Profilkép betöltése
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null && account.getPhotoUrl() != null) {
            Uri photoUri = account.getPhotoUrl();
            Glide.with(this)
                    .load(photoUri)
                    .circleCrop()
                    .into(profileImageView);
        }

        // 👆 Kattintásra menü
        profileImageView.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(MainActivity.this, view);
            popup.getMenuInflater().inflate(R.menu.profile_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_sign_out) {
                    signOut();
                    return true;
                }
                return false;
            });
            popup.show();
        });
    }

    // 🚪 Kijelentkezés logika
    private void signOut() {
        FirebaseAuth.getInstance().signOut();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build());

        googleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Intent intent = new Intent(this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish(); // lezárja a jelenlegi Activity-t
        });
    }  
}