---
title: "0034 — Find First and Last Position of Element in Sorted Array"
layout: default
parent: Binary Search
grand_parent: Leetcode
nav_order: 34
permalink: /18-Binary-Search/0034-find-first-and-last-position-of-element-in-sorted-array/

problem: 34 - Find First and Last Position of Element in Sorted Array
url: https://leetcode.com/problems/find-first-and-last-position-of-element-in-sorted-array/
difficulty: Medium
section: Binary Search
patterns: [lower-bound, upper-bound, binary-search-boundaries]
data_structures: [array]
time: O(log n)
space: O(1)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0034 — Find First and Last Position of Element in Sorted Array

| Field | Value |
|---|---|
| **Pattern** | Lower Bound · Upper Bound · Binary Search Boundaries |
| **Difficulty** | Medium |
| **Section** | Binary Search |
| **Time** | O(log n) |
| **Space** | O(1) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Microsoft, Meta |
| **LeetCode** | [Find First and Last Position of Element in Sorted Array]([https://leetcode.com/problems/find-first-and-last-position-of-element-in-sorted-array/](https://leetcode.com/problems/find-first-and-last-position-of-element-in-sorted-array/)) |

## 1. Problem in Simple Words

**Given:** an array `nums` sorted in non-decreasing order (duplicates allowed) and an integer `target`.

**Required:** return `[firstIndex, lastIndex]` of `target` in `nums`, or `[-1, -1]` if it is absent. The algorithm must run in `O(log n)`.

**Constraints that matter:**
- `0 <= nums.length <= 10^5` — the array can be **empty** (Example 3), so any code that touches `nums[0]` or `nums[mid]` before checking the length can crash. At this size a linear scan would pass, but the statement requires O(log n).
- `-10^9 <= nums[i], target <= 10^9` — values stay within `int` range, and only comparisons are needed. Avoid tricks like `target + 1` near `Integer.MAX_VALUE`; here it would be safe, but the code below does not depend on it.
- The array is **non-decreasing**, so equal values are contiguous. That is what makes "first" and "last" occurrence well-defined and findable by binary search.
- The target may be absent, present once, or fill the entire array (O(n) copies). A solution that finds one occurrence and then walks outward would degrade to O(n) in that last case.

## 2. Recognition Signals

- "Sorted array" plus "O(log n)" plus "starting and ending position": two boundaries of a contiguous run of equal values.
- "First index where something becomes true" is the signature of a **boundary binary search**: in a sorted array, the predicate `nums[i] >= target` is false for a prefix and then true for the rest.
- The first occurrence is the **lower bound** (first index with `nums[i] >= target`); the position just after the last occurrence is the **upper bound** (first index with `nums[i] > target`).
- Pattern: **two boundary binary searches (lower bound and upper bound) over the half-open range `[0, n)`.**

## 3. Core Idea

Define two helpers over the search range `[0, n]` (the answer can be `n` when no element qualifies):
- `lowerBound(target)`: the first index `i` with `nums[i] >= target`.
- `upperBound(target)`: the first index `i` with `nums[i] > target`.

The first occurrence of `target` is `lowerBound(target)`, provided that index is inside the array and `nums[first] == target`. The last occurrence is `upperBound(target) - 1`.

**Why it's correct:** because `nums` is sorted, `nums[i] >= target` is false for some prefix and true for the remaining suffix, so the boundary between them is exactly what binary search finds. All elements equal to `target` lie between the lower bound (inclusive) and the upper bound (exclusive). If no element equals `target`, then either `lowerBound == n` or `nums[lowerBound] != target`, which the guard catches.

**Invariant (both searches, range `[lo, hi)`):** every index below `lo` fails the predicate, and every index at or above `hi` satisfies it (for the lower bound, "fails" means `nums[i] < target`; for the upper bound, "fails" means `nums[i] <= target`).

**Termination:** while `lo < hi`, `mid = lo + (hi - lo) / 2` satisfies `lo <= mid < hi`. Either `lo = mid + 1` (grows) or `hi = mid` (shrinks, since `mid < hi`), so the range strictly shrinks and the loop ends with `lo == hi` as the boundary.

## 4. Approach 1 — Brute Force

Scan left to right for the first match, and right to left (or continue the scan) for the last match.

```java
public int[] searchRangeLinear(int[] nums, int target) {
    int first = -1;
    int last = -1;
    for (int i = 0; i < nums.length; i++) {
        if (nums[i] == target) {
            if (first == -1) {
                first = i;
            }
            last = i;
        }
    }
    return new int[] {first, last};
}
```

**Time:** O(n).
**Space:** O(1).

Not acceptable as the final answer: it ignores sortedness and violates the required O(log n). A common half-measure, one binary search to find *any* occurrence followed by walking outward to the ends, is also O(n) in the worst case (for example, an array of all equal values).

## 5. Approach 2 — Optimized (Lower Bound and Upper Bound)

1. Compute `first = lowerBound(nums, target)`.
2. If `first == n` or `nums[first] != target`, return `[-1, -1]`.
3. Compute `last = upperBound(nums, target) - 1`.
4. Return `[first, last]`.

```java
public int[] searchRange(int[] nums, int target) {
    int first = lowerBound(nums, target);
    if (first == nums.length || nums[first] != target) {
        return new int[] {-1, -1};
    }
    int last = upperBound(nums, target) - 1;
    return new int[] {first, last};
}

// First index i in [0, n] with nums[i] >= target (n if none).
private int lowerBound(int[] nums, int target) {
    int lo = 0;
    int hi = nums.length;
    while (lo < hi) {
        int mid = lo + (hi - lo) / 2;
        if (nums[mid] < target) {
            lo = mid + 1;
        } else {
            hi = mid;
        }
    }
    return lo;
}

// First index i in [0, n] with nums[i] > target (n if none).
private int upperBound(int[] nums, int target) {
    int lo = 0;
    int hi = nums.length;
    while (lo < hi) {
        int mid = lo + (hi - lo) / 2;
        if (nums[mid] <= target) {
            lo = mid + 1;
        } else {
            hi = mid;
        }
    }
    return lo;
}
```

**Time:** O(log n): two independent binary searches.
**Space:** O(1).

This is optimal for the requirement. The only difference between the two helpers is `<` versus `<=` in the comparison, which moves the boundary from "start of the run" to "just after the run". The same lower-bound helper also solves "Search Insert Position" directly.

## 6. Dry Run

Example 1: `nums = [5,7,7,8,8,10]`, `target = 8`, `n = 6`. The tricky part is that both searches see the same duplicates but stop at different boundaries.

`lowerBound(8)` (move right while `nums[mid] < 8`):

| lo | hi | mid | nums[mid] | Test | Action |
|---|---|---|---|---|---|
| 0 | 6 | 3 | 8 | 8 < 8 false | hi = 3 |
| 0 | 3 | 1 | 7 | 7 < 8 true | lo = 2 |
| 2 | 3 | 2 | 7 | 7 < 8 true | lo = 3 |

Exit: `lo == hi == 3`, so `first = 3`, and `nums[3] == 8`.

`upperBound(8)` (move right while `nums[mid] <= 8`):

| lo | hi | mid | nums[mid] | Test | Action |
|---|---|---|---|---|---|
| 0 | 6 | 3 | 8 | 8 <= 8 true | lo = 4 |
| 4 | 6 | 5 | 10 | 10 <= 8 false | hi = 5 |
| 4 | 5 | 4 | 8 | 8 <= 8 true | lo = 5 |

Exit: `lo == hi == 5`, so `last = 5 - 1 = 4`. Final answer: `[3, 4]`.

## 7. Complexity Summary

| Approach | Time | Space | Notes |
|---|---|---|---|
| Linear scan | O(n) | O(1) | Violates the O(log n) requirement |
| Binary search for one occurrence, then expand | O(n) worst case | O(1) | Degrades when many duplicates equal the target |
| Lower bound plus upper bound | O(log n) | O(1) | Two boundary searches; standard answer |

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|---|---|---|
| Empty array | No elements to index | `lowerBound` returns `0 == nums.length`, so the guard returns `[-1, -1]` before `nums[first]` |
| Target absent but within the value range (`6` in the example) | `lowerBound` lands on an element that is not the target | `nums[first] != target` returns `[-1, -1]` |
| Target smaller than every element | `lowerBound` returns `0`, but `nums[0] != target` | Same guard |
| Target larger than every element | `lowerBound` returns `n`, an invalid index | `first == nums.length` is checked first, so `nums[first]` is never evaluated |
| Single element equal to the target | First and last coincide | `lowerBound = 0`, `upperBound = 1`, result `[0, 0]` |
| All elements equal to the target | Run covers the whole array | `lowerBound = 0`, `upperBound = n`, result `[0, n - 1]` |
| Target appears once in the middle | First and last are equal | The two bounds differ by exactly one |
| Extreme values (`±10^9`) | Overflow temptations | Only `<` / `<=` comparisons are used; no arithmetic on values |

## 9. Java Notes

- Use the half-open range `[lo, hi)` with `hi = nums.length`, not `nums.length - 1`. The answer `n` ("no element qualifies") must be representable.
- Evaluate `first == nums.length` before `nums[first] != target` in the guard; `||` short-circuits, so the array is never indexed at `n`.
- `mid = lo + (hi - lo) / 2` rounds down, so `mid < hi` whenever `lo < hi`, which guarantees the loops make progress. With `n <= 10^5` the plain `(lo + hi) / 2` would not overflow, but the safe form is a free habit.
- The two helpers differ only by `<` versus `<=`. Keeping them as two separate methods avoids the `target + 1` trick, which would overflow if `target` were `Integer.MAX_VALUE` (not possible under these constraints, but a good habit).
- Return `new int[] {first, last}`; LeetCode expects a two-element array.

## 10. Common Mistakes

- Running one binary search and then expanding left and right linearly, which is O(n) on arrays full of the target. Fix: use two boundary searches.
- Using `lo <= hi` with `hi = mid` (or `lo = mid`), which can loop forever. Fix: pair `lo < hi` with `hi = mid` and `lo = mid + 1`.
- Indexing `nums[first]` before checking `first == nums.length`, which throws `ArrayIndexOutOfBoundsException` when the target is larger than everything or the array is empty. Fix: check the bound first.
- Forgetting the `- 1` after `upperBound`, returning one past the last occurrence. Fix: `last = upperBound - 1`.
- Mixing `<` and `<=` between the two searches, finding the same boundary twice. Fix: lower bound uses `nums[mid] < target`; upper bound uses `nums[mid] <= target`.
- Setting `hi = nums.length - 1` while still using a half-open style update, which makes the answer `n` unreachable. Fix: use `hi = nums.length` consistently.

## 11. Interview Takeaway

- **Trigger sentence:** "First and last occurrence in a sorted array with duplicates means two boundary binary searches: the first index with `nums[i] >= target` and the first index with `nums[i] > target`."
- **90-second explanation:** Because the array is sorted, all copies of the target form one contiguous block, so I just need that block's two boundaries. I write a lower-bound search over the half-open range `[0, n)`: it returns the first index whose value is at least the target; if `nums[mid] < target` I move `lo` past `mid`, otherwise I set `hi = mid`. If that index is `n`, or the value there isn't the target, the target is absent and I return `[-1, -1]`. Otherwise that index is the first occurrence. For the last occurrence, I run the same search with `<=`, which finds the first index strictly greater than the target, and subtract one. Each search halves the range with `mid` rounded down, so `mid < hi` and the loop always makes progress. That's O(log n) time and O(1) space. I avoid the "find one occurrence and walk outward" shortcut because it becomes O(n) when the whole array equals the target.
- **Related problems:** 704 (Binary Search), 35 (Search Insert Position), 278 (First Bad Version), 33 (Search in Rotated Sorted Array), 162 (Find Peak Element).

## 12. Recall Questions

**Q:** What predicate does each helper search for?
**A:** The lower bound finds the first index with `nums[i] >= target`; the upper bound finds the first index with `nums[i] > target`.

**Q:** Why is the last occurrence `upperBound - 1`?
**A:** The upper bound is the first index after the run of target values ends, so the last index in the run is one before it.

**Q:** Why is the search range `[0, n)` with `hi = n` instead of `n - 1`?
**A:** The correct answer can be `n` (no element satisfies the predicate), so `n` must be a representable result; the half-open range allows that.

**Q:** Why does finding one occurrence and then expanding fail the requirement?
**A:** If the whole array equals the target, expanding takes O(n) steps, so the worst case is no longer O(log n).

**Q:** Why can't the loop run forever?
**A:** While `lo < hi`, `mid` rounds down to a value below `hi`; the update is either `lo = mid + 1` or `hi = mid`, both of which strictly shrink the range.

## 13. Final Code

```java
class Solution {
    public int[] searchRange(int[] nums, int target) {
        int first = lowerBound(nums, target);
        if (first == nums.length || nums[first] != target) {
            return new int[] {-1, -1};
        }
        int last = upperBound(nums, target) - 1;
        return new int[] {first, last};
    }

    private int lowerBound(int[] nums, int target) {
        int lo = 0;
        int hi = nums.length;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] < target) {
                lo = mid + 1;
            } else {
                hi = mid;
            }
        }
        return lo;
    }

    private int upperBound(int[] nums, int target) {
        int lo = 0;
        int hi = nums.length;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] <= target) {
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

Given an integer array sorted in non-decreasing order (possibly empty, with duplicates) and a target, return the first and last index of the target, or `[-1, -1]` if absent, in O(log n). Re-derive: lower bound = first index with `nums[i] >= target` via binary search on `[0, n)` (`lo = mid + 1` when `nums[mid] < target`, else `hi = mid`); if it is `n` or the value there is not the target, return `[-1, -1]`; otherwise the last index is the upper bound (same search with `<=`) minus one.
