package de.hsrm.derns002.dsmoa.service.preferences;

import android.content.Context;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;

import de.hsrm.derns002.dsmoa.service.dsm.algorithms.MyWithDbScan;
import moa.clusterers.Clusterer;

public class MyWithDbScanTestPreference extends EditTextPreference {

    private View mView;

    public MyWithDbScanTestPreference(Context context, AttributeSet attrs) {
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
            new MyWithDbScanTestTask(Integer.parseInt(getEditText().getText().toString())).execute();
        }
    }

    private class MyWithDbScanTestTask extends ClustererTestTask {

        private MyWithDbScan mMyWithDbScan;

        public MyWithDbScanTestTask(int numInstances) {
            super(numInstances);

            mMyWithDbScan = new MyWithDbScan();
            // use parameters from the MOA-GUI
            mMyWithDbScan.resetLearningImpl();
            //-h horizon (default: 1000), Range of the window.
            mMyWithDbScan.horizonOption.setValue(1000);
            //-e epsilon (default: 0.02), Defines the epsilon neighbourhood
            mMyWithDbScan.epsilonOption.setValue(0.1);
            //-b beta (default: 0.2)
            mMyWithDbScan.betaOption.setValue(0.2);
            //-m mu (default: 1.0)
            mMyWithDbScan.muOption.setValue(1); // is also minNumberOfPoints-Value!
            //-i initPoints (default: 1000), Number of points to use for initialization.
            mMyWithDbScan.initPointsOption.setValue(5);
            //-o offline (default: 2.0), offline multiplier for epsilion.
            mMyWithDbScan.offlineOption.setValue(2.0);
            //-l lambda (default: 0.25)
            mMyWithDbScan.lambdaOption.setValue(0.25);
            //-s processingSpeed (default: 100), Number of incoming points per time unit.
            mMyWithDbScan.speedOption.setValue(1);
            //-M evaluateMicroClustering, Evaluate the underlying microclustering instead of the
            // macro clustering
            mMyWithDbScan.evaluateMicroClusteringOption.setValue(true);
        }

        @Override
        Preference getPreference() {
            return MyWithDbScanTestPreference.this;
        }

        @Override
        View getView() {
            return mView;
        }

        @Override
        Clusterer getClusterer() {
            return mMyWithDbScan;
        }

        @Override
        int getNumClusters() {
            return mMyWithDbScan.getClusteringResult().getClustering().size();
        }

    }
}
