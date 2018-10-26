package com.iot.app;


import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;


@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    CancellationSignal mCancellationSignal;
    boolean mSelfCancelled;
    private Context context;

    public FingerprintHandler(Context context) {
        this.context = context;
    }

    public void startAuth(FingerprintManager mFingerprintManager, FingerprintManager.CryptoObject cryptoObject) {
        mCancellationSignal = new CancellationSignal();
        mSelfCancelled = false;
        mFingerprintManager.authenticate(cryptoObject, mCancellationSignal, 0, this, null);
    }

    public void cancelAuth() {
        if (mCancellationSignal != null) {
            mSelfCancelled = true;
            mCancellationSignal.cancel();
            mCancellationSignal = null;
        }
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        if (!mSelfCancelled) {
            this.update(context.getString(R.string.auth_error_try_again) + errString, false);
        }
    }

    @Override
    public void onAuthenticationFailed() {

        this.update(context.getString(R.string.auth_failed_try_again), false);
        ((PasscodeActivity) context).restartAuth();

    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        this.update(context.getString(R.string.error_colon) + helpString, false);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        this.update(context.getString(R.string.auth_successful), true);
    }

    private void update(String s, boolean b) {
        ((PasscodeActivity) context).fingerprintResponseUpdate(s, b);
    }

}
