package de.hsrm.derns002.dsmoa.service.dsm.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import moa.cluster.CFCluster;
import moa.cluster.Cluster;
import moa.cluster.Clustering;
import moa.clusterers.macro.AbstractMacroClusterer;
import moa.clusterers.macro.NonConvexCluster;
import moa.clusterers.macro.dbscan.DenseMicroCluster;

/**
 * Copycat of the original DbScan with some Logs for better understanding
 * or even some changes in code for testing. For testing purposes only.
 */
public class MyDbScan extends AbstractMacroClusterer {

    private static final String TAG = "MyDbScan";

    Clustering datasource;
    private double mEps;
    private int mMinPts;

    public MyDbScan(Clustering microClusters, double eps, int MinPts) {
        datasource = microClusters;
        mEps = eps;
        mMinPts = MinPts;
    }

    private ArrayList<DenseMicroCluster> expandCluster(DenseMicroCluster dmc,
                                                       List<DenseMicroCluster> neighbours,
                                                       ArrayList<DenseMicroCluster> arrayList,
                                                       Vector<DenseMicroCluster> dbmc) {

        if (!dmc.isClustered()) {
            dmc.setClustered();
            arrayList.add(dmc);
        }
        while (!neighbours.isEmpty()) {
            DenseMicroCluster mc = neighbours.get(0);
            neighbours.remove(0);
            if (!mc.isVisited()) {
                mc.setVisited();
                List<DenseMicroCluster> neighbours2 = getNeighbourhood(mc, dbmc);
                if (neighbours2.size() >= mMinPts) {
                    while (!neighbours2.isEmpty()) {
                        DenseMicroCluster temp = neighbours2.get(0);
                        neighbours2.remove(0);
                        if (!temp.isVisited()) {
                            neighbours.add(temp);
                        }
                    }
                    neighbours.addAll(neighbours2);
                    if (!mc.isClustered()) {
                        mc.setClustered();
                        arrayList.add(mc);
                    }
                }
            }
        }
        return arrayList;
    }

    private List<DenseMicroCluster> getNeighbourhood(DenseMicroCluster mc,
                                                     Vector<DenseMicroCluster> dbmc) {
        List<DenseMicroCluster> res = new Vector<DenseMicroCluster>();
        for (DenseMicroCluster dmc : dbmc) {
            if (distance(dmc.getCFCluster().getCenter(), mc.getCFCluster().getCenter()) < mEps) {
                res.add(dmc);
            }
        }
        return res;
    }

    /**
     * eclidean distance
     *
     * @param center
     * @param center2
     * @return
     */
    private double distance(double[] center, double[] center2) {
        double d = 0D;
        for (int i = 0; i < center.length; i++) {
            d += Math.pow((center[i] - center2[i]), 2);
        }
        return Math.sqrt(d);
    }

    @Override
    public Clustering getClustering(Clustering microClusters) {
        if (microClusters != null && microClusters.size() != 0) {
            //Log.d(TAG, "\n* * * getClustering() started with microClusters.size() = " + microClusters.size() + " * * *");
            Vector<DenseMicroCluster> dbmc = new Vector<DenseMicroCluster>();
            for (Cluster c : microClusters.getClustering()) {
                CFCluster cf = null;
                if (c instanceof CFCluster) {
                    cf = (CFCluster) c;
                    dbmc.add(new DenseMicroCluster(cf));
                } else
                    throw new RuntimeException();
            }
            //Log.d(TAG, "converted microclusters to vector densemicroclusters (dbmc) of size = " + dbmc.size());

            //Log.d(TAG, "grouping densemicroclusters");
            ArrayList<ArrayList<DenseMicroCluster>> clusters = new ArrayList<ArrayList<DenseMicroCluster>>();
            for (DenseMicroCluster dmc : dbmc) {
                if (!dmc.isVisited()) {
                    dmc.setVisited();
                    List<DenseMicroCluster> neighbours = getNeighbourhood(dmc,
                            dbmc);
                    //Log.d(TAG, "neighbours of microcluster = " + neighbours.size());
                    if (neighbours.size() >= mMinPts) {
                        //Log.d(TAG, "making new macrocluster");
                        ArrayList<DenseMicroCluster> cluster = expandCluster( // TODO: ??!?!?
                                dmc, neighbours,
                                new ArrayList<DenseMicroCluster>(), dbmc);
                        clusters.add(cluster);
                    } else {
                        //Log.d(TAG, "making NO new macrocluster");
                    }
                }
            }
            // ** create big microclusters,
            // CFCluster[] res = new CFCluster[clusters.size()];
            // int clusterPos = 0;
            // for (ArrayList<DenseMicroCluster> cluster : clusters) {
            // if (cluster.size() != 0) {
            // CFCluster temp = (CFCluster) (cluster.get(0).mCluster.copy());
            // res[clusterPos] = temp;
            // for (int i = 1; i < cluster.size(); i++) {
            // res[clusterPos].add(cluster.get(i).mCluster);
            // }
            // clusterPos++;
            // }
            // }
            // Clustering result = new Clustering(res);

            // **
            // TODO: ?!?!?!?
            CFCluster[] res = new CFCluster[clusters.size()];
            int clusterPos = 0;
            for (ArrayList<DenseMicroCluster> cluster : clusters) {
                if (cluster.size() != 0) {
                    CFCluster temp = new NonConvexCluster(
                            cluster.get(0).getCFCluster(),
                            Convert2microclusterList(cluster));
                    res[clusterPos] = temp;
                    for (int i = 1; i < cluster.size(); i++) {
                        res[clusterPos].add(cluster.get(i).getCFCluster());
                    }
                    clusterPos++;
                }
            }

            // //// count Noise
            int noise = 0;
            for (DenseMicroCluster c : dbmc) {
                if (!c.isClustered()) {
                    noise++;
                }
            }
            //Log.w(TAG, "microclusters which are not clustered: " + noise);
            Clustering result = new Clustering(res);
            //setClusterIDs(result);
            setMyClusterIDs(result);
            // int i = 0;
            // for (Cluster c : result.getClustering()) {
            // c.setId(i++);
            // }
            //Log.d(TAG, "* * * returning clustering of size = " + result.size() + " * * *\n");
            return result;
        }
        //Log.w(TAG, "getClustering() started with microClusters zero or null");
        return new Clustering();
    }

