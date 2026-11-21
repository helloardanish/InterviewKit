---
title: "Tree"
layout: default
parent: Data Structures & Algorithms
nav_order: 6
has_children: true
permalink: /data-structures/Tree/tree/

topic: Tree
section: Non-Linear Data Structures
concepts: [root, parent, child, leaf, edge, height, depth, subtree]
types: [binary-tree, binary-search-tree, avl-tree, red-black-tree, b-tree]
tags: [data-structures, tree, hierarchical-data, java]
---

# Tree Data Structure

## 1. What Is a Tree?

A **Tree** is a non-linear hierarchical data structure consisting of nodes connected by edges.

Unlike arrays, linked lists, stacks, and queues, which typically store elements sequentially, a tree organizes data in a parent-child relationship.

A tree starts with one special node called the **Root**. Other nodes are connected below it through edges.

### Real-World Examples

**Example 1: Family Tree**

A family tree represents relationships between family members.

```text
             Grandfather
                  |
             Father
             /     \
          Child1  Child2
```

**Example 2: Computer File System**

A folder can contain files and other folders.

```text
Root
├── Documents
│   ├── Resume.pdf
│   └── Notes.txt
├── Photos
│   ├── Family
│   └── Travel
└── Projects
    ├── Java
    └── React
```

**Example 3: Organization Structure**

A company has a hierarchical structure.

```text
             CEO
              |
       Engineering Manager
          /          \
    Team Lead A    Team Lead B
       /               \
  Developer         Developer
```

**Example 4: HTML DOM**

HTML documents are represented internally as a tree of elements.

```html
<html>
  <body>
    <h1>Welcome</h1>
    <p>Hello World</p>
  </body>
</html>
```

The `html` element is the root, and its child elements form branches.

## 2. Why Do We Need Trees?

Trees are useful when data has a hierarchical relationship.

They help us:

- Represent hierarchical information.
- Search and retrieve data efficiently.
- Organize files and folders.
- Store database indexes.
- Represent mathematical expressions.
- Implement autocomplete and dictionaries.
- Manage dependencies and organizational structures.

## 3. Understanding a Tree with an Example

Consider this tree:

```text
                 A
               /   \
              B     C
             / \     \
            D   E     F
               / \
              G   H
```

This tree contains:

- 8 nodes: A, B, C, D, E, F, G, H.
- 7 edges.
- Root node: A.
- Leaf nodes: D, F, G, H.

Let's understand its terminology.

## 4. Important Tree Terminology

### 4.1 Node

A node represents an individual element in a tree.

Example:

```text
A, B, C, D, E, F, G, H
```

Each of these is a node.

### 4.2 Root Node

The root is the topmost node of a tree.

```text
        A
       / \
      B   C
```

Here, A is the root.

A tree has exactly one root.

### 4.3 Edge

An edge is a connection between two nodes.

```text
A ----- B
```

The connection between A and B is an edge.

A tree with V nodes has exactly:

\[
E = V - 1
\]

edges.

### 4.4 Parent Node

A node that has one or more child nodes is called a parent.

```text
        A
       / \
      B   C
```

A is the parent of B and C.

### 4.5 Child Node

A node directly connected below another node is called its child.

B and C are children of A.

### 4.6 Sibling Nodes

Nodes that share the same parent are called siblings.

```text
        A
       /|\
      B C D
```

B, C, and D are siblings.

### 4.7 Leaf Node

A node that has no children is called a leaf node.

```text
        A
       / \
      B   C
     / \
    D   E
```

Leaf nodes:

- C
- D
- E

### 4.8 Internal Node

A node that has at least one child is called an internal node.

In the above tree:

- A
- B

are internal nodes.

The root may also be an internal node.

### 4.9 Ancestor

An ancestor of a node is any node on the path from the root to that node, excluding the node itself.

```text
        A
       /
      B
     /
    C
   /
  D
```

For D:

- A is an ancestor.
- B is an ancestor.
- C is an ancestor.

### 4.10 Descendant

