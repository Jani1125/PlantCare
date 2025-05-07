package hu.nje.plantcare.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import hu.nje.plantcare.MainActivity;
import hu.nje.plantcare.R;
import hu.nje.plantcare.database.PlantDatabase;
import hu.nje.plantcare.database.entity.Notification;

public class PlantWateringReceiver extends BroadcastReceiver {

    private static final String TAG = "PlantWateringReceiver";
    private static final String CHANNEL_ID = "plant_watering_channel";
    private static final int NOTIFICATION_BASE_ID = 200; // Bázis ID, ehhez adjuk hozzá a növény ID-ját

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals("hu.nje.plantcare.ACTION_WATERING_REMINDER")) {
            int plantId = intent.getIntExtra("plantId", -1);
            String plantName = intent.getStringExtra("plantName");

            if (plantId != -1 && plantName != null) {
                Log.i(TAG, "Locsolási emlékeztető érkezett a(z) " + plantName + " nevű növényhez (ID: " + plantId + ").");
                showNotification(context, plantName, plantId); // Átadjuk a plantId-t is

                // Az értesítés kiküldésének rögzítése az adatbázisban
                PlantDatabase db = PlantDatabase.getDatabase(context);
                new Thread(() -> {
                    Notification notification = db.notificationDao().getNotificationById(plantId);
                    if (notification != null) {
                        notification.setDelivered(true);
                        db.notificationDao().updateNotification(notification);
                    }
                }).start();

                // Itt beütemezhetjük a következő értesítést, ha szükséges (a jelenlegi implementáció az AlarmManager-ben ismétlődőre van állítva)
            }
        }
    }

    private void showNotification(Context context, String plantName, int plantId) { // Elfogadjuk a plantId-t
        createNotificationChannel(context);

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_can) // Használd a popup_message.xml-ben lévő ikont
                .setContentTitle("Locsolási emlékeztető!")
                .setContentText("Ne felejtse el meglocsolni a(z) " + plantName + " nevű növényt!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true); // Az értesítés eltűnik, ha a felhasználó rákattint

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_BASE_ID + plantId, builder.build()); // Egyedi értesítés ID a növényhez
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Növény locsolási emlékeztetők";
            String description = "Emlékeztetők a saját növények locsolására.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}