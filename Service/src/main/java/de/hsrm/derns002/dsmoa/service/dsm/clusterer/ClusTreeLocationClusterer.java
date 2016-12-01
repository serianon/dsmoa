package de.hsrm.derns002.dsmoa.service.dsm.clusterer;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import moa.clusterers.clustree.ClusTree;

/**
 * Will create no cluster. Despite DenstreamWithDBScan and CluStreamWithKmeans
 * having similar issues, this algorithm doesn't have the options to produce
 * the failure due to configuration - it just doesn't have that many parameters
 * to be set wrong, resulting in that unwanted behaviour.
 */
public class ClusTreeLocationClusterer extends LocationClustererTemplate {

    public ClusTreeLocationClusterer(Context context) {
        super(context);

        ClusTree clusTree = new ClusTree();
        clusTree.resetLearningImpl();
        clusTree.horizonOption.setValue(1000);
        clusTree.maxHeightOption.setValue(8);

        setClusterer("ClusTree", clusTree);
    }

    @Override
    public void trainOnLocation(Location location) {
        mClusterer.trainOnInstance(mLocationInstance.getInstance());
        int microCluster = mClusterer.getMicroClusteringResult() == null ? -1 : mClusterer
                .getMicroClusteringResult().size();
        int macroCluster = mClusterer.getClusteringResult() == null ? -1 : mClusterer
                .getClusteringResult().size();
        Log.d(mClustererName, "microclusters: " + microCluster + ", clusters: " + macroCluster);
    }

}
