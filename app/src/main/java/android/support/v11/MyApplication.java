package android.support.v11;

import android.app.Application;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatDelegate;

import com.iot.app.R;
import com.iot.app.home.PrimePreference;
//public class MyApplication extends android.support.multidex.MultiDexApplication {

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        int default_theme = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(getString(R.string.theme_setting), getResources().getInteger(R.integer.default_theme));
        if (default_theme == Const.WHITE_LIGHT) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (default_theme == Const.BLACK_DARK) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        PrimePreference rhPreference = new PrimePreference(getApplicationContext());
        Const.updateResourcesLegacy(getApplicationContext(), rhPreference.getLanguageCode());
    }
}
