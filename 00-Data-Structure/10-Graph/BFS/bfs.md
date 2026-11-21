---
title: "Breadth-First Search (BFS)"
layout: default
parent: Graph
grand_parent: Data Structures & Algorithms
nav_order: 1
permalink: /data-structures/Graph/BFS/bfs/

topic: Breadth-First Search
section: Graph Traversal
concepts: [level-order-traversal, visited-set, queue]
data_structures: [graph, queue, hashset]
complexities: [O(V+E)]
tags: [algorithms, graph, bfs, traversal, java]
---


# Breadth-First Search (BFS)

## 1. What Is Breadth-First Search?

**Breadth-First Search (BFS)** is a graph traversal algorithm used to visit all reachable vertices of a graph, exploring them level by level.

BFS starts from a selected vertex and visits:

1. The starting vertex.
2. All its immediate neighbors.
3. All neighbors of those vertices.
4. The next level of vertices.
5. Continues until all reachable vertices have been visited.

BFS uses a **Queue (FIFO — First In, First Out)** to decide which vertex to visit next.

### Real-World Examples

Think about these situations:

**Example 1: Finding people in a social network**

Suppose you want to find people connected to you on a social network.

- Level 0: You.
- Level 1: Your direct friends.
- Level 2: Friends of your friends.
- Level 3: Friends of those people.

BFS explores these levels in order.

**Example 2: Searching for a nearby hospital**

Imagine you are looking for the nearest hospital in a road network where every road has equal cost.

BFS explores nearby locations first, then locations farther away.

**Example 3: Spreading information**

Imagine a school announces an important message.

- First, the principal informs teachers.
- Teachers inform their students.
- Students inform their classmates.

This spreading process can be represented using BFS.

## 2. Why Is BFS Needed?

Suppose you have a graph with many connected vertices.

You want to:

- Visit every reachable vertex.
- Find the shortest path in an unweighted graph.
- Find the minimum number of edges between two vertices.
- Identify connected components.
- Check whether a graph is bipartite.
- Explore a network level by level.

BFS helps solve these problems efficiently.

## 3. Understanding BFS with an Example

Consider this undirected graph:

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

We will perform BFS starting from vertex A.

### 3.1 BFS Traversal Order

Start from A.

**Step 1: Visit A**

A is the starting vertex.

Its neighbors are B and C.

```text
Queue: [B, C]
Visited: [A, B, C]
```

**Step 2: Visit B**

Remove B from the queue.

B's neighbors are A and D.

- A is already visited.
- D is not visited, so add D.

```text
Queue: [C, D]
Visited: [A, B, C, D]
```

**Step 3: Visit C**

Remove C from the queue.

C's neighbors are A and E.

- A is already visited.
- E is not visited, so add E.

```text
Queue: [D, E]
Visited: [A, B, C, D, E]
```

**Step 4: Visit D**

Remove D from the queue.

D's neighbors are B, E, and F.

- B is already visited.
- E is already visited.
- F is not visited, so add F.

```text
Queue: [E, F]
Visited: [A, B, C, D, E, F]
```

**Step 5: Visit E**

Remove E from the queue.

Its neighbors are C, D, and F.

All are already visited.

```text
Queue: [F]
Visited: [A, B, C, D, E, F]
```

**Step 6: Visit F**

Remove F from the queue.

Its neighbors D and E are already visited.

```text
Queue: []
Visited: [A, B, C, D, E, F]
```

The queue is empty, so BFS is complete.

### Final BFS Traversal

```text
A → B → C → D → E → F
```

**Important:** The exact traversal order can vary depending on the order in which neighbors are stored.

BFS guarantees level-by-level traversal, not one universal ordering among vertices at the same level.

## 4. Understanding BFS Levels

BFS explores vertices according to their distance from the starting vertex, where distance means the minimum number of edges.

Starting from A:

```text
Level 0:          A

Level 1:       B     C

Level 2:       D     E

Level 3:          F
```

| Level | Vertices | Distance from A |
|---|---|---:|
| 0 | A | 0 |
| 1 | B, C | 1 |
| 2 | D, E | 2 |
| 3 | F | 3 |

For example:

- Distance(A, A) = 0
- Distance(A, B) = 1
- Distance(A, D) = 2
- Distance(A, F) = 3

This is why BFS is useful for finding the shortest path in an unweighted graph.

## 5. How Does BFS Work Internally?

BFS primarily uses two data structures:

