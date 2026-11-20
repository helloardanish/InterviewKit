---
title: "0129 — Sum Root to Leaf Numbers"
layout: default
parent: Binary Tree General
grand_parent: Leetcode
nav_order: 129
permalink: /09-Binary-Tree-General/0129-sum-root-to-leaf-numbers/

problem: 129 - Sum Root to Leaf Numbers
url: https://leetcode.com/problems/sum-root-to-leaf-numbers/
difficulty: Medium
section: Tree / DFS
patterns: [tree-recursion, running-value-accumulation]
data_structures: [tree]
time: O(n)
space: O(h)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0129 — Sum Root to Leaf Numbers

| Field | Value |
|---|---|
| **Pattern** | Tree Recursion with Running-Value Accumulation |
| **Difficulty** | Medium |
| **Section** | Tree / DFS |
| **Time** | O(n) |
| **Space** | O(h) — h = tree height |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Meta |
| **LeetCode** | [Sum Root to Leaf Numbers](https://leetcode.com/problems/sum-root-to-leaf-numbers/) |

## 1. Problem in Simple Words

**Given:** the root of a binary tree where every node holds a single digit `0`–`9`.

**Required:** treat each root-to-leaf path as forming a multi-digit number (most significant digit first, i.e., root digit first), and return the sum of all such numbers across every root-to-leaf path in the tree.

**Constraints that matter:**
- `1 <= number of nodes <= 1000` — the tree always has at least one node (no empty-tree case to special-case here, unlike some related problems).
- **"The depth of the tree will not exceed 10"** — this is a strong, deliberate constraint: it caps any individual root-to-leaf number at 10 digits, meaning even the largest possible single path-number stays comfortably within `int` range, and the problem explicitly states the final summed answer is guaranteed to fit in a 32-bit integer too — removing any need for `long` arithmetic or overflow handling anywhere in the solution.
- **"A leaf node is a node with no children"** — the same precise leaf definition seen in related path-sum problems: a path's number is only "complete" and contributes to the total once it reaches a node with neither a left nor right child.

## 2. Recognition Signals

- "Each root-to-leaf path represents a number, sum them all" → the defining signal for **tree DFS with an accumulating running value**, very similar in spirit to Path Sum (112), but here the accumulation is multiplicative-positional (building a number digit by digit) rather than purely additive.
- "Root-to-leaf path represents a number, most significant digit first" → the natural way to build this number incrementally while descending is `currentNumber = currentNumber * 10 + node.val` at each step — exactly how you'd manually construct a multi-digit number one digit at a time, left to right.
- Needing to **sum together a value from every distinct leaf** across potentially many branches → the recursive case must **combine results from both children** (summing their respective leaf-path totals) rather than just picking one side, which is a slightly different recursive combination pattern than Path Sum's `||` (any-match) logic — here it's genuinely additive across all paths, not just a yes/no existence check.
- Named pattern: **Tree DFS with Running Digit-Accumulation, Summed Across All Leaves**.

## 3. Core Idea

Recursively descend the tree, building up the "number so far" by taking the previous accumulated value, multiplying by 10, and adding the current node's digit — exactly mirroring how reading digits left to right constructs a number. When a leaf is reached, that fully-built number is the complete value for this particular root-to-leaf path, and should be returned directly. For a non-leaf node, the total contributed is the **sum** of whatever its left subtree's leaves contribute plus whatever its right subtree's leaves contribute (each computed with the already-updated "number so far" carried into them).

**Why it's correct:** building a number by repeatedly doing `value = value * 10 + nextDigit` is exactly how positional base-10 numbers are constructed from their digits in order (e.g., starting from `0`, then `1` → `0*10+1=1`, then `2` → `1*10+2=12`, then `3` → `12*10+3=123`) — so carrying this running value down through the recursion, updating it at each node, naturally produces the correct complete number by the time any given leaf is reached. Since every leaf contributes exactly one distinct root-to-leaf number, and the problem asks for the sum across *all* such paths, each recursive call must return the sum of everything reachable through it — which for a non-leaf node means summing (not choosing between) its two children's contributions.

**Invariant:** at any point during the recursive descent, the value being passed into a given recursive call correctly represents the multi-digit number formed by all ancestor digits visited so far (from the root down to, but not including, the current node) — so by the time a leaf is reached, applying one more digit-accumulation step with the leaf's own value yields exactly the complete number that root-to-leaf path represents.

## 4. Approach 1 — Brute Force (Collect Each Path's Digits, Parse as a Number, Sum)

1. Recursively explore the tree, maintaining a `List<Integer>` of digits seen so far along the current path.
2. At each leaf, convert the accumulated digit list into its represented integer value (e.g., by building a string of the digits and parsing it, or manually iterating and combining).
3. Sum these leaf values across all recursive branches.

```java
public int sumNumbersBruteForce(TreeNode root) {
    return explore(root, new ArrayList<>());
}

private int explore(TreeNode node, List<Integer> digitsSoFar) {
    digitsSoFar.add(node.val);

    int result;
    if (node.left == null && node.right == null) {
        StringBuilder sb = new StringBuilder();
        for (int d : digitsSoFar) {
            sb.append(d);
        }
        result = Integer.parseInt(sb.toString());
    } else {
        int leftSum = (node.left != null) ? explore(node.left, digitsSoFar) : 0;
        int rightSum = (node.right != null) ? explore(node.right, digitsSoFar) : 0;
        result = leftSum + rightSum;
    }

    digitsSoFar.remove(digitsSoFar.size() - 1);
    return result;
}
```

**Time:** O(n · h) in the worst case — each leaf triggers an O(path length) string-building and parsing operation, and with up to n leaves (each at depth up to `h <= 10`), this totals to O(n · h), though given the constraint `h <= 10`, this is effectively still quite fast in practice; it's the *technique* (repeated string construction and parsing) that's inefficient relative to the cleaner alternative, not necessarily the raw performance at this problem's small scale.
**Space:** O(h) for the `digitsSoFar` list and the recursion stack, plus the overhead of repeated `StringBuilder`/`Integer.parseInt` allocations at every leaf.

This is not optimal: converting digits to a string and parsing them back into an integer at every single leaf is unnecessary indirection — the same numeric value can be built up directly as an `int` through simple arithmetic during the descent itself, avoiding any string manipulation entirely.

## 5. Approach 2 — Optimized (Running Integer Accumulation via Multiply-and-Add)

1. Define a recursive helper `sum(node, currentNumber)`:
   - Compute `newNumber = currentNumber * 10 + node.val` (extend the running number by this node's digit).
   - If `node` is a leaf (`node.left == null && node.right == null`), return `newNumber` directly — this path is complete.
   - Otherwise, recursively sum the contributions from the left and right children (passing `newNumber` into each, or `0` for the sum if a child is absent), and return their total.
2. Call `sum(root, 0)` to start (an initial "number so far" of `0`, which becomes just `root.val` after the first multiply-and-add step).

```java
public int sumNumbers(TreeNode root) {
    return sum(root, 0);
}

private int sum(TreeNode node, int currentNumber) {
    int newNumber = currentNumber * 10 + node.val;

    if (node.left == null && node.right == null) {
        return newNumber;
    }

    int total = 0;
    if (node.left != null) {
        total += sum(node.left, newNumber);
    }
    if (node.right != null) {
        total += sum(node.right, newNumber);
    }

    return total;
}
```

**Time:** O(n) — every node is visited exactly once, with O(1) work (one multiplication, one addition, a leaf check) performed at each.
**Space:** O(h) for the recursion call stack — given the constraint `h <= 10`, this is trivially small and never a practical concern for this specific problem, though the technique generalizes correctly to any depth.

This is optimal: it builds each path's number directly via simple integer arithmetic as the recursion descends, with no string conversion, no auxiliary list, and no repeated work at the leaves — every node contributes exactly one multiply-and-add operation to the final result.

## 6. Dry Run

`root = [4,9,0,5,1]` (tree: `4` has children `9,0`; `9` has children `5,1`; `0` has no children)

| call | node | currentNumber (in) | newNumber | leaf? | returns |
|---|---|---|---|---|---|
| sum(4, 0) | 4 | 0 | 0*10+4=4 | no | sum(9,4) + sum(0,4) |
| sum(9, 4) | 9 | 4 | 4*10+9=49 | no | sum(5,49) + sum(1,49) |
| sum(5, 49) | 5 | 49 | 49*10+5=495 | yes | 495 |
| sum(1, 49) | 1 | 49 | 49*10+1=491 | yes | 491 |
| sum(0, 4) | 0 | 4 | 4*10+0=40 | yes | 40 |

Combining: `sum(9,4) = 495 + 491 = 986`; `sum(4,0) = 986 + 40 = 1026`. Final answer: `1026`, matching the expected output (`495 + 491 + 40 = 1026`).

## 7. Complexity Summary

| Approach | Time | Space | Notes |
|---|---|---|---|
| Collect Digits, Parse String at Each Leaf | O(n·h) | O(h) list/stack + string allocation overhead | Unnecessary string conversion at every leaf |
| Running Integer Accumulation | O(n) | O(h) recursion only | Direct arithmetic, no string conversion anywhere |

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|---|---|---|
| Single-node tree (`root = [5]`) | The root is itself the only (trivial, one-digit) path | `sum(5, 0)`: `newNumber = 0*10+5 = 5`; the root is also a leaf (no children), so it returns `5` directly |
| A node with only one child (not two, not zero) | Must correctly sum only the existing child's contribution, not attempt to call into a null child | The `if (node.left != null)` / `if (node.right != null)` checks independently guard each recursive call, so only existing children contribute to `total` |
| Node value `0` appearing as a non-root digit (e.g., the "0" in `[4,9,0,5,1]`, representing the path `4->0` as the number `40`, not `4` or something that drops the leading structure) | Must correctly preserve positional value, not accidentally treat a `0` digit as insignificant | `newNumber = currentNumber * 10 + node.val` correctly incorporates a `0` digit into the proper decimal position, exactly as manual digit-by-digit number construction would (40, not 4) |
| Maximum depth tree (depth exactly 10, the problem's stated upper bound) | The largest single path-number could have up to 10 digits | Even a 10-digit number (max value around 9,999,999,999... but constrained further since each digit is 0-9 and depth caps it) stays within safe `int` range per the problem's explicit guarantee that the answer fits in 32 bits |
| Two different paths producing the same leaf-number by coincidence (not actually possible given distinct tree positions, but worth considering conceptually) | Must still sum all contributions, not deduplicate | The algorithm sums every leaf's contribution unconditionally; there's no deduplication logic, nor should there be, since each leaf represents a genuinely distinct root-to-leaf path regardless of whether its numeric value happens to coincide with another path's value |
| A perfectly linear (skewed) tree, e.g., every node has only a right child | Single long path, single leaf, single number | The recursion simply follows the one existing child at each step, building up the full multi-digit number along that single path, and returns it directly once the lone leaf is reached |

## 9. Java Notes

- `currentNumber * 10 + node.val` is the standard idiom for incrementally building a base-10 integer from a sequence of digits, in most-significant-digit-first order — functionally the inverse operation of the common `n % 10` / `n /= 10` idiom used to *extract* digits from an already-complete number (seen, for example, in the Happy Number problem).
- No `long` is needed anywhere, thanks to the problem's explicit guarantees (`depth <= 10`, and the final answer guaranteed to fit in a 32-bit integer) — this is a case where reading the constraints carefully upfront avoids unnecessary defensive type choices.
- `total += sum(node.left, newNumber);` and the equivalent for the right child are independent, separate accumulations into `total` — this is additive combination (summing both children's contributions), distinct from the `||` (either-or) combination pattern seen in boolean-result tree problems like Path Sum, since here every valid leaf's value genuinely needs to contribute to the final total, not just serve as a yes/no signal.
- Avoiding string conversion entirely (no `StringBuilder`, no `Integer.parseInt`) is a meaningful simplification specific to this problem's structure — since the "number" being built is always base-10 with single-digit node values, direct arithmetic is both simpler and more efficient than any string-based intermediate representation.

## 10. Common Mistakes

- Using addition instead of the multiply-by-10-then-add idiom (e.g., `currentNumber + node.val` instead of `currentNumber * 10 + node.val`) — this would incorrectly treat the path `4 -> 9` as representing `4 + 9 = 13` instead of the correct positional number `49`. Fix: always multiply the running value by 10 before adding the next digit, to correctly shift all previously accumulated digits one position to the left.
- Combining the left and right subtree results with `||` or `Math.max` instead of plain addition (`+`) — this problem requires the **sum** of all root-to-leaf numbers, not a boolean existence check (as in Path Sum) or the single largest path value; every leaf's number must contribute additively to the final total. Fix: always sum the two children's returned totals together.
- Forgetting to guard the recursive calls with `if (node.left != null)` / `if (node.right != null)` and instead unconditionally calling `sum(node.left, ...)` even when `node.left` is `null` — this would throw a `NullPointerException` attempting to access `node.val` on a null reference inside the recursive call. Fix: always check for a child's existence before recursing into it.
- Checking only `node.left == null` (or only `node.right == null`) as the leaf condition, instead of requiring **both** to be null — this would incorrectly treat a node with just one missing child as if it were already a complete leaf, truncating its path's number prematurely and ignoring the subtree that actually does exist on the other side. Fix: a true leaf requires both `node.left == null` and `node.right == null` simultaneously.

## 11. Interview Takeaway

- **Trigger sentence:** "Sum all root-to-leaf numbers formed by single-digit nodes → DFS building the running number via `currentNumber * 10 + node.val`, returning the complete number at each leaf and summing children's contributions elsewhere."
- **90-second explanation:** "Each root-to-leaf path spells out a number one digit at a time, most significant digit first, so I can build that number incrementally as I recurse down the tree: at each node, I take whatever number I've built so far, multiply it by ten to shift it over, and add the current node's digit — exactly how you'd construct a multi-digit number by hand, left to right. Once I reach a true leaf — a node with no children at all — that running value is the complete number for this specific path, so I just return it directly. For any node that isn't a leaf, I need the total contribution from everything beneath it, which means summing whatever its left subtree's leaves add up to and whatever its right subtree's leaves add up to — this is a genuine sum across all paths, not just picking whichever side 'wins,' since the problem wants the total across every distinct root-to-leaf number in the tree."
- **Related problems:** 112 (Path Sum, a related but boolean-existence-check variant with additive rather than positional accumulation), 113 (Path Sum II, collecting the actual paths), 257 (Binary Tree Paths, listing all root-to-leaf paths as strings).

## 12. Recall Questions

**Q:** Why is `currentNumber * 10 + node.val` the correct way to extend a running number with a new digit, rather than simple addition?
**A:** Multiplying by 10 first shifts every digit already accumulated one decimal place to the left, making room for the new digit to occupy the ones place — this exactly mirrors how reading a multi-digit number left to right works (e.g., building "49" requires the "4" to become "40" before adding "9", not simply "4 + 9").

**Q:** Why must a non-leaf node's recursive result be the **sum** of its children's contributions rather than, say, the larger of the two or a boolean combination?
**A:** The problem asks for the total sum of all root-to-leaf numbers across the entire tree, and each leaf represents one genuinely distinct path contributing its own number to that total, so a non-leaf node's job is to correctly aggregate (via addition) everything reachable beneath it, not to select or test among its children's results.

**Q:** Why doesn't this problem require `long` arithmetic anywhere, given that numbers could in principle have many digits?
**A:** The problem explicitly caps the tree's depth at 10, which bounds any single root-to-leaf number to at most 10 digits, and further explicitly guarantees that the final summed answer fits within a 32-bit integer — both guarantees mean plain `int` arithmetic is always safe here.

**Q:** Why is checking both `node.left == null` and `node.right == null` together necessary for correctly identifying a leaf, rather than checking just one?
**A:** A node with only one missing child still has an unexplored subtree through its other child, and treating it as a leaf prematurely would cut that path short, incorrectly reporting an incomplete number instead of continuing the traversal into the subtree that genuinely exists.

**Q:** How does the combination logic in this problem fundamentally differ from the combination logic in Path Sum (112), even though both are tree-DFS problems with a running accumulated value?
**A:** Path Sum only needs to know whether *any* valid path exists, so it combines its children's boolean results with `||` (succeeding if either side succeeds); this problem needs the sum of *every* leaf's numeric contribution across the whole tree, so it combines children's results with `+` (adding both sides together, since every valid leaf matters, not just the existence of one).

## 13. Final Code

```java
/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode() {}
 *     TreeNode(int val) { this.val = val; }
 *     TreeNode(int val, TreeNode left, TreeNode right) {
 *         this.val = val;
 *         this.left = left;
 *         this.right = right;
 *     }
 * }
 */
class Solution {
    public int sumNumbers(TreeNode root) {
        return sum(root, 0);
    }

    private int sum(TreeNode node, int currentNumber) {
        int newNumber = currentNumber * 10 + node.val;

        if (node.left == null && node.right == null) {
            return newNumber;
        }

        int total = 0;
        if (node.left != null) {
            total += sum(node.left, newNumber);
        }
        if (node.right != null) {
            total += sum(node.right, newNumber);
        }

        return total;
    }
}
```

## 14. Self-Test

Given the root of a binary tree with single-digit node values (depth at most 10), where each root-to-leaf path represents a multi-digit number, return the sum of all such numbers. Re-derive: recursively build the running number as `currentNumber * 10 + node.val` while descending; at a true leaf (no children), return that complete number; for any other node, sum the contributions from whichever children exist, passing the updated running number into each.
