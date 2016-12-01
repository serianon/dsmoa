package de.hsrm.derns002.dsmoa.service.preferences;

import android.os.AsyncTask;
import android.preference.Preference;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import moa.clusterers.Clusterer;
import moa.core.TimingUtils;
import moa.streams.clustering.RandomRBFGeneratorEvents;
import weka.core.Instance;

public abstract class ClustererTestTask extends AsyncTask<Void, Void, Void> {

    protected static final String TAG = "ClustererTestTask";

    protected int mNumInstances;
    protected int mNumInstancesProcessed = 0;
    protected double mDurationSeconds = 0.0;
    protected int mNumClusters = 0;
    protected RandomRBFGeneratorEvents mRandomRBFEvents;

    public ClustererTestTask(int numInstances) {
        mNumInstances = numInstances;
    }

    abstract Preference getPreference();

    abstract View getView();

    abstract Clusterer getClusterer();

    abstract int getNumClusters();

    protected void setupGenerator() {
        mRandomRBFEvents = new RandomRBFGeneratorEvents();
        // use parameters from the MOA-GUI
        mRandomRBFEvents.modelRandomSeedOption.setValue(1);
        mRandomRBFEvents.instanceRandomSeedOption.setValue(5);
        mRandomRBFEvents.numClusterOption.setValue(5);
        mRandomRBFEvents.numClusterRangeOption.setValue(3);
        mRandomRBFEvents.kernelRadiiOption.setValue(0.07);
        mRandomRBFEvents.kernelRadiiRangeOption.setValue(0.0);
        mRandomRBFEvents.densityRangeOption.setValue(0.0);
        mRandomRBFEvents.speedOption.setValue(500);
        mRandomRBFEvents.speedRangeOption.setValue(0);
        mRandomRBFEvents.noiseLevelOption.setValue(0.1);
        mRandomRBFEvents.noiseInClusterOption.setValue(true);
        mRandomRBFEvents.eventFrequencyOption.setValue(30000);
        mRandomRBFEvents.eventMergeSplitOption.setValue(false);
        mRandomRBFEvents.eventDeleteCreateOption.setValue(false);
        mRandomRBFEvents.decayHorizonOption.setValue(1000);
        mRandomRBFEvents.decayThresholdOption.setValue(0.01);
        mRandomRBFEvents.evaluationFrequencyOption.setValue(1000);
        mRandomRBFEvents.numAttsOption.setValue(2);
        mRandomRBFEvents.prepareForUse();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "starting test run");
        getPreference().setEnabled(false);

        Snackbar.make(getView(), "Testing with " + mNumInstances + " instances...",
                Snackbar.LENGTH_INDEFINITE).show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        setupGenerator();

        TimingUtils.enablePreciseTiming();
        long startTimeNano = TimingUtils.getNanoCPUTimeOfCurrentThread();
        while (mRandomRBFEvents.hasMoreInstances() && mNumInstancesProcessed < mNumInstances) {
            Instance trainInst = mRandomRBFEvents.nextInstance();
            mNumInstancesProcessed++;
            getClusterer().trainOnInstance(trainInst);
        }
        mDurationSeconds = TimingUtils.nanoTimeToSeconds(TimingUtils.
                getNanoCPUTimeOfCurrentThread() - startTimeNano);
        mNumClusters = getNumClusters();

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.d(TAG, "finished test run");
        getPreference().setEnabled(true);

        double durationSecondsRounded = Math.round(mDurationSeconds * 1000.0) / 1000.0;
        String resultMsg = mNumInstancesProcessed + " instances processed in " + durationSecondsRounded
                + " seconds with " + mNumClusters + " clusters";
        Log.d(TAG, resultMsg);

        final Snackbar snackbar = Snackbar.make(getView(), resultMsg, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(android.R.string.ok, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

}