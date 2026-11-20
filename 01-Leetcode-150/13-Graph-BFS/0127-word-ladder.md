---
title: "0127 — Word Ladder"
layout: default
parent: Graph BFS
grand_parent: Leetcode
nav_order: 127
permalink: /13-Graph-BFS/0127-word-ladder/

problem: 127 - Word Ladder
url: https://leetcode.com/problems/word-ladder/
difficulty: Hard
section: Graph / BFS
patterns: [bfs, shortest-path, implicit-graph, bidirectional-bfs]
data_structures: [string, hash-set, queue]
time: O(N * L^2 * 26)
space: O(N * L)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0127 — Word Ladder

| Field | Value |
|---|---|
| **Pattern** | BFS on Implicit Graph (Shortest Path) |
| **Difficulty** | Hard |
| **Section** | Graph / BFS |
| **Time** | O(N · L² · 26) |
| **Space** | O(N · L) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Amazon, Google, Meta, Microsoft, Bloomberg |
| **LeetCode** | [Word Ladder](https://leetcode.com/problems/word-ladder/) |

## 1. Problem in Simple Words

**Given:**
- `beginWord`: the starting word.
- `endWord`: the target word.
- `wordList`: a list of valid intermediate words (a dictionary).

**Required:**
- Return the **number of words** in the shortest transformation sequence from `beginWord` to `endWord`, or `0` if no such sequence exists.
- A transformation sequence is `beginWord → s₁ → s₂ → … → sₖ` where:
  - Every adjacent pair differs by **exactly one letter**.
  - Every `sᵢ` is in `wordList`.
  - `sₖ == endWord`.
- Note: `beginWord` does **not** need to be in `wordList`, but `endWord` **must** be.

**Constraints that actually matter:**
- `1 <= beginWord.length <= 10` — words are short, so generating all single-letter variants is cheap (at most `10 × 26 = 260` per word).
- `1 <= wordList.length <= 5000` — the dictionary is moderately large; an O(N²) pairwise comparison (≈ 25 million) is borderline, and the intended solution avoids it.
- All words have the same length and consist of lowercase English letters — this is what makes the "change one letter" operation well-defined.
- `beginWord != endWord` — no trivial zero-step case.
- All words in `wordList` are unique — no duplicate handling needed.

## 2. Recognition Signals

- **"Shortest transformation sequence"** — the word *shortest* plus a discrete step operation is the canonical **BFS shortest-path** signal.
- Each word is a **state**; an edge connects two words that differ by one letter. This is an **implicit graph**: nodes are words, edges are one-letter changes.
- The dictionary defines the valid state space — only words in `wordList` (plus the start) are usable as intermediate states.
- Recognition trigger: *"minimum single-letter changes from one word to another, intermediate words must be in a given dictionary"* → **BFS over the dictionary**, generating neighbors by trying all single-letter substitutions (or by precomputing wildcard patterns).
- This is structurally identical to **Minimum Genetic Mutation** (LeetCode 433), just with a larger alphabet (26 vs 4) and a bigger dictionary.

## 3. Core Idea

**Intuition.** Treat each word as a node. Two words are connected if they differ in exactly one position. BFS from `beginWord` explores words in order of transformation length; the first time we reach `endWord`, we have the shortest sequence.

**Key observation.** We never need to compare every pair of words (which would be O(N² · L)). Instead, for each word we generate all `L × 26` single-letter variants and check membership in the dictionary set. This is O(L · 26) per word, far cheaper than pairwise comparison when `N` is large.

**An alternative optimization: wildcard patterns.** Replace each position with a wildcard (`*`) to create pattern keys. Words sharing a pattern are one mutation apart. This groups neighbors efficiently but uses more memory. Both approaches are valid; the variant-generation approach is simpler and standard in interviews.

**Variables.**
- `Set<String> wordSet` — the dictionary, for O(1) membership tests.
- `Set<String> visited` — words already enqueued.
- `Queue<String> queue` — BFS frontier.
- `int steps` — BFS level (number of words in the sequence so far).

**Invariant.** BFS explores words in non-decreasing order of transformation length. The first time `endWord` is dequeued (or generated), the current level is the minimum sequence length.

**Early exit conditions.**
- If `endWord` is not in `wordSet`, return `0` immediately — it can never be reached.
- If `beginWord.equals(endWord)`, return `1` (though the constraints exclude this).

## 4. Approach 1 — BFS with On-the-Fly Neighbor Generation

**Logic.** BFS from `beginWord`. For each dequeued word, try all `L` positions × 26 letters; if the resulting word is in `wordSet` and unvisited, mark it visited and enqueue it. If it equals `endWord`, return the current step count + 1. If the queue empties, return `0`.

### Java code (BFS, on-the-fly neighbors)

```java
import java.util.*;

class Solution {
    public int ladderLength(String beginWord, String endWord, List<String> wordList) {
        Set<String> wordSet = new HashSet<>(wordList);
        if (!wordSet.contains(endWord)) return 0;

        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.offer(beginWord);
        visited.add(beginWord);

        int steps = 1;
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                String current = queue.poll();
                if (current.equals(endWord)) return steps;

                char[] chars = current.toCharArray();
                for (int pos = 0; pos < chars.length; pos++) {
                    char original = chars[pos];
                    for (char c = 'a'; c <= 'z'; c++) {
                        if (c == original) continue;
                        chars[pos] = c;
                        String next = new String(chars);
                        if (wordSet.contains(next) && visited.add(next)) {
                            queue.offer(next);
                        }
                    }
                    chars[pos] = original;
                }
            }
            steps++;
        }
        return 0;
    }
}
```

**Complexity.**
- Time: O(N · L · 26 · L) — for each of at most `N` words, we generate `L × 26` candidates, each costing O(L) to build. This is O(N · L² · 26).
- Space: O(N · L) for the word set, visited set, and queue.

**When it is useful.** This is the standard, clean solution. It avoids the O(N²) pairwise comparison and is easy to explain in an interview.

## 5. Approach 2 — Optimized: Bidirectional BFS

**Algorithm steps.**
1. Build the word set; check `endWord` membership.
2. Maintain two frontier sets: `front` (from `beginWord`) and `back` (from `endWord`).
3. At each step, expand the **smaller** frontier by generating all single-letter variants. For each variant in the word set and not visited, add it to the next frontier. If a variant is in the **other** frontier's visited set, the two searches have met — return the current total steps.
4. If either frontier empties, return `0`.

### Java code (bidirectional BFS)

```java
import java.util.*;

class Solution {
    public int ladderLength(String beginWord, String endWord, List<String> wordList) {
        Set<String> wordSet = new HashSet<>(wordList);
        if (!wordSet.contains(endWord)) return 0;

        Set<String> front = new HashSet<>();
        Set<String> back = new HashSet<>();
        front.add(beginWord);
        back.add(endWord);

        Set<String> visited = new HashSet<>();
        visited.add(beginWord);
        visited.add(endWord);

        int steps = 1;
        while (!front.isEmpty() && !back.isEmpty()) {
            if (front.size() > back.size()) {
                Set<String> tmp = front;
                front = back;
                back = tmp;
            }

            Set<String> nextFront = new HashSet<>();
            for (String word : front) {
                char[] chars = word.toCharArray();
                for (int pos = 0; pos < chars.length; pos++) {
                    char original = chars[pos];
                    for (char c = 'a'; c <= 'z'; c++) {
                        if (c == original) continue;
                        chars[pos] = c;
                        String next = new String(chars);

                        if (wordSet.contains(next)) {
                            if (back.contains(next)) {
                                return steps + 1;
                            }
                            if (visited.add(next)) {
                                nextFront.add(next);
                            }
                        }
                    }
                    chars[pos] = original;
                }
            }
            front = nextFront;
            steps++;
        }
        return 0;
    }
}
```

**Explanation of important logic.**
- **Expand the smaller frontier.** This is the key optimization: the frontier size grows exponentially, so always expanding the smaller side roughly halves the branching factor at each level, often reducing the search space dramatically.
- **Meeting condition.** If a generated variant is in the other frontier's visited set, the two searches have connected; the total path length is `steps + 1` (the current level plus the meeting edge).
- **Shared `visited` set.** Both frontiers mark visited in the same set, preventing the searches from revisiting words already explored by either side.
- **`steps` counting.** Each iteration expands one level from one side; when the frontiers meet, the total number of words is `steps + 1`.

**Complexity.**
- Time: O(N · L² · 26) in the worst case, but in practice much faster because the frontier size is roughly the square root of the unidirectional BFS frontier.
- Space: O(N · L) for the word set, visited set, and frontiers.

**Why it is optimal.** Bidirectional BFS is asymptotically the same worst case but is significantly faster in practice for large dictionaries, which is why it is the preferred interview answer for Word Ladder. The unidirectional BFS is acceptable and simpler; the bidirectional version demonstrates deeper understanding of BFS optimization.

## 6. Dry Run

**Example 1:** `beginWord = "hit"`, `endWord = "cog"`, `wordList = ["hot","dot","dog","lot","log","cog"]`.

This example is useful because it exercises a **multi-step path** with branching, and the optimal path length is 5 words.

**Graph (edges = differ by one letter):**
- `hit` ↔ `hot` (h→o)
- `hot` ↔ `dot` (h→d), `hot` ↔ `lot` (h→l)
- `dot` ↔ `dog` (t→g)
- `lot` ↔ `log` (t→g)
- `dog` ↔ `cog` (d→c)
- `log` ↔ `cog` (l→c)

**BFS trace (unidirectional):**

| Steps | Queue | Dequeued | Neighbors generated (in wordSet, unvisited) |
|---|---|---|---|
| 1 | `[hit]` | `hit` | `hot` |
| 2 | `[hot]` | `hot` | `dot`, `lot` |
| 3 | `[dot, lot]` | `dot` | `dog` |
| 3 | `[lot, dog]` | `lot` | `log` |
| 4 | `[dog, log]` | `dog` | `cog` (== endWord → return 5) |

**Final result:** `5` (words: `hit → hot → dot → dog → cog`).

**Bidirectional trace:**
- `front = {hit}`, `back = {cog}`, `steps = 1`
- Expand `front` (size 1): `hit → hot`; `hot` not in `back`; `front = {hot}`, `steps = 2`
- Expand `front` (size 1): `hot → dot, lot`; neither in `back`; `front = {dot, lot}`, `steps = 3`
- Expand smaller frontier (`dot` or `lot`, both size 2): `dot → dog`; `dog` not in `back`; `front = {dog}`, `steps = 4`
- Expand `front` (size 1): `dog → cog`; `cog` in `back` → return `steps + 1 = 5`

**Final result:** `5`.

## 7. Complexity Summary

| Approach | Time | Space (auxiliary) | Notes |
|---|---|---|---|
| BFS on-the-fly | O(N · L² · 26) | O(N · L) | Simple; standard interview answer |
| Bidirectional BFS | O(N · L² · 26) worst case, much faster in practice | O(N · L) | Preferred for large dictionaries; demonstrates optimization |

Input space is O(N · L) for the word list and is not counted as auxiliary. Both approaches are asymptotically similar, but bidirectional BFS is significantly faster in practice.

## 8. Edge Cases

| Case | Why it matters | How the code handles it |
|---|---|---|
| `endWord` not in `wordList` | Cannot be reached | Early return `0` |
| `beginWord == endWord` | Zero transformations needed | Excluded by constraints; code returns `1` if it occurs (BFS dequeues start first) |
| `wordList` contains only `endWord` | No intermediate words | BFS generates neighbors; if `endWord` is one mutation away, returns `2`; otherwise `0` |
| `wordList` empty | No valid intermediate states | `wordSet` empty (except start); BFS exhausts → `0` |
| Words of length 1 | Only 25 possible mutations | Works; `L = 1`, generation is trivial |
| Disconnected graph | No path from start to end | BFS empties queue → `0` |
| `beginWord` in `wordList` | Duplicate in set | `Set` deduplicates; harmless |
| Very large word list (5000 words) | Tests efficiency of neighbor generation | On-the-fly generation is O(L · 26) per word, not O(N) pairwise comparison |
| Multiple shortest paths | BFS returns the first one found | Correct — all shortest paths have the same length |

## 9. Java Notes

- **`String.toCharArray()` + rebuild** is the standard way to mutate one character. Remember to restore `chars[pos] = original` after trying each letter.
- **`visited.add(next)` returns a boolean** — `if (visited.add(next))` is a compact way to check "was it new?" and mark it visited in one call.
- **`new HashSet<>(wordList)`** converts a `List<String>` to a `Set<String>` in one line for O(1) membership tests.
- **`Queue<String> queue = new LinkedList<>()`** works, but `ArrayDeque` is preferred in production for speed.
- **Bidirectional BFS frontier swapping:** `if (front.size() > back.size()) { swap }` — always expand the smaller frontier. This is the key optimization.
- **Early return on `endWord` not in `wordSet`** is critical: without it, BFS would explore the entire reachable component and then return `0`, which is correct but slower.
- **String building cost:** `new String(chars)` is O(L) and is called `L × 26` times per word, contributing the `L²` factor in the time complexity.

## 10. Common Mistakes

- **Using DFS instead of BFS.** DFS does not guarantee the shortest transformation sequence; BFS does.
- **Comparing every pair of words (O(N² · L)).** With `N = 5000`, this is ~25 million comparisons — borderline TLE. The on-the-fly generation is O(N · L · 26) and much faster.
- **Forgetting to check `endWord` is in `wordList`.** If it is not, the answer is `0`, but a naive BFS might explore the whole component and eventually return `0` — correct but slower, and the early check is clearer.
- **Marking visited on dequeue instead of enqueue.** Allows duplicate queue entries and wastes work; mark on enqueue.
- **Forgetting to restore the character** after trying each letter — subsequent positions would see the mutated character.
- **Off-by-one in step counting.** The sequence length includes both `beginWord` and `endWord`; the BFS level starts at 1 (for `beginWord`) and increments per level.
- **Bidirectional BFS: not sharing the visited set.** Each side must know what the other has visited to detect meeting; a shared set is the standard approach.
- **Bidirectional BFS: returning `steps` instead of `steps + 1`** when the frontiers meet — the meeting edge adds one to the count.

## 11. Interview Takeaway

**Recognition trigger to memorize:** *"Minimum single-letter changes from one word to another, intermediate words must be in a dictionary → BFS over the dictionary, generating neighbors by trying all L × 26 single-letter substitutions (or use bidirectional BFS for speed)."*

**90-second explanation:** "This is a shortest-path problem on an implicit graph. Each dictionary word is a node; two words are connected if they differ in exactly one letter. I run BFS from the start word. For each word I dequeue, I generate all L × 26 single-letter variants and check whether the result is in the dictionary and unvisited. If it is the end word, I return the current sequence length plus one; otherwise I enqueue it. If the queue empties without reaching the end, I return 0. The key optimization is to avoid O(N²) pairwise comparison — generating variants on the fly is O(L × 26) per word. For large dictionaries, I would use bidirectional BFS, expanding the smaller frontier from both ends, which dramatically reduces the search space in practice. Time is O(N · L² · 26) and space is O(N · L)."

**Related problems:**
- [Minimum Genetic Mutation](https://leetcode.com/problems/minimum-genetic-mutation/) — the direct analogue with a 4-letter alphabet; same BFS skeleton.
- [Word Ladder II](https://leetcode.com/problems/word-ladder-ii/) — returns all shortest paths, requiring BFS + backtracking.
- [Open the Lock](https://leetcode.com/problems/open-the-lock/) — BFS over lock combinations, an implicit graph with 4-digit states.
- [Snakes and Ladders](https://leetcode.com/problems/snakes-and-ladders/) — BFS on a board with fixed-step moves and teleports; another implicit-graph shortest path.

## 12. Recall Questions

**Q1. Why is BFS the right algorithm here rather than DFS?**
A: Every transformation is one unweighted edge, and we need the **shortest** sequence. BFS explores in non-decreasing distance, so the first time it reaches `endWord` it has found the minimum. DFS does not guarantee that.

**Q2. Why is generating single-letter variants better than comparing all pairs of words?**
A: Pairwise comparison is O(N² · L), which is ~25 million operations for N = 5000. Variant generation is O(N · L · 26) per word, which is much smaller and avoids the quadratic blowup.

**Q3. What happens if `endWord` is not in `wordList`?**
A: It can never be a valid intermediate or final state, so the answer is `0`. The code checks this upfront.

**Q4. What are the time and space complexities, and what dominates?**
A: O(N · L² · 26) time — for each of at most N words, we generate L × 26 candidates, each costing O(L) to build. Space is O(N · L) for the word set, visited set, and queue.

**Q5. How does bidirectional BFS improve performance?**
A: It expands the smaller frontier from both ends simultaneously. The frontier size grows exponentially, so expanding the smaller side roughly halves the branching factor per level, often reducing the search space from O(b^d) to O(b^(d/2)).

**Q6. Why must the visited set be shared in bidirectional BFS?**
A: Each side needs to know what the other has explored to detect when the frontiers meet. A shared set prevents revisiting words and ensures the meeting condition is correct.

**Q7. Why do we start the step count at 1 instead of 0?**
A: The sequence length includes both `beginWord` and `endWord`. Starting at 1 accounts for `beginWord` itself; each BFS level adds one word to the sequence.

**Q8. What is the difference between this problem and Minimum Genetic Mutation?**
A: The structure is identical (BFS on an implicit graph of strings differing by one character), but Word Ladder has a 26-letter alphabet (vs 4) and a larger dictionary (5000 vs 10), making efficiency more important and bidirectional BFS more valuable.

## 13. Final Code

### Java code (final, optimized bidirectional BFS)

```java
import java.util.*;

class Solution {
    public int ladderLength(String beginWord, String endWord, List<String> wordList) {
        Set<String> wordSet = new HashSet<>(wordList);
        if (!wordSet.contains(endWord)) return 0;

        Set<String> front = new HashSet<>();
        Set<String> back = new HashSet<>();
        front.add(beginWord);
        back.add(endWord);

        Set<String> visited = new HashSet<>();
        visited.add(beginWord);
        visited.add(endWord);

        int steps = 1;
        while (!front.isEmpty() && !back.isEmpty()) {
            if (front.size() > back.size()) {
                Set<String> tmp = front;
                front = back;
                back = tmp;
            }

            Set<String> nextFront = new HashSet<>();
            for (String word : front) {
                char[] chars = word.toCharArray();
                for (int pos = 0; pos < chars.length; pos++) {
                    char original = chars[pos];
                    for (char c = 'a'; c <= 'z'; c++) {
                        if (c == original) continue;
                        chars[pos] = c;
                        String next = new String(chars);

                        if (wordSet.contains(next)) {
                            if (back.contains(next)) {
                                return steps + 1;
                            }
                            if (visited.add(next)) {
                                nextFront.add(next);
                            }
                        }
                    }
                    chars[pos] = original;
                }
            }
            front = nextFront;
            steps++;
        }
        return 0;
    }
}
```

## 14. Self-Test

Consider `beginWord = "hit"`, `endWord = "cog"`, `wordList = ["hot","dot","dog","lot","log","cog"]`. Trace the bidirectional BFS: show the contents of `front`, `back`, `visited`, and `steps` at each iteration until the frontiers meet. Then answer: at which iteration do the frontiers meet, which word is the meeting point, and why is the returned value `steps + 1` rather than `steps`? Finally, explain what would change if the dictionary also contained `"hog"` (which connects `hot` and `dog` but is not on the shortest path) — would the answer change, and would the number of BFS iterations change?