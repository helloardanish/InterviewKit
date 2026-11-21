---
title: "AVL Tree"
layout: default
parent: Tree
grand_parent: Data Structures & Algorithms
nav_order: 4
permalink: /data-structures/Tree/AVL-Tree/avl-tree/

topic: AVL Tree
section: Self-Balancing Trees
concepts: [self-balancing-tree, balance-factor, height, rotations]
operations: [left-rotation, right-rotation, insertion, deletion]
complexities: [O(log n)]
tags: [data-structures, avl-tree, balanced-tree, java]
---

# AVL Tree

## 1. What Is an AVL Tree?

An **AVL Tree (Adelson-Velsky and Landis Tree)** is a self-balancing Binary Search Tree (BST).

It follows all the normal BST rules:

- Values smaller than a node are stored in its left subtree.
- Values greater than a node are stored in its right subtree.
- Duplicate values follow a defined policy.

However, an AVL Tree has an additional rule:

**The height difference between the left and right subtrees of every node must never be greater than 1.**

This rule keeps the tree balanced and ensures efficient operations.

### Why Do We Need AVL Trees?

Consider inserting the following values into a normal BST:

```text
10, 20, 30, 40, 50
```

A normal BST might look like this:

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

This tree has become similar to a Linked List.

Searching for an element may require checking almost every node.

- Best/Average case in a balanced BST: O(log n)
- Worst case in an ordinary BST: O(n)

An AVL Tree automatically balances itself after insertion and deletion.

The resulting tree could look like this:

```text
        20
       /  \
     10    40
          /  \
         30   50
```

Now the height is much smaller, and searching remains efficient.

---

## 2. Real-World Analogy

Imagine a school library with books arranged in a hierarchical structure.

If every new book is added only to the right side, finding a particular book may require checking many books one by one.

Instead, the librarian periodically reorganizes the shelves so that the arrangement remains balanced.

An AVL Tree does something similar automatically.

Whenever an insertion or deletion makes the tree unbalanced, it rearranges a small part of the tree using **rotations**.

The important point is that the BST ordering is preserved while the tree becomes balanced again.

---

## 3. AVL Tree Properties

An AVL Tree follows these properties:

1. It is a Binary Search Tree.
2. Every node can have at most two children.
3. The height difference between the left and right subtrees of every node must be at most 1.
4. The tree automatically balances itself after insertion and deletion.
5. Searching, insertion, and deletion take O(log n) time.

### Balance Factor

The balance factor tells us whether a node is balanced.

**Formula:**

```text
Balance Factor = Height(Left Subtree) - Height(Right Subtree)
```

For an AVL Tree, every node must have a balance factor of:

```text
-1, 0, or +1
```

| Balance Factor | Meaning |
|---|---|
| 0 | Left and right subtrees have equal height |
| +1 | Left subtree is taller by one |
| -1 | Right subtree is taller by one |
| +2 | Unbalanced: left-heavy |
| -2 | Unbalanced: right-heavy |

A balance factor outside the range [-1, +1] means the node needs rebalancing.

### Example

```text
       30
      /  \
    20    40
   /
  10
```

Using edge-based height:

- Height of node 10 = 0
- Height of node 20 = 1
- Height of node 40 = 0
- Height of node 30 = 2

Balance factors:

```text
Node 10: 0 - (-1) =  1? 
```

For clarity, an empty subtree has height -1, so a leaf has height 0.

```text
Node 10:
Left  = -1
Right = -1
BF    = -1 - (-1) = 0

Node 20:
Left  = 0
Right = -1
BF    = 0 - (-1) = +1

Node 30:
Left  = 1
Right = 0
BF    = 1 - 0 = +1
```

All nodes are balanced.

---

## 4. Height of an AVL Tree

The height of an AVL Tree grows logarithmically with the number of nodes.

This is the reason AVL Trees provide O(log n) operations.

Using the edge-based height convention:

- Empty tree height = -1
- Leaf node height = 0

For a node:

```text
Height(node) = 1 + max(
    Height(node.left),
    Height(node.right)
)
```

An AVL Tree maintains a height proportional to log(n).

This prevents the tree from becoming a long, unbalanced chain.

---

## 5. AVL Tree Rotations

Rotations are operations that rearrange nodes while preserving the BST ordering.

