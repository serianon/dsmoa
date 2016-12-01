package de.hsrm.derns002.dsmoa.service.dsm.clusterer;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import de.hsrm.derns002.dsmoa.service.dsm.algorithms.MyWithDbScan;

/**
 * Copycat of the original DenStreamWithDBScan with the purpose to
 * add Logs in interesting parts of the code to learn more about
 * what is happening (or even manipulate the code for testing purposes).
 */
public class MyWithDbScanLocationClusterer extends LocationClustererTemplate {

    public MyWithDbScanLocationClusterer(Context context) {
        super(context);

        MyWithDbScan myWithDbScan = new MyWithDbScan();
        myWithDbScan.resetLearningImpl();
        //-h horizon (default: 1000), Range of the window.
        myWithDbScan.horizonOption.setValue(1000);
        //-e epsilon (default: 0.02), Defines the epsilon neighbourhood
        myWithDbScan.epsilonOption.setValue(0.1);
        //-b beta (default: 0.2)
        myWithDbScan.betaOption.setValue(0.0002);
        //-m mu (default: 1.0)
        myWithDbScan.muOption.setValue(1); // is also minNumberOfPoints-Value!
        //-i initPoints (default: 1000), Number of points to use for initialization.
        myWithDbScan.initPointsOption.setValue(5);
        //-o offline (default: 2.0), offline multiplier for epsilion.
        myWithDbScan.offlineOption.setValue(2.0);
        //-l lambda (default: 0.25)
        myWithDbScan.lambdaOption.setValue(0.25);
        //-s processingSpeed (default: 100), Number of incoming points per time unit.
        myWithDbScan.speedOption.setValue(10);
        //-M evaluateMicroClustering, Evaluate the underlying microclustering instead of the
        // macro clustering
        myWithDbScan.evaluateMicroClusteringOption.setValue(false);

        setClusterer("MyWithDbScan", myWithDbScan);
    }

    public void trainOnLocation(Location location) {
        mClusterer.trainOnInstance(mLocationInstance.getInstance());
        int microCluster = mClusterer.getMicroClusteringResult() == null ? -1 : mClusterer
                .getMicroClusteringResult().size();
        int macroCluster = mClusterer.getClusteringResult() == null ? -1 : mClusterer
                .getClusteringResult().size();
        Log.d(mClustererName, "microclusters: " + microCluster + ", clusters: " + macroCluster);
    }

}
