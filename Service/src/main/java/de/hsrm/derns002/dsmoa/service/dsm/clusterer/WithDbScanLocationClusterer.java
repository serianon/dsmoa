package de.hsrm.derns002.dsmoa.service.dsm.clusterer;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import moa.clusterers.denstream.WithDBSCAN;

/**
 * Produces microcluster like CluStreamWithKmeans, but just like
 * these algorithms doesn't produce macrocluster, ever.
 */
public class WithDbScanLocationClusterer extends LocationClustererTemplate {

    public WithDbScanLocationClusterer(Context context) {
        super(context);

        WithDBSCAN withDbScan = new WithDBSCAN();
        withDbScan.resetLearningImpl();
        //-h horizon (default: 1000), Range of the window.
        withDbScan.horizonOption.setValue(1000);
        //-e epsilon (default: 0.02), Defines the epsilon neighbourhood
        withDbScan.epsilonOption.setValue(0.1); //denStreamWithDbScan.epsilonOption.setValue(0.0001);
        //-b beta (default: 0.2)
        withDbScan.betaOption.setValue(0.2);
        //-m mu (default: 1.0)
        withDbScan.muOption.setValue(1); // is also minNumberOfPoints-Value!
        //-i initPoints (default: 1000), Number of points to use for initialization.
        withDbScan.initPointsOption.setValue(5);
        //-o offline (default: 2.0), offline multiplier for epsilion.
        withDbScan.offlineOption.setValue(2.0);
        //-l lambda (default: 0.25)
        withDbScan.lambdaOption.setValue(0.25);
        //-s processingSpeed (default: 100), Number of incoming points per time unit.
        withDbScan.speedOption.setValue(1);
        //-M evaluateMicroClustering, Evaluate the underlying microclustering instead of the
        // macro clustering
        withDbScan.evaluateMicroClusteringOption.setValue(true);

        setClusterer("WithDbScan", withDbScan);
    }

    public void trainOnLocation(Location location) {
        mClusterer.trainOnInstance(mLocationInstance.getInstance());
        Log.d(mClustererName, "size of macroclustering: " + mClusterer.getClusteringResult()
                .getClustering().size());
        Log.d(mClustererName, "size of microclustering: " + mClusterer.getMicroClusteringResult()
                .getClustering().size());
    }

}
