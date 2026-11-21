---
title: "B-Tree"
layout: default
parent: Tree
grand_parent: Data Structures & Algorithms
nav_order: 6
permalink: /data-structures/Tree/B-Tree/b-tree/

topic: B-Tree
section: Advanced Trees
concepts: [multiway-tree, balanced-tree, disk-based-indexing, node-splitting]
operations: [search, insertion, deletion]
complexities: [O(log n)]
tags: [data-structures, b-tree, database-indexing, java]
---

# B-Tree

## 1. What Is a B-Tree?

A **B-Tree** is a self-balancing, multiway search tree in which each node can contain multiple sorted keys and multiple child references.

It maintains its balance by splitting, merging, and redistributing nodes during insertion and deletion.

Unlike a Binary Search Tree:

- A Binary Search Tree node contains one key and at most two children.
- A B-Tree node can contain multiple keys and more than two children.

### Example

A Binary Search Tree might look like this:

```text
              50
             /  \
           30    70
          / \    / \
        20  40  60  80
```

A B-Tree can store multiple keys in the same node:

```text
              [30 | 60]
             /    |    \
          [10]  [40]  [70 | 80]
```

The keys inside every node are sorted.

The child pointers divide the key ranges:

- Left child: keys smaller than 30.
- Middle child: keys between 30 and 60.
- Right child: keys greater than 60.

This allows the tree to store many keys at each level and reduce its overall height.

---

## 2. Why Do We Need B-Trees?

Imagine a database containing 10 million customer records.

Suppose each database lookup requires reading a record from disk.

Disk access is significantly slower than accessing data already in memory.

If we use a Binary Search Tree, we may need to traverse many levels to find a record.

A B-Tree solves this problem by storing many keys in each node.

For example:

```text
              [20 | 40 | 60 | 80]
             /    |    |    |    \
           ...   ...  ...  ...   ...
```

Each node can guide the search across several possible ranges.

This reduces the number of tree levels and therefore the number of disk accesses required.

### Important Idea

**B-Trees reduce tree height by increasing the number of keys and children stored in each node.**

This makes them particularly suitable for disk-based storage systems.

---

## 3. Real-World Analogy

Imagine searching for a topic in a large textbook.

You do not start reading every page from the beginning.

Instead:

1. Open the index.
2. Find the relevant alphabet section.
3. Follow the page reference.
4. Open the relevant page.

A B-Tree works similarly.

Each node contains several sorted keys that help determine which child or range should be searched next.

Instead of moving through one key at a time across many levels, you can narrow down the search using several keys at each level.

---

## 4. Important B-Tree Terminology

| Term | Meaning |
|---|---|
| Node | Contains multiple keys and child references |
| Key | A value stored in the node |
| Order (m) | Maximum number of children in a node under the common order convention |
| Degree (t) | Minimum degree used in another common B-Tree definition |
| Internal Node | Node containing one or more keys and child references |
| Leaf Node | Node without children |
| Root | Topmost node |
| Split | Dividing a full node into two nodes |
| Merge | Combining nodes during deletion |
| Redistribution | Moving a key between sibling nodes |
| Separator Key | Key that separates child key ranges |

**Important:** B-Tree terminology varies between textbooks. Some define order as maximum children, while others use minimum degree. This document uses both conventions explicitly.

---

## 5. Properties of a B-Tree

A B-Tree follows these important properties.

For a B-Tree of order `m`, where order means the maximum number of children:

1. A node can contain at most `m` children.
2. A node can contain at most `m - 1` keys.
3. Every non-root internal node must have at least `ceil(m / 2)` children.
4. Every non-root node must contain at least `ceil(m / 2) - 1` keys.
5. A node with `k` keys has exactly `k + 1` children if it is an internal node.
6. Keys inside a node are stored in sorted order.
7. All leaves appear at the same depth.
8. The root can have fewer keys than other nodes.

### Example: B-Tree of Order 4

For order 4:

```text
Maximum children = 4
Maximum keys     = 3

Minimum children for non-root internal nodes = 2
Minimum keys for non-root nodes              = 1
```

A valid node might be:

```text
       [20 | 40 | 60]
```

It has three keys and can have up to four children.

### Visual Example

```text
                  [30 | 60]
                 /    |    \
                /     |     \
          [10 | 20] [40 | 50] [70 | 80]
```

All leaf nodes are at the same depth.

