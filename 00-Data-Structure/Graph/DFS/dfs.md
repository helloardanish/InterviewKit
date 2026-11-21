---
title: "Depth-First Search (DFS)"
layout: default
parent: Graph
grand_parent: Data Structures & Algorithms
nav_order: 2
permalink: /data-structures/Graph/DFS/dfs/

topic: Depth-First Search
section: Graph Traversal
concepts: [depth-first-traversal, visited-set, recursion, backtracking]
data_structures: [graph, stack, hashset]
complexities: [O(V+E)]
tags: [algorithms, graph, dfs, traversal, recursion, java]
---

# Depth-First Search (DFS)

## 1. What Is Depth-First Search?

**Depth-First Search (DFS)** is a graph traversal algorithm that explores a path as deeply as possible before backtracking to explore other paths.

DFS starts from a selected vertex and:

1. Visits the starting vertex.
2. Moves to one of its unvisited neighbors.
3. Continues exploring unvisited neighbors as deeply as possible.
4. When it reaches a vertex with no unvisited neighbors, it backtracks.
5. Continues exploring other available paths.
6. Stops when all reachable vertices have been visited.

DFS uses a **Stack (LIFO — Last In, First Out)** or recursion to manage the traversal.

### Real-World Examples

**Example 1: Exploring a maze**

Imagine you are exploring a maze.

- Start at the entrance.
- Choose a path.
- Continue moving forward.
- If you reach a dead end, go back.
- Try another path.

This is similar to DFS.

**Example 2: Exploring folders on a computer**

Imagine a folder containing subfolders.

```text
Documents
├── Projects
│   ├── Java
│   │   ├── Spring
│   │   └── Hibernate
│   └── React
├── Photos
└── Reports
```

DFS can explore one folder and all its subfolders before moving to the next folder.

**Example 3: Solving a puzzle**

Suppose you are solving a puzzle with multiple possible moves.

You try one sequence of moves until it fails or reaches a solution. Then you backtrack and try another sequence.

DFS follows this approach.

## 2. Why Is DFS Needed?

DFS is useful when you need to:

- Explore all reachable vertices.
- Detect cycles in a graph.
- Find connected components.
- Check whether a path exists between two vertices.
- Perform topological sorting.
- Find strongly connected components.
- Solve maze and backtracking problems.
- Explore dependencies between objects.

## 3. Understanding DFS with an Example

Consider the following undirected graph:

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

We will perform DFS starting from vertex A.

For this example, assume we visit neighbors in the order they appear in the adjacency list.

### 3.1 DFS Traversal Order

**Step 1: Start at A**

Visit A.

Choose its first unvisited neighbor B.

```text
Visited: A
```

**Step 2: Move to B**

Visit B.

B's neighbors are A and D.

- A is already visited.
- D is unvisited.

Move to D.

```text
Visited: A → B
```

**Step 3: Move to D**

Visit D.

D's neighbors are B, E, and F.

- B is already visited.
- E is unvisited.

Move to E.

```text
Visited: A → B → D
```

**Step 4: Move to E**

Visit E.

E's neighbors are C, D, and F.

- C is unvisited.
- D is already visited.

Move to C.

```text
Visited: A → B → D → E
```

**Step 5: Move to C**

Visit C.

C's neighbors are A and E.

Both are already visited.

There are no unvisited neighbors, so backtrack to E.

```text
Visited: A → B → D → E → C
```

**Step 6: Backtrack to E and move to F**

E has another unvisited neighbor, F.

Visit F.

```text
Visited: A → B → D → E → C → F
```

F's neighbors D and E are already visited.

There are no more unvisited vertices reachable from A.

### Final DFS Traversal

```text
A → B → D → E → C → F
```

**Important:** DFS traversal order depends on the order in which neighbors are processed. A different adjacency-list order can produce a different valid DFS traversal.

## 4. Understanding Backtracking in DFS

Backtracking is one of the most important concepts in DFS.

