package hu.nje.plantcare.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.slider.Slider;

import hu.nje.plantcare.R;
import hu.nje.plantcare.database.entity.Plant;

public class PlantDetailAdapter extends RecyclerView.Adapter<PlantDetailAdapter.PlantViewHolder>
{

    private OnFavoriteClickListener clickListener;
    private OnBackClickListener backListener;

    private Plant plant;

    public PlantDetailAdapter(Plant plant,OnFavoriteClickListener clickListener, OnBackClickListener backListener)
    {
        this.plant = plant;
        this.clickListener=clickListener;
        this.backListener=backListener;
    }

    public void setPlant(Plant plant)
    {
        this.plant = plant;
    }

    @NonNull
    @Override
    public PlantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.searched_item_details, parent, false);
        return new PlantViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantViewHolder holder, int position) {

        holder.back2list.setOnClickListener(v->{
            if(backListener!=null)
            {
                backListener.OnBackClick();
            }
        });
        Glide.with(holder.itemView.getContext())
                .load(plant.getImgUrl()) // <-- Ez az URL-ed
                //.placeholder(R.drawable.placeholder) // opcionális betöltés közbeni kép
                //.error(R.drawable.error_image)       // opcionális hiba esetén
                .into(holder.plantImage);

        holder.commonNameTextView.setText(plant.getCommonName());
        holder.scientificNameTextView.setText(plant.getScientificName());
        holder.typeTextView.setText(plant.type);
        holder.cycleTextView.setText(plant.cycle);
        holder.wateringTextView.setText(plant.watering);
        holder.favoriteSwitch.setChecked(plant.isFavorite);
        holder.favoriteSwitch.setOnClickListener(v->{
            if (clickListener != null) {
                clickListener.OnFavoriteClick();
                plant.setFavorite(holder.favoriteSwitch.isChecked());
                notifyItemChanged(position);
//                holder.favoriteSwitch.setChecked(plant.isFavorite);
            }
        });
        holder.descriptionTextView.setText(plant.description);

    }

    @Override
    public int getItemCount() {
        return plant == null ? 0 : 1;
    }


    public static class PlantViewHolder extends RecyclerView.ViewHolder
    {
        private final Button back2list;
        private final ImageView plantImage;
        private final TextView commonNameTextView;
        private final TextView scientificNameTextView;
        private final TextView typeTextView;
        private final TextView cycleTextView;
        private final TextView wateringTextView;
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        private final Switch favoriteSwitch;
        private final TextView descriptionTextView;

        PlantViewHolder(View itemView)
        {

            super(itemView);
            back2list = itemView.findViewById(R.id.btn_backToList);
            plantImage = itemView.findViewById(R.id.plant_image);
            commonNameTextView = itemView.findViewById(R.id.common_name);
            scientificNameTextView = itemView.findViewById(R.id.scientific_name);
            typeTextView = itemView.findViewById(R.id.type);
            cycleTextView = itemView.findViewById(R.id.cycle);
            wateringTextView = itemView.findViewById(R.id.watering);
            favoriteSwitch = itemView.findViewById(R.id.favorite_switch);
            descriptionTextView = itemView.findViewById(R.id.description);

        }
    }

    public interface OnFavoriteClickListener{
        void OnFavoriteClick();
    }
    public interface OnBackClickListener{
        void OnBackClick();
    }

}
