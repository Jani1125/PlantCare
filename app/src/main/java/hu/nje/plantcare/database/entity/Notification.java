package hu.nje.plantcare.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notifications")
public class Notification {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int plantId; // Az értesítéshez tartozó növény ID-ja
    private String plantName; // A növény neve az értesítésben
    private long notificationTime; // Az értesítés beütemezett időpontja
    private boolean isDelivered; // Jelzi, hogy az értesítés ki lett-e küldve

    public Notification() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlantId() {
        return plantId;
    }

    public void setPlantId(int plantId) {
        this.plantId = plantId;
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public long getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(long notificationTime) {
        this.notificationTime = notificationTime;
    }

    public boolean isDelivered() {
        return isDelivered;
    }

    public void setDelivered(boolean delivered) {
        isDelivered = delivered;
    }
}