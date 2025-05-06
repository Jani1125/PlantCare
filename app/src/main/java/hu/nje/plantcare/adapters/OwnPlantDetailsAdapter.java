package hu.nje.plantcare.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import hu.nje.plantcare.R;
import hu.nje.plantcare.database.Watering;
import hu.nje.plantcare.database.entity.OwnPlant;

public class OwnPlantDetailsAdapter extends RecyclerView.Adapter<OwnPlantDetailsAdapter.OwnPlantDetailsViewHolder>
{
    private OwnPlant ownPlant;
    private final OnBackClickListener backClickListener;
    public OwnPlantDetailsAdapter(OwnPlant ownPlant,OnBackClickListener backClickListener)
    {
        this.ownPlant = ownPlant;
        this.backClickListener = backClickListener;
    }


    public void setOwnPlant(OwnPlant ownPlant){this.ownPlant=ownPlant;}

    @NonNull
    @Override
    public OwnPlantDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.own_plant_item_details,parent,false);
        return new OwnPlantDetailsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OwnPlantDetailsViewHolder holder, int position) {
        holder.back2OwnList.setOnClickListener(v->{
            if(backClickListener!=null)
            {
                backClickListener.OnBackClick();
            }
        });
        Glide.with(holder.itemView.getContext())
                .load(ownPlant.getImgUrl()) // <-- Ez az URL-ed
                .placeholder(R.drawable.plant_placeholder) // opcionális betöltés közbeni kép
                .error(R.drawable.plantfavpic)       // opcionális hiba esetén
                .into(holder.plantImage);

        holder.commonNameTextView.setText(ownPlant.getCommonName());
        holder.typeTextView.setText(ownPlant.getType());

        Watering frequency=Watering.getWateringInDays(ownPlant.getWatering());
        String watering = "Every "+frequency.getMinDays()+"-"+frequency.getMaxDays()+" days";
        holder.wateringTextView.setText(watering);

        holder.descriptionTextView.setText(ownPlant.getDescription());
    }

    @Override
    public int getItemCount() {
        return 1;
    }




    public static class OwnPlantDetailsViewHolder extends RecyclerView.ViewHolder
    {
        private final Button back2OwnList;
        private final ImageView plantImage;
        private final TextView commonNameTextView;
        private final TextView typeTextView;
        private final TextView wateringTextView;
        private final TextView descriptionTextView;

        public OwnPlantDetailsViewHolder(View itemView)
        {
            super(itemView);
            back2OwnList        = itemView.findViewById(R.id.btn_backToOwnList);
            plantImage          = itemView.findViewById(R.id.own_plant_image);
            commonNameTextView  = itemView.findViewById(R.id.own_common_name);
            typeTextView        = itemView.findViewById(R.id.own_type);
            wateringTextView    = itemView.findViewById(R.id.own_watering);
            descriptionTextView = itemView.findViewById(R.id.own_description);

        }
    }

    public interface OnBackClickListener{
        void OnBackClick();
    }
}
