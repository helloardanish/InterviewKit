---
title: "Priority Queue"
layout: default
parent: Data Structures & Algorithms
nav_order: 8
permalink: /data-structures/Priority-Queue/priority-queue/

topic: Priority Queue
section: Heap-Based Data Structures
concepts: [priority, min-priority-queue, max-priority-queue]
implementations: [binary-heap, array, linked-list]
java_classes: [PriorityQueue]
complexities: [O(1), O(log n), O(n)]
tags: [data-structures, priority-queue, heap, java]
---

# Priority Queue

## 1. Introduction

A **Priority Queue** is a special type of Queue in which every element has a priority.

In a normal Queue, the person who comes first is served first (FIFO – First In, First Out).

But in a Priority Queue, the element with the **highest priority is served first**, even if it arrived later.

### Real-Life Example: Hospital Emergency

Imagine people waiting outside a hospital.

Not every patient has the same medical condition. A patient with a serious heart problem or heavy bleeding may need immediate treatment, while someone with a fever may be able to wait.

The hospital should treat patients according to their medical priority, not simply according to who arrived first.

For this example, assume:

- Higher number = Higher priority.
- Priority `9` is more urgent than priority `6`.
- Priority `6` is more urgent than priority `4`.
- Priority `4` is more urgent than priority `2`.
- If two patients have the same priority, the one who arrived first is served first (FIFO).

## 2. Visual Representation

![Priority Queue]({{ '/00-Data-Structure/09-Priority-Queue/priority-queue.png' | relative_url }})

## 3. Understanding Priority Queue Operations

Let's understand how a Priority Queue works using our hospital example.

### Step 1: First patient arrives

```text
Enqueue(P -> Fever, 2)

Peek -> Patient who has fever
```

The queue contains:

```text
Fever (2)
```

The fever patient is currently the highest-priority patient.

### Step 2: Second patient arrives

```text
Enqueue(P -> Eye, 4)

Peek -> Patient who has eye problem
```

The queue contains:

```text
Eye   (4)  <- Highest Priority
Fever (2)
```

Although the eye patient arrived later, their priority is higher.

### Step 3: Third patient arrives

```text
Enqueue(P -> Heart, 9)

Peek -> Patient who has heart problem
```

The queue contains:

```text
Heart (9)  <- Highest Priority
Eye   (4)
Fever (2)
```

The heart patient is now served first because priority `9` is the highest.

### Step 4: Fourth patient arrives

```text
Enqueue(P -> Liver, 6)

Peek -> Patient who has heart problem
```

The queue contains:

```text
Heart (9)  <- Highest Priority
Liver (6)
Eye   (4)
Fever (2)
```

The heart patient remains at the front because priority `9` is still the highest.

### Step 5: Serve the highest-priority patient

```text
Dequeue()

Peek -> Patient who has liver problem
```

The heart patient is removed from the queue.

The queue now contains:

```text
Liver (6)  <- Highest Priority
Eye   (4)
Fever (2)
```

The liver patient is now at the front.

### Step 6: A new emergency patient arrives

```text
Enqueue(P -> Bleeding, 9)

Peek -> Patient who is bleeding
```

The queue becomes:

```text
Bleeding (9)  <- Highest Priority
Liver    (6)
Eye      (4)
Fever    (2)
```

Although the bleeding patient arrived last, they are served first because they have the highest priority.

### Final result

If we repeatedly call `Dequeue()`, the patients will be served in this order:

```text
1. Bleeding (9)
2. Liver    (6)
3. Eye      (4)
4. Fever    (2)
```

**Important:** The priority queue does not simply follow arrival order. It follows priority order.

---

## 4. Priority Queue Terminology

| Term | Meaning |
|---|---|
| Priority | Importance assigned to an element |
| Enqueue | Insert an element into the Priority Queue |
| Dequeue | Remove the highest-priority element |
| Peek | View the highest-priority element without removing it |
| Highest Priority | Element that should be processed first |
| Tie | Two or more elements have equal priority |

In Java, the corresponding methods are:

| Concept | Java method |
|---|---|
| Enqueue | `offer()` |
| Dequeue | `poll()` |
| Peek | `peek()` |

Java's `PriorityQueue` does not provide methods literally named `enqueue()` and `dequeue()`.

## 5. Types of Priority Queue

There are two common types of Priority Queue.

### 5.1 Max Priority Queue

The element with the highest priority value is served first.

Example:

```text
Insert:  2, 4, 9, 6

Priority Queue:

9  <- Highest Priority
6
4
2
```

Dequeue order:

