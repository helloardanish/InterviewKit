---
title: "0106 — Construct Binary Tree from Inorder and Postorder Traversal"
layout: default
parent: Binary Tree General
grand_parent: Leetcode
nav_order: 106
permalink: /09-Binary-Tree-General/0106-construct-binary-tree-from-inorder-and-postorder-traversal/

problem: 106 - Construct Binary Tree from Inorder and Postorder Traversal
url: https://leetcode.com/problems/construct-binary-tree-from-inorder-and-postorder-traversal/
difficulty: Medium
section: Tree / Divide and Conquer
patterns: [tree-recursion, hashmap, traversal-reconstruction]
data_structures: [tree, hashmap, array]
time: O(n)
space: O(n)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0106 — Construct Binary Tree from Inorder and Postorder Traversal

| Field | Value |
|---|---|
| **Pattern** | Divide and Conquer with Index Map + Postorder Root at End |
| **Difficulty** | Medium |
| **Section** | Tree / Divide and Conquer |
| **Time** | O(n) |
| **Space** | O(n) — hashmap + recursion stack |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Meta |
| **LeetCode** | [Construct Binary Tree from Inorder and Postorder Traversal](https://leetcode.com/problems/construct-binary-tree-from-inorder-and-postorder-traversal/) |

## 1. Problem in Simple Words

**Given:** two integer arrays, `inorder` and `postorder`, representing the in-order and post-order traversals of the same binary tree.

**Required:** reconstruct and return that binary tree.

**Constraints that matter:**
- `1 <= inorder.length <= 3000` and `postorder.length == inorder.length` — both arrays always have matching, non-zero length; no need to handle a completely empty input at the top level, though empty *sub*-ranges will naturally arise during recursion and must be handled as the base case.
- **All values are unique**, and every value in `postorder` also appears in `inorder` — uniqueness is essential: it guarantees that finding a specific value's position in `inorder` always identifies exactly one node, with no ambiguity about which occurrence is meant.
- Both traversals are **guaranteed valid** or derived from the same actual tree — there's no need to validate consistency between the two arrays; the reconstruction is guaranteed to be possible and unique given unique values.

## 2. Recognition Signals

- "Construct a binary tree from two traversal orders" → a classic **divide-and-conquer tree reconstruction** problem; recognizing which traversal identifies the root and which one identifies the left/right split is the key skill.
- **Post-order's last element is always the root** of the (sub)tree it represents, since post-order visits left, then right, then the root itself last — this is the anchor fact that makes the whole reconstruction possible: once the root's value is known, its position within the corresponding `inorder` range splits that range into "everything in the left subtree" (left of the root's position) and "everything in the right subtree" (right of the root's position).
- Needing to repeatedly find "where does this value appear in `inorder`" → the signal for a **HashMap from value to index**, built once up front, to avoid repeated linear scans (which would otherwise degrade the whole algorithm to O(n²)).
- Named pattern: **Divide-and-Conquer Tree Reconstruction via Root-Finding and Range-Splitting**.

## 3. Core Idea

The last element of any post-order range is that subtree's root. Look up that value's position in the corresponding `inorder` range — everything to the left of that position (within the range) belongs to the left subtree, and everything to the right belongs to the right subtree. Since post-order lists left-subtree-elements, then right-subtree-elements, then the root, the **right** subtree's elements are the ones immediately preceding the root in `postorder` — so processing `postorder` from the **end backward**, building the right subtree first and then the left subtree, naturally lines up with how post-order lays out its elements.

**Why it's correct:** this is a direct, provable consequence of how post-order and in-order traversals are defined. Post-order always places a (sub)tree's root as the very last element of that subtree's range, which immediately identifies it. In-order always places a node's entire left subtree before it and entire right subtree after it (within that node's range), so once the root's position in the `inorder` range is known, the range splits cleanly and unambiguously into exactly the left and right subtrees' respective elements — with uniqueness of values guaranteeing the split point is found correctly every time.

**Invariant:** at any point in the recursion, given a correctly-corresponding pair of `(inorder range, postorder position range)` representing some subtree, the algorithm correctly identifies that subtree's root (the current end of the postorder range) and correctly partitions the remaining ranges into the left and right subtrees' corresponding ranges, which themselves satisfy the same correspondence property for the recursive calls.

## 4. Approach 1 — Brute Force (Subarray Slicing with Linear Search for Root Index)

1. At each recursive call, the root is the last element of the current `postorder` subarray.
2. Linearly search the current `inorder` subarray for that root value's index.
3. Slice out new subarrays for the left `inorder`/`postorder` and right `inorder`/`postorder` portions (creating actual new array copies), and recurse on each to build the left and right subtrees.

```java
public TreeNode buildTreeBruteForce(int[] inorder, int[] postorder) {
    if (inorder.length == 0) {
        return null;
    }

    int rootVal = postorder[postorder.length - 1];
    TreeNode root = new TreeNode(rootVal);

    int rootIndex = 0;
    for (int i = 0; i < inorder.length; i++) {
        if (inorder[i] == rootVal) {
            rootIndex = i;
            break;
        }
    }

    int[] leftInorder = Arrays.copyOfRange(inorder, 0, rootIndex);
    int[] rightInorder = Arrays.copyOfRange(inorder, rootIndex + 1, inorder.length);
    int[] leftPostorder = Arrays.copyOfRange(postorder, 0, rootIndex);
    int[] rightPostorder = Arrays.copyOfRange(postorder, rootIndex, postorder.length - 1);

    root.left = buildTreeBruteForce(leftInorder, leftPostorder);
    root.right = buildTreeBruteForce(rightInorder, rightPostorder);

    return root;
}
```

**Time:** O(n²) in the worst case — each recursive call performs a linear search for the root's index (O(n) in the worst case, e.g., for a completely skewed tree) and creates new array copies via `Arrays.copyOfRange` (also O(n) per call), across O(n) total calls.
**Space:** O(n²) in the worst case for all the intermediate array copies created across the recursion, plus O(h) for the recursion stack itself.

This is not optimal: repeatedly searching for the root's index and copying subarrays at every recursive call wastes significant time and memory — the root-finding can be made O(1) with a precomputed lookup, and the array copying can be avoided entirely by passing index ranges into the original arrays instead of creating new array objects.

## 5. Approach 2 — Optimized (HashMap Index Lookup + Range Pointers, No Array Copying)

1. Build a `Map<Integer, Integer> inorderIndex` mapping each value in `inorder` to its index, in one O(n) pass — this replaces the need for any linear search during recursion.
2. Use a single shared `postIndex` variable (an instance/class-level field, or passed by reference via an array/wrapper), initialized to `postorder.length - 1`, tracking which element of `postorder` to consume next — since the recursion will process the root, then (critically) the right subtree, then the left subtree, consuming `postorder` from the end backward in exactly that order.
3. Define a recursive helper `build(inStart, inEnd)` operating on index ranges into the *original* `inorder` array (no copying):
   - Base case: if `inStart > inEnd`, return `null` (empty range).
   - Otherwise: the root's value is `postorder[postIndex]`; decrement `postIndex`. Create the root node, look up its index in `inorder` via the hashmap.
   - **Critically, build the right subtree before the left subtree**: `root.right = build(rootIndex + 1, inEnd)`, then `root.left = build(inStart, rootIndex - 1)` — this order matches how `postIndex` naturally decrements through `postorder`'s root-right-left-reversed-from-the-end structure.
4. Call `build(0, inorder.length - 1)` to construct the whole tree.

```java
class Solution {
    private int postIndex;
    private Map<Integer, Integer> inorderIndex;
    private int[] postorder;

    public TreeNode buildTree(int[] inorder, int[] postorder) {
        this.postorder = postorder;
        this.postIndex = postorder.length - 1;
        this.inorderIndex = new HashMap<>();

        for (int i = 0; i < inorder.length; i++) {
            inorderIndex.put(inorder[i], i);
        }

        return build(0, inorder.length - 1);
    }

    private TreeNode build(int inStart, int inEnd) {
        if (inStart > inEnd) {
            return null;
        }

        int rootVal = postorder[postIndex];
        postIndex--;

        TreeNode root = new TreeNode(rootVal);
        int rootIndex = inorderIndex.get(rootVal);

        root.right = build(rootIndex + 1, inEnd);
        root.left = build(inStart, rootIndex - 1);

        return root;
    }
}
```

**Time:** O(n) — building the hashmap is O(n); each of the n recursive calls does O(1) work (a map lookup, a node creation, two recursive calls on sub-ranges) beyond the recursive calls themselves, so the total work across the whole recursion tree is O(n).
**Space:** O(n) for the hashmap, plus O(h) for the recursion call stack (O(log n) for a balanced tree, up to O(n) for a completely skewed one) — no array copying occurs anywhere, since all recursive calls operate on index ranges into the single original `inorder` array.

This is optimal: it eliminates both sources of the brute force's inefficiency — the hashmap makes root-index lookup O(1) instead of O(n), and passing index ranges (rather than copied subarrays) eliminates the O(n) copying cost at every recursive call, bringing the overall time down from O(n²) to O(n).

## 6. Dry Run

`inorder = [9,3,15,20,7]`, `postorder = [9,15,7,20,3]`

`inorderIndex = {9:0, 3:1, 15:2, 20:3, 7:4}`; `postIndex` starts at `4`.

| call | inStart, inEnd | rootVal (postorder[postIndex]) | postIndex after | rootIndex | right subtree call | left subtree call |
|---|---|---|---|---|---|---|
| build(0,4) | 0,4 | 3 | 3 | 1 | build(2,4) | build(0,0) (after right returns) |
| build(2,4) | 2,4 | 20 | 2 | 3 | build(4,4) | build(2,2) (after right returns) |
| build(4,4) | 4,4 | 7 | 1 | 4 | build(5,4)=null | build(4,3)=null |
| build(2,2) | 2,2 | 15 | 0 | 2 | build(3,2)=null | build(2,1)=null |
| build(0,0) | 0,0 | 9 | -1 | 0 | build(1,0)=null | build(0,-1)=null |

Tree assembled: `build(4,4)` returns node `7` (leaf). `build(2,2)` returns node `15` (leaf). `build(2,4)` returns node `20` with `right=7`, `left=15`. `build(0,0)` returns node `9` (leaf). `build(0,4)` (the root call) returns node `3` with `right = [20 with children 15,7]`, `left = 9`.

Final tree: `3` has left child `9` and right child `20`; `20` has left child `15` and right child `7`. This matches the expected output `[3,9,20,null,null,15,7]`.

## 7. Complexity Summary

| Approach | Time | Space | Notes |
|---|---|---|---|
| Subarray Slicing + Linear Search | O(n²) worst case | O(n²) worst case (copies) + O(h) recursion | Repeated searching and array copying at every call |
| HashMap Lookup + Index Ranges | O(n) | O(n) (hashmap) + O(h) recursion | No copying; O(1) root-index lookup per call |

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|---|---|---|
| Single-node tree (`inorder = [-1]`, `postorder = [-1]`) | Minimal valid input | `build(0,0)`: `rootVal = -1`, `rootIndex = 0`; `build(1,0)` and `build(0,-1)` both immediately hit the base case `inStart > inEnd`, returning `null` for both children |
| A node with only a left child (no right child) | `build(rootIndex+1, inEnd)` for the right subtree must correctly return `null` | If `rootIndex == inEnd`, then `rootIndex + 1 > inEnd`, so the right-subtree call immediately satisfies the base case, correctly producing no right child |
| A node with only a right child (no left child) | `build(inStart, rootIndex-1)` for the left subtree must correctly return `null` | If `rootIndex == inStart`, then `rootIndex - 1 < inStart`, so the left-subtree call immediately satisfies the base case, correctly producing no left child |
| Completely skewed tree (e.g., every node has only a right child, or only a left child) | Recursion depth could reach up to n (3000), and the brute force's linear search would be at its worst here too | The optimized approach's O(1) hashmap lookup avoids any search-time penalty regardless of skew; recursion depth is a separate, inherent concern shared by both approaches for any tree-reconstruction technique built on direct recursion |
| Right subtree must be built **before** the left subtree | Building them in the wrong order would consume `postIndex` out of sync with what `postorder`'s structure actually represents at each position | The code explicitly calls `build` for `root.right` first, then `root.left` — this order is essential and directly reflects post-order's left-right-root structure read backward (root, then right, then left) |
| Large but non-skewed (balanced) tree near the 3000-node constraint boundary | Must perform efficiently at the upper end of allowed input size | The O(n) time and O(n) + O(log n) space bounds comfortably handle inputs of this size without issue |

## 9. Java Notes

- Using a shared, mutable `postIndex` field (rather than passing and returning an updated index value through each recursive call) is a common and convenient technique in Java for this kind of traversal-reconstruction problem — it avoids the awkwardness of needing to return multiple values (the constructed subtree *and* the updated index) from each recursive call, at the cost of making the helper method depend on instance state rather than being purely a function of its parameters.
- `inorderIndex.get(rootVal)` returns an `Integer`, which is then auto-unboxed when assigned to `int rootIndex` — safe here since every `postorder` value is guaranteed (by the problem's constraints) to also exist in `inorder`, so the map lookup will never return `null`.
- No array copying occurs anywhere in the optimized solution — `inStart`/`inEnd` are just `int` indices into the single original `inorder` array, and `postorder` itself is never sliced either, only indexed via the shared `postIndex` counter.
- The **critical ordering detail** — building `root.right` before `root.left` — is specific to reconstructing from **post-order** (processed backward); the equivalent problem using **pre-order** instead of post-order would require the opposite order (`root.left` before `root.right`), since pre-order lists the root first, then the left subtree, then the right subtree, naturally processed forward.

## 10. Common Mistakes

- Building `root.left` before `root.right` — since `postIndex` is being consumed from the end of `postorder` backward, and post-order's structure is left-subtree-elements, then right-subtree-elements, then root, the elements immediately *before* the root in `postorder` belong to the **right** subtree, not the left; building the left subtree first would consume `postIndex` values that actually belong to the right subtree, corrupting the entire reconstruction. Fix: always build the right subtree first when reconstructing from post-order processed backward.
- Forgetting to decrement `postIndex` (or decrementing it in the wrong place, e.g., before using it to read the root's value) — this would either reuse the same root value for multiple nodes, or skip over a value entirely, corrupting the tree structure. Fix: read `postorder[postIndex]` first, then decrement, in that exact order, for every single recursive call that doesn't hit the base case.
- Performing a linear search for the root's index in `inorder` on every recursive call instead of precomputing a hashmap — functionally correct, but this is precisely the inefficiency that degrades the brute force to O(n²); recognizing and fixing this is the core optimization this problem is testing for.
- Creating new array copies (via slicing) for every recursive call instead of passing index ranges into the original arrays — also functionally correct but wasteful; this compounds with the linear-search inefficiency to make the brute force's worst-case space complexity O(n²) as well, not just its time.

## 11. Interview Takeaway

- **Trigger sentence:** "Reconstruct a binary tree from in-order and post-order traversals → post-order's last element is always the (sub)tree's root; find its position in in-order to split into left/right ranges, and process post-order backward, building the right subtree before the left."
- **90-second explanation:** "Post-order always ends with the root of whatever subtree it's describing, so the very last element I haven't yet consumed is always my next root. Once I know the root's value, I look up where it sits in the in-order array — everything before that position is the left subtree, everything after is the right subtree, since in-order always lists a node's left subtree, then itself, then its right subtree. The subtlety is the order I build things in: since I'm consuming post-order from the end backward, and post-order's layout is left-elements, then right-elements, then root, the values immediately before the root actually belong to the right subtree, not the left — so I have to recursively build the right subtree first, before the left, to keep my shrinking post-order pointer in sync with what it's actually pointing at. To avoid any repeated searching, I precompute a hashmap from value to in-order index up front, and I never copy any arrays — I just pass index ranges into the original in-order array through the recursion."
- **Related problems:** 105 (Construct Binary Tree from Preorder and Inorder Traversal, the mirror-image problem with the opposite left/right build order), 889 (Construct Binary Tree from Preorder and Postorder Traversal), 108 (Convert Sorted Array to Binary Search Tree, a related range-splitting recursive construction).

## 12. Recall Questions

**Q:** Why is the last element of a post-order traversal always that (sub)tree's root?
**A:** Post-order traversal is defined as visiting the left subtree, then the right subtree, then the node itself last, so by definition the very final element of any post-order sequence for a given subtree must be that subtree's own root.

**Q:** Why must the right subtree be recursively built before the left subtree in this specific reconstruction?
**A:** Because `postIndex` is being consumed from the end of the `postorder` array backward, and post-order lists a subtree's elements as left-subtree-elements, then right-subtree-elements, then the root — so working backward from the root, the elements encountered next actually belong to the right subtree, and only after those are exhausted do the left subtree's elements appear; building left first would misalign the shared index with the actual data.

**Q:** Why does precomputing a hashmap from value to in-order index reduce the time complexity from O(n²) to O(n)?
**A:** Without it, finding the root's position within the current in-order range requires a linear scan at every one of the n recursive calls, giving O(n²) in the worst case; with a precomputed hashmap, that same lookup becomes O(1) per call, reducing the total lookup cost across all calls to O(n).

**Q:** Why is it safe to pass index ranges into the original `inorder` array instead of creating new sliced sub-arrays at each recursive call?
**A:** The algorithm never needs to modify the array's contents, only to read values within a specific bounded range, so index bounds (`inStart`, `inEnd`) fully describe the relevant sub-range without requiring any actual copying of data — avoiding this copying is what eliminates the brute force's additional O(n) cost (and O(n²) total) per level of recursion.

**Q:** Why does uniqueness of values in both arrays matter for the correctness of the root-lookup step?
**A:** If values could repeat, looking up "the index of this root value in `inorder`" could be ambiguous — there might be multiple valid positions — making it impossible to reliably determine the correct split point between the left and right subtrees; uniqueness guarantees exactly one unambiguous match every time.

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
    private int postIndex;
    private Map<Integer, Integer> inorderIndex;
    private int[] postorder;

    public TreeNode buildTree(int[] inorder, int[] postorder) {
        this.postorder = postorder;
        this.postIndex = postorder.length - 1;
        this.inorderIndex = new HashMap<>();

        for (int i = 0; i < inorder.length; i++) {
            inorderIndex.put(inorder[i], i);
        }

        return build(0, inorder.length - 1);
    }

    private TreeNode build(int inStart, int inEnd) {
        if (inStart > inEnd) {
            return null;
        }

        int rootVal = postorder[postIndex];
        postIndex--;

        TreeNode root = new TreeNode(rootVal);
        int rootIndex = inorderIndex.get(rootVal);

        root.right = build(rootIndex + 1, inEnd);
        root.left = build(inStart, rootIndex - 1);

        return root;
    }
}
```

## 14. Self-Test

Given the in-order and post-order traversals of a binary tree with unique values, reconstruct and return the tree. Re-derive: precompute a hashmap from value to in-order index; using a shared pointer starting at the end of `postorder`, recursively take the current pointer's value as the root, look up its in-order position to split the current range, and build the **right** subtree before the left subtree (since post-order processed backward yields right-subtree elements before left-subtree elements), decrementing the shared pointer each time a root is consumed.
