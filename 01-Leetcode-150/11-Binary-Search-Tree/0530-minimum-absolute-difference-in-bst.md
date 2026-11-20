---
title: "0530 — Minimum Absolute Difference in BST"
layout: default
parent: Binary Search Tree
grand_parent: Leetcode
nav_order: 530
permalink: /11-Binary-Search-Tree/0530-minimum-absolute-difference-in-bst/

problem: 530 - Minimum Absolute Difference in BST
url: https://leetcode.com/problems/minimum-absolute-difference-in-bst/
difficulty: Easy
section: Binary Search Tree
patterns: [inorder-traversal, previous-node-tracking]
data_structures: [binary-search-tree]
time: O(n)
space: O(h)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0530 — Minimum Absolute Difference in BST

| Field | Value |
|---|---|
| **Pattern** | Inorder Traversal with Previous-Node Tracking |
| **Difficulty** | Easy |
| **Section** | Binary Search Tree |
| **Time** | O(n) |
| **Space** | O(h) — h = tree height |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Meta |
| **LeetCode** | [Minimum Absolute Difference in BST](https://leetcode.com/problems/minimum-absolute-difference-in-bst/) |

## 1. Problem in Simple Words

**Given:** The root of a Binary Search Tree (BST).

**Required:** Return the smallest absolute difference between the values of any two different nodes in the tree.

**Constraints that actually matter:**
- `2 <= number of nodes <= 10^4` — there are always at least two nodes, so at least one pair exists and the answer is always well defined. A brute force that compares all pairs is up to about 5 * 10^7 comparisons, which is slow but borderline; a linear solution is expected.
- `0 <= Node.val <= 10^5` — values are small and non-negative. Differences can never overflow `int`, and values like `0` must be handled as ordinary values (do not use `0` or `-1` carelessly as a "nothing seen yet" marker).
- **The tree is a BST.** This is the key hint. The problem is not about a general tree: the sorted-order property of a BST is what lets you avoid comparing all pairs. An inorder traversal of a BST visits values in ascending order.

## 2. Recognition Signals

- "Minimum difference between any two values" + "BST" → the BST's inorder traversal gives the values in sorted order, and in a sorted sequence the minimum difference is always between **adjacent** elements.
- "Any two different nodes" sounds like an all-pairs problem, but sortedness collapses it to comparing each value with its immediate predecessor only.
- Whenever a BST problem involves relationships between values (closest, kth, validity, ordering), think **inorder traversal with a running "previous visited node"**.

**Pattern:** Inorder traversal with a previous-node pointer. Walk the BST in ascending order and compare each value only with the value visited just before it.

## 3. Core Idea

An inorder traversal of a BST produces its values in strictly ascending order. For any sorted sequence, the minimum absolute difference between two elements is attained by some adjacent pair. Reason: take any non-adjacent pair `a < c` with some element `b` between them. Then `b - a < c - a` and `c - b < c - a`, so an adjacent pair is always at least as close as the non-adjacent pair.

So it is enough to traverse the tree inorder, remember the value of the previously visited node, and at every node update the answer with `current - previous`.

**Invariant:** When a node is visited, `prev` holds the value of the node visited immediately before it in sorted order, and `min` is the smallest adjacent difference among all nodes visited so far. After the traversal ends, `min` is the smallest difference over all adjacent pairs, which equals the smallest difference over all pairs.

## 4. Approach 1 — Brute Force (Collect Values, Compare All Pairs)

**Logic:** Traverse the tree (in any order) to collect every value into a list, then compare every pair of values and keep the smallest absolute difference.

```java
public int getMinimumDifference(TreeNode root) {
    List<Integer> values = new ArrayList<>();
    collect(root, values);

    int min = Integer.MAX_VALUE;
    for (int i = 0; i < values.size(); i++) {
        for (int j = i + 1; j < values.size(); j++) {
            min = Math.min(min, Math.abs(values.get(i) - values.get(j)));
        }
    }
    return min;
}

private void collect(TreeNode node, List<Integer> values) {
    if (node == null) {
        return;
    }
    values.add(node.val);
    collect(node.left, values);
    collect(node.right, values);
}
```

- **Time:** O(n²) for the nested loops, plus O(n) for collecting the values.
- **Space:** O(n) for the list, plus O(h) for the recursion stack.

**Why it is not optimal:** It ignores the BST property completely. With up to `10^4` nodes it does about `5 * 10^7` pair comparisons, and it would work identically on an arbitrary tree, which is a sign that it is not using the structure the problem gives you. A middle-ground variant, collecting values via an inorder traversal and comparing only neighbors in the list, is O(n) time but still uses O(n) extra space for the list. The next approach removes that list.

## 5. Approach 2 — Optimized (Inorder Traversal with a Previous Pointer)

**Algorithm:**
1. Keep two pieces of shared state: `prev` (the value of the previously visited node, initially absent) and `min` (the best difference so far, initially `Integer.MAX_VALUE`).
2. Do an inorder traversal: recurse left, visit the node, recurse right.
3. When visiting a node: if `prev` is present, set `min = Math.min(min, node.val - prev)`. Then set `prev = node.val`.
4. After the traversal, return `min`.

```java
class Solution {
    private Integer prev = null;
    private int min = Integer.MAX_VALUE;

    public int getMinimumDifference(TreeNode root) {
        inorder(root);
        return min;
    }

    private void inorder(TreeNode node) {
        if (node == null) {
            return;
        }

        inorder(node.left);

        if (prev != null) {
            min = Math.min(min, node.val - prev);
        }
        prev = node.val;

        inorder(node.right);
    }
}
```

- **Time:** O(n). Each node is visited exactly once and does O(1) work.
- **Space:** O(h) auxiliary for the recursion stack, where `h` is the tree height. That is O(log n) for a balanced BST and O(n) for a completely skewed one. No list of values is stored.

**Why this is optimal:** Every node must be looked at at least once, so O(n) time is a lower bound. The space is only the traversal stack, the minimum an inorder traversal needs without modifying the tree. Because the traversal visits values in sorted order, only adjacent differences are checked, and that is provably enough.

## 6. Dry Run

Example: `root = [4,2,6,1,3]`. Chosen because the closest pair (3 and 4) is **not** a parent and child, which exercises the reason for using inorder neighbors.

The tree: `4` has left child `2` (with children `1` and `3`) and right child `6`. Inorder order: `1, 2, 3, 4, 6`.

Initial: `prev = null`, `min = MAX_VALUE`.

| Visit order | node.val | prev before | Action | min after | prev after |
|-------------|----------|-------------|--------|-----------|------------|
| 1 | 1 | null | no difference yet | MAX_VALUE | 1 |
| 2 | 2 | 1 | min(MAX, 2-1=1) | 1 | 2 |
| 3 | 3 | 2 | min(1, 3-2=1) | 1 | 3 |
| 4 | 4 | 3 | min(1, 4-3=1) | 1 | 4 |
| 5 | 6 | 4 | min(1, 6-4=2) | 1 | 6 |

Exit condition: the traversal finishes after visiting all five nodes.

**Final answer:** `1`, matching the expected output. Note that the pair `(3, 4)` was found as adjacent inorder values even though node 3 is the right child of node 2, not a child of node 4.

## 7. Complexity Summary

| Approach | Time | Space (auxiliary) | Notes |
|----------|------|-------------------|-------|
| 1. Collect values, compare all pairs | O(n²) | O(n) list + O(h) stack | Ignores the BST property |
| (Middle ground) Inorder into a list, compare neighbors | O(n) | O(n) list + O(h) stack | Correct, but stores every value |
| 2. Inorder with a previous pointer | O(n) | O(h) stack | Optimal; no extra list |

Input space for the tree is O(n) in all cases. The answer itself is a single integer.

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|------|------------------|--------------------------|
| Exactly two nodes (the minimum allowed) | There is only one pair, so `min` must be updated exactly once | The first visit only sets `prev`; the second visit computes the single difference, so the sentinel `MAX_VALUE` is replaced and never returned |
| Closest pair is not parent and child (as in the dry run) | Comparing only a node with its direct children would miss pairs like 3 and 4 | Inorder visits 3 immediately before 4, so they are compared as neighbors regardless of tree shape |
| Node value `0` | Using `0` as a "no previous node" sentinel would confuse a real value `0` with "nothing seen" | `prev` is an `Integer` that is `null` until the first visit, so a real `0` is treated like any other value |
| Completely skewed tree (a chain of up to 10^4 nodes) | Recursion depth reaches `n`, which can risk a stack overflow in some environments | The algorithm is still correct; if depth is a concern, use the iterative inorder variant in the Java Notes |
| Values at the upper bound (`10^5`) | Large values might suggest an overflow concern | Differences are at most `10^5`, far from the `int` limit, so no special handling is needed |
| Differences of the same size appearing in several places | Ties must not break the minimum tracking | `Math.min` keeps the smaller value and is unaffected by equal candidates |

## 9. Java Notes

- **`Integer prev = null` as the "nothing visited yet" marker:** this avoids a magic sentinel value. Since `0` is a legal node value, using `0` (or any in-range number) as the initial `prev` would be ambiguous. Use a boxed `Integer` or a separate boolean.
- **`int min = Integer.MAX_VALUE`:** a safe starting value because every real difference is at most `10^5`. It is guaranteed to be overwritten because there are at least two nodes.
- **Shared state across recursive calls:** `prev` and `min` must persist between calls, so they are instance fields (or wrapped in an array). A local `int` parameter would be copied per call, and updates would be lost. This is the same shared-state idea used in other tree-recursion problems.
- **Recursion depth:** a skewed tree reaches depth `n` (up to `10^4`). That usually fits in Java's default stack, but an iterative inorder traversal avoids the concern entirely:

```java
public int getMinimumDifference(TreeNode root) {
    Deque<TreeNode> stack = new ArrayDeque<>();
    TreeNode current = root;
    Integer prev = null;
    int min = Integer.MAX_VALUE;

    while (current != null || !stack.isEmpty()) {
        while (current != null) {
            stack.push(current);
            current = current.left;
        }
        current = stack.pop();

        if (prev != null) {
            min = Math.min(min, current.val - prev);
        }
        prev = current.val;

        current = current.right;
    }
    return min;
}
```

- `ArrayDeque` is preferred over the legacy `Stack` class for a stack in modern Java.

## 10. Common Mistakes

- **Comparing each node only with its direct children (or parent).** This misses pairs such as a node and the maximum of its left subtree, as with 4 and 3 in the example. The closest value to a node is its inorder predecessor or successor, which may be deep in a subtree. Fix: traverse inorder and compare with the previously visited node.
- **Not sharing `prev` across recursive calls.** Passing `prev` as a plain `int` parameter means each call modifies only its own copy, so later nodes compare against the wrong value or `null`. Fix: store `prev` (and `min`) as fields, or in a mutable holder.
- **Using a pre-order or post-order traversal with the previous-node idea.** Only inorder visits a BST in sorted order; other orders give "previous" nodes that are not the closest in value. Fix: always recurse left, then visit, then recurse right.
- **Initialising `prev` to a real in-range value such as `0`.** A first node with a value near that sentinel produces a bogus difference, and a real node with value `0` is indistinguishable from "no previous". Fix: use `null` (boxed `Integer`) or an explicit boolean for "no previous node yet".

## 11. Interview Takeaway

- **Trigger sentence to memorize:** "Closest or minimum difference between values in a BST → inorder traversal gives sorted order, so only compare each node with the previous visited node."
- **How to explain it aloud in 90 seconds:** "The tree is a BST, so an inorder traversal visits the values in ascending order. In any sorted list the smallest difference between two elements is always between neighbors, because if two elements have something between them, the pair of neighbors involving that middle element is at least as close. So I don't need to compare all pairs. I do an inorder traversal and keep the value of the previously visited node. At each node, if there is a previous value, I update the minimum with the current value minus the previous one, then set previous to the current value. I keep previous and the minimum as shared state across the recursion. Each node is visited once, so time is O(n), and the only extra space is the recursion stack, O(h). If I'm worried about a deeply skewed tree overflowing the stack, I can do the same thing with an explicit stack iteratively."
- **Related problems using this pattern:**
  - LeetCode 783 — Minimum Distance Between BST Nodes (identical problem)
  - LeetCode 98 — Validate Binary Search Tree
  - LeetCode 230 — Kth Smallest Element in a BST
  - LeetCode 94 — Binary Tree Inorder Traversal

## 12. Recall Questions

**Q:** Why is it enough to compare only adjacent values in sorted order to find the minimum difference?
**A:** For any non-adjacent pair `a < c` there is some `b` between them, and both `b - a` and `c - b` are smaller than `c - a`, so an adjacent pair is always at least as close.

**Q:** Why must the traversal be inorder rather than pre-order or post-order?
**A:** Only inorder visits the nodes of a BST in ascending value order, which is what makes "the previous visited node" the closest smaller value.

**Q:** What goes wrong if you only compare each node with its parent or children?
**A:** The closest value to a node can be its inorder predecessor or successor deep inside a subtree, such as 3 and 4 in the example, so a local comparison can miss the true minimum.

**Q:** Why is `prev` stored as a field, or in a mutable holder, instead of being passed down as a parameter?
**A:** Java passes primitives by value, so a parameter update would stay inside one call. The value left by the left subtree must be visible when processing the current node and the right subtree.

**Q:** Why is `null` a better "no previous node yet" marker than `0` or `-1` here?
**A:** `0` is a legal node value, and any in-range sentinel can be confused with a real value or produce a bogus first difference. `null` is unambiguous.

**Q:** How can the same idea avoid recursion-depth risk on a skewed tree?
**A:** Use an iterative inorder traversal with an explicit stack, tracking `prev` and `min` in local variables.

## 13. Final Code

```java
class Solution {
    private Integer prev = null;
    private int min = Integer.MAX_VALUE;

    public int getMinimumDifference(TreeNode root) {
        inorder(root);
        return min;
    }

    private void inorder(TreeNode node) {
        if (node == null) {
            return;
        }

        inorder(node.left);

        if (prev != null) {
            min = Math.min(min, node.val - prev);
        }
        prev = node.val;

        inorder(node.right);
    }
}
```

## 14. Self-Test

You have the root of a Binary Search Tree with at least two nodes. Return the smallest absolute difference between the values of any two different nodes. Think about what order a BST can be walked in so that the closest values end up next to each other, and what single piece of state you must carry between visits.
