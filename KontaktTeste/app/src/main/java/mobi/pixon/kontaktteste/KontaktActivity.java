package mobi.pixon.kontaktteste;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by danilopantani on 17/09/17.
 */

public class KontaktActivity extends AppCompatActivity {

    @BindView(R.id.kontakt_lbl_status)
    TextView lblStatus;

    //================================================================================
    // Variables
    //================================================================================

    private final static int MAX_LOG = 3;
    private ProximityManager proximityManager;
    private List<String> logs = new ArrayList<>();

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
        ButterKnife.bind(this);
        initSdk();
    }

    @Override
    protected void onStart() {
        super.onStart();
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
        KontaktSDK.initialize("TMpXXzAVRlvuTIZYBMiwEuXUrGPZHOco");

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

    private void logResponse(String log) {
        Timber.d(log);
        logs.add(log);

        String allLog = "";
        List<String> tempList = logs;

        if (logs.size() > MAX_LOG) {
            tempList = logs.subList(logs.size() - (MAX_LOG + 1), logs.size() - 1);
        }

        for (String text : tempList) {
            allLog += "\n***************************\n" + text;
        }
        lblStatus.setText(allLog);

    }

    //================================================================================
    // Simple Ranging & Monitoring Devices
    //================================================================================

    private IBeaconListener createSimpleIBeaconListener() {
        return new SimpleIBeaconListener() {
            @Override
            public void onIBeaconDiscovered(IBeaconDevice ibeacon, IBeaconRegion region) {
                String log = "SimpleIBeaconListener onIBeaconDiscovered: " + ibeacon.toString() +
                        "IBeaconRegion: " + region.toString();
                logResponse(log);
            }
        };
    }

    private EddystoneListener createSimpleEddystoneListener() {
        return new SimpleEddystoneListener() {
            @Override
            public void onEddystoneDiscovered(IEddystoneDevice eddystone, IEddystoneNamespace namespace) {
                String log = "SimpleEddystoneListener onEddystoneDiscovered: " + eddystone.toString() +
                        "IEddystoneNamespace: " + namespace.toString();
                logResponse(log);
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
                String log = "IBeaconListener onIBeaconDiscovered: " + iBeacon.toString() +
                        "IBeaconRegion: " + region.toString();
                logResponse(log);
            }

            @Override
            public void onIBeaconsUpdated(List<IBeaconDevice> iBeacons, IBeaconRegion region) {
                String log = "IBeaconListener onIBeaconsUpdated: " + iBeacons +
                        "IBeaconRegion: " + region.toString();
                logResponse(log);
            }

            @Override
            public void onIBeaconLost(IBeaconDevice iBeacon, IBeaconRegion region) {
                String log = "IBeaconListener onIBeaconLost: " + iBeacon.toString() +
                        "IBeaconRegion: " + region.toString();
                logResponse(log);
            }
        };
    }

    private EddystoneListener createEddystoneListener() {
        return new EddystoneListener() {
            @Override
            public void onEddystoneDiscovered(IEddystoneDevice eddystone, IEddystoneNamespace namespace) {
                String log = "EddystoneListener onEddystoneDiscovered: " + eddystone.toString() +
                        "IEddystoneNamespace: " + namespace.toString();
                logResponse(log);
            }

            @Override
            public void onEddystonesUpdated(List<IEddystoneDevice> eddystones, IEddystoneNamespace namespace) {
                String log = "EddystoneListener onEddystonesUpdated: " + eddystones +
                        "IEddystoneNamespace: " + namespace.toString();
                logResponse(log);
            }

            @Override
            public void onEddystoneLost(IEddystoneDevice eddystone, IEddystoneNamespace namespace) {
                String log = "EddystoneListener onEddystoneLost: " + eddystone.toString() +
                        "IEddystoneNamespace: " + namespace.toString();
                logResponse(log);
            }
        };
    }

    private SpaceListener createSpaceListener() {
        return new SpaceListener() {
            @Override
            public void onRegionEntered(IBeaconRegion region) {
                String log = "SpaceListener onRegionEntered: " + region.toString();
                logResponse(log);
            }

            @Override
            public void onRegionAbandoned(IBeaconRegion region) {
                String log = "SpaceListener onRegionAbandoned: " + region.toString();
                logResponse(log);
            }

            @Override
            public void onNamespaceEntered(IEddystoneNamespace namespace) {
                String log = "SpaceListener onNamespaceEntered: " + namespace.toString();
                logResponse(log);
            }

            @Override
            public void onNamespaceAbandoned(IEddystoneNamespace namespace) {
                String log = "SpaceListener onNamespaceAbandoned: " + namespace.toString();
                logResponse(log);
            }
        };
    }

}
