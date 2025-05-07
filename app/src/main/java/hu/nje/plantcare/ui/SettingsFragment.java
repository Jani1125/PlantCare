package hu.nje.plantcare.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import hu.nje.plantcare.R;
import hu.nje.plantcare.database.OwnPlantDao;
import hu.nje.plantcare.database.PlantDatabase;
import hu.nje.plantcare.database.entity.OwnPlant;
import hu.nje.plantcare.utils.NotificationScheduler;

public class SettingsFragment extends Fragment {

    private List<String> menuItems;
    private static final String PREF_DARK_MODE = "dark_mode";
    private static final String PREF_NOTIFICATIONS_ENABLED = "notifications_enabled";
    private Switch reminderSwitch;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        menuItems = new ArrayList<>();
        menuItems.add("Home");
        menuItems.add("Search");
        menuItems.add("Favourite plants");
        menuItems.add("Own plants");
        menuItems.add("Plant scanner");
        menuItems.add("Settings");
        menuItems.add("Notifications");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Menü kezelés közös osztállyal
        MenuManager.setupMenu(
                (AppCompatActivity) requireActivity(),
                menuItems,
                view.findViewById(R.id.menuRecyclerView),
                view.findViewById(R.id.hamMenuIcon),
                view.findViewById(R.id.profileImageView)
        );

        Switch themeSwitch = view.findViewById(R.id.switch3);
        boolean isDarkMode = PreferenceManager.getDefaultSharedPreferences(requireContext())
                .getBoolean(PREF_DARK_MODE, false);
        themeSwitch.setChecked(isDarkMode);

        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PreferenceManager.getDefaultSharedPreferences(requireContext()).edit()
                    .putBoolean(PREF_DARK_MODE, isChecked)
                    .apply();

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        reminderSwitch = view.findViewById(R.id.switch2);
        boolean areNotificationsEnabled = PreferenceManager.getDefaultSharedPreferences(requireContext())
                .getBoolean(PREF_NOTIFICATIONS_ENABLED, true); // Alapértelmezettként engedélyezve
        reminderSwitch.setChecked(areNotificationsEnabled);

        reminderSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PreferenceManager.getDefaultSharedPreferences(requireContext()).edit()
                    .putBoolean(PREF_NOTIFICATIONS_ENABLED, isChecked)
                    .apply();

            PlantDatabase db = PlantDatabase.getDatabase(requireContext());
            OwnPlantDao ownPlantDao = db.ownPlantDao();

            if (!isChecked) {
                // Ha kikapcsoljuk az értesítéseket, töröljük a meglévőket
                new Thread(() -> {
                    List<Integer> allPlantIds = ownPlantDao.getAllOwnPlantIds();
                    for (int plantId : allPlantIds) {
                        NotificationScheduler.cancelWatering(requireContext(), plantId);
                    }
                }).start();
            } else {
                // Ha bekapcsoljuk az értesítéseket, újraütemezzük a meglévő növények értesítéseit
                new Thread(() -> {
                    List<OwnPlant> allOwnPlants = ownPlantDao.getAllOwnPlants();
                    for (OwnPlant plant : allOwnPlants) {
                        NotificationScheduler.scheduleWatering(
                                requireContext(),
                                plant.getId(),
                                plant.getCommonName(),
                                plant.getWatering(),
                                plant.getImgUrl()
                        );
                    }
                }).start();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Beállítjuk a téma és az értesítések állapotát a switcheknek, amikor a fragment újra láthatóvá válik
        Switch themeSwitch = requireView().findViewById(R.id.switch3);
        boolean isDarkMode = PreferenceManager.getDefaultSharedPreferences(requireContext())
                .getBoolean(PREF_DARK_MODE, false);
        themeSwitch.setChecked(isDarkMode);

        reminderSwitch = requireView().findViewById(R.id.switch2);
        boolean areNotificationsEnabled = PreferenceManager.getDefaultSharedPreferences(requireContext())
                .getBoolean(PREF_NOTIFICATIONS_ENABLED, true);
        reminderSwitch.setChecked(areNotificationsEnabled);
    }
}