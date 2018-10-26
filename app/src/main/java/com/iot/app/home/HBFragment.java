package com.iot.app.home;

import android.support.v11.BaseFragment;
import android.support.v4.app.Fragment;
import android.util.Log;

public class HBFragment extends BaseFragment {

    public String TAG = this.getClass().getSimpleName();

    protected HomeActivity getAct() {
        return ((HomeActivity) getActivity());
    }

    protected HomeActivity getCont() {
        return ((HomeActivity) getContext());
    }

    public void onBackPressed() {
        Log.e(TAG, TAG + " onBackPressed invoked");
    }

    @Override
    public void onBackPressed(Fragment fragment) {
        Log.e(TAG, TAG + " onBackPressed fragment invoked ");
    }

    @Override
    public void onStopParent() {
        Log.e(TAG, TAG + " onStopParent invoked");
    }

    @Override
    public void onStopParent(Fragment fragment) {
        Log.e(TAG, TAG + " onStopParent fragment invoked");
    }
}
