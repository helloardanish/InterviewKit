---
title: "0108 — Convert Sorted Array to Binary Search Tree"
layout: default
parent: Divide-and-Conquer
grand_parent: Leetcode
nav_order: 108
permalink: /16-Divide-and-Conquer/0108-convert-sorted-array-to-binary-search-tree/

problem: 108 - Convert Sorted Array to Binary Search Tree
url: "https://leetcode.com/problems/convert-sorted-array-to-binary-search-tree/"
difficulty: Easy
section: Divide and Conquer
patterns: [divide-and-conquer, middle-as-root]
data_structures: [array, binary-search-tree]
time: O(n)
space: O(log n)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0108 — Convert Sorted Array to Binary Search Tree**

| Field | Value |
|---|---|
| **Pattern** | Divide and Conquer + Middle as Root |
| **Difficulty** | Easy |
| **Section** | Divide and Conquer |
| **Time** | O(n) |
| **Space** | O(log n) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Amazon, Google, Meta, Microsoft, Bloomberg |
| **LeetCode** | [Convert Sorted Array to Binary Search Tree](https://leetcode.com/problems/convert-sorted-array-to-binary-search-tree/) |


## 1. Problem in Simple Words

**Given:** An integer array `nums` sorted in strictly increasing order.

**Required:** Build a **height-balanced** binary search tree (BST) containing exactly these values, and return its root. Height-balanced means that for every node, the heights of its left and right subtrees differ by at most 1. Many valid trees exist and any one is accepted.

A note on the statement: the page text I fetched only says "convert it to a binary search tree" in its first sentence, but Example 2's explanation calls both accepted answers "height-balanced BSTs", and this is LeetCode 108, whose requirement is a height-balanced BST. I treat balance as required. Without it, a plain chain would trivially be a valid BST and the problem would have no content.

**Constraints that actually matter:**
- `1 <= nums.length <= 10^4` — the array is never empty, and with at most 10,000 elements a balanced tree has height about 14, so recursion depth is tiny. Quadratic work (10^8 steps) would be wasteful, and linear time is easily achievable.
- `-10^4 <= nums[i] <= 10^4` — small signed values. Values are only placed, never combined arithmetically, so overflow is not a concern.
- **`nums` is sorted in strictly increasing order.** Sortedness is what makes a BST valid for free: any split point puts smaller values on the left and larger on the right. Strictly increasing means all values are distinct, so no duplicate-handling rule is needed.
- **Any valid height-balanced BST is accepted** (Example 1 lists two different answers). That means ties, such as which middle to pick in an even-length range, do not matter.

## 2. Recognition Signals

- "Sorted array" + "build a BST" → an inorder traversal of a BST is sorted, so the array is exactly the inorder sequence of the tree you must produce.
- "Height-balanced" → the root must split the remaining elements into two halves of nearly equal size, so choose the **middle element** as the root.
- Tree construction where the left and right parts are independent sub-problems of the same shape → divide and conquer with recursion on index ranges.
- Whenever you are asked to build a balanced structure from sorted data, picking the median as the pivot and recursing on both sides is the standard move.

**Pattern:** Divide and conquer using the middle element as the root, recursing on the left and right sub-ranges.

## 3. Core Idea

Because the array is sorted, choosing any index `mid` as the root automatically satisfies the BST rule: everything to its left is smaller, and everything to its right is larger. To keep the tree balanced, choose `mid` so that the left part and right part have sizes that differ by at most 1. Then build the left subtree from the left part and the right subtree from the right part using exactly the same rule.

**Invariant:** For a call on the range `[left, right]`, the returned subtree contains exactly `nums[left..right]`, is a valid BST, and has left and right subtrees whose node counts differ by at most 1. Since the sizes of the two halves differ by at most 1, the heights of the two subtrees differ by at most 1 too, so the tree is height-balanced at every level by induction.

## 4. Approach 1 — Brute Force

**Logic:** The naive first idea is to insert the elements one by one into an ordinary BST, in the order given.

```java
public TreeNode sortedArrayToBST(int[] nums) {
    TreeNode root = null;
    for (int value : nums) {
        root = insert(root, value);
    }
    return root;
}

private TreeNode insert(TreeNode root, int value) {
    TreeNode node = new TreeNode(value);
    if (root == null) {
        return node;
    }

    TreeNode current = root;
    while (true) {
        if (value < current.val) {
            if (current.left == null) {
                current.left = node;
                break;
            }
            current = current.left;
        } else {
            if (current.right == null) {
                current.right = node;
                break;
            }
            current = current.right;
        }
    }
    return root;
}
```

- **Time:** O(n²). Each value is larger than everything inserted before it, so every insertion walks down the entire right chain: 0 + 1 + 2 + ... + (n - 1) steps.
- **Space:** O(n) for the tree itself. The insertion is iterative, so there is no recursion stack.

**Why it is not optimal:** It is not just slow, it is **wrong for this problem**. Inserting sorted values produces a right-leaning chain of height `n`, which is a valid BST but is nowhere near height-balanced, so it violates the problem's requirement. It also wastes quadratic time. The fix is to stop inserting in order and instead decide the shape up front by choosing the middle element as the root.

## 5. Approach 2 — Optimized

**Algorithm:**
1. Define `build(nums, left, right)` that returns the root of a balanced BST for `nums[left..right]`.
2. If `left > right`, the range is empty, so return `null`.
3. Compute `mid = left + (right - left) / 2` and create a node holding `nums[mid]`.
4. Set its left child to `build(nums, left, mid - 1)` and its right child to `build(nums, mid + 1, right)`.
5. Return the node. The answer is `build(nums, 0, nums.length - 1)`.

```java
public TreeNode sortedArrayToBST(int[] nums) {
    return build(nums, 0, nums.length - 1);
}

private TreeNode build(int[] nums, int left, int right) {
    if (left > right) {
        return null;
    }

    int mid = left + (right - left) / 2;
    TreeNode root = new TreeNode(nums[mid]);
    root.left = build(nums, left, mid - 1);
    root.right = build(nums, mid + 1, right);
    return root;
}
```

- **Time:** O(n). Each element becomes exactly one node, with O(1) work per call. In total there are `n` calls that create nodes and `n + 1` calls that hit the empty-range base case.
- **Space:** O(log n) for the recursion stack. Halving the range at every step bounds the depth by about `log2(n + 1)`, at most 14 for `n = 10^4`. The output tree itself takes O(n) and is required regardless.

**Why this is optimal:** Every element must be turned into a node, so O(n) time is a lower bound. The tree must be built anyway, so the only extra memory is the recursion stack, which is logarithmic because the tree is balanced. Passing index bounds avoids copying sub-arrays: slicing with `Arrays.copyOfRange` at every call would add about O(n log n) of copying work for no benefit.

## 6. Dry Run

Example: `nums = [-10, -3, 0, 5, 9]`. Chosen because the range has an odd size at the top and splits into two even-size ranges below, so it exercises the middle choice and an even-length split. The calls that create nodes are shown, and empty ranges return `null`.

| Step | Call range `[left, right]` | mid | Node created | Left range | Right range |
|------|----------------------------|-----|--------------|------------|-------------|
| 1 | [0, 4] | 2 | 0 | [0, 1] | [3, 4] |
| 2 | [0, 1] | 0 | -10 | [0, -1] (empty) | [1, 1] |
| 3 | [1, 1] | 1 | -3 | [1, 0] (empty) | [2, 1] (empty) |
| 4 | [3, 4] | 3 | 5 | [3, 2] (empty) | [4, 4] |
| 5 | [4, 4] | 4 | 9 | [4, 3] (empty) | [5, 4] (empty) |

Exit condition: all ranges are exhausted; every empty range returned `null` and every created node has been attached to its parent.

**Final answer:** the tree `0` with left child `-10` (whose right child is `-3`) and right child `5` (whose right child is `9`), i.e. `[0,-10,5,null,-3,null,9]`. This is the alternative answer listed in the problem's Example 1 explanation, and it is height-balanced: both subtrees of the root have height 2. The first example output `[0,-3,9,-10,null,5]` differs only because it picks the upper middle in even-length ranges.

## 7. Complexity Summary

| Approach | Time | Space (auxiliary) | Notes |
|----------|------|-------------------|-------|
| 1. Insert values in order | O(n²) | O(1) beyond the tree | Produces a chain; violates height-balance |
| 2. Middle as root, recurse on ranges | O(n) | O(log n) recursion stack | Optimal; balanced by construction |

The output tree is O(n) and is required by both approaches, so it is not counted as auxiliary space. The input array is used read-only.

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|------|------------------|--------------------------|
| Single element (`n = 1`) | Smallest legal input | `build(0, 0)` makes one node and both child ranges are empty, so it returns a single-node tree |
| Two elements (`n = 2`, Example 2) | Even-length range: there are two possible middles | `mid = left + (right - left) / 2` picks the lower one, giving root `1` with right child `3`; the upper-middle answer is also accepted, so either is valid |
| Perfectly balanced size (`n = 2^k - 1`, e.g. 3 or 7) | Every level is full, which is the easy case | Each split leaves two equal halves, producing a perfect tree |
| Duplicates / all-same values | Duplicates would make the BST rule ambiguous | Not possible: the input is strictly increasing, so all values are distinct and no tie rule is needed |
| Negative numbers | Negative values may suggest special handling | Values are never compared or combined; only their order in the array matters, so negatives are placed like any other value |
| Extreme values (`-10^4` and `10^4`) | Might raise overflow concerns | Nothing is computed from values, and `mid` uses only indices, so there is no overflow |
| Maximum size (`n = 10^4`) | Checks efficiency and recursion depth | O(n) time, and depth is about 14 because ranges halve each step |
| Empty range reached in recursion | Must not create nodes or index out of bounds | The `left > right` base case returns `null` before `nums[mid]` is accessed |

## 9. Java Notes

- **`mid = left + (right - left) / 2`:** the overflow-safe form of the midpoint. It is not strictly needed here because indices are at most `10^4`, but it is a good habit, and it picks the lower middle for even-length ranges.
- **Pass `left` and `right` indices, not sub-arrays:** `Arrays.copyOfRange` creates a new array on every call, adding about O(n log n) total copying. Index ranges give O(1) per call.
- **Any valid answer is accepted:** the judge checks that the result is a height-balanced BST with the right values, not that it matches one specific tree, so lower-middle and upper-middle both pass.
- **Recursion depth** is about `log2(n)`, so there is no stack-overflow risk. The naive insertion approach, by contrast, would reach depth `n` if written recursively, which is another reason to avoid it.
- **`TreeNode` is a plain class with `val`, `left`, and `right`:** the constructor `new TreeNode(x)` sets only the value, with both children `null`.

## 10. Common Mistakes

- **Inserting the sorted values one at a time into a BST.** It silently yields a right-leaning chain of height `n`. That is a valid BST but not height-balanced, so it fails the requirement, and it takes O(n²) time. Fix: pick the middle element as the root and recurse on both halves.
- **Using `left >= right` as the base case.** This returns `null` for single-element ranges and silently drops those values from the tree, so the tree has fewer than `n` nodes. Fix: the empty range is `left > right`; a range with `left == right` still holds one value and must create a node.
- **Choosing the first (or last) element as the root of each range.** This also yields a chain, because one side is always empty. Fix: always choose the middle index so the halves have nearly equal size.
- **Copying sub-arrays at every recursive call.** It is correct but adds O(n log n) copying and extra memory. Fix: pass `left` and `right` indices into the original array.

## 11. Interview Takeaway

- **Trigger sentence to memorize:** "Sorted array to height-balanced BST → pick the middle element as the root, then recursively build the left subtree from the left half and the right subtree from the right half."
- **How to explain it aloud in 90 seconds:** "The array is sorted, which is exactly the inorder sequence of a BST. So if I pick any element as the root, everything to its left is smaller and everything to its right is larger, and the BST property holds automatically. To make the tree height-balanced, I choose the middle element so the left and right halves have sizes that differ by at most one. I then solve the same problem on the left half and the right half. I pass index bounds instead of copying sub-arrays, and the base case is an empty range, where I return null. Each element becomes one node, so the time is O(n), and since the ranges halve at each level, the recursion depth is only about log n, so extra space is O(log n). I'd avoid inserting the values one by one because sorted input produces a chain with quadratic time, and a chain is not balanced."
- **Related problems using this pattern:**
  - LeetCode 109 — Convert Sorted List to Binary Search Tree
  - LeetCode 110 — Balanced Binary Tree
  - LeetCode 105 — Construct Binary Tree from Preorder and Inorder Traversal
  - LeetCode 98 — Validate Binary Search Tree

## 12. Recall Questions

**Q:** Why does choosing the middle element as the root keep the tree height-balanced?
**A:** The two sub-ranges then have sizes that differ by at most one, so by induction the two subtrees have nearly equal node counts and therefore heights that differ by at most one at every node.

**Q:** Why is the resulting tree automatically a valid BST?
**A:** The array is sorted and strictly increasing, so everything left of the middle index is smaller than the root and everything right of it is larger, and the same holds recursively inside each sub-range.

**Q:** Why does inserting the sorted values one by one fail this problem?
**A:** Each new value is larger than all previous ones, so it is always attached at the end of the right chain. The tree is a chain of height `n`, which is not height-balanced, and the insertions cost O(n²) in total.

**Q:** Why do both the lower middle and the upper middle work for an even-length range?
**A:** Either choice leaves sub-ranges of sizes differing by one, so both give height-balanced trees, and the problem accepts any valid answer.

**Q:** What should the base case be, and what goes wrong with `left >= right`?
**A:** The base case is the empty range `left > right`, returning `null`. Using `left >= right` also returns `null` for a one-element range, which silently drops that value.

**Q:** What is the recursion depth, and why?
**A:** About `log2(n + 1)`, since the range is roughly halved at each level and the tree is balanced. For `n = 10^4` that is at most 14.

## 13. Final Code

```java
public TreeNode sortedArrayToBST(int[] nums) {
    return build(nums, 0, nums.length - 1);
}

private TreeNode build(int[] nums, int left, int right) {
    if (left > right) {
        return null;
    }

    int mid = left + (right - left) / 2;
    TreeNode root = new TreeNode(nums[mid]);
    root.left = build(nums, left, mid - 1);
    root.right = build(nums, mid + 1, right);
    return root;
}
```

## 14. Self-Test

You have an integer array sorted in strictly increasing order. Build a height-balanced binary search tree that contains exactly those values. Think about which element makes the BST rule hold for free and keeps the two halves nearly equal, and what the base case of your recursion should be.
