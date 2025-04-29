package hu.nje.plantcare;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.PopupMenu;

import android.view.View;

import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    private static final String PREF_DARK_MODE = "dark_mode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Téma beállítása a mentett állapot alapján
        boolean isDarkMode = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(PREF_DARK_MODE, false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
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
        menuItems.add("Notifications");


        MenuManager.setupMenu(
                this,
                menuItems,
                findViewById(R.id.menuRecyclerView),
                findViewById(R.id.hamMenuIcon),
                findViewById(R.id.profileImageView)
        );


        //Egyszeri üzenet teszteléshez

        OneTimeWorkRequest testWorkRequest =
                new OneTimeWorkRequest.Builder(ReminderWorker.class)
                        .setInitialDelay(5, TimeUnit.SECONDS) // 5 másodperc múlva üzenet
                        .build();

        WorkManager.getInstance(this).enqueue(testWorkRequest);


        //meghívás appon kívűl
        //scheduleDailyReminder();



        //engedélyezés
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 100);
        }

        //appon belül
        if (!hasPopupBeenShown) {
            showWaterReminderPopup();
            hasPopupBeenShown= true;
        }

    }


    //üzentek kezelése appon belül
    private void showWaterReminderPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.popup_message, null);
        builder.setView(view);

        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
     //FONTOOOOOOOOOS rész
    //üzenetek kezelése appon kívűl
    /*private void scheduleDailyReminder() {
        Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build();

        PeriodicWorkRequest reminderRequest =
                new PeriodicWorkRequest.Builder(ReminderWorker.class, 24, TimeUnit.HOURS)
                        .setInitialDelay(15, TimeUnit.MINUTES) //15 perc a legkisebb idő amire állítani lehet!!!!
                        .setConstraints(constraints)
                        .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "DailyWaterReminder",
                ExistingPeriodicWorkPolicy.KEEP,
                reminderRequest


        );
    }
    */


}