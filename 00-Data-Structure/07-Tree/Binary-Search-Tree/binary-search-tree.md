---
title: "Binary Search Tree (BST)"
layout: default
parent: Tree
grand_parent: Data Structures & Algorithms
nav_order: 2
permalink: /data-structures/Tree/Binary-Search-Tree/binary-search-tree/

topic: Binary Search Tree
section: Tree Fundamentals
concepts: [bst-property, left-subtree, right-subtree, ordered-tree]
operations: [search, insertion, deletion, minimum, maximum]
complexities: [O(log n), O(n)]
tags: [data-structures, binary-search-tree, bst, java]
---

# Binary Search Tree (BST)

## 1. What Is a Binary Search Tree?

A **Binary Search Tree (BST)** is a special type of Binary Tree that follows a specific ordering property.

For every node:

- All values in its left subtree are smaller than the node's value.
- All values in its right subtree are greater than the node's value.
- Both left and right subtrees must also follow the same BST property.

Example:

```text
                 50
               /    \
             30      70
            /  \    /  \
           20  40  60  80
```

Let's understand the structure:

- 50 is the root.
- Values smaller than 50 are on the left.
- Values greater than 50 are on the right.
- For node 30, 20 is smaller and 40 is greater.
- For node 70, 60 is smaller and 80 is greater.

This ordering rule applies to the entire subtree, not just the immediate children.

**Important:** A Binary Search Tree is a Binary Tree, but every Binary Tree is not a Binary Search Tree.

## 2. Why Do We Need a BST?

Imagine storing these numbers in an ordinary unsorted list:

```text
50, 30, 70, 20, 40, 60, 80
```

If we want to search for 60, we may need to check each element one by one.

A BST organizes the values so that we can eliminate half of the remaining search area at each step in a balanced tree.

For example, searching for 60:

1. Start at 50.
2. 60 is greater than 50, so move right.
3. Reach 70.
4. 60 is smaller than 70, so move left.
5. Find 60.

We visited only three nodes.

### Applications

| Application | Usage |
|---|---|
| Searching | Find values using ordered comparisons |
| Ordered data | Maintain values in sorted order |
| Range queries | Find values between two boundaries |
| Database concepts | Understand tree-based indexing |
| Sets and Maps | Foundation for ordered tree-based collections |
| Autocomplete and dictionaries | Related tree structures can support ordered lookup |

Java's `TreeSet` and `TreeMap` use Red-Black Trees, which are self-balancing BSTs.

## 3. BST Properties

Consider:

```text
                 50
               /    \
             30      70
            /  \    /  \
           20  40  60  80
```

### Property 1: Left Subtree

All values in the left subtree must be smaller than the current node.

For node 50:

```text
          50
         /
        30
       /  \
      20  40
```

20, 30, and 40 are all smaller than 50.

### Property 2: Right Subtree

All values in the right subtree must be greater than the current node.

For node 50:

```text
       50
         \
          70
         /  \
        60  80
```

60, 70, and 80 are all greater than 50.

### Property 3: Both Subtrees Must Be BSTs

It is not enough to check only the immediate children.

Consider:

```text
          50
         /  \
        30   70
       / \
      20  60
```

This is NOT a valid BST.

Why?

60 is the right child of 30, so it is greater than 30. However, it is inside the left subtree of 50, so it must also be smaller than 50.

Since 60 is greater than 50, the BST property is violated.

### Property 4: Inorder Traversal Produces Sorted Values

For a BST, inorder traversal visits:

```text
Left → Root → Right
```

Result:

```text
20 → 30 → 40 → 50 → 60 → 70 → 80
```

This is one of the most important properties of a BST.

For distinct keys, inorder traversal produces values in strictly ascending order.

## 4. BST Node Structure in Java

A BST node contains:

1. Data
2. Reference to the left child
3. Reference to the right child

```java
class Node {

    int data;

    Node left;
    Node right;

    Node(int data) {
        this.data = data;
    }
}
```

