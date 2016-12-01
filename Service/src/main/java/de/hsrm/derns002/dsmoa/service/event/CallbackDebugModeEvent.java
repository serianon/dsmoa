package de.hsrm.derns002.dsmoa.service.event;

import android.content.SharedPreferences;

public class CallbackDebugModeEvent {

    private static final String PREFKEY_LABEL = "label";
    private static final String PREFKEY_CLUSTER_NUMBER = "clusternumber";
    private static final String PREFKEY_DELAY = "delay";
    private static final String PREFKEY_METHOD = "method";

    private String label;
    private int clusterNumber;
    private int delay;
    private int methodIndex;

    public CallbackDebugModeEvent(SharedPreferences sharedPreferences) {
        label = sharedPreferences.getString(PREFKEY_LABEL, "null");
        clusterNumber = Integer.valueOf(sharedPreferences.getString(PREFKEY_CLUSTER_NUMBER, "-1"));
        delay = Integer.valueOf(sharedPreferences.getString(PREFKEY_DELAY, "0"));
        methodIndex = Integer.valueOf(sharedPreferences.getString(PREFKEY_METHOD, "-1"));
    }

    public String getLabel() {
        return label;
    }

    public int getClusterNumber() {
        return clusterNumber;
    }

    public int getDelay() {
        return delay;
    }

    public int getMethodIndex() {
        return methodIndex;
    }

}
