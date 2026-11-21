---
title: "Graph"
layout: default
parent: Data Structures & Algorithms
nav_order: 9
has_children: true
permalink: /data-structures/Graph/graph/

topic: Graph
section: Non-Linear Data Structures
concepts: [vertices, edges, directed-graph, undirected-graph, weighted-graph, unweighted-graph]
representations: [adjacency-list, adjacency-matrix, edge-list]
algorithms: [bfs, dfs]
complexities: [O(V+E), O(V^2)]
tags: [data-structures, graph, graph-representation, java]
---

# Graphs in Data Structures and Algorithms

## 1. What Is a Graph?

A **Graph** is a non-linear data structure consisting of a collection of:

- **Vertices (Nodes):** Represent individual objects or entities.
- **Edges:** Represent connections or relationships between vertices.

A graph is commonly represented as:

\[
G = (V, E)
\]

Where:

- \(G\) = Graph
- \(V\) = Set of vertices
- \(E\) = Set of edges

### Real-World Examples

Think about these situations:

| Situation | Vertices (Nodes) | Edges (Connections) |
|---|---|---|
| Social network | People | Friendships |
| Google Maps | Cities or locations | Roads |
| Computer network | Computers | Network connections |
| Airline network | Airports | Flights |
| Family relationships | Family members | Relationships |
| Website navigation | Web pages | Links between pages |

For example, imagine five cities connected by roads.

- Patna is connected to Delhi.
- Patna is connected to Ranchi.
- Delhi is connected to Lucknow.
- Ranchi is connected to Kolkata.

Each city is a vertex, and each road is an edge.

A graph helps us understand and process these relationships efficiently.

## 2. Basic Graph Terminology

Before implementing graphs, understand these important terms.

### 2.1 Vertex (Node)

A vertex represents an individual object in a graph.

Example:

```text
A, B, C, D, E
```

Here, there are five vertices.

### 2.2 Edge

An edge represents a connection between two vertices.

Example:

```text
A ----- B
```

This represents an edge between A and B.

### 2.3 Adjacent Vertices

Two vertices are adjacent if an edge directly connects them.

Example:

```text
A ----- B ----- C
```

- A and B are adjacent.
- B and C are adjacent.
- A and C are not directly adjacent.

### 2.4 Degree of a Vertex

The degree of a vertex is the number of edges connected to it in an undirected graph.

Example:

```text
      B
      |
C --- A --- D
      |
      E
```

The degree of A is 4 because four edges are connected to it.

- Degree(A) = 4
- Degree(B) = 1
- Degree(C) = 1
- Degree(D) = 1
- Degree(E) = 1

For a directed graph, we distinguish between:

- **In-degree:** Number of incoming edges.
- **Out-degree:** Number of outgoing edges.

### 2.5 Path

A path is a sequence of vertices connected by edges.

Example:

```text
A ----- B ----- C ----- D
```

A → B → C → D is a path from A to D.

### 2.6 Cycle

A cycle is a path that starts and ends at the same vertex, with at least one edge.

Example:

```text
A ----- B
|       |
|       |
D ----- C
```

A → B → C → D → A forms a cycle.

### 2.7 Connected Graph

An undirected graph is connected if every vertex can be reached from every other vertex.

Example:

```text
A ----- B ----- C

D ----- E
```

This graph is disconnected because there is no path between the group containing A, B, C and the group containing D, E.

## 3. Types of Graphs

### 3.1 Undirected Graph

In an undirected graph, edges do not have a direction.

If A is connected to B, B is also connected to A.

Example:

```text
A ----- B
|       |
|       |
C ----- D
```

Real-world example: A mutual friendship network.

If two people are friends, the relationship is generally mutual.

Java representation:

```java
graph.get("A").add("B");
graph.get("B").add("A");
```

Both directions are stored.

### 3.2 Directed Graph (Digraph)

In a directed graph, every edge has a direction.

Example:

```text
A -----> B
|
|
v
C
```

- A → B means A has a directed connection to B.
- A → C means A has a directed connection to C.
- It does not automatically mean B → A or C → A.

