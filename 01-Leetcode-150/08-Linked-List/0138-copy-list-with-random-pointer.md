---
title: "0138 — Copy List with Random Pointer"
layout: default
parent: Linked List
grand_parent: Leetcode
nav_order: 138
permalink: /08-Linked-List/0138-copy-list-with-random-pointer/

problem: 138 - Copy List with Random Pointer
url: https://leetcode.com/problems/copy-list-with-random-pointer/
difficulty: Medium
section: Linked List / Hash Map
patterns: [interweaving-nodes, old-to-new-mapping]
data_structures: [linked-list, hashmap]
time: O(n)
space: O(1)
solved_unaided: false
confidence: 0
last_reviewed:
next_review:
tags: [leetcode, top-interview-150, java]
---

# 0138 — Copy List with Random Pointer

| Field | Value |
|---|---|
| **Pattern** | Interweaving Nodes + Old-to-New Mapping |
| **Difficulty** | Medium |
| **Section** | Linked List / Hash Map |
| **Time** | O(n) |
| **Space** | O(1) — with interweaving approach; O(n) with hashmap |
| **Tags** | leetcode · top-interview-150 · java |
| **Companies** | Google, Amazon, Meta |
| **LeetCode** | [Copy List with Random Pointer](https://leetcode.com/problems/copy-list-with-random-pointer/) |

## 1. Problem in Simple Words

**Given:** The head of a linked list where each node has a `val`, a `next` pointer, and an additional `random` pointer that can point to any node in the list (or `null`).

**Required:** Construct a complete deep copy of the list — `n` brand new nodes, with the copy's `next` and `random` pointers correctly mirroring the original list's structure, but pointing only to nodes within the *copied* list, never back into the original.

**Constraints that actually matter:**
- `0 <= n <= 1000` — moderate size; performance isn't the real challenge — the core difficulty is correctly handling the `random` pointer, which can point to **any** node in the list, including one that hasn't been created yet at the time you'd naturally want to set it (since a simple single forward pass would encounter a node's `random` target before or after processing that target node, with no guarantee of order).
- **`Node.random` can point to any node, forward or backward in the list, or to itself, or be `null`** — this is the entire crux of the problem: a `next`-only linked list copy would be trivial with a single forward pass, but the `random` pointer's arbitrary target (potentially a node not yet copied) means you need some way to correctly resolve "what is the *copy* of whatever node this `random` pointer targets," even when that target hasn't been visited yet in a straightforward single pass.
- The explicit requirement that **"none of the pointers in the new list should point to nodes in the original list"** rules out any shortcut involving directly reusing or referencing original node objects for the final result — every single node, and every single pointer, must reference only the newly created copies.

## 2. Recognition Signals

- "Deep copy a linked list where nodes have an extra pointer that can reference *any* node in the list" → the signature of needing an **old-node-to-new-node mapping**: since a node's `random` target might not have been created yet when that node is first processed, some mechanism is needed to either defer resolving `random` pointers until all nodes exist, or to look up "what's the copy of this specific original node" on demand.
- The most direct technique is a **`HashMap<Node, Node>`** mapping each original node to its corresponding newly created copy, built during a first pass, then used during a second pass to correctly wire up both `next` and `random` pointers by looking up the appropriate copies.
- A cleverer, O(1)-extra-space alternative (beyond the required output) is the **interweaving technique**: temporarily splice each copied node directly *after* its corresponding original node in the same list (so the list becomes `original1 -> copy1 -> original2 -> copy2 -> ...`), which makes "the copy of node X" always immediately accessible as `X.next` without needing any separate map — then, in a final pass, detangle the interwoven list back into two separate lists (the original, restored, and the copy).

**Pattern:** Old-to-New Node Mapping (via `HashMap`, the straightforward approach) — with Node Interweaving as the more advanced, O(1)-extra-space alternative (temporarily interleave copies directly into the original list structure to make lookups implicit via adjacency, then separate the two lists back apart).

## 3. Core Idea

The fundamental challenge is that when processing a node and wanting to set its *copy's* `random` pointer, the target of that `random` pointer might be a node whose copy doesn't exist yet (if it appears later in the list) or might already exist (if it appeared earlier) — there's no guaranteed order. Two ways to resolve this:

