---
title: "Selection Sort"
layout: default
parent: Data Structures & Algorithms
nav_order: 13
permalink: /data-structures/Selection-Sort/selection-sort/

topic: Selection Sort
section: Sorting Algorithms
concepts: [comparison-sort, minimum-selection, in-place-sort]
complexities: [O(n^2)]
tags: [algorithms, sorting, selection-sort, java]
---

# Selection Sort

## 1. What is Selection Sort?

**Selection Sort** is a simple comparison-based sorting algorithm that repeatedly finds the smallest element from the unsorted portion of an array and places it at the beginning of that portion.

It divides the array into two parts:

1. **Sorted portion:** Elements already placed in their correct positions.
2. **Unsorted portion:** Elements that still need to be sorted.

Initially, the entire array is unsorted.

In every pass, Selection Sort finds the smallest element in the unsorted portion and swaps it with the first element of that portion.

This process continues until the entire array is sorted.

### Real-world example: Arranging students by height

Imagine five students standing randomly in a row.

You want to arrange them from shortest to tallest.

How would you do it?

1. Look at all students and identify the shortest one.
2. Move that student to the first position.
3. Ignore the first position because it is now sorted.
4. Find the shortest student among the remaining students.
5. Move that student to the second position.
6. Repeat until everyone is arranged.

This is exactly how Selection Sort works.

**The important idea:** Find the smallest remaining element and put it in its correct position.

![Selection sort](./selection-sort.png)

## 2. How Does Selection Sort Work?

Consider the following unsorted array:

```java
int[] arr = {64, 25, 12, 22, 11};
```

We want to sort it in ascending order.

### Initial array

```text
[64, 25, 12, 22, 11]
```

Initially, every element belongs to the unsorted portion.

### Pass 1: Find the smallest element

Search the entire array:

```text
64, 25, 12, 22, 11
```

The smallest element is `11`.

Swap it with the first element, `64`.

```text
Before: [64, 25, 12, 22, 11]
After:  [11, 25, 12, 22, 64]
         ↑
    Sorted portion
```

Now `11` is in its correct position.

### Pass 2: Find the next smallest element

Ignore the first element.

Search only the remaining unsorted portion:

```text
[11 | 25, 12, 22, 64]
```

The smallest element in the unsorted portion is `12`.

Swap it with `25`.

```text
Before: [11, 25, 12, 22, 64]
After:  [11, 12, 25, 22, 64]
         ↑    ↑
       Sorted portion
```

### Pass 3: Find the next smallest element

Search:

```text
[11, 12 | 25, 22, 64]
```

The smallest element is `22`.

Swap it with `25`.

```text
Before: [11, 12, 25, 22, 64]
After:  [11, 12, 22, 25, 64]
         ↑    ↑   ↑
       Sorted portion
```

### Pass 4: Find the next smallest element

Search:

```text
[11, 12, 22 | 25, 64]
```

The smallest element is already `25`.

No swap is necessary.

```text
[11, 12, 22, 25, 64]
```

The array is sorted.

### Complete pass summary

| Pass | Minimum Found | Swap | Array After Pass |
|---|---:|---|---|
| Initial | — | — | 64, 25, 12, 22, 11 |
| 1 | 11 | 64 ↔ 11 | 11, 25, 12, 22, 64 |
| 2 | 12 | 25 ↔ 12 | 11, 12, 25, 22, 64 |
| 3 | 22 | 25 ↔ 22 | 11, 12, 22, 25, 64 |
| 4 | 25 | No swap | 11, 12, 22, 25, 64 |

<escape>```text
Final sorted array:

[11, 12, 22, 25, 64]

Sorted portion grows from left to right.
```</escape>

**Observation:** After every pass, one more element reaches its correct position at the beginning of the array.

## 3. Selection Sort Algorithm

### Pseudocode

```text
START

Given an array of n elements

FOR i = 0 TO n - 2:

    minIndex = i

    FOR j = i + 1 TO n - 1:

        IF arr[j] < arr[minIndex]:

            minIndex = j

    IF minIndex != i:

        SWAP arr[i] AND arr[minIndex]

END
```

