package com.bignerdranch.coyne.udacitymoviesearch.database;

/**
 * Created by Patrick Coyne on 7/16/2017.
 */

public class MovieDbSchema {
    public static final class MovieTable{
        public static final String NAME = "movies";

        public static final class Cols{
            public static final String ID = "id";
            public static final String TITLE = "title";
            public static final String RELEASE_DATE = "releasedate";
            public static final String VOTE_AVERAGE = "voteaverage";
            public static final String POSTER_PATH = "posterpath";
            public static final String OVERVIEW = "overview";
        }
    }
}