There are four imbalance cases:

1. LL (Left-Left)
2. RR (Right-Right)
3. LR (Left-Right)
4. RL (Right-Left)

Understanding these four cases is the most important part of learning AVL Trees.

### 5.1 LL Rotation (Left-Left Case)

This occurs when a node becomes unbalanced because a new node is inserted into the left subtree of its left child.

Example insertion:

```text
30, 20, 10
```

Before rotation:

```text
        30
       /
      20
     /
    10
```

Node 30 has balance factor +2.

**Solution: Right Rotation**

After rotation:

```text
       20
      /  \
     10   30
```

The middle node becomes the new root of this subtree.

Remember:

```text
LL Case → Right Rotation
```

### 5.2 RR Rotation (Right-Right Case)

This occurs when a node becomes unbalanced because a new node is inserted into the right subtree of its right child.

Example insertion:

```text
10, 20, 30
```

Before rotation:

```text
10
  \
   20
     \
      30
```

Node 10 has balance factor -2.

**Solution: Left Rotation**

After rotation:

```text
       20
      /  \
     10   30
```

Remember:

```text
RR Case → Left Rotation
```

### 5.3 LR Rotation (Left-Right Case)

This occurs when a node becomes unbalanced because a new node is inserted into the right subtree of its left child.

Example insertion:

```text
30, 10, 20
```

Before rotation:

```text
       30
      /
     10
       \
        20
```

This case requires two rotations.

**Step 1: Left Rotation on the left child (10)**

```text
       30
      /
     20
    /
   10
```

**Step 2: Right Rotation on the unbalanced node (30)**

```text
       20
      /  \
     10   30
```

Remember:

```text
LR Case → Left Rotation + Right Rotation
```

### 5.4 RL Rotation (Right-Left Case)

This occurs when a node becomes unbalanced because a new node is inserted into the left subtree of its right child.

Example insertion:

```text
10, 30, 20
```

Before rotation:

```text
10
  \
   30
  /
 20
```

This case also requires two rotations.

**Step 1: Right Rotation on the right child (30)**

```text
10
  \
   20
     \
      30
```

**Step 2: Left Rotation on the unbalanced node (10)**

```text
       20
      /  \
     10   30
```

Remember:

```text
RL Case → Right Rotation + Left Rotation
```

### Rotation Cheat Sheet

| Case | Imbalance | Required Rotation |
|---|---|---|
| LL | Left of left child | Right Rotation |
| RR | Right of right child | Left Rotation |
| LR | Right of left child | Left, then Right |
| RL | Left of right child | Right, then Left |

**Important:** These names describe where the imbalance occurs, not the direction of the rotation.

---

## 6. How AVL Tree Rotations Work Internally

Consider a right rotation:

Before:

```text
          y
         /
        x
       / \
      T1  T2
```

After:

```text
         x
        / \
       T1  y
          /
         T2
```

The rotation changes the parent-child relationships but preserves the BST ordering.

The values satisfy:

```text
T1 < x < T2 < y
```

Therefore, the resulting tree remains a valid BST.

### Right Rotation

```java
private Node rightRotate(Node y) {

    Node x = y.left;
    Node t2 = x.right;

    // Perform rotation
    x.right = y;
    y.left = t2;

    // Update heights
    updateHeight(y);
    updateHeight(x);

    return x;
}
```

### Left Rotation

Before:

```text
      x
       \
        y
       / \
      T2  T3
```

After:

```text
        y
       / \
      x   T3
       \
        T2
```

Java implementation:

```java
private Node leftRotate(Node x) {

    Node y = x.right;
    Node t2 = y.left;

    // Perform rotation
    y.left = x;
    x.right = t2;

    // Update heights
    updateHeight(x);
    updateHeight(y);

    return y;
}
```

Notice that heights are updated from the lower node to the new subtree root.

---

## 7. AVL Tree Node Implementation in Java

Each node stores:

- Data
- Reference to the left child
- Reference to the right child
- Height of the node

```java
class Node {

    int key;

    Node left;
    Node right;

    int height;

    Node(int key) {
        this.key = key;
        this.height = 0;
    }
}
```

For a newly created node, height is 0 because it is a leaf.

---

## 8. Complete AVL Tree Implementation in Java

The following implementation supports:

