---
title: "0230 — Kth Smallest Element in a BST"
layout: default
parent: Binary Search Tree
grand_parent: Leetcode
nav_order: 230
permalink: /11-Binary-Search-Tree/0230-kth-smallest-element-in-a-bst/

problem: 230 - Kth Smallest Element in a BST
url: https://leetcode.com/problems/kth-smallest-element-in-a-bst/
difficulty: Medium
section: Binary Search Tree
patterns: [inorder-traversal, early-termination]
data_structures: [binary-search-tree, stack]
time: O(h + k)
space: O(h)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0230 — Kth Smallest Element in a BST

| Field | Value |
|---|---|
| **Pattern** | Inorder Traversal with Early Termination |
| **Difficulty** | Medium |
| **Section** | Binary Search Tree |
| **Time** | O(h + k) — h = tree height |
| **Space** | O(h) — stack for iterative inorder |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Meta |
| **LeetCode** | [Kth Smallest Element in a BST](https://leetcode.com/problems/kth-smallest-element-in-a-bst/) |

## 1. Problem in Simple Words

**Given:** The root of a binary search tree (BST) and an integer `k`.

**Required:** Return the `k`th smallest value among all node values, where `k` is 1-indexed (`k = 1` means the smallest value).

**Constraints that actually matter:**
- `1 <= k <= n <= 10^4` — `k` is always valid, so you never need to handle "k larger than the number of nodes". With up to `10^4` nodes, even O(n log n) passes, but the problem clearly intends a solution that uses the BST property.
- `0 <= Node.val <= 10^4` — values are small and non-negative; nothing numeric to worry about, no overflow.
- **The tree is a BST.** An inorder traversal of a BST visits values in ascending order, so the `k`th smallest value is simply the `k`th node visited by an inorder traversal. That means you can stop the traversal as soon as you reach it.
- **The follow-up** asks what to do if the tree is modified often and `k`th smallest is queried frequently. That hints that re-traversing from scratch on every query is too slow in that scenario, and that extra bookkeeping stored in the nodes is the intended direction (covered in the Interview Takeaway).

## 2. Recognition Signals

- "`k`th smallest" + "BST" → inorder traversal. Inorder on a BST is already sorted, so no sorting is needed.
- "`k`th" with a small bound suggests you do not need the whole traversal; a traversal that **stops after `k` visits** is better than one that collects everything first.
- A follow-up about frequent inserts, deletes, and queries is the standard cue for an **order-statistic tree**: each node stores the size of its subtree so a rank query descends one path instead of walking the tree.

**Pattern:** Inorder traversal with early termination. Walk the BST in ascending order, count visits, and return at the `k`th one.

## 3. Core Idea

A BST has the property that, for every node, everything in its left subtree is smaller and everything in its right subtree is larger. Visiting left subtree, then node, then right subtree (inorder) therefore enumerates values from smallest to largest. The `k`th smallest value is the `k`th node visited.

To avoid wasted work, do the inorder traversal iteratively with an explicit stack: push the whole left spine, pop a node (that is the next smallest value), decrement a counter, and if the counter hits zero return that node's value. Otherwise move to the popped node's right child and repeat.

**Invariant:** Each time a node is popped, it is the next smallest value not yet counted, because everything smaller than it has already been popped (its left subtree, and all earlier ancestors and their left subtrees). After `j` pops, exactly the `j` smallest values have been counted.

## 4. Approach 1 — Brute Force (Collect All Values, Sort, Index)

**Logic:** Traverse the tree in any order to collect all values into a list, sort the list, and return the element at index `k - 1`.

```java
public int kthSmallest(TreeNode root, int k) {
    List<Integer> values = new ArrayList<>();
    collect(root, values);
    Collections.sort(values);
    return values.get(k - 1);
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

- **Time:** O(n log n) because of the sort, plus O(n) to collect the values.
- **Space:** O(n) for the list, plus O(h) for the recursion stack.

**Why it is not optimal:** It works for any tree, which means it throws away the BST property. Sorting is unnecessary because an inorder traversal already produces sorted order, and it always visits all `n` nodes even when `k` is small. (A middle-ground version collects values with an inorder traversal so no sort is needed. That is O(n) time but still builds the whole list.)

## 5. Approach 2 — Optimized (Iterative Inorder with Early Exit)

**Algorithm:**
1. Create an empty stack and set `current = root`.
2. While `current != null` or the stack is not empty:
   - Push `current` and move left until `current` is `null` (walk the left spine).
   - Pop the top node into `current`. This is the next smallest value.
   - Decrement `k`. If `k == 0`, return `current.val`.
   - Move to `current.right` and continue.
3. Because `k <= n`, the loop always returns before it ends.

```java
public int kthSmallest(TreeNode root, int k) {
    Deque<TreeNode> stack = new ArrayDeque<>();
    TreeNode current = root;

    while (current != null || !stack.isEmpty()) {
        while (current != null) {
            stack.push(current);
            current = current.left;
        }

        current = stack.pop();
        k--;
        if (k == 0) {
            return current.val;
        }

        current = current.right;
    }

    throw new IllegalArgumentException("k is larger than the number of nodes");
}
```

- **Time:** O(h + k). Reaching the first (smallest) node costs `h` pushes, and then each of the `k` pops is followed by at most one more descent; across the whole run the number of pushes and pops is bounded by `h + k`-ish. In the worst case (`k = n`) this is O(n).
- **Space:** O(h) for the explicit stack, where `h` is the tree height: O(log n) for a balanced tree and O(n) for a completely skewed one.

**Why this is optimal:** You must visit at least `k` nodes to know the `k`th smallest value without extra bookkeeping in the tree, so O(k) work is a lower bound, plus the unavoidable descent to the leftmost node. The stack is the minimum state an inorder traversal needs. Compared with Approach 1, it never sorts, never stores all values, and stops early. A recursive inorder with a shared counter is equally correct but uses the call stack; the iterative form makes the early exit simple and avoids deep-recursion concerns.

## 6. Dry Run

Example: `root = [5,3,6,2,4,null,null,1]`, `k = 3`. Chosen because the answer is deep in the left subtree, so it exercises the left-spine descent and shows that early exit skips the right side.

The tree: `5` has left child `3` (with left child `2` that has left child `1`, and right child `4`) and right child `6`. Inorder order: `1, 2, 3, 4, 5, 6`, so the 3rd smallest is `3`.

Initial: `stack = []` (bottom to top), `current = 5`, `k = 3`.

| Step | Action | Stack after (bottom → top) | current after | k after |
|------|--------|----------------------------|---------------|---------|
| 1 | Walk left spine: push 5, 3, 2, 1 | [5, 3, 2, 1] | null | 3 |
| 2 | Pop 1 (visit); k decremented; k is not 0; go to 1.right | [5, 3, 2] | null | 2 |
| 3 | Left walk does nothing (current is null); pop 2; k decremented; go to 2.right | [5, 3] | null | 1 |
| 4 | Pop 3; k decremented to 0 → return 3 | [5] | 3 | 0 |

Exit condition: `k == 0` after popping node `3`.

**Final answer:** `3`, matching the expected output. Nodes `4`, `5`, and `6` were never visited, which shows the benefit of early termination.

## 7. Complexity Summary

| Approach | Time | Space (auxiliary) | Notes |
|----------|------|-------------------|-------|
| 1. Collect all values, sort, index | O(n log n) | O(n) list + O(h) stack | Ignores BST property and early exit |
| 2. Iterative inorder, stop at k | O(h + k) | O(h) stack | Optimal without augmenting the tree |

The input tree takes O(n) space in both; only auxiliary space is counted above. With a tree augmented with subtree sizes (the follow-up), a query costs O(h) regardless of `k`.

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|------|------------------|--------------------------|
| `k = 1` (smallest value) | The answer is the leftmost node, so the traversal should stop right after descending the left spine | The first pop is the leftmost node; `k` becomes 0 immediately and the method returns without visiting anything else |
| `k = n` (largest value) | The traversal must visit every node and return the last one | `k` reaches 0 only at the final pop, so all nodes are processed; the final `throw` is never reached because `k <= n` |
| Single-node tree (`n = 1`, `k = 1`) | Smallest possible input | The spine walk pushes the root, pops it, `k` becomes 0, and the root's value is returned |
| Completely left-skewed tree (a chain of left children) | The stack holds all `n` nodes, so space is O(n) | The algorithm is still correct; stack depth is bounded by `n`, and an explicit stack avoids recursion-depth problems |
| Completely right-skewed tree | The left spine is a single node each time, so the stack stays tiny | Each iteration pushes one node, pops it, and moves right, giving O(1) stack and O(k) time |
| Node value `0` | A careless "nothing seen yet" sentinel could confuse a real `0` | No sentinel is used; the algorithm counts pops with `k`, so `0` is just a value |
| Off-by-one on `k` (1-indexed) | Returning at `k == 1` or decrementing after the check shifts the answer by one | `k` is decremented first and the check is `k == 0`, so `k = 1` returns the very first popped (smallest) value |

## 9. Java Notes

- **Use `ArrayDeque` as the stack, not `java.util.Stack`.** `Stack` is a legacy synchronized class built on `Vector`. With `ArrayDeque`, `push` and `pop` operate on the front and are O(1).
- **Reusing the parameter `k` is safe here.** The method is iterative, so `k--` modifies only the local copy. In a recursive version, a primitive counter must live in a field (or a one-element array), because a by-value parameter would not carry updates between calls.
- **Recursive alternative with a shared counter:** a field `count` incremented during the inorder visit, with a field `result` set when `count == k`, is correct but visits no fewer nodes unless you also add an early-return check; the iterative form makes early exit natural.
- **No overflow or arithmetic:** values are only returned, never combined. `int` is enough for `k` and the counter.
- **Why the final `throw`:** Java requires all paths to return a value. The constraints guarantee `1 <= k <= n`, so it is unreachable, and throwing documents the impossible case instead of silently returning a wrong value.

## 10. Common Mistakes

- **Using a traversal order other than inorder.** Pre-order and post-order do not visit a BST in sorted order, so the `k`th visited node is not the `k`th smallest. Fix: always go left, visit, then go right.
- **Off-by-one on the count.** The problem is 1-indexed. Returning when the counter reaches `k` after an extra increment, or checking before decrementing, returns the `(k+1)`th or `(k-1)`th value silently. Fix: decrement `k` after each pop and return exactly when it hits `0`, or count visits and return when `count == k`.
- **Forgetting to move to the right child after popping.** Without `current = current.right`, the loop keeps popping stack nodes and never explores right subtrees, so larger values in the right subtree are skipped and the count is wrong. Fix: after handling the popped node, set `current = current.right`.
- **Collecting everything and sorting even though the tree is a BST.** It passes but costs O(n log n) and misses the intended insight. Fix: rely on inorder order and stop at the `k`th node.

## 11. Interview Takeaway

- **Trigger sentence to memorize:** "`k`th smallest in a BST → inorder traversal gives sorted order, so count nodes as you visit them and stop at the `k`th."
- **How to explain it aloud in 90 seconds:** "Because it's a BST, an inorder traversal, left then node then right, visits the values in ascending order, so the `k`th smallest is just the `k`th node I visit. I do this iteratively with a stack. I push every node along the left spine, pop the top, which is the next smallest value, and decrement `k`. When `k` reaches zero I return that node's value. If not, I move to its right child and repeat. I don't need to sort or store all the values, and I stop as soon as I find the answer, so the time is the height to reach the leftmost node plus `k` steps, O(h + k), and the extra space is the stack, O(h)."
- **Follow-up (frequent inserts, deletes, and queries):** "I would augment each node with the size of its subtree. Then a query at a node compares `k` with the left subtree's size: if `k` is at most the left size, go left; if `k` equals the left size plus one, the current node is the answer; otherwise subtract the left size plus one from `k` and go right. That makes each query O(h) without a full traversal. Inserts and deletes just update the stored sizes on the path from the root, also O(h). To keep `h` small under many updates, I would use a self-balancing tree, so queries and updates are O(log n)."
- **Related problems using this pattern:**
  - LeetCode 94 — Binary Tree Inorder Traversal
  - LeetCode 98 — Validate Binary Search Tree
  - LeetCode 530 — Minimum Absolute Difference in BST
  - LeetCode 173 — Binary Search Tree Iterator

## 12. Recall Questions

**Q:** Why does the `k`th node visited by an inorder traversal equal the `k`th smallest value in a BST?
**A:** In a BST every node's left subtree holds smaller values and its right subtree holds larger values, so visiting left, node, right enumerates all values in ascending order.

**Q:** Why can the iterative solution stop early, and what does that save?
**A:** Once the `k`th value is popped it is the answer, since every smaller value has already been counted. Nodes later in sorted order are never visited, so with a small `k` the work is O(h + k) instead of O(n).

**Q:** What would happen if you forgot to set `current = current.right` after popping a node?
**A:** The traversal would never enter right subtrees, so values larger than a popped node (but within its right subtree) would be skipped, and the counted order would not be the true sorted order.

**Q:** How do you avoid an off-by-one error with a 1-indexed `k`?
**A:** Decrement `k` right after each pop and return when it equals `0`. Then `k = 1` returns the first popped node, the smallest value.

**Q:** For the follow-up with frequent updates, what extra information makes `k`th smallest fast, and how is a query answered?
**A:** Store each node's subtree size. At each node compare `k` with the size of the left subtree: go left if `k` is at most that size, return the node if `k` is one more than that size, otherwise subtract and go right. One root-to-node path gives O(h) per query.

**Q:** Why prefer `ArrayDeque` over `Stack` for the explicit stack in Java?
**A:** `Stack` is a legacy synchronized class built on `Vector`, while `ArrayDeque` is a modern, unsynchronized deque with O(1) `push` and `pop`.

## 13. Final Code

```java
public int kthSmallest(TreeNode root, int k) {
    Deque<TreeNode> stack = new ArrayDeque<>();
    TreeNode current = root;

    while (current != null || !stack.isEmpty()) {
        while (current != null) {
            stack.push(current);
            current = current.left;
        }

        current = stack.pop();
        k--;
        if (k == 0) {
            return current.val;
        }

        current = current.right;
    }

    throw new IllegalArgumentException("k is larger than the number of nodes");
}
```

## 14. Self-Test

You have the root of a binary search tree and an integer `k` (1-indexed). Return the `k`th smallest node value. Think about which traversal order of a BST yields sorted values, how to stop as soon as you have the `k`th one, and what extra per-node information would make repeated queries fast if the tree changes often.
