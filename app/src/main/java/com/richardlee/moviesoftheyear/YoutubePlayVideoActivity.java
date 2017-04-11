package com.richardlee.moviesoftheyear;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.ViewGroup;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class YoutubePlayVideoActivity extends YouTubeBaseActivity
        implements YouTubePlayer.OnInitializedListener {

    private static final String TAG = "YoutubePlayVideoAct";
    static final String GOOGLE_API_KEY = "AIzaSyCq4W2wvzvj4PrtY-aOCPC9OFmd0nJNJ7A";
    public static final String YOUTUBE_VIDEO_ID = "YOUTUBE_VIDEO_ID";
    private String mVideoID;
    private YouTubePlayerView mPlayerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConstraintLayout layout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.activity_youtube, null);
        setContentView(layout);

        Intent intent = getIntent();
        mVideoID = intent.getStringExtra(YOUTUBE_VIDEO_ID);

        //Comment for tests on emulators
        mPlayerView = new YouTubePlayerView(this);

        mPlayerView.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layout.addView(mPlayerView);

        mPlayerView.initialize(GOOGLE_API_KEY, this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Log.d(TAG, "onDestroy: starts");
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        //Log.d(TAG, "onInitializationSuccess,provider: " + provider.getClass().toString());
        //Toast.makeText(this, "Initialized Youtube Player successfully", Toast.LENGTH_LONG).show();

        youTubePlayer.setPlaybackEventListener(playbackEventListener);
        youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener);

        if (!wasRestored) {
            youTubePlayer.cueVideo(mVideoID);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        final int REQUEST_CODE = 1;

        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, REQUEST_CODE).show();
        } else {
            String errorMessage = String.format("There was an error initializing the YoutubePlayer (%1$s)", youTubeInitializationResult.toString());
            //Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener() {
        @Override
        public void onPlaying() {
            //Toast.makeText(YoutubePlayVideoActivity.this, "Good, video is playing ok", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onPaused() {
            //Toast.makeText(YoutubePlayVideoActivity.this, "Video has paused", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onStopped() {
            //Toast.makeText(YoutubePlayVideoActivity.this, "Video has stopped", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onBuffering(boolean b) {

        }

        @Override
        public void onSeekTo(int i) {

        }
    };

    private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {
        @Override
        public void onLoading() {

        }

        @Override
        public void onLoaded(String s) {

        }

        @Override
        public void onAdStarted() {
            //Toast.makeText(YoutubePlayVideoActivity.this, "Click Ad now, make the video creator rich!", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onVideoStarted() {
            //Toast.makeText(YoutubePlayVideoActivity.this, "Video has started", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onVideoEnded() {
            //Toast.makeText(YoutubePlayVideoActivity.this, "Congratulations! You've completed another video.", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {

        }
    };
}
