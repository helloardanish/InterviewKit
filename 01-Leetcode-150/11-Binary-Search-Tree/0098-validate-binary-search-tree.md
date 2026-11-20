---
title: "0098 — Validate Binary Search Tree"
layout: default
parent: Binary Search Tree
grand_parent: Leetcode
nav_order: 98
permalink: /11-Binary-Search-Tree/0098-validate-binary-search-tree/

problem: 98 - Validate Binary Search Tree
url: https://leetcode.com/problems/validate-binary-search-tree/
difficulty: Medium
section: Binary Search Tree
patterns: [dfs-with-bounds, inorder-traversal]
data_structures: [tree]
time: O(n)
space: O(h)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0098 — Validate Binary Search Tree

| Field | Value |
|---|---|
| **Pattern** | DFS with Bounds (or Inorder Traversal) |
| **Difficulty** | Medium |
| **Section** | Binary Search Tree |
| **Time** | O(n) |
| **Space** | O(h) — h = tree height |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Meta |
| **LeetCode** | [Validate Binary Search Tree](https://leetcode.com/problems/validate-binary-search-tree/) |

## 1. Problem in Simple Words

**Given:** the root of a binary tree.

**Required:** return `true` if it is a valid BST: for every node, **every** node in its left subtree is strictly less, **every** node in its right subtree is strictly greater, and both subtrees are themselves valid BSTs.

**Constraints that matter:**
- `1 <= nodes <= 10^4` — O(n) is the target; the tree can be skewed, so recursion depth can reach 10^4, which is usually fine in Java but worth noting.
- `-2^31 <= Node.val <= 2^31 - 1` — **values can equal `Integer.MIN_VALUE` or `Integer.MAX_VALUE`.** This rules out using those constants as "infinite" sentinel bounds with `int`. This is the constraint most likely to cause a silent wrong answer.
- Inequalities are **strict**: duplicates are invalid anywhere in the tree.

## 2. Recognition Signals

- "Valid BST" where the rule is stated about whole subtrees, not just a node and its children. This hints that a local parent-child check is insufficient.
- "Left subtree contains only keys less than the node's key" means each node carries an inherited *allowed range* from all of its ancestors.
- In-order traversal of a BST is strictly increasing, which gives a second, equivalent formulation.
- Pattern: **DFS passing a `(low, high)` open interval down the tree**, or equivalently **in-order traversal checking strict ascent**.

## 3. Core Idea

Every node must lie strictly inside an interval determined by its ancestors. Going left tightens the upper bound to the parent's value; going right tightens the lower bound to the parent's value. A node is valid iff `low < val < high`, and both children are valid under their tightened intervals.

**Why it's correct:** a node's constraints come from every ancestor, not just its parent. Each time we step left or right, exactly one bound is replaced by the parent's value, which is the tightest bound that ancestor imposes. So the interval always equals the intersection of all ancestor constraints.

**Invariant:** when visiting a node, `(low, high)` is the exact open interval that every ancestor's BST rule requires this node's value to lie in.

## 4. Approach 1 — Brute Force

For every node, scan its entire left subtree to confirm all values are smaller and its entire right subtree to confirm all are larger, then recurse.

```java
public boolean isValidBSTBruteForce(TreeNode root) {
    if (root == null) return true;
    return allLess(root.left, root.val)
        && allGreater(root.right, root.val)
        && isValidBSTBruteForce(root.left)
        && isValidBSTBruteForce(root.right);
}

private boolean allLess(TreeNode node, int limit) {
    if (node == null) return true;
    return node.val < limit && allLess(node.left, limit) && allLess(node.right, limit);
}

private boolean allGreater(TreeNode node, int limit) {
    if (node == null) return true;
    return node.val > limit && allGreater(node.left, limit) && allGreater(node.right, limit);
}
```

**Time:** O(n · h), which is O(n²) for a skewed tree: each node triggers a full scan of its subtree.
**Space:** O(h) recursion.

Not optimal: ancestor constraints are re-verified by rescanning subtrees, when they could be carried down as a pair of bounds in O(1) per node.

## 5. Approach 2 — Optimized (Bounds Recursion)

1. Start with `low = null`, `high = null` (meaning unbounded).
2. At each node, if `low != null && val <= low` or `high != null && val >= high`, return `false`.
3. Recurse left with `(low, val)` and right with `(val, high)`.
4. A null node is valid.

```java
public boolean isValidBST(TreeNode root) {
    return validate(root, null, null);
}

private boolean validate(TreeNode node, Integer low, Integer high) {
    if (node == null) {
        return true;
    }
    if (low != null && node.val <= low) return false;
    if (high != null && node.val >= high) return false;

    return validate(node.left, low, node.val)
        && validate(node.right, node.val, high);
}
```

**Time:** O(n). Each node is visited once with O(1) work.
**Space:** O(h) recursion (O(n) worst case when skewed), no other auxiliary structures.

This is optimal: every node must be examined. The alternative is an iterative in-order traversal that tracks the previous value and requires strict increase: same O(n) time and O(h) space. It is preferable when you want to avoid recursion depth risk (using an explicit stack) or when the in-order sequence is useful elsewhere.

## 6. Dry Run

`root = [5,1,4,null,null,3,6]`. The tricky part is that `3` is in the right subtree of `5` but less than `5`, even though it is a valid left child of `4`.

| Call | node | low | high | Check | Result |
|---|---|---|---|---|---|
| 1 | 5 | null | null | no violation | recurse |
| 2 | 1 (left of 5) | null | 5 | 1 < 5 ok | recurse; both children null, true |
| 3 | 4 (right of 5) | 5 | null | `4 <= 5` | **false** |

Exit: the right child `4` violates `low = 5` immediately, so the `&&` short-circuits and the answer is `false`. Note that the deeper nodes `3` and `6` were never even visited.

## 7. Complexity Summary

| Approach | Time | Space | Notes |
|---|---|---|---|
| Rescan subtrees per node | O(n · h) | O(h) aux | O(n²) when skewed |
| Bounds recursion | O(n) | O(h) aux | Use `Integer` null bounds or `long` bounds; avoids sentinel overflow |
| Iterative in-order, previous value | O(n) | O(h) aux | Strict `prev < current` check; explicit stack avoids deep recursion |

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|---|---|---|
| Single node | Trivially valid | No bounds violated, returns `true` |
| Duplicate values (e.g. `[2,2,2]`) | Strictness: equal values are invalid | Checks use `<=` and `>=` for rejection |
| Node equals `Integer.MIN_VALUE` or `Integer.MAX_VALUE` | `int` sentinels like `Integer.MIN_VALUE` as "no bound" would wrongly reject a legal node | `null` bounds mean unbounded, so no value is mistaken for a sentinel |
| Valid-looking child but invalid against a distant ancestor (the `[5,1,4,null,null,3,6]` case) | Parent-only comparison passes `3 < 4` | Bounds carry the ancestor's constraint down, so `4 <= 5` fails first |
| Skewed tree (depth up to 10^4) | Recursion depth | Acceptable for Java's default stack at this size; iterative in-order is the safe alternative |
| Negative values | Nothing special, but sentinel choices like `0` would be wrong | No arithmetic on values, only comparisons |

## 9. Java Notes

- Using `Integer.MIN_VALUE` / `Integer.MAX_VALUE` as initial bounds is the classic bug here: a root of `Integer.MIN_VALUE` fails the strict `<` test against itself. Use `Integer` with `null`, or `long` bounds (`Long.MIN_VALUE`, `Long.MAX_VALUE`) which cannot collide with any `int` value.
- With boxed `Integer low/high`, comparisons like `node.val <= low` auto-unbox. That is safe here because `null` is checked first. Never compare two `Integer` objects with `==` or `!=`.
- `&&` short-circuiting ordering matters for efficiency, not correctness: the left subtree is validated first and a failure there skips the right subtree entirely.
- Recursion depth can reach 10^4 on a skewed tree; Java's default stack handles this, but an iterative in-order traversal with `ArrayDeque` removes the concern.

## 10. Common Mistakes

- Comparing each node only to its direct children. This accepts `[5,1,4,null,null,3,6]` because `4 > 1`-style local checks pass. Fix: carry ancestor bounds down.
- Using `Integer.MIN_VALUE`/`MAX_VALUE` as sentinels with `int`, which wrongly rejects a legal tree containing those values. Fix: `Integer` null bounds or `long` bounds.
- Using `<` / `>` for rejection instead of `<=` / `>=`, which accepts duplicates. Fix: equality must fail.
- In the in-order variant, initializing `prev` to `Integer.MIN_VALUE` for the same sentinel reason. Fix: track `prev` as `Integer`/`null` or use a "first node seen" flag.

## 11. Interview Takeaway

- **Trigger sentence:** "A BST rule is about whole subtrees, so pass an open `(low, high)` interval down the recursion, or check that in-order is strictly increasing."
- **90-second explanation:** Checking a node against only its children is not enough, because a value deep in the right subtree must still be greater than the root. So I give each recursive call an allowed open interval. Going left, the upper bound becomes the current node's value; going right, the lower bound becomes it. A node fails if it is not strictly between the bounds. I use `null` for unbounded rather than `Integer.MIN_VALUE`/`MAX_VALUE`, because the node values themselves can be those extremes. Each node is visited once, so it is O(n) time and O(h) stack. An equivalent approach is an in-order traversal that checks each value is strictly greater than the previous one, which is handy if I want to avoid deep recursion.
- **Related problems:** 94 (Binary Tree Inorder Traversal), 230 (Kth Smallest Element in a BST), 99 (Recover Binary Search Tree), 530 (Minimum Absolute Difference in BST), 501 (Find Mode in Binary Search Tree).

## 12. Recall Questions

**Q:** Why is comparing a node only to its parent insufficient?
**A:** The BST property applies to entire subtrees, so a node must respect every ancestor's constraint. A right-subtree node can be greater than its parent yet still smaller than the grandparent.

**Q:** Why are `Integer.MIN_VALUE` and `Integer.MAX_VALUE` unsafe as initial bounds?
**A:** Node values can equal them, and the checks are strict, so a legal node with that value would be rejected. `null` or `long` bounds cannot collide with any `int` value.

**Q:** How does going left or right change the bounds?
**A:** Left replaces the upper bound with the parent's value; right replaces the lower bound with it. The other bound is inherited unchanged.

**Q:** Why does in-order traversal solve this problem?
**A:** A BST's in-order sequence is strictly increasing, so the tree is valid iff every visited value is strictly greater than the previous one.

**Q:** What makes duplicates invalid, and where is that enforced in code?
**A:** The definition requires strictly less on the left and strictly greater on the right. The rejection tests use `<=` and `>=`, so equal values fail.

## 13. Final Code

```java
class Solution {
    public boolean isValidBST(TreeNode root) {
        return validate(root, null, null);
    }

    private boolean validate(TreeNode node, Integer low, Integer high) {
        if (node == null) {
            return true;
        }
        if (low != null && node.val <= low) return false;
        if (high != null && node.val >= high) return false;

        return validate(node.left, low, node.val)
            && validate(node.right, node.val, high);
    }
}
```

## 14. Self-Test

Given the root of a binary tree, decide whether it is a valid BST, where every node in a node's left subtree is strictly smaller and every node in its right subtree is strictly larger. Node values may be any `int`, including the extremes. Re-derive: recurse with an open interval `(low, high)` that starts unbounded; reject a node outside it, then recurse left with `high` set to the node's value and right with `low` set to it.
