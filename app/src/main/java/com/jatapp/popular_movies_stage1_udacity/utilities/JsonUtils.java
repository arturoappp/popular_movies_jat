package com.jatapp.popular_movies_stage1_udacity.utilities;


import com.jatapp.popular_movies_stage1_udacity.model.Movie;
import com.jatapp.popular_movies_stage1_udacity.model.Review;
import com.jatapp.popular_movies_stage1_udacity.model.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    private static final String KEY_RESULTS_LIST = "results";
    private static final String KEY_ID = "id";

    //movies Json
    private static final String KEY_TITLE = "title";
    private static final String KEY_ORIGINAL_TITLE = "original_title";
    private static final String KEY_POSTER = "poster_path";
    private static final String KEY_SYNOPSIS = "overview";
    private static final String KEY_USER_RATING = "vote_average";
    private static final String KEY_RELASE_DATE = "release_date";

    //video Json
    private static final String KEY_KEY_VIDEO = "key";
    public static final String KEY_NAME = "name";
    public static final String KEY_SITE = "site";
    public static final String KEY_SIZE = "size";
    public static final String KEY_TYPE = "type";
    public static final String KEY_ISO_639_1 = "iso_639_1";

    //review Json
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_CONTENT = "content";
    public static final String KEY_URL = "url";

    public static List<Movie> parseMovieJson(String json) throws JSONException {
        List<Movie> movieList = new ArrayList<>();

        JSONObject jsonObject = new JSONObject(json);
        JSONArray jsonArrayMovies = jsonObject.optJSONArray(KEY_RESULTS_LIST);

        for (int i = 0; i < jsonArrayMovies.length(); i++) {
            JSONObject movieJSON = jsonArrayMovies.optJSONObject(i);
            String id = movieJSON.optString(KEY_ID);
            String title = movieJSON.optString(KEY_TITLE);
            String original_title = movieJSON.optString(KEY_ORIGINAL_TITLE);
            String poster = movieJSON.optString(KEY_POSTER);
            String overview = movieJSON.optString(KEY_SYNOPSIS);
            String vote_average = movieJSON.optString(KEY_USER_RATING);
            String release_date = movieJSON.optString(KEY_RELASE_DATE);

            Movie movie = new Movie(id, title, original_title, poster, overview, vote_average, release_date);
            movieList.add(movie);
        }
        return movieList;
    }

    public static List<Video> parseVideosJson(String json) throws JSONException {
        List<Video> videoList = new ArrayList<>();

        JSONObject jsonObject = new JSONObject(json);
        JSONArray jsonArray = jsonObject.optJSONArray(KEY_RESULTS_LIST);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject videoJSON = jsonArray.optJSONObject(i);
            String id = videoJSON.optString(KEY_ID);
            String key = videoJSON.optString(KEY_KEY_VIDEO);
            String name = videoJSON.optString(KEY_NAME);
            String site = videoJSON.optString(KEY_SITE);
            String size = videoJSON.optString(KEY_SIZE);
            String type = videoJSON.optString(KEY_TYPE);
            String iso = videoJSON.optString(KEY_ISO_639_1);

            Video video = new Video(id,name,site,size,type,key,iso);
            videoList.add(video);
        }
        return videoList;
    }

    public static List<Review> parseReviewJson(String json) throws JSONException {
        List<Review> reviewList = new ArrayList<>();

        JSONObject jsonObject = new JSONObject(json);
        JSONArray jsonArray = jsonObject.optJSONArray(KEY_RESULTS_LIST);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject reviewJSON = jsonArray.optJSONObject(i);
            String  author = reviewJSON.optString(KEY_AUTHOR);
            String content = reviewJSON.optString(KEY_CONTENT);
            String id = reviewJSON.optString(KEY_ID);
            String url = reviewJSON.optString(KEY_URL);

            Review review = new Review(author,content,id,url);
            reviewList.add(review);
        }
        return reviewList;
    }

    private static List<String> jSonArrayToList(JSONArray jsonArray) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0, l = jsonArray.length(); i < l; i++) {
            list.add(jsonArray.optString(i));
        }
        return list;
    }
}
