package erik.com.example.news.tasks;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import erik.com.example.news.api.NewsClient;
import erik.com.example.news.api.ServiceGenerator;
import erik.com.example.news.api.models.Content;
import erik.com.example.news.api.models.SingleNews;
import erik.com.example.news.constants.Constants;
import erik.com.example.news.providers.NewsDataProvider;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import erik.com.example.news.api.models.Result;

public class LoadNewsDetailTask extends AsyncTask<Void, Void, Result> {

    private final NewsDataProvider.DetailNewsCallback callback;
    private final String singleNewsAPIUrl;
    private Content newsContent;

    public LoadNewsDetailTask(final NewsDataProvider.DetailNewsCallback callback,
                              final String singleNewsAPIUrl) {

        this.callback = callback;
        this.singleNewsAPIUrl = singleNewsAPIUrl;
    }

    @Override
    protected Result doInBackground(Void... voids) {
        final NewsClient client =
                ServiceGenerator.createService(NewsClient.class);
        final Call<SingleNews> call = client.getSingleNewsJson(singleNewsAPIUrl, Constants.API_KEY,
                Constants.SHOW_BLOCKS, Constants.SHOW_FIELDS_THUMBNAIL);
        call.enqueue(new Callback<SingleNews>() {
            @Override
            public void onResponse(@NonNull Call<SingleNews> call, @NonNull Response<SingleNews> response) {
                final SingleNews body = response.body();
                if (null == body) {
                    return;
                }
                newsContent = body.getResponse().getContent();
                callback.onNewsDetailLoaded(newsContent);
            }

            @Override
            public void onFailure(@NonNull Call<SingleNews> call, @NonNull Throwable t) {
                Log.e("Fail to get results: ", t.getMessage());
            }
        });
        return null;
    }
}
