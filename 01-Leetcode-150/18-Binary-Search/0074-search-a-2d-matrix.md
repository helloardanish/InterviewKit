---
title: "0074 — Search a 2D Matrix"
layout: default
parent: Binary Search
grand_parent: Leetcode
nav_order: 74
permalink: /18-Binary-Search/0074-search-a-2d-matrix/

problem: 74 - Search a 2D Matrix
url: https://leetcode.com/problems/search-a-2d-matrix/
difficulty: Medium
section: Binary Search
patterns: [binary-search, index-flattening]
data_structures: [matrix, array]
time: O(log(m*n))
space: O(1)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0074 — Search a 2D Matrix

| Field | Value |
|---|---|
| **Pattern** | Binary Search · Index Flattening |
| **Difficulty** | Medium |
| **Section** | Binary Search |
| **Time** | O(log(m*n)) |
| **Space** | O(1) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Microsoft, Meta |
| **LeetCode** | [Search a 2D Matrix]([https://leetcode.com/problems/search-a-2d-matrix/](https://leetcode.com/problems/search-a-2d-matrix/)) |


## 1. Problem in Simple Words

**Given:** An `m x n` integer matrix with two properties: each row is sorted in non-decreasing order, and the first integer of each row is greater than the last integer of the previous row. Also given an integer `target`.

**Required:** Return `true` if `target` appears in the matrix, otherwise `false`, in O(log(m * n)) time.

**Constraints that actually matter:**
- **"You must write a solution in O(log(m * n)) time"** — this is an explicit requirement. Scanning all `m * n` cells would pass the tests (at most `10^4` cells) but breaks the requirement. The target complexity is a hint that you binary search over **all** cells at once, not row by row.
- **The second property is the key:** the first element of every row is greater than the last element of the row above. Combined with sorted rows, reading the matrix row by row produces a single non-decreasing sequence of `m * n` values. The matrix is a sorted 1D array wrapped into rows.
- `1 <= m, n <= 100` — the matrix is never empty and always rectangular. Single-row and single-column matrices are legal.
- `-10^4 <= matrix[i][j], target <= 10^4` — small signed values that are only compared. Rows are non-decreasing, so duplicates inside a row are possible; the search only needs to find any equal value.

## 2. Recognition Signals

- "Sorted" plus an O(log) target on a 2D structure → look for a way to turn it into a 1D binary search.
- "First element of each row is greater than the last element of the previous row" → this is the cue that the rows chain together into one globally sorted order. It is what separates this problem from the harder LeetCode 240, where only rows and columns are individually sorted.
- A rectangular grid with fixed row width `n` has an exact mapping between a flat index `k` and a cell: `row = k / n`, `col = k % n`. When you see a grid that is conceptually a flattened array, binary search over indices and convert on demand.

**Pattern:** Binary search over a virtual flattened array: search indices `0..m*n-1` and map each index to `(k / n, k % n)`.

## 3. Core Idea

Imagine the matrix laid out as one array `flat` of length `m * n`, where `flat[k] = matrix[k / n][k % n]`. By the two properties, `flat` is sorted in non-decreasing order. The array does not need to be built: the mapping lets you read `flat[k]` directly from the matrix in O(1).

Then it is an ordinary binary search for an exact value.

- **Search space:** the closed range of flat indices `[lo, hi]`, initially `[0, m * n - 1]`.
- **Invariant:** if `target` is in the matrix, its flat index lies in `[lo, hi]`.
- **Update rules:** compute `mid`, read the value there. If it equals `target`, return `true`. If it is smaller than `target`, every index up to `mid` is too small, so `lo = mid + 1`. If it is larger, every index from `mid` up is too large, so `hi = mid - 1`.
- **Why it cannot loop forever:** `mid` always lies in `[lo, hi]`, and each update moves one boundary strictly past `mid` (`lo = mid + 1` or `hi = mid - 1`). So `hi - lo` shrinks by at least one each iteration, and the loop ends when `lo > hi`, meaning the range is empty and the target is absent.

## 4. Approach 1 — Brute Force

**Logic:** Check every cell and return `true` on the first match.

```java
public boolean searchMatrix(int[][] matrix, int target) {
    for (int[] row : matrix) {
        for (int value : row) {
            if (value == target) {
                return true;
            }
        }
    }
    return false;
}
```

- **Time:** O(m * n) in the worst case, when the target is absent or in the last cell.
- **Space:** O(1).

**Why it is not optimal:** It uses none of the sortedness and misses the explicit O(log(m * n)) requirement. At `m = n = 100` it is up to 10,000 comparisons, versus about 14 for binary search.

## 5. Approach 2 — Optimized

**Algorithm:**
1. Let `m` be the number of rows and `n` the number of columns. Set `lo = 0` and `hi = m * n - 1`.
2. While `lo <= hi`:
   - `mid = lo + (hi - lo) / 2`.
   - Read `value = matrix[mid / n][mid % n]`.
   - If `value == target`, return `true`.
   - If `value < target`, set `lo = mid + 1`; otherwise set `hi = mid - 1`.
3. Return `false`.

```java
public boolean searchMatrix(int[][] matrix, int target) {
    int m = matrix.length;
    int n = matrix[0].length;

    int lo = 0;
    int hi = m * n - 1;

    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        int value = matrix[mid / n][mid % n];

        if (value == target) {
            return true;
        }
        if (value < target) {
            lo = mid + 1;
        } else {
            hi = mid - 1;
        }
    }
    return false;
}
```

- **Time:** O(log(m * n)). Each iteration halves the range of flat indices, so there are at most about `log2(m * n)` iterations (at most 14 for 10,000 cells). Each iteration does O(1) work (one division, one modulo, one array read).
- **Space:** O(1).

**Why this is optimal:** The requirement is O(log(m * n)), and a comparison search of a sorted sequence of `m * n` items cannot beat logarithmic time. Memory is constant, since the flattened array is never materialized.

**Equivalent alternative:** do two binary searches. First binary search the rows for the last row whose first element is `<= target`, then binary search within that row. This is O(log m + log n) = O(log(m * n)), the same complexity. The single flattened search is shorter and has fewer off-by-one traps. The two-step version is preferable if you find the row/column reasoning easier to explain, or if rows were stored separately.

## 6. Dry Run

Example: `matrix = [[1,3,5,7],[10,11,16,20],[23,30,34,60]]`, `target = 13` (Example 2). Chosen because the target is absent and lies between values in the middle row, so the search moves both boundaries and ends on an empty range. Here `m = 3`, `n = 4`, so flat indices run from 0 to 11.

Initial: `lo = 0`, `hi = 11`.

| Iteration | lo | hi | mid | (row, col) = (mid / 4, mid % 4) | value | Compare with 13 | Action | New lo | New hi |
|-----------|----|----|-----|----------------------------------|-------|-----------------|--------|--------|--------|
| 1 | 0 | 11 | 5 | (1, 1) | 11 | 11 < 13 | lo = mid + 1 | 6 | 11 |
| 2 | 6 | 11 | 8 | (2, 0) | 23 | 23 > 13 | hi = mid - 1 | 6 | 7 |
| 3 | 6 | 7 | 6 | (1, 2) | 16 | 16 > 13 | hi = mid - 1 | 6 | 5 |

Exit condition: `lo = 6 > hi = 5`, so the range is empty.

**Final answer:** `false`, matching the expected output. The values around where 13 would sit are 11 and 16, and neither equals 13.

## 7. Complexity Summary

| Approach | Time | Space (auxiliary) | Notes |
|----------|------|-------------------|-------|
| 1. Scan every cell | O(m * n) | O(1) | Correct but violates the O(log(m * n)) requirement |
| 2. Binary search on flattened indices | O(log(m * n)) | O(1) | Meets the requirement; no copy of the matrix |
| (Alternative) Binary search row, then column | O(log m + log n) | O(1) | Same complexity; two searches instead of one |

The input matrix is used read-only. The output is a single boolean.

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|------|------------------|--------------------------|
| Single cell (`1 x 1`) | Smallest legal input | `lo = hi = 0`, one probe reads `matrix[0][0]`; if it differs, the range empties and it returns `false` |
| Single row (`m = 1`) | The matrix is just one sorted row | `mid / n` is always `0`, so it reduces to a normal binary search along the row |
| Single column (`n = 1`) | Division by `n = 1` makes every column index `0` | `mid / 1 = mid` and `mid % 1 = 0`, so it searches down the column correctly |
| Target smaller than the first element or larger than the last | The target is outside the whole range | The search keeps moving one side until the range empties and returns `false` |
| Target at the first cell or the last cell | Boundary cells must be reachable | The range is `[0, m*n - 1]`, which includes both ends, so they are found |
| Duplicates inside a row (rows are non-decreasing) | Several cells may equal the target | The search returns `true` as soon as any probe equals the target, and the order is unaffected |
| All-same values (possible only when `m = 1`) | Every probe equals the same value | If it equals the target, the first probe returns `true`; otherwise comparisons are all `<` or all `>`, and the range empties |
| Negative values | Sign might seem to matter | Only `<`, `>`, and `==` are used, so sign is irrelevant |
| Extreme sizes and values | Might raise overflow concerns | `m * n <= 10^4` and values are `|v| <= 10^4`, far below `int` limits; `mid` uses `lo + (hi - lo) / 2` anyway |

## 9. Java Notes

- **Index mapping uses the column count `n`:** `row = mid / n` and `col = mid % n`. Dividing by `m` instead is a common bug that only shows up on non-square matrices.
- **`mid` is non-negative,** so integer division and modulo behave as plain floor division and remainder; there is no negative-number surprise.
- **`lo + (hi - lo) / 2`:** the overflow-safe midpoint. It is not strictly needed at these sizes (`m * n <= 10^4`) but is a good habit.
- **`matrix[0].length` is used as `n`:** the problem guarantees every row has `n` entries, so this is safe. The matrix is never empty, so `matrix[0]` always exists.
- **Closed interval `[lo, hi]` with `while (lo <= hi)`:** this fits an exact-match search because both bounds are real indices that can still hold the target. Use `mid + 1` and `mid - 1` so the range always shrinks.
- No recursion and no extra objects are allocated.

## 10. Common Mistakes

- **Swapping the mapping or using the wrong dimension:** writing `row = mid % n, col = mid / n`, or dividing by the number of rows `m`. On a non-square matrix this reads the wrong cells or throws an index error, so valid targets may be reported missing. Fix: always use `row = mid / n` and `col = mid % n`, where `n` is the number of columns.
- **Using `while (lo < hi)` with `hi = m * n - 1` and no extra check.** The loop exits with one candidate index never examined, so a target sitting exactly there is missed. Fix: use `while (lo <= hi)` with `hi = mid - 1`, or switch to the half-open form with `hi = m * n` consistently.
- **Setting `lo = mid` or `hi = mid`.** In the closed-interval form this can leave the range unchanged and loop forever. Fix: always step past `mid` with `mid + 1` and `mid - 1`.
- **Using the staircase search from LeetCode 240 (start at the top-right corner, move left or down).** It also gives the right answer but runs in O(m + n), which does not meet the O(log(m * n)) requirement, and it ignores the stronger property that rows chain together. Fix: exploit that the whole matrix is one sorted sequence and binary search it.

## 11. Interview Takeaway

- **Trigger sentence to memorize:** "Each row is sorted and each row starts after the previous one ends → the matrix is a sorted 1D array in disguise; binary search indices `0..m*n-1` and map each index to `(k / n, k % n)`."
- **How to explain it aloud in 90 seconds:** "The two properties mean that if I read the matrix row by row, I get one globally non-decreasing sequence of `m * n` values. So I don't need row-by-row logic. I binary search over the flat indices from 0 to `m * n - 1`. For a flat index `mid`, the cell is in row `mid / n` and column `mid % n`, where `n` is the number of columns. I read that value: if it equals the target I return true, if it's smaller I move `lo` to `mid + 1`, and if it's larger I move `hi` to `mid - 1`. The range shrinks every step, and when it becomes empty, the target is not there. That's O(log(m * n)) time and O(1) space, because I never build the flattened array. An alternative is to binary search for the right row first and then search inside that row, which has the same complexity. I would not use the top-right staircase walk from the harder variant, since it's O(m + n)."
- **Related problems using this pattern:**
  - LeetCode 240 — Search a 2D Matrix II
  - LeetCode 35 — Search Insert Position
  - LeetCode 33 — Search in Rotated Sorted Array
  - LeetCode 34 — Find First and Last Position of Element in Sorted Array

## 12. Recall Questions

**Q:** Why can the matrix be treated as a single sorted array?
**A:** Each row is sorted, and the first element of each row is greater than the last element of the previous row, so reading the rows one after another yields a non-decreasing sequence of all `m * n` values.

**Q:** How do you convert a flat index into a row and column, and why divide by `n`?
**A:** `row = k / n` and `col = k % n`, where `n` is the number of columns. Each row holds `n` cells, so dividing by `n` counts how many complete rows come before index `k`.

**Q:** Why does the loop use `lo <= hi` and `mid + 1` / `mid - 1`?
**A:** Both bounds are real indices that may still hold the target, so the range stays non-empty while `lo <= hi`. Stepping past `mid` on each side guarantees the range shrinks, so the loop terminates, and when `lo > hi` the target cannot be present.

**Q:** What happens with a single-column matrix?
**A:** With `n = 1`, `mid / 1 = mid` and `mid % 1 = 0`, so the search simply runs down the column.

**Q:** Why is the staircase search from the harder 2D matrix problem not enough here?
**A:** It takes O(m + n) steps, which is worse than the required O(log(m * n)), and it does not use the extra property that each row starts after the previous row ends.

**Q:** How does the two-binary-search alternative work, and how does its complexity compare?
**A:** First binary search for the last row whose first element is at most the target, then binary search within that row. It costs O(log m + log n), which equals O(log(m * n)), the same as the flattened search.

## 13. Final Code

```java
public boolean searchMatrix(int[][] matrix, int target) {
    int m = matrix.length;
    int n = matrix[0].length;

    int lo = 0;
    int hi = m * n - 1;

    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        int value = matrix[mid / n][mid % n];

        if (value == target) {
            return true;
        }
        if (value < target) {
            lo = mid + 1;
        } else {
            hi = mid - 1;
        }
    }
    return false;
}
```

## 14. Self-Test

You have an `m x n` matrix whose rows are sorted and whose first element in each row is greater than the last element of the previous row. Decide whether a target value appears in it, in O(log(m * n)) time. Think about what single sorted sequence the matrix hides, and how to turn an index in that sequence back into a row and column.
