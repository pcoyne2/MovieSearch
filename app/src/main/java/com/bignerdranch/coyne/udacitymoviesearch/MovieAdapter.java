package com.bignerdranch.coyne.udacitymoviesearch;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Patrick Coyne on 7/18/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieHolder> {
    private List<Movie> mMovies;
    private Context mContext;

    public MovieAdapter(Context context, List<Movie> movies) {
        mMovies = movies;
        mContext = context;
    }

    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
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

    class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
            Picasso.with(mContext).load("https://image.tmdb.org/t/p/original" + movie.getPosterPath())
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
            Intent intent = new Intent(mContext, DetailActivity.class);
            //Used for testing should pass information more efficiently
            intent.putExtra(mContext.getString(R.string.title), mMovie.getTitle());
            intent.putExtra(mContext.getString(R.string.id), mMovie.getId());
            intent.putExtra(mContext.getString(R.string.overview), mMovie.getOverview());
            intent.putExtra(mContext.getString(R.string.poster_path), mMovie.getPosterPath());
            intent.putExtra(mContext.getString(R.string.release_date), mMovie.getReleaseDate());
            intent.putExtra(mContext.getString(R.string.vote_average), mMovie.getVoteAverage());
            mContext.startActivity(intent);
        }
    }

}
