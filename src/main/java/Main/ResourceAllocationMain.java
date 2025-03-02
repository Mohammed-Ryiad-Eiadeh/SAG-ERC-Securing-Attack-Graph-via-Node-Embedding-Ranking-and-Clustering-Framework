package Main;

import Attack_Defence_Graph.org.GraphData;
import ConcurrentAttacks.org.ConcurrentAttack;
import KMeansClustering.org.NodeClustering;
import ResourceAllocationsApproaches.org.AllocationApproaches;
import org.tribuo.util.Util;
import weka.core.ManhattanDistance;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResourceAllocationMain implements StoreAllocationResults.org.SaveRA_Results {
    public static void main(String[] args) {
        // Select all test cases or the graphs as once.
        var pathURL = System.getProperty("user.dir") + "\\Datasets With Random Investments";
        List<String> listOfAttackGraphs = new ArrayList<>();
        Path folder = Path.of(pathURL);
        if (Files.exists(folder) && Files.isDirectory(folder)) {
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(folder)) {
                for (Path path : directoryStream) {
                    listOfAttackGraphs.add(path.getFileName().toString());
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        // Define a string array to store the name of the allocation approach as the header of the performance matrix
        var headers = new String[5];

        // Define a hashmap that maps each graph with the allocation approaches sorted for it
        var mapGraphsToAllocationSortedMethods = new HashMap<String, HashMap<String, Double>>();

        // define the duration time object
        var durationTime = 0L;

        // Iterate over all graph cases we have
        for (String graphCase : listOfAttackGraphs) {
            System.out.println(graphCase);
            graphCase = graphCase.replace(".txt", "");
            var task = new GraphData(Path.of(pathURL + "\\" + graphCase + ".txt"));
            var attackDefenceGraph = task.getAttackDefenceGraph();
            var AdjMat = task.getAdjacencyMatrix(attackDefenceGraph);
            var assetLossVec = task.getNodeAssetsLossValues();

            // Generate paths by genetic algorithm from each entry node to each asset
            var concurrentAttackers = new ConcurrentAttack(AdjMat,
                assetLossVec,
                500,
                0.2,
                0.4,
                0.6,
                100);
            var concurrentAttacks = concurrentAttackers.getAllPaths();

            // Define the security Budget
            var resources = 20;

            // Define a hashmap that maps the allocation to their cost relative reduction
            var mapAllocationMethodToRelativeCostReduction = new HashMap<String, Double>();

            // Define and initialize the array to hold the relative cost reduction from each allocation approach
            double[] scoresRow = new double[5];

            // Fetch the current time in ms
            var startTime = System.currentTimeMillis();

            // Calculate the expected relative reduction in the cost after allocating resources using risk based defense
            var riskBasedDefence = new AllocationApproaches(task,
                    attackDefenceGraph,
                    AdjMat,
                    concurrentAttacks,
                    assetLossVec,
                    resources);
            riskBasedDefence.callRiskBasedDefense();
            headers[0] = "riskBasedDefence";
            scoresRow[0] = riskBasedDefence.getExpectedCostReduction();
            mapAllocationMethodToRelativeCostReduction.put(headers[0], scoresRow[0]);
            mapGraphsToAllocationSortedMethods.put(graphCase, mapAllocationMethodToRelativeCostReduction);

            // Calculate the expected relative reduction in the cost after allocating resources using defense in depth
            var defenceInDepth = new AllocationApproaches(task,
                    attackDefenceGraph,
                    AdjMat,
                    concurrentAttacks,
                    assetLossVec,
                    resources);
            defenceInDepth.callDefenseInDepth();
            headers[1] = "defenceInDepth";
            scoresRow[1] = defenceInDepth.getExpectedCostReduction();
            mapAllocationMethodToRelativeCostReduction.put(headers[1], scoresRow[1]);
            mapGraphsToAllocationSortedMethods.put(graphCase, mapAllocationMethodToRelativeCostReduction);

            // Calculate the expected relative reduction in the cost after allocating resources using behavioral defender
            var behavioralDefender = new AllocationApproaches(task,
                    attackDefenceGraph,
                    AdjMat,
                    concurrentAttacks,
                    assetLossVec,
                    resources);
            behavioralDefender.callBehavioralDefender();
            headers[2] = "behavioralDefender";
            scoresRow[2] = behavioralDefender.getExpectedCostReduction();
            mapAllocationMethodToRelativeCostReduction.put(headers[2], scoresRow[2]);
            mapGraphsToAllocationSortedMethods.put(graphCase, mapAllocationMethodToRelativeCostReduction);

            // Calculate the expected relative reduction in the cost after allocating resources using behavioral defender
            var minCut = new AllocationApproaches(task,
                    attackDefenceGraph,
                    AdjMat,
                    concurrentAttacks,
                    assetLossVec,
                    resources);
            minCut.callMinCut();
            headers[3] = "minCut";
            scoresRow[3] = minCut.getExpectedCostReduction();
            mapAllocationMethodToRelativeCostReduction.put(headers[3], scoresRow[3]);
            mapGraphsToAllocationSortedMethods.put(graphCase, mapAllocationMethodToRelativeCostReduction);

            // Cluster the nodes into K-clusters to gather similar nodes
            NodeClustering clustering = new NodeClustering(System.getProperty("user.dir") + "\\Node Embeddings_" + graphCase + ".csv",
                    3,
                    new ManhattanDistance(),
                    100);
            HashMap<Integer, ArrayList<Integer>> map = clustering.getMapClustersToNodes();

            for (var c : map.keySet()) {
                var cluster = map.get(c);
                System.out.print(c + " < ");
                for (var n : cluster) {
                    if (assetLossVec[n - 1] > 0) {
                        System.out.print(n + ", ");
                    }
                }
                System.out.println(" > ");
            }

            // Calculate the expected relative reduction in the cost after allocating resources using TrustRank centrality + In-Degree nodes
            var SAG_ERC = new AllocationApproaches(task,
                    attackDefenceGraph,
                    AdjMat,
                    concurrentAttacks,
                    assetLossVec,
                    resources);
            SAG_ERC.call_SIS_RA(map, 100);
            headers[4] = "SAG-ERC";
            scoresRow[4] = SAG_ERC.getExpectedCostReduction();
            mapAllocationMethodToRelativeCostReduction.put(headers[4], scoresRow[4]);
            mapGraphsToAllocationSortedMethods.put(graphCase, mapAllocationMethodToRelativeCostReduction);

            // Fetch the current time in ms
            var endTime = System.currentTimeMillis();

            // calculate the duration time for each graph
            System.out.println("allocation for graph :\t" + graphCase + "\thas been carried out");
            durationTime += (endTime - startTime);
        }
        // Duration time for resource allocation over all graphs
        System.out.println("\nThe time of applying all of our resource allocation is : " + Util.formatDuration(0, durationTime));

        System.out.println("The allocation process has been completed");
        StoreAllocationResults.org.SaveRA_Results.storeDataFromHashMap("Results", headers, mapGraphsToAllocationSortedMethods);
    }
}