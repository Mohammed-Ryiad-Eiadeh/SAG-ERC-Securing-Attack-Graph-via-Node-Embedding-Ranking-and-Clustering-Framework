package ResourceAllocationsApproaches.org;

import Attack_Defence_Graph.org.GraphData;
import BehavioralDefense.org.BehavioralDefender;
import CostFunction.org.CostFunction;
import Defender.org.Defenders;
import EntireGraphRisk.org.GraphRisk;
import GraphAnalysisMethods.org.InDegreeNodes;
import GraphAnalysisMethods.org.MinCutEdges;
import PersonalizedTrustRank.org.TrustRankAlgorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


public class AllocationApproaches {
    private final GraphData task;
    private final Defenders[][] DefendersMatrix;
    private final double[][] AdjMatrix;
    private final HashMap<Integer, ArrayList<List<List<Integer>>>> concurrentAttack;
    private final double[] AssetLossVector;
    private double ExpectedCostReduction;
    private final int SecurityBudget;

    public AllocationApproaches(GraphData task, Defenders[][] defenders, double[][] adjMatrix, HashMap<Integer, ArrayList<List<List<Integer>>>> concurrentAttack, double[] assetLossVector, int securityBudget) {
        if (task == null) {
            throw new IllegalArgumentException("The graph task object is null!");
        }
        if (defenders == null) {
            throw new IllegalArgumentException("The defenders object is null!");
        }
        if (adjMatrix == null) {
            throw new IllegalArgumentException("The adjacent matrix is null!");
        }
        if (concurrentAttack == null) {
            throw new IllegalArgumentException("The concurrent attack object is null!");
        }
        if (assetLossVector == null) {
            throw new IllegalArgumentException("The concurrent attack object is null!");
        }
        if (securityBudget <= 0) {
            throw new IllegalArgumentException("The security budget have to be positive integer!");
        }
        this.task = task;
        this.DefendersMatrix = defenders;
        this.AdjMatrix = adjMatrix;
        this.concurrentAttack = concurrentAttack;
        this.AssetLossVector = assetLossVector;
        this.SecurityBudget = securityBudget;
        this.ExpectedCostReduction = 0.0;
    }

    private double calculateTheCostBeforeAllocation() {
        // Get the simultaneous attacks to simulate real attackers
        HashMap<Integer, ArrayList<List<List<Integer>>>> concurrentAttacks = concurrentAttack;
        // Get the assets involving into this attack paths
        Set<Integer> assets = concurrentAttacks.keySet();
        // Calculate the cost for the generated paths before allocating the resources
        CostFunction costFunctionBeforeAllocation = new CostFunction(AdjMatrix, AssetLossVector);
        double costsBeforeAllocation = 0.0d;
        for (Integer asset : assets) {
            ArrayList<List<List<Integer>>> pathsToThisAsset = concurrentAttacks.get(asset);
            double cost = 0.0;
            for (List<List<Integer>> paths : pathsToThisAsset) {
                for (List<Integer> path : paths) {
                    cost += costFunctionBeforeAllocation.computeCost(path);
                }
            }
            costsBeforeAllocation += cost / concurrentAttacks.get(asset).size();
            costsBeforeAllocation += costsBeforeAllocation / pathsToThisAsset.size();
        }
        return costsBeforeAllocation;
    }

    private double calculateTheCostAfterAllocation(double[][] updatedAdjacentMatrix) {
        // Get the simultaneous attacks to simulate real attackers
        HashMap<Integer, ArrayList<List<List<Integer>>>> concurrentAttacks = concurrentAttack;
        // Get the assets involving into this attack paths
        Set<Integer> assets = concurrentAttacks.keySet();
        CostFunction costFunctionAfterAllocation = new CostFunction(updatedAdjacentMatrix, AssetLossVector);
        double costsAfterAllocation = 0.0d;
        for (Integer asset : assets) {
            ArrayList<List<List<Integer>>> pathsToThisAsset = concurrentAttacks.get(asset);
            double cost = 0.0;
            for (List<List<Integer>> paths : pathsToThisAsset) {
                for (List<Integer> path : paths) {
                    cost += costFunctionAfterAllocation.computeCost(path);
                }
            }
            costsAfterAllocation += cost / concurrentAttacks.get(asset).size();
            costsAfterAllocation += costsAfterAllocation / pathsToThisAsset.size();
        }
        return costsAfterAllocation;
    }

