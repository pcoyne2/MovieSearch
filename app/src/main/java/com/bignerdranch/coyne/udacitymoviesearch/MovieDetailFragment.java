package com.bignerdranch.coyne.udacitymoviesearch;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class MovieDetailFragment extends Fragment {

    private static final String ARG_MOVIE_ID = "movie_id";

    List<Trailer> trailers = new ArrayList<>();

    private boolean isFavorite;


    private TextView header, releaseDate, voteAvg, overview;
    private ImageView poster;
    private ImageButton favorite;
    private RecyclerView trailerRecyclerView;

    private TrailerAdapter mTrailerAdapter;

    Movie mMovie;

    public static MovieDetailFragment newInstance(String id){
        Bundle args = new Bundle();
        args.putSerializable(ARG_MOVIE_ID, id);

        MovieDetailFragment fragment = new MovieDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public MovieDetailFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        header = (TextView)view.findViewById(R.id.title);
        releaseDate = (TextView)view.findViewById(R.id.release_date);
        voteAvg = (TextView)view.findViewById(R.id.vote_average);
        overview = (TextView)view.findViewById(R.id.overview);
        favorite = (ImageButton)view.findViewById(R.id.favorite);
        poster = (ImageView)view.findViewById(R.id.poster);
        trailerRecyclerView = (RecyclerView)view.findViewById(R.id.trailer_recycler_view);
        trailerRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFavorite){
                    //Delete Movie
                    MovieLab.get(getActivity()).deleteMovie(mMovie);
                    favorite.setImageResource(R.drawable.ic_star);
                }else {
                    MovieLab.get(getActivity()).addMovie(mMovie);
                    favorite.setImageResource(R.drawable.ic_star_favorite);
                }
                isFavorite = !isFavorite;
            }
        });

        Bundle bundle = getActivity().getIntent().getExtras();
        mMovie = new Movie(bundle.getString(getString(R.string.id)),
                bundle.getString(getString(R.string.title)),
                bundle.getString(getString(R.string.release_date)),
                bundle.getString(getString(R.string.vote_average)),
                bundle.getString(getString(R.string.poster_path)),
                bundle.getString(getString(R.string.overview)));
        header.setText(mMovie.getTitle());
        releaseDate.setText(mMovie.getReleaseDate());
        voteAvg.setText(mMovie.getVoteAverage()+"/10");
        overview.setText(mMovie.getOverview());
        Picasso.with(getActivity()).load("https://image.tmdb.org/t/p/original" + mMovie.getPosterPath())
                .fit()
                .centerCrop()
                .error(R.drawable.despicable_me)
                .into(poster);
        FetchTrailerTask weatherTask = new FetchTrailerTask();
        weatherTask.execute(mMovie.getId());

        isMovieFavorite();

        return view;
    }

    public void isMovieFavorite(){
        Movie movie = MovieLab.get(getActivity()).getMovie(mMovie.getId());
        if(movie == null){
            isFavorite = false;
            favorite.setImageResource(R.drawable.ic_star);
        }else{
            isFavorite = true;
            favorite.setImageResource(R.drawable.ic_star_favorite);
        }
    }

    public class FetchTrailerTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = MovieDetailFragment.FetchTrailerTask.class.getSimpleName();
        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
                throws JSONException {

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray results = forecastJson.getJSONArray("results");

            for(int i = 0; i < results.length(); i++) {
                // Get the JSON object representing the details
                JSONObject dayForecast = results.getJSONObject(i);
                String name = dayForecast.getString(getString(R.string.name));
                String key = dayForecast.getString(getString(R.string.key));
                String site = dayForecast.getString(getString(R.string.site));
                trailers.add(new Trailer(name, key, site));
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

            int numDays = 7;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String APPID_PARAM = "api_key";

                Uri builtUri = Uri.parse(getString(R.string.base_url)).buildUpon()
                        .appendPath(params[0]) //movie_id
                        .appendPath("videos")
                        .appendQueryParameter(APPID_PARAM, getString(R.string.api_key))
                        .appendQueryParameter("language", "en-US")
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
                return getWeatherDataFromJson(forecastJsonStr, numDays);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(String[] strings) {
//            adapter.clear();
//            for(int i=0; i<trailers.size();i++){
//                adapter.add(trailers.get(i));
//            }
//            list.setAdapter(adapter);
            mTrailerAdapter = new TrailerAdapter(trailers);
            trailerRecyclerView.setAdapter(mTrailerAdapter);

            super.onPostExecute(strings);
        }
    }

    private class TrailerHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Trailer mTrailer;
        TextView trailerName;

        public TrailerHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.trailer_list_item, parent, false));
            itemView.setOnClickListener(this);

            trailerName= (TextView) itemView.findViewById(R.id.trailer_name);
        }

        public void bind(Trailer trailer) {
            mTrailer = trailer;
//            mTitleTextView.setText(mMovie.getTitle());
//            SimpleDateFormat dateFormat = new SimpleDateFormat(MovieFragment.DATE_FORMAT);
//            SimpleDateFormat timeFormat = new SimpleDateFormat(MovieFragment.TIME_FORMAT);
//            mDateTextView.setText(dateFormat.format(mMovie.getDate()) + " " + timeFormat.format(mMovie.getTime()));
//            mSolvedImageView.setVisibility(movie.isSolved() ? View.VISIBLE : View.GONE);
            trailerName.setText(trailer.getTitle());
        }

        public Trailer getMovie() {
            return mTrailer;
        }

        @Override
        public void onClick(View v) {
            //Send intent for youtube
            Toast.makeText(getActivity(), mTrailer.getTitle(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v="+mTrailer.getKey())));
        }
    }

    private class TrailerAdapter extends RecyclerView.Adapter<TrailerHolder> {
        private List<Trailer> mTrailers;

        public TrailerAdapter(List<Trailer> trailers) {
            mTrailers = trailers;
        }

        @Override
        public TrailerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new TrailerHolder(layoutInflater, parent);

        }

        @Override
        public void onBindViewHolder(TrailerHolder holder, int position) {
            Trailer trailer = mTrailers.get(position);
            holder.bind(trailer);


        }

        @Override
        public int getItemCount() {
            return mTrailers.size();
        }

        public void setMovies(List<Trailer> trailers) {
            mTrailers = trailers;
        }
    }
}
