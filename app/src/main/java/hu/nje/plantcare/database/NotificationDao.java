package hu.nje.plantcare.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import hu.nje.plantcare.database.entity.Notification;

@Dao
public interface NotificationDao {
    @Insert
    long insertNotification(Notification notification);

    @Query("SELECT * FROM notifications ORDER BY notificationTime DESC")
    List<Notification> getAllNotifications();

    @Query("SELECT * FROM notifications WHERE plantId = :plantId")
    List<Notification> getNotificationsForPlant(int plantId);

    @Query("SELECT * FROM notifications WHERE id = :notificationId")
    Notification getNotificationById(long notificationId);

    @Update
    void updateNotification(Notification notification);

    @Query("DELETE FROM notifications WHERE id = :notificationId")
    void deleteNotification(long notificationId);

    @Query("DELETE FROM notifications WHERE plantId = :plantId")
    void deleteNotificationsForPlant(int plantId);
}