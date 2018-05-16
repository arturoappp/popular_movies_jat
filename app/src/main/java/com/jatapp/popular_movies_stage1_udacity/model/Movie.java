package com.jatapp.popular_movies_stage1_udacity.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {
    private String id;
    private String title;
    private String original_title;
    private String poster;
    private byte[] poster_offline;
    private String overview;
    private String vote_average;
    private String release_date;

    public Movie(String id, String title, String original_title, String poster,  String overview, String vote_average, String release_date) {
        this.id = id;
        this.title = title;
        this.original_title = original_title;
        this.poster = poster;
        this.overview = overview;
        this.vote_average = vote_average;
        this.release_date = release_date;
    }

    public Movie(String id, String title, String original_title, String poster, byte[] poster_offline, String overview, String vote_average, String release_date) {
        this.id = id;
        this.title = title;
        this.original_title = original_title;
        this.poster = poster;
        this.poster_offline = poster_offline;
        this.overview = overview;
        this.vote_average = vote_average;
        this.release_date = release_date;
    }

    public byte[] getPoster_offline() {
        return poster_offline;
    }

    public void setPoster_offline(byte[] poster_offline) {
        this.poster_offline = poster_offline;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getVote_average() {
        return vote_average;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.original_title);
        dest.writeString(this.poster);
        dest.writeByteArray(this.poster_offline);
        dest.writeString(this.overview);
        dest.writeString(this.vote_average);
        dest.writeString(this.release_date);
    }

    protected Movie(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.original_title = in.readString();
        this.poster = in.readString();
        this.poster_offline = in.createByteArray();
        this.overview = in.readString();
        this.vote_average = in.readString();
        this.release_date = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
