---
title: "0019 — Remove Nth Node From End of List"
layout: default
parent: Linked List
grand_parent: Leetcode
nav_order: 19
permalink: /08-Linked-List/0019-remove-nth-node-from-end-of-list/

problem: 19 - Remove Nth Node From End of List
url: https://leetcode.com/problems/remove-nth-node-from-end-of-list/
difficulty: Medium
section: Linked List / Two Pointers
patterns: [two-pointers, dummy-head, gap-pointers]
data_structures: [linked-list]
time: O(sz)
space: O(1)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0019 — Remove Nth Node From End of List

| Field | Value |
|---|---|
| **Pattern** | Gap Pointers with Dummy Head |
| **Difficulty** | Medium |
| **Section** | Linked List / Two Pointers |
| **Time** | O(sz) — one pass |
| **Space** | O(1) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Meta |
| **LeetCode** | [Remove Nth Node From End of List](https://leetcode.com/problems/remove-nth-node-from-end-of-list/) |

## 1. Problem in Simple Words

**Given:** the head of a linked list and an integer `n`.

**Required:** remove the node that is `n` positions from the **end** of the list (so `n = 1` means removing the last node), and return the new head.

**Constraints that matter:**
- `1 <= sz <= 30` and `1 <= n <= sz` — `n` is always guaranteed valid (never larger than the list's length), so there's no need to handle an out-of-range `n` as an error case.
- The list can have exactly one node, and removing it (when `n = 1` on a single-node list) must correctly return an empty list (`null` head) — this is the kind of boundary case that's easy to mishandle without a dummy head.
- **Follow-up: "Could you do this in one pass?"** — this directly signals that a naive two-pass approach (count the length, then walk again to the removal point) is the straightforward baseline, and a single-pass two-pointer technique is the intended refinement.

## 2. Recognition Signals

- "Nth node from the **end**" of a singly linked list (which only supports forward traversal, no `.prev` or length property) → the inherent difficulty is that "distance from the end" isn't directly knowable without first finding the end — this is the classic setup for a **two-pointer gap technique**.
- The explicit one-pass follow-up is a strong, direct hint toward maintaining a **fixed gap of `n` nodes between two pointers**, so that when the lead pointer reaches the end, the trailing pointer is automatically positioned exactly at the node needing removal (or just before it).
- Needing to handle removing the **head node itself** (when `n` equals the list's full length) as a valid, unexceptional case → the signal to use a **dummy head node**, a standard technique for avoiding special-casing "the node to modify is the head" in linked-list deletion problems.
- Named pattern: **Two-Pointer Fixed Gap (Runner Technique) with a Dummy Head**.

## 3. Core Idea

Use two pointers, both starting at a dummy node placed just before the real head. First, advance a `fast` pointer `n + 1` steps ahead of a `slow` pointer (so there's a gap of `n` nodes between them, with `fast` positioned one node further than strictly necessary, accounting for the dummy offset). Then advance both pointers together, one step at a time, until `fast` reaches the end (`null`). At that point, `slow` is positioned exactly at the node **just before** the one that needs removal, so `slow.next = slow.next.next` performs the deletion directly.

**Why it's correct:** if `fast` is always exactly `n + 1` nodes ahead of `slow` (counting the dummy-to-head gap), then by the time `fast` falls off the end of the list (becomes `null`), `slow` has advanced exactly as many steps as `fast` did — meaning `slow` is now positioned `n + 1` nodes before the end, i.e., immediately before the node that is `n` nodes from the end. This maintains the gap invariant throughout, so only a single pass (plus the initial `n+1`-step head start) is needed — no separate length-counting pass required.

**Invariant:** throughout the simultaneous advancement phase, the number of nodes between `slow` and `fast` (inclusive of `fast`, exclusive of `slow`) remains constant at `n + 1`, which is precisely what guarantees `slow` lands on the correct "just before the target" position exactly when `fast` exhausts the list.

## 4. Approach 1 — Brute Force (Two-Pass: Count Length, Then Remove)

1. Traverse the entire list once to count its total length, `len`.
2. Compute the 0-indexed position (from the head) of the node to remove: `removeIndex = len - n`.
3. Walk from the head again, using a dummy node, to reach the node just before `removeIndex`, and unlink the target node.

```java
public ListNode removeNthFromEndBruteForce(ListNode head, int n) {
    int len = 0;
    for (ListNode node = head; node != null; node = node.next) {
        len++;
    }

    ListNode dummy = new ListNode(0, head);
    ListNode current = dummy;
    for (int i = 0; i < len - n; i++) {
        current = current.next;
    }

    current.next = current.next.next;
    return dummy.next;
}
```

**Time:** O(sz) — one full pass to count the length, plus a second pass of up to `sz` steps to reach the removal point; both passes are linear, so this is still O(sz) overall, just with two traversals instead of one.
**Space:** O(1) auxiliary — only a counter and a couple of pointers.

This is not optimal with respect to the problem's own explicit follow-up: it works correctly and has the same O(sz) time complexity asymptotically, but it requires **two separate traversals** of the list, whereas the problem specifically challenges for a **single-pass** solution — a meaningful distinction in an interview setting even though the Big-O class doesn't change.

## 5. Approach 2 — Optimized (One-Pass, Two-Pointer Fixed Gap)

1. Create a dummy node pointing to `head` (`dummy.next = head`), to uniformly handle the case where the head itself must be removed.
2. Initialize both `slow` and `fast` at `dummy`.
3. Advance `fast` exactly `n + 1` steps forward (this establishes the fixed gap; advancing `n+1` rather than `n` accounts for `slow` needing to end up just *before* the target, not *at* it).
4. While `fast != null`, advance both `slow` and `fast` one step at a time together — this preserves the gap until `fast` runs off the end.
5. Once `fast == null`, `slow` is positioned at the node just before the one to remove: perform `slow.next = slow.next.next`.
6. Return `dummy.next` (the possibly-updated head).

```java
public ListNode removeNthFromEnd(ListNode head, int n) {
    ListNode dummy = new ListNode(0, head);
    ListNode slow = dummy;
    ListNode fast = dummy;

    for (int i = 0; i < n + 1; i++) {
        fast = fast.next;
    }

    while (fast != null) {
        slow = slow.next;
        fast = fast.next;
    }

    slow.next = slow.next.next;
    return dummy.next;
}
```

**Time:** O(sz) — `fast` advances `n + 1` steps initially, then both pointers advance together for the remainder of the list; every node is visited by `fast` exactly once overall (either during the initial head start or the joint phase), giving a single linear pass.
**Space:** O(1) auxiliary — only the dummy node and two pointers, regardless of list length.

This is optimal: it answers the problem's explicit follow-up directly, achieving the removal in a genuine single traversal rather than two, while maintaining the same O(sz) time and O(1) space as the two-pass approach — a strictly better solution along the dimension the problem specifically asks about.

## 6. Dry Run

`head = [1,2,3,4,5]`, `n = 2` → `dummy -> 1 -> 2 -> 3 -> 4 -> 5`

Initial fast-advancement (`n + 1 = 3` steps from `dummy`):

| step | fast position |
|---|---|
| start | dummy |
| 1 | 1 |
| 2 | 2 |
| 3 | 3 |

Joint advancement phase (`slow` starts at `dummy`, `fast` starts at node `3`):

| iteration | slow | fast | fast == null? |
|---|---|---|---|
| 1 | 1 (dummy.next) | 4 (fast.next) | no |
| 2 | 2 (slow.next) | 5 (fast.next) | no |
| 3 | 3 (slow.next) | null (fast.next, since 5 is the last node) | **yes, exit** |

Exit: loop stops since `fast` is now `null`. `slow` is at node `3`. Perform `slow.next = slow.next.next`, which sets node `3`'s next to node `5` (skipping over node `4`). Final list: `1 -> 2 -> 3 -> 5`, matching the expected output `[1,2,3,5]`.

## 7. Complexity Summary

| Approach | Time | Space | Notes |
|---|---|---|---|
| Two-Pass (Count Then Remove) | O(sz) | O(1) aux | Correct, same Big-O, but traverses the list twice |
| One-Pass, Two-Pointer Fixed Gap | O(sz) | O(1) aux | Single traversal, directly answers the follow-up |

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|---|---|---|
| Removing the head itself (`n == sz`, e.g. `head=[1,2], n=2`) | The "node before the target" would be the dummy, not a real node | The dummy node naturally serves as that "node before," so `slow` correctly ends up at `dummy` itself when the head must be removed, and `dummy.next = dummy.next.next` correctly updates what the new head is |
| Single-node list, remove it (`head=[1], n=1`) | Result must be an empty list (`null` head) | `fast` advances `n+1=2` steps from `dummy` (to `null`, past the single node); loop condition `fast != null` is already false, so `slow` stays at `dummy`; `slow.next = slow.next.next` sets `dummy.next` to `null`, and the function returns `dummy.next = null` correctly |
| Removing the last node (`n = 1`) | Must correctly identify and unlink the tail | `slow` ends up at the second-to-last node (the correct "node before the target"), and `slow.next = slow.next.next` sets its `next` to `null`, correctly removing the tail |
| Two-node list, remove the first (`head=[1,2], n=1`)  | Wait, `n=1` removes the *last* node, not first — worth double-checking which end "first" vs "last" refers to | `n` always counts from the end, so `n=1` on `[1,2]` removes node `2`, leaving `[1]`, as shown in Example 3; the two-pointer gap logic handles this the same way as any other case |
| `n` equal to `sz` with a longer list | Removing the head of a multi-node list | Same mechanism as the single-node case: the dummy node ensures `slow` can correctly represent "the position before the head" without any special-case branch |

## 9. Java Notes

- The dummy node constructor `new ListNode(0, head)` uses the three-argument `ListNode(int val, ListNode next)` constructor (as provided in LeetCode's standard `ListNode` definition) to create the dummy and link it to the real head in one line.
- Advancing `fast` by `n + 1` steps rather than `n` is the detail most likely to cause an off-by-one error — the `+1` accounts for the fact that `slow` needs to stop **one node before** the target (to perform the unlink via `.next.next`), not directly **at** the target.
- No array or length-tracking variable is needed in the optimized approach — the two-pointer gap technique inherently "knows" when it has reached the right position purely through the fixed spacing between the pointers, without ever explicitly counting or storing the list's length.
- Since `fast` is guaranteed (by the problem's constraint `n <= sz`) to never need to advance past the actual end of the list plus one, there's no risk of a `NullPointerException` during the initial `n+1`-step advancement, as long as that initial loop correctly stops exactly at `n+1` iterations.

## 10. Common Mistakes

- Advancing `fast` by only `n` steps initially instead of `n + 1` — this causes `slow` to end up positioned exactly **at** the node to be removed, rather than just before it, making the deletion (`slow.next = slow.next.next`) target the wrong node (it would instead skip the node *after* the intended target). Fix: always advance the lead pointer `n + 1` steps to correctly offset for needing the "just before" position.
- Omitting the dummy node and starting both pointers at the real `head` — this breaks when the head itself needs to be removed, since there's no "node before the head" to position `slow` at; the code would need an awkward special case to handle updating the head pointer separately. Fix: always use a dummy node preceding the real head for deletion problems that might need to remove the first node.
- Forgetting to return `dummy.next` (returning the original `head` variable instead, which might now be stale if the head itself was removed) — this returns the wrong list when the first node was deleted. Fix: always return `dummy.next`, since it correctly reflects the current head after any possible removal, including of the original head.
- Using a counting approach but off-by-one in computing `removeIndex = len - n` (e.g., using `len - n - 1` or `len - n + 1`) — produces a node one position off from the intended target. Fix: carefully verify with a small example (like Example 1) that `len - n` lands the walk on the correct "node before the target" when starting the walk from the dummy.

## 11. Interview Takeaway

- **Trigger sentence:** "Remove the nth node from the *end* of a singly linked list, ideally in one pass → two pointers with a fixed gap of n+1, using a dummy head to uniformly handle head removal."
- **90-second explanation:** "Since a singly linked list only lets me go forward, I can't directly jump to 'n nodes from the end' without first knowing the total length — unless I keep two pointers a fixed distance apart. I start both at a dummy node just before the real head, then advance a fast pointer n+1 steps ahead first. That extra +1, beyond just n, is what ensures my slow pointer ends up exactly one node before the one I need to remove, rather than landing directly on it — important because deleting a node in a singly linked list means updating the *previous* node's next pointer. Then I move both pointers forward together, one step at a time, until the fast pointer falls off the end of the list. At that point, the slow pointer is guaranteed to be sitting right before the target node, so I just skip over it. The dummy head means I don't need any special-case logic for when the node being removed happens to be the original head itself."
- **Related problems:** 876 (Middle of the Linked List, same fast/slow pointer family), 141 (Linked List Cycle, same two-pointer family, different purpose), 21 (Merge Two Sorted Lists, same dummy-head idiom).

## 12. Recall Questions

**Q:** Why does the fast pointer need to advance `n + 1` steps ahead initially, rather than just `n` steps?
**A:** Deleting a node in a singly linked list requires modifying the `next` pointer of the node *before* it, so the slow pointer needs to end up one position earlier than the target itself; the extra step in the initial advancement is what shifts the final resting point from "at the target" to "just before the target."

**Q:** Why is a dummy node necessary here, rather than starting both pointers directly at `head`?
**A:** If the node to be removed turns out to be the original head (when `n` equals the list's total length), there would be no real node to position the "slow" pointer at that represents "the node before the head" — the dummy node fills that role uniformly, so the same deletion logic (`slow.next = slow.next.next`) works correctly whether or not the head itself is being removed.

**Q:** How does maintaining a constant gap between the two pointers let the algorithm determine the correct removal position without ever counting the list's length?
**A:** Once the fast pointer is `n + 1` nodes ahead, advancing both pointers together preserves that exact spacing; when the fast pointer exhausts the list (becomes null), the number of steps it took to get there equals the number of steps the slow pointer also took, which by the fixed-gap relationship places the slow pointer exactly one node before the nth-from-the-end node, with no separate length computation required.

**Q:** Why must the function return `dummy.next` rather than the original `head` parameter?
**A:** If the original head node was the one removed, the `head` variable still refers to a node that's no longer part of the list (and may have been logically "deleted," in the sense that nothing before it now points to it, even though its `next` pointer value hasn't changed) — `dummy.next` always correctly reflects whatever the current first node of the list is after the operation.

**Q:** In what specific sense does the optimized solution improve on the two-pass approach, given they share the same O(sz) time complexity?
**A:** The improvement is in the number of **traversals** of the list, not the asymptotic time class — the two-pass approach walks the entire list twice (once to count, once to remove), while the two-pointer approach accomplishes the same result in a single combined traversal, which is exactly what the problem's explicit follow-up question is asking for.

## 13. Final Code

```java
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
    public ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode dummy = new ListNode(0, head);
        ListNode slow = dummy;
        ListNode fast = dummy;

        for (int i = 0; i < n + 1; i++) {
            fast = fast.next;
        }

        while (fast != null) {
            slow = slow.next;
            fast = fast.next;
        }

        slow.next = slow.next.next;
        return dummy.next;
    }
}
```

## 14. Self-Test

Given the head of a linked list and an integer `n`, remove the node that is `n` positions from the end, in a single pass. Re-derive: use a dummy node before the head, advance a fast pointer `n + 1` steps ahead of a slow pointer (both starting at the dummy), then move both together until the fast pointer reaches `null`; the slow pointer will then be positioned exactly before the node to remove, so skip it via `slow.next = slow.next.next` and return `dummy.next`.
