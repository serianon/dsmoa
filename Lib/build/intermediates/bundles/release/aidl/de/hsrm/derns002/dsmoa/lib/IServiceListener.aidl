package de.hsrm.derns002.dsmoa.lib;

oneway interface IServiceListener {

    // Clustering

    void _onLabeledClusterEnter(String clusterName);

    void _onLabeledClusterExit(String clusterName);

    void _onUnlabeledClusterEnter(int clusterId);

    void _onUnlabeledClusterExit(int clusterId);

    // Predictions

    void _onPredictionChange(String clusterName);

}


