---
title: "Red-Black Tree"
layout: default
parent: Tree
grand_parent: Data Structures & Algorithms
nav_order: 5
permalink: /data-structures/Tree/Red-Black-Tree/red-black-tree/

topic: Red-Black Tree
section: Self-Balancing Trees
concepts: [red-black-properties, self-balancing-tree, recoloring, rotations]
operations: [insertion, deletion, search]
java_classes: [TreeMap, TreeSet]
complexities: [O(log n)]
tags: [data-structures, red-black-tree, balanced-tree, java]
---

# Red-Black Tree

## 1. What Is a Red-Black Tree?

A **Red-Black Tree** is a self-balancing Binary Search Tree in which every node is assigned one of two colors:

- Red
- Black

It follows all the normal BST rules:

- Values smaller than a node are stored in its left subtree.
- Values greater than a node are stored in its right subtree.
- Duplicate keys follow a defined policy.

It also follows five additional rules that help keep the tree balanced.

Unlike an AVL Tree, which checks the height difference between the left and right subtrees, a Red-Black Tree uses coloring rules to control its height.

### Why Do We Need Red-Black Trees?

Consider inserting sorted values into an ordinary BST:

```text
10, 20, 30, 40, 50
```

The tree may become:

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

This structure behaves like a Linked List.

Searching may require O(n) time.

A Red-Black Tree rearranges nodes through rotations and recoloring to maintain a balanced structure.

One possible balanced result is:

```text
          20(B)
         /     \
      10(B)   40(B)
              /   \
           30(R)  50(R)
```

`B` represents Black, and `R` represents Red.

This tree follows the Red-Black properties.

---

## 2. Real-World Analogy

Imagine organizing a large library into sections.

Each section has a color label:

- Red
- Black

The colors are not used to decide where books belong. The book values still follow the normal BST ordering.

Instead, the color rules ensure that no path through the library becomes excessively longer than another.

Whenever a new book is added or removed, the librarian may:

- Change some section colors.
- Move a section to another position.
- Perform both operations.

A Red-Black Tree uses the same idea through **recoloring and rotations**.

---

## 3. Five Properties of a Red-Black Tree

Every Red-Black Tree must satisfy five properties.

### Property 1: Every Node Is Red or Black

Every node must have exactly one of two colors:

```text
RED
BLACK
```

There are no other colors.

### Property 2: The Root Is Black

The root node must always be black.

Example:

```text
       20(B)
      /     \
   10(R)   30(R)
```

The root is black.

### Property 3: Every NIL Leaf Is Black

Every missing child is treated as a special black NIL (null) leaf.

For example:

```text
         20(B)
        /     \
     10(R)   30(R)
     /  \     /  \
   NIL NIL  NIL  NIL
```

All NIL leaves are black.

In implementations, NIL leaves may be represented explicitly by sentinel nodes or implicitly by null references with black color.

### Property 4: A Red Node Cannot Have a Red Child

A red node cannot have a red parent or red child.

Invalid:

```text
       20(B)
       /
     10(R)
     /
    5(R)
```

Here, node 10 and node 5 are both red.

This violates the Red-Black property.

Valid:

```text
       20(B)
       /
     10(R)
     /
    5(B)
```

A red node must have black children.

### Property 5: Every Path Has the Same Number of Black Nodes

For every node, all paths from that node to its descendant NIL leaves must contain the same number of black nodes.

This is called the **Black Height property**.

Example:

```text
          20(B)
         /     \
      10(B)   30(B)
      /  \     /  \
    NIL NIL  NIL  NIL
```

Every path contains the same number of black nodes.

The black height is commonly defined as the number of black nodes on a path, excluding the starting node and including the NIL leaf. Some explanations use a different counting convention, but the balancing rule is unchanged.

### Summary of the Five Properties

