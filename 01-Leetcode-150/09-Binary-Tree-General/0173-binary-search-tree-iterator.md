---
title: "0173 — Binary Search Tree Iterator"
layout: default
parent: Binary Tree General
grand_parent: Leetcode
nav_order: 173
permalink: /09-Binary-Tree-General/0173-binary-search-tree-iterator/

problem: 173 - Binary Search Tree Iterator
url: https://leetcode.com/problems/binary-search-tree-iterator/
difficulty: Medium
section: Tree / Design / Stack
patterns: [controlled-inorder-traversal, explicit-stack, left-spine-pushing]
data_structures: [tree, stack]
time: O(1) amortized
space: O(h)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0173 — Binary Search Tree Iterator

| Field | Value |
|---|---|
| **Pattern** | Controlled Inorder Traversal with Explicit Stack |
| **Difficulty** | Medium |
| **Section** | Tree / Design / Stack |
| **Time** | O(1) amortized per `next()` |
| **Space** | O(h) — h = tree height |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Meta |
| **LeetCode** | [Binary Search Tree Iterator](https://leetcode.com/problems/binary-search-tree-iterator/) |

## 1. Problem in Simple Words

**Given:** a requirement to design a `BSTIterator` class that walks a binary search tree's nodes in **in-order** (ascending) sequence, exposing a pointer-style interface.

**Required:** `BSTIterator(TreeNode root)` initializes the iterator (conceptually positioned just before the smallest element); `hasNext()` reports whether more elements remain; `next()` advances the pointer and returns the next element in ascending order.

**Constraints that matter:**
- `1 <= number of nodes <= 10^5`, and up to `10^5` total calls to `hasNext`/`next` — a large combined workload, making per-call efficiency genuinely important, not just a theoretical concern.
- **"You may assume that `next()` calls will always be valid"** — there's no need for `next()` itself to defend against being called when nothing remains; that's what `hasNext()` is for, and the caller is trusted to check it first.
- **Follow-up: "Could you implement `next()` and `hasNext()` to run in average O(1) time and use O(h) memory, where h is the height of the tree?"** — this explicitly invites comparing a simple approach that precomputes the entire traversal up front (using O(n) memory) against a more careful approach that performs the traversal incrementally, using memory proportional only to the tree's height, not its total size.

## 2. Recognition Signals

- "Iterator over an in-order traversal, with `next()`/`hasNext()`" → the direct signal for implementing a **controlled, resumable traversal** — the core challenge is doing an in-order DFS *incrementally*, one step at a time across multiple method calls, rather than all at once.
- The explicit O(h)-memory follow-up (as opposed to O(n)) → strongly suggests using an **explicit stack that only ever holds the nodes along the current "left spine"** being explored, rather than materializing the full traversal sequence ahead of time — this is the standard technique for converting recursive in-order traversal into an iterative, pausable process using bounded memory.
- The recursive definition of in-order traversal (left subtree, then node, then right subtree) naturally maps onto a stack-based simulation: **push all left children down to the leftmost node first**, then each `next()` call pops one node (visiting it) and, if it has a right child, pushes that child's entire left spine before the next `next()` call.
- Named pattern: **Iterative In-Order Traversal via Explicit Stack (Controlled/Resumable DFS)**.

## 3. Core Idea

Maintain a stack that always holds exactly the ancestors (and the current candidate node) still needing their *own* visit, specifically along the "leftmost path not yet fully explored." At construction, push the root and then every left child down to the leftmost node — this puts the smallest element at the top of the stack, ready to be the first `next()` result. Each call to `next()` pops the top of the stack (this is the next in-order element), and then, if that popped node has a right child, pushes that right child and *its* entire left spine onto the stack — this correctly prepares the stack so its new top is whatever comes next in in-order sequence. `hasNext()` is simply a check of whether the stack is empty.

**Why it's correct:** in-order traversal visits a node's entire left subtree, then the node itself, then its entire right subtree. Maintaining the stack as "the path of nodes whose left subtrees have already been fully explored or don't exist, each one still awaiting its own visit" exactly mirrors the state a recursive in-order traversal would have on its call stack at the moment it's about to visit the node currently on top. Pushing a popped node's right child's left spine after visiting it correctly resumes the traversal into that node's right subtree, exploring *its* leftmost path first — precisely matching what recursive in-order traversal would do next.

**Invariant:** at any point between calls, the stack (from bottom to top) holds exactly the sequence of ancestors still pending their own visit along the current traversal position, with the top of the stack always being precisely the next node that `next()` should return — and the total number of nodes ever on the stack at once is bounded by the tree's height, since each stack entry corresponds to one step along a single root-to-node path.

## 4. Approach 1 — Non-Optimal for Memory (Precompute the Entire Traversal Up Front)

1. In the constructor, perform a complete in-order traversal of the entire tree (recursively or iteratively), storing every value into a `List<Integer>` (or array) in ascending order.
2. Maintain a simple integer index `pointer`, initialized to `-1` (representing "before the first element").
3. `hasNext()`: return `pointer + 1 < list.size()`.
4. `next()`: increment `pointer`, then return `list.get(pointer)`.

```java
class BSTIteratorBruteForce {
    private List<Integer> values = new ArrayList<>();
    private int pointer = -1;

    public BSTIteratorBruteForce(TreeNode root) {
        inorder(root);
    }

    private void inorder(TreeNode node) {
        if (node == null) {
            return;
        }
        inorder(node.left);
        values.add(node.val);
        inorder(node.right);
    }

    public boolean hasNext() {
        return pointer + 1 < values.size();
    }

    public int next() {
        pointer++;
        return values.get(pointer);
    }
}
```

**Time:** O(n) for the constructor's full traversal; `hasNext()` and `next()` are both O(1) per call thereafter.
**Space:** O(n) auxiliary for the fully materialized `values` list, held for the entire lifetime of the iterator — this does not satisfy the follow-up's O(h)-memory requirement.

This is correct and has excellent per-call time, but it fails the problem's explicit follow-up: it uses O(n) memory regardless of how many `next()` calls are ever actually made (even if the caller only ever calls `next()` once and stops, the entire tree was still traversed and stored up front) — the follow-up specifically asks for memory proportional to the tree's *height*, not its total size.

## 5. Approach 2 — Optimized (Explicit Stack, Incremental Left-Spine Pushing)

1. Maintain a `Deque<TreeNode> stack`.
2. In the constructor, call a helper `pushLeftSpine(root)` which pushes `root`, then `root.left`, then that node's `left`, and so on, until reaching a node with no left child — this puts the smallest element's node on top of the stack.
3. `hasNext()`: return `!stack.isEmpty()`.
4. `next()`: pop the top node (`current`) — this is the next in-order value. If `current.right != null`, call `pushLeftSpine(current.right)` to push that subtree's entire left spine onto the stack, preparing for subsequent calls. Return `current.val`.

```java
import java.util.*;

class BSTIterator {
    private Deque<TreeNode> stack = new ArrayDeque<>();

    public BSTIterator(TreeNode root) {
        pushLeftSpine(root);
    }

    private void pushLeftSpine(TreeNode node) {
        while (node != null) {
            stack.push(node);
            node = node.left;
        }
    }

    public boolean hasNext() {
        return !stack.isEmpty();
    }

    public int next() {
        TreeNode current = stack.pop();
        if (current.right != null) {
            pushLeftSpine(current.right);
        }
        return current.val;
    }
}
```

**Time:** O(1) **amortized** per `next()` call — although a single call to `pushLeftSpine` could, in the worst case, push many nodes at once (e.g., a long left spine), each individual node is pushed onto the stack exactly once and popped exactly once across the *entire* lifetime of the iterator; since there are at most n nodes total, the total work across all `next()` calls combined is O(n), making the *average* cost per call O(1), even though any single call's cost can vary. `hasNext()` is always strictly O(1).
**Space:** O(h) — the stack never holds more than one node per level of depth along the current traversal path, so its maximum size is bounded by the tree's height, not its total node count.

This is optimal: it directly satisfies the follow-up's requirements — O(1) average time per call and O(h) memory — by never materializing more of the traversal than is needed to know the *next* single element, incrementally extending the stack's frontier exactly as far as each `next()` call requires.

## 6. Dry Run

`root = [7,3,15,null,null,9,20]` (tree: `7` has children `3,15`; `15` has children `9,20`)

**Constructor:** `pushLeftSpine(7)`: push `7`; `7.left = 3`, push `3`; `3.left = null`, stop. Stack (bottom to top): `[7, 3]`.

| call | stack before | pop | right child? | pushLeftSpine on right child | stack after | returned |
|---|---|---|---|---|---|---|
| next() | [7,3] | 3 | no (3.right=null) | — | [7] | 3 |
| next() | [7] | 7 | yes (7.right=15) | push 15; 15.left=9, push 9; 9.left=null, stop | [15,9] | 7 |
| hasNext() | [15,9] | — | — | — | [15,9] | true |
| next() | [15,9] | 9 | no | — | [15] | 9 |
| hasNext() | [15] | — | — | — | [15] | true |
| next() | [15] | 15 | yes (15.right=20) | push 20; 20.left=null, stop | [20] | 15 |
| hasNext() | [20] | — | — | — | [20] | true |
| next() | [20] | 20 | no | — | [] | 20 |
| hasNext() | [] | — | — | — | [] | false |

Exit: the sequence of returned values — `3, 7, 9, 15, 20` — exactly matches the expected output, and `hasNext()` correctly reports `false` only once the stack is fully empty.

## 7. Complexity Summary

| Approach | Time | Space | Notes |
|---|---|---|---|
| Precompute Entire Traversal | O(n) constructor, O(1) per call after | O(n) for the whole lifetime | Fails the O(h)-memory follow-up |
| Explicit Stack, Incremental Left-Spine Pushing | O(1) amortized per `next()`, O(1) for `hasNext()` | O(h) | Satisfies both parts of the follow-up |

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|---|---|---|
| Single-node tree | Minimal valid BST | `pushLeftSpine(root)` pushes just the one node (no left child to continue into); the first `next()` pops it, finds no right child, and the stack is empty afterward, correctly making `hasNext()` return `false` |
| A node with no left child at all (its left subtree is entirely absent) | `pushLeftSpine` must correctly handle stopping immediately | The `while (node != null)` loop in `pushLeftSpine` naturally terminates right away if `node.left` is `null` from the very first step, pushing just that single node |
| A completely left-skewed tree (every node has only a left child) | The initial `pushLeftSpine` call in the constructor could push up to n nodes at once | This is exactly the worst case for stack size, correctly bounded at O(h) = O(n) for this particular skewed shape — still satisfies O(h) memory, since h legitimately equals n here |
| A completely right-skewed tree (every node has only a right child) | Every single `next()` call would trigger exactly one more `pushLeftSpine` call (each pushing just one node, since there's no left child to chain into) | Each `next()` call does a small, bounded amount of work in this specific shape; the amortized O(1) guarantee still holds, it's just realized differently (many cheap calls instead of one expensive one and many free ones) |
| Calling `hasNext()` multiple times in a row without calling `next()` in between | Must not have any side effects that would corrupt the iterator's state | `hasNext()` only reads `stack.isEmpty()`, performing no mutation whatsoever, so it can be called any number of times safely with no effect on subsequent `next()` behavior |
| Exhausting the iterator completely (calling `next()` until `hasNext()` is `false`) | Must visit every node exactly once, in the correct ascending order | By the stack invariant, every node is pushed exactly once (either during the initial `pushLeftSpine` or as part of some later node's right-subtree exploration) and popped exactly once, guaranteeing each node is visited exactly once, in in-order sequence |

## 9. Java Notes

- `Deque<TreeNode> stack = new ArrayDeque<>();` used via `push`/`pop` is the standard idiomatic Java stack, as in other stack-based problems — `ArrayDeque` is preferred over the legacy `java.util.Stack` class.
- The "amortized O(1)" claim for `next()` is a classic example of **amortized analysis**: no single call is individually guaranteed to be fast (a call right after construction on a left-skewed tree's root could, in principle, trigger a `pushLeftSpine` that does more work, though in this specific algorithm the *initial* spine-push happens once in the constructor, and each subsequent `next()`'s `pushLeftSpine` call is bounded by how many nodes get pushed at that specific step) — but summed over the *entire sequence* of calls across the object's lifetime, the total work is linear in the number of nodes, so the *average* cost per call is constant.
- Implementing the class with instance fields (`stack`) rather than static/shared state is essential here, since multiple independent `BSTIterator` instances could be created over the same or different trees simultaneously, and each needs its own independent traversal state.
- No recursion is used anywhere in the optimized solution — the entire traversal is driven by the explicit stack and the `while` loop inside `pushLeftSpine`, which is part of what makes it naturally suited to being *paused* and *resumed* across separate method calls, unlike a standard recursive traversal which runs to completion in one shot.

## 10. Common Mistakes

- Pushing only the single next node on each `next()` call, rather than pushing that node's *entire* left spine when descending into a new right subtree — this would incorrectly skip over smaller elements deeper in that right subtree's left-leaning path, violating in-order (ascending) sequence. Fix: always push the complete left spine (via the `pushLeftSpine` loop), not just a single node, whenever moving into a new subtree.
- Forgetting to check `current.right != null` before calling `pushLeftSpine` on it — attempting to call `pushLeftSpine(null)` would actually still work correctly here (the `while (node != null)` loop simply wouldn't execute at all), but it's cleaner and slightly more efficient to skip the call entirely when there's no right child, and omitting the check entirely without realizing `pushLeftSpine` already handles `null` gracefully could lead to unnecessary confusion about whether it's actually safe.
- Precomputing the entire traversal into a list (as in the brute-force approach) under the mistaken belief that this is required for O(1) *time* per call — while it does achieve O(1) time, it unnecessarily sacrifices the O(h) *memory* requirement that the follow-up specifically also asks for; both time and space requirements need to be satisfied together, not just one or the other. Fix: use the explicit-stack technique, which achieves both simultaneously.
- Attempting to track "the current node" with a single pointer variable and manually re-deriving its in-order successor via ad hoc parent-pointer logic (assuming the tree even has parent pointers, which this `TreeNode` definition typically doesn't) — this is significantly more complex and error-prone than the clean stack-based approach, which naturally handles successor-finding as an emergent property of the stack's structure without needing any parent references at all.

## 11. Interview Takeaway

- **Trigger sentence:** "Iterator over a BST's in-order traversal, O(1) average time and O(h) memory → simulate the recursive in-order traversal's call stack explicitly, pushing each new subtree's full left spine before returning its smallest (leftmost) element."
- **90-second explanation:** "In-order traversal recursively explores a node's whole left subtree before visiting the node itself, so I simulate that recursion's call stack directly with an explicit stack, rather than letting actual recursive calls run to completion all at once. At construction, I push the root and then follow left children all the way down, so the smallest element ends up on top of the stack, ready for the first call. Each `next()` call pops that top node — that's the next element in ascending order — and if it has a right child, I push that child's entire left spine too, since that's exactly what the traversal needs to explore next to keep producing ascending values. `hasNext()` is just checking whether anything's left on the stack. Because every node only ever gets pushed once and popped once across the whole lifetime of the iterator, the total work stays linear overall, making each individual call average out to constant time — and since the stack only ever holds nodes along a single root-to-current path at any moment, its size never exceeds the tree's height, satisfying the memory requirement too."
- **Related problems:** 94 (Binary Tree Inorder Traversal, the non-iterator version of the same core technique), 230 (Kth Smallest Element in a BST, solvable with the same iterative in-order approach), 281 (Zigzag Iterator, a different but related "controlled, resumable traversal" design pattern).

## 12. Recall Questions

**Q:** Why must `pushLeftSpine` push an entire chain of left children, rather than just the single node passed to it?
**A:** In-order traversal requires fully exploring a subtree's leftmost path before that subtree's own root can be considered "next," so pushing just one node would cause the traversal to incorrectly visit a node before its smaller left-descendant values, breaking the required ascending order.

**Q:** Why is the time complexity described as "amortized O(1)" rather than simply "O(1)" for every individual call?
**A:** Any single `next()` call could, in principle, trigger a `pushLeftSpine` that pushes several nodes at once (if the newly-entered right subtree has a long left spine), so that specific call does more than constant work — but since every node is pushed and popped exactly once across the iterator's entire lifetime, the total work sums to O(n), making the *average* cost per call constant even though individual calls can vary.

**Q:** Why does the stack's maximum size stay bounded by the tree's height, rather than growing with the total number of nodes?
**A:** At any given moment, the stack holds exactly one node per level of depth along the current path being explored (the ancestors still pending their own visit), and since a path from the root to any node can have at most `h` nodes (where `h` is the tree's height), the stack can never exceed that bound, regardless of how many total nodes the tree has elsewhere.

**Q:** Why does the brute-force "precompute everything up front" approach fail to meet the problem's follow-up, even though it has excellent per-call time performance?
**A:** The follow-up asks for both O(1) average time *and* O(h) memory simultaneously; precomputing the full traversal achieves the time goal but requires O(n) memory to store the complete sequence of values for the iterator's entire lifetime, which doesn't scale down to O(h) regardless of how few `next()` calls are actually made.

**Q:** How does this stack-based technique relate to how a recursive in-order traversal would naturally behave, if it could somehow be "paused" between visiting nodes?
**A:** The explicit stack's contents at any point are essentially equivalent to what the call stack of a recursive in-order traversal would look like at the exact moment just before visiting the node currently on top — this technique essentially manually manages that same "pending to visit" state that recursion would otherwise track implicitly, but does so in a way that can be paused and resumed across separate method invocations.

## 13. Final Code

```java
import java.util.*;

/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode(int x) { val = x; }
 * }
 */
class BSTIterator {
    private Deque<TreeNode> stack = new ArrayDeque<>();

    public BSTIterator(TreeNode root) {
        pushLeftSpine(root);
    }

    private void pushLeftSpine(TreeNode node) {
        while (node != null) {
            stack.push(node);
            node = node.left;
        }
    }

    public boolean hasNext() {
        return !stack.isEmpty();
    }

    public int next() {
        TreeNode current = stack.pop();
        if (current.right != null) {
            pushLeftSpine(current.right);
        }
        return current.val;
    }
}

/**
 * Your BSTIterator object will be instantiated and called as such:
 * BSTIterator obj = new BSTIterator(root);
 * boolean param_1 = obj.hasNext();
 * int param_2 = obj.next();
 */
```

## 14. Self-Test

Design an iterator over a BST's in-order traversal supporting `next()` and `hasNext()`, achieving average O(1) time and O(h) memory. Re-derive: maintain a stack holding the current path's pending ancestors; initialize by pushing the root's entire left spine; on each `next()`, pop the top node (the next in-order value), and if it has a right child, push that child's entire left spine before returning the popped node's value; `hasNext()` simply checks whether the stack is empty.
