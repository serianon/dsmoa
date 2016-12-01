package de.hsrm.derns002.dsmoa.lib;

/**
 * A callback-interface which abstracts and simplifies the IPC-AIDL-logic. Use this interface with
 * the {@link ServiceConnector} instead of implementing the AIDL-interfaces manually.
 */
public interface ISimpleServiceListener {

    /**
     * Called when the DsmService has been bound *and* the listener has been registered successfully
     */
    void onConnected();

    /**
     * Called when the connection to the DsmService has been terminated by the service or the user
     */
    void onDisconnected();

    void onLabeledClusterEnter(String clusterName);

    void onLabeledClusterExit(String clusterName);

    void onUnlabeledClusterEnter(int clusterId);

    void onUnlabeledClusterExit(int clusterId);

    void onPredictionChange(String clusterName);

}