Consider:

```text
A ----- B ----- D
        |
        |
        C
```

Starting from A:

1. Visit A.
2. Move to B.
3. Move to D.
4. D has no unvisited neighbors.
5. Backtrack to B.
6. Visit C.
7. C has no unvisited neighbors.
8. Backtrack to B and then A.
9. Traversal is complete.

The traversal is:

```text
A → B → D → C
```

Notice that DFS does not move to another neighbor until it has finished exploring the current path.

## 5. How Does DFS Work Internally?

DFS primarily uses two concepts:

### 5.1 Stack

A Stack follows LIFO:

**Last In, First Out.**

The most recently inserted element is removed first.

Example:

```text
Stack: [A, B, C]

Remove from top:

C

Remaining stack:

[A, B]
```

DFS uses this behavior to explore the most recently discovered path first.

### 5.2 Recursion

A recursive method calls itself to process another vertex.

Every recursive call is stored in the program's call stack.

For example:

```java
dfs(A)
    dfs(B)
        dfs(D)
            dfs(E)
                dfs(C)
```

When a recursive call finishes, execution returns to the previous call.

This automatic return is called backtracking.

### 5.3 Visited Set

A visited array or set tracks vertices that have already been discovered.

It prevents repeated traversal and infinite recursion when the graph contains cycles.

For example:

```text
A ----- B
|       |
|       |
D ----- C
```

Without visited tracking, DFS could keep following the cycle indefinitely.

## 6. DFS Algorithm — Step by Step

The recursive DFS algorithm is:

1. Select a starting vertex.
2. Mark it as visited.
3. Process or print the current vertex.
4. Visit every unvisited neighbor recursively.
5. Return when there are no unvisited neighbors.

### Pseudocode — Recursive DFS

```text
DFS(graph, current, visited):

    visited.add(current)

    process(current)

    for each neighbor of current:

        if neighbor is not visited:

            DFS(graph, neighbor, visited)
```

## 7. Java Implementation Using Adjacency List — Recursive DFS

We will use the adjacency-list representation introduced in the Graph Introduction document.

### 7.1 Complete Java Code

```java
import java.util.*;

public class DFSGraph {

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

    // Recursive DFS
    public void dfs(int start) {

        if (!graph.containsKey(start)) {
            System.out.println("Starting vertex does not exist.");
            return;
        }

        Set<Integer> visited = new HashSet<>();

        dfsRecursive(start, visited);
    }

    private void dfsRecursive(int current, Set<Integer> visited) {

        // Mark current vertex as visited
        visited.add(current);

        // Process current vertex
        System.out.print(current + " ");

        // Explore all neighbors
        for (int neighbor : graph.get(current)) {

            if (!visited.contains(neighbor)) {

                dfsRecursive(neighbor, visited);
            }
        }
    }

    public static void main(String[] args) {

        DFSGraph graph = new DFSGraph();

        graph.addEdge(0, 1);
        graph.addEdge(0, 2);
        graph.addEdge(1, 3);
        graph.addEdge(2, 4);
        graph.addEdge(3, 4);
        graph.addEdge(3, 5);
        graph.addEdge(4, 5);

        System.out.println("DFS Traversal:");

        graph.dfs(0);
    }
}
```

### 7.2 Output

```text
DFS Traversal:
0 1 3 4 2 5
```

The output follows the neighbor insertion order.

### 7.3 Important Lines Explained

**Creating the visited set**

```java
Set<Integer> visited = new HashSet<>();
```

Stores vertices that have already been visited.

**Marking the current vertex**

```java
visited.add(current);
```

Marks the current vertex before exploring its neighbors.

**Processing the current vertex**

```java
System.out.print(current + " ");
```

Prints the vertex when it is first discovered.

**Exploring neighbors**

```java
for (int neighbor : graph.get(current)) {
```

Checks all vertices directly connected to the current vertex.

**Recursive call**