- Insertion
- Deletion
- Searching
- Inorder traversal
- Height calculation
- Balance factor calculation
- Automatic balancing through rotations

```java
public class AVLTree {

    static class Node {

        int key;

        Node left;
        Node right;

        int height;

        Node(int key) {
            this.key = key;
            this.height = 0;
        }
    }

    private Node root;

    // Get height of a node
    private int height(Node node) {
        return node == null ? -1 : node.height;
    }

    // Update height
    private void updateHeight(Node node) {

        node.height = 1 + Math.max(
            height(node.left),
            height(node.right)
        );
    }

    // Calculate balance factor
    private int getBalance(Node node) {

        if (node == null) {
            return 0;
        }

        return height(node.left) - height(node.right);
    }

    // Right Rotation
    private Node rightRotate(Node y) {

        Node x = y.left;
        Node t2 = x.right;

        x.right = y;
        y.left = t2;

        updateHeight(y);
        updateHeight(x);

        return x;
    }

    // Left Rotation
    private Node leftRotate(Node x) {

        Node y = x.right;
        Node t2 = y.left;

        y.left = x;
        x.right = t2;

        updateHeight(x);
        updateHeight(y);

        return y;
    }

    // Balance a node after insertion or deletion
    private Node balance(Node node) {

        if (node == null) {
            return null;
        }

        updateHeight(node);

        int balance = getBalance(node);

        // Left-heavy
        if (balance > 1) {

            // LR case
            if (getBalance(node.left) < 0) {
                node.left = leftRotate(node.left);
            }

            // LL case
            return rightRotate(node);
        }

        // Right-heavy
        if (balance < -1) {

            // RL case
            if (getBalance(node.right) > 0) {
                node.right = rightRotate(node.right);
            }

            // RR case
            return leftRotate(node);
        }

        return node;
    }

    // Insert a value
    public void insert(int key) {
        root = insert(root, key);
    }

    private Node insert(Node node, int key) {

        if (node == null) {
            return new Node(key);
        }

        if (key < node.key) {
            node.left = insert(node.left, key);
        }
        else if (key > node.key) {
            node.right = insert(node.right, key);
        }
        else {
            // Ignore duplicates
            return node;
        }

        return balance(node);
    }

    // Search for a value
    public boolean search(int key) {

        Node current = root;

        while (current != null) {

            if (key == current.key) {
                return true;
            }

            if (key < current.key) {
                current = current.left;
            }
            else {
                current = current.right;
            }
        }

        return false;
    }

    // Find minimum node
    private Node findMin(Node node) {

        while (node.left != null) {
            node = node.left;
        }

        return node;
    }

    // Delete a value
    public void delete(int key) {
        root = delete(root, key);
    }

    private Node delete(Node node, int key) {

        if (node == null) {
            return null;
        }

        if (key < node.key) {
            node.left = delete(node.left, key);
        }
        else if (key > node.key) {
            node.right = delete(node.right, key);
        }
        else {

            // Case 1: No child or one child
            if (node.left == null || node.right == null) {

                Node child = (node.left != null)
                    ? node.left
                    : node.right;

                node = child;
            }

            // Case 2: Two children
            else {

                Node successor = findMin(node.right);

                node.key = successor.key;

                node.right = delete(
                    node.right,
                    successor.key
                );
            }
        }

        if (node == null) {
            return null;
        }

        return balance(node);
    }

    // Inorder traversal
    public void inorder() {
        inorder(root);
        System.out.println();
    }

    private void inorder(Node node) {

        if (node == null) {
            return;
        }

        inorder(node.left);

        System.out.print(node.key + " ");

        inorder(node.right);
    }

    // Get tree height
    public int getHeight() {
        return height(root);
    }

    public static void main(String[] args) {

        AVLTree tree = new AVLTree();

        tree.insert(10);
        tree.insert(20);
        tree.insert(30);
        tree.insert(40);
        tree.insert(50);
        tree.insert(25);

        System.out.println("Inorder Traversal:");

        tree.inorder();

        System.out.println(
            "Height: " + tree.getHeight()
        );

        System.out.println(
            "Search 30: " + tree.search(30)
        );

        tree.delete(30);

        System.out.println(
            "After deleting 30:"
        );

        tree.inorder();
    }
}
```

### Expected Output

