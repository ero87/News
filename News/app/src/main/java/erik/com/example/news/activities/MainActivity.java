package erik.com.example.news.activities;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.SearchView;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import erik.com.example.news.adapters.NewsAdapter;
import erik.com.example.news.database.entities.NewsDb;
import erik.com.example.news.api.models.Result;
import erik.com.example.news.providers.NewsDataProvider;
import erik.com.example.news.services.NewsService;
import erik.com.example.news.utils.Utils;
import erik.com.example.news.R;

import static erik.com.example.news.services.NewsService.NEWS_LOADED_INTENT_ACTION;

public class MainActivity extends AppCompatActivity implements NewsDataProvider.NewsCallback,
        NewsDataProvider.PinnedNewsCallback, NewsDataProvider.NewsFromDBCallback {

    private static final int PAGE_SIZE = 6;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.progress)
    ProgressBar progressBar;
    @BindView(R.id.pin_items_list)
    RecyclerView pinnedNewsRecyclerView;
    @BindView(R.id.news_items_list)
    RecyclerView newsRecyclerView;

    private boolean isInGridMode = false;
    private NewsAdapter newsListAdapter;
    private NewsAdapter pinnedNewsAdapter;
    private List<Result> pinnedList = new ArrayList<>();
    private List<Result> newsList = new ArrayList<>();
    private NewsDataProvider dataProvider;
    private RecyclerView.LayoutManager layoutManager;
    private MenuItem listModeMenuItem;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Utils.isNetworkAvailable(MainActivity.this)) {
                dataProvider.loadNews(MainActivity.this);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        initializeDataProvider();
        initializeNewsListView();
        initializePinnedNewsListView();
        startNewsService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        listModeMenuItem = menu.findItem(R.id.action_view_list);
        initializeSearchView(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_view_list) {
            if (isInGridMode) {
                makeListMode();
                return true;
            }
            makeGridMode();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNewsLoaded(final List<Result> newNewsList) {
        newsList.addAll(newNewsList);
        if (null != newsListAdapter) {
            newsListAdapter.notifyDataSetChanged();
        }
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onNewsLoadedFromDB(List<NewsDb> newsDbList) {
        initializeNewsListViewFromDB(newsDbList);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(
                broadcastReceiver, new IntentFilter(NEWS_LOADED_INTENT_ACTION));
        if (Utils.isNetworkAvailable(this)) {
            pinnedList.clear();
            dataProvider.loadPinnedNews(this, this);
        }
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @Override
    public void onPinnedNewsLoaded(Result newPinnedNews) {
        pinnedList.add(newPinnedNews);
        if (null != pinnedNewsAdapter) {
            pinnedNewsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE && !isInGridMode) {
            makeGridMode();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT && isInGridMode) {
            makeListMode();
        }
    }

    /* Helper Methods */

    /**
     * Initializes data provider for getting available news.
     */
    private void initializeDataProvider() {
        dataProvider = NewsDataProvider.getInstance();
        if (Utils.isNetworkAvailable(this)) {
            dataProvider.loadNews(this);
            dataProvider.loadPinnedNews(this, this);
            return;
        }
        dataProvider.loadNewsFromDB(this, new SoftReference<Context>(this));
    }

    /**
     * Starts news foreground service for getting new added news immediately.
     */
    private void startNewsService() {
        final Intent intent = new Intent(this, NewsService.class);
        startService(intent);
    }

    /**
     * Initializes news list view using data from API request.
     */
    private void initializeNewsListView() {
        layoutManager = new LinearLayoutManager(this);
        newsRecyclerView.setLayoutManager(layoutManager);
        newsRecyclerView.setHasFixedSize(true);
        newsListAdapter = new NewsAdapter(this, newsList);
        newsRecyclerView.setAdapter(newsListAdapter);
        newsRecyclerView.addOnScrollListener(recyclerViewOnScrollListener);
    }

    /**
     * Initializes pinned news list view using data from API request and shared prefs.
     */
    private void initializePinnedNewsListView() {
        pinnedNewsRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        pinnedNewsRecyclerView.setHasFixedSize(true);
        pinnedNewsAdapter = new NewsAdapter(this, pinnedList, true);
        pinnedNewsRecyclerView.setAdapter(pinnedNewsAdapter);
    }

    /**
     * Initializes news list view using data from database.
     *
     * @param newsDbList news list to iterate and fill adapter
     */
    private void initializeNewsListViewFromDB(final List<NewsDb> newsDbList) {
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsRecyclerView.setHasFixedSize(true);
        final List<Result> results = new ArrayList<>();
        for (final NewsDb savedNewsDb : newsDbList) {
            final Result result = new Result();
            result.setWebTitle(savedNewsDb.getNewsTitle());
            result.setSectionName(savedNewsDb.getNewsCategory());
            result.setDescription(savedNewsDb.getNewsDescription());
            result.setImageBytes(savedNewsDb.getNewsImage());
            results.add(result);
        }
        newsListAdapter = new NewsAdapter(this, results);
        newsRecyclerView.setAdapter(newsListAdapter);
    }

    /**
     * Initializes search view and listened search query changes, makes filter over news and
     * pinned news lists.
     *
     * @param menu toolbar's menu search item
     */
    private void initializeSearchView(final Menu menu) {
        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        if (searchManager != null) {
            searchView.setSearchableInfo(searchManager
                    .getSearchableInfo(getComponentName()));
        }
        searchView.setMaxWidth(Integer.MAX_VALUE);
        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                newsListAdapter.getFilter().filter(query);
                pinnedNewsAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                newsListAdapter.getFilter().filter(query);
                pinnedNewsAdapter.getFilter().filter(query);
                return false;
            }
        });
    }

    /**
     * Makes news list view grid mode.
     */
    private void makeGridMode() {
        layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        newsRecyclerView.setLayoutManager(layoutManager);
        listModeMenuItem.setIcon(R.drawable.ic_grid);
        isInGridMode = true;
    }

    /**
     * Makes news list view ordinal list mode.
     */
    private void makeListMode() {
        layoutManager = new LinearLayoutManager(this);
        newsRecyclerView.setLayoutManager(layoutManager);
        listModeMenuItem.setIcon(R.drawable.ic_list);
        isInGridMode = false;
    }

    /**
     * Listener for handling recycler view position changing and makes new API requests for
     * receiving new news data (pagination).
     */
    private RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            final int visibleItemCount = layoutManager.getChildCount();
            final int totalItemCount = layoutManager.getItemCount();
            int firstVisibleItemPosition = 0;
            if (layoutManager instanceof LinearLayoutManager) {
                firstVisibleItemPosition =
                        ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
            } else {
                final int[] positions = new int[2];
                ((StaggeredGridLayoutManager) layoutManager).findFirstVisibleItemPositions(positions);
                firstVisibleItemPosition = positions[0];
            }
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0
                    && totalItemCount >= PAGE_SIZE) {
                dataProvider.loadNews(MainActivity.this);
            }
        }
    };
}
