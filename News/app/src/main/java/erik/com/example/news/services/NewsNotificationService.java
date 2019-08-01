package erik.com.example.news.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import erik.com.example.news.R;
import erik.com.example.news.activities.MainActivity;
import erik.com.example.news.constants.Constants;
import erik.com.example.news.providers.NewsDataProvider;

public class NewsNotificationService extends Service implements NewsDataProvider.NewsNotificationCallback {
    private static final String NEW_NEWS_SIZE_KEY_FILE =
            "erik.com.example.news.services.NewsNotificationService.NEW_NEWS_SIZE_KEY_FILE";
    private static final String NEW_SIZE_KEY =
            "erik.com.example.news.services.NewsNotificationService.NEW_SIZE_KEY";
    private static final int NOTIFICATION_CODE = 1001;
    private static final long TIME_TO_RESTART_SERVICE_AFTER = 1000;

    private SharedPreferences sharedPrefs;
    private final NewsDataProvider provider = NewsDataProvider.getInstance();
    private final Handler newsHandler = new Handler();
    private final Runnable newsHandlerTask = new Runnable() {
        @Override
        public void run() {
            provider.loadNewsNotification(NewsNotificationService.this);
            newsHandler.postDelayed(newsHandlerTask, Constants.NEWS_FETCH_INTERVAL);
        }
    };

    public NewsNotificationService() {
        // Empty require constructor
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sharedPrefs = getSharedPreferences(NEW_NEWS_SIZE_KEY_FILE, MODE_PRIVATE);
        newsHandlerTask.run();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onNewNewsLoaded(Integer newsCount) {
        final int currentNewsSize = sharedPrefs.getInt(NEW_SIZE_KEY, Integer.MAX_VALUE);
        if (newsCount > currentNewsSize) {
            sendNotification();
            sharedPrefs.edit().putInt(NEW_SIZE_KEY, newsCount).apply();
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        restartServiceOnAppClose();
        super.onTaskRemoved(rootIntent);
    }

    /* Helper Methods */

    /**
     * Generates and sends notification to inform about new news availability.
     */
    private void sendNotification() {
        final Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        final PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, intent, 0);
        final NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, Constants.CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_new)
                        .setContentTitle(getString(R.string.notification_title))
                        .setContentText(getString(R.string.notification_text))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);
        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_CODE, builder.build());
    }

    /**
     * Restarts the service after 1 second when app closes.
     */
    private void restartServiceOnAppClose() {
        final Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        final PendingIntent restartServicePendingIntent =
                PendingIntent.getService(getApplicationContext(), 1,
                        restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        final AlarmManager alarmService =
                (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        if (alarmService != null) {
            alarmService.set(
                    AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + TIME_TO_RESTART_SERVICE_AFTER,
                    restartServicePendingIntent);
        }
    }
}
