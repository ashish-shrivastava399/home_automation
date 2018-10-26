package android.support.v11;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.iot.app.R;

import java.util.ArrayList;

public class PermissionUtils {

    private Context context;
    private PermissionResultCallback permissionResultCallback;
    private Activity current_activity;
    private ArrayList<String> permission_list = new ArrayList<>();
    private ArrayList<String> listPermissionsNeeded = new ArrayList<>();
    private String dialog_content = "";
    private int req_code;
    private SharedPreferences permissionStatus;

    public PermissionUtils(Context context) {
        this.context = context;
        this.current_activity = (Activity) context;
        permissionResultCallback = (PermissionResultCallback) context;
    }


    /**
     * Check the API Level & Permission
     *
     * @param permissions    ArrayList<String>
     * @param dialog_content String
     * @param request_code   int
     */

    public void check_permission(ArrayList<String> permissions, String dialog_content, int request_code) {
        permissionStatus = current_activity.getSharedPreferences("permissionStatus", Context.MODE_PRIVATE);
        this.permission_list = permissions;
        this.dialog_content = dialog_content;
        this.req_code = request_code;

        if (Build.VERSION.SDK_INT >= 23) {
            checkAndRequestPermissions(permissions, request_code);
        } else {
            permissionResultCallback.allPermissionGranted(request_code);
        }

    }


    /**
     * Check and request the Permissions
     *
     * @param permissions  ArrayList<String>
     * @param request_code int
     */

    public void checkAndRequestPermissions(ArrayList<String> permissions, int request_code) {

        if (permissions.size() > 0) {
            listPermissionsNeeded = new ArrayList<>();

            boolean bool = false;
            for (int i = 0; i < permissions.size(); i++) {
                int hasPermission = ContextCompat.checkSelfPermission(current_activity, permissions.get(i));

                if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(current_activity, permissions.get(i))) {
                        bool = true;
                    }
                    listPermissionsNeeded.add(permissions.get(i));
                }
            }

            if (!listPermissionsNeeded.isEmpty()) {
                if (bool) {
                    showMessageOKCancel(dialog_content,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            dialog.cancel();
                                            ActivityCompat.requestPermissions(current_activity, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), req_code);
                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            Log.i("Permission", "not fully given");
                                            dialog.cancel();
                                            break;
                                    }
                                }
                            });
                } else if (permissionStatus.getBoolean(permission_list.get(0), false)) {
                    //Previously Permission Request was cancelled with 'Never Ask Again',
                    // Redirect to Settings after showing Information about why you need the permission
                    showMessageOKCancel(dialog_content,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            dialog.cancel();
                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            Uri uri = Uri.fromParts(current_activity.getString(R.string._package), current_activity.getPackageName(), null);
                                            intent.setData(uri);
                                            current_activity.startActivityForResult(intent, Const.REQUEST_PERMISSION_SETTING);
                                            Toast.makeText(context, R.string.go_to_setting, Toast.LENGTH_LONG).show();
                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            Log.i("Permission", "not fully given");
                                            dialog.cancel();
                                            break;
                                    }
                                }
                            });
                } else {
                    ActivityCompat.requestPermissions(current_activity, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), request_code);
                }

                SharedPreferences.Editor editor = permissionStatus.edit();
                editor.putBoolean(permission_list.get(0), true);
                editor.apply();
            } else {
                permissionResultCallback.allPermissionGranted(request_code);
            }
        }
    }

    /**
     * @param requestCode  int
     * @param grantResults int[]
     */
    public void onRequestPermissionsResult(final int requestCode, int[] grantResults) {
        switch (requestCode) {
            case Const.REQUEST_PERMISSIONS:
                if (grantResults.length > 0) {
                    boolean all_granted = false;
                    for (int grantResult : grantResults) {
                        if (grantResult == PackageManager.PERMISSION_GRANTED) {
                            all_granted = true;
                        } else {
                            all_granted = false;
                            break;
                        }
                    }

                    if (all_granted) {
                        permissionResultCallback.allPermissionGranted(requestCode);
                    } else {
                        boolean bool = false;
                        for (int i = 0; i < listPermissionsNeeded.size(); i++) {
                            int hasPermission = ActivityCompat.checkSelfPermission(current_activity, listPermissionsNeeded.get(i));
                            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                                if (ActivityCompat.shouldShowRequestPermissionRationale(current_activity, listPermissionsNeeded.get(i))) {
                                    bool = true;
                                }
                            } else {
                                listPermissionsNeeded.remove(listPermissionsNeeded.get(i));
                                i--;
                            }
                        }
                        if (bool) {
                            showMessageOKCancel(dialog_content,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    dialog.cancel();
                                                    ActivityCompat.requestPermissions(current_activity, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), req_code);
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    Log.i("Permission", "not fully given");
                                                    dialog.cancel();
                                                    if (listPermissionsNeeded.size() == permission_list.size())
                                                        permissionResultCallback.allPermissionDenied(requestCode);
                                                    else
                                                        permissionResultCallback.partialPermissionGranted(requestCode);
                                                    break;
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(context, R.string.unable_to_get_all_permissions, Toast.LENGTH_LONG).show();
                            permissionResultCallback.partialPermissionGranted(requestCode);
                        }
                    }
                }
                break;
        }
    }


    /**
     * Explain why the app needs permissions
     *
     * @param message    String
     * @param okListener DialogInterface.OnClickListener
     */
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
       AlertDialog.Builder dialog =  new AlertDialog.Builder(current_activity);
       dialog.setMessage(message)
                .setPositiveButton(R.string.grant, okListener)
                .setNegativeButton(R.string.cancel, okListener)
                .create()
                .show();

    }

}