Real-world example: Following someone on a social network.

You can follow another person without that person following you back.

Java representation:

```java
graph.get("A").add("B");
graph.get("A").add("C");
```

We do not automatically add reverse connections.

### 3.3 Weighted Graph

In a weighted graph, each edge has a value called a weight.

The weight may represent:

- Distance
- Cost
- Time
- Network latency
- Travel expenses

Example:

```text
       10
  A -------- B
  |          |
5 |          | 15
  |          |
  C -------- D
       20
```

Here:

- A to B has weight 10.
- A to C has weight 5.
- B to D has weight 15.
- C to D has weight 20.

Real-world example: Google Maps uses road distances and travel times to calculate routes.

### 3.4 Unweighted Graph

In an unweighted graph, edges do not have associated weights.

Example:

```text
A ----- B
|       |
|       |
C ----- D
```

Every connection is treated equally.

### 3.5 Cyclic and Acyclic Graphs

**Cyclic Graph:** Contains at least one cycle.

```text
A ----- B
|       |
D ----- C
```

**Acyclic Graph:** Contains no cycles.

```text
A ----- B ----- C
      |
      D
```

A connected, undirected, acyclic graph is called a **Tree**.

A directed acyclic graph is called a **DAG (Directed Acyclic Graph)**.

DAGs are useful in task scheduling, dependency management, and build systems.

## 4. Example Graph for Implementation

We will use the following undirected graph throughout this document.

```text
        A
       / \
      B   C
      |   |
      D---E
       \ /
        F
```

Vertices:

```text
A, B, C, D, E, F
```

Edges:

```text
A - B
A - C
B - D
C - E
D - E
D - F
E - F
```

There are:

- 6 vertices
- 7 undirected edges

Notice that an undirected edge is a two-way connection.

For example, A - B means both A is connected to B and B is connected to A.

## 5. Graph Representation

A computer needs a way to store graph vertices and their connections.

There are two commonly used representations:

1. Adjacency Matrix
2. Adjacency List

Both represent the same graph, but store its connections differently.

# Part 1: Adjacency Matrix

## 6. What Is an Adjacency Matrix?

An **Adjacency Matrix** is a two-dimensional array used to represent connections between vertices.

If a graph has `V` vertices, its adjacency matrix has:

```text
V × V
```

rows and columns.

Each cell indicates whether two vertices are connected.

For an unweighted graph:

- `1` means an edge exists.
- `0` means no edge exists.

### 6.1 Understanding the Matrix

Consider our example graph:

```text
        A
       / \
      B   C
      |   |
      D---E
       \ /
        F
```

Vertices:

```text
A, B, C, D, E, F
```

First, assign an index to every vertex.

| Vertex | Index |
|---|---:|
| A | 0 |
| B | 1 |
| C | 2 |
| D | 3 |
| E | 4 |
| F | 5 |

Now create a 6 × 6 matrix.

Each row represents a source vertex, and each column represents a destination vertex.

```text
     A B C D E F
   +------------
A  | 0 1 1 0 0 0
B  | 1 0 0 1 0 0
C  | 1 0 0 0 1 0
D  | 0 1 0 0 1 1
E  | 0 0 1 1 0 1
F  | 0 0 0 1 1 0
```

Let's understand how to read it.

- `matrix[0][1] = 1`: A is connected to B.
- `matrix[0][2] = 1`: A is connected to C.
- `matrix[0][3] = 0`: A is not directly connected to D.
- `matrix[3][5] = 1`: D is connected to F.

Because this is an undirected graph:

```java
matrix[0][1] = 1;
matrix[1][0] = 1;
```

Both positions must be updated.

### 6.2 Java Implementation of Adjacency Matrix

