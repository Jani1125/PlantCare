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

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.PlantViewHolder> {

    private List<Plant> plants;

    public DetailsAdapter(List<Plant> plantList) {
        this.plants=plantList;
    }

    public void setPlants(List<Plant> plants) {
        this.plants = plants;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.detailitem, parent, false);
        return new PlantViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantViewHolder holder, int position) {
        Plant currentPlant = plants.get(position);
        holder.commonNameTextView.setText(currentPlant.getCommonName());
        holder.scientificNameTextView.setText(currentPlant.getScientificName());
        holder.typeNameTextView.setText(currentPlant.getType());
        holder.cycleNameTextView.setText(currentPlant.getCycle());
        holder.waterNameTextView.setText(currentPlant.getWatering());



    }

    @Override
    public int getItemCount() {
        return plants == null ? 0 : plants.size();
    }

    static class PlantViewHolder extends RecyclerView.ViewHolder {
        private final TextView commonNameTextView;
        private final TextView scientificNameTextView;

        private final  TextView typeNameTextView;
        private final TextView cycleNameTextView;

        private  final TextView waterNameTextView;



        PlantViewHolder(View itemView) {
            super(itemView);
            commonNameTextView = itemView.findViewById(R.id.common_name);
            scientificNameTextView = itemView.findViewById(R.id.scientific_name);
            typeNameTextView = itemView.findViewById(R.id.type);
            cycleNameTextView = itemView.findViewById(R.id.cycle);
            waterNameTextView=itemView.findViewById(R.id.watering);


        }
    }
}
