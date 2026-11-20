---
title: "0236 — Lowest Common Ancestor of a Binary Tree"
layout: default
parent: Binary Tree General
grand_parent: Leetcode
nav_order: 236
permalink: /09-Binary-Tree-General/0236-lowest-common-ancestor-of-a-binary-tree/

problem: 236 - Lowest Common Ancestor of a Binary Tree
url: https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-tree/
difficulty: Medium
section: Tree / DFS
patterns: [tree-recursion, path-finding, bottom-up-lca]
data_structures: [tree, hashmap]
time: O(n)
space: O(h)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0236 — Lowest Common Ancestor of a Binary Tree

| Field | Value |
|---|---|
| **Pattern** | Bottom-Up LCA via Tree Recursion |
| **Difficulty** | Medium |
| **Section** | Tree / DFS |
| **Time** | O(n) |
| **Space** | O(h) — h = tree height |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Meta |
| **LeetCode** | [Lowest Common Ancestor of a Binary Tree](https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-tree/) |

## 1. Problem in Simple Words

**Given:** the root of a binary tree and two nodes `p` and `q` known to exist in it.

**Required:** find their lowest common ancestor (LCA) — the deepest node that has both `p` and `q` as descendants, where a node **is considered a descendant of itself** (so if `p` is itself an ancestor of `q`, then `p` is the LCA, as shown in Example 2).

**Constraints that matter:**
- `2 <= number of nodes <= 10^5` — a moderately large tree; an O(n) solution is expected to comfortably handle this, but anything significantly worse (like repeated per-query path searches without reuse) would need to be used carefully.
- **All `Node.val` are unique**, and `p != q`, and **both `p` and `q` are guaranteed to exist in the tree** — this removes the need to search for or validate the existence of either node; the algorithm can assume both will be found somewhere during traversal.
- **"A node can be a descendant of itself"** — this specific clarification (highlighted directly in the problem, with Example 2 built specifically to illustrate it) means the LCA could literally be `p` or `q` itself, not just some node strictly above both — a detail easy to overlook if the implicit definition of "ancestor" is assumed to be strict (excluding self).
- The tree here is a **general binary tree, not a binary search tree** — there's no ordering property (like "left subtree is smaller") to exploit for navigation; the only way to determine relationships between nodes is through actual traversal of the tree's structure.

## 2. Recognition Signals

- "Lowest common ancestor in a general (non-BST) binary tree" → rules out the simpler BST-specific technique (which can use value comparisons to decide whether to go left or right without ever needing to actually search both subtrees) — in a general tree, there's no shortcut based on values alone, so an **actual search through the structure** is required.
- Needing to find a node that has **both** targets somewhere beneath it (or is one of the targets itself) → the natural recursive framing: a node is a candidate ancestor if searching its left subtree finds one target and its right subtree finds the other (meaning this node is exactly the "meeting point"), or if the node itself is one of the two targets and the other is found somewhere in its subtree.
- The elegant insight that makes this solvable in a single pass: **a recursive call on a subtree can simply report "found `p`," "found `q`," "found both (meaning this subtree's return value already IS the answer)," or "found neither"** — propagating this information upward naturally identifies the LCA as the first point where both targets' signals converge.
- Named pattern: **Bottom-Up Recursive Search, Combining Child Results to Identify the Meeting Point**.

## 3. Core Idea

Recursively search the tree. At each node: if the node itself is `null`, or matches `p`, or matches `q`, return that node directly (a `null` means "not found here," while finding `p` or `q` is itself useful information to propagate upward). Otherwise, recursively search the left and right subtrees. If **both** searches return non-null results, this means `p` was found in one subtree and `q` in the other (or one of them was this very node, propagated up) — so **this node itself is the LCA**, since it's the point where paths to both targets diverge. If only **one** side returns non-null, propagate that result upward unchanged (both targets must be on that same side, so the actual LCA is somewhere further down that path, and whatever was found there is exactly what should continue being passed up). If **neither** side finds anything, return `null`.

**Why it's correct:** the LCA is, by definition, the deepest node from which both `p` and `q` are reachable (with "reachable" including the node itself). A node's left and right subtrees are completely disjoint sets of descendants — so if `p` is found via the left recursive call and `q` is found via the right recursive call (in either order), this current node is provably the lowest point where both become reachable, since any node further down could only see one of the two targets, not both. If only one subtree reports finding something, that "something" (which is either `p`, `q`, or an already-resolved LCA bubbling up from deeper in the recursion) is exactly the correct value to keep propagating upward unchanged, since the true LCA — if it hasn't already been found — must still be somewhere along that path.

