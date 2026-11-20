---
title: "0105 — Construct Binary Tree from Preorder and Inorder Traversal"
layout: default
parent: Binary Tree General
grand_parent: Leetcode
nav_order: 105
permalink: /09-Binary-Tree-General/0105-construct-binary-tree-from-preorder-and-inorder-traversal/

problem: 105 - Construct Binary Tree from Preorder and Inorder Traversal
url: https://leetcode.com/problems/construct-binary-tree-from-preorder-and-inorder-traversal/
difficulty: Medium
section: Tree / Recursion
patterns: [recursive-tree-building, index-map-lookup]
data_structures: [tree, hashmap, array]
time: O(n)
space: O(n)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0105 — Construct Binary Tree from Preorder and Inorder Traversal

| Field | Value |
|---|---|
| **Pattern** | Recursive Tree Building with Index Map Lookup |
| **Difficulty** | Medium |
| **Section** | Tree / Recursion |
| **Time** | O(n) |
| **Space** | O(n) — hashmap for inorder indices + recursion stack |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Meta |
| **LeetCode** | [Construct Binary Tree from Preorder and Inorder Traversal](https://leetcode.com/problems/construct-binary-tree-from-preorder-and-inorder-traversal/) |

## 1. Problem in Simple Words

**Given:** Two integer arrays, `preorder` and `inorder`, representing the preorder and inorder traversals of the same binary tree.

**Required:** Reconstruct and return the original binary tree.

**Constraints that actually matter:**
- `1 <= preorder.length <= 3000`, `inorder.length == preorder.length` — moderate size; a naive approach re-searching through arrays repeatedly risks O(n²), while the optimal approach achieves O(n).
- **All values are unique** across both arrays — this is essential: it guarantees that looking up "where does this specific value appear in `inorder`" always has exactly one unambiguous answer, which is exactly what makes a value-to-index lookup map a valid, reliable technique here.
- **Preorder visits root, then left subtree, then right subtree** — this means the **first element of any preorder segment is always that subtree's root**. **Inorder visits left subtree, then root, then right subtree** — this means that once you know the root's value, **its position within the corresponding inorder segment splits that segment exactly into "everything in the left subtree" (before the root) and "everything in the right subtree" (after the root)**. This complementary relationship between the two traversal orders is the entire key to reconstructing the tree.

## 2. Recognition Signals

- "Reconstruct a tree from two traversal orders" → the classic **recursive tree-building from traversals** technique: preorder's first element always identifies the current subtree's root, and inorder's position of that same value always identifies exactly how to split the remaining elements into "left subtree" and "right subtree" portions, both in preorder and in inorder, enabling a clean recursive construction.
- Recognizing the specific roles of each traversal — **preorder gives you "who's the root," inorder gives you "how to split into left/right"** — is the core insight; this complementary pairing is the reason this specific combination of two traversals (as opposed to, say, inorder + postorder, which plays a similar but mirrored role, or preorder + postorder alone, which is *not* always sufficient to uniquely reconstruct a tree) works.
- Whenever repeatedly determining "the index of a specific value within an array" is needed across many recursive calls, and the array's values are guaranteed unique — precomputing a `value -> index` hash map once, upfront, turns each lookup into O(1) instead of an O(n) linear search, which is the key optimization that takes this from O(n²) down to O(n) overall.

**Pattern:** Recursive Tree Construction from Preorder + Inorder (preorder's first element is always the current subtree's root; inorder's position of that value splits the remaining range into left and right subtree portions), optimized via a precomputed value-to-index map for O(1) root-position lookups instead of repeated linear search.

## 3. Core Idea

The first element of any preorder segment is always the root of the subtree that segment represents (since preorder visits the root before anything else). Once you know that root's value, find its position within the *corresponding* inorder segment — everything to the left of that position (within the inorder segment) belongs to the left subtree, and everything to the right belongs to the right subtree. Crucially, the **sizes** of these two inorder sub-segments (left-of-root-count, right-of-root-count) tell you exactly how to partition the **remaining preorder segment** too: the next `leftSize` elements in preorder (right after the current root) represent the left subtree's preorder traversal, and the following `rightSize` elements represent the right subtree's preorder traversal. Recursively apply this same logic to build the left subtree from its preorder/inorder sub-segments, and the right subtree from its own sub-segments, attaching both as children of the current root.

**Invariant:** At every recursive call, given a valid preorder segment and its corresponding inorder segment (both representing the exact same set of nodes, just in their respective traversal orders), the first element of the preorder segment is correctly identified as that subtree's root, and the inorder segment is correctly split (using that root's position) into exactly the left and right subtree's own inorder segments — with the preorder segment similarly split by the matching sizes — so the recursion correctly reconstructs every subtree at every level, bottoming out at empty segments (representing `null` children).

## 4. Approach 1 — Brute Force (Repeated Linear Search + Array Copying)

**Logic:** At each recursive step, take the first element of the current preorder segment as the root, perform a linear search through the current inorder segment to find that value's position, and construct new sub-arrays (via copying) for the left and right preorder/inorder segments to pass into the recursive calls.

```java
public TreeNode buildTree(int[] preorder, int[] inorder) {
    if (preorder.length == 0) {
        return null;
    }

    int rootVal = preorder[0];
    TreeNode root = new TreeNode(rootVal);

    int rootIndexInInorder = 0;
    for (int i = 0; i < inorder.length; i++) {
        if (inorder[i] == rootVal) {
            rootIndexInInorder = i;
            break;
        }
    }

    int[] leftInorder = Arrays.copyOfRange(inorder, 0, rootIndexInInorder);
    int[] rightInorder = Arrays.copyOfRange(inorder, rootIndexInInorder + 1, inorder.length);

    int[] leftPreorder = Arrays.copyOfRange(preorder, 1, 1 + leftInorder.length);
    int[] rightPreorder = Arrays.copyOfRange(preorder, 1 + leftInorder.length, preorder.length);

    root.left = buildTree(leftPreorder, leftInorder);
    root.right = buildTree(rightPreorder, rightInorder);

    return root;
}
```

- **Time:** O(n²) in the worst case — the linear search for the root's position in `inorder` is O(n) per call, repeated across O(n) total recursive calls (one per node), and the array copying at each level also contributes additional O(n) work per call in the worst case (e.g., a completely skewed tree).
- **Space:** O(n²) in the worst case — repeatedly copying sub-arrays at every level of recursion uses cumulative space proportional to `n` at each of up to `n` levels, in addition to the O(n) recursion stack itself.

**Why it is not optimal:** The repeated linear search for each root's position, combined with the overhead of creating new array copies at every single recursive call, together push this well past the O(n) time and space that's achievable — both of these costs are avoidable with simple preprocessing (a lookup map) and by working with index ranges into the original arrays instead of creating new sub-arrays.

## 5. Approach 2 — Optimized (Index-Range Recursion with a Value-to-Index Map)

**Algorithm:**
1. Precompute a `Map<Integer, Integer>` mapping each value in `inorder` to its index, built in one O(n) pass — this allows O(1) lookup of "where is this root value in `inorder`" at every recursive call, instead of a linear search.
2. Use a recursive helper that operates on **index ranges** into the original `preorder` and `inorder` arrays (never creating new sub-arrays), tracking a shared `preorderIndex` pointer (since preorder elements are always consumed strictly left to right, one "next root" at a time, regardless of which subtree is currently being built) plus `inorderStart`/`inorderEnd` bounds for the current subtree's inorder range.
3. At each call: if `inorderStart > inorderEnd`, return `null` (empty range, no subtree here). Otherwise, take `preorder[preorderIndex]` as the current root, increment `preorderIndex` (consuming it), look up the root's index in `inorder` via the map, recursively build the left subtree using `inorderStart` to `rootIndex - 1`, then recursively build the right subtree using `rootIndex + 1` to `inorderEnd` (the left subtree **must** be built first, since it consumes the correct next chunk of `preorderIndex` before the right subtree's construction begins).