```java
public class GraphMatrix {

    private final int[][] matrix;
    private final int vertices;

    public GraphMatrix(int vertices) {
        this.vertices = vertices;
        this.matrix = new int[vertices][vertices];
    }

    public void addEdge(int source, int destination) {

        matrix[source][destination] = 1;

        // For an undirected graph, add the reverse edge.
        matrix[destination][source] = 1;
    }

    public void removeEdge(int source, int destination) {

        matrix[source][destination] = 0;

        // Remove the reverse edge too.
        matrix[destination][source] = 0;
    }

    public boolean hasEdge(int source, int destination) {
        return matrix[source][destination] == 1;
    }

    public void printGraph() {

        System.out.print("  ");

        for (int i = 0; i < vertices; i++) {
            System.out.print(i + " ");
        }

        System.out.println();

        for (int i = 0; i < vertices; i++) {

            System.out.print(i + " ");

            for (int j = 0; j < vertices; j++) {
                System.out.print(matrix[i][j] + " ");
            }

            System.out.println();
        }
    }

    public static void main(String[] args) {

        GraphMatrix graph = new GraphMatrix(6);

        graph.addEdge(0, 1);
        graph.addEdge(0, 2);
        graph.addEdge(1, 3);
        graph.addEdge(2, 4);
        graph.addEdge(3, 4);
        graph.addEdge(3, 5);
        graph.addEdge(4, 5);

        graph.printGraph();

        System.out.println(graph.hasEdge(0, 1)); // true
        System.out.println(graph.hasEdge(0, 3)); // false
    }
}
```

### 6.3 Output

```text
  0 1 2 3 4 5
0 0 1 1 0 0 0
1 1 0 0 1 0 0
2 1 0 0 0 1 0
3 0 1 0 0 1 1
4 0 0 1 1 0 1
5 0 0 0 1 1 0

true
false
```

### 6.4 Weighted Adjacency Matrix

For a weighted graph, instead of storing `1`, store the edge weight.

Example:

```text
       A    B    C
A      0   10    5
B     10    0   15
C      5   15    0
```

This represents:

- A to B: 10
- A to C: 5
- B to C: 15

For an undirected weighted graph, the matrix is symmetric.

**Important:** A weight of zero can be a valid edge weight. Therefore, using `0` to mean no edge is ambiguous in some applications. You can use `Integer.MAX_VALUE`, `Double.POSITIVE_INFINITY`, or a separate boolean matrix to represent missing edges.

## 7. Advantages and Disadvantages of Adjacency Matrix

### Advantages

1. Simple to understand and implement.
2. Checking whether an edge exists is very fast: O(1).
3. Adding an edge is O(1).
4. Useful for dense graphs, where many possible edges exist.
5. Convenient for mathematical graph operations.

### Disadvantages

1. Requires O(V²) memory even if there are very few edges.
2. Finding all neighbors of a vertex takes O(V).
3. Adding a new vertex generally requires creating a larger matrix and copying existing data.
4. Not memory-efficient for sparse graphs.

# Part 2: Adjacency List

## 8. What Is an Adjacency List?

An **Adjacency List** stores each vertex along with a list of all vertices directly connected to it.

Instead of allocating space for every possible connection, it stores only the connections that actually exist.

Consider the same graph:

```text
        A
       / \
      B   C
      |   |
      D---E
       \ /
        F
```

Its adjacency list looks like this:

```text
A → B → C

B → A → D

C → A → E

D → B → E → F

E → C → D → F

F → D → E
```

Each vertex has a list of its neighbors.

For example:

```text
A → B → C
```

means A is directly connected to B and C.

Because this is an undirected graph, every connection appears in both vertices' lists.

For example:

```text
A → B
B → A
```

### 8.1 Java Implementation Using HashMap and ArrayList

A common way to implement an adjacency list in Java is:

```java
Map<Integer, List<Integer>> graph = new HashMap<>();
```

Here:

- `Map` stores each vertex.
- `List` stores its neighboring vertices.
- `HashMap` provides convenient lookup by vertex.
- `ArrayList` stores the neighbors.

Example:

```java
import java.util.*;

public class GraphList {

    private final Map<Integer, List<Integer>> graph = new HashMap<>();

    // Add a vertex
    public void addVertex(int vertex) {
        graph.putIfAbsent(vertex, new ArrayList<>());
    }

    // Add an undirected edge
    public void addEdge(int source, int destination) {

        addVertex(source);
        addVertex(destination);

        graph.get(source).add(destination);
        graph.get(destination).add(source);
    }

    // Remove an undirected edge
    public void removeEdge(int source, int destination) {

        if (graph.containsKey(source)) {
            graph.get(source).remove(Integer.valueOf(destination));
        }

        if (graph.containsKey(destination)) {
            graph.get(destination).remove(Integer.valueOf(source));
        }
    }

    // Check whether an edge exists
    public boolean hasEdge(int source, int destination) {

        return graph.containsKey(source)
                && graph.get(source).contains(destination);
    }

    // Display graph
    public void printGraph() {

        for (Map.Entry<Integer, List<Integer>> entry : graph.entrySet()) {

            System.out.println(
                    entry.getKey() + " -> " + entry.getValue()
            );
        }
    }

    public static void main(String[] args) {

        GraphList graph = new GraphList();

        graph.addEdge(0, 1);
        graph.addEdge(0, 2);
        graph.addEdge(1, 3);
        graph.addEdge(2, 4);
        graph.addEdge(3, 4);
        graph.addEdge(3, 5);
        graph.addEdge(4, 5);

        graph.printGraph();

        System.out.println(graph.hasEdge(0, 1)); // true
        System.out.println(graph.hasEdge(0, 3)); // false
    }
}
```

### 8.2 Output

The order of vertices printed by a HashMap is not guaranteed.

One possible output:

```text
0 -> [1, 2]
1 -> [0, 3]
2 -> [0, 4]
3 -> [1, 4, 5]
4 -> [2, 3, 5]
5 -> [3, 4]

true
false
```

### 8.3 Adjacency List Using Array of Lists

If vertices are numbered from `0` to `V - 1`, an array of lists is another common implementation.

```java
import java.util.*;

public class GraphArrayList {

    private final List<Integer>[] graph;

    @SuppressWarnings("unchecked")
    public GraphArrayList(int vertices) {

        graph = new ArrayList[vertices];

        for (int i = 0; i < vertices; i++) {
            graph[i] = new ArrayList<>();
        }
    }

    public void addEdge(int source, int destination) {

        graph[source].add(destination);
        graph[destination].add(source);
    }

    public void printGraph() {

        for (int i = 0; i < graph.length; i++) {

            System.out.println(i + " -> " + graph[i]);
        }
    }

    public static void main(String[] args) {

        GraphArrayList graph = new GraphArrayList(6);

        graph.addEdge(0, 1);
        graph.addEdge(0, 2);
        graph.addEdge(1, 3);
        graph.addEdge(2, 4);
        graph.addEdge(3, 4);
        graph.addEdge(3, 5);
        graph.addEdge(4, 5);

        graph.printGraph();
    }
}
```

**When should you use which implementation?**

- Use `Map<Integer, List<Integer>>` when vertices are dynamic or represented by strings, IDs, or other objects.
- Use `List<Integer>[]` when vertices are numbered consecutively from `0` to `V - 1`. This is common in competitive programming and DSA problems.

## 9. Weighted Adjacency List

For a weighted graph, each neighbor must also store its edge weight.

One simple approach is to create a small class representing an edge.

```java
import java.util.*;

class Edge {

    int destination;
    int weight;

    Edge(int destination, int weight) {
        this.destination = destination;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return destination + "(" + weight + ")";
    }
}

public class WeightedGraph {

    private final Map<Integer, List<Edge>> graph = new HashMap<>();

    public void addEdge(int source, int destination, int weight) {

        graph.putIfAbsent(source, new ArrayList<>());
        graph.putIfAbsent(destination, new ArrayList<>());

        graph.get(source).add(new Edge(destination, weight));
        graph.get(destination).add(new Edge(source, weight));
    }

    public void printGraph() {

        for (Map.Entry<Integer, List<Edge>> entry : graph.entrySet()) {

            System.out.println(
                    entry.getKey() + " -> " + entry.getValue()
            );
        }
    }

    public static void main(String[] args) {

        WeightedGraph graph = new WeightedGraph();

        graph.addEdge(0, 1, 10);
        graph.addEdge(0, 2, 5);
        graph.addEdge(1, 2, 15);

        graph.printGraph();
    }
}
```

