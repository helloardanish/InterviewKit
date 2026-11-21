---
title: "0295 — Find Median from Data Stream"
layout: default
parent: Heap
grand_parent: Leetcode
nav_order: 295
permalink: /19-Heap/0295-find-median-from-data-stream/

problem: 295 - Find Median from Data Stream
url: https://leetcode.com/problems/find-median-from-data-stream/
difficulty: Hard
section: Heap
patterns: [two-heaps, balanced-halves]
data_structures: [heap, priority-queue]
time: O(log n) per addNum, O(1) per findMedian
space: O(n)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0295 — Find Median from Data Stream

| Field | Value |
|---|---|
| **Pattern** | Two Heaps · Balanced Halves |
| **Difficulty** | Hard |
| **Section** | Heap |
| **Time** | O(log n) per addNum, O(1) per findMedian |
| **Space** | O(n) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Microsoft, Meta |
| **LeetCode** | [Find Median from Data Stream]([https://leetcode.com/problems/find-median-from-data-stream/](https://leetcode.com/problems/find-median-from-data-stream/)) |

## 1. Problem in Simple Words

**Given:** A stream of integers arriving one at a time. You must implement a class `MedianFinder` with two operations:
- `void addNum(int num)` adds a number from the stream.
- `double findMedian()` returns the median of all numbers added so far. The median is the middle value of the sorted list, or the mean of the two middle values when the count is even. Answers within `10^-5` are accepted.

A note on the statement: LeetCode blocked the page fetch, so I took the statement and constraints from search-result copies of the problem page. They are worth a quick check against the live page.

**Constraints that actually matter:**
- **Calls to `addNum` and `findMedian` are interleaved,** with at most `5 * 10^4` calls in total. Sorting all stored values on every `findMedian` costs O(n log n) per query, which is too slow in the worst case (about 25,000 queries over up to 25,000 values).
- **There is always at least one element before `findMedian` is called,** so you never need to define the median of an empty set.
- `-10^5 <= num <= 10^5` — values are small and signed. Sums of two values cannot overflow `int`, but the result for an even count can be a half-integer such as `2.5`, so the return type is `double` and the division must be floating-point.
- Only the **middle** of the sorted order is ever needed, not the whole order. That points to a structure that keeps just the boundary elements easy to reach.
- The follow-ups ask how to optimize if all values are in `[0, 100]`, and if 99% of values are in `[0, 100]`. Both are answered by counting buckets (see the Interview Takeaway).

## 2. Recognition Signals

- "Median of a changing collection" with repeated queries → you cannot re-sort each time. Maintain a structure where the middle elements are always cheap to read.
- "Only the middle matters" → split the data into a lower half and an upper half. The median lives at the boundary between them, so you only need the largest of the lower half and the smallest of the upper half.
- "Largest of one group and smallest of another" → a **max-heap** for the lower half and a **min-heap** for the upper half, so each root is exactly the boundary element.
- A "design the class" problem with a stream of insertions → pick a data structure whose insert is cheap, and make the query O(1).

**Pattern:** Two heaps. A max-heap holds the smaller half, a min-heap holds the larger half, and the sizes are kept balanced so the median is at the heap roots.

## 3. Core Idea

Keep two heaps:
- `lower`: a **max-heap** holding the smaller half of the numbers, so its root is the largest of the lower half.
- `upper`: a **min-heap** holding the larger half, so its root is the smallest of the upper half.

**Invariants:**
1. Every element in `lower` is less than or equal to every element in `upper`.
2. `lower.size() == upper.size()` or `lower.size() == upper.size() + 1` (the lower half holds the extra element when the count is odd).

With these invariants, the median is read directly from the roots: if the count is odd, it is `lower`'s root; if even, it is the average of the two roots.

To add `num` while keeping both invariants, use a simple three-step routine:
1. Push `num` into `lower`.
2. Move `lower`'s largest element to `upper`. This guarantees ordering: whichever element moves is the largest among the lower candidates, so everything left in `lower` is still at most everything in `upper`.
3. If `upper` now has more elements than `lower`, move `upper`'s smallest back to `lower`.

Step 1 and 2 make the ordering correct regardless of whether `num` is small or large. Step 3 restores the size balance. Each add does a constant number of heap operations, each O(log n).

## 4. Approach 1 — Brute Force

**Logic:** Keep all numbers in a list that is always sorted. To add a number, find its insertion position with binary search and insert it there. The median is then read from the middle of the list.

```java
class MedianFinder {
    private final List<Integer> sorted = new ArrayList<>();

    public void addNum(int num) {
        int index = Collections.binarySearch(sorted, num);
        if (index < 0) {
            index = -index - 1;
        }
        sorted.add(index, num);
    }

    public double findMedian() {
        int size = sorted.size();
        if (size % 2 == 1) {
            return sorted.get(size / 2);
        }
        return (sorted.get(size / 2 - 1) + sorted.get(size / 2)) / 2.0;
    }
}
```

- **Time:** `addNum` is O(n): the binary search is O(log n), but inserting in the middle of an `ArrayList` shifts up to `n` elements. `findMedian` is O(1).
- **Space:** O(n) for the list.

**Why it is not optimal:** It keeps the entire stream in sorted order, but only the middle is needed. Every insertion pays for shifting elements, and a descending stream makes every insertion shift the whole list. A simpler variant (append unsorted, sort on every query) is even worse: O(n log n) per query. The two-heap approach keeps just enough order to reach the middle and makes every insertion O(log n).

## 5. Approach 2 — Optimized

**Algorithm:**
1. Create `lower` as a max-heap and `upper` as a min-heap.
2. `addNum(num)`:
   - `lower.offer(num)`.
   - `upper.offer(lower.poll())`, moving the largest element of the lower side across.
   - If `upper.size() > lower.size()`, do `lower.offer(upper.poll())`.
3. `findMedian()`:
   - If `lower.size() > upper.size()`, return `lower.peek()`.
   - Otherwise return `(lower.peek() + upper.peek()) / 2.0`.

```java
class MedianFinder {
    private final PriorityQueue<Integer> lower = new PriorityQueue<>(Collections.reverseOrder());
    private final PriorityQueue<Integer> upper = new PriorityQueue<>();

    public void addNum(int num) {
        lower.offer(num);
        upper.offer(lower.poll());
        if (upper.size() > lower.size()) {
            lower.offer(upper.poll());
        }
    }

    public double findMedian() {
        if (lower.size() > upper.size()) {
            return lower.peek();
        }
        return (lower.peek() + upper.peek()) / 2.0;
    }
}
```

- **Time:** `addNum` is O(log n): at most three heap operations (`offer`, `poll`, `offer`/`poll`), each O(log n). `findMedian` is O(1): it only reads the roots.
- **Space:** O(n) to hold all numbers across the two heaps.

**Why this is optimal:** Any structure that supports arbitrary insertions and exact median queries needs to store all the elements, so O(n) space is required. Insertion at O(log n) with an O(1) query is the standard best trade-off here. Compared with Approach 1, insertion drops from O(n) to O(log n), because a heap only moves elements along one root-to-leaf path instead of shifting a whole array.

**Why the order of operations matters:** Pushing the new number into `lower` first and then moving `lower`'s maximum to `upper` guarantees that every element left in `lower` is at most every element in `upper`, whether `num` is small or large. Then the size check restores balance. If you instead choose a heap by comparing `num` with a root, you must also rebalance sizes; the push-through trick keeps both rules in three lines.

## 6. Dry Run

Example: the call sequence from the problem statement: `addNum(1)`, `addNum(2)`, `findMedian()`, `addNum(3)`, `findMedian()`. Chosen because it covers an odd count and an even count and forces a size rebalance. The heaps are shown as sets; `lower`'s root is its largest element and `upper`'s root is its smallest.

| Step | Operation | What happens | lower (max-heap) | upper (min-heap) | Result |
|------|-----------|--------------|------------------|------------------|--------|
| 1 | `addNum(1)` | push 1 into lower; move lower's max (1) to upper; upper is larger than lower, so move upper's min (1) back to lower | {1} | {} | none |
| 2 | `addNum(2)` | push 2 into lower: {1,2}; move lower's max (2) to upper; sizes are 1 and 1, so no rebalance | {1} | {2} | none |
| 3 | `findMedian()` | sizes are equal, so average the roots: (1 + 2) / 2.0 | {1} | {2} | 1.5 |
| 4 | `addNum(3)` | push 3 into lower: {1,3}; move lower's max (3) to upper: {2,3}; upper (2 elements) is larger than lower (1), so move upper's min (2) to lower | {1,2} | {3} | none |
| 5 | `findMedian()` | lower has more elements, so return lower's root | {1,2} | {3} | 2.0 |

Exit condition: all five operations have been processed.

**Final answer:** the outputs are `null, null, 1.5, null, 2.0` for the add and query calls in order, matching the expected output `[null, null, null, 1.5, null, 2.0]`.

## 7. Complexity Summary

| Approach | `addNum` | `findMedian` | Space (auxiliary) | Notes |
|----------|----------|--------------|-------------------|-------|
| 1. Sorted list with binary-search insertion | O(n) | O(1) | O(n) | Shifting elements dominates; the whole order is kept unnecessarily |
| 2. Two heaps (max-heap and min-heap) | O(log n) | O(1) | O(n) | Optimal trade-off; only the boundary elements are exposed |

`n` is the number of elements added so far. Both approaches store all `n` values, and the output is a single `double` per query.

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|------|------------------|--------------------------|
| Exactly one element | The median is that element, and the upper heap is empty | The add routine ends with `lower` holding the element and `upper` empty; `findMedian` sees `lower` larger and returns it as a `double` |
| Two elements | Even count: the average of the two | One element ends up in each heap, so the roots are averaged with `/ 2.0` |
| Odd count versus even count | They need different formulas | The size relation `lower.size() == upper.size()` or `lower.size() == upper.size() + 1` decides which formula applies |
| Duplicates and all-same values | Equal values may sit on both sides of the boundary | The invariant uses "at most", not "strictly less", so equal values in either heap are valid |
| Streams in strictly ascending or descending order | A naive structure might degrade | Each add is still three heap operations, so it stays O(log n); the sorted-list approach would shift all elements on every descending insertion |
| Negative numbers and values at the limits (`-10^5`, `10^5`) | Signs and extremes might cause trouble | Only comparisons and one sum of two values (at most `2 * 10^5` in magnitude) are used, so there is no overflow |
| Query right after the first add | The problem guarantees at least one element | `peek()` on `lower` is never null at query time |
| Many interleaved operations (`5 * 10^4` calls) | Total cost must stay low | Each call is O(log n) or O(1), so the total is about `5 * 10^4 * log(5 * 10^4)` operations |

## 9. Java Notes

- **`PriorityQueue` is a min-heap by default.** For the max-heap use `new PriorityQueue<>(Collections.reverseOrder())`. Avoid the comparator `(a, b) -> b - a`: the subtraction can overflow when values are near the `int` limits. Here the range is small, but `Collections.reverseOrder()` (or `Integer.compare(b, a)`) is the safe habit.
- **`peek()` returns an `Integer`,** which is unboxed on return or in arithmetic. It returns `null` only if the heap is empty, which the problem's guarantee (at least one element) rules out at query time.
- **`(lower.peek() + upper.peek()) / 2.0`:** the sum of two `Integer`s is an `int`, and dividing by `2.0` makes the result a `double`. Dividing by `2` would truncate `1.5` to `1`.
- **`offer` and `poll` are O(log n); `peek` is O(1).** That is what gives O(log n) adds and O(1) queries.
- **Returning an `Integer` from a method declared `double`** (as `return lower.peek();` does) is allowed: Java unboxes the value and widens it to `double`.
- **Fields are `final`:** the heap references never change, only their contents.

## 10. Common Mistakes

- **Using integer division when averaging.** `(a + b) / 2` with `int` operands drops the `.5`, so a median of `1.5` becomes `1` silently. Fix: divide by `2.0`.
- **Not keeping the sizes balanced, or not enforcing the ordering between the heaps.** For example, adding each new number straight to `lower` or `upper` by parity, without moving elements across, can leave a large value in `lower` and a smaller one in `upper`, and the roots stop being the middle values. Fix: use the push-through routine (push to `lower`, move its max to `upper`, then rebalance).
- **Using the wrong heap orientation.** If `lower` is a min-heap, its root is the smallest element of the lower half rather than the largest, so the median candidate is not at the root. Fix: `lower` must be a max-heap and `upper` a min-heap.
- **Sorting the whole list on every `findMedian`.** It is correct but costs O(n log n) per query, which can time out on interleaved calls. Fix: keep the two heaps (or a sorted structure) updated incrementally.

## 11. Interview Takeaway

- **Trigger sentence to memorize:** "Running median of a stream → two heaps: a max-heap for the smaller half, a min-heap for the larger half, sizes balanced so the median is read from the roots."
- **How to explain it aloud in 90 seconds:** "I only need the middle of the sorted order, so I split the numbers into a lower half and an upper half. The lower half lives in a max-heap, so its root is the largest of the small numbers. The upper half lives in a min-heap, so its root is the smallest of the large numbers. I keep two invariants: everything in the lower heap is at most everything in the upper heap, and the lower heap has either the same number of elements as the upper or one more. To add a number, I push it into the lower heap, move the lower heap's largest element to the upper heap, and if the upper heap becomes bigger, I move its smallest back. That keeps both invariants whatever the new number is. The median is the lower root when the count is odd, and the average of the two roots when it's even, using floating-point division. Each add is a few heap operations, so O(log n), and each query is O(1)."
- **Follow-up, all values in `[0, 100]`:** keep a counting array of 101 buckets plus the total count. Adding is O(1). To find the median, walk the buckets, accumulating counts, until you reach the middle rank (or the two middle ranks); that is at most 101 steps, so it is constant time, and space is constant.
- **Follow-up, 99% of values in `[0, 100]`:** keep the same 101 buckets for the in-range values, and put the rare out-of-range values in two small side structures (for example, one for values below 0 and one for values above 100, each as a sorted structure or heap, along with their counts). To find the median, walk the order: outliers below the range, then the buckets, then outliers above. Because 99% of values are in the buckets, the middle rank almost always lands inside the bucket range, so the query is mostly a scan of at most 101 buckets and the side structures are only touched in the rare case that the middle rank falls in them.
- **Related problems using this pattern:**
  - LeetCode 480 — Sliding Window Median
  - LeetCode 703 — Kth Largest Element in a Stream
  - LeetCode 215 — Kth Largest Element in an Array
  - LeetCode 502 — IPO

## 12. Recall Questions

**Q:** Why is the lower half a max-heap and the upper half a min-heap?
**A:** The median depends on the largest element of the lower half and the smallest element of the upper half, and a heap exposes its extreme element at the root, so each half's heap type is chosen to put exactly that boundary element at the top.

**Q:** What two invariants do the heaps maintain?
**A:** Every element in the lower heap is at most every element in the upper heap, and the lower heap has the same size as the upper heap or one more.

**Q:** Why does pushing the new number into the lower heap and then moving the lower heap's maximum to the upper heap preserve the ordering?
**A:** The element that moves is the largest of the lower candidates, so everything left in the lower heap is at most it, and everything already in the upper heap was at least as large as the old lower heap's elements; the new number either moved across (if it was the largest) or stayed below the old lower maximum.

**Q:** How is the median computed from the heaps?
**A:** If the lower heap has more elements, the median is its root; if the heaps have equal sizes, it is the average of the two roots, using `2.0` so the division is floating-point.

**Q:** What are the time costs of the operations, and why?
**A:** `addNum` is O(log n) because it does a constant number of heap `offer` and `poll` operations, and `findMedian` is O(1) because it only reads the roots.

**Q:** How would you optimize if every value is known to be in `[0, 100]`?
**A:** Use a counting array of 101 buckets and a total count. Insertion is O(1), and the median is found by scanning at most 101 buckets, which is constant time.

## 13. Final Code

```java
class MedianFinder {
    private final PriorityQueue<Integer> lower = new PriorityQueue<>(Collections.reverseOrder());
    private final PriorityQueue<Integer> upper = new PriorityQueue<>();

    public void addNum(int num) {
        lower.offer(num);
        upper.offer(lower.poll());
        if (upper.size() > lower.size()) {
            lower.offer(upper.poll());
        }
    }

    public double findMedian() {
        if (lower.size() > upper.size()) {
            return lower.peek();
        }
        return (lower.peek() + upper.peek()) / 2.0;
    }
}
```

## 14. Self-Test

You must support two operations on a growing stream of integers: add a number, and report the current median. Both must be fast even when interleaved many times. Think about which two groups the numbers can be split into so the median sits at their boundary, which heap type each group needs, and how to keep the groups ordered and nearly equal in size.
