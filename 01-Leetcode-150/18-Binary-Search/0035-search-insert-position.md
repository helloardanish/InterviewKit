---
title: "0035 — Search Insert Position"
layout: default
parent: Binary Search
grand_parent: Leetcode
nav_order: 35
permalink: /18-Binary-Search/0035-search-insert-position/

problem: 35 - Search Insert Position
url: https://leetcode.com/problems/search-insert-position/
difficulty: Easy
section: Binary Search
patterns: [binary-search, lower-bound]
data_structures: [array]
time: O(log n)
space: O(1)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0035 — Search Insert Position

| Field | Value |
|---|---|
| **Pattern** | Binary Search · Lower Bound |
| **Difficulty** | Easy |
| **Section** | Binary Search |
| **Time** | O(log n) |
| **Space** | O(1) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Microsoft, Meta |
| **LeetCode** | [Search Insert Position]([https://leetcode.com/problems/search-insert-position/](https://leetcode.com/problems/search-insert-position/)) |

## 1. Problem in Simple Words

**Given:** An array `nums` of distinct integers sorted in ascending order, and an integer `target`.

**Required:** Return the index of `target` if it is in the array. Otherwise return the index where it would have to be inserted to keep the array sorted. The algorithm must run in O(log n) time.

**Constraints that actually matter:**
- **"You must write an algorithm with O(log n) runtime complexity"** — this is an explicit requirement, not just a hint. A linear scan would pass the tests at `n <= 10^4`, but it violates the stated requirement, so binary search is expected.
- `nums` is **sorted ascending with distinct values** — sortedness is what makes binary search possible, and distinctness means "the index of the target" is unambiguous.
- `1 <= nums.length <= 10^4` — the array is never empty, and a binary search takes at most about 14 steps.
- `-10^4 <= nums[i], target <= 10^4` — small signed values. They are only compared, never combined, so negatives and extremes cause no overflow issues.
- The answer can be anywhere from `0` (target smaller than everything) to `n` (target larger than everything). **`n` is a legal answer even though it is not a valid index into the array**, so the search space has `n + 1` possible answers, not `n`.

## 2. Recognition Signals

- "Sorted array" plus "O(log n)" → binary search.
- "Return the index if found, otherwise the position where it would be inserted" → this is exactly the definition of a **lower bound**: the first index whose value is greater than or equal to `target`. When the target is present, that first index is the target's own index (values are distinct), and when it is absent, it is the insertion point.
- The answer may be one past the last element, which tells you to include `n` in the search space rather than only the indices `0..n-1`.

**Pattern:** Binary search for the lower bound (the first position whose value is `>= target`) over the half-open range `[0, n)` with an answer range of `[0, n]`.

## 3. Core Idea

Ask the same yes/no question at every position: "is `nums[i] >= target`?" Because the array is sorted, the answers form a pattern of all `false` followed by all `true` (for example `F F T T` for `[1,3,5,6]` and target `5`). The answer to the problem is the index of the first `true`, or `n` if there is none. A binary search finds the boundary of such a monotonic pattern by repeatedly probing the middle.

- **Search space:** the candidate answers `lo..hi`, initially `[0, n]`, where `hi` is exclusive as a probe position but is itself a valid answer.
- **Invariant:** every index less than `lo` holds a value `< target`, and every index at or after `hi` holds a value `>= target` (or lies past the end). So the answer is always in `[lo, hi]`.
- **Update rules:** if `nums[mid] < target`, the answer is after `mid`, so `lo = mid + 1`. Otherwise `nums[mid] >= target`, so `mid` itself might be the answer and we keep it with `hi = mid`.
- **Why it cannot loop forever:** with `lo < hi`, the midpoint `mid = lo + (hi - lo) / 2` always satisfies `lo <= mid < hi`. If we set `lo = mid + 1`, `lo` strictly increases. If we set `hi = mid`, `hi` strictly decreases because `mid < hi`. So the range `hi - lo` shrinks every iteration and reaches zero.

When the loop ends, `lo == hi`, and by the invariant this is exactly the first index with `nums[i] >= target`, or `n`.

## 4. Approach 1 — Brute Force

**Logic:** Scan from the left and return the first index whose value is at least `target`. If none is found, the target belongs at the end, so return `n`.

```java
public int searchInsert(int[] nums, int target) {
    for (int i = 0; i < nums.length; i++) {
        if (nums[i] >= target) {
            return i;
        }
    }
    return nums.length;
}
```

- **Time:** O(n) in the worst case, when the answer is near the end.
- **Space:** O(1).

**Why it is not optimal:** It is correct, but it ignores that the array is sorted and it breaks the problem's explicit O(log n) requirement. It would still pass at `n <= 10^4`, which is why the requirement matters: an interviewer will ask for the logarithmic solution.

## 5. Approach 2 — Optimized

**Algorithm:**
1. Set `lo = 0` and `hi = nums.length`. Note that `hi` is `n`, not `n - 1`, because `n` is a legal answer.
2. While `lo < hi`:
   - Compute `mid = lo + (hi - lo) / 2`.
   - If `nums[mid] < target`, the insertion point is to the right, so set `lo = mid + 1`.
   - Otherwise set `hi = mid`, because `mid` may itself be the answer.
3. Return `lo`.

```java
public int searchInsert(int[] nums, int target) {
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
```

- **Time:** O(log n). The range `hi - lo` is roughly halved each iteration, so there are at most about `log2(n + 1)` iterations (about 14 for `n = 10^4`).
- **Space:** O(1). Only `lo`, `hi`, and `mid` are used.

**Why this is optimal:** O(log n) is the best possible for searching a sorted array by comparisons, and it meets the problem's requirement. There is no time-space trade-off: the algorithm uses constant memory. The same code handles a present target (returns its index) and an absent one (returns the insertion point), with no special cases, because it always finds the first position whose value is `>= target`.

## 6. Dry Run

Example: `nums = [1, 3, 5, 6]`, `target = 2` (Example 2). Chosen because the target is absent and falls in the middle, so the search takes both branches (moving `hi` down and `lo` up) before settling.

Initial: `lo = 0`, `hi = 4`.

| Iteration | lo | hi | mid | nums[mid] | Compare with target 2 | Action | New lo | New hi |
|-----------|----|----|-----|-----------|-----------------------|--------|--------|--------|
| 1 | 0 | 4 | 2 | 5 | 5 >= 2 | hi = mid | 0 | 2 |
| 2 | 0 | 2 | 1 | 3 | 3 >= 2 | hi = mid | 0 | 1 |
| 3 | 0 | 1 | 0 | 1 | 1 < 2 | lo = mid + 1 | 1 | 1 |

Exit condition: `lo == hi == 1`, so the loop stops.

**Final answer:** `1`, matching the expected output. Index 1 is where `2` would be inserted, between `1` and `3`.

## 7. Complexity Summary

| Approach | Time | Space (auxiliary) | Notes |
|----------|------|-------------------|-------|
| 1. Linear scan | O(n) | O(1) | Correct but violates the O(log n) requirement |
| 2. Binary search (lower bound) | O(log n) | O(1) | Meets the requirement; no extra memory |

The input array is used read-only; there is no output structure beyond a single integer, so input and output space are the same for both approaches.

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|------|------------------|--------------------------|
| Single element (`n = 1`) | The smallest legal input; the answer is `0` or `1` | With `lo = 0`, `hi = 1`, one probe at `mid = 0` decides: `nums[0] < target` gives `lo = 1`, otherwise `hi = 0`, so it returns `1` or `0` |
| Target smaller than every element | The insertion point is the very front | Every probe has `nums[mid] >= target`, so `hi` shrinks to `0` and the code returns `0` |
| Target larger than every element (Example 3, answer `4`) | The answer is `n`, which is not a valid index | `hi` starts at `n` and `lo = mid + 1` can reach `n`, so the loop ends with `lo = n` and returns it; using `hi = n - 1` would make this impossible |
| Target equals an existing element (Example 1) | Must return that element's index, not the position after it | The comparison is `nums[mid] < target` (strict), so equal values go to the `hi = mid` branch and the search converges on that exact index |
| Duplicates / all-same values | A repeated target could make "the index" ambiguous | Not possible here because values are distinct. If duplicates existed, the same code would return the first occurrence, which is still a correct insertion point |
| Negative numbers | Negative values or targets might seem special | Only `<` comparisons are used, so sign does not matter |
| Min and max values (`-10^4`, `10^4`) | Might raise overflow concerns | Nothing is added or subtracted from values; `mid = lo + (hi - lo) / 2` uses indices only and cannot overflow |
| Maximum size (`n = 10^4`) | Checks that the solution is truly logarithmic | About 14 iterations |

## 9. Java Notes

- **`mid = lo + (hi - lo) / 2`:** the overflow-safe midpoint. Indices are at most `10^4`, so `(lo + hi) / 2` would also be fine here, but this form is the habit to carry to larger inputs. It rounds down, which is what the termination argument relies on (`mid < hi` when `lo < hi`).
- **`hi = nums.length` (exclusive upper bound), not `nums.length - 1`:** this is deliberate. With `while (lo < hi)` and `hi = mid`, the loop treats `hi` as "one past the last candidate", and it lets the answer `n` be returned naturally.
- **`Arrays.binarySearch` exists but is not used here:** when the key is not found it returns `-(insertionPoint) - 1`, so `idx >= 0 ? idx : -idx - 1` gives the same answer. The explicit loop is shown because the lower-bound logic is the point of the exercise.
- **Closed-interval alternative:** a version with `lo = 0`, `hi = n - 1`, `while (lo <= hi)`, an early return on equality, and `return lo` at the end also works, but the half-open version above has fewer off-by-one traps.
- No recursion or auxiliary objects are involved, so there is nothing to allocate.

## 10. Common Mistakes

- **Starting with `hi = nums.length - 1` while using `while (lo < hi)` and `hi = mid`.** The loop can then never return `n`, so a target larger than every element silently returns `n - 1`, which is wrong. Fix: use `hi = nums.length`, or switch to the matching closed-interval loop.
- **Setting `lo = mid` instead of `lo = mid + 1`.** When `hi - lo == 1`, `mid == lo`, so `lo` never changes and the loop hangs forever. Fix: always move `lo` past `mid` when `nums[mid] < target`.
- **Using `nums[mid] <= target` for the "go right" branch.** On a present target this moves past it and returns the index after the target (the upper bound), which is wrong for Example 1. Fix: use strict `<`, so values equal to the target stay on the `hi = mid` side.
- **Returning `mid` (or `-1`) after the loop.** `mid` is declared inside the loop and does not hold the answer. The answer is `lo` (equal to `hi`) once the range is empty. Fix: return `lo`.

## 11. Interview Takeaway

- **Trigger sentence to memorize:** "Sorted array, find the index or the insertion point, O(log n) → binary search for the first index whose value is `>= target` (lower bound), over `[0, n]` with `hi = n`."
- **How to explain it aloud in 90 seconds:** "Because the array is sorted, the question 'is `nums[i] >= target`' is false for a prefix and true for the rest. The answer is the first position where it becomes true, or `n` if it never does, and that is also the target's index if it is present. I binary search that boundary. I keep a range `lo` to `hi` that always contains the answer, starting with `lo = 0` and `hi = n`, since `n` is a valid answer. At each step I take the middle. If its value is smaller than the target, the answer is strictly to the right, so `lo = mid + 1`. Otherwise the middle might be the answer, so I keep it and set `hi = mid`. When `lo` equals `hi`, that is the answer. It terminates because the middle is always strictly below `hi`, so each step shrinks the range. That is O(log n) time and O(1) space. A linear scan would also work but violates the required complexity."
- **Related problems using this pattern:**
  - LeetCode 34 — Find First and Last Position of Element in Sorted Array
  - LeetCode 74 — Search a 2D Matrix
  - LeetCode 33 — Search in Rotated Sorted Array
  - LeetCode 162 — Find Peak Element

## 12. Recall Questions

**Q:** Why does the search start with `hi = n` rather than `n - 1`?
**A:** The answer can be `n` when the target is larger than every element, and that is not a valid array index. Starting `hi` at `n` lets the search return it.

**Q:** Why is it `hi = mid` and not `hi = mid - 1` when `nums[mid] >= target`?
**A:** `mid` itself could be the first position with a value `>= target`, so it must stay inside the range; discarding it could skip the answer.

**Q:** Why can this loop never run forever?
**A:** With `lo < hi`, `mid` is always less than `hi`. Setting `lo = mid + 1` increases `lo`, and setting `hi = mid` decreases `hi`, so `hi - lo` strictly shrinks until the loop ends.

**Q:** What does the invariant guarantee when the loop exits with `lo == hi`?
**A:** Everything before `lo` is smaller than the target and everything from `hi` onward is at least the target, so `lo` is exactly the first index with a value `>= target`, or `n`.

**Q:** Why does the same code work whether the target is present or absent?
**A:** It never looks for equality. It finds the first position with a value `>= target`, which is the target's own index when it exists and the insertion point when it does not.

**Q:** What would change if the array contained duplicates?
**A:** The code would return the index of the first occurrence of the target (or the insertion point), which is still a correct insertion index.

## 13. Final Code

```java
public int searchInsert(int[] nums, int target) {
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
```

## 14. Self-Test

You have a sorted array of distinct integers and a target. Return the target's index if present; otherwise return the index where it would be inserted to keep the array sorted, in O(log n). Think about what yes/no question is false for a prefix of the array and true for the rest, and why the answer range must include `n`.
