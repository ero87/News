package erik.com.example.news.adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import erik.com.example.news.activities.MainActivity;
import erik.com.example.news.activities.NewsDetailActivity;
import erik.com.example.news.api.models.Result;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import erik.com.example.news.database.AppDataBase;
import erik.com.example.news.utils.UIUtils;
import erik.com.example.news.utils.Utils;
import erik.com.example.news.R;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.PinnedNewsViewHolder> implements Filterable {

    public static final String CURRENT_ITEM_API_URL = "erik.com.example.news.adapters.CURRENT_ITEM_API_URL";
    public static final String CURRENT_ITEM_ID = "erik.com.example.news.adapters.CURRENT_ITEM_ID";

    private static final String IMAGE_TRANSITION_NAME = "newsImage";
    private static final String TEXT_TRANSITION_NAME = "newsTitle";
    private static final int VIEW_TYPE_PINNED_NEWS = 1;
    private static final int VIEW_TYPE_NEWS = 0;

    private final Context context;
    private final List<Result> newsList;
    private final boolean pinned;
    private List<Result> newsListFiltered;

    public NewsAdapter(final Context context, final List<Result> newsList, final boolean pinned) {
        this.context = context;
        this.newsList = newsList;
        newsListFiltered = newsList;
        this.pinned = pinned;
    }

    public NewsAdapter(final Context context, final List<Result> newsList) {
        this(context, newsList, false);
    }

    @Override
    public int getItemViewType(int position) {
        return pinned ? VIEW_TYPE_PINNED_NEWS : VIEW_TYPE_NEWS;
    }



    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    newsListFiltered = newsList;
                } else {
                    final List<Result> filteredList = new ArrayList<>();
                    for (final Result row : newsList) {
                        if (row.getWebTitle().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    newsListFiltered = filteredList;
                }
                final FilterResults filterResults = new FilterResults();
                filterResults.values = newsListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                newsListFiltered = (ArrayList<Result>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @NonNull
    @Override
    public PinnedNewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (VIEW_TYPE_NEWS == viewType) {
            return new NewsViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }
        return new PinnedNewsViewHolder(LayoutInflater.from(parent.getContext()), parent);
    }

    @Override
    public void onBindViewHolder(@NonNull PinnedNewsViewHolder holder, int position) {
        final Result news = newsListFiltered.get(position);
        holder.bind(news);
    }

    @Override
    public int getItemCount() {
        return newsListFiltered.size();
    }


    class NewsViewHolder extends PinnedNewsViewHolder {

        @BindView(R.id.news_category)
        TextView newsCategory;

        NewsViewHolder(final LayoutInflater inflater, final ViewGroup parent) {
            super(inflater.inflate(R.layout.news_item, parent, false));
            ButterKnife.bind(this, itemView);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        void bind(final Result news) {
            super.bind(news);
            final String category = news.getSectionName();
            newsCategory.setText(category);
            newsCategory.setBackground(UIUtils.getRandomColoredDrawable(category, context));
        }

    }

    public class PinnedNewsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.news_title)
        TextView newsTitle;
        @BindView(R.id.news_image)
        ImageView newsImage;

        PinnedNewsViewHolder(final LayoutInflater inflater, final ViewGroup parent) {
            super(inflater.inflate(R.layout.pinned_news_item, parent, false));
            ButterKnife.bind(this, itemView);
        }

        PinnedNewsViewHolder(View inflate) {
            super(inflate);
            ButterKnife.bind(this, itemView);
        }

        /**
         * Binds holder views by news data from specified position.
         *
         * @param news For getting specified news data
         */
        void bind(final Result news) {
            newsTitle.setText(news.getWebTitle());
            bindImage(this, news);
            itemView.setOnClickListener(getOnItemClickListener(this, news.getApiUrl()));
        }

        /* Helper Methods */

        /**
         * Gets view holder click listener, makes shared views transitions, data passing and opens
         * new detailed activity.
         *
         * @param holder For getting adapter position and access views transitions
         * @param apiUrl For passing specified news's api url into Detail screen.
         * @return On recycler view holder click listener
         */
        private View.OnClickListener getOnItemClickListener(final PinnedNewsViewHolder holder,
                                                            final String apiUrl) {
            return new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onClick(View view) {
                    final Intent intent = new Intent(context, NewsDetailActivity.class);
                    if (Utils.isNetworkAvailable(context)) {
                        intent.putExtra(CURRENT_ITEM_API_URL, apiUrl);
                    } else {
                        final String title =
                                newsListFiltered.get(holder.getAdapterPosition()).getWebTitle();
                        final AppDataBase appDataBase = AppDataBase.getAppDatabase(context);
                        final int newsId = appDataBase.newsDao().getNewsIdByTitle(title);
                        intent.putExtra(CURRENT_ITEM_ID, newsId);
                    }
                    final Pair<View, String> pairImage =
                            Pair.create((View) holder.newsImage, IMAGE_TRANSITION_NAME);
                    final Pair<View, String> pairTitle =
                            Pair.create((View) holder.newsTitle, TEXT_TRANSITION_NAME);
                    @SuppressWarnings("unchecked") final ActivityOptionsCompat optionsCompat =
                            ActivityOptionsCompat
                                    .makeSceneTransitionAnimation(
                                    (MainActivity) context, pairImage, pairTitle);
                    context.startActivity(intent, optionsCompat.toBundle());
                }
            };
        }

        /**
         * Binds image from database if api url is null and from request using Picasso image download
         * library - otherwise.
         *
         * @param holder For accessing holder's image view
         * @param news   For getting api url or image bytes.
         */
        private void bindImage(final PinnedNewsViewHolder holder, final Result news) {
            if (null == news.getFields()) {
                if (null != news.getImageBytes()) {
                    holder.newsImage.setImageBitmap(Utils.getBitmapFromBytes(news.getImageBytes()));
                }
                return;
            }
            final String thumbnailImage = news.getFields().getThumbnail();
            Picasso.get()
                    .load(thumbnailImage)
                    .placeholder(R.drawable.news_image_placeholder)
                    .into(holder.newsImage);
        }
    }
}
