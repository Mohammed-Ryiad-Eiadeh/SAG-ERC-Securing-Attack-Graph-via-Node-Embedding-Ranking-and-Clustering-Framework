package KMeansClustering.org;

import weka.clusterers.SimpleKMeans;
import weka.core.DistanceFunction;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

import java.util.ArrayList;
import java.util.HashMap;

public class NodeClustering {
    private final String Embeddings_File_Path;
    private final int Num_Clusters;
    private final DistanceFunction Distance_Function;
    private final int Max_Iteration;

    public NodeClustering(String embeddings_file_path, int num_clusters, DistanceFunction distance_function, int max_iteration) {
        Embeddings_File_Path = embeddings_file_path;
        Num_Clusters = num_clusters;
        Distance_Function = distance_function;
        Max_Iteration = max_iteration;
    }

    public HashMap<Integer, ArrayList<Integer>> getMapClustersToNodes() {
        HashMap<Integer, ArrayList<Integer>> mapInstantToCluster = new HashMap<>();
        try {
            DataSource dataSource = new DataSource(Embeddings_File_Path);
            Instances instanceData = dataSource.getDataSet();

            Remove remove = new Remove();
            remove.setAttributeIndices("1");
            remove.setInputFormat(instanceData);
            Instances pre_processedData = Filter.useFilter(instanceData, remove);

            SimpleKMeans simpleKMeans = new SimpleKMeans();
            simpleKMeans.setNumClusters(Num_Clusters);
            simpleKMeans.setDistanceFunction(Distance_Function);
            simpleKMeans.setMaxIterations(Max_Iteration);
            simpleKMeans.buildClusterer(pre_processedData);

            for (int instantID = 0; instantID < pre_processedData.size(); instantID++) {
                Instance dataInstance = pre_processedData.instance(instantID);
                int clusterID = simpleKMeans.clusterInstance(dataInstance);
                mapInstantToCluster.computeIfAbsent(clusterID, K -> new ArrayList<>()).add(instantID + 1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return mapInstantToCluster;
    }
}
