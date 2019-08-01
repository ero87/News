package erik.com.example.news.tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.lang.ref.SoftReference;

import erik.com.example.news.database.AppDataBase;
import erik.com.example.news.database.entities.NewsDb;
import erik.com.example.news.providers.NewsDataProvider;

public class LoadNewsDetailFromDBTask extends AsyncTask<Void, Void, NewsDb> {
    private final NewsDataProvider.DetailNewsFromDBCallback callback;
    private final SoftReference<Context> context;
    private final int newsId;

    public LoadNewsDetailFromDBTask(final NewsDataProvider.DetailNewsFromDBCallback callback,
                                    final SoftReference<Context> context, final int newsId) {
        this.callback = callback;
        this.context = context;
        this.newsId = newsId;
    }

    @Override
    protected NewsDb doInBackground(Void... voids) {
        final AppDataBase appDataBase = AppDataBase.getAppDatabase(context.get());
        return appDataBase.newsDao().loadNewsById(newsId);
    }

    @Override
    protected void onPostExecute(NewsDb newsDb) {
        super.onPostExecute(newsDb);
        callback.onNewsDetailLoadedFromDB(newsDb);
    }
}
