# SAG-ERC: Securing Attack-Graph-based Systems through Node Embedding, Ranking, and Clustering

# Abstract
Securing interdependent systems (where a successful attack on an asset can lead to the compromise of other assets in the system) is a challenging task.
This paper introduces a security framework for these interdependent systems managed by a single defender. Using attack graphs to model vulnerabilities, we propose a resource allocation strategy that prioritizes edges through in-degree nodes. We apply a random walk embedding algorithm to project nodes into feature vectors, followed by KMeans clustering to group similar nodes and calculate the severity risk of each cluster. We also use TrustRank algorithm to evaluate asset influence. Resources are then allocated based on normalized cluster risks and node ranking scores. Our method outperforms existing resource allocation algorithms across four real-world systems, supported by statistical validation. The implementation source code is available for further development.

# Framework
Our proposed framework, SAG-ERC, addresses resource allocation challenges in interdependent systems. It leverages attack graphs by focusing on entry nodes (attackers’ access points) and critical asset nodes (defender’s targets). Using a Genetic Algorithm (GA), SAG-ERC identifies the most likely attack paths between entry and asset nodes, evaluating them based on potential asset losses and associated risks. Feature vectors for nodes are then generated using a random walk node embedding technique, enabling K-Means clustering to group nodes with similar properties. The severity of risk for each cluster is normalized to guide the defender's security budget allocation. SAG-ERC integrates TrustRank to rank nodes, prioritizing critical assets and ensuring proportional resource distribution across incoming edges. In summary, the framework strategically secures critical assets by optimizing resource allocation across the attack graph.

![Screenshot (20)](https://github.com/user-attachments/assets/809026b8-e47c-4185-8f9d-2347748ac9ce)

# Fitness Function
$F_2(P) = \max_{P \in P_m} \big(\exp\big(-\sum_{(v_i,v_j)\in P} {x_{i,j}}\big) + Wf\sum_{v_m\in P} L_m\big).$
   
   $P$ is the given attack path.

   $P_m$ is a set of attack paths.

   $v_i,v_j$ are the nodes in $P$.

   $L_m$ is the loss corresponding to node $v_m$

   $Wf$ is the weight factor lies in [0,1]
   
This function accounts for the total asset loss that the system will lose if the attack is occured successfully.

# Random Walk, Negative Sampling, and Gradient Descent for Node Embedding
Node embedding transforms a node into a \(d\)-dimensional vector, capturing its structural properties for easier analysis, which simplifies tasks like node classification, prediction, and community detection. In the context of attack graphs, the goal is to project critical asset nodes into a \(d\)-dimensional vector space, capturing their structural characteristics. The log-likelihood objective function aims to maximize the likelihood of neighboring nodes appearing together in random walks, refining the embeddings by positioning closely related nodes nearer in the vector space. To address the computational challenges in large-scale graphs, negative sampling is employed, approximating the normalization term by selecting a small subset of nodes, typically 5–20 negative samples. In SAG-ERC, all asset nodes are specifically considered as negative sample nodes, generating both positive and negative samples efficiently. The embeddings are optimized using stochastic gradient descent (SGD), updating the vectors based on the gradient at each epoch.

# KMeans Clustering
K-means clustering is an unsupervised algorithm that divides a set data points into $K$ distinct clusters. Each data point is assigned to the cluster with the closest mean. The algorithm creates $K$ groups, each centered around a mean value, and ensures that the clusters are well-separated by a large distance. In this study, we use K-means to cluster different nodes based on their aforementioned embeddings. Here, the distance is calculated using the Manhattan distance.

# TrustRank
TrustRank is a linkage analysis algorithm designed to identify useful web pages and distinguish them from spam, improving search engine rankings. Unlike PageRank, which only considers link structure, TrustRank incorporates page quality by simulating an imaginary surfer who prefers higher-quality links. The algorithm uses a damping factor to control the likelihood of following links, and it propagates trust across the graph by adjusting node rankings iteratively. The rankings are updated based on a combination of the link structure and an initial seed vector, which is set according to asset loss for asset nodes and to a constant value for non-asset nodes. This process helps identify more reliable and valuable nodes within the network.

# Main Motivation
The motivation behind this resource allocation strategy is to prioritize the protection of critical assets within a system by focusing on clusters with higher risk levels. Assets are ranked based on their importance within the graph, and only those with potential financial losses in the event of a breach are considered. By normalizing the ranks within each cluster according to the severity of each cluster, the strategy ensures that the resource allocation is proportional to both the risk associated with the cluster and the importance (rank) of the asset. This method emphasizes safeguarding assets that are more vulnerable to attack by assigning resources based on their relative risk and importance within the network.

# Our Contribution
1) Introduce a novel resource allocation method tailored for interdependent systems, shaping decision-making processes for system security.
   
2) Adopt the random walk method to transform each node in the attack graph into a feature vector and apply the KMeans clustering algorithm to group similar nodes into clusters.
   
3) Employ the TrustRank algorithm to analyze asset nodes and determine the importance of each asset within the system.
   
4) Allocate limited security resources to the incoming edges of each asset, guided by cluster-level risk normalization and asset-level rank normalization.
    
5) Assess SAG-ERC across four systems and compare its performance with four baseline resource allocation methods.  

For our assessment, we used four distinct attack graphs, each symbolizing a different interdependent system and network structure. These datasets include attack graphs from real-world interconnected systems, namely DER.1, SCADA, E-commerce, and VOIP. Signifies an attack step, and we consider every edge to be directional. 

