---
title: "Queue"
layout: default
parent: Data Structures & Algorithms
nav_order: 4
permalink: /data-structures/Queue/queue/

topic: Queue
section: Linear Data Structures
concepts: [fifo, enqueue, dequeue, front, rear]
implementations: [array, linked-list, circular-queue, deque]
complexities: [O(1), O(n)]
tags: [data-structures, queue, fifo, java]
---

# Queue Data Structure in Java

A beginner-friendly guide to understanding, implementing, using, and solving coding problems with Queue in Java.

---

# 1. Introduction to Queue

## What is a Queue?

A **Queue** is a linear data structure that follows the **FIFO (First In, First Out)** principle.

In simple words:

> The element that we insert first will be the first element to be removed.

### Real-world example: People standing in a line

Imagine that people are standing in a queue to purchase movie tickets.

Three people are standing in a line:

- Person 1
- Person 2
- Person 3

Person 1 arrives first, Person 2 arrives second, and Person 3 arrives third.

Who will get the ticket first?

Obviously, Person 1!

Because Person 1 arrived first.

When Person 1 leaves, Person 2 moves forward, followed by Person 3.

This is exactly how a Queue works.

### Understanding FIFO

FIFO means **First In, First Out**.

Consider inserting three elements:

1. Insert 10.
2. Insert 20.
3. Insert 30.

Now remove the elements:

1. 10 is removed first.
2. 20 is removed second.
3. 30 is removed last.

Remember: The first element inserted is the first element removed.

### What are FRONT and REAR?

A Queue has two important ends:

- **FRONT:** The position from which elements are removed.
- **REAR:** The position at which new elements are inserted.

The fundamental operations happen at these two ends:

- Insertion happens at REAR.
- Removal happens at FRONT.
- Viewing the next element happens at FRONT.

Unlike a Stack, where insertion and removal happen at the same end, a Queue uses two different ends.

---

# 2. Visual Representation of Queue

Before writing Java code, let's understand how a Queue looks visually.

## Figure 1: Queue Data Structure – Our Illustration

![Queue]({{ '/00-Data-Structure/05-Queue/queue.png' | relative_url }})

This illustration represents how elements are arranged in a Queue.

Notice the two important positions:

- FRONT: The first element waiting to be removed.
- REAR: The last element where new elements are inserted.

When a new element arrives, it is added at REAR.

When an element is removed, it is removed from FRONT.

## Figure 2: Queue Representation and Operations

![Queue Representation]({{ '/00-Data-Structure/05-Queue/Queue-Representation.png' | relative_url }})

This diagram illustrates the working of Queue operations.

Consider the following example:

```text
Initial Queue:

FRONT                       REAR
  |                           |
  v                           v
+------+------+------+
|  10  |  20  |  30  |
+------+------+------+
```

After inserting 40:

```text
FRONT                              REAR
  |                                  |
  v                                  v
+------+------+------+------+ 
|  10  |  20  |  30  |  40  |
+------+------+------+------+
```

After removing an element:

```text
       FRONT                  REAR
         |                      |
         v                      v
    +------+------+------+
    |  20  |  30  |  40  |
    +------+------+------+
```

Notice that 10 was removed because it was the first element inserted.

## Figure 3: Queue Time and Space Complexity

![Queue Time and Space Complexity]({{ '/00-Data-Structure/05-Queue/Queue-Time-Space-Complexity.png' | relative_url }})

This diagram provides a quick overview of the time and space complexity of Queue operations.

For a typical Queue implementation:

- Insertion: O(1)
- Removal: O(1)
- Peek: O(1)
- Search: O(n)

The actual complexity can vary depending on the implementation. For example, `PriorityQueue` has different insertion and removal complexities.

We will understand this in detail later.

## Figure 4: Queue UML Class Diagram

![Queue UML]({{ '/00-Data-Structure/05-Queue/Queue-UML.png' | relative_url }})

This UML diagram provides a technical representation of Queue and its operations.

In Java, `Queue` is an interface rather than a concrete class.

Different classes, such as `LinkedList`, `ArrayDeque`, and `PriorityQueue`, implement the Queue interface.

---

# 3. Basic Queue Operations

Let's understand the fundamental operations using our people-standing-in-a-line example.

## 3.1 enqueue() – Insert an element

The `enqueue()` operation means adding a new element at the REAR of the Queue.

For example:

```text
Queue:

FRONT                 REAR
  |                     |
  v                     v
+------+------+------+
|  10  |  20  |  30  |
+------+------+------+
```

Now insert 40.

```text
Queue:

FRONT                        REAR
  |                            |
  v                            v
+------+------+------+------+
|  10  |  20  |  30  |  40  |
+------+------+------+------+
```

In Java, the Queue interface does not have a method named `enqueue()`.

Instead, we use:

```java
queue.offer(40);
```

Or:

