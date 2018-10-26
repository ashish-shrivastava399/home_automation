package com.iot.app.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.iot.app.R;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    public BottomNavigationView bottomNavigationView;
    public MenuItem prevMenuItem;
    public ViewPager viewPager;
    public HomePagerAdapter adapter;
    public ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            if (prevMenuItem != null) {
                prevMenuItem.setChecked(false);
            } else {
                bottomNavigationView.getMenu().getItem(0).setChecked(false);
            }
            bottomNavigationView.getMenu().getItem(position).setChecked(true);
            prevMenuItem = bottomNavigationView.getMenu().getItem(position);
            viewPager.destroyDrawingCache();
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    public BottomNavigationView.OnNavigationItemSelectedListener bNTV = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_home:
                    viewPager.setCurrentItem(0);
                    viewPager.destroyDrawingCache();
                    break;
                case R.id.action_info:
                    viewPager.setCurrentItem(1);
                    viewPager.destroyDrawingCache();
                    break;
                case R.id.action_account:
                    viewPager.setCurrentItem(2);
                    viewPager.destroyDrawingCache();
                    break;
            }
            return false;
        }
    };



    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        viewPager = findViewById(R.id.view_pager);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(bNTV);

        adapter = new HomePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(0);

        viewPager.addOnPageChangeListener(pageChangeListener);

        viewPager.destroyDrawingCache();
    }

    class HomePagerAdapter extends FragmentStatePagerAdapter {

        HomePagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return HomeFragment.newInstance();
                case 1:
                    return InfoFragment.newInstance();
                case 2:
                    return AccountFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

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
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            for (Fragment f : fragments) {
                if (f instanceof HBFragment)
                    ((HBFragment) f).onBackPressed();
            }
        } catch (Exception e) {
            Log.e("HOME", "Exception --> tellFragments" + e.getMessage());
        }
    }
}