**HashMap approach:** First pass: walk the original list, creating a new (copy) node for every original node, and record the mapping `original -> copy` in a hash map (not yet setting `next`/`random` on the copies). Second pass: walk the original list again, and for each original node, use the map to set its copy's `next` to `map.get(original.next)` and its copy's `random` to `map.get(original.random)` (both lookups are guaranteed to succeed, including correctly resolving to `null` if the original pointer was `null`, by also storing a `null -> null` mapping or handling `null` as a special case before the lookup).

**Interweaving approach:** First pass: walk the original list once, and for each original node `X`, create its copy `X'` and splice it in directly after `X` (so `X.next` becomes `X'`, and `X'.next` becomes whatever `X.next` originally was). This means `X.next` always gives you `X'`, the copy of `X`, without needing any lookup structure. Second pass: walk the interwoven list again; for each original node `X` (now followed by its copy `X'`), set `X'.random = X.random.next` (since `X.random` is some original node `Y`, and `Y.next` is now `Y'`, exactly the copy we want) — this correctly resolves every `random` pointer using only adjacency, no map needed. Third pass: walk through once more, unweaving the interleaved structure back into two separate lists — restoring the original list's `next` pointers to skip over the copies, and simultaneously building the copy list's `next` pointers to skip over the originals.

**Invariant (interweaving):** After the first pass, for every original node `X`, `X.next` is always exactly `X`'s copy — this adjacency relationship is what the second pass exploits to resolve every `random` pointer in O(1) per node without any separate data structure, and the third pass carefully restores both lists to their correct independent structures using this same adjacency.

## 4. Approach 1 — HashMap-Based Old-to-New Mapping

**Logic:** Two passes. First, create every copy node and record the original-to-copy mapping. Second, use the mapping to correctly wire up both `next` and `random` pointers on the copies.

```java
public Node copyRandomList(Node head) {
    if (head == null) {
        return null;
    }

    Map<Node, Node> oldToNew = new HashMap<>();

    Node current = head;
    while (current != null) {
        oldToNew.put(current, new Node(current.val));
        current = current.next;
    }

    current = head;
    while (current != null) {
        Node copy = oldToNew.get(current);
        copy.next = oldToNew.get(current.next);
        copy.random = oldToNew.get(current.random);
        current = current.next;
    }

    return oldToNew.get(head);
}
```

(Note: `oldToNew.get(null)` correctly returns `null` by default `HashMap` behavior, since `null` was never added as a key, so this naturally and correctly handles `next`/`random` pointers that are `null` without needing an explicit special case.)

- **Time:** O(n) — two linear passes through the list, each doing O(1) average-case hash map operations per node.
- **Space:** O(n) auxiliary for the `HashMap`, storing all `n` original-to-copy mappings.

**Why it is not optimal:** This is simple, clear, and correctly solves the problem in O(n) time, but it uses O(n) auxiliary space for the hash map, which the more advanced interweaving technique avoids entirely (beyond the space inherently required for the output copy itself).

## 5. Approach 2 — Optimized (Node Interweaving, O(1) Auxiliary Space)

**Algorithm:**
1. **Pass 1 — interweave copies into the original list:** for each original node `current`, create its copy, and splice it directly after `current`: save `current.next` (the original next node) into a temp variable, set `current.next = copy`, set `copy.next = ` (the saved original next node), then advance `current` to that saved original next node (skipping over the copy you just inserted) to continue the interweaving.
2. **Pass 2 — resolve `random` pointers using adjacency:** for each original node `current` (now interwoven with its copy immediately after it), if `current.random != null`, set `current.next.random = current.random.next` (the copy's random should point to the *copy* of whatever the original's random pointed to, which is exactly one step past that original node in the now-interwoven list).
3. **Pass 3 — detangle into two separate lists:** walk through the interwoven list once more, restoring `current.next` to skip over the copy (pointing to the next *original* node) and simultaneously building the copy list's `next` pointers to skip over the originals (pointing to the next *copy* node).
4. Return the saved head of the copy list (which was `head.next` right after pass 1, before any detangling).

