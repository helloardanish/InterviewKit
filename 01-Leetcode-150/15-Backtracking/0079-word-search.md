---
title: "0079 — Word Search"
layout: default
parent: Backtracking
grand_parent: Leetcode
nav_order: 79
permalink: /15-Backtracking/0079-word-search/

problem: 79 - Word Search
url: [https://leetcode.com/problems/word-search/](https://leetcode.com/problems/word-search/)
difficulty: Medium
section: Backtracking / Matrix
patterns: [backtracking, dfs-with-visited-marking]
data_structures: [matrix]
time: O(m * n * 3^L)
space: O(L)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

**# 0079 — Word Search**

| Field | Value |
|---|---|
| **Pattern** | Backtracking + DFS (Visited Marking) |
| **Difficulty** | Medium |
| **Section** | Backtracking / Matrix |
| **Time** | O(m * n * 3^L) |
| **Space** | O(L) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Amazon, Google, Meta, Microsoft, Bloomberg |
| **LeetCode** | [Word Search](https://leetcode.com/problems/word-search/) |


## 1. Problem in Simple Words

**Given:** An `m x n` grid of letters `board` and a string `word`.

**Required:** Return `true` if `word` can be spelled by a path through the grid, where each step moves to a horizontally or vertically adjacent cell and **no cell is used more than once within the same path**.

**Constraints that actually matter:**
- `1 <= m, n <= 6` and `1 <= word.length <= 15` — at most 36 cells and a path of at most 15 steps. These tiny bounds are the hint that an exponential-looking search (backtracking) is the intended solution, not a clever polynomial algorithm.
- Letters are **case sensitive** and are only lowercase and uppercase English letters. `'a'` and `'A'` are different cells, and the board never contains characters like `#`, so such a character can safely be used as a temporary marker.
- Adjacency is **4-directional** only (no diagonals), and the word may start from **any** cell.
- "Cannot reuse a cell" applies **within one path**. Different attempts (other starting cells, other branches) may use the same cell, so any "used" marking must be undone when you backtrack.
- The follow-up asks about search pruning for a larger board. Some copies of the statement also note that a few tests may exceed the stated limits to check that pruning is used. That is the signal to add cheap early-exit checks on top of the basic search.

## 2. Recognition Signals

- "Does a sequence exist as a **path through adjacent cells**" → a search over paths in a grid, i.e. DFS.
- "A cell cannot be used more than once" → you need a visited marker, and since paths branch, it must be **reversible** (mark on entry, unmark on exit).
- "Start anywhere" → loop over all cells as possible starting points.
- Very small `m`, `n`, and word length → exhaustive backtracking is acceptable; the work is in pruning dead branches early.

**Pattern:** Backtracking (DFS with a visited marker that is set on the way in and restored on the way out), started from every cell.

## 3. Core Idea

Define `backtrack(r, c, index)` as "can the word's suffix starting at `index` be spelled by a path that starts at cell `(r, c)`, using only cells not already on the current path?" The answers are:

1. If `index == word.length()`, every character has been matched, so return `true`.
2. If `(r, c)` is outside the grid, already on the current path, or its letter differs from `word[index]`, return `false`.
3. Otherwise mark `(r, c)` as used, try the four neighbours with `index + 1`, then **unmark** `(r, c)` and return whether any neighbour succeeded.

Run this from every cell with `index = 0`. The unmarking step is what lets other branches (and other starting cells) reuse the cell. The search stops mismatching branches immediately, which is the pruning built into the matching check.

**Invariant:** While `backtrack` is running at depth `index`, exactly the cells holding `word[0..index-1]` along the current path are marked, and no others. When it returns, the marks are restored, so the board is in the same state as before the call.

## 4. Approach 1 — Brute Force

**Logic:** Plain backtracking with a separate `boolean[][] visited` matrix and no extra pruning. From every cell, extend the path one adjacent cell at a time, abandoning a branch the moment a letter does not match or a cell was already used.

The search itself cannot be avoided here: the worst case stays exponential in the word length no matter how it is written, because you must explore candidate paths. What this baseline leaves on the table is the extra matrix and any early rejection of impossible inputs.

```java
public boolean exist(char[][] board, String word) {
    int m = board.length;
    int n = board[0].length;
    boolean[][] visited = new boolean[m][n];

    for (int r = 0; r < m; r++) {
        for (int c = 0; c < n; c++) {
            if (search(board, word, r, c, 0, visited)) {
                return true;
            }
        }
    }
    return false;
}

private boolean search(char[][] board, String word, int r, int c, int index, boolean[][] visited) {
    if (index == word.length()) {
        return true;
    }
    if (r < 0 || c < 0 || r >= board.length || c >= board[0].length
            || visited[r][c] || board[r][c] != word.charAt(index)) {
        return false;
    }

    visited[r][c] = true;
    boolean found = search(board, word, r + 1, c, index + 1, visited)
            || search(board, word, r - 1, c, index + 1, visited)
            || search(board, word, r, c + 1, index + 1, visited)
            || search(board, word, r, c - 1, index + 1, visited);
    visited[r][c] = false;
    return found;
}
```

- **Time:** O(m * n * 3^L) in the worst case, where `L = word.length()`. Each of the `m * n` start cells can begin a search; the first step has up to 4 choices, and every later step has at most 3, because the cell just came from is already marked.
- **Space:** O(m * n) for `visited`, plus O(L) for the recursion stack (depth is at most `L`).

**Why it is not optimal:** It is correct, but it allocates an `m x n` matrix that is not needed (the board itself can carry the marker), and it starts a search even when the board cannot possibly contain the word (too few cells, or a letter the board lacks). On larger boards those wasted searches are what the follow-up's pruning removes.

## 5. Approach 2 — Optimized

**Algorithm:**
1. **Quick rejections.** If `word.length() > m * n`, return `false`. Count the letters on the board and the letters the word needs; if the word needs more of any letter than the board has, return `false`.
2. **Start from the rarer end.** If the word's first letter occurs more often on the board than its last letter, reverse the word. A path spelling the reversed word is the same path read backwards, so the answer is unchanged, but fewer starting cells match the first character.
3. **Backtrack in place.** For every cell, call `backtrack(r, c, 0)`. On entering a matching cell, overwrite it with the sentinel `'#'` (the board contains only letters, so `'#'` never matches a word character and doubles as the visited marker). After exploring the four neighbours, restore the original letter.

```java
public boolean exist(char[][] board, String word) {
    int m = board.length;
    int n = board[0].length;
    if (word.length() > m * n) {
        return false;
    }

    int[] boardCount = new int[128];
    for (char[] row : board) {
        for (char ch : row) {
            boardCount[ch]++;
        }
    }

    int[] wordCount = new int[128];
    for (int i = 0; i < word.length(); i++) {
        char ch = word.charAt(i);
        if (++wordCount[ch] > boardCount[ch]) {
            return false;
        }
    }

    if (boardCount[word.charAt(0)] > boardCount[word.charAt(word.length() - 1)]) {
        word = new StringBuilder(word).reverse().toString();
    }

    for (int r = 0; r < m; r++) {
        for (int c = 0; c < n; c++) {
            if (backtrack(board, word, r, c, 0)) {
                return true;
            }
        }
    }
    return false;
}

private boolean backtrack(char[][] board, String word, int r, int c, int index) {
    if (index == word.length()) {
        return true;
    }
    if (r < 0 || c < 0 || r >= board.length || c >= board[0].length
            || board[r][c] != word.charAt(index)) {
        return false;
    }

    char saved = board[r][c];
    board[r][c] = '#';
    boolean found = backtrack(board, word, r + 1, c, index + 1)
            || backtrack(board, word, r - 1, c, index + 1)
            || backtrack(board, word, r, c + 1, index + 1)
            || backtrack(board, word, r, c - 1, index + 1);
    board[r][c] = saved;
    return found;
}
```

- **Time:** O(m * n * 3^L) in the worst case, the same bound as Approach 1, plus O(m * n + L) for the letter counts. In practice the quick rejections and the rarer-end start remove many searches entirely.
- **Space:** O(L) for the recursion stack. The two `int[128]` count arrays are constant size, and no visited matrix is needed.

**Why this is optimal:** The exponential worst case in the word length is inherent to searching for a path, so the realistic improvements are the constant-factor and memory ones: no extra matrix (the board stores the marker and is restored), and early exits that avoid the search altogether when the answer is certainly `false`. This is the standard accepted solution and is what the "search pruning" follow-up asks for.

## 6. Dry Run

Example: `board = [["A","B","C","E"],["S","F","C","S"],["A","D","E","E"]]`, `word = "ABCB"`. Chosen because it returns `false` and so exercises the tricky parts: the "no cell reuse" rule and restoring cells while backtracking. The table follows the core search (Approach 1, or Approach 2 without its quick rejection). Direction order is down, up, right, left; a marked cell is shown as `#`.

| Step | Call (row, col, index) | Cell holds | Needs | Result |
|------|------------------------|------------|-------|--------|
| 1 | (0,0) index 0 | A | A | match, mark `#`, try neighbours |
| 2 | (1,0) index 1 | S | B | mismatch, false |
| 3 | (0,1) index 1 (right of 1) | B | B | match, mark `#` |
| 4 | (1,1) index 2 | F | C | mismatch, false |
| 5 | (0,2) index 2 | C | C | match, mark `#` |
| 6 | (1,2) index 3 | C | B | mismatch, false |
| 7 | (0,3) index 3 | E | B | mismatch, false |
| 8 | (0,1) index 3 (left of step 5's cell) | `#` | B | already on the path, false; step 5 restores its cell to `C`, returns false |
| 9 | (0,0) index 2 (left of step 3's cell) | `#` | C | already on the path, false; step 3 restores its cell to `B`, returns false |
| 10 | back at step 1 | | | no other neighbour in bounds; restore its cell to `A`, return false |
| 11 | start (2,0) index 0 | A | A | match; neighbours S and D both differ from `B`; restore, false |

Exit condition: every starting cell has been tried, and none produced a full match.

**Final answer:** `false`, matching the expected output. Step 8 is the key moment: the search tried to reuse the `B` at (0,1), which is blocked because that cell is still marked. Without that rule the path A, B, C, B would be wrongly accepted. Note that Approach 2's letter-count check would reject this input immediately: the board has only one `B`, but the word needs two.

## 7. Complexity Summary

| Approach | Time | Space (auxiliary) | Notes |
|----------|------|-------------------|-------|
| 1. Backtracking with a visited matrix | O(m * n * 3^L) | O(m * n + L) | Correct baseline, no pruning |
| 2. In-place backtracking with pruning | O(m * n * 3^L) worst case | O(L) | Same worst case; no matrix, early exits, rarer-end start |

`L` is the word length. The input board is O(m * n) in both and, in Approach 2, is temporarily modified and fully restored. The letter-count arrays are fixed-size and add O(1).

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|------|------------------|--------------------------|
| Single-cell board (`m = n = 1`) with a one-letter word | Smallest allowed input; no neighbours to explore | The cell matches `word[0]`, then every neighbour is called with `index == 1 == word.length()` and returns `true` immediately |
| Word longer than the number of cells (`L > m * n`) | A path cannot exist because cells cannot be reused | Approach 2 returns `false` up front; Approach 1 fails naturally because the used cells run out |
| Repeated letters (duplicates) | A letter needed twice must come from two different cells | The marker blocks reusing the same cell, and the letter-count check rejects words that need more copies than the board has |
| Board made of one repeated letter (all same values) | Every cell is a valid start, so the search space is largest and branches overlap | Correctness is unaffected; time approaches the worst case, and the restore step keeps branches independent |
| Word matches only when started from a later cell | A solution that quits after the first failing start would miss it | The outer loops try every cell, returning `true` as soon as any start succeeds |
| Numeric concerns (negative numbers, integer limits) | Not applicable: the data is letters, and the only numbers are indices and counts well below `int` limits | Nothing to guard; indexes are checked for bounds before use |
| Case sensitivity (`'a'` versus `'A'`) | Different characters must not match each other | Comparison uses exact `char` equality, and the count arrays are indexed by the actual character code |
| Maximum sizes (6 x 6 board, word length 15) | Largest legal search | Depth is at most 15, so recursion is safe, and the 3^L bound keeps the search small |

## 9. Java Notes

- **Mutate and restore the board instead of allocating `visited`:** saving the letter, writing `'#'`, and writing it back avoids an `m x n` matrix. It is safe only because `'#'` cannot appear in the input.
- **Restore on every exit path:** the restore line sits after the `||` chain, which is stored in a variable, so it still runs when a neighbour returns `true` early. If you wrote `return a() || b() || ...` directly, you would have no place to restore. This also leaves the caller's board unchanged.
- **`||` short-circuits:** once one direction returns `true`, the remaining directions are skipped, which stops the search as soon as the word is found.
- **`int[128]` indexed by `char`:** a `char` widens to `int` for indexing. Input is limited to English letters, so codes are below 128. Use a larger array (for example `int[65536]`) if arbitrary Unicode were possible.
- **`new StringBuilder(word).reverse().toString()`:** the idiomatic way to reverse a `String`. Strings are immutable, so a new one is created, and reassigning the parameter `word` is fine.
- **Recursion depth** is bounded by the word length (at most 15) and by the number of cells, so stack overflow is not a concern.

## 10. Common Mistakes

- **Not restoring the cell when backtracking.** Leaving the marker in place makes later branches and later starting cells see cells as used when they are not, so valid words are reported as missing. This fails silently. Fix: always restore the cell (or clear `visited`) after exploring all four neighbours and before returning.
- **Having no "used cell" check at all.** The search can step back onto a cell already on the path. On the example it accepts `"ABCB"` by walking A, B, C and then back onto the same B, which gives the wrong answer `true`. Fix: mark the cell on entry and treat marked cells as mismatches.
- **Trying only the first cell that matches `word[0]`.** If that start fails, the word may still exist from another cell. Fix: loop over all cells and return `true` if any start succeeds.
- **Sharing one `visited` array across independent starts without clearing it.** Marks left by an earlier failed start make later starts fail. Fix: unmark on backtrack (which leaves the array clean) or allocate fresh state per start.

## 11. Interview Takeaway

- **Trigger sentence to memorize:** "Find a path through adjacent grid cells spelling a word, with no cell reused → DFS backtracking from every cell: mark the cell on the way in, restore it on the way out."
- **How to explain it aloud in 90 seconds:** "I treat every cell as a possible start and run a depth-first search that tries to match the word one character at a time. At a cell I check three things: it is inside the board, it has not already been used on the current path, and its letter equals the character I need. If so, I mark the cell as used, try the four neighbours for the next character, and then unmark the cell before returning, because other branches and other starting points are allowed to use it. If I run out of characters, I have matched the whole word and return true. The worst case is exponential in the word length, about the number of cells times 3 to the power of the word length, since each step has at most three new directions. The board is tiny, so that is acceptable. For the pruning follow-up, I reject immediately if the word is longer than the number of cells or needs more of a letter than the board has, and I start from the rarer end of the word, since a reversed path is the same path. I mark cells in place with a sentinel instead of allocating a visited matrix."
- **Related problems using this pattern:**
  - LeetCode 212 — Word Search II
  - LeetCode 200 — Number of Islands
  - LeetCode 130 — Surrounded Regions
  - LeetCode 46 — Permutations

## 12. Recall Questions

**Q:** Why must a cell be unmarked when the DFS backtracks out of it?
**A:** The "no reuse" rule applies only within a single path. Other branches and other starting cells are allowed to use that cell, so leaving it marked would wrongly block them.

**Q:** Why is the worst-case time `3^L` per starting cell rather than `4^L`?
**A:** After the first step, one of the four neighbours is the cell you just came from, which is marked as used, so each later step has at most three valid directions.

**Q:** Why is it valid to reverse the word when its first letter is more common on the board than its last letter?
**A:** A path that spells the reversed word is the same path traversed backwards, so one exists exactly when the other does. Starting from the rarer letter means fewer starting cells match, so fewer searches begin.

**Q:** Why do you need to start the search from every cell instead of stopping after the first cell that matches the first letter?
**A:** The first match can be a dead end while another cell with the same letter starts a valid path, so every cell has to be tried before concluding the answer is `false`.

**Q:** Why is `'#'` a safe visited marker here?
**A:** The board and the word contain only English letters, so `'#'` never equals any character of the word and a marked cell is treated as a mismatch.

**Q:** What cheap checks make the search faster on large boards or hopeless inputs?
**A:** Reject immediately when the word is longer than the number of cells, reject when the word needs more copies of a letter than the board has, and start from the rarer end of the word.

## 13. Final Code

```java
public boolean exist(char[][] board, String word) {
    int m = board.length;
    int n = board[0].length;
    if (word.length() > m * n) {
        return false;
    }

    int[] boardCount = new int[128];
    for (char[] row : board) {
        for (char ch : row) {
            boardCount[ch]++;
        }
    }

    int[] wordCount = new int[128];
    for (int i = 0; i < word.length(); i++) {
        char ch = word.charAt(i);
        if (++wordCount[ch] > boardCount[ch]) {
            return false;
        }
    }

    if (boardCount[word.charAt(0)] > boardCount[word.charAt(word.length() - 1)]) {
        word = new StringBuilder(word).reverse().toString();
    }

    for (int r = 0; r < m; r++) {
        for (int c = 0; c < n; c++) {
            if (backtrack(board, word, r, c, 0)) {
                return true;
            }
        }
    }
    return false;
}

private boolean backtrack(char[][] board, String word, int r, int c, int index) {
    if (index == word.length()) {
        return true;
    }
    if (r < 0 || c < 0 || r >= board.length || c >= board[0].length
            || board[r][c] != word.charAt(index)) {
        return false;
    }

    char saved = board[r][c];
    board[r][c] = '#';
    boolean found = backtrack(board, word, r + 1, c, index + 1)
            || backtrack(board, word, r - 1, c, index + 1)
            || backtrack(board, word, r, c + 1, index + 1)
            || backtrack(board, word, r, c - 1, index + 1);
    board[r][c] = saved;
    return found;
}
```

## 14. Self-Test

You have a grid of letters and a word. Decide whether the word can be spelled by a path of horizontally or vertically adjacent cells, never using the same cell twice in one path. Think about how to mark a cell as used so that only the current path is affected, and what cheap checks you can do before searching at all.