**Invariant:** every recursive call's return value is either `null` (neither `p` nor `q` exists anywhere in this subtree), a reference to `p` or `q` itself (exactly one of the two was found in this subtree, and the other has not yet been found as of this call), or a reference to the already-determined LCA (both have been found, and this subtree's call has already identified their precise meeting point) — and this three-way distinction is exactly what allows each parent call to correctly combine its children's results.

## 4. Approach 1 — Brute Force (Find Both Root-to-Node Paths, Compare)

1. Perform a DFS from the root to find the complete path (sequence of nodes) from the root down to `p`, storing it in a list.
2. Perform a second DFS to find the complete path from the root down to `q`, storing it in another list.
3. Walk both paths simultaneously from the start (the root); the last node at which both paths still agree (before they diverge, or before one path ends) is the LCA.

```java
public TreeNode lowestCommonAncestorBruteForce(TreeNode root, TreeNode p, TreeNode q) {
    List<TreeNode> pathToP = new ArrayList<>();
    List<TreeNode> pathToQ = new ArrayList<>();

    findPath(root, p, new ArrayList<>(), pathToP);
    findPath(root, q, new ArrayList<>(), pathToQ);

    TreeNode lca = null;
    int i = 0;
    while (i < pathToP.size() && i < pathToQ.size() && pathToP.get(i) == pathToQ.get(i)) {
        lca = pathToP.get(i);
        i++;
    }

    return lca;
}

private boolean findPath(TreeNode node, TreeNode target, List<TreeNode> currentPath, List<TreeNode> resultPath) {
    if (node == null) {
        return false;
    }

    currentPath.add(node);

    if (node == target) {
        resultPath.addAll(currentPath);
        currentPath.remove(currentPath.size() - 1);
        return true;
    }

    if (findPath(node.left, target, currentPath, resultPath) ||
        findPath(node.right, target, currentPath, resultPath)) {
        currentPath.remove(currentPath.size() - 1);
        return true;
    }

    currentPath.remove(currentPath.size() - 1);
    return false;
}
```

**Time:** O(n) — each `findPath` call is a single DFS traversal, O(n) in the worst case; running it twice is still O(n) overall.
**Space:** O(n) worst case — storing two full root-to-node paths, each of which can be as long as O(h) (and `h` itself can be O(n) for a skewed tree), plus the temporary `currentPath` lists used during the search, plus the usual O(h) recursion stack.

This is correct and still O(n) time, but it's less elegant and uses more auxiliary space than necessary: it performs two entirely separate traversals and explicitly materializes two full path lists, when the same result can be achieved in a **single** traversal pass with no extra path-storage structure at all.

## 5. Approach 2 — Optimized (Single-Pass Bottom-Up Recursion)

1. Base case: if `root == null`, or `root == p`, or `root == q`, return `root` directly.
2. Recursively search: `left = lowestCommonAncestor(root.left, p, q)`, `right = lowestCommonAncestor(root.right, p, q)`.
3. If both `left != null` and `right != null`, this node is the LCA — return `root`.
4. If exactly one of `left`/`right` is non-null, return that one (propagate it upward).
5. If both are `null`, return `null`.

```java
class Solution {
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || root == p || root == q) {
            return root;
        }

        TreeNode left = lowestCommonAncestor(root.left, p, q);
        TreeNode right = lowestCommonAncestor(root.right, p, q);

        if (left != null && right != null) {
            return root;
        }

        return (left != null) ? left : right;
    }
}
```

**Time:** O(n) — a single traversal of the tree, visiting each node exactly once in the worst case (no early termination is strictly guaranteed in general, since both `p` and `q` could be deep in different branches requiring the full tree to be explored, though in practice some pruning can occur once both are found).
**Space:** O(h) for the recursion call stack, where `h` is the tree's height — O(log n) for a balanced tree, up to O(n) for a completely skewed one; no auxiliary path-storage structures are needed at all.

This is optimal: it achieves the same O(n) time as the brute force in a **single** traversal pass, with no explicit path lists or auxiliary storage beyond the call stack itself — the recursive return-value propagation technique elegantly encodes all the necessary "have I found `p`, `q`, or both" information without needing to separately track or compare full paths.

## 6. Dry Run

`root = [3,5,1,6,2,0,8,null,null,7,4]`, `p = 5`, `q = 1` (tree: `3` has children `5,1`; `5` has children `6,2`; `2` has children `7,4`; `1` has children `0,8`)

