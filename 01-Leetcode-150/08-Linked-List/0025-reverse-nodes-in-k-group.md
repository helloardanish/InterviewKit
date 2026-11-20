---
title: "0025 — Reverse Nodes in k-Group"
layout: default
parent: Linked List
grand_parent: Leetcode
nav_order: 25
permalink: /08-Linked-List/0025-reverse-nodes-in-k-group/

problem: 25 - Reverse Nodes in k-Group
url: https://leetcode.com/problems/reverse-nodes-in-k-group/
difficulty: Hard
section: Linked List
patterns: [in-place-reversal, dummy-head, group-processing]
data_structures: [linked-list]
time: O(n)
space: O(1)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0025 — Reverse Nodes in k-Group

| Field | Value |
|---|---|
| **Pattern** | In-Place Reversal with Dummy Head + Group Processing |
| **Difficulty** | Hard |
| **Section** | Linked List |
| **Time** | O(n) — each node reversed once |
| **Space** | O(1) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Meta |
| **LeetCode** | [Reverse Nodes in k-Group](https://leetcode.com/problems/reverse-nodes-in-k-group/) |

## 1. Problem in Simple Words

**Given:** the head of a linked list and a positive integer `k`.

**Required:** reverse the nodes of the list in groups of `k` at a time; if the number of remaining nodes at the end is fewer than `k`, that final partial group is left unchanged (not reversed).

**Constraints that matter:**
- `1 <= k <= n <= 5000` — `k` is always valid (never exceeds the list's length), so there's no need to validate `k` against the list size separately.
- **"You may not alter the values in the list's nodes, only nodes themselves may be changed"** — this is a critical, explicit constraint: the solution must physically relink node pointers to achieve the reversal, rather than taking the much simpler shortcut of just copying values into a new order while keeping the original node objects (and their `next` pointers) untouched.
- **Follow-up: "Can you solve the problem in O(1) extra memory space?"** — this directly invites comparing a solution that uses auxiliary space proportional to the group size (e.g., a stack) against a solution that performs the reversal using only a constant number of pointer variables, regardless of `k` or `n`.

## 2. Recognition Signals

- "Reverse nodes in groups of k" → an extension of the simpler "reverse an entire linked list" problem, but repeated across consecutive fixed-size chunks, with the added complexity of needing to correctly **reconnect each reversed group to its neighbors**.
- "Leftover nodes fewer than k should remain unchanged" → this requires a **lookahead check**: before reversing a group, you must first confirm that at least `k` nodes actually remain, which means counting or peeking ahead before committing to reverse.
- "Only nodes may be changed, not values" + the O(1) space follow-up → together these rule out both a value-copying shortcut *and* a value-extraction-into-an-array approach, strongly pointing toward **in-place pointer reversal using a constant number of temporary pointer variables**, the same core technique as reversing a full linked list, just applied repeatedly to bounded-size segments.
- Named pattern: **In-Place Segment Reversal with Group-Boundary Reconnection** (a repeated application of the classic three-pointer linked-list reversal technique).

## 3. Core Idea

Process the list one group of `k` nodes at a time. For each group: first check whether at least `k` nodes remain (if not, stop — the remaining nodes are left as-is); if there are enough, reverse just those `k` nodes in place using the standard three-pointer (`prev`, `current`, `next`) reversal technique, then reconnect the reversed group's new head and new tail to the surrounding list (the node before the group, and whatever comes after it) correctly. Repeat for the next group, starting from where the just-reversed group's original head (now its tail) left off.

**Why it's correct:** reversing a bounded segment of exactly `k` nodes via the standard pointer-flipping technique is a well-established, directly provable operation (each step flips exactly one `next` pointer backward, and after `k` steps, the segment's direction is fully inverted). The remaining correctness burden is purely about **reconnection**: the node immediately before the group must point to what was the group's *last* node (now its new first node after reversal), and what was the group's *first* node (now its new last node) must point to wherever the next group's processing will eventually connect — this is why a dummy node and careful tracking of "the node before this group" and "the group's original head" are essential scaffolding around the core reversal step.

**Invariant:** after fully processing some number of complete groups, the portion of the list from the dummy node's `next` up through the last fully-reversed group is a completely valid, correctly-connected linked list reflecting all processed groups in their final (reversed) form, and the node most recently used as "the tail of the last processed group" always correctly points to the start of whatever comes next (either the start of the next group to be processed, or the unchanged leftover tail).

## 4. Approach 1 — Non-Optimal (Stack-Based Reversal, O(k) Extra Space Per Group)

1. Use a dummy node before `head`, and a `groupPrev` pointer initialized to the dummy, tracking the node just before the current group.
2. For each group: walk ahead to check if `k` nodes remain; if not, stop and leave the rest unchanged.
3. If `k` nodes do remain, push all `k` node references (not values) onto a `Deque<ListNode>` used as a stack.
4. Pop the stack one node at a time, relinking each popped node's `next` to the following popped node (or to whatever follows the group, for the very last pop), and set `groupPrev.next` to the first popped node (the group's new head).
5. Advance `groupPrev` to the end of the now-reversed group (the original first node of the group, now its tail) and repeat for the next group.