| Property | Rule |
|---|---|
| 1 | Every node is Red or Black |
| 2 | Root is Black |
| 3 | NIL leaves are Black |
| 4 | Red nodes cannot have Red children |
| 5 | Every path from a node to descendant NIL leaves has the same black height |

These five properties ensure that the tree cannot become excessively unbalanced.

---

## 4. Important Terminology

Understanding these terms makes insertion and deletion easier.

| Term | Meaning |
|---|---|
| Parent | The node directly above another node |
| Child | A node directly below another node |
| Grandparent | Parent of the current node's parent |
| Sibling | Another child of the same parent |
| Uncle | Sibling of the current node's parent |
| NIL | Special black leaf representing a missing child |
| Recoloring | Changing a node's color |
| Rotation | Rearranging parent-child relationships |
| Black Height | Number of black nodes along a path under a consistent counting convention |

### Example

```text
              40(B)
             /     \
          20(R)    60(B)
          /  \
       10(B) 30(B)
```

For node 10:

- Parent = 20
- Grandparent = 40
- Uncle = 60
- Sibling = 30

---

## 5. Why Does a Red-Black Tree Stay Balanced?

Two properties are especially important:

1. A red node cannot have a red child.
2. Every path from a node to its descendant NIL leaves has the same black height.

These properties prevent one path from becoming significantly longer than another.

The longest possible path cannot have more than twice as many nodes as the shortest path.

This gives the height bound:

```text
Height <= 2 * log2(n + 1)
```

Therefore:

```text
Height = O(log n)
```

As a result:

- Searching: O(log n)
- Insertion: O(log n)
- Deletion: O(log n)

This is the main reason Red-Black Trees are useful for maintaining ordered data.

---

## 6. Red-Black Tree Rotations

Rotations rearrange nodes while preserving BST ordering.

There are two basic rotations:

1. Left Rotation
2. Right Rotation

### 6.1 Left Rotation

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
private void leftRotate(Node x) {

    Node y = x.right;

    x.right = y.left;

    if (y.left != null) {
        y.left.parent = x;
    }

    y.parent = x.parent;

    if (x.parent == null) {
        root = y;
    }
    else if (x == x.parent.left) {
        x.parent.left = y;
    }
    else {
        x.parent.right = y;
    }

    y.left = x;
    x.parent = y;
}
```

### 6.2 Right Rotation

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

Java implementation:

```java
private void rightRotate(Node y) {

    Node x = y.left;

    y.left = x.right;

    if (x.right != null) {
        x.right.parent = y;
    }

    x.parent = y.parent;

    if (y.parent == null) {
        root = x;
    }
    else if (y == y.parent.left) {
        y.parent.left = x;
    }
    else {
        y.parent.right = x;
    }

    x.right = y;
    y.parent = x;
}
```

**Important:** Rotations preserve BST ordering, but the Red-Black color rules may still need to be restored afterward.

---

## 7. Red-Black Tree Insertion

Insertion starts like normal BST insertion.

The new node is initially colored **Red**.

Why Red?

Because inserting a red node does not immediately increase the number of black nodes on paths to NIL leaves.

However, insertion can violate the rule that a red node cannot have a red child.

The tree repairs these violations using recoloring and rotations.

### Important Terms Used During Insertion

Suppose the newly inserted node is `N`.

- `P` = Parent
- `G` = Grandparent
- `U` = Uncle

There are several insertion cases.

### Case 1: New Node Is the Root

Insert the first node:

```text
20
```

Initially, the new node is red:

```text
20(R)
```

But the root must be black.

Recolor it:

```text
20(B)
```

### Case 2: Parent Is Black

Before:

```text
       20(B)
       /
     10(B)
```

Insert 5:

```text
       20(B)
       /
     10(B)
     /
    5(R)
```

There is no Red-Red violation because the parent is black.

No rotation or recoloring is required.

### Case 3: Parent and Uncle Are Red

Before insertion:

```text
           40(B)
          /     \
       20(R)    60(R)
