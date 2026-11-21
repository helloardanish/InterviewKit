---
title: "Merge Sort"
layout: default
parent: Data Structures & Algorithms
nav_order: 15
permalink: /data-structures/Merge-Sort/merge-sort/

topic: Merge Sort
section: Sorting Algorithms
concepts: [divide-and-conquer, merging, stable-sort]
complexities: [O(n log n)]
tags: [algorithms, sorting, merge-sort, divide-and-conquer, java]
---

# Merge Sort

## 1. What is Merge Sort?

**Merge Sort** is a popular sorting algorithm based on the **Divide and Conquer** technique.

Instead of sorting an entire array at once, Merge Sort:

1. **Divide:** Splits the array into smaller halves.
2. **Conquer:** Recursively sorts each half.
3. **Merge:** Combines the sorted halves into one sorted array.

It keeps dividing the array until every subarray contains only one element. A single-element array is already sorted.

Then it merges these small sorted arrays into larger sorted arrays until the entire array is sorted.

### Real-world examples

- **Sorting exam papers:** Divide a large collection of exam papers into smaller groups, sort each group by marks, and merge the groups in order.
- **Organizing books:** Divide a large pile of books into smaller piles, arrange each pile alphabetically, and combine them into one sorted collection.
- **Merging contact lists:** Two people have separate contact lists sorted alphabetically. You can combine them into one sorted list by repeatedly comparing the next contact from each list.
- **Large datasets:** When a dataset is too large to sort efficiently in memory, external Merge Sort can sort smaller chunks and merge them from storage.

## 2. How Does Merge Sort Work?

Consider the following array:

```text
[38, 27, 43, 3, 9, 82, 10]
```

We want to sort it in ascending order.

Merge Sort follows two main activities:

- Splitting the array into smaller parts.
- Merging those parts in sorted order.

### Phase 1: Divide

Keep dividing the array into two halves until every subarray contains one element.

```text
                    [38, 27, 43, 3, 9, 82, 10]
                              /       \
                 [38, 27, 43]         [3, 9, 82, 10]
                   /     \               /       \
              [38]    [27, 43]       [3, 9]    [82, 10]
                       /    \         /   \       /   \
                    [27]  [43]      [3]  [9]   [82] [10]
```

Notice that the array has an odd number of elements.

The two halves do not always have the same size. One side can contain one more element than the other.

Continue until all subarrays contain exactly one element.

**Why stop at one element?**

Because an array containing only one element is already sorted. There is nothing to compare or rearrange.

### Phase 2: Merge

Now start combining the smaller arrays in sorted order.

To merge two sorted arrays:

1. Compare the first available element from each array.
2. Copy the smaller element into the result.
3. Move forward in the array from which the element was taken.
4. Repeat until one array is empty.
5. Copy all remaining elements from the other array.

#### Merge 1: Combine [27] and [43]

```text
Left:  [27]
Right: [43]

Compare 27 and 43.

27 < 43, so take 27 first.
Then take 43.

Result: [27, 43]
```

#### Merge 2: Combine [38] and [27, 43]

```text
Left:  [38]
Right: [27, 43]

Compare 38 and 27 → Take 27.
Compare 38 and 43 → Take 38.
Left is empty → Take remaining 43.

Result: [27, 38, 43]
```

#### Merge 3: Combine [3] and [9]

```text
Left:  [3]
Right: [9]

Compare 3 and 9 → Take 3.
Take remaining 9.

Result: [3, 9]
```

#### Merge 4: Combine [82] and [10]

```text
Left:  [82]
Right: [10]

Compare 82 and 10 → Take 10.
Take remaining 82.

Result: [10, 82]
```

#### Merge 5: Combine [3, 9] and [10, 82]

```text
Left:  [3, 9]
Right: [10, 82]

Compare 3 and 10 → Take 3.
Compare 9 and 10 → Take 9.
Left is empty → Take 10 and 82.

Result: [3, 9, 10, 82]
```

#### Final Merge: Combine [27, 38, 43] and [3, 9, 10, 82]

```text
Left:  [27, 38, 43]
Right: [3, 9, 10, 82]

Compare 27 and 3  → Take 3.
Compare 27 and 9  → Take 9.
Compare 27 and 10 → Take 10.
Compare 27 and 82 → Take 27.
Compare 38 and 82 → Take 38.
Compare 43 and 82 → Take 43.
Left is empty → Take remaining 82.

Result: [3, 9, 10, 27, 38, 43, 82]
```

<caption>Final sorted array (ascending order)</caption>

```text
[3, 9, 10, 27, 38, 43, 82]
```

