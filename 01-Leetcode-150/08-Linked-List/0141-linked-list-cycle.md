---
title: "0141 — Linked List Cycle"
layout: default
parent: Linked List
grand_parent: Leetcode
nav_order: 141
permalink: /08-Linked-List/0141-linked-list-cycle/

problem: 141 - Linked List Cycle
url: https://leetcode.com/problems/linked-list-cycle/
difficulty: Easy
section: Linked List
patterns: [floyd-tortoise-hare, cycle-detection]
data_structures: [linked-list, hashset]
time: O(n)
space: O(1)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0141 — Linked List Cycle

| Field | Value |
|---|---|
| **Pattern** | Floyd's Tortoise and Hare (Cycle Detection) |
| **Difficulty** | Easy |
| **Section** | Linked List |
| **Time** | O(n) |
| **Space** | O(1) — with Floyd's; O(n) with hashset |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Meta |
| **LeetCode** | [Linked List Cycle](https://leetcode.com/problems/linked-list-cycle/) |

## 1. Problem in Simple Words

**Given:** the head of a singly linked list.

**Required:** determine whether the list contains a cycle — some node's `next` pointer eventually leads back to a node already visited, rather than terminating at `null`.

**Constraints that matter:**
- `0 <= number of nodes <= 10^4` — small to moderate size; the empty list (`head == null`) is explicitly allowed and must be handled as "no cycle."
- `pos` (the internal index the tail connects back to, `-1` meaning no cycle) is **not passed as a parameter** to the function — this is a strong signal that the solution cannot rely on knowing where the cycle starts or whether one exists at all; it must be genuinely *detected* by traversal, not looked up.
- **Follow-up: solve using O(1) memory** — this explicitly invites comparing a simple `HashSet`-based approach (which uses O(n) space) against a smarter O(1)-space technique, making this a good problem for demonstrating the space-time trade-off explicitly.

## 2. Recognition Signals

- "Does following `next` pointers ever lead back to an already-visited node" → the defining question of **cycle detection in a linked structure**, structurally identical to the Happy Number problem's digit-transformation "cycle," just with actual pointers instead of a mathematical function.
- A singly linked list has exactly one `next` per node — this determinism (no branching, no choice of where to go next) is exactly the property that makes Floyd's cycle detection applicable, since it relies on modeling traversal as a deterministic sequence.
- The explicit O(1) memory follow-up is a direct hint toward the **two-pointer (slow/fast) technique** as the "true" optimal solution, distinguishing it from the simpler but space-costlier hash-set approach.
- Named pattern: **Floyd's Tortoise and Hare (Cycle Detection)**.

## 3. Core Idea

Use two pointers starting at `head`: a `slow` pointer that advances one node per step, and a `fast` pointer that advances two nodes per step. If there's no cycle, `fast` will reach the end (`null`) first, since it's moving twice as fast through a finite path. If there is a cycle, `fast` will eventually enter the cycle and start "lapping" `slow`, and because `fast` closes the gap to `slow` by exactly one node per step once both are inside the cycle, they are guaranteed to land on the exact same node at some point.

**Why it's correct:** in a list with no cycle, `fast` reaches `null` after finitely many steps (it can never revisit a node, since the list terminates), correctly signaling no cycle. In a list with a cycle, once both pointers have entered the cycle, the distance between them (measured going forward around the cycle) decreases by exactly one each iteration, since `fast` gains two steps of progress against `slow`'s one — this guarantees they meet within at most one full cycle length of iterations, since the gap can never "jump over" zero (it decreases by exactly one at a time, from a bounded starting distance).

**Invariant:** at every step of the traversal, if a cycle exists and both pointers are already inside it, the distance from `fast` to `slow` (measured in the forward traversal direction around the cycle) strictly decreases by one per iteration until it reaches zero, at which point `slow == fast`, correctly signaling the cycle's detection.

## 4. Approach 1 — Brute Force (HashSet of Visited Nodes)

1. Create a `Set<ListNode> visited` (using reference identity, not value equality, since node values can repeat even in an acyclic list).
2. Traverse the list one node at a time. For each node, if it's already in `visited`, a cycle has been detected — return `true`. Otherwise, add it to `visited` and move to the next node.
3. If traversal reaches `null`, return `false` — the list terminated normally with no cycle.

```java
public boolean hasCycleBruteForce(ListNode head) {
    Set<ListNode> visited = new HashSet<>();
    ListNode current = head;

    while (current != null) {
        if (visited.contains(current)) {
            return true;
        }
        visited.add(current);
        current = current.next;
    }

    return false;
}
```

**Time:** O(n) — each node is visited and inserted into the set at most once, with O(1) average-case set operations.
**Space:** O(n) auxiliary — the set can hold up to `n` node references before a cycle is detected or the list ends.

This is not optimal per the problem's own explicit follow-up: it correctly achieves O(n) time, but uses O(n) space, whereas the follow-up specifically asks for a constant-memory solution — Floyd's cycle detection achieves the same time complexity without needing to remember every node visited.

## 5. Approach 2 — Optimized (Floyd's Tortoise and Hare)

1. If `head == null` or `head.next == null`, there can't be a cycle with fewer than 2 nodes involved in a loop back to themselves in a meaningful way for this check — but more simply, just let the general loop handle it: initialize `slow = head`, `fast = head`.
2. Loop while `fast != null && fast.next != null` (this guards against `fast` or `fast.next` being null before attempting to advance `fast` by two):
   - Advance `slow = slow.next` (one step).
   - Advance `fast = fast.next.next` (two steps).
   - If `slow == fast`, a cycle has been detected — return `true`.
3. If the loop exits because `fast` (or `fast.next`) became `null`, the list terminated with no cycle — return `false`.

```java
public boolean hasCycle(ListNode head) {
    ListNode slow = head;
    ListNode fast = head;

    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;

        if (slow == fast) {
            return true;
        }
    }

    return false;
}
```

**Time:** O(n) — in the no-cycle case, `fast` reaches the end in at most n/2 iterations; in the cycle case, both pointers together take at most O(n) total steps to meet (the meeting happens within one cycle length of iterations after both pointers enter the cycle).
**Space:** O(1) auxiliary — only two pointer variables are used, regardless of the list's length, directly satisfying the follow-up's constant-memory requirement.

This is optimal: it matches the brute force's O(n) time while eliminating all auxiliary space beyond a constant number of pointers, which is exactly what the problem's follow-up asks for — the standard trade-off achieved by using pointer speed differential instead of memory to detect repetition.

## 6. Dry Run

`head = [3,2,0,-4]` with the tail (`-4`) connecting back to index `1` (the node with value `2`)

| step | slow | fast | slow==fast? |
|---|---|---|---|
| init | 3 | 3 | — |
| 1 | 2 (slow.next) | 0 (fast.next.next: 3→2→0) | no |
| 2 | 0 (slow.next) | 2 (fast.next.next: 0→-4→2, since -4's next is 2, the cycle target) | no |
| 3 | -4 (slow.next) | -4 (fast.next.next: 2→0→-4) | **yes** |

Exit: `slow == fast` at step 3, both landing on the node with value `-4`. Final answer: `true`, matching the expected output.

Contrast with `head = [1]`, `pos = -1` (no cycle): `slow = 1`, `fast = 1` initially; the loop condition `fast != null && fast.next != null` is immediately false since `fast.next` is `null` (single node, no next), so the loop body never executes, and the function returns `false` directly.

## 7. Complexity Summary

| Approach | Time | Space | Notes |
|---|---|---|---|
| HashSet of Visited Nodes | O(n) | O(n) aux | Correct but doesn't satisfy the O(1)-memory follow-up |
| Floyd's Tortoise and Hare | O(n) | O(1) aux | Matches the follow-up's constant-memory requirement |

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|---|---|---|
| Empty list (`head == null`) | No nodes at all, trivially no cycle | `fast` starts as `null`; loop condition `fast != null` is immediately false, returns `false` |
| Single node, no cycle (`head.next == null`) | Minimal acyclic case | `fast.next` is `null` on the first condition check, loop never executes, returns `false` |
| Single node with a self-loop (node's `next` points to itself) | Smallest possible cycle | `fast` and `slow` both start at `head`; after one iteration, `slow = head.next = head` and `fast = head.next.next = head`, so `slow == fast` immediately, correctly returning `true` |
| Two nodes forming a cycle (`pos = 0`, tail points back to head) | Small cycle, must still be detected correctly | Both pointers traverse the 2-node cycle at different speeds and converge within a couple of iterations |
| Long list with a cycle starting deep into it | Must correctly detect the cycle regardless of how far into the list it begins | Floyd's algorithm's correctness doesn't depend on where the cycle starts — once both pointers are inside the cycle (regardless of how many acyclic "tail" nodes preceded it), the gap-closing argument applies identically |
| Long list with no cycle at all | Must correctly terminate and report no cycle, not loop forever | `fast` reaching `null` (or `fast.next` being `null`) is guaranteed within a bounded number of steps for any finite acyclic list, so the loop always terminates correctly |

## 9. Java Notes

- `Set<ListNode>` in the brute-force approach relies on `ListNode`'s default `equals()`/`hashCode()` (reference identity, inherited from `Object`, since `ListNode` doesn't override them) — this is exactly the desired behavior here, since two distinct nodes could coincidentally hold the same `val` in an otherwise acyclic list, and identity (not value) is what determines whether a node has truly been revisited.
- The loop condition `fast != null && fast.next != null` must check both parts, and in that order (short-circuit `&&`) — checking `fast.next != null` first would throw a `NullPointerException` if `fast` itself were already `null`.
- `slow = slow.next` is always safe to execute without an additional null-check inside the loop, because the loop condition already guarantees `fast != null && fast.next != null`, and since `fast` only ever advances the same or further than `slow`, `slow` can never be null at this point in the loop body either.
- No recursion is used, so there's no stack-depth concern regardless of list length, unlike a naive recursive traversal approach which could risk a `StackOverflowError` on a very long list (though this specific problem's constraints, `n <= 10^4`, would likely be fine either way).

## 10. Common Mistakes

- Advancing `fast` by only one step per iteration (matching `slow`'s speed) instead of two — this degrades the technique to two pointers moving in lockstep, which can never detect a cycle since they'd always occupy the same relative position and never converge from different starting offsets. Fix: `fast` must always move exactly two steps for every one step `slow` takes.
- Checking `fast.next != null` before checking `fast != null` in the loop condition (or omitting the `fast != null` check entirely) — this throws a `NullPointerException` when `fast` itself has already become `null` from a previous iteration's `fast = fast.next.next` assignment. Fix: always check `fast != null` first, relying on short-circuit evaluation to avoid dereferencing a null `fast`.
- Comparing node *values* (`slow.val == fast.val`) instead of node *references* (`slow == fast`) to detect a meeting — this produces false positives whenever two distinct, non-cyclically-connected nodes happen to share the same value. Fix: always compare object references directly for linked-list node identity checks, not their stored values.
- Initializing `fast` one step ahead of `slow` at the very start (as is done in some other cycle-detection problems, like Happy Number) instead of starting both at `head` — for this specific problem, starting both at the same node and checking after the first movement (rather than before) is the standard, correct pattern; conflating the two initialization styles across different cycle-detection problems is a common source of off-by-one confusion.

## 11. Interview Takeaway

- **Trigger sentence:** "Does traversing a linked structure via a deterministic 'next' step ever revisit a node → Floyd's tortoise and hare, slow pointer one step, fast pointer two steps, per the O(1)-memory follow-up."
- **90-second explanation:** "I use two pointers starting at the head: a slow one moving one node at a time, and a fast one moving two nodes at a time. If the list has no cycle, the fast pointer will simply reach the end and return null-terminate first, since it's covering ground twice as quickly through a finite path — so I check for that as my no-cycle exit condition. If there is a cycle, once both pointers have entered it, the fast pointer gains exactly one node of relative progress on the slow pointer every iteration, so the gap between them shrinks by one each time until they land on the exact same node — that's my cycle-detected signal. This gives the same O(n) time as a simpler hash-set-of-visited-nodes approach, but without needing any extra memory beyond the two pointers themselves, directly satisfying the problem's O(1)-space follow-up."
- **Related problems:** 142 (Linked List Cycle II, find where the cycle starts), 202 (Happy Number, same technique on an implicit sequence), 287 (Find the Duplicate Number, Floyd's applied to an array treated as an implicit linked list).

## 12. Recall Questions

**Q:** Why is it guaranteed that the fast pointer will eventually meet the slow pointer if a cycle exists, rather than possibly skipping past it forever?
**A:** Once both pointers are inside the cycle, the fast pointer's relative distance ahead of the slow pointer decreases by exactly one node every iteration (since it gains two steps of progress against the slow pointer's one), so the gap can only shrink one step at a time down to exactly zero — it can never jump over zero and miss the meeting point.

**Q:** Why must the loop condition check `fast != null` before `fast.next != null`, specifically in that order?
**A:** Java's `&&` operator short-circuits, meaning it only evaluates the second condition if the first is true; checking `fast != null` first ensures `fast.next` is only ever accessed when `fast` itself is a valid, non-null reference, preventing a `NullPointerException`.

**Q:** Why does comparing `slow == fast` (reference equality) matter instead of comparing `slow.val == fast.val`?
**A:** Node values in a linked list are not guaranteed to be unique, so two entirely different, non-cyclically-connected nodes could coincidentally hold equal values, which would cause a value-based comparison to falsely report a cycle where none exists — only reference identity correctly identifies "this is truly the same node."

**Q:** Why does the fast pointer's failure to reach a cycle (i.e., reaching `null` instead) correctly prove the list has no cycle?
**A:** In a finite acyclic list, following `next` pointers must eventually terminate at `null`, since there's no way to revisit any node; if the fast pointer (which is strictly making at least as much progress as the slow pointer) reaches `null`, that's conclusive proof the list has a genuine end and therefore no cycle exists anywhere in it.

**Q:** What space-complexity trade-off does Floyd's algorithm make compared to the HashSet-based approach, and why does that trade-off matter here specifically?
**A:** Floyd's algorithm achieves the same O(n) time complexity as the HashSet approach but uses only O(1) auxiliary space instead of O(n), which directly answers the problem's explicit follow-up question asking for a constant-memory solution.

## 13. Final Code

```java
/**
 * Definition for singly-linked list.
 * class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode(int x) {
 *         val = x;
 *         next = null;
 *     }
 * }
 */
public class Solution {
    public boolean hasCycle(ListNode head) {
        ListNode slow = head;
        ListNode fast = head;

        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;

            if (slow == fast) {
                return true;
            }
        }

        return false;
    }
}
```

## 14. Self-Test

Given the head of a singly linked list, determine whether it contains a cycle, using O(1) memory. Re-derive: use two pointers starting at `head` — a slow one advancing one node per step, a fast one advancing two — and loop while `fast` and `fast.next` are both non-null; if the two pointers ever become equal (same node reference), a cycle exists; if the fast pointer reaches the end of the list first, there's no cycle.
