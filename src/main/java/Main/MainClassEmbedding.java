package Main;

import Attack_Defence_Graph.org.GraphData;
import RandomWalk.NodeEmbedded;
import RandomWalk.PositiveAndNegativeSamples;
import RandomWalk.PrepareGraph;
import RandomWalk.RandomWalk;
import StoreEmbeddings.org.SaveEmbeddings;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MainClassEmbedding implements SaveEmbeddings {
    public static void main(String[] args) {
        // Select all test cases or the graphs as once.
        List<String> listOfAttackGraphs = new ArrayList<>();
        Path folder = Path.of(System.getProperty("user.dir") + "\\Datasets");
        if (Files.exists(folder) && Files.isDirectory(folder)) {
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(folder)) {
                for (Path path : directoryStream) {
                    listOfAttackGraphs.add(path.getFileName().toString());
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        // Define the dimension of the embeddings
        var embeddingDimension = 256;

        // Iterate over all graph cases we have
        for (String graphCase : listOfAttackGraphs) {
            graphCase = graphCase.replace(".txt", "");
            var task = new GraphData(Path.of(System.getProperty("user.dir") + "\\Datasets\\" + graphCase + ".txt"));
            var attackDefenceGraph = task.getAttackDefenceGraph();
            var AdjMat = task.getAdjacencyMatrix(attackDefenceGraph);
            var assetLossVec = task.getNodeAssetsLossValues();

            // Prepare the graph to operate according to the JGraphT library
            PrepareGraph graphPrep = new PrepareGraph(AdjMat);
            var graph = graphPrep.getGraph();

            // Generate the sequences according to the random walk algorithm
            var randomWalk = new RandomWalk(graph, assetLossVec, 100);
            var sequence = randomWalk.getSequences();

            // Get a dataset including positive and negative samples
            var samples = new PositiveAndNegativeSamples(AdjMat, assetLossVec, sequence);
            var dataPositiveAndNegativeSamples = samples.generatePositiveSamplesViaBigRamAndNegativeSamples();

            // Define the embedding engine to learn the embeddings and to store them
            var embedding = new NodeEmbedded(graph.vertexSet().size(),
                    dataPositiveAndNegativeSamples,
                    embeddingDimension,
                    150,
                    0.01,
                    12345);
            System.out.println("\nStart learning for Graph: " + graphCase);
            embedding.trainModel();

            var embeddings = embedding.getEmbeddings();
            SaveEmbeddings.storeGraphEmbeddings(embeddings, "Node Embeddings" + "_" +graphCase);
        }
    }
}