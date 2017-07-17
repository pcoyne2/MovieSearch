package com.bignerdranch.coyne.udacitymoviesearch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bignerdranch.coyne.udacitymoviesearch.database.MovieBaseHelper;
import com.bignerdranch.coyne.udacitymoviesearch.database.MovieCursorWrapper;
import com.bignerdranch.coyne.udacitymoviesearch.database.MovieDbSchema;
import com.bignerdranch.coyne.udacitymoviesearch.database.MovieDbSchema.MovieTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Patrick Coyne on 7/16/2017.
 */

public class MovieLab {
    private static MovieLab sMovieLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static MovieLab get(Context context){
        if(sMovieLab == null){
            sMovieLab = new MovieLab(context);
        }
        return sMovieLab;
    }

    public MovieLab(Context context) {
        mContext = context;
        mDatabase = new MovieBaseHelper(mContext).getWritableDatabase();
    }

    public void addMovie(Movie c){
        ContentValues values = getContentValues(c);
        mDatabase.insert(MovieTable.NAME, null, values);
    }

    public void updateMovie(Movie Movie){
        String uuid = Movie.getId();
        ContentValues values = getContentValues(Movie);

        mDatabase.update(MovieTable.NAME, values,
                MovieTable.Cols.ID + " = ?",
                new String[]{uuid});
    }

    public void deleteMovie(Movie movie){
//        ContentValues values = getContentValues(movie);

        mDatabase.delete(MovieTable.NAME,
                MovieTable.Cols.ID + " = ?",
                new String[]{movie.getId()});
    }

    private MovieCursorWrapper queryMovies(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                MovieTable.NAME,
                null, //columns - null select all columns
                whereClause,
                whereArgs,
                null, //groupby
                null, //having
                null //orderby
        );

        return new MovieCursorWrapper(cursor);
    }

    public List<Movie> getMovies() {
        List<Movie> Movies = new ArrayList<>();

        MovieCursorWrapper cursorWrapper = queryMovies(null, null);

        try {
            cursorWrapper.moveToFirst();
            while (!cursorWrapper.isAfterLast()){
                Movies.add(cursorWrapper.getMovie());
                cursorWrapper.moveToNext();
            }
        }finally{
            cursorWrapper.close();
        }
        return Movies;
    }

    public Movie getMovie(String id){
        MovieCursorWrapper cursor = queryMovies(
                MovieTable.Cols.ID + " = ?",
                new String[]{id}
        );
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getMovie();
        } finally {
            cursor.close();
        }
    }

//    public File getPhotoFile(Movie Movie){
//        File filesDir = mContext.getFilesDir();
//        return new File(filesDir, Movie.getPhotoFileName());
//    }

    private static ContentValues getContentValues(Movie movie){
        ContentValues values = new ContentValues();
        values.put(MovieTable.Cols.ID, movie.getId().toString());
        values.put(MovieTable.Cols.TITLE, movie.getTitle());
        values.put(MovieTable.Cols.RELEASE_DATE, movie.getReleaseDate());
        values.put(MovieTable.Cols.VOTE_AVERAGE, movie.getVoteAverage());
        values.put(MovieTable.Cols.POSTER_PATH, movie.getPosterPath());
        values.put(MovieTable.Cols.OVERVIEW, movie.getOverview());
        return values;
    }
}
