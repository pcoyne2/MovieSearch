package com.bignerdranch.coyne.udacitymoviesearch.contentproviderdb;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Patrick Coyne on 7/18/2017.
 */

public class MovieContract {

    public static final String AUTHORITY = "com.bignerdranch.coyne.udacitymoviesearch";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+AUTHORITY);

    public static final String PATH_FAVORITES = "favorites";

    public static final class MovieEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();
        public static final String TABLE_NAME = "favorites";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_DATE = "releasedate";
        public static final String COLUMN_AVG = "voteaverage";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_OVERVIEW = "overview";
    }
}