Example:

```java
Node root = new Node(50);

root.left = new Node(30);
root.right = new Node(70);
```

Creates:

```text
        50
       /  \
      30   70
```

The node structure is the same as a normal Binary Tree.

The difference is how we insert, search, and delete values.

## 5. Insertion in a BST

Insertion means adding a new value while maintaining the BST ordering property.

### Example

Insert the following values in this order:

```text
50, 30, 70, 20, 40, 60, 80
```

### Step 1: Insert 50

The tree is empty, so 50 becomes the root.

```text
50
```

### Step 2: Insert 30

30 is smaller than 50, so insert it on the left.

```text
    50
   /
  30
```

### Step 3: Insert 70

70 is greater than 50, so insert it on the right.

```text
     50
    /  \
   30   70
```

### Step 4: Insert 20

- 20 is smaller than 50.
- Move left to 30.
- 20 is smaller than 30.
- Insert on the left of 30.

```text
       50
      /  \
     30   70
    /
   20
```

### Step 5: Insert 40

- 40 is smaller than 50.
- Move left to 30.
- 40 is greater than 30.
- Insert on the right of 30.

```text
       50
      /  \
     30   70
    / \
   20 40
```

### Step 6: Insert 60

- 60 is greater than 50.
- Move right to 70.
- 60 is smaller than 70.
- Insert on the left of 70.

```text
       50
      /  \
     30   70
    / \   /
   20 40 60
```

### Step 7: Insert 80

- 80 is greater than 50.
- Move right to 70.
- 80 is greater than 70.
- Insert on the right of 70.

Final tree:

```text
                 50
               /    \
             30      70
            /  \    /  \
           20  40  60  80
```

### Java Code: Recursive Insertion

```java
static Node insert(Node root, int value) {

    // If the current position is empty,
    // create and return a new node.
    if (root == null) {
        return new Node(value);
    }

    if (value < root.data) {

        root.left = insert(root.left, value);

    } else if (value > root.data) {

        root.right = insert(root.right, value);

    }

    // Duplicate values are ignored in this implementation.
    return root;
}
```

Usage:

```java
Node root = null;

root = insert(root, 50);
root = insert(root, 30);
root = insert(root, 70);
root = insert(root, 20);
root = insert(root, 40);
root = insert(root, 60);
root = insert(root, 80);
```

**Why do we return `root`?**

When recursion reaches a null position, it creates a new node.

The returned node is assigned to its parent's left or right reference.

For example:

```java
root.left = insert(root.left, value);
```

This connects the newly created node to the correct position.

### Java Code: Iterative Insertion

Insertion can also be implemented without recursion.

```java
static Node insertIterative(Node root, int value) {

    Node newNode = new Node(value);

    if (root == null) {
        return newNode;
    }

    Node current = root;
    Node parent = null;

    while (current != null) {

        parent = current;

        if (value < current.data) {

            current = current.left;

        } else if (value > current.data) {

            current = current.right;

        } else {

            // Ignore duplicates.
            return root;
        }
    }

    if (value < parent.data) {
        parent.left = newNode;
    } else {
        parent.right = newNode;
    }

    return root;
}
```

## 6. Searching in a BST

Searching means finding whether a particular value exists in the tree.

Consider:

```text
                 50
               /    \
             30      70
            /  \    /  \
           20  40  60  80
```

Search for 60.

### Step-by-Step Execution

1. Start at 50.
2. 60 is greater than 50, move right.
3. Reach 70.
4. 60 is smaller than 70, move left.
5. Reach 60.
6. Target found.

### Java Code: Recursive Search

```java
static boolean search(Node root, int target) {

    if (root == null) {
        return false;
    }

    if (root.data == target) {
        return true;
    }

    if (target < root.data) {
        return search(root.left, target);
    }

    return search(root.right, target);
}
```

Usage:

