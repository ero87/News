package erik.com.example.news.providers;

import android.content.Context;

import java.lang.ref.SoftReference;
import java.util.List;

import erik.com.example.news.api.models.Content;
import erik.com.example.news.database.entities.NewsDb;
import erik.com.example.news.api.models.Result;
import erik.com.example.news.tasks.LoadNewNewsTask;
import erik.com.example.news.tasks.LoadNewsDetailFromDBTask;
import erik.com.example.news.tasks.LoadNewsDetailTask;
import erik.com.example.news.tasks.LoadNewsFromDBTask;
import erik.com.example.news.tasks.LoadNewsTask;
import erik.com.example.news.tasks.LoadPinnedNewsTask;

public class NewsDataProvider {
    private static volatile NewsDataProvider sInstance;
    private static final Object lockObject = new Object();

    private NewsDataProvider() {
    }

    /**
     * gets thread safe singleton instance of news data provider
     *
     * @return singleton instance
     */
    public static NewsDataProvider getInstance() {
        if (sInstance == null) {
            synchronized (lockObject) {
                if (sInstance == null) {
                    // if instance is null, initialize
                    sInstance = new NewsDataProvider();
                }

            }
        }
        return sInstance;
    }

    /**
     * Loads news data.
     *
     * @param callback For handle already available data
     */
    public void loadNews(final NewsCallback callback) {
        new LoadNewsTask(callback).execute();
    }

    /**
     * Loads news data from database.
     *
     * @param callback For handle already available data
     * @param context  For accessing database
     */
    public void loadNewsFromDB(final NewsFromDBCallback callback, final SoftReference<Context> context) {
        new LoadNewsFromDBTask(callback, context).execute();
    }


    /**
     * Loads news detailed data.
     *
     * @param callback For handle already available data
     * @param apiUrl   For loading specified news detailed data by calling API request
     */
    public void loadNewsDetail(final DetailNewsCallback callback,
                               final String apiUrl) {
        new LoadNewsDetailTask(callback, apiUrl).execute();
    }

    /**
     * Loads news detailed data from database.
     *
     * @param callback For handle already available data
     * @param context  For accessing database
     * @param newsId   For getting a specified news
     */
    public void loadNewsDetailFromDB(final DetailNewsFromDBCallback callback,
                                     final SoftReference<Context> context,
                                     final int newsId) {
        new LoadNewsDetailFromDBTask(callback, context, newsId).execute();
    }

    /**
     * Loads pinned news.
     *
     * @param callback For handle already available data
     * @param context  For accessing shared preferences to get pinned items
     */
    public void loadPinnedNews(final PinnedNewsCallback callback, final Context context) {
        new LoadPinnedNewsTask(callback, context).execute();
    }

    /**
     * Loads available news for sending notification.
     *
     * @param callback For handle already available data
     */
    public void loadNewsNotification(final NewsNotificationCallback callback) {
        new LoadNewNewsTask(callback).execute();
    }

    /* Callback Interfaces */

    /**
     * Callback to handle new data receiving
     */
    public interface NewsCallback {
        void onNewsLoaded(List<Result> newsList);
    }

    /**
     * Callback to handle new data receiving
     */
    public interface PinnedNewsCallback {
        void onPinnedNewsLoaded(Result newPinnedNews);
    }

    /**
     * Callback to handle new data receiving
     */
    public interface DetailNewsCallback {
        void onNewsDetailLoaded(Content singleNews);
    }

    /**
     * Callback to handle new data receiving
     */
    public interface NewsFromDBCallback {
        void onNewsLoadedFromDB(List<NewsDb> newsList);
    }

    /**
     * Callback to handle new data receiving
     */
    public interface DetailNewsFromDBCallback {
        void onNewsDetailLoadedFromDB(NewsDb singleNews);
    }

    /**
     * Callback to handle new data receiving
     */
    public interface NewsNotificationCallback {
        void onNewNewsLoaded(Integer newsCount);
    }

}
