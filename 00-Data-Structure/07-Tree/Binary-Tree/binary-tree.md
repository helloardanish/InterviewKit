---
title: "Binary Tree"
layout: default
parent: Tree
grand_parent: Data Structures & Algorithms
nav_order: 1
permalink: /data-structures/Tree/Binary-Tree/binary-tree/

topic: Binary Tree
section: Tree Fundamentals
concepts: [root, leaf, height, depth, full-binary-tree, complete-binary-tree, perfect-binary-tree]
operations: [insertion, deletion, traversal]
complexities: [O(log n), O(n)]
tags: [data-structures, binary-tree, tree, java]
---

# Binary Tree

## 1. What Is a Binary Tree?

A **Binary Tree** is a non-linear hierarchical data structure in which each node can have at most two children.

These children are called:

1. Left Child
2. Right Child

A node can have:
- No children.
- Only a left child.
- Only a right child.
- Both left and right children.

Example:

```text
             10
            /  \
           20   30
          / \     \
         40  50    60
```

In this example:

- 10 is the root node.
- 20 is the left child of 10.
- 30 is the right child of 10.
- 40 and 50 are children of 20.
- 60 is the right child of 30.
- 40, 50, and 60 are leaf nodes.

**Important:** A Binary Tree does not require values to be sorted. It only restricts each node to at most two children.

## 2. Why Do We Need Binary Trees?

Binary Trees help organize data hierarchically and solve problems efficiently.

Common applications include:

| Application | Usage |
|---|---|
| Binary Search Tree | Efficient searching and sorting |
| Binary Heap | Priority Queue implementation |
| Expression Tree | Represent mathematical expressions |
| Decision Tree | Decision-making systems |
| Huffman Tree | Data compression |
| Syntax Tree | Compiler design |
| Game Trees | Chess and other game algorithms |

## 3. Structure of a Binary Tree Node

Every node generally contains three components:

```text
       +----------------------+
       |                      |
       v                      v
   +-------+             +-------+
   |  LEFT |             | RIGHT |
   +-------+             +-------+
          \               /
           +-------------+
           |    DATA     |
           +-------------+
```

A Java Binary Tree node contains:

1. Data: The value stored in the node.
2. Left: Reference to the left child.
3. Right: Reference to the right child.

### Java Node Class

```java
class Node {

    int data;

    Node left;
    Node right;

    Node(int data) {
        this.data = data;
        this.left = null;
        this.right = null;
    }
}
```

For example:

```java
Node root = new Node(10);

root.left = new Node(20);
root.right = new Node(30);
```

This creates:

```text
        10
       /  \
      20   30
```

Here:
- `root` points to node 10.
- `root.left` points to node 20.
- `root.right` points to node 30.

## 4. Types of Binary Trees

Understanding the different types of Binary Trees is important for interviews.

### 4.1 Full Binary Tree

A Full Binary Tree is a binary tree in which every node has either:

- Zero children, or
- Exactly two children.

Example:

```text
           A
          / \
         B   C
        / \
       D   E
```

This is a Full Binary Tree.

The following is not full:

```text
           A
          /
         B
        / \
       C   D
```

A has exactly one child, so it violates the full binary tree property.

### 4.2 Complete Binary Tree

A Complete Binary Tree is a binary tree in which:

- Every level is completely filled except possibly the last level.
- The last level is filled from left to right without gaps.

Example:

```text
           A
          / \
         B   C
        / \  /
       D  E F
```

This is complete.

The following is not complete:

```text
           A
          / \
         B   C
          \   \
           D   E
```

There are gaps before occupied positions in the last level.

**Real-world application:** Binary Heaps use Complete Binary Trees.

### 4.3 Perfect Binary Tree

A Perfect Binary Tree has:

- Every internal node with exactly two children.
- All leaf nodes at the same level.

Example:

```text
           A
          / \
         B   C
        / \ / \
       D  E F  G
```

For a perfect binary tree of height H, where root height/depth starts at 0:

\[
N = 2^{H+1} - 1
\]

For example, if H = 2:

\[
N = 2^3 - 1 = 7
\]

### 4.4 Balanced Binary Tree

A balanced binary tree keeps its height relatively small compared with its number of nodes.

For example:

```text
             50
           /    \
         30      70
        / \      / \
       20 40    60 80
```

This structure allows operations to be performed efficiently when used as a search tree.

A common formal definition used by AVL trees is:

\[
|Height(Left) - Height(Right)| \leq 1
\]

This condition must hold at every node.

Examples of self-balancing search trees:

- AVL Tree
- Red-Black Tree

### 4.5 Skewed Binary Tree

A Skewed Binary Tree is a tree in which every node has at most one child.

**Left-Skewed Tree**

```text
        A
       /
      B
     /
    C
   /
  D
```

**Right-Skewed Tree**

```text
    A
     \
      B
       \
        C
         \
          D
```

A skewed tree behaves similarly to a linked list.

Its height can become O(N), where N is the number of nodes.

## 5. Important Binary Tree Properties

Assuming the root is at level 0 and height is measured in edges:

### Property 1: Maximum Nodes at a Level

At level L, the maximum number of nodes is:

\[
2^L
\]

Example:

| Level | Maximum Nodes |
|---|---:|
| 0 | 1 |
| 1 | 2 |
| 2 | 4 |
| 3 | 8 |
| 4 | 16 |

### Property 2: Maximum Nodes in a Binary Tree

For a binary tree of height H:

\[
N_{\max} = 2^{H+1} - 1
\]

Example: Height = 3.

\[
N_{\max} = 2^4 - 1 = 15
\]

### Property 3: Minimum Height

For a binary tree containing N nodes, the minimum possible height is:

\[
H_{\min} = \lceil \log_2(N+1) \rceil - 1
\]

### Property 4: Number of Edges

A non-empty tree containing N nodes has:

\[
E = N - 1
\]

### Property 5: Leaf and Internal Nodes in a Full Binary Tree

For a non-empty full binary tree:

\[
L = I + 1
\]

Where:
- L = Number of leaf nodes.
- I = Number of internal nodes.

### Property 6: Maximum and Minimum Height

For N nodes:

- Minimum possible height: \(\lceil \log_2(N+1) \rceil - 1\)
- Maximum possible height: \(N - 1\)

The maximum occurs in a completely skewed tree.

## 6. Creating a Binary Tree in Java

Let's create the following tree:

```text
             10
            /  \
           20   30
          / \     \
         40  50    60
```

### Complete Java Program

```java
public class BinaryTree {

    static class Node {

        int data;
        Node left;
        Node right;

        Node(int data) {
            this.data = data;
        }
    }

    public static void main(String[] args) {

        // Create root node
        Node root = new Node(10);

        // Create second level
        root.left = new Node(20);
        root.right = new Node(30);

        // Create third level
        root.left.left = new Node(40);
        root.left.right = new Node(50);

        root.right.right = new Node(60);

        System.out.println("Root: " + root.data);
        System.out.println("Left: " + root.left.data);
        System.out.println("Right: " + root.right.data);
    }
}
```

Output:

```text
Root: 10
Left: 20
Right: 30
```

Notice that we manually created the tree.

Unlike a BST, a normal Binary Tree does not have an automatic insertion rule based on values.

## 7. Binary Tree Traversal

**Tree Traversal** means visiting every node of a tree in a particular order.

There are four important traversal techniques:

| Traversal | Category | Order |
|---|---|---|
| Inorder | DFS | Left → Root → Right |
| Preorder | DFS | Root → Left → Right |
| Postorder | DFS | Left → Right → Root |
| Level Order | BFS | Level by Level |

Consider this tree:

```text
             A
            / \
           B   C
          / \   \
         D   E   F
```

### 7.1 Inorder Traversal

<badge color="info">Left → Root → Right</badge>

Steps:

