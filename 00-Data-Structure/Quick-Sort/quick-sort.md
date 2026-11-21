---
title: "Quick Sort"
layout: default
parent: Data Structures & Algorithms
nav_order: 16
permalink: /data-structures/Quick-Sort/quick-sort/

topic: Quick Sort
section: Sorting Algorithms
concepts: [divide-and-conquer, pivot, partitioning, in-place-sort]
complexities: [O(n log n), O(n^2)]
tags: [algorithms, sorting, quick-sort, divide-and-conquer, java]
---

# Quick Sort

## 1. What is Quick Sort?

**Quick Sort** is a popular comparison-based sorting algorithm that uses the **Divide and Conquer** technique.

It selects an element called a **pivot** and rearranges the array so that:

- Elements smaller than the pivot are placed on its left.
- Elements greater than the pivot are placed on its right.
- The pivot reaches its correct sorted position.

Then Quick Sort recursively sorts the left and right portions.

Unlike Merge Sort, which divides the array into halves before sorting, Quick Sort divides the array based on the pivot's value.

### Real-world examples

- **Arranging students by height:** Choose one student as a reference. Move shorter students to one side and taller students to the other. Repeat the process for each group.
- **Organizing books by price:** Choose one book's price as a reference. Place cheaper books on one side and more expensive books on the other.
- **Arranging exam scores:** Choose a score as a pivot and divide the remaining scores into lower and higher groups.

## 2. How Does Quick Sort Work?

Consider this array:

```text
[10, 7, 8, 9, 1, 5]
```

We want to sort it in ascending order.

Quick Sort follows three main steps:

1. Choose a pivot.
2. Partition the array around the pivot.
3. Recursively sort the left and right partitions.

### Step 1: Choose a pivot

For this example, we will use the **last element as the pivot**.

```text
[10, 7, 8, 9, 1, 5]
                     ^
                   Pivot
```

Pivot = `5`

### Step 2: Partition the array

Move elements smaller than or equal to the pivot to its left.

Move elements greater than the pivot to its right.

The array initially looks like:

```text
[10, 7, 8, 9, 1, 5]
```

Compare each element with pivot `5`:

| Element | Comparison | Action |
|---:|---|---|
| 10 | 10 > 5 | Leave on right side |
| 7 | 7 > 5 | Leave on right side |
| 8 | 8 > 5 | Leave on right side |
| 9 | 9 > 5 | Leave on right side |
| 1 | 1 <= 5 | Move to left side |

Swap `1` with `10`:

```text
Before: [10, 7, 8, 9, 1, 5]
After:  [1, 7, 8, 9, 10, 5]
```

Finally, place the pivot after the smaller elements by swapping it with `7`.

```text
Before: [1, 7, 8, 9, 10, 5]
After:  [1, 5, 8, 9, 10, 7]
```

Now the pivot `5` is in its correct sorted position.

```text
[1 | 5 | 8, 9, 10, 7]
```

- Left partition: `[1]`
- Pivot: `5`
- Right partition: `[8, 9, 10, 7]`

**Important:** The right partition is not necessarily sorted. Only the pivot's position is guaranteed to be correct.

### Step 3: Recursively sort the partitions

Now apply Quick Sort to the left and right partitions.

The left partition contains only one element, so it is already sorted.

For the right partition:

```text
[8, 9, 10, 7]
```

Choose the last element as the pivot.

Pivot = `7`

Since all other elements are greater than `7`, the pivot moves to the beginning:

```text
[7, 9, 10, 8]
```

Now:

```text
[7 | 9, 10, 8]
```

Continue sorting the right portion `[9, 10, 8]`.

Choose `8` as pivot:

```text
[8, 10, 9]
```

Continue with `[10, 9]`.

Choose `9` as pivot:

```text
[9, 10]
```

All partitions are now sorted.

### Final sorted array

```text
[1, 5, 7, 8, 9, 10]
```

## 3. Visualize Quick Sort

Use the interactive visualization to observe:

- How the pivot is selected.
- How the left and right pointers scan the array.
- How elements are exchanged.
- How a partition is completed.
- How recursive calls sort the remaining partitions.

## 4. Quick Sort Algorithm

Quick Sort is generally implemented using two methods:

1. `quickSort()` — recursively sorts the partitions.
2. `partition()` — rearranges elements around the pivot.

