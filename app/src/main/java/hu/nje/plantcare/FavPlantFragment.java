package hu.nje.plantcare;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


import hu.nje.plantcare.adapters.FavAdapter;
import hu.nje.plantcare.database.Plant;

public class FavPlantFragment extends Fragment {

    private RecyclerView recyclerView;
    private FavAdapter adapter;
    private List<Plant> plantList;

    public FavPlantFragment() {
        this.plantList = plantList;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fav_plant, container, false);

        recyclerView = view.findViewById(R.id.favrecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Adapter beállítása
        adapter = new FavAdapter(plantList);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
