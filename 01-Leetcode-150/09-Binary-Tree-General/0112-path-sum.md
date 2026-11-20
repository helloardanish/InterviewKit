---
title: "0112 — Path Sum"
layout: default
parent: Binary Tree General
grand_parent: Leetcode
nav_order: 112
permalink: /09-Binary-Tree-General/0112-path-sum/

problem: 112 - Path Sum
url: https://leetcode.com/problems/path-sum/
difficulty: Easy
section: Tree / DFS
patterns: [tree-recursion, running-sum-subtraction]
data_structures: [tree]
time: O(n)
space: O(h)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0112 — Path Sum

| Field | Value |
|---|---|
| **Pattern** | Tree Recursion with Running-Sum Subtraction |
| **Difficulty** | Easy |
| **Section** | Tree / DFS |
| **Time** | O(n) |
| **Space** | O(h) — h = tree height |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Meta |
| **LeetCode** | [Path Sum](https://leetcode.com/problems/path-sum/) |

## 1. Problem in Simple Words

**Given:** the root of a binary tree and an integer `targetSum`.

**Required:** return `true` if there exists at least one **root-to-leaf** path whose node values sum exactly to `targetSum`.

**Constraints that matter:**
- `0 <= number of nodes <= 5000` — the empty tree is explicitly allowed (Example 3) and must return `false` regardless of `targetSum`, since there are no root-to-leaf paths at all in an empty tree (not even a trivial one, since there's no root).
- **"A leaf is a node with no children"** — this is an explicit, important definitional clarification: a path must terminate at a true leaf (both children absent), not just stop partway at any arbitrary node; a path ending at a node with only one child present doesn't count, even if the partial sum happens to equal `targetSum` at that point.
- `-1000 <= Node.val, targetSum <= 1000` — values (and their sums along a path of up to 5000 nodes) stay comfortably within `int` range even in the most extreme case (5000 × 1000 = 5,000,000), so no overflow concerns.

## 2. Recognition Signals

- "Does a root-to-leaf path sum to a target" → the direct, canonical signal for **tree DFS with a running accumulator**, since a path is inherently defined by a root-to-node sequence, and DFS naturally explores exactly these sequences.
- The explicit **leaf** requirement (not just "any node") → the base case must specifically check `node.left == null && node.right == null`, not just `node == null` — a common and important distinction from the simpler "Maximum Depth" style problems where any null check suffices as the base case.
- The most natural way to check a running sum against a target while descending is to **subtract each node's value from the target as you go**, so that by the time you reach a leaf, checking "does the remaining target equal this leaf's value" is equivalent to checking "did the full path sum to the original target" — this avoids needing to pass an accumulating sum *and* compare it only at the very end against the original target in a separate step.
- Named pattern: **Tree DFS with Running Target Subtraction**.

## 3. Core Idea

Recursively descend the tree, subtracting each visited node's value from the remaining target as you go. At a leaf node, the path sums to the original `targetSum` if and only if the node's own value exactly equals whatever target remains by the time you reach it. For a non-leaf node, the answer is `true` if *either* the left subtree or the right subtree (recursed into with the reduced target) can complete a valid path.

**Why it's correct:** a root-to-leaf path's total sum equals `targetSum` exactly when, after subtracting every node's value along that path from `targetSum`, the result is exactly the value of the final (leaf) node still waiting to be subtracted — equivalently, if you subtract the leaf's value too, the remainder is zero. Framing the check as "does `remainingSum - leaf.val == 0`" (or identically, "does `leaf.val == remainingSum`" before subtracting the leaf itself) is just an algebraic rearrangement of "does the sum of all values along this path equal `targetSum`," computed incrementally rather than requiring the full path to be summed and compared only at the end.

**Invariant:** at any point during the recursive descent, `remainingSum` (the value being passed into the current recursive call) correctly equals `targetSum` minus the sum of all ancestor values already subtracted along the current root-to-this-node path — so a leaf satisfies the target exactly when its own value equals whatever `remainingSum` it receives.

## 4. Approach 1 — Brute Force (Collect All Root-to-Leaf Path Value Lists, Then Sum Each)

1. Recursively explore the tree, building up a `List<Integer>` representing the full sequence of values from the root to the current node, at every step.
2. Whenever a leaf is reached, compute the sum of the entire accumulated list for that path and compare it against `targetSum`.
3. Return `true` if any path's full sum matches.

```java
public boolean hasPathSumBruteForce(TreeNode root, int targetSum) {
    if (root == null) {
        return false;
    }
    return explore(root, new ArrayList<>(), targetSum);
}

private boolean explore(TreeNode node, List<Integer> pathSoFar, int targetSum) {
    pathSoFar.add(node.val);

    if (node.left == null && node.right == null) {
        int sum = 0;
        for (int v : pathSoFar) {
            sum += v;
        }
        boolean found = (sum == targetSum);
        pathSoFar.remove(pathSoFar.size() - 1);
        return found;
    }

    boolean leftResult = node.left != null && explore(node.left, pathSoFar, targetSum);
    boolean rightResult = !leftResult && node.right != null && explore(node.right, pathSoFar, targetSum);

    pathSoFar.remove(pathSoFar.size() - 1);
    return leftResult || rightResult;
}
```

**Time:** O(n · h) in the worst case — each leaf triggers an O(path length) summation, and path lengths are bounded by the tree's height `h`; across all leaves, this can total up to O(n · h), which degrades to O(n²) for a completely skewed tree.
**Space:** O(h) for the `pathSoFar` list at any given moment (bounded by the current recursion depth), plus O(h) for the recursion stack itself — comparable to the optimized approach's space, but with needlessly repeated summation work at every leaf.

This is not optimal: re-summing the entire accumulated path at every leaf redoes work that could instead be maintained incrementally as the recursion descends — there's no need to store the whole path's values and sum them after the fact when the running total (or equivalently, the running remainder) can simply be carried along and updated by O(1) work per node.

## 5. Approach 2 — Optimized (Running Sum Subtraction, No Path Storage)

1. Base case: if `root == null`, return `false` (an empty (sub)tree can't satisfy any path sum requirement — there's no path here at all).
2. Leaf check: if `root.left == null && root.right == null` (this is a true leaf), return `root.val == targetSum` (does this leaf's value exactly account for whatever target sum remains).
3. Recursive case: return `hasPathSum(root.left, targetSum - root.val) || hasPathSum(root.right, targetSum - root.val)` — recurse into both children with the target reduced by the current node's value, succeeding if either subtree finds a valid path.

```java
public boolean hasPathSum(TreeNode root, int targetSum) {
    if (root == null) {
        return false;
    }

    if (root.left == null && root.right == null) {
        return root.val == targetSum;
    }

    int remaining = targetSum - root.val;
    return hasPathSum(root.left, remaining) || hasPathSum(root.right, remaining);
}
```

**Time:** O(n) — every node is visited at most once across the entire recursion (with `||` short-circuiting potentially skipping some right-subtree calls once a left-subtree match is already found, making this an upper bound, not always a tight one).
**Space:** O(h) for the recursion call stack, where `h` is the tree's height — O(log n) for a balanced tree, up to O(n) in the worst case for a completely skewed tree (with `n` up to 5000, this worst case remains entirely manageable and poses no practical stack-overflow risk for this problem's constraints).

This is optimal: it eliminates the brute force's redundant re-summation at every leaf by maintaining the "remaining target" incrementally as a single integer passed through the recursion, achieving true O(n) time with no auxiliary storage beyond the call stack itself.

## 6. Dry Run

`root = [5,4,8,11,null,13,4,7,2,null,null,null,1]`, `targetSum = 22` (tree: `5` → children `4,8`; `4` → child `11` (no right); `11` → children `7,2`; `8` → children `13,4`; the second `4` → child `1` (no left))

Following the path `5 -> 4 -> 11 -> 2` (one of the valid root-to-leaf paths):

| call | node | targetSum passed in | leaf? | remaining if not leaf | result |
|---|---|---|---|---|---|
| hasPathSum(5, 22) | 5 | 22 | no | 22-5=17 | recurse left(4,17) \|\| right(8,17) |
| hasPathSum(4, 17) | 4 | 17 | no (has left child 11) | 17-4=13 | recurse left(11,13) (right is null, short-circuits to false if left fails) |
| hasPathSum(11, 13) | 11 | 13 | no | 13-11=2 | recurse left(7,2) \|\| right(2,2) |
| hasPathSum(7, 2) | 7 | 2 | yes (leaf) | — | 7==2? **false** |
| hasPathSum(2, 2) | 2 | 2 | yes (leaf) | — | 2==2? **true** |

Exit: `hasPathSum(11,13)` returns `true` (via the right branch, since left returned `false`); this propagates back up through `hasPathSum(4,17)` → `true`, and then `hasPathSum(5,22)`'s left branch already returns `true`, short-circuiting the `||` so the right branch (the `8` subtree) is never explored. Final answer: `true`, matching the expected output (the path `5+4+11+2 = 22`).

## 7. Complexity Summary

| Approach | Time | Space | Notes |
|---|---|---|---|
| Collect Full Paths, Sum at Each Leaf | O(n·h) worst case (O(n²) skewed) | O(h) list + O(h) recursion | Redundant re-summation at every leaf |
| Running Sum Subtraction | O(n) | O(h) recursion only | Maintains remaining sum incrementally, no re-summation |

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|---|---|---|
| Empty tree (`root == null`) | No paths exist at all, not even a trivial one | The very first base case `root == null` returns `false` immediately, regardless of `targetSum` (correctly matching Example 3, where `targetSum = 0` still yields `false`) |
| Single-node tree where the root's value equals `targetSum` | The root is itself both the start and the leaf of the only path | The leaf check (`root.left == null && root.right == null`) correctly identifies the root as a leaf in this case, and `root.val == targetSum` directly determines the answer |
| A node with only **one** child (not two, not zero) | Must NOT be treated as a leaf, even though it might seem like a "dead end" if the other branch were mistakenly ignored | The leaf check explicitly requires **both** `left == null` and `right == null`; a node with just one child fails this check and correctly proceeds to the recursive case, exploring only the subtree that actually exists (the recursive call on the `null` child naturally returns `false` via the base case, contributing nothing but correctly not short-circuiting the real child's exploration incorrectly) |
| A partial-path sum coincidentally equals `targetSum` at a non-leaf node | Must not be mistaken for a valid answer | The leaf check gates the success condition — reaching a target sum of zero (or matching value) at a non-leaf is irrelevant; only reaching it exactly at a true leaf counts |
| Negative node values mixed with positive ones | Running sum must correctly handle addition/subtraction in any direction | Plain integer subtraction (`targetSum - root.val`) handles negative values with no special-casing required |
| No valid path exists anywhere in the tree (Example 2) | Must correctly return `false` after exhausting all possibilities | Every leaf's check fails (no leaf's value matches its respective remaining target), and the `||` chains correctly propagate `false` all the way back up to the top-level call |

## 9. Java Notes

- `root.left == null && root.right == null` is the precise, correct definition of a leaf for this problem — using just `root == null` as the base/success-check point (a common pattern in simpler tree problems like Maximum Depth) would be a conceptual mismatch here, since this problem specifically requires the path to terminate at a genuine leaf, not just anywhere a `null` happens to be encountered.
- The `||` operator's short-circuit evaluation means that once `hasPathSum(root.left, remaining)` returns `true`, `hasPathSum(root.right, remaining)` is never even called — this is a meaningful, automatic efficiency gain (avoiding unnecessary exploration of a subtree once a valid path is already confirmed elsewhere), not just incidental syntax.
- `targetSum - root.val` naturally handles negative node values and negative target sums alike, since it's plain integer arithmetic with no special range restrictions needed given the problem's modest bounds (`-1000` to `1000` for both).
- No explicit path-tracking data structure (list, stack, etc.) is needed in the optimized solution — the "path so far" is implicitly represented purely by the sequence of recursive calls and the single `remaining`/`targetSum` integer threaded through them, which is both simpler and more memory-efficient than explicitly maintaining a growing list.

## 10. Common Mistakes

- Checking `root == null` as the success condition (i.e., treating "falling off the tree" as validation) instead of explicitly checking for a true leaf — this would incorrectly validate paths that don't actually terminate at a leaf, or fail to correctly distinguish valid leaf-ending paths from paths that merely ran out of one side's children. Fix: always explicitly check `root.left == null && root.right == null` to identify a genuine leaf.
- Checking `root.val == targetSum` at every node (not just leaves) and returning `true` as soon as this matches anywhere in the tree — this incorrectly treats any node whose cumulative path sum happens to hit the target as a valid answer, even if that node isn't a leaf and the path doesn't actually end there. Fix: only perform the final sum-equality check at true leaves; non-leaf nodes should only ever recurse further, never short-circuit as a "success" on their own.
- Using `targetSum -= root.val` as a mutation on a shared/instance variable instead of passing a locally-scoped, freshly-computed `remaining` value into each recursive call — since the left and right subtrees need independent copies of "target minus everything subtracted so far along *this specific path*," mutating a single shared variable across both recursive branches would corrupt the calculation for whichever branch runs second. Fix: always compute and pass a new local value (`int remaining = targetSum - root.val;`) into each recursive call, never mutate a shared field.
- Returning `root.left != null && root.right != null` instead of `root.left == null && root.right == null` for the leaf check (an easy small inversion/confusion) — this would incorrectly identify nodes with *both* children as "leaves" instead of nodes with *neither* child. Fix: double check the leaf condition carefully; a leaf is defined by the explicit *absence* of both children.

## 11. Interview Takeaway

- **Trigger sentence:** "Does any root-to-leaf path sum to a target → DFS with running target subtraction, checking equality only at true leaves (both children absent)."
- **90-second explanation:** "As I recurse down the tree, I subtract each node's value from the target sum I'm tracking, so by the time I reach any given node, the value I'm carrying represents exactly what the remaining path still needs to add up to. The key detail is that the problem specifically requires the path to end at a true leaf — a node with no children at all, not just any node where I happen to run out of one branch — so my success check only fires when I'm at a node with both children absent, and it simply checks whether that leaf's own value exactly equals whatever target remains. For any non-leaf node, I just recurse into whichever children exist, and the overall answer is true if either the left or right subtree manages to find a valid path with the reduced target. This avoids ever needing to store or re-sum an entire path's worth of values — the single running 'remaining target' integer threaded through the recursion carries all the information I need."
- **Related problems:** 113 (Path Sum II, return all valid paths rather than just a boolean), 437 (Path Sum III, paths don't need to start at the root or end at a leaf), 129 (Sum Root to Leaf Numbers, a related running-value-along-a-path technique).

## 12. Recall Questions

**Q:** Why must the success check happen specifically at a true leaf (`left == null && right == null`), rather than at any node where the running sum happens to reach zero or match the target?
**A:** The problem explicitly requires a complete root-to-**leaf** path, so a partial path that happens to sum correctly at some intermediate node (one that still has at least one child) doesn't satisfy the requirement — only a path that genuinely terminates at a leaf counts as valid.

**Q:** Why is subtracting the current node's value from the target (rather than adding to a running total and comparing against the original target only at the end) a convenient way to frame this problem?
**A:** It allows the leaf-level check to be a simple, local equality test (`leaf.val == remaining`) using only the single value being threaded through the recursion, without needing to separately remember and compare against the original, unmodified `targetSum` at every leaf.

**Q:** Why does passing a freshly computed `remaining` value into each recursive call (rather than mutating a single shared variable) matter for correctness?
**A:** The left and right subtrees each need their own independent view of "how much target remains after subtracting everything along this specific path so far," and since both subtrees share the same parent node's contribution but diverge afterward, a single mutated shared variable would incorrectly let one subtree's exploration affect the starting state of the other.

**Q:** Why does the `||` short-circuit behavior between the left and right recursive calls matter beyond just correctness?
**A:** Once a valid path is found in the left subtree, there's no need to also search the right subtree, since the overall answer is already determined to be `true`; short-circuiting avoids this unnecessary additional exploration, which can meaningfully reduce work in trees where a valid path is found early.

**Q:** What specifically distinguishes this problem's base/success case from a problem like Maximum Depth of Binary Tree, which simply checks `node == null`?
**A:** Maximum Depth needs to know when it has run out of tree to descend into, for which any `null` suffices as a base case; this problem specifically cares about where root-to-leaf *paths* end, which requires distinguishing a genuine leaf (no children at all) from a node that merely lacks one particular child but still has the other, since only true leaves can validly terminate a qualifying path.

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
    public boolean hasPathSum(TreeNode root, int targetSum) {
        if (root == null) {
            return false;
        }

        if (root.left == null && root.right == null) {
            return root.val == targetSum;
        }

        int remaining = targetSum - root.val;
        return hasPathSum(root.left, remaining) || hasPathSum(root.right, remaining);
    }
}
```

## 14. Self-Test

Given the root of a binary tree and a target sum, determine whether any root-to-leaf path's values add up exactly to the target. Re-derive: recursively descend, subtracting each node's value from the target as you go; at a true leaf (no children), check whether its own value equals whatever target remains; for any other node, recurse into whichever children exist with the reduced target, succeeding if either subtree finds a match.
