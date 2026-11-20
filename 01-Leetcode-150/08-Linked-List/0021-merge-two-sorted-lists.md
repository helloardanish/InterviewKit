---
title: "0021 — Merge Two Sorted Lists"
layout: default
parent: Linked List
grand_parent: Leetcode
nav_order: 21
permalink: /08-Linked-List/0021-merge-two-sorted-lists/

problem: 21 - Merge Two Sorted Lists
url: https://leetcode.com/problems/merge-two-sorted-lists/
difficulty: Easy
section: Linked List / Two Pointers
patterns: [dummy-head, splice-merge]
data_structures: [linked-list]
time: O(m+n)
space: O(1)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0021 — Merge Two Sorted Lists

| Field | Value |
|---|---|
| **Pattern** | Dummy Head with Splice-Merge |
| **Difficulty** | Easy |
| **Section** | Linked List / Two Pointers |
| **Time** | O(m + n) |
| **Space** | O(1) — reuses existing nodes |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Meta |
| **LeetCode** | [Merge Two Sorted Lists](https://leetcode.com/problems/merge-two-sorted-lists/) |

## 1. Problem in Simple Words

**Given:** The heads of two sorted (non-decreasing) linked lists, `list1` and `list2`.

**Required:** Merge them into one sorted list by **splicing together the existing nodes** (not creating new nodes with copied values), and return the head of the merged list.

**Constraints that actually matter:**
- `0 <= number of nodes in both lists <= 50` — tiny; performance is a complete non-issue. Importantly, **either or both lists can be empty** — the algorithm must correctly handle this without special-casing beyond what naturally falls out of the main logic.
- **"The list should be made by splicing together the nodes of the first two lists"** — this explicitly means the solution should reuse the existing node objects, rearranging their `next` pointers to interleave them in sorted order, rather than creating brand-new nodes and copying values over (though the latter would still produce a correct-*looking* result, it doesn't follow the problem's stated approach, and is also simply more work than necessary).
- **Both lists are already sorted in non-decreasing order** — this is what makes a simple, single-pass two-pointer merge possible: at any point, the smallest not-yet-used node is guaranteed to be at the front of one of the two remaining list portions, so comparing just the two current front nodes is always sufficient to pick the next node for the merged result.

## 2. Recognition Signals

- "Merge two sorted lists into one sorted list" → the classic **merge step of merge sort**, applied directly to linked lists rather than arrays — compare the current front elements of both lists, take the smaller one, and advance that list's pointer, repeating until one list is exhausted, then attach whatever remains of the other list wholesale.
- The explicit **"splice together the nodes"** instruction is itself a signal toward in-place pointer manipulation (rewiring `next` references of the existing nodes) rather than constructing an entirely new list from scratch — this is both more space-efficient and matches the problem's stated intent.
- Whenever building a new list (or merged structure) node by node, where the final list's length and composition isn't known in advance — using a **dummy head node** (a placeholder whose `next` eventually points to the true first node of the result) is the standard technique that simplifies attaching the very first real node, avoiding special-case logic for it.

**Pattern:** Two-Pointer Splice Merge with Dummy Head (compare the current front nodes of both sorted lists, splice the smaller one onto the result via pointer rewiring, advance that list's pointer, and repeat until one list is exhausted, then attach the remainder of the other list directly).

## 3. Core Idea

Since both input lists are already sorted, the smallest value not yet placed into the merged result must be at the front of one of the two remaining list portions (whichever hasn't been fully consumed). So, use two pointers, one into each list, and a separate "tail" pointer for the merged result being built (starting from a dummy head). At each step, compare the values at the two current pointers; splice the smaller one onto the end of the merged result (by setting the result tail's `next` to point directly at that node — no new node is created), and advance that specific list's pointer to its own next node. Repeat until one of the two input pointers becomes `null`. At that point, the other list's remaining nodes are already known to all be `>=` everything placed so far (since both inputs are individually sorted), so the entire remainder can simply be attached directly as the final `next` link, with no further comparisons needed.

**Invariant:** At any point during the merge, the result list built so far (from the dummy head's `next` through the current tail) contains exactly the smallest `k` nodes (for whatever `k` have been placed so far) from the combined pool of both input lists, correctly sorted — and the two input pointers always point to the smallest not-yet-placed node from each respective original list, so comparing just these two candidates is always sufficient to correctly determine the next node to place.

## 4. Approach 1 — Recursive Splice Merge

**Logic:** Recursively compare the heads of both lists; whichever is smaller becomes the head of the merged result, with its `next` pointer set to the result of recursively merging the rest of that list with the entirety of the other list.

```java
public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
    if (list1 == null) {
        return list2;
    }
    if (list2 == null) {
        return list1;
    }

    if (list1.val <= list2.val) {
        list1.next = mergeTwoLists(list1.next, list2);
        return list1;
    } else {
        list2.next = mergeTwoLists(list1, list2.next);
        return list2;
    }
}
```

- **Time:** O(m + n) — each node from both lists is visited and spliced exactly once across the full recursion.
- **Space:** O(m + n) auxiliary for the recursion call stack in the worst case (each recursive call adds a stack frame until one list is exhausted), since Java doesn't guarantee tail-call optimization.

**Why it is presented as a valid but less space-efficient alternative:** This recursive approach is elegant and concise, directly mirroring the problem's recursive structure ("the merge of two lists starting with the smaller head, followed by the merge of the rest"), and is fully correct with the same O(m+n) time complexity as the iterative version. However, it uses O(m+n) auxiliary stack space in the worst case (e.g., one list being entirely smaller than the other, requiring a full chain of recursive calls before any base case is hit), whereas the iterative version achieves the same result with O(1) auxiliary space.

## 5. Approach 2 — Optimized (Iterative Splice Merge with Dummy Head)

**Algorithm:**
1. Create a `dummy` head node (value irrelevant) and a `tail` pointer initialized to `dummy`.
2. While both `list1` and `list2` are non-null:
   - If `list1.val <= list2.val`, splice `list1` onto the result: `tail.next = list1`, then advance `list1 = list1.next`.
   - Otherwise, splice `list2` onto the result: `tail.next = list2`, then advance `list2 = list2.next`.
   - Advance `tail = tail.next` (move the result's tail pointer to the just-spliced node).
3. Once the loop ends (one list is exhausted), attach whichever list still has remaining nodes directly: `tail.next = (list1 != null) ? list1 : list2`.
4. Return `dummy.next` (the true head of the merged list).

```java
public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
    ListNode dummy = new ListNode(0);
    ListNode tail = dummy;

    while (list1 != null && list2 != null) {
        if (list1.val <= list2.val) {
            tail.next = list1;
            list1 = list1.next;
        } else {
            tail.next = list2;
            list2 = list2.next;
        }
        tail = tail.next;
    }

    tail.next = (list1 != null) ? list1 : list2;

    return dummy.next;
}
```

- **Time:** O(m + n) — each node from both lists is visited and spliced exactly once, either during the main comparison loop or as part of the final wholesale attachment of the remaining list.
- **Space:** O(1) auxiliary — only the `dummy`, `tail`, and the two input pointers; no new nodes are created and no recursion stack is used, since all existing nodes are simply re-linked in place.

**Why this is optimal:** Every node from both input lists must be examined at least once to determine its correct position in the merged result, so O(m+n) time is a hard lower bound, matching both approaches. Space is O(1) because the iterative version avoids both the unnecessary node-copying that a naive "build new nodes" approach would require, and the O(m+n) recursion stack that Approach 1 incurs — directly fulfilling the problem's explicit "splice together the nodes" instruction with the most space-efficient implementation of that idea.

## 6. Dry Run

Example: `list1 = [1,2,4]`, `list2 = [1,3,4]`.
Chosen because it's the canonical example, and it includes a tie (`1 == 1`) exercising the `<=` comparison's tie-breaking behavior, plus a final wholesale-attachment step.

Initial: `dummy` created, `tail = dummy`.

| Step | list1 (current) | list2 (current) | list1.val <= list2.val? | Action | tail after |
|------|------------------------|------------------------|--------------------------------|--------|----------------|
| 1 | 1 | 1 | Yes (1<=1) | tail.next=list1(1); list1=list1.next(2) | node(1) [from list1] |
| 2 | 2 | 1 | No (2<=1 false) | tail.next=list2(1); list2=list2.next(3) | node(1) [from list2] |
| 3 | 2 | 3 | Yes (2<=3) | tail.next=list1(2); list1=list1.next(4) | node(2) |
| 4 | 4 | 3 | No (4<=3 false) | tail.next=list2(3); list2=list2.next(4) | node(3) |
| 5 | 4 | 4 | Yes (4<=4) | tail.next=list1(4); list1=list1.next(null) | node(4) [from list1] |

Now `list1 == null`, loop condition `list1 != null && list2 != null` is false, loop ends.

**Final wholesale attachment:** `tail.next = (list1 != null) ? list1 : list2` → `list1` is null, so `tail.next = list2` (which currently points to the remaining node with value 4).

Exit condition: loop ends when `list1` becomes null; final attachment step appends the rest of `list2`.

**Final answer:** Following `dummy.next`: `1 -> 1 -> 2 -> 3 -> 4 -> 4`, i.e., `[1,1,2,3,4,4]`, matching the expected output exactly.

## 7. Complexity Summary

| Approach | Time | Space (Auxiliary) | Notes |
|----------|------|--------------------|-------|
| 1. Recursive splice merge | O(m+n) | O(m+n) (recursion stack) | Correct and elegant, but uses stack space proportional to input size |
| 2. Iterative splice merge, dummy head | O(m+n) | O(1) | Optimal; no new nodes created, no recursion stack used |

`m`, `n` are the lengths of `list1` and `list2` respectively.

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|------|-------------------|--------------------------|
| Both lists empty (`list1=[]`, `list2=[]`, example 2) | Should return an empty list (null head), not error | The main `while` loop's condition is immediately false (`null != null` is false for both), so the loop never executes; the final attachment `tail.next = (list1 != null) ? list1 : list2` sets `tail.next = null` (since both are null), and `dummy.next` is correctly `null`, representing an empty result list |
| One list empty, the other non-empty (e.g. `list1=[]`, `list2=[0]`, example 3) | Should return the non-empty list entirely unchanged | The main loop's condition is immediately false (`list1` is null), so the loop never executes; the final attachment correctly sets `tail.next = list2` (since `list1` is null), directly attaching the entirety of `list2` as the result |
| Tied values between the two lists' current fronts (e.g. both lists currently pointing to a node with value 1, as in the dry run) | Must consistently and correctly handle the tie without duplicating or skipping any node | The `<=` comparison deterministically picks `list1`'s node in a tie, which is a valid, arbitrary-but-consistent choice — either list's node could be chosen first in a tie without affecting overall correctness, since both values are equal and both nodes still get included in the result eventually |
| Lists of very different lengths (one much longer than the other) | The main comparison loop ends as soon as the shorter list is exhausted; the remainder of the longer list must still all be correctly attached | The final `tail.next = (list1 != null) ? list1 : list2` step correctly attaches whichever list still has remaining nodes, in a single O(1) pointer assignment, since that remaining list is already known to be sorted and all its values are already confirmed to be >= everything already placed in the result |
| Negative values (constraint allows down to -100) | Comparisons must work correctly regardless of sign | Plain integer comparison (`<=`) works identically for negative values, with no special-casing needed |
| Maximum constraint size (50 nodes in each list) | Should still perform efficiently, though given the tiny size, this is a non-issue for performance | The O(m+n) time complexity comfortably handles this size regardless of the specific value distribution |

## 9. Java Notes

- **Dummy head node pattern:** as with other linked-list-construction problems (e.g., LC 2, Add Two Numbers), using a placeholder `dummy` node and a separate `tail` pointer avoids needing special-case logic to distinguish "attaching the first real node" from "attaching every subsequent node" — both are handled identically.
- **Splicing (`tail.next = list1`) instead of creating new nodes (`tail.next = new ListNode(list1.val)`):** this is the key detail directly fulfilling the problem's explicit instruction to splice together the *existing* nodes — it's also simply more efficient, avoiding unnecessary object allocation entirely.
- **No overflow or numeric risk:** this problem involves only direct value comparisons between individual node values (bounded to `-100` to `100` per the constraints); no arithmetic that could overflow is ever performed.
- **Recursion depth consideration (relevant to Approach 1):** with list lengths bounded to 50 each, the maximum possible recursion depth here is trivially small and poses no practical stack overflow risk for this specific problem's constraints — but the O(m+n) auxiliary space characterization remains accurate and worth understanding conceptually, since it would become a real concern for much longer lists in a less constrained variant of this problem.

## 10. Common Mistakes

- **Creating entirely new nodes with copied values instead of splicing the existing nodes.** While this would still produce a list with the correct sequence of *values*, it doesn't follow the problem's explicit "splicing together the nodes" instruction, and it's simply wasteful compared to directly reusing the existing node objects via pointer rewiring. Fix: always directly reassign `next` pointers of the existing nodes (`tail.next = list1`), never allocate new `ListNode` objects to represent values that already exist in one of the input lists.
- **Forgetting the final wholesale-attachment step after the main comparison loop ends.** Without `tail.next = (list1 != null) ? list1 : list2`, the remainder of whichever list wasn't fully exhausted during the main loop would be silently dropped from the result. Fix: always explicitly attach whatever remains of the non-exhausted list as the final step, after the main comparison loop.
- **Forgetting to use a dummy head node, and instead trying to special-case "is this the very first node of the merged result" inside the main loop.** This adds unnecessary branching complexity compared to the cleaner dummy-head approach, which uniformly handles the first node the same way as every subsequent one. Fix: always initialize a dummy head and a separate tail pointer at the start, returning `dummy.next` at the end.
- **Using strict `<` instead of `<=` (or vice versa) in the comparison, without realizing both choices are actually valid for correctness, just with a different (but equally valid) tie-breaking convention.** This isn't actually a bug either way for this specific problem, but it's worth being deliberate about which convention is chosen and understanding that both produce a correctly sorted result, with the only difference being which list's node is chosen first in the event of a tie. Fix: pick either `<=` or `<` consistently, and understand that the choice only affects tie-breaking order, not overall correctness.

## 11. Interview Takeaway

- **Trigger sentence to memorize:** "Merge two sorted linked lists into one sorted list, splicing nodes together → two pointers comparing the current front node of each list, attaching the smaller one to the result via pointer rewiring, with a dummy head to simplify construction."
- **90-second explanation:** "Since both lists are already individually sorted, I know the smallest value not yet placed in my merged result must be at the front of one of the two remaining list portions. I use a dummy head node to start building my result, along with a tail pointer to track where to attach the next node. I compare the current front nodes of both lists, and whichever has the smaller value gets spliced directly onto the end of my result — I'm not creating a new node, just rewiring the existing node's position by setting my result's tail's `next` pointer directly to it — and then I advance that specific list's pointer to its own next node. I repeat this until one of the two lists runs out of nodes entirely. At that point, I know the other list's remaining nodes are all already correctly sorted and all at least as large as everything I've placed so far, so I can just attach that entire remaining chunk directly as my final step, without needing any more comparisons. This runs in O(m plus n) time, visiting each node from both lists exactly once, and because I'm only ever rewiring existing nodes' next pointers rather than creating new ones or using recursion, it achieves this with O(1) extra space."
- **Related problems using this pattern:**
  - LeetCode 23 — Merge k Sorted Lists (extends this exact two-list merge technique, typically combined with a heap or divide-and-conquer)
  - LeetCode 88 — Merge Sorted Array (the array-based analog of this same merge-sort-style technique)
  - LeetCode 148 — Sort List (uses this merge step as a subroutine within a full merge-sort implementation on a linked list)

## 12. Recall Questions

**Q:** Why is it always sufficient to compare just the two current front nodes of both lists to determine the next node for the merged result, rather than needing to look further ahead into either list?
**A:** Since both input lists are already individually sorted, the front node of each remaining list portion is guaranteed to be the smallest not-yet-placed value from that particular list, so the true overall smallest remaining value across both lists must be one of these two specific candidates, making a direct comparison between just these two always sufficient.

**Q:** Why is it safe to attach the entire remainder of one list in a single step once the other list becomes exhausted, without any further comparisons?
**A:** The exhausted list has contributed all of its nodes already, and since the still-remaining list is itself individually sorted, every one of its remaining values is guaranteed to be greater than or equal to everything already placed into the merged result so far, so no further comparison against the (now empty) other list is needed.

**Q:** Why does the problem's explicit instruction to "splice together the nodes" specifically rule out creating brand-new nodes with copied values?
**A:** Splicing means directly reusing and rearranging the existing node objects' `next` pointers to form the merged sequence, which is both more faithful to what the problem asks for and more efficient, since it avoids the unnecessary memory allocation and value-copying that creating entirely new nodes would require.

**Q:** Why does the iterative approach use less auxiliary space than the recursive approach, despite both achieving the same O(m+n) time complexity?
**A:** The recursive approach builds up a call stack with one stack frame per node processed in the worst case (since each recursive call doesn't return until the entire rest of the merge completes), consuming O(m+n) stack space, whereas the iterative approach uses a simple loop with a fixed, small number of pointer variables that don't grow with the input size.

**Q:** What specific problem would arise if the final wholesale-attachment step after the main loop were omitted entirely?
**A:** Whichever list still had remaining, unprocessed nodes at the point the main loop exited (because the other list became exhausted first) would never get attached to the result at all, causing those nodes to be silently and incorrectly dropped from the final merged list.

## 13. Final Code

```java
public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
    ListNode dummy = new ListNode(0);
    ListNode tail = dummy;

    while (list1 != null && list2 != null) {
        if (list1.val <= list2.val) {
            tail.next = list1;
            list1 = list1.next;
        } else {
            tail.next = list2;
            list2 = list2.next;
        }
        tail = tail.next;
    }

    tail.next = (list1 != null) ? list1 : list2;

    return dummy.next;
}
```

## 14. Self-Test

You have the heads of two sorted linked lists. Merge them into one sorted list by splicing together the existing nodes, and return the head. Think about why comparing just the two current front nodes is always enough to pick the next node correctly, and why the remainder of one list can be attached wholesale once the other list is exhausted.