A descendant is any node that appears below a particular node.

For B in the previous example:

- C is a descendant.
- D is a descendant.

### 4.11 Depth of a Node

The depth of a node is the number of edges from the root to that node.

```text
Level 0:          A

Level 1:       B     C

Level 2:     D   E     F
```

| Node | Depth |
|---|---:|
| A | 0 |
| B | 1 |
| C | 1 |
| D | 2 |
| E | 2 |
| F | 2 |

The root has depth 0.

### 4.12 Height of a Node

The height of a node is the number of edges on the longest downward path from that node to a leaf.

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

### 4.13 Height of a Tree

The height of a tree is the height of its root.

For the above tree:

\[
Height(Tree) = 2
\]

### 4.14 Level of a Node

Using the common convention where the root is at level 0:

\[
Level(Node) = Depth(Node)
\]

Some textbooks define the root as level 1. Always check the convention being used.

### 4.15 Degree of a Node

The degree of a node is the number of children it has.

```text
          A
        / | \
       B  C  D
         / \
        E   F
```

- Degree(A) = 3
- Degree(C) = 2
- Degree(B) = 0
- Degree(E) = 0

The degree of a tree is the maximum degree of any node in the tree.

### 4.16 Subtree

A subtree is a tree formed by a node and all its descendants.

```text
          A
         / \
        B   C
       / \
      D   E
```

The subtree rooted at B is:

```text
       B
      / \
     D   E
```

## 5. Important Properties of Trees

Remember these properties:

1. A non-empty tree has exactly one root.
2. Every node except the root has exactly one parent.
3. A tree with V nodes has V − 1 edges.
4. There is exactly one simple path between any two nodes in a tree.
5. A tree contains no cycles.
6. Removing any edge from a tree disconnects it.
7. Adding an edge between two existing nodes creates a cycle.
8. Every node is reachable from the root in a rooted tree.

### Important Formula

For a tree containing V nodes:

\[
E = V - 1
\]

For example, if a tree contains 10 nodes:

\[
E = 10 - 1 = 9
\]

## 6. Types of Trees

There are several types of trees in computer science.

### 6.1 General Tree

A general tree allows each node to have any number of children.

```text
              A
          /   |   \
         B    C    D
        / \       / \
       E   F     G   H
```

A node can have zero, one, two, or many children.

Applications:

- File systems.
- Organization hierarchies.
- Family trees.
- HTML DOM.

### 6.2 Binary Tree

A Binary Tree is a tree in which each node can have at most two children.

These children are called:

- Left Child
- Right Child

Example:

```text
          A
         / \
        B   C
       / \   \
      D   E   F
```

A node can have:

- No children.
- Only a left child.
- Only a right child.
- Both left and right children.

**Important:** A binary tree does not necessarily have sorted values.

### 6.3 Binary Search Tree (BST)

A Binary Search Tree is a special type of binary tree that follows an ordering rule.

For every node:

- Values in the left subtree are smaller than the node.
- Values in the right subtree are greater than the node.

Example:

```text
          50
         /  \
       30    70
      / \    / \
    20  40  60  80
```

Searching for 60:

1. Start at 50.
2. 60 is greater than 50, move right.
3. Reach 70.
4. 60 is smaller than 70, move left.
5. Find 60.

A balanced BST supports search, insertion, and deletion in O(log n) time.

An ordinary BST can become skewed, making these operations O(n) in the worst case.

### 6.4 Full Binary Tree

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

B and A have two children, while C, D, and E have zero children.

No node has exactly one child.

### 6.5 Complete Binary Tree

A Complete Binary Tree has:

- Every level completely filled except possibly the last.
- The last level filled from left to right without gaps.

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
          E   F
