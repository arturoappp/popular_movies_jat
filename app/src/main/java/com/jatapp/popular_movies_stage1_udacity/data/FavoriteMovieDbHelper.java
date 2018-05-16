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
package com.jatapp.popular_movies_stage1_udacity.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jatapp.popular_movies_stage1_udacity.data.FavoriteMovieContract.FavoriteMovieEntry;


public class FavoriteMovieDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "favorite.db";
    private static final int DATABASE_VERSION = 1;

    public FavoriteMovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_FAVORITE_TABLE =

                "CREATE TABLE " + FavoriteMovieEntry.TABLE_NAME + " (" +
                        FavoriteMovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        FavoriteMovieEntry.COLUMN_FAVORITE_ID + " INTEGER NOT NULL, " +
                        FavoriteMovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT ," +
                        FavoriteMovieEntry.COLUMN_OVERVIEW + " TEXT , " +
                        FavoriteMovieEntry.COLUMN_POSTER + " TEXT , " +
                        FavoriteMovieEntry.COLUMN_POSTER_OFFLINE + " BLOB , " +
                        FavoriteMovieEntry.COLUMN_RELEASE_DATE + " TEXT , " +
                        FavoriteMovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                        FavoriteMovieEntry.COLUMN_VOTE_AVERAGE + " TEXT  ); ";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteMovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}