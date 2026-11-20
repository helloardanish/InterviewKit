---
title: "0211 — Design Add and Search Words Data Structure"
layout: default
parent: Trie
grand_parent: Leetcode
nav_order: 211
permalink: /14-Trie/0211-design-add-and-search-words-data-structure/

problem: 211 - Design Add and Search Words Data Structure
url: https://leetcode.com/problems/design-add-and-search-words-data-structure/
difficulty: Medium
section: Trie / Design
patterns: [trie, prefix-tree, dfs, wildcard-search]
data_structures: [trie, hash-map, array]
time: O(L) add, O(26^L) worst-case search
space: O(total characters inserted)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0211 — Design Add and Search Words Data Structure

| Field | Value |
|---|---|
| **Pattern** | Trie + DFS for Wildcard Search |
| **Difficulty** | Medium |
| **Section** | Trie / Design |
| **Time** | O(L) add, O(26^L) worst-case search |
| **Space** | O(total characters inserted) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Amazon, Google, Meta, Microsoft, Bloomberg |
| **LeetCode** | [Design Add and Search Words Data Structure](https://leetcode.com/problems/design-add-and-search-words-data-structure/) |

## 1. Problem in Simple Words

**Given:**
- A design task: implement a `WordDictionary` class supporting two operations:
  - `addWord(String word)` — add a word to the dictionary.
  - `search(String word)` — return `true` if any word in the dictionary matches the pattern.

**Required:**
- In `search`, the pattern may contain the wildcard character `'.'`, which matches **any single letter**.
- All other characters in the pattern must match exactly.
- A word matches only if it has the **same length** as the pattern (since `.` matches exactly one character).

**Constraints that actually matter:**
- `1 <= word.length <= 25` — words are short, so the worst-case branching of wildcard search is bounded (though `26^L` is theoretical; the trie structure prunes it heavily).
- `word` in `addWord` consists of lowercase English letters only — no wildcards on insert.
- `word` in `search` consists of lowercase English letters and `'.'` — at most 25 characters.
- At most `10⁴` calls to `addWord` and `search` combined — the trie must handle many operations, and search must be efficient in practice.
- The dictionary may contain words that are prefixes of each other — the trie must distinguish exact words from prefixes.

**Interpretation of the 403 error:** The URL fetch was blocked by LeetCode's anti-bot protection (HTTP 403 Forbidden). The statement above is reconstructed from the well-known LeetCode 211 problem; verify against the live page in a browser or the LeetCode mobile app. Alternatives: search "Design Add and Search Words Data Structure" in the LeetCode problems list, or check the problem's discussion tab.

## 2. Recognition Signals

- The problem asks to **add words** and **search with wildcards** — a dictionary with pattern matching.
- The wildcard `.` matches exactly one character, and words must match in length — this is a **character-by-character traversal** problem.
- The natural structure is a **trie** (prefix tree): each node represents a prefix, and edges are characters. Insertion is a walk; search with `.` requires **branching over all children** at wildcard positions.
- Recognition trigger: *"dictionary with add and wildcard search, `.` matches any letter"* → **trie + DFS/backtracking at wildcard nodes**.
- This is a direct extension of LeetCode 208 (Implement Trie), adding wildcard handling on top.

## 3. Core Idea

**Intuition.** A trie stores words character by character. For a normal search, we walk down the trie following each character. For a wildcard `.`, we cannot know which child to follow, so we must **try all existing children** — a DFS/backtracking step. If any branch leads to a match of the remaining pattern, the search succeeds.

**Key observation.** The wildcard forces branching only at `.` positions. In the worst case, a pattern of all `.` characters explores the entire trie (up to `26^L` theoretical paths, but in practice bounded by the number of stored words and their shared prefixes). For typical inputs, the trie's structure prunes most branches.

**Variables.**
- `TrieNode root` — the root of the trie.
- Each `TrieNode` has:
  - `TrieNode[] children = new TrieNode[26]` — one slot per lowercase letter.
  - `boolean isEnd` — whether a word ends at this node.

**Invariant.** For any node, the path from the root spells a prefix of every word inserted through it. `isEnd` is true iff that prefix is exactly an inserted word.

**Search algorithm (DFS).**
- `dfs(node, word, index)`:
  - If `index == word.length()`, return `node.isEnd` (we consumed the whole pattern; the node must be a word end).
  - Let `c = word.charAt(index)`.
  - If `c != '.'`: follow `node.children[c - 'a']` if it exists; recurse with `index + 1`.
  - If `c == '.'`: try **every non-null child**; if any recursive call returns true, return true.
  - Return false if no branch matches.

## 4. Approach 1 — Trie with DFS for Wildcard Search (Standard)

**Logic.** Build a trie with `addWord`. For `search`, run a recursive DFS that handles `.` by branching over all children. Use a 26-slot array per node for O(1) child lookup.

### Java code (array-based trie + DFS)

```java
class WordDictionary {
    private static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        boolean isEnd = false;
    }

    private final TrieNode root;

    public WordDictionary() {
        root = new TrieNode();
    }

    public void addWord(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            int idx = c - 'a';
            if (node.children[idx] == null) {
                node.children[idx] = new TrieNode();
            }
            node = node.children[idx];
        }
        node.isEnd = true;
    }

    public boolean search(String word) {
        return dfs(root, word, 0);
    }

    private boolean dfs(TrieNode node, String word, int index) {
        if (node == null) return false;
        if (index == word.length()) return node.isEnd;

        char c = word.charAt(index);
        if (c != '.') {
            return dfs(node.children[c - 'a'], word, index + 1);
        }

        // Wildcard: try all existing children.
        for (int i = 0; i < 26; i++) {
            if (node.children[i] != null && dfs(node.children[i], word, index + 1)) {
                return true;
            }
        }
        return false;
    }
}
```

**Complexity.**
- `addWord`: O(L) time, O(L) new nodes in the worst case.
- `search`: O(L) for patterns without `.`; O(26^L) worst case for all-wildcard patterns, but pruned heavily by the trie's structure (only existing branches are explored).
- Space: O(total characters inserted) for the trie; O(L) recursion stack for search.

**When it is useful.** This is the standard, interview-ready solution. The array-based trie gives fast child lookup, and the DFS cleanly handles wildcards.

## 5. Approach 2 — Optimized: Hash-Map Children with DFS

**Algorithm steps.**
1. Define `TrieNode` with `Map<Character, TrieNode> children` and `boolean isEnd`.
2. `addWord`: walk down, using `computeIfAbsent` to create missing children, mark `isEnd` at the end.
3. `search`: DFS from the root. For a normal character, look up the child in the map; for `.`, iterate over all values in the map and recurse.

### Java code (hash-map trie + DFS)

```java
import java.util.*;

class WordDictionary {
    private static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEnd = false;
    }

    private final TrieNode root;

    public WordDictionary() {
        root = new TrieNode();
    }

    public void addWord(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> new TrieNode());
        }
        node.isEnd = true;
    }

    public boolean search(String word) {
        return dfs(root, word, 0);
    }

    private boolean dfs(TrieNode node, String word, int index) {
        if (index == word.length()) return node.isEnd;

        char c = word.charAt(index);
        if (c != '.') {
            TrieNode next = node.children.get(c);
            return next != null && dfs(next, word, index + 1);
        }

        // Wildcard: try all existing children.
        for (TrieNode child : node.children.values()) {
            if (dfs(child, word, index + 1)) {
                return true;
            }
        }
        return false;
    }
}
```

**Explanation of important logic.**
- **`computeIfAbsent`** creates a child only when needed, keeping insert concise.
- **Wildcard iteration over `children.values()`** is naturally sparse — only existing children are tried, which is more efficient than iterating 26 slots when the trie is sparse.
- **Same DFS structure** — the only difference is how children are stored and accessed.

**Complexity.**
- `addWord`: O(L) average time (hash map operations are O(1) average).
- `search`: O(L) average for non-wildcard patterns; O(branching^L) worst case for wildcards, where branching is the number of existing children at each wildcard node.
- Space: O(total characters inserted) nodes, each with a small hash map.

**Why the array version is preferred here.** The alphabet is fixed at 26 lowercase letters, so the array gives predictable O(1) lookup and is the standard interview answer. The hash-map version is more memory-efficient for sparse tries and naturally iterates only existing children at wildcards, but adds hashing overhead. Both are accepted; the array version is simpler to reason about for this problem.

## 6. Dry Run

**Example operations:**
`addWord("bad")`, `addWord("dad")`, `addWord("mad")`,
`search("pad")`, `search("bad")`, `search(".ad")`, `search("b..")`.

This sequence is useful because it exercises exact search (hit and miss), a leading wildcard, and multiple wildcards.

**Trie after insertions (relevant paths):**
- root → b → a → d (isEnd at d)
- root → d → a → d (isEnd at d)
- root → m → a → d (isEnd at d)

**Trace of searches:**

| Search | Pattern | DFS path | Result |
|---|---|---|---|
| `search("pad")` | p, a, d | root.children['p'] is null → fail | `false` |
| `search("bad")` | b, a, d | root → b → a → d, `isEnd` true | `true` |
| `search(".ad")` | ., a, d | root: try all children (b, d, m); for each, follow a → d; all three reach `isEnd` | `true` |
| `search("b..")` | b, ., . | root → b → a (only child) → d (only child), `isEnd` true | `true` |

**Detailed trace of `search(".ad")`:**
- Index 0 is `.`: iterate children `b`, `d`, `m`.
  - Branch `b`: index 1 is `a` → child `a` exists → index 2 is `d` → child `d` exists → index 3 == length → `isEnd` true → return true.
- Short-circuit on the first successful branch.

**Final results:** `[false, true, true, true]`.

## 7. Complexity Summary

| Approach | `addWord` | `search` (no wildcard) | `search` (all wildcards) | Space |
|---|---|---|---|---|
| Array-based trie | O(L) | O(L) | O(26^L) worst case, pruned | O(total prefixes × 26) slots |
| Hash-map trie | O(L) avg | O(L) avg | O(branching^L) worst case, pruned | O(total prefixes) nodes |

Input space is O(L) per word and is not counted as auxiliary. The wildcard worst case is theoretical; in practice, the number of stored words and shared prefixes bounds the search.

## 8. Edge Cases

| Case | Why it matters | How the code handles it |
|---|---|---|
| Search for a word not in the dictionary | Must return `false` | DFS reaches a null child or a non-`isEnd` node |
| Search with all wildcards (`"..."`) | Maximum branching | DFS tries all existing children at each level; returns true if any stored word has that length |
| Search for a prefix of a stored word | Must return `false` (length mismatch) | DFS reaches `index == length` at a non-`isEnd` node |
| Search with a wildcard at the last position | Tests `isEnd` check after wildcard branching | DFS branches, then checks `isEnd` at `index == length` |
| Empty dictionary | No words to match | `root` has no children; DFS returns false |
| Word that is a prefix of another word | Must distinguish exact word from prefix | `isEnd` flag handles it |
| Single-character word and pattern | Tests root-level wildcard | DFS at root branches over children; `isEnd` check at index 1 |
| Pattern longer than any stored word | No match possible | DFS reaches a null child before consuming the pattern |

## 9. Java Notes

- **`c - 'a'`** converts a lowercase letter to an index `0..25` for the array-based trie.
- **`computeIfAbsent`** is the idiomatic one-liner for hash-map children.
- **Wildcard iteration:** in the array version, loop `i = 0..25` and check `node.children[i] != null`; in the hash-map version, iterate `node.children.values()` directly.
- **Recursion depth** is at most `L ≤ 25`, so recursion is completely safe.
- **`private static class TrieNode`** — static so it does not hold an implicit reference to the outer `WordDictionary` instance.
- **LeetCode method signatures:** `addWord(String word)` and `search(String word)` — match them exactly.
- **Do not confuse with LeetCode 208:** the only addition is the wildcard handling in `search`; `addWord` is identical to `insert`.

## 10. Common Mistakes

- **Treating `.` as matching zero or more characters.** The wildcard matches **exactly one** character. Patterns and words must have the same length.
- **Forgetting to check `isEnd` at the end of the pattern.** A path may exist for a prefix, but the search should only succeed if the full pattern ends at a word end.
- **Not branching over all children at a wildcard.** Trying only one child (e.g., the first non-null) misses valid matches.
- **Recursing into null children.** Always check for null before recursing; otherwise you get a `NullPointerException` or wasted calls.
- **Using a `Set<String>` instead of a trie.** A set requires comparing the pattern against every stored word (O(N · L) per search), which is far slower than trie traversal for large dictionaries.
- **Marking `isEnd` at intermediate nodes.** Only the final node of an inserted word should be marked.
- **Assuming the wildcard worst case is always hit.** In practice, the trie prunes most branches; do not over-optimize prematurely.

## 11. Interview Takeaway

**Recognition trigger to memorize:** *"Dictionary with add and search where `.` matches any single letter → trie + DFS; at a wildcard node, branch over all existing children; at the end of the pattern, check `isEnd`."*

**90-second explanation:** "I use a trie to store the words, with each node holding a 26-slot children array and an `isEnd` flag. `addWord` walks down the trie, creating nodes as needed and marking `isEnd` at the final node. For `search`, I run a DFS from the root. At each position, if the character is a normal letter, I follow that single child. If it is a wildcard `.`, I try every non-null child and recurse — if any branch matches the rest of the pattern, I return true. When the pattern is fully consumed, I return `node.isEnd` to ensure the match is a complete word, not just a prefix. Add is O(L); search is O(L) without wildcards and up to O(26^L) in the worst case with all wildcards, but the trie structure prunes most branches in practice."

**Related problems:**
- [Implement Trie (Prefix Tree)](https://leetcode.com/problems/implement-trie-prefix-tree/) — the base trie without wildcards; this problem is a direct extension.
- [Word Search II](https://leetcode.com/problems/word-search-ii/) — trie + grid DFS for finding all dictionary words in a board.
- [Replace Words](https://leetcode.com/problems/replace-words/) — trie for finding the shortest root prefix of each word.
- [Maximum XOR of Two Numbers in an Array](https://leetcode.com/problems/maximum-xor-of-two-numbers-in-an-array/) — binary trie for XOR maximization, showing the trie's versatility beyond strings.

## 12. Recall Questions

**Q1. Why is a trie better than a `Set<String>` for this problem?**
A: A set requires comparing the pattern against every stored word, which is O(N · L) per search. A trie traverses only the relevant prefixes, and wildcards branch only over existing children, making search much faster for large dictionaries.

**Q2. How does the DFS handle the wildcard `.`?**
A: At a wildcard position, the DFS iterates over all non-null children and recurses into each. If any branch matches the rest of the pattern, it returns true; otherwise false.

**Q3. Why must we check `isEnd` at the end of the pattern?**
A: Because a path may exist for a prefix without the prefix being a complete word. `isEnd` distinguishes "this prefix is a word" from "this prefix is only part of a longer word".

**Q4. What are the time complexities of `addWord` and `search`?**
A: `addWord` is O(L). `search` is O(L) for patterns without wildcards and O(26^L) in the worst case with all wildcards, though the trie prunes most branches in practice.

**Q5. Why does the pattern and word length have to match?**
A: Because `.` matches exactly one character. A pattern of length `k` can only match words of length `k`.

**Q6. What happens if you search for a pattern longer than any stored word?**
A: The DFS reaches a null child before consuming the pattern and returns false.

**Q7. How would you modify the solution to support `*` (matching zero or more characters)?**
A: In the DFS, at a `*`, you would try (a) consuming zero characters (recurse with the same index + 1 in the pattern) and (b) consuming one character (recurse with the same pattern position but advance the trie node). This is a more complex wildcard and requires careful index management.

**Q8. Why is the array-based trie preferred over the hash-map trie here?**
A: The alphabet is fixed at 26 lowercase letters, so the array gives O(1) lookup with no hashing overhead and predictable performance. The hash-map version is better for large or sparse alphabets.

## 13. Final Code

### Java code (final, optimized array-based trie + DFS)

```java
class WordDictionary {
    private static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        boolean isEnd = false;
    }

    private final TrieNode root;

    public WordDictionary() {
        root = new TrieNode();
    }

    public void addWord(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            int idx = c - 'a';
            if (node.children[idx] == null) {
                node.children[idx] = new TrieNode();
            }
            node = node.children[idx];
        }
        node.isEnd = true;
    }

    public boolean search(String word) {
        return dfs(root, word, 0);
    }

    private boolean dfs(TrieNode node, String word, int index) {
        if (node == null) return false;
        if (index == word.length()) return node.isEnd;

        char c = word.charAt(index);
        if (c != '.') {
            return dfs(node.children[c - 'a'], word, index + 1);
        }

        // Wildcard: try all existing children.
        for (int i = 0; i < 26; i++) {
            if (node.children[i] != null && dfs(node.children[i], word, index + 1)) {
                return true;
            }
        }
        return false;
    }
}
```

## 14. Self-Test

Consider the operations: `addWord("cat")`, `addWord("car")`, `addWord("card")`, `addWord("dog")`, then `search("c.t")`, `search("c..")`, `search("c...")`, `search("...")`, `search("do.")`, `search("do..")`. Draw the trie after the insertions, marking `isEnd` at the appropriate nodes. Then trace each search, showing which branches the DFS explores at each wildcard position and which node it reaches at the end of the pattern. Finally, answer: for `search("c...")`, why does the DFS return `true` even though `"c"` alone is not a word? And for `search("do..")`, why does the DFS return `false` even though `"dog"` is in the dictionary?