package RandomWalk;

import java.util.ArrayList;
import java.util.Collections;

public class PositiveAndNegativeSamples {
    private final double[][] adjMatrix;
    private final double[] assetLossVec;
    private final ArrayList<ArrayList<Integer>> sequences;

    /**
     * This constructor is used to initialize the adjacency matrix that is going to be passed to page rank
     * @param adjMatrix The adjacency matrix which represents the attack-defence graph
     * @param lossVector The assets' loss values as a vector
     * @param sequences The sequences which is the random walks
     */
    public PositiveAndNegativeSamples(double[][] adjMatrix, double[] lossVector, ArrayList<ArrayList<Integer>> sequences) {
        if (adjMatrix == null) {
            throw new IllegalArgumentException("The matrix is null!");
        }
        if (lossVector == null) {
            throw new IllegalArgumentException("The loss vector is null");
        }
        if (sequences == null) {
            throw new IllegalArgumentException("The random walks are null");
        }
        this.adjMatrix = adjMatrix;
        this.assetLossVec = lossVector;
        this.sequences = sequences;
    }

    public ArrayList<Dataset> generatePositiveSamplesViaBigRamAndNegativeSamples() {
        ArrayList<Dataset> dataset = new ArrayList<>();
        for (ArrayList<Integer> sequence : sequences) {
            for (int i = 0; i < sequence.size() - 1; i++) {
                dataset.add(new Dataset(sequence.get(i), sequence.get(i + 1), "positive"));
            }
        }
        ArrayList<Integer> listOfTopAssetNodes = new ArrayList<>();
        for (int nodeIndex = 0; nodeIndex < assetLossVec.length; nodeIndex++) {
            if (assetLossVec[nodeIndex] > 0) {
                listOfTopAssetNodes.add(nodeIndex + 1);
            }
        }
        ArrayList<Integer> nonAssetsNode = new ArrayList<>();
        for (int node = 0; node < adjMatrix.length; node++) {
            if (assetLossVec[node] == 0) {
                nonAssetsNode.add(node + 1);
            }
        }
        for (Integer integer : nonAssetsNode) {
            for (Integer asset : listOfTopAssetNodes) {
                int node = integer;
                if (adjMatrix[node - 1][asset - 1] == 0) {
                    if (!dataset.contains(new Dataset(node, asset - 1, "negative"))) {
                        dataset.add(new Dataset(node, asset - 1, "negative"));
                    }
                }
            }
        }
        Collections.shuffle(dataset);
        return dataset;
    }
}
