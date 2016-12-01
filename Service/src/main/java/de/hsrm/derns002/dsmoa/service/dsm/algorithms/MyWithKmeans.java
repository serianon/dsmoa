package de.hsrm.derns002.dsmoa.service.dsm.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import moa.cluster.CFCluster;
import moa.cluster.Cluster;
import moa.cluster.Clustering;
import moa.cluster.SphereCluster;
import moa.clusterers.AbstractClusterer;
import moa.clusterers.clustream.ClustreamKernel;
import moa.core.Measurement;
import moa.options.IntOption;
import weka.core.DenseInstance;
import weka.core.Instance;

/**
 * Copycat of the original WithKmeans with some Logs for better understanding
 * or even some changes in code for testing. For testing purposes only.
 */
public class MyWithKmeans extends AbstractClusterer {

    private static final long serialVersionUID = 1L;

    public IntOption timeWindowOption = new IntOption("horizon",
            'h', "Rang of the window.", 1000);

    public IntOption maxNumKernelsOption = new IntOption(
            "maxNumKernels", 'm',
            "Maximum number of micro kernels to use.", 100);

    public IntOption kernelRadiFactorOption = new IntOption(
            "kernelRadiFactor", 't',
            "Multiplier for the kernel radius", 2);

    public IntOption kOption = new IntOption(
            "k", 'k',
            "k of macro k-means (number of clusters)", 5);

    private int timeWindow;
    private long timestamp = -1;
    private ClustreamKernel[] kernels;
    private boolean initialized;
    private List<ClustreamKernel> buffer; // Buffer for initialization with kNN
    private int bufferSize;
    private double t;
    private int m;

    public MyWithKmeans() {

    }

    @Override
    public void resetLearningImpl() {
        this.kernels = new ClustreamKernel[maxNumKernelsOption.getValue()];
        this.timeWindow = timeWindowOption.getValue();
        this.initialized = false;
        this.buffer = new LinkedList<ClustreamKernel>();
        this.bufferSize = maxNumKernelsOption.getValue();
        t = kernelRadiFactorOption.getValue();
        m = maxNumKernelsOption.getValue();
    }

    @Override
    public void trainOnInstanceImpl(Instance instance) {
        int dim = instance.numValues();
        timestamp++;
        // 0. Initialize
        if (!initialized) {
            if (buffer.size() < bufferSize) {
                buffer.add(new ClustreamKernel(instance, dim, timestamp, t, m));
                return;
            } else {
                for (int i = 0; i < buffer.size(); i++) {
                    kernels[i] = new ClustreamKernel(new DenseInstance(1.0, buffer.get(i).getCenter()), dim, timestamp, t, m);
                }

                buffer.clear();
                initialized = true;
                return;
            }
        }


        // 1. Determine closest kernel
        ClustreamKernel closestKernel = null;
        double minDistance = Double.MAX_VALUE;
        for (int i = 0; i < kernels.length; i++) {
            //System.out.println(i+" "+kernels[i].getWeight()+" "+kernels[i].getDeviation());
            double distance = distance(instance.toDoubleArray(), kernels[i].getCenter());
            if (distance < minDistance) {
                closestKernel = kernels[i];
                minDistance = distance;
            }
        }

        // 2. Check whether instance fits into closestKernel
        double radius = 0.0;
        if (closestKernel.getWeight() == 1) {
            // Special case: estimate radius by determining the distance to the
            // next closest cluster
            radius = Double.MAX_VALUE;
            double[] center = closestKernel.getCenter();
            for (int i = 0; i < kernels.length; i++) {
                if (kernels[i] == closestKernel) {
                    continue;
                }

                double distance = distance(kernels[i].getCenter(), center);
                radius = Math.min(distance, radius);
            }
        } else {
            radius = closestKernel.getRadius();
        }

        if (minDistance < radius) {
            // Date fits, put into kernel and be happy
            closestKernel.insert(instance, timestamp);
            return;
        }

        // 3. Date does not fit, we need to free
        // some space to insert a new kernel
        long threshold = timestamp - timeWindow; // Kernels before this can be forgotten

        // 3.1 Try to forget old kernels
        for (int i = 0; i < kernels.length; i++) {
            if (kernels[i].getRelevanceStamp() < threshold) {
                kernels[i] = new ClustreamKernel(instance, dim, timestamp, t, m);
                return;
            }
        }

        // 3.2 Merge closest two kernels
        int closestA = 0;
        int closestB = 0;
        minDistance = Double.MAX_VALUE;
        for (int i = 0; i < kernels.length; i++) {
            double[] centerA = kernels[i].getCenter();
            for (int j = i + 1; j < kernels.length; j++) {
                double dist = distance(centerA, kernels[j].getCenter());
                if (dist < minDistance) {
                    minDistance = dist;
                    closestA = i;
                    closestB = j;
                }
            }
        }
        assert (closestA != closestB);

        kernels[closestA].add(kernels[closestB]);
        kernels[closestB] = new ClustreamKernel(instance, dim, timestamp, t, m);
    }