```text
Inorder Traversal:
10 20 25 30 40 50

Height: 2

Search 30: true

After deleting 30:
10 20 25 40 50
```

The exact shape of the tree is maintained through rotations. Inorder traversal remains sorted because the AVL Tree is still a BST.

---

## 9. AVL Tree Insertion Explained Step by Step

Let's insert:

```text
10, 20, 30, 40, 50, 25
```

### Step 1: Insert 10

```text
10
```

The tree is balanced.

### Step 2: Insert 20

```text
10
  \
   20
```

Balance factor of 10 = -1.

The tree is still balanced.

### Step 3: Insert 30

```text
10
  \
   20
     \
      30
```

Balance factor of 10 = -2.

This is an RR case.

Perform a left rotation:

```text
       20
      /  \
     10   30
```

### Step 4: Insert 40

```text
       20
      /  \
     10   30
            \
             40
```

The tree is still balanced.

### Step 5: Insert 50

Before rebalancing:

```text
       20
      /  \
     10   30
            \
             40
               \
                50
```

Node 30 becomes unbalanced with balance factor -2.

This is an RR case.

Perform a left rotation at node 30:

```text
       20
      /  \
     10   40
         /  \
        30   50
```

### Step 6: Insert 25

25 is inserted into the left subtree of 30:

```text
       20
      /  \
     10   40
         /  \
        30   50
       /
      25
```

Node 20 becomes unbalanced.

Its left subtree is taller, and the insertion occurred in the right subtree of its left child.

This is an LR case.

Perform:

1. Left rotation at node 10's parent? No: rotate the left child of 20, which is node 10, to the left.
2. Right rotation at node 20.

In this particular tree, node 10 has no right child, so a left rotation at node 10 would not be valid. The actual imbalance is at node 20's right subtree: its left subtree has height 2 and its right subtree has height 2 after inserting 25, so node 20 remains balanced.

The final tree is:

```text
       20
      /  \
     10   40
         /  \
        30   50
       /
      25
```

This example demonstrates why balance factors must be calculated rather than guessing a rotation from the insertion path.

---

## 10. AVL Tree Deletion

Deletion is similar to deletion in a normal BST, but the tree must be rebalanced afterward.

There are three BST deletion cases:

### Case 1: Delete a Leaf Node

Before:

```text
       20
      /  \
     10   30
```

Delete 10:

```text
       20
         \
          30
```

Recalculate heights and balance factors.

### Case 2: Delete a Node With One Child

Before:

```text
       20
      /
     10
       \
        15
```

Delete 10:

```text
       20
      /
     15
```

The child takes the deleted node's place.

### Case 3: Delete a Node With Two Children

Before:

```text
       20
      /  \
     10   30
         /  \
        25   40
```

Delete 30.

Find the inorder successor, which is the smallest value in the right subtree.

```text
Inorder Successor = 40
```

Replace 30 with 40 and remove the original successor node.

After deletion:

```text
       20
      /  \
     10   40
         /
        25
```

After any deletion, update heights and rebalance every affected ancestor on the way back to the root.

**Important:** AVL deletion may require rebalancing at multiple ancestors. Unlike insertion, where one rebalancing operation is enough to restore the affected subtree's original height, deletion can continue to reduce subtree heights.

---

## 11. AVL Tree vs Binary Search Tree

| Feature | Normal BST | AVL Tree |
|---|---|---|
| Structure | Binary Search Tree | Self-balancing BST |
| Balance | Not guaranteed | Strictly balanced |
| Search | Average O(log n), worst O(n) | O(log n) worst case |
| Insertion | Average O(log n), worst O(n) | O(log n) worst case |
| Deletion | Average O(log n), worst O(n) | O(log n) worst case |
| Rotations | Not required | Required |
| Implementation | Simpler | More complex |
| Height | Can become O(n) | O(log n) |
| Suitable for | Simple ordered data | Search-intensive applications |

---

## 12. AVL Tree vs Red-Black Tree

Both AVL Trees and Red-Black Trees are self-balancing BSTs.

However, they use different balancing rules.