### Understanding the two loops

**Outer loop (`i`)**

- Controls the current position where the next smallest element should be placed.
- After each pass, the sorted portion grows by one element.
- At most `n - 1` passes are needed.

**Inner loop (`j`)**

- Searches the remaining unsorted portion.
- Compares elements to find the smallest value.
- Updates `minIndex` whenever a smaller element is found.

**`minIndex`**

Stores the index of the smallest element found so far.

Notice that Selection Sort does not swap elements every time it finds a smaller value. It only remembers the minimum index and performs a swap after completing the entire search.

## 4. Java Implementation

### Approach 1: Basic Selection Sort

```java
import java.util.Arrays;

public class SelectionSort {

    public static void selectionSort(int[] arr) {

        int n = arr.length;

        for (int i = 0; i < n - 1; i++) {

            // Assume current element is the minimum
            int minIndex = i;

            // Find the actual minimum in the unsorted portion
            for (int j = i + 1; j < n; j++) {

                if (arr[j] < arr[minIndex]) {
                    minIndex = j;
                }
            }

            // Swap only if a smaller element was found
            if (minIndex != i) {

                int temp = arr[i];

                arr[i] = arr[minIndex];

                arr[minIndex] = temp;
            }
        }
    }

    public static void main(String[] args) {

        int[] arr = {64, 25, 12, 22, 11};

        selectionSort(arr);

        System.out.println(Arrays.toString(arr));
    }
}
```

**Output:**

```text
[11, 12, 22, 25, 64]
```

### Understanding the code

- `n` stores the total number of elements.
- `i` represents the position where the next smallest element should be placed.
- `minIndex = i` assumes the first unsorted element is the minimum.
- The inner loop searches for a smaller element.
- If a smaller element is found, `minIndex` is updated.
- After the inner loop finishes, the minimum element is swapped into position `i`.

**Important:** The swap happens after the inner loop, not inside it.

### Approach 2: Using a separate swap method

We can improve code readability by moving the swap operation into a separate method.

```java
public class SelectionSort {

    public static void selectionSort(int[] arr) {

        int n = arr.length;

        for (int i = 0; i < n - 1; i++) {

            int minIndex = findMinimumIndex(arr, i);

            swap(arr, i, minIndex);
        }
    }

    private static int findMinimumIndex(int[] arr, int start) {

        int minIndex = start;

        for (int j = start + 1; j < arr.length; j++) {

            if (arr[j] < arr[minIndex]) {
                minIndex = j;
            }
        }

        return minIndex;
    }

    private static void swap(int[] arr, int i, int j) {

        int temp = arr[i];

        arr[i] = arr[j];

        arr[j] = temp;
    }
}
```

This separates the responsibilities:

- `selectionSort()` controls sorting.
- `findMinimumIndex()` finds the smallest element.
- `swap()` exchanges two elements.

## 5. Selection Sort in Descending Order

So far, we have sorted elements in ascending order.

What if we want to sort them from largest to smallest?

Instead of finding the minimum element, we find the maximum element in the unsorted portion.

### Java implementation

```java
public static void selectionSortDescending(int[] arr) {

    int n = arr.length;

    for (int i = 0; i < n - 1; i++) {

        int maxIndex = i;

        for (int j = i + 1; j < n; j++) {

            if (arr[j] > arr[maxIndex]) {
                maxIndex = j;
            }
        }

        int temp = arr[i];

        arr[i] = arr[maxIndex];

        arr[maxIndex] = temp;
    }
}
```

Example:

```text
Input:  [64, 25, 12, 22, 11]

Output: [64, 25, 22, 12, 11]
```

Only the comparison condition changes:

| Sorting Order | Condition |
|---|---|
| Ascending | `arr[j] < arr[minIndex]` |
| Descending | `arr[j] > arr[maxIndex]` |

## 6. Time Complexity of Selection Sort

Selection Sort contains two nested loops.

Let's understand how many comparisons it performs.

For an array of `n` elements:

| Pass | Comparisons |
|---|---:|
| 1 | n - 1 |
| 2 | n - 2 |
| 3 | n - 3 |
| 4 | n - 4 |
| ... | ... |
| Last | 1 |

Total comparisons:

\[
(n-1)+(n-2)+\cdots+1
\]

Using the sum formula:

\[
\frac{n(n-1)}{2}
\]

Therefore:

\[
\boxed{O(n^2)}
\]

### Best Case — O(n²)

Consider an already sorted array:

```text
[1, 2, 3, 4, 5]
```

Even though the array is already sorted, Selection Sort still searches the remaining unsorted portion during every pass.

It must verify that no smaller element exists.

Total comparisons:

\[
\frac{n(n-1)}{2}
\]

Time Complexity: **O(n²)**

### Average Case — O(n²)

For a randomly ordered array, Selection Sort still performs the same number of comparisons.

Time Complexity: **O(n²)**

### Worst Case — O(n²)

Consider a reverse-sorted array:

```text
[5, 4, 3, 2, 1]
```

The algorithm searches for the smallest remaining element during every pass.

Time Complexity: **O(n²)**

### Complexity summary

| Case | Time Complexity |
|---|---|
| Best | O(n²) |
| Average | O(n²) |
| Worst | O(n²) |

**Important:** Unlike optimized Bubble Sort, Selection Sort cannot achieve O(n) best-case time using its standard implementation because it must still inspect every remaining element to confirm the minimum.

## 7. Space Complexity

Selection Sort modifies the original array directly.

It uses only a few additional variables:

- `i` — outer loop counter.
- `j` — inner loop counter.
- `minIndex` — index of the minimum element.
- `temp` — temporary variable for swapping.

It does not create another array.

**Auxiliary Space Complexity: O(1)**

Selection Sort is an in-place sorting algorithm.

## 8. Important Properties of Selection Sort

### A. In-place Sorting

Selection Sort sorts the original array without requiring another array proportional to the input size.

Auxiliary Space: O(1).

### B. Not Stable by Default

Standard Selection Sort is generally **not stable**.

What does stability mean?

A stable sorting algorithm preserves the original relative order of elements that have equal sorting keys.

Consider:

```text
[(A, 5), (B, 3), (C, 5)]
```

Here:

- A has a value of 5.
- B has a value of 3.
- C has a value of 5.

The original relative order of A and C is A before C.

Selection Sort finds the smallest element, B, and swaps it with A:

```text
Before:

[(A, 5), (B, 3), (C, 5)]

After first pass:

[(B, 3), (A, 5), (C, 5)]
```

This example preserves A and C, but consider a slightly different array:

```text
[(A, 4), (B, 4), (C, 3)]
```

Selection Sort swaps A and C:

```text
[(C, 3), (B, 4), (A, 4)]
```

Originally, A came before B.

After sorting, B comes before A.

The relative order of equal elements has changed.

Therefore, standard Selection Sort is not stable.

### C. Not Adaptive

Selection Sort performs approximately the same number of comparisons regardless of whether the input is already sorted or randomly arranged.

It does not automatically become faster for nearly sorted data.

### D. Comparison-Based Sorting

Selection Sort compares elements to determine their ordering.

It belongs to the comparison-based sorting family.

### E. Performs Few Swaps

This is one of Selection Sort's most useful characteristics.

For an array of `n` elements, it performs at most `n - 1` swaps.

This is fewer swaps than standard Bubble Sort, which can perform O(n²) swaps.

However, both algorithms perform O(n²) comparisons.

## 9. Bubble Sort vs Selection Sort

Both algorithms have O(n²) average and worst-case time complexity, but their approaches are different.

| Feature | Bubble Sort | Selection Sort |
|---|---|---|
| Approach | Compare adjacent elements | Find minimum element |
| Swapping | During comparisons | After each pass |
| Comparisons | O(n²) | O(n²) |
| Swaps | O(n²) worst case | O(n) maximum |
| Best Case | O(n) optimized | O(n²) |
| Auxiliary Space | O(1) | O(1) |
| Stable | Yes, standard version | No, standard version |
| Adaptive | Optimized version | No |