This is one of the most important balancing properties of a B-Tree.

---

## 6. B-Tree Order vs Minimum Degree

Two common definitions are used in B-Tree implementations.

### Definition 1: Order (m)

Order means the maximum number of children.

For order `m`:

```text
Maximum children = m
Maximum keys     = m - 1

Minimum children = ceil(m / 2)
Minimum keys     = ceil(m / 2) - 1
```

For example, an order-5 B-Tree has:

```text
Maximum children = 5
Maximum keys     = 4
Minimum children = 3
Minimum keys     = 2
```

### Definition 2: Minimum Degree (t)

Minimum degree is commonly used in algorithm textbooks and implementations.

For minimum degree `t`:

```text
Maximum children = 2t
Maximum keys     = 2t - 1

Minimum children = t
Minimum keys     = t - 1
```

For example, if `t = 2`:

```text
Maximum children = 4
Maximum keys     = 3
Minimum children = 2
Minimum keys     = 1
```

This is the commonly studied 2-3-4 B-Tree configuration.

### Comparison

| Feature | Order m | Minimum Degree t |
|---|---|---|
| Maximum children | m | 2t |
| Maximum keys | m - 1 | 2t - 1 |
| Minimum children | ceil(m/2) | t |
| Minimum keys | ceil(m/2) - 1 | t - 1 |

**Interview tip:** Always clarify which definition is being used before solving a B-Tree problem.

The Java implementation in this document uses minimum degree `t`.

---

## 7. How Searching Works in a B-Tree

Searching in a B-Tree is similar to searching in a BST, but each node contains multiple sorted keys.

Consider:

```text
                  [30 | 60]
                 /    |    \
          [10 | 20] [40 | 50] [70 | 80]
```

Search for `50`.

### Step 1: Start at the root

```text
[30 | 60]
```

Compare 50 with the keys:

```text
50 > 30
50 < 60
```

Therefore, search the middle child.

### Step 2: Search the selected child

```text
[40 | 50]
```

The key 50 is found.

### Searching Algorithm

```text
BTreeSearch(node, key):

    i = 0

    while i < numberOfKeys(node)
          and key > node.keys[i]:

        i = i + 1

    if i < numberOfKeys(node)
       and key == node.keys[i]:

        return FOUND

    if node is a leaf:

        return NOT_FOUND

    return BTreeSearch(node.children[i], key)
```

### Java Implementation

```java
public boolean search(int key) {
    return search(root, key);
}

private boolean search(Node node, int key) {

    if (node == null) {
        return false;
    }

    int i = 0;

    while (i < node.n && key > node.keys[i]) {
        i++;
    }

    if (i < node.n && key == node.keys[i]) {
        return true;
    }

    if (node.leaf) {
        return false;
    }

    return search(node.children[i], key);
}
```

---

## 8. B-Tree Insertion

Insertion is one of the most important B-Tree operations.

The basic idea is:

1. Find the correct leaf node.
2. Insert the new key in sorted order if space is available.
3. If the node is full, split it.
4. Move the middle key to the parent.
5. Continue splitting upward if necessary.
6. If the root splits, create a new root.

### Example: Insert 10, 20, 30

Assume minimum degree:

```text
t = 2
```

Maximum keys per node:

```text
2t - 1 = 3
```

#### Step 1: Insert 10

```text
[10]
```

#### Step 2: Insert 20

```text
[10 | 20]
```

#### Step 3: Insert 30

```text
[10 | 20 | 30]
```

The node is now full, but this is still a valid B-Tree.

#### Step 4: Insert 40

Before descending into a full root, split it.

The middle key moves up:

```text
          [20]
         /    \
       [10]   [30]
```

Now insert 40 into the correct child:

```text
          [20]
         /    \
       [10]   [30 | 40]
```

The tree remains balanced.

### Understanding Node Splitting

Suppose a full node contains:

```text
[10 | 20 | 30]
```

Split it:

```text
        [20]
       /    \
     [10]   [30]
```

The middle key moves into the parent.

The remaining keys become separate child nodes.

For minimum degree `t`, a full node has `2t - 1` keys. Its median key at index `t - 1` is promoted, leaving `t - 1` keys on each side.

### Root Splitting

The root is special because it has no parent.

When a full root is split:

1. Create a new root.
2. Move the old root under it.
3. Promote the median key.
4. Create two child nodes.

This increases the tree height by one.

---

## 9. B-Tree Deletion

