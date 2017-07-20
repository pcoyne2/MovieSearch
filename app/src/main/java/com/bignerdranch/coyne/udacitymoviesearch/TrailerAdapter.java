package com.bignerdranch.coyne.udacitymoviesearch;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Patrick Coyne on 7/18/2017.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerHolder> {
    private List<Trailer> mTrailers;
    private Context mContext;

    public TrailerAdapter(Context context, List<Trailer> trailers) {
        mTrailers = trailers;
        mContext = context;
    }

    @Override
    public TrailerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
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

    class TrailerHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
            Toast.makeText(mContext, mTrailer.getTitle(), Toast.LENGTH_SHORT).show();
            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v="+mTrailer.getKey())));
        }
    }

}

