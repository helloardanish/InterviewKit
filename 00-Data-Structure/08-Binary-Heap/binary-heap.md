---
title: "Binary Heap"
layout: default
parent: Data Structures & Algorithms
nav_order: 7
permalink: /data-structures/Binary-Heap/binary-heap/

topic: Binary Heap
section: Heap-Based Data Structures
concepts: [complete-binary-tree, min-heap, max-heap, heap-property]
operations: [insert, extract-min, extract-max, peek, heapify]
complexities: [O(1), O(log n), O(n)]
tags: [data-structures, binary-heap, min-heap, max-heap, java]
---

# Binary Heap in Java

## 1. What Is a Binary Heap?

A **Binary Heap** is a special type of binary tree that follows two important rules:

1. It is a **Complete Binary Tree**.
2. It follows the **Heap Property**.

A binary heap is commonly used to implement **Priority Queues**, where elements are processed according to their priority rather than their insertion order.

For example:

Imagine a hospital emergency room. Patients arrive at different times, but the most critical patient should be treated first.

A normal queue follows FIFO (First In, First Out).

A priority queue follows priority. The most critical patient gets attention first.

A binary heap is one efficient way to implement this behavior.

---

## 2. Understanding the Binary Tree

A binary tree is a tree in which each node can have at most two children:

- Left child
- Right child

Example:

```text
          10
         /  \
        20   30
       / \
      40  50
```

Each node contains a value and can have zero, one, or two children.

A binary heap uses this binary tree structure but adds additional rules.

## 3. Complete Binary Tree

A binary heap must always be a **Complete Binary Tree**.

A complete binary tree follows these rules:

1. Every level is completely filled except possibly the last level.
2. The last level must be filled from left to right.
3. There must not be a gap between nodes on the last level.

Valid complete binary tree:

```text
          10
         /  \
        20   30
       / \   /
      40 50 60
```

Invalid complete binary tree:

```text
          10
         /  \
        20   30
         \   /
         40 50
```

Why is the second tree invalid?

Because the left child position of node 20 is empty, but its right child exists. This violates the left-to-right filling rule.

**Why does a heap need to be complete?**

A complete binary tree keeps its height small.

For `n` nodes, its height is approximately:

```text
O(log n)
```

This makes insertion and deletion efficient.

---

## 4. Types of Binary Heap

There are two common types:

1. Max Heap
2. Min Heap

### 4.1 Max Heap

In a Max Heap, every parent node must be greater than or equal to its children.

```text
          90
         /  \
        70   80
       / \   /
      40 50 60
```

Rules:

- Parent >= Left Child
- Parent >= Right Child
- The largest element is always at the root.

Example:

```text
        90
       /  \
      70   80
```

90 is greater than both children.

**Important:** A Max Heap does not mean the entire tree is sorted. The relationship is only between each parent and its children.

### 4.2 Min Heap

In a Min Heap, every parent node must be smaller than or equal to its children.

```text
          10
         /  \
        20   30
       / \   /
      40 50 60
```

Rules:

- Parent <= Left Child
- Parent <= Right Child
- The smallest element is always at the root.

### Max Heap vs Min Heap

| Feature | Max Heap | Min Heap |
|---|---|---|
| Root element | Maximum | Minimum |
| Parent-child rule | Parent >= Children | Parent <= Children |
| Useful for | Largest element first | Smallest element first |
| Java PriorityQueue | Reverse comparator | Default comparator |

---

## 5. Array Representation of a Binary Heap

One of the most useful properties of a complete binary tree is that it can be stored efficiently in an array.

We do not need separate Node objects or left/right references.

Consider this Max Heap:

```text
          90
         /  \
        70   80
       / \   /
      40 50 60
```

Its array representation is:

```java
int[] heap = {90, 70, 80, 40, 50, 60};
```

The elements are stored level by level, from left to right.

### Index Relationships

For a zero-based array, if the current node is at index `i`:

| Relationship | Formula |
|---|---|
| Parent | `(i - 1) / 2` |
| Left child | `2 * i + 1` |
| Right child | `2 * i + 2` |

Example:

```text
Array: [90, 70, 80, 40, 50, 60]

Index:   0   1   2   3   4   5
Value:  90  70  80  40  50  60
```

For the node at index 1 (value 70):

```text
Parent index = (1 - 1) / 2 = 0
Left index   = 2 * 1 + 1 = 3
Right index  = 2 * 1 + 2 = 4
```

Therefore:

```text
          90
         /
        70
       / \
      40 50
```

### Why Use an Array?

- No need to store pointers to children.
- Parent and child positions are calculated directly.
- Memory is used efficiently.
- The complete-tree structure avoids gaps in the array.

---

## 6. Important Binary Heap Operations

The main operations are:

| Operation | Description | Time Complexity |
|---|---|---|
| Peek | Read the root element | O(1) |
| Insert | Add a new element | O(log n) |
| Delete root | Remove the root element | O(log n) |
| Heapify | Restore the heap property | O(log n) |
| Build Heap | Convert an array into a heap | O(n) |
| Search | Find an arbitrary value | O(n) |

Let's understand each operation.

## 7. Insertion in a Max Heap

When inserting an element:

1. Add the new element at the end of the array.
2. Compare it with its parent.
3. If it is greater than its parent, swap them.
4. Continue until the Max Heap property is restored.

This process is called **Heapify Up**, **Sift Up**, or **Bubble Up**.

### Example

Initial Max Heap:

```text
          50
         /  \
        30   40
       / \
      10 20
```

Array:

```java
[50, 30, 40, 10, 20]
```

Insert `60`.

**Step 1: Add 60 at the end.**

```text
          50
         /  \
        30   40
       / \   /
      10 20 60
```

Array:

```java
[50, 30, 40, 10, 20, 60]
```

**Step 2: Compare 60 with its parent (40).**

60 is greater than 40, so swap them.

```text
          50
         /  \
        30   60
       / \   /
      10 20 40
```

**Step 3: Compare 60 with its new parent (50).**

60 is greater than 50, so swap again.

```text
          60
         /  \
        30   50
       / \   /
      10 20 40
```

Final array:

```java
[60, 30, 50, 10, 20, 40]
```

The Max Heap property is restored.

### Java Implementation: Insert into Max Heap

```java
import java.util.ArrayList;
import java.util.List;

public class MaxHeap {

    private final List<Integer> heap = new ArrayList<>();

    public void insert(int value) {
        heap.add(value);

        int currentIndex = heap.size() - 1;

        while (currentIndex > 0) {
            int parentIndex = (currentIndex - 1) / 2;

            if (heap.get(parentIndex) >= heap.get(currentIndex)) {
                break;
            }

            swap(parentIndex, currentIndex);

            currentIndex = parentIndex;
        }
    }

    private void swap(int i, int j) {
        int temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }

    public List<Integer> getHeap() {
        return new ArrayList<>(heap);
    }
}
```

Usage:

```java
public class Main {

    public static void main(String[] args) {

        MaxHeap maxHeap = new MaxHeap();

        maxHeap.insert(50);
        maxHeap.insert(30);
        maxHeap.insert(40);
        maxHeap.insert(10);
        maxHeap.insert(20);
        maxHeap.insert(60);

        System.out.println(maxHeap.getHeap());
    }
}
```

Output:

```text
[60, 30, 50, 10, 20, 40]
```

**Time Complexity:** O(log n)

In the worst case, the new element travels from the last level to the root.

---

## 8. Deletion from a Max Heap

In a Max Heap, deletion usually means removing the root element (the maximum value).

Steps:

1. Save the root element.
2. Move the last element to the root.
3. Remove the last array position.
4. Compare the new root with its children.
5. Swap it with the larger child if necessary.
6. Continue until the heap property is restored.

This process is called **Heapify Down**, **Sift Down**, or **Bubble Down**.

### Example

Initial Max Heap:

```text
          90
         /  \
        70   80
       / \   /
      40 50 60
```

Array:

```java
[90, 70, 80, 40, 50, 60]
```

**Step 1: Remove the root (90).**

**Step 2: Move the last element (60) to the root.**

```text
          60
         /  \
        70   80
       / \
      40 50
```

Array:

```java
[60, 70, 80, 40, 50]
```

**Step 3: Compare 60 with its children.**

Children are 70 and 80.

The larger child is 80.

Swap 60 and 80.

```text
          80
         /  \
        70   60
       / \
      40 50
```

Final array:

```java
[80, 70, 60, 40, 50]
```

The Max Heap property is restored.

### Java Implementation: Delete Root

Add the following methods to the `MaxHeap` class:

```java
public Integer peek() {
    return heap.isEmpty() ? null : heap.get(0);
}

public Integer deleteRoot() {

    if (heap.isEmpty()) {
        return null;
    }

    int root = heap.get(0);

    int lastElement = heap.remove(heap.size() - 1);

    if (!heap.isEmpty()) {
        heap.set(0, lastElement);
        heapifyDown(0);
    }

    return root;
}

private void heapifyDown(int index) {

    int size = heap.size();

    while (true) {

        int largest = index;

        int left = 2 * index + 1;
        int right = 2 * index + 2;

        if (left < size && heap.get(left) > heap.get(largest)) {
            largest = left;
        }

        if (right < size && heap.get(right) > heap.get(largest)) {
            largest = right;
        }

        if (largest == index) {
            break;
        }

        swap(index, largest);

        index = largest;
    }
}
```

Usage:

```java
MaxHeap maxHeap = new MaxHeap();

maxHeap.insert(90);
maxHeap.insert(70);
maxHeap.insert(80);
maxHeap.insert(40);
maxHeap.insert(50);
maxHeap.insert(60);

System.out.println(maxHeap.peek());       // 90
System.out.println(maxHeap.deleteRoot()); // 90
System.out.println(maxHeap.getHeap());    // [80, 70, 60, 40, 50]
```

**Time Complexity:** O(log n)

The element may move from the root to the last level.

---

## 9. Heapify: Understanding Sift Down

Heapify is the process of restoring the heap property when a node violates it.

For a Max Heap:

- Compare the parent with its children.
- Find the largest among them.
- Swap if a child is larger than the parent.
- Continue down the tree.

Example:

```text
          10
         /  \
        30   20
```

The parent (10) violates the Max Heap property.

Compare:

```text
10, 30, 20
```

The largest is 30.

Swap 10 and 30:

```text
          30
         /  \
        10   20
```

Now the heap property is restored.

### Recursive Heapify

```java
private void heapify(int[] arr, int size, int index) {

    int largest = index;

    int left = 2 * index + 1;
    int right = 2 * index + 2;

    if (left < size && arr[left] > arr[largest]) {
        largest = left;
    }

    if (right < size && arr[right] > arr[largest]) {
        largest = right;
    }

    if (largest != index) {

        int temp = arr[index];
        arr[index] = arr[largest];
        arr[largest] = temp;

        heapify(arr, size, largest);
    }
}
```

**Time Complexity:** O(log n)

**Auxiliary Space:** O(log n) for the recursive call stack in the worst case.

An iterative implementation can perform the same operation using O(1) auxiliary space.

---

## 10. Building a Heap from an Existing Array

Suppose we have this array:

```java
int[] arr = {4, 10, 3, 5, 1};
```

We want to convert it into a Max Heap.

Instead of inserting elements one by one, we can build a heap efficiently using the bottom-up approach.

### Step 1: Find the Last Non-Leaf Node

For an array of size `n`:

```java
int lastNonLeaf = n / 2 - 1;
```

For five elements:

```text
n = 5

lastNonLeaf = 5 / 2 - 1
            = 1
```

Indexes 0 and 1 are non-leaf nodes.

Indexes 2, 3 and 4 are leaves.

### Step 2: Heapify from Right to Left

Start at index 1 and move towards index 0.

Initial array:

```text
[4, 10, 3, 5, 1]
```

Heapify index 1:

```text
          4
         / \
        10  3
       / \
      5   1
```

10 is already larger than 5 and 1, so no change.

Heapify index 0:

```text
          4
         / \
        10  3
```

10 is larger than 4, so swap.

```text
          10
         /  \
        4    3
       / \
      5   1
```

The node 4 now has child 5, which is larger. Swap again.

Final heap:

```text
          10
         /  \
        5    3
       / \
      4   1
```

Array:

```java
[10, 5, 3, 4, 1]
```

### Java Implementation: Build Max Heap

```java
public static void buildMaxHeap(int[] arr) {

    int n = arr.length;

    // Start from the last non-leaf node.
    for (int i = n / 2 - 1; i >= 0; i--) {
        heapify(arr, n, i);
    }
}
```

**Time Complexity: O(n)**

This is an important interview question.

Why isn't it O(n log n)?

Although an individual heapify operation can take O(log n), most nodes are close to the bottom and can move only a small distance.

The total work across all nodes is O(n).

---

## 11. Complete Java Max Heap Implementation

The following implementation combines the main operations:

