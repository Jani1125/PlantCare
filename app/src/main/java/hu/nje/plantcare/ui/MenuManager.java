package hu.nje.plantcare.ui;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import hu.nje.plantcare.MainActivity;
import hu.nje.plantcare.R;
import hu.nje.plantcare.SplashActivity;
import hu.nje.plantcare.adapters.MenuAdapter;

public class MenuManager {

    public static void setupMenu(AppCompatActivity activity, List<String> menuItems,
                                 RecyclerView menuRecyclerView, ImageView hamMenuIcon,
                                 ImageView profileImageView) {

        MenuAdapter menuAdapter = new MenuAdapter(activity, menuItems, item -> {
            if ("Home".equals(item)) {
                Intent intent = new Intent(activity, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(intent);
            } else if ("Search".equals(item)) {
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new SearchFragment())
                        .addToBackStack(null)
                        .commit();
            } else if ("Favourite plants".equals(item)) {
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new FavPlantFragment())
                        .addToBackStack(null)
                        .commit();
            } else if ("Own plants".equals(item)) {
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new OwnPlantFragment())
                        .addToBackStack(null)
                        .commit();
            } else if ("Plant details".equals(item)) {
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new PlantDetailsFragment())
                        .addToBackStack(null)
                        .commit();
            } else if ("Settings".equals(item)) {
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new SettingsFragment())
                        .addToBackStack(null)
                        .commit();
            } else if ("Notifications".equals(item)) {
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new MessagesFragment())
                    .addToBackStack(null)
                    .commit();
        }

            //Plant scanne maid ide
            menuRecyclerView.setVisibility(View.GONE);
        });

        menuRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        menuRecyclerView.setAdapter(menuAdapter);

        hamMenuIcon.setOnClickListener(v -> {
            if (menuRecyclerView.getVisibility() == View.GONE) {
                menuRecyclerView.setVisibility(View.VISIBLE);
            } else {
                menuRecyclerView.setVisibility(View.GONE);
            }
        });

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(activity);
        if (account != null && account.getPhotoUrl() != null) {
            Glide.with(activity)
                    .load(account.getPhotoUrl())
                    .circleCrop()
                    .into(profileImageView);
        }

        profileImageView.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(activity, view);
            popup.getMenuInflater().inflate(R.menu.profile_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_sign_out) {
                    signOut(activity);
                    return true;
                }
                return false;
            });
            popup.show();
        });
    }

    private static void signOut(AppCompatActivity activity) {
        FirebaseAuth.getInstance().signOut();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(activity,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build());

        googleSignInClient.signOut().addOnCompleteListener(activity, task -> {
            Intent intent = new Intent(activity, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intent);
            activity.finish();
        });
    }
}
