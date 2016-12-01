package de.hsrm.derns002.dsmoa.lib;

import de.hsrm.derns002.dsmoa.lib.IServiceListener;

interface IService {

    // Listener

    void registerListener(IServiceListener listener);

    boolean unregisterListener(IServiceListener listener);

    // Labels

    boolean setLabelForCurrentCluster(String labelName);

    boolean removeLabelForCurrentCluster();

    boolean removeAllLabels();

}