```java
System.out.println(search(root, 60)); // true
System.out.println(search(root, 90)); // false
```

### Java Code: Iterative Search

```java
static boolean searchIterative(Node root, int target) {

    Node current = root;

    while (current != null) {

        if (target == current.data) {
            return true;
        }

        if (target < current.data) {
            current = current.left;
        } else {
            current = current.right;
        }
    }

    return false;
}
```

The iterative implementation does not use recursive call-stack space.

## 7. Finding Minimum and Maximum Values

In a BST:

- The minimum value is the leftmost node.
- The maximum value is the rightmost node.

Example:

```text
                 50
               /    \
             30      70
            /  \    /  \
           20  40  60  80
```

Minimum = 20

Maximum = 80

### Find Minimum

```java
static Node findMin(Node root) {

    if (root == null) {
        return null;
    }

    while (root.left != null) {
        root = root.left;
    }

    return root;
}
```

### Find Maximum

```java
static Node findMax(Node root) {

    if (root == null) {
        return null;
    }

    while (root.right != null) {
        root = root.right;
    }

    return root;
}
```

Usage:

```java
Node minNode = findMin(root);
Node maxNode = findMax(root);

if (minNode != null) {
    System.out.println("Minimum: " + minNode.data);
}

if (maxNode != null) {
    System.out.println("Maximum: " + maxNode.data);
}
```

## 8. Deletion in a BST

Deletion is one of the most important BST operations.

Deleting a node must preserve the BST ordering property.

There are three main cases.

### Case 1: Deleting a Leaf Node

A leaf node has no children.

Example: Delete 20.

Before:

```text
          50
         /  \
        30   70
       / \
      20  40
```

After:

```text
          50
         /  \
        30   70
         \
          40
```

We simply remove the reference to node 20.

### Case 2: Deleting a Node with One Child

Example: Delete 30.

Before:

```text
          50
         /  \
        30   70
          \
           40
```

After:

```text
          50
         /  \
        40   70
```

The child replaces the deleted node.

The replacement may be either the left child or the right child.

### Case 3: Deleting a Node with Two Children

This is the most interesting case.

Example: Delete 50.

Before:

```text
                 50
               /    \
             30      70
            /  \    /  \
           20  40  60  80
```

We have two common approaches:

**Approach 1: Inorder Successor**

The inorder successor is the smallest value in the right subtree.

For 50:

- Right subtree starts at 70.
- Move left to 60.
- The inorder successor is 60.

Replace 50 with 60, then delete the original 60 node.

After:

```text
                 60
               /    \
             30      70
            /  \       \
           20  40      80
```

**Approach 2: Inorder Predecessor**

The inorder predecessor is the largest value in the left subtree.

For 50:

- Left subtree starts at 30.
- Move right to 40.
- The inorder predecessor is 40.

Replace 50 with 40, then delete the original 40 node.

Both approaches preserve the BST ordering property.

### Java Code: BST Deletion

This implementation uses the inorder successor.

```java
static Node delete(Node root, int key) {

    if (root == null) {
        return null;
    }

    // Find the node to delete.
    if (key < root.data) {

        root.left = delete(root.left, key);

    } else if (key > root.data) {

        root.right = delete(root.right, key);

    } else {

        // Case 1: No child.
        if (root.left == null && root.right == null) {
            return null;
        }

        // Case 2: Only right child.
        if (root.left == null) {
            return root.right;
        }

        // Case 2: Only left child.
        if (root.right == null) {
            return root.left;
        }

        // Case 3: Two children.

        // Find inorder successor.
        Node successor = findMin(root.right);

        // Copy successor value.
        root.data = successor.data;

        // Delete original successor node.
        root.right = delete(root.right, successor.data);
    }

    return root;
}
```

Usage:

```java
root = delete(root, 50);
```

**Important:** Always assign the returned root:

```java
root = delete(root, key);
```

This is necessary because deleting the root may change which node is the root.

## 9. Inorder Traversal of a BST

