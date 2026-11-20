---
title: "0002 — Add Two Numbers"
layout: default
parent: Linked List
grand_parent: Leetcode
nav_order: 2
permalink: /08-Linked-List/0002-add-two-numbers/

problem: 2 - Add Two Numbers
url: https://leetcode.com/problems/add-two-numbers/
difficulty: Medium
section: Linked List / Simulation
patterns: [dummy-head, digit-by-digit-addition]
data_structures: [linked-list]
time: O(max(m,n))
space: O(max(m,n))
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0002 — Add Two Numbers

| Field | Value |
|---|---|
| **Pattern** | Dummy Head + Digit-by-Digit Addition |
| **Difficulty** | Medium |
| **Section** | Linked List / Simulation |
| **Time** | O(max(m, n)) |
| **Space** | O(max(m, n)) — for the result list |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Meta |
| **LeetCode** | [Add Two Numbers](https://leetcode.com/problems/add-two-numbers/) |

## 1. Problem in Simple Words

**Given:** Two non-empty linked lists, `l1` and `l2`, each representing a non-negative integer with digits stored in **reverse order** (least significant digit first), one digit per node.

**Required:** Add the two numbers and return the sum as a new linked list, also in reverse-digit order.

**Constraints that actually matter:**
- `1 <= number of nodes in each list <= 100` — each number can have up to 100 digits. This is the single most important constraint: a 100-digit number is **far larger than any primitive numeric type can hold** (a `long` only safely holds about 19 decimal digits), which rules out the seemingly "obvious" shortcut of converting each list to a number, adding them with `+`, and converting back.
- **Digits are stored least-significant-first (reverse order)** — this is actually what makes a direct digit-by-digit simulation easy: adding two numbers digit by digit, starting from the least significant digit and carrying into the next, naturally proceeds in the *same* left-to-right direction as traversing these lists from head to tail, with no need to reverse anything first.
- **The two lists can have different lengths**, and there might be a final carry-out after the longer list is exhausted (as in example 3, where `9999999 + 9999 = 10009998`, producing a result with one more digit than either input) — both of these must be handled correctly by the simulation's termination and carry logic.

## 2. Recognition Signals

- "Add two numbers, digits stored as a linked list, in reverse order" → the explicit "reverse order" storage is the single biggest hint: it's specifically chosen so that **simulating grade-school column addition** (rightmost/least-significant digit first, carrying into the next column) can be done by simply walking both lists forward from their heads simultaneously — no reversal, no recursion-from-the-end, no extra bookkeeping about where the "end" of each number is.
- The constraint allowing up to 100 digits per number is itself a strong signal **against** the naive "convert to a numeric type and use built-in arithmetic" approach, since no primitive Java type (and not even unboxed `long`) can represent a 100-digit number — this pushes directly toward the digit-by-digit linked-list simulation as the only generally correct approach (short of using `BigInteger`, which sidesteps but doesn't really "solve" the intended linked-list manipulation exercise).
- Whenever building a new linked list node by node as a result, where the final list's length isn't known in advance — using a **dummy head node** (a placeholder node whose `next` eventually points to the real first node of the result) is the standard technique that avoids needing special-case logic for attaching the very first real node.

**Pattern:** Digit-by-Digit Simulation with Carry, Using a Dummy Head (walk both input lists simultaneously from their heads, adding corresponding digits plus any carry from the previous column, creating one result node per column, and building the result list via a dummy head to simplify the "attach the first node" logic).

## 3. Core Idea

Since the digits are stored least-significant-first, adding the two numbers is exactly grade-school column addition, processed left to right through the lists: at each step, take the current digit from `l1` (or 0 if `l1` has been exhausted), the current digit from `l2` (or 0 if exhausted), and the carry from the previous step; sum these three values, the result's ones digit becomes the new node's value, and the result's tens digit (0 or 1) becomes the new carry for the next step. Continue until both lists are exhausted **and** there's no remaining carry. Using a dummy head node to start building the result list sidesteps the need for special-case logic to handle attaching the very first node differently from all subsequent ones — the dummy's `next` pointer is simply followed at the end to get the true head of the result.

**Invariant:** After processing column `i` (0-indexed from the least significant digit), the result list built so far correctly represents the sum of the first `i+1` digits of both numbers (padded with leading zeros/0-nodes as needed for the shorter list), plus whatever carry remains to be applied to column `i+1` — so once both input lists are exhausted and the carry is fully resolved (added as a final extra node if nonzero), the result list correctly represents the complete sum.

## 4. Approach 1 — Convert to Numeric Type, Add, Convert Back (Flawed for This Problem's Constraints)

**Logic:** Traverse each list to reconstruct the represented number (reversing the digit order back to normal), add the two numbers using a numeric type, then convert the sum back into a reverse-order linked list.

```java
// ILLUSTRATIVE ONLY — this approach is NOT correct for the full constraint
// range of this problem, since a 100-digit number far exceeds what any
// primitive numeric type (even long) can represent.
public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
    long num1 = toNumber(l1);
    long num2 = toNumber(l2);
    long sum = num1 + num2; // Overflows silently for large inputs!

    return fromNumber(sum);
}

private long toNumber(ListNode node) {
    long result = 0;
    long multiplier = 1;
    while (node != null) {
        result += node.val * multiplier;
        multiplier *= 10;
        node = node.next;
    }
    return result;
}

private ListNode fromNumber(long num) {
    ListNode dummy = new ListNode(0);
    ListNode current = dummy;
    if (num == 0) {
        return new ListNode(0);
    }
    while (num > 0) {
        current.next = new ListNode((int) (num % 10));
        current = current.next;
        num /= 10;
    }
    return dummy.next;
}
```

- **Time:** O(max(m, n)) if it worked correctly for all inputs — but it doesn't.
- **Space:** O(max(m, n)) for the result list.

**Why it is not acceptable here:** With up to 100 digits per number, both `num1` and `num2` (and especially their sum) can vastly exceed the range of a `long` (which tops out around 19 decimal digits), causing silent integer overflow and producing a completely incorrect result for any sufficiently large input — this isn't just "suboptimal," it's **outright incorrect** for a significant portion of the problem's valid input space. It's shown here specifically as a cautionary example of a tempting but fundamentally broken shortcut, not as a valid baseline to optimize from. (Using `java.math.BigInteger` instead of `long` would fix the overflow issue, but doing so would bypass the actual linked-list manipulation skill this problem is designed to test.)

## 5. Approach 2 — Optimized/Correct (Digit-by-Digit Simulation with Carry)

**Algorithm:**
1. Create a `dummy` head node (value irrelevant, e.g. 0) and a `current` pointer initialized to `dummy`.
2. Initialize `carry = 0`.
3. While `l1 != null` or `l2 != null` or `carry != 0`:
   - Let `digit1 = (l1 != null) ? l1.val : 0`, and advance `l1` if it wasn't null.
   - Let `digit2 = (l2 != null) ? l2.val : 0`, and advance `l2` if it wasn't null.
   - Compute `sum = digit1 + digit2 + carry`.
   - Create a new node with value `sum % 10`, attach it as `current.next`, and advance `current` to this new node.
   - Update `carry = sum / 10`.
4. Return `dummy.next` (the true head of the result list, skipping the placeholder dummy node).

```java
public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
    ListNode dummy = new ListNode(0);
    ListNode current = dummy;
    int carry = 0;

    while (l1 != null || l2 != null || carry != 0) {
        int digit1 = (l1 != null) ? l1.val : 0;
        int digit2 = (l2 != null) ? l2.val : 0;

        int sum = digit1 + digit2 + carry;
        carry = sum / 10;

        current.next = new ListNode(sum % 10);
        current = current.next;

        if (l1 != null) {
            l1 = l1.next;
        }
        if (l2 != null) {
            l2 = l2.next;
        }
    }

    return dummy.next;
}
```

- **Time:** O(max(m, n)), where `m` and `n` are the lengths of `l1` and `l2` — each node of both lists is visited exactly once, and the loop runs for at most one extra iteration beyond the longer list's length (to handle a final carry).
- **Space:** O(max(m, n)) for the newly created result list (required output; this isn't "extra" auxiliary space in the sense of something that could be avoided, since the result itself must be built as a new list of comparable size), plus O(1) truly auxiliary space for the `carry` and pointer variables.

**Why this is optimal:** Every digit of both input numbers must be examined at least once to correctly compute the sum, so O(max(m,n)) time is a hard lower bound, and this approach achieves it directly via a single synchronized pass through both lists. Unlike Approach 1, this correctly handles arbitrarily large inputs (up to the constraint's 100-digit limit and beyond, in principle) because it never attempts to represent the full numbers in any fixed-size numeric type — it only ever works with individual digits (0-9) and a single-digit carry (0 or 1), both of which trivially fit within `int` with enormous headroom.

## 6. Dry Run

Example: `l1 = [9,9,9,9,9,9,9]`, `l2 = [9,9,9,9]` (representing 9999999 + 9999 = 10009998).
Chosen because it's the third canonical example, specifically exercising both the differing-list-lengths handling and a final carry-out that extends the result beyond either input's length.

Initial: `dummy` created, `current = dummy`, `carry = 0`.

| Step | l1.val (or 0) | l2.val (or 0) | carry before | sum | new node val (sum%10) | carry after (sum/10) |
|------|---------------------|---------------------|-------------------|-----|------------------------------|------------------------------|
| 1 | 9 | 9 | 0 | 18 | 8 | 1 |
| 2 | 9 | 9 | 1 | 19 | 9 | 1 |
| 3 | 9 | 9 | 1 | 19 | 9 | 1 |
| 4 | 9 | 9 | 1 | 19 | 9 | 1 |
| 5 | 9 | 0 (l2 exhausted) | 1 | 10 | 0 | 1 |
| 6 | 9 | 0 | 1 | 10 | 0 | 1 |
| 7 | 9 | 0 | 1 | 10 | 0 | 1 |
| 8 | 0 (l1 exhausted too) | 0 | 1 | 1 | 1 | 0 |

After step 8, `carry = 0` and both `l1` and `l2` are `null`, so the loop's condition (`l1 != null || l2 != null || carry != 0`) becomes false, ending the loop.

**Final answer:** Result list (following `dummy.next`): `8 -> 9 -> 9 -> 9 -> 0 -> 0 -> 0 -> 1`, i.e., `[8,9,9,9,0,0,0,1]`, matching the expected output exactly.

## 7. Complexity Summary

| Approach | Time | Space (Auxiliary) | Notes |
|----------|------|--------------------|-------|
| 1. Convert to numeric type, add, convert back | O(max(m,n)) if it worked | N/A | **Incorrect** for large inputs due to overflow; shown only as a cautionary example |
| 2. Digit-by-digit simulation with carry | O(max(m,n)) | O(max(m,n)) for the required result list; O(1) truly extra | Correct and optimal for all valid inputs within the constraints |

`m`, `n` are the lengths of `l1` and `l2` respectively.

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|------|-------------------|--------------------------|
| Both lists represent 0 (`l1=[0]`, `l2=[0]`, example 2) | Should produce a result of exactly `[0]`, not an empty list or multiple zero nodes | `digit1=0, digit2=0, carry=0`, `sum=0`; a single node with value 0 is created, `carry` stays 0, and the loop ends after this one iteration (both lists now null, carry 0) — correctly producing `[0]` |
| Lists of different lengths (e.g. `l1` longer than `l2`) | Must correctly treat the exhausted shorter list's "missing" digits as 0, not stop processing early or error | The ternary checks (`(l1 != null) ? l1.val : 0`) correctly substitute 0 once a list is exhausted, and the loop continues as long as *either* list still has nodes (or a carry remains), ensuring the longer list's remaining digits are still correctly processed |
| A final carry-out after both lists are exhausted (e.g. `999 + 1 = 1000`, needing an extra leading — i.e., most-significant, which is last in this reverse-order representation — digit) | Must correctly append one final extra node for the carry, not silently drop it | The loop's continuation condition explicitly includes `|| carry != 0`, so even after both `l1` and `l2` become null, the loop runs one more time if a carry remains, correctly creating that final extra digit node |
| Single-node lists (minimum constraint, `1 <= length <= 100`) | Should work correctly even for the smallest possible valid inputs | The algorithm's logic doesn't depend on any minimum length beyond what the loop and null-checks already handle generically — a single-node-each case is just a very short instance of the same general process |
| Maximum-length lists (100 digits each) | Should correctly compute the sum without any overflow, unlike the flawed Approach 1 | Since Approach 2 only ever works with single digits (0-9) and a single-digit carry (0 or 1) at any given time, there is no risk of overflow regardless of how many total digits are being summed across the full length of both lists |
| Sum of two digits plus carry reaching its maximum possible value (`9+9+1=19`) | Must correctly extract both the new digit (9) and the new carry (1) from this two-digit intermediate sum | `sum % 10` and `sum / 10` correctly extract the ones digit and the tens digit respectively for any sum in the range 0-19 (the maximum possible per-column sum, since each digit is 0-9 and carry is 0 or 1), covering this case correctly like any other |

## 9. Java Notes

- **Dummy head node pattern:** creating a placeholder `dummy` node and tracking a separate `current` pointer that starts at `dummy` is a very common idiom for linked-list-building problems — it avoids needing special-case logic to distinguish "attaching the very first real node" from "attaching every subsequent node," since both cases are handled identically by `current.next = newNode; current = current.next;`.
- **`sum % 10` and `sum / 10` for extracting a digit and carry from a sum that could be up to 19:** standard, idiomatic arithmetic for simulating column addition with carry, directly mirroring how addition is taught by hand.
- **No overflow risk:** since every arithmetic operation here only ever involves single digits (0-9) and a carry (0 or 1), with a maximum possible intermediate `sum` of 19, there's no risk of overflow at any point, regardless of how long the overall input lists are — this is precisely why the digit-by-digit approach correctly sidesteps the overflow problem that plagues Approach 1.
- **Null-safe ternary checks (`(l1 != null) ? l1.val : 0`) combined with separate null-guarded advancement (`if (l1 != null) l1 = l1.next;`):** this pattern is essential for correctly handling lists of different lengths without throwing a `NullPointerException` when one list is exhausted before the other.

## 10. Common Mistakes

- **Attempting to convert each list into a numeric type (`long`, or even trying to parse into an `int`) and adding directly.** As demonstrated in Approach 1, this silently fails (via overflow) for any input where the represented numbers exceed what the chosen numeric type can hold — given the problem explicitly allows up to 100-digit numbers, this failure mode is not a rare edge case but a near-certainty for a meaningful portion of valid test inputs. Fix: always process the numbers digit by digit directly from the linked list representation, never converting to a fixed-size numeric type.
- **Forgetting to include `|| carry != 0` in the loop's continuation condition.** Without this, a final necessary carry-out digit (as in the `9999999 + 9999` example) would be silently dropped, producing an incorrect, truncated result. Fix: always continue the loop as long as either list still has remaining nodes, or there's a nonzero carry left to apply.
- **Forgetting to use a dummy head node, and instead trying to handle "is this the first node of the result" as a special case inside the loop.** While not incorrect if done carefully, this typically adds unnecessary branching complexity (checking "is `current` still null" on every iteration) compared to the cleaner dummy-head approach. Fix: use a dummy head node to uniformly handle attaching every result node, including the first, through the same simple logic.
- **Advancing `l1`/`l2` unconditionally without checking for null first**, which would throw a `NullPointerException` once a shorter list has already been fully traversed. Fix: always guard the advancement of each list pointer (`l1 = l1.next`) with a null check specific to that pointer, independent of the other list's state.

## 11. Interview Takeaway

- **Trigger sentence to memorize:** "Add two numbers represented as reverse-order linked lists → simulate column addition digit by digit, tracking a carry, using a dummy head to simplify result-list construction — never convert to a numeric type, since the numbers can be far too large."
- **90-second explanation:** "Since the digits are stored least-significant-first, I can add the two numbers exactly the way I'd do column addition by hand, just walking both lists forward from their heads instead of needing to find or work from the end. I use a dummy placeholder node to start building my result list, which lets me treat attaching the very first real result node the same way as every subsequent one, avoiding any special-casing. At each step, I take the current digit from each list, defaulting to 0 if that particular list has already run out of nodes, add those two digits plus whatever carry came from the previous step, and the ones digit of that sum becomes my new result node's value, while the tens digit becomes the new carry. I keep going as long as either list still has nodes left, or there's still a leftover carry to apply — that second condition is what correctly handles a case like 999 plus 1, where the result ends up with one more digit than either original number. It's important that I never try to convert either list into an actual numeric type like a long and just add them directly, since these numbers can have up to 100 digits, which is far larger than any primitive numeric type in Java can correctly represent without overflowing."
- **Related problems using this pattern:**
  - LeetCode 445 — Add Two Numbers II (the same problem, but with digits stored in forward/normal order, typically requiring a stack or list reversal first)
  - LeetCode 415 — Add Strings (the same core digit-by-digit addition with carry technique, applied to strings instead of linked lists)
  - LeetCode 67 — Add Binary (the same technique, applied to binary string addition)

## 12. Recall Questions

**Q:** Why is converting each linked list into a `long` (or any other primitive numeric type) and adding them directly fundamentally broken for this problem, rather than just being a "slower" alternative?
**A:** The problem explicitly allows numbers with up to 100 digits, which vastly exceeds the roughly 19-digit capacity of a `long`, so this approach doesn't just run slowly on large inputs — it silently produces a completely incorrect result due to integer overflow, making it outright wrong rather than merely suboptimal.

**Q:** Why does storing digits in reverse order (least significant first) actually make this problem easier to solve, rather than harder?
**A:** Grade-school addition is naturally performed starting from the least significant digit and carrying into more significant digits, so storing the digits in that same order means the algorithm can process both lists by simply walking forward from their heads, exactly mirroring the natural addition process, without needing to reverse either list first or work backward from an unknown end point.

**Q:** Why is the condition `carry != 0` necessary in the loop's continuation check, in addition to checking whether either list still has remaining nodes?
**A:** It's possible for both input lists to be fully exhausted while a carry from the final column of addition still needs to be applied as an entirely new, extra digit in the result (as happens whenever the sum of the two numbers' most significant digits, plus any carry into that column, is itself 10 or greater), and without this condition, that final carry digit would be silently dropped from the result.

**Q:** Why doesn't the digit-by-digit simulation approach ever risk integer overflow, regardless of how many digits the input numbers have?
**A:** At any single point in the computation, the algorithm only ever works with individual digits (each between 0 and 9) and a single carry value (0 or 1), so the maximum possible intermediate sum at any step is 19, which is trivially far within the range of a basic `int`, completely independent of how many total digits are being processed across the full length of the input lists.

**Q:** Why is using a dummy head node considered good practice here, rather than just a stylistic preference?
**A:** Without a dummy head, the code would need separate, explicit logic to handle creating and attaching the very first node of the result list differently from every subsequent node (since there's no existing node to attach the first one *to* otherwise), whereas the dummy head provides that initial anchor point uniformly, letting every node, including the first, be attached using the exact same simple line of code.

## 13. Final Code

```java
public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
    ListNode dummy = new ListNode(0);
    ListNode current = dummy;
    int carry = 0;

    while (l1 != null || l2 != null || carry != 0) {
        int digit1 = (l1 != null) ? l1.val : 0;
        int digit2 = (l2 != null) ? l2.val : 0;

        int sum = digit1 + digit2 + carry;
        carry = sum / 10;

        current.next = new ListNode(sum % 10);
        current = current.next;

        if (l1 != null) {
            l1 = l1.next;
        }
        if (l2 != null) {
            l2 = l2.next;
        }
    }

    return dummy.next;
}
```

## 14. Self-Test

You have two non-empty linked lists representing non-negative integers, digits stored in reverse order. Add the two numbers and return the sum as a linked list, also in reverse order. Think about why simulating column addition digit by digit, carrying as you go, is required here — and why converting the lists to a numeric type like `long` first would be a serious mistake given the constraints.
