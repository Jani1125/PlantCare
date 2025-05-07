package hu.nje.plantcare.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import hu.nje.plantcare.R;
import hu.nje.plantcare.database.entity.Notification;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notification> notificationList;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault());

    public NotificationAdapter(List<Notification> notificationList) {
        this.notificationList = notificationList;
    }

    public void setNotifications(List<Notification> notificationList) {
        this.notificationList = notificationList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.messages_item, parent, false);
        return new NotificationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification currentNotification = notificationList.get(position);
        holder.notificationPlantName.setText(currentNotification.getPlantName());
        holder.notificationTime.setText(sdf.format(currentNotification.getNotificationTime()));
        holder.notificationStatus.setText(currentNotification.isDelivered() ? "Állapot: Kiküldve" : "Állapot: Ütemezve");

        // Kép betöltése, ha van URL
        if (currentNotification.getPlantImageUrl() != null && !currentNotification.getPlantImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(currentNotification.getPlantImageUrl())
                    .apply(new RequestOptions().placeholder(R.drawable.plant_placeholder).error(R.drawable.plant_placeholder))
                    .into(holder.notificationPlantImage);
        } else {
            // Ha nincs kép URL, akkor a plantfavpic.png-t jelenítjük meg
            holder.notificationPlantImage.setImageResource(R.drawable.plantfavpic);
        }
    }

    @Override
    public int getItemCount() {
        return notificationList == null ? 0 : notificationList.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        ImageView notificationPlantImage;
        TextView notificationPlantName;
        TextView notificationTime;
        TextView notificationStatus;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            notificationPlantImage = itemView.findViewById(R.id.notificationPlantImage);
            notificationPlantName = itemView.findViewById(R.id.notificationPlantName);
            notificationTime = itemView.findViewById(R.id.notificationTime);
            notificationStatus = itemView.findViewById(R.id.notificationStatus);
        }
    }
}