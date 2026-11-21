---
title: "Big-O Notation"
layout: default
parent: Data Structures & Algorithms
nav_order: 1
permalink: /data-structures/Big-O-Notation/big-o-notation/

topic: Big-O Notation
section: Algorithm Analysis
concepts: [time-complexity, space-complexity, asymptotic-analysis]
complexities: [O(1), O(log n), O(n), O(n log n), O(n^2), O(2^n)]
tags: [data-structures, algorithms, big-o, time-complexity, space-complexity]
---

# Big-O Notation

## 1. Introduction

Imagine two students are given the same task:

**Find a particular name from a list of 10 students.**

- Student A checks every name one by one.
- Student B uses an already sorted list and repeatedly eliminates half of the remaining names.

Both students can find the answer, but they use different approaches.

Now imagine the list contains:

- 10 students
- 100 students
- 1,000 students
- 1,000,000 students

Which approach will work better as the list grows?

This is the kind of question that **Big-O Notation** helps us answer.

### What is Big-O Notation?

Big-O notation describes how an algorithm's time or space requirements grow as the input size increases.

It helps us compare algorithms without depending on:

- A particular computer.
- A programming language.
- A processor speed.
- The exact execution time in milliseconds.

Instead, we study how the algorithm behaves when the input becomes larger.

**In simple words: Big-O tells us how an algorithm scales.**

---

## 2. Big-O Complexity Chart

Before learning the details, understand the common complexity classes.

![Big O Complexity Chart]({{ '/00-Data-Structure/02-Big-O-Notation/big-o-complexity-chart.png' | relative_url }})

The chart compares how different complexity functions grow as the input size increases.

Let's understand each one.

### 2.1 O(1) – Constant Time

The execution time does not depend on the input size.

Example:

```java
int[] numbers = {10, 20, 30, 40, 50};

System.out.println(numbers[2]);
```

We access the element directly using its index.

Whether the Array contains 5 elements or 5 million elements, accessing an element by a valid index takes constant time.

**Think about it:** If you know the exact seat number in a classroom, do you need to check every seat to find that student?

No. You can go directly to the required seat.

Examples:

- Accessing an Array element by index.
- Reading the top element of a Stack.
- Reading the front element of a Queue.
- Checking whether an integer variable is greater than zero.

### 2.2 O(log n) – Logarithmic Time

The problem becomes significantly smaller after every operation, often by dividing the remaining input in half.

Example: Binary Search.

Imagine finding a number in a sorted list of 1,000 numbers.

Instead of checking every number, we:

1. Check the middle element.
2. Decide whether the required number is on the left or right.
3. Ignore the half that cannot contain the answer.
4. Repeat.

The search space becomes:

```text
1000 elements
      |
      v
  500 elements
      |
      v
  250 elements
      |
      v
  125 elements
      |
      v
  ...
```

We need only about 10 divisions to reduce 1,000 elements to one.

Examples:

- Binary Search.
- Searching in a balanced Binary Search Tree.
- Insertion and deletion in balanced trees.
- Operations in a Skip List on average.

**Important:** Binary Search requires sorted data and efficient access to the middle element. It is not automatically O(log n) on every data structure.

### 2.3 O(n) – Linear Time

Execution time grows proportionally with the input size.

Example:

```java
int[] numbers = {10, 20, 30, 40, 50};

for (int number : numbers) {
    System.out.println(number);
}
```

If there are 5 elements, the loop executes 5 times.

If there are 1,000 elements, it executes 1,000 times.

Examples:

- Traversing an Array.
- Searching an unsorted Array.
- Finding the maximum element.
- Counting elements in a collection by traversal.

### 2.4 O(n log n) – Linearithmic Time

This complexity commonly appears in efficient sorting algorithms.

Examples:

- Merge Sort.
- Heap Sort.
- Average-case Quick Sort.
- TimSort for general inputs.

For example, Merge Sort repeatedly divides an Array into smaller parts and then merges the sorted parts.

It combines:

- Approximately log n levels of division.
- O(n) work at each level.

Therefore:

\[
O(n \log n)
\]

