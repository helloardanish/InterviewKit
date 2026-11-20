---
title: "0082 — Remove Duplicates from Sorted List II"
layout: default
parent: Linked List
grand_parent: Leetcode
nav_order: 82
permalink: /08-Linked-List/0082-remove-duplicates-from-sorted-list-ii/

problem: 82 - Remove Duplicates from Sorted List II
url: https://leetcode.com/problems/remove-duplicates-from-sorted-list-ii/
difficulty: Medium
section: Linked List
patterns: [dummy-head, sentinel-prev-pointer]
data_structures: [linked-list]
time: O(n)
space: O(1)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0082 — Remove Duplicates from Sorted List II

| Field | Value |
|---|---|
| **Pattern** | Dummy Head with Sentinel Prev Pointer |
| **Difficulty** | Medium |
| **Section** | Linked List |
| **Time** | O(n) |
| **Space** | O(1) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Meta |
| **LeetCode** | [Remove Duplicates from Sorted List II](https://leetcode.com/problems/remove-duplicates-from-sorted-list-ii/) |

## 1. Problem in Simple Words

**Given:** the head of a **sorted** (ascending) linked list.

**Required:** delete **every** node whose value appears more than once in the list — not just trim duplicates down to one copy, but remove all occurrences of any value that repeats — leaving only values that appeared exactly once, still in sorted order.

**Constraints that matter:**
- `0 <= number of nodes <= 300` — the empty list is explicitly allowed and must simply return `null`/empty with no special handling beyond the natural loop behavior.
- `-100 <= Node.val <= 100` — small bounded value range; this doesn't affect the core algorithm but confirms no overflow or large-number concerns anywhere.
- **The list is guaranteed sorted in ascending order** — this is the single most important constraint: because of sorting, all occurrences of any given value are guaranteed to be **adjacent** to each other, meaning duplicate detection only ever requires comparing a node to its immediate neighbor, never a lookup against far-away or already-passed values.
- Crucially, this is "Sorted List II" (distinguishing it from the simpler "Sorted List I" variant) — the requirement here is to **remove all copies** of a duplicated value entirely, not to keep one representative copy, which is a materially different (and trickier) deletion rule.

## 2. Recognition Signals

- "Sorted list" + "remove all nodes with a duplicate value" → the sortedness is the direct enabler of a clean single-pass solution, since duplicates are guaranteed to be contiguous — no hash set or multi-pass value-counting is structurally necessary.
- The phrase "leaving only distinct numbers" (as opposed to "leaving only one of each number") is the signal that **every** occurrence of a repeated value must go, including what would normally be considered "the first" copy — this distinguishes it from the more common "keep just one" duplicate-removal variant.
- Needing to potentially remove the **head node itself** (if its value turns out to be a duplicate, as in Example 2) → the standard cue for a **dummy head / sentinel node** to avoid special-casing updates to the list's starting point.
- Named pattern: **Sentinel (Dummy) Node with a Trailing "Last Known Good" Pointer**, scanning ahead to detect and skip runs of duplicate values.

## 3. Core Idea

Use a dummy node before the head, and a `prev` pointer that always points to the last node confirmed to be **not** part of any duplicate run. Walk a `current` pointer through the list; whenever `current.val == current.next.val`, a duplicate run has been found — advance `current` through the entire run (consuming all nodes sharing that value), then link `prev.next` directly past the whole run. If no duplicate is found at `current`, it's safe to advance `prev` to `current` before moving `current` forward.

**Why it's correct:** because the list is sorted, every node with a given value is directly adjacent to every other node with that same value — so a single lookahead check (`current.val == current.next.val`) is sufficient to detect the *start* of a duplicate run, and continuing to advance while the value keeps matching correctly consumes the *entire* run, however long it is. Linking `prev.next` to whatever comes immediately after the run entirely excises every node in that run from the list in one step, and `prev` is only ever advanced past nodes that have been confirmed unique, ensuring the final list contains exactly the values that appeared exactly once.

**Invariant:** at any point during the scan, `prev.next` correctly points to the next node that belongs in the final, deduplicated-and-purged list — meaning every node between what `prev` currently points past and `current`'s position has either already been confirmed unique and linked in, or is part of a duplicate run currently being skipped over and will never be linked in.

## 4. Approach 1 — Brute Force (Frequency Check via Nested Scan)

1. For each node in the list, scan the **entire rest of the list** (not exploiting sortedness at all) to count how many total times that node's value appears anywhere in the list.
2. Build a new list by walking the original list again, including only nodes whose value's total count was exactly `1`.

```java
public ListNode deleteDuplicatesBruteForce(ListNode head) {
    ListNode dummy = new ListNode(0, head);
    ListNode current = dummy.next;

    while (current != null) {
        int count = 0;
        for (ListNode node = head; node != null; node = node.next) {
            if (node.val == current.val) {
                count++;
            }
        }
        current.duplicateCount = count; // conceptually; in practice, use a separate structure
        current = current.next;
    }

    ListNode resultDummy = new ListNode(0);
    ListNode tail = resultDummy;
    for (ListNode node = head; node != null; node = node.next) {
        if (node.duplicateCount == 1) {
            tail.next = node;
            tail = tail.next;
        }
    }
    tail.next = null;

    return resultDummy.next;
}
```

**Time:** O(n²) — for each of the n nodes, a full O(n) scan of the list is performed to count occurrences of its value.
**Space:** O(1) beyond the (conceptual) per-node counter, or O(n) if a separate `Map<ListNode, Integer>` is used instead of mutating node objects directly (which isn't typically allowed on a standard `ListNode` class anyway, making this approach somewhat awkward to implement cleanly in practice).

This is not optimal: it completely ignores the problem's sortedness guarantee, which is precisely what makes a much simpler, linear solution possible. Re-scanning the whole list for every node is wasted work once you recognize that duplicates must be adjacent.

## 5. Approach 2 — Optimized (Single Pass, Dummy Head + Trailing Pointer)

1. Create a dummy node pointing to `head`; initialize `prev = dummy` and `current = head`.
2. While `current != null`:
   - If `current.next != null && current.val == current.next.val`: a duplicate run starts here. Advance `current` through the entire run — `while (current.next != null && current.val == current.next.val) current = current.next;` — then set `prev.next = current.next` (unlinking the whole run at once), and advance `current = current.next`.
   - Otherwise (no duplicate at this position): advance `prev = current`, then advance `current = current.next`.
3. Return `dummy.next`.

```java
public ListNode deleteDuplicates(ListNode head) {
    ListNode dummy = new ListNode(0, head);
    ListNode prev = dummy;
    ListNode current = head;

    while (current != null) {
        if (current.next != null && current.val == current.next.val) {
            int duplicateVal = current.val;
            while (current.next != null && current.next.val == duplicateVal) {
                current = current.next;
            }
            prev.next = current.next;
        } else {
            prev = current;
        }
        current = current.next;
    }

    return dummy.next;
}
```

**Time:** O(n) — although there's a `while` loop nested inside the outer `while` loop, `current` only ever advances forward through the list and never revisits a node, so the combined work across both loops is bounded by a single linear pass over all n nodes.
**Space:** O(1) auxiliary — only the dummy node, `prev`, `current`, and a temporary value variable are used, regardless of list length.

This is optimal: it directly exploits the sorted-list guarantee to detect and remove entire duplicate runs with a single forward pass, needing no auxiliary counting structure or second traversal — every node is visited exactly once by `current`.

## 6. Dry Run

`head = [1,2,3,3,4,4,5]`

| current | current.next.val == current.val? | action | prev after | current after |
|---|---|---|---|---|
| 1 | next is 2, no | prev=1 | 1 | 2 |
| 2 | next is 3, no | prev=2 | 2 | 3 |
| 3 (first) | next is 3, **yes** | inner loop: current advances through both 3's (current.next.val==3 matches once more, then stops at second 3 since its next is 4); prev.next = current.next (= node 4) | 2 (unchanged) | 4 (the node after the duplicate run) |
| 4 (first) | next is 4, **yes** | inner loop: current advances to second 4; prev.next = current.next (= node 5) | 2 (unchanged) | 5 |
| 5 | next is null, no | prev=5 | 5 | null |

Exit: `current` becomes `null`, loop ends. The list, read from `dummy.next`, is `1 -> 2 -> 5` (both `3`s and both `4`s were excised as full duplicate runs). Final answer: `[1,2,5]`, matching the expected output.

## 7. Complexity Summary

| Approach | Time | Space | Notes |
|---|---|---|---|
| Nested Scan (ignores sortedness) | O(n²) | O(1) to O(n) depending on implementation | Re-scans the whole list per node |
| Single Pass, Dummy + Trailing Pointer | O(n) | O(1) aux | Exploits sortedness: duplicates are always adjacent |

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|---|---|---|
| Empty list (`head == null`) | No nodes to process | `current = head = null`; the `while (current != null)` loop never executes, returns `dummy.next = null` |
| All values distinct (no duplicates at all) | Every node should be preserved | The duplicate-check condition never fires; `prev` simply advances alongside `current` through the entire list unchanged |
| All values identical (e.g. `[2,2,2]`) | Entire list should become empty | The duplicate-run detection fires once at the very start, consumes the whole list, and sets `prev.next` (which is `dummy.next`) to `null`, correctly returning an empty list |
| Duplicate run at the very start (e.g. `[1,1,1,2,3]`) | The *head itself* must be removed, with `prev` still correctly at the dummy | Since `prev` starts at `dummy`, the first duplicate run (even if it includes the original head) is correctly excised via `dummy.next` being updated, with no special-casing needed |
| Duplicate run at the very end (e.g. `[1,2,3,3]`) | Must correctly terminate after unlinking the final run, with `prev.next` becoming `null` | The inner while loop correctly advances `current` to the last node of the run, and `prev.next = current.next` sets it to `null` since there's nothing after the run |
| Single-node list (`head = [5]`) | Trivial case, no duplicates possible | `current.next` is `null`, so the duplicate check's first condition (`current.next != null`) is false; `prev` advances to `current`, loop ends normally, list unchanged |

## 9. Java Notes

- The dummy node constructor `new ListNode(0, head)` uses the three-argument constructor from LeetCode's standard `ListNode` definition, linking the dummy directly to the real head in one line.
- The inner `while` loop (`while (current.next != null && current.next.val == duplicateVal)`) is what correctly handles duplicate runs of **any length**, not just pairs — three, four, or more copies of the same value are all consumed by this single inner loop before the outer logic unlinks the entire run at once.
- `prev.next = current.next` performs the actual removal — it's a single pointer reassignment that discards the *entire* identified run in one step, regardless of how many nodes were in it, which is more efficient than removing one node at a time.
- Note that `prev` is only ever advanced (`prev = current`) in the *non-duplicate* branch — this is intentional and essential: `prev` must never be allowed to point at a node that turns out to be part of a duplicate run, since that node is about to be deleted from the list entirely.

## 10. Common Mistakes

- Advancing `prev` unconditionally on every iteration (regardless of whether a duplicate was found) instead of only in the non-duplicate branch — this would leave `prev` pointing at a node that's about to be removed, corrupting the list structure when `prev.next` is later reassigned. Fix: only advance `prev` when the current node has been confirmed to not be part of a duplicate run.
- Implementing this as if it were the simpler "Sorted List I" variant — i.e., just skipping *consecutive duplicates past the first occurrence*, keeping one copy of each value — which solves a different (easier) problem than what's asked here. Fix: re-read the problem statement carefully; this variant requires removing **every** copy of a duplicated value, not reducing runs down to a single representative.
- Forgetting the inner `while` loop and only checking `current.val == current.next.val` once (handling only exactly-two-element duplicate runs) — this fails on runs of three or more identical values, since after advancing past just two of them, the next node (part of the same run) wouldn't be recognized as still belonging to that duplicate group correctly without continued checking. Fix: always fully consume a duplicate run with an inner loop before moving on, regardless of how many repeated copies it contains.
- Not using a dummy node and instead trying to special-case "what if the duplicate run includes the original head" with separate logic — unnecessarily complicates the code; the dummy node handles this uniformly with the exact same logic path as any other duplicate run in the middle of the list.

## 11. Interview Takeaway

- **Trigger sentence:** "Sorted linked list, remove *all* copies of any duplicated value (not just trim to one) → single pass with a dummy head and a trailing 'last confirmed unique' pointer, exploiting the fact that sortedness makes duplicates always adjacent."
- **90-second explanation:** "Because the list is sorted, any value that repeats must do so in one contiguous block — I'll never need to look far ahead or keep a count across the whole list. I walk through with a `current` pointer, and whenever I see that the next node shares the same value, I know I've found the start of a duplicate run. I keep advancing `current` through that entire run, however long it is, then I link the node before the run — tracked by a separate `prev` pointer — directly to whatever comes right after the run, cutting out every single duplicate copy in one step. Critically, I only ever advance `prev` when I've confirmed the current node is *not* part of a duplicate run, since `prev` always needs to point at the last node that's actually going to survive in the final list. A dummy node before the head means I don't need any special logic for when the very first values in the list turn out to be duplicates themselves."
- **Related problems:** 83 (Remove Duplicates from Sorted List, the simpler "keep one copy" variant), 26 (Remove Duplicates from Sorted Array), 19 (Remove Nth Node From End of List, same dummy-head deletion idiom).

## 12. Recall Questions

**Q:** Why does the list being sorted make this problem solvable in a single linear pass, without needing a hash map or separate counting step?
**A:** Sorting guarantees that every occurrence of a particular value is directly adjacent to every other occurrence of that same value, so a duplicate run can always be fully detected and consumed just by comparing each node to its immediate neighbor — there's never a need to look far ahead or remember values seen much earlier.

**Q:** Why must `prev` only be advanced in the branch where no duplicate was detected, rather than on every iteration?
**A:** `prev` represents the last node confirmed to survive in the final list, and if it were advanced onto a node that turns out to be part of a duplicate run, the later `prev.next = current.next` reassignment would incorrectly try to use a soon-to-be-deleted node as the anchor point, corrupting the list's linkage.

**Q:** What is the key difference between this problem and the simpler "Remove Duplicates from Sorted List" (without the "II")?
**A:** The simpler variant keeps exactly one representative copy of each value, trimming only the *extra* duplicates; this variant removes **every single node** that shares its value with at least one other node, meaning a value that appears even twice is entirely absent from the final result, not just reduced to a single instance.

**Q:** Why is the inner `while` loop necessary, rather than just a single `if` check for `current.val == current.next.val`?
**A:** A duplicate run can consist of more than two identical values in a row, and a single comparison would only correctly detect and handle a run of exactly two; the inner loop is what allows the algorithm to correctly consume a run of any length before deciding where to relink `prev.next`.

**Q:** Why is a dummy node particularly important for this specific problem, given that the head might itself be part of a duplicate run?
**A:** If the very first values in the list are duplicates of each other, the entire beginning of the list — including the original head — needs to be removed, and without a dummy node serving as `prev`'s starting point, there would be no valid "node before the list" to correctly update when the new head needs to become some node further into the original list.

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
    public ListNode deleteDuplicates(ListNode head) {
        ListNode dummy = new ListNode(0, head);
        ListNode prev = dummy;
        ListNode current = head;

        while (current != null) {
            if (current.next != null && current.val == current.next.val) {
                int duplicateVal = current.val;
                while (current.next != null && current.next.val == duplicateVal) {
                    current = current.next;
                }
                prev.next = current.next;
            } else {
                prev = current;
            }
            current = current.next;
        }

        return dummy.next;
    }
}
```

## 14. Self-Test

Given the head of a sorted linked list, remove every node whose value appears more than once, keeping only values that occur exactly once, still in sorted order. Re-derive: use a dummy head and a `prev` pointer tracking the last confirmed-unique node; scan with `current`, and whenever `current.val` equals `current.next.val`, advance `current` through the entire matching run and set `prev.next` to skip past it entirely; otherwise, advance `prev` to `current` before moving on.
