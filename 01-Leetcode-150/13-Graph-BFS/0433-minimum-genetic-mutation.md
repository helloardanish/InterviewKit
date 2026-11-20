---
title: "0433 — Minimum Genetic Mutation"
layout: default
parent: Graph BFS
grand_parent: Leetcode
nav_order: 433
permalink: /13-Graph-BFS/0433-minimum-genetic-mutation/

problem: 433 - Minimum Genetic Mutation
url: https://leetcode.com/problems/minimum-genetic-mutation/
difficulty: Medium
section: Graph / BFS
patterns: [bfs, shortest-path, implicit-graph]
data_structures: [string, hash-set, queue]
time: O(B * L * 4)
space: O(B * L)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0433 — Minimum Genetic Mutation

| Field | Value |
|---|---|
| **Pattern** | BFS on Implicit Graph (Shortest Path) |
| **Difficulty** | Medium |
| **Section** | Graph / BFS |
| **Time** | O(B · L · 4) |
| **Space** | O(B · L) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Amazon, Google, Meta, Microsoft, Bloomberg |
| **LeetCode** | [Minimum Genetic Mutation](https://leetcode.com/problems/minimum-genetic-mutation/) |

## 1. Problem in Simple Words

**Given:**
- `startGene`: an 8-character string over `{A, C, G, T}`.
- `endGene`: an 8-character string over `{A, C, G, T}`.
- `bank`: a list of valid gene strings (each 8 characters over `{A, C, G, T}`).

**Required:**
- Return the **minimum number of mutations** to turn `startGene` into `endGene`, or `-1` if impossible.
- One mutation = change exactly **one** character to another of `{A, C, G, T}`.
- **Every intermediate gene must be in `bank`** to be valid. The start gene is assumed valid even if not in `bank`.

**Constraints that actually matter:**
- `0 <= bank.length <= 10` — very small, so even exponential search would technically pass, but the intended solution is BFS.
- All strings are length 8 and use only `A, C, G, T` — the state space is bounded by `4^8 = 65536` possible genes, but only `bank` entries are valid intermediate states.
- `startGene` and `endGene` may not be in `bank`; `startGene` is always valid, `endGene` must be reachable via `bank`.
- No duplicates in `bank` are guaranteed in practice (though the statement does not explicitly say so; a `Set` handles duplicates safely).

## 2. Recognition Signals

- **"Minimum number of mutations"** — the word *minimum* plus a discrete step operation is the canonical **BFS shortest-path** signal.
- The gene string is a **state**, and a mutation is an **edge** between states. Valid states are the `bank` entries (plus the start). This is an **implicit graph**: nodes are strings, edges are one-character mutations.
- The problem is structurally identical to **Word Ladder**: start word, end word, dictionary, one-letter changes, minimum steps.
- Recognition trigger: *"minimum single-character changes from one string to another, intermediate strings must be in a given set"* → **BFS over the set of valid strings**.

## 3. Core Idea

**Intuition.** Treat each gene string as a node. Two genes are connected by an edge if they differ in exactly one position. BFS from `startGene` explores genes in order of mutation count; the first time we reach `endGene`, we have the minimum.

**Key observation.** We never need to enumerate all `4^8` possible genes — only genes in `bank` (plus the start) are valid intermediate states. So the search space is at most `bank.length + 1 ≤ 11` nodes. For each node, we generate neighbors by trying all `8 × 4 = 32` single-character substitutions and checking membership in `bank`.

**Variables.**
- `Set<String> bankSet` — the valid genes, for O(1) membership tests.
- `Set<String> visited` — genes already enqueued.
- `Queue<String> queue` — BFS frontier.
- `int mutations` — BFS level (number of mutations so far).

**Invariant.** BFS explores genes in non-decreasing order of mutations from `startGene`. The first time `endGene` is dequeued (or generated), the current level is the minimum number of mutations.

**Early exit conditions.**
- If `startGene.equals(endGene)`, return `0` immediately.
- If `endGene` is not in `bank`, return `-1` immediately (it can never be an intermediate state, and we cannot land on it).

## 4. Approach 1 — BFS with On-the-Fly Neighbor Generation

**Logic.** BFS from `startGene`. For each dequeued gene, try all 8 positions × 4 nucleotides; if the resulting gene is in `bankSet` and unvisited, mark it visited and enqueue it. If it equals `endGene`, return the current mutation count + 1. If the queue empties, return `-1`.

### Java code (BFS, on-the-fly neighbors)

```java
import java.util.*;

class Solution {
    public int minMutation(String startGene, String endGene, String[] bank) {
        if (startGene.equals(endGene)) return 0;

        Set<String> bankSet = new HashSet<>(Arrays.asList(bank));
        if (!bankSet.contains(endGene)) return -1;

        char[] genes = {'A', 'C', 'G', 'T'};
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();

        queue.offer(startGene);
        visited.add(startGene);
        int mutations = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            mutations++;
            for (int i = 0; i < size; i++) {
                String current = queue.poll();
                char[] chars = current.toCharArray();

                for (int pos = 0; pos < chars.length; pos++) {
                    char original = chars[pos];
                    for (char g : genes) {
                        if (g == original) continue;
                        chars[pos] = g;
                        String next = new String(chars);

                        if (bankSet.contains(next) && visited.add(next)) {
                            if (next.equals(endGene)) return mutations;
                            queue.offer(next);
                        }
                    }
                    chars[pos] = original;
                }
            }
        }
        return -1;
    }
}
```

**Complexity.**
- Time: O(B · L · 4) where `B = bank.length` (≤ 10) and `L = 8`. Each of at most `B+1` genes generates `L × 4 = 32` candidates, each requiring O(L) to build and O(1) average for set lookup.
- Space: O(B · L) for the bank set, visited set, and queue.

**When it is useful.** This is the standard, clean solution. It does not require preprocessing the bank into a graph and works directly on the strings, which is easy to explain in an interview.

## 5. Approach 2 — Optimized: Precompute Adjacency Between Bank Genes

**Algorithm steps.**
1. Build a set of valid genes: `bankSet = bank ∪ {startGene}`.
2. If `endGene` is not in `bankSet`, return `-1`.
3. Precompute an adjacency map: for every pair of genes in `bankSet`, if they differ in exactly one position, add an edge. (Since `B ≤ 10`, this is at most 55 pairs, each compared in O(L).)
4. BFS from `startGene` using the precomputed adjacency. Return the level at which `endGene` is reached, or `-1`.

### Java code (precomputed adjacency)

```java
import java.util.*;

class Solution {
    public int minMutation(String startGene, String endGene, String[] bank) {
        if (startGene.equals(endGene)) return 0;

        Set<String> bankSet = new HashSet<>(Arrays.asList(bank));
        if (!bankSet.contains(endGene)) return -1;
        bankSet.add(startGene); // start is always valid

        List<String> genes = new ArrayList<>(bankSet);
        Map<String, List<String>> adj = new HashMap<>();
        for (String g : genes) adj.put(g, new ArrayList<>());

        for (int i = 0; i < genes.size(); i++) {
            for (int j = i + 1; j < genes.size(); j++) {
                if (differByOne(genes.get(i), genes.get(j))) {
                    adj.get(genes.get(i)).add(genes.get(j));
                    adj.get(genes.get(j)).add(genes.get(i));
                }
            }
        }

        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        queue.offer(startGene);
        visited.add(startGene);
        int mutations = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                String current = queue.poll();
                if (current.equals(endGene)) return mutations;
                for (String next : adj.get(current)) {
                    if (visited.add(next)) {
                        queue.offer(next);
                    }
                }
            }
            mutations++;
        }
        return -1;
    }

    private boolean differByOne(String a, String b) {
        int diff = 0;
        for (int i = 0; i < a.length(); i++) {
            if (a.charAt(i) != b.charAt(i)) {
                if (++diff > 1) return false;
            }
        }
        return diff == 1;
    }
}
```

**Explanation of important logic.**
- **Precomputing adjacency** turns the BFS inner loop into a simple neighbor iteration, removing the 32-candidate generation per node. With `B ≤ 10`, the preprocessing is trivial.
- **`bankSet.add(startGene)`** ensures the start is a node in the graph even if it is not in `bank`. This is required because the start is always valid.
- **`differByOne`** counts mismatches and short-circuits at 2, which is correct because two genes are connected iff they differ in exactly one position.
- **Visited check on enqueue** prevents duplicate queue entries and is safe because BFS explores in non-decreasing mutation count.

**Complexity.**
- Time: O(B² · L) for preprocessing plus O(B + E) for BFS, where `E ≤ B²`. With `B ≤ 10`, this is effectively constant.
- Space: O(B²) for the adjacency map (at most 55 edges) plus O(B · L) for strings.

**Why it is optimal (for this input size).** The search space is at most 11 nodes, so any correct approach is fast. The on-the-fly BFS has time O(B · L · 4) ≈ 10 × 8 × 4 = 320 candidate generations; the precomputed version does at most 55 × 8 = 440 character comparisons upfront and then a trivial BFS. Both are effectively O(1) here; the precomputed version is cleaner for larger `bank` sizes and is the version that scales to Word Ladder–style problems.

## 6. Dry Run

**Example 2:** `startGene = "AACCGGTT"`, `endGene = "AAACGGTA"`, `bank = ["AACCGGTA","AACCGCTA","AAACGGTA"]`.

This example is useful because the optimal path is **two mutations** and requires an intermediate gene (`AACCGCTA` or `AACCGGTA`) that is in the bank, exercising the "intermediate must be valid" rule.

**Graph (edges = differ by exactly one character):**
- `AACCGGTT` (start) ↔ `AACCGGTA` (differ at pos 7: T↔A)
- `AACCGGTT` ↔ `AACCGCTA` (differ at pos 5: G↔C)
- `AACCGGTA` ↔ `AAACGGTA` (differ at pos 2: C↔A)
- `AACCGCTA` ↔ `AAACGGTA` (differ at pos 2 and pos 5? Let's check: `AACCGCTA` vs `AAACGGTA` — pos 2 C↔A, pos 5 G↔G? Actually `AACCGCTA` = A A C C G C T A; `AAACGGTA` = A A A C G G T A; differences at pos 2 (C vs A) and pos 5 (C vs G) → **two** differences, so not adjacent.)

Let me recheck the bank:
- `AACCGGTA` vs `AAACGGTA`: A A C C G G T A vs A A A C G G T A → only pos 2 differs (C vs A) → adjacent.
- `AACCGCTA` vs `AAACGGTA`: A A C C G C T A vs A A A C G G T A → pos 2 (C vs A) and pos 5 (C vs G) differ → not adjacent.

So the only path is `AACCGGTT → AACCGGTA → AAACGGTA`, length 2.

**BFS trace:**

| Level (mutations) | Queue | Dequeued | Neighbors generated (in bank, unvisited) |
|---|---|---|---|
| 1 | `[AACCGGTT]` | `AACCGGTT` | `AACCGGTA`, `AACCGCTA` |
| 1 | `[AACCGGTA, AACCGCTA]` | `AACCGGTA` | `AAACGGTA` (== endGene → return 1+1=2) |

**Final result:** `2`.

## 7. Complexity Summary

| Approach | Time | Space (auxiliary) | Notes |
|---|---|---|---|
| BFS on-the-fly | O(B · L · 4) | O(B · L) | Simple; no preprocessing |
| BFS with precomputed adjacency | O(B² · L) + O(B + E) | O(B² + B · L) | Cleaner inner loop; scales to larger banks |

With `B ≤ 10` and `L = 8`, both are effectively constant. Input space is O(B · L) for the bank and is not counted as auxiliary.

## 8. Edge Cases

| Case | Why it matters | How the code handles it |
|---|---|---|
| `startGene == endGene` | Zero mutations needed | Early return `0` |
| `endGene` not in `bank` | Cannot be an intermediate or final valid state | Early return `-1` |
| Empty `bank` | No valid intermediate states | `bankSet` empty (except start); BFS exhausts immediately → `-1` |
| `startGene` not in `bank` | Start is always valid by assumption | Added to `bankSet` explicitly in the precomputed version; implicitly handled by BFS seeding in the on-the-fly version |
| No path from start to end | Graph disconnected | BFS empties queue → `-1` |
| Bank contains the start gene | Duplicate in set | `Set` deduplicates; harmless |
| Bank contains genes not reachable from start | Irrelevant to the answer | BFS never visits them |
| `endGene` is one mutation from start | Direct edge | BFS returns `1` on the first expansion |
| `startGene` and `endGene` differ in more than one position but no path | Tests that intermediate validity matters | BFS explores only valid intermediates → `-1` |

## 9. Java Notes

- **`String.toCharArray()` + rebuild** is the standard way to mutate one character; `char[] chars = current.toCharArray()`, change `chars[pos]`, then `new String(chars)`. Remember to restore `chars[pos] = original` after trying each nucleotide.
- **`visited.add(next)` returns a boolean** — `if (visited.add(next))` is a compact way to check "was it new?" and mark it visited in one call.
- **`new HashSet<>(Arrays.asList(bank))`** converts a `String[]` to a `Set<String>` in one line. `Set.of(bank)` also works in Java 9+ but throws on duplicates; `Arrays.asList` + `HashSet` is safer.
- **`Queue<String> queue = new LinkedList<>()`** works, but `ArrayDeque` is preferred in production for speed.
- **Early return on `startGene.equals(endGene)`** is important: without it, BFS would still work but would do unnecessary work, and the precomputed version would need the start added to the graph.
- **`endGene` in bank check** is a critical early exit: if `endGene` is not a valid gene, no path can end there.

## 10. Common Mistakes

- **Forgetting to check `endGene` is in `bank`.** If it is not, the answer is `-1`, but a naive BFS might still explore the whole component and eventually return `-1` — correct but slower, and the early check is clearer.
- **Forgetting to add `startGene` to the valid set in the precomputed version.** The start is assumed valid but may not be in `bank`; without adding it, the adjacency map has no node for it.
- **Generating neighbors without checking `bankSet`.** Only genes in `bank` (plus the start) are valid intermediate states; generating arbitrary mutations explores invalid states.
- **Marking visited on dequeue instead of enqueue.** Allows duplicate queue entries and wastes work; mark on enqueue.
- **Forgetting to restore the character** after trying each nucleotide in the on-the-fly version — subsequent positions would see the mutated character.
- **Using DFS instead of BFS.** DFS does not guarantee the minimum number of mutations; BFS does.
- **Returning `mutations` instead of `mutations + 1`** when the neighbor equals `endGene` — off-by-one in the level counting.

## 11. Interview Takeaway

**Recognition trigger to memorize:** *"Minimum single-character changes from one string to another, intermediate strings must be in a given set → BFS over the set of valid strings, generating neighbors by trying all single-character substitutions."*

**90-second explanation:** "This is a shortest-path problem on an implicit graph. Each valid gene string is a node; two genes are connected if they differ in exactly one position. I run BFS from the start gene. For each gene I dequeue, I generate all 32 single-character mutations (8 positions × 4 nucleotides) and check whether the result is in the bank and unvisited. If it is the end gene, I return the current mutation count plus one; otherwise I enqueue it. If the queue empties without reaching the end, I return `-1`. The key insight is that only bank entries are valid intermediate states, so the search space is tiny — at most 11 nodes here. Time is O(B · L · 4) and space is O(B · L), which is effectively constant given the constraints."

**Related problems:**
- [Word Ladder](https://leetcode.com/problems/word-ladder/) — the direct analogue with words and a dictionary; same BFS-on-implicit-graph skeleton.
- [Word Ladder II](https://leetcode.com/problems/word-ladder-ii/) — returns all shortest paths, requiring BFS + backtracking.
- [Open the Lock](https://leetcode.com/problems/open-the-lock/) — BFS over lock combinations, an implicit graph with 4-digit states.
- [Snakes and Ladders](https://leetcode.com/problems/snakes-and-ladders/) — BFS on a board with fixed-step moves and teleports; another implicit-graph shortest path.

## 12. Recall Questions

**Q1. Why is BFS the right algorithm here rather than DFS?**
A: Every mutation is one unweighted edge, and we need the **minimum** number of mutations. BFS explores in non-decreasing distance, so the first time it reaches `endGene` it has found the minimum. DFS does not guarantee that.

**Q2. Why must every intermediate gene be in `bank`, and how does the algorithm enforce it?**
A: The problem defines only bank entries as valid genes. The algorithm enforces this by only enqueuing neighbors that are in `bankSet` (the bank plus the start gene).

**Q3. What happens if `endGene` is not in `bank`?**
A: It can never be a valid intermediate or final state, so the answer is `-1`. The code checks this upfront.

**Q4. What are the time and space complexities, and why are they effectively constant here?**
A: O(B · L · 4) time and O(B · L) space, where `B = bank.length ≤ 10` and `L = 8`. With these bounds, the search space is at most 11 nodes and 32 candidate mutations each, so it is trivially fast.

**Q5. Why do we need a separate `visited` set even though `bankSet` bounds the states?**
A: Without it, BFS could enqueue the same gene multiple times from different predecessors, inflating the queue and potentially causing incorrect level counts. `visited` ensures each gene is enqueued at most once.

**Q6. Why is `startGene` treated as valid even if it is not in `bank`?**
A: The problem explicitly states the starting point is assumed valid. In the precomputed version, we add it to the valid set; in the on-the-fly version, we seed BFS with it directly.

**Q7. What is the difference between the on-the-fly BFS and the precomputed-adjacency BFS?**
A: On-the-fly BFS generates 32 candidate mutations per dequeued gene and checks bank membership. Precomputed BFS builds an adjacency list once (comparing all pairs of valid genes) and then iterates neighbors directly. Both are correct; the precomputed version has a cleaner BFS loop and scales better if `bank` were larger.

**Q8. How does the algorithm handle `startGene == endGene`?**
A: It returns `0` immediately, since zero mutations are needed.

## 13. Final Code

### Java code (final, optimized BFS on-the-fly)

```java
import java.util.*;

class Solution {
    public int minMutation(String startGene, String endGene, String[] bank) {
        if (startGene.equals(endGene)) return 0;

        Set<String> bankSet = new HashSet<>(Arrays.asList(bank));
        if (!bankSet.contains(endGene)) return -1;

        char[] genes = {'A', 'C', 'G', 'T'};
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();

        queue.offer(startGene);
        visited.add(startGene);
        int mutations = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            mutations++;
            for (int i = 0; i < size; i++) {
                String current = queue.poll();
                char[] chars = current.toCharArray();

                for (int pos = 0; pos < chars.length; pos++) {
                    char original = chars[pos];
                    for (char g : genes) {
                        if (g == original) continue;
                        chars[pos] = g;
                        String next = new String(chars);

                        if (bankSet.contains(next) && visited.add(next)) {
                            if (next.equals(endGene)) return mutations;
                            queue.offer(next);
                        }
                    }
                    chars[pos] = original;
                }
            }
        }
        return -1;
    }
}
```

## 14. Self-Test

Consider `startGene = "AACCGGTT"`, `endGene = "AACCGGTA"`, `bank = ["AACCGGTA","AACCGCTA","AAACGGTA"]`. Trace the on-the-fly BFS: for the start gene, list every candidate mutation generated (position, nucleotide, resulting string) and mark which ones are in `bank`. Then answer: at which BFS level is `endGene` first generated, and what is the returned value? Finally, modify the bank to `["AACCGGTA","AACCGCTA","AAACGGTA","AACCGGTC"]` and explain whether the answer changes, and why adding `AACCGGTC` (which is one mutation from the start but not on a path to the end) does or does not affect the result.