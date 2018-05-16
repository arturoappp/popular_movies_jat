package com.jatapp.popular_movies_stage1_udacity.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jatapp.popular_movies_stage1_udacity.R;
import com.jatapp.popular_movies_stage1_udacity.model.Video;
import com.jatapp.popular_movies_stage1_udacity.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoAdapterViewHolder> {


    private final List<Video> mData;
    private final VideoAdapterOnClickHandler mClickHandler;
    private Context mContext;


    public VideoAdapter(List<Video> data, VideoAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
        mData = data;
    }

    @Override
    public VideoAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_video_movie, parent, false);
        return new VideoAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoAdapterViewHolder holder, int position) {
        Video video = mData.get(position);
        holder.tv_name.setText(video.getName());
        holder.tv_iso.setText(video.getType()+" - "+video.getIso());
        Uri img_path = NetworkUtils.getYoutubeImagUrl(video.getKey());
        Picasso.with(mContext)
                .load(img_path)
                .placeholder(R.drawable.ic_video_black_24dp)
                .into(holder.img_video);
    }

    @Override
    public int getItemCount() {
        if (null == mData) return 0;
        return mData.size();
    }

    /**
     * The interface that receives onClick messages.
     */
    public interface VideoAdapterOnClickHandler {
        void onClick(Video video);
    }

    public class VideoAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tv_name)
        TextView tv_name;

        @BindView(R.id.tv_iso)
        TextView tv_iso;

        @BindView(R.id.img_video)
        ImageView img_video;

        public VideoAdapterViewHolder(View view) {
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
            Video video = mData.get(adapterPosition);
            mClickHandler.onClick(video);
        }
    }

    public void setData(List<Video> data) {
        mData.addAll(data);
        notifyDataSetChanged();
    }
    public void resetData() {
        mData.clear();
        notifyDataSetChanged();
    }


}