```java
public TreeNode buildTree(int[] preorder, int[] inorder) {
    Map<Integer, Integer> inorderIndexMap = new HashMap<>();
    for (int i = 0; i < inorder.length; i++) {
        inorderIndexMap.put(inorder[i], i);
    }

    int[] preorderIndex = {0}; // Mutable wrapper to track position across recursive calls.
    return build(preorder, inorderIndexMap, preorderIndex, 0, inorder.length - 1);
}

private TreeNode build(int[] preorder, Map<Integer, Integer> inorderIndexMap,
                        int[] preorderIndex, int inorderStart, int inorderEnd) {
    if (inorderStart > inorderEnd) {
        return null;
    }

    int rootVal = preorder[preorderIndex[0]];
    preorderIndex[0]++;
    TreeNode root = new TreeNode(rootVal);

    int rootIndexInInorder = inorderIndexMap.get(rootVal);

    root.left = build(preorder, inorderIndexMap, preorderIndex, inorderStart, rootIndexInInorder - 1);
    root.right = build(preorder, inorderIndexMap, preorderIndex, rootIndexInInorder + 1, inorderEnd);

    return root;
}
```

- **Time:** O(n) — the map is built in O(n), and each of the `n` recursive calls (one per node) does O(1) work (a map lookup plus a constant number of pointer updates), for a total of O(n).
- **Space:** O(n) — O(n) for the map, plus O(h) for the recursion stack (which is itself bounded by O(n) in the worst case for a skewed tree), and critically, **no additional array copying** at any level — all recursive calls operate on index ranges into the same original arrays.