### 2.5 O(n²) – Quadratic Time

Execution time grows approximately with the square of the input size.

Example:

```java
int[] numbers = {10, 20, 30, 40, 50};

for (int i = 0; i < numbers.length; i++) {

    for (int j = 0; j < numbers.length; j++) {

        System.out.println(numbers[i] + ", " + numbers[j]);
    }
}
```

For 5 elements:

\[
5 \times 5 = 25
\]

For 100 elements:

\[
100 \times 100 = 10,000
\]

Examples:

- Bubble Sort.
- Selection Sort.
- Insertion Sort in the average and worst cases.
- Comparing every pair of elements.

### 2.6 O(2ⁿ) – Exponential Time

The amount of work approximately doubles whenever the input size increases by one.

Example: Generating all possible subsets of a set.

For 3 elements:

\[
2^3 = 8
\]

For 5 elements:

\[
2^5 = 32
\]

For 20 elements:

\[
2^{20} = 1,048,576
\]

Examples:

- Generating all subsets.
- Some brute-force recursive solutions.
- Certain recursive algorithms that explore two choices at every step.

### 2.7 O(n!) – Factorial Time

The number of possible arrangements grows extremely quickly.

Example: Finding every possible ordering of 5 different objects.

\[
5! = 5 \times 4 \times 3 \times 2 \times 1 = 120
\]

For 10 objects:

\[
10! = 3,628,800
\]

Examples:

- Generating all permutations.
- Brute-force solutions to some Traveling Salesperson Problem instances.

---

## 3. Comparing Common Time Complexities

The following table shows the general growth order, from slower growth to faster growth.

| Complexity | Name | Example |
|---|---|---|
| O(1) | Constant | Array index access |
| O(log n) | Logarithmic | Binary Search |
| O(n) | Linear | Array traversal |
| O(n log n) | Linearithmic | Merge Sort |
| O(n²) | Quadratic | Bubble Sort |
| O(n³) | Cubic | Three nested loops |
| O(2ⁿ) | Exponential | Subset generation |
| O(n!) | Factorial | Permutation generation |

For sufficiently large input sizes, the general growth order is:

\[
O(1) < O(\log n) < O(n) < O(n\log n)
\]

\[
< O(n^2) < O(n^3) < O(2^n) < O(n!)
\]

This is a growth comparison, not a guarantee that every O(n) implementation will run faster than every O(log n) implementation for small inputs. Actual performance also depends on constants, memory access, and implementation details.

---

## 4. Understanding Big-O, Big-Theta and Big-Omega

These three notations are used to describe algorithmic complexity.

### 4.1 Big-O – Upper Bound

Big-O describes an asymptotic upper bound on an algorithm's growth.

It is commonly used when discussing worst-case complexity.

Example:

```java
for (int i = 0; i < n; i++) {
    System.out.println(i);
}
```

The loop executes n times.

Its complexity is:

\[
O(n)
\]

### 4.2 Big-Omega (Ω) – Lower Bound

Big-Omega describes an asymptotic lower bound.

For example, an algorithm that checks the first element and immediately finds the answer may take constant time.

For Linear Search:

- Best case: Ω(1), when the first element matches.
- Worst case: O(n), when the element is last or absent.

### 4.3 Big-Theta (Θ) – Tight Bound

Big-Theta describes a tight asymptotic bound: the function grows at the same order from both above and below.

For a loop that always executes exactly n times:

\[
\Theta(n)
\]

### Important Difference

| Notation | Meaning |
|---|---|
| O(f(n)) | Asymptotic upper bound |
| Ω(f(n)) | Asymptotic lower bound |
| Θ(f(n)) | Asymptotically tight bound |

Do not assume Big-O always means worst case. Big-O is mathematically an upper bound, although it is commonly used to report worst-case algorithm complexity.

---

## 5. How to Calculate Big-O

Let's learn some simple rules.

### Rule 1: Ignore Constants

Example:

```java
for (int i = 0; i < n; i++) {
    System.out.println(i);
}

for (int i = 0; i < n; i++) {
    System.out.println(i);
}
```

The first loop executes n times.

