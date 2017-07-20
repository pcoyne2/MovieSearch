//package com.bignerdranch.coyne.udacitymoviesearch.database;
//
//import android.content.Context;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//
//import com.bignerdranch.coyne.udacitymoviesearch.database.MovieDbSchema.MovieTable;
//
///**
// * Created by Patrick Coyne on 7/16/2017.
// */
//
//public class MovieBaseHelper extends SQLiteOpenHelper{
//    private static final int VERSION = 1;
//    private static final String DATABSE_NAME = "movieFavorites.db";
//
//    public MovieBaseHelper(Context context) {
//        super(context, DATABSE_NAME, null, VERSION);
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase sqLiteDatabase) {
//        sqLiteDatabase.execSQL("create table "+ MovieTable.NAME + "(" +
//        " _id integer primary key autoincrement, " +
//        MovieTable.Cols.ID + ", " +
//                MovieTable.Cols.TITLE + ", "+
//                MovieTable.Cols.RELEASE_DATE + ", "+
//                MovieTable.Cols.VOTE_AVERAGE + ", "+
//                MovieTable.Cols.POSTER_PATH + ", "+
//                MovieTable.Cols.OVERVIEW + ")"
//        );
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
//
//    }
//}
