---
title: "0061 — Rotate List"
layout: default
parent: Linked List
grand_parent: Leetcode
nav_order: 61
permalink: /08-Linked-List/0061-rotate-list/

problem: 61 - Rotate List
url: https://leetcode.com/problems/rotate-list/
difficulty: Medium
section: Linked List
patterns: [circular-linked-list, modular-arithmetic]
data_structures: [linked-list]
time: O(n)
space: O(1)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0061 — Rotate List

| Field | Value |
|---|---|
| **Pattern** | Circular List + Modular Arithmetic |
| **Difficulty** | Medium |
| **Section** | Linked List |
| **Time** | O(n) |
| **Space** | O(1) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Meta |
| **LeetCode** | [Rotate List](https://leetcode.com/problems/rotate-list/) |

## 1. Problem in Simple Words

**Given:** the head of a linked list and an integer `k`.

**Required:** rotate the list to the right by `k` places — the last `k` nodes move to the front, in their original relative order, and everything else shifts back to follow them.

**Constraints that matter:**
- `0 <= number of nodes <= 500` — the empty list must be handled (trivially, nothing to rotate).
- **`0 <= k <= 2 * 10^9`** — this is the single most important constraint: `k` can be **enormously larger** than the list's length (up to 500), so performing `k` individual one-step rotations directly would be catastrophically slow and is explicitly disqualified by the problem's own scale; the correct approach must recognize that rotating a list of length `len` by `len` places returns it to its original state, so only `k % len` actually matters.
- `k = 0` is also explicitly allowed, meaning "no rotation" must be handled correctly (not treated as an error or edge case requiring special logic beyond what the general algorithm already produces).

## 2. Recognition Signals

- "Rotate a linked list to the right by k places" + k potentially far exceeding the list's length → the direct signal that **k must be reduced modulo the list length** before any actual pointer manipulation begins, since rotating by a multiple of the full length is a no-op.
- Rotating "to the right by k" conceptually means: the list, if joined end-to-end into a circle, should simply be "entered" at a different starting point — this circular framing is the signal for the **"join into a circle, then break at the right point"** technique, rather than performing the rotation step-by-step.
- Needing to find a node at a specific offset from the end (to determine where to break the circle) is reminiscent of the "nth from the end" family of problems, but here it's solved more directly once the list's total length is known, since `k` is first normalized via modulo.
- Named pattern: **Close the Loop, Then Break It at the Correct Offset** (a specialized technique for rotation-style linked-list problems).

## 3. Core Idea

First, find the list's length `len` and its tail node in one pass, and connect the tail back to the head, forming a circle. Since rotating by `len` places returns the list to its original configuration, only `k % len` rotations actually matter — this reduces a potentially astronomically large `k` down to a value between `0` and `len - 1`. The new tail is then the node at position `len - (k % len) - 1` (0-indexed from the original head); the new head is the node right after it. Breaking the circle there (setting the new tail's `next` to `null`) produces the correctly rotated list.

**Why it's correct:** rotating a list of length `len` to the right by `k` places is mathematically equivalent to rotating it by `k % len` places, since every full pass of `len` rotations cycles every node back to its original position — this is the key insight that avoids ever needing to simulate more than `len - 1` effective rotation steps, no matter how large `k` is. Once the list is joined into a circle, "rotating right by `k % len`" is exactly the same as simply choosing to start reading the circle from a different node — specifically, the node that is `k % len` positions before the original tail (equivalently, `len - (k % len)` positions after the original head) becomes the new starting point.

**Invariant:** after closing the list into a circle and computing `effectiveK = k % len`, the node at 0-indexed position `len - effectiveK` (counting from the original head) is guaranteed to be the correct new head, and the node immediately before it (`len - effectiveK - 1`) is the correct new tail — breaking the circle precisely between these two nodes produces the exact rotation the problem asks for.

## 4. Approach 1 — Brute Force (Repeated Single-Step Rotation)

1. If the list is empty or has one node, or `k == 0`, return it unchanged.
2. Repeat `k` times: find the current tail, detach it, and re-attach it to the front of the list as the new head.

