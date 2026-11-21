---
title: "Insertion Sort"
layout: default
parent: Data Structures & Algorithms
nav_order: 14
permalink: /data-structures/Insertion-Sort/insertion-sort/

topic: Insertion Sort
section: Sorting Algorithms
concepts: [comparison-sort, sorted-prefix, stable-sort, in-place-sort]
complexities: [O(n), O(n^2)]
tags: [algorithms, sorting, insertion-sort, java]
---

# Insertion Sort

## 1. What is Insertion Sort?

**Insertion Sort** is a simple sorting algorithm that builds a sorted portion of an array one element at a time.

It takes each element from the unsorted portion and inserts it into its correct position in the already sorted portion.

Think of arranging playing cards in your hand:

1. You pick up one card.
2. You compare it with the cards already in your hand.
3. You move larger cards to make space.
4. You insert the new card in the correct position.
5. You repeat until all cards are arranged.

That is exactly how Insertion Sort works!

### Real-world examples

- **Playing cards:** Arrange cards in ascending or descending order as you receive them.
- **Student marks:** Insert a newly received exam score into an already sorted list of scores.
- **Bookshelf:** Insert a new book into its correct alphabetical position without rearranging the entire shelf.
- **Incoming records:** Maintain a small, sorted list as new records arrive.

## 2. How Does Insertion Sort Work?

Insertion Sort divides an array into two conceptual parts:

- **Sorted portion:** Elements already arranged in the correct order.
- **Unsorted portion:** Elements that still need to be inserted into the sorted portion.

Initially, the first element is considered sorted. The algorithm then picks the next element and inserts it into the correct position.

### Example

Consider this array:

```text
[5, 3, 8, 4, 2]
```

We want to sort it in ascending order.

### Step 1: Initial state

```text
[5 | 3, 8, 4, 2]
```

- `[5]` is the sorted portion.
- `[3, 8, 4, 2]` is the unsorted portion.

### Step 2: Insert 3

Take `3` as the current element, called the **key**.

Compare it with `5`.

Since `5 > 3`, shift `5` one position to the right and insert `3` at the beginning.

```text
Before: [5 | 3, 8, 4, 2]

After:  [3, 5 | 8, 4, 2]
```

Now `[3, 5]` is sorted.

### Step 3: Insert 8

Take `8` as the key.

Compare it with `5`.

Since `5 < 8`, no shifting is required. `8` is already in the correct position.

```text
Before: [3, 5 | 8, 4, 2]

After:  [3, 5, 8 | 4, 2]
```

### Step 4: Insert 4

Take `4` as the key.

Compare it with the elements in the sorted portion:

1. `8 > 4` → shift `8` right.
2. `5 > 4` → shift `5` right.
3. `3 < 4` → stop.
4. Insert `4` after `3`.

```text
Before: [3, 5, 8 | 4, 2]

After:  [3, 4, 5, 8 | 2]
```

### Step 5: Insert 2

Take `2` as the key.

Compare it with the sorted elements:

1. `8 > 2` → shift `8`.
2. `5 > 2` → shift `5`.
3. `4 > 2` → shift `4`.
4. `3 > 2` → shift `3`.
5. No elements remain on the left, so insert `2` at the beginning.

```text
Before: [3, 4, 5, 8 | 2]

After:  [2, 3, 4, 5, 8]
```

<caption>Final sorted array (ascending order)</caption>

```text
[2, 3, 4, 5, 8]
```

### Complete execution summary

| Pass | Key | Action | Array |
|---|---:|---|---|
| Initial | — | First element is sorted | `[5, 3, 8, 4, 2]` |
| 1 | 3 | Shift 5, insert 3 | `[3, 5, 8, 4, 2]` |
| 2 | 8 | Already in position | `[3, 5, 8, 4, 2]` |
| 3 | 4 | Shift 8 and 5, insert 4 | `[3, 4, 5, 8, 2]` |
| 4 | 2 | Shift 8, 5, 4, 3, insert 2 | `[2, 3, 4, 5, 8]` |

## 3. Visualize Insertion Sort

Use the interactive visualization to follow each pass. Observe how the sorted portion grows, how larger elements shift right, and where the key is inserted.

## 4. Insertion Sort Algorithm

### Pseudocode

```text
InsertionSort(array):
    for i = 1 to array.length - 1:

        key = array[i]
        j = i - 1

        while j >= 0 AND array[j] > key:
            array[j + 1] = array[j]
            j = j - 1

        array[j + 1] = key
```