```

Insert 10:

```text
           40(B)
          /     \
       20(R)    60(R)
       /
     10(R)
```

Now parent and new node are both red.

The uncle is also red.

**Solution: Recoloring**

1. Change parent to Black.
2. Change uncle to Black.
3. Change grandparent to Red.
4. Continue checking from the grandparent.

After recoloring:

```text
           40(R)
          /     \
       20(B)    60(B)
       /
     10(R)
```

The root must remain black, so recolor it if necessary.

### Case 4: Parent Is Red, Uncle Is Black, and the New Node Forms a Triangle

Consider:

```text
          40(B)
         /
       20(R)
          \
           30(R)
```

This is a Left-Right (LR) configuration.

**Solution:**

1. Left rotate at the parent (20).
2. The structure becomes a Left-Left configuration.
3. Right rotate at the grandparent (40).
4. Recolor the resulting parent and grandparent appropriately.

Final tree:

```text
          30(B)
         /     \
       20(R)   40(R)
```

### Case 5: Parent Is Red, Uncle Is Black, and the New Node Forms a Line

Consider:

```text
          40(B)
         /
       20(R)
       /
     10(R)
```

This is a Left-Left (LL) configuration.

**Solution:**

1. Right rotate at the grandparent (40).
2. Recolor the former parent to Black.
3. Recolor the former grandparent to Red.

Final tree:

```text
          20(B)
         /     \
       10(R)   40(R)
```

### Mirror Cases

The same logic applies on the right side.

| Configuration | Action |
|---|---|
| Left-Left | Right rotate grandparent |
| Left-Right | Left rotate parent, then right rotate grandparent |
| Right-Right | Left rotate grandparent |
| Right-Left | Right rotate parent, then left rotate grandparent |

The cases involving a red uncle use recoloring rather than immediately rotating.

**Important:** The actual insertion algorithm uses the relationship between parent, uncle, and grandparent. The line/triangle cases apply when the uncle is black.

---

## 8. Red-Black Tree Insertion Example

Insert:

```text
10, 20, 30
```

### Step 1: Insert 10

The first node becomes black.

```text
10(B)
```

### Step 2: Insert 20

New nodes are red.

```text
10(B)
    \
    20(R)
```

The parent is black, so no violation exists.

### Step 3: Insert 30

```text
10(B)
    \
    20(R)
        \
        30(R)
```

There is a Red-Red violation.

The parent is red, and the uncle is black (NIL).

This is a Right-Right case.

Perform a left rotation at node 10 and recolor.

Final tree:

```text
        20(B)
       /     \
    10(R)   30(R)
```

The tree now satisfies all Red-Black properties.

---

## 9. Red-Black Tree Node Implementation in Java

Each node stores:

- Key
- Color
- Left child
- Right child
- Parent

```java
class Node {

    int key;

    boolean isRed;

    Node left;
    Node right;
    Node parent;

    Node(int key) {
        this.key = key;
        this.isRed = true;
    }
}
```

A boolean is used for simplicity:

```text
true  = RED
false = BLACK
```

A production implementation may use an enum or a dedicated sentinel NIL node.

---

## 10. Complete Red-Black Tree Insertion Implementation in Java

The following implementation supports:

- Node insertion
- BST ordering
- Left and right rotations
- Red-Black insertion fix-up
- Searching
- Inorder traversal

It uses `null` references to represent NIL leaves. Helper methods treat null nodes as black.

```java
public class RedBlackTree {

    private static final boolean RED = true;
    private static final boolean BLACK = false;

    static class Node {

        int key;
        boolean color;

        Node left;
        Node right;
        Node parent;

        Node(int key) {
            this.key = key;
            this.color = RED;
        }
    }

    private Node root;

    // Null NIL nodes are considered black
    private boolean colorOf(Node node) {
        return node == null ? BLACK : node.color;
    }

    private void setColor(Node node, boolean color) {
        if (node != null) {
            node.color = color;
        }
    }