### 5.1 Queue

A Queue follows FIFO:

**First In, First Out.**

The vertex inserted first is processed first.

For example:

```text
Queue: [A, B, C]

Remove from front:

A

Remaining queue:

[B, C]
```

### 5.2 Visited Set

A visited array or set keeps track of vertices that have already been discovered.

Why is it necessary?

Consider a cyclic graph:

```text
A ----- B
|       |
|       |
D ----- C
```

Without a visited structure, BFS could repeatedly visit the same vertices.

For example:

- A discovers B and D.
- B can discover C.
- C can discover D again.
- D can discover A again.

This can cause repeated processing or an infinite loop.

Therefore, mark a vertex as visited **when it is added to the queue**, not when it is removed.

This prevents the same vertex from being inserted multiple times.

## 6. BFS Algorithm — Step by Step

The general algorithm is:

1. Select a starting vertex.
2. Create an empty queue.
3. Create a visited structure.
4. Mark the starting vertex as visited.
5. Add the starting vertex to the queue.
6. While the queue is not empty:
   - Remove the front vertex.
   - Process or print that vertex.
   - Visit all its unvisited neighbors.
   - Mark each neighbor as visited.
   - Add each neighbor to the queue.
7. Stop when the queue becomes empty.

### Pseudocode

```text
BFS(graph, start):

    create empty queue
    create visited set

    visited.add(start)
    queue.add(start)

    while queue is not empty:

        current = queue.remove()

        process(current)

        for each neighbor of current:

            if neighbor is not visited:

                visited.add(neighbor)
                queue.add(neighbor)
```

## 7. Java Implementation Using Adjacency List

We will use the adjacency-list representation introduced in the Graph Introduction document.

### 7.1 Complete Java Code

```java
import java.util.*;

public class BFSGraph {

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

    // Breadth-First Search
    public void bfs(int start) {

        if (!graph.containsKey(start)) {
            System.out.println("Starting vertex does not exist.");
            return;
        }

        // Track visited vertices
        Set<Integer> visited = new HashSet<>();

        // Queue for BFS
        Queue<Integer> queue = new LinkedList<>();

        // Mark start as visited
        visited.add(start);

        // Add start to queue
        queue.offer(start);

        while (!queue.isEmpty()) {

            // Remove the first vertex
            int current = queue.poll();

            // Process current vertex
            System.out.print(current + " ");

            // Visit all neighbors
            for (int neighbor : graph.get(current)) {

                if (!visited.contains(neighbor)) {

                    // Mark visited before adding to queue
                    visited.add(neighbor);

                    queue.offer(neighbor);
                }
            }
        }
    }

    public static void main(String[] args) {

        BFSGraph graph = new BFSGraph();

        graph.addEdge(0, 1);
        graph.addEdge(0, 2);
        graph.addEdge(1, 3);
        graph.addEdge(2, 4);
        graph.addEdge(3, 4);
        graph.addEdge(3, 5);
        graph.addEdge(4, 5);

        System.out.println("BFS Traversal:");

        graph.bfs(0);
    }
}
```

### 7.2 Output

```text
BFS Traversal:
0 1 2 3 4 5
```

### 7.3 Important Lines Explained

**Creating a Queue**

```java
Queue<Integer> queue = new LinkedList<>();
```

The Queue stores vertices waiting to be processed.

You can also use `ArrayDeque`, which is generally preferred for ordinary single-threaded queue operations:

```java
Queue<Integer> queue = new ArrayDeque<>();
```

**Marking the Starting Vertex**

```java
visited.add(start);
queue.offer(start);
```

The starting vertex is marked visited before being inserted.

**Removing the Next Vertex**

```java
int current = queue.poll();
```

`poll()` removes and returns the front element.

**Processing Neighbors**

```java
for (int neighbor : graph.get(current)) {
```

This loop checks all vertices directly connected to the current vertex.

**Avoiding Repeated Visits**

```java
if (!visited.contains(neighbor)) {

    visited.add(neighbor);
    queue.offer(neighbor);
}
```

The neighbor is marked visited before being inserted into the queue.

This is one of the most important details in BFS.

## 8. BFS Using an Adjacency Matrix

BFS can also be implemented using an adjacency matrix.

The main difference is how we find neighbors.

With an adjacency list:

```java
for (int neighbor : graph.get(current))
```

With an adjacency matrix:

```java
for (int neighbor = 0; neighbor < vertices; neighbor++) {

    if (matrix[current][neighbor] == 1) {
        // Neighbor exists
    }
}
```

