package com.iot.app.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.iot.app.R;

public class AboutUsFragment extends HBFragment {
    Toolbar aUFToolbar;
    ImageView aUFBackIcon;

    public static AboutUsFragment newInstance() {
        return new AboutUsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account_about_us, container, false);

        aUFBackIcon = view.findViewById(R.id.account_auf_backIcon);
        aUFBackIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction;
                transaction = getAct().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.account_frame, new MainAccountFragment());
                transaction.commit();
                getCont().bottomNavigationView.setVisibility(View.VISIBLE);

            }
        });
        aUFToolbar = view.findViewById(R.id.account_auf_toolbar);
        getAct().setSupportActionBar(aUFToolbar);
        aUFToolbar.setTitle(R.string.about_us);
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser)
            if (aUFToolbar != null)
                aUFToolbar.setTitle(R.string.about_us);
    }
}
