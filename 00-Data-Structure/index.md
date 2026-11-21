---
title: Data Structures & Algorithms
layout: default
nav_order: 2
has_children: true
permalink: /data-structures/
---

# Data Structures & Algorithms

**Build the foundations. Understand the trade-offs. Implement them in Java.**

This section covers core data structures, searching and sorting algorithms, and fundamental problem-solving techniques. It is designed to support the [LeetCode 150 practice track](../leetcode/).

## Learning roadmap

| Order | Topic | Key concepts |
|---:|---|---|
| 01 | [Big-O Notation](Big-O-Notation/big-o-notation/) | Time and space complexity, best/average/worst cases |
| 02 | [Dynamic Arrays](Dynamic-Arrays/dynamic-arrays/) | Indexing, resizing, amortized complexity |
| 03 | [Linked Lists](Linked-List/linked-list/) | Singly and doubly linked lists, pointer operations |
| 04 | [Stack](Stack/Stack/) | LIFO, push, pop, peek and applications |
| 05 | [Queue](Queue/Queue/) | FIFO, enqueue, dequeue and queue variants |
| 06 | [Hash Map](Hash-Map/hash-map/) | Hashing, `hashCode()`, `equals()`, collisions and resizing |
| 07 | [Tree](Tree/tree/) | Nodes, edges, height, depth and terminology |
| 08 | [Binary Tree](Tree/Binary-Tree/binary-tree/) | Binary tree properties and representations |
| 09 | [Binary Search Tree](Tree/Binary-Search-Tree/binary-search-tree/) | Ordering invariant, search, insertion and deletion |
| 10 | [Tree Traversal](Tree/Tree-Traversal/tree-traversal/) | Preorder, inorder, postorder and level order |
| 11 | [AVL Tree](Tree/AVL-Tree/avl-tree/) | Self-balancing trees and rotations |
| 12 | [Red-Black Tree](Tree/Red-Black-Tree/red-black-tree/) | Balancing properties, rotations and recoloring |
| 13 | [B-Tree](Tree/B-Tree/b-tree/) | Multiway search trees and database/storage use cases |
| 14 | [Binary Heap](Binary-Heap/binary-heap/) | Min-heap, max-heap and heap operations |
| 15 | [Priority Queue](Priority-Queue/priority-queue/) | Priority-based retrieval and heap-backed implementation |
| 16 | [Graph](Graph/graph/) | Vertices, edges, directed, undirected and weighted graphs |
| 17 | [Breadth-First Search](Graph/BFS/bfs/) | Queue-based traversal and unweighted shortest paths |
| 18 | [Depth-First Search](Graph/DFS/dfs/) | Recursive and iterative traversal, visited tracking |

## Searching algorithms

| Algorithm | Explanation | Time complexity |
|---|---|---:|
| [Linear Search](Linear-Search/linear-search/) | Check elements sequentially | \(O(n)\) |
| [Binary Search](Binary-Search/binary-search/) | Halve a sorted search range repeatedly | \(O(\log n)\) |

## Sorting algorithms

| Algorithm | Main idea | Best | Average | Worst |
|---|---|---:|---:|---:|
| [Bubble Sort](Bubble-Sort/bubble.sort/) | Swap adjacent out-of-order elements | \(O(n)\)* | \(O(n^2)\) | \(O(n^2)\) |
| [Selection Sort](Selection-Sort/selection-sort/) | Select the minimum remaining element | \(O(n^2)\) | \(O(n^2)\) | \(O(n^2)\) |
| [Insertion Sort](Insertion-Sort/insertion-sort/) | Insert into the sorted prefix | \(O(n)\) | \(O(n^2)\) | \(O(n^2)\) |
| [Merge Sort](Merge-Sort/merge-sort/) | Divide, sort and merge | \(O(n\log n)\) | \(O(n\log n)\) | \(O(n\log n)\) |
| [Quick Sort](Quick-Sort/quick-sort/) | Partition around a pivot | \(O(n\log n)\) | \(O(n\log n)\) | \(O(n^2)\) |
| [Heap Sort](Heap-Sort/heap-sort/) | Build a heap and repeatedly extract its root | \(O(n\log n)\) | \(O(n\log n)\) | \(O(n\log n)\) |

*Bubble Sort has \(O(n)\) best-case time when implemented with an early-exit check.

## Algorithmic foundations

| Topic | Key concepts |
|---|---|
| [Recursion](Recursion/recursion/) | Base case, recursive case, call stack and recurrence |
| [Big-O Notation](Big-O-Notation/big-o-notation/) | Growth rates and asymptotic analysis |

## Quick complexity reference

| Structure / operation | Typical time complexity |
|---|---:|
| Array access by index | \(O(1)\) |
| Dynamic array append | \(O(1)\) amortized |
| Dynamic array insertion/deletion in the middle | \(O(n)\) |
| Linked-list search | \(O(n)\) |
| Linked-list insertion at a known node | \(O(1)\) |
| Stack push / pop | \(O(1)\) |
| Queue enqueue / dequeue | \(O(1)\) |
| Hash map get / put / remove | \(O(1)\) average; \(O(n)\) worst case |
| Balanced BST search / insert / delete | \(O(\log n)\) |
| Heap peek | \(O(1)\) |
| Heap insertion / root removal | \(O(\log n)\) |
| BFS / DFS with adjacency lists | \(O(V + E)\) |

These are typical complexities and depend on the implementation and assumptions. Hash map operations, for example, are average-case constant time rather than an unconditional guarantee.

## What you should be able to explain

For every data structure or algorithm, understand:

1. How it is represented in memory.
2. Its core invariant and how operations preserve it.
3. The time and auxiliary-space complexity of its operations.
4. Edge cases: empty input, one element, duplicates and boundary values.
5. Iterative versus recursive implementations and their trade-offs.
6. How Java's Collections Framework relates to the concept.

## Continue practicing

Apply these concepts in the [LeetCode 150 problem set](../leetcode/), then study [Design Patterns](../design-patterns/) for object-oriented design interviews.