Inorder traversal is particularly important for BSTs.

It produces values in ascending order when the BST follows the standard ordering rule.

```java
static void inorder(Node root) {

    if (root == null) {
        return;
    }

    inorder(root.left);

    System.out.print(root.data + " ");

    inorder(root.right);
}
```

For:

```text
                 50
               /    \
             30      70
            /  \    /  \
           20  40  60  80
```

Output:

```text
20 30 40 50 60 70 80
```

This property is useful for:

- Printing BST values in sorted order.
- Checking whether a tree is a valid BST.
- Finding the kth smallest element.
- Finding the kth largest element.
- Range queries.

## 10. Validating a Binary Search Tree

A common interview problem is checking whether a Binary Tree is a valid BST.

A common mistake is checking only whether:

```text
left child < parent < right child
```

That is not sufficient.

Every node must satisfy the constraints imposed by all its ancestors.

### Example of an Invalid BST

```text
          50
         /  \
        30   70
       / \
      20  60
```

60 is greater than 30, but it must also be smaller than 50.

Therefore, this is invalid.

### Correct Java Implementation

Use minimum and maximum allowed bounds.

```java
static boolean isValidBST(Node root) {

    return validate(root, Long.MIN_VALUE, Long.MAX_VALUE);
}

static boolean validate(Node node, long min, long max) {

    if (node == null) {
        return true;
    }

    if (node.data <= min || node.data >= max) {
        return false;
    }

    return validate(node.left, min, node.data)
            && validate(node.right, node.data, max);
}
```

Why use `long`?

Because Java `int` values can reach `Integer.MIN_VALUE` and `Integer.MAX_VALUE`. Using `long` bounds avoids overflow or incorrect boundary handling.

This implementation assumes distinct keys and the strict BST rule.

## 11. Kth Smallest and Kth Largest Element

Because inorder traversal produces sorted values, we can find the kth smallest element efficiently without sorting the entire tree.

### Kth Smallest

For example:

```text
        50
       /  \
      30   70
     / \   / \
    20 40 60 80
```

Inorder:

```text
20 30 40 50 60 70 80
```

The 3rd smallest element is 40.

### Java Code

```java
static int kthSmallest(Node root, int k) {

    java.util.Deque<Node> stack = new java.util.ArrayDeque<>();

    Node current = root;

    int count = 0;

    while (current != null || !stack.isEmpty()) {

        while (current != null) {
            stack.push(current);
            current = current.left;
        }

        current = stack.pop();

        count++;

        if (count == k) {
            return current.data;
        }

        current = current.right;
    }

    throw new IllegalArgumentException("Invalid k");
}
```

Time: O(H + k) in the general traversal path, bounded by O(N).

Space: O(H).

### Kth Largest

Use reverse inorder traversal:

```text
Right → Root → Left
```

This visits values in descending order.

## 12. Floor and Ceiling in a BST

These are useful operations in ordered data structures.

**Floor:** Largest value less than or equal to the target.

**Ceiling:** Smallest value greater than or equal to the target.

Example:

```text
BST values: 10, 20, 30, 40, 50

Target: 35

Floor:   30
Ceiling: 40
```

### Floor Implementation

```java
static Integer floor(Node root, int key) {

    Integer result = null;

    while (root != null) {

        if (root.data == key) {
            return root.data;
        }

        if (key < root.data) {

            root = root.left;

        } else {

            result = root.data;
            root = root.right;
        }
    }

    return result;
}
```

### Ceiling Implementation

```java
static Integer ceiling(Node root, int key) {

    Integer result = null;

    while (root != null) {

        if (root.data == key) {
            return root.data;
        }

        if (key > root.data) {

            root = root.right;

        } else {

            result = root.data;
            root = root.left;
        }
    }

    return result;
}
```

## 13. BST Time Complexity

The time complexity of BST operations depends on the height of the tree.

Let:
- N = Number of nodes.
- H = Height of the tree.

