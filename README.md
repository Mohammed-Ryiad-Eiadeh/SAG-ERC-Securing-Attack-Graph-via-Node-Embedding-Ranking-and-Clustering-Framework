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

