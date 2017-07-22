package com.bignerdranch.coyne.udacitymoviesearch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.coyne.udacitymoviesearch.contentproviderdb.MovieContract;
import com.bignerdranch.coyne.udacitymoviesearch.contentproviderdb.MovieDbHelper;
import com.bignerdranch.coyne.udacitymoviesearch.contentproviderdb.TestUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Patrick Coyne on 7/16/2017.
 */

public class MovieListFragment extends Fragment {

    private final String KEY_RECYCLER_STATE = "recycler_state";

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private Callbacks mCallbacks;
    private String sortBy;
    private int pageNumber=1;
    private static Bundle rvStateBundle;

//    private SQLiteDatabase mDb;

    List<Movie> movies = new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        sortBy = sharedPref.getString(getString(R.string.pref_sort_key), getString(R.string.favorites));
        pageNumber = 1;
        setHasOptionsMenu(true);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.movie_recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

//        MovieDbHelper dbHelper = new MovieDbHelper(getActivity());
//
//        mDb = dbHelper.getWritableDatabase();

//        TestUtil.insertFakeData(mDb);

//        Cursor cursor = getAllMovies();

//        updateList();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

//        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
//        String newSort = sharedPref.getString(getString(R.string.pref_sort_key), getString(R.string.favorites));
//        if(!sortBy.equalsIgnoreCase(newSort)){
            updateList();
//        }
        Log.d("TAG", "OnResume");
        if(rvStateBundle != null){
            Parcelable listState = rvStateBundle.getParcelable(KEY_RECYCLER_STATE);
            mRecyclerView.getLayoutManager().onRestoreInstanceState(listState);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        rvStateBundle = new Bundle();
        Parcelable listState = mRecyclerView.getLayoutManager().onSaveInstanceState();
        rvStateBundle.putParcelable(KEY_RECYCLER_STATE, listState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.settings_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        switch (item.getItemId()){
            case R.id.top_rated:
//                sortBy = getString(R.string.top_rated);
                pageNumber = 1;
                editor.putString(getString(R.string.pref_sort_key), getString(R.string.top_rated));
                editor.commit();
                updateList();
                return true;
            case R.id.favorites:
                editor.putString(getString(R.string.pref_sort_key), getString(R.string.favorites));
                editor.commit();
                pageNumber = 1;
                updateList();
                return true;
            case R.id.popular:
                editor.putString(getString(R.string.pref_sort_key), getString(R.string.popular));
                editor.commit();
                pageNumber = 1;
                updateList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public interface Callbacks{
        void onMovieSelected(Movie movie);
    }

    private void updateList(){
        //Check if loading favorites
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        sortBy = sharedPref.getString(getString(R.string.pref_sort_key), getString(R.string.favorites));
        if(sortBy.equals(getString(R.string.favorites))){
//            MovieLab movieLab = MovieLab.get(getActivity());
            movies.clear();
            Cursor cursor = getAllMovies();
//                    getActivity().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
//                    null,
//                    null,
//                    null,
//                    null);
            cursor.moveToFirst();
            if(cursor.getCount()!= 0){
                movies.add(getMovieFromCursor(cursor));
            }
            while(cursor.moveToNext()){
                movies.add(getMovieFromCursor(cursor));
            }
        }else{
            FetchMovieTask fetchMovieTask = new FetchMovieTask();
            String[] params = new String[]{sortBy, Integer.toString(pageNumber)};
            fetchMovieTask.execute(params);
        }
//
        if(mMovieAdapter == null){
            mMovieAdapter = new MovieAdapter(getActivity(), movies);
            mRecyclerView.setAdapter(mMovieAdapter);
        }else{
            mRecyclerView.getRecycledViewPool().clear();
            mMovieAdapter.setMovies(movies);
            mMovieAdapter.notifyDataSetChanged();
        }
    }

    private Cursor getAllMovies(){
        return getActivity().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    private Movie getMovieFromCursor(Cursor c){
        int idIndex = c.getColumnIndex(MovieContract.MovieEntry.COLUMN_ID);
        int titleIndex = c.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE);
        int dateIndex = c.getColumnIndex(MovieContract.MovieEntry.COLUMN_DATE);
        int posterIndex = c.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER);
        int overviewIndex = c.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW);
        int avgIndex = c.getColumnIndex(MovieContract.MovieEntry.COLUMN_AVG);

        String id = c.getString(idIndex);
        String title = c.getString(titleIndex);
        String date = c.getString(dateIndex);
        String poster = c.getString(posterIndex);
        String overview = c.getString(overviewIndex);
        String avg = c.getString(avgIndex);

        Movie movie = new Movie(id, title, date, avg, poster, overview);
        return movie;
    }

    public class FetchMovieTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        private String[] getMovieDataFromJson(String forecastJsonStr, String page)
                throws JSONException {

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray results = forecastJson.getJSONArray("results");

            if(Integer.valueOf(page).equals(1)){
                movies.clear();
            }

            for(int i = 0; i < results.length(); i++) {

                // Get the JSON object representing the details
                JSONObject dayForecast = results.getJSONObject(i);
                String title = dayForecast.getString(getString(R.string.title));
                String release = dayForecast.getString(getString(R.string.release_date));
                String voteAverage = dayForecast.getString(getString(R.string.vote_average));
                String posterPath = dayForecast.getString(getString(R.string.poster_path));
                String id = dayForecast.getString(getString(R.string.id));
                String plot = dayForecast.getString(getString(R.string.overview));
                Movie movie = new Movie(id, title, release, voteAverage, posterPath, plot);

                movies.add(movie);

            }

            return new String[0];

        }
        @Override
        protected String[] doInBackground(String... params) {

            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                final String APPID_PARAM = "api_key";

                Uri builtUri = Uri.parse(getString(R.string.base_url)).buildUpon()
                        .appendPath(params[0])
                        .appendQueryParameter(APPID_PARAM, getString(R.string.api_key))
                        .appendQueryParameter("page", params[1])
                        .build();

                URL url = new URL(builtUri.toString());

//                Log.v(LOG_TAG, "Built URI " + builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                    Log.d("LINE", line);
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();

//                Log.v(LOG_TAG, "Forecast string: " + forecastJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(forecastJsonStr, params[1]);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(String[] strings) {
//            updateList();
            mRecyclerView.getRecycledViewPool().clear();
            mMovieAdapter.notifyDataSetChanged();
            super.onPostExecute(strings);
        }
    }

}