| Operation | Average Time (balanced/random shape) | Worst Time (skewed) |
|---|---:|---:|
| Search | O(log N) | O(N) |
| Insert | O(log N) | O(N) |
| Delete | O(log N) | O(N) |
| Find Minimum | O(log N) | O(N) |
| Find Maximum | O(log N) | O(N) |
| Inorder Traversal | O(N) | O(N) |
| Validate BST | O(N) | O(N) |

### Why Does a Skewed BST Become Slow?

Consider inserting sorted values:

```text
10, 20, 30, 40, 50
```

The resulting BST is:

```text
10
  \
   20
     \
      30
        \
         40
           \
            50
```

This behaves like a linked list.

Searching for 50 requires visiting every node.

Time complexity becomes O(N).

### Balanced BST

A balanced tree keeps the height close to logarithmic.

```text
          30
         /  \
        20   40
       /       \
      10        50
```

Searching can be performed in O(log N) time.

**Important:** An ordinary BST does not automatically rebalance itself.

AVL Trees and Red-Black Trees solve this problem by maintaining balancing rules.

## 14. Duplicate Values in a BST

The standard BST implementation in this document ignores duplicates.

For example:

```java
if (value < root.data) {
    root.left = insert(root.left, value);
} else if (value > root.data) {
    root.right = insert(root.right, value);
}
```

If the value is equal, no insertion happens.

Other valid policies include:

1. Store duplicates consistently on the left.
2. Store duplicates consistently on the right.
3. Store a frequency/count field in each node.

For example, a node can contain:

```java
class Node {

    int data;
    int count = 1;

    Node left;
    Node right;

    Node(int data) {
        this.data = data;
    }
}
```

The third approach is useful when we want one structural node per distinct value.

Whatever policy is selected must be applied consistently in insertion, searching, deletion, and validation.

## 15. BST vs Java TreeSet and TreeMap

Java provides built-in ordered collections.

### TreeSet

`TreeSet` stores unique values in sorted order.

```java
import java.util.TreeSet;

TreeSet<Integer> numbers = new TreeSet<>();

numbers.add(50);
numbers.add(30);
numbers.add(70);
numbers.add(20);
numbers.add(40);

System.out.println(numbers);
```

Output:

```text
[20, 30, 40, 50, 70]
```

### TreeMap

`TreeMap` stores key-value pairs sorted by key.

```java
import java.util.TreeMap;

TreeMap<Integer, String> students = new TreeMap<>();

students.put(3, "Charlie");
students.put(1, "Alice");
students.put(2, "Bob");

System.out.println(students);
```

Output:

```text
{1=Alice, 2=Bob, 3=Charlie}
```

Both `TreeSet` and `TreeMap` are backed by Red-Black Trees in standard OpenJDK implementations.

Their main operations generally take O(log N) time.

## 16. Complete Java BST Implementation

The following program combines:

- Node creation.
- Insertion.
- Searching.
- Minimum and maximum.
- Deletion.
- Inorder traversal.
- Height calculation.