## 3. Visualize Merge Sort

Use the interactive visualization to follow the complete process:

- Watch the array split into smaller subarrays.
- See how recursion reaches single-element arrays.
- Observe comparisons between the front elements of two sorted arrays.
- Follow how sorted results grow during merging.

## 4. Merge Sort Algorithm

Merge Sort is usually implemented using two methods:

1. `mergeSort()` — divides the array recursively.
2. `merge()` — combines two sorted portions.

### Pseudocode

```text
mergeSort(array, left, right):

    if left >= right:
        return

    mid = left + (right - left) / 2

    mergeSort(array, left, mid)
    mergeSort(array, mid + 1, right)

    merge(array, left, mid, right)
```

### Merge pseudocode

```text
merge(array, left, mid, right):

    Create temporary left and right arrays

    i = 0
    j = 0
    k = left

    while i < leftArray.length AND j < rightArray.length:

        if leftArray[i] <= rightArray[j]:
            array[k] = leftArray[i]
            i++
        else:
            array[k] = rightArray[j]
            j++

        k++

    Copy remaining elements from leftArray

    Copy remaining elements from rightArray
```

## 5. Java Implementation

### Merge Sort (Ascending Order)

```java
import java.util.Arrays;

public class MergeSort {

    public static void mergeSort(int[] arr, int left, int right) {

        // Base condition: one element is already sorted
        if (left >= right) {
            return;
        }

        // Find the middle index safely
        int mid = left + (right - left) / 2;

        // Divide the array into two halves
        mergeSort(arr, left, mid);
        mergeSort(arr, mid + 1, right);

        // Merge the sorted halves
        merge(arr, left, mid, right);
    }

    private static void merge(
            int[] arr, int left, int mid, int right) {

        int n1 = mid - left + 1;
        int n2 = right - mid;

        // Create temporary arrays
        int[] leftArray = new int[n1];
        int[] rightArray = new int[n2];

        // Copy data into temporary arrays
        for (int i = 0; i < n1; i++) {
            leftArray[i] = arr[left + i];
        }

        for (int j = 0; j < n2; j++) {
            rightArray[j] = arr[mid + 1 + j];
        }

        int i = 0;
        int j = 0;
        int k = left;

        // Compare and merge elements
        while (i < n1 && j < n2) {

            if (leftArray[i] <= rightArray[j]) {
                arr[k] = leftArray[i];
                i++;
            } else {
                arr[k] = rightArray[j];
                j++;
            }

            k++;
        }

        // Copy remaining elements from left array
        while (i < n1) {
            arr[k] = leftArray[i];
            i++;
            k++;
        }

        // Copy remaining elements from right array
        while (j < n2) {
            arr[k] = rightArray[j];
            j++;
            k++;
        }
    }

    public static void main(String[] args) {

        int[] arr = {38, 27, 43, 3, 9, 82, 10};

        mergeSort(arr, 0, arr.length - 1);

        System.out.println(Arrays.toString(arr));
    }
}
```

**Output:**

```text
[3, 9, 10, 27, 38, 43, 82]
```

### Understanding the important parts

**1. Base condition**

```java
if (left >= right) {
    return;
}
```

Stops recursion when the current portion contains zero or one element.

**2. Calculate the middle index**

```java
int mid = left + (right - left) / 2;
```

Finds the middle without using `(left + right) / 2`, which could overflow for very large integer indices.

**3. Divide into two halves**

```java
mergeSort(arr, left, mid);
mergeSort(arr, mid + 1, right);
```

Sorts the left half and right half independently.

**4. Merge sorted halves**

```java
merge(arr, left, mid, right);
```

Combines the two sorted portions into one sorted portion.

**5. Create temporary arrays**

```java
int[] leftArray = new int[n1];
int[] rightArray = new int[n2];
```

The temporary arrays preserve the values from both halves while the original array is overwritten with merged results.

**6. Compare elements**

```java
if (leftArray[i] <= rightArray[j]) {
    arr[k] = leftArray[i];
    i++;
} else {
    arr[k] = rightArray[j];
    j++;
}
```

Always copy the smaller available element into the original array.

Notice that we use `<=` instead of `<`. This helps preserve stability when equal elements are encountered.

**7. Copy remaining elements**

```java
while (i < n1) {
    arr[k] = leftArray[i];
    i++;
    k++;
}
```

Once one temporary array is exhausted, the remaining elements in the other array are already sorted. They can be copied directly.

The same logic is applied to the right array.

## 6. Merge Sort in Descending Order

To sort in descending order, reverse the comparison condition in the merge method.