```java
public Node copyRandomList(Node head) {
    if (head == null) {
        return null;
    }

    // Pass 1: interweave copies into the original list.
    Node current = head;
    while (current != null) {
        Node copy = new Node(current.val);
        copy.next = current.next;
        current.next = copy;
        current = copy.next;
    }

    // Pass 2: resolve random pointers using adjacency.
    current = head;
    while (current != null) {
        if (current.random != null) {
            current.next.random = current.random.next;
        }
        current = current.next.next;
    }

    // Pass 3: detangle into two separate lists.
    Node newHead = head.next;
    current = head;
    Node copyCurrent = newHead;
    while (current != null) {
        current.next = copyCurrent.next;
        current = current.next;
        if (current != null) {
            copyCurrent.next = current.next;
            copyCurrent = copyCurrent.next;
        }
    }

    return newHead;
}
```

- **Time:** O(n) — three linear passes through the (interwoven, then detangled) list, each doing O(1) work per node.
- **Space:** O(1) auxiliary (excluding the required output copy nodes themselves) — no hash map or other data structure proportional to `n` is used; the technique instead exploits the list's own structure (temporarily) as the "lookup mechanism."

**Why this is optimal:** Every node and every `random` pointer must be examined at least once to correctly construct the copy, so O(n) time is a hard lower bound, matching both approaches. Space is O(1) auxiliary (beyond the unavoidable output) because the interweaving technique cleverly uses the adjacency of each original node and its freshly created copy as an implicit "lookup table" — `current.next` always gives you the copy of `current` during the relevant phase — eliminating the need for any separate hash map, a genuine space improvement over Approach 1.

## 6. Dry Run

Example: `head = [[7,null],[13,0],[11,4],[10,2],[1,0]]` (values with `random_index`; e.g., node 1 (value 13) has `random` pointing to node 0 (value 7)).
Chosen because it's the canonical example, and it's complex enough to meaningfully exercise all three passes of the interweaving technique (abbreviated here for conciseness, focusing on the key mechanics rather than every single pointer).

**After Pass 1 (interweaving):** The list becomes: `7 -> 7' -> 13 -> 13' -> 11 -> 11' -> 10 -> 10' -> 1 -> 1' -> null`, where each `X'` is the newly created copy of `X`, and `X.next` now points directly to `X'`.

**Pass 2 (resolving random pointers):** For node `13` (whose original `random` points to node `7`, index 0): `13.next.random = 13.random.next` → `13'.random = 7.next` → `13'.random = 7'` (correctly, the copy of node 7). This same logic correctly resolves every other node's copy's random pointer using only this adjacency relationship, without any lookup table.

**Pass 3 (detangling):** Walking through the interwoven list, `current.next` is repeatedly reset to skip the copy (restoring the original list: `7 -> 13 -> 11 -> 10 -> 1 -> null`), while `copyCurrent.next` is simultaneously built to skip the originals (forming the new copy list: `7' -> 13' -> 11' -> 10' -> 1' -> null`).

Exit condition: all three passes complete.

**Final answer:** The returned `newHead` (which is `7'`) leads a fully independent copy list `[[7,null],[13,0],[11,4],[10,2],[1,0]]`, matching the expected output exactly, with the original list also correctly restored to its original structure as a side effect of pass 3.

## 7. Complexity Summary

| Approach | Time | Space (Auxiliary) | Notes |
|----------|------|--------------------|-------|
| 1. HashMap-based mapping | O(n) | O(n) | Correct and simple; straightforward to reason about |
| 2. Node interweaving | O(n) | O(1) (excl. output) | Optimal; exploits list adjacency instead of a hash map |

Output space (the `n` newly created copy nodes) is required by both approaches and isn't counted as "extra" auxiliary space.

## 8. Edge Cases

