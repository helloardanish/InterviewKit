---
title: "0102 — Binary Tree Level Order Traversal"
layout: default
parent: Binary Tree BFS
grand_parent: Leetcode
nav_order: 102
permalink: /10-Binary-Tree-BFS/0102-binary-tree-level-order-traversal/

problem: 102 - Binary Tree Level Order Traversal
url: https://leetcode.com/problems/binary-tree-level-order-traversal/
difficulty: Medium
section: Tree / BFS-DFS
patterns: [level-order-grouping, depth-indexed-collection]
data_structures: [tree, queue, list]
time: O(n)
space: O(n)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0102 — Binary Tree Level Order Traversal

| Field | Value |
|---|---|
| **Pattern** | Level-Order Grouping with Queue |
| **Difficulty** | Medium |
| **Section** | Tree / BFS-DFS |
| **Time** | O(n) |
| **Space** | O(n) — queue + output |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Meta |
| **LeetCode** | [Binary Tree Level Order Traversal](https://leetcode.com/problems/binary-tree-level-order-traversal/) |

## 1. Problem in Simple Words

**Given:** The root of a binary tree.

**Required:** Return the node values grouped level by level, from left to right within each level, ordered top to bottom overall — a list of lists, one inner list per level.

**Constraints that actually matter:**
- `0 <= number of nodes <= 2000` — moderate size; the tree can be **empty**, which must correctly produce an empty outer list (not a list containing one empty inner list).
- This is the **foundational, canonical level-order traversal problem** that many other tree problems (LC 199 Right Side View, LC 637 Average of Levels, LC 103 Zigzag Level Order, and others) build directly on top of — recognizing and internalizing this base pattern pays off across a whole family of related problems, since each of those is really just "do level-order traversal, but compute/record something slightly different per level" rather than requiring a fundamentally different technique.
- The requirement that each level's values be grouped into their **own separate inner list** (rather than, say, a single flat list with level-boundary markers) is exactly what the standard "capture `levelSize` before processing" BFS idiom directly produces, making this problem's output format a very natural fit for that specific technique.

## 2. Recognition Signals

- "Traverse a tree level by level, left to right, grouping values by level" → the signature, textbook use case for **BFS (breadth-first search) using a queue**: process nodes level by level, where the order nodes are dequeued naturally preserves left-to-right order within each level (since each parent enqueues its left child before its right child, and the queue itself is first-in-first-out).
- The specific idiom of **capturing the queue's current size (`levelSize`) before an inner loop that processes exactly that many nodes** is the standard technique for cleanly separating "this level's nodes" from "next level's nodes" within a single shared queue — recognizing this exact idiom is foundational for a wide range of level-order-based tree problems.
- Whenever a problem's output needs to be explicitly grouped or bucketed by depth/level (as opposed to just processing nodes level-by-level internally without needing to expose that grouping in the final result) — building a separate list per level, as each level is processed, is the direct, natural way to produce that specific output shape.

**Pattern:** Level-Order (BFS) Grouping (process the tree level by level using a queue, capturing each level's size before processing it, and collecting that level's values into its own list before moving to the next level) — with Depth-Indexed DFS Collection as a dual alternative, appending each node's value into a result list indexed by its depth.

## 3. Core Idea

Use a queue to process the tree level by level. At the start of each iteration of the outer loop, capture `levelSize = queue.size()` — this is exactly the number of nodes belonging to the *current* level (not yet mixed with any next-level nodes, since children are only enqueued *during* this same iteration, after the size has already been captured). Process exactly `levelSize` nodes: dequeue each, add its value to a fresh list representing the current level, and enqueue its children (which will become part of the *next* level's batch). Once all `levelSize` nodes have been processed, add the completed level's list to the overall result, and repeat for the next level (now populated in the queue) until the queue is empty.

**Invariant:** At the start of each outer-loop iteration, the queue contains exactly the nodes of one complete level, in left-to-right order — this holds initially (the queue starts with just `root`, trivially "level 0" in left-to-right order) and is preserved by each iteration, since processing exactly `levelSize` nodes (capturing that count *before* any enqueueing happens during this iteration) ensures the children enqueued during this iteration form exactly the next complete level, again in left-to-right order (since each node enqueues its left child before its right child, and nodes themselves are dequeued in left-to-right order).

## 4. Approach 1 — BFS (Level-Order, Explicit Queue)

**Logic:** Standard level-order traversal. For each level, capture its size, collect its nodes' values into a fresh list, and enqueue their children for the next level.

```java
public List<List<Integer>> levelOrder(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) {
        return result;
    }

    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);

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

        result.add(currentLevel);
    }

    return result;
}
```

- **Time:** O(n) — every node is enqueued and dequeued exactly once.
- **Space:** O(n) in the worst case for the queue — proportional to the tree's maximum width (up to roughly n/2 nodes at the widest level of a bushy, e.g. complete, tree). The output itself is also O(n), but that's required regardless of approach.

**Why it is presented as the standard, foundational approach:** This is the canonical, most directly intuitive way to produce a level-grouped traversal, and is almost universally the first technique taught and expected for this exact problem — nearly every other level-order-based tree problem is a direct variation of this same structure.

## 5. Approach 2 — DFS (Depth-Indexed Collection)

**Algorithm:**
1. Maintain a `result` list of lists, growing dynamically as new depths are first encountered during the traversal.
2. Define a recursive helper `dfs(node, depth)`:
   - If `node` is `null`, return immediately.
   - If `depth == result.size()` (this depth hasn't been seen yet), add a new empty list to `result` for it.
   - Add `node.val` to `result.get(depth)`.
   - Recurse into the left child, then the right child, both with `depth + 1` (left-before-right order is essential here, since both contribute to the same level's list and must appear in the correct left-to-right order within it).
3. Call `dfs(root, 0)`, then return `result`.

```java
public List<List<Integer>> levelOrder(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    dfs(root, 0, result);
    return result;
}

private void dfs(TreeNode node, int depth, List<List<Integer>> result) {
    if (node == null) {
        return;
    }

    if (depth == result.size()) {
        result.add(new ArrayList<>());
    }

    result.get(depth).add(node.val);

    dfs(node.left, depth + 1, result);
    dfs(node.right, depth + 1, result);
}
```

- **Time:** O(n) — every node is visited exactly once across the entire recursion.
- **Space:** O(h) for the recursion call stack (O(log n) balanced, O(n) worst-case skewed), plus the O(n) output itself (required regardless of approach).

**Why this is presented as the alternative, more space-efficient-in-some-cases approach:** Both approaches share O(n) time; the DFS version's *auxiliary* space (excluding the required output) is bounded by the tree's height rather than its width, which can be meaningfully smaller for a wide, shallow tree. Unlike the related right-side-view problem (LC 199), the left-before-right visiting order here is essential for correctness (not just a matter of which "wins" at a given depth) — since every node at a given depth must appear in its level's list in the correct left-to-right order, and only visiting left before right guarantees that.

## 6. Dry Run

Example: `root = [3,9,20,null,null,15,7]`.
Chosen because it's the canonical example, directly showing three distinct levels being correctly grouped.

Using the BFS approach: `queue = [3]`.

**Level 0:** `levelSize=1`. Dequeue 3, add to `currentLevel=[3]`; enqueue children 9, 20. `result=[[3]]`.

**Level 1:** `levelSize=2` (queue now holds `[9,20]`). Dequeue 9, add to `currentLevel=[9]`; 9 has no children, nothing enqueued. Dequeue 20, add to `currentLevel=[9,20]`; enqueue children 15, 7. `result=[[3],[9,20]]`.

**Level 2:** `levelSize=2` (queue now holds `[15,7]`). Dequeue 15, add to `currentLevel=[15]`; no children. Dequeue 7, add to `currentLevel=[15,7]`; no children. `result=[[3],[9,20],[15,7]]`.

Exit condition: queue is empty after level 2's processing.

**Final answer:** `[[3],[9,20],[15,7]]`, matching the expected output exactly.

## 7. Complexity Summary

| Approach | Time | Space (Auxiliary, excl. output) | Notes |
|----------|------|--------------------|-------|
| 1. BFS, level-order with queue | O(n) | O(n) worst case (queue, proportional to tree width) | Canonical, foundational approach for this entire problem family |
| 2. DFS, depth-indexed collection | O(n) | O(h) (tree height) | More space-efficient for wide-but-shallow trees; left-before-right order is essential |

Output space (the result list of lists) is O(n) in both and is required regardless of approach.

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|------|-------------------|--------------------------|
| Empty tree (`root == null`, example 3) | Should return an empty outer list, not a list containing an empty inner list | BFS: the initial `if (root == null) return result;` check returns the still-empty `result` immediately, before the queue is ever touched; DFS: `dfs(null, 0, result)` returns immediately without adding anything to `result`, correctly leaving it empty |
| Single-node tree (example 2) | Should produce a list containing one inner list with that single value | BFS: `levelSize=1` for the single iteration, correctly producing `[[1]]`; DFS: depth 0 gets its list created and populated with the one value, then no further recursion occurs since both children are null |
| A tree where one level has significantly more nodes than adjacent levels | The grouping must correctly isolate each level's full set of nodes, regardless of size differences between levels | Both approaches handle this uniformly — BFS via the fresh `levelSize` capture each iteration, DFS via the depth-indexed list simply accumulating however many values naturally arrive at that depth, with no assumption about level sizes being similar |
| A completely skewed tree (e.g., every node has only a left child) | Each level then contains exactly one node | BFS: every `levelSize` is 1, correctly producing one single-element inner list per level; DFS: every new depth's list gets exactly one value added before recursion moves to the next depth, since there's no sibling to contribute a second value at the same depth |
| Negative node values (constraint allows down to -1000) | Should be handled identically to positive values | Neither approach performs any value-based logic beyond adding values to lists, so sign is irrelevant |
| Maximum constraint size (2000 nodes) | Should perform efficiently at the upper bound | Both O(n) time approaches comfortably handle this size regardless of tree shape |

## 9. Java Notes

- **Capturing `levelSize = queue.size()` before the inner loop (BFS):** the essential idiom that isolates exactly one level's nodes per outer-loop iteration, since children enqueued during that same iteration would otherwise be indistinguishable from the current level's remaining nodes if the queue's size were checked dynamically inside the loop instead.
- **`result.get(depth).add(node.val)` (DFS):** relies on `result` having already been grown to include an entry for `depth` (via the `depth == result.size()` check) before this line executes — the order of these two steps (grow the list if this depth is new, then add the value) is essential.
- **Left-before-right recursion order in the DFS approach is essential here** (unlike the related right-side-view problem, where right-before-left was essential) — getting this backward would produce each level's values in right-to-left order instead of the required left-to-right order.
- **`Queue<TreeNode>` via `LinkedList`:** the standard, idiomatic Java choice for BFS, providing O(1) `offer`/`poll` operations.

## 10. Common Mistakes

- **Forgetting to capture `levelSize` before the inner loop, and instead checking `queue.size()` dynamically inside the loop's condition.** Since children get enqueued during the very same loop that's dequeuing the current level's nodes, this would incorrectly cause the loop to continue processing into the next level's nodes, mixing two levels together into one list instead of correctly separating them. Fix: always capture `levelSize` into a fixed local variable immediately before the inner loop begins.
- **In the DFS approach, recursing into the right child before the left child**, which would produce each level's list with its values in right-to-left order instead of the required left-to-right order. Fix: always recurse left before right in this specific problem (in contrast to the related right-side-view problem, which deliberately does the opposite).
- **Returning `null` instead of an empty list for an empty input tree.** The expected output format for an empty tree is an empty list (`[]`), not `null`, which could cause a `NullPointerException` for any caller that doesn't specifically check for `null` before using the result. Fix: initialize `result` as an empty `ArrayList` and return it directly (even for an empty tree), never returning `null`.
- **Reusing the same `currentLevel` list object across multiple levels instead of creating a fresh one for each level.** Since `result.add(currentLevel)` adds a *reference* to the list, not a copy, reusing (clearing and refilling) the same list object across levels would cause every entry in `result` to end up reflecting only the *final* level's contents, since they'd all be referencing the same underlying mutable list. Fix: always create a brand new `List<Integer>` for each level's values.

## 11. Interview Takeaway

- **Trigger sentence to memorize:** "Group tree node values level by level, left to right → BFS with a queue, capturing each level's size before processing it to cleanly separate one level's nodes from the next."
- **90-second explanation:** "I process the tree level by level using a queue. The key idiom is capturing the queue's current size at the very start of each level's processing — since that size represents exactly how many nodes belong to the current level, before any of their children get enqueued during this same round of processing. I then dequeue exactly that many nodes, collecting their values into a fresh list for this level, while enqueueing each one's children for the next round. Once I've processed exactly that many nodes, I add the completed level's list to my overall result and move on to the next level, which the queue now holds. This naturally preserves left-to-right order within each level, since parents always enqueue their left child before their right child, and the queue processes everything in first-in-first-out order. This is really the foundational pattern behind a whole family of related tree problems — right side view, average of levels, zigzag level order, and others are all essentially this same level-by-level processing structure, just computing or recording something slightly different for each level instead of collecting the full list of values."
- **Related problems using this pattern:**
  - LeetCode 199 — Binary Tree Right Side View (records only the last value per level)
  - LeetCode 637 — Average of Levels in Binary Tree (records the average per level)
  - LeetCode 103 — Binary Tree Zigzag Level Order Traversal (alternates left-to-right and right-to-left per level)
  - LeetCode 515 — Find Largest Value in Each Tree Row (records the max per level)

## 12. Recall Questions

**Q:** Why is it essential to capture the queue's size into a fixed variable before the inner loop begins, rather than checking it dynamically as the loop progresses?
**A:** Children get enqueued onto the same shared queue during the very processing of their parent's level, so the queue's size changes while a level is being processed, and without a fixed snapshot taken beforehand, the loop would incorrectly continue into nodes that actually belong to the next level rather than stopping precisely at the end of the current one.

**Q:** Why does processing the queue in first-in-first-out order, combined with each node enqueueing its left child before its right child, guarantee left-to-right order is preserved within each level?
**A:** Nodes are dequeued in the exact order they were enqueued, and since every node consistently enqueues its left child immediately before its right child, the relative left-to-right ordering established at one level is faithfully carried forward and reproduced at the next level as those children are themselves later dequeued in that same preserved order.

**Q:** Why must the DFS alternative recurse into the left child before the right child, specifically for this problem, when some related problems require the opposite order?
**A:** This problem requires each level's values to appear in left-to-right order within their list, and visiting left before right ensures values are appended to each depth's list in exactly that correct order, whereas a problem needing to identify a specific "rightmost" value at each depth instead benefits from the reverse order.

**Q:** Why must a fresh list object be created for each level's values, rather than reusing and clearing a single list object across levels?
**A:** Adding a list to the result only adds a reference to that same list object, not an independent copy of its current contents, so reusing one mutable list across multiple levels would cause every entry already added to the result to all end up reflecting whatever the shared list's contents happen to be at the very end, rather than each level's actually distinct values.

**Q:** Why is an empty tree expected to produce an empty outer list, rather than a list containing one empty inner list?
**A:** An empty tree has no levels at all, since there are no nodes to belong to any level in the first place, so the correct representation is an entirely empty result, not a result implying the existence of at least one (empty) level.

## 13. Final Code

```java
public List<List<Integer>> levelOrder(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) {
        return result;
    }

    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);

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

        result.add(currentLevel);
    }

    return result;
}
```

## 14. Self-Test

You have the root of a binary tree. Return its node values grouped level by level, left to right within each level, top to bottom overall. Think about why capturing the queue's size before processing each level is the key idiom that correctly separates one level's nodes from the next, and how this same structure underlies many related "per-level" tree problems.
