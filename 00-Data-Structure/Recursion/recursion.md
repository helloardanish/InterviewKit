---
title: "Recursion"
layout: default
parent: Data Structures & Algorithms
nav_order: 18
permalink: /data-structures/Recursion/recursion/

topic: Recursion
section: Algorithm Techniques
concepts: [base-case, recursive-case, call-stack, stack-overflow]
techniques: [direct-recursion, indirect-recursion, tail-recursion]
complexities: [O(1), O(n), O(log n), O(2^n)]
tags: [algorithms, recursion, call-stack, java]
---

# Recursion in Java

## 1. What Is Recursion?

**Recursion is a programming technique in which a method calls itself to solve a problem.**

Instead of solving the entire problem in one go, we break it into smaller versions of the same problem until we reach a condition where the answer is already known.

That condition is called the **Base Case**.

### Real-World Example: Opening Nested Boxes

Imagine you have five boxes, one inside another.

You open the first box and find another box.

You open the second box and find another box.

You continue until you reach the smallest box, which contains a gift.

Now you can return from the smallest box and finish closing the boxes one by one.

```text
Open Box 1
    |
    v
  Open Box 2
      |
      v
    Open Box 3
        |
        v
      Open Box 4
          |
          v
        Open Box 5
            |
            v
          Gift Found!
```

This is similar to recursion:

1. A method calls itself.
2. Each call works on a smaller problem.
3. Eventually, a stopping condition is reached.
4. The method calls finish one by one.

### Simple Java Example

```java
public class RecursionExample {

    static void sayHello(int count) {

        if (count == 0) {
            return;
        }

        System.out.println("Hello!");

        sayHello(count - 1);
    }

    public static void main(String[] args) {
        sayHello(5);
    }
}
```

Output:

```text
Hello!
Hello!
Hello!
Hello!
Hello!
```

The method calls itself five times, and then stops when `count == 0`.

---

## 2. Two Essential Parts of Recursion

Every correctly designed recursive solution needs two important parts.

### 2.1 Base Case

The Base Case is the condition that stops recursion.

Without a base case, the method keeps calling itself until the program runs out of stack space.

Example:

```java
if (n == 0) {
    return;
}
```

When `n` becomes zero, the method stops calling itself.

### 2.2 Recursive Case

The Recursive Case is where the method calls itself with a smaller or simpler input.

Example:

```java
sayHello(n - 1);
```

Each call reduces `n` by one.

### Complete Structure

```java
static void recursiveMethod(int n) {

    // Base Case
    if (n == 0) {
        return;
    }

    // Perform some work
    System.out.println(n);

    // Recursive Case
    recursiveMethod(n - 1);
}
```

**Remember:**

- Base Case = When should recursion stop?
- Recursive Case = How can the problem become smaller?

---

## 3. Understanding the Call Stack

This is the most important concept for understanding recursion.

Whenever a method is called in Java, the JVM creates a **Stack Frame** for that method call.

The stack frame stores information such as:

- Method parameters
- Local variables
- Return information
- Other execution-related data

When a method calls itself, another stack frame is created.

When a method finishes, its stack frame is removed.

This follows the **LIFO (Last In, First Out)** principle.

### Example: Countdown

```java
static void countdown(int n) {

    if (n == 0) {
        return;
    }

    System.out.println(n);

    countdown(n - 1);
}
```

Call:

```java
countdown(3);
```

### Step 1: Method Calls

```text
countdown(3)
    |
    v
countdown(2)
    |
    v
countdown(1)
    |
    v
countdown(0)
```

At this point, the base case is reached.

### Step 2: Stack Visualization

Conceptually, the stack looks like this:

```text
        STACK
    +-------------+
    | countdown(0)| <- Top
    +-------------+
    | countdown(1)|
    +-------------+
    | countdown(2)|
    +-------------+
    | countdown(3)|
    +-------------+
```

The `countdown(0)` call returns first.

Then `countdown(1)`, followed by `countdown(2)` and `countdown(3)`.