```java
queue.add(40);
```

Both insert an element into the Queue, but they handle insertion failure differently.

We will understand the difference later.

## 3.2 dequeue() – Remove an element

The `dequeue()` operation means removing an element from the FRONT of the Queue.

Consider:

```text
FRONT                        REAR
  |                            |
  v                            v
+------+------+------+------+
|  10  |  20  |  30  |  40  |
+------+------+------+------+
```

Remove the first element.

```text
       FRONT            REAR
         |                |
         v                v
    +------+------+------+
    |  20  |  30  |  40  |
    +------+------+------+
```

The element 10 is removed because it was inserted first.

In Java, we generally use:

```java
queue.poll();
```

Or:

```java
queue.remove();
```

Both remove and return the element at the FRONT, but they handle an empty Queue differently.

## 3.3 peek() – View the FRONT element

The `peek()` operation returns the element at the FRONT without removing it.

```java
Queue<Integer> queue = new LinkedList<>();

queue.offer(10);
queue.offer(20);
queue.offer(30);

System.out.println(queue.peek());
```

Output:

```text
10
```

The Queue remains unchanged:

```text
FRONT                 REAR
  |                     |
  v                     v
+------+------+------+
|  10  |  20  |  30  |
+------+------+------+
```

**Difference between poll() and peek():**

| Operation | Returns FRONT element | Removes element |
|---|---|---|
| `poll()` | Yes | Yes |
| `peek()` | Yes | No |

Remember this difference.

## 3.4 Complete Example

```java
import java.util.Queue;
import java.util.LinkedList;

public class QueueExample {

    public static void main(String[] args) {

        Queue<Integer> queue = new LinkedList<>();

        // Insert elements
        queue.offer(10);
        queue.offer(20);
        queue.offer(30);

        System.out.println(queue);
        // [10, 20, 30]

        // View FRONT element
        System.out.println(queue.peek());
        // 10

        // Remove FRONT element
        System.out.println(queue.poll());
        // 10

        System.out.println(queue);
        // [20, 30]
    }
}
```

---

# 4. Other Important Queue Methods in Java

Java provides several methods through the `Queue` interface.

| Method | Description | Example |
|---|---|---|
| `offer(element)` | Inserts an element; returns false if capacity prevents insertion | `queue.offer(10)` |
| `add(element)` | Inserts an element; throws exception if insertion fails | `queue.add(10)` |
| `poll()` | Removes and returns FRONT; returns null if empty | `queue.poll()` |
| `remove()` | Removes and returns FRONT; throws exception if empty | `queue.remove()` |
| `peek()` | Returns FRONT without removing; returns null if empty | `queue.peek()` |
| `element()` | Returns FRONT without removing; throws exception if empty | `queue.element()` |
| `isEmpty()` | Checks whether Queue is empty | `queue.isEmpty()` |
| `size()` | Returns number of elements | `queue.size()` |
| `contains()` | Checks whether an element exists | `queue.contains(10)` |
| `clear()` | Removes all elements | `queue.clear()` |

## Understanding add() vs offer()

Both methods insert elements into a Queue.

However, their behavior differs when insertion is unsuccessful.

| Method | If insertion fails |
|---|---|
| `add()` | Throws `IllegalStateException` |
| `offer()` | Returns `false` |

For example, bounded Queues may reject insertion when they are full.

For most Queue-based coding problems, `offer()` is a convenient choice.

## Understanding remove() vs poll()

Both methods remove the FRONT element.

| Method | If Queue is empty |
|---|---|
| `remove()` | Throws `NoSuchElementException` |
| `poll()` | Returns `null` |

## Understanding element() vs peek()

Both methods return the FRONT element without removing it.

| Method | If Queue is empty |
|---|---|
| `element()` | Throws `NoSuchElementException` |
| `peek()` | Returns `null` |

### Example

```java
import java.util.Queue;
import java.util.LinkedList;

public class QueueMethods {

    public static void main(String[] args) {

        Queue<String> queue = new LinkedList<>();

        queue.offer("Person 1");
        queue.offer("Person 2");
        queue.offer("Person 3");

        System.out.println(queue.isEmpty());
        // false

        System.out.println(queue.size());
        // 3

        System.out.println(queue.contains("Person 2"));
        // true

        System.out.println(queue.peek());
        // Person 1

        System.out.println(queue.element());
        // Person 1

        System.out.println(queue.poll());
        // Person 1

        System.out.println(queue.remove());
        // Person 2

        System.out.println(queue);
        // [Person 3]

        queue.clear();

        System.out.println(queue.isEmpty());
        // true
    }
}
```

---

# 5. What Happens When Queue Becomes Empty?

Consider:

```java
Queue<Integer> queue = new LinkedList<>();

queue.offer(10);

System.out.println(queue.poll());
// 10

System.out.println(queue.poll());
// null
```

