package mobi.pixon.kontaktteste;

import android.Manifest;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import butterknife.ButterKnife;
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
        ButterKnife.bind(this);
        requestLocationPermission();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new ProdLogTree());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissionManager != null) {
            permissionManager.onPermissionReceived(requestCode, permissions, grantResults);
        }
    }

    //================================================================================
    // Private Methods
    //================================================================================

    private static class ProdLogTree extends Timber.Tree {
        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
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

    private void requestLocationPermission() {
        if (permissionManager == null) {
            permissionManager = new PermissionManager(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
        }

        permissionManager.requestPermission(getString(R.string.permission_title_location),
                getString(R.string.permission_name_location),
                getString(R.string.permission_rationale_location), true, this);
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