Ascending:

```java
if (leftArray[i] <= rightArray[j])
```

Descending:

```java
if (leftArray[i] >= rightArray[j])
```

### Java Implementation

```java
private static void mergeDescending(
        int[] arr, int left, int mid, int right) {

    int[] leftArray =
            Arrays.copyOfRange(arr, left, mid + 1);

    int[] rightArray =
            Arrays.copyOfRange(arr, mid + 1, right + 1);

    int i = 0;
    int j = 0;
    int k = left;

    while (i < leftArray.length && j < rightArray.length) {

        if (leftArray[i] >= rightArray[j]) {
            arr[k++] = leftArray[i++];
        } else {
            arr[k++] = rightArray[j++];
        }
    }

    while (i < leftArray.length) {
        arr[k++] = leftArray[i++];
    }

    while (j < rightArray.length) {
        arr[k++] = rightArray[j++];
    }
}
```

Use this method in place of the ascending `merge()` method in the main Merge Sort implementation.

**Example:**

```text
Input:  [38, 27, 43, 3, 9, 82, 10]
Output: [82, 43, 38, 27, 10, 9, 3]
```

## 7. Time Complexity

Merge Sort has the same time complexity in the best, average, and worst cases.

| Case | Time Complexity | Explanation |
|---|---|---|
| Best Case | O(n log n) | Array is still divided and merged |
| Average Case | O(n log n) | Consistent divide-and-merge work |
| Worst Case | O(n log n) | Same overall splitting and merging structure |

### Why is Merge Sort O(n log n)?

There are two important operations:

1. Dividing the array.
2. Merging the elements.

#### Step 1: Number of division levels

Each division approximately halves the input size.

For example, an array of 8 elements is divided as follows:

```text
Level 0: 8 elements
Level 1: 4 + 4
Level 2: 2 + 2 + 2 + 2
Level 3: 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1
```

The number of levels is approximately:

\[
\log_2(n)
\]

#### Step 2: Work performed at each level

At every merging level, all `n` elements are processed.

For example:

```text
Level 1: Merge 1-element arrays → n total elements
Level 2: Merge 2-element arrays → n total elements
Level 3: Merge 4-element arrays → n total elements
```

Each level performs O(n) work.

#### Step 3: Total complexity

There are O(log n) levels, and each level performs O(n) work.

Therefore:

\[
O(n) \times O(\log n) = O(n\log n)
\]

**Final time complexity: O(n log n).**

### Recurrence relation

The time complexity can also be expressed using:

\[
T(n) = 2T(n/2) + O(n)
\]

Where:

- `2T(n/2)` represents sorting the two halves.
- `O(n)` represents merging the elements.

Using the Master Theorem:

\[
T(n) = O(n\log n)
\]

## 8. Space Complexity

Merge Sort requires temporary arrays during merging.

For an array of `n` elements, the temporary arrays together hold up to `n` elements.

Therefore:

| Component | Space Complexity |
|---|---|
| Temporary arrays | O(n) |
| Recursion stack | O(log n) |
| Total auxiliary space | O(n) |

The O(n) temporary storage dominates the recursion stack.

**Important:** The common array-based Merge Sort implementation is not an in-place sorting algorithm because it requires additional O(n) memory.

## 9. Properties of Merge Sort

| Property | Description |
|---|---|
| Algorithm | Divide and Conquer |
| Best Time | O(n log n) |
| Average Time | O(n log n) |
| Worst Time | O(n log n) |
| Auxiliary Space | O(n) |
| Stable | Yes, with correct merging |
| In-place | No, in the standard array implementation |
| Adaptive | No, in the standard implementation |
| Comparison-based | Yes |

### Why is Merge Sort stable?

A stable sorting algorithm preserves the relative order of elements with equal values.

Consider:

```text
[(A, 5), (B, 3), (C, 5)]
```

We sort these records by their numeric value.

The correct stable result is:

```text
[(B, 3), (A, 5), (C, 5)]
```

`A` appears before `C` in the original input and remains before `C` after sorting.

This is achieved by choosing the left element when both compared values are equal:

```java
if (leftArray[i] <= rightArray[j])
```

This assumes the original left portion precedes the original right portion and that the recursive sorting and merging are themselves stable.

### Why is standard Merge Sort not adaptive?

The standard implementation continues dividing and merging even if the input is already sorted.

For example:

```text
[1, 2, 3, 4, 5, 6]
```

It still performs the recursive division and merging operations.

Therefore, its best-case time complexity remains O(n log n).

## 10. Merge Sort vs Other Sorting Algorithms

