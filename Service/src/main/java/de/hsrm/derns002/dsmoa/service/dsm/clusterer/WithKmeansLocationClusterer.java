package de.hsrm.derns002.dsmoa.service.dsm.clusterer;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import moa.clusterers.clustream.WithKmeans;

/**
 * The actual CluStream-implementation with k-means. Produces microcluster,
 * but never macrocluster, just like CluStreamWithKmeans.
 */
public class WithKmeansLocationClusterer extends LocationClustererTemplate {

    public WithKmeansLocationClusterer(Context context) {
        super(context);

        WithKmeans withKmeans = new WithKmeans();
        // CluStream will not be initialized after calling default constructor, but only when
        // resetLearningImpl() has been called!
        withKmeans.resetLearningImpl();
        //-h horizon (default: 1000), Rang of the window.
        // NON EXISTENT! Maybe they mean the timeWindowOption:
        withKmeans.timeWindowOption.setValue(1000);
        //-k maxNumKernels (default: 100), Maximum number of micro kernels to use.
        withKmeans.maxNumKernelsOption.setValue(100);
        //-t kernelRadiFactor (default: 2), Multiplier for the kernel radius
        withKmeans.kernelRadiFactorOption.setValue(2);
        //-k of macro k-means (number of clusters)
        withKmeans.kOption.setValue(2);
        //-M evaluateMicroClustering, Evaluate the underlying microclustering instead of the
        // macro clustering
        withKmeans.evaluateMicroClusteringOption.setValue(false);

        setClusterer("WithKmeans", withKmeans);
    }

    @Override
    public void trainOnLocation(Location location) {
        mClusterer.trainOnInstance(mLocationInstance.getInstance());
        // CluStreamWithKmeans doesn't support toString() or getModelDescription()
        Log.d(mClustererName, "microclusters: " + mClusterer.getMicroClusteringResult().size()
                + ", clusters: " + mClusterer.getClusteringResult().size());
    }

}
