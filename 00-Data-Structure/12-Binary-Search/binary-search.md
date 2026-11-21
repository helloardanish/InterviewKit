---
title: "Binary Search"
layout: default
parent: Data Structures & Algorithms
nav_order: 11
permalink: /data-structures/Binary-Search/binary-search/

topic: Binary Search
section: Searching Algorithms
concepts: [sorted-data, divide-and-conquer, search-space]
variants: [iterative, recursive, lower-bound, upper-bound]
complexities: [O(1), O(log n)]
tags: [algorithms, searching, binary-search, divide-and-conquer, java]
---

# Binary Search

## 1. What is Binary Search?

**Binary Search** is an efficient searching algorithm that finds an element by repeatedly dividing the search space into two halves.

Instead of checking every element one by one, Binary Search checks the middle element and decides which half could contain the target.

It eliminates half of the remaining search space after every comparison.

**Important condition:** Binary Search requires the data to be sorted (or, more generally, the search space to have a suitable monotonic property).

### Real-world example: Finding a word in a dictionary

Imagine you want to find the word "Mango" in a dictionary containing thousands of words.

Would you start from the first page and check every word?

Probably not!

Instead:

1. Open the dictionary somewhere near the middle.
2. Check the word on that page.
3. If the word comes alphabetically after "Mango", search the left half.
4. If the word comes before "Mango", search the right half.
5. Repeat until you find the word.

This is the basic idea of Binary Search.

**The important idea:** Each comparison helps eliminate approximately half of the remaining possibilities.

![Binary Search]({{ '/00-Data-Structure/12-Binary-Search/binary-search.png' | relative_url }})

## 2. How Does Binary Search Work?

Consider this sorted array:

```java
int[] numbers = {10, 20, 30, 40, 50, 60, 70, 80, 90};
```

Suppose we want to find `70`.

We maintain three variables:

- `low`: Starting index of the search range.
- `high`: Ending index of the search range.
- `mid`: Middle index of the current search range.

The middle index is calculated as:

```java
int mid = low + (high - low) / 2;
```

### Step-by-step execution

**Initial values:**

```text
low = 0
high = 8
target = 70
```

**Step 1: Find the middle element**

```text
Array:  [10, 20, 30, 40, 50, 60, 70, 80, 90]
Index:    0   1   2   3   4   5   6   7   8
                         ^
                       mid = 4
```

Middle element = `50`

Compare:

```text
50 < 70
```

The target must be on the right side.

Discard the left half, including index 4.

Update:

```java
low = mid + 1;
```

Now:

```text
low = 5
high = 8
```

**Step 2: Search the right half**

```text
Array:  [10, 20, 30, 40, 50, 60, 70, 80, 90]
Index:                        5   6   7   8
                                  ^
                                mid = 6
```

Middle element = `70`

Compare:

```text
70 == 70
```

<escape>```text
Target found at index 6!
```</escape>

**Total comparisons: 2**

Notice that we searched only two elements instead of checking all nine elements.

## 3. Binary Search Algorithm

### Pseudocode

```text
START

Given a sorted array and a target

low = 0
high = array.length - 1

WHILE low <= high:

    mid = low + (high - low) / 2

    IF array[mid] == target:
        RETURN mid

    ELSE IF array[mid] < target:
        low = mid + 1

    ELSE:
        high = mid - 1

RETURN -1

END
```

### Understanding the three conditions

| Condition | Meaning | Action |
|---|---|---|
| `arr[mid] == target` | Target found | Return `mid` |
| `arr[mid] < target` | Target may be on right | `low = mid + 1` |
| `arr[mid] > target` | Target may be on left | `high = mid - 1` |

Why do we use `mid + 1` and `mid - 1`?

Because we have already checked the middle element. There is no reason to include it again.

## 4. Java Implementation

### Approach 1: Iterative Binary Search

This is the most commonly used implementation.

```java
public class BinarySearch {

    public static int search(int[] arr, int target) {

        int low = 0;
        int high = arr.length - 1;

        while (low <= high) {

            int mid = low + (high - low) / 2;

            if (arr[mid] == target) {
                return mid;
            }

            if (arr[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return -1;
    }

    public static void main(String[] args) {

        int[] numbers = {10, 20, 30, 40, 50, 60, 70, 80, 90};

        int target = 70;

        int result = search(numbers, target);

        if (result != -1) {
            System.out.println("Element found at index: " + result);
        } else {
            System.out.println("Element not found");
        }
    }
}
```

