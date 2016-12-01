package de.hsrm.derns002.dsmoa.service.dsm;

import android.content.Context;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;

import de.hsrm.derns002.dsmoa.service.dsm.clusterer.ClusTreeLocationClusterer;
import de.hsrm.derns002.dsmoa.service.dsm.clusterer.CobWebLocationClusterer;
import de.hsrm.derns002.dsmoa.service.dsm.clusterer.LocationClusterer;
import de.hsrm.derns002.dsmoa.service.dsm.clusterer.MyWithDbScanLocationClusterer;
import de.hsrm.derns002.dsmoa.service.dsm.clusterer.MyWithKmeansLocationClusterer;

public class LocationMiner {

    private static final String TAG = "LocationMiner";
    private static final String TRAINCLUSTER_PREFKEY = "traincluster";
    private static final String PERSISTCLUSTER_PREFKEY = "persistcluster";
    private static final String CLUSTERALGO_PREFKEY = "clusteralgo";

    private Context mContext;
    private LocationClusterer mLocationClusterer;

    public LocationMiner(Context context) {
        mContext = context;
        updateLocationClusterer();
        if (mLocationClusterer.isModelInStorage()) {
            mLocationClusterer.loadModelFromStorage();
        }
    }

    public LocationMiner(Context context, LocationClusterer locationClusterer) {
        mContext = context;
        mLocationClusterer = locationClusterer;
        if (mLocationClusterer.isModelInStorage()) {
            mLocationClusterer.loadModelFromStorage();
        }
    }

    public void updateLocationClusterer() {
        int clustererAlgo = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString(CLUSTERALGO_PREFKEY, "0"));
        Log.d(TAG, "setting clusterer-algo no. " + clustererAlgo);
        switch (clustererAlgo) {
            // see arrays.xml for the value-name association
            case 0: mLocationClusterer = new MyWithDbScanLocationClusterer(mContext); break;
            case 1: mLocationClusterer = new MyWithKmeansLocationClusterer(mContext); break;
            case 2: mLocationClusterer = new CobWebLocationClusterer(mContext); break;
            case 3: mLocationClusterer = new ClusTreeLocationClusterer(mContext); break;
            default: Log.w(TAG, "clusterer-algo-index " + clustererAlgo + " unknown"); break;
        }
    }

    public void processLocation(Location location) {
        mLocationClusterer.updateLocationInstance(location);
        // train models
        if (PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(
                TRAINCLUSTER_PREFKEY, true)) {
            mLocationClusterer.trainOnLocation(location);
        }
        // save models
        if (PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(
                PERSISTCLUSTER_PREFKEY, true)) {
            mLocationClusterer.saveModelToStorage();
        }
        // test models
        //Cluster c = mLsm.getClusterForLocation(location);
    }

    public LocationClusterer getLocationClusterer() {
        return mLocationClusterer;
    }

}