| System | # Nodes | # Edges | # Critical Assets | Graph Type |
| --- | --- | --- | --- | --- |
| SCADA [12] | 13 | 20 | 6 | Directed |
| DER.1 [13] | 22 | 32 | 6 | Directed |
| E-Commerce [14] | 20 | 32 | 4 | Directed |
| VOIP [14] | 22 | 35 | 4 | Directed |

Note: all of these datasets are stored in the project directory and is called dynamically so no need to set up their paths.

# Parameter Configuration of Our Experiments
- **Genetic Algorithm (GA) Hyperparameters**:  
  - Maximum number of iterations: `M = 100`  
  - Population size: `N = 500`  
  - Crossover probability: `m_p = 0.4`  
  - Mutation rate: `m_r = 0.2`  
  - Selection probability: `s_p = 0.6`  
  - Weight factor for fitness function: `W_f = 0.001`  

- **Defender's Security Budget**:  
  - `S = 20`  

- **Embedding Parameters**:  
  - Dimension size: `d_s = 256`  
  - Maximum number of hops: `m_h = 100`  
  - Number of epochs: `epochs = 150`  
  - Learning rate: `λ = 0.01`  

- **KMeans Clustering**:  
  - Number of clusters: `K = 3`  
  - Maximum number of epochs: `K_epochs = 100`  

- **TrustRank Hyperparameters**:  
  - Number of iterations: `TR_iter = 100`  
  - Epsilon: `ε = 0.00001`  
  - Weight factor: `W_f^0 = 0.01`  

- **Behavioral Defender Baseline**:  
  - Behavioral level: `a = 0.5`  

- **Initial Edge Weights**:  
  - Randomly generated values between `0` and `1`.  

**Note**: The benefits of our defense strategies apply across different security budgets.

# Comparison of AARA-PR and baseline systems on all datasets

The row "Measurements" show the relative difference of the expected cost $CR$ for all defense scenarios. The larger $CR$, the better the defense method with significance level equals 0.05 for the Friedman test.

** A Comparison (based on relative reduction in expected cost \(CR \%\)) between SAG-ERC and four baseline algorithms.**

**SAG-ERC yields superior performance with the highest mean rank and highest relative reduction in expected cost.**

| **System**          | **SAG-ERC** | **Defense in Depth** | **Behavioral Defender** | **Min-Cut** | **Risk-Based** |
|----------------------|-------------|-----------------------|--------------------------|-------------|----------------|
| SCADA               | **86.49**   | 63.212               | 61.03                   | 80.96       | 61.00          |
| DER.1               | **89.59**   | 46.474               | 50.17                   | 47.82       | 50.01          |
| E-Comm              | **79.13**   | 46.474               | 50.59                   | 46.19       | 50.39          |
| VOIP                | **84.32**   | 43.528               | 44.43                   | 69.24       | 46.00          |
| **Rank First**       | **4**       | 0                     | 0                        | 0           | 0              |
| **Sum of Ranks**     | **16**      | 7                     | 12                       | 11          | 10             |
| **Mean Rank**        | **4**       | 1.75                  | 3.00                     | 2.75        | 2.50           |

# Conclusion
This study introduces a new resource allocation strategy for interdependent system security, focusing on decision-making impacts. We modeled vulnerabilities using attack graphs and proposed a method that prioritizes edges through in-Degree Defense. By adapting random walk with negative sampling and gradient descent, nodes are transformed into feature sets and grouped using KMeans clustering. The risk levels of clusters and assets' ranks are normalized, with resources allocated proportionally to these normalized ratios on the incoming edges of each asset. Our method, demonstrating low sensitivity to varying attacks, and is evaluated across four real-world systems and compared with four existing techniques, showcasing its superior effectiveness. \name proves to be a robust security allocation method, and we offer an open-source implementation for further customization.

# How To Run The Code (read carefully please)

1) Download intellIJ IDEA latest version
2) Dounload JDK 17 or higher
3) Set up the environment variable for the bin folder of the JDK 17+
4) Open the IDEA
5) Open the project
6) Make sure you are connected to the internet
7) Wait while the IDEA download all the libraries that are included as dependencies in the pom XML file
8) Go to the main file (here you will get 3 files that are executable (have "psvm" method)) so these files are as follows:

   a) BehavioralDefenderMain: this class is used to run a behavioral defender based on prospect theory for allocating the resourcess.

   b) DefenseInDepthMain: this class is used to allocate the resourcess equally over all edges of the given graph.

   c) PageRankWithInDegreeMain: this class is used to run AARA-PR which uses PageRank to rank the assets considering the losess and propagating the resourcess on the in-degree edges of each asset.

9) Set up the desired hyperparameters
10) Run the file to see the results.
   
# References

[12] A. R. Hota, A. Clements, S. Sundaram, and S. Bagchi. 2016. Optimal and game-theoretic deployment of security investments in interdependent assets. In International Conference on Decision and Game Theory for Security. 101–113.

[13] S. Jauhar, B. Chen, W. G. Temple, X. Dong, Z. Kalbarczyk, W. H. Sanders, and D. M. Nicol. 2015. Model-based cybersecurity assessment with nescor smart grid failure scenarios. In Dependable Computing (PRDC), 2015 IEEE 21st Pacific Rim International Symposium on. IEEE, 319–324.

[14] G. Modelo-Howard, S. Bagchi, and G. Lebanon. 2008. Determining placement of intrusion detectors for a distributed application through bayesian network modeling. In International Workshop on Recent Advances in Intrusion Detection. Springer, 271–290.

# Contact With Authors

Send email to the following authors for any question about this work, and it is our pleasure to ansawer your question.

Mohammad Aleiadeh, mraleiad@iu.edu or maleiade@purdue.edu

dr. Mustafa Abdallah, mabdall@iu.edu or abdalla0@purdue.edu

Note: Authors are arranged alphabetically.