    // Left Rotation
    private void leftRotate(Node x) {

        Node y = x.right;

        x.right = y.left;

        if (y.left != null) {
            y.left.parent = x;
        }

        y.parent = x.parent;

        if (x.parent == null) {
            root = y;
        }
        else if (x == x.parent.left) {
            x.parent.left = y;
        }
        else {
            x.parent.right = y;
        }

        y.left = x;
        x.parent = y;
    }

    // Right Rotation
    private void rightRotate(Node y) {

        Node x = y.left;

        y.left = x.right;

        if (x.right != null) {
            x.right.parent = y;
        }

        x.parent = y.parent;

        if (y.parent == null) {
            root = x;
        }
        else if (y == y.parent.left) {
            y.parent.left = x;
        }
        else {
            y.parent.right = x;
        }

        x.right = y;
        y.parent = x;
    }

    // Insert
    public void insert(int key) {

        Node node = new Node(key);

        Node parent = null;
        Node current = root;

        // Normal BST insertion position
        while (current != null) {

            parent = current;

            if (key < current.key) {
                current = current.left;
            }
            else if (key > current.key) {
                current = current.right;
            }
            else {
                // Ignore duplicates
                return;
            }
        }

        node.parent = parent;

        if (parent == null) {
            root = node;
        }
        else if (key < parent.key) {
            parent.left = node;
        }
        else {
            parent.right = node;
        }

        fixInsertion(node);
    }

    // Restore Red-Black properties after insertion
    private void fixInsertion(Node node) {

        while (node != root &&
               colorOf(node.parent) == RED) {

            Node parent = node.parent;
            Node grandparent = parent.parent;

            if (parent == grandparent.left) {

                Node uncle = grandparent.right;

                // Case 1: Uncle is red
                if (colorOf(uncle) == RED) {

                    setColor(parent, BLACK);
                    setColor(uncle, BLACK);
                    setColor(grandparent, RED);

                    node = grandparent;
                }
                else {

                    // Case 2: Triangle
                    if (node == parent.right) {

                        node = parent;
                        leftRotate(node);
                    }

                    // Case 3: Line
                    setColor(node.parent, BLACK);
                    setColor(node.parent.parent, RED);

                    rightRotate(node.parent.parent);
                }
            }
            else {

                Node uncle = grandparent.left;

                // Case 1: Uncle is red
                if (colorOf(uncle) == RED) {

                    setColor(parent, BLACK);
                    setColor(uncle, BLACK);
                    setColor(grandparent, RED);

                    node = grandparent;
                }
                else {

                    // Case 2: Triangle
                    if (node == parent.left) {

                        node = parent;
                        rightRotate(node);
                    }

                    // Case 3: Line
                    setColor(node.parent, BLACK);
                    setColor(node.parent.parent, RED);

                    leftRotate(node.parent.parent);
                }
            }
        }

        // Root must always be black
        setColor(root, BLACK);
    }

    // Search
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

        System.out.print(
            node.key +
            (node.color == RED ? "(R) " : "(B) ")
        );

        inorder(node.right);
    }

    public static void main(String[] args) {

        RedBlackTree tree = new RedBlackTree();

        tree.insert(10);
        tree.insert(20);
        tree.insert(30);
        tree.insert(15);
        tree.insert(25);
        tree.insert(5);

        System.out.println("Inorder Traversal:");

        tree.inorder();

        System.out.println(
            "Search 20: " + tree.search(20)
        );

        System.out.println(
            "Search 100: " + tree.search(100)
        );
    }
}
```

### Example Output

```text
Inorder Traversal:
5(R) 10(B) 15(R) 20(B) 25(R) 30(B)