The second loop executes n times.

Total operations:

\[
n+n=2n
\]

We ignore the constant factor.

Therefore:

\[
O(2n)=O(n)
\]

### Rule 2: Ignore Smaller Terms

Example:

```java
for (int i = 0; i < n; i++) {
    System.out.println(i);
}

for (int i = 0; i < n * n; i++) {
    System.out.println(i);
}
```

Total:

\[
n+n^2
\]

For large n, the quadratic term dominates.

Therefore:

\[
O(n+n^2)=O(n^2)
\]

### Rule 3: Consecutive Loops Are Added

```java
for (int i = 0; i < n; i++) {
    System.out.println(i);
}

for (int j = 0; j < n; j++) {
    System.out.println(j);
}
```

Complexity:

\[
O(n)+O(n)=O(2n)=O(n)
\]

### Rule 4: Nested Loops Are Multiplied

```java
for (int i = 0; i < n; i++) {

    for (int j = 0; j < n; j++) {

        System.out.println(i + ", " + j);
    }
}
```

The outer loop executes n times.

For every outer iteration, the inner loop executes n times.

Total:

\[
n \times n=n^2
\]

Complexity:

\[
O(n^2)
\]

### Rule 5: Different Input Sizes Must Be Considered

```java
void printElements(int[] first, int[] second) {

    for (int value : first) {
        System.out.println(value);
    }

    for (int value : second) {
        System.out.println(value);
    }
}
```

Let:

- n = size of the first Array.
- m = size of the second Array.

Complexity:

\[
O(n+m)
\]

We cannot simply write O(n) unless we know that both inputs are represented by the same size variable or have an appropriate relationship.

### Rule 6: Halving the Input Often Produces Logarithmic Complexity

```java
int number = n;

while (number > 1) {

    number = number / 2;
}
```

The values become:

```text
n
n/2
n/4
n/8
n/16
...
1
```

The number of iterations is approximately:

\[
\log_2 n
\]

Therefore:

\[
O(\log n)
\]

---

## 6. Common Data Structure Operations

![Common Data Structure Operations]({{ '/00-Data-Structure/02-Big-O-Notation/common-data-structure-operations.png' | relative_url }})

The following table provides a practical comparison of common data structures.

The exact complexity depends on the implementation and the operation being performed. The table uses typical assumptions explained below.

### 6.1 Data Structure Complexity Table

| Data Structure | Access | Search | Insertion | Deletion |
|---|---:|---:|---:|---:|
| Array | O(1) | O(n) | O(n) | O(n) |
| Dynamic Array / ArrayList | O(1) | O(n) | Amortized O(1)* | O(n) |
| Stack | O(n)** | O(n)** | O(1) | O(1) |
| Queue | O(n)** | O(n)** | O(1) | O(1) |
| Singly Linked List | O(n) | O(n) | O(1)*** | O(1)*** |
| Doubly Linked List | O(n) | O(n) | O(1)*** | O(1)*** |
| Skip List | O(log n) expected | O(log n) expected | O(log n) expected | O(log n) expected |
| Hash Table | N/A | O(1) average | O(1) average | O(1) average |
| Binary Search Tree | O(log n) average**** | O(log n) average**** | O(log n) average**** | O(log n) average**** |
| AVL Tree | O(log n) | O(log n) | O(log n) | O(log n) |
| Red-Black Tree | O(log n) | O(log n) | O(log n) | O(log n) |
| B-Tree | O(log n) | O(log n) | O(log n) | O(log n) |
| Splay Tree | O(log n) amortized | O(log n) amortized | O(log n) amortized | O(log n) amortized |
| KD Tree | O(log n) typical***** | O(log n) typical***** | Depends on variant | Depends on variant |

Notes:

- * ArrayList append is amortized O(1), but an individual append that triggers resizing is O(n). Inserting at an arbitrary index is O(n).
- ** Stack and Queue are designed for restricted access. Reading or searching for an arbitrary element requires traversal in typical implementations. Their normal insertion and removal operations at the permitted ends are O(1).
- *** Linked-list insertion and deletion are O(1) when the required node or insertion position is already known. Finding that position may take O(n).
- **** An ordinary Binary Search Tree can become unbalanced. Its worst-case search, insertion, and deletion complexity is O(n).
- ***** KD-tree complexity depends on dimensionality, distribution, balancing, and the type of query. Worst-case searches can be O(n).