### Java Implementation

```java
import java.util.*;

public class BFSMatrix {

    private final int[][] matrix;
    private final int vertices;

    public BFSMatrix(int vertices) {
        this.vertices = vertices;
        this.matrix = new int[vertices][vertices];
    }

    public void addEdge(int source, int destination) {

        matrix[source][destination] = 1;
        matrix[destination][source] = 1;
    }

    public void bfs(int start) {

        boolean[] visited = new boolean[vertices];

        Queue<Integer> queue = new ArrayDeque<>();

        visited[start] = true;
        queue.offer(start);

        while (!queue.isEmpty()) {

            int current = queue.poll();

            System.out.print(current + " ");

            for (int neighbor = 0; neighbor < vertices; neighbor++) {

                if (matrix[current][neighbor] == 1
                        && !visited[neighbor]) {

                    visited[neighbor] = true;
                    queue.offer(neighbor);
                }
            }
        }
    }

    public static void main(String[] args) {

        BFSMatrix graph = new BFSMatrix(6);

        graph.addEdge(0, 1);
        graph.addEdge(0, 2);
        graph.addEdge(1, 3);
        graph.addEdge(2, 4);
        graph.addEdge(3, 4);
        graph.addEdge(3, 5);
        graph.addEdge(4, 5);

        System.out.println("BFS Traversal:");

        graph.bfs(0);
    }
}
```

Output:

```text
BFS Traversal:
0 1 2 3 4 5
```

## 9. BFS Complexity Analysis

The time complexity depends on the graph representation.

### 9.1 Adjacency List

\[
O(V + E)
\]

Where:

- V = Number of vertices.
- E = Number of edges.

Why?

- Each vertex is added to the queue at most once.
- Each vertex is removed from the queue at most once.
- Each adjacency-list entry is examined once.

For an undirected graph, every edge appears twice in the adjacency lists, but this is still O(V + E).

Space complexity:

\[
O(V)
\]

The visited structure and queue can each store up to V vertices.

### 9.2 Adjacency Matrix

\[
O(V^2)
\]

Why?

For every vertex processed, BFS scans an entire row of V matrix entries.

Across V vertices, this results in V × V checks.

Space complexity:

\[
O(V^2)
\]

The matrix itself requires O(V²) space. The BFS auxiliary space for the queue and visited array is O(V).

### Complexity Comparison

| Operation | Adjacency List | Adjacency Matrix |
|---|---:|---:|
| BFS Time | O(V + E) | O(V²) |
| Graph Storage | O(V + E) | O(V²) |
| BFS Auxiliary Space | O(V) | O(V) |

**Interview tip:** When asked for BFS complexity, always mention the graph representation.

## 10. BFS for a Disconnected Graph

The BFS implementation above visits only vertices reachable from the starting vertex.

Consider:

```text
A ----- B ----- C

D ----- E

F
```

There are three disconnected components:

- Component 1: A, B, C
- Component 2: D, E
- Component 3: F

If you start BFS from A, it visits only:

```text
A → B → C
```

What if you want to visit every vertex, including disconnected components?

Run BFS from every vertex that has not yet been visited.

### Java Implementation

```java
public void bfsAllComponents() {

    Set<Integer> visited = new HashSet<>();

    for (int vertex : graph.keySet()) {

        if (!visited.contains(vertex)) {

            bfsComponent(vertex, visited);
        }
    }
}

private void bfsComponent(int start, Set<Integer> visited) {

    Queue<Integer> queue = new ArrayDeque<>();

    visited.add(start);
    queue.offer(start);

    while (!queue.isEmpty()) {

        int current = queue.poll();

        System.out.print(current + " ");

        for (int neighbor : graph.get(current)) {

            if (!visited.contains(neighbor)) {

                visited.add(neighbor);
                queue.offer(neighbor);
            }
        }
    }
}
```

The complete traversal has O(V + E) time complexity with an adjacency list.

This approach is also useful for counting connected components.

## 11. BFS for Finding the Shortest Path

One of the most important applications of BFS is finding the shortest path in an **unweighted graph**.

Why?

Because BFS visits vertices level by level.

The first time BFS reaches a vertex, it has found a path using the minimum number of edges.

Consider:

```text
        A
       / \
      B   C
      |   |
      D   E
       \ /
        F
```

Suppose you want the shortest path from A to F.

Possible paths include:

```text
A → B → D → F

A → C → E → F
```

Both paths contain 3 edges.

BFS discovers F at distance 3 from A.

### 11.1 Store Distance and Parent

To reconstruct the actual shortest path, maintain:

- `distance[]`: Minimum number of edges from the source.
- `parent[]`: Previous vertex on the discovered shortest path.

### Java Implementation

```java
import java.util.*;

public class BFSShortestPath {

    public static void shortestPath(
            List<Integer>[] graph,
            int source,
            int destination) {

        int vertices = graph.length;

        int[] distance = new int[vertices];
        int[] parent = new int[vertices];

        Arrays.fill(distance, -1);
        Arrays.fill(parent, -1);

        Queue<Integer> queue = new ArrayDeque<>();

        distance[source] = 0;
        queue.offer(source);

        while (!queue.isEmpty()) {

            int current = queue.poll();

            if (current == destination) {
                break;
            }

            for (int neighbor : graph[current]) {

                if (distance[neighbor] == -1) {

                    distance[neighbor] = distance[current] + 1;

                    parent[neighbor] = current;

                    queue.offer(neighbor);
                }
            }
        }

        if (distance[destination] == -1) {

            System.out.println("No path exists.");
            return;
        }

        List<Integer> path = new ArrayList<>();

        for (int vertex = destination;
             vertex != -1;
             vertex = parent[vertex]) {

            path.add(vertex);
        }

        Collections.reverse(path);

        System.out.println("Shortest Path: " + path);

        System.out.println(
                "Distance: " + distance[destination]
        );
    }
}
```

This method assumes `graph` is an adjacency list and that all vertices use valid integer indices.

Example output:

```text
Shortest Path: [0, 1, 3, 5]
Distance: 3
```

There may be multiple shortest paths. The one returned depends on neighbor insertion order.

**Important:** BFS finds shortest paths by number of edges in an unweighted graph. It does not generally find minimum total weight in a weighted graph.

For non-negative weighted edges, Dijkstra's algorithm is commonly used.

## 12. Important Applications of BFS

| Application | How BFS Helps |
|---|---|
| Shortest path in unweighted graphs | Finds minimum number of edges |
| Social networks | Finds people within a given connection level |
| Web crawling | Explores links level by level |
| Network broadcasting | Models spreading information |
| Connected components | Finds separate groups of vertices |
| Bipartite graph checking | Uses two-coloring by levels |
| Maze solving | Finds shortest path when each move has equal cost |
| Tree level-order traversal | Visits nodes level by level |

## 13. Common BFS Mistakes

### Mistake 1: Forgetting the Visited Structure

Incorrect:

```java
queue.offer(neighbor);
```

Without checking whether the neighbor has already been visited, vertices can be inserted repeatedly.

Correct:

```java
if (!visited.contains(neighbor)) {
    visited.add(neighbor);
    queue.offer(neighbor);
}
```

### Mistake 2: Marking Visited Only When Removing from Queue

This can insert the same vertex into the queue multiple times.

Prefer marking visited when adding it to the queue.

### Mistake 3: Using Stack Instead of Queue

BFS requires FIFO behavior.

A Stack uses LIFO behavior, which leads toward DFS-style traversal.

### Mistake 4: Assuming BFS Works for Every Weighted Shortest-Path Problem

BFS finds shortest paths by number of edges, not by total weight.

Use an appropriate weighted shortest-path algorithm when edges have different costs.

### Mistake 5: Ignoring Disconnected Components

A single BFS run only visits vertices reachable from its starting vertex.

Use an outer loop to cover the entire graph.

### Mistake 6: Confusing Traversal Order with Shortest-Path Order

BFS guarantees minimum distance by edge count, but the order of vertices at the same level can vary.

## 14. BFS vs DFS

BFS and DFS are the two fundamental graph traversal algorithms.

| Feature | BFS | DFS |
|---|---|---|
| Full Form | Breadth-First Search | Depth-First Search |
| Traversal | Level by level | Goes deep before backtracking |
| Data Structure | Queue | Stack or recursion |
| Shortest path in unweighted graph | Yes | Not guaranteed |
| Memory | Can be high for wide graphs | Can be high for deep graphs |
| Time with adjacency list | O(V + E) | O(V + E) |
| Common uses | Shortest paths, levels | Cycle detection, topological sorting, connectivity |

### Simple Way to Remember

- **BFS:** Explore nearby vertices first.
- **DFS:** Explore one path as far as possible before backtracking.

