---
title: "0153 — Find Minimum in Rotated Sorted Array"
layout: default
parent: Binary Search
grand_parent: Leetcode
nav_order: 153
permalink: /18-Binary-Search/0153-find-minimum-in-rotated-sorted-array/

problem: 153 - Find Minimum in Rotated Sorted Array
url: https://leetcode.com/problems/find-minimum-in-rotated-sorted-array/
difficulty: Medium
section: Binary Search
patterns: [binary-search-on-pivot, compare-with-right-end]
data_structures: [array]
time: O(log n)
space: O(1)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0153 — Find Minimum in Rotated Sorted Array

| Field | Value |
|---|---|
| **Pattern** | Binary Search on Pivot · Compare with Right End |
| **Difficulty** | Medium |
| **Section** | Binary Search |
| **Time** | O(log n) |
| **Space** | O(1) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Microsoft, Meta |
| **LeetCode** | [Find Minimum in Rotated Sorted Array]([https://leetcode.com/problems/find-minimum-in-rotated-sorted-array/](https://leetcode.com/problems/find-minimum-in-rotated-sorted-array/)) |

## 1. Problem in Simple Words

**Given:** an array `nums` of unique integers that was originally sorted ascending and then rotated between 1 and `n` times (rotating once moves the last element to the front).

**Required:** return the minimum element. The algorithm must run in `O(log n)`.

**Constraints that matter:**
- `1 <= n <= 5000` — a linear scan would pass the tests, but the statement requires O(log n), which is the real requirement. A single-element array is allowed.
- `-5000 <= nums[i] <= 5000` — small values; only comparisons are needed.
- **All values are unique** — so for two different indices, the values are never equal. This makes `nums[mid]` versus `nums[hi]` a strict comparison whenever `mid < hi`. With duplicates the reasoning breaks (that is the separate "Find Minimum II" problem).
- "Rotated between 1 and `n` times" — rotating `n` times returns the original sorted array, so **the array may look completely unrotated** (Example 3: `[11,13,15,17]`). The algorithm must not assume a visible drop exists.

## 2. Recognition Signals

- "Sorted array rotated at an unknown point" plus "O(log n)": binary search for the pivot, which is the position of the minimum.
- A rotated sorted array has two ascending runs, A (larger values) followed by B (smaller values), with a single drop between them, or only one run if it is unrotated. The minimum is the first element of B (or the first element overall).
- Comparing `nums[mid]` with the **right end** `nums[hi]` tells which run `mid` is in: larger means it is in A, smaller means it is in the same run as `hi`.
- Pattern: **binary search on the pivot by comparing `nums[mid]` with `nums[hi]`.**

## 3. Core Idea

Binary search on `[lo, hi]` for the minimum's index:
- If `nums[mid] > nums[hi]`, the drop lies somewhere in `(mid, hi]`, so the minimum is in `[mid + 1, hi]`; set `lo = mid + 1`.
- Otherwise `nums[mid] < nums[hi]`, so `[mid, hi]` is a sorted run, and the minimum of `[lo, hi]` is at `mid` or to its left; set `hi = mid`.

When `lo == hi`, `nums[lo]` is the minimum.

**Why it's correct:** write the range `[lo, hi]` as a run A followed by a run B, with every value in B smaller than every value in A (B may be empty if the range is unrotated). If `nums[mid] > nums[hi]`, then `mid` is in A and `hi` is in B, so the first element of B (the minimum) lies strictly to the right of `mid`. If `nums[mid] < nums[hi]`, then `mid` and `hi` are in the same run (a value in A is never less than a value in B), so `[mid, hi]` has no drop, and the minimum of `[lo, hi]` is at `mid` or earlier. Setting `hi = mid` (not `mid - 1`) keeps `mid` as a candidate, since it may itself be the minimum.

**Invariant:** the minimum of the whole array lies in `[lo, hi]`.

**Termination:** while `lo < hi`, `mid = lo + (hi - lo) / 2` satisfies `mid < hi`, so `nums[mid] != nums[hi]` (unique values) and both updates strictly shrink the range: `lo = mid + 1` increases `lo`, and `hi = mid` decreases `hi`.

## 4. Approach 1 — Brute Force

Scan the array and track the smallest value.

```java
public int findMinLinear(int[] nums) {
    int min = nums[0];
    for (int i = 1; i < nums.length; i++) {
        min = Math.min(min, nums[i]);
    }
    return min;
}
```

**Time:** O(n).
**Space:** O(1).

Not acceptable as the final answer: it ignores the sorted structure and violates the required O(log n). It is a useful correctness reference when testing.

## 5. Approach 2 — Optimized (Binary Search Against the Right End)

1. `lo = 0`, `hi = n - 1`.
2. While `lo < hi`: compute `mid = lo + (hi - lo) / 2`.
3. If `nums[mid] > nums[hi]`, set `lo = mid + 1`; otherwise set `hi = mid`.
4. Return `nums[lo]`.

```java
public int findMin(int[] nums) {
    int lo = 0;
    int hi = nums.length - 1;

    while (lo < hi) {
        int mid = lo + (hi - lo) / 2;
        if (nums[mid] > nums[hi]) {
            lo = mid + 1;
        } else {
            hi = mid;
        }
    }
    return nums[lo];
}
```

**Time:** O(log n); the range halves each iteration.
**Space:** O(1).

This is optimal for the requirement. Comparing against `nums[hi]` is deliberate: comparing against `nums[lo]` is ambiguous, because `nums[lo] <= nums[mid]` happens both when the range is unrotated (minimum on the left) and when it is rotated with `mid` in the larger run (minimum on the right).

## 6. Dry Run

Example 2: `nums = [4,5,6,7,0,1,2]`. The tricky part is that the comparison is against the right end, not the left.

| Iteration | lo | hi | mid | nums[mid] | nums[hi] | Test | Action |
|---|---|---|---|---|---|---|---|
| 1 | 0 | 6 | 3 | 7 | 2 | 7 > 2 | lo = 4 |
| 2 | 4 | 6 | 5 | 1 | 2 | 1 > 2 false | hi = 5 |
| 3 | 4 | 5 | 4 | 0 | 1 | 0 > 1 false | hi = 4 |

Exit condition: `lo == hi == 4`. Final answer: `nums[4] = 0`.

Example 3 (unrotated), `[11,13,15,17]`: `mid=1` (13) is not greater than `nums[3]=17`, so `hi=1`; then `mid=0` (11) is not greater than 13, so `hi=0`; the answer is `nums[0] = 11`.

## 7. Complexity Summary

| Approach | Time | Space | Notes |
|---|---|---|---|
| Linear scan | O(n) | O(1) | Violates the O(log n) requirement |
| Binary search against `nums[hi]` | O(log n) | O(1) | Handles the unrotated case without special-casing |

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|---|---|---|
| Single element | Range of one | `lo == hi == 0`; the loop never runs; returns `nums[0]` |
| Fully unrotated (`[11,13,15,17]`) | No visible drop; the minimum is at index 0 | `nums[mid] > nums[hi]` is never true, so `hi` keeps shrinking to `0` |
| Rotated once (`[4,1,2,3]`) | The minimum is at index 1, right next to the maximum | First comparison `nums[1]=1 > nums[3]=3` is false so `hi=1`; then `lo=0`, `mid=0`: `4 > 1` so `lo=1` |
| Two elements descending (`[2,1]`) | Smallest non-trivial rotated range | `mid=0`, `2 > 1` so `lo=1`; returns `1` |
| Two elements ascending (`[1,2]`) | Unrotated pair | `mid=0`, `1 > 2` false so `hi=0`; returns `1` |
| Minimum at the last index (`[2,3,4,1]`) | Pivot at the far right | `nums[mid] > nums[hi]` repeatedly moves `lo` right until it reaches the end |
| Minimum at `mid` | Must not be discarded | The non-greater branch uses `hi = mid`, keeping `mid` |
| Negative values | Nothing special | Only comparisons are used |

## 9. Java Notes

- Compare with `nums[hi]`, not `nums[lo]`. This choice removes the need for a separate "is the array rotated at all?" check.
- Use `hi = mid` (not `mid - 1`) because `mid` can be the minimum. Pair it with `lo < hi` so the loop terminates.
- `mid = lo + (hi - lo) / 2` rounds down, so `mid < hi` while `lo < hi`; hence `nums[mid]` and `nums[hi]` are different elements and, being unique, never equal.
- The method returns a **value**, `nums[lo]`, not an index.
- With `n <= 5000`, overflow is irrelevant; the safe midpoint form is simply a habit.

## 10. Common Mistakes

- Comparing `nums[mid]` with `nums[lo]` and treating `nums[mid] >= nums[lo]` as "minimum is on the right". That is wrong for an unrotated range (such as `[11,13,15,17]`), where the minimum is at the left. Fix: compare with `nums[hi]`.
- Setting `hi = mid - 1` when `nums[mid] < nums[hi]`, which can discard the minimum when it sits exactly at `mid`. Fix: `hi = mid`.
- Setting `lo = mid` instead of `mid + 1` when `nums[mid] > nums[hi]`. Since `mid` can equal `lo`, the range may stop shrinking. Fix: `lo = mid + 1`.
- Using `while (lo <= hi)` together with `hi = mid`, which loops forever when `lo == hi`. Fix: `while (lo < hi)`.
- Assuming a drop always exists and searching for `nums[i] > nums[i + 1]`, which fails when the array was rotated `n` times. Fix: use the right-end comparison.
- Applying this code to arrays with duplicates. With equal values, `nums[mid] == nums[hi]` leaves the side ambiguous. Fix: that variant needs `hi--` when equal.

## 11. Interview Takeaway

- **Trigger sentence:** "The minimum of a rotated sorted array in O(log n) is a binary search on the pivot: if `nums[mid] > nums[hi]` the minimum is to the right, otherwise it is at `mid` or to the left."
- **90-second explanation:** A rotated sorted array is two ascending runs, with every value in the second run smaller than every value in the first, and the minimum is the start of the second run (or index 0 if there is no rotation). I binary search for it by comparing `nums[mid]` with the right end `nums[hi]`. If `nums[mid] > nums[hi]`, `mid` is in the larger first run and `hi` is in the second, so the drop, and therefore the minimum, is strictly to the right: `lo = mid + 1`. Otherwise `mid` and `hi` are in the same sorted run, so the minimum is at `mid` or earlier: `hi = mid`, keeping `mid` since it could be the answer. I loop while `lo < hi` and return `nums[lo]`. Comparing with the left end would be ambiguous because an unrotated range and a rotated one can both satisfy `nums[lo] <= nums[mid]`. Values are unique, so `nums[mid]` never equals `nums[hi]` for `mid < hi`. That gives O(log n) time and O(1) space.
- **Related problems:** 33 (Search in Rotated Sorted Array), 154 (Find Minimum in Rotated Sorted Array II), 162 (Find Peak Element), 704 (Binary Search).

## 12. Recall Questions

**Q:** Why compare `nums[mid]` with `nums[hi]` rather than with `nums[lo]`?
**A:** `nums[lo] <= nums[mid]` can occur both when the range is unrotated (minimum on the left) and when `mid` is in the larger run of a rotated range (minimum on the right), so it is ambiguous. The comparison with `nums[hi]` is unambiguous.

**Q:** What does `nums[mid] > nums[hi]` tell you?
**A:** `mid` is in the larger first run and `hi` in the smaller second run, so the drop, and therefore the minimum, lies strictly to the right of `mid`.

**Q:** Why is the other branch `hi = mid` and not `hi = mid - 1`?
**A:** When `nums[mid] < nums[hi]` the minimum is at `mid` or to its left, and `mid` itself may be the minimum, so it must stay in the range.

**Q:** How does the algorithm handle an array rotated `n` times (fully sorted)?
**A:** `nums[mid] > nums[hi]` is never true, so `hi` keeps moving left until it reaches index 0, which holds the minimum.

**Q:** Why does uniqueness of values matter?
**A:** It guarantees `nums[mid] != nums[hi]` for `mid < hi`, so each comparison picks a definite side. With duplicates, equality leaves the side undetermined.

## 13. Final Code

```java
class Solution {
    public int findMin(int[] nums) {
        int lo = 0;
        int hi = nums.length - 1;

        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] > nums[hi]) {
                lo = mid + 1;
            } else {
                hi = mid;
            }
        }
        return nums[lo];
    }
}
```

## 14. Self-Test

Given an array of unique integers that was sorted ascending and then rotated between 1 and `n` times (so it may look unrotated), return its minimum in O(log n). Re-derive: binary search with `lo = 0`, `hi = n - 1`; while `lo < hi`, take `mid`; if `nums[mid] > nums[hi]` the minimum lies right of `mid`, so `lo = mid + 1`; otherwise it is at `mid` or left, so `hi = mid`; return `nums[lo]`.
