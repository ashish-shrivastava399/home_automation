package android.support.v11;


import android.support.v4.app.Fragment;

public interface OnBackPressed {
    void onBackPressed();
    void onBackPressed(Fragment fragment);
}