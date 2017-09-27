package mobi.pixon.kontaktteste;

import android.Manifest;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements PermissionManager.PermissionCallback {

    //================================================================================
    // Variables
    //================================================================================

    private PermissionManager locationPermissionManager;
    private PermissionManager bluetoothPermissionManager;

    //================================================================================
    // Lifecycle
    //================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        requestLocationPermission();
//        requesBluetoothPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (locationPermissionManager != null) {
            locationPermissionManager.onPermissionReceived(requestCode, permissions, grantResults);
        }
        if (bluetoothPermissionManager != null) {
            bluetoothPermissionManager.onPermissionReceived(requestCode, permissions, grantResults);
        }
    }

    //================================================================================
    // Permission Manager
    //================================================================================

    @Override
    public void onPermissionGranted(PermissionManager permissionManager) {

    }

    @Override
    public void onPermissionDenied(PermissionManager permissionManager) {

    }

    protected PermissionManager getPermissionManager() {
        return locationPermissionManager;
    }

    private void requestLocationPermission() {
        if (locationPermissionManager == null) {
            locationPermissionManager = new PermissionManager(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
        }

        locationPermissionManager.requestPermission(getString(R.string.permission_title_location),
                getString(R.string.permission_name_location),
                getString(R.string.permission_rationale_location), true, this);
    }

    private void requesBluetoothPermission() {
        if (bluetoothPermissionManager == null) {
            bluetoothPermissionManager = new PermissionManager(this, new String[]{Manifest.permission.BLUETOOTH});
        }

        bluetoothPermissionManager.requestPermission(getString(R.string.permission_title_bluetooth),
                getString(R.string.permission_name_bluetooth),
                getString(R.string.permission_rationale_bluetooth), true, this);
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