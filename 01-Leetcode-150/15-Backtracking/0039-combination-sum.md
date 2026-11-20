---
title: "0039 — Combination Sum"
layout: default
parent: Backtracking
grand_parent: Leetcode
nav_order: 39
permalink: /15-Backtracking/0039-combination-sum/

problem: 39 - Combination Sum
url: https://leetcode.com/problems/combination-sum/
difficulty: Medium
section: Backtracking
patterns: [backtracking, recursion, combination-enumeration]
data_structures: [array, list]
time: O(N^(T/M))
space: O(T/M)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0039 — Combination Sum

| Field | Value |
|---|---|
| **Pattern** | Backtracking (Combination Enumeration with Reuse) |
| **Difficulty** | Medium |
| **Section** | Backtracking |
| **Time** | O(N^(T/M)) |
| **Space** | O(T/M) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Amazon, Google, Meta, Microsoft, Bloomberg |
| **LeetCode** | [Combination Sum](https://leetcode.com/problems/combination-sum/) |

## 1. Problem in Simple Words

**Given:**
- An array `candidates` of **distinct** positive integers.
- A target integer `target`.

**Required:**
- Return **all unique combinations** of `candidates` that sum to `target`.
- Each candidate may be used **unlimited times** in a combination.
- Combinations are **unordered** — `[2,3]` and `[3,2]` are the same combination and should appear only once.
- The order of combinations in the output does not matter.

**Constraints that actually matter:**
- `1 <= candidates.length <= 30` — the array is small.
- `2 <= candidates[i] <= 40` — all candidates are positive (no zeros or negatives), which means the running sum strictly increases and recursion terminates.
- `1 <= target <= 40` — the target is small, bounding the recursion depth to at most `target / min(candidate) ≤ 20`.
- All candidates are **distinct** — no duplicate handling is needed for the input itself.
- The same number may be chosen unlimited times — this is the key difference from Combinations (LeetCode 77).

**Interpretation of the 403 error:** The URL fetch was blocked by LeetCode's anti-bot protection (HTTP 403 Forbidden). The statement above is reconstructed from the well-known LeetCode 39 problem; verify against the live page in a browser or the LeetCode mobile app. Alternatives: search "Combination Sum" in the LeetCode problems list, or check the problem's discussion tab.

## 2. Recognition Signals

- The problem asks for **all combinations** that sum to a target — exhaustive enumeration with a numeric constraint.
- "Each candidate may be used unlimited times" is the key phrase: unlike Combinations, we do **not** advance the `start` index past `i` after choosing `i`; we allow reusing `i` by recursing with the **same** `start`.
- "Unique combinations" and "unordered" mean we must enforce a non-decreasing order of picks — the `start` index handles this.
- Positive candidates guarantee termination: the running sum only grows, so we can prune when `remaining < 0`.
- Recognition trigger: *"all combinations summing to a target, with reuse allowed"* → **backtracking with a `start` index that recurses at the same `i` (not `i+1`)**.

## 3. Core Idea

**Intuition.** Build combinations incrementally, tracking the remaining sum needed. At each step, choose a candidate from index `start` onward, subtract it from the remaining target, and recurse. Because reuse is allowed, the recursive call uses the **same** `start` index — so the same candidate can be picked again. When the remaining target hits 0, record the combination. If it goes negative, prune.

**Key observation.** The `start` index enforces non-decreasing order of picks, which guarantees each combination is generated exactly once (no permutations). Allowing reuse is as simple as passing `i` instead of `i + 1` to the recursive call.

**Variables.**
- `int[] candidates` — the distinct positive integers.
- `int target` — the target sum.
- `List<Integer> path` — the current partial combination.
- `List<List<Integer>> result` — all valid combinations.
- `int start` — the smallest index allowed for the next pick.
- `int remaining` — the sum still needed to reach `target`.

**Invariant.** At the start of `backtrack(start, remaining)`, `path` contains a non-decreasing sequence of candidates (by index), and `remaining` is `target` minus the sum of `path`. When `remaining == 0`, `path` is a valid combination. When `remaining < 0`, the branch is dead.

**Termination.** Because all candidates are positive, `remaining` strictly decreases with each pick, so recursion depth is bounded by `target / min(candidates) ≤ 20`.

## 4. Approach 1 — Brute Force: Enumerate All Sequences and Filter

**Logic.** Generate all sequences of candidates of length up to `target / min(candidate)`, allowing repeats, and keep those that sum to `target` and are non-decreasing (to avoid permutations). This is extremely wasteful and is only a baseline.

### Java code (brute-force sequences)

```java
import java.util.*;

class Solution {
    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        int min = Arrays.stream(candidates).min().getAsInt();
        int maxLen = target / min;
        for (int len = 1; len <= maxLen; len++) {
            int[] seq = new int[len];
            generate(candidates, target, 0, 0, seq, 0, result);
        }
        return result;
    }

    private void generate(int[] candidates, int target, int pos, int start,
                            int[] seq, int sum, List<List<Integer>> result) {
        if (pos == seq.length) {
            if (sum == target) {
                List<Integer> combo = new ArrayList<>();
                for (int v : seq) combo.add(v);
                result.add(combo);
            }
            return;
        }
        for (int i = start; i < candidates.length; i++) {
            seq[pos] = candidates[i];
            generate(candidates, target, pos + 1, i, seq, sum + candidates[i], result);
        }
    }
}
```

**Complexity.**
- Time: O(N^(T/M)) — all sequences of length up to `T/M` are generated, where `M = min(candidates)`.
- Space: O(T/M) per sequence plus output.

**Why it is less efficient.** It generates all non-decreasing sequences, including those whose sum exceeds `target`, and only filters at the end. Backtracking prunes as soon as `remaining < 0`.

## 5. Approach 2 — Optimized: Backtracking with Pruning

**Algorithm steps.**
1. Sort `candidates` (optional but enables early break when `candidates[i] > remaining`).
2. Initialize `result`, `path`.
3. Call `backtrack(0, target)`:
   - If `remaining == 0`, add `new ArrayList<>(path)` to `result` and return.
   - If `remaining < 0`, return (prune).
   - For `i` from `start` to `candidates.length - 1`:
     - If `candidates[i] > remaining`, **break** (since sorted, all later candidates are also too large).
     - Append `candidates[i]` to `path`.
     - Recurse with `start = i` (**not** `i + 1` — reuse allowed) and `remaining - candidates[i]`.
     - Remove the last element from `path` (undo).
4. Return `result`.

### Java code (backtracking with pruning)

```java
import java.util.*;

class Solution {
    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(candidates);
        backtrack(candidates, target, 0, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(int[] candidates, int remaining, int start,
                            List<Integer> path, List<List<Integer>> result) {
        if (remaining == 0) {
            result.add(new ArrayList<>(path));
            return;
        }

        for (int i = start; i < candidates.length; i++) {
            if (candidates[i] > remaining) break;
            path.add(candidates[i]);
            backtrack(candidates, remaining - candidates[i], i, path, result);
            path.remove(path.size() - 1);
        }
    }
}
```

**Explanation of important logic.**
- **Sorting enables the `break`.** Once `candidates[i] > remaining`, all later candidates are at least as large (sorted), so no completion is possible. This prunes entire suffixes of the loop.
- **Passing `i` instead of `i + 1`** allows reuse of the same candidate. This is the key difference from Combinations.
- **The `start` index enforces non-decreasing picks**, so `[2,3]` and `[3,2]` are not both generated.
- **`new ArrayList<>(path)`** copies the current path when a combination is complete. Without the copy, all result entries alias the same mutable list.
- **The undo step** (`path.remove(path.size() - 1)`) restores the path before trying the next candidate.
- **The `remaining < 0` case is handled implicitly** by the `break`: since all candidates are positive, once `remaining` is negative, the loop would have already broken at the candidate that caused the overshoot.

**Complexity.**
- Time: O(N^(T/M)) — the recursion tree has branching factor up to `N` and depth up to `T/M`, where `M = min(candidates)`. In practice, pruning reduces this significantly.
- Space: O(T/M) auxiliary for the recursion stack and path; O(number of combinations × average length) for the output.

**Why it is optimal.** The output itself can be as large as O(N^(T/M)) combinations, so any correct algorithm must spend at least that much time. Backtracking achieves it with only O(T/M) auxiliary space, which is optimal.

## 6. Dry Run

**Example:** `candidates = [2, 3, 6, 7]`, `target = 7`.

This is LeetCode's Example 1. It is useful because it has a single-element solution (`[7]`), a two-element solution (`[2, 2, 3]`), and tests the pruning break (`6 > 1` after picking `6`).

**Sorted candidates:** `[2, 3, 6, 7]`.

**Backtracking trace:**

| Call | start | path before | i chosen | path after | remaining | Action |
|---|---|---|---|---|---|---|
| 1 | 0 | [] | 0 (2) | [2] | 5 | recurse |
| 2 | 0 | [2] | 0 (2) | [2,2] | 3 | recurse |
| 3 | 0 | [2,2] | 0 (2) | [2,2,2] | 1 | recurse |
| 4 | 0 | [2,2,2] | 0 (2) | — | 1 | break: 2 > 1 |
| 3 | 0 | [2,2] | 1 (3) | [2,2,3] | 0 | **add [2,2,3]**, return |
| 2 | 0 | [2] | 1 (3) | [2,3] | 2 | recurse |
| 3 | 1 | [2,3] | 1 (3) | — | 2 | break: 3 > 2 |
| 2 | 0 | [2] | 2 (6) | — | 5 | break: 6 > 5 |
| 1 | 0 | [] | 1 (3) | [3] | 4 | recurse |
| 2 | 1 | [3] | 1 (3) | [3,3] | 1 | recurse |
| 3 | 1 | [3,3] | 1 (3) | — | 1 | break: 3 > 1 |
| 2 | 1 | [3] | 2 (6) | — | 4 | break: 6 > 4 |
| 1 | 0 | [] | 2 (6) | [6] | 1 | recurse |
| 2 | 2 | [6] | 2 (6) | — | 1 | break: 6 > 1 |
| 1 | 0 | [] | 3 (7) | [7] | 0 | **add [7]**, return |

**Final result:** `[[2,2,3],[7]]` — the two valid combinations.

**Key observations from the trace:**
- Reuse is enabled by passing `i` (not `i + 1`): after picking `2`, the next call still starts at index 0, allowing another `2`.
- The `break` prunes efficiently: after picking `6`, `remaining = 1`, and since `6 > 1`, the loop breaks immediately.
- The `start` index prevents permutations: after picking `3`, the next pick starts at index 1, so `[2]` before `[3]` is not generated as `[3, 2]`.

## 7. Complexity Summary

| Approach | Time | Space (auxiliary) | Notes |
|---|---|---|---|
| Brute-force sequences | O(N^(T/M)) | O(T/M) | Generates all sequences, filters at end |
| Backtracking + pruning | O(N^(T/M)) worst case, pruned heavily | O(T/M) | Only valid prefixes; optimal |

The output size can be O(N^(T/M)) and is not counted as auxiliary. Backtracking's O(T/M) auxiliary space is optimal. Here `M = min(candidates)` and `N = candidates.length`.

## 8. Edge Cases

| Case | Why it matters | How the code handles it |
|---|---|---|
| `target` smaller than all candidates | No combination possible | The `break` fires on the first candidate; returns empty list |
| Single candidate equals target | One combination: `[candidate]` | Base case fires after one pick |
| Single candidate smaller than target, divides it | Combination of repeated candidate | Reuse via `start = i` generates it |
| Candidate equal to target alongside others | Single-element combination plus others | `[target]` is added when that candidate is picked first with `remaining = 0` |
| All candidates larger than target | No combinations | `break` fires immediately; empty result |
| Target 1 with candidates ≥ 2 | No combinations (min candidate > 1) | `break` fires; empty result |
| Duplicate candidates in input | Would produce duplicate combinations | Excluded by constraints (distinct). If not, sort + skip `candidates[i] == candidates[i-1]` at the same level |
| Large target with small candidates | Deep recursion | Depth ≤ `target / min(candidate)` ≤ 20; safe |

## 9. Java Notes

- **`Arrays.sort(candidates)`** is essential for the `break` pruning. Without sorting, `candidates[i] > remaining` does not imply later candidates are also too large.
- **`new ArrayList<>(path)`** copies the current path when a combination is complete. Without the copy, all result entries alias the same mutable list and end up empty.
- **`path.remove(path.size() - 1)`** removes the last element. Use `remove(int index)`, not `remove(Object o)`, to avoid ambiguity.
- **Passing `i` instead of `i + 1`** is the reuse mechanism. This is the single most important difference from Combinations (LeetCode 77).
- **Passing `remaining - candidates[i]`** instead of maintaining a running sum is cleaner and avoids recomputing the sum.
- **Recursion depth** is at most `target / min(candidates) ≤ 20`, so there is no stack concern.

## 10. Common Mistakes

- **Passing `i + 1` instead of `i`.** This disallows reuse and produces only combinations with distinct elements — wrong for this problem.
- **Not sorting the array.** The `break` pruning is invalid without sorting; `candidates[i] > remaining` no longer implies later candidates are too large.
- **Using `continue` instead of `break`.** `continue` skips only the current candidate but explores larger ones, which is correct but slower. With sorting, `break` is strictly better.
- **Forgetting to copy the path** (`result.add(path)`). All result entries alias the same list and end up empty.
- **Forgetting the undo step.** The path keeps growing and combinations are wrong.
- **Not enforcing non-decreasing order.** Without the `start` index, permutations like `[2,3]` and `[3,2]` would both be generated, producing duplicates.
- **Assuming `remaining < 0` needs an explicit check.** With the `break`, the loop never recurses with a negative remaining, so the check is implicit. An explicit `if (remaining < 0) return;` at the top is harmless but redundant.

## 11. Interview Takeaway

**Recognition trigger to memorize:** *"All combinations summing to a target, with unlimited reuse → backtracking with a `start` index that recurses at the same `i` (not `i+1`); sort and break when `candidates[i] > remaining`."*

**90-second explanation:** "This is combination enumeration with reuse, so I use backtracking. I sort the candidates so I can prune early. At each step, I try every candidate from `start` onward, subtract it from the remaining target, and recurse. The key is that I pass `i` — not `i + 1` — to the recursive call, which allows the same candidate to be reused. The `start` index enforces non-decreasing order, so each combination is generated exactly once without permutations. If the remaining target hits 0, I add a copy of the path to the result. If a candidate exceeds the remaining target, I break, since all later candidates are at least as large. Time is O(N^(T/M)) where M is the smallest candidate, and auxiliary space is O(T/M) for the recursion stack."

**Related problems:**
- [Combination Sum II](https://leetcode.com/problems/combination-sum-ii/) — each candidate used at most once, and input may have duplicates; requires sorting + skipping duplicates.
- [Combination Sum III](https://leetcode.com/problems/combination-sum-iii/) — combinations of size `k` from `1..9` summing to `n`.
- [Combinations](https://leetcode.com/problems/combinations/) — combinations of size `k`; same `start` index but `i + 1` (no reuse).
- [Subsets](https://leetcode.com/problems/subsets/) — all subsets; include/exclude decisions.

## 12. Recall Questions

**Q1. Why do we pass `i` instead of `i + 1` to the recursive call?**
A: Passing `i` allows the same candidate to be reused in the combination, which is required because each candidate may be used unlimited times. Passing `i + 1` would disallow reuse.

**Q2. Why is the `start` index necessary?**
A: It enforces non-decreasing order of picks, so each combination is generated exactly once. Without it, permutations like `[2,3]` and `[3,2]` would both be generated, producing duplicates.

**Q3. Why do we sort the candidates, and what does the `break` do?**
A: Sorting ensures that once `candidates[i] > remaining`, all later candidates are also too large. The `break` then prunes the entire suffix of the loop, saving work.

**Q4. What are the time and space complexities?**
A: Time is O(N^(T/M)) where `M = min(candidates)` and `N = candidates.length`, with heavy pruning in practice. Auxiliary space is O(T/M) for the recursion stack and path; the output is O(number of combinations × average length).

**Q5. Why must we copy the path when adding to the result?**
A: The `path` list is mutated during backtracking. Without a copy, every entry in `result` would reference the same list, which ends up empty after backtracking completes.

**Q6. What happens if `target` is smaller than all candidates?**
A: The `break` fires on the first candidate, so the loop body never executes and the result is empty.

**Q7. How would you handle duplicate candidates in the input?**
A: Sort the array and, at each level, skip `candidates[i] == candidates[i-1]` when `i > start`. This is the approach for Combination Sum II (LeetCode 40).

**Q8. Why does the algorithm terminate?**
A: All candidates are positive, so `remaining` strictly decreases with each pick. The recursion depth is bounded by `target / min(candidates) ≤ 20`, guaranteeing termination.

## 13. Final Code

### Java code (final, optimized backtracking)

```java
import java.util.*;

class Solution {
    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(candidates);
        backtrack(candidates, target, 0, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(int[] candidates, int remaining, int start,
                            List<Integer> path, List<List<Integer>> result) {
        if (remaining == 0) {
            result.add(new ArrayList<>(path));
            return;
        }

        for (int i = start; i < candidates.length; i++) {
            if (candidates[i] > remaining) break;
            path.add(candidates[i]);
            backtrack(candidates, remaining - candidates[i], i, path, result);
            path.remove(path.size() - 1);
        }
    }
}
```

## 14. Self-Test

Consider `candidates = [2, 3, 5]`, `target = 8`. Trace the backtracking: for each call, show `start`, the current `path`, the chosen `i`, the new `remaining`, and the moment a combination is added. Identify at which points the `break` prunes a branch. Then answer: how many combinations are produced, and what are they? Finally, explain what would change if we passed `i + 1` instead of `i` — which combinations would be missing, and why?