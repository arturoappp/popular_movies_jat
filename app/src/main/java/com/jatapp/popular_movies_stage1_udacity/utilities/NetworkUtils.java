/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jatapp.popular_movies_stage1_udacity.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the network.
 */
public class NetworkUtils {

    private final static String THEMOVIEDB_BASE_URL ="http://api.themoviedb.org/3/movie/";

    private final static String PARAM_API_KEY = "api_key";

    private final static String IMAGE_BASE_URL ="http://image.tmdb.org/t/p/";
    private final static String PARAM_PAGE = "page";
    private final static String PARAM_VIDEOS = "videos";
    private final static String PARAM_REVIEWS = "reviews";
    private final static String IMAGE_SIZE ="w185/";

    public static final String HTTP_IMG_YOUTUBE_COM_VI = "http://img.youtube.com/vi";
    public static final String HTTP_WWW_YOUTUBE_COM_WATCH = "http://www.youtube.com/watch";
    public static final String DEFAUL_FILE_JPG = "0.jpg";
    public static final String PARAM_V = "v";


    public static URL buildUrlReviews(String movieId,String pg,  String api_key) {
        Uri builtUri = Uri.parse(THEMOVIEDB_BASE_URL).buildUpon()
                .appendPath(movieId)
                .appendPath(PARAM_REVIEWS)
                .appendQueryParameter(PARAM_API_KEY, api_key)
                .appendQueryParameter(PARAM_PAGE, pg)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildUrlVideos(String movieId, String api_key) {
        Uri builtUri = Uri.parse(THEMOVIEDB_BASE_URL).buildUpon()
                .appendPath(movieId)
                .appendPath(PARAM_VIDEOS)
                .appendQueryParameter(PARAM_API_KEY, api_key)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }


    public static URL buildUrlMovies(String sortBy, String pg, String api_key) {
        Uri builtUri = Uri.parse(THEMOVIEDB_BASE_URL).buildUpon()
                .appendPath(sortBy)
                .appendQueryParameter(PARAM_API_KEY, api_key)
                .appendQueryParameter(PARAM_PAGE, pg)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }
    public static URL buildUrlImage(String image_url) {
        Uri builtUri = Uri.parse(IMAGE_BASE_URL).buildUpon()
                .appendEncodedPath(IMAGE_SIZE)
                .appendEncodedPath(image_url)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static Uri buildUriYoutubeApp(String key) {
        Uri builtUri = Uri.parse("vnd.youtube:"+key).buildUpon()
                .build();
        return builtUri;
    }

    public static Uri buildUriYoutubeWeb(String key) {
        Uri builtUri = Uri.parse(HTTP_WWW_YOUTUBE_COM_WATCH).buildUpon()
                .appendQueryParameter(PARAM_V,key)
                .build();
        return builtUri;

    }
    public static String getYoutubeImagPath(String key) {
        String imgUrl = HTTP_IMG_YOUTUBE_COM_VI+"/"+key+"/"+ DEFAUL_FILE_JPG;
        return imgUrl;
    }

    public static Uri getYoutubeImagUrl(String key) {
        Uri builtUri = Uri.parse(HTTP_IMG_YOUTUBE_COM_VI).buildUpon()
                .appendPath(key)
                .appendPath(DEFAUL_FILE_JPG)
                .build();
        return builtUri;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }






}