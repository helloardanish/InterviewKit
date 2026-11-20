---
title: "0199 — Binary Tree Right Side View"
layout: default
parent: Binary Tree BFS
grand_parent: Leetcode
nav_order: 199
permalink: /10-Binary-Tree-BFS/0199-binary-tree-right-side-view/

problem: 199 - Binary Tree Right Side View
url: https://leetcode.com/problems/binary-tree-right-side-view/
difficulty: Medium
section: Tree / BFS-DFS
patterns: [level-order-last-node, right-first-dfs]
data_structures: [tree, queue, list]
time: O(n)
space: O(h)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0199 — Binary Tree Right Side View

| Field | Value |
|---|---|
| **Pattern** | Level-Order Last Node (or Right-First DFS) |
| **Difficulty** | Medium |
| **Section** | Tree / BFS-DFS |
| **Time** | O(n) |
| **Space** | O(h) — queue holds one level at a time; DFS stack is O(h) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Meta |
| **LeetCode** | [Binary Tree Right Side View](https://leetcode.com/problems/binary-tree-right-side-view/) |

## 1. Problem in Simple Words

**Given:** The root of a binary tree.

**Required:** Imagine standing to the right of the tree and looking at it from that side — return the values of the nodes visible from that viewpoint, ordered from top to bottom (one value per level: the rightmost visible node at each depth).

**Constraints that actually matter:**
- `0 <= number of nodes <= 100` — tiny; the tree can be **empty**, which must correctly produce an empty result list.
- **"The rightmost visible node at each level" is not simply "the last node added at that level in some arbitrary traversal order"** — it specifically means, for each depth, the node that is furthest to the right among all nodes at that depth, regardless of the tree's specific shape. A level could have a right child missing at some point but still have a visible rightmost node further over (as in example 1, where level 2 consists of nodes 5 and 4, with 4 being rightmost despite being the child of node 3's *left*-side structure — actually let's recheck: root=1,2,3; 2's right child=5, 3's right child=4; level 2 visible rightmost is whichever is positioned furthest right, which here is node 4) — the key subtlety is that "rightmost at a given depth" is a **level-based** concept (BFS-oriented), even though it can also be computed via a cleverly-ordered DFS.
- Exactly one value must be returned **per level** (per depth) that has at least one node — this naturally suggests either a level-order (BFS) traversal that explicitly groups nodes by depth, or a depth-first traversal that visits the rightmost branch first at each level and only records a value the *first* time that depth is reached.

## 2. Recognition Signals

- "What's visible looking at a tree from one side, top to bottom" → the signature of a **level-by-level "last/first node seen" extraction**: either do a level-order (BFS) traversal and take the last node processed at each level, or do a depth-first traversal that deliberately visits the right child before the left child, recording a node's value only the very first time a given depth is reached (since visiting right-before-left guarantees that first visit at each depth is always the rightmost node there).
- The two standard techniques are genuinely dual to each other: **BFS, take the last element of each level's queue batch** (natural, intuitive, directly matches "process level by level") versus **DFS (right child before left child), record the first node seen at each depth** (less obviously connected to the problem's framing at first glance, but more space-efficient in many cases, and a nice example of reframing a "level" concept into a depth-first traversal via a clever visit order plus a "have I recorded this depth yet" check).
- Whenever a problem asks for "one representative node per level of a tree, specifically the extreme on one side" — both the BFS-per-level and the ordered-DFS-with-first-at-depth techniques are standard, interchangeable approaches, each with its own trade-offs.

**Pattern:** Level-Order (BFS) Traversal, Taking the Last Node Per Level — with Right-First DFS (recording the first node encountered at each depth, having visited right children before left children) as the more space-efficient dual alternative.

## 3. Core Idea

