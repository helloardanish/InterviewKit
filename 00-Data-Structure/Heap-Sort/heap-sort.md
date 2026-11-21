---
title: "Heap Sort"
layout: default
parent: Data Structures & Algorithms
nav_order: 17
permalink: /data-structures/Heap-Sort/heap-sort/

topic: Heap Sort
section: Sorting Algorithms
concepts: [heapify, max-heap, min-heap, in-place-sort]
complexities: [O(n log n)]
tags: [algorithms, sorting, heap-sort, heap, java]
---

# Heap Sort

## 1. What is Heap Sort?

**Heap Sort** is a comparison-based sorting algorithm that uses a special tree-based data structure called a **Binary Heap**.

It uses the **Divide and Conquer-like strategy of organizing data through a heap**, but unlike Merge Sort and Quick Sort, it does not use recursive division and merging or pivot-based partitioning.

Heap Sort works in two major phases:

1. **Build a Max Heap:** Rearrange the array so that the largest element is at the root.
2. **Extract Maximum:** Move the root (largest element) to the end of the array, reduce the heap size, and restore the Max Heap property.

Repeat until all elements are sorted.

### Real-world examples

- **Prioritizing tasks:** A task management system can keep the highest-priority task at the top so it can be processed first.
- **Selecting the highest score:** A competition system can repeatedly identify the highest score from a collection.
- **Emergency services:** A priority queue can organize requests so the most urgent task is processed first.
- **Job scheduling:** A system can organize jobs based on priority and repeatedly select the highest-priority job.

## 2. First Understand Binary Heap

Before understanding Heap Sort, we need to understand a **Binary Heap**.

A Binary Heap is a complete binary tree that follows a specific ordering property.

There are two main types:

### 1. Max Heap

In a Max Heap, every parent node is greater than or equal to its children.

The largest element is always at the root.

Example:

```text
              50
             /  \
           30    40
          / \   /
        10  20 35
```

Here:

- 50 is greater than 30 and 40.
- 30 is greater than 10 and 20.
- 40 is greater than 35.

Therefore, this is a valid Max Heap.

### 2. Min Heap

In a Min Heap, every parent node is smaller than or equal to its children.

The smallest element is always at the root.

Example:

```text
              10
             /  \
           20    15
          / \   /
        30  40 25
```

Here, 10 is the smallest element.

### Max Heap vs Min Heap

| Feature | Max Heap | Min Heap |
|---|---|---|
| Root | Largest element | Smallest element |
| Parent | Greater than or equal to children | Smaller than or equal to children |
| Common Use | Maximum priority | Minimum priority |
| Heap Sort | Used for ascending order | Can be used for descending order |

**Important:** A Binary Heap is a complete binary tree, meaning every level is completely filled except possibly the last level, which is filled from left to right.

## 3. How Is a Binary Heap Stored in an Array?

One important property of a Binary Heap is that it can be efficiently represented using an array without creating separate tree nodes.

Consider this Max Heap:

```text
              50
             /  \
           30    40
          / \   /
        10  20 35
```

Its array representation is:

```text
Index:  0   1   2   3   4   5
Value: 50  30  40  10  20  35
```

For a zero-based array, the index relationships are:

| Node | Formula |
|---|---|
| Parent of index `i` | `(i - 1) / 2` |
| Left child of index `i` | `2 * i + 1` |
| Right child of index `i` | `2 * i + 2` |

### Example

Consider the element at index `1`, whose value is `30`.

Its children are:

```text
Left Child  = 2 * 1 + 1 = 3
Right Child = 2 * 1 + 2 = 4
```

Therefore:

```text
              50
             /
           30
          /  \
        10    20
```

Its parent is:

```text
Parent Index = (1 - 1) / 2 = 0
```

The parent is `50`.

**Why use an array instead of a tree with nodes?**

A complete binary tree has no gaps in its level-order representation. Therefore, we can calculate parent and child positions directly using indexes.

This avoids storing separate left-child and right-child references.

## 4. What Is Heapify?

**Heapify** is the process of restoring the Heap property when a node violates it.

For a Max Heap, the parent must be greater than or equal to both children.

If a parent is smaller than one of its children, we swap it with the larger child and continue checking downward.

This operation is also commonly called **sift down** or **percolate down**.

### Example

Consider:

```text
              10
             /  \
           30    20
```

This is not a valid Max Heap because `10` is smaller than its children.

Compare:

- Parent = 10
- Left child = 30
- Right child = 20

The largest element is `30`.

Swap `10` and `30`.

```text
              30
             /  \
           10    20
```

Now the Max Heap property is satisfied.

### Heapify with a larger example

Consider:

```text
              10
             /  \
           30    40
          / \   /
        50  20 35
```

The root violates the Max Heap property.

1. Compare 10, 30, and 40. The largest is 40.
2. Swap 10 and 40.
3. Now compare 10 with its child 35.
4. Swap 10 and 35.

Final tree:

```text
              40
             /  \
           30    35
          / \   /
        50  20 10
```

There is still a violation: `30` is smaller than its child `50`.

This example demonstrates why heapify must be applied to the appropriate subtree and why a single sift-down operation only restores the subtree rooted at the selected node when its child subtrees are already heaps.

## 5. How Does Heap Sort Work?

Let's understand Heap Sort step by step.

Consider this array:

```text
[4, 10, 3, 5, 1]
```

We want to sort it in ascending order.

Heap Sort uses a **Max Heap** to repeatedly extract the largest element.

### Phase 1: Build a Max Heap

Initially:

```text
[4, 10, 3, 5, 1]
```

Tree representation:

```text
              4
             / \
           10   3
          / \
         5   1
```

The last non-leaf node is at index:

```text
n / 2 - 1
```

For `n = 5`:

```text
5 / 2 - 1 = 1
```

Start heapifying from index `1`, then move backward to index `0`.

#### Heapify index 1

Current node = 10

Children:

- Left = 5
- Right = 1

10 is already greater than both children.

No swap is required.

#### Heapify index 0

Current node = 4

Children:

- Left = 10
- Right = 3

The largest is 10.

Swap 4 and 10:

```text
[10, 4, 3, 5, 1]
```

Now heapify the subtree rooted at index `1`.

Its children are 5 and 1.

Since 5 is greater than 4, swap them:

```text
[10, 5, 3, 4, 1]
```

The Max Heap is now ready.

```text
              10
             /  \
            5    3
           / \
          4   1
```

### Phase 2: Extract the Maximum Element

The largest element is always at index `0`.

Swap it with the last element in the active heap.

```text
Before: [10, 5, 3, 4, 1]

After:  [1, 5, 3, 4, 10]
```

Now `10` is in its final sorted position.

Reduce the heap size by one. The last element is no longer part of the heap.

```text
[1, 5, 3, 4 | 10]
```

Heapify the remaining heap.

Compare:

- Parent = 1
- Left = 5
- Right = 3

Swap 1 and 5.

```text
[5, 1, 3, 4 | 10]
```

Continue heapifying at index 1.

Compare 1 and 4.

Swap them:

```text
[5, 4, 3, 1 | 10]
```

Now the remaining portion is a valid Max Heap.

### Phase 3: Repeat Extraction

Repeat the same process.

| Pass | Operation | Array |
|---|---|---|
| Initial | Build Max Heap | `[10, 5, 3, 4, 1]` |
| 1 | Move 10 to end, heapify | `[5, 4, 3, 1, 10]` |
| 2 | Move 5 to end, heapify | `[4, 1, 3, 5, 10]` |
| 3 | Move 4 to end, heapify | `[3, 1, 4, 5, 10]` |
| 4 | Move 3 to end | `[1, 3, 4, 5, 10]` |

<caption>Final sorted array (ascending order)</caption>

```text
[1, 3, 4, 5, 10]
```

## 6. Heap Sort Algorithm

Heap Sort is generally implemented using two methods:

1. `buildMaxHeap()` — converts the array into a Max Heap.
2. `heapify()` — restores the Max Heap property for a subtree.

### Pseudocode

```text
heapSort(array):

    n = array.length

    // Build Max Heap
    for i = n / 2 - 1 down to 0:
        heapify(array, n, i)

    // Extract elements one by one
    for i = n - 1 down to 1:

        swap array[0] and array[i]

        heapify(array, i, 0)
```

### Heapify pseudocode

```text
heapify(array, heapSize, root):

    largest = root

    left = 2 * root + 1
    right = 2 * root + 2

    if left < heapSize AND array[left] > array[largest]:
        largest = left

    if right < heapSize AND array[right] > array[largest]:
        largest = right

    if largest != root:

        swap array[root] and array[largest]

        heapify(array, heapSize, largest)
```