### Example: Why does Selection Sort perform fewer swaps?

Consider a reverse-sorted array:

```text
[5, 4, 3, 2, 1]
```

Selection Sort can sort it using only two swaps:

```text
Pass 1: [1, 4, 3, 2, 5]
Pass 2: [1, 2, 3, 4, 5]
```

The remaining passes require no swaps.

Bubble Sort needs multiple adjacent swaps to move elements across the array.

**Interview takeaway:** Selection Sort is useful when writes or swaps are expensive, but its O(n²) comparisons still make it unsuitable for large datasets.

## 10. Selection Sort with Strings

Selection Sort can also sort Strings.

For Strings, use `compareTo()` instead of the `<` operator.

### Java implementation

```java
import java.util.Arrays;

public class StringSelectionSort {

    public static void selectionSort(String[] arr) {

        int n = arr.length;

        for (int i = 0; i < n - 1; i++) {

            int minIndex = i;

            for (int j = i + 1; j < n; j++) {

                if (arr[j].compareTo(arr[minIndex]) < 0) {
                    minIndex = j;
                }
            }

            String temp = arr[i];

            arr[i] = arr[minIndex];

            arr[minIndex] = temp;
        }
    }

    public static void main(String[] args) {

        String[] names = {"Rahul", "Aman", "Sara", "Priya"};

        selectionSort(names);

        System.out.println(Arrays.toString(names));
    }
}
```

Output:

```text
[Aman, Priya, Rahul, Sara]
```

`compareTo()` returns:

- A negative value when the first String comes before the second.
- Zero when both Strings are equal.
- A positive value when the first String comes after the second.

The comparison follows Java's lexicographical ordering and is case-sensitive.

## 11. Selection Sort with Objects

In real applications, we often sort objects instead of primitive values.

For example, consider a list of students that needs to be sorted by marks.

```java
class Student {

    String name;
    int marks;

    Student(String name, int marks) {
        this.name = name;
        this.marks = marks;
    }

    @Override
    public String toString() {
        return name + " - " + marks;
    }
}
```

### Selection Sort by marks

```java
import java.util.Arrays;

public class StudentSelectionSort {

    public static void selectionSort(Student[] students) {

        int n = students.length;

        for (int i = 0; i < n - 1; i++) {

            int minIndex = i;

            for (int j = i + 1; j < n; j++) {

                if (students[j].marks < students[minIndex].marks) {
                    minIndex = j;
                }
            }

            Student temp = students[i];

            students[i] = students[minIndex];

            students[minIndex] = temp;
        }
    }

    public static void main(String[] args) {

        Student[] students = {
            new Student("Rahul", 85),
            new Student("Aman", 70),
            new Student("Sara", 92),
            new Student("Priya", 78)
        };

        selectionSort(students);

        System.out.println(Arrays.toString(students));
    }
}
```

Output:

```text
[Aman - 70, Priya - 78, Rahul - 85, Sara - 92]
```

**Important:** Standard Selection Sort is still not stable when sorting objects. Students with equal marks may change their relative order.

## 12. Can Selection Sort Be Made Stable?

Yes.

However, the normal swap operation can break stability.

One alternative is to:

1. Find the minimum element in the unsorted portion.
2. Save that element.
3. Shift all elements between its original position and the current position one place to the right.
4. Insert the minimum element at the current position.

This preserves the relative order of equal elements.

### Stable Selection Sort implementation

```java
public static void stableSelectionSort(int[] arr) {

    int n = arr.length;

    for (int i = 0; i < n - 1; i++) {

        int minIndex = i;

        // Find minimum element
        for (int j = i + 1; j < n; j++) {

            if (arr[j] < arr[minIndex]) {
                minIndex = j;
            }
        }

        // No movement required
        if (minIndex == i) {
            continue;
        }

        // Store minimum element
        int minValue = arr[minIndex];

        // Shift elements one position to the right
        while (minIndex > i) {

            arr[minIndex] = arr[minIndex - 1];

            minIndex--;
        }

        // Insert minimum at the correct position
        arr[i] = minValue;
    }
}
```