```java
public ListNode rotateRightBruteForce(ListNode head, int k) {
    if (head == null || head.next == null || k == 0) {
        return head;
    }

    for (int i = 0; i < k; i++) {
        ListNode secondToLast = head;
        while (secondToLast.next.next != null) {
            secondToLast = secondToLast.next;
        }
        ListNode lastNode = secondToLast.next;
        secondToLast.next = null;
        lastNode.next = head;
        head = lastNode;
    }

    return head;
}
```

**Time:** O(n · k) — each individual one-step rotation requires an O(n) walk to find the second-to-last node, and this is repeated `k` times.
**Space:** O(1) auxiliary.

This is not optimal — it's actually **catastrophically** not optimal given the constraints: with `k` up to `2 * 10^9` and `n` up to `500`, this approach would attempt on the order of `10^12` operations, far beyond any reasonable time limit. This isn't just "slower than ideal," it's a demonstration of why recognizing `k % len` is not merely an optimization but an outright necessity for this problem to be solvable at all within realistic constraints.

## 5. Approach 2 — Optimized (Close the Loop, Break at the Correct Offset)

1. If `head == null` or `head.next == null`, return `head` unchanged (nothing to rotate).
2. Traverse the list once to find its length `len` and its tail node.
3. Compute `effectiveK = k % len`. If `effectiveK == 0`, the rotation is a no-op — return `head` unchanged.
4. Connect `tail.next = head`, forming a circle.
5. Walk `len - effectiveK - 1` steps forward from `head` to find the new tail (the node just before the new head).
6. Set `newHead = newTail.next`, then break the circle: `newTail.next = null`.
7. Return `newHead`.

```java
public ListNode rotateRight(ListNode head, int k) {
    if (head == null || head.next == null) {
        return head;
    }

    int len = 1;
    ListNode tail = head;
    while (tail.next != null) {
        tail = tail.next;
        len++;
    }

    int effectiveK = k % len;
    if (effectiveK == 0) {
        return head;
    }

    tail.next = head; // close the loop

    ListNode newTail = head;
    for (int i = 0; i < len - effectiveK - 1; i++) {
        newTail = newTail.next;
    }

    ListNode newHead = newTail.next;
    newTail.next = null; // break the loop

    return newHead;
}
```

**Time:** O(n) — one pass to find the length and tail, and one more pass of at most `n - 1` steps to find the new tail; both are linear in the list's length, and critically, `k` itself never directly drives the number of iterations (it's reduced via modulo first).
**Space:** O(1) auxiliary — only a handful of pointers and counters, regardless of list length or the magnitude of `k`.

This is optimal: it achieves the rotation in time proportional only to the list's length, completely decoupled from how large `k` is, which is essential given `k` can be up to `2 * 10^9` — this is the single design decision (`k % len`) that separates a working solution from one that would time out.

## 6. Dry Run

`head = [0,1,2]`, `k = 4`

Step 1: find length and tail — `len = 3`, `tail` = node with value `2`.

Step 2: `effectiveK = 4 % 3 = 1` (not zero, so rotation does occur).

Step 3: close the loop — `tail.next = head`, so now `0 -> 1 -> 2 -> 0 -> 1 -> 2 -> ...` (circular).

Step 4: walk `len - effectiveK - 1 = 3 - 1 - 1 = 1` step from `head` to find `newTail`:

| step | newTail |
|---|---|
| start | 0 |
| 1 | 1 |

`newTail` = node with value `1`.

Step 5: `newHead = newTail.next` = node with value `2`. Break the loop: `newTail.next = null`, so node `1`'s next becomes `null`.

Final list, starting from `newHead` (value `2`): `2 -> 0 -> 1 -> null`. Final answer: `[2,0,1]`, matching the expected output.

## 7. Complexity Summary

