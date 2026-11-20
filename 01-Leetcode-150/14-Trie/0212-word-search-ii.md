---
title: "0212 — Word Search II"
layout: default
parent: Trie
grand_parent: Leetcode
nav_order: 212
permalink: /14-Trie/0212-word-search-ii/

problem: 212 - Word Search II
url: https://leetcode.com/problems/word-search-ii/
difficulty: Hard
section: Trie / Backtracking
patterns: [trie, backtracking, grid-dfs, pruning]
data_structures: [trie, array, hash-set]
time: O(M * N * 4^L) worst case, pruned heavily by trie
space: O(total characters in words)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0212 — Word Search II

| Field | Value |
|---|---|
| **Pattern** | Trie + Grid Backtracking (DFS with Pruning) |
| **Difficulty** | Hard |
| **Section** | Trie / Backtracking |
| **Time** | O(M · N · 4^L) worst case, heavily pruned |
| **Space** | O(total characters in words) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Amazon, Google, Meta, Microsoft, Bloomberg |
| **LeetCode** | [Word Search II](https://leetcode.com/problems/word-search-ii/) |

## 1. Problem in Simple Words

**Given:**
- An `m × n` board of characters.
- A list of words `words`.

**Required:**
- Return **all words** from `words` that can be formed by a path on the board.
- A path is a sequence of **adjacent** cells (horizontally or vertically), and **each cell may be used at most once per word**.
- The same word should appear only once in the result, even if multiple paths spell it.

**Constraints that actually matter:**
- `m, n <= 12` — the board has at most 144 cells, so per-cell DFS is feasible, but naive per-word search would be too slow.
- `1 <= words.length <= 3 × 10⁴` — the word list is large, so we cannot afford to run a full board search for each word independently.
- `1 <= words[i].length <= 10` — words are short, so DFS depth is bounded by 10, keeping backtracking manageable.
- Words consist of lowercase English letters; the board also contains lowercase letters.
- Words may share prefixes — this is the key property that makes a **trie** the right structure: shared prefixes are traversed once.

**Interpretation of the 403 error:** The URL fetch was blocked by LeetCode's anti-bot protection (HTTP 403 Forbidden). The statement above is reconstructed from the well-known LeetCode 212 problem; verify against the live page in a browser or the LeetCode mobile app. Alternatives: search "Word Search II" in the LeetCode problems list, or check the problem's discussion tab.

## 2. Recognition Signals

- We must find **multiple dictionary words** in a **grid** via adjacency paths — a combination of **trie** (for the dictionary) and **grid DFS/backtracking** (for the board).
- The word list is **large** (`3 × 10⁴`) but words are **short** (`≤ 10`), and they **share prefixes** — this screams "build a trie and traverse the board once, following trie paths".
- Naively running Word Search I for each word would be `O(W · M · N · 4^L)`, which is far too slow. The trie collapses shared prefixes, so the board is traversed once with all words simultaneously.
- Recognition trigger: *"find all dictionary words in a grid"* → **build a trie of the words, then DFS from every cell, walking the trie in parallel with the board path**.

## 3. Core Idea

**Intuition.** Instead of searching for each word separately, insert all words into a trie. Then run a DFS from every board cell, moving to adjacent cells and simultaneously advancing a pointer in the trie. Whenever the trie pointer reaches a node marked `isEnd`, we have found a word — add it to the result. This way, shared prefixes are explored once, and the board is traversed a single time (with branching).

**Key observation.** The trie acts as a **pruning oracle**: if the current board character has no corresponding child in the trie, we stop that branch immediately. This prunes the vast majority of paths, making the search far faster than `4^L` per cell in practice.

**Variables.**
- `TrieNode root` — the trie of all words.
- Each `TrieNode` has:
  - `TrieNode[] children = new TrieNode[26]`.
  - `String word` — the complete word ending at this node (or `null`). Storing the word directly avoids rebuilding it during DFS.
- `List<String> result` — the found words.
- `boolean[][] visited` (or in-place board mutation) — cells on the current path.

**DFS invariant.** `dfs(r, c, node)` explores all paths starting at `(r, c)` whose characters match the trie path from `node`. When `node.word != null`, the corresponding word has been found.

**Optimization: remove found words.** After adding a word to the result, set `node.word = null` to avoid adding duplicates. Optionally, prune leaf nodes (nodes with no children and no word) to shrink the trie during search.

## 4. Approach 1 — Naive: Word Search I for Each Word

**Logic.** For each word in `words`, run a grid DFS from every cell to check whether the word can be formed. Collect the words that succeed.

### Java code (naive per-word DFS)

```java
import java.util.*;

class Solution {
    public List<String> findWords(char[][] board, String[] words) {
        List<String> result = new ArrayList<>();
        for (String word : words) {
            if (exists(board, word)) result.add(word);
        }
        return result;
    }

    private boolean exists(char[][] board, String word) {
        int m = board.length, n = board[0].length;
        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                if (dfs(board, word, r, c, 0)) return true;
            }
        }
        return false;
    }

    private boolean dfs(char[][] board, String word, int r, int c, int idx) {
        if (idx == word.length()) return true;
        if (r < 0 || r >= board.length || c < 0 || c >= board[0].length) return false;
        if (board[r][c] != word.charAt(idx)) return false;

        char saved = board[r][c];
        board[r][c] = '#';
        boolean found = dfs(board, word, r + 1, c, idx + 1)
                        || dfs(board, word, r - 1, c, idx + 1)
                        || dfs(board, word, r, c + 1, idx + 1)
                        || dfs(board, word, r, c - 1, idx + 1);
        board[r][c] = saved;
        return found;
    }
}
```

**Complexity.**
- Time: O(W · M · N · 4^L), where `W = words.length`, `L = max word length`. With `W = 3 × 10⁴`, this is far too slow.
- Space: O(L) recursion depth.

**Why it is insufficient.** The board is re-traversed for every word, and shared prefixes are recomputed each time. This is the baseline that motivates the trie solution.

## 5. Approach 2 — Optimized: Trie + Grid DFS (Standard)

**Algorithm steps.**
1. Build a trie from all words. Each node stores `children[26]` and a `String word` (the complete word if this node ends a word, else `null`).
2. For every cell `(r, c)` on the board, start a DFS with the trie root.
3. In `dfs(r, c, node)`:
   - If out of bounds or the cell is already on the current path, return.
   - Look up `child = node.children[board[r][c] - 'a']`. If `child == null`, return (prune).
   - If `child.word != null`, add it to the result and set `child.word = null` (deduplicate).
   - Mark the cell visited, recurse into all four neighbors with `child`, then unmark.
4. Return the result list.

### Java code (trie + DFS)

```java
import java.util.*;

class Solution {
    private static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        String word = null;
    }

    private final List<String> result = new ArrayList<>();
    private int m, n;

    public List<String> findWords(char[][] board, String[] words) {
        TrieNode root = buildTrie(words);
        m = board.length;
        n = board[0].length;

        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                dfs(board, r, c, root);
            }
        }
        return result;
    }

    private TrieNode buildTrie(String[] words) {
        TrieNode root = new TrieNode();
        for (String word : words) {
            TrieNode node = root;
            for (char ch : word.toCharArray()) {
                int idx = ch - 'a';
                if (node.children[idx] == null) {
                    node.children[idx] = new TrieNode();
                }
                node = node.children[idx];
            }
            node.word = word;
        }
        return root;
    }

    private void dfs(char[][] board, int r, int c, TrieNode node) {
        if (r < 0 || r >= m || c < 0 || c >= n) return;
        char ch = board[r][c];
        if (ch == '#') return;
        TrieNode next = node.children[ch - 'a'];
        if (next == null) return;

        if (next.word != null) {
            result.add(next.word);
            next.word = null;
        }

        board[r][c] = '#';
        dfs(board, r + 1, c, next);
        dfs(board, r - 1, c, next);
        dfs(board, r, c + 1, next);
        dfs(board, r, c - 1, next);
        board[r][c] = ch;
    }
}
```

**Complexity.**
- Build trie: O(total characters in `words`).
- Board DFS: O(M · N · 4^L) worst case, but the trie prunes almost all branches in practice. Each cell is the start of at most `4^L` paths theoretically, but only paths matching trie prefixes are explored.
- Space: O(total characters in `words`) for the trie; O(L) recursion depth.

**Why it is optimal.** The trie collapses shared prefixes, so the board is traversed with all words simultaneously. This is the standard, interview-ready solution.

## 6. Dry Run

**Example:**
`board = [['o','a','a','n'],['e','t','a','e'],['i','h','k','r'],['i','f','l','v']]`,
`words = ["oath","pea","eat","rain"]`.

This example is useful because it exercises multiple words sharing prefixes (`"ea"` in `"eat"` and `"pea"`), paths that require backtracking, and a word that does not exist (`"rain"`).

**Trie after insertions (relevant paths):**
- o → a → t → h (word "oath")
- p → e → a (word "pea")
- e → a → t (word "eat")
- r → a → i → n (word "rain")

**DFS from cell (0,0) = 'o':**
- `o` matches trie child `o`. Node is not a word end.
- Neighbors: (0,1)='a', (1,0)='e'. Only `a` matches trie child of `o`.
- `(0,1)='a'` matches `o → a`. Not a word end.
- Neighbors: (0,0) visited, (0,2)='a' (no child), (1,1)='t' matches `o → a → t`.
- `(1,1)='t'` matches `o → a → t`. Not a word end.
- Neighbors: (1,0)='e' (no child), (1,2)='a' (no child), (2,1)='h' matches `o → a → t → h`.
- `(2,1)='h'` matches `o → a → t → h`. `word = "oath"` → add to result.

**DFS from cell (1,3) = 'e'** (finds "eat"):
- `e` matches trie child `e`.
- Neighbors: (0,3)='n' (no child), (2,3)='r' (no child), (1,2)='a' matches `e → a`.
- `(1,2)='a'` matches `e → a`. Not a word end.
- Neighbors: (0,2)='a' (no child), (2,2)='k' (no child), (1,1)='t' matches `e → a → t`.
- `(1,1)='t'` matches `e → a → t`. `word = "eat"` → add to result.

**Words not found:** `"pea"` — no `'p'` on the board. `"rain"` — no valid path from `'r'` through `'a'`, `'i'`, `'n'`.

**Final result:** `["oath","eat"]` (order may vary).

## 7. Complexity Summary

| Approach | Time | Space (auxiliary) | Notes |
|---|---|---|---|
| Naive per-word DFS | O(W · M · N · 4^L) | O(L) | Too slow for large W |
| Trie + grid DFS | O(M · N · 4^L) worst case, pruned heavily | O(total chars in words) | Standard; shared prefixes traversed once |

Input space is O(M · N) for the board and O(total chars) for the words. The trie is O(total chars) auxiliary.

## 8. Edge Cases

| Case | Why it matters | How the code handles it |
|---|---|---|
| Empty board or empty word list | Nothing to search | Returns empty list |
| Single-cell board, single-char word | Minimal case | DFS from the only cell matches |
| Word longer than any path | Cannot be formed | Trie path exists but DFS cannot complete it; no `word` node reached |
| Duplicate words in the list | Must not duplicate in result | `next.word = null` after adding prevents duplicates |
| Word that is a prefix of another | Both must be found | `word` is stored at the prefix node and the longer word's node |
| Board contains `'#'` | `'#'` is used as visited marker | Input is lowercase letters only, so no conflict |
| All words share a long prefix | Tests trie efficiency | Shared prefix traversed once per starting cell, not once per word |
| No words found | Return empty list | `result` stays empty |

## 9. Java Notes

- **In-place board mutation** (`board[r][c] = '#'`) is the standard way to mark visited cells; restore after recursion. This avoids a separate `boolean[][] visited` and is faster.
- **`String word` field in `TrieNode`** — storing the complete word at the terminal node avoids rebuilding the string during DFS. This is a common and efficient trick.
- **`next.word = null`** after adding to the result prevents duplicate additions when multiple paths spell the same word.
- **Recursion depth** is at most `L ≤ 10`, so recursion is safe.
- **`private static class TrieNode`** — static to avoid holding an outer reference.
- **Board dimensions** `m` and `n` are stored as fields to avoid passing them through every recursive call.
- **Optional pruning:** after finding a word, if the trie node has no children, you can remove it from its parent to shrink the trie. This is an optimization, not required for correctness.

## 10. Common Mistakes

- **Running Word Search I for each word separately.** This is O(W · M · N · 4^L) and TLEs for large word lists. The trie is the fix.
- **Not deduplicating found words.** Multiple paths can spell the same word; without `next.word = null`, the result contains duplicates.
- **Forgetting to restore the board cell** after backtracking — corrupts subsequent searches.
- **Using a separate `visited` array instead of in-place marking** — works but is slower and uses more memory; in-place is preferred.
- **Storing the word at every node instead of only terminal nodes** — wastes memory and causes false matches.
- **Not pruning when `next == null`** — exploring branches that cannot match any word, wasting time.
- **Confusing the trie node with the board cell** — the DFS advances both the board position and the trie node simultaneously; they are different.

## 11. Interview Takeaway

**Recognition trigger to memorize:** *"Find all dictionary words in a grid via adjacent paths → build a trie of the words, then DFS from every cell advancing the trie pointer in parallel; prune when the trie has no matching child; deduplicate by nulling the word at terminal nodes."*

**90-second explanation:** "I build a trie from all the words, storing the complete word at each terminal node. Then I run a DFS from every cell on the board. At each step, I look up the current board character in the trie node's children. If there's no matching child, I prune that branch immediately. If the child node has a word, I add it to the result and null it out to avoid duplicates. Then I mark the cell as visited, recurse into all four neighbors with the child node, and unmark. The trie collapses shared prefixes, so the board is traversed once with all words simultaneously — this is far faster than searching for each word independently. Time is O(M · N · 4^L) worst case but heavily pruned; space is O(total characters in words) for the trie plus O(L) recursion depth."

**Related problems:**
- [Word Search](https://leetcode.com/problems/word-search/) — the single-word version; same grid DFS without the trie.
- [Implement Trie (Prefix Tree)](https://leetcode.com/problems/implement-trie-prefix-tree/) — the base trie structure used here.
- [Design Add and Search Words Data Structure](https://leetcode.com/problems/design-add-and-search-words-data-structure/) — trie with wildcard search; another trie + DFS variant.
- [Boggle](https://www.lintcode.com/problem/boggle/) — find all words on a board, essentially the same problem.

## 12. Recall Questions

**Q1. Why is a trie better than searching for each word separately?**
A: The trie collapses shared prefixes, so the board is traversed once with all words simultaneously. Searching each word separately re-traverses the board for every word, which is O(W · M · N · 4^L) and too slow for large word lists.

**Q2. How does the DFS use the trie to prune?**
A: At each board cell, the DFS looks up the character in the current trie node's children. If there is no matching child, no word in the dictionary continues with this path, so the branch is abandoned immediately.

**Q3. Why do we store the complete word at the terminal node?**
A: To avoid rebuilding the string during DFS. When the DFS reaches a node with a non-null `word`, the word has been found, and we can add it directly.

**Q4. How do we avoid adding duplicate words to the result?**
A: After adding a word, we set `node.word = null`. Subsequent paths that reach the same node will not re-add it.

**Q5. Why is in-place board marking preferred over a `visited` array?**
A: It avoids allocating and clearing a `boolean[][]`, and the restore-after-recursion pattern is simple. The board character `'#'` is safe because the input contains only lowercase letters.

**Q6. What are the time and space complexities?**
A: Time is O(M · N · 4^L) worst case, but the trie prunes heavily in practice. Space is O(total characters in words) for the trie plus O(L) recursion depth.

**Q7. What happens if a word is a prefix of another word?**
A: Both are found. The prefix word is stored at its terminal node, and the longer word at its own terminal node. The DFS adds the prefix when it reaches that node and continues to the longer word.

**Q8. How would you optimize further if the trie has many dead branches after finding words?**
A: After finding a word, if the terminal node has no children, remove it from its parent's `children` array. This prunes dead branches and speeds up subsequent searches. It is optional but useful for large word lists.

## 13. Final Code

### Java code (final, optimized trie + grid DFS)

```java
import java.util.*;

class Solution {
    private static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        String word = null;
    }

    private final List<String> result = new ArrayList<>();
    private int m, n;

    public List<String> findWords(char[][] board, String[] words) {
        TrieNode root = buildTrie(words);
        m = board.length;
        n = board[0].length;

        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                dfs(board, r, c, root);
            }
        }
        return result;
    }

    private TrieNode buildTrie(String[] words) {
        TrieNode root = new TrieNode();
        for (String word : words) {
            TrieNode node = root;
            for (char ch : word.toCharArray()) {
                int idx = ch - 'a';
                if (node.children[idx] == null) {
                    node.children[idx] = new TrieNode();
                }
                node = node.children[idx];
            }
            node.word = word;
        }
        return root;
    }

    private void dfs(char[][] board, int r, int c, TrieNode node) {
        if (r < 0 || r >= m || c < 0 || c >= n) return;
        char ch = board[r][c];
        if (ch == '#') return;
        TrieNode next = node.children[ch - 'a'];
        if (next == null) return;

        if (next.word != null) {
            result.add(next.word);
            next.word = null;
        }

        board[r][c] = '#';
        dfs(board, r + 1, c, next);
        dfs(board, r - 1, c, next);
        dfs(board, r, c + 1, next);
        dfs(board, r, c - 1, next);
        board[r][c] = ch;
    }
}
```

## 14. Self-Test

Consider `board = [["a","b"],["c","d"]]` and `words = ["ab","ac","bd","abd","abcd"]`. Build the trie after inserting all words, showing which nodes are terminal. Then trace the DFS from cell `(0,0)='a'`, listing each path explored and where pruning occurs. Answer: which words are found, and for `"abd"`, why does the DFS return to `'a'` before exploring the `'c'` branch? Finally, explain what happens if the word list is `["ab","ab","ab"]` — how does the code avoid adding `"ab"` three times?