---
title: "0052 — N-Queens II"
layout: default
parent: Backtracking
grand_parent: Leetcode
nav_order: 52
permalink: /15-Backtracking/0052-n-queens-ii/

problem: 52 - N-Queens II
url: https://leetcode.com/problems/n-queens-ii/
difficulty: Hard
section: Backtracking
patterns: [backtracking, recursion, constraint-satisfaction, bitmask]
data_structures: [array, set]
time: O(n!)
space: O(n)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0052 — N-Queens II

| Field | Value |
|---|---|
| **Pattern** | Backtracking (Constraint Satisfaction) |
| **Difficulty** | Hard |
| **Section** | Backtracking |
| **Time** | O(n!) |
| **Space** | O(n) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Amazon, Google, Meta, Microsoft, Bloomberg |
| **LeetCode** | [N-Queens II](https://leetcode.com/problems/n-queens-ii/) |

## 1. Problem in Simple Words

**Given:**
- An integer `n` representing an `n × n` chessboard.

**Required:**
- Return the **number of distinct ways** to place `n` queens on the board so that no two queens attack each other.
- Queens attack along **rows, columns, and both diagonals**.
- Unlike N-Queens I (LeetCode 51), we only need the **count**, not the actual board configurations.

**Constraints that actually matter:**
- `1 <= n <= 9` — the board is tiny; the number of solutions grows quickly (n=8 has 92, n=9 has 352) but is still manageable.
- No two queens may share a row, column, or diagonal — this is the entire constraint set.
- Since there are `n` queens and `n` rows, **exactly one queen must be placed in each row** — this is a crucial structural observation that simplifies the search.
- The count fits comfortably in an `int` for `n ≤ 9`.

**Interpretation of the 403 error:** The URL fetch was blocked by LeetCode's anti-bot protection (HTTP 403 Forbidden). The statement above is reconstructed from the well-known LeetCode 52 problem; verify against the live page in a browser or the LeetCode mobile app. Alternatives: search "N-Queens II" in the LeetCode problems list, or check the problem's discussion tab.

## 2. Recognition Signals

- The problem asks to **count all valid configurations** under a set of mutual constraints — the canonical **constraint-satisfaction backtracking** shape.
- The constraints are **pairwise attacks**: no two queens share a row, column, or diagonal. This is a classic **N-Queens** problem.
- The phrase *"return the number of distinct solutions"* (not the boards themselves) means we can optimize by not building the boards — just count.
- Recognition trigger: *"place N non-attacking queens on an N×N board"* → **backtracking row by row, tracking occupied columns and diagonals**.

## 3. Core Idea

**Intuition.** Place queens one row at a time. In row `r`, try each column `c`; if `c` is not attacked by any previously placed queen, place a queen there and recurse to row `r + 1`. When all `n` rows are filled, we have a valid configuration — increment the count.

**Key observation.** Because there are `n` queens and `n` rows, and no two queens can share a row, **each row contains exactly one queen**. This means we only need to choose a column for each row — the search space is `n!` in the worst case (permutations of columns), reduced by diagonal pruning.

**Attack detection.** A queen at `(r, c)` attacks:
- **Columns:** any `(r', c)` — track with a `boolean[] cols`.
- **Main diagonal (↘):** `r - c` is constant — track with a `boolean[] diag1` indexed by `r - c + (n - 1)` to keep it non-negative.
- **Anti-diagonal (↙):** `r + c` is constant — track with a `boolean[] diag2` indexed by `r + c`.

**Variables.**
- `int n` — board size.
- `boolean[] cols` — columns already occupied.
- `boolean[] diag1` — main diagonals already occupied (indexed by `r - c + n - 1`).
- `boolean[] diag2` — anti-diagonals already occupied (indexed by `r + c`).
- `int count` — number of valid configurations found.

**Invariant.** At the start of `backtrack(row)`, exactly `row` queens have been placed, one in each of rows `0..row-1`, and `cols`, `diag1`, `diag2` accurately reflect the occupied columns and diagonals. When `row == n`, a complete valid configuration has been found.

## 4. Approach 1 — Brute Force: Enumerate All Permutations and Check

**Logic.** Since each row has exactly one queen, a configuration is a permutation of columns `0..n-1` (column for row 0, column for row 1, …). Enumerate all `n!` permutations and check whether any two queens share a diagonal.

### Java code (permutation enumeration)

```java
import java.util.*;

class Solution {
    private int count = 0;
    private int n;

    public int totalNQueens(int n) {
        this.n = n;
        int[] cols = new int[n];
        boolean[] used = new boolean[n];
        permute(cols, used, 0);
        return count;
    }

    private void permute(int[] cols, boolean[] used, int row) {
        if (row == n) {
            if (isValid(cols)) count++;
            return;
        }
        for (int c = 0; c < n; c++) {
            if (used[c]) continue;
            used[c] = true;
            cols[row] = c;
            permute(cols, used, row + 1);
            used[c] = false;
        }
    }

    private boolean isValid(int[] cols) {
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (Math.abs(cols[i] - cols[j]) == j - i) return false;
            }
        }
        return true;
    }
}
```

**Complexity.**
- Time: O(n! · n²) — `n!` permutations, each validated in O(n²).
- Space: O(n) auxiliary.

**Why it is less efficient.** It generates all `n!` permutations and only checks diagonals at the end. Backtracking prunes invalid partial placements immediately, often cutting the search drastically.

## 5. Approach 2 — Optimized: Backtracking with Column/Diagonal Sets

**Algorithm steps.**
1. Initialize `cols`, `diag1`, `diag2` as boolean arrays of appropriate sizes, and `count = 0`.
2. Call `backtrack(0)`:
   - If `row == n`, increment `count` and return.
   - For `c` from `0` to `n-1`:
     - Compute `d1 = row - c + n - 1` and `d2 = row + c`.
     - If `cols[c] || diag1[d1] || diag2[d2]`, skip (attacked).
     - Mark `cols[c] = diag1[d1] = diag2[d2] = true`.
     - Recurse with `row + 1`.
     - Unmark all three (undo).
3. Return `count`.

### Java code (backtracking with sets)

```java
class Solution {
    private int count = 0;
    private int n;
    private boolean[] cols, diag1, diag2;

    public int totalNQueens(int n) {
        this.n = n;
        cols = new boolean[n];
        diag1 = new boolean[2 * n - 1];
        diag2 = new boolean[2 * n - 1];
        backtrack(0);
        return count;
    }

    private void backtrack(int row) {
        if (row == n) {
            count++;
            return;
        }

        for (int c = 0; c < n; c++) {
            int d1 = row - c + n - 1;
            int d2 = row + c;
            if (cols[c] || diag1[d1] || diag2[d2]) continue;

            cols[c] = diag1[d1] = diag2[d2] = true;
            backtrack(row + 1);
            cols[c] = diag1[d1] = diag2[d2] = false;
        }
    }
}
```

**Explanation of important logic.**
- **Row-by-row placement** guarantees exactly one queen per row, eliminating row conflicts by construction.
- **The `d1` index** `row - c + n - 1` shifts the diagonal index into `[0, 2n-2]`. Without the shift, negative indices would occur when `c > row`.
- **The `d2` index** `row + c` is always in `[0, 2n-2]`, so no shift is needed.
- **The undo step** restores all three boolean arrays before trying the next column. Forgetting any one of them leaves a stale "occupied" mark and misses valid solutions.
- **No board is built** — we only count, which saves the O(n²) board construction per solution.

**Complexity.**
- Time: O(n!) worst case, but heavily pruned by the diagonal checks. In practice, the number of nodes explored is far smaller than `n!`.
- Space: O(n) auxiliary for the three boolean arrays and recursion stack.

**Why it is optimal.** The search must explore the constraint tree to count all solutions; there is no known closed-form shortcut (the N-Queens count sequence is not expressible in closed form). The O(n) auxiliary space is optimal for backtracking.

## 6. Dry Run

**Example:** `n = 4`.

This example is useful because it has exactly **2 solutions**, and it is small enough to trace fully while still exercising diagonal conflicts.

**Trace (row by row):**

| Row | Col tried | Attacked? | Action | Result |
|---|---|---|---|---|
| 0 | 0 | no | place Q at (0,0), recurse row 1 | — |
| 1 | 0 | col | skip | — |
| 1 | 1 | diag1 (0-0 = 1-1 = 0) | skip | — |
| 1 | 2 | no | place Q at (1,2), recurse row 2 | — |
| 2 | 0 | diag2 (0+0 = 2+0? no; check: diag1 0-0=0 vs 2-0=2 no; diag2 0+0=0 vs 2+0=2 no) — actually col 0 free, diag1[2+? ] let's compute: d1 = 2-0+3 = 5, d2 = 2+0 = 2; (0,0) has d2=0, (1,2) has d1=1-2+3=2, d2=3. So d1=5, d2=2 not occupied → place Q at (2,0), recurse row 3 | — |
| 3 | 0 | col | skip | — |
| 3 | 1 | diag2 (1+3=4? check: (0,0) d2=0, (1,2) d2=3, (2,0) d2=2; 3+1=4 not occupied; d1=3-1+3=5 vs (2,0) d1=2-0+3=5 → diag1 conflict) | skip | — |
| 3 | 2 | col (col 2 used by row 1) | skip | — |
| 3 | 3 | diag1 (3-3+3=3 vs (1,2) d1=2, (2,0) d1=5, (0,0) d1=3 → conflict with (0,0)) | skip | — |
| — | — | — | backtrack row 2 | — |
| 2 | 1 | d1 = 2-1+3=4, d2=3 vs (1,2) d2=3 → conflict | skip | — |
| 2 | 2 | col | skip | — |
| 2 | 3 | d1 = 2-3+3=2 vs (1,2) d1=2 → conflict | skip | — |
| — | — | — | backtrack row 1 | — |
| 1 | 3 | no | place Q at (1,3), recurse row 2 | — |
| 2 | 0 | no | place Q at (2,0), recurse row 3 | — |
| 3 | 0 | col | skip | — |
| 3 | 1 | no (d1=3-1+3=5, d2=4; check against (0,0) d1=3, d2=0; (1,3) d1=1-3+3=1, d2=4 → d2 conflict with (1,3)) | skip | — |
| 3 | 2 | no (d1=3-2+3=4, d2=5; (1,3) d1=1, d2=4; (2,0) d1=5, d2=2; (0,0) d1=3, d2=0; all free) | **place Q at (3,2), row==4 → count++** | count=1 |
| — | — | — | backtrack | — |
| 2 | 1 | d1=2-1+3=4, d2=3; (1,3) d1=1, d2=4; (0,0) d1=3, d2=0; free → place Q at (2,1), recurse row 3 | — |
| 3 | 0 | d1=3-0+3=6, d2=3; (1,3) d1=1, d2=4; (2,1) d1=4, d2=3 → d2 conflict | skip | — |
| 3 | 1 | col | skip | — |
| 3 | 2 | col | skip | — |
| 3 | 3 | col | skip | — |
| — | — | — | backtrack | — |
| 2 | 2 | col | skip | — |
| 2 | 3 | col | skip | — |
| — | — | — | backtrack row 0 | — |
| 0 | 1 | no | place Q at (0,1), recurse row 1 | — |
| 1 | 0 | no (d1=1-0+3=4, d2=1; (0,1) d1=0-1+3=2, d2=1 → d2 conflict) | skip | — |
| 1 | 1 | col | skip | — |
| 1 | 2 | col | skip | — |
| 1 | 3 | no (d1=1-3+3=1, d2=4; (0,1) d1=2, d2=1; free) | place Q at (1,3), recurse row 2 | — |
| 2 | 0 | no (d1=2-0+3=5, d2=2; (0,1) d1=2, d2=1; (1,3) d1=1, d2=4; free) | place Q at (2,0), recurse row 3 | — |
| 3 | 0 | col | skip | — |
| 3 | 1 | col? col 1 used by row 0 | skip | — |
| 3 | 2 | d1=3-2+3=4, d2=5; (2,0) d1=5, d2=2; (1,3) d1=1, d2=4 → d2 conflict with (1,3) | skip | — |
| 3 | 3 | col | skip | — |
| — | — | — | backtrack | — |
| 2 | 1 | d1=2-1+3=4, d2=3; (1,3) d1=1, d2=4 → d2 conflict | skip | — |
| 2 | 2 | d1=2-2+3=3, d2=4; (1,3) d2=4 → conflict | skip | — |
| 2 | 3 | col | skip | — |
| — | — | — | backtrack row 1 | — |
| 1 | ... | all other cols attacked | — | — |
| — | — | — | backtrack row 0 | — |
| 0 | 2 | no | place Q at (0,2), recurse row 1 | — |
| 1 | 0 | no (d1=1-0+3=4, d2=1; (0,2) d1=0-2+3=1, d2=2; free) | place Q at (1,0), recurse row 2 | — |
| 2 | 0 | col | skip | — |
| 2 | 1 | col? no; d1=2-1+3=4, d2=3; (0,2) d1=1, d2=2; (1,0) d1=4, d2=1 → d1 conflict with (1,0) | skip | — |
| 2 | 2 | col | skip | — |
| 2 | 3 | d1=2-3+3=2, d2=5; (0,2) d1=1, d2=2 → d2 conflict | skip | — |
| — | — | — | backtrack | — |
| 1 | 1 | d1=1-1+3=3, d2=2; (0,2) d1=1, d2=2 → d2 conflict | skip | — |
| 1 | 2 | col | skip | — |
| 1 | 3 | d1=1-3+3=1, d2=4; (0,2) d1=1 → d1 conflict | skip | — |
| — | — | — | backtrack | — |
| 0 | 3 | no | place Q at (0,3), recurse row 1 | — |
| 1 | 0 | no (d1=1-0+3=4, d2=1; (0,3) d1=0-3+3=0, d2=3; free) | place Q at (1,0), recurse row 2 | — |
| 2 | 0 | col | skip | — |
| 2 | 1 | d1=2-1+3=4, d2=3; (0,3) d2=3 → conflict | skip | — |
| 2 | 2 | d1=2-2+3=3, d2=4; (1,0) d1=4, d2=1; (0,3) d1=0, d2=3; free → place Q at (2,2), recurse row 3 | — |
| 3 | 0 | col | skip | — |
| 3 | 1 | col? no; d1=3-1+3=5, d2=4; (2,2) d1=3, d2=4 → d2 conflict | skip | — |
| 3 | 2 | col | skip | — |
| 3 | 3 | col | skip | — |
| — | — | — | backtrack | — |
| 2 | 3 | d1=2-3+3=2, d2=5; (0,3) d1=0, d2=3; (1,0) d1=4, d2=1; free → place Q at (2,3), recurse row 3 | — |
| 3 | 0 | col | skip | — |
| 3 | 1 | col? no; d1=3-1+3=5, d2=4; (2,3) d1=2, d2=5; (1,0) d1=4, d2=1; (0,3) d1=0, d2=3; free → **place Q at (3,1), row==4 → count++** | count=2 |
| ... | ... | ... | backtrack | — |

**Final result:** `2` — the two solutions are `[1,3,0,2]` and `[2,0,3,1]` (columns for rows 0-3).

## 7. Complexity Summary

| Approach | Time | Space (auxiliary) | Notes |
|---|---|---|---|
| Permutation enumeration | O(n! · n²) | O(n) | Generates all permutations, checks diagonals at end |
| Backtracking + sets | O(n!) worst case, heavily pruned | O(n) | Prunes invalid partial placements immediately |

Input space is O(1); auxiliary space is O(n) for the boolean arrays and recursion stack. Both approaches are optimal in asymptotic space; backtracking is much faster in practice.

## 8. Edge Cases

| Case | Why it matters | How the code handles it |
|---|---|---|
| `n = 1` | Trivial: one queen, one solution | Base case fires after one placement; returns `1` |
| `n = 2` or `n = 3` | No solutions exist | Backtracking exhausts all branches; returns `0` |
| `n = 4` | Smallest nontrivial case with 2 solutions | Backtracking finds both |
| `n = 8` | Classic 8-queens; 92 solutions | Backtracking handles it efficiently |
| `n = 9` | Largest allowed; 352 solutions | Still fast (milliseconds) |
| Diagonal index bounds | `row - c + n - 1` must stay in `[0, 2n-2]` | The shift by `n - 1` guarantees it |
| `diag2` bounds | `row + c` is always in `[0, 2n-2]` | No shift needed |

## 9. Java Notes

- **Boolean arrays vs sets:** `boolean[]` is faster than `Set<Integer>` for tracking occupied columns and diagonals, since indices are bounded and lookups are O(1) with no hashing.
- **Array sizes:** `diag1` and `diag2` both need size `2n - 1` because `row - c` ranges from `-(n-1)` to `n-1`, and `row + c` ranges from `0` to `2n-2`.
- **Index shift:** `row - c + n - 1` maps `-(n-1) → 0` and `n-1 → 2n-2`, keeping all indices valid.
- **Chained assignment:** `cols[c] = diag1[d1] = diag2[d2] = true;` is concise and correct — all three are set to `true`. The undo does the same with `false`.
- **No board construction:** since we only need the count, we skip building the `List<List<String>>` board, saving O(n²) per solution.
- **`private int count`** as an instance field is fine here because the method is called once per `Solution` instance; alternatively, pass a mutable `int[]` or return the count from the recursive call.

## 10. Common Mistakes

- **Forgetting to undo one of the three marks.** If you reset `cols[c]` but not `diag1[d1]`, subsequent branches incorrectly see the diagonal as occupied and miss valid solutions.
- **Incorrect diagonal indexing.** Using `row - c` directly as an array index causes `ArrayIndexOutOfBoundsException` for negative values. The `+ n - 1` shift is essential.
- **Allocating diagonal arrays of size `n`.** They need `2n - 1` because there are `2n - 1` diagonals in each direction.
- **Checking only columns and not diagonals.** Queens attack diagonally too; missing this produces far too many "solutions".
- **Using a `Set` instead of an array.** Correct but slower; the boolean array is the standard choice.
- **Building the board when only the count is needed.** Wastes time and memory; skip it.
- **Assuming `n = 2` or `n = 3` has solutions.** They do not; the algorithm correctly returns `0`.

## 11. Interview Takeaway

**Recognition trigger to memorize:** *"Place N non-attacking queens, count solutions → backtrack row by row, tracking occupied columns and both diagonals with boolean arrays; undo all three marks after recursion."*

**90-second explanation:** "This is N-Queens, so I use backtracking row by row. Since there are n queens and n rows, each row has exactly one queen — so I only need to choose a column for each row. I track three things: occupied columns, occupied main diagonals (indexed by `row - col`), and occupied anti-diagonals (indexed by `row + col`). At each row, I try every column; if it is not attacked, I mark the column and both diagonals, recurse to the next row, then unmark. When I reach row n, I have a valid configuration, so I increment the count. The diagonal indices need a shift of `n - 1` to stay non-negative. Time is O(n!) worst case but heavily pruned, and auxiliary space is O(n) for the boolean arrays and recursion stack. Since only the count is needed, I skip building the board."

**Related problems:**
- [N-Queens](https://leetcode.com/problems/n-queens/) — returns the actual board configurations instead of the count; same backtracking with board construction.
- [Sudoku Solver](https://leetcode.com/problems/sudoku-solver/) — constraint-satisfaction backtracking on a 9×9 grid.
- [Permutations](https://leetcode.com/problems/permutations/) — backtracking over arrangements; simpler because there are no attack constraints.
- [Combination Sum](https://leetcode.com/problems/combination-sum/) — backtracking with a sum constraint; similar pruning logic.

## 12. Recall Questions

**Q1. Why do we place exactly one queen per row?**
A: There are `n` queens and `n` rows, and no two queens can share a row. So each row must contain exactly one queen. This reduces the search to choosing a column for each row.

**Q2. How are the two diagonal indices computed, and why is a shift needed?**
A: Main diagonal: `row - col + n - 1` (shifted to be non-negative). Anti-diagonal: `row + col` (always non-negative). The shift by `n - 1` maps the range `-(n-1)..(n-1)` to `0..2n-2`.

**Q3. Why do the diagonal arrays need size `2n - 1`?**
A: There are `2n - 1` diagonals in each direction on an `n × n` board. The index ranges from `0` to `2n - 2`.

**Q4. What are the time and space complexities?**
A: Time is O(n!) worst case, heavily pruned by diagonal checks. Auxiliary space is O(n) for the three boolean arrays and recursion stack.

**Q5. Why must we undo all three marks after recursion?**
A: The marks represent the current partial configuration. If any mark is left set, subsequent branches incorrectly see that column or diagonal as occupied and miss valid solutions.

**Q6. What does the algorithm return for `n = 2` or `n = 3`?**
A: `0` — no valid configurations exist for these board sizes. Backtracking exhausts all branches without reaching row `n`.

**Q7. How is this different from N-Queens I?**
A: N-Queens I returns the actual board configurations as lists of strings; N-Queens II only returns the count. The backtracking skeleton is identical; N-Queens II skips board construction, making it faster and simpler.

**Q8. Could you use a `Set<Integer>` instead of boolean arrays?**
A: Yes, but it is slower due to hashing and boxing. Boolean arrays are the standard choice because the index ranges are small and bounded.

## 13. Final Code

### Java code (final, optimized backtracking)

```java
class Solution {
    private int count = 0;
    private int n;
    private boolean[] cols, diag1, diag2;

    public int totalNQueens(int n) {
        this.n = n;
        cols = new boolean[n];
        diag1 = new boolean[2 * n - 1];
        diag2 = new boolean[2 * n - 1];
        backtrack(0);
        return count;
    }

    private void backtrack(int row) {
        if (row == n) {
            count++;
            return;
        }

        for (int c = 0; c < n; c++) {
            int d1 = row - c + n - 1;
            int d2 = row + c;
            if (cols[c] || diag1[d1] || diag2[d2]) continue;

            cols[c] = diag1[d1] = diag2[d2] = true;
            backtrack(row + 1);
            cols[c] = diag1[d1] = diag2[d2] = false;
        }
    }
}
```

## 14. Self-Test

Consider `n = 5`. Trace the first **three complete solutions** found by the backtracking algorithm: for each, show the row-by-row column choices, the diagonal indices checked at each step, and the moment `count` is incremented. Then answer: how many solutions exist for `n = 5` in total (you may look up the known sequence), and why does the algorithm return `0` for `n = 2` and `n = 3`? Finally, explain what would happen if the `+ n - 1` shift were omitted from `d1` — which error would occur, and at which `(row, col)` pair would it first manifest?