| Feature | Bubble Sort | Selection Sort | Insertion Sort | Merge Sort |
|---|---|---|---|---|
| Best Time | O(n)* | O(n²) | O(n) | O(n log n) |
| Average Time | O(n²) | O(n²) | O(n²) | O(n log n) |
| Worst Time | O(n²) | O(n²) | O(n²) | O(n log n) |
| Auxiliary Space | O(1) | O(1) | O(1) | O(n) |
| Stable | Yes* | No* | Yes | Yes |
| In-place | Yes | Yes | Yes | No* |
| Adaptive | Yes* | No | Yes | No* |

\* Bubble Sort's best case depends on early-exit optimization. Standard Selection Sort is unstable. Standard array-based Merge Sort requires O(n) additional space; specialized variants can have different space properties.

### When should you choose Merge Sort?

Choose Merge Sort when:

- You need guaranteed O(n log n) worst-case performance.
- Stability is important.
- You are sorting large datasets.
- You need to sort linked lists efficiently.
- You are working with external sorting, where data is too large to fit in memory.

## 11. Merge Sort for Objects

Merge Sort can also sort objects using their properties.

For example, sort students by marks.

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

### Compare students by marks

Inside the merge method, instead of comparing integers:

```java
if (leftArray[i] <= rightArray[j])
```

Use:

```java
if (leftArray[i].marks <= rightArray[j].marks)
```

The rest of the algorithm follows the same approach, but the temporary arrays should have type `Student[]`.

Because the comparison uses `<=`, students with equal marks retain their original relative order.

For reusable production code, Java's `Comparator` interface can make the sorting logic more flexible.

## 12. Merge Sort for Linked Lists

Merge Sort is particularly suitable for linked lists.

Why?

- Linked lists do not provide efficient random access by index.
- Merge Sort does not require repeatedly accessing the middle element through indexed operations.
- A linked list can be split using slow and fast pointers.
- Sorted linked lists can be merged by rearranging node references.

For a linked list:

- Finding the middle takes O(n).
- Merging two sorted lists takes O(n).
- The total time complexity is O(n log n).

A linked-list implementation can merge by changing node references instead of creating temporary arrays, so it can use O(1) auxiliary node storage during merging. The recursive implementation still requires O(log n) stack space.

## 13. External Merge Sort

What if you have a dataset containing billions of records, but your computer does not have enough RAM to load all of them?

This is where **External Merge Sort** is useful.

The basic process:

1. Divide the large dataset into smaller chunks that fit in memory.
2. Sort each chunk independently.
3. Store each sorted chunk on disk.
4. Merge the sorted chunks into one final sorted dataset.

Example:

```text
Large Dataset
     |
     v
Divide into smaller chunks
     |
     +------ Chunk 1 → Sort → File 1
     |
     +------ Chunk 2 → Sort → File 2
     |
     +------ Chunk 3 → Sort → File 3
     |
     v
Merge sorted files
     |
     v
Final sorted dataset
```

External Merge Sort is widely useful in database systems and large-scale data processing.

## 14. Advantages of Merge Sort

1. Guaranteed O(n log n) time complexity.
2. Stable sorting algorithm.
3. Works well for large datasets.
4. Suitable for linked lists.
5. Useful for external sorting.
6. Predictable performance across different input arrangements.
7. Naturally supports parallel processing because separate halves can be sorted independently.

## 15. Disadvantages of Merge Sort

1. Requires O(n) additional memory for standard array-based implementations.
2. Temporary array creation and copying add overhead.
3. Recursive implementation uses additional stack space.
4. Usually less efficient than simpler algorithms for very small arrays.
5. Standard Merge Sort does not take advantage of already sorted input to reduce its asymptotic complexity.

## 16. Common Mistakes

### Mistake 1: Incorrect base condition

Incorrect:

```java
if (left == right) {
    return;
}
```

This does not correctly handle an empty range where `left > right`.

Correct:

```java
if (left >= right) {
    return;
}
```

### Mistake 2: Incorrect middle calculation

Less safe:

```java
int mid = (left + right) / 2;
```

Safer:

```java
int mid = left + (right - left) / 2;
```

This avoids potential integer overflow.

### Mistake 3: Incorrect temporary array sizes

Correct:

```java
int n1 = mid - left + 1;
int n2 = right - mid;
```

The left range includes both `left` and `mid`. The right range starts at `mid + 1`.

### Mistake 4: Forgetting remaining elements

After the main comparison loop, one temporary array may still contain elements.

Always copy the remaining elements from both arrays.

### Mistake 5: Using `<` instead of `<=`

