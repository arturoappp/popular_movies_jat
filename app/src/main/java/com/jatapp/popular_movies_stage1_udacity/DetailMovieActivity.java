package com.jatapp.popular_movies_stage1_udacity;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jatapp.popular_movies_stage1_udacity.adapters.ReviewAdapter;
import com.jatapp.popular_movies_stage1_udacity.adapters.VideoAdapter;
import com.jatapp.popular_movies_stage1_udacity.data.FavoriteMovieContract;
import com.jatapp.popular_movies_stage1_udacity.model.Movie;
import com.jatapp.popular_movies_stage1_udacity.model.Review;
import com.jatapp.popular_movies_stage1_udacity.model.Video;
import com.jatapp.popular_movies_stage1_udacity.utilities.JsonUtils;
import com.jatapp.popular_movies_stage1_udacity.utilities.NetworkUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailMovieActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List>,
        VideoAdapter.VideoAdapterOnClickHandler
        , ReviewAdapter.ReviewAdapterOnClickHandler {

    private static final String TAG = DetailMovieActivity.class.getSimpleName();

    //Detail data
    @BindView(R.id.img_poster)
    ImageView img_poster;

    @BindView(R.id.title_tv)
    TextView title_tv;

    @BindView(R.id.user_rating_tv)
    TextView user_rating_tv;

    @BindView(R.id.release_date_tv)
    TextView release_date_tv;

    @BindView(R.id.synopsis_tv)
    TextView synopsis_tv;

    @BindView(R.id.favorite_tv)
    TextView favorite_tv;

    //recycle video
    @BindView(R.id.recyclerviewVideo)
    RecyclerView recyclerViewVideo;
    @BindView(R.id.data_vidio_tv)
    TextView data_vidio_tv;
    //loading
    @BindView(R.id.pb_loading_indicator_video)
    ProgressBar pb_loading_indicator_video;


    //recycle Review
    @BindView(R.id.recyclerviewReview)
    RecyclerView recyclerviewReview;
    @BindView(R.id.data_review_tv)
    TextView data_review_tv;
    //loading
    @BindView(R.id.pb_loading_indicator_review)
    ProgressBar pb_loading_indicator_review;

    //Loaders id
    private static final int VIDEO_LOADER_ID = 0;
    private static final int REVIEW_LOADER_ID = 1;
    private static final int FAVORITE_LOADER_ID = 2;
    private Movie mMovie;

    //recycleview load pg
    private boolean loading;
    private boolean isLastPage;
    private int pageReviewCount = 1;
    private boolean isFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);
        ButterKnife.bind(this);
        Intent intentMovieDetail = getIntent();

        if (intentMovieDetail == null || !intentMovieDetail.hasExtra(MainActivity.MOVIE_DETAIL_INTENT)) {
            closeOnError();
        } else {
            mMovie = intentMovieDetail.getParcelableExtra(MainActivity.MOVIE_DETAIL_INTENT);
            //Toast.makeText(this, mMovie.getTitle(), Toast.LENGTH_LONG).show();
            setTitle(mMovie.getTitle());
            populateUI(mMovie);

            //call loaders
            String mMovieId = mMovie.getId();
            if (mMovieId != null || !mMovieId.isEmpty()) {
                if (NetworkUtils.isOnline(this)) {
                    getSupportLoaderManager().initLoader(VIDEO_LOADER_ID, null, this);
                    getSupportLoaderManager().initLoader(REVIEW_LOADER_ID, null, this);
                } else {
                    //showErrorMessage(getString(R.string.error_connection));
                }
                getSupportLoaderManager().initLoader(FAVORITE_LOADER_ID, null, cursorLoaderCallbacks);
            }
        }
    }

    //loader to check favorite
    LoaderManager.LoaderCallbacks<Cursor> cursorLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            switch (id) {
                case FAVORITE_LOADER_ID:
                    return new FavoriteAsyncTaskLoader(DetailMovieActivity.this, mMovie.getId());
                default:
                    return null;
            }
        }
        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (loader.getId() == FAVORITE_LOADER_ID) {
                if (data == null || data.getCount() == 0) {
                    favorite_tv.setText(getString(R.string.make_it_your_favorite));
                    favorite_tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_favorite_border_24dp);
                    isFavorite = false;
                } else {
                    favorite_tv.setText(getString(R.string.you_like_it_favorite));
                    favorite_tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_favorite_24dp);
                    isFavorite = true;
                }
            }
        }
        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };


    //callbacks loader for videos and reviews
    @Override
    public Loader<List> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case VIDEO_LOADER_ID:
                return new VideoAsyncTaskLoader(this, mMovie.getId(), pb_loading_indicator_video);
            case REVIEW_LOADER_ID:
                return new ReviewAsyncTaskLoader(this, mMovie.getId(), pb_loading_indicator_review, pageReviewCount);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<List> loader, List data) {

        if (null == data || data.isEmpty()) {
            if (loader.getId() == VIDEO_LOADER_ID) {
                showErrorMessageVideos();
            } else if (loader.getId() == REVIEW_LOADER_ID) {
                showErrorMessageReview();
            }
        } else {
            if (loader.getId() == VIDEO_LOADER_ID) {
                //recyclerview Video
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                recyclerViewVideo.setLayoutManager(linearLayoutManager);
                final ArrayList<Video> items = new ArrayList<>(data);
                VideoAdapter adapter = new VideoAdapter(items, this);
                recyclerViewVideo.setAdapter(adapter);

                showMovieDataViewVideo();
            } else if (loader.getId() == REVIEW_LOADER_ID) {
                //recyclerview Review
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

                recyclerviewReview.setLayoutManager(linearLayoutManager);
                final ArrayList<Review> items = new ArrayList<>(data);
                final ReviewAdapter adapter = new ReviewAdapter(items, this);
                recyclerviewReview.setAdapter(adapter);
                recyclerviewReview.setNestedScrollingEnabled(false);
                recyclerviewReview.setHasFixedSize(true);
                recyclerviewReview.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        int lastvisibleitemposition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                        if (lastvisibleitemposition == adapter.getItemCount() - 1) {
                            if (!loading && !isLastPage) {
                                loading = true;
                                pageReviewCount++;
                                int loaderId = REVIEW_LOADER_ID;
                                LoaderManager.LoaderCallbacks<List> callback = DetailMovieActivity.this;
                                Bundle bundleForLoader = new Bundle();
                                if (NetworkUtils.isOnline(DetailMovieActivity.this)) {
                                    getSupportLoaderManager().restartLoader(loaderId, bundleForLoader, callback);
                                } else {
                                    //showErrorMessage(getString(R.string.error_connection));
                                }
                            }
                        }
                    }
                });
                showMovieDataViewReview();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<List> loader) {

    }

    private void populateUI(Movie movie) {
        title_tv.setText(movie.getOriginal_title());
        user_rating_tv.setText(movie.getVote_average());
        release_date_tv.setText(movie.getRelease_date());
        synopsis_tv.setText(movie.getOverview());

        //if there is data is a favorite.. can show offline pic
        if (movie.getPoster_offline() == null) {
            URL url = NetworkUtils.buildUrlImage(movie.getPoster());
            String img_path = url.toString();
            Picasso.with(this).load(img_path).into(img_poster, new Callback() {
                //getting byte[] to save img offline favorite
                @Override
                public void onSuccess() {
                    Log.d("PICASSO", "onSuccess");
                    Bitmap bitmap = ((BitmapDrawable) img_poster.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageInByte = baos.toByteArray();
                    mMovie.setPoster_offline(imageInByte);
                }

                @Override
                public void onError() {
                    Log.d("PICASSO", "onError");
                }
            });
        } else {
            Bitmap bmp = BitmapFactory.decodeByteArray(movie.getPoster_offline(), 0, movie.getPoster_offline().length);
            img_poster.setImageBitmap(bmp);
            Toast.makeText(this, "Favorite IMG", Toast.LENGTH_SHORT).show();
        }
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    private void showErrorMessageVideos() {
        data_vidio_tv.setVisibility(View.VISIBLE);
        recyclerViewVideo.setVisibility(View.INVISIBLE);
        pb_loading_indicator_video.setVisibility(View.INVISIBLE);

    }

    private void showErrorMessageReview() {
        data_review_tv.setVisibility(View.VISIBLE);
        recyclerviewReview.setVisibility(View.INVISIBLE);
        pb_loading_indicator_review.setVisibility(View.INVISIBLE);

    }

    private void showMovieDataViewVideo() {
        pb_loading_indicator_video.setVisibility(View.INVISIBLE);
        data_vidio_tv.setVisibility(View.INVISIBLE);
        recyclerViewVideo.setVisibility(View.VISIBLE);
    }

    private void showMovieDataViewReview() {
        data_review_tv.setVisibility(View.INVISIBLE);
        recyclerviewReview.setVisibility(View.VISIBLE);
        pb_loading_indicator_review.setVisibility(View.INVISIBLE);
    }


    static class VideoAsyncTaskLoader extends AsyncTaskLoader<List> {
        Context context;
        String movieId;
        ProgressBar mLoadingIndicatorVideo;

        public VideoAsyncTaskLoader(Context context, String movieId, ProgressBar mLoadingIndicatorVideo) {
            super(context);
            this.context = context;
            this.movieId = movieId;
            this.mLoadingIndicatorVideo = mLoadingIndicatorVideo;
        }

        final List<Movie> mData = null;

        @Override
        protected void onStartLoading() {
            if (mData != null) {
                deliverResult(mData);
            } else {
                mLoadingIndicatorVideo.setVisibility(View.VISIBLE);
                //to ignore a previously loaded
                forceLoad();
            }
        }

        @Override
        public List loadInBackground() {
            String api_key = context.getString(R.string.movie_app_key);
            URL daoRequestUrl = NetworkUtils.buildUrlVideos(movieId, api_key);
            try {
                String jSonResponse = NetworkUtils.getResponseFromHttpUrl(daoRequestUrl);
                List<Video> myJsonData = JsonUtils.parseVideosJson(jSonResponse);
                return myJsonData;
            } catch (org.json.JSONException JSONException) {
                JSONException.printStackTrace();
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public void deliverResult(List data) {
            super.deliverResult(data);
        }
    }


    static class ReviewAsyncTaskLoader extends AsyncTaskLoader<List> {
        Context context;
        String movieId;
        ProgressBar mLoadingIndicatorVideo;
        int page;

        public ReviewAsyncTaskLoader(Context context, String movieId, ProgressBar loadingIndicatorVideo, int page) {
            super(context);
            this.context = context;
            this.movieId = movieId;
            this.mLoadingIndicatorVideo = loadingIndicatorVideo;
            this.page = page;
        }

        final List<Review> mData = null;

        @Override
        protected void onStartLoading() {
            if (mData != null) {
                deliverResult(mData);
            } else {
                mLoadingIndicatorVideo.setVisibility(View.VISIBLE);
                //to ignore a previously loaded
                forceLoad();
            }
        }

        @Override
        public List loadInBackground() {
            String api_key = context.getString(R.string.movie_app_key);
            URL daoRequestUrl = NetworkUtils.buildUrlReviews(movieId, String.valueOf(page), api_key);
            try {
                String jSonResponse = NetworkUtils.getResponseFromHttpUrl(daoRequestUrl);
                List<Review> myJsonData = JsonUtils.parseReviewJson(jSonResponse);
                return myJsonData;
            } catch (org.json.JSONException JSONException) {
                JSONException.printStackTrace();
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public void deliverResult(List data) {
            if (data.isEmpty()) {
            } else {
                //adapter.setMovieData(data);
            }
            super.deliverResult(data);
        }
    }

    static class FavoriteAsyncTaskLoader extends AsyncTaskLoader<Cursor> {
        Context mContext;
        Cursor mData = null;
        String mMovieId;

        public FavoriteAsyncTaskLoader(Context context, String movieId) {
            super(context);
            mContext = context;
            mMovieId = movieId;
        }

        @Override
        protected void onStartLoading() {
            if (mData != null) {
                // Delivers any previously loaded data immediately
                deliverResult(mData);
            } else {
                // Force a new load
                forceLoad();
            }
        }

        @Override
        public Cursor loadInBackground() {
            try {

                Uri uri = FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(mMovieId).build();

                Cursor mCursor = mContext.getContentResolver().query(uri,
                        null,
                        null,
                        null,
                        null);
                int count = mCursor.getCount();
                return mCursor;
            } catch (Exception e) {
                Log.e(TAG, "Failed to load data.");
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public void deliverResult(Cursor data) {
            mData = data;
            super.deliverResult(data);
        }
    }

    @OnClick(R.id.favorite_tv)
    public void adFavorite(View view) {
        if (isFavorite) {
            //remove favorite
            Uri uri = FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(mMovie.getId()).build();

            int count = getContentResolver().delete(uri, null, null);
            if (uri != null) {
                Toast.makeText(getBaseContext(), uri.toString() + " [delete]" + count, Toast.LENGTH_LONG).show();
                getSupportLoaderManager().restartLoader(FAVORITE_LOADER_ID, null, cursorLoaderCallbacks);
            }

        } else {
            //add favorite
            ContentValues contentValues = new ContentValues();
            contentValues.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_FAVORITE_ID, mMovie.getId());
            contentValues.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_TITLE, mMovie.getTitle());
            contentValues.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_ORIGINAL_TITLE, mMovie.getOriginal_title());
            contentValues.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_OVERVIEW, mMovie.getOverview());
            contentValues.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_POSTER, mMovie.getPoster());
            contentValues.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_POSTER_OFFLINE, mMovie.getPoster_offline());
            contentValues.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE, mMovie.getRelease_date());
            contentValues.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_VOTE_AVERAGE, mMovie.getVote_average());

            Uri uri = getContentResolver().insert(FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI, contentValues);
            if (uri != null) {
                Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
                getSupportLoaderManager().restartLoader(FAVORITE_LOADER_ID, null, cursorLoaderCallbacks);
            }
        }
    }


    @Override
    public void onClick(Video video) {
        //Intent youtube
        Intent appIntent = new Intent(Intent.ACTION_VIEW, NetworkUtils.buildUriYoutubeApp(video.getKey()));
        Intent webIntent = new Intent(Intent.ACTION_VIEW, NetworkUtils.buildUriYoutubeWeb(video.getKey()));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }

    @Override
    public void onClick(Review item) {

    }


}
