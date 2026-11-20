---
title: "0022 — Generate Parentheses"
layout: default
parent: Backtracking
grand_parent: Leetcode
nav_order: 22
permalink: /15-Backtracking/0022-generate-parentheses/

problem: 22 - Generate Parentheses
url: https://leetcode.com/problems/generate-parentheses/
difficulty: Medium
section: Backtracking
patterns: [backtracking, recursion, constraint-satisfaction]
data_structures: [string, list]
time: O(4^n / sqrt(n))
space: O(n)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0022 — Generate Parentheses

| Field | Value |
|---|---|
| **Pattern** | Backtracking (Constraint-Based Generation) |
| **Difficulty** | Medium |
| **Section** | Backtracking |
| **Time** | O(4ⁿ / √n) |
| **Space** | O(n) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Amazon, Google, Meta, Microsoft, Bloomberg |
| **LeetCode** | [Generate Parentheses](https://leetcode.com/problems/generate-parentheses/) |

## 1. Problem in Simple Words

**Given:**
- An integer `n` representing the number of pairs of parentheses.

**Required:**
- Return **all well-formed** (balanced) parentheses strings of length `2n`.
- A well-formed string has every opening `(` matched with a later closing `)`, and at no point does the number of closings exceed the number of openings.
- Return the strings in any order.

**Constraints that actually matter:**
- `1 <= n <= 8` — the input is tiny; the total number of well-formed strings is the `n`-th **Catalan number** `Cₙ = (2n)! / ((n+1)! · n!)`. For `n = 8`, that is 1430 strings.
- Only two characters are involved: `(` and `)`.
- The output size is the Catalan number, so any correct algorithm must spend at least that much time writing the answer.
- The order of the output does not matter; the judge accepts any valid enumeration.

**Interpretation of the 403 error:** The URL fetch was blocked by LeetCode's anti-bot protection (HTTP 403 Forbidden). The statement above is reconstructed from the well-known LeetCode 22 problem; verify against the live page in a browser or the LeetCode mobile app. Alternatives: search "Generate Parentheses" in the LeetCode problems list, or check the problem's discussion tab.

## 2. Recognition Signals

- The problem asks for **all valid configurations** of a sequence under a **balance constraint** — the canonical **constraint-based backtracking** shape.
- The constraint is **local and incremental**: at each step, we can add `(` if we still have openings left, and we can add `)` only if the number of closings so far is less than the number of openings so far.
- The phrase *"generate all combinations"* plus a validity rule signals **backtracking with pruning**: build the string character by character, prune as soon as the prefix becomes invalid, and record the string when it reaches length `2n`.
- Recognition trigger: *"generate all balanced parentheses strings of length 2n"* → **backtracking with counters for open and close**.

## 3. Core Idea

**Intuition.** Build the string left to right. At each position, we have two choices: add `(` or add `)`. But not every choice is valid:
- We can add `(` only if we have used fewer than `n` openings.
- We can add `)` only if the number of closings is **strictly less** than the number of openings (otherwise the string would have a prefix with more closings than openings, which can never be balanced).

By exploring only valid choices, we generate exactly the well-formed strings, with no invalid prefixes and no need to filter at the end.

**Key observation.** The two counters `open` and `close` fully capture the state needed for the validity check. The recursion depth is exactly `2n`, and the branching factor is at most 2, but the constraints prune roughly half the branches, resulting in the Catalan number of leaves.

**Variables.**
- `int n` — number of pairs.
- `StringBuilder path` — the current partial string.
- `List<String> result` — all completed well-formed strings.
- `int open` — number of `(` used so far.
- `int close` — number of `)` used so far.

**Invariant.** At the start of `backtrack(open, close)`, `path` has length `open + close`, contains exactly `open` opening and `close` closing parentheses, and every prefix of `path` is balanced (i.e., at no point does `close` exceed `open`). When `path.length() == 2n`, `path` is a complete well-formed string.

**Backtracking template.**
- **Base case:** if `path.length() == 2n`, add `path.toString()` to `result` and return.
- **Add `(`:** if `open < n`, append `(`, recurse with `open + 1, close`, then remove.
- **Add `)`:** if `close < open`, append `)`, recurse with `open, close + 1`, then remove.

## 4. Approach 1 — Brute Force: Generate All 2^(2n) Strings and Filter

**Logic.** Generate all `2^(2n)` strings of length `2n` over `{(, )}`, and for each, check whether it is balanced. This is correct but wasteful: most strings are invalid.

### Java code (brute-force generation)

```java
import java.util.*;

class Solution {
    public List<String> generateParenthesis(int n) {
        List<String> result = new ArrayList<>();
        generate(new char[2 * n], 0, result);
        return result;
    }

    private void generate(char[] current, int pos, List<String> result) {
        if (pos == current.length) {
            if (isBalanced(current)) {
                result.add(new String(current));
            }
            return;
        }
        current[pos] = '(';
        generate(current, pos + 1, result);
        current[pos] = ')';
        generate(current, pos + 1, result);
    }

    private boolean isBalanced(char[] s) {
        int balance = 0;
        for (char c : s) {
            balance += (c == '(') ? 1 : -1;
            if (balance < 0) return false;
        }
        return balance == 0;
    }
}
```

**Complexity.**
- Time: O(2^(2n) · n) — generates `4ⁿ` strings, each checked in O(n).
- Space: O(n) for the current string plus the output.

**Why it is less efficient.** For `n = 8`, `4⁸ = 65,536` strings are generated, while only `1430` are valid. Backtracking constructs only valid strings.

## 5. Approach 2 — Optimized: Backtracking with Open/Close Counters

**Algorithm steps.**
1. Initialize `result` and `path`.
2. Call `backtrack(0, 0)`:
   - If `path.length() == 2n`, add `path.toString()` to `result` and return.
   - If `open < n`, append `(`, recurse with `open + 1, close`, remove.
   - If `close < open`, append `)`, recurse with `open, close + 1`, remove.
3. Return `result`.

### Java code (backtracking with counters)

```java
import java.util.*;

class Solution {
    public List<String> generateParenthesis(int n) {
        List<String> result = new ArrayList<>();
        backtrack(n, 0, 0, new StringBuilder(), result);
        return result;
    }

    private void backtrack(int n, int open, int close, StringBuilder path, List<String> result) {
        if (path.length() == 2 * n) {
            result.add(path.toString());
            return;
        }

        if (open < n) {
            path.append('(');
            backtrack(n, open + 1, close, path, result);
            path.deleteCharAt(path.length() - 1);
        }

        if (close < open) {
            path.append(')');
            backtrack(n, open, close + 1, path, result);
            path.deleteCharAt(path.length() - 1);
        }
    }
}
```

**Explanation of important logic.**
- **The two conditions are the entire validity rule.** `open < n` ensures we never exceed `n` opening parentheses. `close < open` ensures we never place a closing parenthesis that would make the prefix unbalanced.
- **The base case fires exactly when `path.length() == 2n`.** Because the conditions guarantee balance, every leaf of the recursion tree is a valid string — no filtering is needed.
- **`StringBuilder` with append/delete** avoids O(n) string copies at every step; each append and delete is O(1) amortized.
- **`path.toString()`** snapshots the current string when a complete valid string is found.

**Complexity.**
- Time: O(4ⁿ / √n) — the number of valid strings is the `n`-th Catalan number `Cₙ ≈ 4ⁿ / (n^(3/2) · √π)`, and each requires O(n) to copy into the result. So the total time is O(n · Cₙ) = O(4ⁿ / √n).
- Space: O(n) auxiliary for the recursion stack and `StringBuilder`; O(Cₙ · n) for the output.

**Why it is optimal.** The output itself has size O(Cₙ · n), so any correct algorithm must spend at least that much time. Backtracking achieves it with O(n) auxiliary space, which is optimal.

## 6. Dry Run

**Example:** `n = 3`.

This example is useful because it produces exactly 5 well-formed strings (the 3rd Catalan number), and it shows how the conditions prune branches.

**Backtracking trace:**

| Call | open | close | path before | Choice | path after | Action |
|---|---|---|---|---|---|---|
| 1 | 0 | 0 | "" | `(` | "(" | recurse |
| 2 | 1 | 0 | "(" | `(` | "((" | recurse |
| 3 | 2 | 0 | "((" | `(` | "(((" | recurse |
| 4 | 3 | 0 | "(((" | `)` | "((()" | recurse (close < open) |
| 5 | 3 | 1 | "((()" | `)` | "((())" | recurse |
| 6 | 3 | 2 | "((())" | `)` | "((()))" | **length 6 → add "((()))"** |
| 4 | 3 | 0 | "(((" | `)` | — | undo, backtrack |
| 3 | 2 | 0 | "((" | `)` | "(()" | recurse (close < open) |
| 4 | 2 | 1 | "(()" | `(` | "(()(" | recurse |
| 5 | 3 | 1 | "(()(" | `)` | "(()()" | recurse |
| 6 | 3 | 2 | "(()()" | `)` | "(()())" | **add "(()())"** |
| 4 | 2 | 1 | "(()" | `)` | "(())" | recurse |
| 5 | 2 | 2 | "(())" | `(` | "(())(" | recurse |
| 6 | 3 | 2 | "(())(" | `)` | "(())()" | **add "(())()"** |
| 2 | 1 | 0 | "(" | `)` | "()" | recurse |
| 3 | 1 | 1 | "()" | `(` | "()(" | recurse |
| 4 | 2 | 1 | "()(" | `(` | "()((" | recurse |
| 5 | 3 | 1 | "()((" | `)` | "()(()" | recurse |
| 6 | 3 | 2 | "()(()" | `)` | "()(())" | **add "()(())"** |
| 4 | 2 | 1 | "()(" | `)` | "()()" | recurse |
| 5 | 2 | 2 | "()()" | `(` | "()()(" | recurse |
| 6 | 3 | 2 | "()()(" | `)` | "()()()" | **add "()()()"** |

**Final result:** `["((()))","(()())","(())()","()(())","()()()"]` — 5 valid strings, matching `C₃ = 5`.

**Key observations:**
- The condition `close < open` prevents adding `)` when it would unbalance the prefix. For example, after `"("`, `close = 0` and `open = 1`, so `)` is allowed; but after `"()"`, `close = 1` and `open = 1`, so `)` is **not** allowed (would produce `"())"`), and only `(` can be added.
- The condition `open < n` prevents adding more than `n` opening parentheses.

## 7. Complexity Summary

| Approach | Time | Space (auxiliary) | Notes |
|---|---|---|---|
| Brute-force generation | O(4ⁿ · n) | O(n) | Generates all `4ⁿ` strings, filters valid ones |
| Backtracking + counters | O(4ⁿ / √n) | O(n) | Only valid prefixes; optimal |

The output size is O(Cₙ · n) = O(4ⁿ / √n) and is not counted as auxiliary. Backtracking's O(n) auxiliary space is optimal.

## 8. Edge Cases

| Case | Why it matters | How the code handles it |
|---|---|---|
| `n = 1` | Smallest case; one string `"()"` | Backtracking generates it directly |
| `n = 8` | Largest case; 1430 strings | Backtracking handles it efficiently |
| `n = 0` | Excluded by constraints (`n >= 1`) | Would return `[""]` (empty string is trivially balanced) |
| Deep recursion | `2n = 16` for `n = 8` | Depth 16 is trivially safe |
| Output order | Judge accepts any order | Backtracking produces a deterministic but arbitrary order |

## 9. Java Notes

- **`StringBuilder`** is the right tool for building and undoing the current string. `append` and `deleteCharAt(length - 1)` are O(1) amortized; string concatenation would be O(n) per step.
- **`path.toString()`** creates a snapshot of the current string when a complete valid string is found. This is necessary because the `StringBuilder` is mutated afterward.
- **`deleteCharAt(path.length() - 1)`** removes the last character. Use it instead of `setLength(path.length() - 1)` if you want to be explicit, though both work.
- **Passing `open` and `close` as parameters** keeps the method stateless and avoids instance fields.
- **Recursion depth** is exactly `2n ≤ 16`, so there is no stack concern.
- **No need for a separate validity check** — the conditions `open < n` and `close < open` guarantee every generated string is valid.

## 10. Common Mistakes

- **Using only `open < n` and `close < n`.** This allows prefixes like `"())"` where closings exceed openings. The correct condition is `close < open`, not `close < n`.
- **Checking balance only at the end.** This generates all `4ⁿ` strings and filters, which is much slower. The incremental conditions prune invalid prefixes immediately.
- **Forgetting the undo step.** The `StringBuilder` keeps growing and the strings are wrong.
- **Using string concatenation instead of `StringBuilder`.** It creates a new string at every step, which is O(n) per step and wasteful.
- **Adding to the result before the base case.** The string is only complete when `path.length() == 2n`.
- **Assuming the output order matters.** It does not; any order is accepted.

## 11. Interview Takeaway

**Recognition trigger to memorize:** *"Generate all balanced parentheses strings of length 2n → backtracking with `open` and `close` counters; add `(` if `open < n`, add `)` if `close < open`; record when length reaches 2n."*

**90-second explanation:** "This is constraint-based backtracking. I build the string left to right, tracking how many opening and closing parentheses I have used. At each step, I can add `(` if I have used fewer than `n` openings, and I can add `)` only if the number of closings is strictly less than the number of openings — otherwise the prefix would be unbalanced. These two conditions guarantee that every string I build is valid, so when the string reaches length `2n`, I add it to the result. The recursion depth is `2n`, and the number of valid strings is the n-th Catalan number, which grows like `4ⁿ / n^(3/2)`. Time is O(4ⁿ / √n) to build the output, and auxiliary space is O(n) for the recursion stack and StringBuilder."

**Related problems:**
- [Valid Parentheses](https://leetcode.com/problems/valid-parentheses/) — checks whether a single string is balanced; the validity rule is the same.
- [Letter Combinations of a Phone Number](https://leetcode.com/problems/letter-combinations-of-a-phone-number/) — backtracking over choices with no constraints; simpler.
- [Combination Sum](https://leetcode.com/problems/combination-sum/) — backtracking with a sum constraint; similar incremental pruning.
- [N-Queens II](https://leetcode.com/problems/n-queens-ii/) — backtracking with attack constraints; another constraint-satisfaction problem.

## 12. Recall Questions

**Q1. Why is the condition `close < open` correct, while `close < n` is not?**
A: `close < open` ensures that at every prefix, the number of closings never exceeds the number of openings, which is exactly the balance condition. `close < n` only limits the total closings, but allows prefixes like `"())"` where closings exceed openings.

**Q2. Why is no explicit balance check needed at the end?**
A: The two conditions guarantee that every prefix is balanced. When the string reaches length `2n`, it has exactly `n` openings and `n` closings (since `open ≤ n` and `close < open` forces `close = n` when `open = n`), so it is well-formed.

**Q3. What is the total number of well-formed strings for `n` pairs?**
A: The n-th Catalan number `Cₙ = (2n)! / ((n+1)! · n!)`, which grows asymptotically as `4ⁿ / (n^(3/2) · √π)`.

**Q4. What are the time and space complexities?**
A: Time is O(4ⁿ / √n) — proportional to the number of valid strings times their length. Auxiliary space is O(n) for the recursion stack and StringBuilder; the output is O(Cₙ · n).

**Q5. Why is `StringBuilder` preferred over string concatenation?**
A: `StringBuilder.append` and `deleteCharAt` are O(1) amortized, while string concatenation creates a new string of length O(n) at every step. For `n ≤ 8` the difference is small, but `StringBuilder` is the correct habit.

**Q6. What happens if `n = 1`?**
A: The only valid string is `"()"`. The algorithm adds `(` (open=1), then `)` (close=1, close < open), then the base case fires and adds `"()"`.

**Q7. Why does the recursion depth equal `2n`?**
A: Each recursive call appends exactly one character, and the base case fires when the string has `2n` characters. So the depth is exactly `2n`.

**Q8. Could you generate the strings iteratively?**
A: Yes, using a BFS-style queue of partial strings, but backtracking is the standard and clearest approach for this problem.

## 13. Final Code

### Java code (final, optimized backtracking)

```java
import java.util.*;

class Solution {
    public List<String> generateParenthesis(int n) {
        List<String> result = new ArrayList<>();
        backtrack(n, 0, 0, new StringBuilder(), result);
        return result;
    }

    private void backtrack(int n, int open, int close, StringBuilder path, List<String> result) {
        if (path.length() == 2 * n) {
            result.add(path.toString());
            return;
        }

        if (open < n) {
            path.append('(');
            backtrack(n, open + 1, close, path, result);
            path.deleteCharAt(path.length() - 1);
        }

        if (close < open) {
            path.append(')');
            backtrack(n, open, close + 1, path, result);
            path.deleteCharAt(path.length() - 1);
        }
    }
}
```

## 14. Self-Test

Consider `n = 4`. Trace the first five complete strings produced by the backtracking algorithm: for each, show the sequence of `open`/`close` values at each append, and identify at which points the conditions `open < n` and `close < open` prevent a branch from being explored. Then answer: how many well-formed strings exist for `n = 4` (the 4th Catalan number), and why is `"(()))("` never generated? Finally, explain what would happen if the condition were changed from `close < open` to `close <= open` — which invalid string would be generated, and at what point in the recursion would the error first appear?