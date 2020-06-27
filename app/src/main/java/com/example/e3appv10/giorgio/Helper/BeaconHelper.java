package com.example.e3appv10.giorgio.Helper;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e3appv10.FunzioniCambiaBeacon;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;

public class BeaconHelper implements BeaconConsumer, RangeNotifier {

    private BeaconManager mBeaconManager;
    private Beacon nearestBeacon;
    private Region mRegion;
    private Context context;
    private static final long DEFAULT_SCAN_PERIOD_MS = 3000l;
    private static final String ALL_BEACONS_REGION = "AllBeaconsRegion";
    private FunzioniCambiaBeacon funzioniCambiaBeacon;


    public BeaconHelper(Context context, FunzioniCambiaBeacon funzioniCambiaBeacon) {

        this.context = context;
        this.funzioniCambiaBeacon = funzioniCambiaBeacon;

        mBeaconManager = BeaconManager.getInstanceForApplication(context);

        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));

        ArrayList<Identifier> identifiers = new ArrayList<>();

        mRegion = new Region(ALL_BEACONS_REGION, identifiers);
    }

    private void showToastMessage (String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    public void onBeaconServiceConnect() {
        try {
            mBeaconManager.startRangingBeaconsInRegion(mRegion);

            //showToastMessage("Cercando beacon..");

        } catch (RemoteException e) {
            Log.d("TAG", "Exception" + e.getMessage());
        }

        mBeaconManager.addRangeNotifier(this);

    }

    @Override
    public Context getApplicationContext() {
        return context;
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {
        context.unbindService(serviceConnection);
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return context.bindService(intent,  serviceConnection, i);

    }


    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

        if (beacons.size() == 0) {
            //showToastMessage(context.getString(R.string.no_beacons_detected));
        }
        if((funzioniCambiaBeacon != null) && (beacons!=null) && (beacons.size()>0)) {
            //notifico il beacon più vicino
            String idBeacon = getMinimumDistance(beacons).getId2().toString();
            funzioniCambiaBeacon.onChangeSource(idBeacon);
        }
    }


    private Beacon getMinimumDistance(Collection<Beacon> beacons){
        double min = 0.0;
        boolean flag = true;
        String tutti = " ";
        for(Beacon beacon: beacons){
            if(flag){
                min = beacon.getDistance();
                nearestBeacon = beacon;
                flag = false;
            }
            if (beacon.getDistance() < min){
                nearestBeacon = beacon;
                min = beacon.getDistance();
            }
            int distanza = (int) (beacon.getDistance() * 100);
            tutti = tutti + "ID: " + beacon.getId2().toString() + " - distanza: " + distanza + "\n";
        }
        //dò delle informazioni su tutti i beacon rilevati
        funzioniCambiaBeacon.infoBeaconsResived(tutti);
        return nearestBeacon;

    }

    public Beacon getNearestBeacon() {
        return nearestBeacon;
    }

    public void stopDetectingBeacons() {

        try {
            mBeaconManager.stopMonitoringBeaconsInRegion(mRegion);
            //showToastMessage(context.getString(R.string.stop_looking_for_beacons));
        } catch (RemoteException e) {
            Log.d("Errore", "Errore: " + e.getMessage());
        }

        mBeaconManager.removeAllRangeNotifiers();
        mBeaconManager.unbind(this);
    }

    public void startDetectingBeacons() {

        mBeaconManager.setForegroundScanPeriod(DEFAULT_SCAN_PERIOD_MS);
        mBeaconManager.bind(this);
    }


    public void clear(){
        mBeaconManager.removeAllRangeNotifiers();
        mBeaconManager.unbind(this);
    }
}