Deletion is more complicated than searching and insertion.

When removing a key, we must ensure that every non-root node still contains at least the minimum number of keys.

There are three main situations.

### Case 1: Key Is in a Leaf Node

Consider:

```text
             [20 | 40]
            /    |    \
          [10]  [30]  [50]
```

Delete 30.

If the leaf still has the minimum required number of keys, simply remove it.

For this example with `t = 2`, however, a non-root node cannot contain zero keys after deletion. Removing 30 from `[30]` would cause underflow, so the algorithm must first borrow or merge as necessary.

### Case 2: Key Is in an Internal Node

Suppose the key to delete is in an internal node.

There are two common replacement choices:

- **Predecessor:** Largest key in the left subtree.
- **Successor:** Smallest key in the right subtree.

Example:

```text
             [20 | 40]
            /    |    \
       [10 | 15] [25 | 30] [50 | 60]
```

Delete 20.

The predecessor is 15.

Replace 20 with 15, then delete the original 15 from its leaf:

```text
             [15 | 40]
            /    |    \
          [10] [25 | 30] [50 | 60]
```

This works because the left child had enough keys to supply a predecessor.

If the left child has fewer than `t` keys, the algorithm may use the successor if the right child has at least `t` keys.

If both adjacent children have only `t - 1` keys, they are merged with the separator key, and deletion continues in the merged child.

### Case 3: Borrowing or Merging

A node can become deficient after deletion.

The tree repairs this using borrowing or merging.

**Borrowing from a sibling:**

A key moves from a sibling through the parent into the deficient node.

Example:

```text
Before:

          [20]
         /    \
       [10]  [30 | 40]
```

Suppose the left child needs another key.

A key can be moved through the parent, resulting in:

```text
After:

          [30]
         /    \
   [10 | 20]  [40]
```

**Merging:**

If neither sibling can lend a key, merge the deficient node, a parent separator key, and an adjacent sibling.

Example:

```text
Before:

          [20]
         /    \
       [10]   [30]
```

Merge:

```text
[10 | 20 | 30]
```

If the root becomes empty, the merged child becomes the new root, reducing the tree height by one.

### Deletion Summary

| Situation | Action |
|---|---|
| Key in leaf, no underflow | Remove directly |
| Key in internal node | Replace with predecessor or successor |
| Sibling has extra key | Borrow through parent |
| Neither sibling can lend | Merge with parent separator |
| Root becomes empty | Promote its only child |

---

## 10. Complete B-Tree Implementation in Java

The following implementation uses minimum degree `t`.

It supports:

- Insertion
- Searching
- Node splitting
- Inorder traversal
- Duplicate-key rejection

It is a complete insertion/search example, not a complete deletion implementation. Deletion is intentionally kept separate because its borrowing and merging cases deserve their own implementation and testing.

