---
title: "0053 — Maximum Subarray"
layout: default
parent: Kadane-Algorithm
grand_parent: Leetcode
nav_order: 53
permalink: /17-Kadane-Algorithm/0053-maximum-subarray/

problem: 53 - Maximum Subarray
url: "https://leetcode.com/problems/maximum-subarray/"
difficulty: Medium
section: Array / Kadane's Algorithm
patterns: [kadane, dynamic-programming]
data_structures: [array]
time: O(n)
space: O(1)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0053 — Maximum Subarray**

| Field | Value |
|---|---|
| **Pattern** | Kadane's Algorithm + Dynamic Programming |
| **Difficulty** | Medium |
| **Section** | Array / Kadane's Algorithm |
| **Time** | O(n) |
| **Space** | O(1) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Amazon, Google, Meta, Microsoft, Bloomberg |
| **LeetCode** | [Maximum Subarray](https://leetcode.com/problems/maximum-subarray/) |


## 1. Problem in Simple Words

**Given:** an integer array `nums`.

**Required:** return the largest sum of any **non-empty contiguous** subarray.

**Constraints that matter:**
- `1 <= nums.length <= 10^5` — checking every subarray is O(n²), about 5 * 10^9 operations in the worst case, too slow. O(n) is the target. The array is never empty, so there is always at least one valid subarray.
- `-10^4 <= nums[i] <= 10^4` — negatives are allowed, which is what makes the problem non-trivial. If every number were positive, the answer would just be the sum of the whole array. All values can also be negative, in which case the answer is the single largest element, not 0.
- The largest possible magnitude of any subarray sum is `10^5 * 10^4 = 10^9`, which fits in an `int` (max about 2.147 * 10^9), so no `long` is needed under these constraints.
- Follow-up: after the O(n) solution, try a divide and conquer solution, which is more subtle.

(Older copies of this problem online list different bounds; the values above are from the current problem page.)

## 2. Recognition Signals

- "Largest sum of a **contiguous** subarray" in an array with negatives.
- A running sum that you would want to "throw away" when it turns negative, because a negative prefix can only hurt whatever follows it.
- "Best answer ending at position `i`" being computable from "best answer ending at `i - 1`" (a one-variable DP).
- Pattern: **Kadane's algorithm**, a DP whose state is "the best sum of a subarray ending exactly here".

## 3. Core Idea

Let `current` be the best sum of a subarray that ends exactly at index `i`. Such a subarray is either just `nums[i]` by itself, or `nums[i]` appended to the best subarray ending at `i - 1`. So `current = nums[i] + max(0, previous current)`: extend the previous run only if it is positive, otherwise start fresh. The answer is the maximum `current` seen at any index.

**Why it's correct:** every non-empty subarray ends at some index, so the maximum over all indices of "best subarray ending here" is the global maximum. For a fixed end index, a prefix of the subarray with a negative sum can always be dropped to get a larger sum, so the best subarray ending at `i` never includes a negative-sum prefix, which is exactly what the `max(0, ...)` enforces.

**Invariant:** after processing index `i`, `current` equals the maximum sum of a non-empty subarray ending at `i`, and `best` equals the maximum of `current` over all indices up to `i`.

## 4. Approach 1 — Brute Force

For every starting index, extend the subarray one element at a time, keeping a running sum, and track the maximum seen.

```java
public int maxSubArrayBruteForce(int[] nums) {
    int best = nums[0];
    for (int start = 0; start < nums.length; start++) {
        int sum = 0;
        for (int end = start; end < nums.length; end++) {
            sum += nums[end];
            best = Math.max(best, sum);
        }
    }
    return best;
}
```

**Time:** O(n²). Every (start, end) pair is visited once.
**Space:** O(1).

Not optimal: with `n` up to `10^5` this is about 5 * 10^9 steps. It also recomputes overlapping information: the best subarray ending at `end` is closely related to the best subarray ending at `end - 1`, but the brute force throws that away.

## 5. Approach 2 — Optimized

### 5a. Kadane's algorithm (O(n), O(1))

1. Start with `current = best = nums[0]`.
2. For each later element, set `current = max(nums[i], current + nums[i])`.
3. Update `best = max(best, current)`.

```java
public int maxSubArray(int[] nums) {
    int best = nums[0];
    int current = nums[0];

    for (int i = 1; i < nums.length; i++) {
        current = Math.max(nums[i], current + nums[i]);
        best = Math.max(best, current);
    }
    return best;
}
```

**Time:** O(n), one pass.
**Space:** O(1), two integers. It is optimal, since every element must be read.

### 5b. Divide and conquer (the follow-up)

The best subarray in `[lo, hi]` lies entirely in the left half, entirely in the right half, or **crosses the midpoint**. Solve the halves recursively. For the crossing case, the best crossing subarray is the best suffix of the left half (ending at `mid`) plus the best prefix of the right half (starting at `mid + 1`).

```java
public int maxSubArrayDivideAndConquer(int[] nums) {
    return solve(nums, 0, nums.length - 1);
}

private int solve(int[] nums, int lo, int hi) {
    if (lo == hi) {
        return nums[lo];
    }

    int mid = lo + (hi - lo) / 2;
    int leftBest = solve(nums, lo, mid);
    int rightBest = solve(nums, mid + 1, hi);

    int sum = 0;
    int bestLeftSuffix = Integer.MIN_VALUE;
    for (int i = mid; i >= lo; i--) {
        sum += nums[i];
        bestLeftSuffix = Math.max(bestLeftSuffix, sum);
    }

    sum = 0;
    int bestRightPrefix = Integer.MIN_VALUE;
    for (int i = mid + 1; i <= hi; i++) {
        sum += nums[i];
        bestRightPrefix = Math.max(bestRightPrefix, sum);
    }

    return Math.max(Math.max(leftBest, rightBest), bestLeftSuffix + bestRightPrefix);
}
```

**Time:** O(n log n): `T(n) = 2T(n/2) + O(n)`.
**Space:** O(log n) for the recursion stack.

Kadane's is optimal and is the answer to give. Divide and conquer is only worth presenting when asked for the follow-up (or when you need a structure that merges in parallel or on a segment tree, where each segment stores its total, best prefix, best suffix and best subarray, so merging two segments is O(1) and the whole thing is O(n)).

## 6. Dry Run

`nums = [-2,1,-3,4,-1,2,1,-5,4]`. The tricky part is the points where a negative running sum is abandoned.

| i | nums[i] | current + nums[i] | Decision | current | best |
|---|---|---|---|---|---|
| 0 | -2 | (initial) | start | -2 | -2 |
| 1 | 1 | -1 | restart (1 > -1) | 1 | 1 |
| 2 | -3 | -2 | extend (-2 > -3) | -2 | 1 |
| 3 | 4 | 2 | restart (4 > 2) | 4 | 4 |
| 4 | -1 | 3 | extend | 3 | 4 |
| 5 | 2 | 5 | extend | 5 | 5 |
| 6 | 1 | 6 | extend | 6 | 6 |
| 7 | -5 | 1 | extend | 1 | 6 |
| 8 | 4 | 5 | extend | 5 | 6 |

Exit condition: the loop ends after the last index. Final answer: `6`, from the subarray `[4,-1,2,1]` (restarted at index 3 and last extended at index 6).

## 7. Complexity Summary

| Approach | Time | Space | Notes |
|---|---|---|---|
| Brute force over all subarrays | O(n²) | O(1) | Too slow at n = 10^5 |
| Kadane's algorithm | O(n) | O(1) | Standard answer |
| Divide and conquer (crossing sum) | O(n log n) | O(log n) | The follow-up; subtle crossing case |

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|---|---|---|
| Single element | Smallest input | Initial `best = current = nums[0]`; the loop does not run |
| All negative (e.g. `[-3,-1,-2]`) | The answer is the largest single element (`-1`), not `0`, since the subarray must be non-empty | `best` starts at `nums[0]`, not `0`, and `current` is never forced up to `0` |
| All positive | The whole array is the answer | `current` keeps growing; `best` tracks it |
| Zeros mixed in | Equal choices when extending or restarting | `max` picks either; the sum is the same |
| A negative that is worth bridging (`[5,-1,5]` gives 9) vs not (`[5,-6,5]` gives 5) | Depends on whether the running sum stays positive | `current + nums[i]` vs `nums[i]` decides each time |
| Large values | Sums up to about 10^9 | Fits in `int` under these constraints |
| Starting `current` at `Integer.MIN_VALUE` | `current + nums[i]` would overflow | Avoided by starting from `nums[0]` |

## 9. Java Notes

- Initialize `best` and `current` from `nums[0]`, not from `0` and not from `Integer.MIN_VALUE`. Zero breaks all-negative arrays; `Integer.MIN_VALUE` plus a negative number overflows and wraps to a large positive value.
- `int` is safe here because the largest possible magnitude is `10^5 * 10^4 = 10^9 < 2^31 - 1`. With looser constraints, use `long`.
- `Math.max(nums[i], current + nums[i])` is equivalent to `nums[i] + Math.max(0, current)`; use whichever reads clearer.
- In divide and conquer, compute the midpoint as `lo + (hi - lo) / 2`; with these bounds `(lo + hi) / 2` would not overflow, but the habit is free. The crossing loops always run at least once because `lo <= mid < hi`, so the `Integer.MIN_VALUE` initial values are always replaced.
- The recursion depth is about `log2(10^5) ≈ 17`, so there is no stack risk.

## 10. Common Mistakes

- Initializing `best = 0`, which returns `0` for all-negative input. Fix: start from `nums[0]`.
- Resetting the running sum to `0` and only then adding the element when it is negative, while also initializing `best` to `0`. Fix: restart at `nums[i]` itself, not `0`.
- Starting `current` at `Integer.MIN_VALUE`, causing overflow when a negative is added. Fix: start from `nums[0]`.
- Using the brute force on `n = 10^5` and timing out. Fix: Kadane's algorithm.
- Treating the problem as a subsequence problem (picking any positive numbers). The subarray must be contiguous. Fix: either extend the current run or restart it; never skip elements.
- In divide and conquer, forgetting the crossing case and only taking `max(leftBest, rightBest)`, which misses subarrays spanning the midpoint. Fix: add best left suffix plus best right prefix.

## 11. Interview Takeaway

- **Trigger sentence:** "Largest sum of a contiguous subarray means Kadane's algorithm: the best sum ending here is the current number plus the previous best if that is positive, otherwise just the current number."
- **90-second explanation:** I scan the array once, keeping two numbers. `current` is the best sum of a subarray that ends at the current index, and `best` is the best seen anywhere. At each element, I either extend the previous run or start a new one, whichever is larger: `current = max(nums[i], current + nums[i])`. A running sum that has gone negative can only hurt anything that follows, so it is discarded. Because every subarray ends somewhere, the maximum of `current` over all indices is the answer. I initialize both from `nums[0]` so an all-negative array correctly returns its largest element rather than zero. It is one pass, O(n) time and O(1) space. If asked for divide and conquer, I split in half, take the best of the left half, the right half, and the best subarray crossing the midpoint (best left suffix plus best right prefix), giving O(n log n).
- **Related problems:** 121 (Best Time to Buy and Sell Stock), 152 (Maximum Product Subarray), 918 (Maximum Sum Circular Subarray), 1749 (Maximum Absolute Sum of Any Subarray).

## 12. Recall Questions

**Q:** Why is it safe to discard a running sum that has become negative?
**A:** Any subarray that begins with a negative-sum prefix can be improved by dropping that prefix, so the best subarray ending at a later index never includes it.

**Q:** Why is the answer for an all-negative array not `0`?
**A:** The subarray must be non-empty, so the best choice is the single largest (least negative) element.

**Q:** Why initialize `current` and `best` from `nums[0]` rather than `Integer.MIN_VALUE`?
**A:** Adding a negative number to `Integer.MIN_VALUE` overflows and wraps to a large positive value, corrupting the comparison; starting from `nums[0]` avoids that and also handles one-element arrays.

**Q:** In divide and conquer, what are the three places the best subarray can be?
**A:** Entirely in the left half, entirely in the right half, or crossing the midpoint, in which case it is the best suffix of the left half plus the best prefix of the right half.

**Q:** Why does the crossing computation take O(n) per level?
**A:** It scans each half once from the midpoint outward, so each level of the recursion does work proportional to the segment lengths, which sum to n per level.

## 13. Final Code

```java
class Solution {
    public int maxSubArray(int[] nums) {
        int best = nums[0];
        int current = nums[0];

        for (int i = 1; i < nums.length; i++) {
            current = Math.max(nums[i], current + nums[i]);
            best = Math.max(best, current);
        }
        return best;
    }
}
```

## 14. Self-Test

Given an integer array (values may be negative, length at least 1), return the largest sum of any non-empty contiguous subarray in O(n) time and O(1) space. Re-derive: scan once keeping `current`, the best sum of a subarray ending at the current index, and `best`, the best seen; start both at `nums[0]`; for each next element set `current` to the larger of the element alone and `current` plus the element, then update `best`.