### Pseudocode

```text
quickSort(array, low, high):

    if low < high:

        pivotIndex = partition(array, low, high)

        quickSort(array, low, pivotIndex - 1)

        quickSort(array, pivotIndex + 1, high)
```

### Partition pseudocode (Lomuto Partition Scheme)

```text
partition(array, low, high):

    pivot = array[high]

    i = low - 1

    for j = low to high - 1:

        if array[j] <= pivot:

            i++

            swap array[i] and array[j]

    swap array[i + 1] and array[high]

    return i + 1
```

### Understanding the variables

| Variable | Meaning |
|---|---|
| `low` | Starting index of the current partition |
| `high` | Ending index of the current partition |
| `pivot` | Element used to divide the array |
| `i` | Boundary of elements smaller than or equal to the pivot |
| `j` | Scans the current partition |
| `pivotIndex` | Final sorted position of the pivot |

## 5. Java Implementation

### Quick Sort using Lomuto Partition Scheme

This implementation chooses the last element as the pivot.

```java
import java.util.Arrays;

public class QuickSort {

    public static void quickSort(int[] arr, int low, int high) {

        if (low < high) {

            // Partition the array and get pivot position
            int pivotIndex = partition(arr, low, high);

            // Sort elements before pivot
            quickSort(arr, low, pivotIndex - 1);

            // Sort elements after pivot
            quickSort(arr, pivotIndex + 1, high);
        }
    }

    private static int partition(int[] arr, int low, int high) {

        // Choose the last element as pivot
        int pivot = arr[high];

        // Boundary of elements <= pivot
        int i = low - 1;

        // Traverse all elements except pivot
        for (int j = low; j < high; j++) {

            if (arr[j] <= pivot) {

                i++;

                // Swap elements
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }

        // Place pivot in its correct position
        int temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;

        return i + 1;
    }

    public static void main(String[] args) {

        int[] arr = {10, 7, 8, 9, 1, 5};

        quickSort(arr, 0, arr.length - 1);

        System.out.println(Arrays.toString(arr));
    }
}
```

**Output:**

```text
[1, 5, 7, 8, 9, 10]
```

### Understanding the important parts

**1. Base condition**

```java
if (low < high)
```

If the current partition contains zero or one element, it is already sorted.

No further recursive calls are required.

**2. Choose the pivot**

```java
int pivot = arr[high];
```

Select the last element as the pivot.

**3. Initialize the boundary**

```java
int i = low - 1;
```

Initially, no elements have been identified as smaller than or equal to the pivot.

The boundary starts immediately before the current partition.

**4. Scan the array**

```java
for (int j = low; j < high; j++)
```

The `j` pointer visits every element except the pivot.

**5. Compare with the pivot**

```java
if (arr[j] <= pivot)
```

If the current element is smaller than or equal to the pivot, it belongs in the left partition.

**6. Expand the left partition**

```java
i++;

int temp = arr[i];
arr[i] = arr[j];
arr[j] = temp;
```

Move the boundary forward and swap the current element into the left partition.

**7. Place the pivot**

```java
int temp = arr[i + 1];
arr[i + 1] = arr[high];
arr[high] = temp;
```

After scanning all elements, place the pivot immediately after the smaller-or-equal elements.

**8. Return the pivot index**

```java
return i + 1;
```

This is the pivot's final position.

The pivot does not need to be considered again during recursive sorting.

## 6. Quick Sort in Descending Order

To sort in descending order, reverse the partition comparison.

Ascending:

```java
if (arr[j] <= pivot)
```

Descending:

```java
if (arr[j] >= pivot)
```

### Java Implementation

```java
public static void quickSortDescending(
        int[] arr, int low, int high) {

    if (low < high) {

        int pivotIndex = partitionDescending(arr, low, high);

        quickSortDescending(arr, low, pivotIndex - 1);

        quickSortDescending(arr, pivotIndex + 1, high);
    }
}

private static int partitionDescending(
        int[] arr, int low, int high) {

    int pivot = arr[high];

    int i = low - 1;

    for (int j = low; j < high; j++) {

        if (arr[j] >= pivot) {

            i++;

            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

    int temp = arr[i + 1];
    arr[i + 1] = arr[high];
    arr[high] = temp;

    return i + 1;
}
```

