---
title: "0191 — Number of 1 Bits"
layout: default
parent: Bit Manipulation
grand_parent: Leetcode
nav_order: 191
permalink: /20-Bit-Manipulation/0191-number-of-1-bits/
problem: 191 - Number of 1 Bits
url: https://leetcode.com/problems/number-of-1-bits/
difficulty: Easy
section: Bit Manipulation
patterns: [brian-kernighan, bit-clearing]
data_structures: [bits]
time: O(k)
space: O(1)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0191 — Number of 1 Bits

| Field | Value |
|---|---|
| **Pattern** | Brian Kernighan's Algorithm · Bit Clearing |
| **Difficulty** | Easy |
| **Section** | Bit Manipulation |
| **Time** | O(k) |
| **Space** | O(1) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Microsoft, Meta |
| **LeetCode** | [Number of 1 Bits](https://leetcode.com/problems/number-of-1-bits/) |


## 1. Problem in Simple Words

**Given:** a positive integer `n`.

**Required:** return the number of `1` bits (set bits) in its binary representation, also called the Hamming weight. For example, `11` is `1011` in binary, so the answer is `3`; `128` is `10000000`, so the answer is `1`; `2147483645` is `1111111111111111111111111111101`, so the answer is `30`.

**Constraints that matter:**
- `1 <= n <= 2^31 - 1` — `n` is positive and fits in a signed 32-bit `int`, so its top bit (bit 31) is 0 and the answer is at most 31. (Older versions of this problem fed in a 32-bit **unsigned** value, which Java received as a possibly negative `int`; the code below is correct for any `int` bit pattern, so it works for both.)
- The answer depends only on the bit pattern, not on the numeric magnitude, so the work is bounded by the 32-bit width. Everything is O(1) in input size; the interesting comparison is how many steps each method takes in practice.
- Follow-up: "If this function is called many times, how would you optimize it?" — this points to precomputation (a lookup table) or a constant-step parallel bit-counting method.

## 2. Recognition Signals

- "Count the 1 bits / set bits / Hamming weight" is a classic bit-manipulation primitive.
- If you can remove one set bit per step, the number of steps equals the number of set bits, which is better than scanning all 32 positions when bits are sparse.
- A repeated-call follow-up suggests precomputing counts for small chunks (bytes) or using the mask-and-add parallel technique.
- Pattern: **Brian Kernighan's trick: `n &= n - 1` clears the lowest set bit; count the steps until `n` is 0.**

## 3. Core Idea

Subtracting 1 from `n` flips its lowest set bit to 0 and turns every 0 below that bit into 1, leaving all higher bits unchanged. ANDing `n` with `n - 1` therefore zeroes out exactly the lowest set bit and everything below it (which was already zero), clearing precisely one set bit. Repeat until `n` is 0, counting iterations.

**Why it's correct:** each iteration removes exactly one set bit and changes nothing else that matters (the bits below it were already 0, the bits above it are untouched), so the loop runs once per set bit and ends exactly when none remain. The count of iterations equals the number of set bits.

**Invariant:** after `t` iterations, `n` equals the original value with its `t` lowest set bits cleared, and `count == t`.

## 4. Approach 1 — Brute Force

Check each of the 32 bit positions and count those that are set.

```java
public int hammingWeightBruteForce(int n) {
    int count = 0;
    for (int i = 0; i < 32; i++) {
        count += (n >>> i) & 1;
    }
    return count;
}
```

**Time:** O(32), which is O(1), but it always does 32 iterations regardless of how many bits are set.
**Space:** O(1).

Not optimal in effort: it inspects all 32 positions even when `n` has only one or two set bits (for example `128`). It is correct and easy to reason about, and it is a good reference for testing.

## 5. Approach 2 — Optimized

### 5a. Brian Kernighan's algorithm (the standard answer)

1. `count = 0`.
2. While `n != 0`: do `n &= n - 1` (clears the lowest set bit) and increment `count`.
3. Return `count`.

```java
public int hammingWeight(int n) {
    int count = 0;
    while (n != 0) {
        n &= (n - 1);
        count++;
    }
    return count;
}
```

**Time:** O(k), where `k` is the number of set bits (at most 31 for the given constraints, at most 32 for any `int`).
**Space:** O(1).

This is optimal in the sense that it does work proportional to the answer. It also works for negative `int` values (the unsigned form of the problem): even `Integer.MIN_VALUE - 1` wraps to `Integer.MAX_VALUE`, and `MIN_VALUE & MAX_VALUE` is 0, so the single set bit is cleared correctly.

### 5b. Follow-up: byte lookup table

Precompute the bit count of every byte value once, then split `n` into four bytes and add four lookups.

```java
private static final int[] BITS_IN_BYTE = new int[256];
static {
    for (int i = 1; i < 256; i++) {
        BITS_IN_BYTE[i] = BITS_IN_BYTE[i >>> 1] + (i & 1);
    }
}

public int hammingWeightTable(int n) {
    return BITS_IN_BYTE[n & 0xff]
         + BITS_IN_BYTE[(n >>> 8) & 0xff]
         + BITS_IN_BYTE[(n >>> 16) & 0xff]
         + BITS_IN_BYTE[(n >>> 24) & 0xff];
}
```

**Time:** O(1) per call: four lookups and three additions (after a one-time 256-entry setup).
**Space:** O(1) extra (a fixed 256-entry table).

### 5c. Follow-up: parallel counting (mask and add)

Add adjacent bits in pairs, then pairs into nibbles, then nibbles into bytes, then sum the bytes with one multiplication.

```java
public int hammingWeightParallel(int n) {
    n = n - ((n >>> 1) & 0x55555555);
    n = (n & 0x33333333) + ((n >>> 2) & 0x33333333);
    n = (n + (n >>> 4)) & 0x0f0f0f0f;
    return (n * 0x01010101) >>> 24;
}
```

**Time:** O(1): a fixed handful of operations.
**Space:** O(1).

For a one-off call, 5a is the clearest answer. For many calls, the table (5b) trades 256 entries of memory for four lookups, while the parallel version (5c) needs no memory but is harder to derive. Java's `Integer.bitCount(n)` is the production answer and typically compiles to a single hardware popcount instruction; hand-writing the logic is what an interview expects.

## 6. Dry Run

Example 1: `n = 11` (binary `1011`). The tricky part is seeing how each step removes exactly the lowest set bit.

| Iteration | n before | n - 1 | n & (n - 1) | count after |
|---|---|---|---|---|
| 1 | 1011 (11) | 1010 | 1010 (10) | 1 |
| 2 | 1010 (10) | 1001 | 1000 (8) | 2 |
| 3 | 1000 (8) | 0111 | 0000 (0) | 3 |

Exit condition: `n == 0`. Final answer: `3`. For `n = 128` (`10000000`) the loop runs once: `128 & 127 = 0`, so the answer is `1`.

## 7. Complexity Summary

| Approach | Time | Space | Notes |
|---|---|---|---|
| Check all 32 bits | O(32) | O(1) | Always 32 iterations |
| Brian Kernighan (`n &= n - 1`) | O(k), `k` = set bits | O(1) | Standard answer |
| Byte lookup table | O(1) (4 lookups) | O(1) (256-entry table) | Good for many calls |
| Parallel mask-and-add | O(1) (fixed ops) | O(1) | No table; harder to derive |

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|---|---|---|
| `n = 1` | Smallest valid input | One iteration: `1 & 0 = 0`; answer `1` |
| `n` is a power of two (e.g. `128`) | Single set bit | Loop runs exactly once |
| `n = 2^31 - 1` | All 31 low bits set (the maximum count under the constraints) | 31 iterations, each clearing one bit |
| `n = 2147483645` | Example 3: one zero among many ones | 30 iterations; answer `30` |
| Alternating bits | Many zeros between ones | Each iteration still clears exactly the lowest set bit |
| Negative `int` (older unsigned-style inputs) | Sign bit is set; `n > 0` loops would end early | `while (n != 0)` with `n &= n - 1` works on the raw bit pattern, including `Integer.MIN_VALUE` |
| `n = 0` (outside the constraints) | Nothing to count | Loop never runs; returns `0` |

## 9. Java Notes

- `n &= (n - 1)` works on the two's complement bit pattern, so it is correct for negative `int` values too. Even `Integer.MIN_VALUE - 1` wraps around to `Integer.MAX_VALUE`, and their AND is 0.
- In the brute-force loop use `(n >>> i) & 1`. Using `n >>= 1` in a `while (n != 0)` loop would never terminate for negative `n`, because `>>` copies the sign bit; `>>>` fills with zeros.
- Do not use `n % 2` to test the lowest bit: for negative odd `n` it is `-1`, not `1`. Use `n & 1`.
- Operator precedence: `-` binds tighter than `&` in Java, so `n & n - 1` means `n & (n - 1)`, but parentheses make the intent obvious.
- Hex literals like `0x55555555` are valid `int` literals; `0xffffffff`-style values are negative as signed ints, which is why unsigned shifts (`>>>`) are used throughout the parallel version.
- `Integer.bitCount(n)` is built in and is the idiomatic production solution.

## 10. Common Mistakes

- Looping `while (n > 0)`, which exits immediately for negative inputs (the old unsigned form of the problem) and miscounts. Fix: `while (n != 0)`.
- Using `n >> 1` in a shift-and-test loop; for negative `n` the sign bit is copied in and the loop never reaches zero. Fix: use `>>>`.
- Testing the low bit with `n % 2 == 1`, which fails for negative `n` (the remainder is `-1`). Fix: `(n & 1) == 1`.
- Writing `n = n & n - 1` and misreading precedence, or writing `n &= n` by accident. Fix: parenthesize `n & (n - 1)`.
- Checking only 31 bit positions in the brute force, missing bit 31 for unsigned-style inputs. Fix: loop all 32 positions.
- Counting characters of `Integer.toBinaryString(n)` that are `'1'` and forgetting that this is correct only because the string has no padding; the approach works but allocates a string. Fix: prefer bit operations.

## 11. Interview Takeaway

- **Trigger sentence:** "Counting set bits means `n &= n - 1`, which clears the lowest set bit; the number of iterations until zero is the answer."
- **90-second explanation:** The trick is that `n - 1` flips the lowest set bit of `n` to 0 and all the zeros below it to 1, leaving the higher bits alone. So `n & (n - 1)` clears exactly that lowest set bit and nothing else. I loop `n &= n - 1` until `n` is zero, counting iterations, so the loop runs once per set bit, which is faster than checking all 32 positions when the number is sparse. It's O(k) time for k set bits, O(1) space, and it works on the raw bit pattern, including negative ints, so it handles the unsigned-style version of the problem too. If the function is called many times, I'd precompute the bit count of every byte in a 256-entry table and add four lookups, or use the parallel mask-and-add method with `0x55555555`, `0x33333333`, `0x0f0f0f0f` and a multiplication by `0x01010101`. In production Java I'd just call `Integer.bitCount`.
- **Related problems:** 190 (Reverse Bits), 338 (Counting Bits), 231 (Power of Two), 461 (Hamming Distance), 136 (Single Number).

## 12. Recall Questions

**Q:** What does `n & (n - 1)` do to `n`?
**A:** It clears the lowest set bit of `n`. Subtracting 1 flips that bit to 0 and the zeros below it to 1, so the AND zeroes that whole region while higher bits stay the same.

**Q:** Why does the loop run exactly as many times as there are set bits?
**A:** Each iteration clears exactly one set bit and changes nothing else that matters, so after `k` iterations all `k` set bits are gone and `n` is 0.

**Q:** Why prefer `while (n != 0)` over `while (n > 0)`?
**A:** For the unsigned-style inputs Java receives as negative `int`s, `n > 0` is false immediately, so nothing would be counted. `n != 0` is correct for any bit pattern.

**Q:** Why use `>>>` instead of `>>` when shifting to test bits?
**A:** `>>` copies the sign bit, so a negative number never becomes 0 under repeated `>>=`; `>>>` shifts in zeros.

**Q:** How would you speed up many repeated calls?
**A:** Precompute the set-bit count of every byte (256 entries) and add four lookups, or use the parallel mask-and-add technique; both are constant time, and `Integer.bitCount` does it in hardware.

## 13. Final Code

```java
public class Solution {
    // you need to treat n as an unsigned value
    public int hammingWeight(int n) {
        int count = 0;
        while (n != 0) {
            n &= (n - 1);
            count++;
        }
        return count;
    }
}
```

## 14. Self-Test

Given a positive integer (or any 32-bit pattern), return how many of its bits are 1. Re-derive: while `n != 0`, set `n &= n - 1` (this clears the lowest set bit) and increment a counter; return the counter. For many calls, precompute per-byte counts in a 256-entry table and add four lookups, or use the parallel mask-and-add method.