| Case | Why it is tricky | How the code handles it |
|------|-------------------|--------------------------|
| Empty list (`head == null`) | Should return `null` immediately | Both approaches explicitly check `if (head == null) return null;` at the very start, before any list traversal is attempted |
| A node's `random` pointer is `null` | Must correctly produce a `null` random pointer on the corresponding copy, not error or default to some unintended value | HashMap approach: `oldToNew.get(null)` correctly returns `null` by default map behavior; Interweaving approach: the explicit `if (current.random != null)` guard in pass 2 skips setting the copy's random at all when the original is null, leaving it at its default `null` value (new nodes' `random` field defaults to `null` unless explicitly set) |
| A node's `random` pointer points to itself | Must correctly produce a copy whose random pointer points to *that same copy itself*, not back to the original | HashMap approach: `oldToNew.get(current)` (when `current.random == current`) correctly resolves to that same node's own copy; Interweaving approach: `current.next.random = current.random.next` when `current.random == current` becomes `current.next.random = current.next`, correctly pointing the copy to itself |
| Single-node list | Should produce a single-node copy, correctly handling whatever that one node's `random` pointer does (null, or pointing to itself) | Both approaches handle this as a trivial, smaller instance of the same general logic — no special-casing beyond what's already described for the self-referencing and null cases above |
| Multiple nodes sharing the same value (duplicate `val`s, as in example 3) | Node identity (object reference), not value, must be used to distinguish nodes for the mapping/interweaving logic | Both approaches operate purely on node object references throughout (the `HashMap<Node, Node>` keys by reference identity by default for object types without custom `equals`/`hashCode` overrides, and the interweaving technique relies on actual pointer/reference relationships, not values), so duplicate values never cause incorrect node conflation |
| Ensuring the final copy list shares absolutely no node references with the original list | The problem explicitly requires this; any accidental lingering reference to an original node in the final copy would be a correctness violation | Both approaches exclusively construct and link `new Node(...)` objects for the copy, and the interweaving approach's pass 3 carefully and fully restores the original list's `next` pointers to their correct original-only structure, ensuring no cross-contamination remains between the two now-fully-separated lists |

## 9. Java Notes

- **`HashMap<Node, Node>` keyed by object reference:** since `Node` doesn't override `equals`/`hashCode` (a typical assumption for this kind of problem, using default `Object` identity semantics), the map correctly distinguishes nodes by their actual object identity, not by their `val` field, which is essential given that multiple nodes can share the same value.
- **`HashMap.get` returning `null` for an absent key:** this is what makes `oldToNew.get(current.next)` (or `.get(current.random)`) correctly resolve to `null` without any special-case handling, since a `null` original pointer was simply never added as a key during the first pass.
- **In-place pointer rewiring during interweaving (no new auxiliary list structure beyond the copy nodes themselves):** directly manipulating `next` references to temporarily merge two conceptually separate lists into one interwoven structure, then carefully separating them back apart, is the core Java (and general) technique that enables this approach's O(1) auxiliary space.
- **Careful ordering of pointer updates during pass 3 (detangling):** since both the original and copy lists' `next` pointers are being updated in the same pass, care must be taken to read whatever pointer values are needed *before* they get overwritten by that same step — the specific ordering shown in the provided code (updating `current.next` before advancing `current`, then updating `copyCurrent.next` using the now-advanced `current`) is deliberately sequenced to avoid losing needed references prematurely.

## 10. Common Mistakes

- **Trying to set a node's `random` pointer directly during a single forward pass, before all nodes have been created.** If a node's `random` target appears later in the list, its copy wouldn't exist yet at the time you'd want to reference it, leading to either a null pointer issue or requiring an awkward placeholder/backfill mechanism. Fix: use either the two-pass HashMap approach or the interweaving technique, both of which defer or restructure the random-pointer resolution to avoid this ordering problem.
- **Forgetting to handle `null` `random` pointers explicitly in the interweaving approach**, attempting `current.random.next` without first checking `current.random != null`, which would throw a `NullPointerException`. Fix: always guard with `if (current.random != null)` before dereferencing it in pass 2.
- **Accidentally leaving the original list permanently altered (interwoven) by forgetting or incorrectly implementing pass 3 (detangling).** Since the interweaving approach fundamentally modifies the original list's structure as an intermediate step, failing to fully and correctly restore it afterward would leave the original list corrupted, which violates the implicit expectation that the original list's integrity (at least conceptually, and strictly per this problem's requirements about not sharing nodes) is preserved. Fix: ensure pass 3 fully restores every original node's `next` pointer to skip over its interwoven copy, for every node in the list.
- **Using the copy nodes' `val` field (or any value-based comparison) to try to match original and copy nodes, instead of relying on reference identity or direct adjacency.** Since multiple nodes can share the same `val`, this would be unreliable and could easily produce incorrect mappings. Fix: always rely on actual object references (for the HashMap approach) or direct structural adjacency (for the interweaving approach), never on comparing `val` fields to establish correspondence.

## 11. Interview Takeaway

- **Trigger sentence to memorize:** "Deep copy a linked list with an extra pointer that can reference any node → map each original node to its copy (via a HashMap, or more cleverly via temporary interweaving), so random pointers can be resolved regardless of traversal order."
- **90-second explanation:** "The challenge here is the random pointer: it can point to any node in the list, including one I haven't created a copy of yet if I'm just doing a single forward pass. The straightforward fix is a two-pass approach using a hash map: first, I walk the list once just to create every copy node and record a mapping from each original node to its new copy. Then, in a second pass, I use that map to correctly wire up both the next and random pointers on every copy, since by this point every original node's copy already exists in the map, regardless of where in the list it appears. For an O(1) extra space version, I use a cleverer technique: instead of a separate map, I temporarily interweave each copy directly into the original list, right after its corresponding original node, so that 'the copy of node X' is always just immediately accessible as X.next. Then I do a second pass to resolve every random pointer using just this adjacency — if X's random points to Y, then X's copy's random should point to Y.next, which is exactly Y's copy. Finally, I do a third pass to carefully detangle the interwoven structure back into two fully separate lists, restoring the original and producing the finished copy."
- **Related problems using this pattern:**
  - LeetCode 133 — Clone Graph (a very closely related deep-copy problem, using the same old-to-new mapping idea applied to a graph instead of a linked list)
  - LeetCode 1265 — Print Immutable Linked List in Reverse (unrelated technique, but another linked-list-with-constraints problem)
  - LeetCode 430 — Flatten a Multilevel Doubly Linked List (a different but related linked-list-restructuring problem)

## 12. Recall Questions

**Q:** Why can't a simple single forward pass correctly set every node's copy's `random` pointer directly as you go?
**A:** A node's `random` pointer might target a node that appears later in the list, whose copy hasn't been created yet at the point you're processing the current node, so there would be nothing valid to point the copy's `random` reference to at that moment.

**Q:** Why does the interweaving technique's adjacency relationship (`X.next == X'`, the copy) eliminate the need for a separate hash map?
**A:** Since every original node is directly followed by its own copy immediately after the first pass, looking up "what is the copy of this specific original node" becomes as simple as reading that node's `next` pointer, which is exactly the lookup a hash map would otherwise be needed to perform, but accomplished here through the list's own temporary structure instead.

**Q:** Why is it essential to use object reference identity (not value comparison) when mapping original nodes to their copies, in either approach?
**A:** Multiple distinct nodes in the list can share the exact same value, so relying on value comparison to establish correspondence between original and copy nodes would be ambiguous and could easily produce an incorrect mapping, whereas reference identity (or direct structural adjacency) unambiguously identifies each specific node.

**Q:** Why must the interweaving approach's third pass carefully restore the original list's structure, rather than just discarding it once the copy's random pointers are resolved?
**A:** The interweaving technique temporarily merges the original and copy nodes into a single combined structure, so an explicit final step is required to separate them back into two fully independent lists, ensuring the original list's integrity is restored and that no copy nodes remain improperly linked into what should be a clean, separate copy list.

**Q:** What would go wrong if the `if (current.random != null)` check were omitted during the interweaving approach's random-pointer-resolution pass?
**A:** Attempting to access `current.random.next` when `current.random` is actually `null` would immediately throw a `NullPointerException`, since there's no valid node to call `.next` on when the random pointer itself doesn't reference any node.

## 13. Final Code

```java
public Node copyRandomList(Node head) {
    if (head == null) {
        return null;
    }

    // Pass 1: interweave copies into the original list.
    Node current = head;
    while (current != null) {
        Node copy = new Node(current.val);
        copy.next = current.next;
        current.next = copy;
        current = copy.next;
    }

    // Pass 2: resolve random pointers using adjacency.
    current = head;
    while (current != null) {
        if (current.random != null) {
            current.next.random = current.random.next;
        }
        current = current.next.next;
    }

    // Pass 3: detangle into two separate lists.
    Node newHead = head.next;
    current = head;
    Node copyCurrent = newHead;
    while (current != null) {
        current.next = copyCurrent.next;
        current = current.next;
        if (current != null) {
            copyCurrent.next = current.next;
            copyCurrent = copyCurrent.next;
        }
    }

    return newHead;
}
```

## 14. Self-Test

You have the head of a linked list where each node also has a `random` pointer that can reference any node in the list. Construct a complete deep copy, with no shared references to the original. Think about why a node's random pointer target might not have a copy yet in a single pass, and how either a hash map or a temporary interweaving trick resolves this regardless of traversal order.