**BFS approach:** Process the tree level by level using a queue. For each level, before advancing to the next level, note the value of the **last** node dequeued during that level's processing — since all nodes at a given level are processed in left-to-right order (as they were enqueued from their parents, themselves processed left-to-right), the last one dequeued at each level is guaranteed to be the rightmost node at that depth.

**DFS approach (dual/alternative):** Perform a depth-first traversal, but at every node, visit the **right child before the left child** (reversing the usual left-first convention). Track the current depth as the recursion descends. The very first time the traversal reaches a given depth, record that node's value — since right is always explored before left, the first node encountered at any given depth via this traversal order is guaranteed to be the rightmost node at that depth (any node reachable only via a leftward path to that same depth would necessarily be visited *after* the already-explored rightward path has been fully explored to that depth or beyond).

**Invariant (BFS):** At the end of processing each level (a full batch of nodes dequeued from the queue, with their children enqueued for the next level), the last-processed node of that batch is exactly the rightmost node at that depth, because the queue preserves left-to-right order within each level by construction (parents enqueue their left child before their right child, in left-to-right order themselves).

**Invariant (DFS):** The depth-first traversal, visiting right before left at every node, guarantees that the first node visited at any given depth is the rightmost node at that depth — because any node further left at the same depth can only be reached via a path that, at some ancestor, branches left instead of right, and that leftward branch is always explored strictly after the corresponding rightward branch has been fully explored.

## 4. Approach 1 — BFS (Level-Order, Last Node Per Level)

