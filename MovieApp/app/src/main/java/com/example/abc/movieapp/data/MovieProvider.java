package com.example.abc.movieapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by rashi on 11/12/16.
 */

public class MovieProvider extends ContentProvider {

    public static final String LOG_TAG = MovieProvider.class.getSimpleName();

    public static final UriMatcher mURIMatcher = buildUriMatcher();

    private MovieDBHelper mOpenHelper;

    public static final String FAVORITE = "favorite";

    static final int MOVIES = 100;
    static final int MOVIE_WITH_ID = 101;
    static final int FAVORITE_MOVIES = 103;

    static final int REVIEWS = 200;
    static final int REVIEW_WITH_ID = 201;

    static final int VIDEOS = 300;
    static final int VIDEOS_WITH_ID = 301;
    
    static final int POPULAR = 400;
    static final int POPULAR_WITH_ID = 401;
    
    static final int TOP_RATED = 500;
    static final int TOP_RATED_WITH_ID = 501;
    
    private static SQLiteQueryBuilder movieQueryBuilder;
    private static SQLiteQueryBuilder reviewsQueryBuilder;
    private static SQLiteQueryBuilder videosQueryBuilder;
    private static SQLiteQueryBuilder popularMoviesQueryBuilder;
    private static SQLiteQueryBuilder topRatedMoviesQueryBuilder;

    static{
        movieQueryBuilder = new SQLiteQueryBuilder();
        movieQueryBuilder.setTables(MovieContract.MovieEntry.TABLE_NAME);

        reviewsQueryBuilder = new SQLiteQueryBuilder();
        reviewsQueryBuilder.setTables(
                MovieContract.ReviewEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.MovieEntry.TABLE_NAME +
                        " ON " + MovieContract.ReviewEntry.TABLE_NAME +
                        "." + MovieContract.ReviewEntry.COL_MOVIE_ID +
                        " = " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry.COL_MOVIE_ID);

        videosQueryBuilder = new SQLiteQueryBuilder();
        videosQueryBuilder.setTables(
                MovieContract.VideoEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.MovieEntry.TABLE_NAME +
                        " ON " + MovieContract.VideoEntry.TABLE_NAME +
                        "." + MovieContract.VideoEntry.COL_MOVIE_ID +
                        " = " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry.COL_MOVIE_ID);

        popularMoviesQueryBuilder = new SQLiteQueryBuilder();
        popularMoviesQueryBuilder.setTables(
                MovieContract.PopularEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.MovieEntry.TABLE_NAME +
                        " ON " + MovieContract.PopularEntry.TABLE_NAME +
                        "." + MovieContract.PopularEntry.COL_MOVIE_ID +
                        " = " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry.COL_MOVIE_ID);

        topRatedMoviesQueryBuilder = new SQLiteQueryBuilder();
//        topRatedMoviesQueryBuilder.setTables(MovieContract.TopRatedEntry.);
        topRatedMoviesQueryBuilder.setTables(
                MovieContract.TopRatedEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.MovieEntry.TABLE_NAME +
                        " ON " + MovieContract.TopRatedEntry.TABLE_NAME +
                        "." + MovieContract.TopRatedEntry.COL_MOVIE_ID +
                        " = " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry.COL_MOVIE_ID);

    }

    private static final String mMovieById =
            MovieContract.MovieEntry.TABLE_NAME+
                    "." + MovieContract.MovieEntry.COL_MOVIE_ID + " = ? ";

    private static final String mReviewByMovieId =
            MovieContract.ReviewEntry.TABLE_NAME +
                    "." + MovieContract.ReviewEntry.COL_MOVIE_ID + " = ? ";

    private static final String mVideosByMovieId =
            MovieContract.VideoEntry.TABLE_NAME +
                    "." + MovieContract.VideoEntry.COL_MOVIE_ID + " = ? ";

    private static final String mPopularByMovieId =
            MovieContract.PopularEntry.TABLE_NAME +
                    "." + MovieContract.PopularEntry.COL_MOVIE_ID + " = ? ";

    private static final String mTopRatedByMovieId =
            MovieContract.TopRatedEntry.TABLE_NAME +
                    "." + MovieContract.TopRatedEntry.COL_MOVIE_ID + " = ? ";

    private static final String mMovieFavorites =
            MovieContract.MovieEntry.TABLE_NAME+
                    "."+ MovieContract.MovieEntry.COL_FAVORITE + " = ? ";


