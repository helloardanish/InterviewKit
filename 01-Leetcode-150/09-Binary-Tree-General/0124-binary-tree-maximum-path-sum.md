---
title: "0124 — Binary Tree Maximum Path Sum"
layout: default
parent: Binary Tree General
grand_parent: Leetcode
nav_order: 124
permalink: /09-Binary-Tree-General/0124-binary-tree-maximum-path-sum/

problem: 124 - Binary Tree Maximum Path Sum
url: https://leetcode.com/problems/binary-tree-maximum-path-sum/
difficulty: Hard
section: Tree / DFS
patterns: [tree-recursion, max-downward-gain, global-max-tracking]
data_structures: [tree]
time: O(n)
space: O(h)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0124 — Binary Tree Maximum Path Sum

| Field | Value |
|---|---|
| **Pattern** | Tree Recursion with Max Downward Gain + Global Max Tracking |
| **Difficulty** | Hard |
| **Section** | Tree / DFS |
| **Time** | O(n) |
| **Space** | O(h) — h = tree height |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Meta |
| **LeetCode** | [Binary Tree Maximum Path Sum](https://leetcode.com/problems/binary-tree-maximum-path-sum/) |

## 1. Problem in Simple Words

**Given:** the root of a binary tree.

**Required:** find the maximum possible sum of node values along any path — where a "path" is any sequence of nodes connected by edges, each node used at most once, and critically, **the path does not need to pass through the root, and can start and end anywhere**.

**Constraints that matter:**
- `1 <= number of nodes <= 3 * 10^4` — large enough that an approach repeating full subtree scans at every node would be too slow; O(n) is the expected target.
- `-1000 <= Node.val <= 1000` — **negative values are explicitly allowed**, which is the single most important constraint: it means a path shouldn't always greedily include every node reachable from a given point, since a negative-sum branch could actually *decrease* a path's total rather than help it, so the algorithm must be able to recognize when to "cut off" a branch rather than include it.
- "**A node can only appear in the sequence at most once**" and "**the path does not need to pass through the root**" — together these define a path as any simple (non-repeating) sequence of connected nodes anywhere in the tree, including, critically, paths that go *up and over* a node (entering from one child, passing through the node, and exiting through the other child) — not just straight-line descents.

## 2. Recognition Signals

- "Maximum sum path, not necessarily through the root, node used at most once" → in a tree (no cycles), **any simple path has exactly one "highest" node** (the point where it potentially bends from going up on one side to going down on the other) — this structural fact is the key insight that turns an apparently open-ended path-search problem into a per-node, bounded computation.
- The presence of **negative values** is a strong signal that a plain "always take both children" greedy approach is wrong — instead, each subtree's contribution to a path passing *through* it from above should only be included if it's *positive*; a negative contribution should be discarded (treated as zero) rather than dragging the sum down.
- The problem secretly asks for **two distinct things at each node**, which must be computed together: (1) the best path sum achievable *through* this node as the path's peak (which may use both children), used to update a running global answer, and (2) the best *single-branch* sum extendable *upward* to this node's parent (which can use at most one child, since a path continuing upward can't branch) — this two-purpose recursive return value is the hallmark of this exact problem pattern.
- Named pattern: **Tree DFS with Max Downward Gain, Tracking a Global Maximum Separately from the Recursive Return Value**.

## 3. Core Idea

For each node, compute the maximum sum obtainable by a path that starts at this node and goes downward into *at most one* of its children (the "max downward gain" — this is what the node can offer to a path continuing through its parent, since a path can't branch once it's already committed to going through a parent-child edge upward). Separately, at each node, also consider the best path that uses this node as its **peak** — meaning it can incorporate contributions from *both* children simultaneously (since the node itself is the one place a path is allowed to "turn") — and use this to update a single running global maximum across the whole tree. Critically, any child's contribution (whether used for the downward-gain return value or the peak calculation) is clamped to a minimum of `0`, since a negative contribution should simply be excluded from the path rather than subtracted into it.

**Why it's correct:** in a tree, any simple path has a unique topmost node — the single point at which the path either starts going strictly downward, or "turns" from ascending through one child's subtree into descending through the other's. By computing, for every node, the best possible "peak-centered" path sum (node's own value, plus optionally the best non-negative contribution from each child independently) and tracking the overall best such value across all nodes, every possible path in the tree is considered exactly once, at the one node where it peaks. The separate "max downward gain" value returned from each recursive call is what a parent needs to correctly evaluate its *own* peak-centered sum, since a path extending through the parent via one specific child can only use that child's best single-branch extension, not both of that child's children simultaneously.

**Invariant:** when the recursive call for a given node returns, the value returned correctly represents the maximum sum of a path starting at that node and extending into at most one of its subtrees (clamped at a minimum of `0`), and as a side effect, the global maximum variable has already been correctly updated to reflect the best "peak at this node" path sum, considering contributions from both children independently.

## 4. Approach 1 — Brute Force (Separate Max-Downward-Sum Computation at Every Node)

1. For every node in the tree (visited via an outer traversal), treat it as a potential path "peak."
2. For each such candidate peak, run a **separate, fresh** recursive computation to find the maximum downward-sum path achievable starting from its left child, and another separate computation for its right child.
3. Combine: candidate peak sum = `node.val + max(0, leftMaxDownward) + max(0, rightMaxDownward)`. Track the overall maximum across all candidate peaks.

```java
private int globalMax = Integer.MIN_VALUE;

public int maxPathSumBruteForce(TreeNode root) {
    traverseAllNodes(root);
    return globalMax;
}

private void traverseAllNodes(TreeNode node) {
    if (node == null) {
        return;
    }

    int leftMax = Math.max(0, maxDownwardSum(node.left));
    int rightMax = Math.max(0, maxDownwardSum(node.right));
    globalMax = Math.max(globalMax, node.val + leftMax + rightMax);

    traverseAllNodes(node.left);
    traverseAllNodes(node.right);
}

private int maxDownwardSum(TreeNode node) {
    if (node == null) {
        return 0;
    }
    int leftMax = Math.max(0, maxDownwardSum(node.left));
    int rightMax = Math.max(0, maxDownwardSum(node.right));
    return node.val + Math.max(leftMax, rightMax);
}
```

**Time:** O(n²) in the worst case — for each of the n nodes visited by the outer traversal, a fresh `maxDownwardSum` call re-explores the entire subtree beneath it, redoing work that overlaps heavily with computations already performed for nearby nodes.
**Space:** O(h) for each individual recursive call's stack depth, but the *total* redundant work across all the repeated subtree explorations is what makes this approach inefficient in time, not space.

This is not optimal: it recomputes the max-downward-sum for every subtree repeatedly — once as part of each of its ancestors' separate top-level evaluation — resulting in massive redundant recursion. With `n` up to `3 * 10^4`, this O(n²) behavior would very likely time out.

## 5. Approach 2 — Optimized (Single Post-Order Pass, Combined Gain + Global Max Update)

1. Maintain a single instance/field variable `globalMax`, initialized to `Integer.MIN_VALUE` (since all node values could be negative, the true maximum might itself be negative, so `0` is not a safe initial value).
2. Define a recursive helper `maxGain(node)`:
   - Base case: if `node == null`, return `0` (an absent subtree contributes nothing, and critically, never *subtracts* anything either).
   - Recursively compute `leftGain = Math.max(0, maxGain(node.left))` and `rightGain = Math.max(0, maxGain(node.right))` — clamping each to a minimum of `0`, discarding any subtree whose best contribution would be negative.
   - Update `globalMax = Math.max(globalMax, node.val + leftGain + rightGain)` — this evaluates the best path with this node as its peak, using both children.
   - Return `node.val + Math.max(leftGain, rightGain)` — the best single-branch extension usable by this node's *parent*, which can only continue through one child, not both.
3. Call `maxGain(root)` once to populate `globalMax` across the whole tree, then return `globalMax`.

```java
class Solution {
    private int globalMax = Integer.MIN_VALUE;

    public int maxPathSum(TreeNode root) {
        maxGain(root);
        return globalMax;
    }

    private int maxGain(TreeNode node) {
        if (node == null) {
            return 0;
        }

        int leftGain = Math.max(0, maxGain(node.left));
        int rightGain = Math.max(0, maxGain(node.right));

        globalMax = Math.max(globalMax, node.val + leftGain + rightGain);

        return node.val + Math.max(leftGain, rightGain);
    }
}
```

**Time:** O(n) — each node is visited exactly once, with O(1) work (two comparisons, an addition, an update) performed at each, beyond the two recursive calls.
**Space:** O(h) for the recursion call stack, where `h` is the tree's height — O(log n) for a balanced tree, up to O(n) in the worst case for a completely skewed tree.

This is optimal: it computes both the "best peak-centered path through this node" and "best single-branch extension for the parent" in the **same single recursive pass**, eliminating all the redundant re-exploration that made the brute force O(n²) — every subtree is explored exactly once across the entire algorithm's execution.

## 6. Dry Run

`root = [-10,9,20,null,null,15,7]` (tree: `-10` has children `9,20`; `20` has children `15,7`)

Post-order evaluation (children before parents):

| node | leftGain | rightGain | globalMax updated to | returns (node.val + max(leftGain,rightGain)) |
|---|---|---|---|---|
| 9 (leaf) | 0 (no children) | 0 | max(MIN, 9+0+0)=9 | 9+0=9 |
| 15 (leaf) | 0 | 0 | max(9, 15+0+0)=15 | 15+0=15 |
| 7 (leaf) | 0 | 0 | max(15, 7+0+0)=15 (7 < 15, no change) | 7+0=7 |
| 20 | max(0,15)=15 | max(0,7)=7 | max(15, 20+15+7)=42 | 20+max(15,7)=20+15=35 |
| -10 (root) | max(0,9)=9 | max(0,35)=35 | max(42, -10+9+35)=42 (34 < 42, no change) | -10+max(9,35)=-10+35=25 |

Exit: after processing the root, `globalMax = 42`. Final answer: `42`, matching the expected output (the path `15 -> 20 -> 7`).

Note how at node `-10`, even though its own peak-sum candidate (`34`) doesn't beat the existing `42`, the already-correct `globalMax` from node `20`'s evaluation is preserved — the global maximum is a running value across the *entire* tree, not reset or recomputed per node.

## 7. Complexity Summary

| Approach | Time | Space | Notes |
|---|---|---|---|
| Separate Max-Downward-Sum per Candidate Peak | O(n²) worst case | O(h) per call (but massive redundant work overall) | Re-explores overlapping subtrees repeatedly |
| Single Post-Order Pass, Combined Gain + Global Update | O(n) | O(h) | Every subtree explored exactly once |

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|---|---|---|
| Single-node tree (`root = [5]`) | The only possible path is the single node itself | `maxGain(5)`: `leftGain=rightGain=0` (null children); `globalMax = max(MIN, 5+0+0) = 5`; returns `5+0=5`. Final answer: `5` |
| All-negative tree (every node's value is negative) | The best path is still required to be **non-empty**, so some single node (the least negative) must be chosen, not an empty path summing to 0 | Clamping child gains to a minimum of `0` means a node's own value is always at least considered as a path on its own (`node.val + 0 + 0`); `globalMax`'s initial value of `Integer.MIN_VALUE` ensures even a very negative single-node "best path" is correctly captured rather than incorrectly defaulting to some non-negative placeholder |
| A node whose best path requires using **both** children (bending at that node) | The single `maxGain` return value alone (usable by a parent) can't reflect this, since a parent can only extend through one branch | This is precisely why `globalMax` is updated *separately*, using `node.val + leftGain + rightGain` (both children), distinct from what's actually `return`ed (`node.val + max(leftGain, rightGain)`, only one branch) — the two serve different purposes and must be computed differently |
| A subtree whose best contribution is negative | Including it in a parent's path would only hurt the sum | `Math.max(0, maxGain(child))` clamps any negative contribution to `0`, effectively excluding that subtree from being incorporated into any path passing through the parent |
| Deepest allowed tree size (`n` up to `3*10^4`), potentially skewed | Recursion depth could be large in a pathological case | The algorithm's O(h) space scales with height; for this problem's constraints, a worst-case skewed tree's recursion depth is a shared concern with any other tree-recursion approach, not something unique to this particular technique |
| Root itself being the sole correct peak (path does happen to pass through the root, as in Example 1) | Must not assume the answer is always found somewhere other than the root | The algorithm doesn't treat the root specially at all — `globalMax` is updated identically at every node, including the root, so if the root genuinely is the best peak, that's correctly captured in the same unified process |

## 9. Java Notes

- `globalMax` is implemented as an **instance field** (rather than, say, an array-wrapped int or a return-tuple) specifically because Java methods can only return a single value, and this algorithm fundamentally needs to track two different pieces of information simultaneously: the value to actually `return` (for the parent's use) and the running best-answer-so-far (updated as a side effect) — using a field is the simplest idiomatic way to achieve this "return one thing, but also update external state" pattern in Java.
- `Integer.MIN_VALUE` is the correct initial value for `globalMax`, not `0` — since all node values could be negative (down to `-1000`), initializing to `0` would incorrectly suggest an "empty path" of sum zero is always achievable, violating the problem's explicit requirement that the path be **non-empty**.
- `Math.max(0, maxGain(node.left))` performs two logically distinct jobs in one line: it computes the recursive result, and it simultaneously clamps that result to a non-negative floor — this combined clamping-and-computing pattern is common in "maximum subarray/subtree sum" style problems generally (compare to Kadane's algorithm for maximum subarray sum, which uses a conceptually similar "discard negative prefix" idea).
- The distinction between what's **returned** (`node.val + Math.max(leftGain, rightGain)`, single-branch only) and what updates **`globalMax`** (`node.val + leftGain + rightGain`, both branches) is the single most important detail to get right in this solution — conflating the two (e.g., accidentally returning the both-branches sum) would corrupt every ancestor's calculation, since a path can't legally branch except at its own unique peak.

## 10. Common Mistakes

- Returning `node.val + leftGain + rightGain` (using both children) from the recursive helper, instead of `node.val + Math.max(leftGain, rightGain)` (only one child) — this would represent an illegal "path" to a parent, since a path extending upward through this node can only have come from (and continue into) one single branch, not have already used both; a path physically cannot branch at more than one point. Fix: always return only the single best branch's contribution, reserving the two-branch combination exclusively for the separate `globalMax` update.
- Forgetting to clamp child contributions with `Math.max(0, ...)` and instead using `maxGain(node.left)` directly, unclamped — this would allow a negative-sum subtree to be subtracted into a path's total, when the correct behavior is to simply exclude that subtree from the path entirely (contributing nothing, i.e., `0`, rather than a negative amount). Fix: always clamp every child's gain to a minimum of `0` before using it in either the `globalMax` update or the returned value.
- Initializing `globalMax` to `0` instead of `Integer.MIN_VALUE` — this silently produces an incorrect answer of `0` for a tree where every possible path sum is actually negative, since `0` would never be surpassed by any genuinely negative candidate, even though the problem requires a valid (negative) answer in that case. Fix: always initialize the running maximum to the smallest possible value (`Integer.MIN_VALUE`), not an assumed-safe default like `0`.
- Updating `globalMax` using the *returned* value (the single-branch sum) instead of computing a separate `node.val + leftGain + rightGain` expression specifically for the update — this would systematically miss every path that actually needs to bend through a node using both children, since the returned value by design only ever reflects a single branch. Fix: always compute and use the two-branch sum specifically and only for the `globalMax` update, kept distinct from the single-branch `return` value.

## 11. Interview Takeaway

- **Trigger sentence:** "Maximum sum path anywhere in a tree, not necessarily through the root, negative values allowed → post-order DFS computing each node's max single-branch downward gain (clamped at 0) while separately tracking a global max using both-branch sums as each node's potential 'peak.'"
- **90-second explanation:** "Since this is a tree, any simple path has exactly one topmost point — the node where it either starts heading down, or bends from ascending on one side to descending on the other. So for every node, I compute two different things. First, I figure out the best this node can offer upward to its own parent: that's the node's own value plus the better of its two children's best downward contributions — only one child, since a path can't branch except at its true peak. That's what I return. Second, completely separately, I consider this node as a potential peak in its own right, which means it's allowed to use both children simultaneously, bending through itself — so I compute node value plus both children's contributions, and I compare that against a running global best, updating it if this is better. Negative values are the reason I clamp every child's contribution at zero before using it anywhere — a negative-sum subtree should just be excluded from a path, not dragged in and subtracted. Both of these computations happen together in a single post-order pass, so every node and every subtree is only ever explored once."
- **Related problems:** 543 (Diameter of Binary Tree, a very similar max-downward-gain pattern without the negative-value clamping concern), 687 (Longest Univalue Path, same bend-at-a-peak structure), 53 (Maximum Subarray, the 1D analog using Kadane's algorithm with a similar "discard negative prefix" idea).

## 12. Recall Questions

**Q:** Why must the value returned from the recursive helper use only the *better* of the two children's gains, while the value used to update `globalMax` uses the *sum* of both?
**A:** A path extending upward through this node to its parent can only have come from one single branch, since a path can't legally branch at more than one point — but this node itself is allowed to be the *one* place a path bends, incorporating both children simultaneously, which is exactly the "peak" scenario that `globalMax` is tracking.

**Q:** Why is every child's gain clamped to a minimum of zero before being used in any calculation?
**A:** Node values can be negative, and a subtree whose best achievable sum is negative should simply be excluded from a path passing through its parent rather than subtracted into it — clamping to zero is what correctly models "don't take this branch" rather than "take this branch and let it hurt the total."

**Q:** Why must `globalMax` be initialized to `Integer.MIN_VALUE` rather than `0`?
**A:** The problem requires a non-empty path, and if every node in the tree happens to be negative, the correct answer is itself negative (the single least-negative node) — initializing to `0` would incorrectly suggest that an empty, zero-sum path is always a valid fallback, which the problem explicitly disallows.

**Q:** Why does this problem require tracking a separate global variable instead of just using the recursive function's return value directly as the final answer?
**A:** The final answer needs to consider every possible "peak" node in the tree, each potentially using both of its children, but the recursive call chain only ever has visibility into a single root-to-current-node return path at any given moment — a running global variable is what allows the best peak found *anywhere* in the tree, not just along whatever path the final top-level call happens to return, to be correctly remembered and reported.

**Q:** How does this problem's "clamp negative contributions to zero, but let positive contributions compound" logic conceptually relate to Kadane's algorithm for the maximum subarray sum problem?
**A:** Both techniques are built around the same core idea: when extending a sum-based structure (a subarray in Kadane's case, a tree path here) with a new component, a negative-sum prefix or branch should be discarded entirely (treated as contributing a clean break, effectively a fresh start or a zero contribution) rather than allowed to drag down everything that follows it.

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
    private int globalMax = Integer.MIN_VALUE;

    public int maxPathSum(TreeNode root) {
        maxGain(root);
        return globalMax;
    }

    private int maxGain(TreeNode node) {
        if (node == null) {
            return 0;
        }

        int leftGain = Math.max(0, maxGain(node.left));
        int rightGain = Math.max(0, maxGain(node.right));

        globalMax = Math.max(globalMax, node.val + leftGain + rightGain);

        return node.val + Math.max(leftGain, rightGain);
    }
}
```

## 14. Self-Test

Given the root of a binary tree (values possibly negative), find the maximum sum of any non-empty path, where a path is any simple sequence of connected nodes that need not pass through the root. Re-derive: in a single post-order DFS, for each node compute its children's max downward gains clamped at zero, update a running global maximum using `node.val + leftGain + rightGain` (the node as a potential bending peak), and return `node.val + max(leftGain, rightGain)` (the single-branch extension usable by the parent).