### Important Observation

The method calls happen from top to bottom, but they finish from bottom to top.

This explains why recursive programs can produce different output depending on where the print statement is placed.

---

## 4. Print Before and After the Recursive Call

Consider this method:

```java
static void printNumbers(int n) {

    if (n == 0) {
        return;
    }

    System.out.println("Before: " + n);

    printNumbers(n - 1);

    System.out.println("After: " + n);
}
```

Call:

```java
printNumbers(3);
```

Output:

```text
Before: 3
Before: 2
Before: 1
After: 1
After: 2
After: 3
```

### Why Does This Happen?

Before the recursive call, the current method executes immediately.

After the recursive call, execution pauses until the deeper call returns.

The deeper calls therefore finish first.

**Execution flow:**

```text
printNumbers(3)
    |
    | Print Before: 3
    |
    v
printNumbers(2)
    |
    | Print Before: 2
    |
    v
printNumbers(1)
    |
    | Print Before: 1
    |
    v
printNumbers(0)
    |
    | Base Case
    |
    v
Return to printNumbers(1)
    |
    | Print After: 1
    |
    v
Return to printNumbers(2)
    |
    | Print After: 2
    |
    v
Return to printNumbers(3)
    |
    | Print After: 3
```

This is commonly called the **unwinding phase** of recursion.

---

## 5. Factorial Using Recursion

Factorial is one of the easiest problems to understand recursion.

The factorial of a positive integer `n` is the product of all positive integers from `1` to `n`.

Mathematically:

```text
5! = 5 × 4 × 3 × 2 × 1 = 120
```

Also:

```text
0! = 1
1! = 1
```

### Recursive Formula

```text
factorial(n) = n × factorial(n - 1)

Base Case:
factorial(0) = 1
```

### Java Implementation

```java
public class Factorial {

    static long factorial(int n) {

        if (n < 0) {
            throw new IllegalArgumentException(
                "Factorial is not defined for negative integers."
            );
        }

        if (n == 0 || n == 1) {
            return 1;
        }

        return n * factorial(n - 1);
    }

    public static void main(String[] args) {
        System.out.println(factorial(5));
    }
}
```

Output:

```text
120
```

### Step-by-Step Execution

For:

```java
factorial(5);
```

The recursive calls are:

```text
factorial(5)
    |
    v
5 * factorial(4)
    |
    v
5 * 4 * factorial(3)
    |
    v
5 * 4 * 3 * factorial(2)
    |
    v
5 * 4 * 3 * 2 * factorial(1)
    |
    v
5 * 4 * 3 * 2 * 1
```

Now the calls return:

```text
factorial(1) = 1
factorial(2) = 2 * 1 = 2
factorial(3) = 3 * 2 = 6
factorial(4) = 4 * 6 = 24
factorial(5) = 5 * 24 = 120
```

Final result:

```text
120
```

### Complexity Analysis

| Complexity | Value | Explanation |
|---|---|---|
| Time | O(n) | One call for each integer |
| Auxiliary Space | O(n) | Recursive call stack |

**Important:** Java's `long` can store factorial values only up to `20!`. `21!` exceeds the maximum value of a signed `long`.

For larger factorials, use `BigInteger`.

---

## 6. Sum of Natural Numbers Using Recursion

Problem: Find the sum of all natural numbers from `1` to `n`.

Example:

```text
n = 5

Sum = 1 + 2 + 3 + 4 + 5
    = 15
```

### Recursive Formula

```text
sum(n) = n + sum(n - 1)

Base Case:
sum(0) = 0
```

### Java Implementation

```java
static int sum(int n) {

    if (n == 0) {
        return 0;
    }

    return n + sum(n - 1);
}
```

Usage:

```java
System.out.println(sum(5));
```

Output:

```text
15
```

### Execution

