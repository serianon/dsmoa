package de.hsrm.derns002.dsmoa.service.dsm.clusterer;

import android.location.Location;

import moa.cluster.Cluster;
import moa.cluster.Clustering;

public interface LocationClusterer {

    void updateLocationInstance(Location location);

    void trainOnLocation(Location location);

    Cluster getClusterForLocation(Location location);

    Clustering getMacroClusters();

    Clustering getMicroClusters();

    void resetClusterer();

    boolean isModelInStorage();

    void loadModelFromStorage();

    void saveModelToStorage();

    void deleteModelFromStorage();

}
