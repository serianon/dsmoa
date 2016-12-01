package de.hsrm.derns002.dsmoa.service.dsm.clusterer;

import android.content.Context;

import moa.clusterers.CobWeb;

/**
 * Produces one cluster only, and adjusts that cluster to fit them all.
 * No matter the parameters here. Also, CobWeb "hasn't been tested very
 * well in MOA" as stated by MOA-author A. Bifet himself.
 */
public class CobWebLocationClusterer extends LocationClustererTemplate {

    public CobWebLocationClusterer(Context context) {
        super(context);

        CobWeb cobWeb = new CobWeb();
        cobWeb.resetLearningImpl();
        //-a acuity (default: 1.0), Acuity (minimum standard deviation)
        cobWeb.setAcuity(1.0);
        //-c cutoff (default: 0.002), Cutoff (minimum category utility)
        cobWeb.setCutoff(0.002);
        //-r randomSeed (default: 1), Seed for random noise.
        cobWeb.randomSeedOption.setValue(1);

        setClusterer("CobWeb", cobWeb);
    }

}
