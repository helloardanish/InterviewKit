---
title: "0208 — Implement Trie (Prefix Tree)"
layout: default
parent: Trie
grand_parent: Leetcode
nav_order: 208
permalink: /14-Trie/0208-implement-trie-prefix-tree/

problem: 208 - Implement Trie (Prefix Tree)
url: https://leetcode.com/problems/implement-trie-prefix-tree/
difficulty: Medium
section: Trie / Design
patterns: [trie, prefix-tree, string-design]
data_structures: [trie, hash-map, array]
time: O(L) per operation
space: O(total characters inserted)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0208 — Implement Trie (Prefix Tree)

| Field | Value |
|---|---|
| **Pattern** | Trie (Prefix Tree) |
| **Difficulty** | Medium |
| **Section** | Trie / Design |
| **Time** | O(L) per operation |
| **Space** | O(total characters inserted) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Amazon, Google, Meta, Microsoft, Bloomberg |
| **LeetCode** | [Implement Trie (Prefix Tree)](https://leetcode.com/problems/implement-trie-prefix-tree/) |

## 1. Problem in Simple Words

**Given:**
- A design task: implement a `Trie` class supporting three operations:
  - `insert(String word)` — add a word to the trie.
  - `search(String word)` — return `true` if the exact word was inserted.
  - `startsWith(String prefix)` — return `true` if any inserted word starts with the given prefix.

**Required:**
- All operations must be efficient, ideally O(L) where `L` is the length of the word or prefix.
- The trie must distinguish between a **complete word** and a **prefix** — e.g., after inserting `"apple"`, `search("app")` must return `false` while `startsWith("app")` must return `true`.

**Constraints that actually matter:**
- `1 <= word.length, prefix.length <= 2000` — words can be long, so O(L²) string operations per query would be wasteful; O(L) traversal is the target.
- All inputs consist of lowercase English letters — this allows a fixed-size `TrieNode[26]` array per node, which is faster and more memory-predictable than a `HashMap`.
- At most `3 × 10⁴` total calls — the trie must handle many operations efficiently; O(L) per operation gives at most `6 × 10⁷` character steps, which is fine.
- `search` and `startsWith` are only called on non-empty strings.

**Interpretation of the 403 error:** The URL fetch was blocked by LeetCode's anti-bot protection (HTTP 403 Forbidden). The statement above is reconstructed from the well-known LeetCode 208 problem; verify against the live page in a browser or the LeetCode mobile app. Alternatives: search "Implement Trie" in the LeetCode problems list, or check the problem's discussion tab.

## 2. Recognition Signals

- The problem explicitly asks to implement a **Trie** (prefix tree) — the name itself is the signal.
- The operations are **insert**, **exact search**, and **prefix search** — the three canonical trie operations.
- The distinction between `search` and `startsWith` is the key design clue: the trie must track **word endings** separately from mere path existence.
- Recognition trigger: *"implement a data structure supporting word insertion, exact lookup, and prefix lookup in O(L)"* → **Trie with a boolean `isEnd` flag per node**.
- Related signals in other problems: autocomplete, spell check, IP routing (longest prefix match), and word dictionaries all reduce to tries.

## 3. Core Idea

**Intuition.** A trie is a tree where each node represents a prefix, and each edge represents a character. The root is the empty prefix. Inserting a word walks down the tree, creating nodes as needed, and marks the final node as a word end. Searching walks down the tree and checks the `isEnd` flag; prefix checking walks down and only checks that the path exists.

**Key observation.** The `isEnd` flag is what distinguishes `search` from `startsWith`. After inserting `"apple"`, the path `a → p → p` exists (so `startsWith("app")` is true), but the node at `p` is not marked `isEnd` (so `search("app")` is false).

**Variables.**
- `TrieNode root` — the root of the trie, representing the empty prefix.
- Each `TrieNode` has:
  - `TrieNode[] children = new TrieNode[26]` — one slot per lowercase letter.
  - `boolean isEnd` — whether a word ends at this node.

**Invariant.** For any node, the path from the root to that node spells a prefix of every word inserted through it. `isEnd` is true iff that prefix is exactly a word that was inserted.

**Why a `TrieNode[26]` array.** With only lowercase letters, an array gives O(1) child lookup with no hashing overhead. A `HashMap<Character, TrieNode>` is more memory-efficient for sparse tries but slower per operation; for this problem, the array is the standard choice.

## 4. Approach 1 — Trie with `TrieNode[26]` Array (Standard)

**Logic.** Define a `TrieNode` class with a 26-slot children array and an `isEnd` flag. The `Trie` class holds a root. Each operation traverses from the root, character by character, creating nodes on insert and checking existence on search.

### Java code (array-based trie)

```java
class Trie {
    private static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        boolean isEnd = false;
    }

    private final TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    public void insert(String word) {
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
        TrieNode node = traverse(word);
        return node != null && node.isEnd;
    }

    public boolean startsWith(String prefix) {
        return traverse(prefix) != null;
    }

    private TrieNode traverse(String s) {
        TrieNode node = root;
        for (char c : s.toCharArray()) {
            int idx = c - 'a';
            if (node.children[idx] == null) return null;
            node = node.children[idx];
        }
        return node;
    }
}
```

**Complexity.**
- Time: O(L) per operation — each character is processed once.
- Space: O(total characters inserted × 26) in the worst case for the array slots, though only non-null slots are allocated nodes. More precisely, O(total distinct prefixes) nodes, each with a 26-slot array.

**When it is useful.** This is the standard, interview-ready implementation. The 26-slot array gives the fastest child lookup, and the `traverse` helper avoids duplicating the walk logic between `search` and `startsWith`.

## 5. Approach 2 — Optimized: Trie with `HashMap` Children

**Algorithm steps.**
1. Define `TrieNode` with `Map<Character, TrieNode> children` and `boolean isEnd`.
2. `insert`: walk down, using `computeIfAbsent` to create missing children, mark `isEnd` at the end.
3. `search`: walk down; if any character is missing, return `false`; otherwise return `node.isEnd`.
4. `startsWith`: walk down; return `true` if the full path exists, regardless of `isEnd`.

### Java code (hash-map trie)

```java
import java.util.*;

class Trie {
    private static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEnd = false;
    }

    private final TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    public void insert(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> new TrieNode());
        }
        node.isEnd = true;
    }

    public boolean search(String word) {
        TrieNode node = traverse(word);
        return node != null && node.isEnd;
    }

    public boolean startsWith(String prefix) {
        return traverse(prefix) != null;
    }

    private TrieNode traverse(String s) {
        TrieNode node = root;
        for (char c : s.toCharArray()) {
            node = node.children.get(c);
            if (node == null) return null;
        }
        return node;
    }
}
```

**Explanation of important logic.**
- **`computeIfAbsent`** creates a child only when needed, keeping insert concise.
- **`HashMap` children** save memory when the alphabet is large or the trie is sparse, at the cost of hashing overhead per lookup.
- **Same `traverse` helper** — the search/startsWith distinction is still just the `isEnd` check.

**Complexity.**
- Time: O(L) average per operation — hash lookups are O(1) average.
- Space: O(total distinct prefixes) nodes, each with a small map (only existing children stored).

**Why the array version is preferred here.** The alphabet is fixed at 26 lowercase letters, so the array avoids hashing overhead and gives predictable performance. The hash-map version is better when the alphabet is large or the trie is very sparse; for this problem, both are accepted, but the array version is the standard interview answer.

## 6. Dry Run

**Example operations:**
`insert("apple")`, `search("apple")`, `search("app")`, `startsWith("app")`, `insert("app")`, `search("app")`.

This sequence is useful because it exercises the exact distinction between `search` and `startsWith`, and shows how inserting a prefix changes `search` results.

**Trace (array-based trie, showing only relevant nodes):**

| Operation | Path taken | Nodes created | `isEnd` at final node | Result |
|---|---|---|---|---|
| `insert("apple")` | root → a → p → p → l → e | a, p, p, l, e | true at `e` | — |
| `search("apple")` | root → a → p → p → l → e | none (all exist) | true | `true` |
| `search("app")` | root → a → p → p | none | false (no word ends here yet) | `false` |
| `startsWith("app")` | root → a → p → p | none | false (irrelevant) | `true` |
| `insert("app")` | root → a → p → p | none (path exists) | set true at second `p` | — |
| `search("app")` | root → a → p → p | none | true | `true` |

**Final result:** `[true, false, true, true]` for the four query operations, matching the expected behavior.

## 7. Complexity Summary

| Approach | Time per op | Space | Notes |
|---|---|---|---|
| Array-based trie | O(L) | O(total prefixes × 26) slots | Fastest lookup; standard for fixed alphabet |
| Hash-map trie | O(L) average | O(total prefixes) nodes with small maps | Memory-efficient for sparse/large alphabets |

Input space is O(L) per word and is not counted as auxiliary. Both approaches are asymptotically optimal for trie operations.

## 8. Edge Cases

| Case | Why it matters | How the code handles it |
|---|---|---|
| `search` on a prefix of an inserted word | Must return `false` if the prefix itself was not inserted | `isEnd` is false at the prefix node |
| `startsWith` on a full word | Must return `true` even though the node is an end | `traverse` succeeds; `isEnd` is ignored |
| Inserting a word that is a prefix of an existing word | Must mark the shorter word's end | `isEnd` set at the shorter word's final node |
| Inserting a word that extends an existing word | Path exists; only the new end is marked | No new nodes needed; `isEnd` set at the new final node |
| Searching for a non-existent word sharing a prefix | Must return `false` | `traverse` returns `null` at the missing character |
| Single-character words | Tests the root's children array | `idx = c - 'a'` works for any lowercase letter |
| Max length (2000 characters) | Tests O(L) performance | Each character processed once; no recursion |
| Duplicate insertions | Harmless | `isEnd` is already true; no change |

## 9. Java Notes

- **`c - 'a'`** converts a lowercase letter to an index `0..25`. This is the standard trick for fixed-alphabet tries.
- **`TrieNode[] children = new TrieNode[26]`** allocates 26 references per node; only non-null slots point to actual children. This is memory-heavy for sparse tries but fast.
- **`computeIfAbsent`** is the idiomatic one-liner for hash-map children: `node.children.computeIfAbsent(c, k -> new TrieNode())`.
- **`final TrieNode root`** — the root never changes; marking it `final` documents that.
- **No recursion needed** — all operations are iterative walks, so there is no stack-depth concern even for 2000-character words.
- **`private static class TrieNode`** — static so it does not hold an implicit reference to the outer `Trie` instance, saving memory.
- **LeetCode's method signatures** are `insert(String word)`, `search(String word)`, `startsWith(String prefix)` — match them exactly.

## 10. Common Mistakes

- **Forgetting the `isEnd` flag.** Without it, `search` and `startsWith` become identical, and `search("app")` after inserting `"apple"` wrongly returns `true`.
- **Marking `isEnd` at every node on the path.** Only the final node of the inserted word should be marked; intermediate nodes are prefixes, not words.
- **Using a `boolean` visited array instead of a trie.** A flat set of words cannot answer prefix queries efficiently; the trie's structure is the point.
- **Recursive traversal for long words.** Recursion depth equals word length (up to 2000), which is safe here but unnecessary; iterative is simpler and avoids stack concerns.
- **Forgetting to create intermediate nodes on insert.** The path must be built character by character; skipping creation breaks the trie.
- **Using `HashMap` when the alphabet is fixed.** The array version is faster and more predictable for 26 lowercase letters.
- **Confusing `search` and `startsWith` return logic.** `search` requires `node != null && node.isEnd`; `startsWith` only requires `node != null`.

## 11. Interview Takeaway

**Recognition trigger to memorize:** *"Implement a data structure with insert, exact search, and prefix search in O(L) → trie with a 26-slot children array per node and an `isEnd` flag; `search` checks `isEnd`, `startsWith` only checks path existence."*

**90-second explanation:** "A trie is a tree where each node represents a prefix and each edge is a character. I define a `TrieNode` with a 26-slot array of children (one per lowercase letter) and a boolean `isEnd`. Insert walks down the word, creating nodes as needed, and marks `isEnd` at the final node. Search walks down and returns true only if the path exists and the final node has `isEnd` set. StartsWith walks down and returns true if the path exists, ignoring `isEnd`. All operations are O(L) time, and space is O(total distinct prefixes). The key distinction is the `isEnd` flag — it is what separates 'this is a word' from 'this is just a prefix'."

**Related problems:**
- [Design Add and Search Words Data Structure](https://leetcode.com/problems/design-add-and-search-words-data-structure/) — trie with wildcard `.` support, requiring DFS over children.
- [Word Search II](https://leetcode.com/problems/word-search-ii/) — trie + grid DFS for finding all dictionary words in a board.
- [Replace Words](https://leetcode.com/problems/replace-words/) — trie for finding the shortest root prefix of each word.
- [Maximum XOR of Two Numbers in an Array](https://leetcode.com/problems/maximum-xor-of-two-numbers-in-an-array/) — binary trie for XOR maximization.

## 12. Recall Questions

**Q1. What is the difference between `search` and `startsWith` in a trie?**
A: `search` requires the path to exist **and** the final node to be marked `isEnd` (the exact word was inserted). `startsWith` only requires the path to exist, regardless of `isEnd`.

**Q2. Why do we need the `isEnd` flag at all?**
A: Without it, we cannot distinguish a complete word from a mere prefix. After inserting `"apple"`, the path for `"app"` exists, but `"app"` is not a word unless explicitly inserted.

**Q3. What are the time and space complexities?**
A: O(L) time per operation, where `L` is the word/prefix length. Space is O(total distinct prefixes) nodes, each with a 26-slot array (or a hash map) and a boolean.

**Q4. Why use a `TrieNode[26]` array instead of a `HashMap`?**
A: The alphabet is fixed at 26 lowercase letters, so an array gives O(1) lookup with no hashing overhead. A hash map is better for large or sparse alphabets but slower per operation.

**Q5. What happens if you insert a word that is a prefix of an existing word?**
A: The path already exists, so no new nodes are created; you simply set `isEnd = true` at the shorter word's final node. Now both `search` calls return true.

**Q6. Why is the trie traversal iterative rather than recursive?**
A: Iteration avoids stack-depth concerns for long words (up to 2000 characters) and is simpler to write. Recursion would also work but adds no benefit.

**Q7. How would you extend this trie to support wildcard search (`.` matches any letter)?**
A: In `search`, when encountering `.`, recursively try all non-null children. This is the approach used in LeetCode 211 (Design Add and Search Words Data Structure).

**Q8. What is the memory cost of the array-based trie, and when would you prefer a hash map?**
A: Each node allocates 26 references, even if most are null. For sparse tries or large alphabets, a hash map stores only existing children, saving memory at the cost of hashing overhead.

## 13. Final Code

### Java code (final, optimized array-based trie)

```java
class Trie {
    private static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        boolean isEnd = false;
    }

    private final TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    public void insert(String word) {
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
        TrieNode node = traverse(word);
        return node != null && node.isEnd;
    }

    public boolean startsWith(String prefix) {
        return traverse(prefix) != null;
    }

    private TrieNode traverse(String s) {
        TrieNode node = root;
        for (char c : s.toCharArray()) {
            int idx = c - 'a';
            if (node.children[idx] == null) return null;
            node = node.children[idx];
        }
        return node;
    }
}
```

## 14. Self-Test

Consider the operation sequence: `insert("cat")`, `insert("car")`, `insert("card")`, `search("ca")`, `startsWith("ca")`, `search("car")`, `search("card")`, `startsWith("card")`. Trace the trie after all three insertions, showing which nodes are marked `isEnd`. Then answer: for each of the five query operations, what is the result and why? Finally, explain what would change if we inserted `"ca"` after the three initial insertions — which `isEnd` flags change, and how do the five query results differ?