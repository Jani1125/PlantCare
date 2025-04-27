package hu.nje.plantcare.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;


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

    private List<String> menuItems;

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
        menuItems.add("Notifications");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