    @Override
    public Clustering getMicroClusteringResult() {
        if (!initialized) {
            return new Clustering(new Cluster[0]);
        }

        ClustreamKernel[] result = new ClustreamKernel[kernels.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = new ClustreamKernel(kernels[i], t, m);
        }

        return new Clustering(result);
    }

    @Override
    public Clustering getClusteringResult() {
        if (!initialized) {
            return new Clustering(new Cluster[0]);
        }
        return kMeans_rand(kOption.getValue(), getMicroClusteringResult());
    }

    public Clustering getClusteringResult(Clustering gtClustering) {
        return kMeans_gta(kOption.getValue(), getMicroClusteringResult(), gtClustering);
    }

    public String getName() {
        return "CluStreamWithKMeans " + timeWindow;
    }

    /**
     * Distance between two vectors.
     *
     * @param pointA
     * @param pointB
     * @return dist
     */
    private static double distance(double[] pointA, double[] pointB) {
        double distance = 0.0;
        for (int i = 0; i < pointA.length; i++) {
            double d = pointA[i] - pointB[i];
            distance += d * d;
        }
        return Math.sqrt(distance);
    }

    public static Clustering kMeans_gta(int k, Clustering clustering, Clustering gtClustering) {

        ArrayList<CFCluster> microclusters = new ArrayList<CFCluster>();
        for (int i = 0; i < clustering.size(); i++) {
            if (clustering.get(i) instanceof CFCluster) {
                microclusters.add((CFCluster) clustering.get(i));
            } else {
                System.out.println("Unsupported Cluster Type:" + clustering.get(i).getClass() + ". Cluster needs to extend moa.cluster.CFCluster");
            }
        }

        int n = microclusters.size();
        assert (k <= n);

		/* k-means */
        Random random = new Random(0);
        Cluster[] centers = new Cluster[k];
        int K = gtClustering.size();

        for (int i = 0; i < k; i++) {
            if (i < K) {    // GT-aided
                centers[i] = new SphereCluster(gtClustering.get(i).getCenter(), 0);
            } else {        // Randomized
                int rid = random.nextInt(n);
                centers[i] = new SphereCluster(microclusters.get(rid).getCenter(), 0);
            }
        }

        return cleanUpKMeans(kMeans(k, centers, microclusters), microclusters);
    }

    public static Clustering kMeans_rand(int k, Clustering clustering) {

        ArrayList<CFCluster> microclusters = new ArrayList<CFCluster>();
        for (int i = 0; i < clustering.size(); i++) {
            if (clustering.get(i) instanceof CFCluster) {
                microclusters.add((CFCluster) clustering.get(i));
            } else {
                System.out.println("Unsupported Cluster Type:" + clustering.get(i).getClass() + ". Cluster needs to extend moa.cluster.CFCluster");
            }
        }

        int n = microclusters.size();
        assert (k <= n);

		/* k-means */
        Random random = new Random(0);
        Cluster[] centers = new Cluster[k];

        for (int i = 0; i < k; i++) {
            int rid = random.nextInt(n);
            centers[i] = new SphereCluster(microclusters.get(rid).getCenter(), 0);
        }

        return cleanUpKMeans(kMeans(k, centers, microclusters), microclusters);
    }