```java
if (!visited.contains(neighbor)) {
    dfsRecursive(neighbor, visited);
}
```

If the neighbor has not been visited, DFS explores that neighbor completely before returning to the current vertex.

## 8. DFS Using an Explicit Stack — Iterative DFS

DFS can also be implemented without recursion by using an explicit Stack.

This is useful when:

- The graph can be very deep.
- You want to avoid recursive call-stack overflow.
- You want more control over traversal.

### 8.1 Java Implementation

```java
import java.util.*;

public class IterativeDFS {

    private final Map<Integer, List<Integer>> graph = new HashMap<>();

    public void addVertex(int vertex) {
        graph.putIfAbsent(vertex, new ArrayList<>());
    }

    public void addEdge(int source, int destination) {

        addVertex(source);
        addVertex(destination);

        graph.get(source).add(destination);
        graph.get(destination).add(source);
    }

    public void dfs(int start) {

        if (!graph.containsKey(start)) {
            System.out.println("Starting vertex does not exist.");
            return;
        }

        Set<Integer> visited = new HashSet<>();

        Deque<Integer> stack = new ArrayDeque<>();

        stack.push(start);

        while (!stack.isEmpty()) {

            int current = stack.pop();

            // Skip if already visited
            if (visited.contains(current)) {
                continue;
            }

            visited.add(current);

            System.out.print(current + " ");

            // Push neighbors onto the stack
            for (int neighbor : graph.get(current)) {

                if (!visited.contains(neighbor)) {

                    stack.push(neighbor);
                }
            }
        }
    }

    public static void main(String[] args) {

        IterativeDFS graph = new IterativeDFS();

        graph.addEdge(0, 1);
        graph.addEdge(0, 2);
        graph.addEdge(1, 3);
        graph.addEdge(2, 4);
        graph.addEdge(3, 4);
        graph.addEdge(3, 5);
        graph.addEdge(4, 5);

        System.out.println("Iterative DFS:");

        graph.dfs(0);
    }
}
```

### 8.2 Why Can Iterative DFS Produce a Different Order?

A Stack processes the last inserted neighbor first.

Suppose the adjacency list is:

```text
0 → [1, 2]
```

The algorithm pushes:

```text
Stack: [1, 2]
```

The next element popped is 2.

Therefore, iterative DFS may visit 2 before 1.

For the example graph, the iterative implementation above can produce:

```text
0 2 4 5 3 1
```

This is still a valid DFS traversal.

If you want to reproduce the recursive traversal order, push neighbors in reverse order:

```java
List<Integer> neighbors = graph.get(current);

for (int i = neighbors.size() - 1; i >= 0; i--) {

    int neighbor = neighbors.get(i);

    if (!visited.contains(neighbor)) {
        stack.push(neighbor);
    }
}
```

For more complex iterative DFS problems, where exact recursive discovery and finishing behavior matters, use stack frames or explicit iterator state.

## 9. DFS Using an Adjacency Matrix

DFS can also be implemented using an adjacency matrix.

The difference is how we find neighbors.

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
public class DFSMatrix {

    private final int[][] matrix;
    private final int vertices;

    public DFSMatrix(int vertices) {
        this.vertices = vertices;
        this.matrix = new int[vertices][vertices];
    }

    public void addEdge(int source, int destination) {

        matrix[source][destination] = 1;
        matrix[destination][source] = 1;
    }

    public void dfs(int start) {

        boolean[] visited = new boolean[vertices];

        dfsRecursive(start, visited);
    }

    private void dfsRecursive(int current, boolean[] visited) {

        visited[current] = true;

        System.out.print(current + " ");

        for (int neighbor = 0; neighbor < vertices; neighbor++) {

            if (matrix[current][neighbor] == 1
                    && !visited[neighbor]) {

                dfsRecursive(neighbor, visited);
            }
        }
    }