    public void callRiskBasedDefense() {
        // Select the test case or the graph; construct the defenders; construct the adjacency matrix; display the graph.
        GraphData graphTask = task;
        Defenders[][] defendersMatrix = deepCopy(DefendersMatrix);

        // Calculate the cost before the allocation process
        double costsBeforeAllocation = calculateTheCostBeforeAllocation();

        // Set the spare budget of security resources for each defender
        Defenders.spareBudget_D = SecurityBudget;

        // Get the total risk to do normalizing
        double totalRisk = new GraphRisk(AdjMatrix).totalRisk();

        // Set the defender's security budget
        double budget = Defenders.spareBudget_D;

        // This code segment is referred to allocate spare defenders investments on paths involved in "in" and "out" degree edges
        for (int i = 0; i < AdjMatrix.length; i++) {
            for (int j = 0; j < AdjMatrix[0].length; j++) {
                if (defendersMatrix[i][j].totalInvest() > 0) {
                    Defenders edge = defendersMatrix[i][j];
                    edge.setInvest_D(edge.addSpareInvestFor_D(budget * (Math.exp(-edge.totalInvest()) / totalRisk)));
                }
            }
        }
        // Update the adjacency matrix
        double[][] modifiedAdjMatrix = graphTask.getAdjacencyMatrix(defendersMatrix);
        // Calculate the cost after the allocation process
        double costsAfterAllocation = calculateTheCostAfterAllocation(modifiedAdjMatrix);
        // Calculate the expected cost reduction
        ExpectedCostReduction = Math.abs(costsBeforeAllocation - costsAfterAllocation) / costsBeforeAllocation * 100;
    }

    public void callDefenseInDepth() {
        // Select the test case or the graph; construct the defenders; construct the adjacency matrix; display the graph.
        GraphData graphTask = task;
        Defenders[][] defendersMatrix = deepCopy(DefendersMatrix);

        // Calculate the cost before the allocation process
        double costsBeforeAllocation = calculateTheCostBeforeAllocation();

        // Set the spare budget of security resources for each defender
        Defenders.spareBudget_D = SecurityBudget;

        // This code segment is referred to get the number of all edges in the graph
        int numOfEdges = task.getNumberOfEdges();
        double budget = Defenders.spareBudget_D / numOfEdges;

        // This code segment is referred to allocate spare defenders investments on all edges equally
        for (int i = 0; i < AdjMatrix.length; i++) {
            for (int j = 0; j < AdjMatrix[0].length; j++) {
                if (defendersMatrix[i][j].totalInvest() > 0) {
                    Defenders edge = defendersMatrix[i][j];
                    edge.setInvest_D(edge.addSpareInvestFor_D(budget));
                }
            }
        }
        // Update the adjacency matrix
        double[][] modifiedAdjMatrix = graphTask.getAdjacencyMatrix(defendersMatrix);
        // Calculate the cost after the allocation process
        double costsAfterAllocation = calculateTheCostAfterAllocation(modifiedAdjMatrix);
        // Calculate the expected cost reduction
        ExpectedCostReduction = Math.abs(costsBeforeAllocation - costsAfterAllocation) / costsBeforeAllocation * 100;
    }

    public void callBehavioralDefender() {
        // Select the test case or the graph; construct the defenders; construct the adjacency matrix; display the graph.
        GraphData graphTask = task;
        Defenders[][] defendersMatrix = deepCopy(DefendersMatrix);

        // Calculate the cost before the allocation process
        double costsBeforeAllocation = calculateTheCostBeforeAllocation();

        // Set the spare budget of security resources for each defender
        Defenders.spareBudget_D = SecurityBudget;

        // This code segment is referred to allocate spare defenders investments on all attack paths according to behavioral defender
        double budget = Defenders.spareBudget_D;

        // Apply the behavioral defender
        BehavioralDefender behavioralDefender = new BehavioralDefender(AdjMatrix, 0.5f);
        double[][] newWeights = behavioralDefender.applyBehavioralDefendingForResourceAllocation();
        double sumOfProbWeighting = 0.0d;
        for (double[] newWeight : newWeights) {
            for (int j = 0; j < newWeights[0].length; j++) {
                sumOfProbWeighting += newWeight[j];
            }
        }
        for (int i = 0; i < AdjMatrix.length; i++) {
            for (int j = 0; j < AdjMatrix[0].length; j++) {
                if (AdjMatrix[i][j] > 0) {
                    Defenders edge = defendersMatrix[i][j];
                    edge.setInvest_D(edge.addSpareInvestFor_D(newWeights[i][j] / sumOfProbWeighting * budget));
                }
            }
        }
        // Update the adjacency matrix
        double[][] modifiedAdjMatrix = graphTask.getAdjacencyMatrix(defendersMatrix);
        // Calculate the cost after the allocation process
        double costsAfterAllocation = calculateTheCostAfterAllocation(modifiedAdjMatrix);
        // Calculate the expected cost reduction
        ExpectedCostReduction = Math.abs(costsBeforeAllocation - costsAfterAllocation) / costsBeforeAllocation * 100;
    }