**Why this is optimal:** Every node must be visited and constructed exactly once, so O(n) time is a hard lower bound, and this approach achieves it by eliminating both sources of inefficiency in Approach 1: the O(1) map lookup replaces the O(n) linear search, and operating on index ranges (rather than creating new sub-arrays at every call) eliminates the O(n) copying cost per call. This is the standard, expected optimal solution for this classic tree-reconstruction problem.

## 6. Dry Run

Example: `preorder = [3,9,20,15,7]`, `inorder = [9,3,15,20,7]`.
Chosen because it's the canonical example, directly showing the root-identification and range-splitting mechanics across multiple recursive levels.

`inorderIndexMap = {9:0, 3:1, 15:2, 20:3, 7:4}`. `preorderIndex = [0]`.

**Call 1:** `build(inorderStart=0, inorderEnd=4)`. `rootVal = preorder[0] = 3`. `preorderIndex` → `[1]`. `rootIndexInInorder = map.get(3) = 1`.

- Recurse left: `build(inorderStart=0, inorderEnd=0)` (since `rootIndexInInorder-1 = 0`).
  - `rootVal = preorder[1] = 9`. `preorderIndex` → `[2]`. `rootIndexInInorder = map.get(9) = 0`.
  - Recurse left: `build(inorderStart=0, inorderEnd=-1)` → `inorderStart > inorderEnd`, return `null`.
  - Recurse right: `build(inorderStart=1, inorderEnd=0)` → `inorderStart > inorderEnd`, return `null`.
  - Node `9` created with `left=null, right=null`. Returns node `9`.
- Recurse right: `build(inorderStart=2, inorderEnd=4)` (since `rootIndexInInorder+1 = 2`).
  - `rootVal = preorder[2] = 20`. `preorderIndex` → `[3]`. `rootIndexInInorder = map.get(20) = 3`.
  - Recurse left: `build(inorderStart=2, inorderEnd=2)`.
    - `rootVal = preorder[3] = 15`. `preorderIndex` → `[4]`. `rootIndexInInorder = map.get(15) = 2`.
    - Both children: `build(2,1)` and `build(3,2)`, both empty ranges, return `null`.
    - Node `15` created, `left=null, right=null`. Returns node `15`.
  - Recurse right: `build(inorderStart=4, inorderEnd=4)`.
    - `rootVal = preorder[4] = 7`. `preorderIndex` → `[5]`. `rootIndexInInorder = map.get(7) = 4`.
    - Both children: empty ranges, return `null`.
    - Node `7` created. Returns node `7`.
  - Node `20` created with `left=15, right=7`. Returns node `20`.

Node `3` created with `left=9, right=20` (which itself has `left=15, right=7`).

Exit condition: outermost call returns the fully constructed root.

**Final answer:** Tree rooted at `3`, with left child `9` (leaf) and right child `20` (which has left child `15` and right child `7`) — matching the expected output `[3,9,20,null,null,15,7]` exactly.

## 7. Complexity Summary

| Approach | Time | Space (Auxiliary) | Notes |
|----------|------|--------------------|-------|
| 1. Linear search + array copying | O(n²) worst case | O(n²) worst case | Correct but far too slow/memory-heavy for n up to 3000 |
| 2. Index-range recursion + value-to-index map | O(n) | O(n) | Optimal; map lookup replaces search, index ranges replace copying |

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|------|-------------------|--------------------------|
| Single-node tree (`preorder = [-1]`, `inorder = [-1]`, example 2) | Trivial case; should construct a single node with no children | The initial call has `inorderStart=0, inorderEnd=0`; the root (value -1) is created, and both recursive child calls receive empty ranges (`build(0,-1)` and `build(1,0)`), correctly returning `null` for both children |
| A completely left-skewed or right-skewed tree (every node has only one child, consistently on the same side) | The recursion depth reaches O(n) in this case, and the index-range splitting must still work correctly even though one "side" is always empty | The algorithm's logic doesn't depend on the tree being balanced — a skewed tree simply means one of the two recursive calls at every level always receives an empty range (immediately returning `null`), while the other continues the chain; this is handled correctly without any special-casing |
| Negative values (constraint allows down to -3000) | Value comparisons and map lookups must work correctly regardless of sign | The `HashMap<Integer, Integer>` and direct value usage as map keys work identically regardless of whether values are negative, zero, or positive — no special handling needed |
| The shared `preorderIndex` pointer needing to be correctly threaded through recursive calls in the right order (left subtree fully built before right subtree construction begins) | Since preorder lists all of a subtree's nodes (root, then entire left subtree, then entire right subtree) contiguously, the shared index pointer must advance through exactly the left subtree's nodes before the right subtree's construction can correctly begin reading from the right starting point | The code explicitly builds `root.left` (which fully advances `preorderIndex` through the entire left subtree) *before* building `root.right`, ensuring `preorderIndex` is correctly positioned at the start of the right subtree's preorder segment by the time that recursive call begins |
| Maximum constraint size (`n=3000`) | Should still perform efficiently and correctly at the upper bound | The O(n) time and space complexity, with no per-call array copying, comfortably handles this size |

