package com.richardlee.moviesoftheyear;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.StreamBitmapDecoder;
import com.squareup.picasso.Picasso;

class MoviesRecyclerViewCursorAdapter extends RecyclerView.Adapter<MoviesRecyclerViewCursorAdapter.MovieViewHolder> {
    private static final String TAG = "MoviesRVCursorAdapter";

    private Context mContext;
    private Cursor mMovieCursor;


    public MoviesRecyclerViewCursorAdapter(Context context, @NonNull Cursor movieCursor) {
        mContext = context;
        mMovieCursor = movieCursor;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //Log.d(TAG, "onCreateViewHolder: ");

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);

        return new MovieViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mMovieCursor != null && mMovieCursor.getCount() > 0 ? mMovieCursor.getCount() : 1;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {

        if (mMovieCursor != null && mMovieCursor.getCount() > 0) {

            if (!mMovieCursor.moveToPosition(position)) {
                throw new IllegalStateException("Couldn´t move the cursor for the position " + position);
            }

            int movieTmdbId = mMovieCursor.getInt(mMovieCursor.getColumnIndex(FavoritesMoviesContract.Columns.TMDB_ID));
            String movieTitle = mMovieCursor.getString(mMovieCursor.getColumnIndex(FavoritesMoviesContract.Columns.TITLE));
            String movieReleaseDate = mMovieCursor.getString(mMovieCursor.getColumnIndex(FavoritesMoviesContract.Columns.RELEASE_DATE));
            String movieVoteAverage = mMovieCursor.getString(mMovieCursor.getColumnIndex(FavoritesMoviesContract.Columns.AVERAGE));
            String moviePosterUri = mMovieCursor.getString(mMovieCursor.getColumnIndex(FavoritesMoviesContract.Columns.POSTER_URI));

            String imageFullPath = TmdbApiFactory.getImageRootPath(IMAGE_SIZE.W500) + moviePosterUri;

            //Log.d(TAG, "onBindViewHolder: ID: " + movieTmdbId + " title: " + movieTitle + " image path: " + imageFullPath);

            holder.title.setText(movieTitle);
            holder.releaseDate.setText(movieReleaseDate);
            holder.average.setText(String.valueOf(movieVoteAverage));

            Picasso.with(mContext)
                    .load(imageFullPath)
                    .fit()
                    .centerCrop()
                    .noFade()
                    .error(R.drawable.play)
                    .placeholder(R.drawable.play)
                    .into(holder.moviePhoto);

            /*Glide.with(mContext)
                    .load(imageFullPath)
                    .asBitmap()
                    .imageDecoder(new StreamBitmapDecoder(mContext, DecodeFormat.PREFER_ARGB_8888))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .error(R.drawable.play)
                    .placeholder(R.drawable.play)
                    .into(holder.moviePhoto);*/

        } else {
            holder.moviePhoto.setImageResource(R.drawable.play);
            holder.title.setText(R.string.no_movies_found);
        }
    }

    @Override
    public long getItemId(int position) {

        long id = -1;
        if (mMovieCursor != null && mMovieCursor.getCount() > 0) {
            mMovieCursor.moveToPosition(position);

            id = mMovieCursor.getInt(mMovieCursor.getColumnIndex(FavoritesMoviesContract.Columns.TMDB_ID));
        }
        return id;
    }

    public Movie getMovie(int position) throws NullPointerException {

        Movie movie = null;

        if (mMovieCursor != null && mMovieCursor.getCount() > 0) {
            mMovieCursor.moveToPosition(position);

            movie = new Movie();

            movie.setTmdbID(mMovieCursor.getInt(mMovieCursor.getColumnIndex(FavoritesMoviesContract.Columns.TMDB_ID)));

            movie.setTitle(mMovieCursor.getString(mMovieCursor.getColumnIndex(FavoritesMoviesContract.Columns.TITLE)));
            movie.setReleaseDate(mMovieCursor.getString(mMovieCursor.getColumnIndex(FavoritesMoviesContract.Columns.RELEASE_DATE)));
            movie.setVoteAverage(mMovieCursor.getFloat(mMovieCursor.getColumnIndex(FavoritesMoviesContract.Columns.AVERAGE)));
            movie.setPosterUri(mMovieCursor.getString(mMovieCursor.getColumnIndex(FavoritesMoviesContract.Columns.POSTER_URI)));
            movie.setOverview(mMovieCursor.getString(mMovieCursor.getColumnIndex(FavoritesMoviesContract.Columns.OVERVIEW)));

            movie.setStatus(mMovieCursor.getString(mMovieCursor.getColumnIndex(FavoritesMoviesContract.Columns.STATUS)));
            movie.setGenres(mMovieCursor.getString(mMovieCursor.getColumnIndex(FavoritesMoviesContract.Columns.GENRES)));
            movie.setMainTrailer(mMovieCursor.getString(mMovieCursor.getColumnIndex(FavoritesMoviesContract.Columns.MAIN_TRAILER)));
        }

        return movie;
    }

    Cursor swapCursosr(Cursor newCursor) {

        // If was the same cursor, swap operation does not will be done.
        if (mMovieCursor == newCursor) {
            return null;
        }

        final Cursor oldCursor = mMovieCursor;
        mMovieCursor = newCursor;

        if (newCursor != null) {
            notifyDataSetChanged();
        } else {
            //Notifying observer about remove operation of all items
            notifyItemRangeRemoved(0, getItemCount());
        }

        return oldCursor;
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {

        TextView title = null;
        ImageView moviePhoto = null;
        TextView releaseDate = null;
        TextView average = null;

        public MovieViewHolder(View itemView) {
            super(itemView);

            this.title = (TextView) itemView.findViewById(R.id.txtv_movie_title);
            this.moviePhoto = (ImageView) itemView.findViewById(R.id.imgv_movie_photo);
            this.releaseDate = (TextView) itemView.findViewById(R.id.txv_release_date);
            this.average = (TextView) itemView.findViewById(R.id.txtv_movie_average);
        }
    }
}
