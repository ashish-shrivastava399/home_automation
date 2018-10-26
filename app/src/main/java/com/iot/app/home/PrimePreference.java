package com.iot.app.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v11.Const;

import com.iot.app.R;

import java.util.Locale;

public class PrimePreference {
    private Context mContext;
    private SharedPreferences sharedPrefs;

    public PrimePreference(Context context) {
        mContext = context;
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public boolean isFirstLogin() {
        return !sharedPrefs.getBoolean(mContext.getString(R.string.settings_user_is_first_login),
                mContext.getResources().getBoolean(R.bool.settings_user_has_account_default));
    }

    public boolean isLoggedIn() {
        return sharedPrefs.getBoolean(mContext.getString(R.string.settings_user_is_logged_in),
                mContext.getResources().getBoolean(R.bool.settings_user_logged_in_default));
    }

    public boolean hasPasscode() {
        return sharedPrefs.getBoolean(mContext.getString(R.string.settings_user_has_passcode),
                mContext.getResources().getBoolean(R.bool.settings_user_logged_in_default));
    }

    public void setPasscodeStatus(boolean status) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean(mContext.getString(R.string.settings_user_has_passcode), false);
        editor.apply();
    }

    public void setShufflePasscodeStatus(boolean value) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean(mContext.getString(R.string.settings_user_shuffle_passcode), value);
        editor.apply();
    }

    public boolean isShufflePasscode() {
        return sharedPrefs.getBoolean(mContext.getString(R.string.settings_user_shuffle_passcode),
                true);
    }


    public void setPasscode(String pin) {
        if (isLoggedIn()) {
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString(mContext.getString(R.string.settings_user_passcode), pin);
            editor.putInt(mContext.getString(R.string.settings_user_wrong_attempts_left),
                    mContext.getResources().getInteger(R.integer.settings_user_max_wrong_attempts));
            editor.putBoolean(mContext.getString(R.string.settings_user_is_first_login), true);
            editor.putBoolean(mContext.getString(R.string.settings_user_has_passcode), true);
            editor.apply();
        }
    }

    public boolean matchPasscode(String passkey) {
        String passcode = sharedPrefs.getString(mContext.getString(R.string.settings_user_passcode), null);
        if (passcode.equals(passkey)) {
            updateAttempts(mContext.getResources().getInteger(R.integer.settings_user_max_wrong_attempts));
            return true;
        } else {
            updateAttempts(getWrongAttemptsLeft() - 1);
            return false;
        }
    }


    public void removePasscode() {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.remove(mContext.getString(R.string.settings_user_secret_public_key));
        editor.remove(mContext.getString(R.string.settings_user_passcode));
        editor.remove(mContext.getString(R.string.settings_user_wrong_attempts_left));
        editor.remove(mContext.getString(R.string.settings_user_has_passcode));
        editor.apply();
        resetAttempts();
    }

    private void updateAttempts(int value) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt(mContext.getString(R.string.settings_user_wrong_attempts_left), value);
        editor.putLong(mContext.getString(R.string.settings_user_last_login_attempt_time), Const.getCurrentTimeInMilliseconds());
        editor.apply();
    }

    public int getWrongAttemptsLeft() {
        return sharedPrefs.getInt(mContext.getString(R.string.settings_user_wrong_attempts_left),
                mContext.getResources().getInteger(R.integer.settings_user_max_wrong_attempts));
    }

    public long getLastLoginAttemptTime() {
        return sharedPrefs.getLong(mContext.getString(R.string.settings_user_last_login_attempt_time), Const.getCurrentTimeInMilliseconds());
    }

    public void resetAttempts() {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt(mContext.getString(R.string.settings_user_wrong_attempts_left),
                mContext.getResources().getInteger(R.integer.settings_user_max_wrong_attempts));
        editor.apply();
    }

    public void updateLoginStatus(boolean status) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean(mContext.getString(R.string.settings_user_is_logged_in), status);
        editor.putBoolean(mContext.getString(R.string.settings_user_is_first_login), true);
        editor.apply();
    }

    public void removeLoginStatus() {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.remove(mContext.getString(R.string.settings_user_is_logged_in));
        editor.apply();
    }

    public int getPasscodeSize() {
        return sharedPrefs.getInt(mContext.getString(R.string.settings_user_passcode_size),
                mContext.getResources().getInteger(R.integer.settings_user_passcode_size_default));
    }

    public void setPasscodeSize(int value) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt(mContext.getString(R.string.settings_user_passcode_size), value);
        editor.apply();
    }

    public int getOrgPasscodeSize() {
        return sharedPrefs.getInt(mContext.getString(R.string.settings_user_passcode_org_size),
                mContext.getResources().getInteger(R.integer.settings_user_passcode_size_default));
    }

    public void setOrgPasscodeSize(int value) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt(mContext.getString(R.string.settings_user_passcode_org_size), value);
        editor.apply();
    }

    public boolean hasMultiTheme() {
        return false;
    }

    public void setRequestedView(String type, int pos) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(mContext.getString(R.string.requested_view_type_pos), type + "," + pos);
        editor.apply();
    }

    public void removeRequestedView() {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.remove(mContext.getString(R.string.requested_view_type_pos));
        editor.apply();
    }

    public String getRequestedView() {
        return sharedPrefs.getString(mContext.getString(R.string.requested_view_type_pos), null);
    }


    public void setLanguage(String language, String langCode, int imgId) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(mContext.getString(R.string.requested_language), language);
        editor.putString(mContext.getString(R.string.requested_language_code), langCode);
        editor.putInt(mContext.getString(R.string.requested_language_img_id), imgId);
        editor.apply();
    }

    public String getLanguage() {
        return sharedPrefs.getString(mContext.getString(R.string.requested_language), Locale.getDefault().getDisplayLanguage());
    }

    public String getLanguageCode() {
        return sharedPrefs.getString(mContext.getString(R.string.requested_language_code), "en");
    }


    public int getLanguageImgId() {
        return sharedPrefs.getInt(mContext.getString(R.string.requested_language_img_id), R.drawable.flag_usa);
    }
}
