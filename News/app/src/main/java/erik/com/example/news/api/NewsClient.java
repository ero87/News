package erik.com.example.news.api;

import erik.com.example.news.api.models.News;
import erik.com.example.news.api.models.SingleNews;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface NewsClient {
    @GET("search")
    Call<News> getBaseJson(
            // API key should always be constant
            @Query("api-key") String apiKey,
            // For getting thumbnail image
            @Query("show-fields") String fields,
            // For pagination
            @Query("page") int page);

    @GET
    Call<SingleNews> getSingleNewsJson(
            @Url String url,
            // API key should always be constant
            @Query("api-key") String apiKey,
            // For getting all content
            @Query("show-blocks") String showBlock,
            // For getting thumbnail image
            @Query("show-fields") String fields);
}
