package erik.com.example.news.tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.List;

import erik.com.example.news.database.AppDataBase;
import erik.com.example.news.database.entities.NewsDb;
import erik.com.example.news.providers.NewsDataProvider;

public class LoadNewsFromDBTask extends AsyncTask<Void, Void, List<NewsDb>> {

    private final NewsDataProvider.NewsFromDBCallback callback;
    private final SoftReference<Context> context;

    public LoadNewsFromDBTask(final NewsDataProvider.NewsFromDBCallback callback,
                              final SoftReference<Context> context) {
        this.callback = callback;
        this.context = context;
    }

    @Override
    protected List<NewsDb> doInBackground(Void... voids) {
        final AppDataBase appDataBase = AppDataBase.getAppDatabase(context.get());
        final NewsDb[] allSavedNews = appDataBase.newsDao().loadAll();
        return Arrays.asList(allSavedNews);
    }

    @Override
    protected void onPostExecute(List<NewsDb> newsDbs) {
        super.onPostExecute(newsDbs);
        callback.onNewsLoadedFromDB(newsDbs);
    }
}

