---
title: "0100 — Same Tree"
layout: default
parent: Binary Tree General
grand_parent: Leetcode
nav_order: 100
permalink: /09-Binary-Tree-General/0100-same-tree/

problem: 100 - Same Tree
url: https://leetcode.com/problems/same-tree/
difficulty: Easy
section: Tree / DFS-BFS
patterns: [tree-recursion, paired-traversal]
data_structures: [tree, queue]
time: O(n)
space: O(h)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0100 — Same Tree

| Field | Value |
|---|---|
| **Pattern** | Tree Recursion with Paired Traversal |
| **Difficulty** | Easy |
| **Section** | Tree / DFS-BFS |
| **Time** | O(n) |
| **Space** | O(h) — h = tree height (stack for recursion) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Meta |
| **LeetCode** | [Same Tree](https://leetcode.com/problems/same-tree/) |

## 1. Problem in Simple Words

**Given:** the roots of two binary trees, `p` and `q`.

**Required:** return `true` if the two trees are structurally identical **and** every pair of corresponding nodes holds the same value; otherwise return `false`.

**Constraints that matter:**
- `0 <= number of nodes in both trees <= 100` — small; either tree can be empty, and both being empty simultaneously (two `null` roots) must be correctly treated as "the same" (trivially identical, both representing nothing).
- `-10^4 <= Node.val <= 10^4` — plain integer values with no overflow or special-comparison concerns.
- "**Structurally identical**, and the nodes have the same value" — both conditions must hold simultaneously: matching values at every corresponding position is not sufficient if the shapes differ (Example 2 shows this directly: `[1,2]` vs `[1,null,2]` have matching values at the root, but node `2` is a *left* child in one tree and a *right* child in the other — different structure, so not the same tree).

## 2. Recognition Signals

- "Are two trees structurally identical with matching values" → the direct, canonical signal for a **paired recursive tree traversal**, walking both trees simultaneously and comparing at each corresponding position.
- The comparison must account for **both** structure and value together at every node — this rules out any approach that checks values and structure as separate passes (e.g., comparing a serialized value-list from each tree independently of position), since doing so could miss structural mismatches that happen to produce the same flattened value sequence.
- A node-by-node, position-by-position comparison with a need to explicitly handle three distinct relationships at each step (both null, one null, both non-null) → the signature of a simple three-way base-case/recursive-case structure.
- Named pattern: **Paired Tree Recursion** (simultaneous DFS on two trees, comparing corresponding nodes).

## 3. Core Idea

Walk both trees at the same time, starting from their roots. At each pair of corresponding positions: if both nodes are `null`, that part of the structure matches (trivially the same, since neither tree has anything there). If exactly one is `null` and the other isn't, the structures have diverged — not the same. If both are non-null, they must have equal values, **and** their left subtrees must be the same (recursively), **and** their right subtrees must be the same (recursively) — only if all three conditions hold is this pair of subtrees identical.

**Why it's correct:** "the same tree" is inherently a recursive property — two trees are identical exactly when their roots match (in both existence and value) and their corresponding left and right subtrees are, in turn, identical. This directly mirrors how trees are recursively *defined* in the first place (a tree is either empty, or a node with a left subtree and a right subtree), so checking sameness recursively, following that exact same structural decomposition, is both natural and provably complete — every possible way two trees could differ (a value mismatch, a node present in one but missing in the other, a difference anywhere deeper in either subtree) is caught by one of the three comparison branches.

**Invariant:** for any pair of corresponding positions in the two trees, the recursive function correctly returns whether the subtrees rooted at those two positions are identical, assuming (by induction) that its recursive calls on the left and right children correctly determine whether *those* subtrees are identical.

## 4. Approach 1 — Iterative BFS (Paired Queues)

1. If both `p` and `q` are `null`, return `true` immediately (two empty trees).
2. Create two queues, `queueP` and `queueQ`, each initialized with `p` and `q` respectively.
3. While both queues are non-empty: dequeue one node from each. If both are `null`, continue (this pair matched, nothing more to compare here). If exactly one is `null`, or their values differ, return `false`. Otherwise, enqueue each node's left child onto its respective queue, and each node's right child onto its respective queue (preserving the pairing).
4. After the loop, return `true` if both queues ended up empty at the same time (if one still has elements while the other doesn't, the trees have different numbers of nodes at some level, meaning they weren't actually the same — though with correctly paired traversal, this specific imbalance shouldn't arise if all prior checks passed, making this largely a safety consideration).

```java
public boolean isSameTreeBFS(TreeNode p, TreeNode q) {
    Queue<TreeNode> queueP = new LinkedList<>();
    Queue<TreeNode> queueQ = new LinkedList<>();
    queueP.offer(p);
    queueQ.offer(q);

    while (!queueP.isEmpty() && !queueQ.isEmpty()) {
        TreeNode nodeP = queueP.poll();
        TreeNode nodeQ = queueQ.poll();

        if (nodeP == null && nodeQ == null) {
            continue;
        }
        if (nodeP == null || nodeQ == null || nodeP.val != nodeQ.val) {
            return false;
        }

        queueP.offer(nodeP.left);
        queueQ.offer(nodeQ.left);
        queueP.offer(nodeP.right);
        queueQ.offer(nodeQ.right);
    }

    return queueP.isEmpty() && queueQ.isEmpty();
}
```

**Time:** O(n) — each node from both trees is enqueued and dequeued at most once (n being the total node count across both trees, bounded since each tree has at most 100 nodes).
**Space:** O(n) in the worst case for the two queues combined (e.g., a wide, bushy tree could have many nodes queued simultaneously at a given level).

This is a correct, fully valid alternative, though it's generally considered less immediately intuitive for this specific problem than the direct recursive approach — the recursive version follows the problem's own recursive definition almost verbatim, whereas the BFS version requires additional bookkeeping (paired queues, explicit null-handling at dequeue time) to achieve the same result. Both share O(n) time, but the BFS version's O(n) worst-case space (versus the recursive version's typically smaller O(h) space) makes it the less space-efficient choice for a tree that isn't pathologically skewed.

## 5. Approach 2 — Optimized (Recursive DFS, Direct Structural Comparison)

1. Base case 1: if both `p == null` and `q == null`, return `true` (two empty subtrees are trivially the same).
2. Base case 2: if exactly one of `p`, `q` is `null` (the other isn't), return `false` (structural mismatch — one tree has a node here, the other doesn't).
3. Base case 3 (implicit, falls through): if `p.val != q.val`, return `false` (same structural position, but different values).
4. Recursive case: return `isSameTree(p.left, q.left) && isSameTree(p.right, q.right)` (both corresponding subtrees must also be identical).

```java
public boolean isSameTree(TreeNode p, TreeNode q) {
    if (p == null && q == null) {
        return true;
    }
    if (p == null || q == null || p.val != q.val) {
        return false;
    }

    return isSameTree(p.left, q.left) && isSameTree(p.right, q.right);
}
```

**Time:** O(n) — each node (across both trees, up to 100 nodes each) is visited exactly once.
**Space:** O(h) for the recursion call stack, where `h` is the height of the (shorter-terminating, but effectively bounded by either) tree — O(log n) for a balanced tree, O(n) in the worst case for a completely skewed tree; given the small constraint (`n <= 100` per tree), this worst case is entirely manageable and poses no practical stack-overflow risk here, unlike problems with much larger node-count bounds.

This is optimal in the sense of being the clearest, most direct translation of the problem's own recursive definition — it requires no auxiliary data structure at all beyond the call stack itself, and given this problem's small constraints (at most 100 nodes per tree), the recursive approach's stack depth is never a practical concern, making it the natural and preferred solution here.

## 6. Dry Run

`p = [1,2,1]`, `q = [1,1,2]` (Example 3 — both trees have root `1`, but `p`'s left child is `2` and right child is `1`, while `q`'s left child is `1` and right child is `2`)

| call | p node | q node | check | result |
|---|---|---|---|---|
| isSameTree(p, q) | 1 (root) | 1 (root) | both non-null, vals equal (1==1) → recurse on children | pending on children |
| isSameTree(p.left, q.left) | 2 | 1 | both non-null, vals differ (2 != 1) | **false** |

Exit: the very first recursive call on the left subtrees immediately returns `false` due to the value mismatch (`2 != 1`). Because of `&&` short-circuiting, the right-subtree comparison (`isSameTree(p.right, q.right)`) is never even evaluated. Final answer: `false`, matching the expected output.

## 7. Complexity Summary

| Approach | Time | Space | Notes |
|---|---|---|---|
| Iterative BFS (Paired Queues) | O(n) | O(n) worst case (queue width) | Correct, but more bookkeeping than the direct recursive form |
| Recursive DFS | O(n) | O(h) (O(log n) balanced, O(n) worst case skewed) | Directly mirrors the problem's own recursive definition |

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|---|---|---|
| Both trees empty (`p == null && q == null`) | Must be considered "the same" (trivially) | The first base case explicitly returns `true` for this exact scenario |
| One tree empty, the other not | Clearly different trees | The second base case's `p == null \|\| q == null` (without both being null, since that was already handled) correctly returns `false` |
| Same values but different structure (Example 2: `[1,2]` vs `[1,null,2]`) | Flattened values could look superficially similar, but position (left vs. right child) differs | The recursive comparison explicitly checks `p.left` against `q.left` and `p.right` against `q.right` separately — since `p`'s `2` is a left child but `q`'s `2` is a right child, comparing `p.left (2)` against `q.left (null)` correctly detects the mismatch |
| Same structure but different values (e.g. differing by one node's value deep in the tree) | Must be detected regardless of how deep the mismatch is | The recursion naturally propagates to every node; a value mismatch at any depth triggers `p.val != q.val` at that specific recursive call, returning `false` and short-circuiting the rest |
| Identical trees (all values and structure matching exactly) | Must correctly return `true` only when truly everything matches | Every recursive call successfully matches both value and structure, and the base case of two `null`s is eventually reached at every leaf's children, with no `false` ever triggered |
| Single-node trees with equal vs. differing values | Minimal non-trivial comparison | `p.left/right` and `q.left/right` are all `null`, so both recursive sub-calls immediately hit the "both null" base case and return `true`; the overall result depends purely on whether the single root values match |

## 9. Java Notes

- `p == null && q == null` uses reference equality (`==`), which is correct and intended here, since checking for `null` is inherently a reference comparison, not a value comparison — this is unrelated to the `Integer`/`Character` caching pitfalls that apply specifically to boxed numeric/character *value* comparisons.
- `p.val != q.val` compares two `int` primitives directly, which is always safe and exact in Java — no boxing or caching concerns apply here since `TreeNode.val` is typically declared as a primitive `int`, not an `Integer`.
- The `&&` operator's short-circuit evaluation means that if `isSameTree(p.left, q.left)` returns `false`, the right-subtree comparison `isSameTree(p.right, q.right)` is never even called — this is a meaningful efficiency detail, not just a syntactic convenience, since it avoids unnecessary recursive work once a mismatch is already found.
- `Queue<TreeNode> queueP = new LinkedList<>();` in the BFS version is the standard idiomatic choice for a FIFO queue backing a level-order-style traversal in Java.

## 10. Common Mistakes

- Comparing only node values during traversal without separately verifying both nodes' null-status at each step (e.g., just checking `p.val == q.val` without first confirming neither is `null`) — this throws a `NullPointerException` the moment one of the two trees has a missing node where the other has a value, since `.val` can't be accessed on a `null` reference. Fix: always check for `null` on both sides *before* attempting to access `.val`.
- Checking `p.val != q.val` as the very first comparison, before handling the `null` cases — this also risks a `NullPointerException` if either node is `null`, since the null checks must happen first to safely guard against invalid field access. Fix: always structure the base cases so null-checking strictly precedes value comparison.
- Using `||` instead of `&&` when combining the recursive left and right subtree checks (i.e., `isSameTree(p.left, q.left) || isSameTree(p.right, q.right)`) — this would incorrectly report trees as "the same" if only *one* of the two subtrees matches, when actually **both** must match for the trees to be truly identical. Fix: always combine the two recursive subtree checks with `&&`, requiring both to hold.
- Attempting to solve this by serializing each tree into a flattened list of values (e.g., via an in-order or pre-order traversal) and comparing the two lists for equality, without separately encoding structural information (like explicit `null` markers at every missing child position) — this can produce false positives for genuinely different tree shapes that happen to flatten to the same value sequence (precisely the trap Example 2 is designed to illustrate). Fix: either compare structure directly via paired recursive/iterative traversal (as shown here), or, if serializing, make sure to explicitly include markers for every `null` child position so structural differences are preserved in the serialized form.

## 11. Interview Takeaway

- **Trigger sentence:** "Are two trees structurally identical with matching values at every position → paired recursive traversal, checking both-null, one-null, and value-equality at each corresponding pair of nodes."
- **90-second explanation:** "Two trees are the same exactly when their roots match — both in whether they exist at all, and in their value — and their corresponding left and right subtrees are, in turn, also the same. So I walk both trees together: if both current nodes are null, that part trivially matches, since neither tree has anything there. If only one is null, or their values differ, the trees have diverged and I can return false immediately. Otherwise, I recursively check that both the left subtrees match each other and the right subtrees match each other — both have to hold, not just one, since matching only one side while the other differs still means the overall trees aren't identical. This directly follows the recursive definition of what it even means for two trees to be the same, so there's no extra bookkeeping needed beyond the natural recursive call structure itself."
- **Related problems:** 101 (Symmetric Tree, a closely related mirror-comparison variant), 572 (Subtree of Another Tree, builds directly on this exact function as a helper), 951 (Flip Equivalent Binary Trees, a further variant allowing child-swapping).

## 12. Recall Questions

**Q:** Why must the null-checking base cases be evaluated before any access to `.val` on either node?
**A:** Attempting to read `.val` on a `null` reference throws a `NullPointerException`, so the code must first confirm that both nodes being compared are actually non-null before it's safe to compare their values at all.

**Q:** Why does the recursive case combine the left and right subtree comparisons with `&&` rather than `||`?
**A:** Two trees are only truly identical if *every* corresponding part matches, so both the left subtrees and the right subtrees must independently be confirmed identical — using `||` would incorrectly accept trees where only one side matches while the other clearly differs.

**Q:** Why can two trees have the exact same multiset of values, in the same traversal order even, and still not be "the same tree"?
**A:** "Same tree" requires matching structure as well as matching values, and a flattened value sequence alone can discard positional information — for example, a value appearing as a left child in one tree versus a right child in another can produce identical flattened sequences despite being structurally different trees, as Example 2 demonstrates directly.

**Q:** Why does the `&&` short-circuit behavior in `isSameTree(p.left, q.left) && isSameTree(p.right, q.right)` matter beyond just being valid syntax?
**A:** If the left-subtree comparison already returns `false`, there's no need to also check the right subtree, since the overall answer is already determined to be `false` regardless of what the right side looks like — short-circuiting avoids this unnecessary recursive work.

**Q:** Why is the recursive DFS approach considered the more natural fit for this problem compared to the iterative BFS alternative, even though both achieve the same O(n) time complexity?
**A:** The recursive approach's structure directly mirrors the problem's own recursive definition of tree sameness (matching roots, plus recursively matching subtrees), requiring no additional bookkeeping, whereas the BFS approach needs explicit paired queues and manual null-handling at dequeue time to achieve the same correctness, making it comparatively more complex for an equivalent result.

## 13. Final Code

```java
/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode() {}
 *     TreeNode(int val) { this.val = val; }
 *     TreeNode(int val, TreeNode left, TreeNode right) {
 *         this.val = val;
 *         this.left = left;
 *         this.right = right;
 *     }
 * }
 */
class Solution {
    public boolean isSameTree(TreeNode p, TreeNode q) {
        if (p == null && q == null) {
            return true;
        }
        if (p == null || q == null || p.val != q.val) {
            return false;
        }

        return isSameTree(p.left, q.left) && isSameTree(p.right, q.right);
    }
}
```

## 14. Self-Test

Given the roots of two binary trees, determine whether they are structurally identical with matching values at every corresponding node. Re-derive: recursively compare node pairs — if both are null, they match; if only one is null or their values differ, they don't; otherwise, recursively require both the left subtrees and the right subtrees to also match.
