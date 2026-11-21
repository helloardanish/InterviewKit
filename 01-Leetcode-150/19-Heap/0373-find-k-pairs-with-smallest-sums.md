---
title: "0373 — Find K Pairs with Smallest Sums"
layout: default
parent: Heap
grand_parent: Leetcode
nav_order: 373
permalink: /19-Heap/0373-find-k-pairs-with-smallest-sums/

problem: 373 - Find K Pairs with Smallest Sums
url: https://leetcode.com/problems/find-k-pairs-with-smallest-sums/
difficulty: Medium
section: Heap
patterns: [k-way-merge, min-heap]
data_structures: [heap, array]
time: O(k log min(n, k))
space: O(min(n, k))
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0373 — Find K Pairs with Smallest Sums

| Field | Value |
|---|---|
| **Pattern** | K-Way Merge · Min Heap |
| **Difficulty** | Medium |
| **Section** | Heap |
| **Time** | O(k log min(n, k)) |
| **Space** | O(min(n, k)) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Microsoft, Meta |
| **LeetCode** | [Find K Pairs with Smallest Sums]([https://leetcode.com/problems/find-k-pairs-with-smallest-sums/](https://leetcode.com/problems/find-k-pairs-with-smallest-sums/)) |

## 1. Problem in Simple Words

**Given:** two integer arrays `nums1` and `nums2`, both sorted in non-decreasing order, and an integer `k`.

**Required:** return the `k` pairs `(u, v)`, with `u` from `nums1` and `v` from `nums2`, that have the smallest sums `u + v`.

**Constraints that matter:**
- `1 <= nums1.length, nums2.length <= 10^5` — there can be up to `10^10` pairs. Generating all pairs is impossible in time and memory, so the algorithm must produce only about `k` pairs.
- `1 <= k <= 10^4` and `k <= nums1.length * nums2.length` — `k` is small relative to the number of pairs, and it is always achievable, so there is no "not enough pairs" case.
- `-10^9 <= nums[i] <= 10^9` — a single pair sum lies in `[-2 * 10^9, 2 * 10^9]`, which still fits in an `int` (about `±2.147 * 10^9`). But the **difference of two sums** can reach `4 * 10^9`, which overflows `int`, so a comparator must not subtract sums.
- Both arrays are sorted — this is the structure to exploit: for a fixed `u`, the sums `u + nums2[0], u + nums2[1], ...` are already in non-decreasing order.

## 2. Recognition Signals

- "k smallest" combinations from two sorted sequences: the full set of combinations is huge, but only the first `k` in sorted order are needed.
- Each element `nums1[i]` defines a **sorted list** of sums (`nums1[i] + nums2[0..m-1]`). Merging many sorted lists to get the first `k` smallest elements is a **k-way merge**.
- The next smallest pair overall is always the smallest among the "next unused pair" of each list, so a min-heap of those frontier pairs is enough.
- Pattern: **k-way merge with a min-heap of `(i, j)` index pairs, popping `k` times.**

## 3. Core Idea

Treat row `i` as the sorted sequence of pairs `(i, 0), (i, 1), (i, 2), ...` with sums `nums1[i] + nums2[j]`. Seed a min-heap with the first pair `(i, 0)` of each row, but only for the first `min(n, k)` rows. Repeat `k` times: pop the smallest pair `(i, j)`, output it, and push its row successor `(i, j + 1)` if it exists.

**Why it's correct:** because `nums2` is sorted, within a row the pairs appear in non-decreasing sum order, so the smallest unused pair of a row is always its first unused one. The overall smallest unused pair is therefore the minimum over rows of each row's first unused pair, which is exactly what the heap's root provides. Only the first `min(n, k)` rows are needed: for any row `i >= k`, the pairs `(0,0), (1,0), ..., (k-1,0)` all have sums no larger than `(i,0)` (since `nums1` is sorted), and each of those pairs lies in a different row, so row `i` can never contribute to the `k` smallest.

**Invariant:** the heap contains, for every seeded row that is not exhausted, its smallest not-yet-output pair; every pair already output has a sum less than or equal to every pair still in the heap or not yet generated.

## 4. Approach 1 — Brute Force

Generate every pair, sort them by sum, and take the first `k`.

```java
public List<List<Integer>> kSmallestPairsBruteForce(int[] nums1, int[] nums2, int k) {
    List<int[]> all = new ArrayList<>();
    for (int u : nums1) {
        for (int v : nums2) {
            all.add(new int[] {u, v});
        }
    }
    all.sort((a, b) -> Integer.compare(a[0] + a[1], b[0] + b[1]));

    List<List<Integer>> result = new ArrayList<>();
    for (int i = 0; i < k; i++) {
        result.add(List.of(all.get(i)[0], all.get(i)[1]));
    }
    return result;
}
```

**Time:** O(n · m · log(n · m)).
**Space:** O(n · m).

Not feasible: with both arrays at length `10^5` there are `10^10` pairs, far beyond time and memory limits. It ignores that both arrays are sorted and that only `k <= 10^4` pairs are needed.

## 5. Approach 2 — Optimized (Min-Heap k-Way Merge)

1. Create a min-heap of `int[] {i, j}` ordered by `nums1[i] + nums2[j]`.
2. For `i` from `0` to `min(n, k) - 1`, offer `{i, 0}`.
3. Repeat while `k > 0` and the heap is non-empty: poll `{i, j}`, add `[nums1[i], nums2[j]]` to the result, decrement `k`, and if `j + 1 < nums2.length`, offer `{i, j + 1}`.
4. Return the result.

```java
public List<List<Integer>> kSmallestPairs(int[] nums1, int[] nums2, int k) {
    List<List<Integer>> result = new ArrayList<>();

    PriorityQueue<int[]> minHeap = new PriorityQueue<>(
        (a, b) -> Integer.compare(nums1[a[0]] + nums2[a[1]], nums1[b[0]] + nums2[b[1]]));

    int rows = Math.min(nums1.length, k);
    for (int i = 0; i < rows; i++) {
        minHeap.offer(new int[] {i, 0});
    }

    while (k > 0 && !minHeap.isEmpty()) {
        int[] top = minHeap.poll();
        int i = top[0];
        int j = top[1];
        result.add(List.of(nums1[i], nums2[j]));
        k--;

        if (j + 1 < nums2.length) {
            minHeap.offer(new int[] {i, j + 1});
        }
    }
    return result;
}
```

**Time:** O(k log min(n, k)). There are at most `min(n, k)` initial offers and `k` pop-and-push steps, each O(log of the heap size), and the heap never holds more than `min(n, k)` entries.
**Space:** O(min(n, k)) for the heap, plus O(k) for the output.

This is optimal for the structure: only about `k` pairs are ever examined. The heap stores **indices**, not sums, so each pop can derive its successor `(i, j + 1)`.

## 6. Dry Run

Example 1: `nums1 = [1,7,11]`, `nums2 = [2,4,6]`, `k = 3`. Seed rows: `min(3, 3) = 3`, so the heap starts with `(0,0)` sum 3, `(1,0)` sum 9, `(2,0)` sum 13.

| Step | Heap (sum, pair) before pop | Popped | Output so far | Pushed |
|---|---|---|---|---|
| 1 | 3 (0,0), 9 (1,0), 13 (2,0) | (0,0), sum 3 | [1,2] | (0,1), sum 5 |
| 2 | 5 (0,1), 9 (1,0), 13 (2,0) | (0,1), sum 5 | [1,2], [1,4] | (0,2), sum 7 |
| 3 | 7 (0,2), 9 (1,0), 13 (2,0) | (0,2), sum 7 | [1,2], [1,4], [1,6] | none (`j + 1 = 3` is past the end) |

Exit condition: `k` reaches 0. Final answer: `[[1,2],[1,4],[1,6]]`. The tricky point: the next smallest pairs after `[1,2]` come from the same row (`1 + 4`, `1 + 6`) because they beat `7 + 2 = 9`, and the heap handles that automatically.

## 7. Complexity Summary

| Approach | Time | Space | Notes |
|---|---|---|---|
| Generate all pairs, sort | O(n · m · log(n · m)) | O(n · m) | Up to `10^10` pairs; infeasible |
| Min-heap k-way merge | O(k log min(n, k)) | O(min(n, k)) | Only about `k` pairs touched; output is O(k) extra |

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|---|---|---|
| `k = 1` | Only the first row's first pair matters | Seeds one row, pops `(0,0)`, returns `[nums1[0], nums2[0]]` |
| `k` equals the total number of pairs | Every pair must be output | The heap drains completely; each row advances to its end |
| `nums1` much longer than `k` | Seeding all rows would be wasteful | Only `min(n, k)` rows are seeded |
| `nums2` has length 1 | Each row has just one pair | `j + 1 < nums2.length` is false, so no successors are pushed |
| `nums1` has length 1 | Single row, answer is the first `k` of that row | One seed; successors advance along `nums2` |
| Duplicate values (`[1,1,2]`, `[1,2,3]`) | Equal pairs appear multiple times (`[1,1]` twice) | Each index pair is distinct, so equal-valued pairs are each output separately |
| Equal sums | Ties among different pairs | Any order among ties is valid; which tied pair comes first depends on heap tie-breaking |
| Sums near `±2 * 10^9` | Overflow if comparing by subtraction | Comparator uses `Integer.compare` on the sums; a single sum fits in `int` under these bounds |
| Negative values | Sums can be negative | Only comparisons on sums are used |

## 9. Java Notes

- Store **indices** `{i, j}` in the heap rather than values, because after polling you need `i` and `j + 1` to form the successor.
- The comparator reads `nums1` and `nums2` through the indices (a lambda capturing the arrays), so no sum needs to be stored.
- Use `Integer.compare(sumA, sumB)`; never `sumA - sumB`. Each sum fits in `int` here (at most `2 * 10^9`), but their difference can exceed `int` range and wrap around.
- `List.of(a, b)` builds an immutable two-element list; it is fine here because the result lists are never modified. `Arrays.asList(a, b)` also works.
- `PriorityQueue.offer` and `poll` are O(log size); keeping the heap at most `min(n, k)` entries keeps those costs small.
- Decrementing `k` in the loop mutates the parameter; that is legal in Java and is the simplest counter.

## 10. Common Mistakes

- Generating all `n * m` pairs, which runs out of time and memory at `10^5 x 10^5`. Fix: use the heap and stop after `k` pops.
- Seeding the heap with every element of `nums1` when `n` is large. It is still correct, but wastes time and memory when `k` is small. Fix: seed only `min(n, k)` rows.
- Pushing both `(i + 1, j)` and `(i, j + 1)` after each pop without a visited set, which produces duplicate pairs. Fix: push only `(i, j + 1)`, since all rows are seeded up front.
- Comparing sums by subtraction in the comparator, which can overflow. Fix: `Integer.compare`.
- Storing sums or values in the heap and losing the indices needed to find the successor. Fix: store `{i, j}`.
- Not stopping at `k` (looping until the heap is empty). Fix: stop when `k` pairs have been collected.
- Forgetting the `j + 1 < nums2.length` check, causing `ArrayIndexOutOfBoundsException` at the end of a row. Fix: guard the push.

## 11. Interview Takeaway

- **Trigger sentence:** "k smallest combinations from sorted arrays means each row is a sorted list, so do a k-way merge: a min-heap of the next unused pair of each row, popped `k` times."
- **90-second explanation:** There can be up to `10^10` pairs, but I only need `k`, and both arrays are sorted. For a fixed element of `nums1`, its sums with `nums2` are already in increasing order, so each element of `nums1` gives me a sorted list of pairs, and I just need the first `k` of the merge of those lists. I keep a min-heap of index pairs `(i, j)` ordered by `nums1[i] + nums2[j]`, seeded with `(i, 0)` for the first `min(n, k)` rows; rows beyond index `k` can never matter because `k` earlier rows already have smaller-or-equal first pairs. Then `k` times I pop the smallest pair, output it, and push `(i, j + 1)` from the same row if it exists. That's O(k log min(n, k)) time and O(min(n, k)) space. I compare sums with `Integer.compare` because a difference of two sums can overflow `int`, even though a single sum fits.
- **Related problems:** 23 (Merge k Sorted Lists), 378 (Kth Smallest Element in a Sorted Matrix), 215 (Kth Largest Element in an Array), 719 (Find K-th Smallest Pair Distance).

## 12. Recall Questions

**Q:** Why can each row be treated as a sorted list?
**A:** For a fixed `nums1[i]`, `nums2` is sorted, so the sums `nums1[i] + nums2[j]` are non-decreasing in `j`. The first unused pair in a row is always its smallest unused one.

**Q:** Why is it enough to seed only the first `min(n, k)` rows?
**A:** For a row `i >= k`, its first pair has a sum at least that of each of the first `k` rows' first pairs, so at least `k` pairs from other rows are no larger, and row `i` cannot contribute to the `k` smallest.

**Q:** Why does the heap store indices instead of sums?
**A:** After popping, the algorithm needs the row and column to push the successor `(i, j + 1)` and to output the actual values; a stored sum alone would not give that.

**Q:** Why avoid `a - b` for comparing sums here?
**A:** Two sums can differ by up to `4 * 10^9`, which overflows `int`, giving a wrong sign; `Integer.compare` never overflows.

**Q:** What is the time complexity and why?
**A:** O(k log min(n, k)): `k` pops and at most `k` pushes, each on a heap of at most `min(n, k)` entries.

## 13. Final Code

```java
import java.util.*;

class Solution {
    public List<List<Integer>> kSmallestPairs(int[] nums1, int[] nums2, int k) {
        List<List<Integer>> result = new ArrayList<>();

        PriorityQueue<int[]> minHeap = new PriorityQueue<>(
            (a, b) -> Integer.compare(nums1[a[0]] + nums2[a[1]], nums1[b[0]] + nums2[b[1]]));

        int rows = Math.min(nums1.length, k);
        for (int i = 0; i < rows; i++) {
            minHeap.offer(new int[] {i, 0});
        }

        while (k > 0 && !minHeap.isEmpty()) {
            int[] top = minHeap.poll();
            int i = top[0];
            int j = top[1];
            result.add(List.of(nums1[i], nums2[j]));
            k--;

            if (j + 1 < nums2.length) {
                minHeap.offer(new int[] {i, j + 1});
            }
        }
        return result;
    }
}
```

## 14. Self-Test

Given two sorted integer arrays and an integer `k`, return the `k` pairs (one element from each array) with the smallest sums. Re-derive: treat each `nums1[i]` as a sorted list of pairs `(i, 0), (i, 1), ...`; seed a min-heap with `(i, 0)` for the first `min(n, k)` rows, ordered by `nums1[i] + nums2[j]`; pop `k` times, output the popped pair, and push `(i, j + 1)` if `j + 1` is in range.
