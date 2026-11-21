---
title: "Tree Traversal"
layout: default
parent: Tree
grand_parent: Data Structures & Algorithms
nav_order: 3
permalink: /data-structures/Tree/Tree-Traversal/tree-traversal/

topic: Tree Traversal
section: Tree Algorithms
concepts: [preorder, inorder, postorder, level-order]
algorithms: [dfs, bfs]
complexities: [O(n)]
tags: [data-structures, tree-traversal, dfs, bfs, java]
---

# Tree Traversal in Data Structures

## 1. What is Tree Traversal?

Tree Traversal is the process of visiting every node in a tree exactly once in a particular order.

Consider a tree representing a company's organizational structure. We might want to:

- Visit the manager before their employees.
- Visit employees before their manager.
- Visit employees level by level, starting from the CEO.

Each requirement suggests a different traversal order.

### Example Tree

We will use the following binary tree throughout this document:

```text
              1
            /   \
           2     3
          / \   / \
         4   5 6   7
```

The nodes are:

- Root: 1
- Left subtree: 2, 4, 5
- Right subtree: 3, 6, 7

Every traversal visits all seven nodes, but the visiting order changes.

## 2. Types of Tree Traversal

Tree traversal techniques are commonly divided into two categories.

| Category | Traversal techniques | Main idea |
|---|---|---|
| DFS (Depth-First Search) | Preorder, Inorder, Postorder | Explore a branch deeply before moving to another |
| BFS (Breadth-First Search) | Level Order | Visit nodes level by level |

### DFS vs BFS

**DFS:** Go deep into a branch before exploring another branch.

**BFS:** Visit all nodes at the current level before moving to the next level.

## 3. Preorder Traversal (Root → Left → Right)

Preorder traversal visits nodes in this order:

1. Visit the current node (Root).
2. Traverse the left subtree.
3. Traverse the right subtree.

### Remember

```text
Root → Left → Right
```

### Example

```text
              1
            /   \
           2     3
          / \   / \
         4   5 6   7
```

Step-by-step:

| Step | Action | Visited nodes |
|---|---|---|
| 1 | Visit root 1 | 1 |
| 2 | Move left and visit 2 | 1, 2 |
| 3 | Visit left child 4 | 1, 2, 4 |
| 4 | Return to 2 and visit right child 5 | 1, 2, 4, 5 |
| 5 | Return to root and visit 3 | 1, 2, 4, 5, 3 |
| 6 | Visit left child 6 | 1, 2, 4, 5, 3, 6 |
| 7 | Visit right child 7 | 1, 2, 4, 5, 3, 6, 7 |

**Final Output:**

```text
1 → 2 → 4 → 5 → 3 → 6 → 7
```

### Java Implementation (Recursive)

```java
public static void preorder(Node root) {

    if (root == null) {
        return;
    }

    // 1. Visit Root
    System.out.print(root.data + " ");

    // 2. Traverse Left
    preorder(root.left);

    // 3. Traverse Right
    preorder(root.right);
}
```

### How recursion works

For `preorder(root)`, Java performs:

```text
preorder(1)
    Visit 1

    preorder(2)
        Visit 2

        preorder(4)
            Visit 4
            preorder(null)
            preorder(null)

        preorder(5)
            Visit 5
            preorder(null)
            preorder(null)

    preorder(3)
        Visit 3

        preorder(6)
            Visit 6

        preorder(7)
            Visit 7
```

### Java Implementation (Iterative)

We can implement preorder traversal using a Stack instead of recursion.

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

        // Visit Root
        System.out.print(current.data + " ");

        // Push Right first
        if (current.right != null) {
            stack.push(current.right);
        }

        // Push Left second
        if (current.left != null) {
            stack.push(current.left);
        }
    }
}
```

**Why push the right child first?**

A Stack follows LIFO (Last In, First Out).

If we push the right child first and the left child second, the left child is processed first.

This preserves the order:

```text
Root → Left → Right
```

### Applications of Preorder

- Creating a copy of a tree.
- Serializing a tree.
- Generating prefix expressions.
- Displaying hierarchical structures with parents before children.

## 4. Inorder Traversal (Left → Root → Right)

Inorder traversal visits nodes in this order:

1. Traverse the left subtree.
2. Visit the current node (Root).
3. Traverse the right subtree.

### Remember

```text
Left → Root → Right
```

### Example

```text
              1
            /   \
           2     3
          / \   / \
         4   5 6   7