1. Traverse the left subtree.
2. Visit the current node.
3. Traverse the right subtree.

Result:

```text
D → B → E → A → C → F
```

**Java Code**

```java
public static void inorder(Node root) {

    if (root == null) {
        return;
    }

    inorder(root.left);

    System.out.print(root.data + " ");

    inorder(root.right);
}
```

**Execution Explanation**

For node A:

1. Go to B.
2. Go to D.
3. D has no left child, so print D.
4. Return to B and print B.
5. Visit E and print E.
6. Return to A and print A.
7. Visit C.
8. Print C.
9. Visit F and print F.

Output:

```text
D B E A C F
```

**Important:** Inorder traversal of a BST produces sorted values when using the standard BST ordering rule.

### 7.2 Preorder Traversal

<badge color="info">Root → Left → Right</badge>

Steps:

1. Visit the current node.
2. Traverse the left subtree.
3. Traverse the right subtree.

Result:

```text
A → B → D → E → C → F
```

**Java Code**

```java
public static void preorder(Node root) {

    if (root == null) {
        return;
    }

    System.out.print(root.data + " ");

    preorder(root.left);

    preorder(root.right);
}
```

Applications:

- Copying a tree.
- Serializing a tree when null markers are included.
- Prefix expression generation.

### 7.3 Postorder Traversal

<badge color="info">Left → Right → Root</badge>

Steps:

1. Traverse the left subtree.
2. Traverse the right subtree.
3. Visit the current node.

Result:

```text
D → E → B → F → C → A
```

**Java Code**

```java
public static void postorder(Node root) {

    if (root == null) {
        return;
    }

    postorder(root.left);

    postorder(root.right);

    System.out.print(root.data + " ");
}
```

Applications:

- Deleting a tree.
- Evaluating expression trees.
- Calculating subtree information.

### 7.4 Level Order Traversal

<badge color="info">Level by Level</badge>

Level Order Traversal visits nodes from top to bottom and left to right.

Result:

```text
A → B → C → D → E → F
```

It uses a Queue because a Queue follows FIFO (First In, First Out).

**Java Code**

```java
import java.util.ArrayDeque;
import java.util.Queue;

public static void levelOrder(Node root) {

    if (root == null) {
        return;
    }

    Queue<Node> queue = new ArrayDeque<>();

    queue.offer(root);

    while (!queue.isEmpty()) {

        Node current = queue.poll();

        System.out.print(current.data + " ");

        if (current.left != null) {
            queue.offer(current.left);
        }

        if (current.right != null) {
            queue.offer(current.right);
        }
    }
}
```

### Step-by-Step Execution

Initial tree:

```text
             A
            / \
           B   C
          / \   \
         D   E   F
```

| Step | Processed Node | Queue After Processing |
|---|---|---|
| 1 | A | B, C |
| 2 | B | C, D, E |
| 3 | C | D, E, F |
| 4 | D | E, F |
| 5 | E | F |
| 6 | F | Empty |

Output:

```text
A B C D E F
```

## 8. Complete Java Program for All Traversals

```java
import java.util.ArrayDeque;
import java.util.Queue;

public class BinaryTreeTraversal {

    static class Node {

        int data;
        Node left;
        Node right;

        Node(int data) {
            this.data = data;
        }
    }

    static void inorder(Node root) {

        if (root == null) {
            return;
        }

        inorder(root.left);

        System.out.print(root.data + " ");

        inorder(root.right);
    }

    static void preorder(Node root) {

        if (root == null) {
            return;
        }

        System.out.print(root.data + " ");

        preorder(root.left);

        preorder(root.right);
    }

    static void postorder(Node root) {

        if (root == null) {
            return;
        }

        postorder(root.left);

        postorder(root.right);

        System.out.print(root.data + " ");
    }

    static void levelOrder(Node root) {

        if (root == null) {
            return;
        }

        Queue<Node> queue = new ArrayDeque<>();

        queue.offer(root);

        while (!queue.isEmpty()) {

            Node current = queue.poll();

            System.out.print(current.data + " ");

            if (current.left != null) {
                queue.offer(current.left);
            }

            if (current.right != null) {
                queue.offer(current.right);
            }
        }
    }

    public static void main(String[] args) {

        Node root = new Node(10);

        root.left = new Node(20);
        root.right = new Node(30);

        root.left.left = new Node(40);
        root.left.right = new Node(50);

        root.right.right = new Node(60);

        System.out.print("Inorder: ");
        inorder(root);

        System.out.print("\nPreorder: ");
        preorder(root);

        System.out.print("\nPostorder: ");
        postorder(root);

        System.out.print("\nLevel Order: ");
        levelOrder(root);
    }
}
```