`lowestCommonAncestor(3, 5, 1)`: `3` is not `null`/`5`/`1`, so recurse:
- `left = lowestCommonAncestor(5, 5, 1)`: `5 == p`, return `5` directly (no further recursion into 5's own children needed).
- `right = lowestCommonAncestor(1, 5, 1)`: `1 == q`, return `1` directly.

Back at `3`: `left = 5` (non-null), `right = 1` (non-null) → both non-null, so `3` **is** the LCA. Return `3`.

Final answer: `3`, matching the expected output (Example 1).

For Example 2 (`p=5, q=4`, where `4` is a descendant of `5`): `lowestCommonAncestor(3,5,4)` recurses; `left = lowestCommonAncestor(5,5,4)`: `5==p`, returns `5` immediately (notably, **without** even needing to check whether `4` is somewhere inside `5`'s own subtree, since the base case triggers on matching `p` directly, short-circuiting further search into that branch). `right = lowestCommonAncestor(1,5,4)`: neither `5` nor `4` exists anywhere under `1`, so this returns `null`. Back at `3`: `left=5` (non-null), `right=null` → exactly one non-null, so propagate `left` upward: return `5`. Final answer: `5`, correctly matching Example 2 (since `5` is an ancestor of `4`, and a node counts as its own descendant/ancestor).

## 7. Complexity Summary

| Approach | Time | Space | Notes |
|---|---|---|---|
| Find Both Root-to-Node Paths, Compare | O(n) | O(n) worst case (two stored paths + recursion) | Two separate traversals, explicit path storage |
| Single-Pass Bottom-Up Recursion | O(n) | O(h) | One traversal, no auxiliary path storage |

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|---|---|---|
| `p` is an ancestor of `q` (or vice versa), as in Example 2 | The LCA is `p` (or `q`) itself, not some node strictly above it | The base case `root == p` (or `== q`) returns immediately upon finding either target, without needing to search further into that specific branch for the other — this correctly short-circuits and propagates the found node upward as the answer |
| `p` and `q` are in completely different subtrees (one under `root.left`, the other under `root.right`) | The LCA is their common ancestor where the paths diverge | Both the `left` and `right` recursive calls return non-null (one found `p`, the other found `q`), so the current node is correctly identified as the LCA |
| `p` and `q` are both the root's direct children with the root as their only common ancestor (minimal 3-node-deep case) | Simple, shallow structure | Works identically to deeper cases — the root's own `left`/`right` recursive calls immediately find `p` and `q` as direct matches, and the root correctly returns itself |
| Two-node tree (`root=[1,2]`, `p=1, q=2`) | Minimal valid tree per constraints | `lowestCommonAncestor(1,1,2)`: `1==p`, returns `1` immediately — correctly identifying `1` as the LCA, since `1` is an ancestor of `2` (its direct child) |
| A node with only one child (asymmetric structure) | Must not assume every node has exactly two children | The recursive calls on `root.left`/`root.right` naturally handle a `null` child via the base case, correctly contributing `null` to that side without any special-casing needed |
| Very deep, skewed tree (worst-case height close to n) | Recursion depth could be substantial | The algorithm's O(h) space scales with height; for this problem's constraints (`n` up to `10^5`), a maximally skewed tree's recursion depth is a shared concern with any tree-recursion approach, not unique to this specific technique |

## 9. Java Notes

- `root == p` and `root == q` use **reference equality** (`==`), not value equality — this is both correct and important: the problem gives `p` and `q` as actual `TreeNode` references that exist within the tree, so comparing object identity is the precise, intended check (and happens to also be safe here since `Node.val` is guaranteed unique anyway, though identity comparison is the conceptually correct approach regardless).
- The ternary `(left != null) ? left : right` concisely handles both remaining cases after the "both non-null" check: if `left` is null, this returns `right` (which might itself be `null`, correctly representing "neither found" if `right` is also null) — this single line correctly covers both the "found only on the right" and "found on neither side" cases.
- No `HashMap`, `List`, or other auxiliary data structure is used anywhere in the optimized solution — the entire algorithm's "memory" of what's been found is carried implicitly through recursive return values alone, which is both elegant and exactly why it achieves O(h) rather than O(n) auxiliary space.
- This recursive pattern — "return early on a direct match, otherwise combine children's results to detect a convergence point" — is a reusable template applicable to several other tree problems beyond just LCA, wherever the goal is to find where two or more search targets "meet" within a tree structure.

## 10. Common Mistakes

- Treating "ancestor" as strictly excluding the node itself (i.e., assuming `p` can never be the answer even if `q` is somewhere in `p`'s own subtree) — this directly contradicts the problem's explicit definition and Example 2, which specifically demonstrates that a node counts as its own ancestor. Fix: always let the base case return immediately upon matching `p` or `q`, without additional logic trying to exclude this "self as ancestor" possibility.
- After finding that both `left` and `right` are non-null, continuing to search further or trying to determine "which one is deeper" — once both children report finding something, the current node is unconditionally the correct LCA; there's no need for, and no correct way to meaningfully interpret, any further comparison between `left` and `right`'s specific values at that point, since both are not being compared for equality or depth, merely checked for non-nullness. Fix: as soon as both sides are confirmed non-null, immediately return the current node.
- Using value comparisons (e.g., `root.val < p.val`) to decide which subtree to search, as would be appropriate for a binary **search** tree — this problem is explicitly a general binary tree with no ordering guarantee, so such comparisons have no valid meaning here and would produce incorrect navigation. Fix: always search **both** subtrees in a general binary tree LCA problem, never attempt to use value-based navigation shortcuts.
- Forgetting the `root == null` base case (only checking `root == p` and `root == q`) — this would throw a `NullPointerException` when the recursion naturally reaches the end of a branch (a null child) without having found either target along that path. Fix: always include `root == null` as part of the base case, returning `null` to correctly signal "not found along this path."

## 11. Interview Takeaway

- **Trigger sentence:** "Lowest common ancestor in a general binary tree (no BST ordering to exploit) → single-pass bottom-up recursion: return a direct match immediately, and recognize a node as the LCA when both its subtrees report finding something."
- **90-second explanation:** "I recurse through the tree, and at each node, if it's null or it directly matches p or q, I return it right away — this immediately handles the case where one of the targets turns out to be an ancestor of the other, since the problem explicitly says a node counts as its own ancestor. Otherwise, I search both the left and right subtrees. If both come back with something found, that means p showed up in one subtree and q in the other — which means this exact node is the point where their paths to the root diverge, making it the lowest common ancestor. If only one side found something, I just pass that result straight up, since the real answer must be somewhere further down that same branch, either already resolved or still waiting to be found. This all happens in a single pass with no extra data structures — the return values themselves carry all the information I need as they bubble back up."
- **Related problems:** 235 (Lowest Common Ancestor of a Binary Search Tree, the simpler ordered-navigation variant), 1650 (Lowest Common Ancestor III, with parent pointers instead of root-down search), 1644 (Lowest Common Ancestor II, where p or q might not exist in the tree).

## 12. Recall Questions

**Q:** Why does the base case need to check `root == p` and `root == q` directly, rather than only checking `root == null`?
**A:** The problem explicitly defines a node as being its own ancestor, so if one target node is itself an ancestor of the other, the correct answer is that node itself — returning immediately on a direct match is what correctly captures this case, rather than requiring the algorithm to somehow separately detect "is this node an ancestor of the other target" through some other means.

**Q:** Why does finding non-null results from both the left and right recursive calls specifically indicate that the current node is the LCA?
**A:** The left and right subtrees are entirely disjoint — no node exists in both — so if `p` was found via one side and `q` via the other, this current node is provably the first (lowest) point in the tree from which both targets become reachable, since any node deeper in either subtree could only ever see one of the two targets.

**Q:** Why is it correct to simply propagate whichever single non-null child result exists, without any further processing?
**A:** A non-null result from one side represents either an already-found target node or an already-resolved LCA from deeper in that subtree — in either case, the true answer necessarily lies somewhere along that same path, so passing that exact value upward unchanged is precisely what correctly continues the search toward identifying (or re-confirming) the final answer.

**Q:** Why can't this problem use the kind of value-based left/right navigation shortcut that works for a binary search tree's LCA problem?
**A:** A binary search tree's ordering guarantees that all values in a left subtree are smaller and all values in a right subtree are larger than the current node, allowing value comparisons to determine which single subtree to search; a general binary tree has no such guarantee, so both subtrees must always be searched, since there's no way to know in advance which one (if either alone) contains a given target.

**Q:** Why does this approach need no auxiliary data structures (like stored paths or a hash map), unlike some alternative LCA techniques?
**A:** All the necessary information — whether `p`, `q`, neither, or the already-resolved LCA has been found in a given subtree — is fully captured by the single return value of each recursive call, and this information naturally combines correctly as it propagates back up through the call stack, with no need to separately record or compare full paths.

## 13. Final Code

```java
/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode(int x) { val = x; }
 * }
 */
class Solution {
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || root == p || root == q) {
            return root;
        }

        TreeNode left = lowestCommonAncestor(root.left, p, q);
        TreeNode right = lowestCommonAncestor(root.right, p, q);

        if (left != null && right != null) {
            return root;
        }

        return (left != null) ? left : right;
    }
}
```

## 14. Self-Test

Given the root of a binary tree and two nodes `p` and `q` known to exist in it, find their lowest common ancestor (a node may be its own ancestor). Re-derive: recursively search; return the current node immediately if it's null or matches `p`/`q`; otherwise recurse into both children, and if both return non-null, the current node is the LCA; if only one side is non-null, propagate it upward unchanged.
