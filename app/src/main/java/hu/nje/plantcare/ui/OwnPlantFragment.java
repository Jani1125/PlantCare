package hu.nje.plantcare.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import hu.nje.plantcare.R;
import hu.nje.plantcare.adapters.OwnPlantAdapter;
import hu.nje.plantcare.database.OwnPlantDao;
import hu.nje.plantcare.database.PlantDatabase;
import hu.nje.plantcare.database.entity.OwnPlant;

public class OwnPlantFragment extends Fragment {

    private List<String> menuItems;

    public OwnPlantFragment() {
        // Required empty public constructor
    }

    private PlantDatabase db;
    private OwnPlantDao ownPlantDao;

    private RecyclerView recyclerView;
    private OwnPlantAdapter own_adapter;
    private List<OwnPlant> ownPlantList;
    private Button newPlantButton;

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
        ownPlantDao = db.ownPlantDao();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_own_plant, container, false);


        newPlantButton = view.findViewById(R.id.btn_addPlant);
        newPlantButton.setOnClickListener(v ->{
            //Itt lesz a FormAdapter meghívva
            // NewPlantAdapter
            // newPlant_adapter = new NewPlantAdapter();
        });

        recyclerView = view.findViewById(R.id.ownplantrecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        own_adapter = new OwnPlantAdapter(ownPlantList, new OwnPlantAdapter.OnDeleteClickListener() {
            @Override
            public void OnDeleteClick(int ownPlantId) {
                DeletePlantFromDb(ownPlantId);
            }
        });
        recyclerView.setAdapter(own_adapter);
        GetAllOwnPlantFromDb();

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

    public void DeletePlantFromDb(int id)
    {
        new Thread(()->{
            requireActivity().runOnUiThread(()-> {
                ownPlantDao.deletePlant(id);
            });
        }).start();
    }
    public void GetAllOwnPlantFromDb() {
        new Thread(() -> {
            ownPlantList = ownPlantDao.getAllOwnPlants();  // Run in background
            requireActivity().runOnUiThread(() -> {
                own_adapter.setOwnPlants(ownPlantList);     // Update UI
            });
        }).start();
    }

}
