package com.bignerdranch.coyne.udacitymoviesearch.contentproviderdb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bignerdranch.coyne.udacitymoviesearch.contentproviderdb.MovieContract.MovieEntry;

/**
 * Created by Patrick Coyne on 7/18/2017.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "favorites.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                MovieEntry.TABLE_NAME + " (" +
                MovieEntry.COLUMN_TITLE +", "+
                MovieEntry.COLUMN_ID + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_AVG + ", "+
                MovieEntry.COLUMN_DATE + ", "+
                MovieEntry.COLUMN_POSTER + ", "+
                MovieEntry.COLUMN_OVERVIEW + ");";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
