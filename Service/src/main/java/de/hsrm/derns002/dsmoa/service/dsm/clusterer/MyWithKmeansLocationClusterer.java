package de.hsrm.derns002.dsmoa.service.dsm.clusterer;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import de.hsrm.derns002.dsmoa.service.dsm.algorithms.MyWithKmeans;

/**
 * Copycat of the original CluStreamWithKmeans with the purpose to
 * add Logs in interesting parts of the code to learn more about
 * what is happening (or even manipulate the code for testing purposes).
 */
public class MyWithKmeansLocationClusterer extends LocationClustererTemplate {

    public MyWithKmeansLocationClusterer(Context context) {
        super(context);

        MyWithKmeans cluStreamWithKmeans = new MyWithKmeans();
        // CluStream will not be initialized after calling default constructor, but only when
        // resetLearningImpl() has been called!
        cluStreamWithKmeans.resetLearningImpl();
        //-h horizon (default: 1000), Rang of the window.
        // NON EXISTENT! Maybe they mean the timeWindowOption:
        cluStreamWithKmeans.timeWindowOption.setValue(1000);
        //-k maxNumKernels (default: 100), Maximum number of micro kernels to use.
        cluStreamWithKmeans.maxNumKernelsOption.setValue(100);
        //-t kernelRadiFactor (default: 2), Multiplier for the kernel radius
        cluStreamWithKmeans.kernelRadiFactorOption.setValue(2);
        //-k of macro k-means (number of clusters)
        cluStreamWithKmeans.kOption.setValue(2);
        //-M evaluateMicroClustering, Evaluate the underlying microclustering instead of the
        // macro clustering
        cluStreamWithKmeans.evaluateMicroClusteringOption.setValue(false);

        setClusterer("MyWithKmeans", cluStreamWithKmeans);
    }

    @Override
    public void trainOnLocation(Location location) {
        mClusterer.trainOnInstance(mLocationInstance.getInstance());
        // CluStreamWithKmeans doesn't support toString() or getModelDescription()
        int microCluster = mClusterer.getMicroClusteringResult() == null ? -1 : mClusterer
                .getMicroClusteringResult().size();
        int macroCluster = mClusterer.getClusteringResult() == null ? -1 : mClusterer
                .getClusteringResult().size();
        Log.d(mClustererName, "microclusters: " + microCluster + ", clusters: " + macroCluster);
    }

}
