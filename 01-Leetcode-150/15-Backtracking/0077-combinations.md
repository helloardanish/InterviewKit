---
title: "0077 — Combinations"
layout: default
parent: Backtracking
grand_parent: Leetcode
nav_order: 77
permalink: /15-Backtracking/0077-combinations/

problem: 77 - Combinations
url: https://leetcode.com/problems/combinations/
difficulty: Medium
section: Backtracking
patterns: [backtracking, recursion, combination-enumeration]
data_structures: [array, list]
time: O(C(n,k) * k)
space: O(k)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0077 — Combinations

| Field | Value |
|---|---|
| **Pattern** | Backtracking (Combination Enumeration) |
| **Difficulty** | Medium |
| **Section** | Backtracking |
| **Time** | O(C(n,k) · k) |
| **Space** | O(k) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Amazon, Google, Meta, Microsoft, Bloomberg |
| **LeetCode** | [Combinations](https://leetcode.com/problems/combinations/) |

## 1. Problem in Simple Words

**Given:**
- Two integers `n` and `k`.

**Required:**
- Return **all possible combinations** of `k` numbers chosen from the range `[1, n]`.
- Combinations are **unordered** — `[1,2]` and `[2,1]` are the same combination and should appear only once.
- Return the combinations in **any order**.

**Constraints that actually matter:**
- `1 <= n <= 20` — the range is small enough that the total number of combinations `C(n,k)` is at most `C(20,10) = 184,756`, which is manageable.
- `1 <= k <= n` — `k` is always valid; there is no empty-combination case.
- The output size is `C(n,k)`, so any correct algorithm must spend at least that much time writing the answer.
- The order of numbers within a combination and the order of combinations do not matter; the judge accepts any valid enumeration.

**Interpretation of the 403 error:** The URL fetch was blocked by LeetCode's anti-bot protection (HTTP 403 Forbidden). The statement above is reconstructed from the well-known LeetCode 77 problem; verify against the live page in a browser or the LeetCode mobile app. Alternatives: search "Combinations" in the LeetCode problems list, or check the problem's discussion tab.

## 2. Recognition Signals

- The problem asks for **all combinations** of a fixed size from a range — the canonical **combination enumeration** shape.
- "Combinations are unordered" is the key distinction from permutations: we must avoid generating `[1,2]` and `[2,1]` separately.
- The natural way to enforce order is to only consider numbers **greater than the last chosen number** — this turns the problem into a **backtracking** search over increasing sequences.
- Recognition trigger: *"generate all k-element subsets of [1..n]"* → **backtracking with a `start` index that only moves forward**.

## 3. Core Idea

**Intuition.** Build combinations incrementally. At each step, choose a number greater than the last chosen one, append it to the current path, and recurse. When the path reaches length `k`, record it. The "greater than the last" rule guarantees each combination is generated exactly once, in increasing order.

**Key observation.** The `start` parameter is what prevents duplicates. At recursion depth `d`, we have chosen `d` numbers; the next number must be in `[start, n]`. After choosing `x`, the next call uses `start = x + 1`. This ensures every combination is generated in exactly one order (increasing).

**Variables.**
- `int n`, `int k` — the problem parameters.
- `List<Integer> path` — the current partial combination.
- `List<List<Integer>> result` — all completed combinations.
- `int start` — the smallest number allowed for the next pick.

**Invariant.** At the start of `backtrack(start)`, `path` contains `path.size()` numbers, all in increasing order, and the next number must be at least `start`. When `path.size() == k`, `path` is a complete combination.

**Pruning optimization.** If the remaining numbers `n - start + 1` are fewer than the slots left `k - path.size()`, no completion is possible — prune. Equivalently, the loop bound can be tightened to `i <= n - (k - path.size()) + 1`.

## 4. Approach 1 — Brute Force: Enumerate All Subsets and Filter

**Logic.** Generate all `2ⁿ` subsets of `[1, n]`, keep only those of size `k`. This is correct but wasteful: it generates many subsets that are not of size `k`.

### Java code (subset enumeration)

```java
import java.util.*;

class Solution {
    public List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> result = new ArrayList<>();
        int total = 1 << n;
        for (int mask = 0; mask < total; mask++) {
            if (Integer.bitCount(mask) != k) continue;
            List<Integer> combo = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) {
                    combo.add(i + 1);
                }
            }
            result.add(combo);
        }
        return result;
    }
}
```

**Complexity.**
- Time: O(2ⁿ · n) — enumerates all subsets, not just the `C(n,k)` valid ones.
- Space: O(k) per combination plus the output.

**Why it is less efficient.** For `n = 20`, `2²⁰ ≈ 10⁶` subsets are generated, while only `C(20,10) ≈ 1.8 × 10⁵` are valid. Backtracking avoids the wasted work by constructing only valid combinations.

## 5. Approach 2 — Optimized: Backtracking with Start Index and Pruning

**Algorithm steps.**
1. Initialize `result` and `path`.
2. Call `backtrack(1)`:
   - If `path.size() == k`, add a copy of `path` to `result` and return.
   - For `i` from `start` to `n - (k - path.size()) + 1`:
     - Append `i` to `path`.
     - Recurse with `start = i + 1`.
     - Remove `i` from `path` (undo).
3. Return `result`.

### Java code (backtracking with pruning)

```java
import java.util.*;

class Solution {
    public List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(n, k, 1, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(int n, int k, int start, List<Integer> path, List<List<Integer>> result) {
        if (path.size() == k) {
            result.add(new ArrayList<>(path));
            return;
        }

        // Prune: need (k - path.size()) more numbers from [start, n].
        int maxStart = n - (k - path.size()) + 1;
        for (int i = start; i <= maxStart; i++) {
            path.add(i);
            backtrack(n, k, i + 1, path, result);
            path.remove(path.size() - 1);
        }
    }
}
```

**Explanation of important logic.**
- **`start` enforces increasing order**, so each combination is generated exactly once. Without it, `[1,2]` and `[2,1]` would both appear.
- **`new ArrayList<>(path)`** copies the current path when a combination is complete. Without the copy, all result entries would alias the same mutable list.
- **The undo step** (`path.remove(path.size() - 1)`) restores the path before trying the next number.
- **The pruning bound** `i <= n - (k - path.size()) + 1` skips branches that cannot possibly complete a `k`-element combination. For example, with `n = 4`, `k = 2`, at `start = 1` with an empty path, `maxStart = 4 - 2 + 1 = 3`, so `i` goes up to 3 (not 4) — picking 4 would leave no room for a second number.

**Complexity.**
- Time: O(C(n,k) · k) — each of the `C(n,k)` combinations requires O(k) to copy into the result.
- Space: O(k) auxiliary for the recursion stack and path; O(C(n,k) · k) for the output.

**Why it is optimal.** The output itself has size O(C(n,k) · k), so any correct algorithm must spend at least that much time. Backtracking achieves it with only O(k) auxiliary space, which is optimal.

## 6. Dry Run

**Example:** `n = 4`, `k = 2`.

This example is useful because it is small enough to trace fully and shows how the `start` index and pruning interact.

**Backtracking trace:**

| Call | start | path before | i chosen | path after | Action |
|---|---|---|---|---|---|
| 1 | 1 | [] | 1 | [1] | recurse |
| 2 | 2 | [1] | 2 | [1,2] | size == k → add [1,2], return |
| 2 | 2 | [1] | 3 | [1,3] | add [1,3], return |
| 2 | 2 | [1] | 4 | [1,4] | add [1,4], return |
| 1 | 1 | [] | 2 | [2] | recurse |
| 2 | 3 | [2] | 3 | [2,3] | add [2,3], return |
| 2 | 3 | [2] | 4 | [2,4] | add [2,4], return |
| 1 | 1 | [] | 3 | [3] | recurse |
| 2 | 4 | [3] | 4 | [3,4] | add [3,4], return |
| 1 | 1 | [] | 4 | — | pruned: maxStart = 4 - 2 + 1 = 3, so i stops at 3 |

**Final result:** `[[1,2],[1,3],[1,4],[2,3],[2,4],[3,4]]` — all `C(4,2) = 6` combinations, each exactly once.

**Pruning check:** At `start = 4` with an empty path, `maxStart = 4 - 2 + 1 = 3`, so `i = 4` is never tried. Picking 4 would leave no second number, so the branch is correctly pruned.

## 7. Complexity Summary

| Approach | Time | Space (auxiliary) | Notes |
|---|---|---|---|
| Subset enumeration | O(2ⁿ · n) | O(k) | Generates all subsets, filters by size |
| Backtracking + pruning | O(C(n,k) · k) | O(k) | Only valid combinations; optimal |

The output size is O(C(n,k) · k) and is not counted as auxiliary. Backtracking's O(k) auxiliary space is optimal.

## 8. Edge Cases

| Case | Why it matters | How the code handles it |
|---|---|---|
| `k == 1` | Combinations are single numbers | `maxStart = n - 1 + 1 = n`, loop runs `1..n` |
| `k == n` | Only one combination: `[1..n]` | `maxStart = n - n + 1 = 1`, so only `i = 1` is tried, then recursion fills the rest |
| `n == 1, k == 1` | Smallest case | Returns `[[1]]` |
| `n == 20, k == 10` | Largest case; tests performance | `C(20,10) = 184,756` combinations; backtracking handles it efficiently |
| `k > n` | Excluded by constraints (`k <= n`) | Would return an empty list (loop bound makes no iterations) |
| `k == 0` | Excluded by constraints (`k >= 1`) | Would return `[[]]` if allowed (base case fires immediately) |
| Very large `n` with small `k` | Tests pruning efficiency | Pruning keeps the search to valid combinations only |

## 9. Java Notes

- **`new ArrayList<>(path)`** is essential when adding to the result. Without the copy, every entry in `result` would be the same `path` object, which is mutated during backtracking — all entries would end up identical (and likely empty).
- **`path.remove(path.size() - 1)`** removes the last element. Using `path.remove(Integer.valueOf(i))` would remove by value, which is wrong here (it would remove the first occurrence, not the last).
- **The pruning bound** `n - (k - path.size()) + 1` is derived from: after picking `i`, we need `k - path.size() - 1` more numbers from `[i+1, n]`, which requires `n - i >= k - path.size() - 1`, i.e., `i <= n - (k - path.size()) + 1`.
- **Passing `result` and `path` as parameters** keeps the method stateless, which is cleaner than instance fields.
- **Recursion depth** is at most `k ≤ 20`, so there is no stack concern.

## 10. Common Mistakes

- **Forgetting to copy the path** (`result.add(path)` instead of `result.add(new ArrayList<>(path))`). This is the most common bug: all result entries alias the same list, which is empty by the time backtracking finishes.
- **Not using a `start` index.** Without it, the algorithm generates permutations (`[1,2]` and `[2,1]`) instead of combinations.
- **Removing by value instead of by index.** `path.remove(i)` removes the first occurrence of value `i`, not the last element. Use `path.remove(path.size() - 1)`.
- **Incorrect pruning bound.** Using `i <= n` instead of the tightened bound is correct but slower; using `i < n` is wrong (misses valid combinations).
- **Starting the loop at 0 instead of `start`.** Numbers are `1..n`, and each recursion must only consider numbers greater than the last pick.
- **Adding to the result before the base case.** The combination is only complete when `path.size() == k`.

## 11. Interview Takeaway

**Recognition trigger to memorize:** *"Generate all k-element combinations of [1..n] → backtracking with a `start` index that only moves forward; prune when the remaining numbers cannot fill the remaining slots."*

**90-second explanation:** "This is combination enumeration, so I use backtracking with a `start` index. At each step, I try every number from `start` up to a pruned upper bound, append it to the current path, recurse with `start = i + 1`, and then remove it before trying the next. The `start` index ensures each combination is generated once in increasing order, avoiding permutations. When the path reaches length `k`, I add a copy to the result. The pruning bound `i <= n - (k - path.size()) + 1` skips branches that cannot possibly complete a combination. Time is O(C(n,k) · k) to build the output, and auxiliary space is O(k) for the recursion stack and path."

**Related problems:**
- [Combination Sum](https://leetcode.com/problems/combination-sum/) — combinations with reuse and a target sum; same backtracking skeleton with a `start` index.
- [Combination Sum III](https://leetcode.com/problems/combination-sum-iii/) — combinations of size `k` from `1..9` summing to `n`; a direct variant.
- [Subsets](https://leetcode.com/problems/subsets/) — all subsets; backtracking with include/exclude decisions.
- [Permutations](https://leetcode.com/problems/permutations/) — all orderings; backtracking with a visited set instead of a `start` index.

## 12. Recall Questions

**Q1. Why is the `start` index necessary?**
A: Without it, the algorithm would generate permutations (`[1,2]` and `[2,1]`) instead of combinations. The `start` index enforces increasing order, so each combination is generated exactly once.

**Q2. Why must we copy the path when adding to the result?**
A: The `path` list is mutated during backtracking. Without a copy, every entry in `result` would reference the same list, which ends up empty after backtracking completes.

**Q3. What does the pruning bound `i <= n - (k - path.size()) + 1` mean?**
A: After picking `i`, we need `k - path.size() - 1` more numbers from `[i+1, n]`. This requires `n - i >= k - path.size() - 1`, which rearranges to `i <= n - (k - path.size()) + 1`. Numbers beyond this bound cannot complete a combination.

**Q4. What are the time and space complexities?**
A: Time is O(C(n,k) · k) — each of the `C(n,k)` combinations requires O(k) to copy. Auxiliary space is O(k) for the recursion stack and path; the output is O(C(n,k) · k).

**Q5. Why is `path.remove(path.size() - 1)` correct, while `path.remove(i)` is not?**
A: `remove(int index)` removes the element at that index. `path.remove(i)` would remove the element at index `i`, which is not necessarily the last element. `remove(path.size() - 1)` removes the last element, which is the one just added.

**Q6. How does the algorithm handle `k == n`?**
A: The pruning bound becomes `maxStart = n - n + 1 = 1`, so only `i = 1` is tried at the top level. Recursion then fills the remaining positions with `2, 3, …, n`, producing the single combination `[1, 2, …, n]`.

**Q7. What is the total number of combinations for `n = 5, k = 3`?**
A: `C(5,3) = 10`.

**Q8. Could you solve this iteratively?**
A: Yes, using a combinatorial number system or by simulating the recursion with an explicit stack. Backtracking is the standard and clearest approach.

## 13. Final Code

### Java code (final, optimized backtracking)

```java
import java.util.*;

class Solution {
    public List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(n, k, 1, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(int n, int k, int start, List<Integer> path, List<List<Integer>> result) {
        if (path.size() == k) {
            result.add(new ArrayList<>(path));
            return;
        }

        int maxStart = n - (k - path.size()) + 1;
        for (int i = start; i <= maxStart; i++) {
            path.add(i);
            backtrack(n, k, i + 1, path, result);
            path.remove(path.size() - 1);
        }
    }
}
```

## 14. Self-Test

Consider `n = 5`, `k = 3`. Trace the backtracking: for each call, show `start`, the current `path`, the chosen `i`, and the resulting `path` after append. Identify at which points the pruning bound prevents a branch from being explored, and explain why `i = 4` is never chosen when `path.size() == 0`. Then answer: how many combinations are produced in total, and what is the first combination added to the result? Finally, explain what would happen if the `start` parameter were always `1` instead of `i + 1` — which incorrect combinations would appear, and why?