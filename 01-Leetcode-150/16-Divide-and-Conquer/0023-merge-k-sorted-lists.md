---
title: "0023 — Merge k Sorted Lists"
layout: default
parent: Divide-and-Conquer
grand_parent: Leetcode
nav_order: 23
permalink: /16-Divide-and-Conquer/0023-merge-k-sorted-lists/

problem: 23 - Merge k Sorted Lists
url: "https://leetcode.com/problems/merge-k-sorted-lists/"
difficulty: Hard
section: Linked List / Heap
patterns: [k-way-merge, min-heap, divide-and-conquer]
data_structures: [linked-list, heap]
time: O(N log k)
space: O(k)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0023 — Merge k Sorted Lists**

| Field | Value |
|---|---|
| **Pattern** | K-Way Merge + Min Heap (Priority Queue) |
| **Difficulty** | Hard |
| **Section** | Linked List / Heap |
| **Time** | O(N log k) |
| **Space** | O(k) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Meta, Microsoft, Bloomberg |
| **LeetCode** | [Merge k Sorted Lists](https://leetcode.com/problems/merge-k-sorted-lists/) |


## 1. Problem in Simple Words

**Given:** an array `lists` of `k` linked lists, each already sorted in ascending order.

**Required:** merge them all into one sorted linked list and return its head. Below, `N` is the total number of nodes across all lists.

**Constraints that matter:**
- `0 <= k <= 10^4` and `0 <= lists[i].length <= 500`, with the sum of all lengths at most `10^4` — `k` can be zero (an empty array) and individual lists can be empty (`null` heads), so `null` handling is not optional. Since total nodes `N <= 10^4`, even large `k` means most lists are tiny or empty.
- `-10^4 <= value <= 10^4` — small integers; no overflow concerns in comparisons.
- Each input list is sorted — that is the property to exploit: at any moment, the smallest remaining node overall is the head of one of the `k` lists, so only `k` candidates ever need to be compared.
- The naive idea of merging lists into one running result repeatedly re-walks the early nodes, which is the inefficiency this problem tests for.

## 2. Recognition Signals

- "Merge `k` sorted sequences into one" is the textbook **k-way merge**.
- "Each input is sorted, and I need the next smallest across all of them" means the answer is always among the `k` current heads: a **min-heap of heads**.
- Merging two sorted lists is a known building block, so merging `k` can be organized as a **tree of pairwise merges** (divide and conquer).
- Pattern: **min-heap of the current heads (k-way merge)**, or equivalently **pairwise merging in `log k` rounds**.

## 3. Core Idea

Keep a min-heap containing the current head node of each non-empty list. Repeatedly remove the smallest node, append it to the output, and insert that node's successor (if any) into the heap. The heap always holds at most one node per list, so each push and pop costs `O(log k)`.

**Why it's correct:** because each list is sorted, the next smallest unmerged value overall must be the smallest of the `k` current heads. After taking it, the only new candidate is its successor in the same list, which is exactly what we insert. Each node is therefore output exactly once, in nondecreasing order.

**Invariant:** the heap contains exactly the first not-yet-output node of every list that still has nodes, and everything already appended to the output is less than or equal to everything still in any list.

## 4. Approach 1 — Brute Force

Merge the lists one after another: merge `lists[0]` with `lists[1]`, merge that result with `lists[2]`, and so on, using the standard two-list merge.

```java
public ListNode mergeKListsSequential(ListNode[] lists) {
    ListNode result = null;
    for (ListNode list : lists) {
        result = merge(result, list);
    }
    return result;
}

private ListNode merge(ListNode a, ListNode b) {
    ListNode dummy = new ListNode(0);
    ListNode tail = dummy;

    while (a != null && b != null) {
        if (a.val <= b.val) {
            tail.next = a;
            a = a.next;
        } else {
            tail.next = b;
            b = b.next;
        }
        tail = tail.next;
    }

    tail.next = (a != null) ? a : b;
    return dummy.next;
}
```

**Time:** O(k · N) in the worst case. The accumulated result grows with every merge, and the nodes of the earliest lists are walked again in up to `k - 1` later merges.
**Space:** O(1) auxiliary; nodes are relinked.

Not optimal: the running result is re-traversed over and over. The lists should be combined so that no node is walked more than about `log k` times, or compared against only `k` candidates at a time.

## 5. Approach 2 — Optimized

### 5a. Min-heap of list heads (k-way merge)

1. Create a `PriorityQueue<ListNode>` ordered by `val`.
2. Offer the head of every non-empty list.
3. While the heap is not empty: poll the smallest node, append it to the output with a dummy head and tail, and if it has a `next`, offer that.
4. Return `dummy.next`.

```java
public ListNode mergeKLists(ListNode[] lists) {
    PriorityQueue<ListNode> heap =
        new PriorityQueue<>((a, b) -> Integer.compare(a.val, b.val));

    for (ListNode head : lists) {
        if (head != null) {
            heap.offer(head);
        }
    }

    ListNode dummy = new ListNode(0);
    ListNode tail = dummy;

    while (!heap.isEmpty()) {
        ListNode smallest = heap.poll();
        tail.next = smallest;
        tail = smallest;

        if (smallest.next != null) {
            heap.offer(smallest.next);
        }
    }

    return dummy.next;
}
```

**Time:** O(N log k). Each of the `N` nodes is offered once and polled once, each in `O(log k)` because the heap never holds more than `k` nodes.
**Space:** O(k) for the heap; the output reuses the input nodes.

### 5b. Divide and conquer (pairwise merging)

Merge lists in pairs, then merge the results in pairs, doubling the gap each round until one list remains. There are `⌈log2 k⌉` rounds and every round touches each node at most once.

```java
public ListNode mergeKListsDivideAndConquer(ListNode[] lists) {
    if (lists.length == 0) {
        return null;
    }
    for (int step = 1; step < lists.length; step *= 2) {
        for (int i = 0; i + step < lists.length; i += 2 * step) {
            lists[i] = merge(lists[i], lists[i + step]);
        }
    }
    return lists[0];
}
```

**Time:** O(N log k).
**Space:** O(1) auxiliary (iterative, reusing the `merge` helper from Approach 1). It overwrites entries of the input array.

Both are optimal in time. The heap is the general k-way merge tool: it works when lists arrive as streams, or when `k` is unknown in advance, and it is the shorter code to write. Divide and conquer needs no extra data structure and uses O(1) extra memory, which is preferable if you already have a `merge` helper and want to avoid heap overhead.

## 6. Dry Run

Example 1: `lists = [[1,4,5],[1,3,4],[2,6]]`, using the heap. Letters A, B, C label the three lists; heap contents are shown as value-and-list.

| Step | Heap before | Polled | Offered | Output so far |
|---|---|---|---|---|
| 0 | (build) | none | 1A, 1B, 2C | [] |
| 1 | 1A, 1B, 2C | 1A | 4A | [1] |
| 2 | 1B, 2C, 4A | 1B | 3B | [1,1] |
| 3 | 2C, 3B, 4A | 2C | 6C | [1,1,2] |
| 4 | 3B, 4A, 6C | 3B | 4B | [1,1,2,3] |
| 5 | 4A, 4B, 6C | 4A | 5A | [1,1,2,3,4] |
| 6 | 4B, 5A, 6C | 4B | (none, B ended) | [1,1,2,3,4,4] |
| 7 | 5A, 6C | 5A | (none) | [1,1,2,3,4,4,5] |
| 8 | 6C | 6C | (none) | [1,1,2,3,4,4,5,6] |

Exit condition: the heap is empty. Final answer: `[1,1,2,3,4,4,5,6]`. The tie at steps 1 and 5 (equal values) may be broken either way without changing the values in the output.

## 7. Complexity Summary

| Approach | Time | Space | Notes |
|---|---|---|---|
| Sequential merge into a running result | O(k · N) | O(1) aux | Early nodes are re-walked up to k times |
| Min-heap of heads | O(N log k) | O(k) | Streaming-friendly k-way merge |
| Divide and conquer pairwise merge | O(N log k) | O(1) aux iterative | Mutates the input array; reuses `merge` |

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|---|---|---|
| `lists` is empty (`k = 0`) | There is nothing to offer or merge | Heap stays empty and `dummy.next` is `null`; the D&C version returns `null` explicitly |
| `lists = [null]` or all lists empty | Offering a `null` into a `PriorityQueue` throws `NullPointerException` | Heads are null-checked before `offer`; result is `null` |
| A single non-empty list | No real merging | The heap hands back its nodes in order; D&C returns `lists[0]` untouched |
| Some lists empty among non-empty ones | Mixed nulls | Nulls are skipped when building the heap and handled by `merge` in D&C |
| Duplicate values across lists | Ties in the comparator | Either order is valid for integers; output values are identical |
| Negative values | Sentinel mistakes (starting a min search at `0`) | The heap compares real node values; nothing assumes non-negative |
| Large `k` with tiny lists (up to 10^4 lists) | Many empty lists inflate `k` | Heap holds only non-empty heads; D&C does about 14 rounds |

## 9. Java Notes

- `ListNode` does not implement `Comparable`, so `PriorityQueue<ListNode>` **requires** a comparator; without it the first insertion beyond one element throws `ClassCastException`.
- Use `Integer.compare(a.val, b.val)` instead of `a.val - b.val`. Subtraction is safe for these small values, but it overflows for wide ranges and is a habit worth avoiding.
- `PriorityQueue` is a min-heap by default and does not accept `null` elements, hence the null checks.
- The heap stores nodes, not just values, so after polling you can follow `.next` to find the successor to offer. A heap of `Integer` values would lose that link.
- Appending with `tail.next = smallest; tail = smallest;` leaves `smallest.next` pointing into its old list temporarily; it is overwritten on the next append, and the final node's `next` is already `null`, so no cycle forms.
- `step *= 2` in the D&C loop stays far below `int` limits for `k <= 10^4`.

## 10. Common Mistakes

- Offering `null` heads to the `PriorityQueue`, causing a `NullPointerException` on inputs like `[[]]` or `[null]`. Fix: guard with `if (head != null)`.
- Omitting the comparator, which fails at runtime with `ClassCastException`. Fix: supply `(a, b) -> Integer.compare(a.val, b.val)`.
- Pushing every node of every list into the heap up front. That is correct but uses O(N) space and does O(N log N) work, missing the point that only `k` heads need to be compared. Fix: keep one node per list in the heap.
- Forgetting to offer `smallest.next` after polling, so each list contributes only its first node. Fix: after appending, offer the successor if it exists.
- Merging sequentially for large `k`, which is O(k · N). Fix: use the heap or pairwise merging.
- In the D&C loop, using bounds like `i < lists.length` instead of `i + step < lists.length`, which indexes past the array. Fix: only merge when the partner index exists.

## 11. Interview Takeaway

- **Trigger sentence:** "Merging `k` sorted sequences means a min-heap of the current heads (or pairwise merges in `log k` rounds), both O(N log k)."
- **90-second explanation:** Since every list is already sorted, the next node in the merged output is always the smallest of the `k` current heads. So I keep a min-heap with one node per non-empty list, ordered by value. I repeatedly poll the smallest node, append it to the result through a dummy head, and push that node's successor if it has one. The heap never holds more than `k` nodes, so each of the `N` push and pop operations costs `O(log k)`, giving `O(N log k)` time and `O(k)` extra space, with nodes simply relinked rather than copied. I make sure to skip null heads because a priority queue rejects nulls, and to give it a comparator because `ListNode` isn't comparable. The alternative is divide and conquer: merge lists in pairs using the two-list merge, then merge those results, for `log k` rounds. That is also `O(N log k)` and needs no heap, while merging them one at a time into a growing result would be `O(k · N)`.
- **Related problems:** 21 (Merge Two Sorted Lists), 148 (Sort List), 378 (Kth Smallest Element in a Sorted Matrix), 373 (Find K Pairs with Smallest Sums).

## 12. Recall Questions

**Q:** Why does the heap only ever need to hold `k` nodes?
**A:** Each input list is sorted, so the next output node must be the head of some list. Only the current head of each list is a candidate, and a node's successor becomes a candidate only after it is output.

**Q:** Why is merging the lists one after another O(k · N) rather than O(N)?
**A:** The accumulated result grows with each merge, so nodes from the earliest lists are traversed again in many later merges, up to about `k` times.

**Q:** Why does divide and conquer take `O(N log k)`?
**A:** There are `⌈log2 k⌉` rounds, and each round merges lists whose lengths add up to at most `N`, so every round does at most `O(N)` work.

**Q:** Why must the comparator be supplied for `PriorityQueue<ListNode>`?
**A:** `ListNode` has no natural ordering, so the queue cannot compare elements without a comparator and throws `ClassCastException`.

**Q:** When is the heap approach preferable to divide and conquer?
**A:** When the lists arrive incrementally or `k` is not known in advance, since the heap only needs the current heads. It is also shorter to write from scratch.

## 13. Final Code

```java
import java.util.*;

/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode() {}
 *     ListNode(int val) { this.val = val; }
 *     ListNode(int val, ListNode next) { this.val = val; this.next = next; }
 * }
 */
class Solution {
    public ListNode mergeKLists(ListNode[] lists) {
        PriorityQueue<ListNode> heap =
            new PriorityQueue<>((a, b) -> Integer.compare(a.val, b.val));

        for (ListNode head : lists) {
            if (head != null) {
                heap.offer(head);
            }
        }

        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;

        while (!heap.isEmpty()) {
            ListNode smallest = heap.poll();
            tail.next = smallest;
            tail = smallest;

            if (smallest.next != null) {
                heap.offer(smallest.next);
            }
        }

        return dummy.next;
    }
}
```

## 14. Self-Test

Given an array of `k` sorted linked lists (possibly empty, possibly `k = 0`), merge them into one sorted linked list. Re-derive: put the head of every non-empty list into a min-heap ordered by value; repeatedly poll the smallest node, append it to the output after a dummy head, and offer its `next` if present; return the dummy's `next`. The alternative is pairwise merging with a two-list merge in `log k` rounds.