Possible output:

```text
0 -> [1(10), 2(5)]
1 -> [0(10), 2(15)]
2 -> [0(5), 1(15)]
```

For directed weighted graphs, add the edge only to the source vertex's list. Do not automatically add the reverse edge.

# Part 3: Adjacency Matrix vs Adjacency List

## 10. Key Differences

| Feature | Adjacency Matrix | Adjacency List |
|---|---|---|
| Representation | 2D array | Array of lists or map of lists |
| Space complexity | O(V²) | O(V + E) |
| Check whether edge exists | O(1) | O(degree of vertex) |
| Add an edge | O(1) | O(1) amortized |
| Remove an edge | O(1) | O(degree of vertex) |
| Find all neighbors | O(V) | O(degree of vertex) |
| Add a vertex | Usually O(V²) to resize/copy | Usually O(1) average with HashMap |
| Best suited for | Dense graphs | Sparse graphs |

Here:

- V = Number of vertices
- E = Number of edges
- Degree = Number of neighbors of a vertex

For an adjacency list using a HashMap, the stated average complexities assume normal HashMap behavior. Removing a neighbor from an ArrayList requires searching and shifting elements.

## 11. Sparse Graph vs Dense Graph

Understanding sparse and dense graphs helps you choose the right representation.

### Sparse Graph

A sparse graph has relatively few edges compared to the maximum possible number of edges.

Example:

```text
A ----- B

C ----- D

E ----- F
```

There are many vertices but very few connections.

For a simple undirected graph, the maximum number of edges is:

\[
E_{\max} = \frac{V(V-1)}{2}
\]

An adjacency list is generally more memory-efficient for sparse graphs.

### Dense Graph

A dense graph has many edges, with a large proportion of possible vertex pairs connected.

Example:

```text
A ----- B
|\     /|
| \   / |
|  \ /  |
|  / \  |
| /   \ |
|/     \|
C ----- D
```

An adjacency matrix can be useful for dense graphs because it offers constant-time edge lookup.

## 12. Complexity Comparison with an Example

Suppose a graph contains:

- 1,000 vertices
- 2,000 undirected edges

### Adjacency Matrix

Required cells:

\[
1000 \times 1000 = 1,000,000
\]

It stores one million matrix entries, even though only 2,000 edges exist.

### Adjacency List

For an undirected graph, every edge is stored twice.

Total neighbor entries:

\[
2 \times 2000 = 4000
\]

The adjacency list stores approximately:

- 1,000 vertex-list references
- 4,000 neighbor entries

This is significantly more memory-efficient for this sparse graph.

# Part 4: Directed Graph Implementation

## 13. Adjacency List for a Directed Graph

Consider:

```text
A -----> B
|
|
v
C -----> D
```

The adjacency list is:

```text
A → B, C
B → []
C → D
D → []
```

Java implementation:

```java
import java.util.*;

public class DirectedGraph {

    private final Map<String, List<String>> graph = new HashMap<>();

    public void addVertex(String vertex) {
        graph.putIfAbsent(vertex, new ArrayList<>());
    }

    public void addEdge(String source, String destination) {

        addVertex(source);
        addVertex(destination);

        // Only one direction
        graph.get(source).add(destination);
    }

    public void printGraph() {

        for (Map.Entry<String, List<String>> entry : graph.entrySet()) {

            System.out.println(
                    entry.getKey() + " -> " + entry.getValue()
            );
        }
    }

    public static void main(String[] args) {

        DirectedGraph graph = new DirectedGraph();

        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.addEdge("C", "D");

        graph.printGraph();
    }
}
```

Possible output:

```text
A -> [B, C]
B -> []
C -> [D]
D -> []
```