## 7. Java Implementation

### Heap Sort (Ascending Order)

```java
import java.util.Arrays;

public class HeapSort {

    public static void heapSort(int[] arr) {

        int n = arr.length;

        // Phase 1: Build Max Heap
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(arr, n, i);
        }

        // Phase 2: Extract maximum elements
        for (int i = n - 1; i > 0; i--) {

            // Move current maximum to the end
            swap(arr, 0, i);

            // Restore heap property in remaining heap
            heapify(arr, i, 0);
        }
    }

    private static void heapify(
            int[] arr, int heapSize, int root) {

        int largest = root;

        int left = 2 * root + 1;
        int right = 2 * root + 2;

        // Check left child
        if (left < heapSize &&
                arr[left] > arr[largest]) {

            largest = left;
        }

        // Check right child
        if (right < heapSize &&
                arr[right] > arr[largest]) {

            largest = right;
        }

        // If root is not the largest, swap and continue
        if (largest != root) {

            swap(arr, root, largest);

            heapify(arr, heapSize, largest);
        }
    }

    private static void swap(int[] arr, int i, int j) {

        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static void main(String[] args) {

        int[] arr = {4, 10, 3, 5, 1};

        heapSort(arr);

        System.out.println(Arrays.toString(arr));
    }
}
```

**Output:**

```text
[1, 3, 4, 5, 10]
```

### Understanding the important parts

**1. Build Max Heap**

```java
for (int i = n / 2 - 1; i >= 0; i--) {
    heapify(arr, n, i);
}
```

Start from the last non-leaf node and move backward to the root.

Why?

Leaf nodes do not have children, so they are already valid heaps.

The last non-leaf node in a zero-based array is at index:

```java
n / 2 - 1
```

**2. Calculate child indexes**

```java
int left = 2 * root + 1;
int right = 2 * root + 2;
```

These formulas locate the left and right children in the array representation.

**3. Find the largest element**

```java
int largest = root;
```

Initially, assume the root is the largest.

Then compare it with its left and right children.

**4. Check left child**

```java
if (left < heapSize &&
        arr[left] > arr[largest]) {

    largest = left;
}
```

Only consider the left child if it exists within the active heap.

**5. Check right child**

```java
if (right < heapSize &&
        arr[right] > arr[largest]) {

    largest = right;
}
```

Update the largest index if the right child is greater.

**6. Restore the heap property**

```java
if (largest != root) {

    swap(arr, root, largest);

    heapify(arr, heapSize, largest);
}
```

If a child is larger than the root, swap them.

Then continue heapifying the affected subtree because the swapped element may violate the heap property further down.

**7. Extract the maximum**

```java
swap(arr, 0, i);
```

The root contains the largest element. Move it to the end of the active heap.

**8. Reduce the heap size**

```java
heapify(arr, i, 0);
```

The element at index `i` is now sorted, so the active heap size becomes `i`.

Restore the heap property for the remaining elements.

## 8. Heap Sort in Descending Order

To sort in descending order, use a **Min Heap** instead of a Max Heap.

In a Min Heap:

- The smallest element is at the root.
- Each parent is smaller than or equal to its children.

Repeatedly move the smallest element to the end of the active heap.

### Java Implementation

```java
public static void heapSortDescending(int[] arr) {

    int n = arr.length;

    // Build Min Heap
    for (int i = n / 2 - 1; i >= 0; i--) {
        heapifyMin(arr, n, i);
    }

    // Move smallest elements to the end
    for (int i = n - 1; i > 0; i--) {

        swap(arr, 0, i);

        heapifyMin(arr, i, 0);
    }
}

private static void heapifyMin(
        int[] arr, int heapSize, int root) {

    int smallest = root;

    int left = 2 * root + 1;
    int right = 2 * root + 2;

    if (left < heapSize &&
            arr[left] < arr[smallest]) {

        smallest = left;
    }

    if (right < heapSize &&
            arr[right] < arr[smallest]) {

        smallest = right;
    }

    if (smallest != root) {

        swap(arr, root, smallest);

        heapifyMin(arr, heapSize, smallest);
    }
}
```

**Example:**

