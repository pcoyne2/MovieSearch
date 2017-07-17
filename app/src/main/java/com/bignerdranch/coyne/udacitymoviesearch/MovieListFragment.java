package com.bignerdranch.coyne.udacitymoviesearch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private Callbacks mCallbacks;
    private String sortBy;
    private int pageNumber=1;

    List<Movie> movies = new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        sortBy = sharedPref.getString(getString(R.string.pref_sort_key), getString(R.string.popular));
        pageNumber = 1;
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.movie_recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

//        for(int i=0; i<5; i++){
//            Movie movie = new Movie("324852", "Despicable Me 3",
//                    "2017-06-29", "6.2", //"/6aUWe0GSl69wMTSWWexsorMIvwU.jpg",
//                    "/5qcUGqWoWhEsoQwNUrtf3y3fcWn.jpg",
//                    getString(R.string.sample_overview));
//            MovieLab.get(getActivity()).addMovie(movie);
//        }

        updateList();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String newSort = preferences.getString(getString(R.string.pref_sort_key), getString(R.string.pref_most_popular));
        if(!sortBy.equalsIgnoreCase(newSort)){
            updateList();
        }
        Log.d("TAG", "OnResume");
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
        sortBy = sharedPref.getString(getString(R.string.pref_sort_key), getString(R.string.popular));
        if(sortBy.equals(getString(R.string.favorites))){
            MovieLab movieLab = MovieLab.get(getActivity());
            movies = movieLab.getMovies();
        }else{
            FetchMovieTask fetchMovieTask = new FetchMovieTask();
            String[] params = new String[]{sortBy, Integer.toString(pageNumber)};
            fetchMovieTask.execute(params);
        }
//
        if(mMovieAdapter == null){
            mMovieAdapter = new MovieAdapter(movies);
            mRecyclerView.setAdapter(mMovieAdapter);
        }else{
            mMovieAdapter.setMovies(movies);
            mMovieAdapter.notifyDataSetChanged();
        }
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
            mMovieAdapter.notifyDataSetChanged();
            super.onPostExecute(strings);
        }
    }

    private class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mPosterImage;
        private Movie mMovie;

        public MovieHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_movie_item, parent, false));
            itemView.setOnClickListener(this);

            mPosterImage = (ImageView)itemView.findViewById(R.id.grid_movie_poster);
        }

        public void bind(Movie movie) {
            mMovie = movie;
//            mTitleTextView.setText(mMovie.getTitle());
//            SimpleDateFormat dateFormat = new SimpleDateFormat(MovieFragment.DATE_FORMAT);
//            SimpleDateFormat timeFormat = new SimpleDateFormat(MovieFragment.TIME_FORMAT);
//            mDateTextView.setText(dateFormat.format(mMovie.getDate()) + " " + timeFormat.format(mMovie.getTime()));
//            mSolvedImageView.setVisibility(movie.isSolved() ? View.VISIBLE : View.GONE);
            Picasso.with(getActivity()).load("https://image.tmdb.org/t/p/original" + movie.getPosterPath())
                    .fit()
                    .centerCrop()
                    .error(R.drawable.despicable_me)
                    .into(mPosterImage);
        }

        public Movie getMovie() {
            return mMovie;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            //Used for testing should pass information more efficiently
            intent.putExtra(getString(R.string.title), mMovie.getTitle());
            intent.putExtra(getString(R.string.id), mMovie.getId());
            intent.putExtra(getString(R.string.overview), mMovie.getOverview());
            intent.putExtra(getString(R.string.poster_path), mMovie.getPosterPath());
            intent.putExtra(getString(R.string.release_date), mMovie.getReleaseDate());
            intent.putExtra(getString(R.string.vote_average), mMovie.getVoteAverage());
            startActivity(intent);
        }
    }

    private class MovieAdapter extends RecyclerView.Adapter<MovieHolder> {
        private List<Movie> mMovies;

        public MovieAdapter(List<Movie> movies) {
            mMovies = movies;
        }

        @Override
        public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                return new MovieHolder(layoutInflater, parent);

        }

        @Override
        public void onBindViewHolder(MovieHolder holder, int position) {
            Movie movie = mMovies.get(position);
            holder.bind(movie);


        }

        @Override
        public int getItemCount() {
            return mMovies.size();
        }

        public void setMovies(List<Movie> movies) {
            mMovies = movies;
        }
    }
}