```text
9 -> 6 -> 4 -> 2
```

Our hospital example uses a Max Priority Queue.

### 5.2 Min Priority Queue

The element with the lowest priority value is served first.

Example:

```text
Insert:  2, 4, 9, 6

Priority Queue:

2  <- Highest Priority (smallest value)
4
6
9
```

Dequeue order:

```text
2 -> 4 -> 6 -> 9
```

A Min Priority Queue is useful when smaller values represent greater importance, such as processing the shortest job first.

**Remember:** Whether a larger or smaller number means higher priority depends on the application's rules.

---

## 6. Priority Queue in Java

Java provides a built-in `PriorityQueue` class in the `java.util` package.

```java
import java.util.PriorityQueue;
```

By default, Java's `PriorityQueue` is a **Min Priority Queue**.

That means the smallest element comes first.

### 6.1 Min Priority Queue Example

```java
import java.util.PriorityQueue;

public class Main {

    public static void main(String[] args) {

        PriorityQueue<Integer> queue = new PriorityQueue<>();

        queue.offer(2);
        queue.offer(4);
        queue.offer(9);
        queue.offer(6);

        System.out.println(queue.peek()); // 2

        while (!queue.isEmpty()) {
            System.out.println(queue.poll());
        }
    }
}
```

Output:

```text
2
2
4
6
9
```

Notice that the elements are removed in ascending order.

### 6.2 Max Priority Queue Example

If we want the largest element to come first, we can use `Collections.reverseOrder()`.

```java
import java.util.Collections;
import java.util.PriorityQueue;

public class Main {

    public static void main(String[] args) {

        PriorityQueue<Integer> queue =
                new PriorityQueue<>(Collections.reverseOrder());

        queue.offer(2);
        queue.offer(4);
        queue.offer(9);
        queue.offer(6);

        System.out.println(queue.peek()); // 9

        while (!queue.isEmpty()) {
            System.out.println(queue.poll());
        }
    }
}
```

Output:

```text
9
6
4
2
```

### 6.3 Priority Queue with Custom Objects

In real applications, we often store objects instead of simple numbers.

For example, a hospital needs to store:

- Patient name
- Medical condition
- Priority

Let's create a `Patient` class.

```java
class Patient {

    String name;
    String problem;
    int priority;

    public Patient(String name, String problem, int priority) {
        this.name = name;
        this.problem = problem;
        this.priority = priority;
    }

    @Override
    public String toString() {
        return name + " - " + problem + " - Priority: " + priority;
    }
}
```

Now create a Priority Queue that serves patients with higher priority first.

```java
import java.util.PriorityQueue;

public class Main {

    public static void main(String[] args) {

        PriorityQueue<Patient> queue =
                new PriorityQueue<>(
                    (p1, p2) -> Integer.compare(p2.priority, p1.priority)
                );

        queue.offer(new Patient("P1", "Fever", 2));
        queue.offer(new Patient("P2", "Eye Problem", 4));
        queue.offer(new Patient("P3", "Heart Problem", 9));
        queue.offer(new Patient("P4", "Liver Problem", 6));

        System.out.println("Next Patient:");
        System.out.println(queue.peek());

        System.out.println("\nServing Patients:");

        while (!queue.isEmpty()) {
            System.out.println(queue.poll());
        }
    }
}
```

Output:

```text
Next Patient:
P3 - Heart Problem - Priority: 9

Serving Patients:
P3 - Heart Problem - Priority: 9
P4 - Liver Problem - Priority: 6
P2 - Eye Problem - Priority: 4
P1 - Fever - Priority: 2
```

**Important:** `Integer.compare(p2.priority, p1.priority)` reverses the comparison, making the largest priority number come first. It is safer than subtracting the values because subtraction can overflow.

---

## 7. Handling Patients with Equal Priority

What happens if two patients have the same priority?

For example:

```text
Patient A -> Heart Problem -> Priority 9
Patient B -> Bleeding      -> Priority 9
```

If we want to serve the patient who arrived first, we need an additional sequence number.

This is called **FIFO ordering for equal priorities**.

