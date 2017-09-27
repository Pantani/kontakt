package mobi.pixon.kontaktteste;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import java.lang.ref.WeakReference;

/**
 * Created by danilopantani on 17/09/17.
 */

public class PermissionManager {

    private static final int REQ_PERMISSION = 10;

    private WeakReference<Activity> activityReference;
    private WeakReference<Fragment> fragmentReference;
    private String[] permissionList;
    private PermissionCallback callback;
    private String rationaleMessage;
    private boolean isMandatory;
    private boolean isRequiredByUserAction;
    private AlertDialog.Builder rationaleDialog;
    private String permissionName;
    private String title;

    /**
     * Constructor to use inside activities.
     *
     * @param activity       that will receive the permission results from android.
     * @param permissionList list with all permissions to be requested.
     */
    public PermissionManager(Activity activity, String[] permissionList) {
        this.activityReference = new WeakReference(activity);
        this.permissionList = permissionList;
    }

    /**
     * Constructor to use inside fragments.
     *
     * @param fragment       that will receive the permission results from android.
     * @param permissionList list with all permissions to be requested.
     */
    public PermissionManager(Fragment fragment, String[] permissionList) {
        this.fragmentReference = new WeakReference(fragment);
        this.activityReference = new WeakReference(fragment.getActivity());
        this.permissionList = permissionList;
    }

    /**
     * @param context        Used to Android API verify the permission status.
     * @param permissionList List with permissions to verify.
     * @return true if all the permissions in permissionList is granted, false if one of thos is not
     * granted.
     */
    public static boolean isPermissionGranted(Context context, String[] permissionList) {
        for (String permission : permissionList) {
            boolean isGranted = ContextCompat.checkSelfPermission(context, permission)
                    == PackageManager.PERMISSION_GRANTED;
            if (!isGranted) {
                return false;
            }
        }

        return true;
    }

    /**
     * Set the variable responsible to call {@link #showNeverAskAgain()} in cases that the user action
     * dispatch a permission request. In cases that is not the user actions dispathing permission requests
     * should be set to false.
     *
     * @param isRequiredByUserAction true to call {@link #showNeverAskAgain()}, false to proceed with the
     *                               permission denied (The default is false).
     * @return The instance of this PermissionManager.1
     */
    public PermissionManager isRequiredByUserAction(boolean isRequiredByUserAction) {
        this.isRequiredByUserAction = isRequiredByUserAction;
        return this;
    }

    /**
     * Method responsible to verify if all te permission asked is already granted, so if NOT,
     * will call {@link #showRationaleDialog()} or dispatch the request to Android API.
     *
     * @param rationaleMessage Message to show if the permission is denied some time.
     * @param isMandatory      Flag to know if the permission is mandatory for feature use.
     * @param callback         The callback to notify the requester after the Android API returns
     *                         the results to the permission requests.
     * @return The PermissionManager object to the requester class.
     */
    public PermissionManager requestPermission(String title, String permissionName, String rationaleMessage, boolean isMandatory, PermissionCallback callback) {
        this.callback = callback;
        this.rationaleMessage = rationaleMessage;
        this.isMandatory = isMandatory;
        this.permissionName = permissionName;
        this.title = title;

        if (isPermissionGranted(getContext(), permissionList)) {
            callback.onPermissionGranted(this);
        } else {
            if (shouldShowPermissionRationale(permissionList)) {
                showRationaleDialog();
            } else {
                requestPermission();
            }
        }

        return this;
    }

    /**
     * Verifies if any permission need to show the rationale message.
     *
     * @param permissionList List of permissions to verify.
     * @return true if need to show or false if not.
     */

    private boolean shouldShowPermissionRationale(String[] permissionList) {
        for (String permission : permissionList) {
            boolean shouldShowRequestPermissionRationale =
                    ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission);
            if (shouldShowRequestPermissionRationale) {
                return true;
            }
        }

        return false;
    }

    /**
     * Do the permission requests to Android API.
     */
    private void requestPermission() {
        if (getFragment() != null) {
            getFragment().requestPermissions(permissionList, REQ_PERMISSION);
        } else {
            ActivityCompat.requestPermissions(getActivity(), permissionList, REQ_PERMISSION);
        }
    }

    /**
     * Show the dialog with the rationale message to the user.
     */
    private void showRationaleDialog() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(getActivity());
        }

        if (rationaleDialog == null) {
            builder.setTitle(title)
                    .setMessage(rationaleMessage)
                    .setPositiveButton(R.string.dialog_button_ok_got_it, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            requestPermission();
                        }
                    });
            if (isMandatory) {
                rationaleDialog.setCancelable(false);
            } else {
                rationaleDialog.setNegativeButton(R.string.permission_never_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        callback.onPermissionDenied(PermissionManager.this);
                    }
                });
            }
        }
        rationaleDialog.show();
    }

    /**
     * Method that receives the permissions from Android API and analyse the response.
     * If all permissions is granted, notify the callback. If not, verifies if some of the requested
     * mandatory permissions is marked with Never Ask Again flag, and then show the dialog to the
     * App preferences, where the user can manipulate those permissions.
     * In that cases for mandatory permissions denied, but NOT marked with Never Ask Again flag,
     * will make the request again. And finally for not mandatory permissions denied and marked
     * with Never Ask Again flag will notify the callback with the denied response.
     *
     * @param requestCode  Request code which the Android API is called.
     * @param permissions  The permissions requested to the Android API.
     * @param grantResults The results for the permissions.
     */
    public void onPermissionReceived(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQ_PERMISSION && isPermissionGranted(getContext(), permissions)) {
            callback.onPermissionGranted(this);
        } else if (!shouldShowPermissionRationale(permissions) && (isMandatory || isRequiredByUserAction)) {
            showNeverAskAgain();
        } else if (isMandatory) {
            requestPermission(title, permissionName, rationaleMessage, true, callback);
        } else {
            callback.onPermissionDenied(this);
        }
    }

    /**
     * Method called when a mandatory permission is marked with Never Ask Again flag.
     */
    private void showNeverAskAgain() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(getActivity());
        }
        builder.setTitle(title)
                .setMessage(getContext().getString(R.string.permission_never, permissionName))
                .setPositiveButton(R.string.permission_configurations, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        openSettings();
                    }
                })
                .setCancelable(isRequiredByUserAction)
                .show();
    }

    /**
     * Send user the the app settings screen. Where he can configure the permissions.
     */
    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        getActivity().startActivity(intent);
    }

    /**
     * @return the activity that required the permissions or the activity that the fragment is
     * attached.
     */
    public Activity getActivity() {
        return activityReference.get();
    }

    /**
     * @return the fragment that required the permissions.
     */
    public Fragment getFragment() {
        if (fragmentReference != null) {
            return fragmentReference.get();
        } else {
            return null;
        }
    }

    /**
     * @return the context from the fragment or activity that is requesting permissions.
     */
    public Context getContext() {
        if (getFragment() != null) {
            return getFragment().getContext();
        } else {
            return getActivity();
        }
    }


    /**
     * Callback to notify the requester if the permissions requested are granted or not.
     */
    public interface PermissionCallback {
        void onPermissionGranted(PermissionManager permissionManager);

        void onPermissionDenied(PermissionManager permissionManager);
    }
}