### 6.2 Average vs Worst Case

Consider searching for an element in a Hash Table.

In the average case:

\[
O(1)
\]

But if many keys collide into the same bucket, performance can degrade.

In the worst case, searching may take:

\[
O(n)
\]

This is why average-case and worst-case complexity should be considered separately.

### 6.3 Space Complexity of Common Data Structures

| Data Structure | Typical Space Complexity |
|---|---:|
| Array | O(n) |
| Dynamic Array / ArrayList | O(n) |
| Stack | O(n) |
| Queue | O(n) |
| Singly Linked List | O(n) |
| Doubly Linked List | O(n) |
| Skip List | O(n) expected |
| Hash Table | O(n) |
| Binary Search Tree | O(n) |
| AVL Tree | O(n) |
| Red-Black Tree | O(n) |
| B-Tree | O(n) |
| KD Tree | O(n) |

Space complexity describes how much memory is required as the number of stored elements grows.

For a collection containing n elements, most of these data structures require O(n) storage.

The actual memory usage differs because nodes, references, arrays, balancing information, and unused capacity all contribute to memory consumption.

---

## 7. Array Sorting Algorithms

Sorting means arranging elements in a particular order, usually ascending or descending.

For example:

```text
Before:

[40, 10, 30, 20, 50]

After:

[10, 20, 30, 40, 50]
```

Different sorting algorithms use different strategies and have different time and space requirements.

![Array Sorting Algorithms]({{ '/00-Data-Structure/02-Big-O-Notation/array-sorting-algorithms.png' | relative_url }})


### 7.1 Sorting Complexity Table

| Algorithm | Best Case | Average Case | Worst Case | Auxiliary Space |
|---|---:|---:|---:|---:|
| Quick Sort | O(n log n) | O(n log n) | O(n²) | O(log n) average, O(n) worst |
| Merge Sort | O(n log n) | O(n log n) | O(n log n) | O(n) |
| TimSort | O(n) | O(n log n) | O(n log n) | O(n) |
| Heap Sort | O(n log n) | O(n log n) | O(n log n) | O(1) |
| Bubble Sort | O(n)* | O(n²) | O(n²) | O(1) |
| Insertion Sort | O(n) | O(n²) | O(n²) | O(1) |
| Selection Sort | O(n²) | O(n²) | O(n²) | O(1) |
| Tree Sort | O(n log n) expected | O(n log n) expected | O(n²) | O(n) |
| Shell Sort | Depends on gap sequence | Depends on gap sequence | Depends on gap sequence | O(1) |
| Bucket Sort | O(n + k) under suitable assumptions | O(n + k) expected under suitable distribution | Can be O(n²) | O(n + k) |
| Radix Sort | O(d(n + b)) | O(d(n + b)) | O(d(n + b)) | O(n + b) |
| Counting Sort | O(n + k) | O(n + k) | O(n + k) | O(n + k) |
| Cube Sort | O(n) | O(n log n) | O(n log n) | O(n) |

Notes:

- * Bubble Sort achieves O(n) best-case time when implemented with an optimization that detects whether any swaps occurred.
- `n` = number of elements.
- `k` = range of values or number of buckets, depending on the algorithm.
- `d` = number of digits or passes in Radix Sort.
- `b` = number of possible values per digit, also called the radix or digit base.
- Sorting space refers to auxiliary memory, excluding the input Array. Recursive call stacks are included where relevant.
- Shell Sort complexity depends heavily on the chosen gap sequence. There is no single complexity that applies to all implementations.
- Tree Sort's average complexity assumes a reasonably balanced search tree; an ordinary unbalanced BST can produce O(n²) time.
- Bucket Sort's expected performance depends on the input distribution and the method used to sort individual buckets.
- Cube Sort is a specialized sorting algorithm and is less commonly used than the standard algorithms in this table.

