package hu.nje.plantcare.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import hu.nje.plantcare.R;
import hu.nje.plantcare.adapters.NotificationAdapter;
import hu.nje.plantcare.database.PlantDatabase;
import hu.nje.plantcare.database.entity.Notification;

public class MessagesFragment extends Fragment {

    private List<String> menuItems;
    private RecyclerView notificationsRecyclerView;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList = new ArrayList<>();
    private PlantDatabase db;

    public MessagesFragment() {
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

        db = PlantDatabase.getDatabase(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        notificationsRecyclerView = view.findViewById(R.id.plantdetailsrecyclerView); // Feltételezem, hogy ez a RecyclerView ID-ja a fragment_messages.xml-ben
        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationAdapter = new NotificationAdapter(notificationList);
        notificationsRecyclerView.setAdapter(notificationAdapter);

        loadNotifications();

        // Itt hívjuk meg a közös menü beállítást
        MenuManager.setupMenu(
                (AppCompatActivity) requireActivity(),
                menuItems,
                view.findViewById(R.id.menuRecyclerView),
                view.findViewById(R.id.hamMenuIcon),
                view.findViewById(R.id.profileImageView)
        );

        return view;
    }

    private void loadNotifications() {
        new Thread(() -> {
            List<Notification> notifications = db.notificationDao().getAllNotifications();
            requireActivity().runOnUiThread(() -> {
                notificationList.clear();
                notificationList.addAll(notifications);
                notificationAdapter.notifyDataSetChanged();
            });
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadNotifications(); // Frissítjük a listát, ha a fragment újra láthatóvá válik
    }
}