```java
public class BinarySearchTree {

    static class Node {

        int data;
        Node left;
        Node right;

        Node(int data) {
            this.data = data;
        }
    }

    Node root;

    // Insert a value.
    public void insert(int value) {
        root = insert(root, value);
    }

    private Node insert(Node root, int value) {

        if (root == null) {
            return new Node(value);
        }

        if (value < root.data) {
            root.left = insert(root.left, value);
        } else if (value > root.data) {
            root.right = insert(root.right, value);
        }

        return root;
    }

    // Search for a value.
    public boolean search(int value) {

        Node current = root;

        while (current != null) {

            if (current.data == value) {
                return true;
            }

            if (value < current.data) {
                current = current.left;
            } else {
                current = current.right;
            }
        }

        return false;
    }

    // Find minimum value.
    public Integer findMin() {

        Node node = root;

        if (node == null) {
            return null;
        }

        while (node.left != null) {
            node = node.left;
        }

        return node.data;
    }

    // Find maximum value.
    public Integer findMax() {

        Node node = root;

        if (node == null) {
            return null;
        }

        while (node.right != null) {
            node = node.right;
        }

        return node.data;
    }

    // Delete a value.
    public void delete(int value) {
        root = delete(root, value);
    }

    private Node delete(Node root, int value) {

        if (root == null) {
            return null;
        }

        if (value < root.data) {

            root.left = delete(root.left, value);

        } else if (value > root.data) {

            root.right = delete(root.right, value);

        } else {

            if (root.left == null) {
                return root.right;
            }

            if (root.right == null) {
                return root.left;
            }

            Node successor = findMinNode(root.right);

            root.data = successor.data;

            root.right = delete(root.right, successor.data);
        }

        return root;
    }

    private Node findMinNode(Node root) {

        while (root.left != null) {
            root = root.left;
        }

        return root;
    }

    // Inorder traversal.
    public void inorder() {
        inorder(root);
        System.out.println();
    }

    private void inorder(Node root) {

        if (root == null) {
            return;
        }

        inorder(root.left);

        System.out.print(root.data + " ");

        inorder(root.right);
    }

    // Height using edge-count convention.
    public int height() {
        return height(root);
    }

    private int height(Node root) {

        if (root == null) {
            return -1;
        }

        return 1 + Math.max(
                height(root.left),
                height(root.right)
        );
    }

    public static void main(String[] args) {

        BinarySearchTree bst = new BinarySearchTree();

        bst.insert(50);
        bst.insert(30);
        bst.insert(70);
        bst.insert(20);
        bst.insert(40);
        bst.insert(60);
        bst.insert(80);

        System.out.print("Inorder: ");
        bst.inorder();

        System.out.println("Search 60: " + bst.search(60));

        System.out.println("Minimum: " + bst.findMin());

        System.out.println("Maximum: " + bst.findMax());

        System.out.println("Height: " + bst.height());

        bst.delete(50);

        System.out.print("After deleting 50: ");
        bst.inorder();
    }
}
```

Output:

```text
Inorder: 20 30 40 50 60 70 80
Search 60: true
Minimum: 20
Maximum: 80
Height: 2
After deleting 50: 20 30 40 60 70 80
```

## 17. Common Mistakes

1. Checking only immediate left and right children while validating a BST.
2. Forgetting to assign the returned root after insertion or deletion.
3. Forgetting that sorted insertion can create a skewed BST.
4. Assuming an ordinary BST is automatically balanced.
5. Using a general Binary Tree search algorithm instead of the BST ordering property.
6. Forgetting to handle deletion of a node with two children.
7. Forgetting that duplicate handling must be consistent.
8. Assuming every BST operation always takes O(log N).
9. Confusing inorder successor with inorder predecessor.
10. Forgetting that inorder traversal produces sorted output.

## 18. Interview Questions and Answers

### Q1. What is a Binary Search Tree?

A Binary Search Tree is a Binary Tree in which values in the left subtree are smaller than the node, and values in the right subtree are greater than the node, under the standard distinct-key convention.

### Q2. What is the time complexity of searching in a BST?

- Average under balanced/random shape: O(log N).
- Worst case: O(N).

### Q3. Why can a BST become skewed?

When values are inserted in sorted or nearly sorted order into an ordinary unbalanced BST.

### Q4. Which traversal gives sorted values in a BST?

Inorder traversal.

### Q5. What is the inorder successor?

The smallest node in the right subtree of a node.

### Q6. What is the inorder predecessor?

The largest node in the left subtree of a node.

### Q7. How do you delete a node with two children?

Replace its value with its inorder successor or predecessor, then delete the original successor or predecessor node.

### Q8. What is the minimum value in a BST?

The leftmost node.

### Q9. What is the maximum value in a BST?

The rightmost node.

### Q10. Can a Binary Search Tree contain duplicate values?