```

Because the last level has gaps before its occupied positions.

Complete Binary Trees are used to implement Binary Heaps.

### 6.6 Perfect Binary Tree

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

For a perfect binary tree of height h, with the root at depth 0:

\[
Nodes = 2^{h+1} - 1
\]

For example, height 2:

\[
Nodes = 2^3 - 1 = 7
\]

### 6.7 Balanced Binary Tree

A balanced binary tree keeps its height relatively small compared with its number of nodes.

For an AVL tree, the heights of the left and right subtrees of every node differ by at most 1.

Balanced trees help maintain efficient searching and updating.

Examples:

- AVL Tree
- Red-Black Tree

### 6.8 Skewed Binary Tree

A skewed tree is a binary tree in which every node has at most one child.

Left-skewed:

```text
        A
       /
      B
     /
    C
   /
  D
```

Right-skewed:

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

Its height can become O(n).

## 7. Binary Tree Representation in Java

A binary tree node typically contains three fields:

1. Data
2. Reference to the left child
3. Reference to the right child

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

Understanding this class is fundamental to working with binary trees.

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

- `root` stores the reference to node 10.
- `root.left` points to node 20.
- `root.right` points to node 30.

## 8. Building a Binary Tree in Java

Let's build the following tree:

```text
          10
         /  \
        20   30
       / \     \
      40  50    60
```

### Java Implementation

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

        Node root = new Node(10);

        root.left = new Node(20);
        root.right = new Node(30);

        root.left.left = new Node(40);
        root.left.right = new Node(50);

        root.right.right = new Node(60);

        System.out.println("Root: " + root.data);

        System.out.println(
                "Left Child: " + root.left.data
        );

        System.out.println(
                "Right Child: " + root.right.data
        );
    }
}
```

Output:

```text
Root: 10
Left Child: 20
Right Child: 30
```

**Important:** A basic binary tree does not automatically insert values in sorted order. We manually assign the left and right children here.

A Binary Search Tree uses a separate insertion rule.

## 9. Tree Traversal

Tree Traversal means visiting every node in a tree in a particular order.

The four fundamental traversal methods are:

1. Inorder Traversal
2. Preorder Traversal
3. Postorder Traversal
4. Level Order Traversal

The first three are depth-first traversals. Level order is a breadth-first traversal.

Consider:

```text
          A
         / \
        B   C
       / \   \
      D   E   F
```

### 9.1 Inorder Traversal (Left → Root → Right)

Steps:

1. Visit the left subtree.
2. Visit the current node.
3. Visit the right subtree.

Result:

```text
D → B → E → A → C → F
```

### 9.2 Preorder Traversal (Root → Left → Right)

Steps:

1. Visit the current node.
2. Visit the left subtree.
3. Visit the right subtree.

Result:

```text
A → B → D → E → C → F
```

### 9.3 Postorder Traversal (Left → Right → Root)

Steps:

1. Visit the left subtree.
2. Visit the right subtree.
3. Visit the current node.

Result:

```text
D → E → B → F → C → A
```

### 9.4 Level Order Traversal

Visit nodes level by level from left to right.

Result:

```text
A → B → C → D → E → F
```

Level order traversal uses a Queue, while the depth-first traversals typically use recursion or a Stack.

## 10. Java Implementation of Tree Traversals

Using the `Node` class from earlier:

### Inorder

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

### Preorder

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

### Postorder

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

### Level Order

