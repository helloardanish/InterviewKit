---
title: "0427 — Construct Quad Tree"
layout: default
parent: Divide-and-Conquer
grand_parent: Leetcode
nav_order: 427
permalink: /16-Divide-and-Conquer/0427-construct-quad-tree/

problem: 427 - Construct Quad Tree
url: "https://leetcode.com/problems/construct-quad-tree/"
difficulty: Medium
section: Divide and Conquer
patterns: [divide-and-conquer, recursion, bottom-up-merge]
data_structures: [matrix, tree]
time: O(n^2)
space: O(log n)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0427 — Construct Quad Tree**

| Field | Value |
|---|---|
| **Pattern** | Divide and Conquer + Recursive Tree Construction |
| **Difficulty** | Medium |
| **Section** | Divide and Conquer |
| **Time** | O(n^2) |
| **Space** | O(log n) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Meta, Microsoft, Bloomberg |
| **LeetCode** | [Construct Quad Tree](https://leetcode.com/problems/construct-quad-tree/) |


## 1. Problem in Simple Words

**Given:** an `n x n` grid of `0`s and `1`s.

**Required:** build and return the root of its Quad-Tree. A node covers a square region. If the region is all one value, the node is a **leaf** (`isLeaf = true`, `val` = that value, four null children). Otherwise it is an **internal node** (`isLeaf = false`, `val` can be anything) with four children covering the top-left, top-right, bottom-left and bottom-right quadrants, built the same way recursively.

**Constraints that matter:**
- `n == 2^x` with `0 <= x <= 6` — so `n` is at most 64 (4,096 cells). Because `n` is a power of two, halving a region always gives four exact squares; there is never an odd side to worry about. `n = 1` is allowed and yields a single leaf.
- The grid contains only `0` and `1`, so "uniform" means "every cell equals the first cell".
- For internal nodes, `val` may be `true` or `false`; both are accepted, so there is nothing to compute for it.
- Recursion depth is at most `log2(64) + 1 = 7`, so stack depth is a non-issue.

## 2. Recognition Signals

- "Recursively split a region into four quadrants until each piece is uniform" is a direct statement of **divide and conquer on a square grid**.
- The tree definition itself is recursive: a node is either a leaf or has four children built the same way, so the construction mirrors the definition.
- Halving a side each step (`n = 2^x`) means a small, fixed recursion depth.
- Pattern: **recurse on four quadrants, then merge: if all four results are leaves with the same value, collapse them into one leaf.**

## 3. Core Idea

Define `build(row, col, size)` for the square starting at `(row, col)` with side `size`. A `1 x 1` square is trivially a leaf. For larger squares, build the four quadrant subtrees first. If all four are leaves holding the same value, the whole square is uniform, so return a single leaf with that value. Otherwise return an internal node holding the four subtrees.

**Why it's correct:** a square is uniform exactly when its four quadrants are each uniform and share the same value. By induction, a quadrant that came back as a leaf is uniform with that value, and a quadrant that came back as an internal node contains at least two different values (it was not collapsed), so the parent cannot be uniform. Collapsing is therefore correct, and it cascades upward naturally because a collapsed child is returned as a leaf.

**Invariant:** `build(row, col, size)` returns a leaf if and only if every cell in that square has the same value, and otherwise returns an internal node whose four children correctly represent the four quadrants.

## 4. Approach 1 — Brute Force (Scan, Then Split)

At each recursive call, scan the entire square to test whether it is uniform. If so, return a leaf; if not, split into quadrants and recurse.

```java
public Node constructBruteForce(int[][] grid) {
    return buildTopDown(grid, 0, 0, grid.length);
}

private Node buildTopDown(int[][] grid, int row, int col, int size) {
    if (isUniform(grid, row, col, size)) {
        return new Node(grid[row][col] == 1, true);
    }

    int half = size / 2;
    return new Node(true, false,
        buildTopDown(grid, row, col, half),
        buildTopDown(grid, row, col + half, half),
        buildTopDown(grid, row + half, col, half),
        buildTopDown(grid, row + half, col + half, half));
}

private boolean isUniform(int[][] grid, int row, int col, int size) {
    int first = grid[row][col];
    for (int r = row; r < row + size; r++) {
        for (int c = col; c < col + size; c++) {
            if (grid[r][c] != first) {
                return false;
            }
        }
    }
    return true;
}
```

**Time:** O(n² log n) in the worst case. The squares at any one recursion level partition the grid, so the scans at one level cost at most n² cells total, and there are up to `log2(n)` levels. A near-uniform grid with a single different cell in the last position forces nearly full scans at every level.
**Space:** O(log n) recursion stack, plus the output tree.

Not optimal: the same cells are rescanned at every level of the recursion. Their uniformity is already implied by the results of the sub-calls, so scanning is redundant work.

## 5. Approach 2 — Optimized (Bottom-Up Build and Collapse)

1. If `size == 1`, return a leaf for that cell.
2. Recursively build the four quadrants (`half = size / 2`).
3. If all four are leaves and all have the same `val`, return a single leaf with that `val`.
4. Otherwise return an internal node with the four children.

```java
class Solution {
    public Node construct(int[][] grid) {
        return build(grid, 0, 0, grid.length);
    }

    private Node build(int[][] grid, int row, int col, int size) {
        if (size == 1) {
            return new Node(grid[row][col] == 1, true);
        }

        int half = size / 2;
        Node topLeft = build(grid, row, col, half);
        Node topRight = build(grid, row, col + half, half);
        Node bottomLeft = build(grid, row + half, col, half);
        Node bottomRight = build(grid, row + half, col + half, half);

        if (topLeft.isLeaf && topRight.isLeaf && bottomLeft.isLeaf && bottomRight.isLeaf
                && topLeft.val == topRight.val
                && topLeft.val == bottomLeft.val
                && topLeft.val == bottomRight.val) {
            return new Node(topLeft.val, true);
        }

        return new Node(true, false, topLeft, topRight, bottomLeft, bottomRight);
    }
}
```

**Time:** O(n²). The recurrence is `T(n) = 4T(n/2) + O(1)`, which solves to O(n²), linear in the number of cells: each cell is read exactly once, at a `1 x 1` base case, and each merge does constant work.
**Space:** O(log n) auxiliary for the recursion stack (the returned tree can have up to O(n²) nodes in the worst case, which is output, not auxiliary).

This is optimal in time: every cell must be read at least once, so Ω(n²) is unavoidable. The trade-off is that it creates `1 x 1` leaf nodes for every cell and discards them when collapsing, which is at most 4,096 short-lived nodes here. If that allocation mattered, a 2D prefix-sum table lets you test whether a square is uniform in O(1) and build only the nodes that survive, still O(n²) overall.

## 6. Dry Run

Example 2 (8 x 8). Rows 0-1: `1111 0000`; rows 2-3: `1111 1111`; rows 4-7: `1111 0000`. The tricky part is that one quadrant collapses and one does not.

| Call (row, col, size) | Region | Children returned | Collapse? | Returns |
|---|---|---|---|---|
| (0,0,4) top-left | all `1` | four 2x2 blocks, each collapses to leaf `1`; four leaf `1`s | yes (twice, cascading) | leaf `1` |
| (0,4,4) top-right | rows 0-1 are `0`, rows 2-3 are `1` | 2x2 blocks: leaf `0`, leaf `0`, leaf `1`, leaf `1` | no (values differ) | internal node |
| (4,0,4) bottom-left | all `1` | collapses | yes | leaf `1` |
| (4,4,4) bottom-right | all `0` | collapses | yes | leaf `0` |
| (0,0,8) root | whole grid | leaf `1`, internal, leaf `1`, leaf `0` | no (top-right is not a leaf) | internal root |

Exit condition: the root call returns. The tree has a root with four children, where the top-right child has four leaf children, and this matches the expected serialization `[[0,1],[1,1],[0,1],[1,1],[1,0],null,null,null,null,[1,0],[1,0],[1,1],[1,1]]`.

## 7. Complexity Summary

| Approach | Time | Space | Notes |
|---|---|---|---|
| Scan for uniformity, then split | O(n² log n) | O(log n) aux | Rescans cells at each recursion level |
| Bottom-up build and collapse | O(n²) | O(log n) aux | Each cell read once; output tree is O(n²) worst case |
| Prefix sums plus top-down | O(n²) | O(n²) | O(1) uniformity check; extra table |

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|---|---|---|
| `n = 1` | Smallest input, `half` would be 0 | `size == 1` base case returns a leaf before any halving |
| Entire grid uniform | Result must be a single leaf root with null children | Every level collapses, so the root is returned as a leaf |
| Checkerboard (no uniform block above 1x1) | Maximum node count; nothing ever collapses | Every merge fails the equal-values test, producing a full tree |
| Uniform quadrant next to mixed quadrants | Only some children collapse | Each quadrant is decided independently by its own recursive call |
| Collapse that enables a further collapse | Four 2x2 uniform blocks make a uniform 4x4 | Children are returned already collapsed, so the parent sees four leaves and collapses again |
| Four internal-node children that all have `val = true` | `val` on internal nodes is arbitrary, so equal `val` means nothing | The collapse test also requires `isLeaf` on all four children |

## 9. Java Notes

- `Node` is LeetCode's class with `boolean val`, `boolean isLeaf`, and four child fields. Use `new Node(val, true)` for leaves (children default to `null`) and the six-argument constructor for internal nodes.
- Convert the grid's `int` to the node's `boolean` with `grid[row][col] == 1`. Do not assign an `int` directly.
- Pass `(row, col, size)` indices into the original grid rather than copying subarrays; copying would add O(n²) work per level for no benefit.
- Because Java evaluates arguments left to right, the four recursive calls in the brute-force constructor run in topLeft, topRight, bottomLeft, bottomRight order. The order does not affect correctness here, but keeping it consistent makes tracing easier.
- Quadrant offsets: top-right adds `half` to the column, bottom-left adds `half` to the row, bottom-right adds `half` to both.

## 10. Common Mistakes

- Collapsing based only on equal `val` among the four children, without checking `isLeaf`. Internal nodes can carry any `val`, so four internal children with `true` would be wrongly merged. Fix: require all four children to be leaves.
- Swapping quadrant offsets (for example, adding `half` to the row for the top-right). Fix: top-right is `(row, col + half)`, bottom-left is `(row + half, col)`.
- Missing the `size == 1` base case, so `half` becomes 0 and the recursion never terminates. Fix: stop at a single cell.
- Scanning each square for uniformity at every level, which silently makes the solution O(n² log n). Fix: build children first and collapse from their results.
- Giving a leaf non-null children, or setting `isLeaf = true` on an internal node. Fix: leaves use the two-argument constructor.
- Copying sub-grids (for example with `Arrays.copyOfRange` per row) at each call. Fix: pass indices.

## 11. Interview Takeaway

- **Trigger sentence:** "Recursively partition a square into four quadrants until uniform means divide and conquer: build the four children first, then collapse them into one leaf if they are leaves with equal values."
- **90-second explanation:** I define a recursive function over a square given by its top-left corner and side length. A one-cell square is a leaf. Otherwise I build the four quadrants recursively, then look at the results: if all four came back as leaves with the same value, the whole square is uniform, so I return one leaf instead of four; otherwise I return an internal node holding the four subtrees. Checking `isLeaf` as well as equal values matters, since an internal node's value is arbitrary. This is bottom-up, so I never rescan cells to test uniformity, and the work satisfies `T(n) = 4T(n/2) + O(1)`, which is O(n²), linear in the number of cells and optimal since every cell must be read. Recursion depth is only log2(n), at most 7 here. The simpler top-down version that scans each square before splitting works too but costs O(n² log n) in the worst case.
- **Related problems:** 558 (Logical OR of Two Binary Grids Represented as Quad-Trees), 108 (Convert Sorted Array to Binary Search Tree), 105 (Construct Binary Tree from Preorder and Inorder Traversal).

## 12. Recall Questions

**Q:** Why can the bottom-up approach skip scanning a square to test uniformity?
**A:** A square is uniform exactly when its four quadrants all came back as leaves with the same value, which is already known from the recursive results, so no cell needs to be rescanned.

**Q:** Why must the collapse check include `isLeaf` for all four children?
**A:** An internal node's `val` is arbitrary, so equal `val` fields among internal nodes say nothing about uniformity; only leaves carry a meaningful value.

**Q:** Why is the top-down scan-then-split version O(n² log n) in the worst case?
**A:** The squares at one recursion level partition the grid, so each level scans up to n² cells, and there are up to log2(n) levels.

**Q:** Why is O(n²) a lower bound for this problem?
**A:** Any correct algorithm must read every cell at least once, since a single unread cell could change which nodes are leaves.

**Q:** Why does the recursion need no special handling for odd sizes?
**A:** `n` is a power of two, so halving always produces four exact squares until the side reaches 1.

## 13. Final Code

```java
/*
// Definition for a QuadTree node.
class Node {
    public boolean val;
    public boolean isLeaf;
    public Node topLeft;
    public Node topRight;
    public Node bottomLeft;
    public Node bottomRight;

    public Node() {}

    public Node(boolean _val, boolean _isLeaf) {
        val = _val;
        isLeaf = _isLeaf;
    }

    public Node(boolean _val, boolean _isLeaf, Node _topLeft, Node _topRight, Node _bottomLeft, Node _bottomRight) {
        val = _val;
        isLeaf = _isLeaf;
        topLeft = _topLeft;
        topRight = _topRight;
        bottomLeft = _bottomLeft;
        bottomRight = _bottomRight;
    }
}
*/

class Solution {
    public Node construct(int[][] grid) {
        return build(grid, 0, 0, grid.length);
    }

    private Node build(int[][] grid, int row, int col, int size) {
        if (size == 1) {
            return new Node(grid[row][col] == 1, true);
        }

        int half = size / 2;
        Node topLeft = build(grid, row, col, half);
        Node topRight = build(grid, row, col + half, half);
        Node bottomLeft = build(grid, row + half, col, half);
        Node bottomRight = build(grid, row + half, col + half, half);

        if (topLeft.isLeaf && topRight.isLeaf && bottomLeft.isLeaf && bottomRight.isLeaf
                && topLeft.val == topRight.val
                && topLeft.val == bottomLeft.val
                && topLeft.val == bottomRight.val) {
            return new Node(topLeft.val, true);
        }

        return new Node(true, false, topLeft, topRight, bottomLeft, bottomRight);
    }
}
```

## 14. Self-Test

Given an `n x n` grid of `0`s and `1`s where `n` is a power of two, build its Quad-Tree: a region that is all one value becomes a leaf, otherwise an internal node with four children for the top-left, top-right, bottom-left and bottom-right quadrants. Re-derive: recurse on `(row, col, size)`; a size-1 square is a leaf; otherwise build the four quadrants first, and if all four are leaves with the same value return one leaf with that value, else return an internal node holding the four children.
