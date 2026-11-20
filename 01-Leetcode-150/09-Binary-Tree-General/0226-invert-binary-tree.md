---
title: "0226 — Invert Binary Tree"
layout: default
parent: Binary Tree General
grand_parent: Leetcode
nav_order: 226
permalink: /09-Binary-Tree-General/0226-invert-binary-tree/

problem: 226 - Invert Binary Tree
url: https://leetcode.com/problems/invert-binary-tree/
difficulty: Easy
section: Tree / DFS
patterns: [recursive-swap, post-order-like-traversal]
data_structures: [tree, queue]
time: O(n)
space: O(h)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0226 — Invert Binary Tree

| Field | Value |
|---|---|
| **Pattern** | Recursive Swap (Post-Order Style) |
| **Difficulty** | Easy |
| **Section** | Tree / DFS |
| **Time** | O(n) |
| **Space** | O(h) — h = tree height |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Meta |
| **LeetCode** | [Invert Binary Tree](https://leetcode.com/problems/invert-binary-tree/) |

## 1. Problem in Simple Words

**Given:** The root of a binary tree.

**Required:** Invert the tree — for every node, swap its left and right children — and return the root of the resulting (mirrored) tree.

**Constraints that actually matter:**
- `0 <= number of nodes <= 100` — tiny; performance is a complete non-issue. The tree can be **empty** (`root == null`), which must be handled gracefully as a trivial base case.
- **Inverting means swapping left and right children at every single node**, recursively, all the way down — not just at the root. A common early misunderstanding is to swap only the root's two children and stop; the swap must happen at every node in the tree.
- There's no ordering/BST property to preserve or exploit here — this is a plain binary tree, so the "invert" operation is purely structural (mirroring the shape and children), with no concern for value ordering.

## 2. Recognition Signals

- "Swap left and right children at every node in a tree" → the signature of a simple **recursive tree transformation**: define the operation on a single node in terms of the same operation already correctly applied to its children, and let the recursion handle every level of the tree uniformly.
- This is one of the most classic, minimal examples of **tree recursion**: the inversion of a (sub)tree rooted at any node `X` is exactly "swap `X`'s left and right children, where each child has *already* been fully inverted" — recognizing that the order of "invert children first" vs "swap first, then invert" doesn't actually matter for correctness here (unlike many other tree problems) is a nice, specific insight about this particular problem's structure.
- Whenever a tree problem can be expressed as "do something simple at this node, and recursively do the same thing to both children" — a straightforward DFS-based recursive solution is almost always the simplest, most direct implementation, with an iterative BFS/queue-based alternative also always available for problems that don't inherently require tracking ancestor-specific state.

**Pattern:** Recursive Swap at Every Node (a minimal DFS-style tree recursion: recursively invert both children, then swap them at the current node — or swap first, then recurse; both orders are equally valid here) — with an iterative BFS (level-order, queue-based) traversal as a straightforward non-recursive alternative.

## 3. Core Idea

To invert the tree rooted at a given node: recursively invert the left subtree, recursively invert the right subtree, and then swap the node's `left` and `right` references to point to these (now-inverted) subtrees instead of their original positions. Because swapping two already-fully-inverted subtrees is equivalent to swapping them first and then inverting each (the swap and the recursive inversion of each side are independent operations that don't interfere with each other), the order in which the swap happens relative to the two recursive calls doesn't actually affect correctness — only that all three things (invert left, invert right, swap the two) eventually happen for every node.

**Invariant:** After the function returns for a given node (having fully completed both its recursive calls and the swap), the subtree rooted at that node is a perfect mirror image of what it was before — every left/right child relationship throughout that entire subtree has been reversed, all the way down to the leaves.

## 4. Approach 1 — Iterative (Level-Order, Queue-Based)

**Logic:** Use a queue to process nodes level by level (BFS). For each node dequeued, swap its left and right children, then enqueue both (now-swapped) children for further processing.

```java
public TreeNode invertTree(TreeNode root) {
    if (root == null) {
        return null;
    }

    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);

    while (!queue.isEmpty()) {
        TreeNode current = queue.poll();

        TreeNode temp = current.left;
        current.left = current.right;
        current.right = temp;

        if (current.left != null) {
            queue.offer(current.left);
        }
        if (current.right != null) {
            queue.offer(current.right);
        }
    }

    return root;
}
```

