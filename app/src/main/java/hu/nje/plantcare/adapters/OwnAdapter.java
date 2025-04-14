package hu.nje.plantcare.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import hu.nje.plantcare.R;
import hu.nje.plantcare.database.entity.Plant;

public class OwnAdapter extends RecyclerView.Adapter<OwnAdapter.PlantViewHolder> {

    private List<Plant> plantList;

    public static class PlantViewHolder extends RecyclerView.ViewHolder {
        public TextView commonName;
        public TextView scientificName;


        public PlantViewHolder(View view) {
            super(view);
            commonName = view.findViewById(R.id.plant_common_name);
            scientificName = view.findViewById(R.id.plant_scientific_name);

        }
    }

    public OwnAdapter(List<Plant> plantList) {
        this.plantList = plantList;
    }

    @Override
    public PlantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_own_plant, parent, false);
        return new PlantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlantViewHolder holder, int position) {
        Plant plant = plantList.get(position);
        holder.commonName.setText(plant.getCommonName());
        holder.scientificName.setText(plant.getScientificName());

    }

    @Override
    public int getItemCount() {
        return plantList.size();
    }
}
