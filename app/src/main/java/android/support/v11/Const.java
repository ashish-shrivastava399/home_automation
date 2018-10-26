package android.support.v11;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.iot.app.R;
import com.iot.app.home.PrimePreference;

import java.util.Calendar;
import java.util.Locale;

public class Const {
    public static final int BLUE_DARK = 0;
    public static final int BLACK_DARK = 1;
    public static final int WHITE_LIGHT = 2;
    public static final int RQ_MKT_VIEW_FAV = 0;
    public static final int RQ_MKT_VIEW_BTC = 1;
    public static final int RQ_MKT_VIEW_KRB = 2;
    public static final int RQ_MKT_VIEW_UAH = 3;
    public static final int RQ_ORD_VIEW_OPEN = 0;
    public static final int RQ_ORD_VIEW_CLOSE = 1;
    public static final int RQ_TRADE_VIEW_OPEN = R.string.buy;
    public static final int RQ_QRCODE = 121;
    public static final int RESULT_OK = 90;
    public static final int RQ_CHANGE_PASS = 116;

    private static String TAG = "Const";
    public static final int REQUEST_PERMISSIONS = 1;
    public static final int REQUEST_PERMISSION_SETTING = 2;
    public static final int MAX_VIEW_PAGE = 4;
    public static final int ON_RESTART_REQUEST_CODE = 16;
    public static final int ON_RESTART_RESULT_CODE = 17;
    public static final int ON_CALL_ACTIVITY = 17;

    public static final int PARENT_REQUEST_CODE = 19;
    public static final int CHILD_RESULT_CODE = 20;
    public static final int RC_GET_ACCOUNT = 21;
    public static final int NO_ACCOUNT_RQ = 27;
    public static final int ALREADY_ACCOUNT_RQ = 28;
    public static final int RQ_MOB_VERIFY = 29;
    public static final int RESULT_ALL_OK = 30;
    public static final int RESULT_ERROR = 31;
    public static final int RQ_AADHAAR_SCANNER = 32;
    public static final int RQ_AADHAAR_VERIFY = 33;
    public static final int RESULT_CANCEL = 34;
    public static final int RESULT_RETRY = 35;
    public static final int HEADER = 0;
    public static final int ITEM = 1;
    public static final int LOGOUT = 2;

    public static final String SITE_KEY = "6LdhyWkUAAAAAJOlo-KwyMycMrU9sbxbX35vAqih";
    public static final String HEAD_URL_ENCODE = "application/x-www-form-urlencoded";
    public static final String HEAD_UTF8 = "application/json;charset=UTF-8";

    public static final int BUY_TRADE = 0;
    public static final int SELL_TRADE = 1;

    public static final Integer PERMISSION_WRITE_REQUEST_CODE = 26;
    public static final int NOACCOUNT_RQ = 27;
    public static final int PASSCODE_TURN_OFF = 112;

    public static final int ITEM_MIDDLE = 2;
    public static final int ITEM_CLOSED = 4;
    public static final int ITEM_OPEN = 5;

    public static long getCurrentTimeInMilliseconds() {
        return Calendar.getInstance().getTimeInMillis();
    }

    public static long getTimePlusHours(long time, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.add(Calendar.HOUR, hours);
        return calendar.getTimeInMillis();
    }

    public static boolean updateResourcesLegacy(Context context, String language) {
        try {
            Locale locale = new Locale(language);
            Locale.setDefault(locale);

            Resources resources = context.getResources();

            Configuration configuration = resources.getConfiguration();
            configuration.locale = locale;
            configuration.setLayoutDirection(locale);

            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Exception --> updateResourcesLegacy() " + e.getMessage());
            return false;
        }
    }

    public static void changeBarColor(Context context, Toolbar toolbar, Window window, boolean status) {
        try {
            if (status) {
                if (Build.VERSION.SDK_INT >= 21) {
                    window.setStatusBarColor(context.getResources().getColor(R.color.colorStatusBar));
                } else {
                    toolbar.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                }
            } else {
                if (Build.VERSION.SDK_INT >= 21) {
                    window.setStatusBarColor(context.getResources().getColor(R.color.textColorRed));
                } else {
                    toolbar.setBackgroundColor(context.getResources().getColor(R.color.textColorRed));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception --> changeBarColor() " + e.getMessage());
        }
    }


    public static void showCustomMess(Context context, int messId, boolean status) {
        try {
            LayoutInflater inflater = ((AppCompatActivity) context).getLayoutInflater();
            View viewToast = inflater.inflate(R.layout.view_custom_toast, null);
            ImageView view = viewToast.findViewById(R.id.icon);
            if (status) view.setImageResource(R.drawable.ic_success);
            else view.setImageResource(R.drawable.ic_error);
            TextView textView = viewToast.findViewById(R.id.message);
            textView.setText(context.getString(messId));

            Toast customToast = new Toast(context);

            customToast.setView(viewToast);
            customToast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
            customToast.setDuration(Toast.LENGTH_LONG);
            customToast.show();
        } catch (Exception e) {
            Log.e(TAG, "Exception --> showCustomMess() " + e.getMessage());
        }
    }

    public static void logOut(Context context) {
        PrimePreference rhPreference = new PrimePreference(context);
        rhPreference.removeLoginStatus();
        rhPreference.removePasscode();
        try {
            FirebaseAuth.getInstance().signOut();
        } catch (Exception e) {

        }
    }

    public static ProgressDialog createProgressDialog(Context context, @ColorInt int color, int titleID, int messID) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        if (titleID != 0)
            progressDialog.setTitle(titleID);
        if (messID != 0)
            progressDialog.setMessage(context.getString(messID));
        if (color != 0)
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(color));
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        return progressDialog;
    }

}