    public void callMinCut() {
        // Select the test case or the graph; construct the defenders; construct the adjacency matrix; display the graph.
        GraphData graphTask = task;
        Defenders[][] defendersMatrix = deepCopy(DefendersMatrix);

        // Calculate the cost before the allocation process
        double costsBeforeAllocation = calculateTheCostBeforeAllocation();

        // Set the spare budget of security resources for each defender
        Defenders.spareBudget_D = SecurityBudget;

        // This code segment is referred to capture the min-cut edges among each asset-asset
        MinCutEdges MCE = new MinCutEdges(AdjMatrix, AssetLossVector);
        HashMap<Integer, ArrayList<MinCutEdges.MCEdge>> nodes = MCE.getMinCutEdges();

        // Divide the spare budget of security resources over the number of edges
        int size = nodes.values().stream().mapToInt(ArrayList::size).sum();
        double budget = Defenders.spareBudget_D / size;

        // This code segment is referred to allocate spare defenders investments on paths involved in "in" and "out" degree edges
        for (Integer assetNode : nodes.keySet()) {
            ArrayList<MinCutEdges.MCEdge> parents = nodes.get(assetNode);
            for (MinCutEdges.MCEdge nod : parents) {
                Defenders edge = defendersMatrix[nod.GetStart() - 1][nod.GetEnd() - 1];
                edge.setInvest_D(edge.addSpareInvestFor_D(budget));
            }
        }
        // Update the adjacency matrix
        double[][] modifiedAdjMatrix = graphTask.getAdjacencyMatrix(defendersMatrix);
        // Calculate the cost after the allocation process
        double costsAfterAllocation = calculateTheCostAfterAllocation(modifiedAdjMatrix);
        // Calculate the expected cost reduction
        ExpectedCostReduction = Math.abs(costsBeforeAllocation - costsAfterAllocation) / costsBeforeAllocation * 100;
    }

