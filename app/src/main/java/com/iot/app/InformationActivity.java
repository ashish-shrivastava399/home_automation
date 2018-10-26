package com.iot.app;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v11.Const;
import android.support.v11.PermissionResultCallback;
import android.support.v11.PermissionUtils;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class InformationActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback,
        PermissionResultCallback {

    private final static ArrayList<String> permissions = new ArrayList<>(Arrays.asList(
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION));
    private Runnable r;
    private ViewPager mViewPager;
    private InfoPagerAdapter mPagAdapter;
    private LinearLayout mDotsLayout;
    private TextView mDots[], permission_status;
    private Button mBack, mNext, mSliderButton;
    private int currentDot = 0;
    private int previous_pos = -1;
    private boolean allGranted = false;
    private PermissionUtils permissionUtils;
    private Animation flipToDown, leftToRight, rightToLeft, translateUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        initializeAnimation();
        permissionUtils = new PermissionUtils(this);

        mDotsLayout = findViewById(R.id.dotsLayout);
        mViewPager = findViewById(R.id.sliderView);
        mPagAdapter = new InfoPagerAdapter(this);
        mBack = findViewById(R.id.back);
        mNext = findViewById(R.id.next);


        mBack.setAnimation(rightToLeft);
        mNext.setAnimation(leftToRight);

        leftToRight = new TranslateAnimation(0.0f, -100.0f, 0.0f, 0.0f);
        leftToRight.setDuration(200);
        rightToLeft = new TranslateAnimation(0.0f, 100.0f, 0.0f, 0.0f);
        rightToLeft.setDuration(200);

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int curr = mViewPager.getCurrentItem();
                curr++;
                setEnabled(mNext, curr < Const.MAX_VIEW_PAGE - (allGranted ? 1 : 2));
                mViewPager.setCurrentItem(curr);
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int curr = mViewPager.getCurrentItem();
                curr--;
                setEnabled(mBack, curr > 0);
                mViewPager.setCurrentItem(curr);
            }
        });

        mViewPager.setAdapter(mPagAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(final int position) {
                setEnabled(mNext, position < Const.MAX_VIEW_PAGE - (allGranted ? 1 : 2));
                setEnabled(mBack, position > 0);
                currentDot = position;
                mDots[position].startAnimation(translateUp);
                mDots[previous_pos].startAnimation(translateUp);
            }
        });
        addDots();
        setEnabled(mBack, false);
    }

    private void initializeAnimation() {
        int animationDuration = 1500;
        flipToDown = AnimationUtils.loadAnimation(this, R.anim.fliptodown);
        flipToDown.setDuration(animationDuration);
        leftToRight = AnimationUtils.loadAnimation(this, R.anim.lefttoright);
        leftToRight.setDuration(animationDuration);
        rightToLeft = AnimationUtils.loadAnimation(this, R.anim.righttoleft);
        rightToLeft.setDuration(animationDuration);
        translateUp = new TranslateAnimation(0.0f, 0.0f, 0.0f, -100.0f);
        translateUp.setDuration(100);
        translateUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                addDotsIndicator(currentDot);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void setEnabled(Button button, boolean enable) {
        button.setClickable(enable);
        button.setAlpha(enable ? 1f : 0.5f);
        button.setTextColor(enable ? getResources().getColor(R.color.info_button_enable_txt_color) :
                getResources().getColor(R.color.info_button_disable_txt_color));
    }

    private void addDots() {
        mDots = new TextView[Const.MAX_VIEW_PAGE];
        for (int i = 0; i < mDots.length; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getInteger(R.integer.info_dots_size));
            mDots[i].setTextColor(getResources().getColor(R.color.info_dots_other));
            mDotsLayout.addView(mDots[i]);
        }
        mDotsLayout.setAnimation(flipToDown);
        flipToDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                flipToDown.setDuration(400);
                flipToDown.setAnimationListener(null);
                mDots[0].startAnimation(translateUp);
                final Handler handler = new Handler();
                r = new Runnable() {
                    @Override
                    public void run() {
                        if (translateUp.hasEnded()) {
                            mPagAdapter.setChildCount(Const.MAX_VIEW_PAGE - 1);
                            mPagAdapter.notifyDataSetChanged();
                        } else handler.post(r);
                    }
                };
                handler.post(r);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void addDotsIndicator(int pos) {
        if (mDots.length > 0) {
            if (previous_pos != -1) {
                mDots[previous_pos].setTextColor(getResources().getColor(R.color.info_dots_other));
                mDots[previous_pos].startAnimation(flipToDown);
                mBack.startAnimation(leftToRight);
                mNext.startAnimation(rightToLeft);
            }
            mDots[pos].setTextColor(getResources().getColor(R.color.info_dots_focused));
            mDots[pos].startAnimation(flipToDown);
            previous_pos = pos;
        }
    }

    private void signIn() {
        startActivity(new Intent(this, SignUpActivity.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionUtils.onRequestPermissionsResult(requestCode, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Const.REQUEST_PERMISSION_SETTING) {
            permissionUtils.checkAndRequestPermissions(permissions, Const.REQUEST_PERMISSIONS);
        }
    }

    public void sliderOnClickListener(int position, Button sliderButton, TextView permission_status) {

        if (position == 1) {
            readAgreement();
        } else if (position == 3) {
            signIn();
        } else {
            mSliderButton = sliderButton;
            this.permission_status = permission_status;
            if (allGranted) {
                allPermissionGranted(Const.REQUEST_PERMISSIONS);
            } else {
                setEnabled(mNext, false);
                permissionUtils.check_permission(permissions,
                        getString(R.string.info_explanation_for_permissions),
                        Const.REQUEST_PERMISSIONS);
            }
        }

    }

    private void readAgreement() {
        String url = "https://richamster.com/conditions/";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivityForResult(i, 999);
//        Toast.makeText(this, "Agreement Read", Toast.LENGTH_SHORT).show();
    }

    public void allPermissionDenied(int requestCode) {
        permission_status.setTextColor(getResources().getColor(R.color.info_slider_mess_permission_denied_color));
        permission_status.setText(R.string.all_permission_denied);
        Log.e("Permission", "allPermissionDenied " + requestCode);
    }

    public void partialPermissionGranted(int requestCode) {
        permission_status.setTextColor(getResources().getColor(R.color.info_slider_mess_permission_partial_color));
        permission_status.setText(R.string.partial_permission_granted);
        Log.e("Permission", "partialPermissionGranted " + requestCode);
    }

    public void allPermissionGranted(int requestCode) {
        allGranted = true;
        permission_status.setTextColor(getResources().getColor(R.color.info_slider_mess_permission_granted_color));
        permission_status.setText(R.string.all_permission_granted);
        setEnabled(mNext, true);
        setEnabled(mSliderButton, false);
        mPagAdapter.setChildCount(Const.MAX_VIEW_PAGE);
        mPagAdapter.notifyDataSetChanged();
        Log.e("Permission", "allPermissionGranted " + requestCode);
    }

    public boolean isAllGranted() {
        return allGranted;
    }
}