```text
Input:  [4, 10, 3, 5, 1]
Output: [10, 5, 4, 3, 1]
```

## 9. Time Complexity

Heap Sort has O(n log n) time complexity in the best, average, and worst cases.

| Case | Time Complexity | Explanation |
|---|---|---|
| Best Case | O(n log n) | Building the heap and extracting elements |
| Average Case | O(n log n) | Heap construction and repeated heapify |
| Worst Case | O(n log n) | Each extraction may require O(log n) heapify |

### Why is heapify O(log n)?

A Binary Heap is a complete binary tree.

For `n` elements, its height is approximately:

\[
\log_2(n)
\]

During heapify, an element can move from the root to the bottom of the tree.

At each level, we perform a constant amount of work.

Therefore:

```text
Heapify = O(log n)
```

### Why is building a heap O(n)?

A common mistake is to assume that building a heap takes O(n log n), because there are O(n) nodes and heapify can take O(log n).

However, most nodes are near the bottom of the tree and have very little distance to move.

The total work is proportional to:

\[
\sum_{h=0}^{\lfloor\log_2 n\rfloor}
\frac{n}{2^{h+1}} O(h)
\]

This sum is O(n).

Therefore:

```text
Build Heap = O(n)
```

### Total Heap Sort complexity

There are approximately `n - 1` extraction operations.

Each extraction requires heapify, which takes O(log n).

Therefore:

\[
O(n) + O(n\log n) = O(n\log n)
\]

**Final time complexity: O(n log n).**

## 10. Space Complexity

Heap Sort is an in-place sorting algorithm.

It rearranges the elements inside the original array and does not require another array.

For the recursive `heapify()` implementation:

| Component | Space Complexity |
|---|---|
| Original array | O(n), input storage |
| Extra array | O(1) |
| Recursive heapify stack | O(log n) |
| Total auxiliary space | O(log n) |

**Important:** The recursive implementation uses O(log n) stack space.

If `heapify()` is implemented iteratively, the auxiliary space becomes O(1).

### Iterative Heapify

```java
private static void heapify(
        int[] arr, int heapSize, int root) {

    while (true) {

        int largest = root;

        int left = 2 * root + 1;
        int right = 2 * root + 2;

        if (left < heapSize &&
                arr[left] > arr[largest]) {

            largest = left;
        }

        if (right < heapSize &&
                arr[right] > arr[largest]) {

            largest = right;
        }

        if (largest == root) {
            break;
        }

        swap(arr, root, largest);

        root = largest;
    }
}
```

Replace the recursive `heapify()` method with this iterative version in the main implementation.

Now Heap Sort uses O(1) auxiliary space.

## 11. Properties of Heap Sort

| Property | Description |
|---|---|
| Algorithm | Comparison-based |
| Data Structure | Binary Heap |
| Best Time | O(n log n) |
| Average Time | O(n log n) |
| Worst Time | O(n log n) |
| Build Heap | O(n) |
| Heapify | O(log n) |
| Auxiliary Space | O(1) iterative, O(log n) recursive |
| Stable | No |
| In-place | Yes |
| Adaptive | No |

### Why is Heap Sort unstable?

A stable sorting algorithm preserves the relative order of equal elements.

Consider:

```text
[(A, 5), (B, 3), (C, 5)]
```

We sort by the numeric value.

During heap construction and extraction, elements can move across one another.

A possible result is:

```text
[(C, 5), (A, 5), (B, 3)]
```

The relative order of `A` and `C` has changed.

Therefore, standard Heap Sort is unstable.

### Why is Heap Sort not adaptive?

An adaptive algorithm takes advantage of input that is already or nearly sorted.

Heap Sort still builds a heap and performs extraction operations regardless of the initial arrangement.

Its time complexity remains O(n log n).

## 12. Heap Sort vs Other Sorting Algorithms

| Feature | Insertion Sort | Merge Sort | Quick Sort | Heap Sort |
|---|---|---|---|---|
| Best Time | O(n) | O(n log n) | O(n log n) | O(n log n) |
| Average Time | O(n²) | O(n log n) | O(n log n) | O(n log n) |
| Worst Time | O(n²) | O(n log n) | O(n²) | O(n log n) |
| Auxiliary Space | O(1) | O(n) | O(log n) average stack | O(1) iterative |
| Stable | Yes | Yes | No | No |
| In-place | Yes | No, standard version | Yes, with stack qualification | Yes |
| Adaptive | Yes | No, standard version | No, standard version | No |

