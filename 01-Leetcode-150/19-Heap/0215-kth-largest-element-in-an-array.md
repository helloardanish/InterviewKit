---
title: "0215 — Kth Largest Element in an Array"
layout: default
parent: Heap
grand_parent: Leetcode
nav_order: 215
permalink: /19-Heap/0215-kth-largest-element-in-an-array/

problem: 215 - Kth Largest Element in an Array
url: https://leetcode.com/problems/kth-largest-element-in-an-array/
difficulty: Medium
section: Heap
patterns: [top-k, min-heap, quickselect]
data_structures: [heap, array]
time: O(n log k)
space: O(k)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0215 — Kth Largest Element in an Array

| Field | Value |
|---|---|
| **Pattern** | Top K · Min Heap · Quickselect |
| **Difficulty** | Medium |
| **Section** | Heap |
| **Time** | O(n log k) |
| **Space** | O(k) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Microsoft, Meta |
| **LeetCode** | [Kth Largest Element in an Array]([https://leetcode.com/problems/kth-largest-element-in-an-array/](https://leetcode.com/problems/kth-largest-element-in-an-array/)) |

## 1. Problem in Simple Words

**Given:** an integer array `nums` and an integer `k`.

**Required:** return the `k`-th largest element in sorted order. It is the `k`-th largest **counting duplicates**, not the `k`-th distinct value. For `[3,2,3,1,2,4,5,5,6]` and `k = 4`, sorted descending is `6,5,5,4,3,3,2,2,1`, so the answer is `4`. The problem also asks: can you solve it without sorting?

**Constraints that matter:**
- `1 <= k <= nums.length <= 10^5` — `k` is always valid, so the answer always exists and no "not enough elements" case needs handling. At `n = 10^5`, sorting in O(n log n) is fast enough; the follow-up is about showing a better technique.
- `-10^4 <= nums[i] <= 10^4` — small values, so comparisons never overflow. The value range is only 20,001 wide, which also makes a counting approach possible.
- Duplicates are allowed and count separately. That rules out de-duplicating (for example with a `TreeSet`) and affects quickselect: partitioning schemes that handle many equal values poorly can degrade to O(n²) on an array where every element is equal.
- (One older copy of the page lists `n <= 10^4`; the current problem page and several other copies say `10^5`, which is what is used here.)

## 2. Recognition Signals

- "K-th largest/smallest" or "top k" in an unordered array, where only one rank is needed rather than a full ordering. Full sorting does more work than required.
- "Can you solve it without sorting?" points to a **heap of size k** or **quickselect**.
- A heap whose size is capped at `k` holds the `k` largest values seen so far; its smallest element (the root of a min-heap) is exactly the `k`-th largest.
- Pattern: **min-heap of size `k` (top-k)**, with **quickselect** as the average O(n) alternative.

## 3. Core Idea

Keep a **min-heap** containing at most `k` elements. For each number, add it; if the heap now has more than `k` elements, remove the smallest. After all numbers are processed, the heap holds the `k` largest values and its root, the smallest of them, is the `k`-th largest.

**Why it's correct:** removing the smallest of `k + 1` elements can never remove one of the `k` largest elements overall, because at least `k` retained elements are at least as large as it. So by induction the heap always contains the `k` largest values of the prefix processed so far (or all of them, if fewer than `k` have been seen). Duplicates are handled naturally, since the heap stores a multiset of values.

**Invariant:** after processing the first `i` elements, the heap holds the `min(i, k)` largest of them, so once `i >= k` the root is the `k`-th largest of those `i` elements.

## 4. Approach 1 — Brute Force

Sort the array ascending; the `k`-th largest is at index `n - k`.

```java
public int findKthLargestSort(int[] nums, int k) {
    Arrays.sort(nums);
    return nums[nums.length - k];
}
```

**Time:** O(n log n).
**Space:** O(log n) for the sort's recursion stack (it also reorders the caller's array).

Not optimal given the prompt: it fully orders all `n` elements when only one rank is needed. It is a correct, accepted baseline and a good reference for testing.

## 5. Approach 2 — Optimized

### 5a. Min-heap of size k (the top-k pattern)

```java
public int findKthLargest(int[] nums, int k) {
    PriorityQueue<Integer> minHeap = new PriorityQueue<>();

    for (int num : nums) {
        minHeap.offer(num);
        if (minHeap.size() > k) {
            minHeap.poll();
        }
    }
    return minHeap.peek();
}
```

**Time:** O(n log k): each of the `n` elements causes at most one offer and one poll, each O(log k) because the heap never exceeds `k + 1` elements.
**Space:** O(k) for the heap.

The heap holds the `k` largest so far, and it is a **min**-heap because the element we must be able to evict quickly is the smallest of those `k`, which is the root.

### 5b. Quickselect (average O(n))

Partition around a random pivot into three regions (less than, equal to, greater than), then keep only the region that contains the target index `n - k` in ascending order. The three-way partition matters: with many equal values, a two-way partition can shrink by only one element per round and degrade to O(n²).

```java
public int findKthLargestQuickSelect(int[] nums, int k) {
    int target = nums.length - k;
    int lo = 0;
    int hi = nums.length - 1;
    Random random = new Random();

    while (lo < hi) {
        int pivot = nums[lo + random.nextInt(hi - lo + 1)];

        int lt = lo;
        int gt = hi;
        int i = lo;
        while (i <= gt) {
            if (nums[i] < pivot) {
                swap(nums, lt++, i++);
            } else if (nums[i] > pivot) {
                swap(nums, i, gt--);
            } else {
                i++;
            }
        }

        if (target < lt) {
            hi = lt - 1;
        } else if (target > gt) {
            lo = gt + 1;
        } else {
            return nums[target];
        }
    }
    return nums[lo];
}

private void swap(int[] nums, int a, int b) {
    int tmp = nums[a];
    nums[a] = nums[b];
    nums[b] = tmp;
}
```

**Time:** expected O(n) with a random pivot; worst case O(n²) (extremely unlikely).
**Space:** O(1) auxiliary (iterative); it reorders the input array.

Which to choose: the heap is deterministic, short, and also works when elements arrive as a stream, at the cost of an extra log factor. Quickselect is the faster average-case answer and uses no extra memory, but it mutates the input and needs a careful partition. Because the values lie in a small range (`-10^4` to `10^4`), a counting array of about 20,001 buckets would also solve this in O(n + range) time and O(range) space.

## 6. Dry Run

Heap approach, `nums = [3,2,3,1,2,4,5,5,6]`, `k = 4`. The tricky part is that duplicates count separately and the heap keeps exactly the 4 largest.

| Number | Heap after offer | Polled (if size > 4) | Heap after |
|---|---|---|---|
| 3 | {3} | no | {3} |
| 2 | {2,3} | no | {2,3} |
| 3 | {2,3,3} | no | {2,3,3} |
| 1 | {1,2,3,3} | no | {1,2,3,3} |
| 2 | {1,2,2,3,3} | 1 | {2,2,3,3} |
| 4 | {2,2,3,3,4} | 2 | {2,3,3,4} |
| 5 | {2,3,3,4,5} | 2 | {3,3,4,5} |
| 5 | {3,3,4,5,5} | 3 | {3,4,5,5} |
| 6 | {3,4,5,5,6} | 3 | {4,5,5,6} |

Exit condition: all elements processed. The root of the heap is `4`, the smallest of the four largest values `{4,5,5,6}`. Final answer: `4`.

## 7. Complexity Summary

| Approach | Time | Space | Notes |
|---|---|---|---|
| Sort, index `n - k` | O(n log n) | O(log n) | Simplest; fully orders the array |
| Min-heap of size k | O(n log k) | O(k) | Deterministic; works on streams |
| Quickselect (random pivot, 3-way partition) | Expected O(n), worst O(n²) | O(1) | Mutates input |
| Counting array over value range | O(n + R), R about 20,001 | O(R) | Exploits the small value range |

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|---|---|---|
| `k = 1` (maximum) | Heap size is 1 | Each offer immediately evicts the smaller value; the root is the max |
| `k = n` (minimum) | Nothing is ever evicted | Heap ends with all elements; root is the minimum |
| Single element | `n = k = 1` | Returns that element |
| Duplicates (`[3,2,3,1,2,4,5,5,6]`, `k = 4`) | The `k`-th largest counts duplicates, not distinct values | The heap stores a multiset, so both `5`s occupy slots |
| All elements equal | Two-way quickselect partitioning degrades to O(n²) | Heap is unaffected; the 3-way quickselect groups all equal values into one middle region and returns immediately |
| Already sorted or reverse-sorted input | Fixed-position pivots (first/last) degrade quickselect | Random pivot avoids the pattern; the heap does not depend on order |
| Negative values | Nothing special | Only comparisons are used |

## 9. Java Notes

- `PriorityQueue<Integer>` is a **min-heap** by default, which is exactly what the size-`k` approach needs. For the opposite direction you would pass `Collections.reverseOrder()`.
- After the loop, use `peek()`, not `poll()`, to read the answer. `k <= n` guarantees the heap is non-empty.
- `offer` then `poll` when the size exceeds `k` keeps the heap at most `k + 1` elements, so operations stay O(log k).
- `Arrays.sort(int[])` (used in the brute force) sorts a primitive array with a dual-pivot quicksort and reorders the caller's array.
- Quickselect mutates `nums`. If the caller's array must be preserved, copy it first.
- The quickselect target index is `n - k` in ascending order. Using `k - 1` there is the most common off-by-one.

## 10. Common Mistakes

- Using a max-heap of size `k`, which retains the `k` smallest elements. Fix: use a min-heap so the smallest of the top `k` can be evicted.
- Never evicting, so the heap grows to `n` elements and the solution becomes O(n log n) with O(n) space. Fix: poll whenever size exceeds `k`.
- Returning the `(k - 1)`-th element after an ascending sort instead of the `(n - k)`-th. Fix: `nums[nums.length - k]`.
- Counting distinct values (for example with a `Set` or `TreeSet`). Fix: duplicates count; keep a multiset.
- Implementing quickselect with a two-way Lomuto partition and a fixed pivot, which degrades to O(n²) on all-equal or sorted input. Fix: random pivot with a three-way partition.
- Forgetting to restrict quickselect to the side containing the target, accidentally sorting both sides. Fix: narrow `lo`/`hi` to one region each round.

## 11. Interview Takeaway

- **Trigger sentence:** "The k-th largest without a full sort means a min-heap capped at size `k`, whose root is the answer, or quickselect for average O(n)."
- **90-second explanation:** I keep a min-heap of at most `k` numbers. For each element I add it, and if the heap grows past `k` I remove the smallest. Removing the smallest of `k + 1` values can never discard one of the `k` largest, so the heap always holds the `k` largest values seen so far, and its root, the smallest of them, is the `k`-th largest. Duplicates just occupy separate slots. Each element causes at most one insert and one removal on a heap of size about `k`, so it is O(n log k) time and O(k) space, and it also works on a stream. If I want average O(n) with O(1) extra space, I use quickselect: pick a random pivot, three-way partition into less, equal, and greater, and recurse only into the region containing index `n - k`; the three-way split avoids the O(n²) trap when many values are equal. Sorting and taking index `n - k` is the simple O(n log n) baseline.
- **Related problems:** 703 (Kth Largest Element in a Stream), 347 (Top K Frequent Elements), 973 (K Closest Points to Origin), 414 (Third Maximum Number).

## 12. Recall Questions

**Q:** Why a min-heap and not a max-heap for the k-th largest?
**A:** The heap must hold the `k` largest values and quickly evict the smallest of them when a new value arrives. A min-heap exposes its smallest element at the root, so eviction is O(log k) and the root at the end is the answer.

**Q:** Why can evicting the heap's minimum never throw away part of the true top `k`?
**A:** When the heap has `k + 1` elements, at least `k` of them are greater than or equal to its minimum, so the minimum cannot be among the `k` largest.

**Q:** What is the time complexity of the heap approach and why?
**A:** O(n log k): there are `n` insertions and at most `n` removals, each on a heap of at most `k + 1` elements, costing O(log k).

**Q:** Why does quickselect need a three-way partition here?
**A:** With many equal values, a two-way partition can put all equal elements on one side and shrink the range by only one element per round, giving O(n²). A three-way partition groups all equal elements in the middle region so they are resolved together.

**Q:** Why is the target index `n - k` in quickselect?
**A:** Quickselect finds the element at a given position in ascending order, and the `k`-th largest is at position `n - k` (zero-based) in that order.

## 13. Final Code

```java
import java.util.*;

class Solution {
    public int findKthLargest(int[] nums, int k) {
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();

        for (int num : nums) {
            minHeap.offer(num);
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }
        return minHeap.peek();
    }
}
```

## 14. Self-Test

Given an integer array and `k` (with `1 <= k <= n`), return the `k`-th largest element in sorted order, counting duplicates, ideally without fully sorting. Re-derive: scan the array keeping a min-heap; add each number and, whenever the heap holds more than `k` elements, remove the smallest; at the end the heap's root is the `k`-th largest. For average O(n), quickselect with a random pivot and a three-way partition, narrowing to the region containing index `n - k`.