```text
sum(5)
= 5 + sum(4)
= 5 + 4 + sum(3)
= 5 + 4 + 3 + sum(2)
= 5 + 4 + 3 + 2 + sum(1)
= 5 + 4 + 3 + 2 + 1 + sum(0)
= 5 + 4 + 3 + 2 + 1 + 0
= 15
```

Time Complexity: O(n)

Auxiliary Space: O(n)

---

## 7. Fibonacci Series Using Recursion

The Fibonacci series is a sequence in which each number is the sum of the previous two numbers.

```text
0, 1, 1, 2, 3, 5, 8, 13, 21, 34...
```

Mathematical definition:

```text
fib(0) = 0
fib(1) = 1

fib(n) = fib(n - 1) + fib(n - 2)
```

### Java Implementation

```java
static int fibonacci(int n) {

    if (n < 0) {
        throw new IllegalArgumentException(
            "Input must not be negative."
        );
    }

    if (n == 0) {
        return 0;
    }

    if (n == 1) {
        return 1;
    }

    return fibonacci(n - 1) + fibonacci(n - 2);
}
```

Usage:

```java
for (int i = 0; i < 10; i++) {
    System.out.print(fibonacci(i) + " ");
}
```

Output:

```text
0 1 1 2 3 5 8 13 21 34
```

### Understanding the Recursive Calls

For:

```java
fibonacci(4);
```

The calls form a tree:

```text
                 fib(4)
                /      \
           fib(3)      fib(2)
           /   \       /   \
       fib(2) fib(1) fib(1) fib(0)
        /  \
    fib(1) fib(0)
```

Notice that `fib(2)` is calculated more than once.

This is the reason the simple recursive Fibonacci implementation is inefficient.

### Complexity Analysis

| Complexity | Value |
|---|---|
| Time | O(2ⁿ) upper bound |
| Auxiliary Space | O(n) |

The number of calls grows exponentially.

### Optimized Fibonacci Using Memoization

Memoization means storing previously calculated results so they can be reused.

```java
import java.util.Arrays;

public class Fibonacci {

    static int fibonacci(int n, int[] memo) {

        if (n < 0) {
            throw new IllegalArgumentException();
        }

        if (n <= 1) {
            return n;
        }

        if (memo[n] != -1) {
            return memo[n];
        }

        memo[n] = fibonacci(n - 1, memo)
                + fibonacci(n - 2, memo);

        return memo[n];
    }

    public static void main(String[] args) {

        int n = 10;

        int[] memo = new int[n + 1];
        Arrays.fill(memo, -1);

        System.out.println(fibonacci(n, memo));
    }
}
```

Time Complexity: O(n)

Auxiliary Space: O(n), including the memoization array and recursive stack.

For very large Fibonacci numbers, use `BigInteger` to avoid integer overflow.

---

## 8. Reverse a String Using Recursion

Problem: Reverse a string without using a loop.

Input:

```text
JAVA
```

Output:

```text
AVAJ
```

### Java Implementation

```java
static String reverse(String str) {

    if (str == null || str.length() <= 1) {
        return str;
    }

    return reverse(str.substring(1)) + str.charAt(0);
}
```

Usage:

```java
System.out.println(reverse("JAVA"));
```

Output:

```text
AVAJ
```

### Execution

```text
reverse("JAVA")
= reverse("AVA") + 'J'
= reverse("VA") + 'A' + 'J'
= reverse("A") + 'V' + 'A' + 'J'
= "A" + "V" + "A" + "J"
= "AVAJ"
```

**Complexity note:** This simple implementation creates substring objects and concatenated strings. Depending on the Java version and implementation, the total character-copying work can be O(n²).

A more efficient approach uses a character array and two pointers.

```java
static void reverse(char[] chars, int left, int right) {

    if (left >= right) {
        return;
    }

    char temp = chars[left];
    chars[left] = chars[right];
    chars[right] = temp;

    reverse(chars, left + 1, right - 1);
}
```

Usage:

```java
char[] chars = "JAVA".toCharArray();

reverse(chars, 0, chars.length - 1);

System.out.println(new String(chars));
```

