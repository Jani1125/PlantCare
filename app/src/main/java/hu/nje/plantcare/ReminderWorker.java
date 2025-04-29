package hu.nje.plantcare;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReminderWorker extends Worker {

    public ReminderWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @SuppressLint("MissingPermission")
    @NonNull
    @Override
    public Result doWork() {

        // Értesítési csatorna létrehozása (ha szükséges)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "WATER_REMINDER_CHANNEL",
                    "Locsolási emlékeztető",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getApplicationContext().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        // Értesítés adatai
        String title = "Ideje locsolni!";
        String content = "Ne felejtsd el meglocsolni a növényeket!";

        // Értesítés építése
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "WATER_REMINDER_CHANNEL")
                .setSmallIcon(R.drawable.ic_can) // saját ikon
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Értesítés elküldése
        NotificationManagerCompat.from(getApplicationContext()).notify(1001, builder.build());

        // Értesítés mentése a SharedPreferences-be
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("notifications", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String oldMessages = prefs.getString("history", "");

        // Aktuális idő formázása
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());

        // Új bejegyzés
        String newMessage = timestamp + " - " + content;

        // Hozzáfűzés
        editor.putString("history", oldMessages + newMessage + "\n");
        editor.apply();

        return Result.success();
    }
}