**Example:**

```text
Input:  [10, 7, 8, 9, 1, 5]
Output: [10, 9, 8, 7, 5, 1]
```

## 7. Pivot Selection Strategies

Choosing a good pivot is important because it affects Quick Sort's performance.

### 1. First Element as Pivot

Choose the first element.

```text
[10, 7, 8, 9, 1, 5]
 ^
Pivot = 10
```

Simple to implement, but can perform poorly on already sorted or reverse-sorted arrays with certain partition schemes.

### 2. Last Element as Pivot

Choose the last element.

```text
[10, 7, 8, 9, 1, 5]
                     ^
                 Pivot = 5
```

This is used in the Lomuto implementation above.

It is easy to understand and implement, but can lead to poor partitions.

### 3. Middle Element as Pivot

Choose the middle-position element.

```text
[10, 7, 8, 9, 1, 5]
          ^
       Pivot = 8
```

This can avoid some poor cases, but selecting the middle-position element does not guarantee a balanced partition.

### 4. Random Pivot

Choose a random element as the pivot.

This reduces the likelihood that a particular input arrangement consistently produces poor partitions.

Randomization gives expected O(n log n) performance, but the worst case remains O(n²).

### 5. Median-of-Three

Choose the median value among:

- First element
- Middle element
- Last element

For example:

```text
First  = 10
Middle = 8
Last   = 5
```

The median is `8`.

This strategy often provides better practical pivot choices than simply selecting the first or last element.

**Important:** No pivot strategy guarantees balanced partitions for every possible input.

## 8. Time Complexity

Quick Sort's time complexity depends on how balanced the partitions are.

| Case | Time Complexity | Explanation |
|---|---|---|
| Best Case | O(n log n) | Pivot divides the array into approximately equal halves |
| Average Case | O(n log n) | Partitions are reasonably balanced on average |
| Worst Case | O(n²) | Pivot repeatedly creates highly unbalanced partitions |

### Best Case: O(n log n)

Suppose every pivot divides the array into two approximately equal parts.

For example:

```text
                 8 elements
                /          \
            4 elements    4 elements
             /    \        /    \
            2      2      2      2
           / \    / \    / \    / \
          1   1  1   1  1   1  1   1
```

The number of partition levels is approximately:

\[
\log_2(n)
\]

Each partitioning level processes a total of approximately `n` elements.

Therefore:

\[
O(n) \times O(\log n) = O(n\log n)
\]

### Worst Case: O(n²)

The worst case occurs when the pivot repeatedly creates one empty partition and another partition containing almost all remaining elements.

For example, with an already sorted array and the last-element pivot:

```text
[1, 2, 3, 4, 5]
```

The first pivot is `5`.

```text
[1, 2, 3, 4] | 5
```

The next pivot is `4`.

```text
[1, 2, 3] | 4 | 5
```

This continues until the array is sorted.

The work becomes:

```text
4 + 3 + 2 + 1
```

For `n` elements, the worst-case partitioning work is proportional to:

\[
\frac{n(n-1)}{2}
\]

Therefore:

\[
O(n^2)
\]

### Average Case: O(n log n)

With reasonably balanced partitions on average, Quick Sort performs O(n log n) work.

Randomized pivot selection is a common way to reduce the likelihood of consistently poor partitions.

## 9. Space Complexity

Quick Sort is commonly described as an in-place sorting algorithm because partitioning rearranges elements within the original array.

However, recursive calls require stack memory.

| Case | Recursion Stack |
|---|---|
| Best Case | O(log n) |
| Average Case | O(log n) |
| Worst Case | O(n) |

The standard Lomuto implementation uses O(1) auxiliary space per partition operation, excluding the recursion stack.

**Important:** Quick Sort is in-place with respect to array storage, but it is not strictly O(1) in total auxiliary space because of recursion.

## 10. Properties of Quick Sort

| Property | Description |
|---|---|
| Algorithm | Divide and Conquer |
| Best Time | O(n log n) |
| Average Time | O(n log n) |
| Worst Time | O(n²) |
| Average Stack Space | O(log n) |
| Worst Stack Space | O(n) |
| Stable | No, standard implementation |
| In-place | Yes, with recursion-stack qualification |
| Adaptive | No, standard implementation |
| Comparison-based | Yes |

