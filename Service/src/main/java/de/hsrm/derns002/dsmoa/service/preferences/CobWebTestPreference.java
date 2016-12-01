package de.hsrm.derns002.dsmoa.service.preferences;

import android.content.Context;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;

import moa.clusterers.Clusterer;
import moa.clusterers.CobWeb;

public class CobWebTestPreference extends EditTextPreference {

    private View mView;

    public CobWebTestPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        mView = view;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            new CobWebTestTask(Integer.parseInt(getEditText().getText().toString())).execute();
        }
    }

    private class CobWebTestTask extends ClustererTestTask {

        private CobWeb mCobWeb;

        public CobWebTestTask(int numInstances) {
            super(numInstances);

            mCobWeb = new CobWeb();
            // use parameters from the MOA-GUI
            mCobWeb.resetLearningImpl();
            //-a acuity (default: 1.0), Acuity (minimum standard deviation)
            mCobWeb.setAcuity(1.0);
            //-c cutoff (default: 0.002), Cutoff (minimum category utility)
            mCobWeb.setCutoff(0.002);
            //-r randomSeed (default: 1), Seed for random noise.
            mCobWeb.randomSeedOption.setValue(1);
        }

        @Override
        Preference getPreference() {
            return CobWebTestPreference.this;
        }

        @Override
        View getView() {
            return mView;
        }

        @Override
        Clusterer getClusterer() {
            return mCobWeb;
        }

        @Override
        int getNumClusters() {
            return mCobWeb.numberOfClusters();
        }

    }

}