### Understanding the variables

| Variable | Meaning |
|---|---|
| `i` | Index of the current element to insert |
| `key` | Element that needs to be inserted |
| `j` | Index used to compare elements in the sorted portion |
| `array[j] > key` | Checks whether an element must move right |
| `array[j + 1] = key` | Inserts the key into its correct position |

**Important:** The algorithm shifts larger elements to the right instead of repeatedly swapping adjacent elements.

## 5. Java Implementation

### Basic Insertion Sort (Ascending Order)

```java
import java.util.Arrays;

public class InsertionSort {

    public static void insertionSort(int[] arr) {

        for (int i = 1; i < arr.length; i++) {

            int key = arr[i];
            int j = i - 1;

            // Shift elements greater than key to the right
            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }

            // Insert key at its correct position
            arr[j + 1] = key;
        }
    }

    public static void main(String[] args) {

        int[] arr = {5, 3, 8, 4, 2};

        insertionSort(arr);

        System.out.println(Arrays.toString(arr));
    }
}
```

**Output:**

```text
[2, 3, 4, 5, 8]
```

### Understanding the core logic

```java
int key = arr[i];
```

Store the current element because shifting elements may overwrite its original position.

```java
int j = i - 1;
```

Start comparing from the element immediately before the key.

```java
while (j >= 0 && arr[j] > key) {
    arr[j + 1] = arr[j];
    j--;
}
```

Move every element larger than the key one position to the right.

```java
arr[j + 1] = key;
```

Insert the key into the empty position created by shifting.

**Why do we use `j + 1`?**

After the loop finishes, `j` points to the position immediately before where the key belongs. Therefore, the correct insertion position is `j + 1`.

## 6. Insertion Sort in Descending Order

To sort in descending order, reverse the comparison condition.

Ascending:

```java
arr[j] > key
```

Descending:

```java
arr[j] < key
```

### Java Implementation

```java
public static void insertionSortDescending(int[] arr) {

    for (int i = 1; i < arr.length; i++) {

        int key = arr[i];
        int j = i - 1;

        while (j >= 0 && arr[j] < key) {
            arr[j + 1] = arr[j];
            j--;
        }

        arr[j + 1] = key;
    }
}
```

**Example:**

```text
Input:  [5, 3, 8, 4, 2]
Output: [8, 5, 4, 3, 2]
```

## 7. Time Complexity

Insertion Sort's performance depends on the initial arrangement of elements.

| Case | Time Complexity | Explanation |
|---|---|---|
| Best Case | O(n) | Array is already sorted |
| Average Case | O(n²) | Elements require some shifting |
| Worst Case | O(n²) | Array is sorted in reverse order |

### Best Case: O(n)

Consider an already sorted array:

```text
[1, 2, 3, 4, 5]
```

Each key is compared with the element immediately before it.

Since every element is already in the correct position, no shifting is required.

The algorithm performs approximately `n - 1` comparisons.

Therefore:

```text
Best Case = O(n)
```

### Worst Case: O(n²)

Consider a reverse-sorted array:

```text
[5, 4, 3, 2, 1]
```

Every new key must move to the beginning of the sorted portion.

For example:

- Insert 4: shift 5 once.
- Insert 3: shift 5 and 4.
- Insert 2: shift 5, 4 and 3.
- Insert 1: shift 5, 4, 3 and 2.

Total shifts:

```text
1 + 2 + 3 + 4 = 10
```

For `n` elements, the worst-case number of shifts is:

\[
\frac{n(n-1)}{2}
\]

Therefore:

```text
Worst Case = O(n²)
```

### Average Case: O(n²)

In a randomly arranged array, elements typically need to move through part of the sorted portion.

On average, the algorithm performs a quadratic amount of work.

Therefore:

```text
Average Case = O(n²)
```

## 8. Space Complexity

Insertion Sort sorts the array in place.

It uses only a few extra variables:

- `key`
- `i`
- `j`

It does not create another array.

| Type | Space Complexity |
|---|---|
| Auxiliary Space | O(1) |

**Insertion Sort is an in-place sorting algorithm.**

## 9. Properties of Insertion Sort