    public static void main(String[] args) {

        DFSMatrix graph = new DFSMatrix(6);

        graph.addEdge(0, 1);
        graph.addEdge(0, 2);
        graph.addEdge(1, 3);
        graph.addEdge(2, 4);
        graph.addEdge(3, 4);
        graph.addEdge(3, 5);
        graph.addEdge(4, 5);

        System.out.println("DFS Traversal:");

        graph.dfs(0);
    }
}
```

Output:

```text
DFS Traversal:
0 1 3 4 2 5
```

## 10. DFS for a Disconnected Graph

A single DFS run visits only vertices reachable from the starting vertex.

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

If DFS starts from A, it visits only:

```text
A → B → C
```

To visit all vertices, run DFS from each unvisited vertex.

### Java Implementation

```java
public void dfsAllComponents() {

    Set<Integer> visited = new HashSet<>();

    for (int vertex : graph.keySet()) {

        if (!visited.contains(vertex)) {

            dfsRecursive(vertex, visited);
        }
    }
}

private void dfsRecursive(int current, Set<Integer> visited) {

    visited.add(current);

    System.out.print(current + " ");

    for (int neighbor : graph.get(current)) {

        if (!visited.contains(neighbor)) {

            dfsRecursive(neighbor, visited);
        }
    }
}
```

This approach visits every vertex and every adjacency-list entry once.

Its time complexity is O(V + E).

It is also useful for finding connected components in an undirected graph.

## 11. DFS for Cycle Detection in an Undirected Graph

One important application of DFS is detecting cycles.

Consider:

```text
A ----- B
|       |
|       |
D ----- C
```

There is a cycle:

```text
A → B → C → D → A
```

For an undirected graph, when exploring a vertex:

- If a neighbor is unvisited, recursively explore it.
- If a neighbor is already visited and is not the parent of the current vertex, a cycle exists.

Why ignore the parent?

Consider:

```text
A ----- B
```

When DFS moves from A to B, B can see A as already visited.

But that is simply the edge through which DFS arrived, not a cycle.

### Java Implementation

```java
import java.util.*;

public class CycleDetection {

    private final List<Integer>[] graph;

    @SuppressWarnings("unchecked")
    public CycleDetection(int vertices) {

        graph = new ArrayList[vertices];

        for (int i = 0; i < vertices; i++) {
            graph[i] = new ArrayList<>();
        }
    }

    public void addEdge(int source, int destination) {

        graph[source].add(destination);
        graph[destination].add(source);
    }

