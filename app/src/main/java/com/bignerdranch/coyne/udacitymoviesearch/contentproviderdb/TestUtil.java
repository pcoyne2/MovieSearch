package com.bignerdranch.coyne.udacitymoviesearch.contentproviderdb;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.bignerdranch.coyne.udacitymoviesearch.contentproviderdb.MovieContract.MovieEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Patrick Coyne on 7/18/2017.
 */

public class TestUtil {
    public static void insertFakeData(SQLiteDatabase db){
        if(db == null){
            return;
        }
        //create a list of fake guests
        List<ContentValues> list = new ArrayList<ContentValues>();

        ContentValues cv = new ContentValues();
        cv.put(MovieEntry.COLUMN_TITLE, "Despicable Me 3");
        cv.put(MovieEntry.COLUMN_ID, "324852");
        cv.put(MovieEntry.COLUMN_AVG, "6.2");
        cv.put(MovieEntry.COLUMN_DATE, "2017-06-29");
        cv.put(MovieEntry.COLUMN_POSTER, "/5qcUGqWoWhEsoQwNUrtf3y3fcWn.jpg");
        cv.put(MovieEntry.COLUMN_OVERVIEW, "Gru and his wife Lucy must stop former '80s child star Balthazar Bratt from achieving world domination.");
        list.add(cv);

        cv = new ContentValues();
        cv.put(MovieEntry.COLUMN_TITLE, "Despicable Me 3");
        cv.put(MovieEntry.COLUMN_ID, "324852");
        cv.put(MovieEntry.COLUMN_AVG, "6.2");
        cv.put(MovieEntry.COLUMN_DATE, "2017-06-29");
        cv.put(MovieEntry.COLUMN_POSTER, "/5qcUGqWoWhEsoQwNUrtf3y3fcWn.jpg");
        cv.put(MovieEntry.COLUMN_OVERVIEW, "Gru and his wife Lucy must stop former '80s child star Balthazar Bratt from achieving world domination.");
        list.add(cv);

        cv = new ContentValues();
        cv.put(MovieEntry.COLUMN_TITLE, "Despicable Me 3");
        cv.put(MovieEntry.COLUMN_ID, "324852");
        cv.put(MovieEntry.COLUMN_AVG, "6.2");
        cv.put(MovieEntry.COLUMN_DATE, "2017-06-29");
        cv.put(MovieEntry.COLUMN_POSTER, "/5qcUGqWoWhEsoQwNUrtf3y3fcWn.jpg");
        cv.put(MovieEntry.COLUMN_OVERVIEW, "Gru and his wife Lucy must stop former '80s child star Balthazar Bratt from achieving world domination.");
        list.add(cv);

        cv = new ContentValues();
        cv.put(MovieEntry.COLUMN_TITLE, "Despicable Me 3");
        cv.put(MovieEntry.COLUMN_ID, "324852");
        cv.put(MovieEntry.COLUMN_AVG, "6.2");
        cv.put(MovieEntry.COLUMN_DATE, "2017-06-29");
        cv.put(MovieEntry.COLUMN_POSTER, "/5qcUGqWoWhEsoQwNUrtf3y3fcWn.jpg");
        cv.put(MovieEntry.COLUMN_OVERVIEW, "Gru and his wife Lucy must stop former '80s child star Balthazar Bratt from achieving world domination.");
        list.add(cv);

        cv = new ContentValues();
        cv.put(MovieEntry.COLUMN_TITLE, "Despicable Me 3");
        cv.put(MovieEntry.COLUMN_ID, "324852");
        cv.put(MovieEntry.COLUMN_AVG, "6.2");
        cv.put(MovieEntry.COLUMN_DATE, "2017-06-29");
        cv.put(MovieEntry.COLUMN_POSTER, "/5qcUGqWoWhEsoQwNUrtf3y3fcWn.jpg");
        cv.put(MovieEntry.COLUMN_OVERVIEW, "Gru and his wife Lucy must stop former '80s child star Balthazar Bratt from achieving world domination.");
        list.add(cv);

        //insert all guests in one transaction
        try
        {
            db.beginTransaction();
            //clear the table first
            db.delete (MovieEntry.TABLE_NAME,null,null);
            //go through the list and add one by one
            for(ContentValues c:list){
                db.insert(MovieEntry.TABLE_NAME, null, c);
            }
            db.setTransactionSuccessful();
        }
        catch (SQLException e) {
            //too bad :(
        }
        finally
        {
            db.endTransaction();
        }

    }
}

