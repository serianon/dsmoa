package de.hsrm.derns002.dsmoa.service.dsm.clusterer;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import java.io.File;

import de.hsrm.derns002.dsmoa.service.dsm.LocationInstance;
import moa.cluster.Cluster;
import moa.cluster.Clustering;
import moa.clusterers.Clusterer;
import moa.core.SerializeUtils;

public abstract class LocationClustererTemplate implements LocationClusterer {

    protected static final String MODEL_FILE_SUFFIX = ".model";

    protected Context mContext;
    protected LocationInstance mLocationInstance;
    protected String mClustererName;
    protected Clusterer mClusterer;

    public LocationClustererTemplate(Context context) {
        mContext = context;
        mLocationInstance = new LocationInstance();
    }

    protected void setClusterer(String clustererName, Clusterer clusterer) {
        mClustererName = clustererName;
        mClusterer = clusterer;
    }

    public void updateLocationInstance(Location location) {
        mLocationInstance.setInstance(location);
    }

    public void trainOnLocation(Location location) {
        mClusterer.trainOnInstance(mLocationInstance.getInstance());
        try {
            Log.d(mClustererName, mClusterer.toString());
        } catch (Exception e) {
            Log.w(mClustererName, "clusterer-results can't be printed: " + e.getLocalizedMessage());
        }
    }

    public Cluster getClusterForLocation(Location location) {
        Cluster result;
        int bestIndex = -1;
        double maxInclusion = 0.0;
        double currentInclusion = 0.0;
        mLocationInstance.setInstance(location);
        for (int i = 0; i < mClusterer.getMicroClusteringResult().size(); i++) {
            currentInclusion = mClusterer.getMicroClusteringResult().get(i)
                    .getInclusionProbability(mLocationInstance.getInstance());
            if (currentInclusion >= maxInclusion) {
                maxInclusion = currentInclusion;
                bestIndex = i;
            }
        }
        if (bestIndex == -1) return null;
        result = mClusterer.getMicroClusteringResult().get(bestIndex);
        return result;
    }

    public Clustering getMacroClusters() {
        return mClusterer.getClusteringResult();
    }

    public Clustering getMicroClusters() {
        return mClusterer.getMicroClusteringResult();
    }

    public void resetClusterer() {
        mClusterer.resetLearning();
        deleteModelFromStorage();
    }

    public boolean isModelInStorage() {
        return new File(mContext.getFilesDir(), mClustererName + MODEL_FILE_SUFFIX).exists();
    }

    public void loadModelFromStorage() {
        try {
            File inputFile = new File(mContext.getFilesDir(), mClustererName + MODEL_FILE_SUFFIX);
            Object obj = SerializeUtils.readFromFile(inputFile);
            mClusterer = (Clusterer) obj;
        } catch (Exception e) {
            Log.e(mClustererName, "loading clusterer from storage failed: " + e.getLocalizedMessage());
        }
    }

    public void saveModelToStorage() {
        try {
            File outputFile = new File(mContext.getFilesDir(), mClustererName + MODEL_FILE_SUFFIX);
            SerializeUtils.writeToFile(outputFile, mClusterer);
            Log.d(mClustererName, "clusterer written to: " + outputFile.getAbsolutePath());
        } catch (Exception e) {
            // fail in silence
            Log.e(mClustererName, "writing clusterer to storage failed: " + e.getLocalizedMessage());
        }
    }

    public void deleteModelFromStorage() {
        try {
            File outputFile = new File(mContext.getFilesDir(), mClustererName + MODEL_FILE_SUFFIX);
            boolean deleted = outputFile.delete();
            if (deleted) {
                Log.d(mClustererName, "clusterer " + outputFile.getAbsolutePath() +
                        " deleted from storage");
            } else {
                Log.w(mClustererName, "clusterer " + outputFile.getAbsolutePath() +
                        " couldn't be deleted from storage");
            }
        } catch (Exception e) {
            // fail in silence
            Log.w(mClustererName, "clusterer couldn't be deleted from storage: " +
                    e.getLocalizedMessage());
        }
    }

}