This version is stable because elements are shifted rather than exchanged across the minimum element.

However, shifting can require O(n²) element movements in the worst case.

Its time complexity remains O(n²), and auxiliary space remains O(1).

## 13. Advantages of Selection Sort

1. Very easy to understand and implement.
2. Uses constant auxiliary memory.
3. Performs at most `n - 1` swaps in the standard implementation.
4. Does not require additional arrays.
5. Performs a predictable number of comparisons.
6. Useful when writing to memory is expensive and minimizing swaps is important.

## 14. Disadvantages of Selection Sort

1. O(n²) time complexity in all cases.
2. Not suitable for large datasets.
3. Standard implementation is not stable.
4. Does not benefit significantly from already sorted input.
5. Not adaptive.
6. Generally slower than more advanced sorting algorithms.

## 15. Real-World Applications

Selection Sort is rarely used in large production applications, but its ideas are useful in several situations.

Examples:

1. **Learning sorting fundamentals:** Understanding how an algorithm identifies the smallest remaining element.
2. **Small datasets:** Sorting a very small collection where simplicity is more important than performance.
3. **Expensive write operations:** Useful in situations where minimizing the number of swaps or writes matters.
4. **Educational visualization:** Demonstrating how a sorted prefix grows after each pass.
5. **Understanding sorting properties:** Learning the difference between stable and unstable sorting.

## 16. Selection Sort vs Other Sorting Algorithms

| Algorithm | Best Case | Average Case | Worst Case | Auxiliary Space | Stable? |
|---|---|---|---|---|---|
| Bubble Sort (optimized) | O(n) | O(n²) | O(n²) | O(1) | Yes |
| Selection Sort | O(n²) | O(n²) | O(n²) | O(1) | No* |
| Insertion Sort | O(n) | O(n²) | O(n²) | O(1) | Yes |
| Merge Sort | O(n log n) | O(n log n) | O(n log n) | O(n) | Yes |
| Quick Sort | O(n log n) | O(n log n) | O(n²) | O(log n) average stack | Usually no |
| Heap Sort | O(n log n) | O(n log n) | O(n log n) | O(1) | No |

*Standard Selection Sort is unstable. Stable variants are possible.*

## 17. Common Mistakes

### Mistake 1: Swapping inside the inner loop

Incorrect:

```java
for (int j = i + 1; j < n; j++) {

    if (arr[j] < arr[minIndex]) {

        minIndex = j;

        // Incorrect: swapping here
    }
}
```

The purpose of the inner loop is to find the smallest element, not repeatedly swap elements.

Correct approach:

```java
for (int j = i + 1; j < n; j++) {

    if (arr[j] < arr[minIndex]) {
        minIndex = j;
    }
}

// Swap after the inner loop
```

### Mistake 2: Initializing minIndex incorrectly

Incorrect:

```java
int minIndex = 0;
```

This is wrong for every pass after the first because the search starts from index `i`.

Correct:

```java
int minIndex = i;
```

### Mistake 3: Searching the already sorted portion

Incorrect:

```java
for (int j = 0; j < n; j++) {
    // Search for minimum
}
```

Correct:

```java
for (int j = i + 1; j < n; j++) {
    // Search only the unsorted portion
}
```

### Mistake 4: Incorrect outer loop boundary

For the standard implementation, we only need `n - 1` passes.

Correct:

```java
for (int i = 0; i < n - 1; i++)
```

After those passes, the last element must already be in its correct position.

## 18. Interview Questions and Answers

### Q1. What is Selection Sort?

Selection Sort is a comparison-based sorting algorithm that repeatedly finds the smallest element from the unsorted portion and places it at the beginning of that portion.

### Q2. What is the time complexity of Selection Sort?

- Best Case: O(n²)
- Average Case: O(n²)
- Worst Case: O(n²)

### Q3. What is the space complexity?

O(1) auxiliary space because the sorting happens in the original array.

### Q4. Is Selection Sort stable?

No. The standard swap-based implementation is not stable because swapping a minimum element with the first unsorted element can change the relative order of equal elements.

