package hu.nje.plantcare.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import hu.nje.plantcare.R;
import hu.nje.plantcare.database.Plant;
import hu.nje.plantcare.database.PlantDao;

public class FavAdapter extends RecyclerView.Adapter<FavAdapter.PlantViewHolder> {
    private List<Plant> plantList;
    private PlantDao plantDao;  // Adatbázis hozzáférés

    public FavAdapter(List<Plant> plantList, PlantDao plantDao) {
        this.plantList = plantList;
        this.plantDao = plantDao;
    }

    public FavAdapter(List<Plant> plantList) {
    }

    @Override
    public PlantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ownitem, parent, false);
        return new PlantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlantViewHolder holder, int position) {
        Plant plant = plantList.get(position);
        holder.commonName.setText(plant.getCommonName());
        holder.scientificName.setText(plant.getScientificName());


        // Kedvenc státusz beállítása
        /*if (plant.isFavorite()) {
            holder.favoriteIcon.setImageResource(R.drawable.ic_heart);  // Kedvenc ikont jelenítünk meg
        }*/

        /* Kedvenc gomb működése
        holder.favoriteIcon.setOnClickListener(v -> {
            boolean isFavorite;
            isFavorite = !plant.isFavorite();
            plant.setFavorite(isFavorite);
            plantDao.setFavorite(plant.getId(), isFavorite);  // Frissítjük az adatbázisban
            notifyItemChanged(position);  // Frissítjük az adaptert
        });

         */
    }

    @Override
    public int getItemCount() {
        return plantList.size();
    }

    public static class PlantViewHolder extends RecyclerView.ViewHolder {
        TextView commonName, scientificName;
        ImageView favoriteIcon;

        public PlantViewHolder(View itemView) {
            super(itemView);
            commonName = itemView.findViewById(R.id.common_name);
            scientificName = itemView.findViewById(R.id.scientific_name);
            favoriteIcon = itemView.findViewById(R.id.favorite_icon);  // Kedvenc gomb
        }
    }
}