After the first `poll()`, the Queue becomes empty.

Calling `poll()` again returns `null`.

Similarly:

```java
queue.peek();
```

Returns `null` if the Queue is empty.

However:

```java
queue.remove();
```

Throws:

```text
java.util.NoSuchElementException
```

And:

```java
queue.element();
```

Also throws `NoSuchElementException`.

## How to avoid this?

Always check whether the Queue contains an element before accessing or removing its FRONT.

```java
if (!queue.isEmpty()) {

    System.out.println(queue.poll());

} else {

    System.out.println("Queue is empty");
}
```

**Important:** `ArrayDeque` does not allow null elements. This makes `poll()` and `peek()` returning null a useful way to identify an empty Queue.

---

# 6. How to Implement Queue in Java?

Java provides the `Queue` interface.

Unlike Stack, which has a legacy concrete `Stack` class, Queue is an interface with several implementations.

The most commonly used implementations are:

1. `LinkedList`
2. `ArrayDeque`
3. `PriorityQueue`

Let's understand each one.

## Approach 1: Using LinkedList

`LinkedList` implements the `Queue` interface.

```java
import java.util.Queue;
import java.util.LinkedList;

public class Main {

    public static void main(String[] args) {

        Queue<Integer> queue = new LinkedList<>();

        queue.offer(10);
        queue.offer(20);
        queue.offer(30);

        System.out.println(queue);
        // [10, 20, 30]

        System.out.println(queue.peek());
        // 10

        System.out.println(queue.poll());
        // 10

        System.out.println(queue);
        // [20, 30]
    }
}
```

Syntax:

```java
Queue<DataType> queue = new LinkedList<>();
```

Examples:

```java
Queue<Integer> numbers = new LinkedList<>();

Queue<String> names = new LinkedList<>();

Queue<Character> characters = new LinkedList<>();

Queue<Double> prices = new LinkedList<>();
```

You can also store custom objects:

```java
Queue<Book> books = new LinkedList<>();

books.offer(new Book("Java"));
books.offer(new Book("Spring Boot"));
```

Example class:

```java
class Book {

    String name;

    Book(String name) {
        this.name = name;
    }
}
```

### How does LinkedList work internally?

A LinkedList stores elements in nodes.

Each node contains:

- Data.
- A reference to the next node.
- In a doubly linked list, a reference to the previous node.

For Queue operations:

- Insertion happens at the tail.
- Removal happens at the head.

Both operations take O(1) time.

## Approach 2: Using ArrayDeque (Recommended)

In modern Java, `ArrayDeque` is generally preferred for implementing a normal Queue.

Why?

- It uses a resizable array internally.
- It provides efficient insertion and removal.
- It avoids the node-object overhead of LinkedList.
- It is generally faster for ordinary single-threaded Queue operations.

Example:

```java
import java.util.Queue;
import java.util.ArrayDeque;

public class Main {

    public static void main(String[] args) {

        Queue<Integer> queue = new ArrayDeque<>();

        queue.offer(10);
        queue.offer(20);
        queue.offer(30);

        System.out.println(queue);
        // [10, 20, 30]

        System.out.println(queue.peek());
        // 10

        System.out.println(queue.poll());
        // 10

        System.out.println(queue);
        // [20, 30]
    }
}
```

Syntax:

```java
Queue<DataType> queue = new ArrayDeque<>();
```

**Important:** `ArrayDeque` does not allow null elements.

## Approach 3: Using PriorityQueue

A `PriorityQueue` is a special type of Queue.

Unlike a normal Queue, it does not strictly follow FIFO.

Instead, elements are processed according to their priority.

By default, Java's `PriorityQueue` processes the smallest element first.

Example:

```java
import java.util.Queue;
import java.util.PriorityQueue;

public class Main {

    public static void main(String[] args) {

        Queue<Integer> queue = new PriorityQueue<>();

        queue.offer(30);
        queue.offer(10);
        queue.offer(20);

        System.out.println(queue.peek());
        // 10

        System.out.println(queue.poll());
        // 10

        System.out.println(queue.poll());
        // 20

        System.out.println(queue.poll());
        // 30
    }
}
```

Notice something important:

We inserted:

```text
30 -> 10 -> 20
```

But removed:

```text
10 -> 20 -> 30
```

This happens because PriorityQueue processes elements according to their natural ordering, not their insertion order.

### Max Heap using PriorityQueue

By default, Java uses a min-heap.

If you want the largest element first:

```java
Queue<Integer> queue =
        new PriorityQueue<>((a, b) -> Integer.compare(b, a));

queue.offer(10);
queue.offer(30);
queue.offer(20);

System.out.println(queue.poll()); // 30
System.out.println(queue.poll()); // 20
System.out.println(queue.poll()); // 10
```

Using `Integer.compare()` avoids integer overflow that can occur with subtraction-based comparators.