### 7.2 Bubble Sort – O(n²)

Bubble Sort repeatedly compares adjacent elements and swaps them if they are in the wrong order.

```java
public static void bubbleSort(int[] arr) {

    int n = arr.length;

    for (int i = 0; i < n - 1; i++) {

        boolean swapped = false;

        for (int j = 0; j < n - i - 1; j++) {

            if (arr[j] > arr[j + 1]) {

                int temp = arr[j];
                arr[j] = arr[j + 1];
                arr[j + 1] = temp;

                swapped = true;
            }
        }

        if (!swapped) {
            break;
        }
    }
}
```

Complexity:

- Best: O(n), if the Array is already sorted.
- Average: O(n²).
- Worst: O(n²).
- Auxiliary space: O(1).

### 7.3 Selection Sort – O(n²)

Selection Sort finds the smallest element and places it at the beginning.

It repeats this process for the remaining unsorted elements.

```java
public static void selectionSort(int[] arr) {

    int n = arr.length;

    for (int i = 0; i < n - 1; i++) {

        int minIndex = i;

        for (int j = i + 1; j < n; j++) {

            if (arr[j] < arr[minIndex]) {
                minIndex = j;
            }
        }

        int temp = arr[i];
        arr[i] = arr[minIndex];
        arr[minIndex] = temp;
    }
}
```

Complexity:

- Best: O(n²).
- Average: O(n²).
- Worst: O(n²).
- Auxiliary space: O(1).

### 7.4 Insertion Sort – O(n²)

Insertion Sort builds a sorted portion of the Array one element at a time.

It is similar to arranging playing cards in your hand.

```java
public static void insertionSort(int[] arr) {

    for (int i = 1; i < arr.length; i++) {

        int key = arr[i];
        int j = i - 1;

        while (j >= 0 && arr[j] > key) {

            arr[j + 1] = arr[j];
            j--;
        }

        arr[j + 1] = key;
    }
}
```

Complexity:

- Best: O(n), when already sorted.
- Average: O(n²).
- Worst: O(n²).
- Auxiliary space: O(1).

### 7.5 Merge Sort – O(n log n)

Merge Sort uses the Divide and Conquer approach.

It:

1. Divides the Array into smaller parts.
2. Sorts each part recursively.
3. Merges the sorted parts.

Complexity:

- Best: O(n log n).
- Average: O(n log n).
- Worst: O(n log n).
- Auxiliary space: O(n).

### 7.6 Quick Sort – Average O(n log n)

Quick Sort selects a pivot and partitions the Array into elements smaller and larger than the pivot.

It then sorts the partitions recursively.

Complexity:

- Best: O(n log n).
- Average: O(n log n).
- Worst: O(n²).
- Auxiliary space: O(log n) average recursive stack, O(n) worst case.

The worst case can happen when partitioning repeatedly produces extremely unbalanced partitions.

### 7.7 Heap Sort – O(n log n)

Heap Sort uses a Binary Heap.

It:

1. Builds a heap.
2. Extracts the maximum or minimum element.
3. Places it in the correct position.
4. Repeats until sorted.

Complexity:

- Best: O(n log n).
- Average: O(n log n).
- Worst: O(n log n).
- Auxiliary space: O(1) for an in-place implementation.

### 7.8 TimSort

TimSort is a hybrid sorting algorithm based on Merge Sort and Insertion Sort.

It identifies existing ordered sequences called runs and uses them to improve sorting performance.

Complexity:

- Best: O(n), for already ordered or favorable inputs.
- Average: O(n log n).
- Worst: O(n log n).
- Auxiliary space: O(n).

It is used in Java's object-array sorting and in Python's sorting implementation.

---

## 8. Time Complexity vs Space Complexity

An algorithm can be fast but require more memory. Another algorithm may use less memory but take more time.

We need to consider both.

### Example 1: Creating a New Array

```java
int[] original = {10, 20, 30, 40, 50};

int[] copy = new int[original.length];

for (int i = 0; i < original.length; i++) {
    copy[i] = original[i];
}
```

Time Complexity:

\[
O(n)
\]

