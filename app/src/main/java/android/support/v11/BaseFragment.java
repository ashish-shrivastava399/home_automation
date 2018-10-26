package android.support.v11;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BaseFragment extends Fragment implements OnBackPressed, OnStopParent {

    private String TAG = this.getClass().getSimpleName();

    @Override
    public void onAttach(Context context) {
        Log.e(TAG, TAG + " onAttach invoked");
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, TAG + " onCreate invoked");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, TAG + " onCreateView invoked");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.e(TAG, TAG + " onActivityCreated invoked");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        Log.e(TAG, TAG + " onStart invoked");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.e(TAG, TAG + " onResume invoked");
        super.onResume();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Log.e(TAG, TAG + " setUserVisibleHint " + isVisibleToUser + "   invoked");
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, TAG + " onActivityResult invoked");
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        Log.e(TAG, TAG + " onPause invoked");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.e(TAG, TAG + " onStop invoked");
        super.onStop();
    }

    @Override
    public void onDetach() {
        Log.e(TAG, TAG + " onDestroyView invoked");
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        Log.e(TAG, TAG + " onDestroyView invoked");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, TAG + " onDestroy invoked");
        super.onDestroy();
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