```java
import java.util.PriorityQueue;

class Patient {

    String name;
    String problem;
    int priority;
    long arrivalOrder;

    public Patient(String name, String problem,
                   int priority, long arrivalOrder) {

        this.name = name;
        this.problem = problem;
        this.priority = priority;
        this.arrivalOrder = arrivalOrder;
    }

    @Override
    public String toString() {
        return name + " - " + problem + " - Priority: " + priority;
    }
}

public class Main {

    public static void main(String[] args) {

        PriorityQueue<Patient> queue =
                new PriorityQueue<>((p1, p2) -> {

                    // Higher priority first
                    int result = Integer.compare(
                            p2.priority, p1.priority
                    );

                    // Earlier arrival first when priorities are equal
                    if (result == 0) {
                        return Long.compare(
                                p1.arrivalOrder, p2.arrivalOrder
                        );
                    }

                    return result;
                });

        queue.offer(new Patient("P1", "Heart Problem", 9, 1));
        queue.offer(new Patient("P2", "Bleeding", 9, 2));
        queue.offer(new Patient("P3", "Fever", 2, 3));

        while (!queue.isEmpty()) {
            System.out.println(queue.poll());
        }
    }
}
```

Output:

```text
P1 - Heart Problem - Priority: 9
P2 - Bleeding - Priority: 9
P3 - Fever - Priority: 2
```

P1 is served before P2 because both have priority `9`, but P1 arrived earlier.

Note: Java's `PriorityQueue` does not guarantee FIFO ordering for equal-priority elements unless we explicitly include a tie-breaker like this.

---

## 8. Internal Working: Heap Data Structure

How does a Priority Queue find the highest-priority element efficiently?

A common implementation uses a **Binary Heap**.

A Binary Heap is a complete binary tree that follows a special ordering rule.

There are two main types:

- Max Heap: Parent is greater than or equal to its children.
- Min Heap: Parent is smaller than or equal to its children.

### 8.1 Max Heap Example

Consider these values:

```text
         9
       /   \
      6     4
     /
    2
```

The largest value is always at the root.

Therefore:

```text
Peek() -> 9
```

When we remove `9`, the heap reorganizes itself to maintain the Max Heap property.

### 8.2 Array Representation of Heap

A Binary Heap can be stored in an array instead of creating separate tree nodes.

For example:

```text
Heap:

         9
       /   \
      6     4
     /
    2

Array:

Index:  0  1  2  3
Value: [9, 6, 4, 2]
```

For a zero-based array, the relationships are:

| Relationship | Formula |
|---|---|
| Parent | `(i - 1) / 2` |
| Left Child | `2 * i + 1` |
| Right Child | `2 * i + 2` |

Here, `i` is the index of the current element. Integer division is used for the parent formula.

### 8.3 Insertion (Offer)

When a new element is inserted:

1. Add it at the next available position.
2. Compare it with its parent.
3. If it has higher priority, swap it with its parent.
4. Continue until the heap property is restored.

This process is called **Heapify Up** or **Sift Up**.

### 8.4 Removal (Poll)

When the highest-priority element is removed:

1. Remove the root element.
2. Move the last element to the root.
3. Compare it with its children.
4. Swap it with the child having higher priority if necessary.
5. Continue until the heap property is restored.

This process is called **Heapify Down** or **Sift Down**.

---

## 9. Time and Space Complexity

For a Binary Heap-based Priority Queue:

| Operation | Time Complexity |
|---|---|
| Insert / Offer | O(log n) |
| Remove / Poll | O(log n) |
| Peek | O(1) |
| Search / Contains | O(n) |
| Build Heap | O(n) |
| Size | O(1) |
| Is Empty | O(1) |

Space Complexity:

```text
O(n)
```

Where `n` is the number of elements stored in the Priority Queue.

### Why is insertion O(log n)?

A Binary Heap is a complete binary tree.

Its height is approximately:

```text
log₂(n)
```

During insertion, an element may move from the bottom to the root.

Therefore, the maximum number of comparisons is proportional to the height of the tree.

So:

```text
Insertion = O(log n)
```

### Why is peek O(1)?

The highest-priority element is always stored at the root.

We do not need to search through the heap.

Therefore:

```text
Peek = O(1)
```

---

## 10. Applications of Priority Queue

Priority Queues are useful whenever elements need to be processed according to importance rather than arrival order.

### 10.1 Hospital Emergency Systems

Patients with more serious medical conditions are treated first.

### 10.2 CPU Scheduling

Operating systems can schedule processes based on priority.

### 10.3 Dijkstra's Shortest Path Algorithm

Dijkstra's algorithm uses a Priority Queue to efficiently select the unvisited vertex with the smallest known distance.

### 10.4 A* Search Algorithm

A* uses a Priority Queue to select the next node based on its estimated total path cost.

### 10.5 Event Scheduling

Systems can process events according to their scheduled time or importance.

### 10.6 Network Traffic Management

Network systems can prioritize important packets over less urgent traffic.

### 10.7 Task Scheduling

Applications can process urgent jobs before normal background tasks.

---