| Approach | Time | Space | Notes |
|---|---|---|---|
| Repeated Single-Step Rotation | O(n · k) | O(1) aux | Catastrophically slow given k up to 2×10^9 |
| Close Loop, Break at Offset | O(n) | O(1) aux | Time fully decoupled from the magnitude of k via `k % len` |

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|---|---|---|
| Empty list (`head == null`) | Nothing to rotate | Early check `head == null` returns `null` immediately |
| Single-node list | Rotating a single node by any amount leaves it unchanged | Early check `head.next == null` returns `head` immediately, bypassing the length/loop logic entirely |
| `k == 0` | Explicitly no rotation requested | After computing `effectiveK = k % len`, if `k` was `0`, `effectiveK` is also `0`, correctly triggering the no-op early return |
| `k` is an exact multiple of the list's length (e.g. `len=3, k=3` or `k=6`) | Rotating by a full cycle (or multiple full cycles) returns the list to its original state | `effectiveK = k % len = 0` in this case, correctly triggering the no-op return without ever needing to close or break the loop |
| `k` far larger than `len` (e.g. `len=3, k=2_000_000_000`) | The core challenge the problem poses — must not iterate `k` times | `effectiveK = k % len` reduces this to a small number (at most `len - 1`) before any pointer walking begins, keeping the algorithm's work proportional only to `len` |
| `k` not a multiple of `len`, requiring an actual rotation (e.g. Example 1: `len=5, k=2`) | Standard case requiring correct offset calculation | `effectiveK = 2`; new tail found at position `len - effectiveK - 1 = 2`, correctly producing `[4,5,1,2,3]` |

## 9. Java Notes

