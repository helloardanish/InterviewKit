---
title: "0092 — Reverse Linked List II"
layout: default
parent: Linked List
grand_parent: Leetcode
nav_order: 92
permalink: /08-Linked-List/0092-reverse-linked-list-ii/

problem: 92 - Reverse Linked List II
url: https://leetcode.com/problems/reverse-linked-list-ii/
difficulty: Medium
section: Linked List / In-Place Manipulation
patterns: [dummy-head, head-insertion-reversal]
data_structures: [linked-list]
time: O(n)
space: O(1)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0092 — Reverse Linked List II

| Field | Value |
|---|---|
| **Pattern** | Dummy Head + Head-Insertion Reversal |
| **Difficulty** | Medium |
| **Section** | Linked List / In-Place Manipulation |
| **Time** | O(n) |
| **Space** | O(1) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Meta |
| **LeetCode** | [Reverse Linked List II](https://leetcode.com/problems/reverse-linked-list-ii/) |

## 1. Problem in Simple Words

**Given:** The head of a singly linked list, and two 1-indexed positions `left` and `right` (`left <= right`).

**Required:** Reverse only the sublist of nodes from position `left` to position `right` (inclusive), leaving everything before and after that sublist in its original order, and return the head of the resulting list.

**Constraints that actually matter:**
- `1 <= n <= 500`, `1 <= left <= right <= n` — moderate size; `left == right` is a valid input (a "sublist" of exactly one node, which trivially requires no actual reversal).
- `left` could be `1` (the reversal starts at the very head of the list) — this means the node *before* the sublist to reverse might not exist, which is exactly the kind of edge case a **dummy head node** is designed to eliminate, by guaranteeing there's always a node before position 1 to serve as a uniform "predecessor," regardless of where `left` falls.
- The explicit **follow-up** — "Could you do it in one pass?" — is a direct signal toward the intended optimal technique: reversing the specified sublist using a single traversal with in-place pointer manipulation (the classic "head insertion" reversal trick), rather than a simpler but less elegant two-pass approach (e.g., one pass to locate/extract the sublist, a second to reverse and reinsert it).

## 2. Recognition Signals

- "Reverse a sub-portion of a singly linked list, by position" → the signature of needing to locate the boundary just before the sublist (`left`'s predecessor), then perform an **in-place reversal confined to just that range**, and finally correctly reconnect the reversed range back into the rest of the list on both ends.
- The use of a **dummy head node** is doubly motivated here: it both simplifies "what if `left == 1`" (no real predecessor exists) the same way it does in many other linked-list-construction/-modification problems, and it provides a stable, unchanging reference point (`dummy`) from which to locate `left`'s predecessor via a simple counted walk, regardless of where in the list the reversal starts.
- The specific **one-pass head-insertion reversal technique**: rather than detaching the sublist, reversing it separately, and reattaching it (which naturally suggests multiple distinct phases), repeatedly take the node immediately after a fixed "anchor" point (the sublist's predecessor) and move it to the very front of the sublist being built, one node at a time — this achieves the reversal and the "building the new order" simultaneously, in a single pass through the sublist, which is the key insight behind the follow-up's one-pass challenge.

**Pattern:** Head-Insertion Reversal Within a Bounded Range, Using a Dummy Head (locate the node just before `left` using a dummy head for uniformity, then repeatedly detach the node right after that fixed point and reinsert it at the front of the growing reversed sublist, all within a single pass).

## 3. Core Idea

Use a `dummy` node pointing to `head`, and walk a `prev` pointer forward exactly `left - 1` steps from `dummy` — this lands `prev` on the node immediately before position `left` (the node that will need to connect to what becomes the *new* start of the reversed sublist, which will be the node originally at position `right`). Let `current` be `prev.next` (the node currently at position `left`, which — crucially — will end up at the very *end* of the reversed sublist and keep pointing to whatever comes after position `right`, unchanged throughout the process).

Now, repeat the following `right - left` times: take `current.next` (call it `moving`), detach it from its current position, and reinsert it immediately after `prev` (i.e., as the new first node of the sublist being reversed so far). This is the "head insertion" trick: each iteration takes the next not-yet-reversed node and moves it to the very front of the portion already reversed, which naturally produces a fully reversed sublist by the time all `right - left` moves are complete, with `current` correctly ending up as the sublist's last node (unchanged in position, just now at the "end" instead of the "start") and `prev.next` correctly pointing to what was originally the `right`-th node (now the sublist's new start).

**Invariant:** After `k` iterations of the head-insertion step, the nodes originally at positions `left` through `left+k` have been fully reversed among themselves and correctly reconnected to `prev` at the front, while `current` remains fixed at the original position-`left` node, now correctly positioned as the tail of the growing reversed segment, with its own `next` pointer unchanged (still pointing to whatever follows the *entire* eventually-reversed range, since it was never the node being moved).

## 4. Approach 1 — Two-Pass (Extract, Reverse Separately, Reinsert)

**Logic:** First pass: walk to position `left`, extracting the sublist of nodes from `left` to `right` into a separate structure (or just tracking the boundary nodes). Second pass: reverse that extracted sublist using the standard whole-list reversal technique, then splice the reversed sublist back into its original position, reconnecting both the "before" and "after" boundaries.

```java
public ListNode reverseBetween(ListNode head, int left, int right) {
    ListNode dummy = new ListNode(0);
    dummy.next = head;

    ListNode prev = dummy;
    for (int i = 0; i < left - 1; i++) {
        prev = prev.next;
    }

    ListNode subListHead = prev.next;
    ListNode subListTail = subListHead;
    for (int i = 0; i < right - left; i++) {
        subListTail = subListTail.next;
    }
    ListNode afterSubList = subListTail.next;

    subListTail.next = null; // Detach the sublist entirely.

    // Standard whole-list reversal on the detached sublist.
    ListNode reversedHead = null;
    ListNode current = subListHead;
    while (current != null) {
        ListNode nextTemp = current.next;
        current.next = reversedHead;
        reversedHead = current;
        current = nextTemp;
    }

    // Reconnect: prev -> reversedHead ... subListHead -> afterSubList.
    prev.next = reversedHead;
    subListHead.next = afterSubList;

    return dummy.next;
}
```

- **Time:** O(n) — walking to the boundaries, detaching, reversing, and reconnecting each touch the relevant nodes a bounded constant number of times.
- **Space:** O(1) auxiliary — no additional data structure beyond a handful of pointer variables (this specific two-pass variant happens to also be O(1) space, since the "extraction" here is really just identifying boundary pointers, not copying into a separate list/array).

**Why it is presented as a valid but conceptually two-phase alternative:** This approach is correct and also O(1) space, but it conceptually separates "find and detach the sublist" from "reverse it" into two distinct phases (even though both are still done via pointer manipulation, no actual array/list copying) — the single-pass head-insertion technique shown next achieves the same result with a more unified, single continuous pass that directly answers the problem's explicit one-pass follow-up in the most literal sense, without a distinct "detach, then reverse, then reattach" conceptual structure.

## 5. Approach 2 — Optimized (One-Pass Head-Insertion Reversal)

**Algorithm:**
1. Create a `dummy` node with `dummy.next = head`.
2. Walk `prev` forward `left - 1` steps from `dummy`, landing it on the node immediately before position `left`.
3. Let `current = prev.next` (the node at position `left`; this will become the tail of the reversed sublist and stays fixed throughout).
4. Repeat `right - left` times:
   - Let `moving = current.next` (the next node to be moved to the front of the reversed portion).
   - Detach `moving`: `current.next = moving.next`.
   - Reinsert `moving` right after `prev`: `moving.next = prev.next; prev.next = moving;`.
5. Return `dummy.next`.

```java
public ListNode reverseBetween(ListNode head, int left, int right) {
    ListNode dummy = new ListNode(0);
    dummy.next = head;

    ListNode prev = dummy;
    for (int i = 0; i < left - 1; i++) {
        prev = prev.next;
    }

    ListNode current = prev.next;

    for (int i = 0; i < right - left; i++) {
        ListNode moving = current.next;
        current.next = moving.next;
        moving.next = prev.next;
        prev.next = moving;
    }

    return dummy.next;
}
```

- **Time:** O(n) — a single traversal: `left - 1` steps to locate `prev`, then `right - left` head-insertion steps, together bounded by the list's length.
- **Space:** O(1) auxiliary — only a small, fixed number of pointer variables (`dummy`, `prev`, `current`, `moving`), satisfying the follow-up's one-pass, constant-space goal directly.

**Why this is optimal:** Every node within the `[left, right]** range must be touched at least once to reverse it, so the work done is proportional to the list's length, and this approach achieves O(n) time in a single, continuous traversal — directly and literally satisfying the explicit "can you do it in one pass" follow-up, since there's no separate "detach" phase followed by a distinct "reverse" phase; the detachment and reinsertion (which together constitute the reversal) happen simultaneously, node by node, in one unified loop. Space is O(1) since only a handful of pointers are used, with all manipulation happening via direct reference rewiring on the existing nodes.

## 6. Dry Run

Example: `head = [1,2,3,4,5]`, `left = 2`, `right = 4`.
Chosen because it's the canonical example, directly exercising the head-insertion mechanic across three iterations.

`dummy -> 1 -> 2 -> 3 -> 4 -> 5`.

**Locate `prev`:** walk `left-1=1` step from `dummy`: `prev = node(1)`.

`current = prev.next = node(2)`.

**Head-insertion loop, `right-left=2` iterations:**

| Iteration | current.next (moving) before | Action | List state after |
|-----------|-------------------------------------|--------|------------------------|
| 1 | moving=node(3) | current.next=moving.next(4); moving.next=prev.next(2); prev.next=moving(3) | dummy->1->3->2->4->5 |
| 2 | moving=node(4) (current.next is now 4, since current=2's next was updated to 4 in iter1... let's verify: after iter1, current(2).next=4 (set to moving.next which was 4); so current.next is 4) | moving=current.next=node(4); current.next=moving.next(5); moving.next=prev.next(3); prev.next=moving(4) | dummy->1->4->3->2->5 |

Exit condition: loop runs exactly `right-left=2` times, now complete.

**Final answer:** Following `dummy.next`: `1 -> 4 -> 3 -> 2 -> 5`, matching the expected output `[1,4,3,2,5]` exactly.

## 7. Complexity Summary

| Approach | Time | Space (Auxiliary) | Notes |
|----------|------|--------------------|-------|
| 1. Two-pass (detach, reverse, reattach) | O(n) | O(1) | Correct; conceptually two distinct phases, though both pointer-based |
| 2. One-pass head-insertion reversal | O(n) | O(1) | Optimal; directly and literally answers the one-pass follow-up |

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|------|-------------------|--------------------------|
| `left == right` (a "sublist" of exactly one node, example 2) | No actual reversal is needed; the list should be returned completely unchanged | The head-insertion loop runs `right - left = 0` times, so it never executes, and the list is correctly returned unmodified |
| `left == 1` (reversal starts at the very head of the list) | There's no real predecessor node before position 1 in the original list | The dummy head node provides a uniform "predecessor" regardless of where `left` falls — walking `left - 1 = 0` steps from `dummy` simply leaves `prev = dummy` itself, which correctly serves as the anchor point even when the reversed sublist will become the new head of the list |
| `right == n` (reversal extends all the way to the end of the list) | After the reversal, `current` (fixed at the original position-`left` node, now the sublist's tail) must correctly have `next = null` if nothing follows | Since `current.next` is updated each iteration to skip over the node being moved, by the final iteration `current.next` correctly ends up as whatever followed the original position-`right` node — which is `null` if `right == n`, requiring no special-casing beyond the general mechanism already in place |
| `left == 1` and `right == n` simultaneously (reverse the entire list) | Combines both boundary edge cases at once | Both mechanisms described above apply simultaneously and independently without conflict, correctly producing a fully reversed list |
| Single-node list (`n == 1`, necessarily `left == right == 1`) | Trivial case, should return the single node unchanged | Same reasoning as the `left == right` case above — the loop runs zero times, list returned unchanged |
| Negative node values (constraint allows down to -500) | Comparisons and pointer manipulation must work correctly regardless of value sign | Node values are never compared or used in any arithmetic by this algorithm — only pointer/reference manipulation is performed, so value sign is entirely irrelevant to correctness |

## 9. Java Notes

- **Dummy head node pattern:** as with other linked-list-modification problems, using a `dummy` node whose `next` initially points to `head` provides a uniform anchor point for the `prev` pointer, eliminating the need for special-case logic when `left == 1` (where no real predecessor node would otherwise exist).
- **The three-line head-insertion reconnection** (`current.next = moving.next; moving.next = prev.next; prev.next = moving;`) **must happen in exactly this order:** `current.next` must be updated first (to skip over the node being moved, before that node's own pointers are touched), then `moving.next` is set to point to whatever `prev.next` currently is (the current front of the reversed-so-far portion), and finally `prev.next` is updated to point to `moving` (making it the new front) — reordering these three steps would lose references or produce an incorrect result.
- **`current` remains a fixed reference throughout the entire loop**, never reassigned — only `current.next` is updated each iteration (to reflect that another node has been moved away from directly following it) — this is a subtle but important detail: `current` is *not* "walking forward" through the list; it's anchored at the original position-`left` node the whole time, which is precisely why it correctly ends up as the tail of the reversed sublist.
- **No overflow or numeric risk:** this problem involves only pointer/reference manipulation and small bounded loop counters (`left`, `right` both at most 500); no arithmetic here risks overflow.

## 10. Common Mistakes

- **Getting the order of the three reconnection steps wrong** (e.g., setting `prev.next = moving` before `moving.next` has been correctly assigned, or before `current.next` has been updated to skip the node being moved). This can cause a reference to be overwritten before it's used, corrupting the list structure (potentially even creating an accidental cycle or losing track of part of the list). Fix: always follow the precise order — detach via `current.next = moving.next` first, then set `moving.next = prev.next`, then finally `prev.next = moving`.
- **Reassigning `current` during the loop, treating it as if it should "walk forward" like a typical traversal pointer.** Since `current` is meant to remain fixed at the original position-`left` node throughout (it becomes the tail of the reversed sublist), accidentally moving it would break the entire reversal mechanism. Fix: only ever update `current.next`, never reassign `current` itself within the head-insertion loop.
- **Forgetting the dummy head node and trying to handle `left == 1` as a special case elsewhere in the code.** Without the dummy head, locating "the predecessor of position `left`" when `left == 1` requires awkward separate logic (since no such real predecessor exists), rather than being handled uniformly. Fix: always use a dummy head node pointing to the original `head`, and return `dummy.next` at the end.
- **Running the head-insertion loop the wrong number of times** (e.g., `right - left + 1` instead of `right - left`), which would either under-reverse or attempt one extra, invalid iteration. Fix: carefully verify the loop should run exactly `right - left` times, since the node at position `left` itself doesn't need to be "moved" (it naturally ends up correctly placed as the tail by the end of the process).

## 11. Interview Takeaway

- **Trigger sentence to memorize:** "Reverse a sublist of a linked list between two positions, in one pass → dummy head to locate the predecessor of the start position, then repeatedly move the next node to the front of the reversed segment (head-insertion), leaving the original start node fixed as the eventual tail."
- **90-second explanation:** "I use a dummy head node so I have a uniform way to find the node right before my reversal should start, even if that start is the very first node in the list. I walk forward to find that predecessor, then I identify the node currently at the start of my reversal range — this node is actually going to end up as the *tail* of the reversed segment, and importantly, I never move it; I keep it fixed the whole time. Then, for however many nodes are left in my reversal range, I repeatedly take the node immediately after my fixed node, detach it, and reinsert it right at the front of the segment, immediately after my predecessor pointer. Each time I do this, the node I just moved becomes the new front of the reversed-so-far portion, which is exactly the head-insertion reversal trick. By the time I've done this for every node in the range, the whole segment has been reversed in place, and because I'm directly rewiring existing node references rather than detaching the whole segment separately and reversing it as its own standalone step, this all happens in a single continuous pass through the list, directly answering the follow-up's challenge."
- **Related problems using this pattern:**
  - LeetCode 206 — Reverse Linked List (the simpler whole-list version of this exact reversal technique)
  - LeetCode 25 — Reverse Nodes in k-Group (extends this same head-insertion idea to repeatedly reverse fixed-size chunks)
  - LeetCode 24 — Swap Nodes in Pairs (a related, simpler pointer-rewiring linked-list manipulation)

## 12. Recall Questions

**Q:** Why does the node originally at position `left` end up as the *tail* of the reversed sublist, rather than needing to be explicitly moved anywhere?
**A:** Since every other node in the range gets moved to the front of the segment one at a time via head insertion, the original position-`left` node is simply never selected to be moved — it stays exactly where it started, and as more nodes get inserted in front of it, it naturally ends up at the back of the newly reversed order.

**Q:** Why must the three reconnection steps during each head-insertion iteration happen in a specific, fixed order?
**A:** Each step depends on a reference that would be lost or corrupted if an earlier step hadn't already captured it correctly — specifically, `current.next` must be updated to skip the moving node before anything else changes, and `moving.next` must be set to the current front of the reversed segment before `prev.next` is reassigned to point to `moving`, otherwise the needed reference would already have been overwritten.

**Q:** Why does using a dummy head node eliminate the need for special-case handling when `left` equals 1?
**A:** Without a dummy head, there would be no real node existing before the original head of the list to serve as the "predecessor" that the reversed segment needs to reconnect to, but the dummy head provides exactly such a node unconditionally, regardless of where in the list the reversal actually begins.

**Q:** Why is this specific technique considered a true "one-pass" solution, in contrast to an approach that detaches the sublist, reverses it as a separate step, and then reattaches it?
**A:** The detachment and the reversal happen simultaneously within the very same loop and the very same set of pointer updates for each node, rather than being split into conceptually and sequentially distinct phases (first fully detach everything, then separately reverse, then separately reattach), so the entire transformation occurs within a single continuous traversal of the relevant portion of the list.

**Q:** What would go wrong if the head-insertion loop ran for `right - left + 1` iterations instead of `right - left`?
**A:** This would attempt one extra iteration beyond what's needed, trying to move a node that either doesn't exist within the intended range or has already been correctly placed, which would corrupt the list's structure or cause incorrect behavior, since the original position-`left` node itself was never meant to be moved as part of the head-insertion process.

## 13. Final Code

```java
public ListNode reverseBetween(ListNode head, int left, int right) {
    ListNode dummy = new ListNode(0);
    dummy.next = head;

    ListNode prev = dummy;
    for (int i = 0; i < left - 1; i++) {
        prev = prev.next;
    }

    ListNode current = prev.next;

    for (int i = 0; i < right - left; i++) {
        ListNode moving = current.next;
        current.next = moving.next;
        moving.next = prev.next;
        prev.next = moving;
    }

    return dummy.next;
}
```

## 14. Self-Test

You have the head of a singly linked list and two positions, `left` and `right`. Reverse only the nodes between those positions (inclusive), in one pass. Think about why the node originally at position `left` ends up as the tail of the reversed segment without ever being moved, and why a dummy head node is needed to uniformly handle the case where `left` is the very first position.
