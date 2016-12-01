package de.hsrm.derns002.dsmoa.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import de.hsrm.derns002.dsmoa.lib.IService;
import de.hsrm.derns002.dsmoa.lib.IServiceListener;
import de.hsrm.derns002.dsmoa.service.dsm.LocationMiner;
import de.hsrm.derns002.dsmoa.service.event.CallbackDebugModeEvent;
import de.hsrm.derns002.dsmoa.service.event.DsmCommandEvent;

public class DsmService extends Service implements LocationListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "DsmService";
    private static final String TRACKINGACC_PREFKEY = "trackingaccuracy";
    private static final String DEBUGMODE_PREFKEY = "debugmode";
    private static final String CLUSTERALGO_PREFKEY = "clusteralgo";

    private boolean mDebugMode = false;
    private LocationMiner mLocationMiner;
    private List<IServiceListener> mListeners = new ArrayList<>();
    private LocationManager mLocationManager;
    private final IService.Stub mBinder = new IService.Stub() {

        public void registerListener(IServiceListener listener) {
            Log.d(TAG, "registering listener");
            mListeners.add(listener);
        }

        public boolean unregisterListener(IServiceListener listener) {
            Log.d(TAG, "unregistering listener");
            return mListeners.remove(listener);
        }

        public boolean setLabelForCurrentCluster(String labelName) {
            Log.d(TAG, "setting label for current cluster: " + labelName + " (current cluster = null)");
            return true;
        }

        public boolean removeLabelForCurrentCluster() {
            Log.d(TAG, "removing label for current cluster (current cluster = null)");
            return true;
        }

        public boolean removeAllLabels() {
            Log.d(TAG, "removing all labels (count = null)");
            return true;
        }

    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        mDebugMode = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(DEBUGMODE_PREFKEY, false);
        EventBus.getDefault().register(this);
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        mLocationMiner = new LocationMiner(this);
        if (!mDebugMode) startTrackingByPrefValue(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        EventBus.getDefault().unregister(this);
        stopTracking();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case DEBUGMODE_PREFKEY:
                mDebugMode = sharedPreferences.getBoolean(DEBUGMODE_PREFKEY, false);
                if (mDebugMode) {
                    stopTracking();
                } else {
                    startTrackingByPrefValue(PreferenceManager.getDefaultSharedPreferences(
                            getApplicationContext()));
                }
                break;
            case TRACKINGACC_PREFKEY:
                stopTracking();
                startTrackingByPrefValue(sharedPreferences);
                break;
            case CLUSTERALGO_PREFKEY:
                mLocationMiner.updateLocationClusterer();
                break;
            default: break;
        }
    }

    private void startTrackingByPrefValue(SharedPreferences sharedPreferences) {
        final String setting = sharedPreferences.getString(TRACKINGACC_PREFKEY, "Coarse");
        Dexter.checkPermission(new PermissionListener() {

            @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                int permissionCheck = ContextCompat.checkSelfPermission(DsmService.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) return;
                long minTimeDelta;
                float minDistDelta;
                Criteria criteria = new Criteria();
                switch (setting) {
                    case "Coarse":
                        minTimeDelta = 6 * 1000; // 6 seconds
                        minDistDelta = 30.0f; // 30 meters
                        criteria.setHorizontalAccuracy(Criteria.ACCURACY_MEDIUM);
                        criteria.setVerticalAccuracy(Criteria.ACCURACY_LOW);
                        criteria.setBearingAccuracy(Criteria.ACCURACY_LOW);
                        criteria.setPowerRequirement(Criteria.POWER_LOW);
                        criteria.setAltitudeRequired(false);
                        criteria.setBearingRequired(false);
                        criteria.setCostAllowed(false);
                        criteria.setSpeedRequired(false);
                        Log.d(TAG, "requesting coarse tracking");
                        mLocationManager.requestLocationUpdates(minTimeDelta, minDistDelta, criteria,
                                DsmService.this, null);
                        break;
                    case "Fine":
                        minTimeDelta = 3 * 1000; // 3 seconds
                        minDistDelta = 30.0f; // 30 meters
                        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
                        criteria.setVerticalAccuracy(Criteria.ACCURACY_LOW);
                        criteria.setBearingAccuracy(Criteria.ACCURACY_LOW);
                        criteria.setPowerRequirement(Criteria.POWER_HIGH);
                        criteria.setAltitudeRequired(false);
                        criteria.setBearingRequired(false);
                        criteria.setCostAllowed(false);
                        criteria.setSpeedRequired(false);
                        Log.d(TAG, "requesting fine tracking");
                        mLocationManager.requestLocationUpdates(minTimeDelta, minDistDelta, criteria,
                                DsmService.this, null);
                        break;
                    case "Debug":
                        Log.d(TAG, "requesting debug tracking");
                        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L,
                                0.0f, DsmService.this);
                        break;
                    default:
                        Log.e(TAG, "unknown tracking accuracy prefvalue");
                }
            }

            @Override public void onPermissionDenied(PermissionDeniedResponse response) {
                Log.e(TAG, "no permission granted");
            }

            @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission,
                                                                     PermissionToken token) {}

        }, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void stopTracking() {
        Log.w(TAG, "stop tracking");
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            mLocationManager.removeUpdates(this);
        } else {
            Log.e(TAG, "no permission granted");
        }
    }

    public void onLocationChanged(Location location) {
        if (!mDebugMode) EventBus.getDefault().post(location);
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "onStatusChanged");
    }

    public void onProviderEnabled(String provider) {
        Log.d(TAG, "onProviderEnabled: " + provider);
    }

    public void onProviderDisabled(String provider) {
        Log.d(TAG, "onProviderDisabled: " + provider);
    }

    @SuppressWarnings("unused") // will be called by EventBus
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onLocationEvent(Location location) {
        Log.d(TAG, "new GPS fix received");
        // learn
        mLocationMiner.processLocation(location);
        // send result
        if (EventBus.getDefault().hasSubscriberForEvent(LocationMiner.class)) {
            EventBus.getDefault().post(mLocationMiner);
        }
    }

    @SuppressWarnings("unused") // will be called by EventBus
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onDsmCommandEvent(DsmCommandEvent event) {
        switch (event.getCommand()) {
            case RESET_CLUSTERER: mLocationMiner.getLocationClusterer().resetClusterer(); break;
            default: Log.e(TAG, "unknown DsmCommand"); break;
        }
    }

    @SuppressWarnings("unused") // will be called by EventBus
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onCallbackDebugModeEvent(CallbackDebugModeEvent event) {
        if (!mDebugMode) return;

        Log.d(TAG, "onCallbackDebugModeEvent");
        Log.d(TAG, "waiting for delay...");
        try {
            Thread.sleep(event.getDelay());
        } catch (InterruptedException e) {
            Log.w(TAG, "callback-debug-mode's delay has been interrupted");
        }
        if (mListeners.size() > 0) {
            Log.d(TAG, "triggering method on all listeners");
        } else {
            Log.d(TAG, "no listeners");
        }
        for (int i = 0; i < mListeners.size(); i++) {
            try {
                switch (event.getMethodIndex()) {
                    case 0: mListeners.get(i)._onLabeledClusterEnter(event.getLabel()); break;
                    case 1: mListeners.get(i)._onLabeledClusterExit(event.getLabel()); break;
                    case 2: mListeners.get(i)._onUnlabeledClusterEnter(event.getClusterNumber()); break;
                    case 3: mListeners.get(i)._onUnlabeledClusterExit(event.getClusterNumber()); break;
                    case 4: mListeners.get(i)._onPredictionChange(event.getLabel()); break;
                    default: Log.w(TAG, "onSharedPreferenceChanged called, but method-index "
                            + event.getMethodIndex() + " unknown"); break;
                }
            } catch (RemoteException e) {
                Log.e(TAG, "calling listener " + i + " in callback-debug-mode failed with " +
                        "RemoteException; ignoring");
            }
        }
    }

}