### Why is Quick Sort unstable?

A stable sorting algorithm preserves the original relative order of equal elements.

Consider:

```text
[(A, 5), (B, 3), (C, 5)]
```

We sort by numeric value.

A possible partition can move `C` before `A` even though both have the same value.

The result could be:

```text
[(B, 3), (C, 5), (A, 5)]
```

The relative order of `A` and `C` has changed.

Therefore, standard Quick Sort is unstable.

## 11. Quick Sort vs Merge Sort

Both algorithms use Divide and Conquer, but their approaches are different.

| Feature | Quick Sort | Merge Sort |
|---|---|---|
| Approach | Partition around pivot | Divide into halves and merge |
| Best Time | O(n log n) | O(n log n) |
| Average Time | O(n log n) | O(n log n) |
| Worst Time | O(n²) | O(n log n) |
| Auxiliary Array Space | O(1) for standard in-place partition | O(n) |
| Recursion Stack | O(log n) average, O(n) worst | O(log n) |
| Stable | No, standard version | Yes, standard version |
| In-place | Yes, with stack qualification | No, standard array version |
| LinkedList Suitability | Less suitable | Very suitable |
| External Sorting | Less suitable | Very suitable |

### Which one should you choose?

Choose **Quick Sort** when:

- Sorting arrays in memory.
- Average performance is important.
- You want an in-place partitioning approach.
- Stability is not required.

Choose **Merge Sort** when:

- Guaranteed O(n log n) worst-case time is important.
- Stable sorting is required.
- Sorting linked lists.
- Working with external datasets.

## 12. Quick Sort with Duplicate Elements

Consider an array containing many duplicate values:

```text
[4, 2, 4, 1, 4, 3, 4]
```

The basic Lomuto implementation can handle duplicates correctly, but it may create unbalanced partitions when many elements are equal.

A useful improvement is **Three-Way Partitioning**.

It divides the array into three regions:

1. Elements smaller than the pivot.
2. Elements equal to the pivot.
3. Elements greater than the pivot.

Example:

```text
Input: [4, 2, 4, 1, 4, 3, 4]

Pivot = 4

Less than 4:    [2, 1, 3]
Equal to 4:     [4, 4, 4, 4]
Greater than 4: []

Result: [2, 1, 3 | 4, 4, 4, 4]
```

The algorithm only needs to recursively sort the smaller and greater regions.

This is particularly useful when the array contains many repeated values.

## 13. Advantages of Quick Sort

1. Average time complexity is O(n log n).
2. Performs well in practice for many in-memory array workloads.
3. Uses in-place partitioning.
4. Has good cache locality on arrays.
5. Does not require an additional O(n) merge buffer.
6. Supports different pivot selection strategies.
7. Can be optimized using randomized pivots and three-way partitioning.

## 14. Disadvantages of Quick Sort

1. Worst-case time complexity is O(n²).
2. Standard implementation is not stable.
3. Poor pivot selection can produce unbalanced partitions.
4. Recursive implementation can cause deep recursion.
5. Duplicate-heavy arrays can perform poorly with some partition schemes.

## 15. Common Mistakes

### Mistake 1: Incorrect base condition

Incorrect:

```java
if (low <= high)
```

This can cause unnecessary recursion or incorrect behavior.

Correct:

```java
if (low < high)
```

A partition containing one element is already sorted.

### Mistake 2: Incorrect pivot position

After partitioning, the pivot must be placed in its final position.

Correct:

```java
return i + 1;
```

### Mistake 3: Incorrect recursive ranges

Correct:

```java
quickSort(arr, low, pivotIndex - 1);
quickSort(arr, pivotIndex + 1, high);
```

Do not include the pivot again. Its position is already finalized.

### Mistake 4: Forgetting to swap the pivot

After scanning all elements, the pivot must be moved to the position after the smaller-or-equal elements.

```java
swap(arr, i + 1, high);
```

### Mistake 5: Using the wrong comparison

For ascending order:

```java
arr[j] <= pivot
```

For descending order:

```java
arr[j] >= pivot
```

### Mistake 6: Assuming the pivot divides the array equally

A pivot is not guaranteed to create equal-sized partitions.

A poor pivot can result in O(n²) performance.

### Mistake 7: Assuming Quick Sort is always O(n log n)

