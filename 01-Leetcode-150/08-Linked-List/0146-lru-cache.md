---
title: "0146 — LRU Cache"
layout: default
parent: Linked List
grand_parent: Leetcode
nav_order: 146
permalink: /08-Linked-List/0146-lru-cache/

problem: 146 - LRU Cache
url: https://leetcode.com/problems/lru-cache/
difficulty: Medium
section: Design / Linked List
patterns: [hashmap, doubly-linked-list, dummy-head-tail]
data_structures: [hashmap, doubly-linked-list]
time: O(1)
space: O(capacity)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0146 — LRU Cache

| Field | Value |
|---|---|
| **Pattern** | Hashmap + Doubly Linked List (with Dummy Head & Tail) |
| **Difficulty** | Medium |
| **Section** | Design / Linked List |
| **Time** | O(1) — get and put |
| **Space** | O(capacity) |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Meta |
| **LeetCode** | [LRU Cache](https://leetcode.com/problems/lru-cache/) |

## 1. Problem in Simple Words

**Given:** a requirement to design a key-value cache with a fixed `capacity`, implementing `get(key)` and `put(key, value)`.

**Required:** `get(key)` returns the value for `key` if present (or `-1` if not) and counts as "using" that key, marking it as most recently used. `put(key, value)` inserts or updates a key-value pair, also marking it most recently used; if this causes the cache to exceed `capacity`, the **least recently used** key must be evicted. **Both operations must run in O(1) average time.**

**Constraints that matter:**
- `1 <= capacity <= 3000` and up to `2 * 10^5` total calls to `get`/`put` — the explicit O(1) requirement combined with a high call volume rules out any approach where a single operation might need to scan the whole cache (which could be up to 3000 entries) — that would make a large fraction of up to 200,000 calls each potentially O(capacity), totaling up to ~6×10^8 operations, likely too slow.
- **"The functions get and put must each run in O(1) average time complexity"** — this is the core design challenge: achieving O(1) lookup by key (which immediately suggests a hash map) **while simultaneously** maintaining and updating "recency" ordering in O(1) as well (which a plain hash map alone cannot do, since it has no inherent ordering, and most ordered structures like a sorted list or a simple array-based list cannot reorder in O(1)).
- `get` and `put` **both** count as "using" a key — this means recency tracking must be updated on every single operation, not just on insertion, making the "move this entry to the most-recently-used position" operation one that needs to happen frequently and cheaply.

## 2. Recognition Signals

- "O(1) key lookup" → immediately signals a **hash map**.
- "O(1) **access-order** tracking, with the ability to instantly identify and remove the least recently used item" → this is the harder half of the problem, and it specifically rules out: a sorted structure (reordering costs more than O(1) in general), an `ArrayList` (removing from the middle is O(n)), and a simple counter/timestamp approach combined with scanning for the minimum (finding the minimum is O(n) unless using an additional ordered structure).
- The combination of "O(1) lookup by key" **and** "O(1) move-to-front / remove-from-anywhere by reference" is the precise signature of needing a **hash map of key → node, paired with a doubly linked list** — the hash map gives direct O(1) access to any node by key, and the doubly linked list's structure allows O(1) removal and O(1) insertion at either end **given a direct reference to the node** (which the hash map just supplied).
- Named pattern: **HashMap + Doubly Linked List (the canonical LRU Cache construction)**.

## 3. Core Idea

Maintain a hash map from key to a doubly linked list node (so any key's node can be found in O(1)), and a doubly linked list ordered by recency, with the most-recently-used node at one end (say, right after a `head` sentinel) and the least-recently-used node at the other end (right before a `tail` sentinel). Every time a key is accessed (via `get` or `put`), its corresponding node is removed from its current position in the list and re-inserted immediately after the `head` sentinel, in O(1), using the direct node reference the hash map provided — no searching required. When `put` causes the cache to exceed capacity, the node just before the `tail` sentinel (the true least-recently-used node) is evicted in O(1), again because it's always directly accessible via the sentinel.

**Why it's correct:** the hash map's role is purely "given a key, find the corresponding node instantly" — it never needs to know about ordering. The doubly linked list's role is purely "maintain and expose recency order" — because every node has both a `prev` and `next` reference, it can be unlinked from (and relinked into) any position in O(1) *given a direct reference to it*, without needing to traverse the list to find it. Combining the two means every operation that needs both "find by key" and "reposition by recency" can do the lookup in O(1) via the map and the reposition in O(1) via the list, achieving O(1) for the combined operation. Using sentinel `head`/`tail` dummy nodes (rather than tracking `null`-terminated ends directly) eliminates all edge-case branching for "is this the first/last real node" during insertion and removal.

**Invariant:** at all times, the doubly linked list, read from immediately after `head` to immediately before `tail`, lists every key currently in the cache in strict order from most recently used to least recently used, and the hash map always maps every key currently in the cache to the exact node object representing it in that list — so the map and the list are always perfectly synchronized views of the same underlying set of entries.

## 4. Approach 1 — Brute Force (HashMap + Linear Scan for Eviction)

1. Maintain a `HashMap<Integer, Integer>` for key-value storage, and a separate `HashMap<Integer, Integer>` (or similar) tracking a "last used" timestamp/counter for each key, incremented on every access.
2. `get(key)`: O(1) lookup in the value map; if found, update its timestamp to the current counter value (also O(1)).
3. `put(key, value)`: O(1) insert/update in the value map and timestamp map. If capacity is now exceeded, **scan all entries** to find the one with the smallest (oldest) timestamp, and remove it.

```java
class LRUCacheBruteForce {
    private final int capacity;
    private final Map<Integer, Integer> values = new HashMap<>();
    private final Map<Integer, Integer> timestamps = new HashMap<>();
    private int clock = 0;

    public LRUCacheBruteForce(int capacity) {
        this.capacity = capacity;
    }

    public int get(int key) {
        if (!values.containsKey(key)) {
            return -1;
        }
        timestamps.put(key, clock++);
        return values.get(key);
    }

    public void put(int key, int value) {
        values.put(key, value);
        timestamps.put(key, clock++);

        if (values.size() > capacity) {
            int oldestKey = -1;
            int oldestTime = Integer.MAX_VALUE;
            for (Map.Entry<Integer, Integer> entry : timestamps.entrySet()) {
                if (entry.getValue() < oldestTime) {
                    oldestTime = entry.getValue();
                    oldestKey = entry.getKey();
                }
            }
            values.remove(oldestKey);
            timestamps.remove(oldestKey);
        }
    }
}
```

**Time:** `get` is O(1); `put` is O(1) in the common case, but **O(capacity)** whenever eviction is triggered, since finding the minimum timestamp requires scanning every entry.
**Space:** O(capacity) for the two maps.

This is not optimal: it violates the problem's explicit O(1)-for-both-operations requirement, since `put` can degrade to O(capacity) whenever an eviction occurs — with capacity up to 3000 and up to 200,000 calls, a significant fraction of which could trigger evictions once the cache fills up, this could add up to hundreds of millions of comparison operations, failing to meet the stated performance bar.

## 5. Approach 2 — Optimized (HashMap + Doubly Linked List with Sentinels)

1. Define a `Node` class holding `key`, `value`, `prev`, and `next` (the `key` is stored on the node too, specifically so that when evicting the LRU node, its key can be removed from the hash map in O(1) without needing a reverse lookup).
2. Maintain `Map<Integer, Node> cache`, and two sentinel nodes, `head` and `tail`, permanently linked to each other initially (`head.next = tail`, `tail.prev = head`) and never removed — real entries always live strictly between them, ordered most-recently-used (right after `head`) to least-recently-used (right before `tail`).
3. Helper `remove(node)`: unlink `node` from its current position by relinking its neighbors to each other directly — O(1).
4. Helper `insertAtFront(node)`: insert `node` immediately after `head` (the most-recently-used position) — O(1).
5. `get(key)`: if `key` isn't in `cache`, return `-1`. Otherwise, retrieve the node, `remove` it, `insertAtFront` it (marking it most recently used), and return its value.
6. `put(key, value)`: if `key` already exists, update its node's value, then `remove` and `insertAtFront` it. Otherwise, create a new node, add it to `cache`, `insertAtFront` it, and if `cache.size() > capacity`, evict the least-recently-used node (the one right before `tail`) by removing it from both the list and the map.

```java
class LRUCache {
    private class Node {
        int key, value;
        Node prev, next;
        Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final Map<Integer, Node> cache = new HashMap<>();
    private final Node head = new Node(0, 0);
    private final Node tail = new Node(0, 0);

    public LRUCache(int capacity) {
        this.capacity = capacity;
        head.next = tail;
        tail.prev = head;
    }

    private void remove(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void insertAtFront(Node node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    public int get(int key) {
        if (!cache.containsKey(key)) {
            return -1;
        }
        Node node = cache.get(key);
        remove(node);
        insertAtFront(node);
        return node.value;
    }

    public void put(int key, int value) {
        if (cache.containsKey(key)) {
            Node node = cache.get(key);
            node.value = value;
            remove(node);
            insertAtFront(node);
        } else {
            Node node = new Node(key, value);
            cache.put(key, node);
            insertAtFront(node);

            if (cache.size() > capacity) {
                Node lru = tail.prev;
                remove(lru);
                cache.remove(lru.key);
            }
        }
    }
}
```

**Time:** O(1) for both `get` and `put`, in every case — map lookup/insert/remove is O(1) average, and all list operations (`remove`, `insertAtFront`) are O(1) regardless of list size, since they only ever touch a node's immediate neighbors, never requiring a traversal.
**Space:** O(capacity) — the map and the list together hold at most `capacity` real entries at any time, plus the two fixed sentinel nodes.

This is optimal: it achieves true O(1) for both operations in **every** case, including eviction, directly satisfying the problem's explicit requirement — unlike the brute-force timestamp-scan approach, there is no operation here that can degrade based on how full the cache is.

## 6. Dry Run

`capacity = 2`; operations: `put(1,1)`, `put(2,2)`, `get(1)`, `put(3,3)`, `get(2)`

| operation | list order (MRU → LRU, excluding sentinels) | map keys | return value |
|---|---|---|---|
| put(1,1) | [1] | {1} | — |
| put(2,2) | [2,1] | {1,2} | — |
| get(1) | [1,2] (1 moved to front) | {1,2} | `1` |
| put(3,3) | cache full (size 2, capacity 2); evict LRU (`2`, at tail.prev); insert 3 at front → [3,1] | {1,3} | — |
| get(2) | unchanged, 2 not found | {1,3} | `-1` |

Exit: matches the problem's own worked example exactly — `get(1)` returns `1`, and after `put(3,3)` evicts key `2` (the least recently used at that point), `get(2)` correctly returns `-1`.

## 7. Complexity Summary

| Approach | Time (get/put) | Space | Notes |
|---|---|---|---|
| HashMap + Timestamp, Linear Scan Eviction | O(1) / O(1) amortized but O(capacity) worst-case on eviction | O(capacity) | Violates the strict O(1)-always requirement on eviction |
| HashMap + Doubly Linked List with Sentinels | O(1) / O(1), always | O(capacity) | True O(1) for every call, including eviction |

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|---|---|---|
| `get` on a key never inserted | Must return `-1` without side effects | `cache.containsKey(key)` check returns `-1` immediately, no list modification occurs |
| `put` updating an existing key's value | Must update the value *and* mark it most recently used, without changing cache size | The existing-key branch updates `node.value`, then performs `remove`+`insertAtFront` (a reposition, not a size change) |
| Cache at exactly capacity, then one more `put` with a new key | Must evict exactly one entry (the true LRU) | `cache.size() > capacity` check fires only after the new node is already inserted, ensuring the comparison correctly reflects the post-insertion size before deciding whether to evict |
| `capacity = 1` | Every `put` of a new key evicts the previous one immediately | Works identically to larger capacities — the single real entry is always both the MRU and LRU simultaneously, and inserting a second key immediately triggers eviction of the first |
| Repeated `get` calls on the same key in a row | Each call should still correctly mark it most recently used, even though it's already at the front | `remove` followed by `insertAtFront` works correctly even when the node is already at the front — removing it from (already) the front position and reinserting there is a harmless no-op in terms of final ordering |
| Using sentinel `head`/`tail` nodes rather than null-checking real list ends | Avoids null-pointer edge cases for empty cache or single-entry cache | Since `head` and `tail` always exist and are always linked (directly to each other when empty, or through real nodes otherwise), `remove` and `insertAtFront` never need to check for `null` neighbors — there's always a valid `prev`/`next` to relink, even in the empty-cache case |

## 9. Java Notes

- Storing the `key` on each `Node` (not just the `value`) is a deliberate design choice specifically to support O(1) eviction — when evicting the LRU node (found via `tail.prev`), its key is needed to remove the corresponding entry from the `cache` map, and without storing it on the node, there'd be no O(1) way to know which map entry to remove.
- The sentinel `head`/`tail` technique (two permanent dummy nodes bookending the real list) is what eliminates null-checks during `remove`/`insertAtFront` — every real node always has a valid non-null `prev` and `next`, even at the very ends of the list, since those ends are the sentinels themselves rather than `null`.
- `HashMap<Integer, Node>` stores direct object references to list nodes — this is what makes O(1) "find and reposition by key" possible: the map doesn't need to know anything about list order, it just needs to hand back the exact node object, which already carries its own `prev`/`next` pointers for direct relinking.
- Implementing `LRUCache` as a private inner/nested `Node` class is standard for this kind of self-contained data-structure design problem — it keeps the node definition scoped to where it's used, since `Node` has no meaning outside the context of this particular cache implementation.

## 10. Common Mistakes

- Using a single `HashMap<Integer, Integer>` plus a `LinkedList`/`ArrayList` of keys to track order, and calling `list.remove(key)` (which internally searches for and removes by value, an O(n) operation for most list implementations) to reposition a key on every access — this silently reintroduces O(n) behavior into what should be an O(1) operation, since `remove(Object)` on a `List` must scan to find the matching element. Fix: use direct node references from the hash map (as in the optimal solution), never a value-based search through a list.
- Forgetting to store the `key` on each `Node`, then being unable to remove the correct entry from the `cache` map when evicting the LRU node (since only the node's `value`, not its `key`, would otherwise be known at eviction time). Fix: always store the key alongside the value on each node specifically to support this.
- Checking `cache.size() > capacity` *before* inserting the new node (rather than after) when deciding whether to evict in `put` — this can incorrectly evict even when the cache wasn't actually over capacity yet, or fail to evict when it just became over capacity, depending on the exact off-by-one framing. Fix: always insert first, then check the post-insertion size against capacity to decide whether an eviction is needed.
- Forgetting to update an existing key's position (only updating its value) in `put` when the key already exists — this fails to mark the key as recently used, incorrectly leaving it in its old position in the recency ordering, which could cause it to be evicted prematurely even though it was just accessed. Fix: always perform the same `remove` + `insertAtFront` reposition for an existing key's update, not just a value overwrite.

## 11. Interview Takeaway

- **Trigger sentence:** "Need O(1) lookup by key *and* O(1) tracking/updating of access-recency order → combine a hash map (key → node) with a doubly linked list (maintaining recency order), using sentinel head/tail nodes to avoid null-check edge cases."
- **90-second explanation:** "The challenge is that I need two different things to both be O(1): finding an entry by its key, and knowing (and updating) which entry was least recently used. A hash map alone gives me fast lookup but no ordering information. A doubly linked list alone gives me ordering and O(1) removal from anywhere, but only if I already have a direct reference to the node — otherwise finding it requires a traversal. So I combine them: the hash map stores key to node references, letting me jump straight to any node in O(1), and the doubly linked list maintains strict recency order, with the most recently used entry always right after a `head` sentinel and the least recently used always right before a `tail` sentinel. Every `get` or `put` on an existing key unlinks that node from wherever it currently sits and relinks it right after `head` — both O(1) operations since I already have the node reference from the map. When the cache overflows, I don't need to search for what to evict — it's always exactly the node just before `tail`. The sentinel nodes mean I never have to special-case an empty list or a list with just one real entry."
- **Related problems:** 460 (LFU Cache, a harder variant tracking frequency as well as recency), 432 (All O`one Data Structure, similar hash map + doubly linked list combination idea), 707 (Design Linked List, foundational doubly linked list mechanics).

## 12. Recall Questions

**Q:** Why can't a hash map alone achieve the O(1) requirement for this problem, even though key lookup itself is O(1) in a hash map?
**A:** A hash map has no inherent concept of insertion or access order, so while it can tell you a value in O(1), it provides no efficient way to determine which entry is the least recently used when an eviction is needed — that requires a separate ordering structure.

**Q:** Why is it essential that each list node stores its own `key`, not just its `value`?
**A:** When evicting the least-recently-used node, the algorithm needs to remove the corresponding entry from the hash map as well, which requires knowing that node's key; without storing it directly on the node, there'd be no O(1) way to recover which map entry corresponds to the node being evicted.

**Q:** Why do the sentinel `head` and `tail` nodes eliminate the need for null-checks in the `remove` and `insertAtFront` helper methods?
**A:** Because `head` and `tail` are permanent, always-present nodes that remain linked to each other (directly, when the cache is empty, or through real nodes otherwise), every real node always has genuinely non-null `prev` and `next` references to work with, removing the need to special-case "this node is at the very start/end of the list" during relinking.

**Q:** Why must the recency reposition (`remove` + `insertAtFront`) happen on every `get` call, not just on `put`?
**A:** The problem explicitly defines both `get` and `put` as counting toward a key's "recently used" status, so a `get` that doesn't reposition its node would incorrectly leave that key vulnerable to eviction even though it was just accessed, violating the LRU policy's intended behavior.

**Q:** What specifically makes the timestamp-and-linear-scan brute force approach fail to meet the problem's requirements, given that it's still technically O(1) for most individual calls?
**A:** The requirement is O(1) for *every* call, not just on average across calls that don't trigger eviction — the brute force approach's eviction step requires scanning every entry to find the minimum timestamp, which is O(capacity), and this can occur on a significant fraction of `put` calls once the cache is full, violating the strict per-call O(1) guarantee the problem demands.

## 13. Final Code

```java
import java.util.*;

class LRUCache {
    private class Node {
        int key, value;
        Node prev, next;
        Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final Map<Integer, Node> cache = new HashMap<>();
    private final Node head = new Node(0, 0);
    private final Node tail = new Node(0, 0);

    public LRUCache(int capacity) {
        this.capacity = capacity;
        head.next = tail;
        tail.prev = head;
    }

    private void remove(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void insertAtFront(Node node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    public int get(int key) {
        if (!cache.containsKey(key)) {
            return -1;
        }
        Node node = cache.get(key);
        remove(node);
        insertAtFront(node);
        return node.value;
    }

    public void put(int key, int value) {
        if (cache.containsKey(key)) {
            Node node = cache.get(key);
            node.value = value;
            remove(node);
            insertAtFront(node);
        } else {
            Node node = new Node(key, value);
            cache.put(key, node);
            insertAtFront(node);

            if (cache.size() > capacity) {
                Node lru = tail.prev;
                remove(lru);
                cache.remove(lru.key);
            }
        }
    }
}
```

## 14. Self-Test

Design a fixed-capacity cache supporting `get(key)` and `put(key, value)` in O(1) average time, evicting the least recently used entry when the capacity is exceeded, where both operations count as "use." Re-derive: combine a hash map (key → node) with a doubly linked list ordered by recency, using `head`/`tail` sentinel nodes; on every `get` or `put` of an existing key, unlink the node and reinsert it right after `head`; on inserting a new key that overflows capacity, evict the node just before `tail`, removing it from both the list and the map.
