package hu.nje.plantcare.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import hu.nje.plantcare.database.entity.Notification;
import hu.nje.plantcare.database.entity.OwnPlant;
import hu.nje.plantcare.database.entity.Plant;

@Database(entities = {Plant.class, OwnPlant.class, Notification.class}, version = 6) // Verziót 6-ra növeltük
public abstract class PlantDatabase extends RoomDatabase {
    public abstract PlantDao plantDao();
    public abstract OwnPlantDao ownPlantDao();
    public abstract NotificationDao notificationDao();

    private static volatile PlantDatabase INSTANCE;

    public static PlantDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (PlantDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    PlantDatabase.class, "plant_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}