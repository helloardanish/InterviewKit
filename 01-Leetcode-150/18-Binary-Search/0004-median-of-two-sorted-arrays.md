---
title: "0004 — Median of Two Sorted Arrays"
layout: default
parent: Binary Search
grand_parent: Leetcode
nav_order: 4
permalink: /18-Binary-Search/0004-median-of-two-sorted-arrays/

problem: 4 - Median of Two Sorted Arrays
url: https://leetcode.com/problems/median-of-two-sorted-arrays/
difficulty: Hard
section: Binary Search
patterns: [binary-search-on-partition, partition-by-count]
data_structures: [array]
time: O(log(min(m,n)))
space: O(1)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0004 — Median of Two Sorted Arrays

| Field | Value |
|---|---|
| **Pattern** | Binary Search on Partition |
| **Difficulty** | Hard |
| **Section** | Binary Search |
| **Time** | O(log(min(m,n))) |
| **Space** | O(1) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Microsoft, Apple |
| **LeetCode** | [Median of Two Sorted Arrays](https://leetcode.com/problems/median-of-two-sorted-arrays/) |

## 1. Problem in Simple Words

**Given:** Two sorted arrays `nums1` (length `m`) and `nums2` (length `n`).

**Required:** Return the median of all `m + n` values combined, as a `double`. If the combined length is odd, the median is the middle value; if even, it is the average of the two middle values. The overall running time must be O(log(m + n)).

**Constraints that actually matter:**
- **"The overall run time complexity should be O(log (m+n))"** — an explicit requirement. Merging the two arrays is O(m + n) and does not meet it. A logarithmic bound means you must discard a constant fraction of the candidates at each step, which points to binary search on something other than a value.
- `0 <= m, n <= 1000` and `1 <= m + n <= 2000` — either array can be **empty**, but not both. A solution that assumes both arrays have elements will break on inputs like `[]` and `[3]`.
- `-10^6 <= nums1[i], nums2[i] <= 10^6` — values are well inside the `int` range. Sums of two values cannot overflow, and `Integer.MIN_VALUE` and `Integer.MAX_VALUE` can safely stand in for negative and positive infinity, because no element can equal them.
- Both arrays are **sorted**, and duplicates are allowed, both within an array and across the two. The median must be correct when equal values appear on both sides of a split.
- The median can be a half-integer such as `2.5`, so the return type is `double` and the final averaging must use floating-point division.

## 2. Recognition Signals

- "Two sorted arrays" plus "O(log)" → you cannot merge. Instead of searching for a value, search for the right **split** of the combined sorted order.
- "Median" means "split the sorted data into a lower half and an upper half", so ask where the split falls in each array. The lower half takes some `i` elements from `nums1` and the remaining `j` from `nums2`, with `i + j` fixed. Only one number, `i`, is free, and that is what you binary search.
- When the shape of the answer is "a boundary index in a sorted structure that satisfies a local condition", you can usually binary search on the boundary and check the condition using the elements next to it.

**Pattern:** Binary search on the partition. Cut both arrays so the left parts together hold half of the elements, and search for the cut of the shorter array where every left element is at most every right element.

## 3. Core Idea

Let `half = (m + n + 1) / 2`. Imagine the merged sorted list and split it into a **left part** with `half` elements and a **right part** with the rest. If the total is odd, the median is the largest element of the left part. If even, it is the average of the largest of the left part and the smallest of the right part. The `+ 1` makes the left part hold the extra element when the total is odd.

The left part is made of `i` elements from the front of `nums1` and `j = half - i` elements from the front of `nums2`. Name the four elements next to the two cuts:

- `maxLeft1 = nums1[i - 1]` (or `-∞` if `i == 0`) and `minRight1 = nums1[i]` (or `+∞` if `i == m`)
- `maxLeft2 = nums2[j - 1]` (or `-∞` if `j == 0`) and `minRight2 = nums2[j]` (or `+∞` if `j == n`)

The cut is a correct median split exactly when every left element is at most every right element. Since each array is already sorted, only the cross comparisons need checking: `maxLeft1 <= minRight2` and `maxLeft2 <= minRight1`.

- **Search space:** the number of elements taken from `nums1`, `i` in `[0, m]`. To make sure `j` stays within `[0, n]` for every `i`, always binary search the **shorter** array (swap the arrays if needed). With `m <= n`, `j = half - i >= (n - m + 1) / 2 >= 0` and `j <= half <= n`.
- **Decision at each probe:**
  - If `maxLeft1 > minRight2`, `i` is too large (nums1 gave too many elements to the left), so `hi = i - 1`.
  - Else if `maxLeft2 > minRight1`, `i` is too small, so `lo = i + 1`.
  - Otherwise both conditions hold and the cut is valid.
- **Why this is monotone:** as `i` grows, `maxLeft1` can only grow and `minRight2` (taken at the shrinking index `j`) can only shrink, so "too large" is false for small `i` and true for large `i`. Symmetrically "too small" is true for small `i` and false for large `i`. A valid `i` is where both are false.

**Invariant:** If a valid cut exists, its `i` lies in `[lo, hi]`. A valid cut always exists for sorted inputs, so the loop returns from inside.

**Why it cannot loop forever:** `i = lo + (hi - lo) / 2` lies in `[lo, hi]`, and each update moves a bound strictly past `i` (`hi = i - 1` or `lo = i + 1`), so `hi - lo` shrinks every iteration.

## 4. Approach 1 — Brute Force

**Logic:** Merge the two sorted arrays into one sorted array with the standard two-pointer merge, then read the median from the middle.

```java
public double findMedianSortedArrays(int[] nums1, int[] nums2) {
    int m = nums1.length;
    int n = nums2.length;
    int[] merged = new int[m + n];

    int i = 0;
    int j = 0;
    int k = 0;
    while (i < m && j < n) {
        merged[k++] = nums1[i] <= nums2[j] ? nums1[i++] : nums2[j++];
    }
    while (i < m) {
        merged[k++] = nums1[i++];
    }
    while (j < n) {
        merged[k++] = nums2[j++];
    }

    int total = m + n;
    if (total % 2 == 1) {
        return merged[total / 2];
    }
    return (merged[total / 2 - 1] + merged[total / 2]) / 2.0;
}
```

- **Time:** O(m + n).
- **Space:** O(m + n) for the merged array. A variant that keeps only the last two merged values, stopping once the middle is reached, uses O(1) space but is still O(m + n) time.

**Why it is not optimal:** It is correct and simple, but linear time violates the explicit O(log(m + n)) requirement. It also ignores the real insight: you never need the full merged order, only the value or values at the middle.

## 5. Approach 2 — Optimized

**Algorithm:**
1. Make `nums1` the shorter array (swap the arguments if it is longer). Let `m = nums1.length`, `n = nums2.length`, and `half = (m + n + 1) / 2`.
2. Binary search `i` in `[0, m]` with `lo = 0`, `hi = m`. For each probe, set `j = half - i` and compute the four boundary elements, using `Integer.MIN_VALUE` or `Integer.MAX_VALUE` when a cut is at the edge of an array.
3. If `maxLeft1 <= minRight2` and `maxLeft2 <= minRight1`, the partition is valid:
   - total length odd: return `max(maxLeft1, maxLeft2)`;
   - total length even: return `(max(maxLeft1, maxLeft2) + min(minRight1, minRight2)) / 2.0`.
4. Otherwise, if `maxLeft1 > minRight2`, set `hi = i - 1`; else set `lo = i + 1`.

```java
public double findMedianSortedArrays(int[] nums1, int[] nums2) {
    if (nums1.length > nums2.length) {
        return findMedianSortedArrays(nums2, nums1);
    }

    int m = nums1.length;
    int n = nums2.length;
    int half = (m + n + 1) / 2;

    int lo = 0;
    int hi = m;

    while (lo <= hi) {
        int i = lo + (hi - lo) / 2;
        int j = half - i;

        int maxLeft1 = (i == 0) ? Integer.MIN_VALUE : nums1[i - 1];
        int minRight1 = (i == m) ? Integer.MAX_VALUE : nums1[i];
        int maxLeft2 = (j == 0) ? Integer.MIN_VALUE : nums2[j - 1];
        int minRight2 = (j == n) ? Integer.MAX_VALUE : nums2[j];

        if (maxLeft1 <= minRight2 && maxLeft2 <= minRight1) {
            if ((m + n) % 2 == 1) {
                return Math.max(maxLeft1, maxLeft2);
            }
            return (Math.max(maxLeft1, maxLeft2) + Math.min(minRight1, minRight2)) / 2.0;
        }

        if (maxLeft1 > minRight2) {
            hi = i - 1;
        } else {
            lo = i + 1;
        }
    }

    throw new IllegalArgumentException("Input arrays are not sorted");
}
```

- **Time:** O(log(min(m, n))), which is within the required O(log(m + n)). Each iteration halves the range of candidate cuts in the shorter array, and does O(1) work.
- **Space:** O(1). The one recursive call for swapping the arguments happens at most once and adds a single frame.

**Why this is optimal:** The requirement is logarithmic time, and this meets it while using constant memory. Binary searching only the shorter array is both the faster choice and what keeps `j` inside `[0, n]`. In the even case the sum uses two real values: both the left part and the right part are non-empty when the total is even, so no infinity sentinel ever enters the arithmetic.

## 6. Dry Run

Example: `nums1 = [1, 2]`, `nums2 = [3, 4]` (Example 2, expected `2.5`). Chosen because the first cut is wrong, forcing the search to move, and the final valid cut uses both infinity sentinels. Here `m = n = 2`, so no swap is needed, and `half = (2 + 2 + 1) / 2 = 2`.

Initial: `lo = 0`, `hi = 2`. In the table, `-inf` and `+inf` are the sentinels.

| Iteration | lo | hi | i | j | maxLeft1 | minRight1 | maxLeft2 | minRight2 | maxLeft1 <= minRight2 | maxLeft2 <= minRight1 | Action |
|-----------|----|----|---|---|----------|-----------|----------|-----------|-----------------------|-----------------------|--------|
| 1 | 0 | 2 | 1 | 1 | 1 | 2 | 3 | 4 | yes (1 <= 4) | no (3 > 2) | i too small, lo = i + 1 = 2 |
| 2 | 2 | 2 | 2 | 0 | 2 | +inf | -inf | 3 | yes (2 <= 3) | yes (-inf <= +inf) | valid partition |

Exit condition: iteration 2 finds a valid partition. The total length 4 is even, so the answer is `(max(2, -inf) + min(+inf, 3)) / 2.0 = (2 + 3) / 2.0`.

**Final answer:** `2.5`, matching the expected output. The left part is `[1, 2]` (both from `nums1`, none from `nums2`), and the right part is `[3, 4]`.

## 7. Complexity Summary

| Approach | Time | Space (auxiliary) | Notes |
|----------|------|-------------------|-------|
| 1. Merge, then read the middle | O(m + n) | O(m + n) | O(1) space if only the last two merged values are kept, but still linear time |
| 2. Binary search on the partition | O(log(min(m, n))) | O(1) | Meets the O(log(m + n)) requirement |

The inputs are read-only. The output is one `double`.

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|------|------------------|--------------------------|
| One array empty (for example `[]` and `[1,2,3]`) | There are no elements to cut in one array, and index checks must not fail | The empty array becomes `nums1` (via the swap), so `lo = hi = 0`, `i = 0`, `j = half`, `maxLeft1 = -inf`, `minRight1 = +inf`, and the median comes from `nums2` alone |
| Single element overall (`m + n = 1`) | The smallest legal total | For `[]` and `[5]`: `half = 1`, `i = 0`, `j = 1`, `maxLeft2 = 5`; the total is odd, so it returns `5.0` |
| Odd total versus even total | The two cases need different formulas | `(m + n) % 2` selects between `max(maxLeft1, maxLeft2)` and the average of the two middle values; the `+ 1` in `half` puts the extra element in the left part for odd totals |
| Arrays entirely disjoint (all of `nums1` smaller, or all larger, than `nums2`) | The cut in `nums1` is at an extreme edge, so one side has no real neighbor | The sentinels `-inf` and `+inf` make the comparisons pass naturally at `i = 0` or `i = m` |
| Duplicates across the two arrays, or all-same values | Equal values at the cut must not make a valid cut look invalid | The checks use `<=`, not `<`, so equal values on either side of the cut are accepted |
| Negative numbers | Sign might seem relevant | Only comparisons and one sum are used, and values are bounded by `10^6` in magnitude, so sign does not matter |
| Values at the allowed extremes (`-10^6`, `10^6`) | Might collide with the infinity sentinels or overflow | They are far from `Integer.MIN_VALUE` and `Integer.MAX_VALUE`, so the sentinels never equal a real value, and the sum of two values is at most `2 * 10^6` |
| Equal lengths (`m == n`) | No swap, and the search range is the whole `nums1` | Handled by the same loop; `j` stays within `[0, n]` |
| Maximum sizes (`1000 + 1000`) | Checks that the search is logarithmic | About 10 iterations, because only the shorter array of 1000 is searched |

## 9. Java Notes

- **Return type and division:** the result must be a `double`. Writing `(a + b) / 2` with integers truncates `2.5` to `2`. Use `/ 2.0` (or cast one operand to `double`).
- **`Integer.MIN_VALUE` and `Integer.MAX_VALUE` as infinities:** safe here because the constraints keep real values within `+-10^6`. In a problem where elements can equal those extremes (for example a peak-finding variant with full-range `int` values), this stand-in would be a bug.
- **`half = (m + n + 1) / 2`:** integer division with the `+ 1` handles odd and even totals with one formula. The left part is always at least as large as the right part.
- **Swapping the arrays:** the recursive call with the arguments swapped is a compact way to make `nums1` the shorter array. It recurses at most once.
- **Overflow:** `Math.max(...) + Math.min(...)` adds two real values at most `10^6` in magnitude, so there is no overflow risk.
- **The final `throw`:** the loop always returns for valid sorted inputs. Java still needs a return or throw on every path, and throwing makes a violated precondition visible instead of silently returning a wrong number.

## 10. Common Mistakes

- **Binary searching the longer array, or not swapping.** Then `j = half - i` can become negative or exceed `n`, which throws an index error or reads the wrong elements. Fix: always search the shorter array, so `j` stays in `[0, n]`.
- **Using `half = (m + n) / 2` for odd totals.** The left part is then one element too small, so `max(maxLeft1, maxLeft2)` returns the element just below the median, silently. Fix: use `half = (m + n + 1) / 2`.
- **Averaging with integer division.** For an even total, `(a + b) / 2` drops the `.5`, so `2.5` becomes `2`. Fix: divide by `2.0`.
- **Forgetting the sentinels at the array edges.** When `i == 0`, `i == m`, `j == 0`, or `j == n`, reading `nums1[i - 1]`, `nums1[i]`, `nums2[j - 1]`, or `nums2[j]` is out of bounds. Fix: use `-infinity` for a missing left element and `+infinity` for a missing right element.

## 11. Interview Takeaway

- **Trigger sentence to memorize:** "Median of two sorted arrays in logarithmic time → binary search the cut position in the shorter array, derive the cut in the other array from the requirement that the left part holds half the elements, and accept a cut when every left element is at most every right element."
- **How to explain it aloud in 90 seconds:** "I don't merge the arrays. The median splits the combined sorted order into a left half and a right half, with the left half having `(m + n + 1) / 2` elements. If I take `i` elements from the first array into the left half, then I must take `j = half - i` from the second one, so only `i` is free. A choice of `i` is correct when every element in the left half is at most every element in the right half. Because each array is already sorted, I only need two checks: the last left element of the first array is at most the first right element of the second, and the last left element of the second is at most the first right element of the first. If the first check fails, `i` is too big, so I move left; if the second fails, `i` is too small, so I move right. I binary search `i` over the shorter array, which also keeps `j` in range, and I use negative and positive infinity when a cut is at the edge of an array. Once a valid cut is found, for an odd total the median is the larger of the two last-left elements, and for an even total it's the average of that and the smaller of the two first-right elements, using floating-point division. That's O(log of the shorter length) time and O(1) space."
- **Related problems (some share the binary search idea, others the median or merge idea):**
  - LeetCode 295 — Find Median from Data Stream
  - LeetCode 88 — Merge Sorted Array
  - LeetCode 215 — Kth Largest Element in an Array
  - LeetCode 35 — Search Insert Position

## 12. Recall Questions

**Q:** What does a "valid partition" mean here, and which two comparisons check it?
**A:** The left part (the `i` smallest from `nums1` plus the `j` smallest from `nums2`) must have every element at most every element of the right part. Since each array is sorted, it is enough to check `maxLeft1 <= minRight2` and `maxLeft2 <= minRight1`.

**Q:** Why do we binary search only the shorter array?
**A:** It keeps `j = half - i` within `[0, n]` for every `i` in `[0, m]`, and it makes the search O(log(min(m, n))).

**Q:** Why is `half = (m + n + 1) / 2` used instead of `(m + n) / 2`?
**A:** The `+ 1` makes the left part hold the extra element when the total is odd, so the median is simply the largest element of the left part; for even totals it makes no difference.

**Q:** How do you decide whether to move `lo` or `hi` after an invalid cut?
**A:** If `maxLeft1 > minRight2`, `nums1` contributed too many elements to the left, so `hi = i - 1`. Otherwise `maxLeft2 > minRight1`, meaning it contributed too few, so `lo = i + 1`.

**Q:** Why are infinity sentinels needed, and why are `Integer.MIN_VALUE` and `Integer.MAX_VALUE` safe here?
**A:** A cut at the very start or end of an array has no neighbor on one side, and an infinity makes the comparison automatically true. They are safe because the values are within `+-10^6`, so no real element equals the extremes.

**Q:** What does the algorithm return for an even total, and what Java detail can break it?
**A:** The average of the largest left element and the smallest right element. If you divide an `int` sum by `2` instead of `2.0`, the fractional part is lost.

## 13. Final Code

```java
public double findMedianSortedArrays(int[] nums1, int[] nums2) {
    if (nums1.length > nums2.length) {
        return findMedianSortedArrays(nums2, nums1);
    }

    int m = nums1.length;
    int n = nums2.length;
    int half = (m + n + 1) / 2;

    int lo = 0;
    int hi = m;

    while (lo <= hi) {
        int i = lo + (hi - lo) / 2;
        int j = half - i;

        int maxLeft1 = (i == 0) ? Integer.MIN_VALUE : nums1[i - 1];
        int minRight1 = (i == m) ? Integer.MAX_VALUE : nums1[i];
        int maxLeft2 = (j == 0) ? Integer.MIN_VALUE : nums2[j - 1];
        int minRight2 = (j == n) ? Integer.MAX_VALUE : nums2[j];

        if (maxLeft1 <= minRight2 && maxLeft2 <= minRight1) {
            if ((m + n) % 2 == 1) {
                return Math.max(maxLeft1, maxLeft2);
            }
            return (Math.max(maxLeft1, maxLeft2) + Math.min(minRight1, minRight2)) / 2.0;
        }

        if (maxLeft1 > minRight2) {
            hi = i - 1;
        } else {
            lo = i + 1;
        }
    }

    throw new IllegalArgumentException("Input arrays are not sorted");
}
```

## 14. Self-Test

You have two sorted arrays, either of which may be empty (but not both). Return the median of all their elements combined, in O(log(m + n)) time. Think about how the median splits the merged order into two halves, which single number you can binary search to place that split across both arrays, and what condition tells you a split is correct.