```java
import java.util.ArrayList;
import java.util.List;

public class MaxHeap {

    private final List<Integer> heap = new ArrayList<>();

    public int size() {
        return heap.size();
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    // Insert a value.
    public void insert(int value) {

        heap.add(value);

        int index = heap.size() - 1;

        // Sift Up.
        while (index > 0) {

            int parent = (index - 1) / 2;

            if (heap.get(parent) >= heap.get(index)) {
                break;
            }

            swap(parent, index);

            index = parent;
        }
    }

    // Get the maximum element.
    public Integer peek() {
        return heap.isEmpty() ? null : heap.get(0);
    }

    // Remove and return the maximum element.
    public Integer extractMax() {

        if (heap.isEmpty()) {
            return null;
        }

        int max = heap.get(0);

        int last = heap.remove(heap.size() - 1);

        if (!heap.isEmpty()) {
            heap.set(0, last);
            siftDown(0);
        }

        return max;
    }

    // Restore heap property downwards.
    private void siftDown(int index) {

        int size = heap.size();

        while (true) {

            int largest = index;

            int left = 2 * index + 1;
            int right = 2 * index + 2;

            if (left < size &&
                    heap.get(left) > heap.get(largest)) {
                largest = left;
            }

            if (right < size &&
                    heap.get(right) > heap.get(largest)) {
                largest = right;
            }

            if (largest == index) {
                break;
            }

            swap(index, largest);

            index = largest;
        }
    }

    private void swap(int i, int j) {

        int temp = heap.get(i);

        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }

    public void printHeap() {
        System.out.println(heap);
    }

    public static void main(String[] args) {

        MaxHeap maxHeap = new MaxHeap();

        maxHeap.insert(40);
        maxHeap.insert(20);
        maxHeap.insert(60);
        maxHeap.insert(10);
        maxHeap.insert(50);

        maxHeap.printHeap();

        System.out.println("Maximum: " + maxHeap.peek());

        System.out.println("Removed: " + maxHeap.extractMax());

        maxHeap.printHeap();
    }
}
```

Possible output:

```text
[60, 50, 40, 10, 20]
Maximum: 60
Removed: 60
[50, 20, 40, 10]
```

---

## 12. Min Heap Implementation

A Min Heap works similarly to a Max Heap, but the comparisons are reversed.

For insertion:

```java
if (heap.get(parent) <= heap.get(index)) {
    break;
}
```

For heapify down, find the smallest among the parent and its children:

```java
private void siftDown(int index) {

    int size = heap.size();

    while (true) {

        int smallest = index;

        int left = 2 * index + 1;
        int right = 2 * index + 2;

        if (left < size &&
                heap.get(left) < heap.get(smallest)) {
            smallest = left;
        }

        if (right < size &&
                heap.get(right) < heap.get(smallest)) {
            smallest = right;
        }

        if (smallest == index) {
            break;
        }

        swap(index, smallest);

        index = smallest;
    }
}
```

The smallest element is always at index 0.

---

## 13. Java PriorityQueue

Java provides a built-in `PriorityQueue` class.

It is implemented using a priority-queue data structure backed by a heap.

By default, Java's PriorityQueue behaves as a **Min Heap**.

### Min Heap Example

```java
import java.util.PriorityQueue;

public class Main {

    public static void main(String[] args) {

        PriorityQueue<Integer> pq = new PriorityQueue<>();

        pq.offer(40);
        pq.offer(10);
        pq.offer(30);
        pq.offer(20);

        System.out.println(pq.peek()); // 10

        System.out.println(pq.poll()); // 10
        System.out.println(pq.poll()); // 20
        System.out.println(pq.poll()); // 30
        System.out.println(pq.poll()); // 40
    }
}
```

Output:

```text
10
10
20
30
40
```

### Max Heap Using PriorityQueue

To create a Max Heap:

```java
PriorityQueue<Integer> maxHeap =
        new PriorityQueue<>((a, b) -> Integer.compare(b, a));
```

Example:

```java
PriorityQueue<Integer> maxHeap =
        new PriorityQueue<>((a, b) -> Integer.compare(b, a));

maxHeap.offer(40);
maxHeap.offer(10);
maxHeap.offer(30);
maxHeap.offer(20);

System.out.println(maxHeap.peek()); // 40

while (!maxHeap.isEmpty()) {
    System.out.println(maxHeap.poll());
}
```

Output:

```text
40
30
20
10
```

**Important:** Avoid implementing descending comparison as `b - a`. Integer subtraction can overflow. Use `Integer.compare(b, a)`.

### PriorityQueue Methods

