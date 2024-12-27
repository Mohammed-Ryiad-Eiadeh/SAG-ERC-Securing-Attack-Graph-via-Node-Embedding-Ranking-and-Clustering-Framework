# SAG-ERC: Securing Attack-Graph-based Systems through Node Embedding, Ranking, and Clustering

# Abstract
Securing interdependent systems (where a successful attack on an asset can lead to the compromise of other assets in the system) is a challenging task.
This paper introduces a security framework for these interdependent systems managed by a single defender. Using attack graphs to model vulnerabilities, we propose a resource allocation strategy that prioritizes edges through in-degree nodes. We apply a random walk embedding algorithm to project nodes into feature vectors, followed by KMeans clustering to group similar nodes and calculate the severity risk of each cluster. We also use TrustRank algorithm to evaluate asset influence. Resources are then allocated based on normalized cluster risks and node ranking scores. Our method outperforms existing resource allocation algorithms across four real-world systems, supported by statistical validation. The implementation source code is available for further development.

# Framework
Our proposed framework, SAG-ERC, addresses resource allocation challenges in interdependent systems. It leverages attack graphs by focusing on entry nodes (attackers’ access points) and critical asset nodes (defender’s targets). Using a Genetic Algorithm (GA), SAG-ERC identifies the most likely attack paths between entry and asset nodes, evaluating them based on potential asset losses and associated risks. Feature vectors for nodes are then generated using a random walk node embedding technique, enabling K-Means clustering to group nodes with similar properties. The severity of risk for each cluster is normalized to guide the defender's security budget allocation. SAG-ERC integrates TrustRank to rank nodes, prioritizing critical assets and ensuring proportional resource distribution across incoming edges. In summary, the framework strategically secures critical assets by optimizing resource allocation across the attack graph.

![Screenshot (20)](https://github.com/user-attachments/assets/809026b8-e47c-4185-8f9d-2347748ac9ce)