**Logic:** Standard level-order traversal using a queue. Process one full level at a time (tracking the level's size before processing it); the last node dequeued within each level's batch is added to the result.

```java
public List<Integer> rightSideView(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    if (root == null) {
        return result;
    }

    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);

    while (!queue.isEmpty()) {
        int levelSize = queue.size();

        for (int i = 0; i < levelSize; i++) {
            TreeNode current = queue.poll();

            if (i == levelSize - 1) {
                result.add(current.val);
            }

            if (current.left != null) {
                queue.offer(current.left);
            }
            if (current.right != null) {
                queue.offer(current.right);
            }
        }
    }

    return result;
}
```

- **Time:** O(n) — every node is enqueued and dequeued exactly once.
- **Space:** O(n) in the worst case for the queue — specifically, up to roughly half the nodes could be queued simultaneously at the widest level of a very bushy (e.g., complete) tree.

**Why it is presented as a valid, intuitive approach:** This is the most direct, literal translation of the problem statement ("for each level, what's the rightmost node") into code, and is fully correct with O(n) time. Its space usage, O(n) in the worst case (proportional to the tree's maximum width), can be less favorable than the DFS alternative's O(h) (proportional to the tree's height) for a wide, shallow tree, though more favorable for a narrow, very deep one.

## 5. Approach 2 — DFS (Right-First, Record First Node Per Depth)

**Algorithm:**
1. Maintain a `result` list, where the value at index `depth` (once populated) represents the rightmost node's value at that depth.
2. Define a recursive helper `dfs(node, depth)`:
   - If `node` is `null`, return immediately.
   - If `depth == result.size()` (this is the *first* time this depth has been reached during the traversal), add `node.val` to `result`.
   - Recurse into the **right** child first: `dfs(node.right, depth + 1)`.
   - Then recurse into the **left** child: `dfs(node.left, depth + 1)`.
3. Call `dfs(root, 0)` to start, and return `result`.

```java
public List<Integer> rightSideView(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    dfs(root, 0, result);
    return result;
}

private void dfs(TreeNode node, int depth, List<Integer> result) {
    if (node == null) {
        return;
    }

    if (depth == result.size()) {
        result.add(node.val);
    }

    dfs(node.right, depth + 1, result);
    dfs(node.left, depth + 1, result);
}
```

- **Time:** O(n) — every node is visited exactly once across the entire recursion.
- **Space:** O(h), where `h` is the tree's height, for the recursion call stack — O(log n) for a balanced tree, up to O(n) for a completely skewed tree; the `result` list itself is O(number of levels), which is also bounded by O(h).

**Why this is presented as the more space-efficient alternative:** Both approaches share O(n) time; the DFS version's O(h) space can be meaningfully better than the BFS version's O(n) (proportional to width) for trees that are wide but not very deep (e.g., a nearly-complete tree, where width is roughly n/2 but height is only log n) — though for a very deep, narrow tree, the two approaches' space usage converges. The DFS version is also a nice illustration of reframing a seemingly level-order ("BFS-flavored") requirement into a depth-first traversal via a clever visit order and a simple "have I seen this depth yet" check — a technique worth recognizing as reusable for other "one value per level, specific side" problems.

## 6. Dry Run

Example: `root = [1,2,3,null,5,null,4]` (node 1 has children 2,3; node 2 has only a right child 5; node 3 has only a right child 4).
Chosen because it's the canonical example, and it clearly shows that the rightmost *visible* node at a level isn't simply "always the right child of the previous level's rightmost node" — node 5 (reached via node 2's right) and node 4 (reached via node 3's right) both exist at depth 2, with node 4 being the correct rightmost-visible answer since node 3 (whose child it is) is itself to the right of node 2 (5's parent).

Using the DFS approach: `dfs(1, 0)`.

- `depth=0`, `result.size()=0`, so `0==0`: add `1`. `result=[1]`.
- Recurse right first: `dfs(3, 1)`.
  - `depth=1`, `result.size()=1`, so `1==1`: add `3`. `result=[1,3]`.
  - Recurse right: `dfs(4, 2)`.
    - `depth=2`, `result.size()=2`, so `2==2`: add `4`. `result=[1,3,4]`.
    - Recurse right: `dfs(null, 3)` → returns immediately (null).
    - Recurse left: `dfs(null, 3)` → returns immediately (null).
  - Recurse left: `dfs(null, 2)` (node 3 has no left child) → returns immediately.
- Recurse left: `dfs(2, 1)`.
  - `depth=1`, `result.size()=3` now (already populated from the right side), so `1==3` is false: do NOT add.
  - Recurse right: `dfs(5, 2)`.
    - `depth=2`, `result.size()=3`, so `2==3` is false: do NOT add (node 4 already correctly claimed this depth).
    - Recurse right/left: both `null`, return.
  - Recurse left: `dfs(null, 2)` (node 2 has no left child) → returns immediately.

Exit condition: all recursive calls complete.

**Final answer:** `result = [1,3,4]`, matching the expected output exactly — correctly identifying node 4 (not node 5) as the rightmost visible node at depth 2, precisely because the right-first traversal order ensures node 4 (reached via the tree's rightmost path) is recorded before node 5 is ever reached.

## 7. Complexity Summary

| Approach | Time | Space (Auxiliary) | Notes |
|----------|------|--------------------|-------|
| 1. BFS, last node per level | O(n) | O(n) worst case (queue, proportional to tree width) | Correct and intuitive; direct translation of "per level" into code |
| 2. DFS, right-first, first node per depth | O(n) | O(h) (tree height; better than O(n) for wide-but-shallow trees) | Correct and more space-efficient in many cases; elegant reframing |

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|------|-------------------|--------------------------|
| Empty tree (`root == null`, example 4) | Should return an empty list immediately | BFS: the initial `root == null` check returns the empty `result` list right away; DFS: `dfs(null, 0, result)` immediately returns without adding anything, leaving `result` empty |
| A tree where a level's rightmost node is reached via a left-leaning path deeper in a right-side-rooted subtree (as in the dry run, where node 5 is a right child of node 2, yet node 4, a right child of node 3, is the correct answer) | The "rightmost visible node" depends on the overall horizontal position implied by the full root-to-node path, not just whether a node is specifically a "right child" of its immediate parent | Both approaches correctly handle this because they process nodes in a true left-to-right (BFS) or right-first-with-first-wins (DFS) manner based on the *overall* traversal order, not based on any single node's local left/right relationship to its own parent in isolation |
| A completely left-skewed tree (every node has only a left child, no right children at all) | Every single node should appear in the right-side view, since each level has exactly one node (the leftmost, and only, node at that depth), making it also the "rightmost" by default | BFS: each level's batch size is always 1, so that single node is always both the first and last dequeued, correctly added; DFS: every `dfs(node.right, ...)` call immediately hits `null` and returns, so the traversal proceeds entirely via the `left` recursive calls, with each new depth being visited for the first time via that leftward path, correctly recording every node |
| A single-node tree | Should return a list containing just that one node's value | Both approaches trivially handle this as the simplest possible non-empty case, following the same general logic described above |
| Negative node values (constraint allows down to -100) | Should be handled identically to positive values | Neither approach performs any value-based logic beyond adding values to the result list, so sign is irrelevant |
| Maximum constraint size (100 nodes) | Should perform efficiently at the upper bound | Both O(n) time approaches comfortably handle this small size regardless of tree shape |

## 9. Java Notes

- **BFS: tracking `levelSize = queue.size()` before processing each level, then looping exactly that many times:** this is the standard idiom for processing a tree (or graph) level by level using a single shared queue — capturing the size upfront prevents the loop from accidentally also processing nodes belonging to the *next* level (which get enqueued during the current level's processing).
- **DFS: `depth == result.size()` as the "have I recorded this depth yet" check:** a clean, index-free way to track "is this the first time we've reached this depth," since `result.size()` naturally grows by exactly one each time a new depth is first recorded, keeping it always equal to "the next depth we haven't recorded yet."
- **DFS: recursing into `node.right` before `node.left`:** this is the single most important detail of the DFS approach — reversing the usual left-first convention is precisely what guarantees the first node encountered at each depth is the rightmost one.
- **No overflow or numeric risk:** both approaches involve only reference traversal and small bounded value handling (-100 to 100); no arithmetic risks overflow.

## 10. Common Mistakes

- **In the DFS approach, recursing into `node.left` before `node.right` (keeping the usual convention) without adjusting the "first node at this depth" logic accordingly.** This would cause the *leftmost* node at each depth to be recorded first instead of the rightmost, producing a left-side view instead of the required right-side view. Fix: always visit the right child before the left child in this specific DFS formulation.
- **In the BFS approach, recording the *first* node dequeued at each level instead of the *last*.** This would produce a left-side view instead of a right-side view. Fix: always check `i == levelSize - 1` (the last node processed in the current level's batch) before adding to the result.
- **Forgetting to capture `levelSize` before the inner loop begins, and instead checking `queue.size()` dynamically inside the loop.** Since children get enqueued during the same loop that's dequeuing the current level's nodes, the queue's size changes mid-loop, and using a dynamically-checked size would incorrectly mix nodes from two different levels together. Fix: always capture the level's size into a fixed local variable before the inner loop starts.
- **Assuming "the right child of the rightmost node from the previous level" is always the correct next answer, without accounting for cases where that rightmost node has no right child but a sibling further left does (as demonstrated in the dry run).** This flawed heuristic would miss the correct rightmost node in cases where the true rightmost branch deeper in the tree comes from a structurally different path than a naive "always go right from the previous answer" greedy approach would suggest. Fix: rely on one of the two properly derived techniques (BFS per-level, or right-first DFS with first-at-depth), both of which correctly handle this by considering the full tree structure at each level/depth, not just a single greedy path.

## 11. Interview Takeaway

- **Trigger sentence to memorize:** "What's visible from one side of a tree, top to bottom → either BFS level by level, keeping the last node of each level, or DFS visiting the 'near' side first and recording only the first node seen at each new depth."
- **90-second explanation:** "There are two natural ways to think about this. The straightforward way is breadth-first: I process the tree level by level using a queue, and for each level, I specifically remember the very last node I process in that level's batch, since my queue naturally preserves left-to-right order within each level, that last node is guaranteed to be the rightmost one at that depth. The more elegant, space-efficient alternative reframes this as a depth-first traversal: if I always visit a node's right child before its left child, then the very first time I reach any given depth during that traversal, I'm guaranteed to be looking at the rightmost node at that depth — anything further left at that same depth can only be reached by a path that branches left at some point, and that leftward branch always gets explored after the corresponding rightward one. So I just track the current depth as I recurse, and only record a node's value the first time I reach a depth I haven't seen before. Both approaches run in O(n) time, visiting every node once; the DFS version typically uses less extra space for trees that are wide but not very deep, since its space usage depends on the tree's height rather than its width."
- **Related problems using this pattern:**
  - LeetCode 102 — Binary Tree Level Order Traversal (the base BFS technique this problem specializes)
  - LeetCode 103 — Binary Tree Zigzag Level Order Traversal (a related level-order variant)
  - LeetCode 515 — Find Largest Value in Each Tree Row (a closely related "one value per level" problem, using max instead of rightmost)

## 12. Recall Questions

**Q:** Why does taking the last node dequeued from the queue during each level's processing correctly identify the rightmost node at that level?
**A:** The queue preserves left-to-right order within each level by construction, since every node enqueues its left child before its right child, and nodes themselves are processed from the queue in the same left-to-right order they were added, so the last node processed in a given level's batch is always the one that was positioned furthest to the right.

**Q:** Why does visiting the right child before the left child in a depth-first traversal guarantee that the first node reached at any given depth is the rightmost one at that depth?
**A:** Any node positioned further to the left at the same depth can only be reached via a path that branches leftward at some ancestor, and because right is always explored before left at every single node during this traversal, that leftward branch is always fully explored only after the corresponding rightward branch has already been completely explored, including reaching at least that same depth.

**Q:** Why is it incorrect to assume the rightmost node at each level is always simply "the right child of the previous level's rightmost node"?
**A:** A node's position in the right-side view depends on its overall horizontal position implied by its full path from the root, not just its immediate relationship to its own parent, so the true rightmost branch at a deeper level can originate from a different, more complex path than a naive greedy "always take the right child" approach would follow, as shown when a node reached via a right-side ancestor's right child turns out to be positioned further right than a node reached via a different ancestor's own right child.

**Q:** Why does the DFS approach generally use less space than the BFS approach for a tree that is wide but not very deep?
**A:** The DFS approach's space usage is bounded by the tree's height (the maximum recursion depth along any single path), while the BFS approach's space usage is bounded by the tree's maximum width (the most nodes that could be queued simultaneously at the widest level), and for a wide, shallow tree these two measures can differ substantially, with height being much smaller than width.

**Q:** What specific error would occur if the BFS approach checked `queue.size()` dynamically inside the inner loop instead of capturing it once beforehand into a fixed variable?
**A:** Since child nodes get enqueued onto the very same queue during the same loop that's dequeuing the current level's nodes, the queue's size changes partway through processing a level, so a dynamically-checked size would cause the loop to incorrectly continue processing into nodes that actually belong to the next level, mixing two different levels together rather than correctly isolating one level's batch at a time.

## 13. Final Code

```java
public List<Integer> rightSideView(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    dfs(root, 0, result);
    return result;
}

private void dfs(TreeNode node, int depth, List<Integer> result) {
    if (node == null) {
        return;
    }

    if (depth == result.size()) {
        result.add(node.val);
    }

    dfs(node.right, depth + 1, result);
    dfs(node.left, depth + 1, result);
}
```

## 14. Self-Test

You have the root of a binary tree. Return the values of the nodes visible from the right side, top to bottom. Think about why taking the last node of each BFS level works, and why a DFS that visits right before left, recording only the first node seen at each new depth, achieves the exact same result more space-efficiently.
