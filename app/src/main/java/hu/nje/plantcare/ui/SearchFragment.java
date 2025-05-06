package hu.nje.plantcare.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


import hu.nje.plantcare.R;
import hu.nje.plantcare.adapters.BasicPlantAdapter;
import hu.nje.plantcare.adapters.MenuAdapter;
import hu.nje.plantcare.adapters.PlantDetailAdapter;
import hu.nje.plantcare.api.ApiService;
import hu.nje.plantcare.database.PlantDao;
import hu.nje.plantcare.database.PlantDatabase;
import hu.nje.plantcare.database.entity.BasicPlant;
import hu.nje.plantcare.database.entity.Plant;

public class SearchFragment extends Fragment {

    

    private List<String> menuItems;

    private static final String API_KEY = "sk-MzET67d004cc29a259082";


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

        /*PlantDatabase db = PlantDatabase.getDatabase(requireContext());
        PlantDao plantDao = db.plantDao();

        new Thread(() -> {
            plantDao.deleteAll();
        }).start();
        */


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
                return false;
            }
        });

        // Menü kezelés közös osztállyal
        MenuManager.setupMenu(
                (AppCompatActivity) requireActivity(),
                menuItems,
                view.findViewById(R.id.menuRecyclerView),
                view.findViewById(R.id.hamMenuIcon),
                view.findViewById(R.id.profileImageView)
        );

        return view;
    }

    // API keresés logika
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


            plant=plantDao.getPlant(selectedItemIndex);
            System.out.println("existingPlante eredménye: "+plant);

            /// Az API hívások lecsökkentését elérve, ha a keresett növény már szerepel a kedvencek listában,
            /// úgy az elmentett növény az adatbázisból lesz betöltve, ha pedig nincs benne, úgy beletöltődik

        requireActivity().runOnUiThread(()->{
            if(plant==null)
            {
                System.out.println("A növény részletes nézete API hivás által lett betöltve");

                ApiService.ApiRequest(requireContext(),selectedItemIndex,API_KEY,result ->{
                    detail_adapter = new PlantDetailAdapter(result, new PlantDetailAdapter.OnFavoriteClickListener() {

                        /// /Click eseményre a kedvencekhez kerül a kiválasztott növény
                        /// Ha még nem kedvenc, a kedvencek közé kerül, ha pedig már kedvenc úgy kikerül a kedvencek közül
                        @Override
                        public void OnFavoriteClick() {

                            System.out.println("Meg lett nyomva a favorite switch");

                            //result.setFavorite(!result.isFavorite);
                            if(result!=null) insertIntoDb(result);


                        }
                    }, new PlantDetailAdapter.OnBackClickListener() {
                        @Override
                        public void OnBackClick() {
                            recyclerView.setAdapter(basic_adapter);
                        }
                    });
                    detail_adapter.setPlant(result);
                    recyclerView.setAdapter(detail_adapter);
                });
            }else
            {
                System.out.println("A növény részletes nézete az ADATBÁZISBÓL lett betöltve");
                detail_adapter = new PlantDetailAdapter(plant, new PlantDetailAdapter.OnFavoriteClickListener() {

                    /// /Click eseményre a kedvencekhez kerül a kiválasztott növény
                    /// Ha még nem kedvenc, a kedvencek közé kerül, ha pedig már kedvenc úgy kikerül a kedvencek közül
                    @Override
                    public void OnFavoriteClick() {

                        System.out.println("Meg lett nyomva a favorite switch");

                        //result.setFavorite(!result.isFavorite);
                        if(plant!=null) insertIntoDb(plant);


                    }
                }, new PlantDetailAdapter.OnBackClickListener() {
                    @Override
                    public void OnBackClick() {
                        recyclerView.setAdapter(basic_adapter);
                    }
                });

                detail_adapter.setPlant(plant);
                recyclerView.setAdapter(detail_adapter);
            }

            System.out.println("Meg lett hívva a getDetailsWithApi függvény");
        });


        }).start();


    }

    private void insertIntoDb(Plant p)
    {
        PlantDatabase db = PlantDatabase.getDatabase(requireContext());
        PlantDao plantDao = db.plantDao();

        if (!p.isFavorite()) {// Az adatokat egy új szálban mentjük el az adatbázisba
            new Thread(() -> {
                plantDao.insert(
                        new Plant(
                                p.getPlantId(),
                                p.getCommonName(),
                                p.getScientificName(),
                                p.getType(),
                                p.getCycle(),
                                p.getWatering(),
                                p.getImgUrl(),
                                p.getDescription(),
                                true
                        ));
            }).start();
        } else {
            new Thread(() -> {
                plantDao.deleteOne(p.getId());
            }).start();
        }
    }


}