## 9. Java Notes

- **`int[] preorderIndex = {0};` as a mutable wrapper for a shared, mutating index across recursive calls:** since Java passes primitives by value, a plain `int` parameter couldn't be mutated and have that mutation visible across sibling recursive calls (specifically, the right subtree's call needs to see the index advancement made by the left subtree's call); wrapping it in a single-element array (or alternatively, using a class-level field, or returning an updated index alongside the constructed node) is a common Java idiom for this kind of "shared mutable state across recursive calls" need.
- **`Map<Integer, Integer>` for O(1) value-to-index lookup:** built once upfront in O(n), this converts what would otherwise be a repeated O(n) linear search (once per recursive call) into a single O(1) lookup per call, which is the single biggest factor in bringing the overall algorithm down from O(n²) to O(n).
- **Operating on index ranges (`inorderStart`, `inorderEnd`) into the original arrays, rather than creating new sub-arrays at each recursive call:** this avoids the O(n) copying cost per call that `Arrays.copyOfRange` would otherwise incur, which is the other key factor (alongside the map) in achieving the optimal O(n) time and space.
- **No overflow or numeric risk:** this problem involves only array indexing and value comparisons within the given small bounded range (-3000 to 3000); no arithmetic here risks overflow.

## 10. Common Mistakes

- **Building the right subtree before the left subtree.** Since preorder lists a subtree's root, then its *entire* left subtree, then its *entire* right subtree, the shared `preorderIndex` pointer must be fully advanced through the left subtree's nodes before the right subtree's construction begins reading from the correct next position — building them in the wrong order would cause the right subtree's construction to start reading from the wrong index, corrupting the result. Fix: always construct `root.left` before `root.right`, relying on their natural left-to-right evaluation order in the code.
- **Performing a fresh linear search for the root's position in `inorder` at every recursive call, instead of precomputing a lookup map.** This is the primary source of the O(n²) inefficiency in the brute-force approach. Fix: always build a `value -> index` map once, upfront, before any recursion begins.
- **Creating new sub-arrays (via `Arrays.copyOfRange` or similar) at every recursive call, instead of passing index ranges into the original arrays.** This adds significant unnecessary time and space overhead across the many recursive calls. Fix: always operate on index boundaries (`start`, `end`) into the original, unmodified arrays, never on freshly copied sub-arrays.
- **Forgetting that the same shared `preorderIndex` must be used (and mutated) consistently across the entire recursion, rather than trying to independently compute "which preorder index corresponds to this subtree" at each call based on inorder range sizes alone.** While it's true that the sizes of the inorder sub-ranges determine how many preorder elements belong to each subtree, directly computing preorder boundaries this way (instead of using one continuously advancing shared pointer) is more error-prone to implement correctly; using a single shared, mutating pointer that's simply consumed one element at a time, left subtree first, is the standard and most robust technique. Fix: use a single shared, mutable index into `preorder` that's incremented each time a new root is consumed, rather than recomputing preorder boundaries independently at each call.

## 11. Interview Takeaway