### Which one should you choose?

Choose **Insertion Sort** when:

- The dataset is small.
- The data is nearly sorted.
- Simplicity is important.

Choose **Merge Sort** when:

- Stable sorting is required.
- Guaranteed O(n log n) time is important.
- Sorting linked lists or external datasets.

Choose **Quick Sort** when:

- Average performance on arrays is important.
- In-place partitioning is useful.
- Stability is not required.

Choose **Heap Sort** when:

- Guaranteed O(n log n) worst-case time is required.
- O(1) auxiliary space is important.
- Stability is not required.

## 13. Heap Sort vs Priority Queue

Heap Sort and Priority Queue both use the concept of a heap, but they solve different problems.

| Feature | Heap Sort | Priority Queue |
|---|---|---|
| Purpose | Sort all elements | Process elements by priority |
| Main Operation | Build heap and extract maximum/minimum | Insert and remove highest-priority element |
| Structure | Binary Heap | Often implemented using a Binary Heap |
| Insert | Not the main sorting operation | O(log n) |
| Peek | O(1) on a heap | O(1) |
| Remove Root | O(log n) | O(log n) |
| Result | Fully sorted array | Priority-ordered access |

### Java PriorityQueue example

Java's `PriorityQueue` uses a Min Heap by default.

```java
import java.util.PriorityQueue;

public class PriorityQueueExample {

    public static void main(String[] args) {

        PriorityQueue<Integer> pq = new PriorityQueue<>();

        pq.offer(40);
        pq.offer(10);
        pq.offer(30);
        pq.offer(20);

        while (!pq.isEmpty()) {
            System.out.println(pq.poll());
        }
    }
}
```

**Output:**

```text
10
20
30
40
```

For a Max Heap:

```java
PriorityQueue<Integer> maxHeap =
        new PriorityQueue<>((a, b) -> Integer.compare(b, a));
```

**Important:** Java's `PriorityQueue` is not itself a Heap Sort implementation. It is a priority queue backed by a heap.

## 14. Advantages of Heap Sort

1. Guaranteed O(n log n) worst-case time complexity.
2. Can be implemented with O(1) auxiliary space using iterative heapify.
3. Does not require an additional array.
4. Works well when memory is limited.
5. Provides predictable asymptotic performance.
6. Uses a well-understood Binary Heap structure.

## 15. Disadvantages of Heap Sort

1. Not stable.
2. Does not take advantage of already sorted input.
3. Often has poorer cache locality than Quick Sort.
4. Can involve more swaps than some alternatives.
5. Usually slower in practice than optimized Quick Sort for many in-memory array workloads.
6. Requires understanding of heap construction and heapify.

## 16. Common Mistakes

### Mistake 1: Starting heap construction at the wrong index

Incorrect:

```java
for (int i = n - 1; i >= 0; i--)
```

This unnecessarily processes leaf nodes.

Correct:

```java
for (int i = n / 2 - 1; i >= 0; i--)
```

Start from the last non-leaf node.

### Mistake 2: Incorrect child index calculation

For zero-based indexing:

```java
int left = 2 * root + 1;
int right = 2 * root + 2;
```

Do not accidentally use formulas designed for one-based indexing.

### Mistake 3: Forgetting the heap-size boundary

Incorrect:

```java
if (arr[left] > arr[largest])
```

The child may not exist in the active heap.

Correct:

```java
if (left < heapSize && arr[left] > arr[largest])
```

### Mistake 4: Not updating the heap size

After moving the maximum element to the end, it must no longer be included in heapify.

Correct:

```java
heapify(arr, i, 0);
```

Here `i` is the reduced heap size.

### Mistake 5: Forgetting recursive heapify

After swapping a parent with its larger child, the swapped element may violate the heap property further down.

Correct:

```java
heapify(arr, heapSize, largest);
```

### Mistake 6: Confusing Max Heap and Min Heap

- Max Heap gives the largest element at the root.
- Min Heap gives the smallest element at the root.

For ascending Heap Sort, use a Max Heap.

For descending Heap Sort, use a Min Heap.

### Mistake 7: Assuming heap construction takes O(n log n)

