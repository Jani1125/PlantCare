package hu.nje.plantcare.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import hu.nje.plantcare.R;
import hu.nje.plantcare.database.entity.OwnPlant;

public class OwnPlantAdapter extends RecyclerView.Adapter<OwnPlantAdapter.PlantViewHolder> {

    private List<OwnPlant> ownPlantList;

    private OnDeleteClickListener deleteClickListener;
    public static class PlantViewHolder extends RecyclerView.ViewHolder {
        public TextView commonName;

        public ImageView plantImage;
        public ImageButton deleteButton;


        public PlantViewHolder(View view) {
            super(view);
            commonName = view.findViewById(R.id.plant_common_name);
            plantImage = view.findViewById(R.id.plantImageView);
            deleteButton = view.findViewById(R.id.deleteButton);

        }
    }

    public OwnPlantAdapter(List<OwnPlant> ownPlants, OnDeleteClickListener deleteClickListener)
    {
        this.ownPlantList = ownPlants;
        this.deleteClickListener = deleteClickListener;
    }
    public void setOwnPlants(List<OwnPlant> ownPlants)
    {
        this.ownPlantList=ownPlants;
        notifyDataSetChanged();
    }

    @Override
    public PlantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.own_plant_item, parent, false);
        return new PlantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlantViewHolder holder, int position) {
        OwnPlant currentOwnPlant = ownPlantList.get(position);
        holder.commonName.setText(currentOwnPlant.getCommonName());

        Glide.with(holder.itemView.getContext())
                .load(currentOwnPlant.getImgUrl()) // <-- Ez az URL-ed
                //.placeholder(R.drawable.placeholder) // opcionális betöltés közbeni kép
                .error(R.drawable.plantfavpic)       // opcionális hiba esetén
                .into(holder.plantImage);

        holder.deleteButton.setOnClickListener(v->{
            if(deleteClickListener!=null) deleteClickListener.OnDeleteClick(currentOwnPlant.getId());
        });

    }

    @Override
    public int getItemCount() {
        return ownPlantList == null ? 0 : ownPlantList.size();
    }

    public interface OnDeleteClickListener
    {
        void OnDeleteClick(int ownPlantId);
    }
}