```

Step-by-step:

| Step | Action | Visited nodes |
|---|---|---|
| 1 | Go to the left subtree of 1 | |
| 2 | Go to the left child of 2 and visit 4 | 4 |
| 3 | Return to 2 and visit it | 4, 2 |
| 4 | Visit the right child of 2 | 4, 2, 5 |
| 5 | Return to 1 and visit it | 4, 2, 5, 1 |
| 6 | Visit the left child of 3 | 4, 2, 5, 1, 6 |
| 7 | Visit 3 | 4, 2, 5, 1, 6, 3 |
| 8 | Visit the right child of 3 | 4, 2, 5, 1, 6, 3, 7 |

**Final Output:**

```text
4 → 2 → 5 → 1 → 6 → 3 → 7
```

### Java Implementation (Recursive)

```java
public static void inorder(Node root) {

    if (root == null) {
        return;
    }

    // 1. Traverse Left
    inorder(root.left);

    // 2. Visit Root
    System.out.print(root.data + " ");

    // 3. Traverse Right
    inorder(root.right);
}
```

### Java Implementation (Iterative)

We use a Stack to simulate the recursive call stack.

```java
import java.util.ArrayDeque;
import java.util.Deque;

public static void inorderIterative(Node root) {

    Deque<Node> stack = new ArrayDeque<>();

    Node current = root;

    while (current != null || !stack.isEmpty()) {

        // Go as far left as possible
        while (current != null) {

            stack.push(current);
            current = current.left;
        }

        // Visit the top node
        current = stack.pop();

        System.out.print(current.data + " ");

        // Explore its right subtree
        current = current.right;
    }
}
```

### Important BST Connection

Inorder traversal of a Binary Search Tree (BST) produces values in sorted order, assuming the BST ordering rules are followed.

Example BST:

```text
          50
         /  \
        30   70
       / \   / \
      20 40 60 80
```

Inorder traversal:

```text
20 → 30 → 40 → 50 → 60 → 70 → 80
```

This property is useful for:

- Printing BST elements in ascending order.
- Finding the kth smallest element.
- Checking whether a binary tree satisfies BST ordering rules.
- Retrieving sorted data without sorting an array separately.

### Applications of Inorder

- Retrieving sorted elements from a BST.
- Evaluating expression trees.
- Finding the kth smallest element in a BST.

## 5. Postorder Traversal (Left → Right → Root)

Postorder traversal visits nodes in this order:

1. Traverse the left subtree.
2. Traverse the right subtree.
3. Visit the current node (Root).

### Remember

```text
Left → Right → Root
```

### Example

```text
              1
            /   \
           2     3
          / \   / \
         4   5 6   7
```

Step-by-step:

| Step | Action | Visited nodes |
|---|---|---|
| 1 | Visit leftmost node 4 | 4 |
| 2 | Visit 5 | 4, 5 |
| 3 | Visit parent 2 | 4, 5, 2 |
| 4 | Visit 6 | 4, 5, 2, 6 |
| 5 | Visit 7 | 4, 5, 2, 6, 7 |
| 6 | Visit parent 3 | 4, 5, 2, 6, 7, 3 |
| 7 | Visit root 1 | 4, 5, 2, 6, 7, 3, 1 |

**Final Output:**

```text
4 → 5 → 2 → 6 → 7 → 3 → 1
```

### Java Implementation (Recursive)

```java
public static void postorder(Node root) {

    if (root == null) {
        return;
    }

    // 1. Traverse Left
    postorder(root.left);

    // 2. Traverse Right
    postorder(root.right);

    // 3. Visit Root
    System.out.print(root.data + " ");
}
```

### Java Implementation (Iterative - Two Stacks)

One simple iterative approach uses two stacks.

```java
import java.util.ArrayDeque;
import java.util.Deque;

