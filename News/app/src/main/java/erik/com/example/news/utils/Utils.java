package erik.com.example.news.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import erik.com.example.news.api.models.Content;
import erik.com.example.news.database.AppDataBase;
import erik.com.example.news.database.entities.NewsDb;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Utils {
    private static final int TEXT_SUMMARY_POSITION_IN_BODY = 0;

    private Utils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Saves passed news in database for app offline supporting
     *
     * @param newsContent NewsDb to save
     * @param context     For accessing database instance
     * @throws IOException When a problem occurs while processing image bytes strem
     */
    public static void saveNewsInDB(final Content newsContent,
                                    final Context context) throws IOException {
        final AppDataBase appDataBase = AppDataBase.getAppDatabase(context);
        final NewsDb newsDbForSave = new NewsDb();
        newsDbForSave.setNewsTitle(newsContent.getWebTitle());
        newsDbForSave.setNewsCategory(newsContent.getSectionName());
        newsDbForSave.setNewsDescription(newsContent.getBlocks().getBody()
                .get(TEXT_SUMMARY_POSITION_IN_BODY).getBodyTextSummary());
        Utils.getImageBytes(newsDbForSave, newsContent.getFields().getThumbnail());
        appDataBase.newsDao().insert(newsDbForSave);
    }

    /**
     * Checks is network available
     *
     * @param context For accessing connectivity manager to detect network availability
     * @return true - when network is available, false - vice versa
     */
    public static boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivity =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == connectivity) {
            return false;
        } else {
            final NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (null != info) {
                for (final NetworkInfo anInfo : info) {
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Gets news image bytes from url. This is require for saving image in database via Blob.
     *
     * @param newsDbForSave NewsDb to save
     * @param thumbnailUrl NewsDb image url to download
     * @throws IOException when processing response body
     */

    private static void getImageBytes(final NewsDb newsDbForSave,
                                      final String thumbnailUrl) throws IOException {
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(thumbnailUrl)
                .build();
        final CallbackFuture future = new CallbackFuture();
        client.newCall(request).enqueue(future);
        Response response = null;
        try {
            response = future.get();
            final ResponseBody responseBody = response.body();
            if (null == responseBody) {
                return;
            }
            final byte[] bytes = responseBody.bytes();
            newsDbForSave.setNewsImage(bytes);
        } catch (Exception e) {
            Log.d("GETImageBytes", e.getLocalizedMessage());
        }
    }

    /**
     * Gets bitmap from passed image bytes
     *
     * @param imageBytes Bytes to decode
     * @return Decoded bitmap from image bytes
     */
    public static Bitmap getBitmapFromBytes(final byte[] imageBytes) {
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    /**
     * Callback for making request call synchronous and wait for response
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    static class CallbackFuture extends CompletableFuture<Response> implements Callback {
        public void onResponse(@NonNull Call call, @NonNull Response response) {
            super.complete(response);

        }

        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            super.completeExceptionally(e);
            Log.d("GETImageBytes", e.getLocalizedMessage());
        }
    }
}