### Comparison of Queue Implementations

| Feature | LinkedList | ArrayDeque | PriorityQueue |
|---|---|---|---|
| Internal structure | Doubly Linked List | Resizable circular array | Binary Heap |
| FIFO | Yes, when used as Queue | Yes, when used as Queue | No |
| Insertion | O(1) | O(1) amortized | O(log n) |
| Removal | O(1) | O(1) | O(log n) |
| Peek | O(1) | O(1) | O(1) |
| Null elements | Allowed | Not allowed | Not allowed |
| Thread-safe | No | No | No |
| Typical usage | General Queue | Normal Queue and Deque | Priority-based processing |

**Recommendation:**

For most normal Queue problems in Java:

```java
Queue<Integer> queue = new ArrayDeque<>();
```

For priority-based problems:

```java
Queue<Integer> queue = new PriorityQueue<>();
```

For problems involving concurrent producers and consumers, consider implementations from `java.util.concurrent`, such as `ArrayBlockingQueue` or `LinkedBlockingQueue`.

---

# 7. How Does Queue Work Internally?

Understanding the internal working makes Queue problems much easier.

A Queue can be implemented using:

1. An array.
2. A circular array.
3. A linked list.

Let's understand them one by one.

## 7.1 Array-based Queue

Imagine an array with a capacity of 5.

Initially:

```text
FRONT = -1
REAR  = -1
```

No elements exist in the Queue.

Insert 10:

```text
FRONT = 0
REAR  = 0

Index:    0       1       2       3       4

       +-------+-------+-------+-------+-------+
       |  10   |       |       |       |       |
       +-------+-------+-------+-------+-------+
          ^
          |
       FRONT
       REAR
```

Insert 20:

```text
FRONT = 0
REAR  = 1

       +-------+-------+-------+-------+-------+
       |  10   |  20   |       |       |       |
       +-------+-------+-------+-------+-------+
          ^       ^
          |       |
        FRONT   REAR
```

Insert 30:

```text
FRONT = 0
REAR  = 2

       +-------+-------+-------+-------+-------+
       |  10   |  20   |  30   |       |       |
       +-------+-------+-------+-------+-------+
          ^               ^
          |               |
        FRONT           REAR
```

Now remove an element.

The element at FRONT (10) is removed.

```text
FRONT = 1
REAR  = 2

       +-------+-------+-------+-------+-------+
       |       |  20   |  30   |       |       |
       +-------+-------+-------+-------+-------+
                  ^       ^
                  |       |
                FRONT   REAR
```

Notice that FRONT moves to the next position.

### Problem with a Simple Array Queue

Suppose we keep inserting and removing elements.

Eventually, REAR reaches the last array position.

Even if some elements were removed from the beginning, the Queue may report that it is full.

This is called **Wasted Space** or a false overflow condition in a simple linear Queue.

Example:

```text
Capacity = 5

       +------+------+------+------+------+
       |      |  20  |  30  |  40  |  50  |
       +------+------+------+------+------+
              ^                    ^
            FRONT                REAR
```

Index 0 is empty, but REAR is already at the last position.

We cannot insert another element using a simple linear Queue implementation.

To solve this problem, we use a Circular Queue.

## 7.2 Circular Queue

A Circular Queue connects the last array position back to the first position.

Think of it as a circle rather than a straight line.

When REAR reaches the end of the array, it can move back to index 0 if space is available.

The next position is calculated using:

```java
(rear + 1) % capacity
```

Similarly, FRONT can also wrap around.

Example:

```text
Capacity = 5

       +------+------+------+------+------+
       |  60  |  20  |  30  |  40  |  50  |
       +------+------+------+------+------+
          ^                    ^
          |                    |
        REAR                 FRONT
```

After removing 20 and 30, the Queue may use those positions again.

The circular structure helps reuse available array positions.

### Complete Circular Queue Implementation in Java

```java
public class CircularQueue {

    private int[] arr;
    private int front;
    private int rear;
    private int size;
    private int capacity;

    public CircularQueue(int capacity) {

        this.capacity = capacity;
        this.arr = new int[capacity];

        this.front = 0;
        this.rear = -1;
        this.size = 0;
    }

    // Insert element at REAR
    public boolean enqueue(int value) {

        if (isFull()) {
            return false;
        }

        rear = (rear + 1) % capacity;

        arr[rear] = value;

        size++;

        return true;
    }

    // Remove element from FRONT
    public int dequeue() {

        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }

        int value = arr[front];

        front = (front + 1) % capacity;

        size--;

        return value;
    }

    // View FRONT element
    public int peek() {

        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }

        return arr[front];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == capacity;
    }

    public int size() {
        return size;
    }
}
```

Usage:

```java
public class Main {

    public static void main(String[] args) {

        CircularQueue queue = new CircularQueue(5);

        queue.enqueue(10);
        queue.enqueue(20);
        queue.enqueue(30);

        System.out.println(queue.dequeue()); // 10

        queue.enqueue(40);
        queue.enqueue(50);
        queue.enqueue(60);

        System.out.println(queue.peek()); // 20

        System.out.println(queue.size()); // 5
    }
}
```

Notice how the Queue can reuse positions after removing elements.

This is the fundamental idea behind Circular Queue.

## 7.3 Linked List-based Queue

A Queue can also be implemented using a Linked List.

Each node stores:

- Data.
- Reference to the next node.

We maintain two references:

- `front`: Points to the first node.
- `rear`: Points to the last node.

Example:

```text
FRONT                              REAR
  |                                  |
  v                                  v
+------+    +------+    +------+
|  10  | -> |  20  | -> |  30  | -> null
+------+    +------+    +------+
```

Insertion happens at REAR.

Removal happens at FRONT.

### Complete Linked List Queue Implementation

```java
public class LinkedListQueue {

    private static class Node {

        int data;
        Node next;

        Node(int data) {
            this.data = data;
        }
    }

    private Node front;
    private Node rear;
    private int size;

    // Insert element
    public void enqueue(int value) {

        Node newNode = new Node(value);

        if (rear == null) {
            front = rear = newNode;
        } else {
            rear.next = newNode;
            rear = newNode;
        }

        size++;
    }

    // Remove element
    public int dequeue() {

        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }

        int value = front.data;

        front = front.next;

        if (front == null) {
            rear = null;
        }

        size--;

        return value;
    }

    // View FRONT
    public int peek() {

        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }

        return front.data;
    }

    public boolean isEmpty() {
        return front == null;
    }

    public int size() {
        return size;
    }
}
```

Usage:

```java
public class Main {

    public static void main(String[] args) {

        LinkedListQueue queue = new LinkedListQueue();

        queue.enqueue(10);
        queue.enqueue(20);
        queue.enqueue(30);

        System.out.println(queue.peek());    // 10

        System.out.println(queue.dequeue()); // 10

        System.out.println(queue.size());    // 2
    }
}
```

**Important:** The Linked List implementation does not need a fixed capacity. It grows dynamically until memory or implementation limits are reached.

---

# 8. Time and Space Complexity

Understanding complexity is important for coding interviews.

The complexity depends on the implementation.

## Normal Queue Operations

For `ArrayDeque` and a properly implemented Linked List Queue:

| Operation | Time Complexity |
|---|---|
| Insert (`offer`) | O(1) amortized |
| Remove (`poll`) | O(1) |
| Peek (`peek`) | O(1) |
| `isEmpty()` | O(1) |
| `size()` | O(1) |
| `contains()` | O(n) |

For `ArrayDeque`, insertion is amortized O(1) because its internal array occasionally grows.

## PriorityQueue Operations

| Operation | Time Complexity |
|---|---|
| Insert (`offer`) | O(log n) |
| Remove (`poll`) | O(log n) |
| Peek (`peek`) | O(1) |
| `contains()` | O(n) |
| `size()` | O(1) |

Why does PriorityQueue take O(log n) for insertion and removal?

Because it uses a binary heap and needs to maintain the heap property after these operations.

## Space Complexity

If a Queue contains `n` elements:

```text
Space Complexity = O(n)
```

Because we need memory to store all `n` elements.

---

# 9. Queue and Memory: Can We Insert 10 Million Elements?

Consider:

```java
Queue<Integer> queue = new ArrayDeque<>();

for (int i = 0; i < 10_000_000; i++) {
    queue.offer(i);
}
```

Will this cause an OutOfMemoryError?

Not necessarily.

It depends on:

- Available JVM heap memory.
- Number and size of objects.
- Internal array capacity.
- Other objects occupying memory.
- JVM configuration.

If the Queue requires more memory than the JVM can allocate, it may throw:

```text
java.lang.OutOfMemoryError: Java heap space
```

**Important:** There is no universal maximum number of elements for a Queue. Its practical limit depends on the implementation and available memory.

---

# 10. Real-world Applications of Queue

Queue is not just a theoretical data structure. It is used in many real-world applications.

## 10.1 Printer Job Scheduling

Imagine an office with one printer and multiple people sending print requests.

Requests arrive:

```text
Document A
Document B
Document C
```

The printer processes:

```text
Document A -> Document B -> Document C
```

The first document submitted is printed first.

This is a typical FIFO application.

## 10.2 CPU Scheduling

Operating systems use different scheduling algorithms to manage processes.

For example, Round Robin scheduling uses a circular Queue to give processes turns to execute.

Each process receives a time slice.

If it does not finish within that time, it can be placed at the end of the ready Queue.

## 10.3 Message Queues

Messaging systems such as Apache Kafka and RabbitMQ help applications communicate asynchronously.