```java
public ListNode reverseKGroupStack(ListNode head, int k) {
    ListNode dummy = new ListNode(0, head);
    ListNode groupPrev = dummy;

    while (true) {
        ListNode check = groupPrev;
        for (int i = 0; i < k; i++) {
            check = check.next;
            if (check == null) {
                return dummy.next; // fewer than k nodes remain, leave as-is
            }
        }

        Deque<ListNode> stack = new ArrayDeque<>();
        ListNode node = groupPrev.next;
        for (int i = 0; i < k; i++) {
            stack.push(node);
            node = node.next;
        }

        ListNode afterGroup = node; // node right after this group
        ListNode newGroupHead = stack.peek();
        groupPrev.next = newGroupHead;

        ListNode current = stack.pop();
        while (!stack.isEmpty()) {
            current.next = stack.peek();
            current = stack.pop();
        }
        current.next = afterGroup; // current is now the original head, new tail of the group

        groupPrev = current;
    }
}
```

**Time:** O(n) — each node is pushed and popped exactly once across the whole list's processing.
**Space:** O(k) auxiliary — the stack holds up to `k` node references at any given time during each group's processing (even though this is "only" O(k), not O(n), it still fails the problem's explicit O(1) follow-up whenever `k > 1`).

This is not optimal with respect to the problem's own explicit follow-up: it correctly achieves O(n) time and respects the "don't alter values" constraint (it only ever relinks `next` pointers, pushing and popping node *references*), but it uses O(k) auxiliary space for the stack, falling short of the O(1) space the follow-up specifically asks for.

## 5. Approach 2 — Optimized (In-Place Three-Pointer Reversal, True O(1) Extra Space)