Building a heap using bottom-up heapification takes O(n), not O(n log n).

## 17. Interview Questions and Answers

**Q1. What is Heap Sort?**

Heap Sort is a comparison-based sorting algorithm that uses a Binary Heap to repeatedly extract the largest or smallest element.

**Q2. What is a Binary Heap?**

A Binary Heap is a complete binary tree that follows either the Max Heap or Min Heap property.

**Q3. What is the difference between Max Heap and Min Heap?**

- Max Heap keeps the largest element at the root.
- Min Heap keeps the smallest element at the root.

**Q4. What is heapify?**

Heapify restores the heap property for a subtree by moving a violating node downward until the subtree satisfies the heap property.

**Q5. What is the time complexity of Heap Sort?**

Best, average, and worst cases are all O(n log n).

**Q6. Why does building a heap take O(n)?**

Most nodes are near the leaves and require little or no movement. Summing the work across all tree levels gives O(n).

**Q7. What is the time complexity of heapify?**

O(log n), because an element can move down at most the height of the Binary Heap.

**Q8. Is Heap Sort stable?**

No. Heap operations can change the relative order of equal elements.

**Q9. Is Heap Sort in-place?**

Yes. It rearranges elements in the original array without requiring another array. Iterative heapify gives O(1) auxiliary space.

**Q10. Why is a Binary Heap stored in an array?**

Because it is a complete binary tree, parent and child positions can be calculated directly using array indexes.

**Q11. Why do we build a Max Heap for ascending order?**

The largest element is at the root. Moving it to the end repeatedly produces ascending order.

**Q12. What is the last non-leaf node's index?**

For a zero-based array of size `n`:

```java
n / 2 - 1
```

**Q13. What is the difference between heapify and build heap?**

- Heapify restores the heap property for a subtree.
- Build Heap applies heapify from the last non-leaf node back to the root to construct a complete heap.

**Q14. Can Heap Sort be implemented iteratively?**

Yes. Heapify can be implemented using a loop instead of recursion, giving O(1) auxiliary space.

**Q15. What is the difference between Heap Sort and Priority Queue?**

Heap Sort uses a heap to sort all elements. A Priority Queue uses a heap to insert and retrieve elements according to priority.

## 18. Practice Problems

### Beginner

1. Implement Heap Sort for an integer array.
2. Sort an array in descending order.
3. Build a Max Heap from an unsorted array.
4. Build a Min Heap from an unsorted array.
5. Find the largest element using a Max Heap.
6. Find the smallest element using a Min Heap.

### Intermediate

1. Implement iterative heapify.
2. Implement recursive heapify.
3. Find the K-th largest element using a Max Heap.
4. Find the K-th smallest element using a Min Heap.
5. Merge K sorted arrays using a Priority Queue.
6. Sort an array of objects based on priority.

### Advanced

1. Implement a Binary Heap from scratch.
2. Implement a Priority Queue using a Binary Heap.
3. Find the top K frequent elements.
4. Find the median from a continuous stream of numbers using two heaps.
5. Implement Heap Sort using a generic comparator.
6. Compare Heap Sort and Quick Sort on different input distributions.

## 19. Quick Revision Cheat Sheet

| Concept | Answer |
|---|---|
| Algorithm | Heap Sort |
| Data Structure | Binary Heap |
| Ascending Sort | Max Heap |
| Descending Sort | Min Heap |
| Main Operations | Build Heap, Extract Root, Heapify |
| Build Heap | O(n) |
| Heapify | O(log n) |
| Best Time | O(n log n) |
| Average Time | O(n log n) |
| Worst Time | O(n log n) |
| Auxiliary Space | O(1) iterative |
| Recursive Stack | O(log n) |
| Stable | No |
| In-place | Yes |
| Adaptive | No |
| Best Use Case | Predictable sorting with limited extra memory |

### Remember

**Heap Sort = Build Heap → Extract Root → Heapify → Repeat**

For ascending order:

1. Build a Max Heap.
2. Swap the root with the last element in the active heap.
3. Reduce the heap size.
4. Heapify the root.
5. Repeat until sorted.

The most important concepts to understand are:

- Binary Heap
- Max Heap and Min Heap
- Array representation of a heap
- Heapify
- Build Heap
- Extract Maximum

Once these concepts are clear, Heap Sort becomes much easier to understand and implement.

