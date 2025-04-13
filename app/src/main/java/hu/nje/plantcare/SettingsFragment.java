package hu.nje.plantcare;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import androidx.fragment.app.Fragment;
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

public class SettingsFragment extends Fragment {

    private RecyclerView menuRecyclerView;
    private MenuAdapter menuAdapter;
    private List<String> menuItems;
    private LinearLayout menuLayout;

    private ImageView profileImageView;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        menuRecyclerView = view.findViewById(R.id.menuRecyclerView);
        menuRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        menuAdapter = new MenuAdapter(getContext(), menuItems, item -> {
            if ("Home".equals(item)) {
                // Navigálás a főoldalra
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else if ("Search".equals(item)) {
                // Settings fragment betöltése
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new SearchFragment())
                        .addToBackStack(null)
                        .commit();
            }else if ("Favourite plants".equals(item)) {
                // Settings fragment betöltése
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new FavPlantFragment())
                        .addToBackStack(null)
                        .commit();
            } else if ("Own plants".equals(item)) {
                // Settings fragment betöltése
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new OwnPlantFragment())
                        .addToBackStack(null)
                        .commit();
            } /*else if ("Plant scanner".equals(item)) {
                // Settings fragment betöltése
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new PlantScannerFragment())
                        .addToBackStack(null)
                        .commit();
            }*/else if ("Settings".equals(item)) {
                // Settings fragment betöltése
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new SettingsFragment())
                        .addToBackStack(null)
                        .commit();
            }
            menuRecyclerView.setVisibility(View.GONE);  // Menü elrejtése
        });

        menuRecyclerView.setAdapter(menuAdapter);

        // Hamburger menü ikon kattintás kezelése
        ImageView hamMenuIcon = view.findViewById(R.id.hamMenuIcon);
        hamMenuIcon.setOnClickListener(v -> {
            if (menuRecyclerView.getVisibility() == View.GONE) {
                menuRecyclerView.setVisibility(View.VISIBLE);
            } else {
                menuRecyclerView.setVisibility(View.GONE);
            }
        });

        // Google profilkép betöltése
        profileImageView = view.findViewById(R.id.profileImageView);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (account != null && account.getPhotoUrl() != null) {
            Uri photoUri = account.getPhotoUrl();
            Glide.with(getActivity())
                    .load(photoUri)
                    .circleCrop()
                    .into(profileImageView);
        }

        // Profilkép kattintás esemény kezelése
        profileImageView.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(getActivity(), v);
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

        return view;
    }

    // Kijelentkezés logika
    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getActivity(),
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build());

        googleSignInClient.signOut().addOnCompleteListener(getActivity(), task -> {
            Intent intent = new Intent(getActivity(), SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        });
    }
}
