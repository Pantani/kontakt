package mobi.pixon.kontaktteste;

import android.Manifest;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener;
import com.kontakt.sdk.android.ble.filter.ibeacon.IBeaconFilter;
import com.kontakt.sdk.android.ble.filter.ibeacon.IBeaconFilters;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.manager.ProximityManagerFactory;
import com.kontakt.sdk.android.ble.manager.listeners.EddystoneListener;
import com.kontakt.sdk.android.ble.manager.listeners.IBeaconListener;
import com.kontakt.sdk.android.ble.manager.listeners.SpaceListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleEddystoneListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleIBeaconListener;
import com.kontakt.sdk.android.common.KontaktSDK;
import com.kontakt.sdk.android.common.profile.IBeaconDevice;
import com.kontakt.sdk.android.common.profile.IBeaconRegion;
import com.kontakt.sdk.android.common.profile.IEddystoneDevice;
import com.kontakt.sdk.android.common.profile.IEddystoneNamespace;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import butterknife.OnClick;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements PermissionManager.PermissionCallback {

    //================================================================================
    // Variables
    //================================================================================

    private PermissionManager permissionManager;

    //================================================================================
    // Lifecycle
    //================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestLocationPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissionManager != null) {
            permissionManager.onPermissionReceived(requestCode, permissions, grantResults);
        }
    }

    //================================================================================
    // Permission Manager
    //================================================================================

    @Override
    public void onPermissionGranted() {

    }

    @Override
    public void onPermissionDenied() {

    }

    protected PermissionManager getPermissionManager() {
        return permissionManager;
    }

    private void requestLocationPermission() {
        if (permissionManager == null) {
            permissionManager = new PermissionManager(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
        }

        permissionManager.requestPermission(getString(R.string.permission_title_location),
                getString(R.string.permission_name_location),
                getString(R.string.permission_rationale_location), false, this);
    }

    //================================================================================
    // Actions
    //================================================================================

    @OnClick(R.id.main_btn_kontakt)
    public void didTapKontakt() {
        startActivity(KontaktActivity.getStartIntent(this));
    }

    @OnClick(R.id.main_btn_ibeacon)
    public void didTapIBeacon() {
        startActivity(IBeaconActivity.getStartIntent(this));
    }
}