**Output:**

```text
Element found at index: 6
```

### Understanding the code

- `low = 0`: Start searching from the first index.
- `high = arr.length - 1`: End searching at the last index.
- `while (low <= high)`: Continue while there are elements left to search.
- `mid`: Calculate the middle index.
- `arr[mid] == target`: Check whether the target is found.
- `low = mid + 1`: Search the right half.
- `high = mid - 1`: Search the left half.
- `return -1`: Target does not exist.

### Why use `low + (high - low) / 2` instead of `(low + high) / 2`?

Consider:

```java
int mid = (low + high) / 2;
```

If `low` and `high` are very large integer values, their sum can exceed the maximum value of an `int`.

This causes integer overflow.

The safer expression is:

```java
int mid = low + (high - low) / 2;
```

It avoids overflowing during the addition of `low` and `high`, assuming valid non-negative array indexes.

## 5. Recursive Binary Search

Binary Search can also be implemented using recursion.

Instead of repeatedly updating `low` and `high` in a loop, we call the same method again with a smaller search range.

### Java implementation

```java
public class RecursiveBinarySearch {

    public static int search(
            int[] arr,
            int low,
            int high,
            int target) {

        // Base condition: Search range is empty
        if (low > high) {
            return -1;
        }

        int mid = low + (high - low) / 2;

        // Target found
        if (arr[mid] == target) {
            return mid;
        }

        // Search right half
        if (arr[mid] < target) {
            return search(arr, mid + 1, high, target);
        }

        // Search left half
        return search(arr, low, mid - 1, target);
    }

    public static void main(String[] args) {

        int[] numbers = {10, 20, 30, 40, 50, 60, 70, 80, 90};

        int result = search(
                numbers,
                0,
                numbers.length - 1,
                70
        );

        System.out.println(result);
    }
}
```

**Output:**

```text
6
```

### Iterative vs Recursive Binary Search

| Feature | Iterative | Recursive |
|---|---|---|
| Implementation | Uses a loop | Uses method calls |
| Time Complexity | O(log n) | O(log n) |
| Auxiliary Space | O(1) | O(log n) |
| Stack usage | No recursive stack | Uses call stack |
| Performance | Generally lower overhead | Has method-call overhead |
| Recommendation | Usually preferred | Useful for learning recursion |

## 6. Time Complexity of Binary Search

Why is Binary Search O(log n)?

Because it divides the search space into half after every comparison.

Consider an array of 16 elements.

| Step | Remaining Elements |
|---|---:|
| Initial | 16 |
| After 1 comparison | 8 |
| After 2 comparisons | 4 |
| After 3 comparisons | 2 |
| After 4 comparisons | 1 |

Only a few comparisons are required even when the array contains many elements.

### Mathematical explanation

After each comparison, the remaining search space becomes:

\[
\frac{n}{2},\quad \frac{n}{4},\quad \frac{n}{8},\quad \ldots
\]

After `k` comparisons:

\[
\frac{n}{2^k}
\]

The search finishes when approximately one element remains:

\[
\frac{n}{2^k}=1
\]

Therefore:

\[
n=2^k
\]

Taking logarithm base 2:

\[
k=\log_2(n)
\]

So the time complexity is:

\[
\boxed{O(\log n)}
\]

### Best, Average and Worst Cases

| Case | Time Complexity | Explanation |
|---|---|---|
| Best | O(1) | Target is at the middle initially |
| Average | O(log n) | Several halves are eliminated |
| Worst | O(log n) | Target is at the final candidate or absent |

For an array of `n` elements, the maximum number of comparisons in the standard implementation is approximately:

\[
\lfloor \log_2(n) \rfloor + 1
\]

For example, with 1,000 elements, Binary Search requires at most 10 comparisons.

## 7. Space Complexity

### Iterative Binary Search

Only a few variables are used:

- `low`
- `high`
- `mid`

No additional data structure is created.

**Auxiliary Space: O(1)**

### Recursive Binary Search

Every recursive call creates a stack frame.

Since the search range is divided by two at each call, the maximum recursion depth is logarithmic.

**Auxiliary Space: O(log n)**

### Complexity summary

| Implementation | Time Complexity | Auxiliary Space |
|---|---|---|
| Iterative | O(log n) | O(1) |
| Recursive | O(log n) | O(log n) |

