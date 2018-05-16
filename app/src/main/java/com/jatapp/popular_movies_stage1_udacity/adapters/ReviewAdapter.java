package com.jatapp.popular_movies_stage1_udacity.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jatapp.popular_movies_stage1_udacity.R;
import com.jatapp.popular_movies_stage1_udacity.model.Review;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {


    private final List<Review> mData;
    private final ReviewAdapterOnClickHandler mClickHandler;


    public ReviewAdapter(List<Review> data, ReviewAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
        mData = data;
    }

    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context mContext = parent.getContext();
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_review_movie, parent, false);
        return new ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapterViewHolder holder, int position) {
        Review review = mData.get(position);
        holder.tv_author.setText(review.getAuthor());
        holder.tv_content.setText(review.getContent());

    }

    @Override
    public int getItemCount() {
        if (null == mData) return 0;
        return mData.size();
    }

    /**
     * The interface that receives onClick messages.
     */
    public interface ReviewAdapterOnClickHandler {
        void onClick(Review item);
    }

    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tv_author)
        TextView tv_author;

        @BindView(R.id.tv_content)
        TextView tv_content;

        public ReviewAdapterViewHolder(View view) {
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
            Review item = mData.get(adapterPosition);
            mClickHandler.onClick(item);
        }
    }

    public void setData(List<Review> data) {
        mData.addAll(data);
        notifyDataSetChanged();
    }
    public void resetData() {
        mData.clear();
        notifyDataSetChanged();
    }


}
