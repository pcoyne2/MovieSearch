package com.bignerdranch.coyne.udacitymoviesearch;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by Patrick Coyne on 7/16/2017.
 */

public class MovieDetailFragment extends Fragment {

    private static final String ARG_MOVIE_ID = "movie_id";

    public static MovieDetailFragment newInstance(String id){
        Bundle args = new Bundle();
        args.putSerializable(ARG_MOVIE_ID, id);

        MovieDetailFragment fragment = new MovieDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
