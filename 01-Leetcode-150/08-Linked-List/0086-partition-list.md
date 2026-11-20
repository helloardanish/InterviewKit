---
title: "0086 — Partition List"
layout: default
parent: Linked List
grand_parent: Leetcode
nav_order: 86
permalink: /08-Linked-List/0086-partition-list/

problem: 86 - Partition List
url: https://leetcode.com/problems/partition-list/
difficulty: Medium
section: Linked List
patterns: [dummy-head, two-pointer-partition]
data_structures: [linked-list]
time: O(n)
space: O(1)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0086 — Partition List

| Field | Value |
|---|---|
| **Pattern** | Dummy Head + Two-List Partition |
| **Difficulty** | Medium |
| **Section** | Linked List |
| **Time** | O(n) |
| **Space** | O(1) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Meta |
| **LeetCode** | [Partition List](https://leetcode.com/problems/partition-list/) |

## 1. Problem in Simple Words

**Given:** the head of a linked list and an integer `x`.

**Required:** rearrange the list so that all nodes with values **less than** `x` come before all nodes with values **greater than or equal to** `x`, while **preserving the original relative order** within each of the two groups.

**Constraints that matter:**
- `0 <= number of nodes <= 200` — the empty list must be handled trivially (nothing to partition).
- `-100 <= Node.val <= 100` and `-200 <= x <= 200` — `x` can fall entirely outside the range of any node value (e.g., smaller than every node or larger than every node), meaning one of the two partitions could end up completely empty, which must still produce a correct, well-formed result.
- **"Preserve the original relative order of the nodes in each of the two partitions"** — this is the single most important constraint: a naive approach that just groups nodes by comparison to `x` without carefully maintaining order (e.g., sorting, or building the groups in a way that reverses order) would violate this explicit requirement, even if the "less than x before greater-or-equal" grouping itself were correct.

## 2. Recognition Signals

- "Partition into two groups based on a comparison, preserve relative order within each group" → this is a classic **stable partitioning** problem, structurally similar to the partition step of quicksort but with the explicit stability requirement that quicksort's in-place partition typically does *not* provide.
- Needing to build **two separate sub-lists** that will later be joined together → the signal for using **two separate dummy heads**, one accumulating the "less than x" group and one accumulating the "greater than or equal to x" group, each built up independently in a single pass.
- "Preserve relative order" specifically rules out any swap-based or value-overwriting in-place partition technique (like array partitioning, which doesn't naturally preserve order) — the correct approach for a linked list is to simply **relink existing nodes** into two chains, which preserves order automatically since nodes are appended to each chain in the order they're encountered.
- Named pattern: **Stable Two-Way Partition via Dual Dummy Heads**.

## 3. Core Idea

Walk the original list once, appending each node to one of two separate chains depending on whether its value is less than `x` or greater-than-or-equal to `x` — each chain built with its own dummy head and a tail pointer that always appends to the end. Since nodes are processed strictly in their original order and each one is appended to the end of whichever chain it belongs to, both chains automatically preserve the original relative order of their members. Finally, join the two chains together (less-than chain, followed by the greater-or-equal chain) to form the result.

**Why it's correct:** appending to the tail of a growing chain, in the order nodes are visited, is inherently order-preserving — this is the defining property of a **stable** partition. By using two entirely separate chains (rather than trying to interleave reordering within a single list), there's no risk of accidentally disturbing the relative order within either group, since each group is built completely independently of the other until the final join step.

**Invariant:** at any point during the single pass, the "less than x" chain (read from its dummy's `next` to its current tail) holds exactly the subsequence of originally-encountered nodes with value `< x`, in their original order, and symmetrically for the "greater than or equal to x" chain — so once the full list has been scanned, simply concatenating the two chains produces the correctly partitioned result.

## 4. Approach 1 — Brute Force (Extract Values to Array, Stable Partition, Rebuild)

1. Traverse the list once, copying all node values into an `ArrayList<Integer>` (or array).
2. Build a new list of values by first appending all values `< x` (in their original order), then all values `>= x` (in their original order) — effectively a stable partition performed on the extracted array.
3. Traverse the original list a second time (or build brand-new nodes), overwriting each node's value with the corresponding value from the partitioned array, in order.

```java
public ListNode partitionBruteForce(ListNode head, int x) {
    List<Integer> values = new ArrayList<>();
    for (ListNode node = head; node != null; node = node.next) {
        values.add(node.val);
    }

    List<Integer> partitioned = new ArrayList<>();
    for (int v : values) {
        if (v < x) {
            partitioned.add(v);
        }
    }
    for (int v : values) {
        if (v >= x) {
            partitioned.add(v);
        }
    }

    ListNode current = head;
    for (int v : partitioned) {
        current.val = v;
        current = current.next;
    }

    return head;
}
```

**Time:** O(n) — three linear passes (extract, stable-partition, overwrite), each O(n), summing to O(n) overall.
**Space:** O(n) auxiliary for the two `ArrayList`s holding all node values.

This is not optimal in terms of space: it works correctly and is still linear time, but it needs O(n) extra memory to hold every value twice (once in the original extraction, once in the partitioned order), and it mutates node *values* rather than actual list structure — a valid approach, but one that misses the cleaner, more memory-efficient technique of directly relinking the existing nodes.

## 5. Approach 2 — Optimized (Two Dummy Heads, Relink Existing Nodes)

1. Create two dummy nodes: `lessDummy` (head of the "less than x" chain) and `greaterDummy` (head of the "greater than or equal to x" chain), along with two tail pointers `lessTail` and `greaterTail`, each initialized to their respective dummy.
2. Walk the original list once with a `current` pointer. For each node:
   - If `current.val < x`: append it to the less-than chain (`lessTail.next = current; lessTail = current;`).
   - Otherwise: append it to the greater-or-equal chain (`greaterTail.next = current; greaterTail = current;`).
   - Advance `current = current.next`.
3. After the scan, join the two chains: `lessTail.next = greaterDummy.next` (attach the greater-or-equal chain right after the less-than chain).
4. **Terminate the list properly**: `greaterTail.next = null` (essential, since the original greater-or-equal nodes might still be linked to whatever followed them in the original list).
5. Return `lessDummy.next`.

```java
public ListNode partition(ListNode head, int x) {
    ListNode lessDummy = new ListNode(0);
    ListNode greaterDummy = new ListNode(0);
    ListNode lessTail = lessDummy;
    ListNode greaterTail = greaterDummy;

    ListNode current = head;
    while (current != null) {
        if (current.val < x) {
            lessTail.next = current;
            lessTail = current;
        } else {
            greaterTail.next = current;
            greaterTail = current;
        }
        current = current.next;
    }

    greaterTail.next = null;
    lessTail.next = greaterDummy.next;

    return lessDummy.next;
}
```

**Time:** O(n) — a single pass through the original list, with O(1) work (a comparison and a pointer append) per node.
**Space:** O(1) auxiliary — only two dummy nodes and two tail pointers, regardless of list length; the existing nodes are relinked in place rather than copied or recreated.

This is optimal: it matches the brute force's O(n) time while reducing auxiliary space from O(n) down to O(1), by directly reusing and relinking the original nodes instead of extracting and reconstructing values — this is the standard, more elegant technique for this class of linked-list partitioning problem.

## 6. Dry Run

`head = [1,4,3,2,5,2]`, `x = 3`

| current.val | < x ? | action | lessTail after | greaterTail after |
|---|---|---|---|---|
| 1 | yes | append to less chain | 1 | (dummy) |
| 4 | no | append to greater chain | 1 | 4 |
| 3 | no | append to greater chain | 1 | 3 |
| 2 | yes | append to less chain | 2 | 3 |
| 5 | no | append to greater chain | 2 | 5 |
| 2 | yes | append to less chain | 2 (second 2) | 5 |

After the scan: less chain is `1 -> 2 -> 2` (the two 2's, in original relative order), greater chain is `4 -> 3 -> 5` (in original relative order).

Join: `greaterTail.next = null` (terminates the greater chain after `5`); `lessTail.next = greaterDummy.next` connects the less chain's end (second `2`) to the greater chain's start (`4`).

Final list, from `lessDummy.next`: `1 -> 2 -> 2 -> 4 -> 3 -> 5`. Final answer: `[1,2,2,4,3,5]`, matching the expected output.

## 7. Complexity Summary

| Approach | Time | Space | Notes |
|---|---|---|---|
| Extract Values, Stable Partition, Rebuild | O(n) | O(n) aux | Correct and stable, but copies all values into arrays |
| Two Dummy Heads, Relink Existing Nodes | O(n) | O(1) aux | Reuses original nodes directly, no value copying |

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|---|---|---|
| Empty list (`head == null`) | Nothing to partition | `current = head = null`; the `while` loop never executes; both chains remain empty, `lessDummy.next` is `null`, and `greaterDummy.next` is also `null`, so joining and terminating correctly produces `null` as the final result |
| All values `< x` | The greater-or-equal chain is entirely empty | `greaterTail` never advances past `greaterDummy`; `greaterDummy.next` stays `null`; `lessTail.next = greaterDummy.next` correctly sets the less chain's end to `null`, producing just the less-than chain unchanged |
| All values `>= x` | The less-than chain is entirely empty | `lessTail` never advances past `lessDummy`; `lessTail.next = greaterDummy.next` directly attaches the entire greater-or-equal chain right after the (still-empty) less dummy, so `lessDummy.next` correctly becomes the start of the greater chain |
| `x` smaller than every value, or larger than every value | One partition is empty by definition of the comparison | Handled identically to the above two cases — the algorithm doesn't need to know in advance which partition will be empty; it naturally falls out of the single-pass comparison logic |
| Duplicate values, some equal to `x` exactly (e.g. values of exactly `x`) | Must go into the "greater than or equal to" group, not the "less than" group | The comparison `current.val < x` is strict, so a value exactly equal to `x` correctly routes to the greater-or-equal chain |
| Forgetting to terminate the greater chain (a common subtle bug, called out separately here) | The original list's nodes retain their old `next` pointers unless explicitly overwritten | `greaterTail.next = null` is essential — without it, the last node of the greater-or-equal chain might still point to whatever followed it in the *original* list (now incorrectly reachable, potentially creating a cycle or a malformed, too-long list) |

## 9. Java Notes

- Using two separate dummy nodes (`lessDummy`, `greaterDummy`) rather than trying to track insertion points within a single combined list is what keeps the logic clean — each chain is built completely independently, with no need to track or compare positions across the two groups until the final join.
- `greaterTail.next = null` is easy to forget but is **not optional**: since this solution relinks the *original* node objects rather than creating new ones, the last node appended to the greater-or-equal chain still carries whatever `next` reference it had from the original list (unless it happened to already be the original tail) — explicitly nulling it out is required to produce a properly terminated list.
- The order of the two final steps matters: `lessTail.next = greaterDummy.next` must happen, and `greaterTail.next = null` must also happen, but as long as both happen before returning, their relative order with respect to each other doesn't matter (they modify different nodes' `next` pointers) — though terminating the greater chain is sometimes written first as a matter of style, to "close off" that chain before attaching it.
- Reusing the original `ListNode` objects (rather than allocating new ones, as the brute-force value-copying approach implicitly could also avoid but doesn't take full advantage of) is what allows this approach to achieve true O(1) auxiliary space, beyond just avoiding array allocation.

## 10. Common Mistakes

- Forgetting to set `greaterTail.next = null` after joining the chains — this can leave a dangling reference to whatever came after the last greater-or-equal node in the *original* list, potentially creating an incorrect, overly long list, or in rarer cases (if that dangling node happens to loop back) an actual cycle. Fix: always explicitly terminate the greater-or-equal chain before or after joining, but definitely before returning.
- Using `<=` instead of `<` (or vice versa) in the comparison `current.val < x` — this misclassifies nodes with value exactly equal to `x`, placing them in the wrong partition relative to the problem's explicit "greater than or equal to x" definition for the second group. Fix: strictly use `<` for the less-than group, letting everything else (including exact equality) fall into the other group.
- Trying to partition the list in place with a single pass and swap-based logic (similar to array partitioning techniques) instead of building two separate chains — this risks violating the explicit "preserve relative order" requirement, since swap-based partitioning on a linked list (or even conceptually on an array) typically does not guarantee stability without additional careful bookkeeping. Fix: use the two-chain approach, which preserves order automatically as a natural consequence of append-only construction.
- Not using dummy heads and instead trying to track "the first node of each partition" with nullable variables requiring initial-assignment special-casing — this reintroduces the classic "is this the first node or not" branching complexity that dummy heads are specifically designed to eliminate. Fix: always start both chains with a dummy node to unconditionally support the append-to-tail pattern from the very first matching node.

## 11. Interview Takeaway

- **Trigger sentence:** "Partition a linked list into two value-based groups while preserving relative order within each → build two separate chains with their own dummy heads, appending each node to the appropriate chain in a single pass, then join them."
- **90-second explanation:** "Since I need to preserve the original relative order within each group, I can't just swap nodes around in place the way an array partition might — instead, I build two completely separate chains as I scan the list once: one for values less than x, one for values greater than or equal to x. Each chain has its own dummy head and a tail pointer, so I can always append the current node directly to the end of whichever chain it belongs to. Because I process nodes strictly in their original order and only ever append to the end of a chain, each chain naturally ends up in the correct original relative order with no extra effort. Once I've scanned the whole list, I just connect the end of the less-than chain to the start of the greater-or-equal chain, and critically, I make sure to null out the greater-or-equal chain's tail, since those nodes still carry their original next pointers that could otherwise leave the list malformed."
- **Related problems:** 148 (Sort List, a related but more complex linked-list reordering problem), 328 (Odd Even Linked List, same dual-chain-building technique), 75 (Sort Colors, the array analog of a stable multi-way partition).

## 12. Recall Questions

**Q:** Why does building two separate chains, rather than trying to rearrange a single list in place, naturally guarantee the "preserve relative order" requirement?
**A:** Because nodes are visited in their original order and each one is immediately appended to the tail of whichever chain it belongs to, the relative order within each chain is simply the order in which matching nodes were encountered during the single pass — there's no reordering step that could disturb this.

**Q:** Why is `greaterTail.next = null` a required step, specifically because this solution reuses the original node objects?
**A:** Since the algorithm relinks existing `ListNode` objects rather than creating fresh copies, the last node appended to the greater-or-equal chain still physically holds whatever `next` reference it had in the original list; without explicitly overwriting that reference to `null`, the final list could end up with extra, incorrect nodes attached, or even a cycle.

**Q:** Why are two separate dummy nodes used instead of one?
**A:** Each partition is being built as its own independent chain that needs its own unambiguous "before the first real node" anchor point to support the append-to-tail pattern from the very start; a single dummy node couldn't serve as the anchor for two simultaneously-growing, separate chains.

**Q:** What would go wrong if the comparison used `<=` instead of `<` when deciding which chain to append a node to?
**A:** Nodes with a value exactly equal to `x` would be incorrectly routed into the "less than x" chain, violating the problem's explicit definition that values equal to `x` belong in the "greater than or equal to x" group.

**Q:** Why doesn't this approach need to know in advance whether one of the two partitions will end up empty?
**A:** The single-pass comparison logic handles each node independently based purely on its own value relative to `x`; if every node happens to route to the same chain, the other chain's dummy simply never gets a `next` assigned, and the join/termination logic still produces a correct result without any special-case branching for "what if a partition is empty."

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
    public ListNode partition(ListNode head, int x) {
        ListNode lessDummy = new ListNode(0);
        ListNode greaterDummy = new ListNode(0);
        ListNode lessTail = lessDummy;
        ListNode greaterTail = greaterDummy;

        ListNode current = head;
        while (current != null) {
            if (current.val < x) {
                lessTail.next = current;
                lessTail = current;
            } else {
                greaterTail.next = current;
                greaterTail = current;
            }
            current = current.next;
        }

        greaterTail.next = null;
        lessTail.next = greaterDummy.next;

        return lessDummy.next;
    }
}
```

## 14. Self-Test

Given the head of a linked list and a value `x`, rearrange the list so all nodes less than `x` come before all nodes greater than or equal to `x`, preserving relative order within each group. Re-derive: build two separate chains with their own dummy heads and tail pointers during a single pass, appending each node to the less-than chain or the greater-or-equal chain based on comparison to `x`; afterward, terminate the greater-or-equal chain's tail with `null`, then join the less-than chain's tail to the start of the greater-or-equal chain.