| Method | Description | Complexity |
|---|---|---|
| `offer(e)` | Insert element | O(log n) |
| `add(e)` | Insert element | O(log n) |
| `peek()` | Read root | O(1) |
| `poll()` | Remove root | O(log n) |
| `remove()` | Remove root or throw if empty | O(log n) |
| `isEmpty()` | Check whether empty | O(1) |
| `size()` | Get element count | O(1) |
| `contains(e)` | Search for an element | O(n) |

### Important PriorityQueue Behavior

A PriorityQueue does not keep all elements sorted internally.

For example:

```java
PriorityQueue<Integer> pq = new PriorityQueue<>();

pq.offer(40);
pq.offer(10);
pq.offer(30);
pq.offer(20);

System.out.println(pq);
```

Do not expect the printed representation to be fully sorted.

Only the heap property is guaranteed. The root is the minimum element in a default PriorityQueue.

To get elements in priority order, repeatedly call `poll()`.

---

## 14. Binary Heap vs Binary Search Tree

These two data structures are often confused.

| Feature | Binary Heap | Binary Search Tree |
|---|---|---|
| Structure | Complete binary tree | Depends on insertion and balancing |
| Ordering | Parent-child heap property | Left < Parent < Right (for distinct keys) |
| Root | Min or Max | Depends on inserted values |
| Find minimum | O(1) in Min Heap | O(h) |
| Find maximum | O(1) in Max Heap | O(h) |
| Search arbitrary value | O(n) | O(h) |
| Insert | O(log n) | O(h) |
| Delete root / extract priority | O(log n) | Depends on operation |
| In-order traversal | Not necessarily sorted | Sorted for a valid BST |

Here, `h` is the tree height.

For a balanced BST, `h = O(log n)`.

For an ordinary unbalanced BST, the worst-case height can be O(n).

**Remember:**

- Use a Heap when you frequently need the minimum or maximum element.
- Use a BST when you need ordered searching, range queries, or sorted traversal.

---

## 15. Binary Heap Time and Space Complexity

| Operation | Time Complexity |
|---|---|
| Peek | O(1) |
| Insert | O(log n) |
| Extract Min / Max | O(log n) |
| Heapify one node | O(log n) |
| Build Heap | O(n) |
| Search arbitrary element | O(n) |
| Delete arbitrary element by known index | O(log n), if repaired correctly |
| Delete arbitrary element by value | O(n) search + O(log n) repair |

### Space Complexity

A binary heap storing `n` elements requires:

```text
O(n)
```

space.

For a heap stored in an array:

- The array uses O(n) space.
- Iterative sift-up and sift-down use O(1) auxiliary space.
- Recursive heapify can use O(log n) call-stack space.

---

## 16. Real-World Applications of Binary Heap

### 1. Hospital Emergency Management

Critical patients should be treated before less critical patients.

A priority queue can help select the next patient based on urgency.

### 2. CPU Task Scheduling

Tasks can be selected based on priority.

A higher-priority task can be processed before a lower-priority task.

### 3. Dijkstra's Shortest Path Algorithm

Dijkstra's algorithm uses a priority queue to efficiently select the vertex with the smallest tentative distance.

### 4. Event Scheduling

Systems can process the next scheduled event based on its execution time.

A Min Heap is useful because the earliest event stays at the root.

### 5. Top K Problems

Suppose we have one million numbers and need the ten largest.

A Min Heap of size ten can help solve this without sorting all one million numbers.

Time complexity:

```text
O(n log k)
```

where:

- `n` = total number of elements
- `k` = number of largest elements required

### 6. Merge K Sorted Lists

A Min Heap can efficiently select the smallest current element among multiple sorted lists.

---

## 17. Common Mistakes

1. **Thinking a heap is a sorted array:** Only the heap property is guaranteed.
2. **Confusing Max Heap and Min Heap:** Max Heap keeps the largest element at the root; Min Heap keeps the smallest.
3. **Forgetting the complete-tree rule:** The last level must be filled from left to right.
4. **Using incorrect array indexes:** Remember the zero-based formulas.
5. **Starting Build Heap at the wrong index:** Start at `n / 2 - 1`.
6. **Building a heap through repeated insertion when O(n) is needed:** Bottom-up Build Heap takes O(n).
7. **Assuming arbitrary search is O(log n):** Searching for an arbitrary value is O(n) in the general case.
8. **Assuming PriorityQueue iteration is sorted:** Use repeated `poll()` to retrieve elements in priority order.
9. **Using subtraction in a comparator:** Use `Integer.compare()` to avoid overflow.

---

## 18. Interview Questions and Answers

### Q1. What is a Binary Heap?