| Feature | AVL Tree | Red-Black Tree |
|---|---|---|
| Balancing | Height balance factor | Node coloring rules |
| Strictness | More strictly balanced | Less strictly balanced |
| Search | O(log n) | O(log n) |
| Insertion | O(log n) | O(log n) |
| Deletion | O(log n) | O(log n) |
| Rotations | Up to 2 per rebalancing point | Up to 2 per insertion; deletion has different fix-up cases |
| Search performance | Often better due to smaller height | May have slightly greater height |
| Update performance | Can require more rebalancing | Often fewer rotations |
| Complexity | More involved | More involved |

### When to Use AVL Trees?

AVL Trees are useful when:

- Searching is performed frequently.
- Insertions and deletions are less frequent.
- Strict balancing is important.
- Predictable worst-case search performance is required.

### When to Use Red-Black Trees?

Red-Black Trees are useful when:

- Frequent insertions and deletions are expected.
- A good balance between searching and updating is needed.
- A general-purpose ordered map or set is required.

---

## 13. Time and Space Complexity

Let N be the number of nodes.

| Operation | Time Complexity |
|---|---|
| Search | O(log n) |
| Insert | O(log n) |
| Delete | O(log n) |
| Find Minimum | O(log n) |
| Find Maximum | O(log n) |
| Inorder Traversal | O(n) |
| Calculate Height | O(1) when stored in nodes |
| Single Rotation | O(1) |

### Space Complexity

| Operation | Space Complexity |
|---|---|
| Node storage | O(n) |
| Recursive Search | O(log n) stack |
| Recursive Insertion | O(log n) stack |
| Recursive Deletion | O(log n) stack |
| Iterative Search | O(1) |
| Rotation | O(1) |

The height of an AVL Tree is O(log n), so recursive operations use O(log n) call-stack space.

---

## 14. AVL Tree Applications

AVL Trees are useful in applications that require fast lookup and ordered data.

Examples include:

1. **Database indexing:** Maintaining balanced ordered structures for efficient searching.
2. **In-memory dictionaries:** Storing key-value data in sorted order.
3. **Searching systems:** Fast lookup where updates are relatively less frequent.
4. **Range queries:** Finding values within a given range.
5. **Ordered collections:** Maintaining data in sorted order while supporting updates.

In real database systems, B-Trees and B+ Trees are generally more suitable for disk-based indexes because they reduce disk I/O. AVL Trees are more commonly useful for in-memory ordered structures.

---

## 15. Common Mistakes

1. **Confusing rotation directions**
   - LL → Right Rotation
   - RR → Left Rotation
   - LR → Left Rotation, then Right Rotation
   - RL → Right Rotation, then Left Rotation

2. **Forgetting to update heights**

   Always update the height of the lower node before updating the new subtree root after a rotation.

3. **Calculating balance factors incorrectly**

   Use:

   ```text
   BF = Height(Left) - Height(Right)
   ```

4. **Checking only the root**

   Every node must satisfy the AVL balance condition.

5. **Forgetting to update the root after rotation**

   Always return the new subtree root and assign it to the parent or overall root.

6. **Ignoring deletion rebalancing**

   Deletion can cause imbalance at multiple ancestors.

7. **Assuming every insertion requires rotation**

   Rotations are needed only when a node's balance factor becomes less than -1 or greater than +1.

8. **Confusing AVL with a complete binary tree**

   AVL Trees are height-balanced, but they do not have to be complete or perfect.

---

## 16. Java Built-in Alternatives

Java provides built-in ordered collection implementations.

### TreeMap

```java
import java.util.TreeMap;

public class Main {

    public static void main(String[] args) {

        TreeMap<Integer, String> map = new TreeMap<>();

        map.put(30, "C");
        map.put(10, "A");
        map.put(20, "B");

        System.out.println(map);
    }
}
```

Output:

```text
{10=A, 20=B, 30=C}
```

### TreeSet

```java
import java.util.TreeSet;

public class Main {

    public static void main(String[] args) {

        TreeSet<Integer> set = new TreeSet<>();

        set.add(30);
        set.add(10);
        set.add(20);

        System.out.println(set);
    }
}
```

Output:

```text
[10, 20, 30]
```

**Important:** Java's `TreeMap` and `TreeSet` are backed by Red-Black Trees in the standard OpenJDK implementation, not AVL Trees.

They provide O(log n) basic operations.

---

## 17. Frequently Asked Interview Questions

### Q1. What is an AVL Tree?

An AVL Tree is a self-balancing Binary Search Tree in which the balance factor of every node must be -1, 0, or +1.

