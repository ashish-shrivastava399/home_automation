package com.iot.app.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.iot.app.R;

import java.util.List;

public class AccountFragment extends HBFragment {

    public FragmentTransaction transaction;
    public FragmentManager fragmentManager;
    FrameLayout accountFrame;
    private String TAG = this.getClass().getSimpleName();

    public static AccountFragment newInstance() {
        return new AccountFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, "in onCreateView");
        View view = inflater.inflate(R.layout.frag_h_account, container, false);
        try {
            HBFragment fag = MainAccountFragment.newInstance();
            fragmentManager = getCont().getSupportFragmentManager();
            accountFrame = view.findViewById(R.id.account_frame);
            if (fragmentManager != null && accountFrame != null) {
                transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.account_frame, fag);
                transaction.commit();
            } else Log.e(TAG, "transaction is null --> onViewCreated() ");
            super.onViewCreated(view, savedInstanceState);
        } catch (Exception e) {
            Log.e(TAG, "Exception --> onViewCreated() " + e.getMessage());
        }
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onStopParent() {
        try {
            onPause();
        } catch (Exception e) {
            Log.e(TAG, "Exception ->> onBackPressed" + e.getMessage());
        }
        super.onStopParent();
    }

    @Override
    public void onBackPressed() {
        try {
            tellBackToFragments();
        } catch (Exception e) {
            Log.e("Home", "Exception --> onBackPressed" + e.getMessage());
        }
    }

    private void tellBackToFragments() {
        try {
            List<Fragment> fragments = getCont().getSupportFragmentManager().getFragments();
            for (Fragment f : fragments) {
                if (f instanceof UserDetailsFragment) {
                    try {
                        transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.account_frame, MainAccountFragment.newInstance());
                        getCont().bottomNavigationView.setVisibility(View.VISIBLE);
                        transaction.commit();
                    } catch (Exception e) {

                    }
                } else if (f instanceof AboutUsFragment) {
                    try {
                        transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.account_frame, MainAccountFragment.newInstance());
                        getCont().bottomNavigationView.setVisibility(View.VISIBLE);
                        transaction.commit();
                    } catch (Exception e) {

                    }
                } else if (f instanceof SettingsFragment) {
                    try {
                        transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.account_frame, MainAccountFragment.newInstance());
                        getCont().bottomNavigationView.setVisibility(View.VISIBLE);
                        transaction.commit();
                    } catch (Exception e) {

                    }
                } else if (f instanceof MainAccountFragment)
                {
                    getCont().viewPager.setCurrentItem(0);
                }
                else if (f instanceof HBFragment) {
                    getCont().viewPager.setCurrentItem(0);
                }
            }
        } catch (Exception e) {
            Log.e("HOME", "Exception --> tellFragments" + e.getMessage());
        }
    }
}
