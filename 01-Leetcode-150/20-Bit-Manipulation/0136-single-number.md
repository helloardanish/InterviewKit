---
title: "0136 — Single Number"
layout: default
parent: Bit Manipulation
grand_parent: Leetcode
nav_order: 136
permalink: /20-Bit-Manipulation/0136-single-number/
problem: 136 - Single Number
url: https://leetcode.com/problems/single-number/
difficulty: Easy
section: Bit Manipulation
patterns: [xor-cancellation]
data_structures: [array]
time: O(n)
space: O(1)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0136 — Single Number

| Field | Value |
|---|---|
| **Pattern** | XOR Cancellation |
| **Difficulty** | Easy |
| **Section** | Bit Manipulation |
| **Time** | O(n) |
| **Space** | O(1) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Microsoft, Meta |
| **LeetCode** | [Single Number]([https://leetcode.com/problems/single-number/](https://leetcode.com/problems/single-number/)) |

## 1. Problem in Simple Words

**Given:** a non-empty integer array `nums` in which every element appears exactly twice, except for one element that appears once.

**Required:** return that single element, using linear time and only constant extra space.

**Constraints that matter:**
- `1 <= nums.length <= 3 * 10^4` — small enough that even O(n log n) would run quickly, but the statement explicitly demands linear time, so that is the real requirement. Because every value but one appears twice, `nums.length` is always odd.
- `-3 * 10^4 <= nums[i] <= 3 * 10^4` — negatives and zero are allowed. The single element can be `0` or negative, so "0" cannot be used as a "not found" sentinel.
- **"Linear runtime and constant extra space"** — this rules out the obvious hash-based answers (a set or map uses O(n) space) and sorting (O(n log n) time). It hints at a technique where equal values cancel each other out in place.
- Exactly one element appears once and all the others appear exactly twice; the algorithm relies on this guarantee.

(The page I fetched directly was blocked; the statement and constraints above come from search results of the problem page.)

## 2. Recognition Signals

- "Every element appears twice except one": pairs plus one leftover. The leftover is what remains after pairs cancel.
- "Constant extra space" together with "linear time" and a problem about pairing or duplicates: think bit manipulation, specifically XOR.
- XOR has the property that `a ^ a = 0`, so identical values erase each other, regardless of where they sit in the array.
- Pattern: **XOR all the elements; the pairs cancel and the single number remains.**

## 3. Core Idea

XOR every element together. XOR is commutative and associative, so the order of the elements does not matter. Every value that appears twice contributes `x ^ x = 0`, and `0 ^ y = y`, so what is left is exactly the single value.

**Why it's correct:** group the XOR expression by value. Each paired value `x` appears in two places, and because XOR can be reordered freely, those two occurrences combine to `x ^ x = 0`. All the zeros vanish (`0 ^ y = y`), leaving only the unpaired element.

**Invariant:** after processing a prefix of the array, `result` equals the XOR of the values that have appeared an odd number of times in that prefix; at the end, only the single element has an odd count.

## 4. Approach 1 — Brute Force

Keep a hash set of values seen an odd number of times: add a value if it is absent, remove it if it is present. After one pass, the set holds only the single element.

```java
public int singleNumberHashSet(int[] nums) {
    Set<Integer> unpaired = new HashSet<>();
    for (int num : nums) {
        if (!unpaired.add(num)) {
            unpaired.remove(num);
        }
    }
    return unpaired.iterator().next();
}
```

**Time:** O(n).
**Space:** O(n) in the worst case (up to about n/2 distinct values can be waiting for their partner).

Not optimal: it is linear in time but violates the constant-extra-space requirement. Sorting the array and scanning pairs uses O(1) extra space (aside from the sort's own stack) but costs O(n log n) time, which violates the linear-time requirement.

## 5. Approach 2 — Optimized (XOR)

1. Set `result = 0`.
2. For each `num` in the array, do `result ^= num`.
3. Return `result`.

```java
public int singleNumber(int[] nums) {
    int result = 0;
    for (int num : nums) {
        result ^= num;
    }
    return result;
}
```

**Time:** O(n), one pass.
**Space:** O(1), one integer.

This is optimal: every element must be read at least once, so O(n) time cannot be beaten, and only a single accumulator is stored. XOR works directly on the two's complement bit patterns, so negative values need no special handling.

## 6. Dry Run

Example 2: `nums = [4,1,2,1,2]`. The tricky part is that intermediate values look meaningless until the second `1` and `2` cancel their partners.

| Step | num | Binary of num | result before | result after (binary) | result after (decimal) |
|---|---|---|---|---|---|
| 1 | 4 | 100 | 000 | 100 | 4 |
| 2 | 1 | 001 | 100 | 101 | 5 |
| 3 | 2 | 010 | 101 | 111 | 7 |
| 4 | 1 | 001 | 111 | 110 | 6 |
| 5 | 2 | 010 | 110 | 100 | 4 |

Exit condition: all elements processed. Final answer: `4`. The second `1` and second `2` removed their earlier copies from the accumulator, leaving only `4`.

## 7. Complexity Summary

| Approach | Time | Space | Notes |
|---|---|---|---|
| Sort, then scan pairs | O(n log n) | O(1) aux (sort stack aside) | Violates linear time |
| Hash set toggle | O(n) | O(n) | Violates constant space |
| XOR all elements | O(n) | O(1) | Meets both requirements |

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|---|---|---|
| Single element (`[1]`) | No pairs at all | `0 ^ 1 = 1` |
| The single element is `0` (`[0,1,1]`) | A `0` answer could be mistaken for "nothing found" | The algorithm never uses `0` as a sentinel; the XOR of `0, 1, 1` is `0`, which is the correct answer |
| Negative values (`[-1,-1,-7]`) | Sign handling | XOR operates on the bit pattern; `(-1) ^ (-1) = 0`, then `0 ^ (-7) = -7` |
| Single element at the start, middle, or end | Position should not matter | XOR is commutative and associative, so order is irrelevant |
| Maximum length (`3 * 10^4`, always odd) | Performance | One pass of at most 30,000 XOR operations |
| Values at `±3 * 10^4` | Overflow worries | XOR cannot overflow; the result is always an `int` within the same bit width |

## 9. Java Notes

- Initialize `result` to `0`, the identity for XOR (`0 ^ x = x`). Starting from `nums[0]` and looping from index 1 is equivalent.
- `^=` on `int` works on the 32-bit two's complement pattern, so negative numbers behave correctly, and there is no overflow with XOR.
- In the hash-set version, `Set.add` returns `false` when the element is already present, which makes the add-or-remove toggle a one-liner. The values here lie outside the small `Integer` cache range, so never compare boxed values with `==`; the set uses `equals` internally and is safe.
- `unpaired.iterator().next()` reads the one remaining element; this is only valid because the problem guarantees exactly one element appears once.
- The alternative formula `2 * sum(distinct values) - sum(all values)` also works mathematically, but it needs a set (O(n) space), so it does not meet the constant-space requirement.

## 10. Common Mistakes

- Using a set or map and ignoring the O(1)-space requirement. Fix: use XOR.
- Initializing the accumulator to a non-zero value, which XORs a stray constant into the answer. Fix: start from `0` (or from `nums[0]`).
- Using `+` or `-` instead of `^`, which does not cancel pairs (for example `1 + 1 = 2`). Fix: use bitwise XOR.
- Confusing `^` with `&` or `|`, which do not have the cancellation property. Fix: only XOR satisfies `x ^ x = 0`.
- Using `0` as a "not found" marker or exiting early when the accumulator becomes `0`. A running XOR can pass through `0` at any point (for example after two paired values), and the single element itself can be `0`. Fix: always finish the whole array.
- Applying the same trick to the "every element appears three times except one" variant, where XOR no longer cancels. Fix: that variant needs per-bit counting modulo 3.

## 11. Interview Takeaway

- **Trigger sentence:** "Every element appears twice except one, with constant space: XOR everything, because identical values cancel to zero and order doesn't matter."
- **90-second explanation:** I need the one value that appears once among values that appear exactly twice, in linear time and constant space, so a set or map is out. XOR is the right tool: `x ^ x = 0`, `0 ^ y = y`, and XOR is commutative and associative, so I can regroup the whole expression by value. Every paired value collapses to zero no matter where its two copies sit, and all the zeros disappear, leaving the single element. So I start an accumulator at 0, XOR every element into it, and return it. It's one pass, O(n) time and O(1) space, and negative numbers work because XOR operates on the raw bit patterns. The one thing I avoid is treating 0 as a sentinel, since the single value itself could be 0. A set-based toggle also works but uses O(n) space, and sorting would cost O(n log n).
- **Related problems:** 137 (Single Number II), 260 (Single Number III), 268 (Missing Number), 287 (Find the Duplicate Number), 191 (Number of 1 Bits).

## 12. Recall Questions

**Q:** Why do paired values disappear when XORing the whole array?
**A:** XOR is commutative and associative, so the two copies of any value can be brought together, and `x ^ x = 0`. The zeros then drop out because `0 ^ y = y`.

**Q:** Why does the position of the single element not matter?
**A:** Because XOR is commutative and associative, the order of the elements can be rearranged without changing the result.

**Q:** Why can the answer be `0` without causing a problem?
**A:** The algorithm never uses `0` as a flag; the final accumulator value is simply returned, and if the single element is `0`, the XOR of everything is `0`, which is correct.

**Q:** Why doesn't XOR work for the variant where elements appear three times?
**A:** Three copies of `x` give `x ^ x ^ x = x`, not `0`, so triples do not cancel; that variant needs per-bit counts modulo 3.

**Q:** What requirement does the hash-set solution violate, and why?
**A:** It uses O(n) extra space in the worst case, but the problem requires constant extra space.

## 13. Final Code

```java
class Solution {
    public int singleNumber(int[] nums) {
        int result = 0;
        for (int num : nums) {
            result ^= num;
        }
        return result;
    }
}
```

## 14. Self-Test

Given a non-empty integer array where every element appears twice except one, return the element that appears once, in linear time and constant extra space. Re-derive: XOR all elements into an accumulator starting at 0; because `x ^ x = 0`, `0 ^ y = y`, and XOR is commutative and associative, the pairs cancel and only the single element remains.
