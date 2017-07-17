package com.bignerdranch.coyne.udacitymoviesearch;

/**
 * Created by Patrick Coyne on 7/16/2017.
 */

public class Movie {

    private String id;
    private String mTitle;
    private String mReleaseDate;
    private String mVoteAverage;
    private String mPosterPath;
    private String mOverview;


    public Movie(String id, String title, String releaseDate, String voteAverage,
                 String posterPath, String overview) {
        this.id = id;
        mTitle = title;
        mReleaseDate = releaseDate;
        mVoteAverage = voteAverage;
        mPosterPath = posterPath;
        mOverview = overview;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        mReleaseDate = releaseDate;
    }

    public String getVoteAverage() {
        return mVoteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        mVoteAverage = voteAverage;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public void setPosterPath(String posterPath) {
        mPosterPath = posterPath;
    }

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(String overview) {
        mOverview = overview;
    }
}
