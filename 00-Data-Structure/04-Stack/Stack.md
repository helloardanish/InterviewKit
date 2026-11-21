---
title: "Stack"
layout: default
parent: Data Structures & Algorithms
nav_order: 3
permalink: /data-structures/Stack/stack/

topic: Stack
section: Linear Data Structures
concepts: [lifo, push, pop, peek]
implementations: [array, linked-list, java-stack, deque]
complexities: [O(1), O(n)]
tags: [data-structures, stack, lifo, java]
---

# Stack Data Structure in Java

A beginner-friendly guide to understanding, implementing, using, and solving coding problems with Stack in Java.

---

# 1. Introduction to Stack

## What is a Stack?

A **Stack** is a linear data structure that follows the **LIFO (Last In, First Out)** principle.

In simple words:

> The element that we insert last will be the first element to be removed.

### Real-world example: Stack of books

Imagine that you have three books:

- Book 1
- Book 2
- Book 3

You place them one above another.

The first book is placed at the bottom, the second on top of it, and the third at the very top.

Now, if you want to remove a book, which one will you remove first?

Obviously, Book 3!

You cannot practically remove Book 1 directly because Book 2 and Book 3 are above it.

This is exactly how a Stack works.

### Understanding LIFO

LIFO means **Last In, First Out**.

Consider inserting three books:

1. Insert Book 1.
2. Insert Book 2.
3. Insert Book 3.

Now remove the books:

1. Book 3 is removed first.
2. Book 2 is removed second.
3. Book 1 is removed last.

Remember: The last element inserted is the first element removed.

### What is TOP?

A Stack allows insertion and removal from only one end, called the **TOP**.

We use TOP to identify the position of the most recently inserted element.

- Insertion happens at TOP.
- Removal happens from TOP.
- Viewing the top element happens at TOP.

We do not normally access elements from the middle or bottom when solving Stack problems.

---

# 2. Visual Representation of Stack

Before writing Java code, let's understand how a Stack looks visually.

## Figure 1: Stack Data Structure – Our Illustration

![Stack Example]({{ '/00-Data-Structure/04-Stack/stack.png' | relative_url }})

This illustration represents how elements are arranged in a Stack.

Notice that all insertions and removals happen from the TOP.

## Figure 2: Real-world Example – Stack of Plates
<figure style="background-color: white; padding: 16px; border-radius: 8px;">
  <img src="https://thumb.wikimedia.org/wikipedia/commons/thumb/1/19/Tallrik_-_Ystad-2018.jpg/500px-Tallrik_-_Ystad-2018.jpg" alt="Stack Data Structure">
  <figcaption align="center">
    <strong>Figure 1:</strong> Similarly to a stack of plates, adding or removing is only practical at the top.
  </figcaption>
</figure>

Just like a stack of plates, we add a new plate at the top and remove the topmost plate first.

This is the basic idea behind LIFO (Last In, First Out).

## Figure 3: Stack Operations – Push and Pop

<figure style="background-color: white; padding: 16px; border-radius: 8px;">
  <img
    src="https://thumb.wikimedia.org/wikipedia/commons/thumb/e/e4/Lifo_stack.svg/960px-Lifo_stack.svg.png"
    alt="Stack Data Structure"
    style="display: block; max-width: 100%; height: auto; margin: 0 auto;"
  >
  <figcaption align="center">
    <strong>Figure 2:</strong> Simple representation of a stack runtime with push and pop operations.
  </figcaption>
</figure>


This diagram illustrates two fundamental operations:

- **Push:** Add an element to the top.
- **Pop:** Remove an element from the top.

Notice that the most recently inserted element is removed first.

## Figure 4: Stack UML Class Diagram