public static void postorderIterative(Node root) {

    if (root == null) {
        return;
    }

    Deque<Node> stack1 = new ArrayDeque<>();
    Deque<Node> stack2 = new ArrayDeque<>();

    stack1.push(root);

    while (!stack1.isEmpty()) {

        Node current = stack1.pop();

        stack2.push(current);

        if (current.left != null) {
            stack1.push(current.left);
        }

        if (current.right != null) {
            stack1.push(current.right);
        }
    }

    while (!stack2.isEmpty()) {
        System.out.print(stack2.pop().data + " ");
    }
}
```

The first stack processes the nodes and places them into the second stack.

The second stack reverses the processing order to produce:

```text
Left → Right → Root
```

### Applications of Postorder

- Deleting a tree safely (children before parent).
- Calculating the size or height of a tree.
- Evaluating expression trees.
- Processing directory structures from deepest folders upward.
- Dependency cleanup where child resources must be released before parent resources.

## 6. Level Order Traversal (BFS)

Level Order Traversal visits nodes one level at a time, from left to right.

Unlike preorder, inorder, and postorder, it uses a Queue rather than recursion or a depth-first stack.

### Remember

```text
Level 0 → Level 1 → Level 2 → ...
```

### Example

```text
              1          Level 0
            /   \
           2     3       Level 1
          / \   / \
         4   5 6   7     Level 2
```

Step-by-step:

| Step | Queue | Visited nodes |
|---|---|---|
| 1 | [1] | |
| 2 | [2, 3] | 1 |
| 3 | [3, 4, 5] | 1, 2 |
| 4 | [4, 5, 6, 7] | 1, 2, 3 |
| 5 | [5, 6, 7] | 1, 2, 3, 4 |
| 6 | [6, 7] | 1, 2, 3, 4, 5 |
| 7 | [7] | 1, 2, 3, 4, 5, 6 |
| 8 | [] | 1, 2, 3, 4, 5, 6, 7 |

**Final Output:**

```text
1 → 2 → 3 → 4 → 5 → 6 → 7
```

### Java Implementation

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

        // Visit current node
        System.out.print(current.data + " ");

        // Add left child
        if (current.left != null) {
            queue.offer(current.left);
        }

        // Add right child
        if (current.right != null) {
            queue.offer(current.right);
        }
    }
}
```

### Level Order Traversal by Individual Levels

Sometimes we need the nodes grouped by level instead of a single flat sequence.

Example output:

```text
[
  [1],
  [2, 3],
  [4, 5, 6, 7]
]
```

Java implementation:

```java
import java.util.*;

public static List<List<Integer>> levelOrderByLevel(Node root) {

    List<List<Integer>> result = new ArrayList<>();

    if (root == null) {
        return result;
    }

    Queue<Node> queue = new ArrayDeque<>();

    queue.offer(root);

    while (!queue.isEmpty()) {

        int levelSize = queue.size();

        List<Integer> currentLevel = new ArrayList<>();

        for (int i = 0; i < levelSize; i++) {

            Node current = queue.poll();

            currentLevel.add(current.data);

            if (current.left != null) {
                queue.offer(current.left);
            }

            if (current.right != null) {
                queue.offer(current.right);
            }
        }

        result.add(currentLevel);
    }

    return result;
}
```

**Why do we use `levelSize = queue.size()`?**

It captures the number of nodes belonging to the current level.

We process exactly those nodes before moving to the next level. Their children are added to the queue for the next iteration.

### Applications of Level Order

- Printing a tree level by level.
- Finding the shortest path in an unweighted tree.
- Finding the maximum width of a tree.
- Finding the minimum depth of a tree.
- Building a tree from level-order input.
- Finding nodes at a particular level.

## 7. Complete Java Example

The following program implements all four traversal techniques.

```java
import java.util.*;

public class TreeTraversal {

    static class Node {

        int data;
        Node left;
        Node right;

        Node(int data) {
            this.data = data;
        }
    }

    // Preorder: Root → Left → Right
    static void preorder(Node root) {

        if (root == null) {
            return;
        }

        System.out.print(root.data + " ");

        preorder(root.left);
        preorder(root.right);
    }

    // Inorder: Left → Root → Right
    static void inorder(Node root) {

        if (root == null) {
            return;
        }

        inorder(root.left);

        System.out.print(root.data + " ");

        inorder(root.right);
    }

    // Postorder: Left → Right → Root
    static void postorder(Node root) {

        if (root == null) {
            return;
        }

        postorder(root.left);
        postorder(root.right);

        System.out.print(root.data + " ");
    }

    // Level Order: Level by Level
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

        /*
                  1
                /   \
               2     3
              / \   / \
             4   5 6   7
        */

        Node root = new Node(1);

        root.left = new Node(2);
        root.right = new Node(3);

        root.left.left = new Node(4);
        root.left.right = new Node(5);

        root.right.left = new Node(6);
        root.right.right = new Node(7);

        System.out.print("Preorder: ");
        preorder(root);

        System.out.print("\nInorder: ");
        inorder(root);

        System.out.print("\nPostorder: ");
        postorder(root);

        System.out.print("\nLevel Order: ");
        levelOrder(root);
    }
}
```

