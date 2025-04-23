package hu.nje.plantcare;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import hu.nje.plantcare.adapters.MenuAdapter;
import hu.nje.plantcare.ui.FavPlantFragment;
import hu.nje.plantcare.ui.MenuManager;
import hu.nje.plantcare.ui.OwnPlantFragment;
import hu.nje.plantcare.ui.SearchFragment;
import hu.nje.plantcare.ui.SettingsFragment;

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
    private static boolean hasPopupBeenShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menuItems = new ArrayList<>();
        menuItems.add("Home");
        menuItems.add("Search");
        menuItems.add("Favourite plants");
        menuItems.add("Own plants");
        menuItems.add("Plant scanner");
        menuItems.add("Settings");
        menuItems.add("Notifications");

        if (!hasPopupBeenShown) {
            showWaterReminderPopup();
            hasPopupBeenShown= true;
        }


        MenuManager.setupMenu(
                this,
                menuItems,
                findViewById(R.id.menuRecyclerView),
                findViewById(R.id.hamMenuIcon),
                findViewById(R.id.profileImageView)
        );


    }
    private void showWaterReminderPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.popup_message, null);
        builder.setView(view);

        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}