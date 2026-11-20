---
title: "0637 — Average of Levels in Binary Tree"
layout: default
parent: Binary Tree BFS
grand_parent: Leetcode
nav_order: 637
permalink: /10-Binary-Tree-BFS/0637-average-of-levels-in-binary-tree/

problem: 637 - Average of Levels in Binary Tree
url: https://leetcode.com/problems/average-of-levels-in-binary-tree/
difficulty: Easy
section: Tree / BFS-DFS
patterns: [level-order-aggregation, depth-indexed-accumulation]
data_structures: [tree, queue, list]
time: O(n)
space: O(n)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0637 — Average of Levels in Binary Tree

| Field | Value |
|---|---|
| **Pattern** | Level-Order Aggregation (BFS) or Depth-Indexed Accumulation (DFS) |
| **Difficulty** | Easy |
| **Section** | Tree / BFS-DFS |
| **Time** | O(n) |
| **Space** | O(n) — queue for BFS or recursion stack for DFS |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Meta |
| **LeetCode** | [Average of Levels in Binary Tree](https://leetcode.com/problems/average-of-levels-in-binary-tree/) |

## 1. Problem in Simple Words

**Given:** The root of a binary tree.

**Required:** Return the average value of the nodes on each level, as an array (one average per level, ordered top to bottom).

**Constraints that actually matter:**
- `1 <= number of nodes <= 10^4` — the tree is guaranteed non-empty; moderate size, so performance isn't the real challenge.
- **`Node.val` spans the full 32-bit `int` range** (`-2^31` to `2^31 - 1`) — this is the single most important subtlety: summing even a small number of values that are each near the extremes of `int` range will **overflow** a plain `int` accumulator almost immediately (e.g., just two values near `2^31 - 1` summed together already overflow). Since a level could contain up to roughly `n/2` nodes, any of which could be at the extreme values, the running sum for a level **must** use a wider type (`long`) to avoid silent incorrect results.
- "Answers within `10^-5` of the actual answer will be accepted" — this is a standard floating-point tolerance note, signaling that the expected output type is a floating-point value (`double`) per level, and that minor precision differences from the division operation are acceptable.

## 2. Recognition Signals

- "Compute an aggregate (here, average) per level of a tree" → the signature of a **level-order (BFS) aggregation** problem: process the tree level by level using a queue, and for each level, accumulate whatever aggregate is needed (sum and count here, in order to compute an average) over exactly that level's nodes before moving to the next.
- The explicit mention of the full `int` value range for node values, combined with needing to **sum** multiple values together, is itself a strong signal to use a wider accumulator type (`long`) for the per-level sum, to avoid overflow — this is a common "trap" detail worth specifically watching for whenever a problem both allows extreme individual values *and* requires summing several of them.
- Whenever "one aggregate value per level" is needed, the same BFS-level-processing structure used in level-order traversal (LC 102) or right-side-view (LC 199) applies directly — only the specific aggregate computed per level changes (here: sum divided by count, rather than "last node" or "max value").

**Pattern:** Level-Order (BFS) Aggregation (process the tree level by level using a queue, accumulating a sum and count for each level's nodes, then compute and record that level's average before moving to the next level) — with Depth-Indexed DFS Accumulation as a dual alternative, maintaining per-depth running sum/count totals via a depth-tracking recursive traversal instead of an explicit queue.

## 3. Core Idea

Process the tree level by level, using a queue to hold exactly one level's worth of nodes at a time (the standard "capture `levelSize` before processing" BFS idiom). For each level, iterate through exactly `levelSize` nodes, accumulating their values into a running `sum` (using a `long` to avoid overflow) and counting them (`levelSize` itself is already the count). Once the level's nodes are all processed (and their children enqueued for the next level), compute that level's average as `sum / (double) levelSize` and record it, then proceed to the next level using the newly enqueued children.

**Invariant:** At the end of processing each level's batch of exactly `levelSize` nodes, `sum` holds the exact total of all node values at that depth (with no overflow, due to the `long` accumulator), so `sum / (double) levelSize` correctly computes that level's average — and since each level is fully processed (including enqueueing all of its children) before the next level's batch begins, every level's average is computed and recorded in the correct top-to-bottom order.

## 4. Approach 1 — BFS (Level-Order, Explicit Queue)

**Logic:** Standard level-order traversal. For each level, track the level's node count, sum their values using a `long` accumulator, compute the average, and move to the next level.

```java
public List<Double> averageOfLevels(TreeNode root) {
    List<Double> result = new ArrayList<>();
    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);

    while (!queue.isEmpty()) {
        int levelSize = queue.size();
        long sum = 0;

        for (int i = 0; i < levelSize; i++) {
            TreeNode current = queue.poll();
            sum += current.val;

            if (current.left != null) {
                queue.offer(current.left);
            }
            if (current.right != null) {
                queue.offer(current.right);
            }
        }

        result.add(sum / (double) levelSize);
    }

    return result;
}
```

- **Time:** O(n) — every node is enqueued and dequeued exactly once.
- **Space:** O(n) in the worst case for the queue — proportional to the tree's maximum width (up to roughly n/2 nodes at the widest level of a bushy tree).

**Why it is presented as the standard, direct approach:** This is the most natural, literal translation of "compute something per level" into code, directly reusing the same level-order traversal structure seen in related problems (LC 102, LC 199). It's correct and O(n) time, with the `long` accumulator correctly sidestepping the overflow risk.

## 5. Approach 2 — DFS (Depth-Indexed Running Sum/Count Accumulation)

**Algorithm:**
1. Maintain two parallel lists (or a list of `long[2]` pairs): `sums` and `counts`, indexed by depth, growing dynamically as new depths are first encountered.
2. Define a recursive helper `dfs(node, depth)`:
   - If `node` is `null`, return immediately.
   - If `depth == sums.size()` (this depth hasn't been seen yet), initialize a new entry (`sums.add(0L)`, `counts.add(0)`).
   - Add `node.val` to `sums.get(depth)`, and increment `counts.get(depth)`.
   - Recurse into both children with `depth + 1` (order between left/right doesn't matter here, unlike LC 199, since we're aggregating rather than picking a specific side).
3. After the full traversal, build the result list by computing `sums.get(i) / (double) counts.get(i)` for each depth `i`.

```java
public List<Double> averageOfLevels(TreeNode root) {
    List<Long> sums = new ArrayList<>();
    List<Integer> counts = new ArrayList<>();

    dfs(root, 0, sums, counts);

    List<Double> result = new ArrayList<>();
    for (int i = 0; i < sums.size(); i++) {
        result.add(sums.get(i) / (double) counts.get(i));
    }

    return result;
}

private void dfs(TreeNode node, int depth, List<Long> sums, List<Integer> counts) {
    if (node == null) {
        return;
    }

    if (depth == sums.size()) {
        sums.add(0L);
        counts.add(0);
    }

    sums.set(depth, sums.get(depth) + node.val);
    counts.set(depth, counts.get(depth) + 1);

    dfs(node.left, depth + 1, sums, counts);
    dfs(node.right, depth + 1, sums, counts);
}
```

- **Time:** O(n) — every node is visited exactly once across the entire recursion.
- **Space:** O(h) for the recursion call stack (O(log n) for a balanced tree, O(n) worst-case for a skewed tree), plus O(number of levels) for the `sums`/`counts` lists, which is also bounded by O(h).

**Why this is presented as the more space-efficient alternative:** Both approaches share O(n) time; the DFS version's space usage is bounded by the tree's height rather than its width, which can be meaningfully smaller for a wide, shallow tree (the same trade-off discussed in the related right-side-view problem). Unlike LC 199, the left/right recursion order here is irrelevant to correctness, since both children's values need to be included in the same depth's running total regardless of which is visited first — a simpler case than problems requiring a specific "first node wins" semantic.

## 6. Dry Run

Example: `root = [3,9,20,null,null,15,7]`.
Chosen because it's the canonical example, clearly showing three distinct levels with different sizes and non-trivial averages.

Using the BFS approach:

**Level 0:** `levelSize=1`. Dequeue node 3: `sum=3`. Enqueue children 9, 20. `result.add(3/1.0=3.0)`. `result=[3.0]`.

**Level 1:** `levelSize=2`. Dequeue node 9: `sum=9`; enqueue nothing (9 has no children). Dequeue node 20: `sum=9+20=29`; enqueue children 15, 7. `result.add(29/2.0=14.5)`. `result=[3.0, 14.5]`.

**Level 2:** `levelSize=2`. Dequeue node 15: `sum=15`; no children. Dequeue node 7: `sum=15+7=22`; no children. `result.add(22/2.0=11.0)`. `result=[3.0, 14.5, 11.0]`.

Exit condition: queue is empty after level 2's processing (no further children were enqueued).

**Final answer:** `[3.00000, 14.50000, 11.00000]`, matching the expected output exactly.

## 7. Complexity Summary

| Approach | Time | Space (Auxiliary) | Notes |
|----------|------|--------------------|-------|
| 1. BFS, level-order with queue | O(n) | O(n) worst case (queue, proportional to tree width) | Direct, standard approach; correctly uses `long` for the sum |
| 2. DFS, depth-indexed accumulation | O(n) | O(h) (tree height) plus O(levels) for the sum/count lists | More space-efficient for wide-but-shallow trees |

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|------|-------------------|--------------------------|
| Single-node tree | Should return a single-element list with that node's own value as the average | BFS: `levelSize=1`, sum equals that node's value, average computed as `value/1.0`; DFS: depth 0's sum and count are both correctly initialized and updated from the single node, producing the same result |
| Node values at the extremes of the int range, multiple at the same level (overflow risk) | Summing several values each near `Integer.MAX_VALUE` (or `MIN_VALUE`) in plain `int` arithmetic would overflow and silently produce an incorrect sum | Both approaches use a `long` (not `int`) accumulator for the per-level/per-depth sum, which comfortably holds the sum of up to `10^4` values each up to `~2^31` in magnitude without overflowing, since `long` has roughly 9.2*10^18 of range, vastly more than the worst-case sum of about `10^4 * 2^31 ≈ 2*10^13` |
| A level with a single node (e.g., the root's level, or a level where only one branch continues) | Average should simply equal that single node's value | Both approaches handle this uniformly as a trivial case of the general per-level sum/count logic — no special-casing needed, since dividing a single value by a count of 1 naturally yields that value itself |
| A tree where levels have very different sizes (e.g., one level with 1 node, the next with many) | The algorithm must correctly reset its sum/count tracking at each new level boundary | BFS: the `levelSize` captured fresh at the start of each `while` iteration, combined with a freshly reset `sum=0` each time, naturally isolates each level's accumulation correctly; DFS: each new depth gets its own fresh `sums`/`counts` entry the first time it's encountered, and subsequent visits to that same depth correctly accumulate into that same entry regardless of how many total nodes eventually contribute to it |
| Maximum constraint size (`n=10^4`) | Should perform efficiently and correctly (without overflow) at the upper bound | The O(n) time complexity and overflow-safe `long` accumulator comfortably handle this size |

## 9. Java Notes

- **`long sum` instead of `int sum`:** this is the single most important correctness detail in this problem — given that node values can span the full 32-bit `int` range and a level can contain many nodes, a plain `int` accumulator risks silent overflow; `long` provides enough headroom for the worst-case sum given the constraints.
- **`sum / (double) levelSize`:** the explicit cast to `double` before the division is essential — without it, if both operands were integer types, Java would perform integer division (truncating any fractional part), producing an incorrect (rounded-down) average instead of the required floating-point result.
- **`List<Double>` as the return type:** matches the problem's requirement for floating-point average values, with the stated `10^-5` tolerance accommodating any minor floating-point precision differences from the division.
- **DFS approach's `List<Long>` and `List<Integer>` growing dynamically as new depths are encountered:** mirrors the same "grow the result structure the first time a new depth/level is seen" idiom used in the DFS solution to the related right-side-view problem (LC 199), adapted here to track two parallel running totals (sum and count) instead of a single "first value seen."

## 10. Common Mistakes

- **Using a plain `int` accumulator for the per-level sum instead of `long`.** Given that node values can be as extreme as `Integer.MAX_VALUE` or `Integer.MIN_VALUE`, and a level could contain multiple such values, summing them in `int` arithmetic can silently overflow and produce a completely incorrect (wrapped-around) sum, which would then produce a wrong average without any runtime error to signal the problem. Fix: always use `long` for a sum accumulator whenever the individual values being summed can span a wide range and multiple values are being combined.
- **Performing integer division instead of floating-point division when computing the average** (e.g., `sum / levelSize` where both are integer types, without an explicit cast to `double`). This would truncate any fractional part of the result, producing an incorrect, rounded-down average instead of the required precise floating-point value. Fix: always explicitly cast at least one operand to `double` (or otherwise ensure floating-point division is performed) before dividing.
- **Forgetting to capture `levelSize` before the inner loop begins in the BFS approach**, leading to the same level-mixing bug described in the related right-side-view problem, where children enqueued during the current level's processing would incorrectly also get processed as part of that same level's batch. Fix: always capture the level's size into a fixed local variable before the inner loop starts.
- **In the DFS approach, forgetting to initialize a new `sums`/`counts` entry the first time a new depth is encountered, or initializing it more than once (overwriting previously accumulated data for that depth).** Fix: always check `depth == sums.size()` (exactly, not "greater than or equal to") before adding a fresh zero-initialized entry, ensuring each depth's entry is created exactly once and then correctly accumulated into on every subsequent visit to that same depth.

## 11. Interview Takeaway

- **Trigger sentence to memorize:** "Compute an aggregate (sum, average, max, etc.) per level of a tree → process level by level with BFS (sum and count each level's batch), or track running per-depth totals via a depth-indexed DFS — and watch for overflow if summing extreme-valued nodes."
- **90-second explanation:** "I process the tree level by level, which naturally fits a breadth-first traversal using a queue. For each level, I track how many nodes are in that level's batch and sum up their values as I dequeue them — critically, I use a long for that running sum rather than a plain int, since node values can span the entire 32-bit integer range, and summing even a handful of extreme values in plain int arithmetic would silently overflow and give me a wrong answer. Once I've processed an entire level's batch, I compute that level's average by dividing the sum by the count, making sure to perform floating-point division rather than integer division, and I move on to the next level, which now consists of all the children just enqueued. As an alternative, I could instead do a depth-first traversal, tracking a running sum and count for each depth in parallel lists that grow as new depths are first encountered — this uses less space for a tree that's wide but not very deep, since its space usage depends on the tree's height rather than its width, though unlike some other 'first value at each depth wins' problems, the order I visit the two children in doesn't matter here, since I need to include both of their values in the same depth's running total regardless."
- **Related problems using this pattern:**
  - LeetCode 102 — Binary Tree Level Order Traversal (the base BFS technique this problem specializes)
  - LeetCode 199 — Binary Tree Right Side View (a closely related "one value per level" problem, picking a specific side instead of averaging)
  - LeetCode 515 — Find Largest Value in Each Tree Row (another closely related per-level aggregate, using max instead of average)

## 12. Recall Questions

**Q:** Why is it essential to use a `long` accumulator for the per-level sum, rather than a plain `int`, given this problem's specific constraints?
**A:** Node values can span the entire 32-bit integer range, and a single tree level could contain many such nodes, so summing several extreme values together in ordinary int arithmetic can silently exceed int's maximum representable value and wrap around to an incorrect result, whereas a long provides enough extra range to safely hold the worst-case sum.

**Q:** Why must the division used to compute each level's average be performed as floating-point division rather than integer division?
**A:** Integer division in Java truncates any fractional part of the result, so if the sum and count were both treated as integer types during the division, the computed average would be incorrectly rounded down whenever the true average isn't a whole number, rather than producing the precise floating-point value the problem requires.

**Q:** Unlike the related right-side-view problem, why doesn't the order in which the DFS approach visits a node's left versus right child matter for correctness here?
**A:** This problem needs to combine every node's value at a given depth into a single running total regardless of which specific child it is, rather than needing to identify one specific "winning" node at each depth based on visit order, so both children's contributions get correctly folded into the same depth's sum and count no matter which one is processed first.

**Q:** Why does the BFS approach need to capture the current level's size into a fixed variable before beginning to process that level's nodes?
**A:** Since each node's children get enqueued onto the very same queue during the processing of their parent's level, the queue's size changes while a level is being processed, so without capturing a fixed snapshot of the level's size beforehand, the processing loop could incorrectly continue into nodes that actually belong to the next level down.

**Q:** How would this algorithm need to change if the problem instead asked for the maximum (rather than the average) value at each level?
**A:** Instead of tracking a running sum and count to later divide, each level's (or depth's) aggregate would simply track a running maximum value, updated by comparing each newly encountered node's value against the current maximum for that level, with no division step needed at the end — the same overall level-by-level (or depth-indexed) traversal structure would otherwise remain the same.

## 13. Final Code

```java
public List<Double> averageOfLevels(TreeNode root) {
    List<Double> result = new ArrayList<>();
    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);

    while (!queue.isEmpty()) {
        int levelSize = queue.size();
        long sum = 0;

        for (int i = 0; i < levelSize; i++) {
            TreeNode current = queue.poll();
            sum += current.val;

            if (current.left != null) {
                queue.offer(current.left);
            }
            if (current.right != null) {
                queue.offer(current.right);
            }
        }

        result.add(sum / (double) levelSize);
    }

    return result;
}
```

## 14. Self-Test

You have the root of a binary tree. Return the average value of the nodes at each level, as an array. Think about why a level's running sum needs a wider type than a plain 32-bit int given this problem's value constraints, and why the division to compute each average must be floating-point, not integer, division.