Space Complexity:

\[
O(n)
\]

We copy n elements and allocate another Array of size n.

### Example 2: Reversing an Array In Place

```java
public static void reverse(int[] arr) {

    int left = 0;
    int right = arr.length - 1;

    while (left < right) {

        int temp = arr[left];

        arr[left] = arr[right];
        arr[right] = temp;

        left++;
        right--;
    }
}
```

Time Complexity:

\[
O(n)
\]

Space Complexity:

\[
O(1)
\]

We use only a fixed number of extra variables.

**Important:** O(1) auxiliary space does not mean the algorithm uses no memory. It means its additional memory does not grow with the input size.

---

## 9. How to Calculate Space Complexity

Space complexity measures how much additional memory an algorithm needs as input size grows.

### Example 1: Constant Space – O(1)

```java
public static int sum(int[] arr) {

    int total = 0;

    for (int number : arr) {
        total += number;
    }

    return total;
}
```

We use one variable, `total`, regardless of the Array size.

Auxiliary space:

\[
O(1)
\]

### Example 2: Linear Space – O(n)

```java
public static int[] copyArray(int[] arr) {

    int[] copy = new int[arr.length];

    for (int i = 0; i < arr.length; i++) {
        copy[i] = arr[i];
    }

    return copy;
}
```

We create another Array with n elements.

Auxiliary space:

\[
O(n)
\]

### Example 3: Quadratic Space – O(n²)

```java
int[][] matrix = new int[n][n];
```

The matrix contains n × n elements.

Space complexity:

\[
O(n^2)
\]

### Example 4: Recursive Space

```java
public static void countDown(int n) {

    if (n == 0) {
        return;
    }

    countDown(n - 1);
}
```

Each recursive call uses stack space.

The maximum recursion depth is n.

Space complexity:

\[
O(n)
\]

---

## 10. Common Big-O Patterns in Java

| Code Pattern | Complexity |
|---|---|
| A fixed number of statements | O(1) |
| One loop from 0 to n | O(n) |
| Two separate loops from 0 to n | O(n) |
| Two nested loops from 0 to n | O(n²) |
| Three nested loops from 0 to n | O(n³) |
| Loop that repeatedly divides n by 2 | O(log n) |
| Loop over n with an inner loop over m | O(n × m) |
| Recursive call with n decreasing by 1 | Often O(n), depending on work per call |
| Recursive calls to two subproblems of size n/2 plus linear merging | Often O(n log n) |

Always inspect what the code actually does. The presence of a loop or recursion alone does not determine its complexity.

---

## 11. Practical Example: Finding a Number

Suppose we have:

```java
int[] numbers = {10, 20, 30, 40, 50, 60};
```

We want to find `50`.

### Approach 1: Linear Search

```java
public static int linearSearch(int[] arr, int target) {

    for (int i = 0; i < arr.length; i++) {

        if (arr[i] == target) {
            return i;
        }
    }

    return -1;
}
```

The algorithm checks elements one by one.

Complexity:

- Best: O(1).
- Average: O(n).
- Worst: O(n).

### Approach 2: Binary Search

The Array must be sorted.

```java
public static int binarySearch(int[] arr, int target) {

    int left = 0;
    int right = arr.length - 1;

    while (left <= right) {

        int mid = left + (right - left) / 2;

        if (arr[mid] == target) {
            return mid;
        }

        if (arr[mid] < target) {
            left = mid + 1;
        } else {
            right = mid - 1;
        }
    }

    return -1;
}
```

Complexity:

- Best: O(1).
- Average: O(log n).
- Worst: O(log n).

### Which Is Better?

For a large sorted Array, Binary Search usually requires fewer comparisons.

However, if the data is unsorted, sorting it first has a cost. For a single search, Linear Search may be more appropriate.

Algorithm selection depends on the complete problem, not only the complexity of one operation.

---

## 12. Why Big-O Matters in Real Applications

Imagine a banking application processing customer transactions.

It starts with 100 transactions per day.

Later, it handles:

- 10,000 transactions per day.
- 1 million transactions per day.
- 100 million transactions per day.

