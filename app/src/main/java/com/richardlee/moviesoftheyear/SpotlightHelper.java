package com.richardlee.moviesoftheyear;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;

import com.wooplr.spotlight.SpotlightConfig;
import com.wooplr.spotlight.SpotlightView;

import java.lang.ref.WeakReference;
import java.util.UUID;

public class SpotlightHelper {

    public static String HELP_ITEM_MOVIES_LIST_FIRST_CONTACT_ID = "a";
    public static String HELP_ITEM_FAVORITE_MOVIES_ID = "b";
    public static String HELP_ITEM_MOVIE_DETAILS_ADD_TO_FAV_ID = "v";

    private final SpotlightConfig spotlightConfig;
    private final WeakReference<Activity> activityWeakReference;
    private final WeakReference<View> viewWeakReference;
    private final String mTitle;
    private final String mSubtitle;

    public SpotlightHelper(Activity activity, Context context, View targetView, @Nullable String title, String subtitle) {

        activityWeakReference = new WeakReference<>(activity);
        viewWeakReference = new WeakReference<>(targetView);

        mTitle = title;
        mSubtitle = subtitle;

        spotlightConfig = getSpotlightConfig(context);
    }

    public void show(final String usageId, int delayInMiliseconds) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new SpotlightView.Builder(activityWeakReference.get())
                        .setConfiguration(spotlightConfig)
                        .headingTvText(mTitle)
                        .subHeadingTvText(mSubtitle)
                        .target(viewWeakReference.get())
                        .enableDismissAfterShown(true)
                        .usageId(usageId)
                        .show();
            }
        }, delayInMiliseconds);
    }

    public void show(final String usageId) {

        show(usageId, 1300);
    }

    public static void resetUsageIds(){
        HELP_ITEM_FAVORITE_MOVIES_ID = UUID.randomUUID().toString();
        HELP_ITEM_MOVIE_DETAILS_ADD_TO_FAV_ID = UUID.randomUUID().toString();
        HELP_ITEM_MOVIES_LIST_FIRST_CONTACT_ID = UUID.randomUUID().toString();
    }

    private SpotlightConfig getSpotlightConfig(Context context) {

        WeakReference<Context> contextWeakReference = new WeakReference<>(context);

        SpotlightConfig config = new SpotlightConfig();
        config.setIntroAnimationDuration(300);
        config.setRevealAnimationEnabled(true);
        config.setPerformClick(true);
        config.setFadingTextDuration(400);
        config.setHeadingTvColor(contextWeakReference.get().getResources().getColor(R.color.accent_dark));
        config.setHeadingTvSize(22);
        config.setSubHeadingTvColor(Color.parseColor("#ffffff"));
        config.setSubHeadingTvSize(16);
        config.setMaskColor(Color.parseColor("#dc000000"));
        config.setLineAnimationDuration(300);
        config.setLineAndArcColor(contextWeakReference.get().getResources().getColor(R.color.accent));
        config.setDismissOnTouch(true);
        config.setDismissOnBackpress(true);

        return config;
    }

}