Notice that B does not automatically contain A, because the graph is directed.

# Part 5: Important Graph Concepts for Interviews

## 14. How Many Edges Can a Graph Have?

For a simple undirected graph with V vertices and no self-loops or parallel edges:

\[
E_{\max} = \frac{V(V-1)}{2}
\]

For a simple directed graph without self-loops:

\[
E_{\max} = V(V-1)
\]

A self-loop is an edge that connects a vertex to itself.

## 15. Why Is an Undirected Adjacency Matrix Symmetric?

In an undirected graph, if A is connected to B, B is also connected to A.

Therefore:

\[
matrix[i][j] = matrix[j][i]
\]

This means the matrix is symmetric along its main diagonal.

## 16. Why Is an Undirected Edge Stored Twice in an Adjacency List?

Consider:

```text
A ----- B
```

A's neighbor list contains B:

```text
A → B
```

B's neighbor list contains A:

```text
B → A
```

This makes it easy to find all neighbors of either vertex.

An undirected graph with E edges therefore has exactly 2E adjacency-list entries, assuming no duplicate edges or self-loops.

## 17. What Is a Self-Loop?

A self-loop is an edge connecting a vertex to itself.

Example:

```text
A ----- B
|
|
A
```

In an adjacency matrix, a self-loop is represented by:

```java
matrix[0][0] = 1;
```

In an adjacency list, it is represented by the vertex appearing in its own neighbor list.

## 18. What Is a Multigraph?

A multigraph allows multiple edges between the same pair of vertices.

Example:

```text
A ===== B
```

There may be two or more distinct connections between A and B.

A normal boolean-style adjacency matrix cannot distinguish multiple edges. You can store the number of edges or use a richer representation.

An adjacency list can store repeated neighbors or separate edge objects.

## 19. Which Representation Should You Choose?

| Situation | Recommended Representation |
|---|---|
| Social network with many users and relatively few friendships | Adjacency List |
| Road network with relatively few roads compared to all possible pairs | Adjacency List |
| Dense graph with many connections | Adjacency Matrix |
| Need frequent edge-existence checks | Adjacency Matrix |
| Need to iterate through neighbors frequently | Adjacency List |
| Weighted graph with many possible connections | Depends on density and operations |
| Dynamic vertices identified by strings | Map-based Adjacency List |

**General rule:** Adjacency lists are the default choice for most graph traversal and problem-solving questions. Use an adjacency matrix when the graph is dense or constant-time edge lookup is especially important.

# Part 6: Interview Questions and Answers

## Q1. What is a graph?

A graph is a non-linear data structure consisting of vertices and edges. Vertices represent entities, and edges represent relationships or connections between them.

## Q2. What is the difference between a tree and a graph?

A tree is a special type of graph that is connected and contains no cycles.

A general graph can be disconnected and may contain cycles.

For a tree with V vertices:

\[
E = V - 1
\]

## Q3. What are the two common graph representations?

1. Adjacency Matrix
2. Adjacency List

An adjacency matrix uses a two-dimensional array, while an adjacency list stores the neighbors of each vertex.

## Q4. Which representation is more memory-efficient for sparse graphs?

Adjacency List.

Its space complexity is O(V + E), compared to O(V²) for an adjacency matrix.

## Q5. Which representation provides O(1) edge lookup?

Adjacency Matrix.

You can check:

```java
matrix[source][destination] == 1
```

in constant time.

## Q6. Why do we add an edge twice in an undirected adjacency list?

Because the connection works in both directions.

For A - B:

```java
graph.get("A").add("B");
graph.get("B").add("A");
```

## Q7. What is the difference between a directed and undirected graph?

A directed graph has edges with direction, while an undirected graph has edges without direction.

A → B does not imply B → A in a directed graph.

## Q8. What is a weighted graph?

A graph in which edges have associated values, such as distance, cost, or time.

## Q9. What is the space complexity of an adjacency matrix?

O(V²), because it stores V × V entries.

## Q10. What is the space complexity of an adjacency list?

