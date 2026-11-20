---
title: "0200 — Number of Islands"
layout: default
parent: Graph General
grand_parent: Leetcode
nav_order: 200
permalink: /12-Graph-General/0200-number-of-islands/

problem: 200 - Number of Islands
url: [https://leetcode.com/problems/number-of-islands/](https://leetcode.com/problems/number-of-islands/)
difficulty: Medium
section: Graph / Matrix BFS-DFS
patterns: [flood-fill, bfs, connected-components]
data_structures: [matrix, queue]
time: O(m * n)
space: O(m * n)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

**# 0200 — Number of Islands**

| Field | Value |
|---|---|
| **Pattern** | Flood Fill + BFS (Connected Components) |
| **Difficulty** | Medium |
| **Section** | Graph / Matrix BFS-DFS |
| **Time** | O(m * n) |
| **Space** | O(m * n) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Amazon, Google, Meta, Microsoft, Bloomberg |
| **LeetCode** | [Number of Islands](https://leetcode.com/problems/number-of-islands/) |


## 1. Problem in Simple Words

**Given:** an `m x n` grid of characters `'1'` (land) and `'0'` (water).

**Required:** count the islands, where an island is a maximal group of land cells connected **horizontally or vertically** (not diagonally).

**Constraints that matter:**
- `1 <= m, n <= 300` — up to 90,000 cells. O(m·n) is required and easily fast enough. This size is also the warning sign for recursion: a single snake-shaped island can be 90,000 cells long, so a recursive DFS can be 90,000 frames deep.
- `grid[i][j]` is `'0'` or `'1'` — these are `char`s, not ints. Comparing against the int `1` is a silent bug in Java.
- Connectivity is 4-directional. Two land cells touching only at a corner belong to different islands.
- The grid is never empty (`m, n >= 1`), so `grid[0].length` is always safe.

## 2. Recognition Signals

- "Count groups of connected cells" in a grid means **connected components**.
- A grid where adjacency is defined by neighbors (up, down, left, right) is an implicit graph: cells are nodes, adjacent land cells are edges. No adjacency list needs to be built.
- "Maximal group" means once you find one land cell, you should consume its entire group so it is never counted twice.
- Pattern: **scan the grid; on each unvisited land cell, count one island and flood-fill (DFS or BFS) to mark its whole component.**

## 3. Core Idea

Scan every cell once. The first time the scan meets a land cell, it must belong to an island that has not been counted yet, because every cell of any previously counted island has already been erased. Increment the counter, then flood-fill from that cell, turning every connected land cell into water so the scan never counts that island again.

**Why it's correct:** flood fill from a cell reaches exactly its connected component, because it expands across every 4-neighbor that is land and stops at water and grid edges. Erasing the component guarantees each island contributes exactly one count: the one at its first-scanned cell.

**Invariant:** when the scan reaches a cell, every land cell belonging to an already-counted island has been turned to `'0'`, so any `'1'` seen is the first cell of a new island.

## 4. Approach 1 — Recursive DFS Flood Fill

1. Scan row by row. On `'1'`, increment `islands` and call `sink(r, c)`.
2. `sink` returns if the cell is out of bounds or not `'1'`; otherwise it sets the cell to `'0'` and recurses in the four directions.

```java
public int numIslandsDFS(char[][] grid) {
    int islands = 0;
    for (int r = 0; r < grid.length; r++) {
        for (int c = 0; c < grid[0].length; c++) {
            if (grid[r][c] == '1') {
                islands++;
                sink(grid, r, c);
            }
        }
    }
    return islands;
}

private void sink(char[][] grid, int r, int c) {
    if (r < 0 || c < 0 || r >= grid.length || c >= grid[0].length || grid[r][c] != '1') {
        return;
    }
    grid[r][c] = '0';
    sink(grid, r + 1, c);
    sink(grid, r - 1, c);
    sink(grid, r, c + 1);
    sink(grid, r, c - 1);
}
```

**Time:** O(m · n). Each cell is visited a constant number of times: once by the scan, plus up to four times as a neighbor check.
**Space:** O(m · n) worst case for the recursion stack (a snake-shaped island of ~90,000 cells).

This has the same time complexity as the optimized version, so it is not asymptotically worse, but it is less robust: with up to 90,000 cells, the worst-case recursion depth can exceed Java's default thread stack and throw `StackOverflowError` (the exact limit depends on JVM settings and frame size).

## 5. Approach 2 — Optimized (Iterative BFS Flood Fill)

1. Scan row by row. On `'1'`, increment `islands`, set the cell to `'0'`, and enqueue it.
2. While the queue is non-empty, poll a cell and examine its four neighbors. For each in-bounds `'1'` neighbor, set it to `'0'` **immediately** and enqueue it.
3. Cells are encoded as `r * cols + c` so the queue stores plain `Integer`s instead of coordinate arrays.

```java
public int numIslands(char[][] grid) {
    int rows = grid.length;
    int cols = grid[0].length;
    int[] dr = {1, -1, 0, 0};
    int[] dc = {0, 0, 1, -1};
    int islands = 0;
    Queue<Integer> queue = new ArrayDeque<>();

    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            if (grid[r][c] != '1') {
                continue;
            }
            islands++;
            grid[r][c] = '0';
            queue.offer(r * cols + c);

            while (!queue.isEmpty()) {
                int cell = queue.poll();
                int cr = cell / cols;
                int cc = cell % cols;
                for (int d = 0; d < 4; d++) {
                    int nr = cr + dr[d];
                    int nc = cc + dc[d];
                    if (nr >= 0 && nc >= 0 && nr < rows && nc < cols && grid[nr][nc] == '1') {
                        grid[nr][nc] = '0';
                        queue.offer(nr * cols + nc);
                    }
                }
            }
        }
    }
    return islands;
}
```

**Time:** O(m · n). Every cell is enqueued at most once because it is erased at enqueue time.
**Space:** O(m · n) worst case for the queue, though usually much smaller: on an open grid the frontier is a diagonal wavefront of about min(m, n) cells, but water cells inside an island can force longer frontiers. There is no recursion stack, so depth is never a concern.

This is optimal in time since every cell must be inspected. The trade-off made here is mutating the input grid to avoid a separate `visited` array (which would cost O(m · n) extra space). If the input must be preserved, use a `boolean[][] visited` or restore the grid afterward. Union-Find also solves this in near-linear time, and is preferable when land cells are added over time (the "Number of Islands II" style of problem) rather than for a single static grid.

## 6. Dry Run

Example 2:

```
1 1 0 0 0
1 1 0 0 0
0 0 1 0 0
0 0 0 1 1
```

| Scan reaches | Cell value | Action | islands | Cells erased |
|---|---|---|---|---|
| (0,0) | '1' | new island, BFS | 1 | (0,0), (0,1), (1,0), (1,1) |
| (0,1) .. (1,1) | '0' now | skip (already erased) | 1 | none |
| (2,2) | '1' | new island, BFS; all 4 neighbors are water | 2 | (2,2) |
| (3,3) | '1' | new island, BFS | 3 | (3,3), (3,4) |
| (3,4) | '0' now | skip | 3 | none |

Exit condition: the scan finishes the last cell. Final answer: `3`. The tricky part is (2,2) and (3,3): they touch only diagonally, so they correctly count as separate islands.

## 7. Complexity Summary

| Approach | Time | Space | Notes |
|---|---|---|---|
| Recursive DFS flood fill | O(m · n) | O(m · n) worst-case stack | Simple, but can overflow the stack on one huge snake-shaped island |
| Iterative BFS flood fill | O(m · n) | O(m · n) worst-case queue (small on open grids) | No recursion; mutates input grid |
| Union-Find | O(m · n · α(m · n)) | O(m · n) | Useful for incremental land additions |

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|---|---|---|
| 1 x 1 grid `["1"]` | Smallest input; all four neighbors are out of bounds | Bounds check precedes every grid access; one island counted |
| 1 x 1 grid `["0"]` | No land at all | Scan never enters the island branch; returns 0 |
| All water | Answer is 0 | Every cell is skipped |
| All land | One giant island; the BFS touches all 90,000 cells | Cells are erased on enqueue, so each is enqueued once; no recursion depth issue |
| Diagonal-only neighbors | Easy to over-merge | Only the four orthogonal directions are in `dr`/`dc` |
| Snake-shaped island | Maximizes DFS recursion depth | BFS uses a heap-allocated queue, so depth is irrelevant |
| Single row or single column | Frontier and bounds are one-dimensional | Same code works; the queue holds at most 2 cells |

## 9. Java Notes

- The grid is `char[][]`, so compare with `'1'` and `'0'`. `grid[r][c] == 1` compiles but compares the char code 49 against 1 and is always false.
- Check bounds before indexing, and put the bounds tests ahead of `grid[nr][nc] == '1'` in the `&&` chain so short-circuiting prevents `ArrayIndexOutOfBoundsException`.
- Encoding a cell as `r * cols + c` keeps the queue a `Queue<Integer>` and avoids allocating an `int[2]` per cell. Maximum value here is under 90,000, so `int` is safe.
- `ArrayDeque` is the idiomatic queue; it rejects `null`, which is irrelevant here since only boxed ints are stored.
- A `dr`/`dc` direction array replaces four copy-pasted neighbor blocks and keeps bounds logic in one place.

## 10. Common Mistakes

- Marking a cell visited when it is **polled** instead of when it is **enqueued**. The same cell can then be enqueued many times by different neighbors, inflating the queue and the runtime. Fix: set `grid[nr][nc] = '0'` at the moment of `offer`.
- Comparing against the int `1` instead of the char `'1'`, so no land is ever found and the answer is always 0. Fix: use `'1'`.
- Including diagonal neighbors, which merges islands that only touch at corners. Fix: use exactly four orthogonal directions.
- Forgetting to erase the starting cell before the BFS loop, so it is re-counted or re-enqueued via a neighbor. Fix: set it to `'0'` when it is first enqueued.
- Using recursive DFS on a worst-case grid and hitting `StackOverflowError`. Fix: use BFS or an explicit stack.

## 11. Interview Takeaway

- **Trigger sentence:** "Counting groups of connected cells in a grid is connected components: scan, and on each unvisited land cell, count one and flood-fill its whole component."
- **90-second explanation:** I scan the grid cell by cell. When I hit a land cell, I know it starts an island I have not counted, because I erase every island I finish. So I increment the counter and flood-fill from that cell with a BFS, checking the four orthogonal neighbors and turning each land neighbor to water the moment I enqueue it, so no cell is queued twice. That erases the whole island, and the scan moves on. Every cell is enqueued at most once and checked a constant number of times, so it is O(m·n) time. I choose BFS over recursive DFS because a single winding island can be 90,000 cells long, which risks a stack overflow in Java; BFS keeps its frontier on the heap instead. It does mutate the grid; if I must preserve it, I would use a visited array or restore the cells afterward.
- **Related problems:** 695 (Max Area of Island), 130 (Surrounded Regions), 463 (Island Perimeter), 547 (Number of Provinces), 133 (Clone Graph).

## 12. Recall Questions

**Q:** Why does each island get counted exactly once?
**A:** The scan only counts a land cell it has not seen erased, and the flood fill erases the entire connected component immediately, so no other cell of that island can trigger another count.

**Q:** Why mark a cell visited at enqueue time rather than at poll time?
**A:** If marking waits until poll, several neighbors can enqueue the same cell before it is processed, causing duplicates and extra work. Marking at enqueue makes each cell enter the queue at most once.

**Q:** Why can recursive DFS fail on this problem while BFS cannot?
**A:** A single snake-shaped island can force a recursion depth near 90,000, which can exceed the default thread stack. BFS keeps its frontier in a heap-allocated queue instead.

**Q:** How much space can the BFS queue need?
**A:** On an open grid the frontier is a diagonal wavefront of about min(m, n) cells, but walls can force longer frontiers, so the safe worst-case bound is O(m · n).

**Q:** When would Union-Find be preferable to flood fill?
**A:** When land cells are added incrementally and the island count must be reported after each addition, since Union-Find supports online merging without rescanning the grid.

## 13. Final Code

```java
import java.util.*;

class Solution {
    public int numIslands(char[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;
        int[] dr = {1, -1, 0, 0};
        int[] dc = {0, 0, 1, -1};
        int islands = 0;
        Queue<Integer> queue = new ArrayDeque<>();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] != '1') {
                    continue;
                }
                islands++;
                grid[r][c] = '0';
                queue.offer(r * cols + c);

                while (!queue.isEmpty()) {
                    int cell = queue.poll();
                    int cr = cell / cols;
                    int cc = cell % cols;
                    for (int d = 0; d < 4; d++) {
                        int nr = cr + dr[d];
                        int nc = cc + dc[d];
                        if (nr >= 0 && nc >= 0 && nr < rows && nc < cols && grid[nr][nc] == '1') {
                            grid[nr][nc] = '0';
                            queue.offer(nr * cols + nc);
                        }
                    }
                }
            }
        }
        return islands;
    }
}
```

## 14. Self-Test

Given an `m x n` grid of `'1'` (land) and `'0'` (water) characters, count the connected groups of land, where cells connect only horizontally or vertically. Re-derive: scan every cell; on an unseen `'1'`, count one island and run a BFS from it that turns each reached land cell to `'0'` at the moment it is enqueued, so the entire island is erased before the scan continues.
