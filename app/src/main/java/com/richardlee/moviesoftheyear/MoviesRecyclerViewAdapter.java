package com.richardlee.moviesoftheyear;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.List;

class MoviesRecyclerViewAdapter extends RecyclerView.Adapter<MoviesRecyclerViewAdapter.MovieViewHolder> {
    private static final String TAG = "MoviesRecyclerViewAdapt";
    private WeakReference<Context> mContext;
    private List<Movie> mMovies;

    public MoviesRecyclerViewAdapter(Context context, @NonNull List<Movie> movies) {
        mContext = new WeakReference<Context>(context);
        mMovies = movies;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

//        //Log.d(TAG, "onCreateViewHolder: starts");

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);

        return new MovieViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mMovies != null && mMovies.size() > 0 ? mMovies.size() : 1;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {

        if (mMovies != null && mMovies.size() > 0) {
            Movie movie = mMovies.get(position);
            String imageFullPath = TmdbApiFactory.getImageRootPath(IMAGE_SIZE.W500) + movie.getPosterUri();

            holder.title.setText(movie.getTitle());
            holder.releaseDate.setText(movie.getReleaseDate());
            holder.average.setText(String.valueOf(movie.getVoteAverage()));

            //Log.d(TAG, "setMovieViews: image uri: " + imageFullPath + " for movie tmdbid: " + movie.getTmdbID());


            Picasso.with(mContext.get())
                    .load(imageFullPath)
                    .fit()
                    .centerCrop()
                    .noFade()
                    .error(R.drawable.play)
                    .placeholder(R.drawable.play)
                    .into(holder.moviePhoto);

            /*Glide.with(mContext.get())
                    .load(imageFullPath)
                    .asBitmap()
                    .imageDecoder(new StreamBitmapDecoder(mContext.get(), DecodeFormat.PREFER_ARGB_8888))
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

    Movie getMovie(int position) throws NullPointerException {
        return (mMovies != null && mMovies.size() > 0) ? mMovies.get(position) : null;
    }

    void loadNewData(List<Movie> movies) {
        mMovies = movies;
        notifyDataSetChanged();
    }

    //TODO: Load new Data dynamically

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
