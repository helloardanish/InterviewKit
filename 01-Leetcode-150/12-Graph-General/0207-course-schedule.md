---
title: "0207 — Course Schedule"
layout: default
parent: Graph General
grand_parent: Leetcode
nav_order: 207
permalink: /12-Graph-General/0207-course-schedule/

problem: 207 - Course Schedule
url: https://leetcode.com/problems/course-schedule/
difficulty: Medium
section: Graph / Topological Sort
patterns: [topological-sort, cycle-detection, dfs, bfs, kahn]
data_structures: [graph, hash-map, queue, set]
time: O(V + E)
space: O(V + E)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0207 — Course Schedule

| Field | Value |
|---|---|
| **Pattern** | Topological Sort / Cycle Detection in Directed Graph |
| **Difficulty** | Medium |
| **Section** | Graph / Topological Sort |
| **Time** | O(V + E) |
| **Space** | O(V + E) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Amazon, Google, Meta, Microsoft, Bloomberg |
| **LeetCode** | [Course Schedule](https://leetcode.com/problems/course-schedule/) |

## 1. Problem in Simple Words

**Given:**
- `numCourses`: total number of courses labeled `0` to `numCourses - 1`.
- `prerequisites`: a list of pairs `[a, b]` meaning "to take course `a`, you must first take course `b`".

**Required:**
- Return `true` if it is possible to finish **all** courses, otherwise `false`.
- It is impossible exactly when the prerequisite graph contains a **cycle** (a course ultimately requires itself).

**Constraints that actually matter:**
- `1 <= numCourses <= 2000` — small enough that O(V + E) is trivially fast, but large enough that an O(V²) or O(V·E) approach is wasteful.
- `0 <= prerequisites.length <= 5000` — the edge count is bounded, so adjacency-list representations are cheap.
- `prerequisites[i].length == 2` and `prerequisites[i][0] != prerequisites[i][1]` — no self-loops in the input, but longer cycles are possible.
- All prerequisite pairs are **unique** — no duplicate edges, so we do not need to deduplicate when building the graph.

**Interpretation of the 403 error:** The URL fetch was blocked by LeetCode's anti-bot protection (HTTP 403 Forbidden). The problem statement above is reconstructed from the well-known LeetCode 207 problem; cross-check with the live page in a browser or the LeetCode mobile app. Alternative sources: search "Course Schedule" in the LeetCode problems list, or consult the discussion tab of the problem.

## 2. Recognition Signals

- The problem is about **dependencies between tasks** — "to do A, you must first do B" — which is the canonical shape of a **directed graph**.
- "Can all tasks be completed?" translates directly to "**does the directed graph have a cycle?**"
- The phrase *"possible to finish all courses"* is the classic phrasing for **topological sort feasibility**.
- Pattern: **cycle detection in a directed graph**, solvable two ways:
  - **DFS with three-color marking** (white = unvisited, gray = in current path, black = finished) — a back-edge to a gray node reveals a cycle.
  - **Kahn's algorithm (BFS topological sort)** — repeatedly remove nodes with in-degree 0; if fewer than `numCourses` nodes are removed, a cycle exists.

## 3. Core Idea

**Intuition.** Model courses as nodes and prerequisites as **directed edges**. If `[a, b]` is a prerequisite pair, add an edge `b → a` (course `b` must come before course `a`). A valid course order exists iff the graph is a **DAG** (directed acyclic graph). So the whole problem reduces to: *"Does this directed graph contain a cycle?"*

**Key observation.** In a directed graph, a cycle exists iff:
- **DFS view:** while traversing, we encounter a node that is currently **on the recursion stack** (a back-edge).
- **Kahn view:** after repeatedly removing in-degree-0 nodes, some nodes remain with positive in-degree — those nodes are stuck in a cycle.

**Variables (DFS version):**
- `List<List<Integer>> adj` — adjacency list of the graph.
- `int[] state` — `0` = unvisited, `1` = visiting (on stack), `2` = visited (fully processed).
- Recursion stack — implicit, via the call stack.

**Variables (Kahn version):**
- `int[] inDegree` — number of unmet prerequisites per course.
- `Queue<Integer> queue` — courses ready to be taken (in-degree 0).
- `int taken` — count of courses successfully removed.

**Invariant (DFS).** A node is marked `1` while its subtree is being explored and `2` once all its descendants are confirmed acyclic. If we ever try to explore a node marked `1`, we have found a back-edge → cycle.

**Invariant (Kahn).** The queue always contains exactly the nodes whose in-degree is currently 0. Removing a node decrements its neighbors' in-degrees; when a neighbor hits 0, it is enqueued.

## 4. Approach 1 — DFS with Three-Color Marking

**Logic.** Build the adjacency list. Run DFS from every unvisited node. During DFS:
- Mark the node `1` (visiting).
- Recurse into each neighbor. If a neighbor is `1`, a cycle exists → return `false`.
- After all neighbors are processed, mark the node `2` (done).

If all DFS runs complete without finding a back-edge, return `true`.

### Java code (DFS)

```java
import java.util.*;

class Solution {
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) adj.add(new ArrayList<>());

        for (int[] p : prerequisites) {
            int course = p[0];
            int prereq = p[1];
            adj.get(prereq).add(course); // prereq -> course
        }

        int[] state = new int[numCourses]; // 0 = unvisited, 1 = visiting, 2 = visited
        for (int i = 0; i < numCourses; i++) {
            if (state[i] == 0 && hasCycle(i, adj, state)) {
                return false;
            }
        }
        return true;
    }

    private boolean hasCycle(int node, List<List<Integer>> adj, int[] state) {
        if (state[node] == 1) return true;  // back-edge -> cycle
        if (state[node] == 2) return false; // already fully processed

        state[node] = 1;
        for (int next : adj.get(node)) {
            if (hasCycle(next, adj, state)) return true;
        }
        state[node] = 2;
        return false;
    }
}
```

**Complexity.**
- Time: O(V + E) — each node is visited once, each edge is examined once.
- Space: O(V + E) — adjacency list is O(V + E), `state` is O(V), recursion depth is O(V) in the worst case.

**When it is useful.** DFS is compact and directly expresses "is there a back-edge?". It is the most common interview answer. Recursion depth is at most `numCourses = 2000`, well within the JVM stack.

## 5. Approach 2 — Optimized: Kahn's Algorithm (BFS Topological Sort)

**Algorithm steps.**
1. Build the adjacency list `prereq → course`.
2. Compute `inDegree[course]` = number of prerequisites for each course.
3. Enqueue all courses with `inDegree == 0` (no prerequisites).
4. While the queue is not empty:
   - Dequeue a course; increment `taken`.
   - For each neighbor (course that depends on it), decrement its in-degree.
   - If a neighbor's in-degree hits 0, enqueue it.
5. Return `taken == numCourses`.

### Java code (Kahn's BFS)

```java
import java.util.*;

class Solution {
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) adj.add(new ArrayList<>());

        int[] inDegree = new int[numCourses];
        for (int[] p : prerequisites) {
            int course = p[0];
            int prereq = p[1];
            adj.get(prereq).add(course);
            inDegree[course]++;
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < numCourses; i++) {
            if (inDegree[i] == 0) queue.offer(i);
        }

        int taken = 0;
        while (!queue.isEmpty()) {
            int current = queue.poll();
            taken++;
            for (int next : adj.get(current)) {
                inDegree[next]--;
                if (inDegree[next] == 0) queue.offer(next);
            }
        }
        return taken == numCourses;
    }
}
```

**Explanation of important logic.**
- **In-degree = number of unmet prerequisites.** A course is available only when its in-degree drops to 0.
- **Removing a node = decrementing its dependents' in-degrees.** This models "this prerequisite is now satisfied".
- **Cycle detection by counting.** If the graph has a cycle, the nodes in the cycle never reach in-degree 0, so they are never enqueued. If `taken < numCourses`, a cycle exists.
- **No explicit visited set needed.** The in-degree bookkeeping inherently prevents revisiting: a node is enqueued exactly once, when its in-degree first becomes 0.

**Complexity.**
- Time: O(V + E) — each node is enqueued once, each edge is relaxed once.
- Space: O(V + E) — adjacency list is O(V + E), queue and in-degree array are O(V).

**Why it is optimal.** Detecting a cycle in a general directed graph requires examining every node and edge at least once in the worst case, so Ω(V + E) is a lower bound; both DFS and Kahn achieve it. Kahn's algorithm has the advantage of being **iterative** (no recursion depth concerns) and produces a valid course order as a by-product if you track the dequeued sequence.

## 6. Dry Run

**Example 1 (acyclic):** `numCourses = 4`, `prerequisites = [[1,0],[2,0],[3,1],[3,2]]`.

Meaning: to take 1, need 0; to take 2, need 0; to take 3, need 1 and 2.

This example is useful because it is a **diamond** — multiple paths from 0 to 3 — which tests that in-degree bookkeeping does not double-count or miss edges.

**Graph:** `0 → 1`, `0 → 2`, `1 → 3`, `2 → 3`. In-degrees: `[0, 1, 1, 2]`.

| Step | Queue | current | taken | in-degree after decrements |
|---|---|---|---|---|
| init | `[0]` | — | 0 | `[0,1,1,2]` |
| 1 | `[]` | 0 | 1 | 1→0, 2→0 → `[0,0,0,2]`; enqueue 1, 2 → queue `[1,2]` |
| 2 | `[2]` | 1 | 2 | 3→1 → `[0,0,0,1]` |
| 3 | `[]` | 2 | 3 | 3→0 → `[0,0,0,0]`; enqueue 3 → queue `[3]` |
| 4 | `[]` | 3 | 4 | no neighbors |

`taken = 4 == numCourses` → **true**.

**Example 2 (cyclic):** `numCourses = 2`, `prerequisites = [[1,0],[0,1]]`.

Graph: `0 → 1`, `1 → 0`. In-degrees: `[1, 1]`.

| Step | Queue | current | taken | in-degree |
|---|---|---|---|---|
| init | `[]` | — | 0 | `[1,1]` (no zero in-degree) |

Queue is empty from the start; `taken = 0 < 2` → **false**. Both nodes are trapped in the 2-cycle.

**DFS trace on the cyclic example:** start at 0 → state[0]=1 → neighbor 1 → state[1]=1 → neighbor 0 → state[0]==1 → back-edge → return true (cycle) → **false**.

## 7. Complexity Summary

| Approach | Time | Space (auxiliary) | Notes |
|---|---|---|---|
| DFS three-color | O(V + E) | O(V + E) | Recursive; depth ≤ V |
| Kahn's BFS | O(V + E) | O(V + E) | Iterative; also yields a valid order |

Input space is O(V + E) for the prerequisites array; the adjacency list is O(V + E) auxiliary. Both approaches are asymptotically optimal.

## 8. Edge Cases

| Case | Why it matters | How the code handles it |
|---|---|---|
| `numCourses = 1`, no prerequisites | Trivially finishable | DFS marks the single node done; Kahn enqueues it and returns `taken == 1` |
| No prerequisites at all | All courses are independent | All in-degrees are 0; Kahn enqueues everything; DFS finds no edges |
| Self-loop `[a, a]` | Would be a trivial cycle | Excluded by constraints (`prerequisites[i][0] != prerequisites[i][1]`), but DFS and Kahn both handle it correctly |
| Two-node cycle `[1,0],[0,1]` | Simplest nontrivial cycle | DFS back-edge; Kahn leaves both in-degrees at 1 → `taken = 0` |
| Disconnected components, one cyclic | Tests that we check *all* components | DFS loops over all unvisited nodes; Kahn's global `taken` count catches it |
| Large chain `0→1→2→…→n` | Deep recursion (DFS) | Depth ≤ 2000, safe; Kahn is iterative and unaffected |
| Duplicate prerequisite pairs | Excluded by uniqueness, but would inflate in-degree | Would still be correct for DFS; Kahn would require dedup for exact in-degree semantics (not needed here) |

## 9. Java Notes

- **Adjacency list construction:** `List<List<Integer>> adj = new ArrayList<>(); for (int i = 0; i < numCourses; i++) adj.add(new ArrayList<>());` — do not forget to initialize every inner list, or `adj.get(x)` throws `IndexOutOfBoundsException`.
- **`int[] state` vs `boolean[] visited`:** for cycle detection you need **three** states, not two. A `boolean[]` cannot distinguish "currently on the stack" from "finished". Use `int` with `0/1/2`.
- **Recursion depth:** `numCourses <= 2000`, so DFS recursion is safe under the default JVM stack. For larger constraints you would switch to Kahn or an explicit stack.
- **`Queue<Integer> queue = new LinkedList<>()`** works, but `ArrayDeque` is preferred in production (faster, no synchronization).
- **`int[] inDegree` is a primitive array** — no boxing overhead, and decrementing is O(1).
- **Return type is `boolean`**, so the final comparison `taken == numCourses` is the whole answer in Kahn's version.

## 10. Common Mistakes

- **Using a two-state visited array in DFS.** A `boolean[] visited` cannot detect cycles: it would treat a back-edge as "already visited" and skip it, missing the cycle. You need the `visiting` (gray) state.
- **Forgetting to mark `state[node] = 2` after processing.** Without it, a node reachable via two paths is re-explored, and worse, a cross-edge to a finished node is mistaken for a back-edge.
- **Building the edge in the wrong direction.** The pair `[a, b]` means `b → a` (prerequisite to course). Reversing it still detects cycles (cycle existence is direction-symmetric for this purpose), but it breaks any downstream code that uses the order.
- **Kahn: enqueuing nodes with in-degree 0 only once at the start.** You must also enqueue neighbors whose in-degree drops to 0 during processing — that is the core of the algorithm.
- **Kahn: returning `queue.isEmpty()` instead of `taken == numCourses`.** The queue empties in both acyclic and cyclic graphs; only the count distinguishes them.
- **Not initializing inner lists in `adj`.** Classic `IndexOutOfBoundsException`.
- **Assuming prerequisites are zero-indexed** — they are, but always double-check the problem statement; some variants use 1-indexing.

## 11. Interview Takeaway

**Recognition trigger to memorize:** *"Can all tasks be completed given 'to do A you must first do B' dependencies? → build a directed graph and check for a cycle: DFS with three-color marking, or Kahn's BFS topological sort counting processed nodes."*

**90-second explanation:** "This is a cycle-detection problem on a directed graph. I model each course as a node and each prerequisite `[a, b]` as a directed edge `b → a`. If the graph has a cycle, no valid order exists. I can detect the cycle in two ways. With DFS, I mark nodes as unvisited, visiting, or visited; if I ever reach a node that is currently visiting, I have a back-edge and therefore a cycle. With Kahn's algorithm, I repeatedly remove nodes with in-degree 0 — if I remove all `numCourses` nodes, the graph is acyclic; if some remain, they are stuck in a cycle. Both are O(V + E) time and O(V + E) space. I'd write Kahn's in an interview because it's iterative and also produces a valid course order as a bonus."

**Related problems:**
- [Course Schedule II](https://leetcode.com/problems/course-schedule-ii/) — returns the actual course order, a direct extension of Kahn's algorithm.
- [Minimum Height Trees](https://leetcode.com/problems/minimum-height-trees/) — "peeling leaves" BFS, the undirected analogue of Kahn's in-degree peeling.
- [Find Eventual Safe States](https://leetcode.com/problems/find-eventual-safe-states/) — three-color DFS on a directed graph; same cycle-detection skeleton.
- [Alien Dictionary](https://leetcode.com/problems/alien-dictionary/) — build a directed graph from character orderings and topologically sort it.

## 12. Recall Questions

**Q1. Why does a cycle in the prerequisite graph make it impossible to finish all courses?**
A: In a cycle, every course depends (directly or transitively) on itself. To take any course in the cycle you must first take the one before it, forever — no starting point exists.

**Q2. What does the three-color DFS state mean, and why is a boolean array insufficient?**
A: `0` = unvisited, `1` = on the current recursion stack, `2` = fully processed. A boolean array conflates "on the stack" with "finished", so it cannot distinguish a back-edge (cycle) from a cross-edge to a completed node.

**Q3. In Kahn's algorithm, why does `taken < numCourses` imply a cycle?**
A: Nodes in a cycle always have at least one unprocessed prerequisite within the cycle, so their in-degree never reaches 0 and they are never dequeued. If any node is left undequeued, it must be in (or behind) a cycle.

**Q4. What are the time and space complexities of both approaches, and why are they optimal?**
A: O(V + E) time and O(V + E) space. Cycle detection requires inspecting every node and edge in the worst case, so Ω(V + E) is a lower bound.

**Q5. Does the direction of the edges affect the answer?**
A: No. A directed graph has a cycle iff its edge-reversed graph has a cycle (reversal preserves cycles). However, the direction matters if you also need a valid order, as in Course Schedule II.

**Q6. How would you extend the solution to return a valid course order?**
A: Use Kahn's algorithm and append each dequeued course to a result list. If the list length equals `numCourses`, return it; otherwise return an empty array (cycle).

**Q7. What happens with `numCourses = 1` and no prerequisites?**
A: DFS marks the single node done and returns true; Kahn enqueues it, dequeues it, and `taken = 1 == numCourses` → true.

**Q8. Why is recursion depth safe for the DFS approach here?**
A: The maximum depth is `numCourses <= 2000`, well within the default JVM stack (typically ~10⁴ frames). For much larger `n`, switch to Kahn's iterative BFS.

## 13. Final Code

### Java code (final, optimized Kahn's BFS)

    import java.util.*;

    class Solution {
        public boolean canFinish(int numCourses, int[][] prerequisites) {
            List<List<Integer>> adj = new ArrayList<>();
            for (int i = 0; i < numCourses; i++) adj.add(new ArrayList<>());

            int[] inDegree = new int[numCourses];
            for (int[] p : prerequisites) {
                int course = p[0];
                int prereq = p[1];
                adj.get(prereq).add(course);
                inDegree[course]++;
            }

            Queue<Integer> queue = new LinkedList<>();
            for (int i = 0; i < numCourses; i++) {
                if (inDegree[i] == 0) queue.offer(i);
            }

            int taken = 0;
            while (!queue.isEmpty()) {
                int current = queue.poll();
                taken++;
                for (int next : adj.get(current)) {
                    inDegree[next]--;
                    if (inDegree[next] == 0) queue.offer(next);
                }
            }
            return taken == numCourses;
        }
    }

## 14. Self-Test

Consider `numCourses = 5`, `prerequisites = [[1,0],[2,1],[3,2],[1,3],[4,0]]`. Trace Kahn's algorithm step by step: write the initial in-degree array, the initial queue, and the `taken` count and queue contents after each dequeue. Then answer: which courses are never dequeued, what structure do they form, and why does `taken == numCourses` fail? Finally, explain what the DFS three-color algorithm would report when it first reaches a node that is already marked `1`, and which edge in this example triggers that condition.