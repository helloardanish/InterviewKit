---
title: "0114 — Flatten Binary Tree to Linked List"
layout: default
parent: Binary Tree General
grand_parent: Leetcode
nav_order: 114
permalink: /09-Binary-Tree-General/0114-flatten-binary-tree-to-linked-list/

problem: 114 - Flatten Binary Tree to Linked List
url: https://leetcode.com/problems/flatten-binary-tree-to-linked-list/
difficulty: Medium
section: Tree / DFS
patterns: [preorder-traversal, morris-style-threading]
data_structures: [tree]
time: O(n)
space: O(1)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0114 — Flatten Binary Tree to Linked List

| Field | Value |
|---|---|
| **Pattern** | Preorder Traversal with Morris-Style Threading |
| **Difficulty** | Medium |
| **Section** | Tree / DFS |
| **Time** | O(n) |
| **Space** | O(1) — in-place with Morris-style threading; O(h) with recursion |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Meta |
| **LeetCode** | [Flatten Binary Tree to Linked List](https://leetcode.com/problems/flatten-binary-tree-to-linked-list/) |

## 1. Problem in Simple Words

**Given:** the root of a binary tree.

**Required:** rearrange the tree in place into a "linked list" shape: every node's `left` pointer becomes `null`, and every node's `right` pointer points to the next node — where "next" follows the order of a **pre-order traversal** (visit the node itself, then its left subtree, then its right subtree).

**Constraints that matter:**
- `0 <= number of nodes <= 2000` — the empty tree must simply produce an empty result (no error).
- Reuses the **same `TreeNode` class** for the output — this isn't about constructing a separate linked-list data structure; it's about repurposing each tree node's existing `left`/`right` pointers so that `left` is always `null` and `right` threads through to the next node in pre-order sequence.
- **Follow-up: "Can you flatten the tree in-place (with O(1) extra space)?"** — this directly invites comparing a straightforward "traverse and collect, then relink" approach (which needs O(n) auxiliary storage for the traversal order) against a cleverer technique that performs the rearrangement without needing to remember the full traversal order up front.

## 2. Recognition Signals

- "Rearrange a tree into a linked list matching pre-order" → the most direct approach is simply to **perform a pre-order traversal and relink nodes in that order** — the traversal order *is* the final list order, so the core logic is really about how to achieve this relinking efficiently.
- The explicit O(1)-space follow-up → signals that there's a technique for threading the nodes together **without first storing the whole traversal sequence in an array or list** — this is the same spirit as Morris traversal, which achieves O(1)-space in-order traversal by temporarily using tree pointers themselves to "remember" where to return to, instead of a call stack or explicit list.
- The key structural insight for the O(1) technique: for a node with **both** a left and right child, the pre-order-correct way to splice them together is to find the **rightmost node of the left subtree** (which, in pre-order, is the very last node visited before the right subtree would begin) and attach the original right subtree there — this "find the predecessor and reattach" pattern is a hallmark of Morris-style traversal techniques.
- Named pattern: **Morris-Style In-Place Threading** (adapted here for pre-order flattening, rather than in-order traversal as in the classic Morris technique).

## 3. Core Idea

For any node that has a left child, the correct pre-order-flattened structure requires that the entire left subtree (fully flattened) comes immediately after the node, and the entire right subtree (fully flattened) comes after *that*. So: find the rightmost node within the current node's left subtree (by repeatedly following `.right` pointers) — this node is where the original right subtree needs to be reattached, since it's the last node the left subtree will visit in pre-order. Attach the original right subtree there, then promote the left subtree to take over as the new right subtree, and clear the left pointer. Move to what is now the current node's right child and repeat.

**Why it's correct:** pre-order traversal visits a node, then its entire left subtree, then its entire right subtree — so in the final flattened list, right after a node's position comes its *entire* (also-flattened) left subtree, and only after that does its original right subtree appear. The rightmost node of the left subtree is precisely the last node that subtree's own pre-order traversal will visit, so reattaching the original right subtree exactly there — before promoting the left subtree into the main `right`-pointer chain — correctly splices the two subtrees together in the right order, without ever needing to have pre-computed or stored the full traversal sequence elsewhere.

**Invariant:** at any point during the single pass, everything "to the left of and including" the current node's position in the traversal has already been correctly linked into its final pre-order position via `right` pointers (with `left` already nulled out), and the rest of the original tree structure (not yet processed) remains correctly reachable through the current node's `right` pointer, which may temporarily still represent "what used to be the left subtree, now promoted" rather than the node's true original right subtree.

## 4. Approach 1 — Brute Force (Collect via Pre-order Traversal, Then Relink)

1. Perform a standard pre-order traversal (recursive or using an explicit stack), collecting every node reference into a `List<TreeNode>`, in pre-order sequence.
2. Walk the resulting list, and for each consecutive pair of nodes, set the earlier node's `right` to the later node, and its `left` to `null`. Set the final node's `right` to `null` as well.

```java
public void flattenBruteForce(TreeNode root) {
    if (root == null) {
        return;
    }

    List<TreeNode> preorderList = new ArrayList<>();
    collectPreorder(root, preorderList);

    for (int i = 0; i < preorderList.size() - 1; i++) {
        TreeNode current = preorderList.get(i);
        current.left = null;
        current.right = preorderList.get(i + 1);
    }

    TreeNode last = preorderList.get(preorderList.size() - 1);
    last.left = null;
    last.right = null;
}

private void collectPreorder(TreeNode node, List<TreeNode> list) {
    if (node == null) {
        return;
    }
    list.add(node);
    collectPreorder(node.left, list);
    collectPreorder(node.right, list);
}
```

**Time:** O(n) — the traversal visits every node once to build the list, and the relinking pass visits every node once more.
**Space:** O(n) auxiliary for the `List<TreeNode>` holding every node reference, plus O(h) recursion stack space for the traversal itself.

This is not optimal with respect to the problem's own explicit follow-up: it correctly achieves O(n) time, but uses O(n) auxiliary space to store the traversal order before relinking, whereas the follow-up specifically asks for an O(1)-space solution — a technique that can perform the relinking without ever materializing the full traversal sequence as a separate structure.

## 5. Approach 2 — Optimized (Morris-Style In-Place Threading, True O(1) Extra Space)

1. Start with `current = root`.
2. While `current != null`:
   - If `current.left != null`: find the rightmost node of `current`'s left subtree (call it `predecessor`) by repeatedly following `.right` pointers starting from `current.left`, until reaching a node whose `.right` is `null`.
   - Set `predecessor.right = current.right` (reattach the original right subtree at the end of the left subtree's chain).
   - Set `current.right = current.left` (promote the left subtree to become the new right subtree).
   - Set `current.left = null` (per the problem's required output format).
   - If `current.left == null` (either originally, or now, after the above reassignment already happened — so this check is really "if there was no left subtree to process"), simply advance `current = current.right`.
3. The loop naturally terminates once `current` reaches the end of the fully-threaded chain.

```java
public void flatten(TreeNode root) {
    TreeNode current = root;

    while (current != null) {
        if (current.left != null) {
            TreeNode predecessor = current.left;
            while (predecessor.right != null) {
                predecessor = predecessor.right;
            }
            predecessor.right = current.right;
            current.right = current.left;
            current.left = null;
        }
        current = current.right;
    }
}
```

**Time:** O(n) — although there's a nested `while` loop (the inner one searching for the rightmost node of a left subtree), each edge in the original tree is traversed at most a constant number of times across the entire algorithm's execution: the "find the rightmost node" search for any given left subtree only ever happens once, at the moment that subtree is first promoted, and subsequent iterations of the outer loop walk through already-threaded `right` pointers without re-searching.
**Space:** O(1) auxiliary — only the `current` and `predecessor` pointers are used, with no recursion, no stack, and no auxiliary list, regardless of the tree's size or shape.

This is optimal: it directly answers the problem's explicit follow-up, achieving the same O(n) time as the brute force while reducing auxiliary space from O(n) (plus O(h) recursion) down to true O(1) — the Morris-style "find and reattach" technique is what allows the traversal order to be achieved without first recording it anywhere.

## 6. Dry Run

`root = [1,2,5,3,4,null,6]` (tree: `1` has children `2,5`; `2` has children `3,4`; `5` has children `null,6`)

| current | current.left? | predecessor search | reattach / promote | current.left after | current after (current.right) |
|---|---|---|---|---|---|
| 1 | yes (2) | from 2: 2→4 (2.right=4), 4.right=null → predecessor=4 | predecessor(4).right = current.right(5); current.right = current.left(2); current.left=null | null | 2 |
| 2 | yes (3) | from 3: 3.right=null → predecessor=3 | predecessor(3).right = current.right(4); current.right = current.left(3); current.left=null | null | 3 |
| 3 | no | — | — | null (already) | 4 (3.right, just set) |
| 4 | no | — | — | null | 5 (4.right, which was set to original 5 earlier) |
| 5 | no (5's original left was null) | — | — | null | 6 (5's original right) |
| 6 | no | — | — | null | null (6.right was originally null) |

Exit: `current` becomes `null`, loop ends. Final right-pointer chain: `1 -> 2 -> 3 -> 4 -> 5 -> 6`, with every `left` pointer `null`. Final answer: `[1,null,2,null,3,null,4,null,5,null,6]`, matching the expected output.

## 7. Complexity Summary

| Approach | Time | Space | Notes |
|---|---|---|---|
| Collect via Pre-order, Then Relink | O(n) | O(n) aux (list) + O(h) recursion | Correct, but fails the O(1)-space follow-up |
| Morris-Style In-Place Threading | O(n) | O(1) aux | True constant extra space, directly satisfies the follow-up |

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|---|---|---|
| Empty tree (`root == null`) | Nothing to flatten | The `while (current != null)` loop's condition is immediately false, since `current = root = null`; the function returns without any modification needed |
| Single node (`root = [0]`) | Trivial case, already "flattened" | `current.left` is `null`, so the `if` body never executes; `current = current.right` (which is also `null`) ends the loop immediately, leaving the single node unchanged, which is already correctly formatted |
| A node with only a right child (no left child at all) | The promotion/reattachment logic should be skipped entirely for this node | The `if (current.left != null)` check correctly skips nodes with no left child, simply advancing `current` to the existing right child without any relinking needed |
| A node with only a left child (no right child) | `current.right` is `null` before reattachment, so `predecessor.right = current.right` sets it to `null` | This works correctly — the predecessor's `right` is simply set to `null` as the "original right subtree" to reattach, which is `null` since there wasn't one, and the left subtree is still correctly promoted into the main chain |
| A deeply left-skewed subtree requiring a long predecessor search (e.g., a long chain of left children) | The inner `while` loop searching for the rightmost node could, in a single call, scan many nodes | This is accounted for in the overall O(n) analysis: although any single predecessor search could scan up to O(n) nodes in a pathological case, each such search only happens once per subtree-promotion, and the total work across all searches combined is bounded by the total number of edges in the tree, preserving the overall O(n) bound |
| A node whose left subtree, once promoted, needs its own further flattening deeper in | Must correctly continue processing into the newly-promoted structure, not just the original right subtree | Since `current = current.right` always advances into whatever is now in the `right` pointer (whether originally there or just promoted), the outer loop naturally continues processing into the promoted left subtree, correctly handling any further left children it may have |

## 9. Java Notes

- This technique directly parallels the classic **Morris Traversal** algorithm for O(1)-space in-order tree traversal — both work by temporarily using the tree's own existing pointers to create a "thread" back to where processing needs to resume, rather than relying on an explicit stack or recursion; here, the thread is created by reattaching the original right subtree onto the left subtree's rightmost node.
- No recursion is used at all in the optimized solution, which — beyond satisfying the O(1)-space requirement — also means there's no risk of stack-overflow on deeply skewed trees, unlike the brute-force approach's recursive traversal helper (though with `n <= 2000`, this specific problem's constraints make that a lesser practical concern than it was for, e.g., Maximum Depth of Binary Tree's `n <= 10^4`).
- The inner `while (predecessor.right != null)` loop is a direct linear search for "the rightmost node reachable by following only right pointers from here" — this is a common sub-routine across several tree algorithms (including standard Morris in-order traversal itself) and is worth recognizing as a reusable building block.
- Setting `current.left = null` must happen as a distinct, explicit step — it's easy to assume that reassigning `current.right = current.left` somehow also clears `current.left` automatically, but in Java (as in most languages), assignment to one field has no automatic effect on another field; both reassignments are independently required.

## 10. Common Mistakes

- Reattaching `current.right` to the promoted left subtree **before** finding the predecessor and saving the original right subtree reference — this overwrites `current.right` before its original value has been correctly relocated to the predecessor, permanently losing the reference to the original right subtree. Fix: always find the predecessor and perform `predecessor.right = current.right` *before* overwriting `current.right` with the promoted left subtree.
- Forgetting to set `current.left = null` after promoting the left subtree into the right-pointer chain — the problem explicitly requires every node's `left` to be `null` in the final result, and without this explicit clearing, left pointers would still reflect the original tree structure, failing to match the required output format even if the `right`-pointer chain itself were otherwise correctly threaded.
- Advancing `current` to `current.left` or using some other incorrect pointer instead of `current.right` at the end of each outer loop iteration — since the whole point of the algorithm is to build up the correct structure via `right` pointers (with `left` being nulled out), continuing the traversal must always follow `current.right`, which (after the current iteration's processing) correctly points to either the promoted left subtree or the original right subtree, whichever applies. Fix: always advance via `current = current.right`.
- Searching for the predecessor starting from `current` itself instead of `current.left` — this would incorrectly include `current` in the rightward search, potentially finding the wrong node or (if `current.right` were already set to something during a prior step) producing an infinite loop by following a pointer that leads back toward already-processed structure. Fix: always begin the predecessor search from `current.left`, not from `current` itself.

## 11. Interview Takeaway

- **Trigger sentence:** "Flatten a binary tree into a pre-order-ordered linked list using the existing left/right pointers, O(1) extra space → Morris-style threading: find the left subtree's rightmost node and reattach the original right subtree there, before promoting the left subtree into the right-pointer chain."
- **90-second explanation:** "Pre-order means visit the node, then its whole left subtree, then its whole right subtree — so in the final flattened structure, a node's left subtree (once itself flattened) needs to come immediately after it, with the original right subtree tacked on at the very end of that. The key trick is: instead of first flattening the left subtree recursively and then explicitly finding where to attach the right subtree afterward, I can find the rightmost node of the left subtree directly — by just following right pointers until I hit the end — since that's guaranteed to be the last node the left subtree will ever visit in pre-order. I reattach the original right subtree there, then promote the entire left subtree to become the new right subtree, and null out the left pointer as the problem requires. Then I just move forward along the right-pointer chain and repeat. Every subtree only ever gets its 'rightmost node' searched for once, at the moment it's promoted, so even though there's a loop inside a loop, the total work stays linear across the whole tree, and I never need any extra storage beyond a couple of pointer variables."
- **Related problems:** 94 (Binary Tree Inorder Traversal, the classic Morris Traversal application), 173 (Binary Search Tree Iterator, related O(1)-ish amortized traversal control), 426 (Convert Binary Search Tree to Sorted Doubly Linked List, a related in-place restructuring problem).

## 12. Recall Questions

**Q:** Why is the rightmost node of a node's left subtree exactly the correct place to reattach that node's original right subtree?
**A:** Pre-order traversal visits an entire left subtree before moving on to the right subtree, so the rightmost node of the left subtree (in this threading sense) is precisely the last node that subtree's own pre-order sequence will produce, making it the exact point where the original right subtree needs to continue from.

**Q:** Why must the original right subtree be saved (via reattachment to the predecessor) before `current.right` is overwritten with the promoted left subtree?
**A:** `current.right` is the only reference to the original right subtree at that point in the algorithm; overwriting it before relocating that reference elsewhere (to the predecessor's `right` pointer) would permanently lose access to that entire subtree.

**Q:** Why doesn't the nested while loop (searching for the rightmost node) cause the overall algorithm to become O(n²)?
**A:** Each specific left subtree only ever has its rightmost node searched for exactly once, at the moment that subtree is promoted into the main chain — once promoted, the outer loop simply walks forward through already-threaded right pointers without ever re-searching that same structure, so the total work across all such searches, summed over the whole algorithm, is bounded by the total number of nodes/edges in the tree.

**Q:** Why is this technique described as "Morris-style," and what does it have in common with the classic Morris in-order traversal algorithm?
**A:** Both techniques achieve O(1) extra space by temporarily repurposing the tree's own existing node pointers to create a "thread" that allows the algorithm to correctly continue processing without needing an explicit stack or recursion — here, reattaching the original right subtree to the left subtree's rightmost node serves a conceptually similar threading role.

**Q:** Why must `current.left` be explicitly set to `null`, rather than assuming it becomes `null` automatically once `current.right` is reassigned?
**A:** In Java, reassigning one field of an object has no automatic effect on any other field — `current.left` and `current.right` are entirely independent references, so clearing `current.left` requires its own explicit assignment, separate from whatever happens to `current.right`.

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
    public void flatten(TreeNode root) {
        TreeNode current = root;

        while (current != null) {
            if (current.left != null) {
                TreeNode predecessor = current.left;
                while (predecessor.right != null) {
                    predecessor = predecessor.right;
                }
                predecessor.right = current.right;
                current.right = current.left;
                current.left = null;
            }
            current = current.right;
        }
    }
}
```

## 14. Self-Test

Given the root of a binary tree, flatten it in place into a pre-order-ordered "linked list" using the existing `left`/`right` pointers (`left` always `null`, `right` pointing to the pre-order successor), in O(1) extra space. Re-derive: walk `current` from the root; whenever it has a left child, follow right pointers from that child to find the rightmost node, reattach `current`'s original right subtree there, promote the left subtree into `current.right`, null out `current.left`, then advance `current = current.right`.