    /**
     * (The Actual Algorithm) k-means of (micro)clusters, with specified initialization points.
     *
     * @param k
     * @param centers - initial centers
     * @param data
     * @return (macro)clustering - SphereClusters
     */
    protected static Clustering kMeans(int k, Cluster[] centers, List<? extends Cluster> data) {
        assert (centers.length == k);
        assert (k > 0);

        int dimensions = centers[0].getCenter().length;

        ArrayList<ArrayList<Cluster>> clustering = new ArrayList<ArrayList<Cluster>>();
        for (int i = 0; i < k; i++) {
            clustering.add(new ArrayList<Cluster>());
        }

        while (true) {
            // Assign points to clusters
            for (Cluster point : data) {
                double minDistance = distance(point.getCenter(), centers[0].getCenter());
                int closestCluster = 0;
                for (int i = 1; i < k; i++) {
                    double distance = distance(point.getCenter(), centers[i].getCenter());
                    if (distance < minDistance) {
                        closestCluster = i;
                        minDistance = distance;
                    }
                }

                clustering.get(closestCluster).add(point);
            }

            // Calculate new centers and clear clustering lists
            SphereCluster[] newCenters = new SphereCluster[centers.length];
            for (int i = 0; i < k; i++) {
                newCenters[i] = calculateCenter(clustering.get(i), dimensions);
                clustering.get(i).clear();
            }

            // Convergence check
            boolean converged = true;
            for (int i = 0; i < k; i++) {
                if (!Arrays.equals(centers[i].getCenter(), newCenters[i].getCenter())) {
                    converged = false;
                    break;
                }
            }

            if (converged) {
                break;
            } else {
                centers = newCenters;
            }
        }

        return new Clustering(centers);
    }

    /**
     * Rearrange the k-means result into a set of CFClusters, cleaning up the redundancies.
     *
     * @param kMeansResult
     * @param microclusters
     * @return
     */
    protected static Clustering cleanUpKMeans(Clustering kMeansResult, ArrayList<CFCluster> microclusters) {
		/* Convert k-means result to CFClusters */
        int k = kMeansResult.size();
        CFCluster[] converted = new CFCluster[k];

        for (CFCluster mc : microclusters) {
            // Find closest kMeans cluster
            double minDistance = Double.MAX_VALUE;
            int closestCluster = 0;
            for (int i = 0; i < k; i++) {
                double distance = distance(kMeansResult.get(i).getCenter(), mc.getCenter());
                if (distance < minDistance) {
                    closestCluster = i;
                    minDistance = distance;
                }
            }

            // Add to cluster
            if (converted[closestCluster] == null) {
                converted[closestCluster] = (CFCluster) mc.copy();
            } else {
                converted[closestCluster].add(mc);
            }
        }

        // Clean up
        int count = 0;
        for (int i = 0; i < converted.length; i++) {
            if (converted[i] != null)
                count++;
        }

        CFCluster[] cleaned = new CFCluster[count];
        count = 0;
        for (int i = 0; i < converted.length; i++) {
            if (converted[i] != null)
                cleaned[count++] = converted[i];
        }

        return new Clustering(cleaned);
    }


    /**
     * k-means helper: Calculate a wrapping cluster of assigned points[microclusters].
     *
     * @param assigned
     * @param dimensions
     * @return SphereCluster (with center and radius)
     */
    private static SphereCluster calculateCenter(ArrayList<Cluster> assigned, int dimensions) {
        double[] result = new double[dimensions];
        for (int i = 0; i < result.length; i++) {
            result[i] = 0.0;
        }

        if (assigned.size() == 0) {
            return new SphereCluster(result, 0.0);
        }

        for (Cluster point : assigned) {
            double[] center = point.getCenter();
            for (int i = 0; i < result.length; i++) {
                result[i] += center[i];
            }
        }

        // Normalize
        for (int i = 0; i < result.length; i++) {
            result[i] /= assigned.size();
        }

        // Calculate radius: biggest wrapping distance from center
        double radius = 0.0;
        for (Cluster point : assigned) {
            double dist = distance(result, point.getCenter());
            if (dist > radius) {
                radius = dist;
            }
        }
        SphereCluster sc = new SphereCluster(result, radius);
        sc.setWeight(assigned.size());
        return sc;
    }


    /** Miscellaneous **/

    @Override
    public boolean implementsMicroClusterer() {
        return true;
    }

    public boolean isRandomizable() {
        return false;
    }

    public double[] getVotesForInstance(Instance inst) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected Measurement[] getModelMeasurementsImpl() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void getModelDescription(StringBuilder out, int indent) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}