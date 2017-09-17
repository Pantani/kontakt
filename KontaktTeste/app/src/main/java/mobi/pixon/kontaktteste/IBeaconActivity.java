package mobi.pixon.kontaktteste;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;

import timber.log.Timber;

/**
 * Created by danilopantani on 17/09/17.
 */

public class IBeaconActivity extends AppCompatActivity implements BeaconConsumer {

    //================================================================================
    // Variables
    //================================================================================

    private BeaconManager beaconManager;

    //================================================================================
    // Lifecycle
    //================================================================================

    public static Intent getStartIntent(@NonNull Context context) {
        Intent intent = new Intent(context, IBeaconActivity.class);
        return intent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_ibeacon);

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    //================================================================================
    // BeaconConsumer
    //================================================================================

    @Override
    public void onBeaconServiceConnect() {
        try {

            beaconManager.addMonitorNotifier(createMonitorNotifier());

            //ID do seu beacon
            Identifier identifier = Identifier.parse("23542266-18D1-4FE4-B4A1-23F8195B9D39");

            beaconManager.startMonitoringBeaconsInRegion(new Region("pixon.mobi.acessibilidadebeacon",
                    identifier, null, null));

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private MonitorNotifier createMonitorNotifier() {
        return new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Timber.d("I just saw an beacon for the first time!");
            }

            @Override
            public void didExitRegion(Region region) {
                Timber.d("I no longer see an beacon");
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Timber.d("I have just switched from seeing/not seeing beacons: " + state);
            }
        };
    }
}