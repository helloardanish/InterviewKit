---
title: "0190 — Reverse Bits"
layout: default
parent: Bit Manipulation
grand_parent: Leetcode
nav_order: 190
permalink: /20-Bit-Manipulation/0190-reverse-bits/
problem: 190 - Reverse Bits
url: https://leetcode.com/problems/reverse-bits/
difficulty: Easy
section: Bit Manipulation
patterns: [bit-by-bit-processing, mask-and-swap]
data_structures: [bits]
time: O(1)
space: O(1)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0190 — Reverse Bits

| Field | Value |
|---|---|
| **Pattern** | Bit-by-Bit Processing · Mask and Swap |
| **Difficulty** | Easy |
| **Section** | Bit Manipulation |
| **Time** | O(1) |
| **Space** | O(1) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Microsoft, Meta |
| **LeetCode** | [Reverse Bits]([https://leetcode.com/problems/reverse-bits/](https://leetcode.com/problems/reverse-bits/)) |

## 1. Problem in Simple Words

**Given:** a 32-bit signed integer `n`.

**Required:** reverse its 32 bits (bit 0 becomes bit 31, bit 1 becomes bit 30, and so on) and return the resulting integer. For example, `43261596` is `00000010100101000001111010011100` in binary, and reversing the 32 bits gives `00111001011110000010100101000000`, which is `964176192`.

**Constraints that matter:**
- `0 <= n <= 2^31 - 2` and `n` is even — so `n` is non-negative (bit 31 is 0) and its lowest bit is 0. That means the reversed result has bit 31 equal to 0 (the old bit 0) and therefore is also non-negative and fits in a signed `int`. In general (without this constraint), the reversed pattern can have bit 31 set, which Java would print as a negative `int`; the bit pattern is what matters.
- The width is **fixed at 32 bits**, regardless of how many leading zeros `n` has. Leading zeros of `n` become trailing zeros of the result, and trailing zeros of `n` become leading zeros of the result. Any loop that stops early when `n` runs out of set bits loses those positions.
- Follow-up: "If this function is called many times, how would you optimize it?" — this points to precomputation (a byte lookup table) or a constant-step bit-swapping technique.

## 2. Recognition Signals

- "Reverse the bits of a fixed-width integer": a bit-manipulation problem where the width, not the value, controls the work.
- Reading bits from least significant to most significant while writing them into a result from most significant to least significant is exactly bit reversal.
- A repeated-call follow-up hints at replacing the per-bit loop with a table or a fixed sequence of mask-and-shift steps.
- Pattern: **peel the lowest bit of `n` with `n & 1`, push it onto `result` with a left shift, for exactly 32 iterations.**

## 3. Core Idea

Run exactly 32 iterations. In each one, shift `result` left by one to make room, OR in the lowest bit of `n` (`n & 1`), then shift `n` right by one using the unsigned shift `>>>`. The first bit read (bit 0 of `n`) ends up shifted the most, so it lands at bit 31; the last bit read (bit 31 of `n`) is not shifted afterward, so it lands at bit 0.

**Why it's correct:** after `t` iterations, `result` holds the `t` lowest bits of `n`, in reverse order, in its lowest `t` bit positions. Each new iteration shifts those up by one and places the next bit of `n` at position 0. After 32 iterations, all 32 bits of `n` have been consumed in order from bit 0 to bit 31 and have been laid out from bit 31 down to bit 0.

**Invariant:** after `t` iterations, `result` equals the reversal of the lowest `t` bits of the original `n`, and `n` has been shifted right by `t` positions.

## 4. Approach 1 — Brute Force

Convert `n` to a 32-character binary string (padding with leading zeros), reverse the string, and parse it back.

```java
public int reverseBitsString(int n) {
    String bits = String.format("%32s", Integer.toBinaryString(n)).replace(' ', '0');
    String reversed = new StringBuilder(bits).reverse().toString();
    return Integer.parseUnsignedInt(reversed, 2);
}
```

**Time:** O(32), which is O(1), but with string allocation and parsing overhead.
**Space:** O(32) for the strings.

Not optimal: it works, but it allocates strings and relies on three easy-to-miss details: `Integer.toBinaryString` omits leading zeros (hence the padding), and `Integer.parseInt` would throw for a 32-bit string whose top bit is 1 (hence `parseUnsignedInt`). Direct bit operations avoid all of this.

## 5. Approach 2 — Optimized

### 5a. Bit-by-bit loop (the standard answer)

1. `result = 0`.
2. Repeat 32 times: `result = (result << 1) | (n & 1)`; then `n >>>= 1`.
3. Return `result`.

```java
public int reverseBits(int n) {
    int result = 0;
    for (int i = 0; i < 32; i++) {
        result = (result << 1) | (n & 1);
        n >>>= 1;
    }
    return result;
}
```

**Time:** O(1): exactly 32 iterations of constant work.
**Space:** O(1).

### 5b. Divide and conquer with masks (constant steps, branch-free)

Swap the two 16-bit halves, then swap adjacent bytes within each half, then adjacent nibbles, then adjacent bit pairs, then adjacent bits. Five steps reverse all 32 bits.

```java
public int reverseBitsMasks(int n) {
    n = (n >>> 16) | (n << 16);
    n = ((n & 0xff00ff00) >>> 8) | ((n & 0x00ff00ff) << 8);
    n = ((n & 0xf0f0f0f0) >>> 4) | ((n & 0x0f0f0f0f) << 4);
    n = ((n & 0xcccccccc) >>> 2) | ((n & 0x33333333) << 2);
    n = ((n & 0xaaaaaaaa) >>> 1) | ((n & 0x55555555) << 1);
    return n;
}
```

**Time:** O(1): five constant steps.
**Space:** O(1).

### 5c. Byte lookup table (the follow-up for many calls)

Precompute the reversal of every possible byte once, then reverse an integer with four table lookups.

```java
private static final int[] REVERSED_BYTE = new int[256];
static {
    for (int b = 0; b < 256; b++) {
        REVERSED_BYTE[b] = (REVERSED_BYTE[b >>> 1] >>> 1) | ((b & 1) << 7);
    }
}

public int reverseBitsTable(int n) {
    return (REVERSED_BYTE[n & 0xff] << 24)
         | (REVERSED_BYTE[(n >>> 8) & 0xff] << 16)
         | (REVERSED_BYTE[(n >>> 16) & 0xff] << 8)
         |  REVERSED_BYTE[(n >>> 24) & 0xff];
}
```

**Time:** O(1) per call: four lookups and a few shifts (after a one-time 256-entry setup).
**Space:** O(1) extra (a fixed 256-entry table).

The loop in 5a is the clearest answer and is optimal in complexity terms (constant work for a fixed 32-bit width). For the "called many times" follow-up, the lookup table reduces each call to four lookups and the mask-swap version removes loops entirely; the table trades a little memory for speed, while the mask version needs no memory but is harder to derive from scratch. Java also provides `Integer.reverse(n)`, which implements the mask-swap idea; hand-writing the logic is what an interview expects.

## 6. Dry Run

The real input has 32 bits, so the table below traces the **same loop on an 8-bit analogue** (`n = 00010110`, 8 iterations) to keep it readable; the real code does the identical steps for 32 iterations. The tricky part is iterations 6 to 8: `n` is already `0`, yet the shifts still must happen.

| Iteration | lowest bit of n | result after | n after |
|---|---|---|---|
| start | | 00000000 | 00010110 |
| 1 | 0 | 00000000 | 00001011 |
| 2 | 1 | 00000001 | 00000101 |
| 3 | 1 | 00000011 | 00000010 |
| 4 | 0 | 00000110 | 00000001 |
| 5 | 1 | 00001101 | 00000000 |
| 6 | 0 | 00011010 | 00000000 |
| 7 | 0 | 00110100 | 00000000 |
| 8 | 0 | 01101000 | 00000000 |

Exit condition: the fixed iteration count is reached (not "when `n` is 0"). Final result `01101000` is `00010110` read backwards.

For the real Example 2, `n = 2147483644` is `01111111111111111111111111111100`: bits 2 through 30 are ones (29 of them) and bits 0, 1, and 31 are zeros. Reversing moves the ones to bits 1 through 29, giving `00111111111111111111111111111110`, which is `1073741822`, matching the expected output.

## 7. Complexity Summary

| Approach | Time | Space | Notes |
|---|---|---|---|
| String reverse | O(1) (32 chars) | O(1) (32 chars) | Allocation and parsing overhead; `parseUnsignedInt` needed |
| Bit-by-bit loop | O(1) (32 iterations) | O(1) | Standard answer |
| Mask-and-swap | O(1) (5 steps) | O(1) | No loop; harder to derive |
| Byte lookup table | O(1) (4 lookups) | O(1) (256-entry table) | Best for repeated calls |

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|---|---|---|
| `n = 0` | No set bits | All 32 iterations push zeros; returns `0` |
| `n = 2` (binary `...10`) | Low bits are zero, so an early-exit loop gives the wrong result | The loop always runs 32 times, so the single set bit ends at bit 30: `1073741824` |
| `n = 2147483646` (`2^31 - 2`, the maximum) | A bit palindrome: ones at bits 1 to 30, zeros at bits 0 and 31 | Reversal maps the same pattern onto itself; returns `2147483646` |
| `n` has many leading zeros (small `n`) | Leading zeros become trailing zeros of the result | Fixed 32-iteration count keeps them |
| `n` has trailing zeros | Trailing zeros become leading zeros of the result | The shifts of `result` happen even after `n` becomes 0 |
| Unsigned results with bit 31 set (outside these constraints) | Java prints a negative `int` | The bit pattern is still correct; use `>>>` and avoid `parseInt` on a 32-bit string |

## 9. Java Notes

- Use `>>>` (unsigned right shift), not `>>`. With the stated constraints `n >= 0`, both behave the same, but `>>` would sign-extend a negative `n` and break the loop in general.
- `result << 1` discards the top bit silently on overflow; that is expected for a fixed 32-bit reversal, and Java `int` arithmetic wraps without exceptions.
- `n & 1` extracts the lowest bit as `0` or `1`; OR-ing it in after shifting `result` left avoids any masking of `result`.
- `Integer.parseInt` on a 32-character binary string whose top bit is 1 throws `NumberFormatException` (the value is out of signed range); `Integer.parseUnsignedInt(s, 2)` accepts it.
- `Integer.toBinaryString` drops leading zeros, so the string version must pad to 32 characters.
- Hex literals such as `0xaaaaaaaa` are valid `int` literals in Java (they are negative values as signed ints); pair them with `>>>` so no sign extension occurs.
- `Integer.reverse(n)` exists and does exactly this; it is the production answer, but the problem is about knowing how it works.

## 10. Common Mistakes

- Looping `while (n != 0)` instead of exactly 32 times. For `n = 2`, the loop stops after two iterations with `result = 1` instead of `1073741824`, because the remaining shifts never happen. Fix: always iterate 32 times.
- Doing `result |= n & 1; result <<= 1;`, which shifts one extra time at the end and doubles the answer. Fix: shift first, then OR in the bit.
- Using `>>` instead of `>>>`, which sign-extends for negative inputs and keeps `n` from reaching zero. Fix: `n >>>= 1`.
- Running 31 or 33 iterations. Fix: the width is exactly 32.
- Parsing a 32-bit binary string with `Integer.parseInt`, which throws when the top bit is set. Fix: `Integer.parseUnsignedInt(s, 2)`.
- Forgetting to pad `Integer.toBinaryString` output to 32 characters in the string approach, which reverses fewer than 32 positions. Fix: left-pad with zeros.

## 11. Interview Takeaway

- **Trigger sentence:** "Reversing the bits of a fixed-width integer means peeling the lowest bit with `n & 1` and pushing it onto the result with a left shift, for exactly the full width; for many calls, use a byte lookup table or mask-and-swap."
- **90-second explanation:** Bit reversal is a fixed-width problem: the answer depends on all 32 positions, including zeros at either end. I loop exactly 32 times. Each time I shift the result left by one, OR in the lowest bit of `n` using `n & 1`, and shift `n` right with the unsigned `>>>`. The first bit I read, bit 0, gets shifted the most and ends at bit 31; the last bit read, bit 31, ends at bit 0. I never stop early when `n` becomes zero, because the trailing zeros of `n` need to become leading zeros of the result, which only happens if the result keeps shifting. It's O(1) time (32 iterations) and O(1) space. If the function is called many times, I'd precompute the reversal of all 256 byte values and combine four lookups: reverse each byte and place the lowest byte of `n` at the top. Alternatively, five mask-and-swap steps (swap 16-bit halves, then bytes, nibbles, bit pairs, and single bits) do the same with no loop and no memory. Java's `Integer.reverse` implements that idea.
- **Related problems:** 191 (Number of 1 Bits), 136 (Single Number), 338 (Counting Bits), 7 (Reverse Integer).

## 12. Recall Questions

**Q:** Why must the loop run exactly 32 times rather than until `n` is zero?
**A:** Leading and trailing zeros are part of the 32-bit pattern. Trailing zeros of `n` become leading zeros of the result, and that only happens if `result` keeps shifting for all 32 iterations.

**Q:** Why shift `result` first and then OR in the new bit?
**A:** Shifting first makes room at position 0 for the new bit. OR-ing first and shifting afterward would push every bit one position too high at the end.

**Q:** Why use `>>>` and not `>>` on `n`?
**A:** `>>>` fills with zeros, so `n` always reaches zero and no sign bit is copied in. `>>` sign-extends negative values, which would corrupt the bits being read.

**Q:** How does the byte lookup table speed up repeated calls?
**A:** It precomputes the reversal of all 256 byte values once; each call then takes four lookups (one per byte, placed in swapped byte positions) instead of a 32-iteration loop.

**Q:** Why does the reversed result of any valid input here fit in a signed `int`?
**A:** `n` is even, so its bit 0 is 0, which becomes bit 31 of the result; a zero top bit means the result is non-negative.

## 13. Final Code

```java
public class Solution {
    // you need treat n as an unsigned value
    public int reverseBits(int n) {
        int result = 0;
        for (int i = 0; i < 32; i++) {
            result = (result << 1) | (n & 1);
            n >>>= 1;
        }
        return result;
    }
}
```

## 14. Self-Test

Given a 32-bit integer, return the integer whose bits are the reverse of its 32 bits (bit 0 becomes bit 31, and so on), keeping leading and trailing zeros. Re-derive: loop exactly 32 times; each time shift `result` left by one, OR in `n & 1`, and shift `n` right with `>>>`; return `result`. For many calls, precompute reversed bytes in a 256-entry table and combine four lookups, or use five mask-and-swap steps.