Time Complexity: O(n)

Auxiliary Space: O(n) due to the recursive call stack.

---

## 9. Check Whether a String Is a Palindrome

A palindrome is a word that reads the same forward and backward.

Examples:

```text
MADAM
LEVEL
RADAR
```

### Java Implementation

```java
static boolean isPalindrome(
        String str, int left, int right) {

    if (left >= right) {
        return true;
    }

    if (str.charAt(left) != str.charAt(right)) {
        return false;
    }

    return isPalindrome(str, left + 1, right - 1);
}
```

Usage:

```java
String str = "MADAM";

System.out.println(
    isPalindrome(str, 0, str.length() - 1)
);
```

Output:

```text
true
```

### How It Works

```text
M A D A M
|       |

M == M

  A D A
  |   |

  A == A

    D
```

All matching characters lead to the base case.

Time Complexity: O(n)

Auxiliary Space: O(n)

---

## 10. Print Numbers in Different Orders

The position of the recursive call determines when statements execute.

### Print from 1 to N

```java
static void printAscending(int n) {

    if (n == 0) {
        return;
    }

    printAscending(n - 1);

    System.out.println(n);
}
```

Input:

```java
printAscending(5);
```

Output:

```text
1
2
3
4
5
```

### Print from N to 1

```java
static void printDescending(int n) {

    if (n == 0) {
        return;
    }

    System.out.println(n);

    printDescending(n - 1);
}
```

Input:

```java
printDescending(5);
```

Output:

```text
5
4
3
2
1
```

### Key Difference

| Print Position | Output |
|---|---|
| Before recursive call | N to 1 |
| After recursive call | 1 to N |

This is a common recursion interview question.

---

## 11. Types and Patterns of Recursion

There are different ways to classify recursion.

### 11.1 Direct Recursion

A method directly calls itself.

```java
static void methodA(int n) {

    if (n == 0) {
        return;
    }

    methodA(n - 1);
}
```

### 11.2 Indirect or Mutual Recursion

Two or more methods call each other.

```java
static void methodA(int n) {

    if (n <= 0) {
        return;
    }

    System.out.println("A: " + n);

    methodB(n - 1);
}

static void methodB(int n) {

    if (n <= 0) {
        return;
    }

    System.out.println("B: " + n);

    methodA(n - 1);
}
```

Here, `methodA()` calls `methodB()`, and `methodB()` calls `methodA()`.

### 11.3 Tail Recursion

A recursive call is the final operation performed by the method.

```java
static void print(int n) {

    if (n == 0) {
        return;
    }

    System.out.println(n);

    print(n - 1);
}
```

There is no additional computation after the recursive call.

### 11.4 Head Recursion

The recursive call happens before the main processing.

```java
static void print(int n) {

    if (n == 0) {
        return;
    }

    print(n - 1);

    System.out.println(n);
}
```

### 11.5 Tree Recursion

A method makes multiple recursive calls.

```java
static void treeRecursion(int n) {

    if (n <= 0) {
        return;
    }

    System.out.println(n);

    treeRecursion(n - 1);
    treeRecursion(n - 1);
}
```

The calls form a tree-like structure.

The naive Fibonacci implementation is another example.

### 11.6 Nested Recursion

A recursive call is passed as an argument to another recursive call.

```java
static int nestedRecursion(int n) {

    if (n > 100) {
        return n - 10;
    }

    return nestedRecursion(
        nestedRecursion(n + 11)
    );
}
```

Nested recursion can be difficult to trace because one recursive result becomes the argument of another call.

---

## 12. Recursion vs Iteration

Iteration uses loops such as `for` and `while`.

Recursion uses methods calling themselves.

Example: Calculate the sum from 1 to n.

### Iterative Approach

```java
static int sumIterative(int n) {

    int sum = 0;

    for (int i = 1; i <= n; i++) {
        sum += i;
    }

    return sum;
}
```

### Recursive Approach

