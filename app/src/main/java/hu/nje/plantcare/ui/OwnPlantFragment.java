package hu.nje.plantcare.ui;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import hu.nje.plantcare.R;
import hu.nje.plantcare.adapters.NewPlantAdapter;
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
    private NewPlantAdapter newPlant_adapter;
    private List<OwnPlant> ownPlantList = new ArrayList<>();
    private Button newPlantButton;
    private Button backButton;
    private Button createButton;

    /// Set Image/////////////////////
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri selectedImageUri;
    private NewPlantAdapter.OnImageActionListener imageActionListener;
    /// ////////////////////////////


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


        /// Set Image/////////////////////

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (imageActionListener != null) {
                            imageActionListener.onImageSelected(selectedImageUri.toString()); // Pass to adapter
                        }
                    }
                }
        );


        /// ////////////////////////////
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_own_plant, container, false);


        newPlantButton = view.findViewById(R.id.btn_addPlant);
        backButton = view.findViewById(R.id.btn_Back);
        createButton = view.findViewById(R.id.btn_Create);


        newPlantButton.setOnClickListener(v ->{

            imageActionListener = new NewPlantAdapter.OnImageActionListener() {
                @Override
                public void onSetImageClick() {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    imagePickerLauncher.launch(Intent.createChooser(intent, "Select Picture"));
                }

                @Override
                public void onImageSelected(String imagePath) {
                    // You can also pass this to the adapter or model
                    newPlant_adapter.setImagePath(imagePath);
                }
            };

            newPlant_adapter = new NewPlantAdapter(imageActionListener);


            recyclerView.setAdapter(newPlant_adapter);



            newPlantButton.setVisibility(GONE);
            backButton.setVisibility(VISIBLE);
            createButton.setVisibility(VISIBLE);
            }
        );

        backButton.setOnClickListener(v->{
            recyclerView.setAdapter(own_adapter);
            newPlantButton.setVisibility(VISIBLE);
            backButton.setVisibility(GONE);
            createButton.setVisibility(GONE);
        });


        createButton.setOnClickListener(v->{

            if (newPlant_adapter != null && newPlant_adapter.isFormValid()) {
                OwnPlant plant = newPlant_adapter.setPlantData();
                Toast.makeText(getContext(), "All fields are valid", Toast.LENGTH_SHORT).show();
                new Thread(() -> {
                    ownPlantDao.insertOwnPlant(plant);
                    ownPlantList.clear();
                    GetAllOwnPlantFromDb();

                    requireActivity().runOnUiThread(() -> {
                        own_adapter.setOwnPlants(ownPlantList);
                        newPlantButton.setVisibility(VISIBLE);
                        backButton.setVisibility(GONE);
                        createButton.setVisibility(GONE);
                        recyclerView.setAdapter(own_adapter);
                    });
                }).start();

            } else {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            }

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
        new Thread(() -> {
            ownPlantDao.deletePlant(id);
            ownPlantList = ownPlantDao.getAllOwnPlants();
            requireActivity().runOnUiThread(() -> {
                own_adapter.setOwnPlants(ownPlantList);
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