### Q2. Why is it called AVL?

It is named after its inventors, Georgy Adelson-Velsky and Evgenii Landis.

### Q3. What is a balance factor?

The balance factor is the difference between the heights of the left and right subtrees.

```text
BF = Height(Left) - Height(Right)
```

### Q4. What are the four AVL rotation cases?

- LL: Right Rotation
- RR: Left Rotation
- LR: Left followed by Right Rotation
- RL: Right followed by Left Rotation

### Q5. What is the time complexity of searching in an AVL Tree?

O(log n), even in the worst case.

### Q6. Can an AVL Tree become unbalanced?

A temporary imbalance can occur after insertion or deletion, but the tree is rebalanced before the operation finishes.

### Q7. Is an AVL Tree a complete binary tree?

No. It is height-balanced, but it does not have to be complete.

### Q8. What is the maximum balance factor allowed?

The absolute balance factor must be at most 1.

### Q9. How many rotations can insertion require?

A single or double rotation at the first unbalanced ancestor is sufficient to restore AVL balance after insertion.

### Q10. Can deletion require multiple rotations?

Yes. Deletion can cause height reductions that propagate toward the root, requiring rebalancing at multiple ancestors.

### Q11. Which is better: AVL or Red-Black Tree?

Neither is universally better.

AVL Trees are more strictly balanced and can provide faster lookups in search-heavy workloads. Red-Black Trees often provide a useful balance between lookup and update performance.

### Q12. Does Java TreeMap use AVL Tree?

No. The standard OpenJDK implementation uses a Red-Black Tree.

### Q13. What is the height complexity of an AVL Tree?

O(log n).

### Q14. Does rotation change the inorder traversal?

No. Rotations preserve the BST ordering, so inorder traversal remains unchanged.

### Q15. Why do we update heights after rotations?

Because the parent-child relationships have changed. Correct heights are necessary to calculate balance factors for subsequent operations.

---

## 18. Practice Problems

### Beginner

1. Create an AVL Tree node class in Java.
2. Implement a height calculation method.
3. Implement a balance factor calculation method.
4. Implement right rotation.
5. Implement left rotation.
6. Insert three values that produce an LL imbalance.
7. Insert three values that produce an RR imbalance.

### Intermediate

1. Implement all four rotation cases.
2. Implement AVL insertion.
3. Print the tree level by level.
4. Implement AVL search.
5. Implement AVL deletion.
6. Verify that every node satisfies the AVL balance condition.
7. Compare the height of a normal BST and an AVL Tree after inserting sorted values from 1 to 100.

### Advanced

1. Count the number of rotations during insertion.
2. Implement an AVL Tree that supports duplicate values using a count field.
3. Implement floor and ceiling operations.
4. Implement kth smallest element.
5. Compare AVL Tree and Red-Black Tree performance for different insertion/search ratios.
6. Build an AVL Tree validator that checks both BST ordering and balance conditions.
7. Implement an AVL Tree using iterative operations.

---

## 19. Quick Revision Cheat Sheet

| Concept | Key Point |
|---|---|
| AVL | Self-balancing BST |
| Inventors | Adelson-Velsky and Landis |
| Balance Factor | Height(left) - Height(right) |
| Valid Balance Factor | -1, 0, +1 |
| LL | Right Rotation |
| RR | Left Rotation |
| LR | Left + Right Rotation |
| RL | Right + Left Rotation |
| Search | O(log n) |
| Insert | O(log n) |
| Delete | O(log n) |
| Traversal | O(n) |
| Rotation | O(1) |
| Height | O(log n) |
| Java TreeMap | Red-Black Tree in OpenJDK |

### Most Important Rule to Remember

```text
LL → Right Rotation
RR → Left Rotation
LR → Left + Right Rotation
RL → Right + Left Rotation
```

---

## 20. Recommended Learning Roadmap

After understanding AVL Trees, follow this sequence:

1. Red-Black Tree
2. AVL Tree vs Red-Black Tree
3. B-Tree
4. B+ Tree
5. Trie
6. Segment Tree
7. Fenwick Tree (Binary Indexed Tree)

The next recommended topic is **Red-Black Tree**, which will help you understand how Java's TreeMap and TreeSet maintain balanced ordered data.