1. Use a dummy node before `head`, and `groupPrev` initialized to the dummy.
2. Loop: first check whether `k` nodes remain starting from `groupPrev.next` (walk ahead `k` times without modifying anything yet); if fewer than `k` remain, stop and return `dummy.next`.
3. If `k` nodes are confirmed available, reverse them in place using the standard three-pointer technique: `prev = null`, `current = groupPrev.next` (the group's original head, destined to become its tail); for `k` iterations, save `current.next`, point `current.next` to `prev`, then advance `prev = current` and `current = saved next`.
4. After the reversal loop, `prev` is the new head of this group, and `groupPrev.next` (still the *original* head, now the group's tail) needs its `next` set to `current` (whatever follows the group — either the start of the next group or the leftover tail).
5. Reconnect: save the group's original head (now tail) in a variable before the reversal loop overwrites `groupPrev.next`; after reversing, set `groupPrev.next = prev` (the new group head) and `originalHead.next = current` (link the new tail to whatever follows).
6. Advance `groupPrev = originalHead` (the just-finished group's new tail) and repeat for the next group.

```java
public ListNode reverseKGroup(ListNode head, int k) {
    ListNode dummy = new ListNode(0, head);
    ListNode groupPrev = dummy;

    while (true) {
        ListNode check = groupPrev;
        for (int i = 0; i < k; i++) {
            check = check.next;
            if (check == null) {
                return dummy.next;
            }
        }

        ListNode groupHead = groupPrev.next; // will become the group's tail after reversal
        ListNode prev = null;
        ListNode current = groupHead;

        for (int i = 0; i < k; i++) {
            ListNode nextNode = current.next;
            current.next = prev;
            prev = current;
            current = nextNode;
        }

        // prev is now the new head of this reversed group; current is the node right after the group
        groupPrev.next = prev;
        groupHead.next = current;

        groupPrev = groupHead;
    }
}
```

**Time:** O(n) — each node is visited a constant number of times (once for the lookahead check, once during the reversal loop), across all groups combined, giving O(n) overall despite the nested-loop appearance.
**Space:** O(1) auxiliary — only a fixed, small number of pointer variables (`dummy`, `groupPrev`, `check`, `groupHead`, `prev`, `current`, `nextNode`) are used, regardless of `k` or `n`, directly satisfying the follow-up's O(1)-space requirement.

This is optimal: it matches the stack-based approach's O(n) time while reducing auxiliary space from O(k) down to true O(1), by reversing each group's pointers directly in place rather than staging them in any auxiliary data structure — this is the technique the problem's own follow-up is explicitly steering toward.

## 6. Dry Run

`head = [1,2,3,4,5]`, `k = 3`

**Lookahead check for the first group:** walk 3 steps from `dummy` — reaches node `3`, confirming at least 3 nodes remain (`1,2,3`); continues to be valid since a 4th and 5th node exist beyond.

**Reversing the first group `[1,2,3]`:** `groupHead = 1`, `prev = null`, `current = 1`.

| iteration | current (before) | nextNode saved | current.next set to | prev after | current after |
|---|---|---|---|---|---|
| 1 | 1 | 2 | prev (null) | 1 | 2 |
| 2 | 2 | 3 | prev (1) | 2 | 3 |
| 3 | 3 | 4 | prev (2) | 3 | 4 |

After the loop: `prev = 3` (new group head), `current = 4` (node right after the group). Reconnect: `groupPrev.next = 3` (dummy now points to 3), `groupHead.next = 4` (node `1`, the original head/new tail, points to `4`). List so far: `dummy -> 3 -> 2 -> 1 -> 4 -> 5`. Advance `groupPrev = groupHead = 1`.

**Lookahead check for the second group:** walk 3 steps from node `1` (the new `groupPrev`) — reaches `4`, then `5`, then tries one more step and gets `null` — fewer than 3 nodes remain (only `4,5`). Stop; return `dummy.next`.

Final list: `3 -> 2 -> 1 -> 4 -> 5`, matching the expected output `[3,2,1,4,5]`.

## 7. Complexity Summary

| Approach | Time | Space | Notes |
|---|---|---|---|
| Stack-Based Reversal | O(n) | O(k) aux | Correct, respects value-immutability, but fails the O(1)-space follow-up |
| In-Place Three-Pointer Reversal | O(n) | O(1) aux | True constant extra space, satisfies the follow-up directly |

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|---|---|---|
| `k = 1` | Reversing "groups of 1" should leave the list completely unchanged | Each "group" reversal loop runs exactly once, which trivially reconnects each single node to itself in the same relative position — the list ends up identical to the input |
| `k` equals the full list length `n` | The entire list becomes one single reversed group | The lookahead check confirms exactly `n` nodes remain for the first (and only) group; after reversing, the second group's lookahead immediately finds `0 < k` remaining nodes and stops |
| List length not a multiple of `k` (e.g. Example 2: n=5, k=3) | The final `n mod k` nodes must remain in their original order, untouched | The lookahead check at the start of each group's processing correctly detects when fewer than `k` nodes remain and returns immediately, before any pointers in that leftover segment are touched |
| List length exactly a multiple of `k` | Every node must be included in some reversed group, with none left over | The lookahead check for what would be one group past the last correctly finds `0` remaining nodes (immediately hits `null`), stopping cleanly with the entire list fully processed |
| Single-node list, `k = 1` | Minimal valid input | One group of size 1 is "reversed" (a no-op for a single node), list returned unchanged |
| Reconnecting a reversed group's tail to the correct next node, specifically when a reversed group is immediately followed by leftover (unreversed) nodes | Must not lose the connection between the last reversed group and the leftover tail | `groupHead.next = current` (where `current` is whatever follows the group, captured directly from the reversal loop's final state) correctly links the new tail to the leftover segment, since `current` was never contingent on how many more full groups exist afterward |

## 9. Java Notes

- The dummy node pattern here serves the same purpose as in other linked-list modification problems: it provides a stable, always-valid "node before the first group" so the very first group's reversal doesn't need special-case logic distinct from any subsequent group's reversal.
- The lookahead loop (`for (int i = 0; i < k; i++) { check = check.next; if (check == null) return dummy.next; }`) deliberately does **not** modify any pointers — it's purely a read-only check, which is essential since discovering partway through that fewer than `k` nodes remain must leave the list completely untouched up to that point.
- `groupHead` is captured **before** the reversal loop begins, specifically because after reversal, `groupPrev.next` (and the local variable tracking the "current head") no longer point to the original first node — without saving this reference ahead of time, there'd be no way to know which node needs its `next` pointer set to whatever follows the group.
- This solution reuses the exact same three-pointer (`prev`, `current`, `nextNode`) technique used for reversing an entire linked list (LeetCode 206) — recognizing that this problem is "repeated bounded applications of that exact technique, with added reconnection logic" is the key insight that makes the solution approachable despite its Hard difficulty rating.

## 10. Common Mistakes

- Forgetting to perform the lookahead check before reversing, and instead just attempting to reverse up to `k` nodes and handling a `null` encountered mid-reversal — this risks either crashing, or (worse) partially reversing a group that should have been left entirely untouched because it has fewer than `k` nodes. Fix: always fully verify that `k` nodes are available *before* modifying any pointers in that prospective group.
- Not saving `groupHead` (the group's original first node) before starting the reversal loop — without this reference, after the loop completes there's no way to identify which node needs to be linked forward to whatever follows the group, since that node is now buried at the *end* of the reversed group, not accessible via `groupPrev.next` (which now points to the new head instead). Fix: always capture a reference to the pre-reversal head before the pointer-flipping loop begins.
- Advancing `groupPrev` to the wrong node after finishing a group — it must become the group's new **tail** (which is the original head, saved as `groupHead`), not the new head (`prev`) and not simply "whatever `current` ended up as." Fix: always set `groupPrev = groupHead` after each group's reversal and reconnection are complete.
- Extracting node *values* into an array, sorting/reversing the array, and writing the values back into the existing nodes in the new order — this technically achieves the visually correct output but **directly violates** the problem's explicit constraint that only node structure (pointers), not values, may be altered; this is exactly the kind of shortcut the problem statement is written to prevent, and would likely be rejected as not satisfying the problem's actual requirements even if it passed basic output-matching tests.

## 11. Interview Takeaway

- **Trigger sentence:** "Reverse a linked list in fixed-size groups, leaving a shorter final group untouched → apply the standard three-pointer in-place reversal technique repeatedly per group, with a dummy head and careful before/after reconnection at each group boundary."
- **90-second explanation:** "This is really the classic 'reverse a linked list' technique, just applied repeatedly to bounded chunks of size k instead of the whole list at once, with extra bookkeeping to stitch each reversed chunk back into the surrounding list correctly. For each group, I first walk ahead k steps without touching any pointers, purely to confirm at least k nodes actually remain — if not, I stop immediately and leave everything as-is, since a short final group must stay unreversed. If there are enough nodes, I save a reference to the group's original head, since after reversal that node becomes the group's *tail*, and I'll need to connect it forward to whatever comes next. Then I run the standard prev/current/next-pointer flip exactly k times. Afterward, I connect the node before this group to the new head of the reversed group, and connect the saved original head (now the tail) to whatever node came right after the group. I advance my 'before the next group' pointer to that saved original head, and repeat. All of this happens with just a handful of pointer variables — no array, no stack, no extra structure proportional to k or n."
- **Related problems:** 206 (Reverse Linked List, the foundational single-pass version), 24 (Swap Nodes in Pairs, the k=2 special case of this exact problem), 92 (Reverse Linked List II, reversing one bounded sub-range rather than repeated groups).

## 12. Recall Questions

**Q:** Why is the lookahead check (confirming k nodes remain) performed as a completely separate, read-only step before any reversal begins, rather than being combined with the reversal loop itself?
**A:** If a group turns out to have fewer than k nodes, the problem requires that segment to be left entirely unmodified; combining the check with in-progress reversal risks partially reversing pointers before discovering the shortfall, which would corrupt a segment that was supposed to remain untouched.

**Q:** Why must `groupHead` (the original first node of the group) be saved before the reversal loop runs, rather than being derivable afterward?
**A:** After the three-pointer reversal, the original head ends up at the *tail* end of the now-reversed group, and the only way to correctly link that tail forward to whatever follows the group is to have retained a direct reference to it from before the reversal destroyed the ability to find it via the group's old starting pointer.

**Q:** Why does `groupPrev` get updated to `groupHead` (not `prev`, the new group head) after each group is processed?
**A:** `groupPrev` needs to represent "the node immediately before the *next* group to be processed," and since the just-finished group is now reversed, its new tail — which is exactly the saved `groupHead` — is the node that sits immediately before wherever the next group begins.

**Q:** Why would copying values into a new order instead of relinking actual node pointers fail to satisfy this problem's requirements, even if it produced a list with the visually correct sequence of values?
**A:** The problem explicitly states that only the nodes themselves (their structural linkage) may be changed, not the values they hold; a value-copying approach leaves the original node objects and their relationships unchanged in a structural sense, which directly contradicts this stated constraint regardless of whether the final printed output happens to look correct.

**Q:** How does this problem's core reversal mechanism relate to the simpler "reverse an entire linked list" problem?
**A:** It's the exact same three-pointer in-place reversal technique, just bounded to run for only k iterations at a time instead of running until the end of the whole list, with additional logic layered around it to correctly detect group boundaries and reconnect each reversed segment to its neighbors.

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
    public ListNode reverseKGroup(ListNode head, int k) {
        ListNode dummy = new ListNode(0, head);
        ListNode groupPrev = dummy;

        while (true) {
            ListNode check = groupPrev;
            for (int i = 0; i < k; i++) {
                check = check.next;
                if (check == null) {
                    return dummy.next;
                }
            }

            ListNode groupHead = groupPrev.next;
            ListNode prev = null;
            ListNode current = groupHead;

            for (int i = 0; i < k; i++) {
                ListNode nextNode = current.next;
                current.next = prev;
                prev = current;
                current = nextNode;
            }

            groupPrev.next = prev;
            groupHead.next = current;

            groupPrev = groupHead;
        }
    }
}
```

## 14. Self-Test

Given the head of a linked list and a positive integer `k`, reverse the nodes in groups of `k`, leaving any final group with fewer than `k` nodes unchanged, without altering node values. Re-derive: use a dummy head and a `groupPrev` pointer; before reversing each group, walk ahead `k` steps to confirm enough nodes remain (stop if not); if confirmed, save the group's original head, reverse exactly `k` nodes with the standard three-pointer technique, then link `groupPrev` to the new head and the saved original head (now the tail) to whatever follows; advance `groupPrev` to that saved head and repeat.
