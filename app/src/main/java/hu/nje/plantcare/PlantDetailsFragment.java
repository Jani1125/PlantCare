package hu.nje.plantcare;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import hu.nje.plantcare.database.Plant;

public class PlantDetailsFragment extends Fragment {

    private static final String ARG_PLANT = "plant";

    private Plant plant;

    private TextView commonNameTextView;
    private TextView scientificNameTextView;
    private TextView typeTextView;
    private TextView cycleTextView;
    private TextView wateringTextView;

    public PlantDetailsFragment() {
        // Required empty public constructor
    }

    public static PlantDetailsFragment newInstance(Plant plant) {
        PlantDetailsFragment fragment = new PlantDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PLANT, (Parcelable) plant);  // Növény adatok átadása
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_plantdetails, container, false);

        // UI elemek inicializálása
        commonNameTextView = rootView.findViewById(R.id.common_name);
        scientificNameTextView = rootView.findViewById(R.id.scientific_name);
        typeTextView = rootView.findViewById(R.id.type);
        cycleTextView = rootView.findViewById(R.id.cycle);
        wateringTextView = rootView.findViewById(R.id.watering);

        // A kiválasztott növény adatait megjelenítjük
        if (getArguments() != null) {
            plant = getArguments().getParcelable(ARG_PLANT);

            if (plant != null) {
                commonNameTextView.setText(plant.getCommonName());
                scientificNameTextView.setText(plant.getScientificName());
                typeTextView.setText(plant.getType());
                cycleTextView.setText(plant.getCycle());
                wateringTextView.setText(plant.getWatering());
            }
        }

        return rootView;
    }
}