A Binary Heap is a complete binary tree that follows either the Max Heap or Min Heap property.

### Q2. What is the difference between Max Heap and Min Heap?

In a Max Heap, the parent is greater than or equal to its children. In a Min Heap, the parent is smaller than or equal to its children.

### Q3. Why is a Binary Heap a Complete Binary Tree?

A complete tree keeps its height O(log n), enabling efficient insertion and deletion. It can also be stored compactly in an array.

### Q4. Why is insertion O(log n)?

A new element is added at the end and may move up through the tree to the root. The height of a complete binary tree is O(log n).

### Q5. Why is deletion O(log n)?

After removing the root, the last element moves to the root and may move down through at most the height of the tree.

### Q6. Why is peek O(1)?

The minimum or maximum element is always stored at the root, which is at index 0 in an array representation.

### Q7. Why is Build Heap O(n), not O(n log n)?

Most nodes are near the bottom and require little or no movement. Summing the work across all nodes gives O(n).

### Q8. Can we search for an arbitrary element in O(log n)?

Not in a general Binary Heap. The heap property only provides a parent-child ordering, not a complete left-to-right search ordering. Searching can take O(n).

### Q9. Is a Binary Heap stable?

No. A standard Binary Heap does not guarantee that equal-priority elements preserve their insertion order.

If stable priority processing is required, add a sequence number as a tie-breaker.

### Q10. What is the difference between Heapify and Build Heap?

- Heapify restores the heap property for a particular node, assuming its child subtrees are already heaps.
- Build Heap converts an entire array into a valid heap using bottom-up heapification.

### Q11. Is Java PriorityQueue a Min Heap or Max Heap?

By default, it behaves as a Min Heap. A reverse comparator can make it behave as a Max Heap.

### Q12. Can a Binary Heap contain duplicate values?

Yes. Duplicate values are allowed. The parent-child relationship can use `>=` or `<=`, so equal values do not violate the heap property.

### Q13. What is the height of a Binary Heap?

For `n` nodes, the height is O(log n).

### Q14. What happens if we insert an element into an already full fixed-size heap?

A fixed-size array implementation must either reject the insertion or resize its storage. A dynamically growing array can expand when necessary.

### Q15. What is the difference between Heap Sort and a Binary Heap?

A Binary Heap is a data structure. Heap Sort is a sorting algorithm that uses a heap to sort elements.

---

## 19. Practice Problems

Try these problems in increasing difficulty.

### Beginner

1. Implement a Max Heap using an array.
2. Implement a Min Heap using an array.
3. Insert elements into a Max Heap.
4. Delete the root from a Min Heap.
5. Find the maximum element in a Max Heap.
6. Convert an array into a Max Heap.

### Intermediate

7. Find the Kth largest element in an array.
8. Find the Kth smallest element in an array.
9. Find the top K frequent elements.
10. Merge K sorted arrays.
11. Find the median from a data stream using two heaps.
12. Find the K closest points to the origin.

### Advanced

13. Implement a priority queue from scratch.
14. Merge K sorted linked lists.
15. Find the smallest range covering elements from K sorted lists.
16. Design a task scheduler using a priority queue.

---

## 20. Quick Revision Cheat Sheet

| Concept | Remember |
|---|---|
| Binary Heap | Complete Binary Tree + Heap Property |
| Max Heap | Largest element at root |
| Min Heap | Smallest element at root |
| Parent index | `(i - 1) / 2` |
| Left child | `2 * i + 1` |
| Right child | `2 * i + 2` |
| Last non-leaf index | `n / 2 - 1` |
| Insert | O(log n) |
| Delete root | O(log n) |
| Peek | O(1) |
| Search | O(n) |
| Build Heap | O(n) |
| Storage | O(n) |
| Java default PriorityQueue | Min Heap |

### Most Important Things to Remember

- A Binary Heap is not a fully sorted tree.
- A Max Heap gives quick access to the maximum element.
- A Min Heap gives quick access to the minimum element.
- Insertion uses Sift Up.
- Root deletion uses Sift Down.
- Build Heap takes O(n).
- Java PriorityQueue is a practical implementation for priority-based processing.
- Heap Sort uses a Binary Heap, but they are not the same thing.

**Recommended learning order:**

1. Understand complete binary trees.
2. Learn array representation and index formulas.
3. Implement insertion.
4. Implement deletion and heapify.
5. Implement Build Heap.
6. Practice Java PriorityQueue.
7. Solve Top K and K-way merge problems.