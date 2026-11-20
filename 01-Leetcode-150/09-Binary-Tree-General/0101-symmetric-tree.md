---
title: "0101 — Symmetric Tree"
layout: default
parent: Binary Tree General
grand_parent: Leetcode
nav_order: 101
permalink: /09-Binary-Tree-General/0101-symmetric-tree/

problem: 101 - Symmetric Tree
url: https://leetcode.com/problems/symmetric-tree/
difficulty: Easy
section: Tree / DFS
patterns: [mirror-comparison, paired-recursion]
data_structures: [tree, queue]
time: O(n)
space: O(h)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0101 — Symmetric Tree

| Field | Value |
|---|---|
| **Pattern** | Mirror Comparison with Paired Recursion |
| **Difficulty** | Easy |
| **Section** | Tree / DFS |
| **Time** | O(n) |
| **Space** | O(h) — h = tree height |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Meta |
| **LeetCode** | [Symmetric Tree](https://leetcode.com/problems/symmetric-tree/) |

## 1. Problem in Simple Words

**Given:** The root of a binary tree.

**Required:** Determine whether the tree is a mirror image of itself around its center (i.e., symmetric).

**Constraints that actually matter:**
- `1 <= number of nodes <= 1000` — the tree is guaranteed non-empty (at least the root exists); moderate size, so performance isn't the real challenge.
- **Symmetry is a property of the *whole tree's shape and values together*, compared against its own mirror** — this is subtly different from simply checking if the left and right subtrees are "the same tree" (LC 100, Same Tree); here, the left subtree must match the **mirror image** of the right subtree, meaning corresponding nodes being compared are not "left's left vs right's left" but rather "left's left vs right's right" and "left's right vs right's left" — getting this cross-comparison pairing right is the central subtlety of the problem.
- The explicit **follow-up** — "Could you solve it both recursively and iteratively?" — signals that both solution styles are worth knowing and being able to produce, rather than there being one single "correct" approach to memorize.

## 2. Recognition Signals

- "Check if a tree is a mirror of itself" → the signature of a **paired mirror-comparison recursion**: rather than processing one tree, conceptually compare *two* trees simultaneously (initially, the root's left subtree and the root's right subtree), where "are these two matching" specifically means "do they mirror each other" — values equal, and each one's left child mirrors the other's right child (and vice versa).
- The key structural insight: symmetry of a single tree reduces to asking "is the left subtree the **mirror image** of the right subtree" — and checking whether two given subtrees are mirror images of each other is itself a recursive problem of the same shape, just applied to a pair of nodes instead of one, with the two recursive sub-checks **deliberately cross-matched** (left's left against right's right, left's right against right's left) rather than matched straight across.
- Whenever a problem involves comparing a structure against a transformed (mirrored, reversed, rotated) version of itself — reframing it as "simultaneously walk both the original and the transformed version, checking they agree at every corresponding position" is a broadly reusable technique, whether implemented recursively (paired recursive calls) or iteratively (a queue or stack holding *pairs* of nodes to compare).

**Pattern:** Mirror Comparison via Paired Recursion (recursively check whether two given subtrees are mirror images of each other: equal root values, and the first's left subtree mirrors the second's right subtree while the first's right subtree mirrors the second's left subtree) — with an iterative version using a queue or stack holding pairs of nodes to compare, directly answering the problem's own follow-up.

## 3. Core Idea

Define a helper notion: two (sub)trees, rooted at nodes `t1` and `t2`, are **mirrors of each other** if: both are `null` (trivially mirrors), or both are non-null with equal values **and** `t1`'s left subtree is a mirror of `t2`'s right subtree **and** `t1`'s right subtree is a mirror of `t2`'s left subtree. The overall tree is symmetric exactly when its root's left subtree is a mirror of its root's right subtree, under this definition. This cross-matching (left-vs-right, right-vs-left) rather than straight matching (left-vs-left, right-vs-right) is precisely what captures "mirror image" rather than "identical copy" — the latter would just be checking if the left and right subtrees are the exact same tree (a different, related problem).

**Invariant:** The helper function correctly returns `true` for a pair `(t1, t2)` if and only if the subtree rooted at `t1` and the subtree rooted at `t2` are exact mirror images of each other — every value matches at corresponding mirrored positions, and every structural branching (where children exist vs. where they're absent) also matches at corresponding mirrored positions. Applying this with `t1 = root.left` and `t2 = root.right` therefore correctly determines whether the whole tree is symmetric around its center.

## 4. Approach 1 — Iterative (Queue-Based, Holding Pairs of Nodes)

**Logic:** Use a queue that holds nodes in pairs (two consecutive entries represent one pair to compare). Initialize it with `root.left` and `root.right`. Repeatedly dequeue two nodes at a time; if both are `null`, they trivially match, continue; if exactly one is `null` or their values differ, the tree isn't symmetric, return `false`; otherwise, enqueue their children in the cross-matched order (first node's left paired with second node's right, then first node's right paired with second node's left) for future comparison.

```java
public boolean isSymmetric(TreeNode root) {
    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root.left);
    queue.offer(root.right);

    while (!queue.isEmpty()) {
        TreeNode t1 = queue.poll();
        TreeNode t2 = queue.poll();

        if (t1 == null && t2 == null) {
            continue;
        }
        if (t1 == null || t2 == null || t1.val != t2.val) {
            return false;
        }

        queue.offer(t1.left);
        queue.offer(t2.right);
        queue.offer(t1.right);
        queue.offer(t2.left);
    }

    return true;
}
```

- **Time:** O(n) — every node is enqueued and dequeued exactly once (as part of exactly one pair-comparison).
- **Space:** O(n) in the worst case for the queue — at the widest point, the queue could hold on the order of all nodes at the tree's widest level (times 2, for the paired entries), proportional to the total node count for a very bushy tree.

**Why it is presented as a valid non-recursive alternative:** This directly answers the problem's explicit follow-up ("could you solve it iteratively") and achieves the same O(n) time complexity as the recursive version, while avoiding recursion-depth concerns entirely. Its space usage (O(n) in the worst case for the queue) can be less favorable than the recursive version's O(h) stack space for a well-balanced tree, but more favorable (or comparable) for a very deep, narrow tree where recursion depth could itself become a practical concern.

## 5. Approach 2 — Optimized/Canonical (Recursive Mirror Check)

**Algorithm:**
1. Define a helper `isMirror(TreeNode t1, TreeNode t2)`:
   - If both `t1` and `t2` are `null`, return `true` (two empty subtrees are trivially mirrors).
   - If exactly one of `t1`, `t2` is `null` (but not both), return `false` (mismatched structure).
   - If `t1.val != t2.val`, return `false` (mismatched values).
   - Otherwise, return `isMirror(t1.left, t2.right) && isMirror(t1.right, t2.left)` (the cross-matched recursive check).
2. The main function returns `isMirror(root.left, root.right)`.

```java
public boolean isSymmetric(TreeNode root) {
    return isMirror(root.left, root.right);
}

private boolean isMirror(TreeNode t1, TreeNode t2) {
    if (t1 == null && t2 == null) {
        return true;
    }
    if (t1 == null || t2 == null) {
        return false;
    }
    if (t1.val != t2.val) {
        return false;
    }

    return isMirror(t1.left, t2.right) && isMirror(t1.right, t2.left);
}
```

- **Time:** O(n) — every node is visited exactly once across the entire recursion (each node participates in exactly one `isMirror` call as either the `t1` or `t2` argument).
- **Space:** O(h), where `h` is the tree's height, for the recursion call stack — O(log n) for a balanced tree, up to O(n) for a completely skewed tree.

**Why this is presented as the canonical/preferred solution:** Both approaches share the same O(n) time complexity; their space complexities depend on tree shape and aren't strictly one-better-than-the-other in general, mirroring the same trade-off discussion as Invert Binary Tree (LC 226). The recursive version is generally preferred here for its directness in expressing the problem's inherently recursive "mirror" definition — the cross-matched recursive calls (`t1.left` vs `t2.right`, `t1.right` vs `t2.left`) make the core insight of the problem immediately visible in the code itself, which is valuable both for correctness confidence and for communicating the approach clearly in an interview setting.

## 6. Dry Run

Example: `root = [1,2,2,3,4,4,3]` (symmetric; the tree's left subtree rooted at the first `2` has children `3,4`, and the right subtree rooted at the second `2` has children `4,3`).
Chosen because it's the canonical "true" example, and it clearly exercises the cross-matching logic at a non-trivial depth.

`isSymmetric(1)` calls `isMirror(2[left], 2[right])` (using `2L` and `2R` to distinguish the two value-2 nodes for clarity).

**`isMirror(2L, 2R)`:** both non-null, values equal (2==2). Recurse: `isMirror(2L.left, 2R.right) && isMirror(2L.right, 2R.left)`, i.e., `isMirror(3, 3) && isMirror(4, 4)`.

**`isMirror(3, 3)`** (2L's left child, value 3, vs 2R's right child, value 3): both non-null, values equal (3==3). Both are leaves, so recurse: `isMirror(null, null) && isMirror(null, null)`, both trivially `true`. Returns `true`.

**`isMirror(4, 4)`** (2L's right child, value 4, vs 2R's left child, value 4): both non-null, values equal (4==4). Both leaves, recurse into `isMirror(null,null) && isMirror(null,null)`, both `true`. Returns `true`.

Back in `isMirror(2L, 2R)`: `true && true` → returns `true`.

Exit condition: the outermost `isMirror(2L, 2R)` call returns.

**Final answer:** `isSymmetric` returns `true`, matching the expected output exactly — this tree is indeed symmetric.

## 7. Complexity Summary

| Approach | Time | Space (Auxiliary) | Notes |
|----------|------|--------------------|-------|
| 1. Iterative, queue-based paired comparison | O(n) | O(n) worst case (queue holding paired nodes) | Correct; directly answers the "iteratively" part of the follow-up |
| 2. Recursive mirror check | O(n) | O(h) (tree height; O(log n) balanced, O(n) worst-case skewed) | Canonical, most directly expresses the mirror-matching insight |

Both approaches visit every node exactly once for the core comparison work (O(n) time); space complexities differ based on tree shape.

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|------|-------------------|--------------------------|
| Single-node tree (just the root, no children) | Trivially symmetric, since there's nothing to compare asymmetrically | `isMirror(root.left, root.right)` is called with both arguments `null` (since a single-node tree has no children), immediately returning `true` via the first base case |
| A tree where corresponding mirrored positions have matching values but mismatched structure (e.g. example 2: `[1,2,2,null,3,null,3]`, where one side has a right child but the mirrored position on the other side has a *left* child instead, or vice versa) | Must correctly detect this structural asymmetry, not just compare values where both happen to exist | The `if (t1 == null || t2 == null) return false;` check (after already ruling out both being null) correctly catches the case where one side has a child at a mirrored position but the other side doesn't, regardless of what values might otherwise be present elsewhere |
| A tree with repeated/duplicate values throughout, but genuinely asymmetric structure | Must correctly use structural (null vs. non-null) comparison, not just value comparison, to detect the asymmetry | Since the algorithm explicitly checks both structural correspondence (null-ness matching) and value equality, as two separate and necessary conditions, duplicate values alone can't mask a genuine structural mismatch |
| A deeply unbalanced (skewed) tree, approaching the maximum constraint size (1000 nodes) | The recursive version's O(h) space could approach O(n) in this worst case, a valid concern the iterative version avoids at the cost of its own O(n) queue space | Both approaches remain correct regardless of tree shape; the recursive version's stack depth would scale with the skew, which is a real but non-fatal consideration at this problem's given constraint size (1000 nodes is well within typical default stack depth limits) |
| Negative node values (constraint allows down to -100) | Value comparisons must work correctly regardless of sign | Plain `!=` integer comparison works identically regardless of sign, with no special-casing needed |
| Minimum constraint size (`n=1`, guaranteed by `1 <= number of nodes`) | Should be handled correctly as the simplest possible valid input | Same as the single-node case described above — correctly and trivially returns `true` |

## 9. Java Notes

- **Cross-matched recursive calls (`isMirror(t1.left, t2.right)` and `isMirror(t1.right, t2.left)`) rather than straight-matched (`t1.left` vs `t2.left`):** this specific pairing is the entire crux of correctly checking for a *mirror* relationship rather than an *identical* one — getting this backward would silently change the problem into checking whether the two subtrees are exact duplicates of each other (a related but different problem, LC 100 Same Tree, applied to the two halves).
- **`Queue<TreeNode>` via `LinkedList`, holding nodes in sequential pairs (iterative approach):** a specific convention where two consecutive `poll()` calls retrieve one logical "pair" to compare — this requires care to always enqueue (and dequeue) in matched pairs consistently, or the pairing would desynchronize.
- **Helper method pattern (`isMirror`, separate from the main `isSymmetric` entry point):** since the recursive check fundamentally operates on a *pair* of nodes (not just one), while the public method signature only takes a single `root`, a separate private helper taking two `TreeNode` parameters is the natural way to express the actual recursive logic.
- **No overflow or numeric risk:** this problem involves only direct value equality comparisons (`!=`) between individual node values (bounded to -100 to 100 per the constraints); no arithmetic that could overflow is ever performed.

## 10. Common Mistakes

- **Comparing `t1.left` against `t2.left` and `t1.right` against `t2.right` (straight matching) instead of the cross-matched pairing.** This would incorrectly check whether the two subtrees are identical copies of each other, not whether they're mirror images — a tree that's symmetric would often (though not always, depending on exact structure) fail this incorrect check, and a tree that merely has identical left/right subtrees (not generally what "symmetric" means) might incorrectly pass. Fix: always cross-match — `t1.left` with `t2.right`, and `t1.right` with `t2.left`.
- **Forgetting to separately check "both null" versus "exactly one null" as two distinct conditions**, potentially conflating them or missing one case entirely (e.g., only checking `t1 == null || t2 == null` without first separately confirming `t1 == null && t2 == null` returns `true`, which could cause the "both null" case to incorrectly fall through to a `false` return or a null-pointer-unsafe value comparison). Fix: always check "both null → true" as the very first case, before checking "exactly one null → false," before finally checking value equality (only reachable once both are confirmed non-null).
- **Attempting to call `isSymmetric` recursively on `root` itself directly, rather than defining a separate two-argument helper comparing `root.left` and `root.right`.** The core recursive check fundamentally needs to compare *two* subtrees against each other, which doesn't fit naturally into a single-argument function signature matching the public method; a dedicated helper is the clean solution. Fix: always introduce a separate `isMirror(TreeNode, TreeNode)` helper for the actual recursive logic.
- **In the iterative version, enqueueing children in the wrong order or forgetting to maintain the pairing discipline** (always enqueueing and dequeueing exactly two nodes per logical comparison), which would desynchronize the pairs being compared and produce incorrect results. Fix: always enqueue and dequeue nodes in consistent pairs, matching the same cross-matched logic as the recursive version.

## 11. Interview Takeaway

- **Trigger sentence to memorize:** "Check if a tree is symmetric (a mirror of itself) → recursively (or iteratively, with paired nodes) compare the left and right subtrees as mirrors: equal values, with left's-left matched against right's-right... no wait, left's-left matched against right's-right is wrong — matched against right's-*right* reversed: left's-left paired with right's-right, left's-right paired with right's-left."
- **90-second explanation:** "Checking if a whole tree is symmetric reduces to checking if its left subtree is a mirror image of its right subtree. I define a helper that takes two nodes and checks whether the subtrees rooted at them are mirrors of each other: if both are empty, that's trivially a match; if only one is empty, or their values differ, it's not a match; otherwise, I recursively check two things — does the first node's left child mirror the second node's right child, and does the first node's right child mirror the second node's left child. This cross-matching, rather than matching left-to-left and right-to-right, is exactly what captures a mirror image rather than an identical copy. I call this helper with the root's left and right children to answer the overall question. The problem's follow-up also asks for an iterative version, which I can do using a queue that holds nodes in matched pairs — I dequeue two at a time, perform the same checks, and if they're both valid matching internal nodes, I enqueue their children in that same cross-matched order for future comparison."
- **Related problems using this pattern:**
  - LeetCode 100 — Same Tree (the closely related "are these two trees identical" problem, using straight matching instead of cross matching)
  - LeetCode 226 — Invert Binary Tree (related tree-mirroring concept, but transforms the tree rather than checking a property of it)
  - LeetCode 951 — Flip Equivalent Binary Trees (a related, more general "could these trees be made equal via flips" problem)

## 12. Recall Questions

**Q:** Why must the recursive mirror check compare `t1`'s left child against `t2`'s *right* child (and vice versa), rather than comparing left-to-left and right-to-right?
**A:** A mirror image reverses the left/right arrangement, so the node that should correspond to `t1`'s left child, in a true mirror, is `t2`'s right child, not `t2`'s own left child — straight-matching left-to-left and right-to-right would instead check whether the two subtrees are identical copies of each other, a different property than being mirror images.

**Q:** Why is it necessary to check "both null" and "exactly one null" as two separate, distinct conditions, rather than combining them into a single check?
**A:** "Both null" represents a valid, successful match (two empty subtrees are trivially mirrors of each other), while "exactly one null" represents a genuine structural mismatch (one side has a node where the other doesn't) — these two scenarios require opposite return values, so they must be distinguished rather than conflated into one combined condition.

**Q:** Why does checking whether a tree is symmetric require a two-argument helper function, rather than being expressible as a simple one-argument recursive function operating only on the single `root` parameter?
**A:** Determining symmetry fundamentally requires comparing two different parts of the tree (the left and right subtrees) against each other simultaneously at every corresponding mirrored position, which inherently requires tracking two separate node references throughout the recursion, not just one.

**Q:** How does this problem's mirror-checking logic relate to, but differ from, the "Same Tree" (LC 100) problem?
**A:** Both problems recursively compare two subtrees node by node, checking for matching values and matching structure, but "Same Tree" matches corresponding children straight across (left-to-left, right-to-right) to check if two trees are identical, while this problem's mirror check deliberately cross-matches children (left-to-right, right-to-left) to check if two subtrees are mirror images of one another instead.

**Q:** Why does the iterative version's queue need to maintain nodes in consistently matched pairs, rather than just processing nodes in a normal, ungrouped level-order traversal?
**A:** Each comparison fundamentally involves checking two specific nodes against each other for a mirror match, so the queue must preserve this pairing discipline — always enqueueing and dequeueing exactly two related nodes together — otherwise the algorithm would lose track of which two nodes are supposed to be compared against each other at each step.

## 13. Final Code

```java
public boolean isSymmetric(TreeNode root) {
    return isMirror(root.left, root.right);
}

private boolean isMirror(TreeNode t1, TreeNode t2) {
    if (t1 == null && t2 == null) {
        return true;
    }
    if (t1 == null || t2 == null) {
        return false;
    }
    if (t1.val != t2.val) {
        return false;
    }

    return isMirror(t1.left, t2.right) && isMirror(t1.right, t2.left);
}
```

## 14. Self-Test

You have the root of a binary tree. Determine whether it's symmetric — a mirror image of itself around its center. Think about why comparing the left and right subtrees as mirrors requires cross-matching each side's left child against the other side's right child (not straight matching), and how you'd express this same check iteratively using a queue of paired nodes.