O(n log n) is the best and average-case complexity. The worst case is O(n²) for standard Quick Sort.

## 16. Interview Questions and Answers

**Q1. What is Quick Sort?**

Quick Sort is a comparison-based Divide and Conquer sorting algorithm that selects a pivot, partitions the array around it, and recursively sorts the resulting partitions.

**Q2. What is a pivot?**

A pivot is the element used to divide the array into smaller and larger elements.

**Q3. What is partitioning?**

Partitioning rearranges elements so that values belonging on one side of the pivot are placed there, and the pivot reaches its final sorted position.

**Q4. What is the time complexity of Quick Sort?**

- Best: O(n log n)
- Average: O(n log n)
- Worst: O(n²)

**Q5. Why does Quick Sort have O(n²) worst-case complexity?**

When the pivot repeatedly creates highly unbalanced partitions, each recursive call processes almost all remaining elements.

**Q6. Is Quick Sort stable?**

No. Standard partitioning can change the relative order of equal elements.

**Q7. Is Quick Sort in-place?**

Yes, standard partitioning rearranges elements in the original array. However, recursive calls require stack memory.

**Q8. Which pivot is best?**

There is no universally best pivot. Randomized selection and median-of-three are common strategies to improve practical partition quality.

**Q9. What happens if all elements are equal?**

With basic two-way Lomuto partitioning using `<=`, the pivot can repeatedly produce highly unbalanced partitions, resulting in O(n²) time. Three-way partitioning handles this situation much more efficiently.

**Q10. Can Quick Sort be implemented iteratively?**

Yes. An explicit stack can replace recursive calls.

**Q11. What is the difference between Lomuto and Hoare partition schemes?**

- Lomuto generally uses the last element as pivot and returns its final position.
- Hoare uses two scanning pointers and returns a partition boundary. The recursive ranges must be adjusted accordingly.

**Q12. Why is Quick Sort often fast in practice?**

It typically has good cache locality, low memory overhead, and efficient partitioning for arrays.

**Q13. Can Quick Sort sort a LinkedList efficiently?**

It is generally less suitable than Merge Sort because partitioning a linked list requires traversal and rearrangement of nodes, while random access is inefficient.

**Q14. How can we avoid Quick Sort's worst-case behavior?**

Use better pivot selection, randomized pivots, three-way partitioning for duplicates, or an introspective sorting strategy that switches to Heap Sort when recursion becomes too deep.

## 17. Practice Problems

### Beginner

1. Implement Quick Sort using the last element as pivot.
2. Sort an array in descending order.
3. Implement a separate `partition()` method.
4. Find the final position of a pivot after partitioning.
5. Sort an array containing duplicate values.

### Intermediate

1. Implement Quick Sort using the first element as pivot.
2. Implement randomized Quick Sort.
3. Implement median-of-three pivot selection.
4. Implement Hoare partitioning.
5. Implement three-way Quick Sort.
6. Count the number of comparisons and swaps.

### Advanced

1. Implement iterative Quick Sort using an explicit stack.
2. Find the K-th largest element using Quickselect.
3. Find the K-th smallest element using Quickselect.
4. Implement Introsort.
5. Compare Quick Sort and Merge Sort on different input distributions.

## 18. Quick Revision Cheat Sheet

| Concept | Answer |
|---|---|
| Algorithm | Quick Sort |
| Approach | Divide and Conquer |
| Main Technique | Partitioning |
| Pivot | Reference element used for partitioning |
| Best Time | O(n log n) |
| Average Time | O(n log n) |
| Worst Time | O(n²) |
| Average Stack Space | O(log n) |
| Worst Stack Space | O(n) |
| Stable | No |
| In-place | Yes, with recursion-stack qualification |
| Common Partition Schemes | Lomuto, Hoare |
| Common Pivot Strategies | First, Last, Middle, Random, Median-of-Three |
| Best Use Case | In-memory array sorting |

### Remember

**Quick Sort = Choose Pivot → Partition → Recursively Sort**

The key idea:

- Choose a pivot.
- Put smaller elements on one side and larger elements on the other.
- Fix the pivot in its correct position.
- Repeat for the remaining partitions.

Unlike Merge Sort, Quick Sort does not merge sorted halves. Its main work happens during partitioning.