Output:

```text
Inorder: 40 20 50 10 30 60
Preorder: 10 20 40 50 30 60
Postorder: 40 50 20 60 30 10
Level Order: 10 20 30 40 50 60
```

## 9. Iterative Tree Traversal

So far, we used recursion for DFS traversals.

We can also implement tree traversals iteratively using a Stack.

This is useful when:
- You want to avoid recursive method calls.
- The tree is very deep.
- You want more control over traversal state.

### 9.1 Iterative Inorder Traversal

```java
import java.util.ArrayDeque;
import java.util.Deque;

public static void inorderIterative(Node root) {

    Deque<Node> stack = new ArrayDeque<>();

    Node current = root;

    while (current != null || !stack.isEmpty()) {

        while (current != null) {

            stack.push(current);

            current = current.left;
        }

        current = stack.pop();

        System.out.print(current.data + " ");

        current = current.right;
    }
}
```

### 9.2 Iterative Preorder Traversal

```java
import java.util.ArrayDeque;
import java.util.Deque;

public static void preorderIterative(Node root) {

    if (root == null) {
        return;
    }

    Deque<Node> stack = new ArrayDeque<>();

    stack.push(root);

    while (!stack.isEmpty()) {

        Node current = stack.pop();

        System.out.print(current.data + " ");

        if (current.right != null) {
            stack.push(current.right);
        }

        if (current.left != null) {
            stack.push(current.left);
        }
    }
}
```

Why push right before left?

Because Stack follows LIFO (Last In, First Out).

The left child must be processed first, so we push it last.

### 9.3 Iterative Postorder Traversal

One simple approach uses two stacks.

```java
import java.util.ArrayDeque;
import java.util.Deque;

public static void postorderIterative(Node root) {

    if (root == null) {
        return;
    }

    Deque<Node> first = new ArrayDeque<>();
    Deque<Node> second = new ArrayDeque<>();

    first.push(root);

    while (!first.isEmpty()) {

        Node current = first.pop();

        second.push(current);

        if (current.left != null) {
            first.push(current.left);
        }

        if (current.right != null) {
            first.push(current.right);
        }
    }

    while (!second.isEmpty()) {
        System.out.print(second.pop().data + " ");
    }
}
```

## 10. Tree Traversal Complexity

Let:
- N = Number of nodes.
- H = Height of the tree.
- W = Maximum width of the tree.

| Traversal | Time Complexity | Auxiliary Space |
|---|---:|---:|
| Recursive Inorder | O(N) | O(H) |
| Recursive Preorder | O(N) | O(H) |
| Recursive Postorder | O(N) | O(H) |
| Iterative Inorder | O(N) | O(H) |
| Iterative Preorder | O(N) | O(N) worst case |
| Iterative Postorder (two stacks) | O(N) | O(N) |
| Level Order | O(N) | O(W) |

A skewed tree can have H = N, so recursive traversal can use O(N) call-stack space.

## 11. Common Binary Tree Operations

### 11.1 Count Total Nodes

```java
static int countNodes(Node root) {

    if (root == null) {
        return 0;
    }

    return 1
            + countNodes(root.left)
            + countNodes(root.right);
}
```

Time: O(N)