<figure style="background-color: white; padding: 16px; border-radius: 8px;">
  <img
    src="https://thumb.wikimedia.org/wikipedia/commons/thumb/2/2b/Stack_UML_class_diagram.svg/500px-Stack_UML_class_diagram.svg.png"
    alt="Stack Data Structure"
    style="display: block; max-width: 100%; height: auto; margin: 0 auto;"
  >
  <figcaption align="center">
    <strong>Figure 3:</strong> UML diagram for a stack.
  </figcaption>
</figure>

This UML diagram provides an additional technical view of a Stack and its operations.

---

# 3. Basic Stack Operations

Let's understand the three fundamental operations using our book example.

## 3.1 push() – Insert an element

The `push()` operation adds a new element to the top of the Stack.

```java
Stack<String> stack = new Stack<>();

stack.push("Book 1");
stack.push("Book 2");
stack.push("Book 3");
```

Stack representation:

```text
       TOP
        |
    +---------+
    | Book 3  |  <-- Last inserted
    +---------+
    | Book 2  |
    +---------+
    | Book 1  |  <-- First inserted
    +---------+
```

Every time we call `push()`, the new element becomes the topmost element.

## 3.2 pop() – Remove the top element

The `pop()` operation removes and returns the topmost element.

```java
String removedBook = stack.pop();

System.out.println(removedBook);
```

Output:

```text
Book 3
```

After calling `pop()`:

```text
       TOP
        |
    +---------+
    | Book 2  |
    +---------+
    | Book 1  |
    +---------+
```

**Important:** `pop()` does two things:

1. Removes the top element.
2. Returns the removed element.

We can also directly print the removed element:

```java
System.out.println(stack.pop());
```

Output:

```text
Book 2
```

The returned element can also be stored in a variable for further processing.

## 3.3 peek() – View the top element

The `peek()` operation returns the topmost element without removing it.

```java
String topBook = stack.peek();

System.out.println(topBook);
```

Output:

```text
Book 1
```

The Stack remains unchanged.

**Difference between pop() and peek():**

| Operation | Returns top element | Removes element |
|---|---|---|
| `pop()` | Yes | Yes |
| `peek()` | Yes | No |

Remember this difference. It is frequently tested in Java interviews and coding assessments.

---

# 4. Other Important Stack Methods in Java

Java provides several additional methods.

| Method | Description | Example |
|---|---|---|
| `push(element)` | Adds an element | `stack.push("A")` |
| `pop()` | Removes and returns the top element | `stack.pop()` |
| `peek()` | Returns the top without removing it | `stack.peek()` |
| `empty()` | Checks whether Stack is empty | `stack.empty()` |
| `isEmpty()` | Checks whether Stack is empty | `stack.isEmpty()` |
| `search(element)` | Searches for an element | `stack.search("A")` |
| `contains(element)` | Checks whether an element exists | `stack.contains("A")` |
| `size()` | Returns the number of elements | `stack.size()` |
| `clear()` | Removes all elements | `stack.clear()` |

## Example

```java
import java.util.Stack;

public class StackMethods {

    public static void main(String[] args) {

        Stack<String> stack = new Stack<>();

        stack.push("Book 1");
        stack.push("Book 2");
        stack.push("Book 3");

        System.out.println(stack.isEmpty());
        // false

        System.out.println(stack.size());
        // 3

        System.out.println(stack.contains("Book 2"));
        // true

        System.out.println(stack.peek());
        // Book 3

        System.out.println(stack.search("Book 2"));
        // 2

        System.out.println(stack);
        // [Book 1, Book 2, Book 3]

        stack.pop();

        System.out.println(stack);
        // [Book 1, Book 2]
    }
}
```

## Understanding search()

One important thing about `search()` is that it searches from the TOP of the Stack.

It returns the position of an element, starting from 1.

```text
       TOP
        |
    +---------+
    | Book 3  |  Position 1
    +---------+
    | Book 2  |  Position 2
    +---------+
    | Book 1  |  Position 3
    +---------+
```

```java
System.out.println(stack.search("Book 3")); // 1
System.out.println(stack.search("Book 2")); // 2
System.out.println(stack.search("Book 1")); // 3
System.out.println(stack.search("Book 4")); // -1
```

