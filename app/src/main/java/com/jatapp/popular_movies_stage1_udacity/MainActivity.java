package com.jatapp.popular_movies_stage1_udacity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.facebook.stetho.Stetho;
import com.jatapp.popular_movies_stage1_udacity.adapters.MovieAdapter;
import com.jatapp.popular_movies_stage1_udacity.data.FavoriteMovieContract;
import com.jatapp.popular_movies_stage1_udacity.model.Movie;
import com.jatapp.popular_movies_stage1_udacity.utilities.JsonUtils;
import com.jatapp.popular_movies_stage1_udacity.utilities.NetworkUtils;

import org.json.JSONException;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity implements
        LoaderCallbacks<List<Movie>>, MovieAdapter.MovieAdapterOnClickHandler
        , SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int MOVIE_LOADER_ID = 0;
    private final static int mColumnCountPortrait = 2;
    private final static int mColumnCountLandscape = 3;
    private static final String PAGE_KEY = "page";

    private MovieAdapter adapter;
    // Set the GridLayoutManager
    private static GridLayoutManager gridLayoutManager;
    public static final String MOVIE_DETAIL_INTENT = "movie_detail";
    private static String paramSortBy;

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    @BindView(R.id.tv_message)
    TextView tv_menssage;

    @BindView(R.id.bt_refresh)
    Button bt_refresh;

    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;

    private boolean loading;
    private boolean isLastPage;
    private static int pageCount = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .build());

        //Set preferences and loading
        setupSharedPreferences();
        mLoadingIndicator.setVisibility(View.INVISIBLE);

        if (gridLayoutManager == null) {
            gridLayoutManager = new GridLayoutManager(this, mColumnCountPortrait);
            recyclerView.setLayoutManager(gridLayoutManager);
        }

        //Orientation
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            ((GridLayoutManager) recyclerView.getLayoutManager()).setSpanCount(mColumnCountPortrait);
        } else {
            ((GridLayoutManager) recyclerView.getLayoutManager()).setSpanCount(mColumnCountLandscape);
        }

        //recyclerview
        final ArrayList<Movie> items = new ArrayList<>();
        adapter = new MovieAdapter(items, this);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int lastvisibleitemposition = ((GridLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                if (lastvisibleitemposition == adapter.getItemCount() - 1) {
                    if (!loading && !isLastPage && !paramSortBy.equals(getString(R.string.favorite_param_sortby_url))) {
                        loading = true;
                        loadDataFromMovieDB((++pageCount));
                    }
                }
            }
        });
        //Loder
        loadDataFromMovieDB(pageCount);
    }

    @OnClick(R.id.bt_refresh)
    public void onclick() {
        loadDataFromMovieDB(pageCount);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_settings) {
            Intent intentSetting = new Intent(this, SettingsActivity.class);
            startActivity(intentSetting);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, final Bundle args) {
        switch (id) {
            case MOVIE_LOADER_ID:
                return new MovieAsyncTaskLoader(this);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        loading = false;

        if (null == data) {
            showErrorMessage();
            return;
        }
        if (data.isEmpty()) {
            isLastPage = true;
        } else {
            adapter.setMovieData(data);
            showMovieDataView();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {

    }

    private void loadDataFromMovieDB(int pageCount) {
        int loaderId = MOVIE_LOADER_ID;
        LoaderCallbacks<List<Movie>> callback = MainActivity.this;
        Bundle bundleForLoader = new Bundle();
        bundleForLoader.putString(PAGE_KEY, String.valueOf(pageCount));
        //if favorite list.. show offline
        if (NetworkUtils.isOnline(this)|| paramSortBy.equals(getString(R.string.favorite_param_sortby_url))) {
            getSupportLoaderManager().restartLoader(loaderId, bundleForLoader, callback);
        } else {
            showErrorMessage();
        }
    }

    private void showMovieDataView() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        tv_menssage.setVisibility(View.INVISIBLE);
        bt_refresh.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        if(NetworkUtils.isOnline(this)){
            tv_menssage.setText(getString(R.string.error_data));
        }else{
            tv_menssage.setText(getString(R.string.error_connection));
        }

        mLoadingIndicator.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        tv_menssage.setVisibility(View.VISIBLE);
        bt_refresh.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(Movie movie) {
        Intent intent = new Intent(this, DetailMovieActivity.class);
        intent.putExtra(MOVIE_DETAIL_INTENT, movie);
        startActivity(intent);
    }

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        loadSortByFromPreferences(sharedPreferences);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.key_sort_by))) {
            loadSortByFromPreferences(sharedPreferences);
        }
    }

    private void loadSortByFromPreferences(SharedPreferences sharedPreferences) {
        paramSortBy = sharedPreferences.getString(getString(R.string.key_sort_by), getString(R.string.top_rated_param_sortby_url));
        resetDataAdapter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        resetDataAdapter();
    }
    private void resetDataAdapter(){
        if (adapter != null) {
            adapter.resetMovieData();
            pageCount = 1;
            isLastPage = false;
            loading = false;
        }
    }

    static class MovieAsyncTaskLoader extends AsyncTaskLoader<List<Movie>> {
        final List<Movie> mMovieData = null;
        private Context mContext;

        public MovieAsyncTaskLoader(Context context) {
            super(context);
            mContext = context;
        }

        @Override
        protected void onStartLoading() {
            if (mMovieData != null) {
                deliverResult(mMovieData);
            } else {
                //mLoadingIndicator.setVisibility(View.VISIBLE);
                //to ignore a previously loaded
                forceLoad();
            }
        }

        public List<Movie> loadInBackground() {
            if(paramSortBy.equals(mContext.getString(R.string.favorite_param_sortby_url) )) {
                Cursor data;
                try {
                    Uri uri = FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI;
                    //uri = uri.buildUpon().appendPath(mMovieId).build();
                    data = mContext.getContentResolver().query(uri,
                            null,
                            null,
                            null,
                            null);
                    int count = data.getCount();
                } catch (Exception e) {
                    Log.e(TAG, "Failed to load data.");
                    e.printStackTrace();
                    return null;
                }

                ArrayList<Movie> mArrayList = new ArrayList<Movie>();
                for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
                    // The Cursor is now set to the right position
                    int id_index = data.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_FAVORITE_ID);
                    int originaltitle_index = data.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_ORIGINAL_TITLE);
                    int overview_index = data.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_OVERVIEW);
                    int poster_index = data.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_POSTER);
                    int poster_offline_index = data.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_POSTER_OFFLINE);
                    int releasedate_index = data.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE);
                    int title_index = data.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_TITLE);
                    int vote_average_index = data.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_VOTE_AVERAGE);

                    String id = data.getString(id_index);
                    String originaltitle = data.getString(originaltitle_index);
                    String overview = data.getString(overview_index);
                    String poster = data.getString(poster_index);
                    byte[] poster_offline = data.getBlob(poster_offline_index);
                    String releasedate = data.getString(releasedate_index);
                    String title = data.getString(title_index);
                    String vote_average = data.getString(vote_average_index);

                    mArrayList.add(new Movie(id, title, originaltitle, poster,poster_offline, overview, vote_average, releasedate));
                }
                return mArrayList;
            }else {
                //String page = args.getString(PAGE_KEY);
                String api_key = mContext.getString(R.string.movie_app_key);
                URL movieRequestUrl = NetworkUtils.buildUrlMovies(paramSortBy, String.valueOf(pageCount), api_key);

                try {
                    String jsonMoviesResponse = NetworkUtils
                            .getResponseFromHttpUrl(movieRequestUrl);

                    List<Movie> myJsonMoviesData = JsonUtils.parseMovieJson(jsonMoviesResponse);

                    return myJsonMoviesData;
                } catch (JSONException JSONException) {
                    JSONException.printStackTrace();
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }

        public void deliverResult(List<Movie> data) {
            super.deliverResult(data);
        }
    }


}
