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
        menuAdapter = new MenuAdapter(menuItems, item -> {
            // Menüelemre kattintva valami történhet (például Activity váltás)
            Toast.makeText(MainActivity.this, "Clicked: " + item, Toast.LENGTH_SHORT).show();
            menuRecyclerView.setVisibility(View.GONE);  // Menüpontok elrejtése
        });
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

        /*// Az UI elemek inicializálása azonosító által
        data = findViewById(R.id.data);
        infoText = findViewById(R.id.infoText);
        infoText.setText("");
        //imageView = findViewById(R.id.imageView);

        // Adatbázis elérése
        PlantDatabase db = PlantDatabase.getDatabase(this);
        PlantDao plantDao = db.plantDao();

        // Új szál indítása az adatbázis műveletekhez (mert Room nem engedi a főszálon)
        new Thread(() -> {
            // Az adatbázisból az összes növény lekérése
            List<Plant> plantList = plantDao.getAllPlants();

            // Ha nincs adat az adatbázisban, akkor meghívjuk az API-t
            if (plantList.isEmpty()) {
                runOnUiThread(() -> ApiRequest(1, API_KEY)); // API kérés az UI szálon
                runOnUiThread(() -> infoText.setText("Az adatok az API-ból lettek feltöltve"));
            } else {
                // Ha már van adat az adatbázisban, azt jelenítjük meg az UI-n
                runOnUiThread(() -> {
                    StringBuilder details = new StringBuilder();
                    for (Plant plant : plantList) {
                        details.append("Név: ").append(plant.commonName).append("\n")
                                .append("Tudományos név: ").append(plant.scientificName).append("\n")
                                .append("Típus: ").append(plant.type).append("\n")
                                .append("Életciklus: ").append(plant.cycle).append("\n")
                                .append("Öntözés: ").append(plant.watering).append("\n\n");
                    }
                    // Információ megjelenítése a UI-n
                    infoText.setText("Az adatok a ROOM adatbázisból lettek feltöltve");
                    data.setText(details.toString());

                });
            }
        }).start();
    }

    /**
     * API lekérő függvény, amely egy növény azonosítója alapján kér le adatokat az API-ból.
     * @param id A növény egyedi azonosítója.
     * @param apiKey Az API eléréséhez szükséges kulcs.
     */
    /*public void ApiRequest(int id, String apiKey) {
        //// Az API URL összeállítása a kapott ID és API kulcs alapján ////
        String species_url = baseUrl + id + "?key=" + apiKey;

        // Adatbázis elérése
        PlantDatabase db = PlantDatabase.getDatabase(this);
        PlantDao plantDao = db.plantDao();

        // API kérés indítása Volley-val
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, species_url, null,
                species -> {  // Ha sikeres a kérés
                    data.setText("Sikerült elérni");

                    try {
                        // Az API válasz feldolgozása
                        String commonName = species.optString("common_name", "N/A");
                        JSONArray scientificNamesArray = species.optJSONArray("scientific_name");
                        String scientificName = (scientificNamesArray != null && scientificNamesArray.length() > 0) ? scientificNamesArray.getString(0) : "N/A";
                        String type = species.optString("type", "N/A");
                        String cycle = species.optString("cycle", "N/A");
                        String watering = species.optString("watering", "N/A");

                        // Az adatok hozzáadása a details StringBuilder-hez
                        details.append("Név: ").append(commonName).append("\n")
                                .append("Tudományos név: ").append(scientificName).append("\n")
                                .append("Típus: ").append(type).append("\n")
                                .append("Életciklus: ").append(cycle).append("\n")
                                .append("Öntözés: ").append(watering).append("\n\n");

                        // A kapott adatok megjelenítése az UI-n
                        data.setText(details.toString());


                        // Az adatokat egy új szálban mentjük el az adatbázisba (Room nem engedi a főszálon)
                        new Thread(() -> {
                            plantDao.insert(new Plant(commonName, scientificName, type, cycle, watering));
                        }).start();

                    } catch (Exception e) {
                        // Hiba esetén hibaüzenetet jelenítünk meg
                        data.setText("Hiba a JSON feldolgozásakor");
                        e.printStackTrace();
                    }
                },
                error -> data.setText("ERROR") // Hiba esetén ERROR üzenet
        );

        // Az API kérést hozzáadjuk a Volley kérési sorához
        Volley.newRequestQueue(this).add(request);
    }*/


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