    public void call_SAG_ERC(HashMap<Integer, ArrayList<Integer>> clusterToNodeMapper, int numOfIteration) {
        // Select the test case or the graph; construct the defenders; construct the adjacency matrix; display the graph.
        GraphData graphTask = task;
        Defenders[][] defendersMatrix = deepCopy(DefendersMatrix);

        // Calculate the cost before the allocation process
        double costsBeforeAllocation = calculateTheCostBeforeAllocation();

        // Define a map to map each cluster with its corresponding risk
        HashMap<Integer, Double> mapClusterToRisk = new HashMap<>();

        // Calculate each cluster risk
        for (int clusterIndex : clusterToNodeMapper.keySet()) {
            // Retrieve the nodes in the current cluster
            List<Integer> listOfNodes = clusterToNodeMapper.get(clusterIndex);

            // Initialize cluster risk and a flag to check for assets
            double clusterRisk = 1.0; // Use this value
            boolean clusterHasAtLeastOneAssetFlag = false;

            // Iterate over each node in the cluster
            for (int node : listOfNodes) {
                // Check if the node is an asset
                if (AssetLossVector[node - 1] > 0) {
                    List<Integer> inDegreeNodes = getInDegree(node);
                    // Check if the asset is a reachable node or not
                    if (!inDegreeNodes.isEmpty()) {
                        // Compute risk contribution from in-degree nodes
                        for (int incomingNode : inDegreeNodes) {
                            clusterRisk *= Math.exp(-AdjMatrix[incomingNode - 1][node - 1]);
                        }
                        // Mark the cluster as having at least one asset
                        clusterHasAtLeastOneAssetFlag = true;
                    }
                }
            }
            // Only store the cluster risk if the cluster contains at least one asset
            if (clusterHasAtLeastOneAssetFlag) {
                mapClusterToRisk.put(clusterIndex, clusterRisk); // Convert back from log scale
            }
        }
        // Do cluster risk normalization
        double sumOfClustersRisk = mapClusterToRisk.values().stream().mapToDouble(Double::doubleValue).sum();
        mapClusterToRisk.replaceAll((K, V) -> V / sumOfClustersRisk);

        // Set the spare budget of security resources for each defender
        Defenders.spareBudget_D = SecurityBudget;
        double budget = Defenders.spareBudget_D;

        // This code segment is referred to rank each asset according to centrality algorithms
        HashMap<Integer, Double> scores = new TrustRankAlgorithm(AdjMatrix,
                AssetLossVector,
                numOfIteration)
                .getScores();

        // Map each cluster with its asset nodes scores by TR for later normalization
        HashMap<Integer, Double> mapClusterToAssetRiskSummation = new HashMap<>();
        for (int clusterId : clusterToNodeMapper.keySet()) {
            double sum = 0.0;
            ArrayList<Integer> clusterNodes = new ArrayList<>(clusterToNodeMapper.get(clusterId));
            if (!clusterNodes.isEmpty()) {
                for (int node : clusterNodes) {
                    if (AssetLossVector[node - 1] > 0) {
                        sum += scores.get(node);
                    }
                }
            }
            mapClusterToAssetRiskSummation.put(clusterId, sum);
        }

        // This code segment is referred to capture the connections among each asset and its neighbor nodes
        InDegreeNodes IDN = new InDegreeNodes(AdjMatrix, AssetLossVector);
        HashMap<Integer, ArrayList<Integer>> nodes = IDN.retrieveInDegreeNodes();

        // This code segment is referred to allocate spare defenders investments on paths involved in "in" and "out" degree edges
        for (Integer assetNode : nodes.keySet()) {
            // Get the cluster of this asset node
            int cluster = 0;
            for (; cluster < clusterToNodeMapper.size(); cluster++) {
                if (clusterToNodeMapper.get(cluster).contains(assetNode)) {
                    break;
                }
            }
            // Get the normalized cluster risk
            double clusterImportanceUponNormalizedRisk = mapClusterToRisk.get(cluster);
            // Get the parents of this asset
            ArrayList<Integer> parents = nodes.get(assetNode);
            // Update the investments based on the two level normalization (cluster risk-level and asset rank level)
            for (Integer nod : parents) {
                Defenders edge = defendersMatrix[nod - 1][assetNode - 1];
                int sizeParents = nodes.get(assetNode).stream().toList().size();
                double currentAssetCutOfTotalBudget = budget
                        * clusterImportanceUponNormalizedRisk
                        * (scores.get(assetNode) / mapClusterToAssetRiskSummation.get(cluster))
                        / sizeParents;
                edge.setInvest_D(edge.addSpareInvestFor_D(currentAssetCutOfTotalBudget));
            }
        }
        // Update the adjacency matrix
        double[][] modifiedAdjMatrix = graphTask.getAdjacencyMatrix(defendersMatrix);
        // Calculate the cost after the allocation process
        double costsAfterAllocation = calculateTheCostAfterAllocation(modifiedAdjMatrix);
        // Calculate the expected cost reduction
        ExpectedCostReduction = Math.abs(costsBeforeAllocation - costsAfterAllocation) / costsBeforeAllocation * 100;
    }

    private Defenders[][] deepCopy(Defenders[][] originalVersion) {
        Defenders[][] copyVersion = new Defenders[originalVersion.length][];
        for (int i = 0; i < originalVersion.length; i++) {
            copyVersion[i] = new Defenders[originalVersion[i].length];
            for (int j = 0; j < originalVersion[i].length; j++) {
                copyVersion[i][j] = originalVersion[i][j].clone();
            }
        }
        return copyVersion;
    }

    public double getExpectedCostReduction() {
        return ExpectedCostReduction;
    }

    /**
     * This method is used to get the in degree nodes for the given node.
     * @param node The node of interest.
     * @return List of in degree nodes.
     */
    private ArrayList<Integer> getInDegree(int node) {
        ArrayList<Integer> parentsNodes = new ArrayList<>();
        for (int i = 0; i < AdjMatrix.length; i++) {
            if (AdjMatrix[i][node - 1] > 0) {
                parentsNodes.add(i + 1);
            }
        }
        return parentsNodes;
    }
}

