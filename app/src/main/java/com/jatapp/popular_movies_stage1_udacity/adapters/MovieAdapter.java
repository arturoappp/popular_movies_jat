package com.jatapp.popular_movies_stage1_udacity.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jatapp.popular_movies_stage1_udacity.R;
import com.jatapp.popular_movies_stage1_udacity.model.Movie;
import com.jatapp.popular_movies_stage1_udacity.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private final List<Movie> mMovieData;
    private final MovieAdapterOnClickHandler mClickHandler;
    private Context mContext;


    public MovieAdapter(List<Movie> movieData, MovieAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
        mMovieData = movieData;
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_poster_movie, parent, false);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        Movie movie = mMovieData.get(position);
        URL url = NetworkUtils.buildUrlImage(movie.getPoster());
        String img_path = url.toString();
        Picasso.with(mContext).load(img_path).placeholder(R.drawable.ic_video_black_24dp).into(holder.img_poster);
    }

    @Override
    public int getItemCount() {
        if (null == mMovieData) return 0;
        return mMovieData.size();
    }

    /**
     * The interface that receives onClick messages.
     */
    public interface MovieAdapterOnClickHandler {
        void onClick(Movie movie);
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.img_poster)
        ImageView img_poster;

        public MovieAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Movie movie = mMovieData.get(adapterPosition);
            mClickHandler.onClick(movie);
        }
    }

    public void setMovieData(List<Movie> MovieData) {
        mMovieData.addAll(MovieData);
        notifyDataSetChanged();
    }
    public void resetMovieData() {
        mMovieData.clear();
        notifyDataSetChanged();
    }
}