If the element does not exist, `search()` returns `-1`.

Note: `search()` is a method of Java's legacy `Stack` class, not a standard method of the `Deque` interface.

---

# 5. What Happens When Stack Becomes Empty?

Consider:

```java
Stack<String> stack = new Stack<>();

stack.push("Book 1");

System.out.println(stack.pop()); // Book 1

System.out.println(stack.pop()); // Exception!
```

After the first `pop()`, the Stack becomes empty.

Calling `pop()` again throws:

```text
java.util.EmptyStackException
```

Similarly, calling `peek()` on an empty Stack also throws `EmptyStackException`.

## How to avoid this?

Always check whether the Stack contains an element before accessing its top.

```java
if (!stack.isEmpty()) {
    System.out.println(stack.pop());
} else {
    System.out.println("Stack is empty");
}
```

For modern `Deque` implementations, `pop()` and `peek()` also throw exceptions on an empty deque. Their alternatives are `poll()` and `peekFirst()`, which return `null` when empty.

**Interview tip:** Always consider the empty Stack condition when solving Stack problems.

---

# 6. How to Implement Stack in Java?

There are two common ways to use a Stack in Java.

## Approach 1: Using the Stack class

Java provides a built-in `Stack` class.

```java
import java.util.Stack;

public class Main {

    public static void main(String[] args) {

        Stack<Integer> stack = new Stack<>();

        stack.push(10);
        stack.push(20);
        stack.push(30);

        System.out.println(stack);        // [10, 20, 30]
        System.out.println(stack.peek()); // 30

        stack.pop();

        System.out.println(stack);        // [10, 20]
    }
}
```

Syntax:

```java
Stack<DataType> stack = new Stack<>();
```

Examples:

```java
Stack<Integer> numbers = new Stack<>();
Stack<String> books = new Stack<>();
Stack<Character> characters = new Stack<>();
Stack<Double> prices = new Stack<>();
```

You can also store objects:

```java
Stack<Book> books = new Stack<>();

books.push(new Book("Java"));
books.push(new Book("Spring Boot"));
```

For example:

```java
class Book {

    String name;

    Book(String name) {
        this.name = name;
    }
}
```

**Important:** Java Generics work with reference types, so use `Integer`, `Double`, and `Character` instead of primitive types such as `int`, `double`, and `char`.

## Approach 2: Using Deque and ArrayDeque (Recommended)

In modern Java, `Deque` is generally preferred over the legacy `Stack` class.

Why?

- `Stack` extends `Vector`, which is a legacy synchronized collection.
- `Deque` provides efficient operations at both ends.
- `ArrayDeque` is generally faster for single-threaded Stack operations.
- It provides a cleaner and more flexible implementation.

Example:

```java
import java.util.Deque;
import java.util.ArrayDeque;

public class Main {

    public static void main(String[] args) {

        Deque<Integer> stack = new ArrayDeque<>();

        stack.push(10);
        stack.push(20);
        stack.push(30);

        System.out.println(stack);        // [30, 20, 10]

        System.out.println(stack.peek()); // 30

        System.out.println(stack.pop());  // 30

        System.out.println(stack);        // [20, 10]
    }
}
```

Notice that the method names are almost identical!

| Operation | Stack | Deque |
|---|---|---|
| Insert | `push()` | `push()` |
| Remove | `pop()` | `pop()` |
| View top | `peek()` | `peek()` |
| Check empty | `isEmpty()` | `isEmpty()` |
| Size | `size()` | `size()` |
| Search | `search()` | Not directly available |
| Contains | `contains()` | `contains()` |

For new Java development and coding problems, prefer:

```java
Deque<Integer> stack = new ArrayDeque<>();
```

---

# 7. How Does Stack Work Internally?

Understanding the internal working makes Stack problems much easier.

A Stack can be implemented using:

1. An array.
2. A linked list.

