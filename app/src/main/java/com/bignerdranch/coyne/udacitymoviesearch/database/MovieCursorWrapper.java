//package com.bignerdranch.coyne.udacitymoviesearch.database;
//
//import android.database.Cursor;
//import android.database.CursorWrapper;
//
//import com.bignerdranch.coyne.udacitymoviesearch.Movie;
//import com.bignerdranch.coyne.udacitymoviesearch.database.MovieDbSchema.MovieTable.Cols;
//
///**
// * Created by Patrick Coyne on 7/16/2017.
// */
//
//public class MovieCursorWrapper extends CursorWrapper {
//    public MovieCursorWrapper(Cursor cursor) {
//        super(cursor);
//    }
//
//    public Movie getMovie(){
//        String idString = getString(getColumnIndex(Cols.ID));
//        String title = getString(getColumnIndex(Cols.TITLE));
//        String releaseDate = getString(getColumnIndex(Cols.RELEASE_DATE));
//        String voteAvg = getString(getColumnIndex(Cols.VOTE_AVERAGE));
//        String posterPath = getString(getColumnIndex(Cols.POSTER_PATH));
//        String overview = getString(getColumnIndex(Cols.OVERVIEW));
//
//        Movie movie = new Movie(idString, title, releaseDate, voteAvg,
//                posterPath, overview);
//
//        return movie;
//    }
//}
