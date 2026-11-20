---
title: "0104 — Maximum Depth of Binary Tree"
layout: default
parent: Binary Tree General
grand_parent: Leetcode
nav_order: 104
permalink: /09-Binary-Tree-General/0104-maximum-depth-of-binary-tree/

problem: 104 - Maximum Depth of Binary Tree
url: https://leetcode.com/problems/maximum-depth-of-binary-tree/
difficulty: Easy
section: Tree / DFS-BFS
patterns: [tree-recursion, level-order-bfs]
data_structures: [tree, queue]
time: O(n)
space: O(n)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0104 — Maximum Depth of Binary Tree

| Field | Value |
|---|---|
| **Pattern** | Tree Recursion (DFS) or Level-Order BFS |
| **Difficulty** | Easy |
| **Section** | Tree / DFS-BFS |
| **Time** | O(n) |
| **Space** | O(n) — worst case O(n) for skewed tree; O(log n) balanced |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Meta |
| **LeetCode** | [Maximum Depth of Binary Tree](https://leetcode.com/problems/maximum-depth-of-binary-tree/) |

## 1. Problem in Simple Words

**Given:** the root of a binary tree.

**Required:** return its maximum depth — the number of nodes along the longest path from the root down to the farthest leaf.

**Constraints that matter:**
- `0 <= number of nodes <= 10^4` — the empty tree (`root == null`) is explicitly allowed and must return `0`.
- Up to `10^4` nodes means a **severely unbalanced (skewed) tree could have a depth of up to 10,000** — this is the key practical concern: a naive recursive solution's call stack depth scales with the tree's height, and a tree this deep risks a `StackOverflowError` in a language like Java with a limited default stack size, making an iterative alternative worth knowing even though both approaches share the same O(n) time complexity.
- No constraint distinguishes balanced from unbalanced trees — the solution must correctly handle anything from a perfectly balanced tree to a completely degenerate, linked-list-like skewed tree.

## 2. Recognition Signals

- "Maximum depth" / "longest path from root to a leaf" in a tree → the canonical, most direct application of **tree recursion**: a node's depth is naturally defined in terms of its children's depths.
- The recursive definition falls out almost immediately from the problem statement itself: the depth of an empty tree is `0`; the depth of any non-empty tree is `1` (for the current node) plus the larger of its left and right subtrees' depths — this recursive structure is the strongest possible signal for a **DFS-based recursive solution**.
- Tree problems framed around "traverse every node to compute some aggregate" are also solvable with **BFS (level-order traversal)**, counting the number of levels processed — useful specifically when recursion depth could become a practical concern.
- Named pattern: **Tree DFS Recursion** (with **BFS Level-Order Counting** as a stack-safe alternative).

## 3. Core Idea

A tree's maximum depth is fundamentally a recursive quantity: an empty subtree contributes a depth of `0`, and any non-empty subtree's depth is `1` (counting the node itself) plus whichever of its two children's subtrees is deeper. This recursive relationship can be computed either by **recursively descending** (DFS, naturally expressed as a direct translation of this definition) or by **processing the tree one full level at a time** (BFS, counting how many levels are processed before the queue empties) — both correctly compute the same quantity, just via different traversal strategies.

**Why it's correct:** the longest root-to-leaf path must pass through one of the root's two children's subtrees (or be just the root itself, if both children are absent) — there's no other way to reach a leaf except by descending through exactly one child at each step. So the maximum depth of the whole tree is necessarily `1 + max(depth of left subtree, depth of right subtree)`, and this relationship holds recursively all the way down to the base case of an empty subtree having depth `0`.

**Invariant (recursive):** for any node, the function correctly returns the maximum depth of the subtree rooted at that node, assuming (by induction) that the recursive calls on its children already correctly return the maximum depths of their respective subtrees.

**Invariant (iterative/BFS):** after processing `k` complete levels of the tree via the queue, the level counter correctly reflects the number of levels of nodes that have depth `<= k` from the root, down to and including the deepest level fully processed so far.

## 4. Approach 1 — Recursive DFS (Direct Translation of the Definition)

1. Base case: if `root == null`, return `0` (an empty subtree has depth `0`).
2. Otherwise, recursively compute the depth of the left subtree and the right subtree.
3. Return `1 + Math.max(leftDepth, rightDepth)`.

```java
public int maxDepthRecursive(TreeNode root) {
    if (root == null) {
        return 0;
    }

    int leftDepth = maxDepthRecursive(root.left);
    int rightDepth = maxDepthRecursive(root.right);

    return 1 + Math.max(leftDepth, rightDepth);
}
```

**Time:** O(n) — every node is visited exactly once.
**Space:** O(h) for the recursion call stack, where `h` is the tree's height — in the best case (a balanced tree), this is O(log n), but in the worst case (a completely skewed tree, essentially a linked list), this degrades to O(n).

This is simple and directly mirrors the problem's own recursive definition, but it carries a **practical robustness concern** given the constraints: with up to `10^4` nodes, a maximally skewed tree could have a recursion depth of 10,000, which risks exceeding Java's default call stack size and throwing a `StackOverflowError` — a real failure mode, not just a theoretical one, distinct from the time/space Big-O class itself.

## 5. Approach 2 — Optimized for Robustness (Iterative BFS, Level-Order Counting)

1. If `root == null`, return `0`.
2. Initialize a `Queue<TreeNode>` with `root` enqueued, and a `depth` counter at `0`.
3. While the queue is not empty:
   - Increment `depth` (about to process one full level).
   - Let `levelSize` be the current queue size (the number of nodes at this level).
   - Dequeue exactly `levelSize` nodes, and for each one, enqueue its non-null children (preparing the next level).
4. Return `depth` once the queue is empty.

```java
public int maxDepth(TreeNode root) {
    if (root == null) {
        return 0;
    }

    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);
    int depth = 0;

    while (!queue.isEmpty()) {
        depth++;
        int levelSize = queue.size();

        for (int i = 0; i < levelSize; i++) {
            TreeNode node = queue.poll();
            if (node.left != null) {
                queue.offer(node.left);
            }
            if (node.right != null) {
                queue.offer(node.right);
            }
        }
    }

    return depth;
}
```

**Time:** O(n) — every node is enqueued and dequeued exactly once, matching the recursive approach's time complexity.
**Space:** O(n) in the worst case for the queue (specifically O(w), the maximum width of the tree at any level, which for a tree with up to `10^4` nodes could itself be up to roughly `n/2` for a complete tree's last level) — however, this space lives on the **heap** (as queue contents), not the **call stack**, which is the key practical advantage: it entirely avoids any risk of a `StackOverflowError`, regardless of how skewed or deep the tree is.

This is the more robust choice given the constraints: both approaches share O(n) time, but the iterative BFS approach trades call-stack depth risk for heap-allocated queue space, which is generally far more forgiving in practice (heap space is typically much larger than the default thread stack size in Java) — making it the safer choice specifically for trees that could be deeply skewed, as this problem's constraints explicitly allow.

## 6. Dry Run

`root = [3,9,20,null,null,15,7]` (tree: `3` has children `9` and `20`; `20` has children `15` and `7`)

BFS level-order trace:

| iteration | depth (incremented) | levelSize | nodes processed this level | children enqueued |
|---|---|---|---|---|
| 1 | 1 | 1 | [3] | 9, 20 |
| 2 | 2 | 2 | [9, 20] | 15, 7 (9 has no children) |
| 3 | 3 | 2 | [15, 7] | (none, both are leaves) |

Exit: after iteration 3, the queue is empty (no further children were enqueued), so the `while` loop ends. Final answer: `depth = 3`, matching the expected output.

## 7. Complexity Summary

| Approach | Time | Space | Notes |
|---|---|---|---|
| Recursive DFS | O(n) | O(h) call stack (O(log n) balanced, O(n) worst case) | Simple, direct, but risks stack overflow on deeply skewed trees |
| Iterative BFS (Level-Order) | O(n) | O(w) queue (up to O(n) worst case) | Same time complexity; heap-based space avoids stack-overflow risk entirely |

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|---|---|---|
| Empty tree (`root == null`) | No nodes at all | Both approaches explicitly check for `null` and return `0` immediately |
| Single node (`root` with no children) | Minimal non-empty tree | DFS: both recursive calls hit the base case, returning `1 + max(0,0) = 1`; BFS: one level processed, `depth = 1` |
| Completely skewed tree (every node has only a left child, or only a right child, forming a linked-list shape) | Recursion depth could reach up to 10,000, the maximum allowed node count | DFS risks `StackOverflowError` in this exact scenario; BFS handles it correctly and safely, since each level in a skewed tree has exactly one node, simply requiring `n` iterations of the outer while loop with no stack growth |
| Perfectly balanced tree | Best case for recursion depth (O(log n)) | Both approaches handle this efficiently; DFS's stack depth stays shallow, and BFS's queue width grows but stays manageable |
| Tree where one subtree is much deeper than the other (asymmetric) | Must correctly take the *maximum*, not get confused by the shorter side | `Math.max(leftDepth, rightDepth)` in DFS, or the natural level-by-level progression in BFS, both correctly reflect the overall maximum regardless of asymmetry |
| Negative node values (`-100 <= Node.val <= 100`) | Node values are irrelevant to depth calculation | Neither approach ever inspects `node.val` — depth is purely a function of tree structure, not node contents |

## 9. Java Notes

- `Queue<TreeNode> queue = new LinkedList<>();` is the standard idiomatic choice for a BFS queue in Java — `LinkedList` implements the `Queue` interface and provides O(1) `offer`/`poll` operations; `ArrayDeque` is also a commonly preferred alternative for performance reasons, though `LinkedList` is perfectly adequate here.
- The `levelSize` snapshot (`queue.size()` captured *before* the inner for-loop begins) is essential for level-order processing — without capturing it first, the loop bound would be evaluated dynamically against a queue that's simultaneously growing (as children are enqueued) and shrinking (as nodes are dequeued), making it impossible to cleanly distinguish "this level" from "the next level."
- Java's default thread stack size (`-Xss`, often around 512 KB to 1 MB depending on JVM/platform defaults) can typically accommodate a few thousand to tens of thousands of simple stack frames, meaning a recursion depth of exactly 10,000 (this problem's worst case) is a genuine, non-hypothetical risk area, not a purely theoretical concern.
- `Math.max(leftDepth, rightDepth)` cleanly expresses "the more severe of the two subtree depths" in a single expression, avoiding an explicit `if`/`else` comparison.

## 10. Common Mistakes

- Returning `0` for a null check but forgetting the `+ 1` for the current node in the recursive case (e.g., returning just `Math.max(leftDepth, rightDepth)` without adding `1`) — this undercounts the depth by one at every level, since it fails to count the current node itself as contributing to the path length. Fix: always add `1` to account for the current node when combining the children's depths.
- In the BFS approach, forgetting to capture `levelSize` before the inner loop and instead using `queue.size()` directly as the loop's changing bound — this causes the inner loop to process more or fewer nodes than intended, since the queue's size changes continuously as the loop both removes current-level nodes and adds next-level nodes. Fix: always snapshot the level's size into a separate variable before the inner loop begins.
- Assuming recursive DFS is always the "best" solution without considering the problem's actual constraints — for a tree with up to 10,000 nodes that could be maximally skewed, recursion depth becomes a genuine practical risk, and dismissing this concern (or not being aware of it) is a notable gap in reasoning about this problem's edge cases, even though both approaches are equally correct in terms of Big-O time complexity.
- Confusing "depth" (counted in nodes, per this problem's definition) with "height" measured in edges (a common alternative convention in some textbooks, where a single node has height `0`, not depth `1`) — this problem explicitly defines maximum depth as "the number of **nodes** along the longest path," so a single-node tree has depth `1`, not `0`; using an edge-counting convention instead would produce answers consistently off by one.

## 11. Interview Takeaway

- **Trigger sentence:** "Maximum depth / longest root-to-leaf path in a tree → directly recursive (depth = 1 + max of children's depths), or level-order BFS counting levels for deep/skewed-tree robustness."
- **90-second explanation:** "This problem's definition is essentially already the recursive algorithm: an empty tree has depth zero, and any other tree's depth is one plus whichever of its two children's subtrees is deeper — so the straightforward recursive solution falls directly out of the problem statement. The one thing worth flagging is that with up to 10,000 nodes, a completely skewed tree could have a recursion depth of 10,000, which risks a stack overflow in a language with a bounded call stack like Java. So as a more robust alternative, I'd use level-order BFS instead: process the tree one full level at a time using a queue, incrementing a depth counter once per level, until the queue empties. Both approaches are O(n) time, but the BFS version keeps its extra space on the heap via the queue rather than on the call stack, which is generally far more forgiving for deep, unbalanced trees."
- **Related problems:** 111 (Minimum Depth of Binary Tree, same technique with a subtlety around null children), 102 (Binary Tree Level Order Traversal, the same BFS scaffolding used here), 110 (Balanced Binary Tree, builds on depth computation to check a balance property).

## 12. Recall Questions

**Q:** Why is the maximum depth of a tree defined as `1 + max(leftDepth, rightDepth)` rather than `1 + leftDepth + rightDepth` or some other combination?
**A:** The longest root-to-leaf path can only pass through *one* of the two children at any step (you can't be in both subtrees simultaneously along a single path), so the overall deepest path must follow whichever single subtree is deeper, not some combination of both.

**Q:** Why does a severely skewed tree pose a practical risk for the recursive DFS approach, specifically given this problem's constraints?
**A:** The recursion depth is directly proportional to the tree's height, and a tree with up to 10,000 nodes arranged in a completely skewed (linked-list-like) shape would have a height of 10,000, which could exceed the default call stack size in languages like Java, causing a runtime stack overflow rather than a graceful slowdown.

**Q:** Why is capturing `levelSize` before the inner for-loop essential in the BFS approach?
**A:** The queue is being modified during the inner loop — nodes are removed (for the current level) while their children are simultaneously added (for the next level) — so using the queue's live, changing size as the loop bound would incorrectly blend nodes from two different levels into a single iteration.

**Q:** Why do both the recursive DFS and iterative BFS approaches achieve the same O(n) time complexity despite using very different traversal strategies?
**A:** Both approaches are fundamentally bound by the requirement to visit every node in the tree exactly once to determine the overall maximum depth — recursion and level-order traversal are just two different orders of visiting the same n nodes, and neither revisits any node, so both are linear in the number of nodes.

**Q:** What's the key practical trade-off between the two approaches, given they share identical time complexity?
**A:** The recursive approach's extra space lives on the call stack, which has a comparatively small, fixed size and can be exhausted by a sufficiently deep (skewed) tree, whereas the iterative BFS approach's extra space lives in a heap-allocated queue, which can typically grow much larger before becoming a practical concern — making BFS the more robust choice specifically when the tree's shape (and therefore potential recursion depth) is unknown or could be adversarial.

## 13. Final Code

```java
import java.util.*;

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
    public int maxDepth(TreeNode root) {
        if (root == null) {
            return 0;
        }

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        int depth = 0;

        while (!queue.isEmpty()) {
            depth++;
            int levelSize = queue.size();

            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                if (node.left != null) {
                    queue.offer(node.left);
                }
                if (node.right != null) {
                    queue.offer(node.right);
                }
            }
        }

        return depth;
    }
}
```

## 14. Self-Test

Given the root of a binary tree, return its maximum depth (the number of nodes along the longest root-to-leaf path), being mindful that a skewed tree up to 10,000 nodes deep could risk recursion stack overflow. Re-derive: either recursively return `1 + max(depth of left subtree, depth of right subtree)` with `0` for a null subtree, or iteratively process the tree level by level with a queue, incrementing a depth counter once per fully-processed level until the queue empties.
