---
title: "0130 — Surrounded Regions"
layout: default
parent: Graph General
grand_parent: Leetcode
nav_order: 130
permalink: /12-Graph-General/0130-surrounded-regions/

problem: 130 - Surrounded Regions
url: https://leetcode.com/problems/surrounded-regions/
difficulty: Medium
section: Graph / Matrix BFS-DFS
patterns: [multi-source-bfs, reverse-thinking, flood-fill]
data_structures: [matrix, queue]
time: O(m * n)
space: O(m * n)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

**# 0130 — Surrounded Regions**

| Field | Value |
|---|---|
| **Pattern** | Multi-Source BFS + Reverse Thinking (Flood Fill) |
| **Difficulty** | Medium |
| **Section** | Graph / Matrix BFS-DFS |
| **Time** | O(m * n) |
| **Space** | O(m * n) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Microsoft, Meta |
| **LeetCode** | [Surrounded Regions](https://leetcode.com/problems/surrounded-regions/) |

## 1. Problem in Simple Words

**Given:** an `m x n` board of `'X'` and `'O'`.

**Required:** modify the board in place so that every region of connected `'O'` cells (connected horizontally or vertically) that does **not** touch the board's edge is replaced by `'X'`. Regions that contain at least one `'O'` on the edge stay as they are. Nothing is returned.

**Constraints that matter:**
- `1 <= m, n <= 200` — at most 40,000 cells. O(m·n) is the target. A snake-shaped region can be tens of thousands of cells long, so recursive DFS risks a stack overflow.
- `board[i][j]` is `'X'` or `'O'` — only these two characters appear in the input, so any other character (such as `'#'`) is free to use as a temporary marker without colliding with real data.
- "In place, return nothing" — rules out building and returning a new board; the algorithm must mutate the given one.
- Connectivity is 4-directional; diagonal contact does not connect regions.

## 2. Recognition Signals

- "Surrounded" is defined by what **cannot reach the border**. A property defined by the absence of a path to somewhere is usually easier to compute from the other direction: start from the border and find everything that *can* reach it.
- "Region of connected cells" is a connected-components / flood-fill problem on an implicit grid graph.
- The border is a natural set of starting points, which makes this a **multi-source** search with all border `'O'` cells enqueued up front.
- Pattern: **reverse the question. Flood from the border to mark safe cells, then capture everything unmarked.**

## 3. Core Idea

An `'O'` region survives exactly when some cell of it lies on the border. So instead of asking "is this region enclosed?" for each region, mark every `'O'` that is connected to a border `'O'` as safe (temporarily as `'#'`). After that, any `'O'` still unmarked is not connected to the border, so it is surrounded and becomes `'X'`. Finally, restore the safe cells from `'#'` back to `'O'`.

**Why it's correct:** the set of cells reachable from border `'O'` cells through `'O'` cells is exactly the union of all regions that touch the border. The complement within the `'O'` cells is exactly the set of regions that do not touch it, which is the definition of surrounded.

**Invariant:** after the flood finishes, a cell is `'#'` if and only if it was an `'O'` connected (4-directionally, through `'O'` cells) to a border `'O'`.

## 4. Approach 1 — Brute Force

For every `'O'` cell, run a fresh BFS to test whether it can reach a border cell through `'O'` cells. Cells that cannot are flagged; flip all flagged cells at the end (deciding on the original board so flips don't affect later checks).

```java
public void solveBruteForce(char[][] board) {
    int rows = board.length;
    int cols = board[0].length;
    boolean[][] capture = new boolean[rows][cols];

    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            if (board[r][c] == 'O' && !canReachBorder(board, r, c)) {
                capture[r][c] = true;
            }
        }
    }

    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            if (capture[r][c]) {
                board[r][c] = 'X';
            }
        }
    }
}

private boolean canReachBorder(char[][] board, int startR, int startC) {
    int rows = board.length;
    int cols = board[0].length;
    int[] dr = {1, -1, 0, 0};
    int[] dc = {0, 0, 1, -1};
    boolean[][] visited = new boolean[rows][cols];
    Queue<int[]> queue = new ArrayDeque<>();
    queue.offer(new int[] {startR, startC});
    visited[startR][startC] = true;

    while (!queue.isEmpty()) {
        int[] cell = queue.poll();
        if (cell[0] == 0 || cell[1] == 0 || cell[0] == rows - 1 || cell[1] == cols - 1) {
            return true;
        }
        for (int d = 0; d < 4; d++) {
            int nr = cell[0] + dr[d];
            int nc = cell[1] + dc[d];
            if (nr >= 0 && nc >= 0 && nr < rows && nc < cols
                    && board[nr][nc] == 'O' && !visited[nr][nc]) {
                visited[nr][nc] = true;
                queue.offer(new int[] {nr, nc});
            }
        }
    }
    return false;
}
```

**Time:** O((m·n)²) worst case. Up to m·n `'O'` cells each start a BFS that can touch up to m·n cells, and each call also allocates an O(m·n) `visited` array.
**Space:** O(m·n) for `capture`, plus O(m·n) per BFS call.

Not optimal: every cell in the same region asks the same question and re-explores the same cells. The answer is a property of the whole region, so one flood fill per region (or one flood from the border) is enough.

## 5. Approach 2 — Optimized (Multi-Source BFS from the Border)

1. Scan the board. Every border cell that is `'O'` is changed to `'#'` and enqueued.
2. BFS: poll a cell; for each in-bounds neighbor that is `'O'`, change it to `'#'` immediately and enqueue it.
3. Final pass over the board: `'O'` (unreached, so surrounded) becomes `'X'`; `'#'` (safe) becomes `'O'`.

```java
public void solve(char[][] board) {
    int rows = board.length;
    int cols = board[0].length;
    int[] dr = {1, -1, 0, 0};
    int[] dc = {0, 0, 1, -1};
    Queue<Integer> queue = new ArrayDeque<>();

    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            boolean onBorder = r == 0 || c == 0 || r == rows - 1 || c == cols - 1;
            if (onBorder && board[r][c] == 'O') {
                board[r][c] = '#';
                queue.offer(r * cols + c);
            }
        }
    }

    while (!queue.isEmpty()) {
        int cell = queue.poll();
        int cr = cell / cols;
        int cc = cell % cols;
        for (int d = 0; d < 4; d++) {
            int nr = cr + dr[d];
            int nc = cc + dc[d];
            if (nr >= 0 && nc >= 0 && nr < rows && nc < cols && board[nr][nc] == 'O') {
                board[nr][nc] = '#';
                queue.offer(nr * cols + nc);
            }
        }
    }

    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            if (board[r][c] == 'O') {
                board[r][c] = 'X';
            } else if (board[r][c] == '#') {
                board[r][c] = 'O';
            }
        }
    }
}
```

**Time:** O(m · n). Each cell is enqueued at most once (marked at enqueue time) and the two scans are linear.
**Space:** O(m · n) worst case for the queue (the initial queue alone can hold about 2(m + n) border cells; frontiers can be larger when walls shape the search), and no recursion stack. The `'#'` marker replaces a separate `visited` array.

This is optimal in time since every cell must be examined. A recursive DFS from the border cells is equivalent, but is riskier here because a long snaking region can recurse tens of thousands of frames deep.

## 6. Dry Run

Example 1:

```
X X X X
X O O X
X X O X
X O X X
```

| Step | Action | Board state of the `O`/`#` cells | Queue |
|---|---|---|---|
| Border scan | Only border `'O'` is (3,1); mark it | (3,1)=`#`; (1,1),(1,2),(2,2)=`O` | [(3,1)] |
| BFS poll (3,1) | Neighbors: (2,1)=X, (3,0)=X, (3,2)=X, (4,1) out of bounds. Nothing to add | unchanged | empty |
| Final pass | (1,1),(1,2),(2,2) are still `O` so become `X`; (3,1) is `#` so becomes `O` | (3,1)=`O`, all others `X` | n/a |

Exit condition: queue empty, then the final pass completes. Result:

```
X X X X
X X X X
X X X X
X O X X
```

This matches the expected output. The cluster at (1,1),(1,2),(2,2) is never reached from the border, which is exactly why it is captured.

## 7. Complexity Summary

| Approach | Time | Space | Notes |
|---|---|---|---|
| BFS from every `O` | O((m·n)²) | O(m·n) | Re-explores the same region repeatedly |
| Multi-source BFS from the border | O(m·n) | O(m·n) worst case | Marks safe cells with `'#'`; no `visited` array |

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|---|---|---|
| 1 x 1 `[["X"]]` | Nothing to do | No border `'O'`; final pass changes nothing |
| 1 x 1 `[["O"]]` | The only cell is on the border, so it is not surrounded | Border scan marks it `'#'`; final pass restores it to `'O'` |
| Board with 1 or 2 rows or columns | Every cell is a border cell | All `'O'`s are marked safe; nothing is captured |
| No `'O'` on the border | Every region is surrounded | Queue starts empty; final pass converts every `'O'` to `'X'` |
| All `'O'` | Every cell is border-connected | Entire board marked `'#'` then restored; board unchanged |
| Interior `'O'` touching a border `'O'` only diagonally | 4-connectivity means it is not connected | Only the four orthogonal directions are used, so the interior `'O'` is captured |
| Large snaking region attached to the border | Deep recursion would overflow | Iterative BFS has no recursion |

## 9. Java Notes

- The board is `char[][]`: compare with `'O'`, `'X'`, `'#'`. Comparing against a string or int compiles in some cases but is always false.
- Using `'#'` as a temporary marker is safe only because the input is guaranteed to contain just `'X'` and `'O'`. It also removes the need for a `visited` array and doubles as the "safe" flag for the final pass.
- Bounds checks must come before `board[nr][nc]` in the `&&` chain so short-circuiting prevents `ArrayIndexOutOfBoundsException`.
- Encoding a cell as `r * cols + c` lets the queue be a `Queue<Integer>`, avoiding an `int[2]` allocation per cell (the brute force uses `int[]` for clarity). Max value is under 40,000, so `int` is safe.
- The method returns `void`; all work is on the passed-in array.

## 10. Common Mistakes

- Flipping `'O'` to `'X'` while searching from each `'O'` (instead of deciding first), so later checks see a modified board. Fix: use a separate marker or flag set before flipping.
- Capturing every non-border `'O'` without checking connectivity to the border. Fix: flood from border `'O'`s so interior `'O'`s attached to them are protected.
- Forgetting the final restore of `'#'` back to `'O'`, leaving `'#'` characters in the output. Fix: the last pass handles both `'O'` to `'X'` and `'#'` to `'O'`.
- Marking cells at poll time instead of enqueue time, which allows duplicate enqueues. Fix: set `'#'` at the moment of `offer`.
- Including diagonal neighbors, which wrongly protects regions touching the border only at corners. Fix: use four orthogonal directions only.

## 11. Interview Takeaway

- **Trigger sentence:** "A property defined by *not* reaching the border is easiest to compute backwards: flood from the border, mark what is reachable, and treat the rest as the answer."
- **90-second explanation:** Instead of checking each `'O'` region for whether it is enclosed, I flip the question: which `'O'`s are definitely safe? Those are exactly the ones connected, through other `'O'`s, to an `'O'` on the border. So I enqueue every border `'O'`, marking it `'#'`, and run a BFS that marks every `'O'` neighbor `'#'` as it is enqueued. Anything still `'O'` afterward can't reach the border, so it is surrounded. One final pass turns remaining `'O'` into `'X'` and restores `'#'` to `'O'`. Each cell is enqueued at most once, so it is O(m·n) time. I use BFS rather than recursion because a long snaking region could overflow Java's stack, and the temporary `'#'` marker avoids a separate visited array.
- **Related problems:** 200 (Number of Islands), 695 (Max Area of Island), 417 (Pacific Atlantic Water Flow), 1020 (Number of Enclaves), 133 (Clone Graph).

## 12. Recall Questions

**Q:** Why start the search from the border instead of from each interior `'O'`?
**A:** Whether a region is surrounded depends only on whether it reaches the border. Starting from the border finds all safe cells in one pass, instead of repeating the same reachability question for every cell of a region.

**Q:** Why is a temporary marker like `'#'` needed?
**A:** After the flood, safe `'O'`s and surrounded `'O'`s would look identical. The marker distinguishes them so the final pass can capture one group and restore the other.

**Q:** Why mark a cell when it is enqueued rather than when it is polled?
**A:** Several neighbors can discover the same cell before it is processed. Marking at enqueue time guarantees each cell enters the queue once.

**Q:** Why is an interior `'O'` touching a border `'O'` only diagonally still captured?
**A:** Regions connect horizontally and vertically only, so diagonal contact does not link them, and nothing orthogonal connects that cell to the border.

**Q:** Why does a board with one or two rows never capture anything?
**A:** Every cell is on the border, so every `'O'` region touches it and is therefore not surrounded.

## 13. Final Code

```java
import java.util.*;

class Solution {
    public void solve(char[][] board) {
        int rows = board.length;
        int cols = board[0].length;
        int[] dr = {1, -1, 0, 0};
        int[] dc = {0, 0, 1, -1};
        Queue<Integer> queue = new ArrayDeque<>();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                boolean onBorder = r == 0 || c == 0 || r == rows - 1 || c == cols - 1;
                if (onBorder && board[r][c] == 'O') {
                    board[r][c] = '#';
                    queue.offer(r * cols + c);
                }
            }
        }

        while (!queue.isEmpty()) {
            int cell = queue.poll();
            int cr = cell / cols;
            int cc = cell % cols;
            for (int d = 0; d < 4; d++) {
                int nr = cr + dr[d];
                int nc = cc + dc[d];
                if (nr >= 0 && nc >= 0 && nr < rows && nc < cols && board[nr][nc] == 'O') {
                    board[nr][nc] = '#';
                    queue.offer(nr * cols + nc);
                }
            }
        }

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (board[r][c] == 'O') {
                    board[r][c] = 'X';
                } else if (board[r][c] == '#') {
                    board[r][c] = 'O';
                }
            }
        }
    }
}
```

## 14. Self-Test

Given an `m x n` board of `'X'` and `'O'`, convert in place every 4-connected region of `'O'` that has no cell on the board's edge into `'X'`, leaving edge-touching regions unchanged. Re-derive: enqueue every border `'O'` marking it `'#'`, BFS through neighboring `'O'` cells marking each `'#'` as it is enqueued, then do a final pass turning remaining `'O'` into `'X'` and `'#'` back into `'O'`.