An inefficient algorithm may work perfectly with 100 records but become extremely slow when the data grows.

Big-O helps developers:

1. Choose appropriate data structures.
2. Compare different algorithms.
3. Identify performance bottlenecks.
4. Estimate scalability.
5. Reduce unnecessary memory usage.
6. Design systems that handle larger workloads.

For example:

- Use HashMap for average O(1) key lookup.
- Use ArrayList for efficient indexed access and appending.
- Use a balanced TreeMap when sorted keys and logarithmic operations are needed.
- Use a PriorityQueue when repeatedly retrieving the highest- or lowest-priority element.
- Use Binary Search when searching sorted, index-accessible data.

---

## 13. Common Mistakes When Calculating Big-O

### Mistake 1: Counting Every Statement Separately

```java
int a = 10;
int b = 20;
int c = a + b;
```

There are three statements, but the complexity is O(1), not O(3).

### Mistake 2: Assuming Every Nested Loop Is O(n²)

```java
for (int i = 0; i < n; i++) {

    for (int j = 0; j < 10; j++) {
        System.out.println(i + j);
    }
}
```

The inner loop always executes 10 times.

Total:

\[
10n
\]

Complexity:

\[
O(n)
\]

### Mistake 3: Ignoring Different Input Sizes

```java
for (int i = 0; i < n; i++) {
    // ...
}

for (int j = 0; j < m; j++) {
    // ...
}
```

Correct complexity:

\[
O(n+m)
\]

### Mistake 4: Assuming HashMap Is Always O(1)

HashMap operations are generally O(1) on average, but collisions and other implementation details can affect performance. The worst case is not universally O(1).

### Mistake 5: Assuming LinkedList Insertion Is Always O(1)

Finding the correct position may require O(n) traversal.

Only the pointer update is O(1) when the relevant node or position is already known.

### Mistake 6: Ignoring Recursion Space

Recursive algorithms use the call stack.

Even if the algorithm uses only a few local variables per call, a recursion depth of n can require O(n) stack space.

### Mistake 7: Treating Big-O as Actual Execution Time

O(n) does not tell us whether a program takes 1 millisecond or 10 seconds.

It describes growth, not a precise runtime.

---

## 14. Interview Questions

**Q1. What is Big-O notation?**

Big-O notation describes an asymptotic upper bound on the growth of an algorithm's resource usage, commonly time or space, as input size increases.

**Q2. What is the difference between O(1) and O(n)?**

O(1) is constant growth. O(n) grows proportionally to the input size.

**Q3. What is the time complexity of accessing an Array element?**

O(1), because indexed access is direct.

**Q4. What is the time complexity of searching an unsorted Array?**

O(n) in the worst case, because every element may need to be checked.

**Q5. What is the complexity of Binary Search?**

O(log n) in the average and worst cases, assuming sorted data and efficient middle-element access.

**Q6. What is the complexity of two nested loops?**

If both loops execute n times, O(n²).

**Q7. What is amortized time complexity?**

Amortized analysis calculates the average cost per operation over a sequence of operations, even if individual operations occasionally take longer.

For example, ArrayList append is amortized O(1), although resizing can make an individual insertion O(n).

**Q8. What is the difference between time and space complexity?**

Time complexity describes how the number of operations grows. Space complexity describes how memory usage grows.

**Q9. Which sorting algorithm has O(n log n) worst-case time and O(1) auxiliary space?**

Heap Sort.

**Q10. Which sorting algorithm is stable and has O(n log n) worst-case time?**

Merge Sort and TimSort are common examples of stable sorting algorithms.

**Q11. What is the worst-case complexity of Quick Sort?**

O(n²), when partitioning repeatedly creates highly unbalanced partitions.

**Q12. Why is ArrayList insertion at the end amortized O(1)?**

Most insertions use an available position. Occasionally, resizing requires copying existing elements, but the cost averaged across many insertions remains constant per insertion.

**Q13. Can an O(n²) algorithm be faster than an O(n log n) algorithm?**

Yes. For small input sizes, implementation simplicity, constants, memory access, and overhead can make a quadratic algorithm faster.