For example:

```text
Order Service
      |
      v
Message Queue / Broker
      |
      v
Payment Service
```

An order event can be published and processed by another service later.

**Important:** Real-world message brokers have their own delivery, ordering, persistence, and concurrency semantics. They are more sophisticated than a simple in-memory Java Queue.

## 10.4 Customer Service Systems

Imagine a customer support system.

Requests arrive:

```text
Customer 1
Customer 2
Customer 3
```

Support agents process requests based on the selected scheduling policy.

A simple FIFO Queue processes the oldest request first.

Priority-based support systems may use PriorityQueue instead.

## 10.5 Breadth First Search (BFS)

BFS is one of the most important applications of Queue in programming.

It is used to traverse trees and graphs level by level.

Example:

```text
          1
        /   \
       2     3
      / \   / \
     4   5 6   7
```

BFS traversal:

```text
1 -> 2 -> 3 -> 4 -> 5 -> 6 -> 7
```

We use a Queue to process nodes in the order they are discovered.

This is especially useful for finding the shortest path in an unweighted graph.

## 10.6 Network Request Processing

Servers may use Queues to manage incoming work.

For example:

```text
Client 1
Client 2
Client 3
    |
    v
  Request Queue
    |
    v
Worker Threads
```

In Java, `BlockingQueue` is particularly useful for producer-consumer systems.

## 10.7 Event Handling and Task Scheduling

Queues are useful for:

- Event processing.
- Background jobs.
- Task scheduling.
- Asynchronous processing.
- Producer-consumer applications.

---

# 11. Important Queue Coding Problems

Understanding the theory is not enough. You should practice implementing Queue operations and solving coding problems using FIFO.

Here are some important problems, arranged from beginner to interview level.

## Level 1: Beginner Problems

### Problem 1: Reverse a Queue

Given a Queue:

```text
[10, 20, 30, 40, 50]
```

Reverse it.

Output:

```text
[50, 40, 30, 20, 10]
```

**Logic:**

1. Remove elements from the Queue.
2. Push them into a Stack.
3. Pop elements from the Stack.
4. Insert them back into the Queue.

Java solution:

```java
import java.util.Queue;
import java.util.Stack;
import java.util.LinkedList;

public class ReverseQueue {

    public static void reverse(Queue<Integer> queue) {

        Stack<Integer> stack = new Stack<>();

        while (!queue.isEmpty()) {
            stack.push(queue.poll());
        }

        while (!stack.isEmpty()) {
            queue.offer(stack.pop());
        }
    }

    public static void main(String[] args) {

        Queue<Integer> queue = new LinkedList<>();

        queue.offer(10);
        queue.offer(20);
        queue.offer(30);
        queue.offer(40);
        queue.offer(50);

        reverse(queue);

        System.out.println(queue);
        // [50, 40, 30, 20, 10]
    }
}
```

Time Complexity: O(n)

Space Complexity: O(n)

### Problem 2: Generate Binary Numbers from 1 to N

Given an integer N, generate binary representations of all numbers from 1 to N.

Input:

```text
N = 5
```

Output:

```text
1
10
11
100
101
```

**Logic:**

1. Insert `"1"` into the Queue.
2. Remove the FRONT string.
3. Add it to the result.
4. Generate two new strings by appending `"0"` and `"1"`.
5. Repeat until N numbers are generated.

Java solution:

```java
import java.util.Queue;
import java.util.ArrayDeque;

public class BinaryNumbers {

    public static void generate(int n) {

        Queue<String> queue = new ArrayDeque<>();

        queue.offer("1");

        for (int i = 0; i < n; i++) {

            String current = queue.poll();

            System.out.println(current);

            queue.offer(current + "0");
            queue.offer(current + "1");
        }
    }

    public static void main(String[] args) {

        generate(5);
    }
}
```

Time Complexity: O(n) generated strings, excluding the total cost of string construction and output.

Space Complexity: O(n) for the generated Queue at its peak for this algorithm.

---

## Level 2: Intermediate Problems

### Problem 3: Implement Stack Using Queues

Implement a Stack using Queue data structures.

Your Stack should support:

- `push(value)`
- `pop()`
- `top()`
- `empty()`

Example:

```text
push(10)
push(20)
push(30)

top() -> 30

pop() -> 30

top() -> 20
```

**Hint:** Use one or two Queues and rearrange elements to maintain LIFO behavior.

### Problem 4: First Non-Repeating Character in a Stream

Given a stream of characters:

```text
a a b c
```

Find the first non-repeating character after each insertion.

Output:

```text
a
#
b
b
```

Here, `#` means no non-repeating character currently exists.

**Hint:** Use:

- A Queue to maintain candidate characters in arrival order.
- A frequency array or HashMap to track character counts.

### Problem 5: Circular Queue

