---
title: "0117 — Populating Next Right Pointers in Each Node II"
layout: default
parent: Binary Tree General
grand_parent: Leetcode
nav_order: 117
permalink: /09-Binary-Tree-General/0117-populating-next-right-pointers-in-each-node-ii/

problem: 117 - Populating Next Right Pointers in Each Node II
url: https://leetcode.com/problems/populating-next-right-pointers-in-each-node-ii/
difficulty: Medium
section: Tree / BFS
patterns: [level-order-traversal, dummy-head, next-pointer-threading]
data_structures: [tree]
time: O(n)
space: O(1)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0117 — Populating Next Right Pointers in Each Node II

| Field | Value |
|---|---|
| **Pattern** | Level-Order Threading with Dummy Head (No Queue) |
| **Difficulty** | Medium |
| **Section** | Tree / BFS |
| **Time** | O(n) |
| **Space** | O(1) — uses the next pointer itself as the level thread |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Meta |
| **LeetCode** | [Populating Next Right Pointers in Each Node II](https://leetcode.com/problems/populating-next-right-pointers-in-each-node-ii/) |

## 1. Problem in Simple Words

**Given:** the root of a binary tree where each node has a `left`, `right`, and `next` pointer (all `next` pointers initially `null`), and the tree may be **any shape** (not necessarily perfect or complete).

**Required:** populate every node's `next` pointer to point to the node immediately to its right on the same level, or `null` if there is none.

**Constraints that matter:**
- `0 <= number of nodes <= 6000` — the empty tree must be handled trivially (nothing to populate).
- `-100 <= Node.val <= 100` — plain small integers, no overflow concerns.
- **This is explicitly the "II" variant — the tree may be any arbitrary shape**, unlike the simpler "I" version which guarantees a *perfect* binary tree (every level fully filled). This distinction is critical: in an arbitrary tree, a node's right neighbor might not be its sibling, or even a cousin at an easily-predictable position — it could require skipping over several absent nodes to find.
- **Follow-up: "You may only use constant extra space. The recursive approach is fine — implicit stack space does not count as extra space for this problem."** — this explicitly permits recursion (since its stack space is exempted from the space-complexity accounting for this specific problem), but still invites comparing a simpler auxiliary-queue-based BFS approach against a technique that uses the tree's own already-populated `next` pointers to traverse each level without needing a separate queue.

## 2. Recognition Signals

- "Connect nodes at the same level" → the defining phrase for **level-order (BFS) processing** — some mechanism must group and traverse nodes level by level.
- "The tree may be any shape" (not perfect/complete) → rules out any formula-based approach that would work for a perfect tree (like directly computing array-style index relationships) — a more general, structure-aware approach is needed that can handle missing children at arbitrary positions.
- The key efficiency insight: once a level's `next` pointers are correctly populated, that level **itself becomes a ready-made linked list** that can be traversed via `next` — so instead of needing an explicit queue to discover "what's on the next level," you can use the current level's `next`-pointer chain (which you just finished building) to locate and link the next level's nodes, entirely avoiding auxiliary storage.
- Named pattern: **Level-by-Level Threading Using Already-Established Next Pointers** (a specialized O(1)-space BFS variant specific to problems where the traversal structure itself becomes reusable as you build it).

## 3. Core Idea

Process the tree one level at a time. For each level, walk across it using the `next` pointers that were already set on the *previous* level's pass — this lets you visit every node on the current level without needing a queue. While walking the current level, build the *next* level's `next`-pointer chain: maintain a "dummy" placeholder and a trailing pointer for the level below; for each node on the current level, if it has a left child, link the trailing pointer to it (and advance); if it has a right child, do the same. By the time the current level's walk finishes, the level below has been fully threaded together via `next`, ready to be walked in exactly the same way on the following iteration.

**Why it's correct:** level-order connection requires knowing, for any two nodes destined to become `next`-neighbors, which specific nodes they are — and in an arbitrary (non-perfect) tree, this can't be computed from position alone, since children may be missing anywhere. The correctness of the "use the current level to build the next" technique rests on the current level already being fully connected (by induction, since it was built the same way on the previous iteration, with the root level as the trivial base case of "a single node, no connections needed"); walking it left to right (exactly matching level order) and collecting children in that same left-to-right order guarantees the next level is threaded in the correct order too, since every child encountered during a correctly-ordered scan of the parent level is encountered in the correct left-to-right order for its own level.

**Invariant:** at the start of processing each level, that level's nodes are already correctly linked via `next` pointers (true by induction, built during the previous iteration), and processing it in order while appending each encountered child (left before right, per node, in node-visitation order) to a new chain produces the correctly-ordered `next`-linked chain for the next level down.

## 4. Approach 1 — Non-Optimal for Space (BFS with an Explicit Queue)

1. If `root == null`, return `null`.
2. Use a standard level-order BFS with a `Queue<Node>`, initialized with `root`.
3. While the queue is non-empty: capture the current queue size (`levelSize`, the number of nodes on this level); dequeue each node in turn, setting its `next` to whatever is now at the front of the queue (or `null` if this was the last node of the level), and enqueue its non-null children for the next level.

```java
public Node connectBFS(Node root) {
    if (root == null) {
        return null;
    }

    Queue<Node> queue = new LinkedList<>();
    queue.offer(root);

    while (!queue.isEmpty()) {
        int levelSize = queue.size();
        for (int i = 0; i < levelSize; i++) {
            Node node = queue.poll();
            node.next = (i < levelSize - 1) ? queue.peek() : null;

            if (node.left != null) {
                queue.offer(node.left);
            }
            if (node.right != null) {
                queue.offer(node.right);
            }
        }
    }

    return root;
}
```

**Time:** O(n) — every node is enqueued and dequeued exactly once.
**Space:** O(n) in the worst case for the queue (e.g., a wide tree's last level could hold close to n/2 nodes simultaneously) — this does not satisfy the follow-up's O(1)-extra-space requirement (and the queue's space is genuine heap-allocated auxiliary storage, unlike the implicit recursion stack space the problem explicitly exempts).

This is correct and straightforward, but it fails the problem's explicit follow-up: the queue's space usage scales with the tree's width, which in the worst case is proportional to n — the problem specifically asks for a solution using only constant extra space (excluding, if used, recursion stack space).

## 5. Approach 2 — Optimized (Use Established Next Pointers, No Queue)

1. If `root == null`, return `null`.
2. Set `levelStart = root` (the leftmost node of the current level).
3. While `levelStart != null`:
   - Create a dummy node (`dummy`) and a trailing pointer `tail = dummy`, both scoped to building the *next* level's chain.
   - Walk `current = levelStart` across the current level via `current.next`: for each `current`, if it has a `left` child, set `tail.next = current.left` and advance `tail`; if it has a `right` child, set `tail.next = current.right` and advance `tail`.
   - After the walk, set `levelStart = dummy.next` (the leftmost node of the next level, or `null` if the next level is empty, ending the outer loop).
4. Return the original `root` (now fully threaded).

```java
public Node connect(Node root) {
    Node levelStart = root;

    while (levelStart != null) {
        Node dummy = new Node(0);
        Node tail = dummy;
        Node current = levelStart;

        while (current != null) {
            if (current.left != null) {
                tail.next = current.left;
                tail = tail.next;
            }
            if (current.right != null) {
                tail.next = current.right;
                tail = tail.next;
            }
            current = current.next;
        }

        levelStart = dummy.next;
    }

    return root;
}
```

**Time:** O(n) — each node is visited a constant number of times overall: once as `current` while its own level is being walked, and up to twice as a potential child being linked (once when checked as a left child, once as a right child, though each node is only actually assigned to `tail.next` once).
**Space:** O(1) auxiliary (excluding the output structure itself, which is modified in place) — only a small, constant number of pointer variables (`levelStart`, `dummy`, `tail`, `current`) are used per level, and critically, a **new** `dummy`/`tail` pair is created for each level but immediately discarded after that level's processing, never accumulating with tree size.

This is optimal: it directly satisfies the follow-up's O(1)-extra-space requirement by using the tree's own already-established `next` pointers (built on the previous iteration) to traverse the current level, entirely avoiding any queue or other structure whose size would scale with the tree's width.

## 6. Dry Run

`root = [1,2,3,4,5,null,7]` (tree: `1` has children `2,3`; `2` has children `4,5`; `3` has only right child `7`)

**Level 0** (`levelStart = 1`): walk `current = 1`. `1.left = 2` → `tail.next = 2`, `tail = 2`. `1.right = 3` → `tail.next = 3`, `tail = 3`. `current = current.next = null` (1 has no next yet, its next hasn't been set — wait, 1 is the root, processed alone, so `current.next` is still null from initialization, ending this level's walk). `levelStart = dummy.next = 2`. (Note: this also means `2.next` is now `3`, correctly linking them.)

**Level 1** (`levelStart = 2`): new dummy/tail. `current = 2`: `2.left=4` → link `4`, tail=4; `2.right=5` → link `5`, tail=5. `current = 2.next = 3` (set during level 0's processing). `3.left = null` (no left child); `3.right = 7` → link `7`, tail=7. `current = 3.next = null` (end of level 1). `levelStart = dummy.next = 4`. (This also sets `4.next=5` and `5.next=7`, correctly threading level 2's nodes together.)

**Level 2** (`levelStart = 4`): new dummy/tail. `current=4`: no children. `current = 4.next = 5`: no children. `current = 5.next = 7`: no children. `current = 7.next = null`, loop ends. `levelStart = dummy.next = null` (dummy's next was never set, since no children were found at this level).

Outer loop exits since `levelStart == null`. Final `next` chain, level by level: `1 -> null`; `2 -> 3 -> null`; `4 -> 5 -> 7 -> null`. This matches the expected output `[1,#,2,3,#,4,5,7,#]`.

## 7. Complexity Summary

| Approach | Time | Space | Notes |
|---|---|---|---|
| BFS with Explicit Queue | O(n) | O(n) worst case (queue width) | Correct, but fails the O(1)-space follow-up |
| Use Established Next Pointers | O(n) | O(1) aux | True constant extra space, no queue needed |

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|---|---|---|
| Empty tree (`root == null`) | Nothing to connect | `levelStart = root = null`; the outer `while` loop condition is immediately false, function returns `null` directly |
| Single node | Trivial case, no connections needed | Level 0's walk finds no children (if it's a true single-node tree), `dummy.next` stays `null`, `levelStart` becomes `null`, loop ends after one iteration; the single node's `next` correctly remains `null` (its default/initial value) |
| A node with only a right child, no left child (arbitrary tree shape, the key distinction from the "I" variant) | The next level's chain must correctly skip over the missing left child without leaving a gap or a dangling reference | The `if (current.left != null)` / `if (current.right != null)` checks independently handle each child's presence, so a missing left child simply means that `if` block is skipped, and the right child (if present) is still correctly linked |
| A level where a node's next sibling has no children at all, but a sibling further to the right does | Must correctly "see through" an empty-contribution node to find the next real child | Since the current-level walk continues via `current = current.next` regardless of whether the current node contributed any children, nodes with no children are still visited and simply contribute nothing to the next level's chain, without breaking the walk |
| Last level of the tree (all leaf nodes, no further children) | The outer loop must correctly terminate | After processing the deepest level, `dummy.next` remains `null` (no children were found anywhere), so `levelStart` becomes `null`, and the outer `while` loop condition correctly ends the algorithm |
| Large, wide tree near the 6000-node constraint boundary | Must perform efficiently without excessive memory use | The O(1)-per-level auxiliary space (just a few pointers, freshly created and discarded each iteration) scales effortlessly regardless of how wide any given level is |

## 9. Java Notes

- The `dummy` node created at the start of each level's processing is a **local variable**, scoped only to that iteration of the outer `while` loop — a fresh one is created every time, and the previous iteration's dummy becomes eligible for garbage collection once that iteration ends, which is part of why this technique achieves true O(1) *auxiliary* space rather than accumulating any state across levels.
- `current = current.next` is what allows the inner loop to traverse the *current* level without any queue — this only works because, by the loop's invariant, the current level's `next` pointers were already correctly set during the *previous* outer iteration (or, for the root level, there's only one node, so no traversal is needed).
- The explicit distinction between this problem ("II," arbitrary tree shape) and the simpler "I" variant (perfect binary tree) matters for implementation: a solution for the perfect-tree version can often get away with assuming every node has exactly zero or two children uniformly, allowing some shortcuts (like directly connecting `node.left.next = node.right` and inferring cross-parent connections more simply) — those shortcuts don't generalize to this problem's arbitrary shapes, which is why the more general "always check for and conditionally link children" pattern is needed here.
- Node objects here reuse the same `Node` class across both the "I" and "II" variants on LeetCode (defined with `val`, `left`, `right`, `next` fields), so the core connection logic shown is structurally compatible with either problem, differing only in whether shortcuts assuming a perfect shape are safe to take.

## 10. Common Mistakes

- Assuming every node has either zero or exactly two children (as would be guaranteed in the simpler "I" / perfect-tree variant) and therefore only checking `current.left` without separately checking `current.right`, or vice versa, or assuming their presence is correlated — in this "II" variant, a node can have just a left child, just a right child, or either/both/neither, completely independently, so both checks must always be performed separately. Fix: always independently check and handle `current.left` and `current.right`.
- Using the *current* level's dummy/tail pair to also attempt tracking the current level's own traversal (conflating "the structure I'm walking" with "the structure I'm building") — these must be kept conceptually and operationally separate: `current` walks the *already-linked* current level via its pre-existing `next` pointers, while `dummy`/`tail` independently build the *next* level's chain from scratch. Fix: maintain clearly separate pointers/variables for "traversing this level" versus "building the next level."
- Forgetting to reset `tail` to a fresh `dummy` node at the start of each new level's processing (e.g., reusing the same `tail` reference across multiple levels without resetting it) — this would incorrectly continue appending nodes from a later level onto an earlier level's already-finished chain, corrupting the structure. Fix: always create a brand-new `dummy`/`tail` pair at the very start of each outer loop iteration.
- Setting `levelStart = current` (the last node processed on the current level) instead of `levelStart = dummy.next` (the first node of the next level) — this would incorrectly attempt to continue the outer loop using a node from the *wrong* level, immediately breaking the descent to the next level down. Fix: always advance `levelStart` using `dummy.next`, which correctly points to the first (leftmost) node discovered on the level below.

## 11. Interview Takeaway

- **Trigger sentence:** "Connect same-level nodes via next pointers in an arbitrary (possibly incomplete) binary tree, in O(1) extra space → use each level's already-built next-pointer chain to traverse it while simultaneously building the next level's chain."
- **90-second explanation:** "The key realization is that once I've connected one level together with next pointers, that level has effectively become its own linked list, which I can walk without needing any separate queue. So I process the tree level by level: for the current level, I walk across it using the next pointers that were already set up during the previous pass. As I visit each node on the current level, I check for a left child and a right child independently — since this version of the problem allows any arbitrary combination of missing children, not just a perfectly filled tree — and I link whichever ones exist onto a new chain I'm building for the level below, using a dummy placeholder and a trailing pointer. By the time I finish walking the current level, the next level down is already fully threaded together, ready for me to repeat the exact same process one level deeper. This completely avoids needing an explicit queue, since the tree's own structure — once connected — becomes the traversal mechanism for the next pass."
- **Related problems:** 116 (Populating Next Right Pointers in Each Node, the simpler perfect-tree variant), 102 (Binary Tree Level Order Traversal, the general BFS version of this idea using an explicit queue), 103 (Binary Tree Zigzag Level Order Traversal).

## 12. Recall Questions

**Q:** Why does this "II" variant require independently checking both `current.left` and `current.right`, whereas a perfect-tree solution might be able to take shortcuts?
**A:** In an arbitrary tree, a node can have just a left child, just a right child, both, or neither, with no guaranteed relationship between the two, so each must be checked and handled completely independently; a perfect tree guarantees every non-leaf node has exactly two children, which allows assumptions this problem's general case doesn't support.

**Q:** Why is it valid to traverse the current level using `current = current.next` instead of a queue?
**A:** By the time the algorithm reaches a given level, that level's `next` pointers were already correctly established during the previous outer-loop iteration (with the root level needing no such setup, as it's a single node), so following those pointers left to right is equivalent to a queue-based level traversal, just without needing any separate storage.

**Q:** Why must a brand-new `dummy`/`tail` pair be created at the start of every level's processing, rather than reused across levels?
**A:** The dummy/tail pair's job is to build the *next* level's chain fresh, and reusing a prior level's tail would cause newly discovered children to be incorrectly appended onto the end of an already-completed, unrelated chain from a previous level, corrupting the structure.

**Q:** Why does `levelStart = dummy.next` correctly identify the start of the next level, rather than some other reference?
**A:** `dummy` is a placeholder node whose `next` field only ever gets set when the very first child is discovered during the current level's walk, so `dummy.next` necessarily points to the leftmost (first-discovered, in correct left-to-right order) node of the level below — exactly the starting point needed to begin traversing that level on the following outer-loop iteration.

**Q:** Why does this approach qualify as O(1) auxiliary space despite creating new `dummy` and `tail` variables on every iteration of the outer loop?
**A:** Although a new dummy/tail pair is created per level, only one such pair exists at any given time — the previous level's pair is discarded (eligible for garbage collection) before or as the new one is created — so the total extra space in use at any single moment remains a small constant, never growing proportionally with the tree's size or width.

## 13. Final Code

```java
/**
 * Definition for a Node.
 * class Node {
 *     public int val;
 *     public Node left;
 *     public Node right;
 *     public Node next;
 *
 *     public Node() {}
 *     public Node(int _val) { val = _val; }
 *     public Node(int _val, Node _left, Node _right, Node _next) {
 *         val = _val;
 *         left = _left;
 *         right = _right;
 *         next = _next;
 *     }
 * }
 */
class Solution {
    public Node connect(Node root) {
        Node levelStart = root;

        while (levelStart != null) {
            Node dummy = new Node(0);
            Node tail = dummy;
            Node current = levelStart;

            while (current != null) {
                if (current.left != null) {
                    tail.next = current.left;
                    tail = tail.next;
                }
                if (current.right != null) {
                    tail.next = current.right;
                    tail = tail.next;
                }
                current = current.next;
            }

            levelStart = dummy.next;
        }

        return root;
    }
}
```

## 14. Self-Test

Given the root of a binary tree of arbitrary shape (nodes with `left`, `right`, and `next` fields), populate every node's `next` pointer to its same-level right neighbor, using only constant extra space. Re-derive: starting from the root as the first level, repeatedly walk the current level via its already-established `next` pointers while building a `next`-linked chain for the level below (using a per-level dummy node and trailing pointer, independently checking and linking each node's left and right children if present); advance to the next level via the dummy's `next`, stopping once a level yields no children.