Space: O(H) recursion stack.

### 11.2 Count Leaf Nodes

A leaf node has no left or right child.

```java
static int countLeafNodes(Node root) {

    if (root == null) {
        return 0;
    }

    if (root.left == null && root.right == null) {
        return 1;
    }

    return countLeafNodes(root.left)
            + countLeafNodes(root.right);
}
```

Time: O(N)

Space: O(H)

### 11.3 Find Maximum Value

```java
static int findMaximum(Node root) {

    if (root == null) {
        throw new IllegalArgumentException("Tree is empty");
    }

    int max = root.data;

    if (root.left != null) {
        max = Math.max(max, findMaximum(root.left));
    }

    if (root.right != null) {
        max = Math.max(max, findMaximum(root.right));
    }

    return max;
}
```

Time: O(N)

Space: O(H)

### 11.4 Calculate Height of a Binary Tree

Using the convention that an empty tree has height -1 and a leaf has height 0:

```java
static int height(Node root) {

    if (root == null) {
        return -1;
    }

    int leftHeight = height(root.left);

    int rightHeight = height(root.right);

    return 1 + Math.max(leftHeight, rightHeight);
}
```

Example:

```text
          A
         / \
        B   C
       /
      D
```

- Height(D) = 0
- Height(B) = 1
- Height(C) = 0
- Height(A) = 2

Time: O(N)

Space: O(H)

**Note:** Some problems define an empty tree's height as 0 and a leaf's height as 1. Both conventions are valid if used consistently.

### 11.5 Search for a Value

A normal Binary Tree has no ordering rule, so we may need to inspect every node.

```java
static boolean search(Node root, int target) {

    if (root == null) {
        return false;
    }

    if (root.data == target) {
        return true;
    }

    return search(root.left, target)
            || search(root.right, target);
}
```

Time:
- Best case: O(1)
- Worst case: O(N)

Space: O(H)

## 12. Binary Tree vs Binary Search Tree

This is a very common interview question.

| Feature | Binary Tree | Binary Search Tree |
|---|---|---|
| Maximum children | 2 | 2 |
| Ordering rule | None | Left < Root < Right (for distinct keys) |
| Searching | O(N) generally | O(H) |
| Insertion | Depends on chosen strategy | Follows BST ordering |
| Deletion | Depends on chosen strategy | Follows BST deletion rules |
| Inorder output | Not necessarily sorted | Sorted for distinct keys |

**Remember:** Every BST is a Binary Tree, but every Binary Tree is not a BST.

## 13. Common Mistakes

1. Forgetting the base case `if (root == null)`.
2. Confusing Inorder, Preorder, and Postorder.
3. Assuming Binary Tree values are automatically sorted.
4. Forgetting that a node may have only one child.
5. Using a Queue incorrectly in Level Order Traversal.
6. Pushing the left child before the right child in iterative preorder.
7. Assuming recursive traversal always uses O(log N) space. A skewed tree can require O(N).
8. Confusing height measured in edges with height measured in nodes.

## 14. Interview Questions and Answers

### Q1. What is a Binary Tree?

A non-linear hierarchical data structure in which each node has at most two children.

### Q2. What is the difference between a Full and Complete Binary Tree?

Full: Every node has either zero or two children.

Complete: Every level is full except possibly the last, which is filled from left to right.

### Q3. What is the difference between a Complete and Perfect Binary Tree?

Complete trees may have an incomplete last level.

Perfect trees have every level completely filled.

### Q4. Which traversal gives sorted output for a BST?

Inorder traversal.

### Q5. Which traversal uses a Queue?

Level Order Traversal.

### Q6. Which traversals use a Stack?

Inorder, Preorder, and Postorder can all be implemented using a Stack.

Recursion internally uses the call stack.

### Q7. What is the time complexity of traversing a Binary Tree?

O(N), because every node is visited once.

### Q8. What is the space complexity of recursive DFS?

O(H), where H is the height of the tree.

