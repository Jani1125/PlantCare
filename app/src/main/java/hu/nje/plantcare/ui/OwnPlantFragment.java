package hu.nje.plantcare.ui;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import hu.nje.plantcare.R;
import hu.nje.plantcare.adapters.NewPlantAdapter;
import hu.nje.plantcare.adapters.OwnPlantAdapter;
import hu.nje.plantcare.adapters.OwnPlantDetailsAdapter;
import hu.nje.plantcare.database.OwnPlantDao;
import hu.nje.plantcare.database.PlantDatabase;
import hu.nje.plantcare.database.entity.OwnPlant;
import hu.nje.plantcare.utils.NotificationScheduler;

public class OwnPlantFragment extends Fragment {

    private List<String> menuItems;
    private PlantDatabase db;
    private OwnPlantDao ownPlantDao;
    private RecyclerView recyclerView;
    private OwnPlantAdapter own_adapter;
    private OwnPlantDetailsAdapter own_details_adapter;
    private NewPlantAdapter newPlant_adapter;
    private List<OwnPlant> ownPlantList = new ArrayList<>();
    private OwnPlant ownPlant;
    private Button newPlantButton;
    private Button backButton;
    private Button createButton;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri selectedImageUri;
    private NewPlantAdapter.OnImageActionListener imageActionListener;
    private static final String PREF_NOTIFICATIONS_ENABLED = "notifications_enabled";

    public OwnPlantFragment() {
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
        ownPlantDao = db.ownPlantDao();

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        requireContext().getContentResolver().takePersistableUriPermission(
                                uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        );
                        selectedImageUri = uri;
                        if (imageActionListener != null) {
                            imageActionListener.onImageSelected(uri.toString());
                        }
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_own_plant, container, false);

        newPlantButton = view.findViewById(R.id.btn_addPlant);
        backButton = view.findViewById(R.id.btn_Back);
        createButton = view.findViewById(R.id.btn_Create);
        recyclerView = view.findViewById(R.id.ownplantrecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        own_adapter = new OwnPlantAdapter(ownPlantList, new OwnPlantAdapter.OnDeleteClickListener() {
            @Override
            public void OnDeleteClick(int ownPlantId) {
                NotificationScheduler.cancelWatering(requireContext(), ownPlantId);
                DeletePlantFromDb(ownPlantId);
            }
        }, new OwnPlantAdapter.OnOwnPlantClickListener() {
            @Override
            public void onOwnPlantClick(int plantId) {
                GetOwnPlantDetailsFromDb(plantId);
            }
        });
        recyclerView.setAdapter(own_adapter);
        GetAllOwnPlantFromDb();

        newPlantButton.setOnClickListener(v -> {
            imageActionListener = new NewPlantAdapter.OnImageActionListener() {
                @Override
                public void onSetImageClick() {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.setType("image/*");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                    imagePickerLauncher.launch(intent);
                }

                @Override
                public void onImageSelected(String imagePath) {
                    newPlant_adapter.setImagePath(imagePath);
                }
            };

            newPlant_adapter = new NewPlantAdapter(imageActionListener);
            recyclerView.setAdapter(newPlant_adapter);
            newPlantButton.setVisibility(GONE);
            backButton.setVisibility(VISIBLE);
            createButton.setVisibility(VISIBLE);
        });

        backButton.setOnClickListener(v -> {
            recyclerView.setAdapter(own_adapter);
            newPlantButton.setVisibility(VISIBLE);
            backButton.setVisibility(GONE);
            createButton.setVisibility(GONE);
        });

        createButton.setOnClickListener(v -> {
            if (newPlant_adapter != null && newPlant_adapter.isFormValid()) {
                OwnPlant plant = newPlant_adapter.setPlantData();
                Toast.makeText(getContext(), "All fields are valid", Toast.LENGTH_SHORT).show();
                new Thread(() -> {
                    long insertedId = ownPlantDao.insertOwnPlant(plant);
                    if (insertedId > 0) {
                        boolean areNotificationsEnabled = PreferenceManager.getDefaultSharedPreferences(requireContext())
                                .getBoolean(PREF_NOTIFICATIONS_ENABLED, true);
                        if (areNotificationsEnabled) {
                            NotificationScheduler.scheduleWatering(
                                    requireContext(),
                                    (int) insertedId,
                                    plant.getCommonName(),
                                    plant.getWatering(),
                                    plant.getImgUrl()
                            );
                        } else {
                            Log.i("OwnPlantFragment", "Értesítések le vannak tiltva, nem ütemezünk a(z) " + plant.getCommonName() + " nevű növényhez.");
                        }
                    }
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

        MenuManager.setupMenu(
                (AppCompatActivity) requireActivity(),
                menuItems,
                view.findViewById(R.id.menuRecyclerView),
                view.findViewById(R.id.hamMenuIcon),
                view.findViewById(R.id.profileImageView)
        );

        return view;
    }

    private void DeletePlantFromDb(int id) {
        new Thread(() -> {
            ownPlantDao.deletePlant(id);
            ownPlantList = ownPlantDao.getAllOwnPlants();
            requireActivity().runOnUiThread(() -> {
                own_adapter.setOwnPlants(ownPlantList);
            });
        }).start();
    }

    private void GetAllOwnPlantFromDb() {
        new Thread(() -> {
            ownPlantList = ownPlantDao.getAllOwnPlants();
            requireActivity().runOnUiThread(() -> {
                own_adapter.setOwnPlants(ownPlantList);
            });
        }).start();
    }

    private void GetOwnPlantDetailsFromDb(int selectedItemId) {
        System.out.println("Meg lett nyomva a saját növény, a hozzá tartozó id: " + selectedItemId);
        ownPlantDao = db.ownPlantDao();

        new Thread(() -> {
            ownPlant = ownPlantDao.getOwnPlant(selectedItemId);
            System.out.println("A megnyomott növény neve: " + ownPlant.getCommonName());
            if (ownPlant != null) {
                requireActivity().runOnUiThread(() -> {
                    own_details_adapter = new OwnPlantDetailsAdapter(ownPlant, new OwnPlantDetailsAdapter.OnBackClickListener() {
                        @Override
                        public void OnBackClick() {
                            recyclerView.setAdapter(own_adapter);
                        }
                    });
                    own_details_adapter.setOwnPlant(ownPlant);
                    recyclerView.setAdapter(own_details_adapter);
                });
            }
        }).start();
    }
}