Yes, if an explicit duplicate-handling policy is defined. The examples here ignore duplicates.

### Q11. What is the difference between a BST and a Heap?

A BST maintains an ordering relationship between left and right subtrees.

A Heap maintains a parent-child priority relationship. A Max Heap ensures each parent is greater than or equal to its children, while a Min Heap ensures each parent is smaller than or equal to its children.

### Q12. How can we check whether a Binary Tree is a BST?

Use lower and upper bounds for every node, or verify that inorder traversal follows the required ordering rule.

### Q13. What is the difference between an ordinary BST and an AVL Tree?

An ordinary BST does not automatically maintain balance.

An AVL Tree is a self-balancing BST that maintains a height difference of at most one between the left and right subtrees of every node.

### Q14. What is the time complexity of finding the kth smallest value?

With inorder traversal, O(H + k) in the usual iterative implementation, with O(N) as the worst-case bound.

### Q15. What is a self-balancing BST?

A BST that automatically adjusts its structure after insertions and deletions to keep its height logarithmic.

Examples: AVL Tree and Red-Black Tree.

## 19. Practice Problems

### Beginner Level

1. Implement BST insertion.
2. Implement BST search.
3. Find the minimum value.
4. Find the maximum value.
5. Print inorder traversal.
6. Find the height of a BST.
7. Count total nodes.
8. Count leaf nodes.
9. Find the kth smallest element.
10. Find the kth largest element.

### Intermediate Level

1. Delete a node from a BST.
2. Validate whether a Binary Tree is a BST.
3. Find the inorder successor.
4. Find the inorder predecessor.
5. Find the lowest common ancestor in a BST.
6. Find floor and ceiling of a value.
7. Find the range of values between two numbers.
8. Convert a sorted array into a balanced BST.
9. Find the closest value to a target.
10. Find two nodes whose sum equals a target.

### Advanced Level

1. Recover a BST where two nodes are swapped.
2. Construct a BST from preorder traversal.
3. Construct a BST from postorder traversal.
4. Find the largest BST subtree in a Binary Tree.
5. Merge two BSTs.
6. Convert a BST into a sorted doubly linked list.
7. Implement a BST iterator.
8. Design a self-balancing BST.

## 20. Quick Revision Cheat Sheet

| Operation / Concept | Key Point |
|---|---|
| BST | Ordered Binary Tree |
| Left Subtree | Smaller values |
| Right Subtree | Greater values |
| Search | Compare and move left/right |
| Insert | Find the correct empty position |
| Delete Leaf | Remove directly |
| Delete One Child | Replace with child |
| Delete Two Children | Use successor or predecessor |
| Minimum | Leftmost node |
| Maximum | Rightmost node |
| Inorder | Sorted ascending output |
| Inorder Successor | Minimum in right subtree |
| Inorder Predecessor | Maximum in left subtree |
| Balanced BST Search | O(log N) |
| Skewed BST Search | O(N) |
| Ordinary BST | Does not self-balance |
| AVL / Red-Black | Self-balancing BSTs |

### Complexity Summary

| Operation | Average (balanced/random shape) | Worst |
|---|---:|---:|
| Search | O(log N) | O(N) |
| Insert | O(log N) | O(N) |
| Delete | O(log N) | O(N) |
| Find Min | O(log N) | O(N) |
| Find Max | O(log N) | O(N) |
| Traversal | O(N) | O(N) |

---

## 21. Recommended Learning Roadmap

Continue learning in this order:

1. Binary Tree Introduction
2. Binary Tree Traversals
3. Binary Search Tree
4. BST Search, Insertion, and Deletion
5. BST Interview Problems
6. AVL Tree
7. Red-Black Tree
8. Binary Heap
9. Trie
10. Segment Tree

**Recommended next topic: AVL Tree** — understand why ordinary BSTs become skewed, how rotations work, and how self-balancing keeps search, insertion, and deletion efficient.