```java
static int sumRecursive(int n) {

    if (n == 0) {
        return 0;
    }

    return n + sumRecursive(n - 1);
}
```

### Comparison

| Feature | Recursion | Iteration |
|---|---|---|
| Implementation | Method calls itself | Uses loops |
| Termination | Base case | Loop condition |
| Memory | Usually uses call stack | Usually uses constant extra space |
| Performance | Method-call overhead | Often faster |
| Readability | Often cleaner for recursive structures | Often simpler for repetitive tasks |
| Stack overflow | Possible with deep recursion | Not caused by loop depth |
| Suitable for | Trees, divide and conquer, backtracking | Simple repeated operations |

**Important:** Recursion is not automatically better than iteration. Choose the approach that makes the solution easiest to understand and maintain while meeting performance requirements.

---

## 13. Recursion and Stack Overflow

Every recursive method call requires a stack frame.

If recursion becomes too deep, the JVM may run out of available stack space.

This results in:

```text
java.lang.StackOverflowError
```

### Example of Infinite Recursion

```java
static void infiniteRecursion() {

    infiniteRecursion();
}
```

There is no base case, so the method keeps calling itself.

### Example of Deep Recursion

```java
static void count(int n) {

    if (n == 0) {
        return;
    }

    count(n - 1);
}
```

Calling:

```java
count(1000000);
```

may cause a `StackOverflowError`, depending on the JVM and available stack size.

**Important:** Java does not guarantee tail-call optimization. Even tail-recursive Java methods normally consume one stack frame per recursive call.

For very deep recursion, consider an iterative implementation or an explicit stack data structure.

---

## 14. How to Solve Any Recursion Problem

Follow this systematic approach.

### Step 1: Understand the Problem

Identify what the method must calculate or return.

Example:

Find the factorial of `n`.

### Step 2: Identify the Base Case

Ask:

What is the smallest input for which the answer is already known?

For factorial:

```text
factorial(0) = 1
```

### Step 3: Identify the Recursive Relationship

Express the problem using a smaller input.

```text
factorial(n) = n * factorial(n - 1)
```

### Step 4: Ensure Progress

Every recursive call must move towards the base case.

For example:

```java
factorial(n - 1);
```

The input gets smaller.

### Step 5: Trust the Recursive Call

Assume the smaller problem returns the correct result.

Then use that result to solve the current problem.

### Step 6: Trace a Small Example

Test with:

```text
n = 3
```

Trace:

```text
factorial(3)
= 3 * factorial(2)
= 3 * 2 * factorial(1)
= 3 * 2 * 1
= 6
```

### Step 7: Analyze Complexity

Calculate:

- Number of recursive calls
- Work performed in each call
- Maximum recursion depth

This helps determine time and auxiliary space complexity.

---

## 15. Recursion in Data Structures and Algorithms

Recursion is used extensively in advanced algorithms.

| Topic | How Recursion Is Used |
|---|---|
| Binary Search | Search a smaller half |
| Merge Sort | Divide array into smaller parts |
| Quick Sort | Sort partitions recursively |
| Tree Traversal | Visit child nodes |
| Graph DFS | Explore connected nodes |
| Backtracking | Explore possible choices |
| Dynamic Programming | Solve overlapping subproblems |
| Tower of Hanoi | Move smaller stacks of disks |

### Example: Binary Search

```java
static int binarySearch(
        int[] arr, int low, int high, int target) {

    if (low > high) {
        return -1;
    }

    int mid = low + (high - low) / 2;

    if (arr[mid] == target) {
        return mid;
    }

    if (target < arr[mid]) {
        return binarySearch(arr, low, mid - 1, target);
    }

    return binarySearch(arr, mid + 1, high, target);
}
```

Time Complexity: O(log n)

Auxiliary Space: O(log n) due to recursive calls.

### Example: Tree Traversal

```java
class Node {

    int data;
    Node left;
    Node right;

    Node(int data) {
        this.data = data;
    }
}

static void inorder(Node root) {

    if (root == null) {
        return;
    }

    inorder(root.left);

    System.out.println(root.data);

    inorder(root.right);
}
```