## 8. What Happens If the Element Is Not Found?

Consider:

```java
int[] arr = {10, 20, 30, 40, 50, 60, 70};
int target = 45;
```

Binary Search keeps narrowing the search range.

Eventually:

```text
low > high
```

This means there are no remaining elements to check.

The algorithm returns `-1`.

**Important:** Always handle the not-found case in your implementation.

## 9. Handling Duplicate Elements

What if an array contains the same value multiple times?

Example:

```java
int[] arr = {10, 20, 20, 20, 30, 40, 50};
int target = 20;
```

The normal Binary Search implementation may return any matching index, depending on where it encounters the target.

It does not guarantee the first or last occurrence.

### A. Find the First Occurrence

We continue searching on the left even after finding the target.

```java
public static int firstOccurrence(int[] arr, int target) {

    int low = 0;
    int high = arr.length - 1;
    int result = -1;

    while (low <= high) {

        int mid = low + (high - low) / 2;

        if (arr[mid] == target) {

            result = mid;
            high = mid - 1;

        } else if (arr[mid] < target) {

            low = mid + 1;

        } else {

            high = mid - 1;
        }
    }

    return result;
}
```

For the example above, the result is:

```text
1
```

### B. Find the Last Occurrence

We continue searching on the right after finding the target.

```java
public static int lastOccurrence(int[] arr, int target) {

    int low = 0;
    int high = arr.length - 1;
    int result = -1;

    while (low <= high) {

        int mid = low + (high - low) / 2;

        if (arr[mid] == target) {

            result = mid;
            low = mid + 1;

        } else if (arr[mid] < target) {

            low = mid + 1;

        } else {

            high = mid - 1;
        }
    }

    return result;
}
```

For the example above, the result is:

```text
3
```

Both algorithms have O(log n) time complexity.

## 10. Finding the Insertion Position

Binary Search can also help find where a new element should be inserted into a sorted array.

Example:

```java
int[] arr = {10, 20, 30, 50, 60};
int target = 40;
```

The correct insertion position is index `3`.

We can find this using a lower-bound style Binary Search.

```java
public static int insertionPosition(int[] arr, int target) {

    int low = 0;
    int high = arr.length;

    while (low < high) {

        int mid = low + (high - low) / 2;

        if (arr[mid] < target) {
            low = mid + 1;
        } else {
            high = mid;
        }
    }

    return low;
}
```

Output:

```text
3
```

This returns the first index whose value is greater than or equal to the target.

If all elements are smaller, it returns `arr.length`.

If duplicates exist, it returns the position before the first equal element.

**Note:** Finding the insertion position takes O(log n), but physically inserting into an array may take O(n) because elements need to be shifted.

## 11. Binary Search in Java Collections

Java provides built-in Binary Search methods.

### A. Using Arrays.binarySearch()

```java
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {

        int[] arr = {10, 20, 30, 40, 50, 60};

        int index = Arrays.binarySearch(arr, 40);

        System.out.println(index);
    }
}
```

Output:

```text
3
```

**Important points:**

- The array must be sorted before searching.
- If the element is found, the method returns its index.
- If the element is not found, it returns a negative value.

The negative result is:

\[
-(insertionPoint)-1
\]

For example, searching for `35` in:

```text
[10, 20, 30, 40, 50]
```

The insertion point is `3`.

Therefore:

```text
-(3) - 1 = -4
```

The method returns `-4`.

When duplicates exist, the returned matching index is not guaranteed to be the first or last occurrence.

### B. Using Collections.binarySearch()

```java
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        List<Integer> numbers = new ArrayList<>(
                List.of(10, 20, 30, 40, 50)
        );

        int index = Collections.binarySearch(numbers, 40);

        System.out.println(index);
    }
}
```

Output:

```text
3
```

The list must be sorted according to the same ordering used for the search.

For a random-access list such as ArrayList, this is efficient.

For LinkedList, `Collections.binarySearch()` avoids repeatedly using expensive indexed access, but navigation still makes it generally O(n) in time. Binary Search is not automatically O(log n) for every collection.

## 12. Linear Search vs Binary Search

