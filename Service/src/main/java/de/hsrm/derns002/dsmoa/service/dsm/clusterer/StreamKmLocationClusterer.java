package de.hsrm.derns002.dsmoa.service.dsm.clusterer;

import android.content.Context;

import moa.clusterers.streamkm.StreamKM;

/**
 * Fails with ArrayIndexOutOfBoundsException when choosing random
 * centers. Might be a problem with our LocationInstances though.
 */
public class StreamKmLocationClusterer extends LocationClustererTemplate {

    public StreamKmLocationClusterer(Context context) {
        super(context);

        StreamKM streamKm = new StreamKM();
        streamKm.resetLearningImpl();
        //-s sizeCoreset (default: 10000), Size of the coreset.
        streamKm.sizeCoresetOption.setValue(10000);
        //-k numClusters (default: 5), Number of clusters to compute.
        streamKm.numClustersOption.setValue(2);
        //-w width (default: 100000), Size of Window for training learner.
        streamKm.widthOption.setValue(100000);
        //-r randomSeed (default: 1), Seed for random behaviour of the classifier.
        streamKm.widthOption.setValue(1);

        setClusterer("StreamKm", streamKm);
    }

}
