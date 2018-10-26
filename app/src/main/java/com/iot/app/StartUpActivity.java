package com.iot.app;

import android.animation.Animator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v11.Const;
import android.support.v11.PathAnimatorListener;
import android.support.v11.PenPainter;
import android.support.v11.SyncTextPathView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.iot.app.home.HomeActivity;
import com.iot.app.home.PrimePreference;

public class StartUpActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    private PrimePreference prime;
    private Intent goToIntent;
    private boolean inForeground;
    private Handler handler = new Handler();
    private boolean isCancelTXT = false;

    private SyncTextPathView textView;
    private ProgressBar circularProgressbar;
    private ProgressDialog progressDialog;


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.e(TAG, "in runnable run() method");
            Log.e(TAG, "isCancelTXT " + isCancelTXT);
            if (!isCancelTXT) {
                handler.removeCallbacks(runnable);
            } else {
                if (goToIntent == null) {
                    backgroundCheckStatus(true);
                } else {
                    startActivity(goToIntent);
                    overridePendingTransition(R.anim.gofrom_rightleft, R.anim.goto_rightleft);
                    finishAffinity();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        inForeground = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);
        prime = new PrimePreference(getApplicationContext());
        try {
            circularProgressbar = findViewById(R.id.circularProgressbar);
            circularProgressbar.setVisibility(View.GONE);
            textView = findViewById(R.id.appName);
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    textView.setPathPainter(new PenPainter());
                    textView.startAnimation(0, 1);
                    textView.setAnimatorListener(new PathAnimatorListener() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if (!isCancel) {
                                isCancelTXT = true;
                                textView.showFillColorText();
                                handler.post(runnable);
                            }
                        }
                    });
                }
            });
            progressDialog = Const.createProgressDialog(this, getResources().getColor(R.color.colorPrimaryDeepDark),
                    R.string.slow_internet_connection, R.string.loading);
        } catch (Exception e1) {
            Log.e(TAG, "Exception " + e1.getMessage());
        }
        backgroundCheckStatus(false);
        Log.e(TAG, "onCreate invoked");
    }

    private void backgroundCheckStatus(boolean fromHere) {
        Log.e(TAG, "in backgroundCheckStatus()  start isFirstLogin " + prime.isFirstLogin());
        if (prime.isFirstLogin()) {
            goToIntent = new Intent(StartUpActivity.this, InformationActivity.class);
        } else {
            Log.e(TAG, "in backgroundCheckStatus() in else isFirstLogin  " + prime.isFirstLogin() + " isConnected true");
            if (!prime.isLoggedIn())
                goToIntent = new Intent(StartUpActivity.this, LoginActivity.class);
            else if (prime.hasPasscode()) {
                goToIntent = new Intent(StartUpActivity.this, PasscodeActivity.class);
                goToIntent.putExtra(getString(R.string.entry_pass), R.string.pass_check_point);
            } else {
                goToIntent = new Intent(StartUpActivity.this, HomeActivity.class);
            }
        }

        if (fromHere) handler.post(runnable);
    }

    @Override
    public void onBackPressed() {
        handler.removeCallbacks(runnable);
        Log.d(TAG, "onBackPressed invoked");
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart invoked");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop invoked");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume invoked");
    }

    @Override
    protected void onPause() {
        inForeground = false;
        handler.removeCallbacks(runnable);
        Log.d(TAG, "onPause invoked");
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        inForeground = true;
        Log.d(TAG, "onRestart invoked");
    }


}