### Q9. Can a Binary Tree have duplicate values?

Yes. A normal Binary Tree can contain duplicate values. A BST must define a consistent duplicate-key policy.

### Q10. What is the maximum number of nodes at level L?

\[
2^L
\]

Assuming the root is at level 0.

### Q11. What is the maximum number of nodes in a Binary Tree of height H?

\[
2^{H+1}-1
\]

### Q12. What is the minimum possible height of a Binary Tree with N nodes?

\[
\lceil \log_2(N+1) \rceil - 1
\]

### Q13. What is the worst-case height of a Binary Tree with N nodes?

N − 1, which occurs in a skewed tree.

### Q14. Why is a Binary Heap a Complete Binary Tree?

A complete structure allows the heap to be stored efficiently in an array without requiring explicit pointers for each child.

### Q15. What is the difference between DFS and BFS in a Binary Tree?

DFS explores a branch deeply before backtracking.

BFS visits nodes level by level.

## 15. Practice Problems

### Beginner

1. Create a Binary Tree manually.
2. Print Inorder Traversal.
3. Print Preorder Traversal.
4. Print Postorder Traversal.
5. Print Level Order Traversal.
6. Count total nodes.
7. Count leaf nodes.
8. Count internal nodes.
9. Find the height of a tree.
10. Find the maximum and minimum values.
11. Search for a given value.
12. Find the sum of all nodes.

### Intermediate

1. Find the diameter of a Binary Tree.
2. Check whether two Binary Trees are identical.
3. Check whether a Binary Tree is symmetric.
4. Check whether a Binary Tree is balanced.
5. Find the lowest common ancestor.
6. Print the left view.
7. Print the right view.
8. Print the top view.
9. Print the bottom view.
10. Perform Zigzag Level Order Traversal.
11. Find the maximum width.
12. Find all nodes at distance K from a target node.

### Advanced

1. Serialize and deserialize a Binary Tree.
2. Construct a Binary Tree from Inorder and Preorder traversals.
3. Construct a Binary Tree from Inorder and Postorder traversals.
4. Flatten a Binary Tree into a linked list.
5. Find the maximum path sum.
6. Perform vertical order traversal.
7. Perform boundary traversal.
8. Find the minimum time to burn a Binary Tree.
9. Recover a tree from preorder traversal.
10. Find the largest BST subtree in a Binary Tree.

## 16. Quick Revision Cheat Sheet

| Concept | Important Point |
|---|---|
| Binary Tree | At most two children |
| Left Child | First child reference |
| Right Child | Second child reference |
| Full Binary Tree | Zero or two children |
| Complete Binary Tree | Last level filled left to right |
| Perfect Binary Tree | Every level completely filled |
| Balanced Binary Tree | Height remains relatively small |
| Skewed Binary Tree | One-sided chain |
| Inorder | Left → Root → Right |
| Preorder | Root → Left → Right |
| Postorder | Left → Right → Root |
| Level Order | Level by level |
| DFS | Uses recursion or Stack |
| BFS | Uses Queue |
| Traversal Time | O(N) |
| Recursive DFS Space | O(H) |

### Important Formulas

Maximum nodes at level L:

\[
2^L
\]

Maximum nodes at height H:

\[
2^{H+1}-1
\]

Minimum height for N nodes:

\[
\lceil \log_2(N+1) \rceil - 1
\]

Number of edges:

\[
E=N-1
\]

---

## 17. Recommended Learning Roadmap

After understanding Binary Trees, continue in this order:

1. Binary Tree Traversals (recursive and iterative).
2. Common Binary Tree Problems.
3. Binary Search Tree (BST).
4. BST Search, Insertion, and Deletion.
5. AVL Tree.
6. Red-Black Tree.
7. Binary Heap.
8. Trie.
9. Segment Tree.

**Recommended next topic: Binary Search Tree (BST)** — understand its ordering property, searching, insertion, deletion, and how its performance changes when the tree becomes skewed.