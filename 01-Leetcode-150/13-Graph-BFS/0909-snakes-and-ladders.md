---
title: "0909 — Snakes and Ladders"
layout: default
parent: Graph BFS
grand_parent: Leetcode
nav_order: 909
permalink: /13-Graph-BFS/0909-snakes-and-ladders/

problem: 909 - Snakes and Ladders
url: https://leetcode.com/problems/snakes-and-ladders/
difficulty: Medium
section: Graph / BFS
patterns: [bfs, shortest-path, grid-to-graph]
data_structures: [array, queue, set]
time: O(n^2)
space: O(n^2)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0909 — Snakes and Ladders

| Field | Value |
|---|---|
| **Pattern** | BFS on Implicit Graph (Shortest Path) |
| **Difficulty** | Medium |
| **Section** | Graph / BFS |
| **Time** | O(n²) |
| **Space** | O(n²) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Amazon, Google, Meta, Microsoft, Bloomberg |
| **LeetCode** | [Snakes and Ladders](https://leetcode.com/problems/snakes-and-ladders/) |

## 1. Problem in Simple Words

**Given:**
- An `n × n` board where cells are numbered `1` to `n²` in **boustrophedon** order: the bottom row is numbered left→right, the next row up right→left, alternating, ending at `n²` in the top-left corner.
- A 2D integer array `board` where `board[r][c]` is either `-1` (normal square) or a **destination** square number. If it is not `-1`, landing on that square **immediately teleports** you to the square with that number (a ladder if higher, a snake if lower). You move exactly once per landing; chained teleports do not happen.

**Required:**
- Starting on square `1`, each move lets you choose a die roll `1..6` and advance that many squares.
- Return the **minimum number of dice rolls** to reach square `n²`, or `-1` if it is impossible.

**Constraints that actually matter:**
- `2 <= n <= 20` — at most 400 squares, so an O(n²) BFS is instant.
- `board[r][c] == -1` or `1 <= board[r][c] <= n²` — destinations are valid square numbers.
- `board[0][0] == -1` and `board[n-1][0] == -1` — the start and end squares are never teleports, so the start is well-defined and the goal is always reachable as a square (though not necessarily reachable by dice).
- Teleports never form a multi-step chain per landing — after a teleport, the next move is a fresh dice roll.

**Interpretation of the 403 error:** The URL fetch was blocked by LeetCode's anti-bot protection (HTTP 403 Forbidden). The statement above is reconstructed from the well-known LeetCode 909 problem; verify against the live page in a browser or the LeetCode mobile app. Alternatives: search "Snakes and Ladders" in the LeetCode problems list, or check the problem's discussion tab.

## 2. Recognition Signals

- **"Minimum number of moves"** on a board where each move has a fixed set of outcomes — the canonical **BFS shortest-path** signal.
- The board is a **grid**, but movement is defined by **square numbers**, not row/column adjacency. This is an **implicit graph**: nodes are square numbers `1..n²`, and edges go from `s` to `s + d` (for `d = 1..6`), remapped through any snake or ladder.
- Teleports make the graph **directed** (a teleport edge `s → board[s]` is not symmetric), but BFS does not care about direction.
- Recognition trigger: *"minimum dice rolls to reach the end of a snakes-and-ladders board"* → **BFS over square numbers**, with a **square-number → (row, col) mapping** to read teleports.

## 3. Core Idea

**Intuition.** Treat each square number `1..n²` as a node in a graph. From square `s`, the six possible rolls lead to squares `s+1, s+2, …, s+6` (capped at `n²`), and if the landing square has a teleport, you are immediately relocated to its destination. BFS from square `1` finds the minimum number of rolls to reach `n²`.

**Key observation.** The board's 2D layout matters **only** for looking up teleports. BFS operates purely on square numbers. So we need a helper that converts a square number `s` to its `(row, col)` coordinates, accounting for the boustrophedon ordering.

**Square-number → coordinates mapping.** With `n` rows and `n` columns:
- Let `q = (s - 1) / n` be the **row index from the bottom** (0 = bottom row).
- Let `colOffset = (s - 1) % n`.
- The actual row in `board` is `r = n - 1 - q` (top row is index 0).
- The column depends on whether the row is left-to-right or right-to-left:
  - Bottom row (`q = 0`, even) is left→right: `c = colOffset`.
  - Next row up (`q = 1`, odd) is right→left: `c = n - 1 - colOffset`.
  - In general: `c = (q % 2 == 0) ? colOffset : n - 1 - colOffset`.

**Variables.**
- `Queue<Integer> queue` — squares to visit, in BFS order.
- `boolean[] visited` — squares already reached.
- `int moves` — number of dice rolls so far (BFS level).
- `n2 = n * n` — the target square.

**Invariant.** BFS explores squares in non-decreasing order of dice rolls. The first time we dequeue `n²`, the current `moves` is the minimum. If the queue empties without reaching `n²`, it is unreachable.

## 4. Approach 1 — BFS over Square Numbers (Standard)

**Logic.** BFS from square `1`. For each dequeued square `s`, try rolls `d = 1..6`:
- Compute `next = s + d`. If `next > n²`, skip.
- Convert `next` to `(r, c)` and if `board[r][c] != -1`, set `next = board[r][c]`.
- If `next` is unvisited, mark it and enqueue it.
- Track BFS levels; when `n²` is dequeued, return the level.

### Java code (BFS)

```java
import java.util.*;

class Solution {
    public int snakesAndLadders(int[][] board) {
        int n = board.length;
        int n2 = n * n;
        boolean[] visited = new boolean[n2 + 1];

        Queue<Integer> queue = new LinkedList<>();
        queue.offer(1);
        visited[1] = true;

        int moves = 0;
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int s = queue.poll();
                if (s == n2) return moves;
                for (int d = 1; d <= 6; d++) {
                    int next = s + d;
                    if (next > n2) break;
                    int[] rc = squareToCoord(next, n);
                    int r = rc[0], c = rc[1];
                    if (board[r][c] != -1) {
                        next = board[r][c];
                    }
                    if (!visited[next]) {
                        visited[next] = true;
                        queue.offer(next);
                    }
                }
            }
            moves++;
        }
        return -1;
    }

    private int[] squareToCoord(int s, int n) {
        int q = (s - 1) / n;
        int colOffset = (s - 1) % n;
        int r = n - 1 - q;
        int c = (q % 2 == 0) ? colOffset : n - 1 - colOffset;
        return new int[] {r, c};
    }
}
```

**Complexity.**
- Time: O(n²) — at most `n²` squares, each with 6 roll attempts. The `squareToCoord` conversion is O(1).
- Space: O(n²) — visited array and queue.

**When it is useful.** This is the standard, clean solution. The `squareToCoord` helper isolates the only place where the 2D layout matters, keeping the BFS logic simple.

## 5. Approach 2 — Optimized: Precompute the Teleport Map Once

**Algorithm steps.**
1. Precompute an array `jump[1..n²]` where `jump[s]` is the square you end up on after landing on `s` (either `s` itself if no teleport, or `board[r][c]` if there is one).
2. Run BFS on square numbers using `jump` for O(1) teleport lookup — no per-step coordinate conversion.
3. Return the BFS level when `n²` is dequeued, or `-1`.

### Java code (precomputed teleport map)

```java
import java.util.*;

class Solution {
    public int snakesAndLadders(int[][] board) {
        int n = board.length;
        int n2 = n * n;

        // Precompute jump[s] for every square.
        int[] jump = new int[n2 + 1];
        for (int s = 1; s <= n2; s++) {
            int q = (s - 1) / n;
            int colOffset = (s - 1) % n;
            int r = n - 1 - q;
            int c = (q % 2 == 0) ? colOffset : n - 1 - colOffset;
            jump[s] = (board[r][c] != -1) ? board[r][c] : s;
        }

        boolean[] visited = new boolean[n2 + 1];
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(1);
        visited[1] = true;

        int moves = 0;
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int s = queue.poll();
                if (s == n2) return moves;
                for (int d = 1; d <= 6; d++) {
                    int next = s + d;
                    if (next > n2) break;
                    next = jump[next];
                    if (!visited[next]) {
                        visited[next] = true;
                        queue.offer(next);
                    }
                }
            }
            moves++;
        }
        return -1;
    }
}
```

**Explanation of important logic.**
- **`jump[s] = s` for normal squares** makes the BFS inner loop uniform: always `next = jump[next]`, no branch.
- **One-time coordinate conversion** removes the `squareToCoord` call from the hot loop; the cost is O(n²) upfront, which does not change the asymptotic complexity.
- **`break` when `next > n2`** is correct because `s + d` increases with `d`; once one roll overshoots, all larger rolls do too.
- **Visited on enqueue** prevents duplicate queue entries and bounds the queue size.

**Complexity.**
- Time: O(n²) — O(n²) preprocessing plus O(6 · n²) BFS.
- Space: O(n²) — `jump`, `visited`, and queue.

**Why it is optimal.** BFS finds the shortest number of rolls because each roll is one unweighted edge. Any correct solution must consider each square at least once in the worst case, so Ω(n²) is a lower bound; both approaches meet it. The precomputed version is marginally faster in practice and arguably cleaner because the BFS loop has no coordinate math.

## 6. Dry Run

**Example:** `board = [[-1,-1,-1,-1,-1,-1],[-1,-1,-1,-1,-1,-1],[-1,-1,-1,-1,-1,-1],[-1,35,-1,-1,13,-1],[-1,-1,-1,-1,-1,-1],[-1,15,-1,-1,-1,-1]]` (n = 6).

This is LeetCode's Example 1. It is useful because it has **two teleports** (a ladder at square 2 → 15, and a snake at square 35 → 13) and requires a non-trivial path, exercising both coordinate conversion and teleport handling.

**Board layout (square numbers per cell):**

- Row 5 (bottom, left→right): 1, 2, 3, 4, 5, 6
- Row 4 (right→left): 12, 11, 10, 9, 8, 7
- Row 3 (left→right): 13, 14, 15, 16, 17, 18
- Row 2 (right→left): 24, 23, 22, 21, 20, 19
- Row 1 (left→right): 25, 26, 27, 28, 29, 30
- Row 0 (top, right→left): 36, 35, 34, 33, 32, 31

Teleports: square 2 → 15 (ladder), square 35 → 13 (snake).

**BFS trace (levels shown as dice rolls):**

| Moves | Queue (squares) | Dequeued | Rolls tried | Enqueued (after teleport) |
|---|---|---|---|---|
| 0 | `[1]` | — | — | — |
| 1 | `[]` | 1 | 2..7 | 2→15, 3, 4, 5, 6, 7 → queue `[15,3,4,5,6,7]` |
| 1 | `[15,3,4,5,6,7]` | 15 | 16..21 | 16,17,18,19,20,21 |
| 1 | `[3,4,5,6,7,16,…]` | 3 | 4..9 | (4..9 already seen or new) |
| … | … | … | … | … |
| 3 | queue contains 36 | 36 | — | return 3 |

The optimal path is `1 → 2 (ladder to 15) → 17 → 36` in 3 rolls. Other 3-roll paths exist; BFS returns the first one at level 3.

**Final result:** `3`.

## 7. Complexity Summary

| Approach | Time | Space (auxiliary) | Notes |
|---|---|---|---|
| BFS with per-step coordinate conversion | O(n²) | O(n²) | Simple; conversion in hot loop |
| BFS with precomputed jump array | O(n²) | O(n²) | Faster constant factor; cleaner inner loop |

Input space is O(n²) for the board and is not counted as auxiliary. Both approaches are asymptotically optimal.

## 8. Edge Cases

| Case | Why it matters | How the code handles it |
|---|---|---|
| `n = 2`, no teleports | Smallest board; tests basic BFS | Works: 1 → 3 → 4 in 2 rolls |
| Target unreachable (all snakes block) | Must return `-1` | BFS exhausts the queue without dequeuing `n²` → `-1` |
| Teleport on square 1 | Excluded by `board[n-1][0] == -1`, but would be handled | Jump array would redirect the start; BFS would follow it |
| Teleport on square `n²` | Excluded by `board[0][0] == -1` | If present, landing there would redirect; BFS would not count it as a win until it dequeues `n²` |
| Ladder to a square that is also a snake | Not possible per constraints (each square has one destination) | Jump array holds exactly one destination |
| Odd vs even rows (boustrophedon) | The core layout subtlety | `c = (q % 2 == 0) ? colOffset : n - 1 - colOffset` handles both |
| Overshooting `n²` with a roll | Rolls beyond the board are invalid | `if (next > n2) break;` |
| Multiple visits to the same square | Would blow up the queue | `visited` set on enqueue prevents it |

## 9. Java Notes

- **`int[] jump` indexed by square number** is the idiomatic way to flatten the board; it avoids repeated coordinate math and makes the BFS loop branch-free for teleports.
- **Integer division and modulo** are safe because `s >= 1`; `(s - 1) / n` and `(s - 1) % n` give the correct row-from-bottom and column offset.
- **`break` vs `continue`** in the roll loop: `break` is correct because `next` increases monotonically with `d`; once it exceeds `n²`, all larger `d` do too.
- **`boolean[] visited` sized `n² + 1`** so indices `1..n²` are valid; index 0 is unused.
- **`LinkedList` vs `ArrayDeque`:** `LinkedList` works but `ArrayDeque` is preferred in production for speed.
- **Return `-1` when the queue empties** — do not return `moves` or a sentinel like `Integer.MAX_VALUE`.

## 10. Common Mistakes

- **Forgetting the boustrophedon (zigzag) ordering.** Treating every row as left→right gives wrong coordinates on odd rows and wrong teleports. The column formula must alternate by row.
- **Applying teleports repeatedly.** A teleport relocates you once per landing; you do not follow a chain of teleports. The code applies `jump[next]` exactly once.
- **Counting a teleport as an extra move.** The dice roll and the teleport are one move; BFS levels count dice rolls only.
- **Marking visited on dequeue instead of enqueue.** This can enqueue the same square multiple times; mark on enqueue.
- **Off-by-one in square numbering.** Squares are 1-indexed; using 0-indexed arithmetic without `s - 1` shifts every row and column.
- **Checking `s == n²` after processing rolls instead of on dequeue.** Either works if consistent, but checking on dequeue is the clean BFS-level semantics.
- **Using DFS instead of BFS.** DFS does not guarantee the minimum number of rolls; BFS does.

## 11. Interview Takeaway

**Recognition trigger to memorize:** *"Minimum number of moves on a board with fixed-step moves and teleports → BFS over square numbers, with a square → (row, col) mapping that accounts for boustrophedon ordering and a jump lookup for teleports."*

**90-second explanation:** "This is a shortest-path problem on an implicit graph. Each square number is a node; from square s, the six dice rolls lead to s+1 through s+6, and landing on a teleport square relocates you to its destination. I run BFS from square 1, tracking the number of dice rolls as BFS levels. The only subtle part is converting a square number to its row and column on the board, because the board is numbered in a zigzag: bottom row left-to-right, next row right-to-left, and so on. I precompute a jump array mapping each square to where you actually end up, so the BFS inner loop is a simple six-iteration check with a visited set. The first time I dequeue square n², the current level is the minimum number of rolls. Time and space are both O(n²), which is optimal since there are n² squares."

**Related problems:**
- [Word Ladder](https://leetcode.com/problems/word-ladder/) — BFS on an implicit graph where nodes are words and edges are one-letter changes; same shortest-path skeleton.
- [Open the Lock](https://leetcode.com/problems/open-the-lock/) — BFS over lock combinations, an implicit graph with 4-digit states.
- [Minimum Knight Moves](https://leetcode.com/problems/minimum-knight-moves/) — BFS on an infinite chessboard, an implicit graph with fixed move offsets.
- [Jump Game IV](https://leetcode.com/problems/jump-game-iv/) — BFS with jump edges defined by equal values; another implicit-graph shortest path.

## 12. Recall Questions

**Q1. Why is BFS the right algorithm here rather than DFS?**
A: Every dice roll is one unweighted edge, and we need the **minimum** number of rolls. BFS explores in non-decreasing distance, so the first time it reaches `n²` it has found the minimum. DFS does not guarantee that.

**Q2. Why does the board's boustrophedon numbering matter, and where is it handled?**
A: The board is numbered left→right on the bottom row, right→left on the next, alternating. The coordinate conversion `c = (q % 2 == 0) ? colOffset : n - 1 - colOffset` handles it; without it, teleports on odd rows would be read from the wrong cell.

**Q3. Why do we apply the teleport only once per landing?**
A: The problem says landing on a snake or ladder moves you to the destination; you do not continue moving. So `jump[next]` is applied exactly once, and the next move starts from the destination.

**Q4. What are the time and space complexities, and why are they optimal?**
A: O(n²) time and space. There are `n²` squares, each with 6 roll options; any correct solution must consider each square at least once in the worst case, so Ω(n²) is a lower bound.

**Q5. Why is `if (next > n2) break;` correct instead of `continue`?**
A: Because `next = s + d` increases with `d`. Once `s + d > n²`, all larger `d` also overshoot, so breaking early is safe and slightly faster.

**Q6. What does returning `-1` mean, and when does it happen?**
A: It means square `n²` is unreachable from square 1 via any sequence of dice rolls and teleports. It happens when the BFS queue empties without ever dequeuing `n²`.

**Q7. Why must `visited` be marked on enqueue rather than dequeue?**
A: Marking on dequeue allows the same square to be enqueued multiple times from different predecessors, inflating the queue and wasting work. Marking on enqueue guarantees each square enters the queue at most once.

**Q8. How does the precomputed `jump` array change the solution, and does it change the complexity?**
A: It moves the coordinate conversion out of the hot loop into a one-time O(n²) pass, making the BFS inner loop branch-free for teleports. The asymptotic complexity stays O(n²) in both time and space.

## 13. Final Code

### Java code (final, optimized BFS with precomputed jump map)

```java
import java.util.*;

class Solution {
    public int snakesAndLadders(int[][] board) {
        int n = board.length;
        int n2 = n * n;

        // Precompute jump[s]: where you land after landing on square s.
        int[] jump = new int[n2 + 1];
        for (int s = 1; s <= n2; s++) {
            int q = (s - 1) / n;
            int colOffset = (s - 1) % n;
            int r = n - 1 - q;
            int c = (q % 2 == 0) ? colOffset : n - 1 - colOffset;
            jump[s] = (board[r][c] != -1) ? board[r][c] : s;
        }

        boolean[] visited = new boolean[n2 + 1];
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(1);
        visited[1] = true;

        int moves = 0;
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int s = queue.poll();
                if (s == n2) return moves;
                for (int d = 1; d <= 6; d++) {
                    int next = s + d;
                    if (next > n2) break;
                    next = jump[next];
                    if (!visited[next]) {
                        visited[next] = true;
                        queue.offer(next);
                    }
                }
            }
            moves++;
        }
        return -1;
    }
}
```

## 14. Self-Test

Consider `n = 3` with `board = [[-1,-1,-1],[-1,-1,-1],[-1,-1,-1]]` (a plain 3×3 board, no teleports). Write out the square-number layout row by row (remember the zigzag), then trace BFS from square 1 to square 9, listing the queue contents at each level. Answer: what is the minimum number of rolls, and how many distinct shortest paths exist? Then modify the board by placing a ladder at square 3 that goes to square 7 and a snake at square 7 that goes to square 2 — note that this creates a situation where square 7 is both a ladder destination and a snake source. Explain what `jump[3]` and `jump[7]` become, and whether BFS can ever reach square 9 in this modified board.