Design a Circular Queue with a fixed capacity.

Support:

- `enqueue(value)`
- `dequeue()`
- `front()`
- `rear()`
- `isEmpty()`
- `isFull()`

**Hint:** Use modulo arithmetic:

```java
(rear + 1) % capacity
```

---

## Level 3: Advanced Interview Problems

### Problem 6: Binary Tree Level Order Traversal

Given a binary tree:

```text
          1
        /   \
       2     3
      / \   / \
     4   5 6   7
```

Return:

```text
[
  [1],
  [2, 3],
  [4, 5, 6, 7]
]
```

**Hint:** Use BFS with a Queue.

At each level:

1. Store the current Queue size.
2. Process exactly that many nodes.
3. Insert their children into the Queue.
4. Repeat for the next level.

### Problem 7: Rotten Oranges

You are given a grid containing:

- `0` = Empty cell.
- `1` = Fresh orange.
- `2` = Rotten orange.

Every minute, rotten oranges infect adjacent fresh oranges in four directions.

Find the minimum time required to rot all oranges.

Example:

```text
Input:

[
  [2, 1, 1],
  [1, 1, 0],
  [0, 1, 1]
]

Output:

4
```

**Hint:** Use Multi-source BFS.

Insert all initially rotten oranges into the Queue, then process them level by level.

### Problem 8: Sliding Window Maximum

Given:

```text
nums = [1, 3, -1, -3, 5, 3, 6, 7]
k = 3
```

Find the maximum value in every window of size 3.

Output:

```text
[3, 3, 5, 5, 6, 7]
```

**Hint:** Use a Monotonic Deque to maintain useful indices.

Expected Time Complexity: O(n).

### Problem 9: Shortest Path in an Unweighted Graph

Given an unweighted graph, find the shortest distance between a source and destination.

**Hint:** Use BFS with a Queue.

BFS explores all vertices at distance 1 before distance 2, and so on. Therefore, the first time we reach a vertex gives its shortest distance in an unweighted graph.

---

# 12. Common Mistakes Beginners Make

| Mistake | Correct Understanding |
|---|---|
| Confusing Queue with Stack | Queue follows FIFO; Stack follows LIFO |
| Inserting at FRONT | Normal Queue insertion happens at REAR |
| Removing from REAR | Normal Queue removal happens at FRONT |
| Confusing `poll()` and `peek()` | `poll()` removes; `peek()` only reads |
| Calling `remove()` on an empty Queue | It throws `NoSuchElementException` |
| Assuming Queue is always FIFO | `PriorityQueue` uses priority ordering |
| Assuming `Queue` is a concrete class | `Queue` is an interface |
| Using `LinkedList` everywhere | Prefer `ArrayDeque` for ordinary single-threaded Queue usage |
| Forgetting circular array wrapping | Use modulo arithmetic |
| Assuming Queue is thread-safe | `ArrayDeque` and `LinkedList` are not thread-safe |

---

# 13. Important Java Interview Questions

### Q1. What is a Queue?

A Queue is a linear data structure that follows FIFO (First In, First Out). Elements are inserted at the REAR and removed from the FRONT.

### Q2. What is the difference between Stack and Queue?

| Stack | Queue |
|---|---|
| LIFO | FIFO |
| Insert using push | Insert using offer |
| Remove using pop | Remove using poll |
| Access the top | Access the front |
| One primary operation end | Two operation ends |

### Q3. What is the difference between add() and offer()?

Both insert elements.

- `add()` throws an exception if insertion fails.
- `offer()` returns false if insertion fails.

### Q4. What is the difference between remove() and poll()?

Both remove and return the FRONT element.

- `remove()` throws `NoSuchElementException` if empty.
- `poll()` returns null if empty.

### Q5. What is the difference between peek() and element()?

Both return the FRONT element without removing it.

- `peek()` returns null if empty.
- `element()` throws `NoSuchElementException` if empty.

### Q6. Can Queue contain duplicate elements?

Yes. A normal Queue can contain duplicate values.

```java
Queue<Integer> queue = new ArrayDeque<>();

queue.offer(10);
queue.offer(20);
queue.offer(10);
```

### Q7. Can Queue store different data types?

A generically typed Queue stores elements of its declared type.

You can use:

```java
Queue<Object> queue = new ArrayDeque<>();

queue.offer("Hello");
queue.offer(100);
queue.offer(10.5);
```

However, this sacrifices compile-time type safety.

### Q8. Is Queue thread-safe in Java?

The `Queue` interface does not guarantee thread safety.

Common implementations such as `ArrayDeque` and `LinkedList` are not thread-safe.

For concurrent applications, Java provides:

- `ArrayBlockingQueue`
- `LinkedBlockingQueue`
- `ConcurrentLinkedQueue`
- `PriorityBlockingQueue`

Choose based on whether you need blocking, bounded capacity, or non-blocking concurrent access.

