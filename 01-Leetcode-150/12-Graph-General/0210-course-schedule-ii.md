---
title: "0210 — Course Schedule II"
layout: default
parent: Graph General
grand_parent: Leetcode
nav_order: 210
permalink: /12-Graph-General/0210-course-schedule-ii/

problem: 210 - Course Schedule II
url: https://leetcode.com/problems/course-schedule-ii/
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

# 0210 — Course Schedule II

| Field | Value |
|---|---|
| **Pattern** | Topological Sort (Kahn's BFS / DFS) |
| **Difficulty** | Medium |
| **Section** | Graph / Topological Sort |
| **Time** | O(V + E) |
| **Space** | O(V + E) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Amazon, Google, Meta, Microsoft, Bloomberg |
| **LeetCode** | [Course Schedule II](https://leetcode.com/problems/course-schedule-ii/) |

## 1. Problem in Simple Words

**Given:**
- `numCourses`: total number of courses labeled `0` to `numCourses - 1`.
- `prerequisites`: a list of pairs `[a, b]` meaning "to take course `a`, you must first take course `b`".

**Required:**
- Return **any valid order** in which all courses can be finished.
- If no such order exists (because of a cycle), return an **empty array**.
- Multiple valid answers may exist; the judge accepts any of them.

**Constraints that actually matter:**
- `1 <= numCourses <= 2000` — small enough for O(V + E) to be instant; large enough that O(V²) is wasteful.
- `0 <= prerequisites.length <= 5000` — sparse graph, adjacency-list friendly.
- `prerequisites[i].length == 2` and `prerequisites[i][0] != prerequisites[i][1]` — no self-loops in the input, but longer cycles are possible.
- All prerequisite pairs are **unique** — no duplicate edges; no need to deduplicate when building the graph.

**Interpretation of the 403 error:** The URL fetch was blocked by LeetCode's anti-bot protection (HTTP 403 Forbidden). The statement above is reconstructed from the well-known LeetCode 210 problem; verify against the live page in a browser or the LeetCode mobile app. Alternatives: search "Course Schedule II" in the LeetCode problems list, or check the problem's discussion tab.

## 2. Recognition Signals

- The problem asks for an **ordering of tasks subject to "before" constraints** — the textbook definition of a **topological sort**.
- "Return any valid order" (not just a boolean) means we must actually construct the order, not merely detect feasibility.
- "If impossible, return an empty array" is exactly the **cycle-detection** condition from Course Schedule I.
- Pattern: **topological sort on a directed graph**, solvable with:
  - **Kahn's algorithm (BFS + in-degree)** — naturally produces the order as nodes are dequeued.
  - **DFS post-order** — push each node onto a result list after all its descendants are processed, then reverse.

## 3. Core Idea

**Intuition.** Model courses as nodes and prerequisite pairs as **directed edges**: `[a, b]` means `b` must come before `a`, so add an edge `b → a`. A valid course order is a **topological order** of this DAG. If the graph contains a cycle, no such order exists.

**Key observation.** Both standard topological-sort algorithms produce the order as a by-product of their cycle detection:
- **Kahn's:** nodes are dequeued in a valid order; if fewer than `numCourses` nodes are dequeued, the remainder are trapped in a cycle.
- **DFS:** a node is appended to the result after all its descendants; reversing the post-order yields a valid topological order. A back-edge (node encountered while still on the recursion stack) signals a cycle.

**Variables (Kahn's version):**
- `List<List<Integer>> adj` — adjacency list `prereq → dependent courses`.
- `int[] inDegree` — number of unmet prerequisites per course.
- `Queue<Integer> queue` — courses ready to be taken.
- `int[] order` / `List<Integer> result` — the topological order being built.

**Variables (DFS version):**
- `int[] state` — `0` unvisited, `1` visiting, `2` visited.
- `List<Integer> result` — post-order accumulation (reversed at the end).

**Invariant (Kahn).** The queue holds exactly the nodes with in-degree 0 among the not-yet-removed nodes. Each dequeue removes a node from the graph and decrements its neighbors' in-degrees, exposing new in-degree-0 nodes.

**Invariant (DFS).** When a node is marked `2`, all its descendants have already been appended to `result`. Thus appending the node after its descendants yields a reverse topological order.

## 4. Approach 1 — DFS Post-Order

**Logic.** Build the adjacency list. Run DFS from each unvisited node with three-color marking. On detecting a back-edge (a neighbor in state `1`), set a cycle flag. After all descendants of a node are processed, append the node to `result`. At the end, reverse `result`; if a cycle was detected, return an empty array.

### Java code (DFS)

```java
import java.util.*;

class Solution {
    private boolean hasCycle = false;

    public int[] findOrder(int numCourses, int[][] prerequisites) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) adj.add(new ArrayList<>());

        for (int[] p : prerequisites) {
            int course = p[0];
            int prereq = p[1];
            adj.get(prereq).add(course); // prereq -> course
        }

        int[] state = new int[numCourses];
        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < numCourses; i++) {
            if (state[i] == 0) dfs(i, adj, state, result);
        }

        if (hasCycle) return new int[0];

        Collections.reverse(result);
        int[] order = new int[numCourses];
        for (int i = 0; i < numCourses; i++) order[i] = result.get(i);
        return order;
    }

    private void dfs(int node, List<List<Integer>> adj, int[] state, List<Integer> result) {
        if (hasCycle) return;
        if (state[node] == 1) { hasCycle = true; return; }
        if (state[node] == 2) return;

        state[node] = 1;
        for (int next : adj.get(node)) {
            dfs(next, adj, state, result);
        }
        state[node] = 2;
        result.add(node);
    }
}
```

**Complexity.**
- Time: O(V + E) — each node and edge is processed once.
- Space: O(V + E) — adjacency list O(V + E), state O(V), result O(V), recursion O(V).

**When it is useful.** DFS is compact and the post-order idea is elegant. It is a natural fit when you already think in terms of "finish all dependencies before emitting a node". Recursion depth is ≤ 2000, safe on the JVM.

## 5. Approach 2 — Optimized: Kahn's Algorithm (BFS)

**Algorithm steps.**
1. Build `adj` with edges `prereq → course`.
2. Compute `inDegree[course]` for every course.
3. Enqueue all courses with `inDegree == 0`.
4. While the queue is non-empty:
   - Dequeue a course, append it to `order`.
   - For each dependent course, decrement its in-degree; if it reaches 0, enqueue it.
5. If `order.length == numCourses`, return `order`; otherwise return an empty array.

### Java code (Kahn's BFS)

```java
import java.util.*;

class Solution {
    public int[] findOrder(int numCourses, int[][] prerequisites) {
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

        int[] order = new int[numCourses];
        int idx = 0;
        while (!queue.isEmpty()) {
            int current = queue.poll();
            order[idx++] = current;
            for (int next : adj.get(current)) {
                inDegree[next]--;
                if (inDegree[next] == 0) queue.offer(next);
            }
        }

        return idx == numCourses ? order : new int[0];
    }
}
```

**Explanation of important logic.**
- **In-degree = number of unmet prerequisites.** A course becomes available exactly when its in-degree hits 0.
- **Order is built naturally.** Every dequeue appends a course whose prerequisites have all been processed — that is precisely a valid topological position.
- **Cycle detection by count.** Nodes in a cycle never reach in-degree 0, so `idx < numCourses` signals a cycle. Returning `new int[0]` matches the required "empty array" output.
- **No separate visited set.** In-degree bookkeeping guarantees each node is enqueued exactly once, so no revisits occur.

**Complexity.**
- Time: O(V + E) — each node enqueued once, each edge relaxed once.
- Space: O(V + E) — adjacency list, in-degree array, queue, and output array.

**Why it is optimal.** Topological sorting requires inspecting every node and edge, so Ω(V + E) is a lower bound; Kahn's algorithm meets it. It is **iterative** (no stack-depth concerns) and produces the order directly without a final reversal. This makes it the preferred interview answer.

## 6. Dry Run

**Example 1 (acyclic):** `numCourses = 4`, `prerequisites = [[1,0],[2,0],[3,1],[3,2]]`.

Graph: `0 → 1`, `0 → 2`, `1 → 3`, `2 → 3`. In-degrees: `[0, 1, 1, 2]`.

This example is useful because it is a **diamond** with two paths from 0 to 3, testing that in-degree bookkeeping handles multi-path dependencies correctly.

| Step | Queue | current | order so far | in-degree after decrements |
|---|---|---|---|---|
| init | `[0]` | — | `[]` | `[0,1,1,2]` |
| 1 | `[1,2]` | 0 | `[0]` | 1→0, 2→0 → `[0,0,0,2]` |
| 2 | `[2]` | 1 | `[0,1]` | 3→1 → `[0,0,0,1]` |
| 3 | `[3]` | 2 | `[0,1,2]` | 3→0 → `[0,0,0,0]` |
| 4 | `[]` | 3 | `[0,1,2,3]` | no neighbors |

`idx = 4 == numCourses` → return `[0,1,2,3]`. (Another valid answer, e.g. `[0,2,1,3]`, would also be accepted.)

**Example 2 (cyclic):** `numCourses = 2`, `prerequisites = [[1,0],[0,1]]`.

Graph: `0 → 1`, `1 → 0`. In-degrees `[1,1]`.

Initial queue is empty (no in-degree 0). Loop never runs; `idx = 0 < 2` → return `[]` (empty array).

**DFS trace on the cyclic example:** start at 0 → state[0]=1 → neighbor 1 → state[1]=1 → neighbor 0 is state 1 → `hasCycle = true` → return empty array.

## 7. Complexity Summary

| Approach | Time | Space (auxiliary) | Notes |
|---|---|---|---|
| DFS post-order | O(V + E) | O(V + E) | Requires final reverse; recursion depth ≤ V |
| Kahn's BFS | O(V + E) | O(V + E) | Iterative; order built directly; preferred in interviews |

Input space is O(V + E) for the prerequisites array; the adjacency list is O(V + E) auxiliary. Both are asymptotically optimal.

## 8. Edge Cases

| Case | Why it matters | How the code handles it |
|---|---|---|
| `numCourses = 1`, no prerequisites | Trivial order `[0]` | Kahn enqueues 0, dequeues it, `idx = 1` → returns `[0]` |
| No prerequisites at all | Any permutation is valid | All in-degrees 0; queue holds all; returns `[0,1,…,n-1]` |
| Two-node cycle `[1,0],[0,1]` | No valid order exists | Queue starts empty or drains without reaching `n`; returns `[]` |
| Disconnected components | Each component is topologically sorted independently | Kahn merges them through the shared queue; DFS loops over all roots |
| Deep chain `0→1→2→…→n` | Deep recursion for DFS | Depth ≤ 2000, safe; Kahn unaffected |
| Duplicate prerequisite pairs | Excluded by uniqueness, would inflate in-degree | Not a concern under the guarantee; Kahn assumes unique edges |
| Self-loop `[a, a]` | Would be an immediate cycle | Excluded by constraints; both algorithms detect it correctly |

## 9. Java Notes

- **Initialize every inner list** in `adj` before use, or `adj.get(x)` throws `IndexOutOfBoundsException`.
- **DFS needs three states, not a boolean array.** `int[] state` with `0/1/2` distinguishes "on the stack" from "finished"; a boolean conflates them and misses back-edges.
- **Post-order + reverse** is the DFS idiom for topological sort. Appending before reversing would give reverse topological order.
- **`int[] order` with an index pointer** in Kahn's version is more efficient than a `List<Integer>` + conversion, and avoids boxing.
- **`LinkedList` vs `ArrayDeque`:** `LinkedList` works as a queue but `ArrayDeque` is preferred in production for speed.
- **Return `new int[0]`** for the cyclic case, not `null` — the judge expects an array.
- **Recursion depth** ≤ `numCourses = 2000`, safe under the default JVM stack.

## 10. Common Mistakes

- **Forgetting to reverse the DFS post-order.** Without the reversal, the output is the reverse topological order and is wrong.
- **Using a boolean visited array in DFS.** Conflates "on stack" with "finished", so back-edges are missed.
- **Kahn's: enqueuing in-degree-0 nodes only once at the start.** Neighbors whose in-degree drops to 0 during processing must also be enqueued.
- **Kahn's: returning `order` even when `idx < numCourses`.** The incomplete array contains garbage zeros and is invalid; must return an empty array.
- **Reversing the edge direction.** `[a, b]` means `b → a`. Reversing it still detects cycles but produces the reverse order, which may be judged wrong (prerequisites must come first).
- **Forgetting `state[node] = 2`** after processing a DFS node — causes re-exploration and false cycle detection on cross-edges.
- **Not handling `numCourses = 1` with no prerequisites** — should return `[0]`, not an empty array.

## 11. Interview Takeaway

**Recognition trigger to memorize:** *"Return an ordering of tasks subject to 'must come before' constraints → topological sort the directed graph; Kahn's BFS produces the order directly and detects cycles when fewer than V nodes are dequeued."*

**90-second explanation:** "This is topological sort on a directed graph. Each course is a node; a prerequisite pair `[a, b]` becomes a directed edge `b → a`, meaning b must come before a. I use Kahn's algorithm: compute each node's in-degree (number of unmet prerequisites), enqueue all nodes with in-degree 0, then repeatedly dequeue a node, append it to the order, and decrement its dependents' in-degrees — enqueuing any that reach 0. If I dequeue all V nodes, the order is valid; if some remain, they are trapped in a cycle and I return an empty array. Time and space are both O(V + E), which is optimal since every node and edge must be inspected. The DFS alternative uses three-color marking and a reversed post-order, but Kahn's is iterative and builds the order directly, so I prefer it in interviews."

**Related problems:**
- [Course Schedule](https://leetcode.com/problems/course-schedule/) — the feasibility-only version; identical graph, boolean answer.
- [Minimum Height Trees](https://leetcode.com/problems/minimum-height-trees/) — leaf-peeling BFS, the undirected analogue of in-degree peeling.
- [Find Eventual Safe States](https://leetcode.com/problems/find-eventual-safe-states/) — three-color DFS on a directed graph.
- [Alien Dictionary](https://leetcode.com/problems/alien-dictionary/) — build a graph from character orderings and topologically sort it.

## 12. Recall Questions

**Q1. Why does a valid course order exist iff the prerequisite graph is a DAG?**
A: A topological order requires every edge to point forward. A cycle forces some node to precede itself, which is impossible; conversely, every DAG admits a topological order.

**Q2. In Kahn's algorithm, what exactly does `idx < numCourses` at the end signify?**
A: Some nodes were never dequeued, meaning their in-degree never reached 0 — they are in (or depend on) a cycle. No valid order exists.

**Q3. Why does DFS append nodes in post-order and then reverse?**
A: Post-order appends a node only after all its descendants, so descendants appear before the node. Reversing makes prerequisites come before dependents — a valid topological order.

**Q4. What are the time and space complexities, and why are they optimal?**
A: O(V + E) time and space. Topological sort must inspect every node and edge, so Ω(V + E) is a lower bound.

**Q5. How does Kahn's algorithm handle multiple valid answers?**
A: Any order of dequeuing among simultaneously available (in-degree 0) nodes is valid. The queue's arbitrary order produces one valid answer; the judge accepts any.

**Q6. Why is a boolean visited array insufficient for the DFS cycle check?**
A: It cannot distinguish "currently on the recursion stack" from "finished". A back-edge (cycle) is a neighbor on the stack; without the third state, it is mistaken for an already-finished node.

**Q7. What does the code return when a cycle exists, and why?**
A: `new int[0]` (empty array), because the problem specifies returning an empty array when no valid order exists. Returning `null` would fail the judge.

**Q8. Could you solve this without an explicit in-degree array?**
A: Kahn's algorithm fundamentally needs in-degree counts. DFS does not — it uses the three-color state instead. So the choice of algorithm determines the auxiliary structures.

## 13. Final Code

### Java code (final, optimized Kahn's BFS)

    import java.util.*;

    class Solution {
        public int[] findOrder(int numCourses, int[][] prerequisites) {
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

            int[] order = new int[numCourses];
            int idx = 0;
            while (!queue.isEmpty()) {
                int current = queue.poll();
                order[idx++] = current;
                for (int next : adj.get(current)) {
                    inDegree[next]--;
                    if (inDegree[next] == 0) queue.offer(next);
                }
            }

            return idx == numCourses ? order : new int[0];
        }
    }

## 14. Self-Test

Consider `numCourses = 6`, `prerequisites = [[1,0],[2,1],[3,2],[4,2],[5,3],[5,4]]`. Trace Kahn's algorithm: write the initial in-degree array, the initial queue, and the order built after each dequeue. Then answer: is the produced order unique? If not, name two distinct valid orders. Finally, modify the input by adding `[1,5]` and explain exactly where Kahn's algorithm stalls — which nodes remain undequeued, what in-degrees they have, and why the function returns an empty array.