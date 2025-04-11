package hu.nje.plantcare.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import hu.nje.plantcare.R;
import hu.nje.plantcare.database.Plant;

public class PlantDetailsAdapter extends RecyclerView.Adapter<PlantDetailsAdapter.PlantViewHolder> {

    private List<Plant> plants;

    public void setPlants(List<Plant> plants) {
        this.plants = plants;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_plant_detail, parent, false);
        return new PlantViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantViewHolder holder, int position) {
        Plant currentPlant = plants.get(position);
        holder.commonNameTextView.setText(currentPlant.getCommonName());
        holder.scientificNameTextView.setText(currentPlant.getScientificName());
    }

    @Override
    public int getItemCount() {
        return plants == null ? 0 : plants.size();
    }

    static class PlantViewHolder extends RecyclerView.ViewHolder {
        private final TextView commonNameTextView;
        private final TextView scientificNameTextView;

        PlantViewHolder(View itemView) {
            super(itemView);
            commonNameTextView = itemView.findViewById(R.id.common_name);
            scientificNameTextView = itemView.findViewById(R.id.scientific_name);
        }
    }
}