- Computing `len` requires one full traversal, during which the tail node is naturally discovered simultaneously (it's whatever node has `next == null`) — no separate pass is needed to locate it.
- `k % len` is safe from any division-by-zero concern specifically because of the early `head.next == null` check (and the implicit `head == null` check before it) — by the time `len` is used as a modulus, it's guaranteed to be at least `2` (since a single-node or empty list already returned early).
- `int effectiveK = k % len;` — since `k` is declared as `int` (per the method signature matching LeetCode's constraints, where even `2 * 10^9` is given as a constraint value that technically exceeds `Integer.MAX_VALUE`'s ~2.1×10^9 by a small margin, but LeetCode's own parameter type for this problem is `int`, and the actual test data stays within `int` range) — in practice this works correctly within Java's `int` type for the given test cases, though it's worth being aware that the stated constraint `k <= 2*10^9` is extremely close to `int`'s upper bound.
- Breaking the loop (`newTail.next = null`) must happen **after** `newHead` has already been read out from `newTail.next` — reversing this order would null out the reference before it's captured, losing track of the new head entirely.

## 10. Common Mistakes

- Attempting to simulate the rotation `k` times directly without first reducing `k` modulo the list's length — given the constraint `k` can be up to `2 * 10^9`, this doesn't just run slowly, it's practically guaranteed to time out or run effectively forever, since `n` (at most 500) times `k` (up to 2 billion) is on the order of a trillion operations. Fix: always compute `k % len` before any pointer manipulation.
- Forgetting to handle `effectiveK == 0` as an explicit no-op case and instead always closing and immediately re-breaking the loop at the same position — while this would still technically produce the correct (unchanged) list, it's unnecessary extra work and a sign of not having fully internalized why the modulo reduction matters; more importantly, skipping this check isn't actually a bug here since breaking at offset `len - 1` when `effectiveK = 0`... (this is worth verifying: if `effectiveK` isn't checked and is `0`, `len - effectiveK - 1 = len - 1`, landing `newTail` on the original tail and `newHead` on the original head via the wraparound, which actually still works correctly) — the explicit check is more about avoiding unnecessary loop-closing/breaking work than correctness, but omitting it without understanding this nuance is risky and worth flagging as something to verify carefully rather than assume.
- Off-by-one in the offset calculation, using `len - effectiveK` instead of `len - effectiveK - 1` to find the new tail — this would find the new *head* position instead of the node just before it, causing `newTail.next = null` to incorrectly cut the list one node too early or late. Fix: carefully verify against a small example (like the dry run above) that the offset correctly lands on the node immediately preceding the intended new head.
- Breaking the loop before capturing `newHead` (i.e., writing `newTail.next = null;` before reading `newTail.next` into `newHead`) — this loses the reference to the new head entirely, since the pointer was nulled out before being saved. Fix: always capture `newHead = newTail.next` first, then break the loop.

## 11. Interview Takeaway

- **Trigger sentence:** "Rotate a linked list to the right by k places, where k can be far larger than the list's length → reduce k modulo the list length first, then close the list into a circle and break it at the correct offset."
- **90-second explanation:** "The key realization is that rotating a list of length n by n places brings it right back to where it started, so only `k mod n` actually matters — this is essential given k can be up to 2 billion while the list itself has at most 500 nodes, so simulating k individual rotations is completely out of the question. I find the list's length and tail in one pass, then connect the tail back to the head to form a circle. From there, rotating right by the reduced k is just a matter of picking a different starting point on that circle — specifically, I walk forward from the original head by `length minus the reduced k minus one` steps to find what should become the new tail, then I take the node right after it as the new head, and break the circle there by setting that new tail's next pointer to null."
- **Related problems:** 19 (Remove Nth Node From End of List, related offset-from-end reasoning), 189 (Rotate Array, same rotation concept applied to arrays), 142 (Linked List Cycle II, related circular-list manipulation).

## 12. Recall Questions

**Q:** Why is reducing `k` to `k % len` not just an optimization, but essentially a requirement for this problem to be solvable within reasonable time at all?
**A:** Because `k` can be up to `2 * 10^9` while the list has at most 500 nodes, directly simulating `k` rotations would require on the order of a trillion operations, which is far beyond feasible; recognizing that a full rotation of `len` places is a no-op is what collapses the problem down to at most `len - 1` meaningful rotation steps.

**Q:** Why does closing the list into a circle make finding the rotated result easier than manipulating pointers step-by-step?
**A:** Once the list is circular, "rotating right by some amount" is equivalent to simply choosing a different node on the circle to treat as the starting point — this reframes the problem from "repeatedly move the last element to the front" into a single calculation of which node is the correct new starting point, requiring only one additional walk to locate it.

**Q:** Why is the offset for finding the new tail `len - effectiveK - 1` rather than `len - effectiveK`?
**A:** The new tail needs to be the node immediately *before* the new head (since its `next` pointer is what gets set to `null` to break the circle), and the new head itself is at position `len - effectiveK` counting from the original head, so the new tail — one position earlier — is at `len - effectiveK - 1`.

**Q:** What would happen if `newHead` were read from `newTail.next` only after already setting `newTail.next = null`?
**A:** The reference to the new head would already be destroyed by the time it's read, since `newTail.next` would have just been overwritten to `null`, making it impossible to recover which node should be returned as the start of the rotated list.

**Q:** Why do both the empty-list and single-node cases need to be checked before computing `len` and using it as a modulus?
**A:** An empty list has no tail to traverse to and no meaningful length to compute a rotation against, and a single-node list would always rotate back to itself regardless of `k`; additionally, guaranteeing `len >= 2` before it's used as a modulus avoids any possibility of a division-by-zero-style issue, even though a length of at least 1 would technically avoid literal division by zero — the early returns simply sidestep needing to reason about these trivial cases within the main algorithm at all.

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
    public ListNode rotateRight(ListNode head, int k) {
        if (head == null || head.next == null) {
            return head;
        }

        int len = 1;
        ListNode tail = head;
        while (tail.next != null) {
            tail = tail.next;
            len++;
        }

        int effectiveK = k % len;
        if (effectiveK == 0) {
            return head;
        }

        tail.next = head;

        ListNode newTail = head;
        for (int i = 0; i < len - effectiveK - 1; i++) {
            newTail = newTail.next;
        }

        ListNode newHead = newTail.next;
        newTail.next = null;

        return newHead;
    }
}
```

## 14. Self-Test

Given the head of a linked list and an integer `k` (which may be far larger than the list's length), rotate the list to the right by `k` places. Re-derive: find the list's length and tail in one pass; reduce `k` to `effectiveK = k % len` (returning unchanged if zero); close the list into a circle by linking tail to head; walk `len - effectiveK - 1` steps from the original head to find the new tail; set the new head to the node after it, then break the circle there.