The recursive method visits:

1. Left subtree
2. Current node
3. Right subtree

This is called Inorder Traversal.

---

## 16. Understanding Recursion Complexity

Recursion complexity depends on:

1. Number of recursive calls.
2. Work done in each call.
3. Maximum recursion depth.

### Example 1: Linear Recursion

```java
static void print(int n) {

    if (n == 0) {
        return;
    }

    System.out.println(n);

    print(n - 1);
}
```

Each call creates one more call with `n - 1`.

Time Complexity: O(n)

Auxiliary Space: O(n)

### Example 2: Binary Recursion

```java
static void example(int n) {

    if (n <= 0) {
        return;
    }

    example(n - 1);
    example(n - 1);
}
```

Each call creates two more calls.

Time Complexity: O(2ⁿ)

Auxiliary Space: O(n)

Although the total number of calls is exponential, the maximum depth is linear because each path decreases `n` by one.

### Example 3: Divide and Conquer

```java
static void divide(int n) {

    if (n <= 1) {
        return;
    }

    divide(n / 2);
}
```

Each call reduces the input by half.

Time Complexity: O(log n)

Auxiliary Space: O(log n)

### Common Complexity Patterns

| Recursive Pattern | Time Complexity | Auxiliary Stack Space |
|---|---|---|
| `T(n - 1)` | O(n), if each call does O(1) work | O(n) |
| `T(n / 2)` | O(log n), if each call does O(1) work | O(log n) |
| `2T(n - 1)` | O(2ⁿ), if each call does O(1) work | O(n) |
| `2T(n / 2) + O(n)` | O(n log n) | O(log n) for balanced divide and conquer |
| `T(n - 1) + O(n)` | O(n²) | O(n) |

These are common patterns, not universal formulas. Always consider the actual work performed by each call and the number of calls.

---

## 17. Common Recursion Mistakes

1. **Missing base case:** Causes infinite recursion and eventually stack overflow.
2. **Incorrect base case:** The recursion may stop too early or too late.
3. **Not reducing the input:** Recursive calls may never reach the base case.
4. **Wrong return statement:** The result of the recursive call may be ignored.
5. **Incorrect order of operations:** Printing before and after the call produces different results.
6. **Assuming recursion is always efficient:** Some recursive solutions repeat calculations unnecessarily.
7. **Ignoring stack space:** Deep recursion can cause StackOverflowError.
8. **Using recursion for unnecessarily large inputs:** An iterative solution may be safer.
9. **Not handling invalid inputs:** For example, negative factorial input should be rejected.
10. **Forgetting integer overflow:** Recursive calculations can still exceed Java's numeric limits.

---

## 18. Interview Questions and Answers

### Q1. What is recursion?

Recursion is a programming technique in which a method calls itself to solve smaller instances of a problem.

### Q2. What are the two essential components of recursion?

- Base Case: Stops recursion.
- Recursive Case: Calls the method with a smaller or simpler problem.

### Q3. What happens if there is no base case?

The recursive method may continue calling itself until the JVM throws a StackOverflowError.

### Q4. What is a call stack?

The call stack is the memory structure used to manage active method calls. Each method invocation has its own stack frame.

### Q5. Why does recursion use extra memory?

Each recursive call generally creates another stack frame containing method execution information, parameters, and local variables.

### Q6. What is the difference between direct and indirect recursion?

- Direct recursion: A method calls itself.
- Indirect recursion: One method calls another method that eventually calls the original method.

### Q7. What is tail recursion?

Tail recursion occurs when the recursive call is the final operation performed by the method.

### Q8. Does Java support tail-call optimization?

Java does not guarantee tail-call optimization. Tail-recursive methods can still consume stack space for every call.

### Q9. What is the difference between recursion and iteration?

Recursion uses method calls, while iteration uses loops. Iteration generally uses less stack memory and can be more efficient for simple repeated operations.