### Q9. What is a Circular Queue?

A Circular Queue is a Queue implementation in which the last array position connects back to the first position.

It helps reuse empty positions and avoids wasted space in a simple linear array Queue.

### Q10. What is a PriorityQueue?

A PriorityQueue is a Queue implementation that processes elements based on priority rather than insertion order.

Java's default PriorityQueue is a min-heap, so the smallest element is processed first.

### Q11. What is the difference between ArrayDeque and LinkedList?

`ArrayDeque` uses a resizable circular array, whereas `LinkedList` uses a doubly linked list.

`ArrayDeque` is generally preferred for ordinary Queue and Stack operations because it avoids the per-node memory overhead of LinkedList.

### Q12. What is BlockingQueue?

`BlockingQueue` is an interface in `java.util.concurrent`.

It supports thread-safe producer-consumer operations.

For example, when a consumer tries to take an element from an empty Queue, it can wait until an element becomes available.

### Q13. What is the difference between Queue and Deque?

| Queue | Deque |
|---|---|
| Usually inserts at REAR | Can insert at both ends |
| Usually removes from FRONT | Can remove from both ends |
| FIFO usage | Can implement FIFO or LIFO |
| `offer()` and `poll()` | `offerFirst()`, `offerLast()`, `pollFirst()`, `pollLast()` |

Deque means Double-Ended Queue.

### Q14. Which Queue implementation is best for BFS?

`ArrayDeque` is generally a good choice.

```java
Queue<Integer> queue = new ArrayDeque<>();
```

It provides efficient insertion and removal for normal BFS traversal.

### Q15. Can we implement a Queue using Stacks?

Yes.

A Queue can be implemented using two Stacks.

One Stack handles insertion, and the other handles removal. Elements are transferred when necessary to maintain FIFO ordering.

---

# 14. Quick Revision Cheat Sheet

| Concept | Remember |
|---|---|
| Data Structure | Linear |
| Principle | FIFO |
| Insertion position | REAR |
| Removal position | FRONT |
| Insert operation | Enqueue / Offer |
| Remove operation | Dequeue / Poll |
| Read next element | Peek |
| First inserted | FRONT |
| Last inserted | REAR |
| Java interface | `Queue` |
| Normal Queue implementation | `ArrayDeque` |
| Linked implementation | `LinkedList` |
| Priority-based implementation | `PriorityQueue` |
| Circular implementation | Circular Array |
| Push/insert complexity | O(1) amortized for ArrayDeque |
| Remove complexity | O(1) for ArrayDeque |
| Search complexity | O(n) |
| BFS | Queue |
| Thread-safe Queue | `BlockingQueue` or suitable concurrent implementation |

## Most Important Java Syntax

```java
import java.util.Queue;
import java.util.ArrayDeque;

public class Main {

    public static void main(String[] args) {

        Queue<Integer> queue = new ArrayDeque<>();

        // Insert elements at REAR
        queue.offer(10);
        queue.offer(20);
        queue.offer(30);

        // View FRONT
        System.out.println(queue.peek()); // 10

        // Remove FRONT
        System.out.println(queue.poll()); // 10

        // Check empty
        System.out.println(queue.isEmpty()); // false

        // Queue size
        System.out.println(queue.size()); // 2

        System.out.println(queue); // [20, 30]
    }
}
```

---

# 15. Practice Roadmap

Follow this order to become comfortable with Queue problems.

- [ ] Understand FIFO and FRONT/REAR.
- [ ] Understand enqueue, dequeue and peek.
- [ ] Practice Java Queue using LinkedList.
- [ ] Practice Java Queue using ArrayDeque.
- [ ] Understand PriorityQueue and min-heap/max-heap.
- [ ] Implement Queue using an array.
- [ ] Implement Queue using a Linked List.
- [ ] Implement Circular Queue.
- [ ] Reverse a Queue.
- [ ] Generate Binary Numbers using Queue.
- [ ] Implement Stack using Queues.
- [ ] Find First Non-Repeating Character in a Stream.
- [ ] Solve Binary Tree Level Order Traversal.
- [ ] Solve Rotten Oranges using BFS.
- [ ] Solve Shortest Path using BFS.
- [ ] Understand Monotonic Deque.
- [ ] Solve Sliding Window Maximum.

---

## Final Takeaway

A Queue is one of the most important data structures in programming.

Whenever a problem involves:

- Processing elements in the order they arrive.
- Scheduling tasks or requests.
- Managing waiting lists.
- Processing messages.
- Traversing a tree level by level.
- Finding the shortest path in an unweighted graph.
- Processing elements in a sliding window.

Think about whether a Queue can solve the problem.

Once you understand `offer()`, `poll()`, and `peek()`, you have understood the foundation of Queue.

The remaining challenge is recognizing when to apply it in a coding problem.