package erik.com.example.news.tasks;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.List;

import erik.com.example.news.api.NewsClient;
import erik.com.example.news.api.ServiceGenerator;
import erik.com.example.news.api.models.News;
import erik.com.example.news.api.models.Result;
import erik.com.example.news.constants.Constants;
import erik.com.example.news.providers.NewsDataProvider;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoadNewsTask extends AsyncTask<Void, Void, List<Result>> {

    private static int pageSize = 0;

    private final NewsDataProvider.NewsCallback callback;
    private List<Result> results;

    public LoadNewsTask(final NewsDataProvider.NewsCallback callback) {
        this.callback = callback;
        pageSize++;
    }

    @Override
    protected List<Result> doInBackground(Void... voids) {
        final NewsClient client = ServiceGenerator.createService(NewsClient.class);
        final Call<News> call =
                client.getBaseJson(Constants.API_KEY, Constants.SHOW_FIELDS_THUMBNAIL, pageSize);
        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(@NonNull Call<News> call, @NonNull Response<News> response) {
                final News body = response.body();
                if (null == body) {
                    return;
                }
                results = body.getResponse().getResults();
                callback.onNewsLoaded(results);
            }

            @Override
            public void onFailure(@NonNull Call<News> call, @NonNull Throwable t) {
                Log.e("Fail to get results: ", t.getMessage());
            }
        });
        return results;
    }
}