| Feature | Linear Search | Binary Search |
|---|---|---|
| Searching approach | One by one | Divide and conquer |
| Sorted data required | No | Yes |
| Best Case | O(1) | O(1) |
| Average Case | O(n) | O(log n) |
| Worst Case | O(n) | O(log n) |
| Iterative Space | O(1) | O(1) |
| Implementation | Very simple | Requires careful boundaries |
| Suitable for | Small or unsorted data | Large sorted data |

### Example: 1 million elements

Suppose an array contains 1,000,000 sorted elements.

| Algorithm | Approximate Maximum Comparisons |
|---|---:|
| Linear Search | 1,000,000 |
| Binary Search | 20 |

Why?

Because:

\[
2^{20}=1,048,576
\]

Binary Search can eliminate more than half of the remaining elements with each comparison.

## 13. Advantages of Binary Search

1. Very efficient for large sorted datasets.
2. Reduces the search space by half after each comparison.
3. Has O(log n) time complexity.
4. Can be implemented iteratively with O(1) auxiliary space.
5. Useful for finding first and last occurrences.
6. Can find insertion positions in sorted data.
7. Forms the foundation of many advanced algorithms.

## 14. Disadvantages of Binary Search

1. Standard Binary Search requires sorted data.
2. Sorting an unsorted collection can add significant cost.
3. More difficult to implement correctly than Linear Search.
4. Incorrect boundary conditions can cause infinite loops or missed elements.
5. Not efficient on linked lists because they do not provide constant-time middle access.
6. Standard Binary Search is not suitable when the data changes frequently and maintaining sorted order is expensive.

## 15. Common Mistakes

### Mistake 1: Searching an unsorted array

Binary Search assumes that elements are ordered.

Incorrect:

```java
int[] arr = {40, 10, 70, 20, 50};
```

The algorithm may return an incorrect result or fail to find an existing element.

Correct:

```java
Arrays.sort(arr);
```

Sort before using Binary Search, if the cost of sorting is justified.

### Mistake 2: Using `low <= high` incorrectly

For the classic exact-match implementation, use:

```java
while (low <= high)
```

When using an insertion-position or lower-bound implementation, the usual condition is:

```java
while (low < high)
```

These are different Binary Search patterns. Do not mix their boundary-update rules.

### Mistake 3: Forgetting `+1` and `-1`

Incorrect:

```java
low = mid;
high = mid;
```

This may cause an infinite loop when `low` and `high` are adjacent.

Correct for exact-match Binary Search:

```java
low = mid + 1;
high = mid - 1;
```

### Mistake 4: Incorrect middle calculation

Prefer:

```java
int mid = low + (high - low) / 2;
```

This avoids the common overflow risk of `(low + high) / 2`.

### Mistake 5: Using Binary Search on LinkedList without considering access cost

A LinkedList does not provide O(1) access to its middle element.

For a LinkedList, a normal iterator-based Linear Search is often more appropriate.

## 16. Real-World Applications

1. **Dictionary lookup:** Finding a word in an alphabetically sorted dictionary.
2. **Phone contacts:** Searching names in a sorted contact list.
3. **Database indexes:** B-tree and B+ tree indexes use related ordered-search principles.
4. **Version lookup:** Finding a version in a sorted sequence.
5. **Finding boundaries:** Locating the first or last position satisfying a condition.
6. **Finding thresholds:** Finding the minimum capacity or speed that satisfies a requirement.
7. **Search in sorted datasets:** Efficiently finding a value in sorted arrays.

## 17. Advanced Applications of Binary Search

Binary Search is not limited to finding a target value in an array.

It can also be used when the answer space has a monotonic property.

### A. Binary Search on Answer

Imagine you have a machine that can process a certain number of items per hour.

You need to find the minimum processing speed that allows all items to be completed before a deadline.

Instead of testing every possible speed, Binary Search can test the middle speed and eliminate half of the possible answers.

This pattern is called **Binary Search on Answer**.

Typical problems:

- Minimum eating speed.
- Minimum shipping capacity.
- Minimum days required.
- Minimum time to complete a task.
- Maximum minimum distance between objects.

The key requirement is that the feasibility condition must be monotonic: once a candidate works, all larger candidates (or all smaller candidates, depending on the problem) must also work.

### B. Search in a Rotated Sorted Array

Example:

```java
int[] arr = {40, 50, 60, 70, 10, 20, 30};
```

This array was originally sorted but rotated.

A modified Binary Search can still search it in O(log n) time when values are distinct.

### C. Search in a 2D Matrix

Binary Search can be applied to certain sorted matrices.