Using `<` can make the standard merge operation unstable when equal elements are selected from the right half before equal elements from the left half.

Use:

```java
if (leftArray[i] <= rightArray[j])
```

### Mistake 6: Incorrect recursive ranges

Correct:

```java
mergeSort(arr, left, mid);
mergeSort(arr, mid + 1, right);
```

The two ranges must not overlap, and neither should be skipped.

### Mistake 7: Forgetting the empty array case

For an empty array, `arr.length - 1` is `-1`.

The base condition handles this correctly:

```java
mergeSort(arr, 0, arr.length - 1);
```

## 17. Interview Questions and Answers

**Q1. What is Merge Sort?**

Merge Sort is a stable comparison-based sorting algorithm that uses Divide and Conquer. It divides the array into smaller parts, sorts them recursively, and merges the sorted parts.

**Q2. What is the time complexity of Merge Sort?**

Best, average, and worst cases are all O(n log n).

**Q3. Why is Merge Sort O(n log n)?**

The array is divided into O(log n) levels, and each level performs O(n) merging work.

**Q4. What is the recurrence relation of Merge Sort?**

`T(n) = 2T(n/2) + O(n)`.

**Q5. What is the space complexity of Merge Sort?**

O(n) auxiliary space for the standard array-based implementation, including temporary merge arrays. The recursion stack adds O(log n).

**Q6. Is Merge Sort stable?**

Yes, when the merge operation selects from the left portion first for equal elements.

**Q7. Is Merge Sort an in-place algorithm?**

The standard array implementation is not in-place because it uses O(n) temporary storage.

**Q8. Does Merge Sort perform better on an already sorted array?**

Its standard implementation still performs O(n log n) work, even when the input is already sorted.

**Q9. Why is Merge Sort preferred for LinkedLists?**

It does not require efficient random access. Splitting and merging can be performed by traversing and rearranging node references.

**Q10. What is External Merge Sort?**

It is a technique for sorting datasets larger than available memory. Data is sorted in smaller chunks and then merged from external storage.

**Q11. Can Merge Sort be implemented iteratively?**

Yes. Bottom-up Merge Sort starts by merging adjacent one-element runs, then two-element runs, four-element runs, and so on. It avoids recursive calls.

**Q12. Why do we need temporary arrays?**

They preserve the values from the two sorted halves while the merged result is written back into the original array.

**Q13. Can Merge Sort be parallelized?**

Yes. The two halves can be sorted independently, and their results can then be merged. Parallel execution introduces scheduling and synchronization overhead.

**Q14. Which is better: Merge Sort or Quick Sort?**

It depends on the requirements.

- Merge Sort guarantees O(n log n) worst-case time and is stable.
- Quick Sort generally performs well on arrays and can use less auxiliary memory, but its worst case is O(n²) for standard implementations.

## 18. Practice Problems

### Beginner

1. Implement Merge Sort for an integer array.
2. Sort an array in descending order.
3. Merge two already sorted arrays.
4. Count the number of comparisons during merging.
5. Sort an array containing duplicate values.

### Intermediate

1. Sort an array of student objects by marks.
2. Sort an array of strings alphabetically.
3. Implement iterative (bottom-up) Merge Sort.
4. Merge two sorted linked lists.
5. Find the middle node of a linked list using slow and fast pointers.

### Advanced

1. Count inversions in an array using Merge Sort.
2. Find the reverse pairs in an array.
3. Find the K-th smallest element using an appropriate sorting or selection approach.
4. Implement External Merge Sort for large files.
5. Implement a parallel Merge Sort using Java's ForkJoin framework.

## 19. Quick Revision Cheat Sheet

| Concept | Answer |
|---|---|
| Algorithm | Merge Sort |
| Approach | Divide and Conquer |
| Main Steps | Divide, Sort, Merge |
| Base Condition | `left >= right` |
| Best Time | O(n log n) |
| Average Time | O(n log n) |
| Worst Time | O(n log n) |
| Auxiliary Space | O(n) |
| Recursion Stack | O(log n) |
| Stable | Yes |
| In-place | No, standard array implementation |
| Adaptive | No, standard implementation |
| Comparison-based | Yes |
| Best Use Cases | Large datasets, linked lists, external sorting |

### Remember

**Merge Sort = Divide → Sort → Merge**

The key idea is simple:

- Break a large problem into smaller problems.
- Solve the smaller problems.
- Combine their results to solve the original problem.

Unlike Bubble Sort, Selection Sort, and Insertion Sort, Merge Sort guarantees O(n log n) time complexity even in the worst case.

