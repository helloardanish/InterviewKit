---
title: "0046 — Permutations"
layout: default
parent: Backtracking
grand_parent: Leetcode
nav_order: 46
permalink: /15-Backtracking/0046-permutations/

problem: 46 - Permutations
url: https://leetcode.com/problems/permutations/
difficulty: Medium
section: Backtracking
patterns: [backtracking, recursion, permutation-enumeration]
data_structures: [array, list, set]
time: O(n! * n)
space: O(n)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0046 — Permutations

| Field | Value |
|---|---|
| **Pattern** | Backtracking (Permutation Enumeration) |
| **Difficulty** | Medium |
| **Section** | Backtracking |
| **Time** | O(n! · n) |
| **Space** | O(n) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Amazon, Google, Meta, Microsoft, Bloomberg |
| **LeetCode** | [Permutations](https://leetcode.com/problems/permutations/) |

## 1. Problem in Simple Words

**Given:**
- An array `nums` of **distinct** integers.

**Required:**
- Return **all possible permutations** of `nums`, in any order.
- A permutation is an arrangement of all elements in a specific order. For `n` distinct elements, there are exactly `n!` permutations.
- Each permutation must contain every element exactly once.

**Constraints that actually matter:**
- `1 <= nums.length <= 6` — the input is tiny; `6! = 720` permutations at most, so even inefficient approaches pass.
- All integers in `nums` are **distinct** — this is crucial: it means we do not need to deduplicate permutations. If duplicates existed, we would need a `used` set plus a `HashSet<List<Integer>>` or sorting + skipping.
- The order of permutations in the output does not matter — the judge accepts any valid enumeration.

**Interpretation of the 403 error:** The URL fetch was blocked by LeetCode's anti-bot protection (HTTP 403 Forbidden). The statement above is reconstructed from the well-known LeetCode 46 problem; verify against the live page in a browser or the LeetCode mobile app. Alternatives: search "Permutations" in the LeetCode problems list, or check the problem's discussion tab.

## 2. Recognition Signals

- The problem asks for **all possible orderings** of a set — the canonical **permutation enumeration** shape.
- "All possible" (not "count" or "minimum") signals exhaustive enumeration via recursion.
- Elements are **distinct**, so we do not need to worry about duplicate permutations — a simple `used` boolean array suffices.
- The key difference from combinations: **order matters**, so at each position we can pick **any unused element**, not just elements after a `start` index.
- Recognition trigger: *"generate all permutations of a distinct set"* → **backtracking with a `used[]` boolean array**.

## 3. Core Idea

**Intuition.** Build permutations position by position. At each position, try every element that has not been used yet, append it to the current path, mark it used, recurse to fill the next position, then unmark and remove it before trying the next element. When the path contains all `n` elements, record it.

**Key observation.** The `used[]` array is what prevents reusing an element. Unlike combinations, there is no `start` index — at every position, any unused element is a valid choice. This is why the branching factor decreases as `n, n-1, n-2, …, 1`, giving `n!` leaves.

**Variables.**
- `boolean[] used` — whether each index of `nums` has been placed in the current path.
- `List<Integer> path` — the current partial permutation.
- `List<List<Integer>> result` — all completed permutations.
- `int n` — the length of `nums`.

**Invariant.** At the start of `backtrack()`, `path` contains some number of elements, all distinct, and `used[i]` is true iff `nums[i]` is in `path`. When `path.size() == n`, `path` is a complete permutation.

**Backtracking template.**
- **Base case:** if `path.size() == n`, add a copy of `path` to `result` and return.
- **Recursive case:** for each `i` from `0` to `n-1`:
  - If `used[i]`, skip.
  - Mark `used[i] = true`, append `nums[i]` to `path`.
  - Recurse.
  - Undo: remove the last element from `path`, set `used[i] = false`.

## 4. Approach 1 — Brute Force: Generate All Sequences and Filter

**Logic.** Generate all `nⁿ` sequences of length `n` (allowing repeats), then filter to those that use each element exactly once. This is extremely wasteful but correct.

### Java code (brute-force sequences)

```java
import java.util.*;

class Solution {
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        int n = nums.length;
        int total = (int) Math.pow(n, n);
        for (int code = 0; code < total; code++) {
            int[] indices = new int[n];
            int x = code;
            for (int i = 0; i < n; i++) {
                indices[i] = x % n;
                x /= n;
            }
            boolean[] seen = new boolean[n];
            boolean valid = true;
            for (int idx : indices) {
                if (seen[idx]) { valid = false; break; }
                seen[idx] = true;
            }
            if (!valid) continue;
            List<Integer> perm = new ArrayList<>();
            for (int idx : indices) perm.add(nums[idx]);
            result.add(perm);
        }
        return result;
    }
}
```

**Complexity.**
- Time: O(nⁿ · n) — generates all `nⁿ` sequences and checks each.
- Space: O(n) per sequence plus output.

**Why it is less efficient.** For `n = 6`, `nⁿ = 46,656` sequences are generated, while only `720` are valid. Backtracking constructs only valid permutations.

## 5. Approach 2 — Optimized: Backtracking with `used[]` Array

**Algorithm steps.**
1. Initialize `result`, `path`, and `used = new boolean[n]`.
2. Call `backtrack()`:
   - If `path.size() == n`, add `new ArrayList<>(path)` to `result` and return.
   - For `i` from `0` to `n-1`:
     - If `used[i]`, skip.
     - Set `used[i] = true`, append `nums[i]` to `path`.
     - Recurse.
     - Undo: remove the last element from `path`, set `used[i] = false`.
3. Return `result`.

### Java code (backtracking with used array)

```java
import java.util.*;

class Solution {
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        boolean[] used = new boolean[nums.length];
        backtrack(nums, used, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(int[] nums, boolean[] used, List<Integer> path, List<List<Integer>> result) {
        if (path.size() == nums.length) {
            result.add(new ArrayList<>(path));
            return;
        }

        for (int i = 0; i < nums.length; i++) {
            if (used[i]) continue;
            used[i] = true;
            path.add(nums[i]);
            backtrack(nums, used, path, result);
            path.remove(path.size() - 1);
            used[i] = false;
        }
    }
}
```

**Explanation of important logic.**
- **The `used[]` array** prevents picking the same element twice. Without it, the algorithm would generate sequences with repeats.
- **`new ArrayList<>(path)`** copies the current path when a permutation is complete. Without the copy, all entries in `result` would alias the same mutable list.
- **The undo step** (`path.remove(path.size() - 1)` and `used[i] = false`) restores the state before trying the next element. Forgetting either half breaks the algorithm.
- **No `start` index.** Unlike combinations, order matters, so every unused element is a valid choice at every position.

**Complexity.**
- Time: O(n! · n) — there are `n!` permutations, each requiring O(n) to copy into the result.
- Space: O(n) auxiliary for the recursion stack, `path`, and `used`; O(n! · n) for the output.

**Why it is optimal.** The output itself has size O(n! · n), so any correct algorithm must spend at least that much time. Backtracking achieves it with O(n) auxiliary space, which is optimal.

## 6. Dry Run

**Example:** `nums = [1, 2, 3]`.

This example is useful because it is small enough to trace fully and shows the branching factor decreasing from 3 to 2 to 1.

**Backtracking trace:**

| Call | used state | path before | i chosen | path after | Action |
|---|---|---|---|---|---|
| 1 | [F,F,F] | [] | 0 (1) | [1] | recurse |
| 2 | [T,F,F] | [1] | 1 (2) | [1,2] | recurse |
| 3 | [T,T,F] | [1,2] | 2 (3) | [1,2,3] | size == n → add [1,2,3], return |
| 2 | [T,F,F] | [1] | 2 (3) | [1,3] | recurse |
| 3 | [T,F,T] | [1,3] | 1 (2) | [1,3,2] | add [1,3,2], return |
| 1 | [F,F,F] | [] | 1 (2) | [2] | recurse |
| 2 | [F,T,F] | [2] | 0 (1) | [2,1] | recurse |
| 3 | [T,T,F] | [2,1] | 2 (3) | [2,1,3] | add [2,1,3], return |
| 2 | [F,T,F] | [2] | 2 (3) | [2,3] | recurse |
| 3 | [F,T,T] | [2,3] | 0 (1) | [2,3,1] | add [2,3,1], return |
| 1 | [F,F,F] | [] | 2 (3) | [3] | recurse |
| 2 | [F,F,T] | [3] | 0 (1) | [3,1] | recurse |
| 3 | [T,F,T] | [3,1] | 1 (2) | [3,1,2] | add [3,1,2], return |
| 2 | [F,F,T] | [3] | 1 (2) | [3,2] | recurse |
| 3 | [F,T,T] | [3,2] | 0 (1) | [3,2,1] | add [3,2,1], return |

**Final result:** `[[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]` — all `3! = 6` permutations.

## 7. Complexity Summary

| Approach | Time | Space (auxiliary) | Notes |
|---|---|---|---|
| Brute-force sequences | O(nⁿ · n) | O(n) | Generates all sequences, filters valid ones |
| Backtracking + `used[]` | O(n! · n) | O(n) | Only valid permutations; optimal |

The output size is O(n! · n) and is not counted as auxiliary. Backtracking's O(n) auxiliary space is optimal.

## 8. Edge Cases

| Case | Why it matters | How the code handles it |
|---|---|---|
| `n == 1` | Single permutation | Base case fires immediately; returns `[[x]]` |
| `n == 6` | Largest case; `720` permutations | Backtracking handles it trivially |
| Duplicate elements in `nums` | Would produce duplicate permutations | Excluded by constraints (distinct elements). If duplicates existed, sort + skip `nums[i] == nums[i-1] && !used[i-1]` |
| Empty array | Excluded by constraints (`n >= 1`) | Would return `[[]]` if allowed |
| Negative numbers | No special handling needed | `used[]` and `path` work on any integers |
| Large values | No arithmetic, only arrangement | No overflow concerns |

## 9. Java Notes

- **`new ArrayList<>(path)`** is essential when adding to the result. Without it, all entries alias the same `path`, which is empty by the end.
- **`path.remove(path.size() - 1)`** removes the last element (by index). Do not use `path.remove(Integer.valueOf(nums[i]))`, which removes by value and may remove the wrong occurrence.
- **The `used[]` boolean array** is more efficient than a `Set<Integer>` for tracking used indices, since the elements are distinct and indexed.
- **Recursion depth** is at most `n ≤ 6`, so there is no stack concern.
- **Passing `used`, `path`, and `result` as parameters** keeps the method stateless and avoids issues if the `Solution` object is reused.
- **Order of undo:** remove from `path` first, then reset `used[i] = false`. The order does not matter as long as both happen before the next iteration, but doing both immediately after the recursive call is the clearest.

## 10. Common Mistakes

- **Forgetting to copy the path** (`result.add(path)` instead of `result.add(new ArrayList<>(path))`). All result entries would alias the same list and end up empty.
- **Forgetting to reset `used[i] = false`** after recursion. Subsequent branches would think the element is still used, missing valid permutations.
- **Forgetting to remove the last element from `path`.** The path would grow incorrectly across branches.
- **Using a `start` index like combinations.** That would generate only combinations, not permutations, because it would force increasing order.
- **Removing by value instead of by index.** `path.remove(nums[i])` removes the first occurrence of that value; if duplicates existed, it could remove the wrong one. Here elements are distinct, but `remove(path.size() - 1)` is still the correct idiom.
- **Using a `Set` instead of a `boolean[]`.** A `Set<Integer>` works but is slower and more error-prone; the `used[]` array is the standard choice for distinct indexed elements.

## 11. Interview Takeaway

**Recognition trigger to memorize:** *"Generate all permutations of a distinct set → backtracking with a `used[]` boolean array; at each position, try every unused element, recurse, then undo."*

**90-second explanation:** "This is permutation enumeration, so I use backtracking. I keep a `used[]` boolean array to track which elements are already in the current path. At each step, I iterate over all elements, skip the used ones, mark the chosen one as used, append it to the path, and recurse. When the path contains all `n` elements, I add a copy to the result. After the recursive call, I undo: remove the last element from the path and reset `used[i] = false`. Unlike combinations, there is no `start` index — order matters, so every unused element is valid at every position. Time is O(n! · n) to build the output, and auxiliary space is O(n) for the recursion stack, path, and used array."

**Related problems:**
- [Permutations II](https://leetcode.com/problems/permutations-ii/) — permutations with duplicates; requires sorting and skipping duplicate branches.
- [Combinations](https://leetcode.com/problems/combinations/) — combinations of size `k`; uses a `start` index instead of `used[]`.
- [Subsets](https://leetcode.com/problems/subsets/) — all subsets; include/exclude decisions.
- [Next Permutation](https://leetcode.com/problems/next-permutation/) — generates the next lexicographic permutation in O(n) without enumerating all.

## 12. Recall Questions

**Q1. Why is a `used[]` array necessary?**
A: Without it, the algorithm would pick the same element multiple times, generating sequences with repeats instead of permutations.

**Q2. Why must we copy the path when adding to the result?**
A: The `path` list is mutated during backtracking. Without a copy, every entry in `result` would reference the same list, which ends up empty after backtracking completes.

**Q3. Why is there no `start` index in permutations, unlike combinations?**
A: In combinations, order does not matter, so a `start` index enforces increasing order and avoids duplicates. In permutations, order matters, so every unused element is a valid choice at every position.

**Q4. What are the time and space complexities?**
A: Time is O(n! · n) — there are `n!` permutations, each requiring O(n) to copy. Auxiliary space is O(n) for the recursion stack, `path`, and `used`; the output is O(n! · n).

**Q5. What happens if `nums` contains duplicates?**
A: The current algorithm would generate duplicate permutations. To fix it, sort `nums` and skip a branch when `nums[i] == nums[i-1] && !used[i-1]`. This is the approach for LeetCode 47 (Permutations II).

**Q6. How does the algorithm handle `n == 1`?**
A: The base case fires immediately after one pick, producing `[[nums[0]]]`.

**Q7. Why is `path.remove(path.size() - 1)` preferred over `path.remove(nums[i])`?**
A: `remove(int index)` removes by index; `remove(Object o)` removes by value. Using `remove(path.size() - 1)` is unambiguous and always removes the last element, which is the one just added.

**Q8. What is the total number of permutations for `n = 5`?**
A: `5! = 120`.

## 13. Final Code

### Java code (final, optimized backtracking)

```java
import java.util.*;

class Solution {
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        boolean[] used = new boolean[nums.length];
        backtrack(nums, used, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(int[] nums, boolean[] used, List<Integer> path, List<List<Integer>> result) {
        if (path.size() == nums.length) {
            result.add(new ArrayList<>(path));
            return;
        }

        for (int i = 0; i < nums.length; i++) {
            if (used[i]) continue;
            used[i] = true;
            path.add(nums[i]);
            backtrack(nums, used, path, result);
            path.remove(path.size() - 1);
            used[i] = false;
        }
    }
}
```

## 14. Self-Test

Consider `nums = [1, 2, 3, 4]`. Trace the first four permutations produced by the backtracking algorithm: for each call, show the current `used` state, the path before and after appending, and the moment a permutation is added to the result. Then answer: how many permutations are produced in total, and why is the first permutation `[1,2,3,4]`? Finally, explain what would happen if the `used[i] = false` reset were removed — which permutations would be missing, and why would the result list be incomplete?