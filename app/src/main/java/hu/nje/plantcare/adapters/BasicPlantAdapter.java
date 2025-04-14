package hu.nje.plantcare.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import hu.nje.plantcare.R;
import hu.nje.plantcare.database.entity.BasicPlant;
import hu.nje.plantcare.database.entity.Plant;

public class BasicPlantAdapter extends RecyclerView.Adapter<BasicPlantAdapter.PlantViewHolder> {

    private List<BasicPlant> plants;

    public BasicPlantAdapter(List<BasicPlant> plantList) {
        this.plants=plantList;
    }

    public void setPlants(List<BasicPlant> plants) {
        this.plants = plants;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.searched_item, parent, false);
        return new PlantViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantViewHolder holder, int position) {
        BasicPlant currentPlant = plants.get(position);
        holder.commonNameTextView.setText(currentPlant.getCommon_name());
        holder.scientificNameTextView.setText(currentPlant.getScientific_name());

        Glide.with(holder.itemView.getContext())
                .load(currentPlant.getImgUrl()) // <-- Ez az URL-ed
                //.placeholder(R.drawable.placeholder) // opcionális betöltés közbeni kép
                //.error(R.drawable.error_image)       // opcionális hiba esetén
                .into(holder.plantImage);


    }

    @Override
    public int getItemCount() {
        return plants == null ? 0 : plants.size();
    }


    static class PlantViewHolder extends RecyclerView.ViewHolder {
        private final TextView commonNameTextView;
        private final TextView scientificNameTextView;

        private final ImageView plantImage;




        PlantViewHolder(View itemView) {
            super(itemView);
            commonNameTextView = itemView.findViewById(R.id.common_name);
            scientificNameTextView = itemView.findViewById(R.id.scientific_name);
            plantImage=itemView.findViewById(R.id.plant_image);



        }
    }
}
