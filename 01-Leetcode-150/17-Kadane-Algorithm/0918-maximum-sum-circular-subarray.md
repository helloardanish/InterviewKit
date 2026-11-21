---
title: "0918 — Maximum Sum Circular Subarray"
layout: default
parent: Kadane-Algorithm
grand_parent: Leetcode
nav_order: 918
permalink: /17-Kadane-Algorithm/0918-maximum-sum-circular-subarray/

problem: 918 - Maximum Sum Circular Subarray
url: https://leetcode.com/problems/maximum-sum-circular-subarray/
difficulty: Medium
section: Array / Dynamic Programming
patterns: [kadane, circular-array, dynamic-programming]
data_structures: [array]
time: O(n)
space: O(1)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0918 — Maximum Sum Circular Subarray

| Field | Value |
|---|---|
| **Pattern** | Kadane's Algorithm (Max & Min) on Circular Array |
| **Difficulty** | Medium |
| **Section** | Array / Dynamic Programming |
| **Time** | O(n) |
| **Space** | O(1) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Amazon, Google, Meta, Microsoft, Bloomberg |
| **LeetCode** | [Maximum Sum Circular Subarray](https://leetcode.com/problems/maximum-sum-circular-subarray/) |

## 1. Problem in Simple Words

**Given:**
- A circular integer array `nums` of length `n`. "Circular" means the end connects back to the beginning: after index `n-1` comes index `0`.

**Required:**
- Return the **maximum possible sum** of a **non-empty** subarray of the circular array.
- A subarray is a contiguous segment; in a circular array it may wrap around (e.g., the last two elements plus the first element).

**Constraints that actually matter:**
- `1 <= n <= 3 × 10⁴` — large enough that an O(n²) enumeration of all subarrays is too slow; O(n) is required.
- `-3 × 10⁴ <= nums[i] <= 3 × 10⁴` — values can be negative, so the maximum subarray might be a single element, and the "wrap-around" case needs careful handling to avoid summing the entire array.
- A subarray must be **non-empty** — this matters when all values are negative: the answer is the largest single element, not 0.
- The total sum of the array fits in an `int` for these bounds (max `3 × 10⁴ × 3 × 10⁴ = 9 × 10⁸`), but using `long` for intermediates is a safe habit.

**Interpretation of the 403 error:** The URL fetch was blocked by LeetCode's anti-bot protection (HTTP 403 Forbidden). The statement above is reconstructed from the well-known LeetCode 918 problem; verify against the live page in a browser or the LeetCode mobile app. Alternatives: search "Maximum Sum Circular Subarray" in the LeetCode problems list, or check the problem's discussion tab.

## 2. Recognition Signals

- The problem asks for the **maximum subarray sum** — the direct signal for **Kadane's algorithm**.
- The array is **circular**, which introduces exactly two cases: the best subarray either **does not wrap** (a normal contiguous segment) or **wraps** (a suffix plus a prefix).
- A wrapping subarray is the complement of a **non-wrapping middle segment**; maximizing the wrapping sum is equivalent to **minimizing the excluded middle** — this is the key insight that reduces the problem to running Kadane twice (once for max, once for min).
- Recognition trigger: *"maximum subarray sum on a circular array"* → **Kadane for max (non-wrapping case) + Kadane for min (wrapping case = total − min subarray), with the all-negative guard**.

## 3. Core Idea

**Intuition.** There are only two ways a maximum subarray can look on a circular array:
1. **Non-wrapping:** a normal contiguous segment. The answer is the standard Kadane maximum.
2. **Wrapping:** a suffix `nums[i..n-1]` plus a prefix `nums[0..j]`. The elements **not** included form a contiguous middle segment `nums[j+1..i-1]`. So the wrapping sum is `total − (sum of the middle segment)`. To maximize the wrapping sum, we **minimize** the middle segment — the minimum subarray sum, again by Kadane.

**Key observation.** The answer is `max(maxNormal, total − minNormal)`, **except** when the minimum subarray is the entire array (i.e., all elements are negative). In that case `total − minNormal = 0`, which corresponds to the empty subarray — invalid. So when `maxNormal < 0`, return `maxNormal`.

**Variables.**
- `maxNormal` — Kadane's maximum subarray sum (non-wrapping case).
- `minNormal` — Kadane's minimum subarray sum.
- `total` — sum of all elements.
- `currentMax` — running maximum ending at the current index.
- `currentMin` — running minimum ending at the current index.

**Invariant (Kadane).** `currentMax` is the maximum sum of a subarray ending at the current index. It is either the current element alone or the previous `currentMax` plus the current element — whichever is larger. `maxNormal` tracks the best `currentMax` seen so far. Symmetrically for `currentMin` / `minNormal`.

**Why the all-negative guard is needed.** If every element is negative, then `minNormal` equals `total` (the whole array is the minimum subarray). Then `total − minNormal = 0`, which would suggest the empty subarray is optimal — but the problem requires a non-empty subarray. In this case the correct answer is `maxNormal`, the largest single element (or least-negative segment).

## 4. Approach 1 — Brute Force: Enumerate All Circular Subarrays

**Logic.** For every starting index `i` and every length `1..n`, sum the circular segment and track the maximum. This is O(n²) and correct, but too slow for `n = 3 × 10⁴`.

### Java code (brute force)

    class Solution {
        public int maxSubarraySumCircular(int[] nums) {
            int n = nums.length;
            int best = Integer.MIN_VALUE;
            for (int i = 0; i < n; i++) {
                int sum = 0;
                for (int len = 1; len <= n; len++) {
                    sum += nums[(i + len - 1) % n];
                    best = Math.max(best, sum);
                }
            }
            return best;
        }
    }

**Complexity.**
- Time: O(n²) — `n` starting positions × `n` lengths.
- Space: O(1) auxiliary.

**Why it is less efficient.** For `n = 3 × 10⁴`, that is `9 × 10⁸` operations — too slow. It is useful as a correctness baseline for small inputs and for verifying the O(n) solution during testing.

## 5. Approach 2 — Optimized: Double Kadane (Max and Min)

**Algorithm steps.**
1. Compute `total` = sum of all elements.
2. Run Kadane for the **maximum** subarray → `maxNormal`.
3. Run Kadane for the **minimum** subarray → `minNormal`.
4. If `maxNormal < 0` (all elements negative), return `maxNormal`.
5. Otherwise return `max(maxNormal, total − minNormal)`.

### Java code (double Kadane)

    class Solution {
        public int maxSubarraySumCircular(int[] nums) {
            int total = 0;
            int maxNormal = Integer.MIN_VALUE;
            int minNormal = Integer.MAX_VALUE;
            int currentMax = 0;
            int currentMin = 0;

            for (int x : nums) {
                total += x;
                currentMax = Math.max(x, currentMax + x);
                maxNormal = Math.max(maxNormal, currentMax);
                currentMin = Math.min(x, currentMin + x);
                minNormal = Math.min(minNormal, currentMin);
            }

            if (maxNormal < 0) return maxNormal;

            return Math.max(maxNormal, total - minNormal);
        }
    }

**Explanation of important logic.**
- **Two Kadane passes in one loop.** `currentMax` / `maxNormal` track the max subarray; `currentMin` / `minNormal` track the min. Both are updated per element in O(1).
- **`currentMax = max(x, currentMax + x)`** — the classic Kadane recurrence: either extend the previous subarray or start fresh at `x`.
- **`currentMin = min(x, currentMin + x)`** — the symmetric recurrence for the minimum.
- **The all-negative guard** (`if (maxNormal < 0) return maxNormal;`) is essential. Without it, `total - minNormal` would be `0` when the minimum subarray is the whole array, which is invalid (empty subarray).
- **Why `total - minNormal` is the wrapping case.** A wrapping subarray excludes a contiguous middle segment. Minimizing the excluded sum maximizes the wrapping sum.

**Complexity.**
- Time: O(n) — one pass, constant work per element.
- Space: O(1) — only a handful of scalar variables.

**Why it is optimal.** Any correct algorithm must inspect every element at least once, so Ω(n) is a lower bound; the double Kadane achieves it with O(1) space, which is optimal.

## 6. Dry Run

**Example 1:** `nums = [1, -2, 3, -2]`.

This example is useful because the optimal subarray does not wrap (`[3]` sums to `3`), and the wrapping candidate `[3, -2, 1]` sums to `2`. It shows the algorithm correctly picking the non-wrapping case.

**Trace:**

| i | x | currentMax | maxNormal | currentMin | minNormal | total |
|---|---|---|---|---|---|---|
| 0 | 1 | max(1, 1)=1 | 1 | min(1, 1)=1 | 1 | 1 |
| 1 | -2 | max(-2, -1)=-1 | 1 | min(-2, -1)=-2 | -2 | -1 |
| 2 | 3 | max(3, 2)=3 | 3 | min(3, 1)=1 | -2 | 2 |
| 3 | -2 | max(-2, 1)=1 | 3 | min(-2, -1)=-2 | -2 | 0 |

- `maxNormal = 3` (subarray `[3]`).
- `minNormal = -2` (subarray `[-2]`, either index 1 or 3).
- `total = 0`.
- `total − minNormal = 0 − (−2) = 2` (wrapping subarray `[3, -2, 1]`).
- `maxNormal >= 0`, so return `max(3, 2) = 3`.

**Example 2:** `nums = [5, -3, 5]`.

| i | x | currentMax | maxNormal | currentMin | minNormal | total |
|---|---|---|---|---|---|---|
| 0 | 5 | max(5, 5)=5 | 5 | min(5, 5)=5 | 5 | 5 |
| 1 | -3 | max(-3, 2)=2 | 5 | min(-3, 2)=-3 | -3 | 2 |
| 2 | 5 | max(5, 7)=7 | 7 | min(5, 2)=2 | -3 | 7 |

- `maxNormal = 7` (whole array).
- `minNormal = -3` (subarray `[-3]`).
- `total − minNormal = 7 − (−3) = 10` (wrapping subarray `[5, 5]`, indices 2 and 0).
- Return `max(7, 10) = 10`.

This is the canonical case where the wrapping sum beats the non-wrapping sum.

**Example 3 (all negative):** `nums = [-3, -2, -3]`.

| i | x | currentMax | maxNormal | currentMin | minNormal | total |
|---|---|---|---|---|---|---|
| 0 | -3 | max(-3, -3)=-3 | -3 | min(-3, -3)=-3 | -3 | -3 |
| 1 | -2 | max(-2, -5)=-2 | -2 | min(-2, -5)=-5 | -5 | -5 |
| 2 | -3 | max(-3, -5)=-3 | -2 | min(-3, -8)=-8 | -8 | -8 |

- `maxNormal = -2`.
- `minNormal = -8` (whole array).
- `total − minNormal = 0`.
- Since `maxNormal < 0`, return `maxNormal = -2`. Correct — the best non-empty subarray is the single element `-2`.

## 7. Complexity Summary

| Approach | Time | Space (auxiliary) | Notes |
|---|---|---|---|
| Brute force | O(n²) | O(1) | Enumerates all circular subarrays |
| Double Kadane | O(n) | O(1) | Optimal; handles non-wrapping and wrapping cases |

Input space is O(n) and is not counted as auxiliary. The double Kadane uses O(1) extra space, which is optimal.

## 8. Edge Cases

| Case | Why it matters | How the code handles it |
|---|---|---|
| All elements negative (`[-3,-2,-3]`) | Wrapping formula gives 0 (empty subarray), invalid | `if (maxNormal < 0) return maxNormal;` |
| Single element (`[5]`) | No wrapping possible | `maxNormal = 5`, `minNormal = 5`, `total − minNormal = 0`, return `5` |
| All positive (`[1,2,3]`) | Whole array is optimal; wrapping is meaningless | `minNormal` is the smallest element; `total − minNormal < total = maxNormal`, so `maxNormal` wins |
| Mixed with wrap-around optimal (`[5,-3,5]`) | Tests the wrapping case | `total − minNormal = 10 > maxNormal = 7`, returns `10` |
| Optimal non-wrapping (`[1,-2,3,-2]`) | Tests that non-wrapping can win | `maxNormal = 3 > total − minNormal = 2`, returns `3` |
| Zeros in the array | Zeros can be included or excluded without changing the sum | Kadane handles them naturally; `maxNormal` and `minNormal` are unaffected in value |
| `n = 1` with negative value (`[-5]`) | All-negative guard fires | Returns `-5` |
| `n = 1` with positive value (`[5]`) | No wrapping | Returns `5` |

## 9. Java Notes

- **Initialize `maxNormal` to `Integer.MIN_VALUE`** (not 0) so that all-negative arrays are handled correctly. Initializing to 0 would give a wrong answer for `[-3,-2,-3]`.
- **`currentMax = Math.max(x, currentMax + x)`** is the Kadane recurrence; it works for negative numbers because starting fresh at `x` is always an option.
- **One loop for both Kadane passes** is efficient and avoids iterating twice; the two updates are independent.
- **`total` can be up to `9 × 10⁸`**, which fits in `int`, but if you generalize to larger inputs, use `long` for `total` and the Kadane accumulators to avoid overflow.
- **No need for a separate array or prefix sums** — Kadane is O(1) space.
- **The guard `if (maxNormal < 0)`** is the cleanest way to detect the all-negative case; it is equivalent to `if (minNormal == total)` but more direct.

## 10. Common Mistakes

- **Forgetting the all-negative guard.** Returning `total − minNormal` for `[-3,-2,-3]` gives `0`, which is wrong (empty subarray). The correct answer is `-2`.
- **Initializing `maxNormal` to 0.** This treats the empty subarray as valid and gives `0` for all-negative arrays. Initialize to `Integer.MIN_VALUE`.
- **Only running Kadane once.** A single max-Kadane gives the non-wrapping answer but misses the wrapping case. The min-Kadane is needed for the wrapping case.
- **Assuming the wrapping case is always `total − minNormal` without the guard.** As noted, this is wrong for all-negative arrays.
- **Using `currentMin = Math.min(x, currentMin + x)` incorrectly.** The recurrence is symmetric to max-Kadane: either start fresh at `x` or extend the previous minimum.
- **Confusing the excluded middle with the wrapping subarray.** The wrapping subarray is `total − middle`; maximizing it means minimizing the middle, not maximizing it.

## 11. Interview Takeaway

**Recognition trigger to memorize:** *"Maximum subarray sum on a circular array → run Kadane for max (non-wrapping) and Kadane for min (wrapping = total − min), with the all-negative guard returning maxNormal."*

**90-second explanation:** "A circular subarray is either non-wrapping or wrapping. The non-wrapping case is the standard Kadane maximum. For the wrapping case, the subarray excludes a contiguous middle segment, so its sum is `total − middle`; to maximize it, I minimize the middle with a second Kadane pass for the minimum subarray. The answer is the larger of the two. There is one catch: if all elements are negative, the minimum subarray is the whole array, so `total − min` is 0, which corresponds to the empty subarray — invalid. I detect this with `maxNormal < 0` and return `maxNormal` directly. The whole thing is one pass, O(n) time and O(1) space, which is optimal."

**Related problems:**
- [Maximum Subarray](https://leetcode.com/problems/maximum-subarray/) — the non-circular Kadane problem; the direct foundation for this solution.
- [Maximum Product Subarray](https://leetcode.com/problems/maximum-product-subarray/) — Kadane-style DP with track of both max and min products.
- [Best Sightseeing Pair](https://leetcode.com/problems/best-sightseeing-pair/) — a Kadane-like single-pass optimization.
- [Maximum Sum Circular Subarray II](https://leetcode.com/problems/maximum-sum-circular-subarray-ii/) — the variant where the subarray length is bounded, requiring a different technique.

## 12. Recall Questions

**Q1. Why does the wrapping case reduce to `total − minNormal`?**
A: A wrapping subarray is a suffix plus a prefix; the elements not included form a contiguous middle segment. Its sum is `total − middle`, so maximizing it is equivalent to minimizing the middle segment — the minimum subarray sum.

**Q2. Why is the all-negative guard necessary, and what does it return?**
A: If all elements are negative, the minimum subarray is the whole array, so `total − minNormal = 0`, corresponding to the empty subarray, which is invalid. The guard returns `maxNormal` (the least-negative subarray) instead.

**Q3. What are the time and space complexities?**
A: O(n) time — one pass, constant work per element. O(1) auxiliary space — only scalar variables.

**Q4. What is the Kadane recurrence for the maximum subarray, and why does it work?**
A: `currentMax = max(x, currentMax + x)`. At each index, the best subarray ending here either starts fresh at `x` or extends the previous best. Tracking the maximum over all indices gives the global maximum.

**Q5. Why can `maxNormal` be initialized to `Integer.MIN_VALUE` instead of 0?**
A: Because the subarray must be non-empty. Initializing to `MIN_VALUE` ensures that even for all-negative arrays, the first element updates `maxNormal` correctly. Initializing to 0 would treat the empty subarray as valid.

**Q6. What is the answer for `[5, -3, 5]`, and which case wins?**
A: `10`, from the wrapping subarray `[5, 5]` (indices 2 and 0). `maxNormal = 7` (whole array), `total − minNormal = 10`, so the wrapping case wins.

**Q7. What is the answer for `[-3, -2, -3]`, and why?**
A: `-2`, the single element `-2`. All elements are negative, so the guard `maxNormal < 0` fires and returns `maxNormal = -2`.

**Q8. Why can both Kadane passes be done in a single loop?**
A: The max and min Kadane recurrences are independent — each depends only on its own previous value and the current element. So they can be updated in the same iteration without interference.

## 13. Final Code

### Java code (final, optimized double Kadane)

    class Solution {
        public int maxSubarraySumCircular(int[] nums) {
            int total = 0;
            int maxNormal = Integer.MIN_VALUE;
            int minNormal = Integer.MAX_VALUE;
            int currentMax = 0;
            int currentMin = 0;

            for (int x : nums) {
                total += x;
                currentMax = Math.max(x, currentMax + x);
                maxNormal = Math.max(maxNormal, currentMax);
                currentMin = Math.min(x, currentMin + x);
                minNormal = Math.min(minNormal, currentMin);
            }

            if (maxNormal < 0) return maxNormal;

            return Math.max(maxNormal, total - minNormal);
        }
    }

## 14. Self-Test

Consider `nums = [3, -1, 2, -1]`. Trace the double Kadane: for each element, show `currentMax`, `maxNormal`, `currentMin`, `minNormal`, and `total`. Then answer: what is `maxNormal`, what is `total − minNormal`, and which is the final answer? Explain why the wrapping subarray in this case is `[3, 2]` (indices 0 and 2) and why the excluded middle is `[-1, -1]` (indices 1 and 3). Finally, modify the array to `[-1, -2, -3]` and explain why the answer becomes `-1` rather than `0`.