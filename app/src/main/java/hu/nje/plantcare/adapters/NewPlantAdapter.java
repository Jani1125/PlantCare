package hu.nje.plantcare.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

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
        void onSetImageClick();
        void onImageSelected(String imagePath);
    }
    public void setImagePath(String path) {
        this.imagePath = path;
        notifyItemChanged(0); // or wherever the image preview is
    }
    private String imagePath;
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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                holder.itemView.getContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Frequent (1-3 days)", "Average (4-6 days)", "Minimum (7-14 days)", "None (15-25 days)"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        holder.spinnerWatering.setAdapter(adapter);

        holder.inputDescription.setText("");
        // Load the selected image
        if (imagePath != null) {
            Glide.with(holder.itemView.getContext())
                    .load(imagePath)
                    .placeholder(R.drawable.plant_placeholder) // optional
                    .error(R.drawable.plantfavpic)       // optional
                    .centerCrop()
                    .into(holder.previewImage);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.plantfavpic) // fallback/default image
                    .into(holder.previewImage);
        }


        holder.btnSetImage.setOnClickListener(v -> imageActionListener.onSetImageClick());

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        EditText inputCommonName, inputType, inputDescription;
        Spinner spinnerWatering;
        Button btnSetImage;
        ImageView previewImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            inputCommonName = itemView.findViewById(R.id.inputCommonName);
            inputType = itemView.findViewById(R.id.inputType);
            spinnerWatering = itemView.findViewById(R.id.spinnerWatering);
            inputDescription = itemView.findViewById(R.id.inputDescription);
            previewImage = itemView.findViewById(R.id.previewImageView);

            btnSetImage = itemView.findViewById(R.id.buttonSetImage);
        }
    }

    public OwnPlant setPlantData() {
        if (currentViewHolder == null) return null;

        OwnPlant plant = new OwnPlant();
        plant.setCommonName(currentViewHolder.inputCommonName.getText().toString().trim());
        plant.setType(currentViewHolder.inputType.getText().toString().trim());


        int selectedItemPos=currentViewHolder.spinnerWatering.getSelectedItemPosition();
        String wateringSeq="";
        switch (selectedItemPos){
            case 0:
                wateringSeq="frequent";
                break;
            case 1:
                wateringSeq="average";
                break;
            case 2:
                wateringSeq="minimum";
                break;
            case 3:
                wateringSeq="none";
                break;
        }
        plant.setWatering(wateringSeq);
        plant.setDescription(currentViewHolder.inputDescription.getText().toString().trim());
        plant.setImgUrl(this.imagePath);

        return plant;
    }

    public boolean isFormValid() {
        if (currentViewHolder == null) return false;

        return !currentViewHolder.inputCommonName.getText().toString().trim().isEmpty() &&
                !currentViewHolder.inputType.getText().toString().trim().isEmpty() &&
                !currentViewHolder.spinnerWatering.getSelectedItem().toString().isEmpty() &&
                !currentViewHolder.inputDescription.getText().toString().trim().isEmpty();
    }


}