- **Time:** O(n) — every node is dequeued and processed (swapped) exactly once.
- **Space:** O(n) in the worst case for the queue — specifically, up to roughly half the nodes could be in the queue simultaneously at the widest level of a very bushy tree (e.g., a complete binary tree's last level holds about n/2 nodes).

**Why it is presented as a valid non-recursive alternative:** This iterative BFS approach is fully correct and achieves the same O(n) time complexity as the recursive version. It avoids recursion entirely, which can be a meaningful advantage in languages or contexts where recursion depth is a practical concern (e.g., a very deeply skewed tree causing a stack overflow in a recursive solution) — though given this problem's tiny constraint (`<= 100` nodes), that concern is moot here, making the choice between the two approaches largely stylistic for this specific problem.

## 5. Approach 2 — Optimized/Canonical (Recursive DFS)

**Algorithm:**
1. If `root` is `null`, return `null` immediately (base case: an empty tree is trivially its own inversion).
2. Recursively invert the left subtree: `TreeNode left = invertTree(root.left);`
3. Recursively invert the right subtree: `TreeNode right = invertTree(root.right);`
4. Swap: set `root.left = right` and `root.right = left`.
5. Return `root`.

```java
public TreeNode invertTree(TreeNode root) {
    if (root == null) {
        return null;
    }

    TreeNode left = invertTree(root.left);
    TreeNode right = invertTree(root.right);

    root.left = right;
    root.right = left;

    return root;
}
```

- **Time:** O(n) — every node is visited exactly once across the entire recursion.
- **Space:** O(h), where `h` is the height of the tree, for the recursion call stack — O(log n) for a balanced tree, degrading to O(n) in the worst case for a completely skewed (essentially linear) tree.

**Why this is presented as the canonical/preferred solution:** Both approaches share the same O(n) time complexity, and their space complexities are not strictly comparable in general (O(h) can be better or worse than O(n) depending on the tree's shape — a balanced tree makes the recursive version's O(log n) space better than the iterative version's O(n) queue space, while a completely skewed tree makes them asymptotically similar). The recursive version is typically preferred for this specific classic problem primarily for its remarkable conciseness and directness in expressing the problem's inherently recursive structure — it's often cited as one of the cleanest possible illustrations of simple tree recursion, and is the version most commonly expected and recognized in an interview setting for this exact problem.

## 6. Dry Run

Example: `root = [4,2,7,1,3,6,9]` (representing the tree with 4 at the root, 2 and 7 as its children, 1 and 3 as 2's children, 6 and 9 as 7's children).
Chosen because it's the canonical example, and it's small enough to trace the recursive calls explicitly while still showing multiple levels of swapping.

Using the recursive approach, tracing the call for `invertTree(4)`:

1. `invertTree(4)` calls `invertTree(2)` (left child) first.
   - `invertTree(2)` calls `invertTree(1)` (left child): `1` is a leaf, so both its recursive calls return `null` immediately, and swapping `1`'s (already-null) children is a no-op; returns node `1` (unchanged, since it has no children to swap).
   - `invertTree(2)` calls `invertTree(3)` (right child): similarly, `3` is a leaf, returns node `3` unchanged.
   - Back in `invertTree(2)`: swap `2`'s children: `2.left = 3`, `2.right = 1`. Returns node `2` (now with children swapped: left=3, right=1).
2. `invertTree(4)` calls `invertTree(7)` (right child).
   - `invertTree(7)` calls `invertTree(6)` (left child): leaf, returns unchanged.
   - `invertTree(7)` calls `invertTree(9)` (right child): leaf, returns unchanged.
   - Back in `invertTree(7)`: swap `7`'s children: `7.left = 9`, `7.right = 6`. Returns node `7` (now left=9, right=6).
3. Back in `invertTree(4)`: swap `4`'s children: `4.left = 7` (the now-inverted right subtree), `4.right = 2` (the now-inverted left subtree). Returns node `4`.

Exit condition: the outermost call `invertTree(4)` returns after completing its swap.

**Final answer:** The tree rooted at `4` now has left child `7` (itself with left=9, right=6) and right child `2` (itself with left=3, right=1) — in level-order, this is `[4,7,2,9,6,3,1]`, matching the expected output exactly.

## 7. Complexity Summary

| Approach | Time | Space (Auxiliary) | Notes |
|----------|------|--------------------|-------|
| 1. Iterative, level-order (BFS, queue) | O(n) | O(n) worst case (widest level of the tree) | Correct; avoids recursion entirely |
| 2. Recursive DFS | O(n) | O(h) (tree height; O(log n) balanced, O(n) worst-case skewed) | Canonical, most concise; space depends on tree shape |

Both approaches visit every node exactly once for the core work (O(n) time); their space complexities differ based on tree shape and aren't strictly one-better-than-the-other in general.

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|------|-------------------|--------------------------|
| Empty tree (`root == null`, example 3) | Should return `null` immediately, not error | Both approaches explicitly check `if (root == null) return null;` (recursive version) or check before entering the main loop (iterative version's initial check), correctly handling this trivial case |
| Single-node tree (no children) | Should return the single node unchanged, since it has no children to swap | Recursive: both recursive calls on `null` children return `null` immediately, and swapping two `null` references is a harmless no-op; Iterative: the single node is dequeued, its (both-null) children are swapped (no-op), and neither is re-enqueued since both are null, correctly terminating after one iteration |
| A node with only one child (not a "complete" binary tree) | Swapping must correctly move that single child from one side to the other, not assume both children always exist | Both approaches handle a null child identically to a non-null one in the swap itself (simply swapping whatever references are present, including `null`), so a single-child node correctly ends up with that child on the opposite side, and a `null` on the side that was previously occupied |
| A perfectly symmetric tree (where the tree already looks identical after inversion) | Should still correctly perform the swap operation at every node, even though the final visual result might look unchanged for certain symmetric structures | The algorithm doesn't attempt to detect or special-case symmetry — it performs the same swap operation uniformly at every node regardless of the tree's specific shape or value arrangement, so a symmetric tree is handled correctly as just a particular (coincidental) case of the general algorithm |
| Negative node values (constraint allows down to -100) | Node values themselves are never compared or used in any logic by this algorithm | Since inversion only swaps child *references* (not values, and with no value-based comparisons anywhere in the algorithm), the sign or magnitude of node values is entirely irrelevant to correctness |
| Maximum constraint size (100 nodes) | Should still perform efficiently and correctly at the upper bound | The O(n) time complexity (and bounded space, given the small size) comfortably handles this size regardless of the tree's specific shape |

## 9. Java Notes

- **`Queue<TreeNode>` via `LinkedList` (iterative approach):** `LinkedList` implements the `Queue` interface, providing O(1) `offer` (enqueue) and `poll` (dequeue) operations, making it the standard, idiomatic choice for BFS-style level-order tree traversal in Java.
- **Recursive approach's remarkable brevity:** this is frequently cited as one of the simplest, most elegant examples of tree recursion in introductory algorithm contexts — the entire non-trivial logic fits in just two recursive calls plus a three-line swap, directly mirroring the problem's own recursive definition ("invert a tree" = "invert both children, then swap them").
- **No need for a temporary variable in the recursive version's swap**, since the two already-computed `left` and `right` local variables (holding the results of the two recursive calls) naturally serve as the "temporary" storage — unlike the iterative version, which does need an explicit `temp` variable since it swaps `current.left` and `current.right` directly in place without first capturing both into separate local variables.
- **No overflow or numeric risk:** this problem involves only reference swapping; no arithmetic is performed on node values anywhere.

## 10. Common Mistakes

- **Swapping only the root node's children and forgetting to apply the swap recursively to every node throughout the entire tree.** This produces a tree where only the very top level is mirrored, while every deeper level remains in its original (non-inverted) arrangement — a common early misunderstanding of what "invert the tree" actually requires. Fix: ensure the swap operation is applied at *every* node, via recursion (or an equivalent full traversal like the iterative BFS version), not just once at the root.
- **Forgetting the base case (`if (root == null) return null;`) in the recursive version**, leading to a `NullPointerException` when the recursion reaches a leaf node's null children. Fix: always include an explicit null check as the very first step of the recursive function.
- **In the iterative version, forgetting to check for `null` before enqueueing a child**, which would add `null` entries to the queue and cause a `NullPointerException` when later attempting to dequeue and process one as if it were a real node. Fix: always guard `queue.offer(...)` calls with a null check on the child being added.
- **Attempting to swap `current.left` and `current.right` directly without a temporary variable in the iterative version** (e.g., mistakenly writing `current.left = current.right; current.right = current.left;`, which would incorrectly overwrite `current.left` with the new value before the second line ever reads the *original* `current.left`). Fix: always use a temporary variable (or ensure both original values are captured before either assignment happens) when swapping two fields directly in place.

## 11. Interview Takeaway

- **Trigger sentence to memorize:** "Invert a binary tree (swap left and right children everywhere) → recursively invert both children first, then swap them at the current node — the simplest possible tree recursion."
- **90-second explanation:** "To invert a tree, I need to swap the left and right children at every single node, not just at the root. The cleanest way to express this is recursively: to invert the tree rooted at any given node, I first recursively invert its left subtree and its right subtree — this handles every deeper level of the tree automatically — and then, once both subtrees have been fully inverted, I simply swap the node's left and right references to point to them in the opposite positions. The base case is simply an empty tree, which is trivially its own inversion, so I just return null immediately for that. This is one of the simplest possible illustrations of tree recursion: do something small and local — the swap — and recursively let the same logic handle every other node in the tree. If I wanted to avoid recursion entirely, I could instead use an iterative, queue-based level-order traversal, swapping each node's children as I dequeue it and enqueueing the (now-swapped) children for further processing, which is equally correct and might be preferable in contexts where deep recursion could be a practical concern."
- **Related problems using this pattern:**
  - LeetCode 100 — Same Tree (a related simple recursive tree-comparison problem)
  - LeetCode 101 — Symmetric Tree (checks whether a tree is its own mirror image, directly related conceptually to inversion)
  - LeetCode 104 — Maximum Depth of Binary Tree (another classic minimal recursive tree problem)

## 12. Recall Questions

**Q:** Why does it not matter whether the swap at a given node happens before or after the two recursive calls that invert its children?
**A:** Swapping a node's two children and recursively inverting each child's own subtree are independent operations that don't interfere with each other — inverting a subtree only rearranges things within that subtree, so it doesn't matter whether that rearrangement happens before or after the parent decides which side to place the (already fully inverted, either way) subtree on.

**Q:** Why is swapping only the root's two children insufficient to correctly invert the entire tree?
**A:** Inverting the tree requires reversing the left/right relationship at every single node throughout the whole structure, not just at the topmost level, so any node below the root whose children aren't also explicitly swapped would remain in its original, non-inverted arrangement.

**Q:** Why does the recursive approach's space complexity depend on the tree's height rather than simply being proportional to the total number of nodes?
**A:** The recursion stack only ever holds frames for nodes along the current root-to-leaf path being explored at any given moment, not all nodes in the tree simultaneously, so the maximum stack depth — and therefore the auxiliary space used — is bounded by the tree's height, which can range from O(log n) for a balanced tree up to O(n) for a completely skewed one.

**Q:** Why does the iterative, queue-based approach need an explicit temporary variable to perform the swap, while the recursive approach doesn't?
**A:** The recursive approach already has both the left and right subtrees' results stored in two separate local variables (from the two prior recursive calls) before performing the swap, effectively already having a "temporary" holding spot for each side, whereas the iterative approach swaps the node's fields directly in place without first capturing both original values separately, so an explicit temporary variable is needed to avoid overwriting one value before it's been used.

**Q:** Why doesn't this algorithm need to compare or examine node values at any point?
**A:** Inverting a binary tree is purely a structural transformation — reversing the left/right position of child references at every node — which has nothing to do with what values those nodes happen to hold, so the algorithm only ever manipulates child pointers and never needs to read or compare the `val` field of any node.

## 13. Final Code

```java
public TreeNode invertTree(TreeNode root) {
    if (root == null) {
        return null;
    }

    TreeNode left = invertTree(root.left);
    TreeNode right = invertTree(root.right);

    root.left = right;
    root.right = left;

    return root;
}
```

## 14. Self-Test

You have the root of a binary tree. Invert it — swap the left and right children at every node — and return the root. Think about why recursively inverting both children first, then swapping them at the current node, correctly handles every level of the tree, and why the order of the swap relative to the recursive calls doesn't actually matter here.
