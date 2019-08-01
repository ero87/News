package erik.com.example.news.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.List;

import erik.com.example.news.api.models.Result;
import erik.com.example.news.constants.Constants;
import erik.com.example.news.providers.NewsDataProvider;

public class NewsService extends Service implements NewsDataProvider.NewsCallback{

    public static final String NEWS_LOADED_INTENT_ACTION = "NEW_NEWS_LOADING_ACTION";

    private final NewsDataProvider provider = NewsDataProvider.getInstance();
    private final Handler newsHandler = new Handler();
    private final Runnable newsHandlerTask = new Runnable() {
        @Override
        public void run() {
            provider.loadNews(NewsService.this);
            newsHandler.postDelayed(newsHandlerTask, Constants.NEWS_FETCH_INTERVAL);
        }
    };

    public NewsService() {
        // Empty require constructor
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        newsHandlerTask.run();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onNewsLoaded(List<Result> newsList) {
        final Intent intent = new Intent();
        intent.setAction(NEWS_LOADED_INTENT_ACTION);
        // Send broadcast to Home Screen to notify new news loading
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }
}

