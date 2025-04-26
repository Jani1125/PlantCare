package hu.nje.plantcare.ui;

import android.os.Bundle;
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
import hu.nje.plantcare.api.ApiService;
import hu.nje.plantcare.database.entity.BasicPlant;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private BasicPlantAdapter adapter;
    private List<BasicPlant> plantList = new ArrayList<>();

    private List<String> menuItems;

    private static final String API_KEY = "sk-MzET67d004cc29a259082";
    private static final String baseUrl = "https://perenual.com/api/v2/species/details/";

    public SearchFragment() {
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BasicPlantAdapter(plantList);
        recyclerView.setAdapter(adapter);

        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchWithApi(query);
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
            adapter.setPlants(plantList);
        });
    }
}