O(V + E), because it stores the vertices and their adjacency entries.

For an undirected graph, the adjacency entries total 2E, which is still O(V + E).

## Q11. Can an adjacency list represent a directed graph?

Yes. Store each outgoing neighbor in the source vertex's list. Do not automatically add the reverse connection.

## Q12. Can an adjacency matrix represent a weighted graph?

Yes. Store the weight instead of 1 for an existing edge, and use a suitable sentinel value to indicate no edge.

## Q13. What is the degree of a vertex?

In an undirected graph, it is the number of edges connected to that vertex.

For a directed graph, we use in-degree and out-degree.

## Q14. What is a DAG?

A DAG is a Directed Acyclic Graph. It is a directed graph that contains no directed cycles.

It is useful for task scheduling, dependency resolution, and build systems.

## Q15. What is the maximum number of edges in a simple undirected graph?

For V vertices:

\[
\frac{V(V-1)}{2}
\]

This assumes there are no self-loops or parallel edges.

# Part 7: Practice Problems

Try these problems after understanding both representations.

### Beginner Level

1. Create an undirected graph using an adjacency list.
2. Create an undirected graph using an adjacency matrix.
3. Print all neighbors of a given vertex.
4. Check whether an edge exists between two vertices.
5. Add and remove edges from a graph.
6. Count the number of vertices and edges.
7. Calculate the degree of every vertex.

### Intermediate Level

1. Implement a directed graph using an adjacency list.
2. Implement a weighted graph.
3. Count the in-degree and out-degree of every vertex.
4. Detect whether a graph contains self-loops.
5. Check whether an undirected graph is connected.
6. Convert an adjacency matrix into an adjacency list.
7. Convert an adjacency list into an adjacency matrix.
8. Detect duplicate edges.

### Challenge Level

1. Detect a cycle in an undirected graph.
2. Detect a cycle in a directed graph.
3. Find all connected components.
4. Find the shortest path in an unweighted graph.
5. Find the shortest path in a weighted graph.
6. Implement graph traversal using BFS.
7. Implement graph traversal using DFS.

# Part 8: Quick Revision Cheat Sheet

| Concept | Meaning |
|---|---|
| Graph | Collection of vertices and edges |
| Vertex | Node or entity |
| Edge | Connection between vertices |
| Directed Graph | Edges have directions |
| Undirected Graph | Edges have no directions |
| Weighted Graph | Edges contain weights |
| Degree | Number of connected edges in an undirected graph |
| In-degree | Number of incoming directed edges |
| Out-degree | Number of outgoing directed edges |
| Path | Sequence of connected vertices |
| Cycle | Path that returns to its starting vertex |
| DAG | Directed Acyclic Graph |
| Adjacency Matrix | Two-dimensional array |
| Adjacency List | List of neighbors for each vertex |
| Sparse Graph | Relatively few edges |
| Dense Graph | Relatively many edges |

## Complexity Summary

| Operation | Adjacency Matrix | Adjacency List |
|---|---:|---:|
| Space | O(V²) | O(V + E) |
| Check edge | O(1) | O(degree) |
| Add edge | O(1) | O(1) amortized |
| Remove edge | O(1) | O(degree) |
| Iterate neighbors | O(V) | O(degree) |

## Important Formulas

Maximum edges in a simple undirected graph:

\[
E_{\max} = \frac{V(V-1)}{2}
\]

Maximum edges in a simple directed graph without self-loops:

\[
E_{\max} = V(V-1)
\]

Space complexity:

- Adjacency Matrix: O(V²)
- Adjacency List: O(V + E)

---

## What Should You Learn Next?

Once you understand graph representation, the next important topic is **Graph Traversal**.

Learn these in order:

1. Breadth-First Search (BFS)
2. Depth-First Search (DFS)
3. Connected Components
4. Cycle Detection
5. Topological Sorting
6. Shortest Path Algorithms
7. Minimum Spanning Tree

These algorithms build directly on the adjacency list and adjacency matrix implementations introduced here.