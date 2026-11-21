---
title: "0033 — Search in Rotated Sorted Array"
layout: default
parent: Binary Search
grand_parent: Leetcode
nav_order: 33
permalink: /18-Binary-Search/0033-search-in-rotated-sorted-array/

problem: 33 - Search in Rotated Sorted Array
url: https://leetcode.com/problems/search-in-rotated-sorted-array/
difficulty: Medium
section: Binary Search
patterns: [modified-binary-search, sorted-half-detection]
data_structures: [array]
time: O(log n)
space: O(1)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0033 — Search in Rotated Sorted Array

| Field | Value |
|---|---|
| **Pattern** | Modified Binary Search · Sorted Half Detection |
| **Difficulty** | Medium |
| **Section** | Binary Search |
| **Time** | O(log n) |
| **Space** | O(1) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Microsoft, Meta |
| **LeetCode** | [Search in Rotated Sorted Array](https://leetcode.com/problems/search-in-rotated-sorted-array/) |

## 1. Problem in Simple Words

**Given:** an integer array `nums` that was sorted ascending with **distinct** values and then rotated at an unknown pivot index `k` (`0 <= k < nums.length`), so it looks like `[nums[k], ..., nums[n-1], nums[0], ..., nums[k-1]]`, plus an integer `target`.

**Required:** return the index of `target`, or `-1` if it is not present. The algorithm must run in `O(log n)`.

**Constraints that matter:**
- `1 <= nums.length <= 5000` — a linear scan would pass the tests, but the statement requires O(log n), which is the real requirement. A single-element array is allowed.
- `-10^4 <= nums[i], target <= 10^4` — small values; only comparisons are needed, so overflow is a non-issue.
- **All values are unique** — this is what lets a single comparison (`nums[lo] <= nums[mid]`) reliably reveal which half is sorted. With duplicates this inference breaks (that is the separate "Search in Rotated Sorted Array II" problem).
- The pivot satisfies `0 <= k < n`, so `k = 0` means the array is not rotated at all. (Some copies of the constraints say "guaranteed to be rotated" and others say "possibly rotated"; the algorithm below handles both.)

## 2. Recognition Signals

- "Sorted array rotated at an unknown pivot" plus "O(log n)": the array is almost sorted, so binary search should still work with a twist.
- A rotated sorted array has at most one place where the order breaks (the descent from the largest to the smallest element), so any range of it is either fully sorted or contains that single break.
- Distinct values mean comparing two endpoints of a range tells you whether the break is inside it.
- Pattern: **modified binary search: at each step one half is sorted; test whether the target lies in that sorted half, otherwise search the other half.**

## 3. Core Idea

Use binary search on `[lo, hi]`. At `mid`, if `nums[mid] == target`, done. Otherwise decide which half is sorted:
- If `nums[lo] <= nums[mid]`, the left half `[lo, mid]` is sorted. If `nums[lo] <= target < nums[mid]`, the target must be in the left half, so set `hi = mid - 1`; otherwise it can only be in the right half, so set `lo = mid + 1`.
- Otherwise the right half `[mid, hi]` is sorted. If `nums[mid] < target <= nums[hi]`, the target must be in the right half, so set `lo = mid + 1`; otherwise set `hi = mid - 1`.

**Why it's correct:** the single descent point lies in at most one of the two halves. If it is not inside `[lo, mid]`, then the left half is a plain sorted run and `nums[lo] <= nums[mid]`. If it is inside `[lo, mid]`, then every element before the descent exceeds every element after it, so `nums[lo] > nums[mid]`, and the right half `[mid, hi]` contains no descent and is sorted. For a sorted half, a simple range check on its endpoints tells exactly whether the target could be in it; if not, it can only be in the other half (or be absent), so that half can be safely discarded.

**Invariant:** if `target` is in the array, its index lies in `[lo, hi]`.

**Termination:** each iteration sets `lo = mid + 1` or `hi = mid - 1`, and `lo <= mid <= hi`, so the range strictly shrinks until `lo > hi`, at which point the target is absent.

## 4. Approach 1 — Brute Force

Scan every element and return the index of the target.

```java
public int searchLinear(int[] nums, int target) {
    for (int i = 0; i < nums.length; i++) {
        if (nums[i] == target) {
            return i;
        }
    }
    return -1;
}
```

**Time:** O(n).
**Space:** O(1).

Not acceptable as the final answer: it ignores the sortedness completely and violates the required O(log n). It is a handy correctness reference when testing.

## 5. Approach 2 — Optimized (Single-Pass Binary Search)

1. `lo = 0`, `hi = n - 1`.
2. While `lo <= hi`: compute `mid`; return it if `nums[mid] == target`.
3. If `nums[lo] <= nums[mid]` (left half sorted): if `nums[lo] <= target && target < nums[mid]` then `hi = mid - 1`, else `lo = mid + 1`.
4. Else (right half sorted): if `nums[mid] < target && target <= nums[hi]` then `lo = mid + 1`, else `hi = mid - 1`.
5. Return `-1` if the loop ends.

```java
public int search(int[] nums, int target) {
    int lo = 0;
    int hi = nums.length - 1;

    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        if (nums[mid] == target) {
            return mid;
        }

        if (nums[lo] <= nums[mid]) {
            if (nums[lo] <= target && target < nums[mid]) {
                hi = mid - 1;
            } else {
                lo = mid + 1;
            }
        } else {
            if (nums[mid] < target && target <= nums[hi]) {
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }
    }
    return -1;
}
```

**Time:** O(log n); the range halves each iteration.
**Space:** O(1).

This is optimal for the requirement. An alternative is two passes: first binary search for the pivot (the index of the minimum), then run an ordinary binary search on whichever sorted segment can contain the target. It has the same complexity and is preferable when you want each step to be a textbook binary search, at the cost of writing two loops and handling the pivot off-by-ones.

## 6. Dry Run

Example 1: `nums = [4,5,6,7,0,1,2]`, `target = 0`. The tricky part is that the search must step over the pivot.

| Iteration | lo | hi | mid | nums[mid] | Sorted half | Target in sorted half? | Action |
|---|---|---|---|---|---|---|---|
| 1 | 0 | 6 | 3 | 7 | left `[4..7]` (4 <= 7) | no (0 < 4) | lo = 4 |
| 2 | 4 | 6 | 5 | 1 | left `[0,1]` (0 <= 1) | yes (0 <= 0 < 1) | hi = 4 |
| 3 | 4 | 4 | 4 | 0 | n/a | n/a | equals target, return 4 |

Exit condition: `nums[mid] == target` at index 4. Final answer: `4`.

For Example 2 (same array, `target = 3`): `mid=3` (7) left sorted, 3 not in `[4,7)` so `lo=4`; `mid=5` (1) left `[0,1]`, 3 not in `[0,1)` so `lo=6`; `mid=6` (2) left sorted, 3 not in `[2,2)` so `lo=7`; loop ends and the answer is `-1`.

## 7. Complexity Summary

| Approach | Time | Space | Notes |
|---|---|---|---|
| Linear scan | O(n) | O(1) | Violates the O(log n) requirement |
| Find pivot, then binary search | O(log n) | O(1) | Two loops; same complexity |
| Single-pass modified binary search | O(log n) | O(1) | One loop; sorted-half test each step |

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|---|---|---|
| Single element, hit or miss | Range of size one | `mid = lo`; returns `0` on a hit, otherwise the range empties and `-1` is returned |
| Two elements `[3,1]`, `target = 1` | `lo == mid`, so "left half" is a single element | `nums[lo] <= nums[mid]` uses `<=`, so the left half is treated as sorted; `1` is not in `[3,3)`, so `lo = 1`, then found |
| Array not actually rotated (`k = 0`) | Both halves sorted | Works like ordinary binary search: the "left sorted" branch is always taken correctly |
| Target smaller than every element or larger than every element | Never in any sorted half | Each step shifts toward the other half until the range empties; returns `-1` |
| Target at the pivot boundary (the minimum or the maximum element) | Sits next to the break, where mistakes in bound checks show up | Range checks use `<=` on the sorted half's endpoint (`nums[lo] <= target`, `target <= nums[hi]`), so boundary elements are included |
| Target at index `mid` | Must be caught before any half logic | Equality check comes first |
| Duplicates | `nums[lo] == nums[mid]` no longer proves the left half is sorted | Excluded by the "all values unique" constraint; handling them needs a different problem's approach |

## 9. Java Notes

- Use `nums[lo] <= nums[mid]` with `<=`, not `<`. When `lo == mid` (a range of two elements), the left half is the single element `nums[lo]`, and `<` would misclassify it as the unsorted half.
- `mid = lo + (hi - lo) / 2` is the safe midpoint form; with `n <= 5000` it is not strictly needed, but it is the habit to keep.
- Check `nums[mid] == target` first, so the later range tests can use a strict `<` on the `nums[mid]` side without losing the mid element.
- With this loop shape (`lo <= hi`, `lo = mid + 1`, `hi = mid - 1`), every iteration shrinks the range; there is no `hi = mid` / `lo = mid` pairing that could loop forever.
- Only comparisons on `int` values are used, so there is no overflow risk even at the extremes of the value range.

## 10. Common Mistakes

- Writing `nums[lo] < nums[mid]` (strict) to test the left half. It fails on two-element ranges like `[3,1]` with target `1`, where it wrongly treats the right half as sorted. Fix: use `<=`.
- Using the wrong inclusivity in the target range test, for example `target <= nums[mid]` after the equality check already ran, or omitting `nums[lo] <= target`, which wrongly sends boundary targets to the wrong half. Fix: left half is `nums[lo] <= target < nums[mid]`, right half is `nums[mid] < target <= nums[hi]`.
- Forgetting the equality check at `mid` and relying on the half logic to find the target. Fix: check `nums[mid] == target` first.
- Mixing `hi = mid` with `lo <= hi`, which can loop forever. Fix: use `mid - 1` and `mid + 1` consistently with `lo <= hi`.
- Assuming the array is always rotated and special-casing it. The pivot can be `0`, and the algorithm needs no special case.
- Applying this logic to arrays with duplicates. With equal values, `nums[lo] == nums[mid]` does not tell you whether the left half is sorted. Fix: that variant requires shrinking the range one step when ends are equal.

## 11. Interview Takeaway

- **Trigger sentence:** "A rotated sorted array with distinct values means at every step one half is sorted, so check whether the target lies in the sorted half and discard the other."
- **90-second explanation:** A rotated sorted array has exactly one break where the largest value is followed by the smallest, so after picking `mid`, at least one of the two halves has no break and is plainly sorted. I tell which one by comparing `nums[lo]` to `nums[mid]`: if `nums[lo] <= nums[mid]`, the left half is sorted (I use `<=` so a one-element left half counts). Then I test whether the target lies in that sorted half's value range. If it does, I search that half; if not, I search the other half. If I land on `mid` and it equals the target, I return. Each step discards half the range, so it is O(log n) time and O(1) space. It relies on distinct values: with duplicates, equal endpoints say nothing about which half is sorted. An alternative is to binary search for the pivot first and then do an ordinary binary search on the right segment, which is the same complexity but two loops.
- **Related problems:** 153 (Find Minimum in Rotated Sorted Array), 81 (Search in Rotated Sorted Array II), 162 (Find Peak Element), 704 (Binary Search), 34 (Find First and Last Position of Element in Sorted Array).

## 12. Recall Questions

**Q:** Why is at least one half always sorted?
**A:** A rotated sorted array has at most one descent. That descent lies in at most one of the two halves, so the other half has no descent and is sorted.

**Q:** How do you tell which half is sorted?
**A:** Compare `nums[lo]` with `nums[mid]`. If `nums[lo] <= nums[mid]`, there is no descent in `[lo, mid]`, so the left half is sorted; otherwise the right half is.

**Q:** Why is the comparison `nums[lo] <= nums[mid]` and not `<`?
**A:** When `lo == mid` the left half is a single element, which is sorted; a strict `<` would misclassify it and send two-element ranges like `[3,1]` the wrong way.

**Q:** Why does the algorithm require unique values?
**A:** With duplicates, `nums[lo] == nums[mid]` could mean either a sorted left half or a wraparound through equal values, so the sorted-half test is no longer reliable.

**Q:** What is the invariant that guarantees correctness?
**A:** If the target is present, its index lies in `[lo, hi]`; each step only discards a half that provably cannot contain it.

## 13. Final Code

```java
class Solution {
    public int search(int[] nums, int target) {
        int lo = 0;
        int hi = nums.length - 1;

        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] == target) {
                return mid;
            }

            if (nums[lo] <= nums[mid]) {
                if (nums[lo] <= target && target < nums[mid]) {
                    hi = mid - 1;
                } else {
                    lo = mid + 1;
                }
            } else {
                if (nums[mid] < target && target <= nums[hi]) {
                    lo = mid + 1;
                } else {
                    hi = mid - 1;
                }
            }
        }
        return -1;
    }
}
```

## 14. Self-Test

Given an ascending array of distinct integers that has been rotated at an unknown pivot, and a target, return the target's index or `-1` in O(log n). Re-derive: binary search; return `mid` if it matches; if `nums[lo] <= nums[mid]` the left half is sorted, so go left only when `nums[lo] <= target < nums[mid]`, else go right; otherwise the right half is sorted, so go right only when `nums[mid] < target <= nums[hi]`, else go left.