Search 20: true
Search 100: false
```

The exact node colors and tree shape depend on the insertion sequence and fix-up operations. The important guarantee is that the resulting tree satisfies the Red-Black properties.

---

## 11. Red-Black Tree Deletion

Deletion is more complicated than insertion.

It starts like normal BST deletion, but removing a black node can violate the black-height property.

The implementation must repair the tree using recoloring and rotations.

### Three BST Deletion Cases

**Case 1: Delete a Leaf Node**

```text
       20(B)
      /     \
   10(R)    30(R)
```

Delete 10:

```text
       20(B)
          \
          30(R)
```

Because the removed node was red, the black height is unchanged. No Red-Black fix-up is required.

**Case 2: Delete a Node With One Child**

A Red-Black Tree's rules restrict which color combinations can occur with a single non-NIL child. In the usual valid-tree deletion case, a node with one non-NIL child has a red child replacing a black node.

The replacement can be promoted and recolored black.

**Case 3: Delete a Node With Two Children**

Find the inorder successor (minimum node in the right subtree), replace the deleted node's key/value as appropriate, and physically remove the successor.

The color of the physically removed node determines whether fix-up is necessary.

### Why Is Deletion Difficult?

When a black node is removed, paths through that node may have one fewer black node.

This is often described as a **double-black problem**.

The fix-up procedure examines:

- The sibling
- The sibling's children
- The parent
- Their colors

It then performs recoloring and rotations to restore the properties.

The fix-up cases are more involved than insertion, so they are usually studied separately.

**Interview point:** A complete Red-Black Tree implementation must support deletion fix-up, not just ordinary BST deletion.

---

## 12. AVL Tree vs Red-Black Tree

Both are self-balancing BSTs, but their balancing strategies differ.

| Feature | AVL Tree | Red-Black Tree |
|---|---|---|
| Balancing technique | Height difference | Node colors |
| Balance rule | BF is -1, 0, +1 | Five Red-Black properties |
| Strictness | More strictly balanced | Less strictly balanced |
| Search | O(log n) | O(log n) |
| Insertion | O(log n) | O(log n) |
| Deletion | O(log n) | O(log n) |
| Height | Tighter bound | Up to twice log2(n+1) |
| Rotations during insertion | At most 2 | At most 2 |
| Rotations during deletion | Can occur at multiple ancestors | At most 3 in standard RB deletion |
| Additional metadata | Height or balance factor | Color |
| Lookup-heavy workloads | Often a good choice | Also efficient |
| Frequent updates | Can need more rebalancing | Often a good choice |

### Which One Is Better?

There is no universally best tree.

Choose an AVL Tree when:

- Lookup operations dominate.
- A tighter height bound is valuable.
- Insertions and deletions are relatively less frequent.

Choose a Red-Black Tree when:

- Insertions and deletions are frequent.
- A good balance between lookup and update performance is needed.
- You need a general-purpose ordered collection.

---

## 13. Java TreeMap and TreeSet Internals

Java provides built-in ordered collections:

- `TreeMap`
- `TreeSet`

In the standard OpenJDK implementation, both are backed by a Red-Black Tree.

### TreeMap Example

```java
import java.util.TreeMap;

public class Main {

    public static void main(String[] args) {

        TreeMap<Integer, String> map = new TreeMap<>();

        map.put(30, "Thirty");
        map.put(10, "Ten");
        map.put(20, "Twenty");

        System.out.println(map);

        System.out.println(map.get(20));

        System.out.println(map.firstKey());

        System.out.println(map.lastKey());
    }
}
```

Output:

```text
{10=Ten, 20=Twenty, 30=Thirty}

Twenty

10