```java
import java.util.ArrayList;
import java.util.List;

public class BTree {

    static class Node {

        int[] keys;
        Node[] children;

        int n;
        boolean leaf;

        Node(int t, boolean leaf) {

            keys = new int[2 * t - 1];
            children = new Node[2 * t];

            this.leaf = leaf;
            this.n = 0;
        }
    }

    private final int t;
    private Node root;

    public BTree(int minimumDegree) {

        if (minimumDegree < 2) {
            throw new IllegalArgumentException(
                "Minimum degree must be at least 2"
            );
        }

        this.t = minimumDegree;
        this.root = new Node(t, true);
    }

    // Search
    public boolean search(int key) {
        return search(root, key);
    }

    private boolean search(Node node, int key) {

        int i = 0;

        while (i < node.n && key > node.keys[i]) {
            i++;
        }

        if (i < node.n && key == node.keys[i]) {
            return true;
        }

        if (node.leaf) {
            return false;
        }

        return search(node.children[i], key);
    }

    // Insert
    public void insert(int key) {

        // Ignore duplicate keys
        if (search(key)) {
            return;
        }

        Node oldRoot = root;

        // Root is full
        if (oldRoot.n == 2 * t - 1) {

            Node newRoot = new Node(t, false);

            newRoot.children[0] = oldRoot;

            root = newRoot;

            splitChild(newRoot, 0);

            insertNonFull(newRoot, key);
        }
        else {
            insertNonFull(oldRoot, key);
        }
    }

    // Insert into a node that has available space
    private void insertNonFull(Node node, int key) {

        int i = node.n - 1;

        if (node.leaf) {

            // Shift larger keys to the right
            while (i >= 0 && key < node.keys[i]) {

                node.keys[i + 1] = node.keys[i];

                i--;
            }

            node.keys[i + 1] = key;

            node.n++;
        }
        else {

            // Find correct child
            while (i >= 0 && key < node.keys[i]) {
                i--;
            }

            i++;

            // Split full child before descending
            if (node.children[i].n == 2 * t - 1) {

                splitChild(node, i);

                if (key > node.keys[i]) {
                    i++;
                }
            }

            insertNonFull(node.children[i], key);
        }
    }

    // Split a full child
    private void splitChild(Node parent, int index) {

        Node fullChild = parent.children[index];

        Node newChild = new Node(t, fullChild.leaf);

        newChild.n = t - 1;

        // Copy the largest t - 1 keys
        for (int j = 0; j < t - 1; j++) {

            newChild.keys[j] =
                fullChild.keys[j + t];
        }

        // Copy child pointers if internal node
        if (!fullChild.leaf) {

            for (int j = 0; j < t; j++) {

                newChild.children[j] =
                    fullChild.children[j + t];
            }
        }

        // Reduce original child size
        fullChild.n = t - 1;

        // Shift parent child references
        for (int j = parent.n; j >= index + 1; j--) {

            parent.children[j + 1] =
                parent.children[j];
        }

        parent.children[index + 1] = newChild;

        // Shift parent keys
        for (int j = parent.n - 1; j >= index; j--) {

            parent.keys[j + 1] =
                parent.keys[j];
        }

        // Promote middle key
        parent.keys[index] =
            fullChild.keys[t - 1];

        parent.n++;
    }

    // Inorder traversal
    public void inorder() {
        inorder(root);
        System.out.println();
    }

    private void inorder(Node node) {

        int i;

        for (i = 0; i < node.n; i++) {

            if (!node.leaf) {
                inorder(node.children[i]);
            }

            System.out.print(node.keys[i] + " ");
        }

        if (!node.leaf) {
            inorder(node.children[i]);
        }
    }

    public static void main(String[] args) {

        BTree tree = new BTree(2);

        int[] values = {
            10, 20, 5, 6, 12, 30, 7, 17
        };

        for (int value : values) {
            tree.insert(value);
        }

        System.out.println("Inorder Traversal:");

        tree.inorder();

        System.out.println(
            "Search 12: " + tree.search(12)
        );

        System.out.println(
            "Search 100: " + tree.search(100)
        );
    }
}
```

### Expected Output

```text
Inorder Traversal:
5 6 7 10 12 17 20 30

Search 12: true
Search 100: false
```

### Important Implementation Notes

- `t = 2` means a node can contain at most three keys.
- `keys` has capacity `2 * t - 1`.
- `children` has capacity `2 * t`.
- `n` stores the number of valid keys in a node.
- `leaf` identifies whether the node has children.
- `splitChild()` promotes the middle key into the parent.
- `insertNonFull()` inserts into a node that is guaranteed to have available space.
- Duplicate keys are ignored in this implementation.

---

## 11. B-Tree vs Binary Search Tree vs AVL Tree vs Red-Black Tree

| Feature | BST | AVL | Red-Black | B-Tree |
|---|---|---|---|---|
| Keys per node | 1 | 1 | 1 | Multiple |
| Maximum children | 2 | 2 | 2 | Many |
| Self-balancing | No | Yes | Yes | Yes |
| Balancing method | None | Height | Colors | Split/merge/redistribution |
| Search | Average O(log n), worst O(n) | O(log n) | O(log n) | O(log n) |
| Insertion | Average O(log n), worst O(n) | O(log n) | O(log n) | O(log n) |
| Deletion | Average O(log n), worst O(n) | O(log n) | O(log n) | O(log n) |
| Best suited for | Simple ordered data | Lookup-heavy memory structures | General ordered collections | Disk/page-oriented storage |

**Important:** These complexities assume the usual B-Tree configuration and count key comparisons at the tree level. Searching within a node can use linear or binary search, and the total cost depends on the node size and implementation.

---

## 12. B-Tree vs B+ Tree

B-Tree and B+ Tree are closely related, but they store and organize data differently.

### B-Tree

- Keys and associated records can be stored in internal nodes and leaf nodes.
- Searching may finish at an internal node.
- Leaf nodes do not necessarily need to be linked.

