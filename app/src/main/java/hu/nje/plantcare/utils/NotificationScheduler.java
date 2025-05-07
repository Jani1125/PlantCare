package hu.nje.plantcare.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import hu.nje.plantcare.database.PlantDatabase;
import hu.nje.plantcare.database.entity.Notification;

public class NotificationScheduler {

    private static final String TAG = "NotificationScheduler";
    private static final String WATERING_REMINDER_ACTION = "hu.nje.plantcare.ACTION_WATERING_REMINDER";

    public static void scheduleWatering(Context context, int plantId, String plantName, String wateringFrequency) {
        long triggerAtMillis = calculateTriggerTime(wateringFrequency);

        if (triggerAtMillis == -1) {
            Log.w(TAG, "Érvénytelen locsolási gyakoriság: " + wateringFrequency);
            return;
        }

        // Az első értesítést a hozzáadás időpontjától számítva ütemezzük
        long firstNotificationTime = System.currentTimeMillis() + triggerAtMillis;

        Intent notificationIntent = new Intent(context, PlantWateringReceiver.class);
        notificationIntent.setAction(WATERING_REMINDER_ACTION);
        notificationIntent.putExtra("plantId", plantId);
        notificationIntent.putExtra("plantName", plantName);

        // Egyedi PendingIntent kérékkód a növény ID-ja alapján, hogy később törölni tudjuk
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

            // Értesítés adatainak mentése az adatbázisba
            PlantDatabase db = PlantDatabase.getDatabase(context);
            Notification notification = new Notification();
            notification.setPlantId(plantId);
            notification.setPlantName(plantName);
            notification.setNotificationTime(firstNotificationTime);
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
                plantId, // Ugyanaz a kérékkód, mint az ütemezéskor
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
                return TimeUnit.DAYS.toMillis(2); // Példa: 2 naponta
            case "average":
                return TimeUnit.DAYS.toMillis(5); // Példa: 5 naponta
            case "minimum":
                return TimeUnit.DAYS.toMillis(10); // Példa: 10 naponta
            case "none":
                return -1; // Nincs emlékeztető
            default:
                return -1; // Érvénytelen érték
        }
    }
}