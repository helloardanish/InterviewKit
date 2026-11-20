---
title: "0148 — Sort List"
layout: default
parent: Divide-and-Conquer
grand_parent: Leetcode
nav_order: 148
permalink: /16-Divide-and-Conquer/0148-sort-list/

problem: 148 - Sort List
url: "https://leetcode.com/problems/sort-list/"
difficulty: Medium
section: Linked List
patterns: [merge-sort, slow-fast-pointers, dummy-head]
data_structures: [linked-list]
time: O(n log n)
space: O(log n)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

**# 0148 — Sort List**

| Field | Value |
|---|---|
| **Pattern** | Merge Sort + Slow-Fast Pointers |
| **Difficulty** | Medium |
| **Section** | Linked List |
| **Time** | O(n log n) |
| **Space** | O(log n) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Amazon, Google, Meta, Microsoft, Bloomberg |
| **LeetCode** | [Sort List](https://leetcode.com/problems/sort-list/) |


## 1. Problem in Simple Words

**Given:** the head of a singly linked list.

**Required:** return the head of the list sorted in ascending order.

**Constraints that matter:**
- `0 <= nodes <= 5 * 10^4` — an O(n²) sort (insertion sort) is about 2.5 billion steps in the worst case, too slow. O(n log n) is the target. The empty list is allowed and must return `null`.
- `-10^5 <= Node.val <= 10^5` — plain `int` values, no overflow risk, negatives are allowed.
- **Follow-up: sort in O(n log n) time and O(1) memory.** This is a hint about the intended technique: the usual O(n log n) sorts need either random access (quicksort, heapsort) or an auxiliary array (merge sort on arrays). A linked list needs neither for merge sort, since merging is just relinking nodes.

## 2. Recognition Signals

- "Sort a linked list" plus an O(n log n) requirement. Arrays can be sorted by index tricks; a singly linked list has no random access, which rules out quicksort partitioning by index and heapsort.
- Splitting a list in half is cheap with slow/fast pointers, and merging two sorted lists is a pointer-relinking loop (see Merge Two Sorted Lists). Both pieces of merge sort are natural on a list.
- The O(1)-memory follow-up points to **bottom-up (iterative) merge sort**, which removes the recursion stack.
- Pattern: **merge sort on a linked list: split with slow/fast pointers, merge with a dummy head.**

## 3. Core Idea

Merge sort: split the list into two halves, sort each half recursively, then merge the two sorted halves into one sorted list. The base case is a list with zero or one node, which is already sorted.

**Why it's correct:** the merge step takes two sorted lists and repeatedly appends the smaller head, so the output is sorted and contains exactly the nodes of both inputs. By induction on length, if both halves come back sorted, the merge produces a sorted whole. Each level of recursion touches every node once and there are about log2(n) levels, giving O(n log n).

**Invariant (merge):** the already-built output is sorted, and every node still in `a` or `b` is greater than or equal to the last node appended from that same list, so taking the smaller of the two heads is always safe.

## 4. Approach 1 — Brute Force

Copy the values into an `int[]`, sort the array, and write the values back into the nodes in order.

```java
public ListNode sortListBruteForce(ListNode head) {
    int length = 0;
    for (ListNode node = head; node != null; node = node.next) {
        length++;
    }

    int[] values = new int[length];
    int i = 0;
    for (ListNode node = head; node != null; node = node.next) {
        values[i++] = node.val;
    }

    Arrays.sort(values);

    i = 0;
    for (ListNode node = head; node != null; node = node.next) {
        node.val = values[i++];
    }
    return head;
}
```

**Time:** O(n log n).
**Space:** O(n) for the array.

Not optimal for the follow-up: it uses O(n) extra memory and rewrites node values instead of working with the list structure. It is a fine fallback when the follow-up does not matter, but it does not show the linked-list technique the problem is testing.

## 5. Approach 2 — Optimized

### 5a. Top-down merge sort (cleanest)

1. If `head` is `null` or has one node, return it.
2. Find the middle with slow/fast pointers, starting `fast` at `head.next`. Cut the list after `slow`, producing two non-empty halves.
3. Recursively sort both halves.
4. Merge them with a dummy head, taking from the left list on ties (`<=`).

```java
public ListNode sortList(ListNode head) {
    if (head == null || head.next == null) {
        return head;
    }

    ListNode secondHalf = splitAtMiddle(head);
    ListNode left = sortList(head);
    ListNode right = sortList(secondHalf);
    return merge(left, right);
}

private ListNode splitAtMiddle(ListNode head) {
    ListNode slow = head;
    ListNode fast = head.next;
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
    }
    ListNode secondHalf = slow.next;
    slow.next = null;
    return secondHalf;
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

**Time:** O(n log n). Each level of recursion splits and merges all n nodes in total, and the recursion is about log2(n) levels deep.
**Space:** O(log n) for the recursion stack (about 16 frames for 5 * 10^4 nodes); the merge itself only relinks existing nodes.

### 5b. Bottom-up merge sort (meets the O(1) follow-up)

Instead of recursing, merge runs of length 1, then 2, then 4, and so on, until the run length reaches the list length. Each pass walks the list once, cutting off two runs at a time and merging them back in place.

```java
public ListNode sortListBottomUp(ListNode head) {
    if (head == null || head.next == null) {
        return head;
    }

    int length = 0;
    for (ListNode node = head; node != null; node = node.next) {
        length++;
    }

    ListNode dummy = new ListNode(0, head);

    for (int size = 1; size < length; size *= 2) {
        ListNode prev = dummy;
        ListNode current = dummy.next;

        while (current != null) {
            ListNode left = current;
            ListNode right = split(left, size);
            current = split(right, size);

            prev.next = merge(left, right);
            while (prev.next != null) {
                prev = prev.next;
            }
        }
    }
    return dummy.next;
}

// Keeps the first `size` nodes, cuts them off, and returns the head of the rest (or null).
private ListNode split(ListNode head, int size) {
    for (int i = 1; head != null && i < size; i++) {
        head = head.next;
    }
    if (head == null) {
        return null;
    }
    ListNode rest = head.next;
    head.next = null;
    return rest;
}
```

**Time:** O(n log n): about log2(n) passes, each O(n).
**Space:** O(1) auxiliary, with no recursion stack.

Both are optimal in time, since comparison sorting needs Ω(n log n). Top-down is shorter and easier to write correctly under interview pressure, and its O(log n) stack is usually acceptable. Bottom-up is the answer when the O(1) memory follow-up is demanded strictly.

## 6. Dry Run

Top-down on Example 2, `head = [-1,5,3,4,0]` (an odd length, which exercises the uneven split).

| Call | Split | Result of sorting |
|---|---|---|
| sortList([-1,5,3,4,0]) | [-1,5,3] and [4,0] (first half gets the extra node) | merge of the two sorted halves |
| sortList([-1,5,3]) | [-1,5] and [3] | [-1,3,5] |
| sortList([-1,5]) | [-1] and [5] | merge gives [-1,5] |
| sortList([4,0]) | [4] and [0] | merge gives [0,4] |

Final merge of `[-1,3,5]` and `[0,4]`:

| Head of a | Head of b | Taken | Output so far |
|---|---|---|---|
| -1 | 0 | -1 | [-1] |
| 3 | 0 | 0 | [-1,0] |
| 3 | 4 | 3 | [-1,0,3] |
| 5 | 4 | 4 | [-1,0,3,4] |
| 5 | (exhausted) | append rest | [-1,0,3,4,5] |

Exit condition: one list runs out, then the remaining nodes of the other are attached. Final answer: `[-1,0,3,4,5]`.

## 7. Complexity Summary

| Approach | Time | Space | Notes |
|---|---|---|---|
| Copy to array, sort, write back | O(n log n) | O(n) | Fails the O(1) memory follow-up; rewrites node values |
| Top-down merge sort | O(n log n) | O(log n) | Recursion stack only; merge relinks nodes |
| Bottom-up merge sort | O(n log n) | O(1) | Iterative; satisfies the follow-up |

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|---|---|---|
| Empty list (`null`) | Must not dereference `head.next` | First check returns `head` (`null`) |
| Single node | Already sorted | Base case returns it unchanged |
| Two nodes | A bad middle-finder returns no second half and recurses forever | `fast` starts at `head.next`, so the split always yields two non-empty halves |
| Odd length | Halves are uneven | First half gets the extra node; both halves still strictly smaller than the whole, so recursion terminates |
| Already sorted or reverse sorted | No special structure to exploit; must still be O(n log n) | Merge sort splits evenly regardless of input order |
| All duplicates | Ties must not break the merge or lose nodes | `<=` takes from the left list on ties, which also keeps the sort stable |
| Negative values | Nothing special, but a sentinel like `0` in the dummy would be wrong if used for comparison | The dummy's value is never compared; only `tail.next` is used |

## 9. Java Notes

- `new ListNode(0)` as the dummy: its value is never compared, only its `next` pointer is used. The two-argument constructor `new ListNode(0, head)` is used for the bottom-up dummy.
- Start `fast` at `head.next`, not `head`. With `fast = head`, a two-node list gives `slow` at the second node, `slow.next == null`, no split happens, and `sortList` recurses on the same list forever.
- Cutting the list (`slow.next = null`) is required before recursing, otherwise the "first half" still runs into the second half and the recursion never shrinks.
- Recursion depth is about log2(n) (roughly 16 for 5 * 10^4 nodes), so there is no stack-overflow concern, unlike recursive tree traversals on skewed input.
- In the bottom-up loop, `size *= 2` cannot overflow `int` here because `size < length <= 5 * 10^4`.
- `Arrays.sort(int[])` is used only in the brute force; it sorts primitives with a dual-pivot quicksort.

## 10. Common Mistakes

- Starting `fast = head` in the middle finder, causing infinite recursion on two-node lists. Fix: `fast = head.next`.
- Forgetting `slow.next = null`, so the halves overlap and nodes are duplicated or the recursion never ends. Fix: detach the second half before recursing.
- In `merge`, forgetting to attach the leftover list after one side runs out, silently dropping nodes. Fix: `tail.next = (a != null) ? a : b`.
- Reaching for quicksort or heapsort that assume indexing, or sorting by repeatedly inserting into a sorted prefix (O(n²)), which times out at 5 * 10^4 nodes. Fix: use merge sort.
- Claiming O(1) space for the array-copy solution or for the recursive version. Fix: the array copy is O(n) and the recursion is O(log n); only the bottom-up version is O(1).
- In bottom-up, not advancing `prev` to the tail of each merged run (or not detaching runs with `split`), which corrupts the links between runs. Fix: after each merge, walk `prev` to the end of the merged run.

## 11. Interview Takeaway

- **Trigger sentence:** "Sorting a linked list in O(n log n) means merge sort: split with slow/fast pointers, merge by relinking, and go bottom-up for O(1) space."
- **90-second explanation:** A linked list has no random access, so quicksort and heapsort are awkward, but merge sort fits naturally. I split the list at its middle using slow and fast pointers, starting fast one node ahead so even a two-node list splits into two non-empty halves, and I cut the first half's tail so the halves are separate lists. I recursively sort each half, then merge them with a dummy head, always taking the smaller head and attaching whatever remains when one list runs out. Each level handles all n nodes and there are log n levels, so it is O(n log n) time with only an O(log n) recursion stack, since merging just relinks nodes. If the interviewer pushes on the O(1) memory follow-up, I switch to bottom-up merge sort: measure the length, then repeatedly merge adjacent runs of size 1, 2, 4, and so on, using a helper that cuts a run off the front of the list, which removes the recursion entirely.
- **Related problems:** 21 (Merge Two Sorted Lists), 147 (Insertion Sort List), 876 (Middle of the Linked List), 23 (Merge k Sorted Lists), 912 (Sort an Array).

## 12. Recall Questions

**Q:** Why is merge sort the natural choice for a linked list?
**A:** Splitting needs only pointer walking and merging only relinks existing nodes, so no auxiliary array is needed. Quicksort and heapsort rely on random access, which a singly linked list does not have.

**Q:** Why must `fast` start at `head.next` when finding the middle?
**A:** It guarantees that a two-node list splits into one node and one node. Starting at `head` leaves the second half empty and the recursion never shrinks.

**Q:** Why is `slow.next = null` required?
**A:** Without cutting, the first half still links into the second half, so it is not a separate list and recursion on it never reaches the base case.

**Q:** Where does the O(log n) space come from, and how does bottom-up remove it?
**A:** It is the recursion stack of depth about log2(n). Bottom-up merges runs of doubling size iteratively, so it needs only a few pointers.

**Q:** Why does the merge take from the left list when values are equal?
**A:** It keeps equal elements in their original relative order, which makes the sort stable. For plain integers stability does not change the output, but it matters if nodes carry extra data.

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
    public ListNode sortList(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }

        ListNode secondHalf = splitAtMiddle(head);
        ListNode left = sortList(head);
        ListNode right = sortList(secondHalf);
        return merge(left, right);
    }

    private ListNode splitAtMiddle(ListNode head) {
        ListNode slow = head;
        ListNode fast = head.next;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        ListNode secondHalf = slow.next;
        slow.next = null;
        return secondHalf;
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
}
```

## 14. Self-Test

Given the head of a singly linked list with up to 50,000 integer values, return it sorted ascending in O(n log n) time (ideally O(1) extra memory). Re-derive: merge sort. Return when there are 0 or 1 nodes; find the middle with slow/fast pointers starting `fast` at `head.next`, cut the list after `slow`, sort both halves recursively, and merge them with a dummy head taking the smaller head each time and attaching the leftover list. For O(1) space, do the same iteratively by merging runs of size 1, 2, 4, and so on.
