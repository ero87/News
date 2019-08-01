package erik.com.example.news.tasks;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import erik.com.example.news.api.NewsClient;
import erik.com.example.news.api.ServiceGenerator;
import erik.com.example.news.api.models.News;
import erik.com.example.news.constants.Constants;
import erik.com.example.news.providers.NewsDataProvider;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoadNewNewsTask extends AsyncTask<Void, Void, Integer> {
    private static final int FIRST_PAGE = 1;

    private final NewsDataProvider.NewsNotificationCallback callback;
    private Integer newsCount;

    public LoadNewNewsTask(final NewsDataProvider.NewsNotificationCallback callback) {
        this.callback = callback;
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        final NewsClient client = ServiceGenerator.createService(NewsClient.class);
        final Call<News> call =
                client.getBaseJson(Constants.API_KEY, Constants.SHOW_FIELDS_THUMBNAIL, FIRST_PAGE);
        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(@NonNull Call<News> call, @NonNull Response<News> response) {
                final News body = response.body();
                if (null == body) {
                    return;
                }
                newsCount = body.getResponse().getPageSize();
                callback.onNewNewsLoaded(newsCount);
            }

            @Override
            public void onFailure(@NonNull Call<News> call, @NonNull Throwable t) {
                Log.e("Fail to get response: ", t.getMessage());
            }
        });
        return newsCount;
    }
}
