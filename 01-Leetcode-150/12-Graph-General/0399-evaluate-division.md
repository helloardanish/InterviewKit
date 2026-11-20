---
title: "0399 — Evaluate Division"
layout: default
parent: Graph General
grand_parent: Leetcode
nav_order: 399
permalink: /12-Graph-General/0399-evaluate-division/

problem: 399 - Evaluate Division
url: https://leetcode.com/problems/evaluate-division/
difficulty: Medium
section: Graph / Weighted Traversal
patterns: [graph-traversal, dfs, bfs, union-find, weighted-graph]
data_structures: [graph, hash-map, set]
time: O(E + Q * (V + E))
space: O(V + E)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0399 — Evaluate Division

| Field | Value |
|---|---|
| **Pattern** | Weighted Graph Traversal (DFS / BFS) |
| **Difficulty** | Medium |
| **Section** | Graph / Weighted Traversal |
| **Time** | O(E + Q * (V + E)) |
| **Space** | O(V + E) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Meta, Bloomberg, Uber |
| **LeetCode** | [Evaluate Division](https://leetcode.com/problems/evaluate-division/) |

## 1. Problem in Simple Words

**Given:**
- `equations`: a list of pairs `[Ai, Bi]`.
- `values`: a list of doubles where `values[i]` is the value of `Ai / Bi`.
- `queries`: a list of pairs `[Cj, Dj]`.

**Required:**
- For each query `[Cj, Dj]`, return the value of `Cj / Dj`.
- If the answer cannot be determined from the given equations, return `-1.0`.
- The input is always valid (no contradictions), and variables are strings.

**Constraints that actually matter:**
- `1 <= equations.length <= 20` and `1 <= queries.length <= 20` — the graph is tiny, so even a naive per-query traversal is fast enough.
- `values[i] > 0` — division is well-defined and ratios are positive, which matters for multiplying along paths.
- Variables are arbitrary strings (not just single letters) — so we must key the graph by `String`, not by index.
- Queries may reference variables that never appear in `equations` — those must return `-1.0`.
- The equations are consistent — we can treat each equation as a directed weighted edge and never worry about conflicting paths.

## 2. Recognition Signals

- The problem is about **ratios** (`A / B = k`) and composing them (`A / C = A / B * B / C`). This is exactly **multiplicative transitivity** along a path in a graph.
- Variables are nodes; each equation `A / B = k` gives two directed edges: `A → B` with weight `k`, and `B → A` with weight `1 / k`.
- A query `C / D` is answerable iff `C` and `D` are in the **same connected component**; the answer is the product of edge weights along any path from `C` to `D`.
- Pattern: **weighted graph + path product**. Either build the graph and answer each query with DFS/BFS, or use **weighted Union-Find** to precompute ratios to a component root.

## 3. Core Idea

**Intuition.** Think of every variable as a node. The equation `A / B = k` means "moving from A to B multiplies by k". If we can walk from `C` to `D` through a chain of such edges, the answer is the product of the weights along that walk. If no walk exists, the ratio is undetermined → `-1.0`.

**Key observation.** Because the equations are consistent, the product along *any* path between two nodes in the same component gives the same ratio. So we only need to find *one* path, not the shortest or cheapest — a simple DFS or BFS suffices.

**Variables:**
- `Map<String, Map<String, Double>> graph` — adjacency list where `graph.get(a).get(b)` is the value of `a / b`.
- `Set<String> visited` — per-query visited set to avoid cycles.
- `double product` — running product along the current path.

**Algorithm invariant (DFS).** `dfs(current, target, acc)` returns the product of weights along a path from `current` to `target` multiplied by `acc`, or `-1.0` if no path exists. When `current == target`, it returns `acc`.

**Edge construction.**
- For each `i`: `graph[A].put(B, k)` and `graph[B].put(A, 1.0 / k)`.

## 4. Approach 1 — DFS per Query

**Logic.** Build the weighted graph once. For each query `[C, D]`:
- If either `C` or `D` is not in the graph, return `-1.0`.
- If `C == D`, return `1.0` (a variable divided by itself).
- Otherwise run DFS from `C` looking for `D`, carrying the running product, with a per-query visited set to avoid revisiting nodes.

### Java code (DFS)

```java
    import java.util.*;

    class Solution {
        public double[] calcEquation(List<List<String>> equations, double[] values, List<List<String>> queries) {
            // Build weighted graph.
            Map<String, Map<String, Double>> graph = new HashMap<>();
            for (int i = 0; i < equations.size(); i++) {
                String a = equations.get(i).get(0);
                String b = equations.get(i).get(1);
                double k = values[i];
                graph.computeIfAbsent(a, x -> new HashMap<>()).put(b, k);
                graph.computeIfAbsent(b, x -> new HashMap<>()).put(a, 1.0 / k);
            }

            double[] result = new double[queries.size()];
            for (int i = 0; i < queries.size(); i++) {
                String c = queries.get(i).get(0);
                String d = queries.get(i).get(1);
                if (!graph.containsKey(c) || !graph.containsKey(d)) {
                    result[i] = -1.0;
                } else if (c.equals(d)) {
                    result[i] = 1.0;
                } else {
                    Set<String> visited = new HashSet<>();
                    result[i] = dfs(graph, c, d, 1.0, visited);
                }
            }
            return result;
        }

        private double dfs(Map<String, Map<String, Double>> graph,
                           String current, String target,
                           double product, Set<String> visited) {
            if (current.equals(target)) return product;
            visited.add(current);
            for (Map.Entry<String, Double> entry : graph.get(current).entrySet()) {
                String next = entry.getKey();
                if (visited.contains(next)) continue;
                double ans = dfs(graph, next, target, product * entry.getValue(), visited);
                if (ans != -1.0) return ans;
            }
            return -1.0;
        }
    }
```

**Complexity.**
- Build: O(E).
- Each query: O(V + E) for the DFS in the worst case.
- Total: O(E + Q * (V + E)). With `E, Q <= 20`, this is trivially fast.

**When it is useful.** DFS is the most direct way to express the idea "multiply weights along a path". It is easy to write and reason about, and it handles disconnected components naturally (the DFS simply fails and returns `-1.0`).

## 5. Approach 2 — Optimized: BFS per Query

**Logic.** Same graph, but answer each query with BFS. Start from `C` with product `1.0`, expand neighbors level by level, and stop when `D` is reached. BFS avoids recursion and makes the product bookkeeping explicit via a queue of `(node, product)` pairs.

### Java code (BFS)

```java
import java.util.*;

class Solution {
    public double[] calcEquation(List<List<String>> equations, double[] values, List<List<String>> queries) {
        Map<String, Map<String, Double>> graph = new HashMap<>();
        for (int i = 0; i < equations.size(); i++) {
            String a = equations.get(i).get(0);
            String b = equations.get(i).get(1);
            double k = values[i];
            graph.computeIfAbsent(a, x -> new HashMap<>()).put(b, k);
            graph.computeIfAbsent(b, x -> new HashMap<>()).put(a, 1.0 / k);
        }

        double[] result = new double[queries.size()];
        for (int i = 0; i < queries.size(); i++) {
            String c = queries.get(i).get(0);
            String d = queries.get(i).get(1);
            if (!graph.containsKey(c) || !graph.containsKey(d)) {
                result[i] = -1.0;
                continue;
            }
            if (c.equals(d)) {
                result[i] = 1.0;
                continue;
            }
            result[i] = bfs(graph, c, d);
        }
        return result;
    }

    private double bfs(Map<String, Map<String, Double>> graph, String start, String target) {
        Queue<Pair> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.offer(new Pair(start, 1.0));
        visited.add(start);

        while (!queue.isEmpty()) {
            Pair p = queue.poll();
            String node = p.node;
            double product = p.product;

            if (node.equals(target)) return product;

            for (Map.Entry<String, Double> entry : graph.get(node).entrySet()) {
                String next = entry.getKey();
                if (visited.contains(next)) continue;
                visited.add(next);
                queue.offer(new Pair(next, product * entry.getValue()));
            }
        }
        return -1.0;
    }

    private static class Pair {
        String node;
        double product;
        Pair(String node, double product) {
            this.node = node;
            this.product = product;
        }
    }
}
```

**Explanation of important logic.**
- **Visited marking on enqueue, not dequeue.** Marking when we push a node prevents it from being enqueued multiple times, keeping the queue size bounded.
- **Early return on match.** BFS returns as soon as it dequeues the target, which is slightly more efficient than exploring the whole component.
- **Product carried in the queue element.** Each queue entry stores the cumulative product from the start to that node, so no separate path reconstruction is needed.

**Complexity.**
- Same as DFS: O(E + Q * (V + E)) time, O(V + E) space.
- BFS avoids recursion depth concerns, but here `V <= 40`, so either is fine.

**Why it is optimal (for this input size).** Each query genuinely requires exploring the component in the worst case, so O(V + E) per query is a lower bound for a traversal-based approach. The alternative **weighted Union-Find** achieves near O(1) per query after O(E α(V)) preprocessing, but it is more code and more error-prone; for `Q <= 20` the difference is negligible. The traversal approach is the interview-ready choice.

## 6. Dry Run

**Example:**
- `equations = [["a","b"],["b","c"]]`
- `values = [2.0, 3.0]`
- `queries = [["a","c"],["b","a"],["a","e"],["a","a"],["x","x"]]`

This example is useful because it exercises a **multi-hop path** (`a → b → c`), a **reverse edge** (`b / a`), an **unknown variable** (`e`), and the **self-division** cases (`a / a`, `x / x`).

**Graph built:**
- `a → b : 2.0`, `b → a : 0.5`
- `b → c : 3.0`, `c → b : 1/3`

**Query 1: `a / c` — DFS from `a` to `c`.**

| Step | current | product | visited | Action |
|---|---|---|---|---|
| 1 | a | 1.0 | {a} | a ≠ c; try b with 1.0 * 2.0 |
| 2 | b | 2.0 | {a, b} | b ≠ c; try c with 2.0 * 3.0 |
| 3 | c | 6.0 | {a, b, c} | c == target → return 6.0 |

Result: `6.0`.

**Query 2: `b / a` — DFS from `b` to `a`.**

| Step | current | product | visited | Action |
|---|---|---|---|---|
| 1 | b | 1.0 | {b} | b ≠ a; try a with 1.0 * 0.5 |
| 2 | a | 0.5 | {b, a} | a == target → return 0.5 |

Result: `0.5`.

**Query 3: `a / e`.** `e` is not in the graph → `-1.0`.

**Query 4: `a / a`.** `c.equals(d)` → `1.0`.

**Query 5: `x / x`.** `x` not in graph → `-1.0`.

**Final result:** `[6.0, 0.5, -1.0, 1.0, -1.0]`.

## 7. Complexity Summary

| Approach | Preprocessing | Per Query | Total | Space | Notes |
|---|---|---|---|---|---|
| DFS | O(E) | O(V + E) | O(E + Q(V + E)) | O(V + E) | Simplest; recursion depth ≤ V |
| BFS | O(E) | O(V + E) | O(E + Q(V + E)) | O(V + E) | No recursion; explicit queue |
| Weighted Union-Find | O(E α(V)) | O(α(V)) | O((E + Q) α(V)) | O(V) | Fastest asymptotically; more complex |

Input space is O(E + Q) and is not counted as auxiliary. With the given constraints (`E, Q <= 20`), all three are fast; the traversal approach is the best balance of clarity and speed.

## 8. Edge Cases

| Case | Why it matters | How the code handles it |
|---|---|---|
| Query variable absent from graph | Must return `-1.0`, not crash | `!graph.containsKey(c) || !graph.containsKey(d)` check before traversal |
| Query `[x, x]` where `x` is unknown | Self-division of an unknown variable is still unknown | The `containsKey` check runs first, returns `-1.0` |
| Query `[a, a]` where `a` is known | Trivially `1.0` | `c.equals(d)` short-circuit after the presence check |
| Disconnected variables (no path) | DFS/BFS exhausts the component and returns `-1.0` | Traversal fails gracefully |
| Equation with `values[i] = 0` | Excluded by constraints (`values[i] > 0`); otherwise `1/k` would be infinite | Not a concern here, but note the code assumes `k > 0` |
| Duplicate equations for the same pair | Excluded by "consistent" guarantee; otherwise the last write would win | Not handled specially; safe under the guarantee |
| Chain of length > 1 (e.g., `a/b`, `b/c`, `c/d`) | Tests product accumulation along a path | DFS/BFS multiply edge weights cumulatively |

## 9. Java Notes

- **`computeIfAbsent`** is the clean way to build the nested map: `graph.computeIfAbsent(a, x -> new HashMap<>()).put(b, k)`.
- **`Double` unboxing in `Map<String, Double>`** — iterating `entry.getValue()` autoboxes; for tiny inputs this is negligible, but be aware that `entry.getValue()` returns a `Double`, which unboxes to `double` in the multiplication.
- **Comparing doubles:** never use `==` on computed doubles. Here we only compare `ans != -1.0` and `node.equals(target)` (string comparison), both of which are safe. The sentinel `-1.0` is exact and cannot collide with a real ratio because all ratios are positive.
- **`Pair` class:** Java has no built-in tuple, so define a small static nested class (as in the BFS version) or use `AbstractMap.SimpleEntry<String, Double>`.
- **`ArrayDeque` vs `LinkedList`:** prefer `ArrayDeque` for the queue in production; `LinkedList` is fine here and common in tutorials.
- **String keys, not indices:** variables are strings, so the graph must be `Map<String, ...>`. Do not try to map variables to integer indices unless you want extra bookkeeping.

## 10. Common Mistakes

- **Forgetting the reverse edge.** Only adding `A → B` with weight `k` makes queries like `B / A` fail. Always add both directions (`k` and `1/k`).
- **Using a global visited set across queries.** The visited set must be **per query**, otherwise a node visited in query 1 blocks exploration in query 2.
- **Not checking `c == d` before traversal.** Without the short-circuit, `dfs(a, a)` would still work (it returns `product = 1.0` immediately), but the presence check must come first so that `[x, x]` for an unknown `x` returns `-1.0`, not `1.0`.
- **Returning `0.0` instead of `-1.0`** for an undetermined ratio — `0.0` is a valid ratio in general and would be wrong here.
- **Marking visited after the recursive call** instead of before — can cause infinite loops in cyclic graphs.
- **Integer division** — using `1 / k` with integer `k` truncates to 0; always use `1.0 / k`.

## 11. Interview Takeaway

**Recognition trigger to memorize:** *"Ratios that compose multiplicatively (`A/B = k`) → model as a weighted directed graph with edges `A→B = k` and `B→A = 1/k`; answer each query by multiplying weights along a path from source to target (DFS/BFS), or use weighted Union-Find for faster queries."*

**90-second explanation:** "Each variable is a node. The equation `A / B = k` gives a directed edge `A → B` with weight `k` and a reverse edge `B → A` with weight `1/k`. A query `C / D` is answerable exactly when there's a path from `C` to `D` in this graph, and the answer is the product of the edge weights along that path. I build the graph once, then for each query I check whether both variables exist — if not, return `-1.0`; if they're equal, return `1.0`; otherwise run DFS or BFS from `C` to `D` carrying a running product and a per-query visited set. If the traversal exhausts the component without finding `D`, I return `-1.0`. Time is O(E + Q(V+E)), space is O(V+E)."

**Related problems:**
- [Redundant Connection](https://leetcode.com/problems/redundant-connection/) — undirected graph cycle detection with Union-Find; same graph-building instinct.
- [Accounts Merge](https://leetcode.com/problems/accounts-merge/) — Union-Find over string-keyed components.
- [Number of Connected Components in an Undirected Graph](https://leetcode.com/problems/number-of-connected-components-in-an-undirected-graph/) — component reasoning that underpins the "same component" check here.
- [Course Schedule](https://leetcode.com/problems/course-schedule/) — directed-graph traversal with visited-state management.

## 12. Recall Questions

**Q1. Why do we add two directed edges for each equation?**
A: Because `A / B = k` implies `B / A = 1/k`. Without the reverse edge, queries in the opposite direction would have no path.

**Q2. Why must the visited set be per-query rather than global?**
A: A global set would block exploration of nodes already visited in a previous query, causing false `-1.0` results. Each query is an independent traversal.

**Q3. What is the time complexity, and what dominates it?**
A: O(E + Q(V + E)). Building the graph is O(E); each of the Q queries costs up to O(V + E) for a full traversal of the component.

**Q4. Why is `-1.0` a safe sentinel?**
A: All ratios are positive (since `values[i] > 0`), so `-1.0` can never be a legitimate answer. It cleanly signals "undetermined".

**Q5. How does the code handle `[x, x]` where `x` is not in any equation?**
A: The `containsKey` check runs before the `c.equals(d)` short-circuit, so it returns `-1.0` — an unknown variable divided by itself is still unknown.

**Q6. Why is the product along any path between two nodes the same?**
A: Because the equations are consistent (no contradictions). Any two paths between the same nodes must yield the same ratio, so we only need to find one path.

**Q7. What would weighted Union-Find change?**
A: It would precompute each variable's ratio to its component root, making each query O(α(V)) instead of O(V + E). It is faster asymptotically but more complex; for `Q <= 20` the traversal approach is preferable.

**Q8. How would you handle a query where `C` and `D` are in different components?**
A: The DFS/BFS from `C` exhausts its component without reaching `D`, so it returns `-1.0` naturally — no extra component check is needed.

## 13. Final Code

### Java code (final, optimized DFS)

```java
import java.util.*;

class Solution {
    public double[] calcEquation(List<List<String>> equations, double[] values, List<List<String>> queries) {
        Map<String, Map<String, Double>> graph = new HashMap<>();
        for (int i = 0; i < equations.size(); i++) {
            String a = equations.get(i).get(0);
            String b = equations.get(i).get(1);
            double k = values[i];
            graph.computeIfAbsent(a, x -> new HashMap<>()).put(b, k);
            graph.computeIfAbsent(b, x -> new HashMap<>()).put(a, 1.0 / k);
        }

        double[] result = new double[queries.size()];
        for (int i = 0; i < queries.size(); i++) {
            String c = queries.get(i).get(0);
            String d = queries.get(i).get(1);
            if (!graph.containsKey(c) || !graph.containsKey(d)) {
                result[i] = -1.0;
            } else if (c.equals(d)) {
                result[i] = 1.0;
            } else {
                result[i] = dfs(graph, c, d, 1.0, new HashSet<>());
            }
        }
        return result;
    }

    private double dfs(Map<String, Map<String, Double>> graph,
                        String current, String target,
                        double product, Set<String> visited) {
        if (current.equals(target)) return product;
        visited.add(current);
        for (Map.Entry<String, Double> entry : graph.get(current).entrySet()) {
            String next = entry.getKey();
            if (visited.contains(next)) continue;
            double ans = dfs(graph, next, target, product * entry.getValue(), visited);
            if (ans != -1.0) return ans;
        }
        return -1.0;
    }
}
```

## 14. Self-Test

Consider `equations = [["a","b"],["b","c"],["d","e"]]`, `values = [2.0, 3.0, 4.0]`, and queries `[["a","c"],["c","a"],["a","e"],["e","d"],["d","a"]]`. Trace the DFS for each query, showing the path taken, the running product at each step, and the final returned value. Then answer: for the query `["a","e"]`, exactly where does the traversal terminate and why does it return `-1.0` rather than exploring the `d`–`e` component? Finally, explain what would change in the answer if the visited set were accidentally shared across all five queries instead of being created fresh per query.