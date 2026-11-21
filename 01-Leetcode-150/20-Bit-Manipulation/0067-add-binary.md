---
title: "0067 — Add Binary"
layout: default
parent: Bit Manipulation
grand_parent: Leetcode
nav_order: 67
permalink: /20-Bit-Manipulation/0067-add-binary/

problem: 67 - Add Binary
url: https://leetcode.com/problems/add-binary/
difficulty: Easy
section: Bit Manipulation
patterns: [digit-by-digit-addition, carry-propagation]
data_structures: [string]
time: O(max(m,n))
space: O(max(m,n))
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0067 — Add Binary

| Field | Value |
|---|---|
| **Pattern** | Digit-by-Digit Addition · Carry Propagation |
| **Difficulty** | Easy |
| **Section** | Bit Manipulation |
| **Time** | O(max(m,n)) |
| **Space** | O(max(m,n)) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Microsoft, Meta |
| **LeetCode** | [Add Binary]([https://leetcode.com/problems/add-binary/](https://leetcode.com/problems/add-binary/)) |

## 1. Problem in Simple Words

**Given:** Two binary strings `a` and `b`, each made only of the characters `'0'` and `'1'`.

**Required:** Return their sum as a binary string.

**Constraints that actually matter:**
- `1 <= a.length, b.length <= 10^4` — each string can hold up to 10,000 bits. That is far more than any primitive integer type can store (an `int` holds 31 value bits, a `long` 63). So you **cannot** convert the strings to numbers, add them with `+`, and convert back. The addition has to be done bit by bit on the strings.
- `a` and `b` contain only `'0'` and `'1'` — every column sum is small (at most 1 + 1 + 1 = 3 with a carry), so there is no overflow concern in the per-column arithmetic.
- **No leading zeros except for the number zero itself** — this means the answer must also have no leading zeros, and the answer for `"0" + "0"` is `"0"`, not `"00"` and not an empty string.
- The inputs can have **different lengths**, and the sum can be **one bit longer** than the longer input (for example `"11" + "1" = "100"`), so a final carry has to be handled after both inputs are used up.

## 2. Recognition Signals

- "Add two numbers represented as strings (or lists) of digits" → simulate grade-school column addition, starting from the least significant digit (the right end of each string).
- Inputs far larger than primitive types → do not convert to a numeric type. Work digit by digit, carrying as you go. The same idea is used when adding digits stored in linked lists.
- The result is built from least significant to most significant, so you need a way to collect the digits and then put them in the right order (append to a `StringBuilder` and reverse once).
- A carry can survive after both inputs run out, so the loop condition must include the carry.

**Pattern:** Digit-by-digit addition with a carry, moving from right to left, building the result in reverse and reversing it at the end.

## 3. Core Idea

Binary addition works exactly like decimal column addition, just in base 2. At each column, take the bit from `a` (or `0` if `a` has run out), the bit from `b` (or `0` if `b` has run out), and the carry from the previous column. Their total `sum` is between 0 and 3. The output bit for this column is `sum % 2`, and the carry into the next column is `sum / 2` (which is 0 or 1).

Start at the last character of each string and move left. Keep going while either string still has characters **or** a carry remains. Each step produces one bit, from least to most significant, so the collected bits are in reverse order and must be reversed at the end.

**Invariant:** After processing the `k` rightmost columns, the collected bits are exactly the lowest `k` bits of the sum (in reverse order), and `carry` is the value carried into column `k`. When both inputs are exhausted and `carry == 0`, nothing remains to add, so the collected bits (reversed) are the full sum.

**Why the loop terminates:** each iteration moves at least one pointer left while an input still has bits. Once both inputs are exhausted, `sum` equals the carry, which is at most 1, so the next carry is `1 / 2 = 0` and the loop ends after at most one extra iteration.

## 4. Approach 1 — Brute Force

**Logic:** Parse both strings as binary numbers, add them with `+`, and convert the result back to a binary string.

```java
// ILLUSTRATIVE ONLY: correct only when both inputs and their sum fit in an int.
public String addBinary(String a, String b) {
    int x = Integer.parseInt(a, 2);
    int y = Integer.parseInt(b, 2);
    return Integer.toBinaryString(x + y);
}
```

- **Time:** O(n) for parsing and formatting, if it worked.
- **Space:** O(n) for the result string.

**Why it is not acceptable here:** It is not just slow, it is **wrong for this problem's input sizes**. `Integer.parseInt(a, 2)` throws a `NumberFormatException` when the value does not fit in 31 bits, and even when both parse, `x + y` can silently overflow. With strings of up to 10,000 bits, almost every large test breaks it. Replacing `int` with `long` only moves the limit to 63 bits. (`java.math.BigInteger` would produce the right answer for any length, via `new BigInteger(a, 2).add(new BigInteger(b, 2)).toString(2)`, but it hides the carry logic that this problem is designed to make you write yourself.)

## 5. Approach 2 — Optimized

**Algorithm:**
1. Create an empty `StringBuilder result`. Set `i = a.length() - 1`, `j = b.length() - 1`, and `carry = 0`.
2. While `i >= 0` or `j >= 0` or `carry != 0`:
   - Start `sum = carry`.
   - If `i >= 0`, add `a.charAt(i) - '0'` to `sum` and decrement `i`.
   - If `j >= 0`, add `b.charAt(j) - '0'` to `sum` and decrement `j`.
   - Append `sum % 2` to `result`, and set `carry = sum / 2`.
3. Reverse `result` and return it as a string.

```java
public String addBinary(String a, String b) {
    StringBuilder result = new StringBuilder();
    int i = a.length() - 1;
    int j = b.length() - 1;
    int carry = 0;

    while (i >= 0 || j >= 0 || carry != 0) {
        int sum = carry;
        if (i >= 0) {
            sum += a.charAt(i--) - '0';
        }
        if (j >= 0) {
            sum += b.charAt(j--) - '0';
        }
        result.append(sum % 2);
        carry = sum / 2;
    }

    return result.reverse().toString();
}
```

- **Time:** O(max(m, n)), where `m` and `n` are the two lengths. Each iteration does O(1) work and the loop runs at most `max(m, n) + 1` times.
- **Space:** O(max(m, n)) for the result, which is required output. Besides the result, only a few integers are used.

**Why this is optimal:** Every bit of both inputs has to be read at least once to produce a correct sum, so O(max(m, n)) time is a lower bound. The output itself has up to `max(m, n) + 1` characters, so that much space is also unavoidable. Unlike Approach 1, it never builds a numeric value, so it works for any length. Appending to a `StringBuilder` and reversing once is O(n); inserting at the front on every step would cost O(n²).

## 6. Dry Run

Example: `a = "11"`, `b = "1"` (Example 1, expected `"100"`). Chosen because the inputs have different lengths and the sum has a final carry that creates a new leading bit, so it exercises both the "input exhausted" case and the `carry != 0` loop condition. It also avoids a palindrome result, so the final reversal is visible.

Initial: `i = 1`, `j = 0`, `carry = 0`, `result = ""`.

| Step | i | j | Bit from a | Bit from b | Carry in | sum | Appended (sum % 2) | Carry out (sum / 2) | result so far |
|------|---|---|------------|------------|----------|-----|--------------------|---------------------|---------------|
| 1 | 1 | 0 | 1 | 1 | 0 | 2 | 0 | 1 | "0" |
| 2 | 0 | -1 | 1 | none (0) | 1 | 2 | 0 | 1 | "00" |
| 3 | -1 | -1 | none (0) | none (0) | 1 | 1 | 1 | 0 | "001" |

Exit condition: `i < 0`, `j < 0`, and `carry == 0`, so the loop stops. The collected bits are `"001"`, least significant first.

**Final answer:** reversing `"001"` gives `"100"`, matching the expected output. Step 3 shows why `carry != 0` must be part of the loop condition: both inputs were already exhausted, but the leftover carry still produced the leading `1`.

## 7. Complexity Summary

| Approach | Time | Space (auxiliary) | Notes |
|----------|------|-------------------|-------|
| 1. Parse to numbers, add, convert back | O(n) if it worked | O(n) result | Incorrect for inputs longer than 31 bits (`int`) or 63 bits (`long`) |
| 2. Column addition with a carry | O(max(m, n)) | O(max(m, n)) for the result | Correct for any length; one reversal at the end |

The output is a string of up to `max(m, n) + 1` characters in both approaches, so its space is required regardless of method.

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|------|------------------|--------------------------|
| Both inputs are `"0"` | The answer must be exactly `"0"`: not empty, not `"00"` | The loop runs once (`i = j = 0`), appends `0`, ends with `carry == 0`, and the result is `"0"` |
| Single-character inputs (`"1"` and `"1"`) | The smallest sizes, with a carry out of the only column | Step 1 gives sum 2: append `0`, carry 1; the loop runs again because of the carry, appends `1`; after reversing the answer is `"10"` |
| Inputs of very different lengths | The shorter input runs out first | Once a pointer is negative its bit is treated as `0`, and the loop continues while the other input still has bits |
| Final carry after both inputs are used up | A leading `1` must be added | The `carry != 0` part of the loop condition triggers one more iteration |
| Carry propagating through every column (all ones, such as `"1111"` plus `"1"`) | One carry chains through the whole length | Each column has sum 2 and passes a carry of 1, and the last carry becomes the new leading `1`, giving `"10000"` |
| Leading zeros in the output | Extra leading zeros would make the answer wrong | The inputs have no leading zeros except zero itself, so the top output digit is always `1`, except when both inputs are `"0"` |
| Duplicates and all-same values (for example `"1111"` plus `"1111"`) | Many equal columns | Every column behaves identically with sum 2 or 3; the carry logic handles it without special cases |
| Negative numbers or integer limits | Not applicable: inputs contain only `'0'` and `'1'`, and no numeric type is used | Nothing to handle |
| Maximum size (`10^4` bits each) | Must stay linear and not overflow | The loop runs at most `10^4 + 1` times with single-digit arithmetic, so there is no overflow |

## 9. Java Notes

- **`StringBuilder` with one `reverse()`:** appending is O(1) amortized and reversing is O(n), for O(n) total. Using `result.insert(0, digit)` on every step would shift all existing characters each time and make the algorithm O(n²).
- **`char - '0'` converts a digit character to its value:** the character `'1'` is code 49, not 1, so subtracting `'0'` (48) is required before adding.
- **`i--` and `j--` inside `charAt(...)`:** the post-decrement reads the current index and then moves left. It keeps the loop body short, but make sure the `i >= 0` guard comes before the read.
- **`result.append(sum % 2)`:** appending an `int` adds its decimal text. Since `sum % 2` is 0 or 1, this appends the character `'0'` or `'1'`.
- **`Integer.parseInt(a, 2)` and `Integer.toBinaryString`:** these exist, but `parseInt` throws `NumberFormatException` when the value exceeds `Integer.MAX_VALUE`, and `x + y` can overflow silently. They are only suitable for short inputs.
- **Bit operations alternative:** `sum & 1` and `sum >> 1` give the same results as `sum % 2` and `sum / 2` for non-negative `sum`.

## 10. Common Mistakes

- **Converting the strings to a number type (`parseInt`, `parseLong`, or accumulating `value * 2 + bit`).** With up to 10,000 bits this overflows or throws, and a hand-rolled accumulation overflows silently into wrong answers. Fix: add bit by bit on the strings, never build a numeric value.
- **Leaving `carry != 0` out of the loop condition.** The final carry is dropped, so `"11" + "1"` returns `"00"` instead of `"100"`. Fix: loop while `i >= 0 || j >= 0 || carry != 0`.
- **Forgetting to reverse the result.** Bits are produced least significant first, so the output string comes out backwards (for example `"001"` instead of `"100"`). Fix: call `reverse()` once at the end.
- **Adding characters without subtracting `'0'`, or using `&&` instead of `||` in the loop condition.** Adding `'1'` as 49 gives nonsense sums, and `&&` stops as soon as the shorter string ends, dropping the rest of the longer one. Fix: use `charAt(i) - '0'`, and keep `||`.

## 11. Interview Takeaway

- **Trigger sentence to memorize:** "Add two numbers given as digit strings or lists → simulate column addition from the least significant end with a carry, looping while either input has digits or a carry remains, then reverse the collected digits."
- **How to explain it aloud in 90 seconds:** "The numbers can have up to 10,000 bits, so I can't convert them to an int or a long. I add them the way I would on paper, in base 2. I keep a pointer at the last character of each string, plus a carry that starts at 0. At each step I take the bit from each string, using 0 if that string is already used up, add them and the carry, append the sum mod 2 as the next result bit, and set the carry to the sum divided by 2. I keep going while either string still has bits or the carry is nonzero, which is what handles a final carry creating a new leading bit. The bits come out least significant first, so I append them to a StringBuilder and reverse it once at the end. That is O(max of the two lengths) time, with the output as the only significant memory. I avoid inserting at the front of the builder each time because that would make it quadratic."
- **Related problems using this pattern:**
  - LeetCode 2 — Add Two Numbers
  - LeetCode 415 — Add Strings
  - LeetCode 66 — Plus One
  - LeetCode 43 — Multiply Strings

## 12. Recall Questions

**Q:** Why can't you solve this by converting the strings to integers and adding them?
**A:** The strings can have up to 10,000 bits, which is far beyond what an `int` (31 bits) or a `long` (63 bits) can hold, so parsing throws an exception or the addition overflows.

**Q:** What are the output bit and the new carry at each column?
**A:** The output bit is `sum % 2` and the new carry is `sum / 2`, where `sum` is the bit from `a`, the bit from `b`, and the incoming carry.

**Q:** Why must the loop condition include `carry != 0`?
**A:** A carry can remain after both inputs are exhausted, and it must become the new leading bit; without the condition, the final `1` in a case like `"11" + "1"` is lost.

**Q:** Why do we reverse the result at the end?
**A:** The algorithm produces bits from least significant to most significant, so the collected string is in reverse order of how a number is written.

**Q:** Why is appending to a `StringBuilder` and reversing once better than inserting at index 0 each time?
**A:** Appending is O(1) amortized and one reversal is O(n), giving O(n) overall; inserting at the front shifts every existing character each time, giving O(n²).

**Q:** How does the code treat the shorter string once it has run out of bits?
**A:** It treats the missing bits as `0`, by only adding a bit when its pointer is still non-negative, while the loop continues for the longer string and any carry.

## 13. Final Code

```java
public String addBinary(String a, String b) {
    StringBuilder result = new StringBuilder();
    int i = a.length() - 1;
    int j = b.length() - 1;
    int carry = 0;

    while (i >= 0 || j >= 0 || carry != 0) {
        int sum = carry;
        if (i >= 0) {
            sum += a.charAt(i--) - '0';
        }
        if (j >= 0) {
            sum += b.charAt(j--) - '0';
        }
        result.append(sum % 2);
        carry = sum / 2;
    }

    return result.reverse().toString();
}
```

## 14. Self-Test

You have two binary strings, possibly of different lengths and up to 10,000 characters each. Return their sum as a binary string. Think about why you cannot convert them to a numeric type, what you compute at each column, and why the loop must keep running after both inputs are used up.