### Q5. Is Selection Sort in-place?

Yes. It sorts the original array and requires only a constant amount of additional memory.

### Q6. How many swaps does Selection Sort perform?

At most `n - 1` swaps for an array of `n` elements in the standard implementation.

### Q7. Why is Selection Sort O(n²) even when the array is sorted?

Because it still searches the entire remaining unsorted portion to confirm that the current element is the smallest.

### Q8. What is the difference between Bubble Sort and Selection Sort?

Bubble Sort repeatedly compares adjacent elements and swaps them when necessary.

Selection Sort searches for the smallest remaining element and swaps it into the current position.

### Q9. Why is Selection Sort useful when swaps are expensive?

Because it performs at most O(n) swaps, while Bubble Sort can perform O(n²) swaps.

### Q10. Can Selection Sort sort Strings?

Yes. Use `compareTo()` to compare Strings.

### Q11. Can Selection Sort be made stable?

Yes. Instead of swapping the minimum element with the first unsorted element, save it and shift the intervening elements one position to the right.

### Q12. Is Selection Sort adaptive?

No. Its standard implementation performs O(n²) comparisons regardless of the initial ordering.

### Q13. What happens if the array contains duplicate elements?

Selection Sort can sort duplicate values correctly, but the standard implementation does not guarantee preservation of their original relative order.

### Q14. Which is better: Bubble Sort or Selection Sort?

Selection Sort generally performs fewer swaps, but both have O(n²) average and worst-case time complexity.

The better choice depends on the cost of comparisons and writes.

### Q15. Why is Selection Sort called Selection Sort?

Because it repeatedly selects the smallest remaining element and places it into its correct position.

## 19. Practice Problems

Try implementing these problems using Selection Sort.

### Beginner

1. Sort an integer array in ascending order.
2. Sort an integer array in descending order.
3. Find the minimum element in an array.
4. Find the maximum element in an array.
5. Count the total number of comparisons.
6. Count the number of swaps.
7. Print the array after every pass.

### Intermediate

8. Sort an array of Strings alphabetically.
9. Sort student objects by marks.
10. Sort employee objects by salary.
11. Find the second-smallest element using Selection Sort.
12. Find the Kth smallest element.
13. Implement stable Selection Sort.
14. Sort an array containing duplicate elements.

### Challenge

15. Implement a generic Selection Sort using `Comparator`.
16. Compare the number of swaps in Bubble Sort and Selection Sort.
17. Compare Selection Sort and Insertion Sort on sorted, reverse-sorted, and random arrays.
18. Modify Selection Sort to select both the minimum and maximum elements in each pass (double-ended Selection Sort).
19. Sort objects by multiple fields, such as marks and then name.

## 20. Quick Revision

| Concept | Answer |
|---|---|
| Algorithm type | Sorting |
| Approach | Select minimum and place it correctly |
| Best Case | O(n²) |
| Average Case | O(n²) |
| Worst Case | O(n²) |
| Auxiliary Space | O(1) |
| In-place? | Yes |
| Stable? | No, standard version |
| Adaptive? | No |
| Maximum passes | n - 1 |
| Maximum swaps | n - 1 |
| Ascending comparison | `arr[j] < arr[minIndex]` |
| Descending comparison | `arr[j] > arr[maxIndex]` |

## Final Takeaway

Selection Sort teaches an important algorithm design concept: **find the best candidate first, then put it in the correct position.**

Unlike Bubble Sort, which repeatedly swaps adjacent elements, Selection Sort searches the remaining unsorted portion and performs at most one swap per pass.

Remember these three things:

1. Divide the array into sorted and unsorted portions.
2. Find the smallest element in the unsorted portion.
3. Swap it with the first element of the unsorted portion.

Selection Sort is simple, uses O(1) auxiliary space, and performs very few swaps. However, its O(n²) comparisons make it unsuitable for large datasets.

Once you understand Selection Sort, move on to **Insertion Sort**, which introduces a different idea: taking one element at a time and inserting it into the correct position within an already sorted portion.