For example, when each row is sorted and the first element of each row is greater than the last element of the previous row, the matrix can be treated as one sorted sequence.

## 18. Interview Questions and Answers

### Q1. What is Binary Search?

Binary Search is a searching algorithm that repeatedly divides a sorted search space into two halves until the target is found or no candidates remain.

### Q2. What is the time complexity of Binary Search?

- Best: O(1)
- Average: O(log n)
- Worst: O(log n)

### Q3. Why is Binary Search O(log n)?

Because each comparison reduces the search space by approximately half.

### Q4. Does Binary Search require sorted data?

For standard array-based Binary Search, yes. The data must be sorted according to the comparison order.

### Q5. What is the space complexity of Binary Search?

- Iterative: O(1)
- Recursive: O(log n)

### Q6. Why is `low <= high` used?

Because the search range is inclusive. When `low == high`, one candidate remains and must still be checked.

### Q7. What is the difference between `low < high` and `low <= high`?

`low <= high` is commonly used for exact-match search with inclusive boundaries.

`low < high` is commonly used in lower-bound or insertion-position searches, where `high` is often an exclusive boundary.

### Q8. Can Binary Search work with duplicate elements?

Yes, but normal Binary Search does not guarantee which duplicate occurrence it returns. Modified versions find the first or last occurrence.

### Q9. Can Binary Search be implemented recursively?

Yes. The recursive implementation searches a smaller range by calling the same method again.

### Q10. Why is iterative Binary Search often preferred?

It uses O(1) auxiliary space and avoids recursive method-call overhead.

### Q11. Can Binary Search be used on a LinkedList?

It is possible, but it is generally inefficient because reaching the middle element requires traversal. A sequential search is usually more appropriate.

### Q12. What is the difference between Binary Search and Binary Search Tree?

Binary Search is an algorithm for finding an element in an ordered search space.

A Binary Search Tree (BST) is a data structure where smaller values are stored in the left subtree and larger values in the right subtree (with duplicate handling depending on implementation).

They are related concepts, but they are not the same thing.

### Q13. What does Arrays.binarySearch() return when an element is missing?

It returns a negative value calculated as:

```java
-(insertionPoint) - 1
```

### Q14. Can Binary Search be used without an array?

Yes. It can be used with other sorted random-access collections and on monotonic answer spaces, provided the search conditions are suitable.

### Q15. What is Binary Search on Answer?

It is a technique that searches for the smallest or largest feasible answer when a condition changes monotonically across the candidate range.

## 19. Practice Problems

### Beginner

1. Implement Binary Search using a loop.
2. Implement Binary Search using recursion.
3. Search for an element in a sorted array.
4. Return `-1` when the element does not exist.
5. Count the number of comparisons performed.

### Intermediate

6. Find the first occurrence of an element.
7. Find the last occurrence of an element.
8. Count the occurrences of an element in a sorted array.
9. Find the insertion position of an element.
10. Find the floor of a given number.
11. Find the ceiling of a given number.
12. Find the first and last positions of an element.
13. Find the integer square root of a number.

### Advanced

14. Search in a rotated sorted array.
15. Find the minimum element in a rotated sorted array.
16. Find a peak element.
17. Find the single non-duplicate element in a sorted array.
18. Find the median of two sorted arrays.
19. Find the minimum eating speed.
20. Find the minimum shipping capacity within a given number of days.

## 20. Quick Revision

| Concept | Answer |
|---|---|
| Algorithm type | Searching |
| Approach | Divide and conquer |
| Sorted data required? | Yes, for standard array search |
| Best Case | O(1) |
| Average Case | O(log n) |
| Worst Case | O(log n) |
| Iterative Space | O(1) |
| Recursive Space | O(log n) |
| Middle calculation | `low + (high - low) / 2` |
| Search range | `low <= high` |
| Search right half | `low = mid + 1` |
| Search left half | `high = mid - 1` |
| Not found | Usually `-1` |

## Final Takeaway

Linear Search checks elements one by one. Binary Search uses the sorted order to eliminate half of the remaining possibilities after each comparison.

The most important lesson is:

**A good algorithm does not always do more work faster. Sometimes, it finds a way to avoid unnecessary work entirely.**

Master the iterative implementation first, understand the boundary conditions, and then practice duplicate handling and Binary Search on Answer. These concepts are frequently used in coding interviews.