- **Trigger sentence to memorize:** "Reconstruct a binary tree from its preorder and inorder traversals → preorder's first element is always the current root; that root's position in the corresponding inorder segment splits it into left and right subtree portions — recurse, using a value-to-index map for O(1) root lookups."
- **90-second explanation:** "Preorder traversal visits the root before anything else, so the very first element of any preorder segment always tells me the root of that subtree. Once I know that root's value, I look up its position within the corresponding inorder segment — since inorder visits the left subtree, then the root, then the right subtree, everything before that position in the inorder segment belongs to the left subtree, and everything after belongs to the right subtree. I precompute a hash map from value to index in the inorder array upfront, so I can find any root's position in O(1) instead of searching for it every single time. Then, rather than creating new sub-arrays for every recursive call, I just track index ranges into the original arrays, plus a single shared pointer into the preorder array that I advance by one every time I consume a new root value — critically, I always fully build the left subtree before the right subtree, since preorder lists the entire left subtree's nodes before any of the right subtree's nodes, so my shared pointer needs to be correctly advanced past all of them first. This combination — a lookup map instead of linear search, and index ranges instead of array copying — brings the whole algorithm down to O(n) time and space, visiting and constructing each node exactly once."
- **Related problems using this pattern:**
  - LeetCode 106 — Construct Binary Tree from Inorder and Postorder Traversal (the closely related mirror-image version of this exact technique)
  - LeetCode 889 — Construct Binary Tree from Preorder and Postorder Traversal (a related but structurally different variant, since preorder+postorder alone doesn't always uniquely determine a tree)
  - LeetCode 1008 — Construct Binary Search Tree from Preorder Traversal (a related, simpler problem exploiting BST ordering instead of needing a second traversal array)

## 12. Recall Questions

**Q:** Why is the first element of any preorder segment always guaranteed to be that subtree's root?
**A:** Preorder traversal, by definition, visits a node before visiting either of its subtrees, so the very first value encountered when traversing any subtree in preorder must be that subtree's own root, before any of its descendants are visited.

**Q:** Once a subtree's root value is known, why does finding that value's position within the corresponding inorder segment correctly split the remaining elements into left and right subtree portions?
**A:** Inorder traversal visits a node's entire left subtree before the node itself, and the entire right subtree after it, so within any inorder segment, everything appearing before the root's position must belong to the left subtree, and everything after must belong to the right subtree.

**Q:** Why must the left subtree always be fully constructed before the right subtree begins, rather than the order being arbitrary?
**A:** Preorder traversal lists a subtree's root, followed by every node of its entire left subtree, followed by every node of its entire right subtree, all contiguously — so the shared pointer tracking the next unconsumed preorder index must be advanced through all of the left subtree's nodes first, or the right subtree's construction would incorrectly begin reading from the wrong starting position in the preorder array.

**Q:** Why does precomputing a value-to-index map for the inorder array specifically rely on the problem's guarantee that all values are unique?
**A:** A map from value to index can only provide a single, unambiguous answer if each value appears at exactly one position in the array, and the problem's explicit guarantee of unique values across both traversal arrays is precisely what ensures this lookup is always well-defined and reliable.

**Q:** Why does avoiding the creation of new sub-arrays at each recursive call, in favor of passing index ranges into the original arrays, meaningfully improve this algorithm's efficiency?
**A:** Creating a new sub-array at every one of the many recursive calls (one per node in the tree) requires copying potentially many elements each time, adding cumulative time and space costs that scale poorly, whereas simply tracking start and end index boundaries into the same original, unmodified arrays avoids any such copying entirely, needing only a constant amount of extra bookkeeping per call.

## 13. Final Code

```java
public TreeNode buildTree(int[] preorder, int[] inorder) {
    Map<Integer, Integer> inorderIndexMap = new HashMap<>();
    for (int i = 0; i < inorder.length; i++) {
        inorderIndexMap.put(inorder[i], i);
    }

    int[] preorderIndex = {0};
    return build(preorder, inorderIndexMap, preorderIndex, 0, inorder.length - 1);
}

private TreeNode build(int[] preorder, Map<Integer, Integer> inorderIndexMap,
                        int[] preorderIndex, int inorderStart, int inorderEnd) {
    if (inorderStart > inorderEnd) {
        return null;
    }

    int rootVal = preorder[preorderIndex[0]];
    preorderIndex[0]++;
    TreeNode root = new TreeNode(rootVal);

    int rootIndexInInorder = inorderIndexMap.get(rootVal);

    root.left = build(preorder, inorderIndexMap, preorderIndex, inorderStart, rootIndexInInorder - 1);
    root.right = build(preorder, inorderIndexMap, preorderIndex, rootIndexInInorder + 1, inorderEnd);

    return root;
}
```

## 14. Self-Test

You have two arrays, `preorder` and `inorder`, representing the same binary tree's traversals. Reconstruct and return that tree. Think about why preorder always reveals the current subtree's root, why inorder's position of that root splits the remaining elements into left and right subtree portions, and why the left subtree must always be fully built before the right subtree begins.