### Output

```text
Preorder: 1 2 4 5 3 6 7
Inorder: 4 2 5 1 6 3 7
Postorder: 4 5 2 6 7 3 1
Level Order: 1 2 3 4 5 6 7
```

## 8. Complexity Analysis

Let:

- N = Total number of nodes.
- H = Height of the tree.
- W = Maximum number of nodes at any level.

| Traversal | Time Complexity | Auxiliary Space |
|---|---|---|
| Preorder (recursive) | O(N) | O(H) |
| Inorder (recursive) | O(N) | O(H) |
| Postorder (recursive) | O(N) | O(H) |
| Preorder (iterative) | O(N) | O(N) worst case |
| Inorder (iterative) | O(N) | O(H) |
| Postorder (two stacks) | O(N) | O(N) |
| Level Order | O(N) | O(W) |

### Why is the time complexity O(N)?

Every traversal visits each node exactly once.

Therefore, for N nodes:

```text
Time Complexity = O(N)
```

### Why does recursive DFS use O(H) space?

Each recursive call consumes stack space.

The maximum number of active recursive calls depends on the height of the tree.

- Balanced tree: H = O(log N)
- Skewed tree: H = O(N)

Therefore:

```text
Auxiliary Space = O(H)
```

### Why does Level Order use O(W) space?

The queue can hold nodes from the current level.

The maximum number of nodes in the queue is proportional to the maximum width of the tree.

For a complete binary tree, W can be approximately N/2, so the worst-case auxiliary space is O(N).

*Note:* Auxiliary space excludes the original tree. Recursive stack and traversal data structures are included.

## 9. DFS vs BFS

| Feature | DFS | BFS |
|---|---|---|
| Full form | Depth-First Search | Breadth-First Search |
| Traversal style | Goes deep first | Goes level by level |
| Data structure | Stack / Recursion | Queue |
| Binary tree techniques | Preorder, Inorder, Postorder | Level Order |
| Time complexity | O(N) | O(N) |
| Auxiliary space | O(H) for recursive DFS | O(W) |
| Shortest path in unweighted graph | Not guaranteed | Guaranteed |
| Common applications | Tree processing, expression evaluation | Level-based processing, shortest paths |

## 10. Advanced Concept: Morris Traversal

Morris Traversal is an advanced technique for performing inorder or preorder traversal of a binary tree.

Its key feature is:

```text
Time Complexity: O(N)
Auxiliary Space: O(1)
```

It achieves constant auxiliary space by temporarily modifying tree pointers to create links back to previously visited nodes.

These temporary links are removed during traversal, restoring the original tree structure.

### Morris Inorder Traversal

The general idea:

1. If the current node has no left child, visit it and move right.
2. Otherwise, find the rightmost node in its left subtree (its inorder predecessor).
3. If that predecessor has no right link, create a temporary link to the current node and move left.
4. If the temporary link already exists, remove it, visit the current node, and move right.

Morris traversal is useful when auxiliary memory must be minimized, but it is more complex than ordinary recursive or iterative traversal.

## 11. Common Mistakes

### Mistake 1: Confusing traversal orders

Remember:

```text
Preorder  = Root → Left → Right
Inorder   = Left → Root → Right
Postorder = Left → Right → Root
```

### Mistake 2: Using a Stack for Level Order

Level Order uses a Queue because it must process nodes in the order they were discovered.

### Mistake 3: Forgetting the Null Check

Every recursive traversal must stop when the current node is null.

```java
if (root == null) {
    return;
}
```

### Mistake 4: Assuming Inorder Always Produces Sorted Output

Inorder produces sorted output only when the binary tree follows BST ordering rules.

An ordinary binary tree has no such guarantee.

### Mistake 5: Forgetting to Push Right Before Left in Iterative Preorder

A stack is LIFO. To process the left child first, push the right child before the left child.

