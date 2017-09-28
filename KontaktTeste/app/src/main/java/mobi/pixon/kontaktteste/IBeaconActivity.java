package mobi.pixon.kontaktteste;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import timber.log.Timber;

/**
 * Created by danilopantani on 17/09/17.
 */

public class IBeaconActivity extends AppCompatActivity implements BeaconConsumer {

    @BindView(R.id.ibeacon_lbl_status)
    TextView lblStatus;

    //================================================================================
    // Variables
    //================================================================================

    private final static int MAX_LOG = 3;
    private List<String> logs = new ArrayList<>();
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
        if (beaconManager != null) {
            beaconManager.unbind(this);
        }
    }

    //================================================================================
    // Private Methods
    //================================================================================

    private void logResponse(String log) {
        Timber.e(log);
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
                String log = "I just saw an beacon for the first time!";
                logResponse(log);
            }

            @Override
            public void didExitRegion(Region region) {
                String log = "I no longer see an beacon";
                logResponse(log);
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                String log = "I have just switched from seeing/not seeing beacons: " + state;
                logResponse(log);
            }
        };
    }
}