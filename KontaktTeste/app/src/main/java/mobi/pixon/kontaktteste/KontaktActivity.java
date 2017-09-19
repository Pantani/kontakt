package mobi.pixon.kontaktteste;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

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

import timber.log.Timber;

/**
 * Created by danilopantani on 17/09/17.
 */

public class KontaktActivity extends AppCompatActivity {

    //================================================================================
    // Variables
    //================================================================================

    private ProximityManager proximityManager;

    //================================================================================
    // Lifecycle
    //================================================================================

    public static Intent getStartIntent(@NonNull Context context) {
        Intent intent = new Intent(context, KontaktActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kontakt);

        KontaktSDK.initialize("ngNTVltcrbnkzUAxmCPRyNvhDJXHqpjX"); //Colocar sua chave de api da kontakt aqui

        initSdk();
        startScanning();
    }

    @Override
    protected void onStop() {
        proximityManager.stopScanning();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        proximityManager.disconnect();
        proximityManager = null;
        super.onDestroy();
    }

    //================================================================================
    // Start Scan
    //================================================================================

    private void initSdk() {
        proximityManager = ProximityManagerFactory.create(this);
        proximityManager.setIBeaconListener(createIBeaconListener());
        proximityManager.setEddystoneListener(createEddystoneListener());
        proximityManager.setSpaceListener(createSpaceListener());

        //filtrar os beacons pelo beacon que você deseja
//        List<IBeaconFilter> filterList = Arrays.asList(
//                IBeaconFilters.newProximityUUIDFilter(UUID.fromString("f7826da6-4fa2-4e98-8024-bc5b71e0893e")),
//                IBeaconFilters.newMajorFilter(43),
//                IBeaconFilters.newMinorFilter(34)
//        );

        //filtrar os beacons pelo beacon que você deseja
//        List<IBeaconFilter> filterList = Arrays.asList(
//                (IBeaconFilter) IBeaconFilters.newProximityUUIDFilter(UUID.fromString("f7826da6-4fa2-4e98-8024-bc5b71e0893e"))
//        );

//        proximityManager.filters().iBeaconFilters(filterList);
    }

    private void startScanning() {
        proximityManager.connect(new OnServiceReadyListener() {
            @Override
            public void onServiceReady() {
                proximityManager.startScanning();
            }
        });
    }

    //================================================================================
    // Simple Ranging & Monitoring Devices
    //================================================================================

    private IBeaconListener createSimpleIBeaconListener() {
        return new SimpleIBeaconListener() {
            @Override
            public void onIBeaconDiscovered(IBeaconDevice ibeacon, IBeaconRegion region) {
                Timber.d("SimpleIBeaconListener onIBeaconDiscovered: " + ibeacon.toString() +
                        "IBeaconRegion: " + region.toString());
            }
        };
    }

    private EddystoneListener createSimpleEddystoneListener() {
        return new SimpleEddystoneListener() {
            @Override
            public void onEddystoneDiscovered(IEddystoneDevice eddystone, IEddystoneNamespace namespace) {
                Timber.d("SimpleEddystoneListener onEddystoneDiscovered: " + eddystone.toString() +
                        "IEddystoneNamespace: " + namespace.toString());
            }
        };
    }

    //================================================================================
    // Complex Ranging & Monitoring Devices
    //================================================================================

    private IBeaconListener createIBeaconListener() {
        return new IBeaconListener() {
            @Override
            public void onIBeaconDiscovered(IBeaconDevice iBeacon, IBeaconRegion region) {
                Timber.d("IBeaconListener onIBeaconDiscovered: " + iBeacon.toString() +
                        "IBeaconRegion: " + region.toString());
            }

            @Override
            public void onIBeaconsUpdated(List<IBeaconDevice> iBeacons, IBeaconRegion region) {
                Timber.d("IBeaconListener onIBeaconsUpdated: " + iBeacons +
                        "IBeaconRegion: " + region.toString());
            }

            @Override
            public void onIBeaconLost(IBeaconDevice iBeacon, IBeaconRegion region) {
                Timber.d("IBeaconListener onIBeaconLost: " + iBeacon.toString() +
                        "IBeaconRegion: " + region.toString());
            }
        };
    }

    private EddystoneListener createEddystoneListener() {
        return new EddystoneListener() {
            @Override
            public void onEddystoneDiscovered(IEddystoneDevice eddystone, IEddystoneNamespace namespace) {
                Timber.d("EddystoneListener onEddystoneDiscovered: " + eddystone.toString() +
                        "IEddystoneNamespace: " + namespace.toString());
            }

            @Override
            public void onEddystonesUpdated(List<IEddystoneDevice> eddystones, IEddystoneNamespace namespace) {
                Timber.d("EddystoneListener onEddystonesUpdated: " + eddystones +
                        "IEddystoneNamespace: " + namespace.toString());
            }

            @Override
            public void onEddystoneLost(IEddystoneDevice eddystone, IEddystoneNamespace namespace) {
                Timber.d("EddystoneListener onEddystoneLost: " + eddystone.toString() +
                        "IEddystoneNamespace: " + namespace.toString());
            }
        };
    }

    private SpaceListener createSpaceListener() {
        return new SpaceListener() {
            @Override
            public void onRegionEntered(IBeaconRegion region) {
                Timber.d("SpaceListener onRegionEntered: " + region.toString());
                //IBeacon region has been entered
            }

            @Override
            public void onRegionAbandoned(IBeaconRegion region) {
                Timber.d("SpaceListener onRegionAbandoned: " + region.toString());
                //IBeacon region has been abandoned
            }

            @Override
            public void onNamespaceEntered(IEddystoneNamespace namespace) {
                Timber.d("SpaceListener onNamespaceEntered: " + namespace.toString());
                //Eddystone namespace has been entered
            }

            @Override
            public void onNamespaceAbandoned(IEddystoneNamespace namespace) {
                Timber.d("SpaceListener onNamespaceAbandoned: " + namespace.toString());
                //Eddystone namespace has been abandoned
            }
        };
    }

}
