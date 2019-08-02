package erik.com.example.news.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.ref.SoftReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import erik.com.example.news.R;
import erik.com.example.news.adapters.NewsAdapter;
import erik.com.example.news.api.models.Content;
import erik.com.example.news.database.AppDataBase;
import erik.com.example.news.database.entities.NewsDb;
import erik.com.example.news.providers.NewsDataProvider;
import erik.com.example.news.utils.Utils;

public class NewsDetailActivity extends AppCompatActivity implements
        NewsDataProvider.DetailNewsCallback, NewsDataProvider.DetailNewsFromDBCallback {

    private static final String NO_WEB_CONTENT_HTML = "file:///android_asset/noContent.html";

    public static final String PINNED_NEWS_SHARED_PREF_KEY_FILE =
            "erik.com.example.news.activities.NewsDetailActivity.PINNED_NEWS_SHARED_PREF_KEY_FILE";
    public static final String PINNED_ITEM_URL_KEY =
            "erik.com.example.news.activities.NewsDetailActivity.PINNED_ITEM_URL_KEY";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.news_image)
    ImageView newsImage;
    @BindView(R.id.news_title)
    TextView newsTitle;
    @BindView(R.id.news_description)
    TextView newsDescription;
    @BindView(R.id.news_web_content)
    WebView newsWebContent;
    @BindView(R.id.save_news)
    FloatingActionButton saveNewsButton;
    @BindView(R.id.pin_news)
    FloatingActionButton pinNewsButton;
    private SharedPreferences sharedPreferences;
    private String getSharedPreferencesKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        ButterKnife.bind(this);
        initializeToolbar();
        initializeDataProvider();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNewsDetailLoaded(Content singleNews) {
        initializeUI(singleNews);
    }

    @Override
    public void onNewsDetailLoadedFromDB(NewsDb singleNewsDb) {
        initializeUIFromDB(singleNewsDb);
    }

    /* Helper Methods */

    /**
     * Initializes data provider for getting available news.
     */
    private void initializeDataProvider() {
        final String apiUrl = getIntent().getStringExtra(NewsAdapter.CURRENT_ITEM_API_URL);
        final NewsDataProvider dataProvider = NewsDataProvider.getInstance();
        if (null == apiUrl) {
            final int newsId = getIntent().getIntExtra(NewsAdapter.CURRENT_ITEM_ID, 0);
            dataProvider.loadNewsDetailFromDB(this,
                    new SoftReference<Context>(this), newsId);
            return;
        }
        dataProvider.loadNewsDetail(this, apiUrl);
        sharedPreferences = getSharedPreferences(PINNED_NEWS_SHARED_PREF_KEY_FILE, MODE_PRIVATE);
    }

    /**
     * Initializes action bar and toolbar.
     */
    private void initializeToolbar() {
        toolbar.setTitle(null);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Initializes UI elements using data from API request.
     *
     * @param singleNews For bind views data from single news
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void initializeUI(final Content singleNews) {
        Picasso.get()
                .load(singleNews.getFields().getThumbnail())
                .placeholder(R.drawable.news_image_placeholder)
                .into(newsImage);
        newsTitle.setText(singleNews.getWebTitle());
        newsDescription.setText(singleNews.getBlocks().getBody().get(0).getBodyTextSummary());
        newsWebContent.getSettings().setJavaScriptEnabled(true);
        newsWebContent.loadUrl(singleNews.getWebUrl());
        getSharedPreferencesKey = sharedPreferences.getString(PINNED_ITEM_URL_KEY + singleNews.getId(),"fail");
        if (!getSharedPreferencesKey.equals("fail")) {
            pinNewsButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark)));
        }
        if (checkNewsExistInDb(singleNews)) {
            saveNewsButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark)));
        }
        handleButtonsClick(singleNews);
    }

    /**
     * Initializes UI elements using data from database.
     *
     * @param singleNewsDb For bind views data from single news
     */
    private void initializeUIFromDB(final NewsDb singleNewsDb) {
        newsImage.setImageBitmap(Utils.getBitmapFromBytes(singleNewsDb.getNewsImage()));
        newsTitle.setText(singleNewsDb.getNewsTitle());
        newsDescription.setText(singleNewsDb.getNewsDescription());
        newsWebContent.loadUrl(NO_WEB_CONTENT_HTML);
    }


    /**
     * Handles "save in database" and "pin in home screen" buttons click and corresponding logic.
     *
     * @param singleNews For accessing specified news data
     */
    private void handleButtonsClick(final Content singleNews) {
        saveNewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkNewsExistInDb(singleNews)) {
                    try {
                        Utils.saveNewsInDB(singleNews, NewsDetailActivity.this);
                        Toast.makeText(NewsDetailActivity.this,
                                R.string.news_saved_message, Toast.LENGTH_SHORT).show();
                        saveNewsButton.setEnabled(false);
                        saveNewsButton.setBackgroundTintList(ColorStateList.valueOf(getResources()
                                .getColor(R.color.colorPrimaryDark)));
                    } catch (IOException e) {
                        Log.d("Failed To Save NewsDb", e.getLocalizedMessage());
                        Toast.makeText(NewsDetailActivity.this,
                                R.string.failed_to_save, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(NewsDetailActivity.this,
                            R.string.news_pinned_message , Toast.LENGTH_SHORT).show();
                }
            }
        });
        pinNewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getSharedPreferencesKey.equals("fail")) {
                    Log.d("testTag", "onClick: ");
                    sharedPreferences.edit().putString(PINNED_ITEM_URL_KEY + singleNews.getId(), singleNews.getApiUrl()).apply();
                    pinNewsButton.setEnabled(false);
                    Toast.makeText(NewsDetailActivity.this,
                            R.string.news_pinned_message, Toast.LENGTH_SHORT).show();
                    pinNewsButton.setBackgroundTintList(ColorStateList.valueOf(getResources()
                            .getColor(R.color.colorPrimaryDark)));
                } else {
                    Toast.makeText(NewsDetailActivity.this, R.string.news_exists_message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean checkNewsExistInDb(final Content singleNews) {
        final AppDataBase appDataBase = AppDataBase.getAppDatabase(this);
        final int newsId = appDataBase.newsDao().getNewsIdByTitle(singleNews.getWebTitle());
        NewsDb currentNews = appDataBase.newsDao().loadNewsById(newsId);
        return currentNews != null;
    }
}

