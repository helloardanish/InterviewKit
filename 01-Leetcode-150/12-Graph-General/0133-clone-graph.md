---
title: "0133 — Clone Graph"
layout: default
parent: Graph General
grand_parent: Leetcode
nav_order: 133
permalink: /12-Graph-General/0133-clone-graph/

problem: 133 - Clone Graph
url: https://leetcode.com/problems/clone-graph/
difficulty: Medium
section: Graph / Traversal
patterns: [graph-traversal, dfs, bfs, deep-copy]
data_structures: [graph, hash-map, queue]
time: O(V + E)
space: O(V)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0133 — Clone Graph

| Field | Value |
|---|---|
| **Pattern** | Graph Traversal + Hash Map (Deep Copy) |
| **Difficulty** | Medium |
| **Section** | Graph / Traversal |
| **Time** | O(V + E) |
| **Space** | O(V) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Meta, Amazon, Google, Microsoft, Bloomberg |
| **LeetCode** | [Clone Graph](https://leetcode.com/problems/clone-graph/) |

## 1. Problem in Simple Words

**Given:**
- A reference to a node in a **connected undirected graph**.
- Each node has an `int val` (1-indexed, unique) and a `List<Node> neighbors`.
- The graph is given as an adjacency list; the given node is always the one with `val = 1`.

**Required:**
- Return a **deep copy (clone)** of the graph — new `Node` objects with the same values and the same neighbor relationships.
- The clone must be completely independent of the original: no cloned node may point to an original node.
- The function signature is `Node cloneGraph(Node node)`, where `null` represents an empty graph.

**Constraints that actually matter:**
- `0 <= number of nodes <= 100` — small enough for any traversal; recursion depth is bounded by 100, so DFS recursion will not overflow the stack.
- `1 <= Node.val <= 100` and values are **unique** — we can key a map by `val`, or key by the original `Node` reference. Uniqueness makes `val` a safe identity key.
- **Connected** graph — starting from node 1, every node is reachable, so one traversal from the given node visits the whole graph.
- No repeated edges, no self-loops — simplifies neighbor handling; no need to deduplicate.

## 2. Recognition Signals

- The phrase **"deep copy"** or **"clone"** on a linked structure — the canonical signal that we must create new objects and preserve the pointer structure between them.
- The structure contains **cycles** (undirected graph: 1 → 2 → 1). A naive recursive copy would recurse forever on the back-edge. Cycles force us to remember which nodes we have already copied.
- We need a **one-to-one mapping** between original nodes and cloned nodes, so that when a neighbor is referenced again we can return the *already-created clone* instead of making a duplicate.
- Pattern: **graph traversal (DFS or BFS) + hash map `original → clone`**. This is the same skeleton used for any "copy a cyclic structure" problem (e.g., copy a linked list with random pointers).

## 3. Core Idea

**Intuition.** Copying a graph is copying two things: the **nodes** (values) and the **edges** (neighbor lists). If we traverse the original and, for every node, create a clone the first time we see it, we can then fill each clone's neighbor list by looking up the clones of its neighbors. Because the graph has cycles, the same original node will be reached more than once — the hash map lets us detect that and return the existing clone.

**Key observation.** The hash map `Map<Node, Node> visited` (original → clone) serves two purposes at once:
1. It records which originals have been copied (the "visited" set).
2. It gives O(1) access to the clone of any original, which is exactly what we need to build neighbor lists.

**Variables:**
- `Map<Node, Node> visited` — original node → its clone.
- `clone` — the newly created copy of the current node.
- Recursion stack (DFS) or a `Queue` (BFS) — the traversal frontier.

**Invariant.** Whenever `visited` contains `node`, `visited.get(node)` is a fully-created clone of `node` (its `val` is set; its neighbor list may still be under construction, but the object identity is stable). This is the crucial property that makes the cycle safe: on a back-edge we return the in-progress clone rather than creating a new one, so the edge closes the cycle on the clone side.

**Why the two-phase pattern works.** Create-the-clone on first visit and *immediately* put it in the map, *then* recurse into neighbors. Because the clone is registered before its neighbors are processed, any neighbor that points back to the current node will find the clone in the map and link to it.

## 4. Approach 1 — BFS with a Hash Map (Iterative)

**Logic.** Use a queue to traverse the graph level by level. On the first visit of a node, create its clone and register it in the map. Then enqueue it. While processing, for each neighbor, either fetch the existing clone (if visited) or create a new clone and enqueue it, and append it to the current clone's neighbor list.

### Java code (BFS)
```java
    import java.util.*;

    class Solution {
        public Node cloneGraph(Node node) {
            if (node == null) return null;

            Map<Node, Node> visited = new HashMap<>();
            Queue<Node> queue = new LinkedList<>();

            visited.put(node, new Node(node.val));
            queue.offer(node);

            while (!queue.isEmpty()) {
                Node current = queue.poll();
                Node currentClone = visited.get(current);

                for (Node neighbor : current.neighbors) {
                    if (!visited.containsKey(neighbor)) {
                        visited.put(neighbor, new Node(neighbor.val));
                        queue.offer(neighbor);
                    }
                    // Link the clone of the neighbor into the current clone's list.
                    currentClone.neighbors.add(visited.get(neighbor));
                }
            }

            return visited.get(node);
        }
    }
```

**Complexity.**
- Time: O(V + E) — each node is enqueued once; each edge is examined once per endpoint.
- Space: O(V) — the map holds V entries, the queue holds up to V nodes; the clones themselves are part of the required output.

**When it is useful.** BFS avoids recursion entirely, which is nice when the graph could be deep (though here the depth is bounded by 100). It also makes the "create-then-enqueue" ordering explicit, which some people find easier to reason about than recursion.

## 5. Approach 2 — Optimized: DFS with a Hash Map (Recursive)

**Algorithm steps.**
1. If `node == null`, return `null` (empty graph).
2. If `node` is already in `visited`, return `visited.get(node)` — this is the cycle-breaking line.
3. Otherwise, create `clone = new Node(node.val)` and record `visited.put(node, clone)`. **Register the clone before recursing.**
4. For each `neighbor` in `node.neighbors`, recurse: `clone.neighbors.add(dfs(neighbor))` (using the shared map).
5. Return `clone`.

### Java code (DFS)

```java
import java.util.*;

class Solution {
    private Map<Node, Node> visited = new HashMap<>();

    public Node cloneGraph(Node node) {
        if (node == null) return null;
        return dfs(node);
    }

    private Node dfs(Node node) {
        // If already cloned, return the existing clone (breaks cycles).
        if (visited.containsKey(node)) {
            return visited.get(node);
        }

        // Create the clone and register it BEFORE recursing into neighbors.
        Node clone = new Node(node.val);
        visited.put(node, clone);

        for (Node neighbor : node.neighbors) {
            clone.neighbors.add(dfs(neighbor));
        }

        return clone;
    }
}
```

**Explanation of important logic.**
- **The ordering is everything.** `visited.put(node, clone)` must happen *before* the neighbor loop. If you recurse first and only then insert, a neighbor pointing back to `node` would re-enter `dfs(node)`, not find it in the map, and create a duplicate clone (or recurse infinitely).
- **The base-case check is the visited check.** For graphs, "have I seen this?" and "is it already cloned?" are the same question, so the `containsKey` check doubles as the termination condition.
- **The `visited` map must be shared** across all recursive calls (instance field, or passed as a parameter). A local map inside the recursive call would lose state.
- **Deep-copy guarantee:** every clone is a `new Node`, and every neighbor added is a clone from the map — never an original. So no clone references an original node.

**Complexity.**
- Time: O(V + E) — each node is cloned once; each edge is traversed once per endpoint (twice per undirected edge, which is still linear).
- Space: O(V) — the map holds V entries; the recursion stack is O(V) in the worst case (a path graph of 100 nodes → depth 100, safe).

**Why it is optimal.** Any correct clone must create one new node per original node and set every edge, so Ω(V + E) is a lower bound; the hash-map traversal meets it. A map keyed by node `val` (instead of the node reference) is also valid here because values are unique and bounded to 1..100 — you could even use a fixed-size `Node[101]` array for O(1) lookup with no hashing. The `Map<Node, Node>` version is preferred because it depends only on object identity and does not rely on value uniqueness, which makes it the more general, interview-ready pattern.

## 6. Dry Run

**Example:** `adjList = [[2,4],[1,3],[2,4],[1,3]]` — a 4-cycle: 1↔2, 2↔3, 3↔4, 4↔1. Node 1's neighbors are [2, 4].

This example is useful because it is a **cycle** — the exact situation where the visited map must break a back-edge (4 → 1) instead of recursing forever.

**DFS from node 1 (map shown as original val → clone val):**

| Call | node | visited before | Action | Clone's neighbors added | Returned |
|---|---|---|---|---|---|
| 1 | 1 | `{}` | create clone 1, register | recurse into 2, then 4 | clone1 |
| 2 | 2 | `{1→1'}` | create clone 2, register | recurse into 1, then 3 | clone2 |
| 3 | 1 (from 2) | `{1→1', 2→2'}` | **found** — return clone1 | — | clone1 |
| 4 | 3 | `{1→1', 2→2'}` | create clone 3, register | recurse into 2, then 4 | clone3 |
| 5 | 2 (from 3) | `{…3→3'}` | **found** — return clone2 | — | clone2 |
| 6 | 4 | `{…3→3'}` | create clone 4, register | recurse into 3, then 1 | clone4 |
| 7 | 3 (from 4) | `{…4→4'}` | **found** — return clone3 | — | clone3 |
| 8 | 1 (from 4) | all present | **found** — return clone1 | — | clone1 |
| — | back in call 1 | — | clone1.neighbors = [clone2, clone4] | done | clone1 |

**Final result:** clone1 → neighbors [clone2, clone4]; clone2 → [clone1, clone3]; clone3 → [clone2, clone4]; clone4 → [clone3, clone1]. This matches the output `[[2,4],[1,3],[2,4],[1,3]]`. The back-edges (steps 3, 5, 7, 8) never create duplicates because of the `containsKey` check.

## 7. Complexity Summary

| Approach | Time | Space (auxiliary) | Notes |
|---|---|---|---|
| BFS + Hash Map | O(V + E) | O(V) | No recursion; explicit queue |
| DFS + Hash Map (recursive) | O(V + E) | O(V) | Cleaner code; recursion depth ≤ V (≤ 100 here) |

Input space is O(V + E) for the given graph and is not counted as auxiliary. The O(V) map is unavoidable in any correct clone of a cyclic graph.

## 8. Edge Cases

| Case | Why it matters | How the code handles it |
|---|---|---|
| `node == null` (empty graph) | The problem allows 0 nodes; there is nothing to clone | Explicit `if (node == null) return null;` |
| Single node, no neighbors (`adjList = [[]]`) | Tests that a clone is still created and the neighbor list stays empty | Clone created, `for` loop over empty list does nothing, returns clone |
| Self-referencing cycle (1 → 2 → 1) | Tests cycle-breaking | Back-edge hits `containsKey` and returns the existing clone |
| Node that is its own neighbor | Excluded by "no self-loops", but the ordering (register before recurse) would still handle it | clone is already in map when the self-edge is processed |
| Dense graph (every node connected to every other) | E = O(V²); tests that complexity stays linear in V + E | Each edge is still visited a constant number of times |
| Max size (100 nodes in a path) | Deepest possible recursion | Depth 100 is well within the JVM stack |

## 9. Java Notes

- **The `Node` class is provided by LeetCode** in the editor — do not redefine it in the submission. Its definition is `class Node { public int val; public List<Node> neighbors; public Node(int _val) {...} }`.
- **The constructor takes only `val`** in the standard LeetCode `Node`; the neighbor list is initialized to an empty `ArrayList` by the constructor. So `new Node(node.val)` is enough; neighbors are added via `clone.neighbors.add(...)`.
- **HashMap keyed by object identity:** `Node` does not override `equals`/`hashCode`, so `HashMap<Node, Node>` uses reference identity — exactly what we want (two distinct nodes with the same value would never collide, though here values are unique anyway).
- **Shared state:** the `visited` map is an instance field in the DFS version. If you submit multiple test cases in one run, the field is re-created per `Solution` instance in LeetCode's harness, but as a defensive habit you can make it a local variable passed as a parameter, or clear it.
- **`LinkedList` vs `ArrayDeque` for BFS:** `ArrayDeque` is the preferred queue (faster, no synchronization); `LinkedList` also works and is what many tutorials show.
- **Shallow-copy pitfall:** `new Node(node.val)` then `clone.neighbors = node.neighbors` would be a shallow copy sharing lists — never do this; you must build a new list of cloned neighbors.

## 10. Common Mistakes

- **Recursing before registering the clone.** If you do `dfs(neighbor)` before `visited.put(node, clone)`, the back-edge re-enters `dfs(node)`, the map misses, and you either duplicate nodes or infinitely recurse. Register first.
- **Using a local `visited` map inside the recursive function.** It resets on every call, so the cycle check never fires. The map must be shared (field or parameter).
- **Returning the original node for already-visited nodes.** Defeats the deep copy — the clone would then point to original nodes.
- **Copying only values, not edges.** Creating all nodes but forgetting to populate `clone.neighbors` yields a graph of isolated nodes.
- **Shallow-copying the neighbor list** (`clone.neighbors = node.neighbors`) — the clone shares the original's list object.
- **Assuming `null` cannot be passed.** The empty-graph case (`[]`) sends `null`; without the null check you get an NPE.

## 11. Interview Takeaway

**Recognition trigger to memorize:** *"Deep-copy a cyclic structure (graph / linked list with random pointers) → traverse with DFS or BFS while keeping a hash map `original → clone`, register each clone before recursing so cycles close on the clone side."*

**90-second explanation:** "I need to create one new node for every original node and reproduce every edge. Because the graph is undirected and cyclic, a plain recursive copy would loop forever on back-edges. So I keep a hash map from original node to its clone. On visiting a node, if it is already in the map I return the stored clone — that's how I break cycles. Otherwise I create the clone, put it in the map *before* processing neighbors, then for each neighbor I recursively get its clone and append it to the current clone's neighbor list. Registering the clone first is essential: it guarantees that when a neighbor points back, the clone already exists. This visits each node once and each edge once, so it is O(V + E) time and O(V) space."

**Related problems:**
- [Copy List with Random Pointer](https://leetcode.com/problems/copy-list-with-random-pointer/) — same "deep copy with a hash map original → clone" pattern on a linked list with an extra pointer.
- [Number of Islands](https://leetcode.com/problems/number-of-islands/) — grid graph traversal with a visited set; same DFS skeleton without the copy.
- [Course Schedule](https://leetcode.com/problems/course-schedule/) — directed-graph DFS with visit states; reinforces cycle handling.
- [Graph Valid Tree](https://leetcode.com/problems/graph-valid-tree/) — connectivity and cycle detection on an undirected graph.

## 12. Recall Questions

**Q1. Why can't we just recursively copy neighbors without a hash map?**
A: The graph is undirected and may contain cycles. A back-edge would re-enter an already-processing node, causing infinite recursion or duplicate nodes. The map detects "already cloned" and returns the existing clone.

**Q2. Why must the clone be put into the map before recursing into neighbors?**
A: So that a neighbor pointing back to the current node finds the clone in the map. If we registered after recursion, the back-edge would miss and create a duplicate (or recurse infinitely).

**Q3. What are the time and space complexities, and what dominates space?**
A: O(V + E) time — each node cloned once, each edge processed once per endpoint. O(V) space for the map, plus O(V) recursion/queue; clones are the required output.

**Q4. Does it matter whether we key the map by `Node` or by `val`?**
A: Here values are unique (1..100), so either works. Keying by `Node` uses reference identity and is more general; keying by `val` could use a fixed array `Node[101]`. The `Node`-keyed map is the standard interview answer.

**Q5. How does the code handle an empty graph?**
A: The given node is `null` for `[]`, and `cloneGraph` returns `null` via the explicit null check before any traversal.

**Q6. BFS or DFS — which should you write?**
A: Both are O(V + E) and O(V). DFS is shorter and more common in interviews; BFS avoids recursion. Either is accepted; pick the one you can explain the invariant for.

**Q7. How do you guarantee no clone points to an original node?**
A: Every neighbor added comes from `visited.get(neighbor)` or a freshly created `new Node(...)`; we never add the original `neighbor` object itself. The map only ever stores clones.

**Q8. What changes if the graph were a DAG (no cycles)?**
A: The visited map would still be useful to avoid re-copying shared descendants, but infinite recursion would not occur, so a naive recursion would terminate (though it could still duplicate nodes reached by multiple paths).

## 13. Final Code

### Java code (final, optimized DFS)

```java
import java.util.*;

class Solution {
    private Map<Node, Node> visited = new HashMap<>();

    public Node cloneGraph(Node node) {
        if (node == null) return null;
        return dfs(node);
    }

    private Node dfs(Node node) {
        if (visited.containsKey(node)) {
            return visited.get(node);
        }

        Node clone = new Node(node.val);
        visited.put(node, clone); // register BEFORE recursing (cycle safety)

        for (Node neighbor : node.neighbors) {
            clone.neighbors.add(dfs(neighbor));
        }

        return clone;
    }
}
```

## 14. Self-Test

Consider the triangle graph with nodes 1, 2, 3 where 1 is connected to 2 and 3, and 2 is connected to 3 (adjacency list `[[2,3],[1,3],[1,2]]`). Trace the DFS starting from node 1, writing down for each call: the node, whether it was already in `visited`, the action taken, and the neighbor list built so far. Then answer: at which call does the edge 3 → 1 get resolved, and what object does it link to? Explain why the clone's edge 3' → 1' points to the *same* object as the clone's edge 1' → 3' target, i.e., why the clone is a faithful cycle rather than a set of duplicate nodes.