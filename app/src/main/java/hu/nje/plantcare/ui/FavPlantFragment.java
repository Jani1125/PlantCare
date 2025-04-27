package hu.nje.plantcare.ui;

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

import hu.nje.plantcare.MainActivity;
import hu.nje.plantcare.R;
import hu.nje.plantcare.SplashActivity;
import hu.nje.plantcare.adapters.BasicPlantAdapter;
import hu.nje.plantcare.adapters.MenuAdapter;
import hu.nje.plantcare.adapters.PlantDetailAdapter;
import hu.nje.plantcare.database.PlantDao;
import hu.nje.plantcare.database.PlantDatabase;
import hu.nje.plantcare.database.entity.BasicPlant;
import hu.nje.plantcare.database.entity.Plant;

public class FavPlantFragment extends Fragment {

    private RecyclerView menuRecyclerView;
    private MenuAdapter menuAdapter;
    private List<String> menuItems;
    private LinearLayout menuLayout;

    private ImageView profileImageView;

    public FavPlantFragment() {
        // Required empty public constructor
    }

    private RecyclerView favrecyclerView;

    private BasicPlantAdapter basic_adapter;
    private List<BasicPlant> plantList = new ArrayList<>();

    private PlantDetailAdapter detail_adapter;
    private List<Plant>detailed_plantList = new ArrayList<>();
    private Plant plant;

    private PlantDatabase db;
    private PlantDao plantDao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = PlantDatabase.getDatabase(requireContext());
        plantDao = db.plantDao();

        getItemsFromDb();

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
        View view = inflater.inflate(R.layout.fragment_fav_plant, container, false);

        favrecyclerView = view.findViewById(R.id.favrecyclerView);
        favrecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        basic_adapter = new BasicPlantAdapter(plantList, new BasicPlantAdapter.OnPlantClickListener() {
            @Override
            public void onPlantClick(int plantId) {

                getDetailsFromDb(plantId);
            }
        });
        favrecyclerView.setAdapter(basic_adapter);

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

    private void getItemsFromDb()
    {

        new Thread(() -> {
            try {
                detailed_plantList = plantDao.getAllPlants();
                for (Plant item : detailed_plantList) {
                    BasicPlant convert = new BasicPlant(item.id, item.commonName, item.scientificName, item.imgUrl);
                    plantList.add(convert);
                }
                requireActivity().runOnUiThread(() -> basic_adapter.notifyDataSetChanged());
            } catch (Exception ignored) {}
        }).start();

    }

    private void getDetailsFromDb(int selectedItemId)
    {
        getItemsFromDb();
        plantDao = db.plantDao();
        System.out.println("Meg lett nyomva a favorite item");
        System.out.println("A kapott azonosító: "+selectedItemId);
        new Thread(()->{
            System.out.println("A szálon belül a plant értéke az adatbázis által: "+plant);


            for (Plant p : detailed_plantList)
            {
                if(p.id==selectedItemId)
                {
                    plant=p;
                }
            }
            if(plant!=null)
            {
                System.out.println("A DebugPlant által felvett értékek:");
                System.out.println("Id: "+plant.plantId);
                System.out.println("Common_name: "+plant.commonName);
                System.out.println("Scientific_name: "+plant.scientificName);
            }else
            {
                System.out.println("A Debug plant null értéken maradt");
            }



            requireActivity().runOnUiThread(()-> {
                detail_adapter = new PlantDetailAdapter(plant, new PlantDetailAdapter.OnFavoriteClickListener() {

                    /// /Click eseményre a kedvencekhez kerül a kiválasztott növény
                    /// Ha még nem kedvenc, a kedvencek közé kerül, ha pedig már kedvenc úgy kikerül a kedvencek közül
                    @Override
                    public void OnFavoriteClick() {

                        System.out.println("Meg lett nyomva a favorite switch");

                        //result.setFavorite(!result.isFavorite);
                        if (!plant.isFavorite) {// Az adatokat egy új szálban mentjük el az adatbázisba
                            new Thread(() -> {
                                plantDao.insert(
                                        new Plant(
                                                plant.plantId,
                                                plant.commonName,
                                                plant.scientificName,
                                                plant.type,
                                                plant.cycle,
                                                plant.watering,
                                                plant.imgUrl,
                                                plant.description,
                                                true
                                        ));
                            }).start();
                        } else {
                            new Thread(() -> {
                                plantDao.deleteOne(plant.getPlantId());
                            }).start();
                        }


                    }
                }, new PlantDetailAdapter.OnBackClickListener() {
                    @Override
                    public void OnBackClick() {
                        plantList.clear();
                        detailed_plantList.clear();
                        getItemsFromDb();
                        favrecyclerView.setAdapter(basic_adapter);
                    }
                });

                detail_adapter.setPlant(plant);
                favrecyclerView.setAdapter(detail_adapter);
            });

        }).start();

    }


}