**Q14. Does O(n) always mean the algorithm takes longer than O(log n)?**

No. Big-O compares asymptotic growth as input becomes large. It does not provide exact execution times for small inputs.

**Q15. What is the difference between average-case and worst-case complexity?**

Average-case complexity describes expected performance under a specified input distribution. Worst-case complexity describes the maximum growth for the most unfavorable valid input.

---

## 15. Practice Problems

Try calculating the complexity of each example before checking the answers.

### Problem 1

```java
for (int i = 0; i < n; i++) {
    System.out.println(i);
}
```

### Problem 2

```java
for (int i = 0; i < n; i++) {

    for (int j = 0; j < n; j++) {

        System.out.println(i + j);
    }
}
```

### Problem 3

```java
for (int i = 0; i < n; i++) {
    System.out.println(i);
}

for (int j = 0; j < n * n; j++) {
    System.out.println(j);
}
```

### Problem 4

```java
int i = n;

while (i > 1) {
    i = i / 2;
}
```

### Problem 5

```java
for (int i = 0; i < n; i++) {

    for (int j = 0; j < 10; j++) {

        System.out.println(i + j);
    }
}
```

### Problem 6

```java
for (int i = 1; i < n; i = i * 2) {
    System.out.println(i);
}
```

### Problem 7

```java
for (int i = 0; i < n; i++) {

    for (int j = 0; j < i; j++) {

        System.out.println(i + j);
    }
}
```

### Answers

| Problem | Complexity | Reason |
|---|---|---|
| 1 | O(n) | One loop |
| 2 | O(n²) | Two nested loops |
| 3 | O(n²) | O(n) + O(n²) |
| 4 | O(log n) | Divides input by 2 |
| 5 | O(n) | Inner loop is constant |
| 6 | O(log n) | Multiplies by 2 |
| 7 | O(n²) | Total iterations are approximately n(n-1)/2 |

---

## 16. Quick Revision Cheat Sheet

### Time Complexity

| Complexity | Name | Typical Example |
|---|---|---|
| O(1) | Constant | Array access |
| O(log n) | Logarithmic | Binary Search |
| O(n) | Linear | Array traversal |
| O(n log n) | Linearithmic | Merge Sort |
| O(n²) | Quadratic | Bubble Sort |
| O(n³) | Cubic | Three nested loops |
| O(2ⁿ) | Exponential | Subset generation |
| O(n!) | Factorial | Permutations |

### Important Data Structure Complexities

| Data Structure | Important Operation | Complexity |
|---|---|---:|
| Array | Access | O(1) |
| ArrayList | Append | Amortized O(1) |
| LinkedList | Insert at known node | O(1) |
| Stack | Push / Pop | O(1) |
| Queue | Enqueue / Dequeue | O(1) |
| HashMap | Get / Put | O(1) average |
| Binary Search Tree | Search | O(log n) average, O(n) worst |
| AVL Tree | Search | O(log n) |
| PriorityQueue | Offer / Poll | O(log n) |
| PriorityQueue | Peek | O(1) |

### Important Sorting Complexities

| Algorithm | Average Time | Worst Time |
|---|---:|---:|
| Bubble Sort | O(n²) | O(n²) |
| Selection Sort | O(n²) | O(n²) |
| Insertion Sort | O(n²) | O(n²) |
| Merge Sort | O(n log n) | O(n log n) |
| Quick Sort | O(n log n) | O(n²) |
| Heap Sort | O(n log n) | O(n log n) |
| TimSort | O(n log n) | O(n log n) |

---

## 17. Final Takeaway

Big-O notation is not about counting the exact number of seconds an algorithm takes.

It is about understanding what happens when the input becomes larger.

Remember these three questions whenever you write an algorithm:

1. **Time:** How does the number of operations grow as the input increases?
2. **Space:** How much additional memory does the algorithm need?
3. **Scalability:** Will the algorithm still perform well when the input becomes much larger?

A good programmer does not just ask whether an algorithm works.

A good programmer also asks:

**Will it still work efficiently when the data grows from 10 elements to 10 million elements?**