---
title: "0162 — Find Peak Element"
layout: default
parent: Binary Search
grand_parent: Leetcode
nav_order: 162
permalink: /18-Binary-Search/0162-find-peak-element/

problem: 162 - Find Peak Element
url: https://leetcode.com/problems/find-peak-element/
difficulty: Medium
section: Binary Search
patterns: [binary-search-on-slope, boundary-invariant]
data_structures: [array]
time: O(log n)
space: O(1)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0162 — Find Peak Element

| Field | Value |
|---|---|
| **Pattern** | Binary Search on Slope · Boundary Invariant |
| **Difficulty** | Medium |
| **Section** | Binary Search |
| **Time** | O(log n) |
| **Space** | O(1) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Microsoft, Meta |
| **LeetCode** | [Find Peak Element]([https://leetcode.com/problems/find-peak-element/](https://leetcode.com/problems/find-peak-element/)) |

## 1. Problem in Simple Words

**Given:** an integer array `nums` where no two adjacent elements are equal.

**Required:** return the index of **any** peak: an element strictly greater than both neighbors. Imagine `nums[-1]` and `nums[n]` are `-∞`, so the first or last element can be a peak if it beats its single real neighbor. The algorithm must run in `O(log n)` time.

**Constraints that matter:**
- `1 <= nums.length <= 1000` — a linear scan would pass the tests, but the statement explicitly demands O(log n), so that is the real requirement. A one-element array is allowed (it is itself a peak).
- `-2^31 <= nums[i] <= 2^31 - 1` — values span the full `int` range, so **never subtract values to compare them** (`nums[mid+1] - nums[mid]` can overflow). Use `<` and `>` only.
- `nums[i] != nums[i + 1]` for all valid `i` — this guarantee is essential: with equal neighbors (plateaus), "which way is uphill?" would be undefined and binary search could fail. Because adjacent values are always distinct, every comparison between neighbors has a definite direction.
- Any peak is acceptable, so the answer is not unique (Example 2 may return index 1 or 5). This freedom is what makes a binary search possible.

(The first copy of the page I fetched was an older version without these constraints; the values above come from the current problem page.)

## 2. Recognition Signals

- "O(log n)" on an **unsorted** array. Binary search needs some property that tells you which half to keep, and here it is not sortedness but slope.
- "Return any one of the valid answers" together with boundaries defined as `-∞`: a peak is guaranteed to exist, and you only need to steer toward one.
- Adjacent elements are guaranteed distinct, so at any index you can say whether the array is going up or down to the right.
- Pattern: **binary search on the slope: compare `nums[mid]` with `nums[mid + 1]` and move toward the larger side.**

## 3. Core Idea

Binary search over the index range `[lo, hi]` (both ends inclusive). At `mid`, compare it with its right neighbor:
- If `nums[mid] < nums[mid + 1]`, the array rises to the right of `mid`, so a peak exists in `[mid + 1, hi]`; set `lo = mid + 1`.
- Otherwise `nums[mid] > nums[mid + 1]`, so a peak exists in `[lo, mid]` (including `mid` itself); set `hi = mid`.

When `lo == hi`, that index is a peak.

**Why it's correct:** keep the invariant that `nums[lo - 1] < nums[lo]` and `nums[hi] > nums[hi + 1]`, where out-of-range neighbors count as `-∞`. Under this invariant, the maximum element of `nums[lo..hi]` is a peak: its neighbors inside the range are smaller (adjacent values are distinct), and if it sits at an end of the range, the invariant makes the outside neighbor smaller too. So a peak always lies in `[lo, hi]`. The invariant holds initially because both ends of the whole array are next to `-∞`. Moving `lo = mid + 1` keeps it, since `nums[mid] < nums[mid + 1]` is exactly `nums[lo - 1] < nums[lo]` for the new `lo`. Moving `hi = mid` keeps it, since `nums[mid] > nums[mid + 1]` is exactly `nums[hi] > nums[hi + 1]` for the new `hi`.

**Termination:** `mid = lo + (hi - lo) / 2` rounds down, so while `lo < hi` we have `mid < hi` and `mid + 1 <= hi` is a valid index. Either `lo` increases to `mid + 1` or `hi` decreases to `mid`, so the range strictly shrinks and the loop cannot run forever.

**Invariant:** `nums[lo - 1] < nums[lo]` and `nums[hi] > nums[hi + 1]` (virtual `-∞` outside the array), so a peak lies in `[lo, hi]`.

## 4. Approach 1 — Brute Force

Scan left to right and return the first index that is greater than its right neighbor. Everything before it was strictly increasing, so it also beats its left neighbor. If there is no such index, the array is strictly increasing and the last element is the peak.

```java
public int findPeakElementLinear(int[] nums) {
    for (int i = 0; i < nums.length - 1; i++) {
        if (nums[i] > nums[i + 1]) {
            return i;
        }
    }
    return nums.length - 1;
}
```

**Time:** O(n).
**Space:** O(1).

Not acceptable as the final answer: the problem requires O(log n). It is correct (and would pass the tests at n ≤ 1000), but it does not use the structure that lets us discard half the array on each comparison.

## 5. Approach 2 — Optimized (Binary Search on the Slope)

1. Set `lo = 0`, `hi = nums.length - 1`.
2. While `lo < hi`: compute `mid = lo + (hi - lo) / 2`.
3. If `nums[mid] < nums[mid + 1]`, set `lo = mid + 1`; otherwise set `hi = mid`.
4. Return `lo`.

```java
public int findPeakElement(int[] nums) {
    int lo = 0;
    int hi = nums.length - 1;

    while (lo < hi) {
        int mid = lo + (hi - lo) / 2;
        if (nums[mid] < nums[mid + 1]) {
            lo = mid + 1;
        } else {
            hi = mid;
        }
    }
    return lo;
}
```

**Time:** O(log n); the search range halves each iteration.
**Space:** O(1).

This is optimal for the stated requirement. The search space is the index range `[lo, hi]`, and each comparison discards at least half of it while preserving the invariant above.

## 6. Dry Run

Example 2: `nums = [1,2,1,3,5,6,4]` (n = 7). The tricky part is that the search can skip over the smaller peak at index 1.

| Iteration | lo | hi | mid | Compare | Action | New range |
|---|---|---|---|---|---|---|
| 1 | 0 | 6 | 3 | nums[3]=3 < nums[4]=5 (rising) | lo = 4 | [4, 6] |
| 2 | 4 | 6 | 5 | nums[5]=6 > nums[6]=4 (falling) | hi = 5 | [4, 5] |
| 3 | 4 | 5 | 4 | nums[4]=5 < nums[5]=6 (rising) | lo = 5 | [5, 5] |

Exit condition: `lo == hi == 5`. Final answer: index `5` (value 6), which is a valid peak. (Example 1, `[1,2,3,1]`, goes `lo=0,hi=3` to `mid=1` rising so `lo=2`, then `mid=2` falling so `hi=2`, returning `2`.)

## 7. Complexity Summary

| Approach | Time | Space | Notes |
|---|---|---|---|
| Linear scan for first descent | O(n) | O(1) | Correct, but violates the O(log n) requirement |
| Binary search on the slope | O(log n) | O(1) | Compares only `nums[mid]` and `nums[mid + 1]` |

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|---|---|---|
| One element | No neighbors at all | `lo == hi == 0`, so the loop never runs and `0` is returned |
| Two elements, ascending (`[1,2]`) | Peak is the last element | `mid=0`, `1 < 2` so `lo=1`; returns `1` |
| Two elements, descending (`[2,1]`) | Peak is the first element | `mid=0`, `2 > 1` so `hi=0`; returns `0` |
| Strictly increasing | Peak is the last index, only reachable via the right boundary `-∞` | Every comparison goes right until `lo = n - 1` |
| Strictly decreasing | Peak is index 0, only reachable via the left boundary `-∞` | Every comparison goes left until `hi = 0` |
| Multiple peaks | Result may differ from the sample's | Any peak is accepted; the search returns the one it converges to |
| Values at `Integer.MIN_VALUE` / `Integer.MAX_VALUE` | Subtraction-based comparison would overflow | Only `<` is used; no arithmetic on values |
| Equal adjacent values | Would make the slope undefined | Excluded by the constraint `nums[i] != nums[i + 1]`, which the algorithm relies on |

## 9. Java Notes

- Compare with `nums[mid] < nums[mid + 1]` directly. Writing `nums[mid + 1] - nums[mid] > 0` can overflow because values range over the full `int` span.
- `nums[mid + 1]` is always in bounds: while `lo < hi`, `mid < hi <= n - 1`, so `mid + 1 <= n - 1`. This is why the loop condition is `lo < hi` rather than `lo <= hi`.
- `mid = lo + (hi - lo) / 2` rounds down, which is what guarantees `mid < hi`. With `n <= 1000` the plain `(lo + hi) / 2` would not overflow, but the habit costs nothing.
- No sentinel padding of the array is needed; the `-∞` boundaries are handled implicitly by the invariant and the choice of which side to keep.
- The method returns `lo`, which equals `hi` at termination.

## 10. Common Mistakes

- Using `while (lo <= hi)` with `hi = mid - 1` and reading `nums[mid + 1]`, which goes out of bounds at the last index. Fix: use `lo < hi` and `hi = mid`.
- Setting `lo = mid` instead of `mid + 1`. Because `mid` can equal `lo`, the range stops shrinking and the loop never ends. Fix: `lo = mid + 1` when `nums[mid] < nums[mid + 1]`.
- Setting `hi = mid - 1` in the falling case, which discards `mid`, the very element that might be the peak. Fix: `hi = mid`.
- Comparing `nums[mid]` with `nums[mid - 1]` at `mid = 0` without a bounds check. Fix: compare with the right neighbor only, which is always valid inside the loop.
- Searching for the global maximum, which is a harder problem and unnecessary since any peak is accepted.
- Comparing with subtraction and overflowing for extreme values. Fix: use `<` / `>`.

## 11. Interview Takeaway

- **Trigger sentence:** "Any peak in O(log n) with distinct neighbors and `-∞` ends means binary search on the slope: compare `mid` with `mid + 1` and keep the side that goes uphill."
- **90-second explanation:** The array isn't sorted, but I can still halve the search space using slope. At `mid` I look at the right neighbor. If it's larger, the array is rising there, and since the far end of the array is effectively `-∞`, it must eventually come down, so a peak exists strictly to the right and I set `lo = mid + 1`. If the right neighbor is smaller, `mid` itself could be a peak or one exists to its left, so I keep `[lo, mid]` with `hi = mid`. Adjacent values are guaranteed distinct, so every comparison has a definite answer. The invariant is that `nums[lo-1] < nums[lo]` and `nums[hi] > nums[hi+1]`, counting out-of-range neighbors as `-∞`, which guarantees a peak lies inside `[lo, hi]`. With `mid` rounded down, `mid + 1` is always valid and the range strictly shrinks, so there's no infinite loop. When `lo == hi`, that index is a peak. It runs in O(log n) time and O(1) space, and I compare with `<` rather than subtracting to avoid overflow at extreme `int` values.
- **Related problems:** 852 (Peak Index in a Mountain Array), 1901 (Find a Peak Element II), 33 (Search in Rotated Sorted Array), 153 (Find Minimum in Rotated Sorted Array).

## 12. Recall Questions

**Q:** Why can we discard the left half when `nums[mid] < nums[mid + 1]`?
**A:** The array is rising at `mid`, and the right end is next to `-∞`, so the sequence must come back down somewhere to the right. A peak therefore exists in `[mid + 1, hi]`.

**Q:** Why is `hi = mid` (not `mid - 1`) in the falling case?
**A:** `nums[mid] > nums[mid + 1]`, so `mid` may itself be the peak, or one lies to its left. Excluding `mid` could discard the only peak.

**Q:** Why is `nums[mid + 1]` always a valid index?
**A:** The loop runs only while `lo < hi`, and `mid` rounds down, so `mid < hi <= n - 1`, hence `mid + 1 <= n - 1`.

**Q:** Why does the constraint `nums[i] != nums[i + 1]` matter?
**A:** It guarantees every adjacent comparison is strictly up or strictly down. With a plateau, the slope would be undefined and the algorithm could not tell which side to keep.

**Q:** Why can `lo = mid` cause an infinite loop?
**A:** `mid` can equal `lo`, so the assignment changes nothing and the range never shrinks.

## 13. Final Code

```java
class Solution {
    public int findPeakElement(int[] nums) {
        int lo = 0;
        int hi = nums.length - 1;

        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] < nums[mid + 1]) {
                lo = mid + 1;
            } else {
                hi = mid;
            }
        }
        return lo;
    }
}
```

## 14. Self-Test

Given an integer array (length 1 to 1000, adjacent elements never equal, values anywhere in the `int` range), return the index of any element strictly greater than its neighbors, treating positions outside the array as `-∞`, in O(log n) time. Re-derive: binary search with `lo = 0`, `hi = n - 1`; while `lo < hi`, take `mid`, and if `nums[mid] < nums[mid + 1]` set `lo = mid + 1`, otherwise set `hi = mid`; return `lo`.
