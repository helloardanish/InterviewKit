---
title: "0017 — Letter Combinations of a Phone Number"
layout: default
parent: Backtracking
grand_parent: Leetcode
nav_order: 17
permalink: /15-Backtracking/0017-letter-combinations-of-a-phone-number/

problem: 17 - Letter Combinations of a Phone Number
url: https://leetcode.com/problems/letter-combinations-of-a-phone-number/
difficulty: Medium
section: Backtracking
patterns: [backtracking, recursion, cartesian-product]
data_structures: [string, array, hash-map]
time: O(4^n * n)
space: O(n)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0017 — Letter Combinations of a Phone Number

| Field | Value |
|---|---|
| **Pattern** | Backtracking (Cartesian Product) |
| **Difficulty** | Medium |
| **Section** | Backtracking |
| **Time** | O(4ⁿ · n) |
| **Space** | O(n) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Amazon, Google, Meta, Microsoft, Bloomberg |
| **LeetCode** | [Letter Combinations of a Phone Number](https://leetcode.com/problems/letter-combinations-of-a-phone-number/) |

## 1. Problem in Simple Words

**Given:**
- A string `digits` containing digits from `2` to `9`.

**Required:**
- Return **all possible letter combinations** that the number could represent, using the classic phone keypad mapping:
  - `2 → abc`, `3 → def`, `4 → ghi`, `5 → jkl`, `6 → mno`, `7 → pqrs`, `8 → tuv`, `9 → wxyz`.
- Each digit contributes exactly **one** letter, in the order the digits appear.
- Return the combinations in **any order**.

**Constraints that actually matter:**
- `0 <= digits.length <= 4` — the input is tiny, so the total number of combinations is at most `4⁴ = 256`. Brute force and backtracking are both trivially fast.
- `digits[i]` is a digit from `2` to `9` — no `0` or `1`, so every digit has at least 3 letters. This means no combination is empty due to a missing mapping.
- Empty input (`digits = ""`) must return an **empty list**, not a list containing an empty string. This is a classic off-by-one trap.
- The order of the output does not matter — the judge accepts any permutation of the combinations.

**Interpretation of the 403 error:** The URL fetch was blocked by LeetCode's anti-bot protection (HTTP 403 Forbidden). The statement above is reconstructed from the well-known LeetCode 17 problem; verify against the live page in a browser or the LeetCode mobile app. Alternatives: search "Letter Combinations of a Phone Number" in the LeetCode problems list, or check the problem's discussion tab.

## 2. Recognition Signals

- The problem asks for **all possible combinations** of choices, where each position has a small set of options — the canonical **Cartesian product** shape.
- The number of choices per position varies (3 or 4 letters depending on the digit), and the total output is the product of the choice counts — this is exactly what **backtracking** enumerates.
- The phrase *"return all possible"* (not "count" or "minimum") is the strongest signal for **exhaustive enumeration via recursion**.
- Recognition trigger: *"given a sequence of choices, generate every combination"* → **backtracking: pick one option per position, recurse, then undo the pick**.

## 3. Core Idea

**Intuition.** Think of the input as a sequence of "slots", one per digit. Each slot must be filled with one letter from that digit's mapping. We want every possible way to fill all slots. This is a depth-first enumeration: fix a letter for the first digit, recurse to fill the rest, then try the next letter for the first digit, and so on.

**Key observation.** The recursion depth is exactly `digits.length()`, and at each level we branch over the letters of the current digit. The total number of leaves is the product of the branching factors, which is at most `4⁴ = 256` — small enough that no pruning or memoization is needed.

**Variables.**
- `String[] map` — index `i` holds the letters for digit `i` (e.g., `map[2] = "abc"`).
- `StringBuilder path` — the current partial combination.
- `List<String> result` — all completed combinations.
- `int index` — the current digit position being filled.

**Invariant.** At the start of `backtrack(index)`, `path` contains exactly `index` letters, one chosen for each of the first `index` digits. When `index == digits.length()`, `path` is a complete combination.

**Backtracking template.**
- **Base case:** if `index == digits.length()`, add `path.toString()` to `result` and return.
- **Recursive case:** for each letter `c` in `map[digits.charAt(index) - '0']`:
  - Append `c` to `path`.
  - Recurse with `index + 1`.
  - Remove `c` from `path` (undo).

## 4. Approach 1 — Brute Force: Iterative Cartesian Product

**Logic.** Start with a list containing the empty string. For each digit, replace the list with the Cartesian product of the current list and the digit's letters: for every existing prefix and every letter of the digit, append the new combination. After processing all digits, the list contains every combination.

### Java code (iterative Cartesian product)

```java
import java.util.*;

class Solution {
    public List<String> letterCombinations(String digits) {
        List<String> result = new ArrayList<>();
        if (digits.isEmpty()) return result;

        String[] map = {
            "", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"
        };

        result.add("");
        for (char d : digits.toCharArray()) {
            String letters = map[d - '0'];
            List<String> next = new ArrayList<>();
            for (String prefix : result) {
                for (char c : letters.toCharArray()) {
                    next.add(prefix + c);
                }
            }
            result = next;
        }
        return result;
    }
}
```

**Complexity.**
- Time: O(4ⁿ · n) — there are at most `4ⁿ` combinations, and building each string of length `n` costs O(n).
- Space: O(4ⁿ · n) for the result list (which is the required output, so it is not auxiliary).

**When it is useful.** This is a clean, iterative way to build a Cartesian product without recursion. It is easy to reason about and avoids stack concerns, but it allocates a new list at every digit, which is slightly wasteful. It is a good baseline to contrast with backtracking.

## 5. Approach 2 — Optimized: Backtracking with a StringBuilder

**Algorithm steps.**
1. If `digits` is empty, return an empty list immediately.
2. Build the phone keypad mapping as a `String[]`.
3. Call `backtrack(0)`:
   - If `index == digits.length()`, add `path.toString()` to `result` and return.
   - For each letter `c` in `map[digits.charAt(index) - '0']`:
     - `path.append(c)`.
     - `backtrack(index + 1)`.
     - `path.deleteCharAt(path.length() - 1)` (undo).
4. Return `result`.

### Java code (backtracking)

```java
import java.util.*;

class Solution {
    private static final String[] MAP = {
        "", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"
    };

    public List<String> letterCombinations(String digits) {
        List<String> result = new ArrayList<>();
        if (digits.isEmpty()) return result;

        backtrack(digits, 0, new StringBuilder(), result);
        return result;
    }

    private void backtrack(String digits, int index, StringBuilder path, List<String> result) {
        if (index == digits.length()) {
            result.add(path.toString());
            return;
        }

        String letters = MAP[digits.charAt(index) - '0'];
        for (int i = 0; i < letters.length(); i++) {
            path.append(letters.charAt(i));
            backtrack(digits, index + 1, path, result);
            path.deleteCharAt(path.length() - 1);
        }
    }
}
```

**Explanation of important logic.**
- **The empty-input check is essential.** Without it, the base case would fire immediately and add an empty string to the result, producing `[""]` instead of `[]`. The problem explicitly requires `[]`.
- **`StringBuilder` instead of string concatenation** avoids O(n) copies at every level; appending and deleting are O(1) amortized.
- **The undo step** (`deleteCharAt`) is what makes it backtracking rather than plain recursion: after exploring one letter, we restore `path` to its previous state before trying the next letter.
- **The mapping array is 1-indexed by digit** — `MAP[2] = "abc"`, etc. Index 0 and 1 are unused but present to keep indexing simple.

**Complexity.**
- Time: O(4ⁿ · n) — at most `4ⁿ` leaves, each requiring O(n) to copy the `StringBuilder` into a string.
- Space: O(n) auxiliary for the recursion stack and `StringBuilder`, plus O(4ⁿ · n) for the output.

**Why it is optimal.** The output itself has size O(4ⁿ · n), so any correct algorithm must spend at least that much time writing the answer. Backtracking achieves it with only O(n) auxiliary space (the recursion stack and the current path), which is optimal for the auxiliary space.

## 6. Dry Run

**Example:** `digits = "23"`.

This example is useful because it has two digits with different branching factors (3 letters each), and it is the canonical LeetCode example. It shows the full Cartesian product of two small sets.

**Mapping:** `2 → "abc"`, `3 → "def"`.

**Backtracking trace:**

| Call | index | path before | Letter chosen | path after append | Action |
|---|---|---|---|---|---|
| 1 | 0 | "" | a | "a" | recurse |
| 2 | 1 | "a" | d | "ad" | recurse |
| 3 | 2 | "ad" | — | "ad" | base case → add "ad", return |
| 2 | 1 | "a" | e | "ae" | recurse |
| 3 | 2 | "ae" | — | "ae" | add "ae", return |
| 2 | 1 | "a" | f | "af" | recurse |
| 3 | 2 | "af" | — | "af" | add "af", return |
| 1 | 0 | "" | b | "b" | recurse |
| 2 | 1 | "b" | d | "bd" | recurse → add "bd" |
| 2 | 1 | "b" | e | "be" | recurse → add "be" |
| 2 | 1 | "b" | f | "bf" | recurse → add "bf" |
| 1 | 0 | "" | c | "c" | recurse |
| 2 | 1 | "c" | d | "cd" | add "cd" |
| 2 | 1 | "c" | e | "ce" | add "ce" |
| 2 | 1 | "c" | f | "cf" | add "cf" |

**Final result:** `["ad","ae","af","bd","be","bf","cd","ce","cf"]` (order may vary). The total is `3 × 3 = 9` combinations.

## 7. Complexity Summary

| Approach | Time | Space (auxiliary) | Notes |
|---|---|---|---|
| Iterative Cartesian product | O(4ⁿ · n) | O(4ⁿ · n) extra list at each step | Simple; allocates new lists |
| Backtracking + StringBuilder | O(4ⁿ · n) | O(n) | Optimal auxiliary space; standard interview answer |

The output size is O(4ⁿ · n) and is not counted as auxiliary. Backtracking's O(n) auxiliary space is optimal.

## 8. Edge Cases

| Case | Why it matters | How the code handles it |
|---|---|---|
| `digits = ""` | Must return `[]`, not `[""]` | Early return before backtracking |
| `digits = "2"` | Single digit, 3 combinations | Base case fires after one letter; 3 combinations returned |
| `digits = "7"` or `"9"` | These digits have **4** letters | The loop iterates over all 4 letters; no special case needed |
| `digits` contains `0` or `1` | Excluded by constraints (digits are `2..9`) | Would map to `""` and produce no combinations; not a concern here |
| Maximum length 4 | Tests the upper bound | At most `4⁴ = 256` combinations; trivially fast |
| All digits the same (e.g., `"2222"`) | Tests that each position is independent | Backtracking treats each position separately; `3⁴ = 81` combinations |
| Mixed 3-letter and 4-letter digits | Tests variable branching | The loop adapts to `letters.length()` automatically |

## 9. Java Notes

- **`StringBuilder`** is the right tool for building and undoing the current path. `append` and `deleteCharAt(length - 1)` are O(1) amortized; string concatenation would be O(n) per step.
- **`path.toString()`** creates a snapshot of the current path when a combination is complete. This is necessary because the `StringBuilder` is mutated afterward.
- **The mapping array** `String[] MAP` is a static constant; index `0` and `1` are empty strings to keep digit-to-index mapping direct (`MAP[digit - '0']`).
- **`digits.charAt(index) - '0'`** converts a character digit to its integer value. This is the standard idiom.
- **Recursion depth** is at most `digits.length() ≤ 4`, so there is no stack concern.
- **Passing `result` as a parameter** (rather than using an instance field) keeps the method stateless and avoids issues if the `Solution` object is reused.

## 10. Common Mistakes

- **Forgetting the empty-input check.** Without it, the base case fires on the empty string and adds `""`, producing `[""]` instead of `[]`.
- **Using string concatenation instead of `StringBuilder`.** `path + c` creates a new string at every level, which is O(n) per step and wasteful, though it still passes for `n ≤ 4`.
- **Forgetting the undo step.** Without `deleteCharAt`, the path keeps growing and the combinations are wrong (too long).
- **Adding to the result before the base case.** The combination is only complete when `index == digits.length()`; adding earlier produces partial combinations.
- **Hardcoding the letters for each digit incorrectly.** The mapping must match the phone keypad exactly; `7` and `9` have 4 letters, not 3.
- **Using a `List<Character>` or `char[]` for the path without proper undo.** A `StringBuilder` is simpler and less error-prone.
- **Assuming the output order matters.** It does not; any order is accepted.

## 11. Interview Takeaway

**Recognition trigger to memorize:** *"Generate all combinations where each position has a small set of choices → backtracking: pick one choice per position, recurse, then undo; base case when all positions are filled."*

**90-second explanation:** "This is a Cartesian product of the letter sets for each digit, so I use backtracking. I map each digit to its letters, then recurse position by position: at position `index`, I try each letter for `digits[index]`, append it to the current path, recurse to fill the rest, and then remove it before trying the next letter. When `index` reaches the length of `digits`, the path is a complete combination, so I add a copy to the result. The empty-input case returns an empty list, not a list with an empty string. The recursion depth is at most 4, and the total number of combinations is at most 4⁴ = 256, so it is trivially fast. Time is O(4ⁿ · n) to build the output, and auxiliary space is O(n) for the recursion stack and path."

**Related problems:**
- [Generate Parentheses](https://leetcode.com/problems/generate-parentheses/) — backtracking with a validity constraint at each step.
- [Combination Sum](https://leetcode.com/problems/combination-sum/) — backtracking over a set of choices with reuse.
- [Permutations](https://leetcode.com/problems/permutations/) — backtracking over all orderings of a set.
- [Subsets](https://leetcode.com/problems/subsets/) — backtracking over include/exclude decisions.

## 12. Recall Questions

**Q1. Why does `digits = ""` return `[]` instead of `[""]`?**
A: The problem defines an empty input as having no combinations. Without the early return, the base case would fire immediately and add the empty string, producing `[""]`, which is wrong.

**Q2. What is the role of the undo step in backtracking?**
A: After exploring one letter for the current position, the undo (`deleteCharAt`) restores the path to its state before that letter was appended, so the next letter can be tried without leftover characters.

**Q3. Why is `StringBuilder` preferred over string concatenation here?**
A: `StringBuilder.append` and `deleteCharAt` are O(1) amortized, while string concatenation creates a new string of length O(n) at every step. For `n ≤ 4` the difference is small, but `StringBuilder` is the correct habit.

**Q4. What are the time and space complexities?**
A: Time is O(4ⁿ · n) — at most `4ⁿ` combinations, each of length `n`. Auxiliary space is O(n) for the recursion stack and `StringBuilder`; the output itself is O(4ⁿ · n).

**Q5. Why is the recursion depth bounded by 4?**
A: The input length is at most 4, and the recursion advances one position per call. So the depth is at most `digits.length() ≤ 4`.

**Q6. How does the code handle digits `7` and `9`, which have 4 letters?**
A: The loop iterates over `letters.length()`, which is 4 for these digits, so all 4 letters are tried. No special case is needed.

**Q7. What is the total number of combinations for `"2222"`?**
A: Each digit has 3 letters, so `3⁴ = 81` combinations.

**Q8. Why is the result order not important?**
A: The problem says "in any order", and the judge compares the set of combinations, not their sequence. Backtracking produces them in a deterministic order, but any order would be accepted.

## 13. Final Code

### Java code (final, optimized backtracking)

```java
import java.util.*;

class Solution {
    private static final String[] MAP = {
        "", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"
    };

    public List<String> letterCombinations(String digits) {
        List<String> result = new ArrayList<>();
        if (digits.isEmpty()) return result;

        backtrack(digits, 0, new StringBuilder(), result);
        return result;
    }

    private void backtrack(String digits, int index, StringBuilder path, List<String> result) {
        if (index == digits.length()) {
            result.add(path.toString());
            return;
        }

        String letters = MAP[digits.charAt(index) - '0'];
        for (int i = 0; i < letters.length(); i++) {
            path.append(letters.charAt(i));
            backtrack(digits, index + 1, path, result);
            path.deleteCharAt(path.length() - 1);
        }
    }
}
```

## 14. Self-Test

Consider `digits = "79"`. Write out the backtracking trace: for each call, show the current index, the path before and after appending each letter, and the moment a combination is added to the result. Then answer: how many combinations are produced in total, and why? Finally, explain what would go wrong if the undo step (`path.deleteCharAt(...)`) were removed — what would the result list contain instead, and why would the combinations have the wrong length?