    private static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, MovieContract.PATH_MOVIE , MOVIES);
        uriMatcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", MOVIE_WITH_ID);
        uriMatcher.addURI(authority, MovieContract.PATH_MOVIE + "/" + FAVORITE , FAVORITE_MOVIES);

        uriMatcher.addURI(authority, MovieContract.PATH_REVIEW , REVIEWS);
        uriMatcher.addURI(authority, MovieContract.PATH_REVIEW + "/#" , REVIEW_WITH_ID);

        uriMatcher.addURI(authority, MovieContract.PATH_VIDEO , VIDEOS);
        uriMatcher.addURI(authority, MovieContract.PATH_VIDEO + "/#" , VIDEOS_WITH_ID);

        uriMatcher.addURI(authority, MovieContract.PATH_POPULAR , POPULAR);
        uriMatcher.addURI(authority, MovieContract.PATH_POPULAR + "/#" , POPULAR_WITH_ID);

        uriMatcher.addURI(authority, MovieContract.PATH_TOP_RATED , TOP_RATED);
        uriMatcher.addURI(authority, MovieContract.PATH_TOP_RATED + "/#" , TOP_RATED_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        final int match=mURIMatcher.match(uri);
        Log.d(LOG_TAG,"query: uri : "+uri+", match: "+match);
        switch (match){
            case MOVIES:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case MOVIE_WITH_ID:{
                retCursor = getMovieFromId(uri, projection, sortOrder);
                break;
            }
            case FAVORITE_MOVIES:{
                retCursor = getFavoriteMovies(uri, projection, sortOrder);
                break;
            }
            case REVIEWS:{
                retCursor = reviewsQueryBuilder.query(
                        mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case REVIEW_WITH_ID:{
                retCursor = getReviewWithId(uri, projection, sortOrder);
                break;
            }
            case VIDEOS:{
                retCursor = videosQueryBuilder.query(
                        mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case VIDEOS_WITH_ID:{
                retCursor = getVideosWithId(uri, projection, sortOrder);
                break;
            }
            case POPULAR:{
                retCursor = popularMoviesQueryBuilder.query(
                        mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                Log.d(LOG_TAG, "in query case popular " + projection[1] + " " + retCursor.getCount());
                break;
            }
            case POPULAR_WITH_ID:{
                retCursor = getPopularWithId(uri, projection, sortOrder);
                Log.d(LOG_TAG, "in query case popular with id " + projection.length);
                break;
            }
            case TOP_RATED:{
                Log.d(LOG_TAG, "in top rated case ");

                retCursor = topRatedMoviesQueryBuilder.query(
                        mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                Log.d(LOG_TAG, "in query case top rate " + projection[1] + " " + retCursor.getCount());
                break;
            }
            case TOP_RATED_WITH_ID:{
                retCursor = getTopRatedWithId(uri, projection, sortOrder);
                Log.d(LOG_TAG, "in query case top rated with id " + projection.length);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }



    private Cursor getMovieFromId(Uri uri, String[] projection, String sortOrder) {
        long movie_id = MovieContract.MovieEntry.getMovieIdFromUri(uri);

        return movieQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                mMovieById,
                new String[]{Long.toString(movie_id)},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getFavoriteMovies(Uri uri, String[] projection, String sortOrder) {
        return movieQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                mMovieFavorites,
                new String[]{Long.toString(1)},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getReviewWithId(Uri uri, String[] projection, String sortOrder) {
        long movie_id = MovieContract.ReviewEntry.getMovieIdFromUri(uri);

        return reviewsQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                mReviewByMovieId,
                new String[]{Long.toString(movie_id)},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getVideosWithId(Uri uri, String[] projection, String sortOrder) {
        long movie_id = MovieContract.VideoEntry.getMovieIdFromUri(uri);

        return videosQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                mVideosByMovieId,
                new String[]{Long.toString(movie_id)},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getPopularWithId(Uri uri, String[] projection, String sortOrder) {
        long movie_id = MovieContract.PopularEntry.getMovieIdFromUri(uri);

        return popularMoviesQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                mPopularByMovieId,
                new String[]{Long.toString(movie_id)},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getTopRatedWithId(Uri uri, String[] projection, String sortOrder) {
        long movie_id = MovieContract.TopRatedEntry.getMovieIdFromUri(uri);

        return topRatedMoviesQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                mTopRatedByMovieId,
                new String[]{Long.toString(movie_id)},
                null,
                null,
                sortOrder
        );
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        int match = mURIMatcher.match(uri);

        switch (match){
            case MOVIES:
                Log.d(LOG_TAG, "Movies as getType");
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_WITH_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case FAVORITE_MOVIES:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case REVIEWS:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            case REVIEW_WITH_ID:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            case VIDEOS:
                return MovieContract.VideoEntry.CONTENT_TYPE;
            case VIDEOS_WITH_ID:
                return MovieContract.VideoEntry.CONTENT_TYPE;
            case POPULAR:
                Log.d(LOG_TAG, "Popular as getType");
                return MovieContract.PopularEntry.CONTENT_TYPE;
            case POPULAR_WITH_ID:
                return MovieContract.PopularEntry.CONTENT_ITEM_TYPE;
            case TOP_RATED:
                return MovieContract.TopRatedEntry.CONTENT_TYPE;
            case TOP_RATED_WITH_ID:
                return MovieContract.TopRatedEntry.CONTENT_ITEM_TYPE;
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = mURIMatcher.match(uri);
        Uri returnUri;

        switch (match){
            case MOVIES: {
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, contentValues);
                if (_id > 0)
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case POPULAR:{
                long _id = db.insert(MovieContract.PopularEntry.TABLE_NAME, null, contentValues);
                if ( _id > 0 )
                    returnUri = MovieContract.PopularEntry.buildPopularUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case TOP_RATED:{
                long _id = db.insert(MovieContract.TopRatedEntry.TABLE_NAME, null, contentValues);
                if ( _id > 0 )
                    returnUri = MovieContract.TopRatedEntry.buildTopRatedUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case FAVORITE_MOVIES:{
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, contentValues);
                if ( _id > 0 )
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case MOVIE_WITH_ID: {
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, contentValues);
                if ( _id > 0 )
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case REVIEWS:{
                long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, contentValues);
                if ( _id > 0 )
                    returnUri = MovieContract.ReviewEntry.buildReviewUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case VIDEOS: {
                long _id = db.insert(MovieContract.VideoEntry.TABLE_NAME, null, contentValues);
                if ( _id > 0 )
                    returnUri = MovieContract.VideoEntry.buildVideoUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = mURIMatcher.match(uri);
        int rowsDeleted;

        if ( null == selection ) selection = "1";
        switch (match){
            case POPULAR:{
                rowsDeleted = db.delete(MovieContract.PopularEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case TOP_RATED:{
                rowsDeleted = db.delete(MovieContract.TopRatedEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case MOVIE_WITH_ID: {
                rowsDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case REVIEWS:{
                rowsDeleted = db.delete(MovieContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case VIDEOS: {
                rowsDeleted = db.delete(MovieContract.VideoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = mURIMatcher.match(uri);
        int rowsUpdated;

        switch (match){
            case POPULAR:{
                Log.d("favo", "update popular movie log 2");
                rowsUpdated = db.update(MovieContract.PopularEntry.TABLE_NAME, contentValues, selection,
                        selectionArgs);
                break;
            }
            case TOP_RATED:{
                Log.d("favo", "update top rated movie log 2");
                rowsUpdated = db.update(MovieContract.TopRatedEntry.TABLE_NAME, contentValues, selection,
                        selectionArgs);
                break;
            }
            case FAVORITE_MOVIES:{
                Log.d("favo", "update fav movie log 2");
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, contentValues, selection,
                        selectionArgs);
                break;
            }
            case MOVIE_WITH_ID: {
                Log.d("favo", "update movie log 2");
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, contentValues, selection,
                        selectionArgs);
                break;
            }
            case REVIEWS:{
                Log.d("favo", "update review log 2");
                rowsUpdated = db.update(MovieContract.ReviewEntry.TABLE_NAME, contentValues, selection,
                        selectionArgs);
                break;
            }
            case VIDEOS: {
                Log.d("favo", "update video log 2");
                rowsUpdated = db.update(MovieContract.VideoEntry.TABLE_NAME, contentValues, selection,
                        selectionArgs);
                break;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = mURIMatcher.match(uri);
        Log.d(LOG_TAG,"bulk insert: uri: "+uri+" code:"+match);
        switch (match) {
            case MOVIES:
                Log.d(LOG_TAG,"bulk insert:Movies");
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        Log.d(LOG_TAG,"bulk insert:Movies _id "+_id);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                    Log.d(LOG_TAG, " rows inserted " + returnCount + values[0].getAsString(MovieContract.MovieEntry.COL_TITLE));
                } finally {

                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case POPULAR:
                Log.d(LOG_TAG,"bulk insert:Popular");
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.PopularEntry.TABLE_NAME, null, value);
                        Log.d(LOG_TAG,"bulk insert:Popular _id "+_id);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case TOP_RATED:
                Log.d(LOG_TAG,"bulk insert:Top rated");
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.TopRatedEntry.TABLE_NAME, null, value);
                        Log.d(LOG_TAG,"bulk inser:Top rated _id "+_id);

                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case REVIEWS:
                Log.d(LOG_TAG,"bulk insert:Reviews");
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, value);
                        Log.d(LOG_TAG,"bulk inser:Reviews _id "+_id);

                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case VIDEOS:
                Log.d(LOG_TAG,"bulk insert:Videos");
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.VideoEntry.TABLE_NAME, null, value);
                        Log.d(LOG_TAG,"bulk inser:Videos values keyset: "+value.keySet());
                        Log.d(LOG_TAG,"bulk inser:Videos values ,name: "+value.get("name")+" , movie_id: "+value.get("movie_id")+", key: "+value.get("key"));
                        Log.d(LOG_TAG,"bulk inser:Videos _id "+_id);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