### Q10. What is StackOverflowError?

It is an error that occurs when the thread's stack cannot accommodate additional method calls.

### Q11. What is tree recursion?

Tree recursion occurs when a method makes multiple recursive calls, creating a branching call structure.

### Q12. Why is naive recursive Fibonacci inefficient?

It recalculates the same Fibonacci values multiple times. Memoization or an iterative approach reduces the time complexity to O(n).

### Q13. Can recursion be converted into iteration?

Yes. Many recursive algorithms can be implemented iteratively using loops or an explicit stack.

### Q14. Is recursion always slower than iteration?

Not necessarily in every situation, but recursion has method-call and stack-management overhead. Iteration is often more efficient for simple repeated tasks.

### Q15. What is the difference between recursion and backtracking?

Recursion is a technique for solving problems through smaller calls. Backtracking is an algorithmic strategy that explores choices and reverses decisions when a path fails. Backtracking is commonly implemented using recursion.

---

## 19. Practice Problems

### Beginner

1. Print numbers from 1 to N using recursion.
2. Print numbers from N to 1 using recursion.
3. Find the sum of the first N natural numbers.
4. Calculate factorial of a number.
5. Calculate the power of a number.
6. Find the Nth Fibonacci number.
7. Reverse a string using recursion.
8. Check whether a string is a palindrome.
9. Count the digits of a number.
10. Find the sum of digits of a number.

### Intermediate

11. Find the Greatest Common Divisor (GCD) using recursion.
12. Convert a decimal number to binary using recursion.
13. Perform Binary Search recursively.
14. Find the maximum element in an array recursively.
15. Find the minimum element in an array recursively.
16. Check whether an array is sorted.
17. Remove consecutive duplicate characters from a string.
18. Generate all subsequences of a string.
19. Solve the Tower of Hanoi problem.
20. Implement Merge Sort recursively.

### Advanced

21. Implement Quick Sort.
22. Generate all permutations of a string.
23. Solve the N-Queens problem.
24. Generate all subsets of an array.
25. Solve a Sudoku puzzle using backtracking.
26. Find all paths through a maze.
27. Implement tree traversals.
28. Perform Depth First Search (DFS).
29. Solve the Combination Sum problem.
30. Implement recursive Dynamic Programming with memoization.

---

## 20. Quick Revision Cheat Sheet

| Concept | Explanation |
|---|---|
| Recursion | A method calling itself |
| Base Case | Condition that stops recursion |
| Recursive Case | Method calls itself with a smaller problem |
| Call Stack | Stores active method calls |
| Stack Frame | Information for one method invocation |
| Unwinding | Returning from recursive calls |
| Direct Recursion | Method calls itself |
| Indirect Recursion | Methods call each other |
| Tail Recursion | Recursive call is the final operation |
| Head Recursion | Recursive call happens before processing |
| Tree Recursion | Multiple recursive calls |
| StackOverflowError | Stack cannot accommodate more calls |
| Memoization | Store previously calculated results |
| Backtracking | Explore choices and undo failed choices |

### Most Important Things to Remember

- Every recursive solution needs a base case.
- Every recursive call should make progress towards the base case.
- Method calls are pushed onto the call stack.
- Recursive calls finish in reverse order.
- Code before a recursive call executes while going deeper.
- Code after a recursive call executes while returning.
- Recursion can make tree and divide-and-conquer problems easier to understand.
- Memoization avoids repeated calculations.
- Java does not guarantee tail-call optimization.
- Always consider both time complexity and stack space.

### Recommended Learning Order

1. Understand Base Case and Recursive Case.
2. Understand the Call Stack.
3. Practice printing numbers in ascending and descending order.
4. Solve factorial and sum of natural numbers.
5. Understand Fibonacci and memoization.
6. Practice string and array recursion.
7. Learn recursion in Binary Search and Merge Sort.
8. Move to Backtracking, Trees, and Graph DFS.