Let's understand the array implementation.

## 7.1 Array-based Stack

Imagine an array with a fixed capacity of 5.

```text
Index:    0       1       2       3       4

       +-------+-------+-------+-------+-------+
       |  10   |  20   |  30   |       |       |
       +-------+-------+-------+-------+-------+
                              
                              TOP
```

We maintain a variable called `top` to remember the position of the last inserted element.

Initially:

```java
int top = -1;
```

Why `-1`?

Because no element exists in the Stack.

## 7.2 Push operation

When inserting an element:

1. Check whether the Stack is full.
2. Increase `top`.
3. Insert the element at the new `top` position.

```java
top++;
arr[top] = value;
```

Or simply:

```java
arr[++top] = value;
```

## 7.3 Pop operation

When removing an element:

1. Check whether the Stack is empty.
2. Read the element at `top`.
3. Decrease `top`.
4. Return the removed element.

```java
int value = arr[top];
top--;

return value;
```

Or simply:

```java
return arr[top--];
```

## 7.4 Peek operation

To view the top element:

```java
return arr[top];
```

We do not decrease `top` because we are not removing anything.

## 7.5 Complete Stack implementation using an array

```java
public class MyStack {

    private int[] arr;
    private int top;
    private int capacity;

    public MyStack(int capacity) {
        this.capacity = capacity;
        this.arr = new int[capacity];
        this.top = -1;
    }

    // Insert element
    public void push(int value) {

        if (top == capacity - 1) {
            throw new IllegalStateException("Stack is full");
        }

        arr[++top] = value;
    }

    // Remove and return top element
    public int pop() {

        if (isEmpty()) {
            throw new IllegalStateException("Stack is empty");
        }

        return arr[top--];
    }

    // Return top element without removing
    public int peek() {

        if (isEmpty()) {
            throw new IllegalStateException("Stack is empty");
        }

        return arr[top];
    }

    // Check whether Stack is empty
    public boolean isEmpty() {
        return top == -1;
    }

    // Check whether Stack is full
    public boolean isFull() {
        return top == capacity - 1;
    }

    // Return Stack size
    public int size() {
        return top + 1;
    }
}
```

Usage:

```java
public class Main {

    public static void main(String[] args) {

        MyStack stack = new MyStack(5);

        stack.push(10);
        stack.push(20);
        stack.push(30);

        System.out.println(stack.peek()); // 30

        System.out.println(stack.pop());  // 30

        System.out.println(stack.size()); // 2
    }
}
```

This implementation helps you understand how Stack operations work internally.

---

# 8. Time and Space Complexity

Understanding complexity is important for coding interviews.

For a typical Stack implementation:

| Operation | Time Complexity |
|---|---|
| `push()` | O(1) amortized |
| `pop()` | O(1) |
| `peek()` | O(1) |
| `isEmpty()` | O(1) |
| `size()` | O(1) |
| `contains()` | O(n) |
| `search()` | O(n) |

Why is `contains()` O(n)?

Because the Stack may need to check every element until it finds the requested element.

Why is `push()` O(1) amortized with `ArrayDeque`?

Most insertions take constant time. Occasionally, the internal array needs to grow, which requires copying elements. Across many operations, the average insertion cost remains constant.

## Space Complexity

If a Stack contains `n` elements:

```text
Space Complexity = O(n)
```

Because we need memory to store all `n` elements.

---

# 9. Stack and Memory: Can We Insert 10 Million Elements?

Consider:

```java
Deque<String> stack = new ArrayDeque<>();

for (int i = 0; i < 10_000_000; i++) {
    stack.push("Some book");
}
```

Will this cause an OutOfMemoryError?

Not necessarily.

It depends on:

- Available JVM heap memory.
- Size and type of elements.
- Other objects already occupying memory.
- JVM configuration.

If the Stack requires more memory than the JVM can allocate, it may throw:

```text
java.lang.OutOfMemoryError: Java heap space
```