## 11. Priority Queue vs Normal Queue

| Feature | Normal Queue | Priority Queue |
|---|---|---|
| Ordering | FIFO | Based on priority |
| Removal | Oldest element first | Highest-priority element first |
| Priority | Not required | Required |
| Common implementation | Array / Linked List | Binary Heap |
| Insert | Usually O(1) | O(log n) |
| Remove | Usually O(1) | O(log n) |
| Peek | O(1) | O(1) |
| Example | Ticket counter | Hospital emergency |

Example:

Normal Queue:

```text
Insert: 2, 9, 4, 6

Remove order:

2 -> 9 -> 4 -> 6
```

Priority Queue (Max):

```text
Insert: 2, 9, 4, 6

Remove order:

9 -> 6 -> 4 -> 2
```

---

## 12. Coding Problems to Practice

### Beginner Level

1. Implement a Min Priority Queue.
2. Implement a Max Priority Queue.
3. Find the Kth largest element in an array.
4. Find the Kth smallest element in an array.
5. Find the largest K elements in an array.

### Intermediate Level

6. Merge K sorted arrays.
7. Merge K sorted linked lists.
8. Find the top K frequent elements.
9. Find the K closest points to the origin.
10. Rearrange characters so that adjacent characters are different.

### Advanced Level

11. Find the median from a data stream using two heaps.
12. Implement Dijkstra's shortest path algorithm.
13. Implement the A* search algorithm.
14. Find the smallest range covering elements from K sorted lists.
15. Find the minimum cost to connect ropes.

---

## 13. Common Interview Questions

**Q1. What is a Priority Queue?**

A Priority Queue is a data structure where elements are processed according to their priority rather than their insertion order.

**Q2. What is the difference between a Queue and a Priority Queue?**

A normal Queue follows FIFO, while a Priority Queue removes elements according to priority.

**Q3. Which data structure is commonly used to implement a Priority Queue?**

A Binary Heap is commonly used because it supports efficient insertion and removal.

**Q4. What is the difference between Min Heap and Max Heap?**

- Min Heap: The smallest element is at the root.
- Max Heap: The largest element is at the root.

**Q5. What is the default behavior of Java PriorityQueue?**

Java's `PriorityQueue` uses natural ordering by default. For integers, it behaves as a Min Priority Queue.

**Q6. How can we create a Max Priority Queue in Java?**

```java
PriorityQueue<Integer> queue =
        new PriorityQueue<>(Collections.reverseOrder());
```

**Q7. What is the time complexity of insertion and deletion?**

Both operations take O(log n) time in a Binary Heap.

**Q8. What is the time complexity of peek?**

O(1), because the highest-priority element is available at the root.

**Q9. Does Java PriorityQueue maintain insertion order?**

No. It does not guarantee FIFO ordering for elements with equal priority. We need a tie-breaker if FIFO behavior is required.

**Q10. Is PriorityQueue thread-safe?**

No. Java's standard `PriorityQueue` is not thread-safe. For concurrent applications, use an appropriate alternative such as `PriorityBlockingQueue`.

**Q11. Does iterating over Java PriorityQueue return sorted elements?**

No. Its iterator does not guarantee priority order. To retrieve elements in priority order, repeatedly call `poll()`.

**Q12. Can PriorityQueue contain null?**

No. Java's `PriorityQueue` does not permit null elements.

---

## 14. Quick Revision

| Concept | Remember |
|---|---|
| Priority Queue | Processes elements based on priority |
| Max Priority Queue | Largest priority value first |
| Min Priority Queue | Smallest value first |
| Java default | Min Priority Queue |
| Enqueue | `offer()` |
| Dequeue | `poll()` |
| Peek | `peek()` |
| Internal structure | Binary Heap |
| Insert | O(log n) |
| Remove | O(log n) |
| Peek | O(1) |
| Search | O(n) |
| Space | O(n) |

## 15. Practice Roadmap

Follow this order to understand Priority Queue properly:

1. Understand the difference between Queue and Priority Queue.
2. Practice Min Priority Queue with Java.
3. Practice Max Priority Queue with Java.
4. Create a custom `Patient` class and use a Comparator.
5. Understand how a Binary Heap works.
6. Learn Heapify Up and Heapify Down.
7. Solve Kth largest and Kth smallest element problems.
8. Practice Top K Frequent Elements.
9. Learn how Priority Queue is used in Dijkstra's algorithm.

**Final takeaway:** A normal Queue serves elements according to arrival order, whereas a Priority Queue serves elements according to importance. In Java, `PriorityQueue` is commonly implemented using a Binary Heap.