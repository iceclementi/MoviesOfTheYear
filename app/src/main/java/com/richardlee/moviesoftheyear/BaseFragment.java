package com.richardlee.moviesoftheyear;

import android.support.v4.app.Fragment;

import com.squareup.leakcanary.RefWatcher;

public class BaseFragment extends Fragment {

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        RefWatcher refWatcher = MainApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }
}
