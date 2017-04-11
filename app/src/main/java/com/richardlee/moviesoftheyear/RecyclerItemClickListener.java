package com.richardlee.moviesoftheyear;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.lang.ref.WeakReference;

class RecyclerItemClickListener extends RecyclerView.SimpleOnItemTouchListener {
    private static final String TAG = "RecyclerItemClickListen";

    private final WeakReference<Context> mContext;
    private final WeakReference<RecyclerView> mRecyclerView;
    private final WeakReference<OnRecylerClickListener> mListener;
    private final GestureDetectorCompat mGestureDetector;

    public RecyclerItemClickListener(Context context, RecyclerView recyclerView, OnRecylerClickListener listener) {
        mContext = new WeakReference<Context>(context);
        mRecyclerView = new WeakReference<RecyclerView>(recyclerView);
        mListener = new WeakReference<OnRecylerClickListener>(listener);

        mGestureDetector = new GestureDetectorCompat(mContext.get(), new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {

                View itemView = mRecyclerView.get().findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if (itemView != null && mListener != null) {
                    mListener.get().onItemClick(itemView, mRecyclerView.get().getChildAdapterPosition(itemView));
                }
                return true;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {

                View itemView = mRecyclerView.get().findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if (itemView != null && mListener != null) {
                    mListener.get().onItemLongClick(itemView, mRecyclerView.get().getChildAdapterPosition(itemView));
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

        if (mGestureDetector != null) {
            return mGestureDetector.onTouchEvent(e);
        }
        return false;
    }


    interface OnRecylerClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }
}