**Important:** There is no universal maximum number of elements for a Stack. Its practical limit depends on the implementation and available memory.

---

# 10. Real-world Applications of Stack

Stack is not just a theoretical data structure. It is used in many real-world applications.

## 10.1 Undo and Redo Functionality

Think about writing something in a text editor.

You perform these actions:

1. Type "Hello".
2. Type "World".
3. Delete "World".

Now, if you press Undo, the last operation is reversed.

A Stack can store previous operations.

```text
TOP
 |
 +------------------+
 | Delete "World"   |
 +------------------+
 | Type "World"     |
 +------------------+
 | Type "Hello"     |
 +------------------+
```

Undo removes the most recent action.

Redo functionality can use another Stack to track undone operations.

## 10.2 Browser Back and Forward

Imagine visiting:

```text
Google -> YouTube -> GitHub -> StackOverflow
```

When you press Back:

```text
StackOverflow -> GitHub -> YouTube -> Google
```

A browser can use two Stacks:

- Back Stack: Previously visited pages.
- Forward Stack: Pages you moved forward from.

When you navigate to a new page, the Forward Stack is generally cleared.

## 10.3 Function Calls (Call Stack)

This is one of the most important uses of Stack in programming.

Consider:

```java
public class Main {

    static void first() {
        second();
    }

    static void second() {
        third();
    }

    static void third() {
        System.out.println("Hello");
    }

    public static void main(String[] args) {
        first();
    }
}
```

How does Java manage these function calls?

The JVM uses a call stack for each thread.

Conceptually:

```text
        TOP
         |
    +-----------+
    |  third()  |
    +-----------+
    |  second() |
    +-----------+
    |  first()  |
    +-----------+
    |  main()   |
    +-----------+
```

When `third()` completes, its stack frame is removed.

Then `second()` completes, followed by `first()`.

This is another example of LIFO.

## 10.4 Backtracking Algorithms

Stack is very useful in problems where we need to explore possibilities and return to previous decisions.

Examples:

- Maze solving.
- Depth First Search (DFS).
- Generating permutations.
- Exploring paths in a graph.
- Finding possible combinations.

## 10.5 Expression Evaluation

Stacks are used to process mathematical expressions.

Example:

```text
(10 + 20) * 5
```

They are also useful for:

- Infix to Postfix conversion.
- Postfix expression evaluation.
- Checking balanced parentheses.

---

# 11. Important Stack Coding Problems

Understanding the theory is not enough. You should practice implementing Stack operations and solving problems using LIFO.

Here are some important problems, arranged from beginner to interview level.

## Level 1: Beginner Problems

### Problem 1: Reverse a String Using Stack

Input:

```text
HELLO
```

Output:

```text
OLLEH
```

**Logic:**

1. Push every character into the Stack.
2. Pop every character one by one.
3. The resulting order will be reversed.

Java solution:

```java
import java.util.ArrayDeque;
import java.util.Deque;

public class ReverseString {

    public static String reverse(String str) {

        Deque<Character> stack = new ArrayDeque<>();

        // Push all characters
        for (char ch : str.toCharArray()) {
            stack.push(ch);
        }

        StringBuilder result = new StringBuilder();

        // Pop characters in reverse order
        while (!stack.isEmpty()) {
            result.append(stack.pop());
        }

        return result.toString();
    }

    public static void main(String[] args) {

        System.out.println(reverse("HELLO"));
        // OLLEH
    }
}
```

Time Complexity: O(n)

Space Complexity: O(n)

### Problem 2: Check Balanced Parentheses

Input:

```text
{[()]}
```

Output:

```text
true
```

Input:

```text
{[(])}
```

Output:

```text
false
```

**Logic:**

- When an opening bracket appears, push it.
- When a closing bracket appears, check the top element.
- If it matches, pop the opening bracket.
- Otherwise, return false.
- At the end, the Stack must be empty.

Java solution:

```java
import java.util.ArrayDeque;
import java.util.Deque;

public class BalancedParentheses {

    public static boolean isValid(String str) {

        Deque<Character> stack = new ArrayDeque<>();

        for (char ch : str.toCharArray()) {

            if (ch == '(' || ch == '{' || ch == '[') {

                stack.push(ch);

            } else {

                if (stack.isEmpty()) {
                    return false;
                }

                char top = stack.pop();

                if ((ch == ')' && top != '(') ||
                    (ch == '}' && top != '{') ||
                    (ch == ']' && top != '[')) {

                    return false;
                }
            }
        }

        return stack.isEmpty();
    }

    public static void main(String[] args) {

        System.out.println(isValid("{[()]}")); // true
        System.out.println(isValid("{[(])}")); // false
    }
}
```

Time Complexity: O(n)

Space Complexity: O(n)

## Level 2: Intermediate Problems

### Problem 3: Implement Min Stack

Design a Stack that supports:

- `push(value)`
- `pop()`
- `top()`
- `getMin()`

All operations should take O(1) time.

Example:

```text
push(5)
push(3)
push(7)
push(2)

getMin() -> 2

pop()

getMin() -> 3
```

**Hint:** Use two Stacks.

- One Stack stores all values.
- Another Stack tracks the minimum value at each level.

### Problem 4: Next Greater Element

Given an array:

```text
[4, 5, 2, 10, 8]
```

Find the next greater element for every element.

Output:

```text
[5, 10, 10, -1, -1]
```

**Logic:** For every element, find the first greater element to its right.

Hint: Use a Stack to maintain elements whose next greater element has not yet been found.

Expected Time Complexity: O(n).

## Level 3: Advanced Interview Problems

### Problem 5: Largest Rectangle in Histogram

Input:

```text
[2, 1, 5, 6, 2, 3]
```

Output:

```text
10
```

Hint: Use a Monotonic Stack to calculate the maximum rectangle area.

### Problem 6: Daily Temperatures

Input:

```text
[73, 74, 75, 71, 69, 72, 76, 73]
```

Output:

```text
[1, 1, 4, 2, 1, 1, 0, 0]
```

Find how many days we must wait for a warmer temperature.

Hint: Use a Monotonic Decreasing Stack of indices.

### Problem 7: Evaluate Postfix Expression

Input:

```text
"2 3 4 * +"
```

Output:

```text
14
```

Explanation:

```text
3 * 4 = 12
2 + 12 = 14
```

Hint: Push numbers onto the Stack. When an operator appears, pop the required operands, perform the operation, and push the result.

---

# 12. Common Mistakes Beginners Make

| Mistake | Correct Understanding |
|---|---|
| Confusing `pop()` and `peek()` | `pop()` removes; `peek()` only reads |
| Calling `pop()` on an empty Stack | Check `isEmpty()` first |
| Thinking Stack is FIFO | Stack follows LIFO |
| Thinking `search()` starts from the bottom | Java `Stack.search()` counts from the top, starting at 1 |
| Assuming `push()` can never fail | Memory and implementation limits still apply |
| Using `Stack` in every new Java project | Prefer `Deque` with `ArrayDeque` |
| Forgetting to check unmatched brackets | The Stack must be empty at the end of a balanced-parentheses problem |
| Using `ArrayDeque` with null elements | `ArrayDeque` does not allow null elements |

---

# 13. Important Interview Questions

### Q1. What is a Stack?

A Stack is a linear data structure that follows LIFO (Last In, First Out). Elements are inserted and removed from the same end, called the top.

### Q2. What is the difference between Stack and Queue?

| Stack | Queue |
|---|---|
| LIFO | FIFO |
| Insert using push | Insert using offer |
| Remove using pop | Remove using poll |
| Access the top | Access the front |

### Q3. What is the difference between pop() and peek()?

`pop()` removes and returns the top element, whereas `peek()` returns the top element without removing it.

### Q4. Can Stack contain duplicate elements?