## 15. Interview Questions and Answers

### Q1. What is BFS?

Breadth-First Search is a graph traversal algorithm that explores vertices level by level, starting from a selected vertex.

It uses a Queue to maintain the order of exploration.

### Q2. Which data structure is used in BFS?

Queue.

BFS follows FIFO (First In, First Out).

### Q3. Why is a visited array required?

It prevents repeated visits to vertices, especially when the graph contains cycles.

### Q4. What is the time complexity of BFS?

With an adjacency list: O(V + E).

With an adjacency matrix: O(V²).

### Q5. What is the space complexity of BFS?

O(V) auxiliary space for the queue and visited structure.

### Q6. Does BFS find the shortest path?

Yes, in an unweighted graph, BFS finds a shortest path measured by the number of edges.

### Q7. Can BFS work on a directed graph?

Yes. It follows only the outgoing edges from each vertex.

### Q8. Can BFS work on a disconnected graph?

Yes, but one BFS run only visits the component containing the starting vertex.

To visit every component, run BFS from each unvisited vertex.

### Q9. Why do we mark a vertex as visited when adding it to the queue?

To prevent the same vertex from being added multiple times before it is processed.

### Q10. Can BFS be implemented using recursion?

It can be implemented recursively with additional queue management, but the standard and simplest implementation is iterative using a Queue.

### Q11. Is BFS suitable for weighted graphs?

It can traverse weighted graphs, but it does not generally find the minimum-weight path. Dijkstra's algorithm is suitable for non-negative edge weights.

### Q12. What is the difference between BFS and level-order traversal?

Level-order traversal is BFS applied specifically to a tree. BFS is the more general graph traversal algorithm.

### Q13. Can BFS detect a cycle?

Yes. Cycle detection can be implemented using BFS with parent tracking for undirected graphs, or with other state-tracking techniques for directed graphs.

### Q14. What happens if the graph has no edges?

BFS visits only the starting vertex, assuming that vertex exists.

For a complete traversal, the outer loop visits every isolated vertex separately.

### Q15. Why is BFS O(V + E) with an adjacency list?

Every vertex is processed at most once, and every adjacency-list entry is examined at most once.

## 16. Practice Problems

### Beginner Level

1. Implement BFS using an adjacency list.
2. Implement BFS using an adjacency matrix.
3. Print BFS traversal starting from a given vertex.
4. Count the number of vertices reachable from a starting vertex.
5. Print all vertices at a particular BFS level.
6. Find the degree of each vertex.

### Intermediate Level

1. Find the shortest path in an unweighted graph.
2. Find the minimum number of edges between two vertices.
3. Count connected components using BFS.
4. Check whether an undirected graph is connected.
5. Check whether a graph is bipartite using BFS.
6. Find the shortest path in a binary maze.
7. Find all vertices within distance K of a source.

### Challenge Level

1. Word Ladder.
2. Rotting Oranges.
3. 01 Matrix.
4. Shortest Path in Binary Matrix.
5. Open the Lock.
6. Minimum Genetic Mutation.
7. Snakes and Ladders.

These are common graph problems on coding platforms such as LeetCode.

## 17. Quick Revision Cheat Sheet

| Topic | Key Point |
|---|---|
| Algorithm | Breadth-First Search |
| Category | Graph Traversal |
| Data Structure | Queue |
| Queue Principle | FIFO |
| Tracking | Visited array or set |
| Traversal | Level by level |
| Shortest Path | Unweighted graphs |
| Adjacency List Time | O(V + E) |
| Adjacency Matrix Time | O(V²) |
| Auxiliary Space | O(V) |
| Cycles | Visited tracking prevents repeated traversal |
| Disconnected Graph | Run BFS from every unvisited vertex |

### BFS Template to Remember

```java
public void bfs(int start) {

    boolean[] visited = new boolean[vertices];

    Queue<Integer> queue = new ArrayDeque<>();

    visited[start] = true;
    queue.offer(start);

    while (!queue.isEmpty()) {

        int current = queue.poll();

        System.out.print(current + " ");

        for (int neighbor : graph[current]) {

            if (!visited[neighbor]) {

                visited[neighbor] = true;

                queue.offer(neighbor);
            }
        }
    }
}
```

**Remember these three things:**

1. BFS uses a Queue.
2. Mark vertices visited when inserting them into the queue.
3. BFS finds shortest paths in unweighted graphs.