30
```

### TreeSet Example

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

### TreeMap Complexity

| Operation | Complexity |
|---|---|
| put() | O(log n) |
| get() | O(log n) |
| remove() | O(log n) |
| containsKey() | O(log n) |
| firstKey() | O(log n) |
| lastKey() | O(log n) |
| higherKey() | O(log n) |
| lowerKey() | O(log n) |
| Iteration | O(n) |

**Important Java detail:** TreeMap uses key ordering, either natural ordering or a supplied Comparator. Two keys for which `compare(a, b) == 0` are treated as the same map key, even if `a.equals(b)` is false.

---

## 14. Time and Space Complexity

Let N be the number of nodes.

| Operation | Time Complexity |
|---|---|
| Search | O(log n) |
| Insertion | O(log n) |
| Deletion | O(log n) |
| Find Minimum | O(log n) |
| Find Maximum | O(log n) |
| Inorder Traversal | O(n) |
| Left Rotation | O(1) |
| Right Rotation | O(1) |
| Recoloring | O(1) per node |

### Space Complexity

| Operation | Space Complexity |
|---|---|
| Node storage | O(n) |
| Iterative Search | O(1) |
| Recursive Search | O(log n) |
| Recursive Inorder Traversal | O(log n) |
| Rotation | O(1) |

The recursive traversal stack is O(log n) because the Red-Black Tree height is O(log n).

---

## 15. Red-Black Tree Applications

Red-Black Trees are used where ordered data needs efficient searching and updating.

Examples:

1. **Java TreeMap:** Ordered key-value storage.
2. **Java TreeSet:** Sorted unique values.
3. **Operating systems:** Some kernel implementations use Red-Black Trees for managing ordered structures.
4. **In-memory indexes:** Maintaining sorted data with efficient updates.
5. **Scheduling systems:** Managing events or tasks ordered by time or priority.

They are particularly useful when an application needs predictable logarithmic search, insertion, and deletion.

---

## 16. Common Mistakes

1. Assuming every newly inserted node is black.
   - New nodes are initially red.

2. Forgetting that the root must be black.
   - The root is recolored black after insertion fix-up.

3. Assuming NIL leaves are red.
   - NIL leaves are always black.

4. Performing rotations without updating parent references.
   - Rotations must correctly update all affected links and the root.

5. Forgetting the uncle during insertion.
   - A red uncle usually requires recoloring.

6. Treating every Red-Red violation as a rotation case.
   - If the uncle is red, recoloring is performed first.

7. Confusing AVL and Red-Black balancing.
   - AVL uses heights/balance factors; Red-Black uses colors.

8. Assuming Red-Black Trees are complete binary trees.
   - They are balanced BSTs, not necessarily complete.

9. Ignoring deletion fix-up.
   - Removing a black node can violate black height.

10. Assuming Java TreeMap uses AVL.
    - Standard OpenJDK TreeMap uses a Red-Black Tree.

---

## 17. Frequently Asked Interview Questions

### Q1. What is a Red-Black Tree?

A Red-Black Tree is a self-balancing Binary Search Tree in which each node is colored red or black and follows five balancing properties.

### Q2. Why is it called a Red-Black Tree?

Because every node is assigned either a red or black color.

### Q3. What are the five properties?

1. Every node is red or black.
2. Root is black.
3. NIL leaves are black.
4. A red node cannot have a red child.
5. Every path from a node to descendant NIL leaves has the same black height.

### Q4. Why are new nodes inserted as red?

Inserting a red node does not immediately change the black height of paths. It can still create a Red-Red violation, which the fix-up procedure resolves.

### Q5. What is black height?

The number of black nodes along a path to a descendant NIL leaf, using a consistent counting convention.

### Q6. What happens when a red node is deleted?

If deleting it does not remove a black node from a path, the black-height property remains unchanged and no deletion fix-up is needed.

### Q7. What happens when a black node is deleted?

The black-height property may be violated. The deletion fix-up procedure restores it through recoloring and rotations.

### Q8. What is the maximum height of a Red-Black Tree?

The height is bounded by:

```text
h <= 2 * log2(n + 1)
```

### Q9. What is the time complexity of searching?

O(log n) in the worst case.

### Q10. What is the difference between AVL and Red-Black Trees?

AVL Trees use strict height balancing, whereas Red-Black Trees use node colors and black-height rules.

### Q11. Which tree is better for frequent insertion and deletion?

Red-Black Trees are often preferred because their balancing strategy tends to reduce rebalancing work, although actual performance depends on the workload.

### Q12. Does TreeMap use a Red-Black Tree?

Yes, the standard OpenJDK implementation of TreeMap uses a Red-Black Tree.

### Q13. Does HashMap use a Red-Black Tree?

Java HashMap can convert heavily-collided buckets into tree bins using a balanced tree implementation in supported circumstances. It is not itself a Red-Black Tree; its primary structure is a hash table.

### Q14. Can a Red-Black Tree become unbalanced?

It may temporarily violate its rules during insertion or deletion, but the fix-up procedure restores the properties before the operation completes.

### Q15. How many rotations are required during insertion?

At most two rotations are required in standard Red-Black Tree insertion.

### Q16. How many rotations are required during deletion?

At most three rotations are required in the standard Red-Black Tree deletion algorithm.

### Q17. Are all Red-Black Trees AVL Trees?

No. AVL Trees have a stricter height-balance condition. A Red-Black Tree does not necessarily satisfy the AVL balance-factor condition.

---

## 18. Practice Problems

### Beginner

1. Explain all five Red-Black Tree properties.
2. Identify the root, parent, grandparent, uncle, and sibling in a given tree.
3. Determine whether a given colored BST is a valid Red-Black Tree.
4. Explain why NIL leaves are black.
5. Insert three values that produce a Right-Right violation.
6. Perform a left rotation on a given tree.
7. Perform a right rotation on a given tree.

### Intermediate

1. Implement Red-Black Tree insertion in Java.
2. Implement searching.
3. Implement inorder traversal with node colors.
4. Implement left and right rotations.
5. Implement an insertion fix-up procedure.
6. Write a validator for the five Red-Black properties.
7. Compare the height of an AVL Tree and a Red-Black Tree for the same insertion sequence.

### Advanced

1. Implement Red-Black Tree deletion.
2. Implement deletion fix-up cases.
3. Implement a generic Red-Black Tree supporting custom comparators.
4. Implement TreeMap-like floor, ceiling, higher, and lower operations.
5. Compare AVL and Red-Black Trees using randomized insertion and deletion workloads.
6. Study the OpenJDK TreeMap implementation.
7. Explain how HashMap tree bins differ from TreeMap.

---

## 19. Quick Revision Cheat Sheet

| Concept | Key Point |
|---|---|
| Red-Black Tree | Self-balancing BST |
| Colors | Red and Black |
| Root | Always Black |
| NIL Leaves | Always Black |
| Red-Red | Not allowed |
| Black Height | Equal on all paths from a node to descendant NIL leaves |
| New Node | Initially Red |
| Balancing | Recoloring + Rotations |
| Search | O(log n) |
| Insert | O(log n) |
| Delete | O(log n) |
| Maximum Height | At most 2 log2(n + 1) |
| Left Rotation | Basic right-heavy restructuring |
| Right Rotation | Basic left-heavy restructuring |
| Java TreeMap | Red-Black Tree in OpenJDK |
| Java TreeSet | Red-Black Tree in OpenJDK |

### AVL vs Red-Black: Remember This

```text
AVL Tree:
- Uses height/balance factor.
- More strictly balanced.
- Often attractive for lookup-heavy workloads.

Red-Black Tree:
- Uses node colors.
- Less strictly balanced.
- Often attractive for mixed lookup/update workloads.
- Used by OpenJDK TreeMap and TreeSet.
```

---

## 20. Recommended Learning Roadmap

After understanding Red-Black Trees, continue with:

1. AVL Tree vs Red-Black Tree implementation comparison
2. B-Tree
3. B+ Tree
4. Trie
5. Segment Tree
6. Fenwick Tree (Binary Indexed Tree)

The next recommended topic is **B-Tree**, which introduces multiway balanced search trees and explains why databases use them for efficient disk-based indexing.