Yes. A Stack can contain duplicate values.

```java
Deque<Integer> stack = new ArrayDeque<>();

stack.push(10);
stack.push(20);
stack.push(10);
```

### Q5. Can Stack store different data types?

A generically typed Stack stores elements of its declared type. You can use `Stack<Object>` to store different object types, but this sacrifices compile-time type safety.

### Q6. Is Stack thread-safe in Java?

The legacy `Stack` class inherits synchronized methods from `Vector`. `ArrayDeque` is not thread-safe.

For concurrent access, choose an appropriate concurrent data structure or implement synchronization when necessary.

### Q7. What is Stack Overflow?

Stack Overflow occurs when a thread exhausts its available call-stack memory.

A common example is infinite recursion:

```java
public static void call() {
    call();
}
```

Eventually, Java throws:

```text
java.lang.StackOverflowError
```

Remember the difference:

- `StackOverflowError`: Thread call-stack memory exhausted.
- `OutOfMemoryError`: JVM cannot allocate the required memory.

### Q8. Can we access an element from the middle of a Stack?

The legacy Java `Stack` class inherits indexed access from `Vector`, but this is not a normal Stack operation. In a proper Stack-based algorithm, we should use top-based operations.

### Q9. Can we implement Stack using a Queue?

Yes. A Stack can be implemented using one or two Queues by rearranging elements to maintain LIFO behavior.

### Q10. Which is better: Stack or ArrayDeque?

For most new Java applications, `ArrayDeque` is preferred because it provides efficient Stack operations without the legacy inheritance and synchronization overhead of `Stack`.

---

# 14. Quick Revision Cheat Sheet

| Concept | Remember |
|---|---|
| Data Structure | Linear |
| Principle | LIFO |
| Insertion | Push |
| Removal | Pop |
| Read top | Peek |
| Empty check | isEmpty |
| First inserted | Bottom |
| Last inserted | Top |
| Array implementation | Uses a `top` index |
| Linked List implementation | Uses the head as top |
| Push complexity | O(1) amortized |
| Pop complexity | O(1) |
| Peek complexity | O(1) |
| Search complexity | O(n) |
| Modern Java implementation | `Deque` + `ArrayDeque` |

## Most Important Java Syntax

```java
import java.util.Deque;
import java.util.ArrayDeque;

public class Main {

    public static void main(String[] args) {

        Deque<Integer> stack = new ArrayDeque<>();

        // Insert
        stack.push(10);
        stack.push(20);
        stack.push(30);

        // View top
        System.out.println(stack.peek()); // 30

        // Remove top
        System.out.println(stack.pop());  // 30

        // Check empty
        System.out.println(stack.isEmpty()); // false

        // Stack size
        System.out.println(stack.size()); // 2
    }
}
```

---

# 15. Practice Roadmap

Follow this order to become comfortable with Stack problems.

- [ ] Understand LIFO and the three basic operations.
- [ ] Implement Stack using an array.
- [ ] Implement Stack using a Linked List.
- [ ] Practice Java `Stack` and `ArrayDeque`.
- [ ] Reverse a String using Stack.
- [ ] Check Balanced Parentheses.
- [ ] Implement Min Stack.
- [ ] Solve Next Greater Element.
- [ ] Solve Daily Temperatures.
- [ ] Solve Largest Rectangle in Histogram.
- [ ] Understand Monotonic Stack.
- [ ] Solve Stack-based DFS and backtracking problems.

---

## Final Takeaway

A Stack is one of the simplest yet most powerful data structures.

Whenever a problem involves:

- Processing elements in reverse order.
- Remembering previous operations.
- Returning to a previous state.
- Matching opening and closing brackets.
- Finding the next greater or smaller element.
- Exploring a path and backtracking.

Think about whether a Stack can solve the problem.

Once you understand `push()`, `pop()`, and `peek()`, you have understood the foundation of Stack. The remaining challenge is recognizing when to apply it in a problem.