| Property | Description |
|---|---|
| In-place | Yes |
| Stable | Yes, with the standard strict comparison |
| Adaptive | Yes |
| Online | Yes |
| Comparison-based | Yes |
| Best Time | O(n) |
| Average Time | O(n²) |
| Worst Time | O(n²) |
| Auxiliary Space | O(1) |

### Why is Insertion Sort stable?

A stable sorting algorithm preserves the relative order of elements with equal values.

Consider:

```text
[(A, 5), (B, 3), (C, 5)]
```

We sort these records by their numeric value.

The result is:

```text
[(B, 3), (A, 5), (C, 5)]
```

Notice that `A` still appears before `C`.

Insertion Sort maintains this order because it shifts elements only when:

```java
arr[j] > key
```

It does not shift equal elements.

If we used `>=` instead, equal elements could move past one another, breaking stability.

### Why is Insertion Sort adaptive?

An adaptive algorithm performs better when the input is already or nearly sorted.

Insertion Sort is adaptive because it only shifts elements that are greater than the current key.

For an already sorted array, it performs O(n) work.

### Why is Insertion Sort online?

An online sorting algorithm can process elements as they arrive, without needing the entire input in advance.

For example, if you receive exam scores one at a time, you can insert each new score into the correct position in your existing sorted list.

## 10. Insertion Sort vs Bubble Sort vs Selection Sort

These three algorithms are commonly compared in coding interviews.

| Feature | Bubble Sort | Selection Sort | Insertion Sort |
|---|---|---|---|
| Best Time | O(n)* | O(n²) | O(n) |
| Average Time | O(n²) | O(n²) | O(n²) |
| Worst Time | O(n²) | O(n²) | O(n²) |
| Auxiliary Space | O(1) | O(1) | O(1) |
| Stable | Yes* | No* | Yes |
| Adaptive | Yes* | No | Yes |
| In-place | Yes | Yes | Yes |
| Main Operation | Swapping | Selecting minimum | Shifting and inserting |

\* Bubble Sort's O(n) best case requires an early-exit optimization. Standard Selection Sort is unstable; stable variants are possible.

### Which one should you choose?

- **Bubble Sort:** Useful mainly for learning sorting fundamentals; rarely chosen in production.
- **Selection Sort:** Useful when minimizing the number of swaps is important.
- **Insertion Sort:** A good choice for small or nearly sorted arrays.

Insertion Sort is often more practical than Bubble Sort and Selection Sort for small datasets.

## 11. Sorting Objects Using Insertion Sort

Insertion Sort can also sort objects using their properties.

For example, consider sorting students by marks.

```java
class Student {
    String name;
    int marks;

    Student(String name, int marks) {
        this.name = name;
        this.marks = marks;
    }
}
```

### Sort students by marks

```java
public static void insertionSortStudents(Student[] students) {

    for (int i = 1; i < students.length; i++) {

        Student key = students[i];
        int j = i - 1;

        while (j >= 0 && students[j].marks > key.marks) {
            students[j + 1] = students[j];
            j--;
        }

        students[j + 1] = key;
    }
}
```

This sorts students in ascending order of marks.

Because the comparison uses `>` rather than `>=`, students with equal marks retain their original relative order.

## 12. Advantages of Insertion Sort

1. Simple to understand and implement.
2. Requires only O(1) auxiliary space.
3. Efficient for small datasets.
4. Performs well on nearly sorted arrays.
5. Stable sorting algorithm.
6. Can process incoming elements incrementally.
7. Does not require an additional array.

## 13. Disadvantages of Insertion Sort

1. Inefficient for large datasets.
2. Average and worst-case time complexity is O(n²).
3. Reverse-sorted arrays require many shifts.
4. Generally slower than O(n log n) sorting algorithms for large inputs.

## 14. Real-World Applications

Insertion Sort is useful in several situations:

- Sorting small collections of records.
- Maintaining sorted lists as new elements arrive.
- Sorting nearly sorted datasets.
- Serving as a small-array sorting technique inside hybrid sorting algorithms.
- Sorting small partitions in algorithms such as Quick Sort implementations.

**Important:** Production sorting implementations often use more sophisticated hybrid algorithms. For example, Java's object-array sorting uses TimSort, while primitive-array sorting uses an implementation based on Dual-Pivot Quicksort.

## 15. Common Mistakes

### Mistake 1: Starting the loop at index 0

Incorrect:

```java
for (int i = 0; i < arr.length; i++)
```

Correct:

```java
for (int i = 1; i < arr.length; i++)
```

The first element is already considered sorted.

### Mistake 2: Forgetting to store the key

Incorrect:

```java
int j = i - 1;
```

If you start shifting without saving the current element, its original value may be overwritten.

Correct:

```java
int key = arr[i];
```

### Mistake 3: Using `j > 0` instead of `j >= 0`

Incorrect:

```java
while (j > 0 && arr[j] > key)
```

This prevents the algorithm from comparing the first element.

Correct:

```java
while (j >= 0 && arr[j] > key)
```

### Mistake 4: Forgetting to insert the key

After shifting elements, always place the key into its correct position:

```java
arr[j + 1] = key;
```

### Mistake 5: Using `>=` instead of `>`

Using `>=` can break the stability of Insertion Sort by moving equal elements past each other.

## 16. Interview Questions and Answers

**Q1. What is Insertion Sort?**

Insertion Sort is a comparison-based sorting algorithm that builds a sorted portion of an array by inserting each new element into its correct position.

**Q2. What is the time complexity of Insertion Sort?**

- Best: O(n)
- Average: O(n²)
- Worst: O(n²)

**Q3. Why is the best-case complexity O(n)?**

When the array is already sorted, each element requires only one comparison and no shifting.

**Q4. Why is the worst-case complexity O(n²)?**

When the array is reverse sorted, every new element must be shifted past all previously sorted elements.

**Q5. Is Insertion Sort stable?**

Yes. The standard implementation uses a strict `>` comparison, preserving the relative order of equal elements.

**Q6. Is Insertion Sort in-place?**

Yes. It modifies the original array and requires only O(1) auxiliary space.

**Q7. Is Insertion Sort adaptive?**

Yes. It performs fewer operations when the input is already or nearly sorted.

**Q8. Why do we use a variable called `key`?**

The key stores the current element while larger elements are shifted to the right. It is inserted into the resulting empty position.

**Q9. What is the maximum number of shifts in the worst case?**

For an array of `n` elements:

\[
\frac{n(n-1)}{2}
\]

**Q10. Can Insertion Sort work with a LinkedList?**

Yes, but the implementation and performance depend on how elements are accessed and inserted. An array-based implementation that repeatedly accesses elements by index is not suitable for a LinkedList. A linked-list-specific insertion approach can insert nodes without shifting array elements.

**Q11. When should we use Insertion Sort?**

For small datasets, nearly sorted arrays, or when maintaining a sorted collection as new elements arrive.

**Q12. How is Insertion Sort different from Selection Sort?**

Insertion Sort inserts each element into its correct position by shifting larger elements. Selection Sort repeatedly finds the smallest remaining element and swaps it into the next sorted position.

## 17. Practice Problems

Try solving these problems using Insertion Sort.

### Beginner

1. Sort an integer array in ascending order.
2. Sort an integer array in descending order.
3. Count the number of shifts performed.
4. Find the number of passes required to sort an array.
5. Sort an array containing duplicate values.

### Intermediate

1. Sort an array of student records by marks.
2. Sort an array of strings alphabetically.
3. Implement Insertion Sort using a separate method.
4. Modify Insertion Sort to sort only a specified range.
5. Implement a stable Insertion Sort for objects.

### Advanced

1. Count the number of shifts required to sort an array.
2. Find the number of inversions in an array using a related insertion-based approach.
3. Implement binary insertion sort, which uses Binary Search to find the insertion position.
4. Compare the performance of Insertion Sort and Merge Sort on nearly sorted arrays.

**Note:** Binary insertion sort reduces the number of comparisons needed to find an insertion position, but shifting elements still takes O(n²) time in the worst case.

## 18. Quick Revision Cheat Sheet

| Concept | Answer |
|---|---|
| Algorithm | Insertion Sort |
| Approach | Insert each element into the sorted portion |
| First Sorted Element | Index 0 |
| Current Element | Key |
| Main Operation | Shifting |
| Best Time | O(n) |
| Average Time | O(n²) |
| Worst Time | O(n²) |
| Auxiliary Space | O(1) |
| Stable | Yes |
| Adaptive | Yes |
| In-place | Yes |
| Online | Yes |
| Best Use Case | Small or nearly sorted arrays |

### Remember

**Insertion Sort = Pick → Compare → Shift → Insert**

The key idea is simple: keep the left side sorted and insert every new element into its correct position.

