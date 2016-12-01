package de.hsrm.derns002.dsmoa.service.preferences;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.EditTextPreference;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import moa.classifiers.Classifier;
import moa.classifiers.trees.HoeffdingTree;
import moa.core.TimingUtils;
import moa.streams.generators.RandomRBFGenerator;
import weka.core.Instance;

public class HoeffdingTreeTestPreference extends EditTextPreference {

    private static final String TAG = "HoeffdingTreeTestPref";
    private View mView;

    public HoeffdingTreeTestPreference(Context context, AttributeSet attrs) {
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
        if (positiveResult)  {
            new DsmTestTask(Integer.parseInt(getEditText().getText().toString())).execute();
        }
    }

    private class DsmTestTask extends AsyncTask<Void, Void, Void> {

        private int mNumInstances;
        private int mNumInstancesProcessed = 0;
        private int mCorrectlyClassified = 0;
        private double mDurationSeconds = 0.0;

        public DsmTestTask(int numInstances) {
            mNumInstances = numInstances;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "starting test run");
            HoeffdingTreeTestPreference.this.setEnabled(false);

            Snackbar.make(mView, "Testing HoeffdingTree with " + mNumInstances + " instances...",
                    Snackbar.LENGTH_INDEFINITE).show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Classifier learner = new HoeffdingTree();
            RandomRBFGenerator stream = new RandomRBFGenerator();
            stream.prepareForUse();

            learner.setModelContext(stream.getHeader());
            learner.prepareForUse();

            TimingUtils.enablePreciseTiming();
            long startTimeNano = TimingUtils.getNanoCPUTimeOfCurrentThread();
            while (stream.hasMoreInstances() && mNumInstancesProcessed < mNumInstances) {
                Instance trainInst = stream.nextInstance();
                if (learner.correctlyClassifies(trainInst)) {
                    mCorrectlyClassified++;
                }
                mNumInstancesProcessed++;
                learner.trainOnInstance(trainInst);
            }
            mDurationSeconds = TimingUtils.nanoTimeToSeconds(TimingUtils
                    .getNanoCPUTimeOfCurrentThread() - startTimeNano);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d(TAG, "finished test run");
            HoeffdingTreeTestPreference.this.setEnabled(true);

            double  correctlyClassifiedPercentage = 100.0 * (double) mCorrectlyClassified /
                    (double) mNumInstancesProcessed;
            double durationSecondsRounded = Math.round(mDurationSeconds * 1000.0) / 1000.0;
            String resultMsg = mNumInstancesProcessed + " instances processed in " +
                    durationSecondsRounded + " seconds with " +
                    correctlyClassifiedPercentage + "% accuracy";
            Log.d(TAG, resultMsg);

            final Snackbar snackbar = Snackbar.make(mView, resultMsg, Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(android.R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            });
            snackbar.show();
        }

    }

}