### Mistake 6: Using the Wrong Data Structure

| Traversal | Data structure |
|---|---|
| Recursive DFS | Call Stack |
| Iterative DFS | Explicit Stack |
| Level Order | Queue |

## 12. Interview Questions and Answers

### Q1. What is Tree Traversal?

Tree Traversal is the process of visiting every node in a tree exactly once in a particular order.

### Q2. What are the four main tree traversal techniques?

- Preorder
- Inorder
- Postorder
- Level Order

### Q3. Which traversals are DFS?

Preorder, Inorder, and Postorder are DFS traversals.

### Q4. Which traversal uses a Queue?

Level Order Traversal.

### Q5. Which traversal produces sorted output for a BST?

Inorder Traversal.

### Q6. What is the difference between Preorder and Postorder?

Preorder visits the root before its children.

Postorder visits the root after its children.

### Q7. Which traversal is useful for deleting a tree?

Postorder, because it processes children before their parent.

### Q8. What is the time complexity of tree traversal?

O(N), because each node is visited exactly once.

### Q9. Can DFS be implemented without recursion?

Yes. An explicit Stack can be used to implement iterative DFS.

### Q10. What is the difference between BFS and DFS?

BFS explores nodes level by level using a Queue, while DFS explores a branch deeply using a Stack or recursion.

### Q11. What is Morris Traversal?

Morris Traversal is a technique that performs tree traversal in O(N) time and O(1) auxiliary space by temporarily modifying tree pointers.

### Q12. Does every traversal produce a different result?

No. Different traversals can produce the same sequence for some tree shapes, but they follow different visiting rules.

## 13. Practice Problems

Try solving these problems after understanding all four traversal techniques.

### Beginner

1. Implement recursive preorder traversal.
2. Implement recursive inorder traversal.
3. Implement recursive postorder traversal.
4. Implement iterative level order traversal.
5. Count the total number of nodes using recursion.
6. Print all leaf nodes of a binary tree.

### Intermediate

1. Implement iterative preorder traversal.
2. Implement iterative inorder traversal.
3. Implement iterative postorder traversal.
4. Print nodes level by level.
5. Find the maximum value in a binary tree.
6. Find the height of a binary tree.
7. Find the maximum width of a binary tree.
8. Find the left view and right view of a binary tree.
9. Find the level of a given node.
10. Find the level-order traversal in zigzag order.

### Advanced

1. Implement Morris Inorder Traversal.
2. Implement Morris Preorder Traversal.
3. Construct a binary tree from preorder and inorder arrays.
4. Construct a binary tree from postorder and inorder arrays.
5. Find the boundary traversal of a binary tree.
6. Find the vertical order traversal of a binary tree.
7. Find the top view and bottom view of a binary tree.
8. Find the diagonal traversal of a binary tree.

## 14. Quick Revision Cheat Sheet

| Traversal | Visiting Order | Data Structure |
|---|---|---|
| Preorder | Root → Left → Right | Stack / Recursion |
| Inorder | Left → Root → Right | Stack / Recursion |
| Postorder | Left → Right → Root | Stack / Recursion |
| Level Order | Level by Level | Queue |

### Example Tree

```text
              1
            /   \
           2     3
          / \   / \
         4   5 6   7
```

| Traversal | Output |
|---|---|
| Preorder | 1, 2, 4, 5, 3, 6, 7 |
| Inorder | 4, 2, 5, 1, 6, 3, 7 |
| Postorder | 4, 5, 2, 6, 7, 3, 1 |
| Level Order | 1, 2, 3, 4, 5, 6, 7 |

### Easy Memory Trick

- Preorder: Root comes first.
- Inorder: Root comes in the middle.
- Postorder: Root comes last.
- Level Order: Visit one level at a time.

## 15. Recommended Learning Roadmap

After Tree Traversal, continue with:

1. Binary Tree Problems
2. Height and Diameter of Binary Tree
3. Balanced Binary Tree
4. Lowest Common Ancestor (LCA)
5. Top View, Bottom View, Left View, and Right View
6. Vertical Order Traversal
7. Zigzag Level Order Traversal
8. Construct Binary Tree from Traversals
9. Morris Traversal
10. Serialize and Deserialize Binary Tree

These topics are especially useful for Java coding interviews and advanced tree-based problem solving.