### B+ Tree

- Internal nodes primarily store separator keys and child pointers.
- Actual records or record pointers are stored in leaf nodes.
- Leaf nodes are commonly linked to support efficient sequential and range scans.
- A search generally continues to a leaf.

### Visual Comparison

B-Tree:

```text
             [20 | 40]
            /    |    \
          [10]  [30]  [50]
```

B+ Tree:

```text
             [20 | 40]
            /    |    \
          /      |      \
       [10]    [20|30]  [40|50]
          └───────────────→
```

This is a conceptual illustration. In a B+ Tree, separator keys may also appear in leaf nodes, and actual records are stored at the leaves.

### Comparison Table

| Feature | B-Tree | B+ Tree |
|---|---|---|
| Records | Can appear in internal and leaf nodes | Stored at leaves |
| Internal nodes | Keys and child pointers | Separator keys and child pointers |
| Search | Can finish at an internal node | Typically reaches a leaf |
| Leaf links | Not required | Commonly linked |
| Range queries | Supported | Particularly efficient |
| Sequential scanning | Less convenient | Very efficient |
| Database indexes | Used in some systems | Widely used |

**Important:** B+ Trees are especially popular for database indexes because linked leaves make range scans and sequential access efficient.

---

## 13. B-Tree Applications

B-Trees are especially useful in storage systems.

### 1. Database Indexing

Databases use B-Tree-family indexes to find records without scanning entire tables.

For example:

```sql
SELECT *
FROM employees
WHERE employee_id = 1001;
```

An index can help locate the matching record efficiently.

### 2. File Systems

B-Tree variants can organize file metadata, directory information, and storage extents.

### 3. Large Data Storage

B-Trees reduce the number of storage-page reads required to navigate large ordered datasets.

### 4. Range Queries

B-Tree-family structures support finding values within a range.

Example:

```sql
SELECT *
FROM employees
WHERE salary BETWEEN 50000 AND 100000;
```

### 5. Key-Value Storage

Some storage engines use B-Tree-family structures to maintain sorted keys and support efficient reads and updates.

---

## 14. Time and Space Complexity

Let:

- N = number of keys.
- t = minimum degree.
- h = tree height.

The height of a B-Tree is O(log N), assuming a fixed minimum degree.

| Operation | Time Complexity |
|---|---|
| Search | O(log N) |
| Insertion | O(log N) |
| Deletion | O(log N) |
| Find Minimum | O(log N) |
| Find Maximum | O(log N) |
| Inorder Traversal | O(N) |
| Split a Node | O(t) |
| Merge Nodes | O(t) |

### Space Complexity

| Operation | Space Complexity |
|---|---|
| Store N keys | O(N) |
| Search (recursive) | O(log N) stack |
| Insertion (recursive) | O(log N) stack |
| Traversal (recursive) | O(log N) stack |

For fixed `t`, each node contains a bounded number of keys and child references.

In disk-based implementations, the most important performance measure is often the number of page or block accesses rather than only CPU comparison count.

---

## 15. Common Mistakes

1. Assuming a B-Tree is a Binary Search Tree.
   - A B-Tree can have multiple keys and children per node.

2. Forgetting that keys inside a node must be sorted.

3. Forgetting that all leaves must be at the same depth.

4. Confusing order with minimum degree.
   - These are different conventions.

5. Forgetting to promote the median key during splitting.

6. Splitting the wrong child.
   - The correct child must be split before descending into it when using the top-down insertion algorithm.

7. Assuming deletion is just ordinary BST deletion.
   - B-Tree deletion requires borrowing and merging.

8. Assuming every node must be full.
   - Nodes may contain fewer than their maximum number of keys.

9. Confusing B-Tree and B+ Tree.
   - B+ Tree stores records at leaves and commonly links leaves.

10. Assuming a B-Tree has exactly the same number of children at every node.
    - Different nodes can have different numbers of keys and children, subject to the minimum and maximum constraints.

---

## 16. Frequently Asked Interview Questions

### Q1. What is a B-Tree?

A B-Tree is a self-balancing multiway search tree where each node can store multiple sorted keys and multiple child pointers.

### Q2. Why is a B-Tree useful?

It reduces tree height by storing many keys in each node, reducing disk I/O for large datasets.

### Q3. Is a B-Tree a binary tree?

No. It is a multiway search tree.

