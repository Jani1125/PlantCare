package hu.nje.plantcare.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.preference.PreferenceManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import hu.nje.plantcare.database.PlantDatabase;
import hu.nje.plantcare.database.entity.Notification;

public class NotificationScheduler {

    private static final String TAG = "NotificationScheduler";
    private static final String WATERING_REMINDER_ACTION = "hu.nje.plantcare.ACTION_WATERING_REMINDER";
    private static final String PREF_NOTIFICATIONS_ENABLED = "notifications_enabled";

    public static void scheduleWatering(Context context, int plantId, String plantName, String wateringFrequency, String plantImageUrl) {
        boolean areNotificationsEnabled = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_NOTIFICATIONS_ENABLED, true);

        if (!areNotificationsEnabled) {
            Log.i(TAG, "Értesítések globálisan le vannak tiltva, a(z) " + plantName + " nevű növényhez nem ütemezünk értesítést.");
            return;
        }

        long triggerAtMillis = calculateTriggerTime(wateringFrequency);

        if (triggerAtMillis == -1) {
            Log.w(TAG, "Érvénytelen locsolási gyakoriság: " + wateringFrequency);
            return;
        }

        long firstNotificationTime = System.currentTimeMillis() + triggerAtMillis;

        Intent notificationIntent = new Intent(context, PlantWateringReceiver.class);
        notificationIntent.setAction(WATERING_REMINDER_ACTION);
        notificationIntent.putExtra("plantId", plantId);
        notificationIntent.putExtra("plantName", plantName);

        int requestCode = plantId;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    firstNotificationTime,
                    triggerAtMillis,
                    pendingIntent
            );
            Log.i(TAG, "Locsolási értesítés ütemezve a(z) " + plantName + " nevű növényhez " + wateringFrequency + " gyakorisággal. Első értesítés időpontja: " + Calendar.getInstance().getTimeInMillis());

            // Értesítés adatainak mentése az adatbázisba, beleértve a kép URL-jét
            PlantDatabase db = PlantDatabase.getDatabase(context);
            Notification notification = new Notification();
            notification.setPlantId(plantId);
            notification.setPlantName(plantName);
            notification.setNotificationTime(firstNotificationTime);
            notification.setPlantImageUrl(plantImageUrl);
            db.notificationDao().insertNotification(notification);

        } else {
            Log.e(TAG, "Nem sikerült lekérni az AlarmManager szolgáltatást.");
        }
    }

    public static void cancelWatering(Context context, int plantId) {
        Intent notificationIntent = new Intent(context, PlantWateringReceiver.class);
        notificationIntent.setAction(WATERING_REMINDER_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                plantId,
                notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            Log.i(TAG, "Locsolási értesítés törölve a(z) " + plantId + " ID-jú növényhez.");

            // Értesítések törlése az adatbázisból is
            PlantDatabase db = PlantDatabase.getDatabase(context);
            new Thread(() -> db.notificationDao().deleteNotificationsForPlant(plantId)).start();

        } else {
            Log.e(TAG, "Nem sikerült lekérni az AlarmManager szolgáltatást a törléshez.");
        }
    }

    private static long calculateTriggerTime(String wateringFrequency) {
        switch (wateringFrequency) {
            case "frequent":
                return TimeUnit.DAYS.toMillis(2);
            case "average":
                return TimeUnit.DAYS.toMillis(5);
            case "minimum":
                return TimeUnit.DAYS.toMillis(10);
            case "none":
                return -1;
            default:
                return -1;
        }
    }
}