    private List<CFCluster> Convert2microclusterList(
            ArrayList<DenseMicroCluster> cluster) {
        List<CFCluster> cfCluster = new Vector<CFCluster>();
        for (DenseMicroCluster d : cluster) {
            cfCluster.add(d.getCFCluster());
        }
        return cfCluster;
    }

    protected void setMyClusterIDs(Clustering clustering) {
        // int numOfClusters = clustering.size();
        // Set<Double> oldClusterIDs = new TreeSet<Double>();
        //
        // // Collect all the old IDs of the microclusters
        // for (Cluster c : clustering.getClustering()) {
        // NonConvexCluster ncc = (NonConvexCluster) c;
        // for (Cluster mc : ncc.mMicroClusters) {
        // if (!oldClusterIDs.contains(mc.getId()))
        // oldClusterIDs.add(mc.getId());
        // }
        // }

        HashMap<Double, Integer> countIDs = new HashMap<Double, Integer>();
        for (Cluster c : clustering.getClustering()) {
            HashMap<Double, Integer> ids = new HashMap<Double, Integer>();
            NonConvexCluster ncc = (NonConvexCluster) c;
            for (Cluster mc : ncc.getMicroClusters()) {
                if (!ids.containsKey(mc.getId()))
                    ids.put(mc.getId(), new Integer(1));
                else {
                    int i = ids.get(mc.getId());
                    i++;
                    ids.put(mc.getId(), i);
                }
            }
            // find max
            double maxID = -1d;
            int max = -1;
            for (Map.Entry<Double, Integer> entry : ids.entrySet()) {
                if (entry.getValue() >= max) {
                    max = entry.getValue();
                    maxID = entry.getKey();
                }
            }
            c.setId(maxID);

            if (!countIDs.containsKey(maxID))
                countIDs.put(maxID, new Integer(1));
            else {
                int i = countIDs.get(maxID);
                i++;
                countIDs.put(maxID, i);
            }

        }

        // check if there are 2 clusters with the same color (same id, could
        // appear after a split);
        double freeID = 0;
        List<Double> reservedIDs = new Vector<Double>();
        reservedIDs.addAll(countIDs.keySet());
        for (Map.Entry<Double, Integer> entry : countIDs.entrySet()) {
            if (entry.getValue() > 1 || entry.getKey() == -1) {
                // find first free id, search all the clusters which has the
                // same id and replace the ids with free ids. One cluster can
                // keep its id
                int to = entry.getValue();
                if (entry.getKey() != -1)
                    to--;

                for (int i = 0; i < to; i++) {
                    while (reservedIDs.contains(freeID)
                            /*&& freeID < ColorArray.getNumColors()*/)
                        freeID += 1.0;
                    for (int c = clustering.size() - 1; c >= 0; c--)
                        if (clustering.get(c).getId() == entry.getKey()) {
                            clustering.get(c).setId(freeID);
                            reservedIDs.add(freeID);
                            break;
                        }
                }
            }
        }

        for (Cluster c : clustering.getClustering()) {
            NonConvexCluster ncc = (NonConvexCluster) c;
            for (Cluster mc : ncc.getMicroClusters()) {
                mc.setId(c.getId());
            }
        }
    }

}