### Q4. What is the maximum number of children in a B-Tree of order m?

At most m children.

### Q5. What is the maximum number of keys?

At most m - 1 keys.

### Q6. Why are all leaves at the same level?

This property ensures the tree remains balanced.

### Q7. What happens when a B-Tree node becomes full?

It is split, and its median key is promoted to its parent.

### Q8. What happens if the root is full?

The root is split, and a new root is created. The tree height increases by one.

### Q9. What is minimum degree?

It is a parameter `t` that defines the minimum and maximum number of keys and children in the standard B-Tree definition.

### Q10. What is the difference between B-Tree and BST?

A BST has at most two children per node, while a B-Tree can have many children and multiple keys per node.

### Q11. What is the difference between B-Tree and B+ Tree?

A B-Tree can store records in internal nodes and leaves. A B+ Tree stores records at leaves, while internal nodes contain separator keys.

### Q12. Why are B-Trees preferred for databases?

They reduce the number of disk-page accesses required for searching and updating large ordered datasets.

### Q13. What is the time complexity of B-Tree search?

O(log N) in terms of tree height for fixed degree, with the cost of searching within each node depending on its size.

### Q14. What happens when a node has fewer than the minimum number of keys after deletion?

The algorithm borrows a key from a sibling or merges nodes using a parent separator key.

### Q15. Can a B-Tree have duplicate keys?

A B-Tree implementation can support duplicates, but the duplicate-handling policy must be defined. This example ignores duplicates.

### Q16. What is the difference between B-Tree and Red-Black Tree?

A Red-Black Tree is a binary search tree with one key per node and color-based balancing. A B-Tree stores multiple keys per node and balances through splitting, merging, and redistribution.

### Q17. What is a B-Tree index?

It is an ordered index structure that organizes keys into balanced nodes to support efficient lookup, insertion, deletion, and range access.

---

## 17. Practice Problems

### Beginner

1. Explain what a B-Tree is.
2. Identify the root, internal nodes, and leaf nodes.
3. Calculate maximum and minimum keys for an order-5 B-Tree.
4. Calculate maximum children for a minimum-degree-3 B-Tree.
5. Search for a key in a given B-Tree.
6. Explain why all leaves must be at the same level.

### Intermediate

1. Insert values into a B-Tree of minimum degree 2.
2. Draw the tree after every insertion.
3. Implement B-Tree searching in Java.
4. Implement B-Tree insertion in Java.
5. Implement node splitting.
6. Implement inorder traversal.
7. Explain predecessor and successor during deletion.

### Advanced

1. Implement B-Tree deletion.
2. Implement borrowing from the previous sibling.
3. Implement borrowing from the next sibling.
4. Implement node merging.
5. Build a B-Tree validator.
6. Implement a B+ Tree.
7. Compare B-Tree and B+ Tree range-query performance.
8. Design a database index using a B+ Tree.
9. Study how database page size affects B-Tree fan-out.

---

## 18. Quick Revision Cheat Sheet

| Concept | Key Point |
|---|---|
| B-Tree | Self-balancing multiway search tree |
| Keys per node | Multiple |
| Children per node | Multiple |
| Order m | Maximum m children |
| Maximum keys | m - 1 |
| Minimum degree t | Common algorithm parameter |
| Maximum keys using t | 2t - 1 |
| Minimum keys using t | t - 1 for non-root nodes |
| Leaf depth | Same for every leaf |
| Insertion | Split full nodes |
| Deletion | Borrow or merge |
| Search | O(log N) for fixed degree |
| Insertion | O(log N) for fixed degree |
| Deletion | O(log N) for fixed degree |
| Main use | Database and storage indexing |
| B+ Tree | Records at leaves, commonly linked |

### Most Important Rules to Remember

```text
B-Tree:
- Multiple keys per node.
- Multiple children per node.
- Keys inside nodes are sorted.
- All leaves are at the same depth.
- Full nodes split during insertion.
- Underfull nodes borrow or merge during deletion.
```

---

## 19. Recommended Learning Roadmap

After understanding B-Trees, continue with:

1. B+ Tree
2. B-Tree vs B+ Tree
3. Trie (Prefix Tree)
4. Segment Tree
5. Fenwick Tree (Binary Indexed Tree)
6. Disjoint Set Union (Union-Find)

The next recommended topic is **B+ Tree**, which is especially important for understanding database indexes, range queries, and how storage engines organize large datasets.