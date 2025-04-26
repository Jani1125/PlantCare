package hu.nje.plantcare.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
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
import hu.nje.plantcare.api.ApiService;
import hu.nje.plantcare.database.PlantDao;
import hu.nje.plantcare.database.PlantDatabase;
import hu.nje.plantcare.database.entity.BasicPlant;
import hu.nje.plantcare.database.entity.Plant;

public class SearchFragment extends Fragment {

    private RecyclerView menuRecyclerView;
    private MenuAdapter menuAdapter;
    private List<String> menuItems;
    private LinearLayout menuLayout;

    private ImageView profileImageView;
    private TextView data;
    private TextView infoText;
    private StringBuilder details = new StringBuilder();

    private static final String API_KEY = "sk-MzET67d004cc29a259082";
    private static final String baseUrl = "https://perenual.com/api/v2/species/details/";

    public SearchFragment() {
        // Required empty public constructor
    }

    private RecyclerView recyclerView;

    private BasicPlantAdapter basic_adapter;
    private List<BasicPlant> plantList = new ArrayList<>();

    private PlantDetailAdapter detail_adapter;
    private Plant plant;


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
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        basic_adapter = new BasicPlantAdapter(plantList, new BasicPlantAdapter.OnPlantClickListener() {
            @Override
            public void onPlantClick(int plantId) {
                //Log.d("SearchFragment", "Clicked plant ID: " + plantId);
                getDetailsWithApi(plantId);
            }
        });
        recyclerView.setAdapter(basic_adapter);


        /// ///////////////////////////////////////////////////////////////////////////////////
        /// Setting up the keyword searching field
        ///This search field based on searching with keyword with an api call, and shows the result

        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!query.isEmpty())
                {
                    searchWithApi(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Opcionális: valós idejű keresés
                return false;
            }
        });


        /// ///////////////////////////////////////////////////////////////////////////////////////

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

    /// ///////////////////////////////////////////////////////////
    /// This function create an api call with a keyword, based on what word the user write in.
    private void searchWithApi(String keyword) {
        ApiService.SearchApiRequest(requireContext(), keyword, API_KEY, "https://perenual.com/api/v2/species-list?", results -> {
            plantList.clear();
            plantList.addAll(results);
            basic_adapter.setPlants(plantList);

        });
    }
    /// ////////////////////////////////////////////////////////////
    /// This function will make the details of the selected item
    ///

    private void getDetailsWithApi(int selectedItemIndex)
    {
        System.out.println("Meg lett nyomva egy növény");

        PlantDatabase db = PlantDatabase.getDatabase(requireContext());
        PlantDao plantDao = db.plantDao();


        new Thread(()->{


            Plant existingPlant=plantDao.getPlant(selectedItemIndex);
            System.out.println("existingPlante eredménye: "+existingPlant);

            /// Az API hívások lecsökkentését elérve, ha a keresett növény már szerepel a kedvencek listában,
            /// úgy az elmentett növény az adatbázisból lesz betöltve, ha pedig nincs benne, úgy beletöltődik

        requireActivity().runOnUiThread(()->{
            if(existingPlant==null)
            {
                System.out.println("A növény részletes nézete API hivás által lett betöltve");

                ApiService.ApiRequest(requireContext(),selectedItemIndex,API_KEY,result ->{
                    detail_adapter = new PlantDetailAdapter(result, new PlantDetailAdapter.OnFavoriteClickListener() {

                        ////Click eseményre a kedvencekhez kerül a kiválasztott növény
                        /// Ha még nem kedvenc, a kedvencek közé kerül, ha pedig már kedvenc úgy kikerül a kedvencek közül
                        @Override
                        public void OnFavoriteClick() {

                            System.out.println("Meg lett nyomva a favorite switch");

                            //result.setFavorite(!result.isFavorite);
                            if(!result.isFavorite)
                            {// Az adatokat egy új szálban mentjük el az adatbázisba
                                new Thread(() -> {
                                    plantDao.insert(
                                            new Plant(
                                                    result.plantId,
                                                    result.commonName,
                                                    result.scientificName,
                                                    result.type,
                                                    result.cycle,
                                                    result.watering,
                                                    result.imgUrl,
                                                    result.description,
                                                    !result.isFavorite
                                            ));
                                }).start();
                            }else
                            {
                                new Thread(() -> {
                                    plantDao.deleteOne(result.getId());
                                }).start();
                            }



                        }
                    });
                    detail_adapter.setPlant(result);
                    recyclerView.setAdapter(detail_adapter);
                });
            }else
            {
                System.out.println("A növény részletes nézete az ADATBÁZISBÓL lett betöltve");
                detail_adapter = new PlantDetailAdapter(existingPlant, new PlantDetailAdapter.OnFavoriteClickListener() {

                    ////Click eseményre a kedvencekhez kerül a kiválasztott növény
                    /// Ha még nem kedvenc, a kedvencek közé kerül, ha pedig már kedvenc úgy kikerül a kedvencek közül
                    @Override
                    public void OnFavoriteClick() {

                        System.out.println("Meg lett nyomva a favorite switch");

                        //result.setFavorite(!result.isFavorite);
                        if(!existingPlant.isFavorite)
                        {// Az adatokat egy új szálban mentjük el az adatbázisba
                            new Thread(() -> {
                                plantDao.insert(
                                        new Plant(
                                                existingPlant.plantId,
                                                existingPlant.commonName,
                                                existingPlant.scientificName,
                                                existingPlant.type,
                                                existingPlant.cycle,
                                                existingPlant.watering,
                                                existingPlant.imgUrl,
                                                existingPlant.description,
                                                !existingPlant.isFavorite
                                        ));
                            }).start();
                        }else
                        {
                            new Thread(() -> {
                                plantDao.deleteOne(existingPlant.getId());
                            }).start();
                        }



                    }
                });

                detail_adapter.setPlant(existingPlant);
                recyclerView.setAdapter(detail_adapter);
            }

            System.out.println("Meg lett hívva a getDetailsWithApi függvény");
        });


        }).start();


    }

    /// ////////////////////////////////////////////////////////////////

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