```java
import java.util.*;

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

## 11. Tree Traversal Complexity

Let N be the number of nodes and H be the height of the tree.

| Traversal | Time Complexity | Auxiliary Space |
|---|---:|---:|
| Inorder | O(N) | O(H) |
| Preorder | O(N) | O(H) |
| Postorder | O(N) | O(H) |
| Level Order | O(N) | O(W) |

Where:

- N = Number of nodes.
- H = Height of the tree.
- W = Maximum width of the tree.

For recursive DFS traversals, the call stack can grow to O(H).

For level-order traversal, the queue can hold up to O(W) nodes.

## 12. Tree vs Graph

A tree is a special type of graph.

| Feature | Tree | General Graph |
|---|---|---|
| Structure | Hierarchical | General network |
| Cycles | Not allowed | May contain cycles |
| Connectivity | Connected (for a non-empty tree) | May be disconnected |
| Root | Rooted trees have one root | No root required |
| Parent-child relationship | Present in rooted trees | Not necessarily present |
| Number of edges | V − 1 | Varies |
| Path between two vertices | Exactly one simple path | May be zero, one, or multiple paths |
| Traversal | DFS, BFS | DFS, BFS |

**Important:** A tree is an undirected connected acyclic graph. A rooted tree additionally identifies one vertex as the root and organizes the relationships into parent-child levels.

## 13. Tree vs Binary Tree vs Binary Search Tree

| Feature | General Tree | Binary Tree | Binary Search Tree |
|---|---|---|---|
| Maximum children | No fixed limit | 2 | 2 |
| Left/right distinction | Not required | Yes | Yes |
| Sorted values | Not required | Not required | Required by BST ordering |
| Searching | Depends on structure | O(N) generally | O(log N) average for balanced shape |
| Common usage | Hierarchies | Expression trees, heaps | Searching and ordered data |

A BST is a special kind of binary tree, but not every binary tree is a BST.

## 14. Important Tree Applications

| Application | Usage |
|---|---|
| File systems | Folder and directory hierarchy |
| Databases | B-Trees and B+ Trees for indexing |
| Compilers | Abstract Syntax Trees |
| Web browsers | DOM representation |
| Search engines | Indexing and retrieval structures |
| Autocomplete | Trie data structure |
| Operating systems | Process and directory hierarchies |
| Networking | Routing and spanning trees |
| Artificial Intelligence | Decision trees |
| Data compression | Huffman trees |

## 15. Common Mistakes

### Mistake 1: Assuming Every Tree Is a Binary Tree

A general tree can have any number of children.

A binary tree allows at most two children.

### Mistake 2: Assuming Every Binary Tree Is a BST

A binary tree has no required ordering rule.

A BST follows a specific ordering rule.

### Mistake 3: Confusing Height and Depth

- Depth: Distance from the root to a node.
- Height: Longest downward distance from a node to a leaf.

### Mistake 4: Forgetting Null Checks

Always check for null nodes in recursive traversal.

```java
if (root == null) {
    return;
}
```

### Mistake 5: Assuming Every Tree Is Balanced

A binary tree can be skewed, which can make its height O(N).

### Mistake 6: Confusing Full, Complete, and Perfect Binary Trees

- Full: Every node has zero or two children.
- Complete: Last level is filled from left to right.
- Perfect: All internal nodes have two children and all leaves are at the same depth.

## 16. Interview Questions and Answers

### Q1. What is a Tree?

A tree is a non-linear hierarchical data structure consisting of nodes connected through edges. It has no cycles and is connected.

### Q2. What is the root node?

The topmost node of a rooted tree.

### Q3. How many edges does a tree with N nodes have?

A tree with N nodes has exactly N − 1 edges.

### Q4. What is a leaf node?

A node that has no children.

### Q5. What is the difference between height and depth?

Depth is the distance from the root to a node.

Height is the longest downward distance from a node to a leaf.

### Q6. What is a binary tree?

A tree in which each node has at most two children.

### Q7. What is a Binary Search Tree?

A binary tree that maintains an ordering rule: smaller values are placed in the left subtree and larger values in the right subtree.

### Q8. What is a balanced tree?

A tree whose height remains relatively small compared with its number of nodes.

AVL and Red-Black Trees are examples of self-balancing binary search trees.

### Q9. What is a complete binary tree?

A binary tree in which all levels except possibly the last are completely filled, and the last level is filled from left to right.

### Q10. What is a full binary tree?

A binary tree in which every node has either zero or two children.

### Q11. What is a perfect binary tree?

A binary tree in which every internal node has exactly two children and all leaves are at the same level.

### Q12. Which traversal gives sorted output for a BST?

Inorder traversal gives values in ascending order for a BST with distinct keys and the standard ordering rule.

### Q13. Which traversal uses a Queue?

Level order traversal.

### Q14. Which traversal uses a Stack or recursion?

Inorder, preorder, and postorder traversals.

### Q15. What is the maximum number of nodes in a binary tree of height H?

Assuming root height/depth starts at 0:

\[
N_{\max} = 2^{H+1} - 1
\]

### Q16. What is the maximum number of nodes at level L?

Assuming the root is at level 0:

\[
N_{\max} = 2^L
\]

### Q17. What is the minimum height of a binary tree with N nodes?

For a tree with N nodes and height measured in edges, the minimum possible height is:

\[
H_{\min} = \lceil \log_2(N+1) \rceil - 1
\]

This height is achieved by a complete or appropriately filled binary tree.

### Q18. What is the difference between a tree and a graph?

A tree is a connected, acyclic graph. A general graph may be disconnected and may contain cycles.

### Q19. What happens if you remove an edge from a tree?

The tree becomes disconnected, producing two separate components.

### Q20. Can a tree contain duplicate values?

Yes. Tree structure and value uniqueness are separate concerns. A BST implementation must define how duplicate keys are handled.

## 17. Practice Problems

### Beginner Level

1. Create a binary tree manually in Java.
2. Count the total number of nodes.
3. Count the number of leaf nodes.
4. Find the height of a binary tree.
5. Find the depth of a given node.
6. Print inorder traversal.
7. Print preorder traversal.
8. Print postorder traversal.
9. Print level-order traversal.
10. Find the maximum value in a binary tree.

### Intermediate Level

1. Find the diameter of a binary tree.
2. Check whether two trees are identical.
3. Check whether a binary tree is symmetric.
4. Find the lowest common ancestor.
5. Print the left view of a binary tree.
6. Print the right view of a binary tree.
7. Print the top view of a binary tree.
8. Find the maximum depth of a binary tree.
9. Check whether a tree is height-balanced.
10. Construct a tree from inorder and preorder traversals.

### Challenge Level

1. Serialize and deserialize a binary tree.
2. Flatten a binary tree into a linked list.
3. Maximum path sum in a binary tree.
4. Vertical order traversal.
5. Boundary traversal.
6. Recover a BST.
7. Binary Tree Maximum Path Sum.
8. All Nodes Distance K in Binary Tree.

## 18. Quick Revision Cheat Sheet

| Term | Meaning |
|---|---|
| Tree | Hierarchical non-linear data structure |
| Root | Topmost node |
| Parent | Node with child nodes |
| Child | Node below a parent |
| Sibling | Nodes with the same parent |
| Leaf | Node without children |
| Internal Node | Node with at least one child |
| Ancestor | Node above another node |
| Descendant | Node below another node |
| Depth | Distance from root |
| Height | Longest downward path to a leaf |
| Degree | Number of children |
| Subtree | A node and its descendants |
| Binary Tree | At most two children |
| BST | Binary tree with ordering rules |
| Full Binary Tree | Zero or two children per node |
| Complete Binary Tree | Last level filled left to right |
| Perfect Binary Tree | All internal nodes have two children and all leaves share a level |
| Balanced Tree | Height remains relatively small |
| Skewed Tree | Nodes form a one-sided chain |

### Important Formulas

Number of edges in a tree:

\[
E = V - 1
\]

Maximum nodes at level L in a binary tree:

\[
2^L
\]

Maximum nodes in a binary tree of height H:

\[
2^{H+1} - 1
\]

Minimum possible height for N nodes:

\[
\lceil \log_2(N+1) \rceil - 1
\]

---

## 19. Recommended Learning Roadmap

Learn tree topics in this order:

1. Tree Introduction and Terminology
2. Binary Tree
3. Binary Tree Traversals
4. Binary Search Tree (BST)
5. BST Search, Insertion, and Deletion
6. Balanced Trees (AVL and Red-Black Trees)
7. Binary Heap
8. Trie
9. Segment Tree
10. Fenwick Tree (Binary Indexed Tree)
11. B-Tree and B+ Tree

**Recommended next topic: Binary Tree in Java.** Learn its node structure, construction, recursive traversal, iterative traversal, and common coding problems before moving to Binary Search Trees.