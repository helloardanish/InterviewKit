---
title: "0103 — Binary Tree Zigzag Level Order Traversal"
layout: default
parent: Binary Tree BFS
grand_parent: Leetcode
nav_order: 103
permalink: /10-Binary-Tree-BFS/0103-binary-tree-zigzag-level-order-traversal/

problem: 103 - Binary Tree Zigzag Level Order Traversal
url: https://leetcode.com/problems/binary-tree-zigzag-level-order-traversal/
difficulty: Medium
section: Tree / BFS
patterns: [bfs-level-order, direction-toggle]
data_structures: [tree, queue, linked-list]
time: O(n)
space: O(n)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0103 — Binary Tree Zigzag Level Order Traversal

| Field | Value |
|---|---|
| **Pattern** | BFS Level-Order with Direction Toggle |
| **Difficulty** | Medium |
| **Section** | Tree / BFS |
| **Time** | O(n) |
| **Space** | O(n) — queue + output |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Meta |
| **LeetCode** | [Binary Tree Zigzag Level Order Traversal](https://leetcode.com/problems/binary-tree-zigzag-level-order-traversal/) |

## 1. Problem in Simple Words

**Given:** The root of a binary tree.

**Required:** Return the node values level by level, but alternate the reading direction: level 0 left to right, level 1 right to left, level 2 left to right, and so on. The result is a list of lists, one inner list per level.

**Constraints that actually matter:**
- `0 <= number of nodes <= 2000` — small, so any O(n) approach is fine. The tree can be **empty**, and the answer must then be an empty outer list.
- `-100 <= Node.val <= 100` — values are small and signed; nothing numeric to worry about, no overflow, and values are never compared.
- The zigzag applies to the **order of values within each level's list**, not to the order in which nodes are discovered. The tree's structure never changes; only how each level is read out changes. This is the hint that the problem is plain level-order traversal (LC 102) plus a per-level direction flag.

## 2. Recognition Signals

- "Level order" + "alternate direction each level" → standard BFS with a queue, plus a boolean that flips once per level.
- The phrase "from left to right, then right to left for the next level" tells you the direction is a **per-level** property. A flag toggled after each completed level is enough; no per-node state is needed.
- If a problem asks for any per-level transformation of an otherwise normal level-order result (reverse, sort, aggregate), first build the plain level-order skeleton, then apply the transformation inside the per-level loop.

**Pattern:** BFS level-order traversal with a direction toggle. The queue always stays in normal left-to-right order; only the way each level's values are placed into the output list changes.

## 3. Core Idea

Keep the queue exactly as in ordinary level-order traversal: dequeue a node, enqueue its left child, then its right child. That guarantees the queue always holds each level in left-to-right order. Do not change the enqueue order to get the zigzag, because that would scramble the next level.

Instead, change how each node's value is written into the current level's list. On a left-to-right level, append to the end. On a right-to-left level, insert at the front. A `LinkedList` supports `addFirst` in O(1), so this needs no extra pass. After finishing a level, flip the direction flag.

**Invariant:** At the start of each level, the queue contains exactly that level's nodes in left-to-right order, and the direction flag says how this level's values must be laid out. Processing exactly `levelSize` nodes yields a correctly ordered level list, and the children enqueued during this pass form the next level, again in left-to-right order.

## 4. Approach 1 — BFS, Then Reverse Alternate Levels

**Logic:** Run a normal level-order traversal, collecting each level left to right. After a level's list is built, if its index is odd, reverse that list in place before adding it to the result.

```java
public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) {
        return result;
    }

    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);
    int level = 0;

    while (!queue.isEmpty()) {
        int levelSize = queue.size();
        List<Integer> currentLevel = new ArrayList<>();

        for (int i = 0; i < levelSize; i++) {
            TreeNode current = queue.poll();
            currentLevel.add(current.val);

            if (current.left != null) {
                queue.offer(current.left);
            }
            if (current.right != null) {
                queue.offer(current.right);
            }
        }

        if (level % 2 == 1) {
            Collections.reverse(currentLevel);
        }

        result.add(currentLevel);
        level++;
    }

    return result;
}
```

- **Time:** O(n). Each node is enqueued and dequeued once, and each odd level is reversed once, which costs O(level size). Total reversal work across all levels is at most O(n).
- **Space:** O(n) for the queue in the worst case (proportional to the widest level), plus the required output.

**Why it is not the tightest version:** It is correct and simple, but it touches every node on odd levels a second time during the reversal. The extra pass is cheap and does not change the complexity class, but it can be avoided entirely by placing each value in its final position as it is dequeued.

## 5. Approach 2 — Optimized, Single Pass with addFirst

**Algorithm:**
1. If `root` is `null`, return an empty list.
2. Initialize the queue with `root` and a boolean `leftToRight = true`.
3. For each level, capture `levelSize`, create a fresh `LinkedList<Integer>` for the level, and process exactly `levelSize` nodes:
   - Dequeue a node.
   - If `leftToRight`, `addLast(node.val)`; otherwise `addFirst(node.val)`.
   - Enqueue its left child, then its right child, in the normal order.
4. Add the level's list to the result and flip `leftToRight`.

```java
public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) {
        return result;
    }

    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);
    boolean leftToRight = true;

    while (!queue.isEmpty()) {
        int levelSize = queue.size();
        LinkedList<Integer> currentLevel = new LinkedList<>();

        for (int i = 0; i < levelSize; i++) {
            TreeNode current = queue.poll();

            if (leftToRight) {
                currentLevel.addLast(current.val);
            } else {
                currentLevel.addFirst(current.val);
            }

            if (current.left != null) {
                queue.offer(current.left);
            }
            if (current.right != null) {
                queue.offer(current.right);
            }
        }

        result.add(currentLevel);
        leftToRight = !leftToRight;
    }

    return result;
}
```

- **Time:** O(n). Each node is processed once, and both `addFirst` and `addLast` on a `LinkedList` are O(1).
- **Space:** O(n) for the queue in the worst case, plus the required output.

**Why this is optimal:** Every node must be visited at least once, so O(n) time is a lower bound and this meets it. Compared with Approach 1 it removes the separate reversal pass, so each value is written exactly once, directly into its final position. The queue's O(n) worst-case space is inherent to level-order traversal on a wide tree; a DFS with depth-indexed lists could reduce auxiliary space to O(h), but BFS is the natural, expected solution here.

## 6. Dry Run

Example: `root = [3,9,20,null,null,15,7]`. Chosen because it has three levels, so it exercises a left-to-right level, a reversed level, and a return to left-to-right.

Initial: `queue = [3]`, `leftToRight = true`.

| Level | levelSize | Nodes dequeued in order | Direction | Operation per node | Level list built | Children enqueued | leftToRight after |
|-------|-----------|--------------------------|-----------|--------------------|------------------|-------------------|-------------------|
| 0 | 1 | 3 | left to right | addLast(3) | [3] | 9, 20 | false |
| 1 | 2 | 9, 20 | right to left | addFirst(9) → [9]; addFirst(20) → [20,9] | [20,9] | 15, 7 (from 20) | true |
| 2 | 2 | 15, 7 | left to right | addLast(15) → [15]; addLast(7) → [15,7] | [15,7] | none | false |

Exit condition: the queue is empty after level 2.

**Final answer:** `[[3],[20,9],[15,7]]`, matching the expected output.

## 7. Complexity Summary

| Approach | Time | Space (Auxiliary, excl. output) | Notes |
|----------|------|----------------------------------|-------|
| 1. BFS, reverse odd levels | O(n) | O(n) worst case (queue) | Extra reversal pass over odd levels |
| 2. BFS, addFirst/addLast by direction | O(n) | O(n) worst case (queue) | Single pass; each value written once |

The output itself is O(n) and is required by both approaches.

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|------|------------------|--------------------------|
| Empty tree (`root == null`) | The correct answer is an empty outer list, not a list holding an empty inner list | The early `if (root == null) return result;` returns the empty list before the queue is touched |
| Single node | Level 0 is left to right, so the result is just `[[val]]` | `leftToRight` starts `true`, `addLast` places the value, the loop ends because no children are enqueued |
| Completely left-skewed or right-skewed tree | Every level has exactly one node, so direction has no visible effect, but the code must not break | Each level has `levelSize == 1`; `addFirst` and `addLast` give the same one-element list, and the flag still flips harmlessly |
| Levels with a missing child (non-complete tree) | Null children must not be enqueued, or the next level's size would be wrong | Children are enqueued only when non-null, so `levelSize` always counts real nodes only |
| Negative values or duplicates | Someone might be tempted to sort or compare values | Values are never compared; order depends only on position in the queue, so duplicates and negatives behave like any other value |
| Maximum size (2000 nodes) and deep trees | Must stay efficient and not overflow anything | O(n) time with iterative BFS: no recursion, so no stack-depth risk |

## 9. Java Notes

- **`LinkedList` for the level list in Approach 2:** `addFirst` is O(1) on a `LinkedList`. Using `ArrayList.add(0, value)` instead would be O(k) per insert and could make a level cost O(k²).
- **Declare the level list as `LinkedList<Integer>`, not `List<Integer>`,** so `addFirst` and `addLast` are available. It can still be added to a `List<List<Integer>>` because `LinkedList<Integer>` implements `List<Integer>`.
- **`Collections.reverse(list)` in Approach 1** reverses in place in O(size) and returns nothing; do not try to assign its result.
- **`Queue<TreeNode>` backed by `LinkedList`:** the standard BFS container. `offer` and `poll` are O(1).
- **Capture `levelSize = queue.size()` once per level** before the inner loop, because enqueuing children during the loop changes the queue's size.
- No overflow or numeric concerns: values are in `[-100, 100]` and never combined arithmetically.

## 10. Common Mistakes

- **Changing the enqueue order (right child before left) on alternate levels to "get" the zigzag.** This reorders the *next* level's queue, so that level is read in the wrong order and the pattern drifts after the first flip. Fix: always enqueue left then right, and reverse only how values are written into the current level's list.
- **Toggling the direction flag per node instead of per level.** The flag must flip once after each completed level. Fix: place `leftToRight = !leftToRight` after the inner `for` loop, not inside it.
- **Using `ArrayList.add(0, value)` for right-to-left levels.** It is O(k) per insert, which degrades wide levels to quadratic time. Fix: use `LinkedList.addFirst`, or collect left to right and reverse once.
- **Reusing a single list object across levels, or returning `null` for an empty tree.** Adding a list to the result adds a reference, so reuse corrupts earlier levels; and an empty tree should give `[]`. Fix: create a fresh list per level and return the empty `result` for a null root.

## 11. Interview Takeaway

- **Trigger sentence to memorize:** "Level-order traversal with alternating direction → normal BFS with a queue, keep enqueue order left-then-right, and flip a boolean per level to decide whether to add values at the end or the front."
- **How to explain it aloud in 90 seconds:** "I start from the standard level-order traversal: a queue, and for each level I capture the queue size so I process exactly that level's nodes. The queue always stays in left-to-right order because I enqueue left then right. The zigzag only affects how I write values into the current level's list. I keep a boolean that starts true. On a left-to-right level I append each value to the end; on a right-to-left level I insert each value at the front, using a linked list so that is O(1). After each level I flip the boolean. I deliberately don't change the enqueue order, since that would scramble the next level. Every node is visited once, so time is O(n), and the queue holds at most one level plus its children, so extra space is O(n) in the worst case. An alternative is to collect each level normally and reverse the odd ones, which is equally correct but does an extra pass."
- **Related problems using this pattern:**
  - LeetCode 102 — Binary Tree Level Order Traversal
  - LeetCode 107 — Binary Tree Level Order Traversal II
  - LeetCode 199 — Binary Tree Right Side View
  - LeetCode 637 — Average of Levels in Binary Tree

## 12. Recall Questions

**Q:** Why must the queue's enqueue order (left child, then right child) stay the same on every level?
**A:** The queue's order defines the left-to-right order of the next level. If you enqueue right-first on alternate levels, the next level is dequeued in the wrong order and the zigzag pattern drifts instead of alternating cleanly.

**Q:** Where exactly should the direction flag be flipped, and why?
**A:** Once per level, after the inner loop finishes. Direction is a property of a whole level, so flipping per node would change direction in the middle of a level and break the pattern.

**Q:** Why is `LinkedList.addFirst` preferred over `ArrayList.add(0, x)` for building a right-to-left level?
**A:** `addFirst` on a linked list is O(1), while inserting at index 0 of an array list shifts every existing element, costing O(k) each time and O(k²) per level.

**Q:** How does reversing odd levels after collecting them compare with using `addFirst`?
**A:** Both are O(n) overall and produce the same output. The reversal approach makes a second pass over odd levels, while `addFirst` places each value directly into its final position in one pass.

**Q:** Why is it essential to capture `levelSize` before the inner loop?
**A:** Children are enqueued into the same queue during the loop, so the queue's size grows while you process the level. Without a fixed snapshot, the loop would spill into the next level's nodes and mix two levels.

## 13. Final Code

```java
public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) {
        return result;
    }

    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);
    boolean leftToRight = true;

    while (!queue.isEmpty()) {
        int levelSize = queue.size();
        LinkedList<Integer> currentLevel = new LinkedList<>();

        for (int i = 0; i < levelSize; i++) {
            TreeNode current = queue.poll();

            if (leftToRight) {
                currentLevel.addLast(current.val);
            } else {
                currentLevel.addFirst(current.val);
            }

            if (current.left != null) {
                queue.offer(current.left);
            }
            if (current.right != null) {
                queue.offer(current.right);
            }
        }

        result.add(currentLevel);
        leftToRight = !leftToRight;
    }

    return result;
}
```

## 14. Self-Test

You have the root of a binary tree. Return its node values level by level, but alternate the reading direction on each level: left to right on level 0, right to left on level 1, and so on. Think about why the queue's enqueue order must stay fixed, and where the direction flag should flip.
