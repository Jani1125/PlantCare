package hu.nje.plantcare.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import hu.nje.plantcare.R;
import hu.nje.plantcare.database.entity.OwnPlant;


public class NewPlantAdapter extends RecyclerView.Adapter<NewPlantAdapter.ViewHolder> {

    private OwnPlant newOwnPlant;
    private Context context;
    private ViewHolder currentViewHolder;

    public interface OnImageActionListener {
        void onCameraClick();
        void onGalleryClick();
    }

    private final OnImageActionListener imageActionListener;

    public NewPlantAdapter(OnImageActionListener listener) {
        this.imageActionListener = listener;
    }

    @NonNull
    @Override
    public NewPlantAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.form_field_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewPlantAdapter.ViewHolder holder, int position) {

        currentViewHolder = holder;

        holder.inputCommonName.setText("");
        holder.inputType.setText("");
        holder.inputWatering.setText("");
        holder.inputDescription.setText("");
        Glide.with(holder.itemView.getContext())
                .load(R.drawable.plant_placeholder) // <-- Ez az URL-ed
                //.placeholder(R.drawable.placeholder) // opcionális betöltés közbeni kép
                //.error(R.drawable.error_image)       // opcionális hiba esetén
                .into(holder.previewImage);

        holder.btnCamera.setOnClickListener(v -> imageActionListener.onCameraClick());
        holder.btnGallery.setOnClickListener(v -> imageActionListener.onGalleryClick());
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        EditText inputCommonName, inputType, inputWatering, inputDescription;
        Button btnCamera, btnGallery;
        ImageView previewImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            inputCommonName = itemView.findViewById(R.id.inputCommonName);
            inputType = itemView.findViewById(R.id.inputType);
            inputWatering = itemView.findViewById(R.id.inputWatering);
            inputDescription = itemView.findViewById(R.id.inputDescription);
            previewImage = itemView.findViewById(R.id.previewImageView);

            btnCamera = itemView.findViewById(R.id.buttonTakePicture);
            btnGallery = itemView.findViewById(R.id.buttonChooseGallery);
        }
    }

    public OwnPlant setPlantData() {
        if (currentViewHolder == null) return null;

        OwnPlant plant = new OwnPlant();
        plant.setCommonName(currentViewHolder.inputCommonName.getText().toString().trim());
        plant.setType(currentViewHolder.inputType.getText().toString().trim());
        plant.setWatering(currentViewHolder.inputWatering.getText().toString().trim());
        plant.setDescription(currentViewHolder.inputDescription.getText().toString().trim());
        System.out.println(plant.getCommonName());
        // Optionally handle image if needed
        return plant;
    }

    public boolean isFormValid() {
        if (currentViewHolder == null) return false;

        return !currentViewHolder.inputCommonName.getText().toString().trim().isEmpty() &&
                !currentViewHolder.inputType.getText().toString().trim().isEmpty() &&
                !currentViewHolder.inputWatering.getText().toString().trim().isEmpty() &&
                !currentViewHolder.inputDescription.getText().toString().trim().isEmpty();
    }


}