    public boolean hasCycle() {

        boolean[] visited = new boolean[graph.length];

        for (int vertex = 0; vertex < graph.length; vertex++) {

            if (!visited[vertex]) {

                if (dfsCycle(vertex, -1, visited)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean dfsCycle(
            int current,
            int parent,
            boolean[] visited) {

        visited[current] = true;

        for (int neighbor : graph[current]) {

            if (!visited[neighbor]) {

                if (dfsCycle(neighbor, current, visited)) {
                    return true;
                }

            } else if (neighbor != parent) {

                return true;
            }
        }

        return false;
    }

    public static void main(String[] args) {

        CycleDetection graph = new CycleDetection(4);

        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(3, 0);

        System.out.println(graph.hasCycle()); // true
    }
}
```

This implementation assumes an undirected graph without parallel edges or self-loops. Those cases need additional handling if they are allowed.

## 12. Important Applications of DFS

| Application | How DFS Helps |
|---|---|
| Cycle detection | Detects cycles in directed and undirected graphs |
| Connected components | Finds separate groups of connected vertices |
| Path finding | Checks whether a path exists |
| Topological sorting | Orders vertices in a DAG |
| Strongly connected components | Finds mutually reachable groups |
| Maze solving | Explores possible paths |
| Backtracking | Tries possibilities and reverses failed choices |
| Dependency analysis | Explores dependencies recursively |

## 13. DFS Complexity Analysis

The time complexity depends on the graph representation.

### 13.1 Adjacency List

\[
O(V + E)
\]

Why?

- Every vertex is visited at most once.
- Every adjacency-list entry is examined at most once.

For an undirected graph, each edge appears twice in the adjacency lists, but this is still O(V + E).

Space complexity:

\[
O(V)
\]

This includes:

- Visited array or set.
- Recursion call stack, or explicit stack in iterative DFS.

The recursive call stack can reach O(V) in the worst case.

### 13.2 Adjacency Matrix

\[
O(V^2)
\]

Why?

For every visited vertex, DFS scans all V columns of its matrix row.

In the worst case, all V vertices are visited.

Space complexity:

\[
O(V^2)
\]

The matrix itself requires O(V²) storage. DFS auxiliary space is O(V).

### Complexity Comparison

| Operation | Adjacency List | Adjacency Matrix |
|---|---:|---:|
| DFS Time | O(V + E) | O(V²) |
| Graph Storage | O(V + E) | O(V²) |
| DFS Auxiliary Space | O(V) | O(V) |

## 14. BFS vs DFS

Both are fundamental graph traversal algorithms, but their exploration strategies differ.

| Feature | BFS | DFS |
|---|---|---|
| Full Form | Breadth-First Search | Depth-First Search |
| Traversal | Level by level | Deep before backtracking |
| Data Structure | Queue | Stack or recursion |
| Principle | FIFO | LIFO |
| Shortest path in unweighted graph | Yes | Not guaranteed |
| Cycle detection | Yes | Yes |
| Connected components | Yes | Yes |
| Topological sorting | Kahn's algorithm | DFS-based approach |
| Time with adjacency list | O(V + E) | O(V + E) |
| Auxiliary space | O(V) | O(V) |

### Simple Way to Remember

- **BFS:** Explore all nearby vertices first.
- **DFS:** Explore one path as deeply as possible, then backtrack.

## 15. Common DFS Mistakes

### Mistake 1: Forgetting the Visited Structure

Without visited tracking, DFS may repeatedly traverse cycles.

Correct:

```java
if (!visited.contains(neighbor)) {
    dfsRecursive(neighbor, visited);
}
```

### Mistake 2: Marking Visited After Recursive Calls

Incorrect:

```java
dfsRecursive(neighbor, visited);
visited.add(neighbor);
```

This can cause infinite recursion in cyclic graphs.

Correct:

```java
visited.add(current);
```

Mark the current vertex immediately when entering the DFS method.

### Mistake 3: Confusing Stack and Queue

- DFS uses Stack or recursion.
- BFS uses Queue.

### Mistake 4: Assuming DFS Finds the Shortest Path

DFS finds a path if one exists, but the first path it discovers may not be the shortest.

Use BFS for shortest paths in unweighted graphs.

### Mistake 5: Ignoring Disconnected Components

One DFS call does not necessarily visit the entire graph.

Use an outer loop to start DFS from every unvisited vertex.

### Mistake 6: Stack Overflow with Deep Recursion

A graph containing a very long chain of vertices may cause `StackOverflowError` in recursive DFS.

Use iterative DFS when the graph can be very deep.

## 16. Interview Questions and Answers

### Q1. What is DFS?

Depth-First Search is a graph traversal algorithm that explores a path as deeply as possible before backtracking.

### Q2. Which data structure is used in DFS?

Stack.

It can be implemented using an explicit Stack or the program's recursion call stack.

### Q3. What is the time complexity of DFS?

- Adjacency List: O(V + E).
- Adjacency Matrix: O(V²).

### Q4. What is the space complexity of DFS?

O(V) auxiliary space for visited tracking and the stack or recursion calls.

### Q5. What is backtracking?

Backtracking means returning to a previous vertex when the current vertex has no unvisited neighbors, then exploring another available path.

### Q6. Can DFS detect cycles?

Yes. DFS can detect cycles in both directed and undirected graphs, but the implementation differs.

For undirected graphs, parent tracking is commonly used.

For directed graphs, recursion-stack state or three-color marking is commonly used.

### Q7. Does DFS find the shortest path?

Not necessarily.

DFS may find a valid path, but it does not guarantee the minimum number of edges.

### Q8. Can DFS work on a directed graph?

Yes. It follows outgoing edges from each vertex.

### Q9. Can DFS work on a disconnected graph?

Yes. To visit every component, run DFS from every unvisited vertex.

### Q10. What is the difference between recursive and iterative DFS?

Recursive DFS uses the call stack automatically.

Iterative DFS uses an explicitly managed Stack.

Both can achieve O(V + E) time with an adjacency list.

### Q11. Why is a visited array required?

It prevents repeated traversal and infinite recursion when cycles exist.

### Q12. What is the difference between DFS and backtracking?

DFS is a graph traversal strategy.

Backtracking is a broader problem-solving technique that explores choices and reverses decisions when a path fails.

DFS is frequently used to implement backtracking algorithms.

### Q13. What is a DFS tree?

A DFS tree is the collection of discovery edges used when DFS first visits vertices. In a disconnected graph, the traversal produces a DFS forest containing multiple trees.

### Q14. What is topological sorting?

Topological sorting is a linear ordering of vertices in a directed acyclic graph such that every directed edge u → v places u before v.

DFS can be used to produce a topological ordering.

### Q15. Can DFS be used to find connected components?

Yes. Run DFS from every unvisited vertex. Each DFS run identifies one connected component in an undirected graph.

## 17. Practice Problems

### Beginner Level

1. Implement recursive DFS using an adjacency list.
2. Implement iterative DFS using a Stack.
3. Implement DFS using an adjacency matrix.
4. Print all vertices reachable from a given vertex.
5. Count the number of reachable vertices.
6. Check whether a path exists between two vertices.
7. Traverse a disconnected graph.

### Intermediate Level

1. Count connected components.
2. Detect a cycle in an undirected graph.
3. Detect a cycle in a directed graph.
4. Find all paths between two vertices.
5. Check whether an undirected graph is connected.
6. Find the number of islands in a grid.
7. Perform topological sorting using DFS.
8. Find the number of provinces.

### Challenge Level

1. Number of Islands.
2. Clone Graph.
3. Course Schedule.
4. Course Schedule II.
5. Pacific Atlantic Water Flow.
6. Surrounded Regions.
7. Graph Valid Tree.
8. Critical Connections in a Network.
9. Strongly Connected Components.

## 18. Quick Revision Cheat Sheet

| Topic | Key Point |
|---|---|
| Algorithm | Depth-First Search |
| Category | Graph Traversal |
| Data Structure | Stack / Recursion |
| Stack Principle | LIFO |
| Traversal | Deep before backtracking |
| Tracking | Visited array or set |
| Shortest Path | Not guaranteed |
| Adjacency List Time | O(V + E) |
| Adjacency Matrix Time | O(V²) |
| Auxiliary Space | O(V) |
| Cycle Detection | Supported |
| Disconnected Graph | Run DFS from every unvisited vertex |

### DFS Template to Remember

```java
public void dfs(int current, boolean[] visited) {

    visited[current] = true;

    System.out.print(current + " ");

    for (int neighbor : graph[current]) {

        if (!visited[neighbor]) {

            dfs(neighbor, visited);
        }
    }
}
```

**Remember these three things:**

1. DFS uses a Stack or recursion.
2. Explore deeply before backtracking.
3. Always track visited vertices to avoid repeated traversal.

---

## 19. Recommended Learning Roadmap

After understanding BFS and DFS, continue with these graph topics:

1. Connected Components
2. Cycle Detection in Undirected Graphs
3. Cycle Detection in Directed Graphs
4. Bipartite Graph
5. Topological Sorting
6. Shortest Path Algorithms
7. Minimum Spanning Tree
8. Strongly Connected Components