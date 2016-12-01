package de.hsrm.derns002.dsmoa.service.dsm.clusterer;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import moa.clusterers.clustream.Clustream;

/**
 * Do not use!
 * MOAs implementation of "CluStream" seems to be incomplete, and MOAs "CluStreamWithKmeans"
 * seems to be the actual implementation you'd expect.
 */
public class CluStreamLocationClusterer extends LocationClustererTemplate {

    public CluStreamLocationClusterer(Context context) {
        super(context);

        Clustream cluStream = new Clustream();
        // CluStream will not be initialized after calling default constructor, but only when
        // resetLearningImpl() has been called!
        cluStream.resetLearningImpl();
        //-h horizon (default: 1000), Rang of the window.
        // NON EXISTENT! Maybe they mean the timeWindowOption:
        cluStream.timeWindowOption.setValue(1000);
        //-k maxNumKernels (default: 100), Maximum number of micro kernels to use.
        cluStream.maxNumKernelsOption.setValue(100);
        //-t kernelRadiFactor (default: 2), Multiplier for the kernel radius
        cluStream.kernelRadiFactorOption.setValue(2);
        //-M evaluateMicroClustering, Evaluate the underlying microclustering instead of the macro clustering
        cluStream.evaluateMicroClusteringOption.setValue(false);

        setClusterer("CluStream", cluStream);
    }

    @Override
    public void trainOnLocation(Location location) {
        mClusterer.trainOnInstance(mLocationInstance.getInstance());
        // CluStream doesn't support toString()
        Log.d(mClustererName, "microclusters: " + mClusterer.getMicroClusteringResult().size()
                + ", clusters: " + mClusterer.getClusteringResult().size());
    }

}
