---
title: "Hash Map"
layout: default
parent: Data Structures & Algorithms
nav_order: 5
permalink: /data-structures/Hash-Map/hash-map/

topic: Hash Map
section: Hash-Based Data Structures
concepts: [hashing, hash-function, hashcode, equals, collisions, buckets]
implementations: [separate-chaining, linked-list, red-black-tree]
java_classes: [HashMap, LinkedHashMap, Hashtable, ConcurrentHashMap]
complexities: [O(1), O(n), O(log n)]
tags: [data-structures, hashing, hashmap, hashcode, equals, java]
---

# HashMap in Java

## 1. What Is a HashMap?

A `HashMap` is a data structure in Java that stores data in **key-value pairs**.

Each key is associated with a value.

For example, imagine a student management system:

| Student ID (Key) | Student Name (Value) |
|---|---|
| 101 | Danish |
| 102 | Rahul |
| 103 | Amit |
| 104 | Priya |

Instead of searching through every student record, we can use the student ID to retrieve the corresponding name.

```java
Map<Integer, String> students = new HashMap<>();

students.put(101, "Danish");
students.put(102, "Rahul");
students.put(103, "Amit");
students.put(104, "Priya");

System.out.println(students.get(102));
```

Output:

```text
Rahul
```

The key `102` is used to retrieve its associated value.

### Important Characteristics

- Stores data as key-value pairs.
- Keys must be unique.
- Values can be duplicated.
- Allows one `null` key.
- Allows multiple `null` values.
- Does not guarantee insertion order.
- Is not synchronized by default.
- Provides average O(1) time complexity for basic `put()` and `get()` operations when hash distribution is good.

### HashMap Package

```java
import java.util.HashMap;
import java.util.Map;
```

---

## 2. How to Create and Use a HashMap

### Creating a HashMap

```java
Map<String, Integer> map = new HashMap<>();
```

Here:

- `String` is the key type.
- `Integer` is the value type.

### Adding Elements – put()

```java
map.put("Java", 10);
map.put("Python", 20);
map.put("Spring", 30);
```

### Retrieving Elements – get()

```java
System.out.println(map.get("Java"));
```

Output:

```text
10
```

### Updating an Existing Key

```java
map.put("Java", 50);
```

If the key already exists, its value is replaced.

### Removing Elements – remove()

```java
map.remove("Python");
```

### Checking Whether a Key Exists

```java
map.containsKey("Java");
```

### Checking Whether a Value Exists

```java
map.containsValue(50);
```

### Getting the Size

```java
map.size();
```

### Iterating Over a HashMap

```java
for (Map.Entry<String, Integer> entry : map.entrySet()) {

    System.out.println(
        entry.getKey() + " = " + entry.getValue()
    );
}
```

---

# 3. How Does HashMap Work Internally?

This is the most important section.

To understand HashMap, first understand its internal data structure.

A Java HashMap internally uses:

**An array of buckets, where each bucket can contain a linked list or a balanced tree of entries.**

In simplified form:

```text
                  HashMap
                     |
                     v
              Internal Array
                     |
       +-------------+-------------+
       |             |             |
       v             v             v
    Bucket 0      Bucket 1      Bucket 2
       |             |             |
       v             v             v
     Node          Node          Node
       |             |             |
       v             v             v
    Node          Node          Node
```

Each bucket can contain one or more entries.

In modern Java implementations (Java 8 and later), a bucket can use:

1. A single node.
2. A linked list of nodes.
3. A balanced Red-Black Tree when collision conditions trigger treeification.

### Internal Node Structure

The internal HashMap entry is represented by a node similar to this:

```java
static class Node<K, V> implements Map.Entry<K, V> {

    final int hash;

    final K key;

    V value;

    Node<K, V> next;

    // Constructor and other methods
}
```

Each node contains:

| Field | Purpose |
|---|---|
| `hash` | Stored hash value of the key |
| `key` | Actual key object |
| `value` | Associated value |
| `next` | Reference to the next node in the bucket |

The `next` reference allows multiple entries to be stored in the same bucket using a linked list.

**Note:** This is a simplified representation of the Java implementation. Tree bins use additional tree-node fields.

---

# 4. Understanding Hashing

Hashing is the process of converting a key into an integer value using a hash function.

For example:

```java
String key = "Java";

int hash = key.hashCode();

System.out.println(hash);
```

Output:

```text
2301506
```

The exact result comes from the `String` class's hash-code implementation.

The hash code helps HashMap determine where the key-value pair should be stored.

### Basic Flow

```text
Key
 |
 v
hashCode()
 |
 v
Hash Value
 |
 v
Bucket Index Calculation
 |
 v
Store or Retrieve Entry
```

For example:

```text
Key = "Java"

hashCode() = 2301506

HashMap calculates an internal hash
              |
              v
Calculates bucket index
              |
              v
Stores the entry in that bucket
```

**Important:** A hash code is not the same as a bucket index.

The hash code is an integer produced from the key. HashMap transforms that value to calculate the bucket index.

---

# 5. What Is hashCode() in Java?

Every Java object inherits the `hashCode()` method from the `Object` class.

Method declaration:

```java
public native int hashCode();
```

The `hashCode()` method returns an integer representing the object's hash code.

Example:

```java
String str1 = "Java";
String str2 = "Java";

System.out.println(str1.hashCode());
System.out.println(str2.hashCode());
```

Both strings produce the same hash code because `String` overrides `hashCode()` based on its contents.

### Important hashCode() Contract

Java defines important rules for `hashCode()` and `equals()`.

**Rule 1: Equal objects must have equal hash codes.**

If:

```java
obj1.equals(obj2) == true
```

Then:

```java
obj1.hashCode() == obj2.hashCode()
```

must also be true.

**Rule 2: Different objects can have the same hash code.**

For example:

```java
String str1 = "FB";
String str2 = "Ea";

System.out.println(str1.hashCode());
System.out.println(str2.hashCode());
```

Both produce:

```text
2236
```

However:

```java
System.out.println(str1.equals(str2));
```

Output:

```text
false
```

This is a hash collision.

**Rule 3: Hash code should remain consistent while information used by equals() remains unchanged.**

If an object's relevant fields do not change, repeated calls to `hashCode()` should return the same value during the same execution of the application.

A hash code is not required to remain the same across different JVM executions.

### Why Do We Need Both hashCode() and equals()?

Because they perform different jobs.

| Method | Responsibility |
|---|---|
| `hashCode()` | Helps identify the bucket |
| `equals()` | Determines whether two keys are logically equal |

HashMap uses both methods to identify keys correctly.

---

# 6. How String Implements hashCode()

The `String` class overrides `hashCode()`.

Conceptually, its implementation is:

```java
public int hashCode() {

    int h = 0;

    for (int i = 0; i < length(); i++) {
        h = 31 * h + charAt(i);
    }

    return h;
}
```

This uses a polynomial hash calculation.

### Example: "ABC"

Character values:

```text
A = 65
B = 66
C = 67
```

Calculation:

```text
Initial hash = 0

For A:
hash = 31 * 0 + 65
     = 65

For B:
hash = 31 * 65 + 66
     = 2081

For C:
hash = 31 * 2081 + 67
     = 64578
```

Therefore:

```java
System.out.println("ABC".hashCode());
```

Output:

```text
64578
```

### Why Does String Use 31?

31 is a small prime number that provides a useful hash distribution for strings.

There is also an implementation advantage:

```text
31 * h = (h << 5) - h
```

This allows multiplication by 31 to be expressed using a bit shift and subtraction.

The actual Java implementation uses integer arithmetic, which naturally wraps on overflow.

### Important

This is a conceptual representation of String hashing. Modern JDK implementations may use optimized internal representations and cached hash values.

---

# 7. How HashMap Calculates the Internal Hash

HashMap does not simply use the raw result of `key.hashCode()` as the bucket index.

In Java 8 and later, the internal hash calculation is conceptually:

```java
static final int hash(Object key) {

    int h;

    return (key == null)
        ? 0
        : (h = key.hashCode()) ^ (h >>> 16);
}
```

This is called hash spreading.

### Understanding the Formula

```java
h ^ (h >>> 16)
```

Where:

- `h` is the original hash code.
- `>>>` is the unsigned right-shift operator.
- `^` is the bitwise XOR operator.

### Why Does HashMap Use This?

A bucket index is calculated using a small number of lower-order bits.

If the lower bits of hash codes are poorly distributed, many keys can land in the same bucket.

The hash-spreading operation mixes the higher-order bits into the lower-order bits.

This can improve distribution across buckets.

### Example

Suppose:

```text
Original hash = h
```

Java calculates:

```text
Internal hash = h ^ (h >>> 16)
```

This mixed value is then used for bucket selection.

**Important:** HashMap does not modify the original key's `hashCode()` method. It calculates a separate internal hash value.

---

# 8. How HashMap Calculates the Bucket Index

After calculating the internal hash, HashMap determines the bucket index.

The formula is:

```java
index = (n - 1) & hash;
```

Where:

- `n` = current capacity of the internal array.
- `hash` = internal spread hash.

### Why Use Bitwise AND Instead of Modulo?

For a positive capacity that is a power of two:

```text
n = 16

n - 1 = 15
```

In binary:

```text
16 = 10000
15 = 01111
```

The operation:

```java
hash & 15
```

extracts the lower four bits.

This is efficient and produces an index between `0` and `15`.

### Example

Suppose:

```text
Capacity = 16

Internal hash = 12345
```

The bucket index is:

```java
index = (16 - 1) & 12345;

index = 15 & 12345;

index = 9;
```

Therefore, the entry is stored in bucket index 9.

### Why Is HashMap Capacity Always a Power of Two?

The internal table capacity is maintained as a power of two (for normal table sizes).

Examples:

```text
16
32
64
128
256
512
1024
```

This makes bucket calculation using bitwise AND efficient.

It also makes resizing efficient because entries can be redistributed using one additional hash bit.

---

# 9. Internal Working of put() Method

Consider:

```java
Map<String, Integer> map = new HashMap<>();

map.put("Java", 100);
```

What happens internally?

Let's understand the steps.

### Step 1: Calculate the Hash Code

HashMap calls:

```java
"Java".hashCode();
```

Result:

```text
2301506
```

### Step 2: Calculate the Internal Hash

HashMap applies:

```java
hash = h ^ (h >>> 16);
```

### Step 3: Calculate the Bucket Index

Suppose the table capacity is 16.

HashMap calculates:

```java
index = (16 - 1) & hash;
```

The entry is assigned to the resulting bucket.

### Step 4: Check Whether the Bucket Is Empty

If the bucket is empty, HashMap creates a new node.

Conceptually:

```java
table[index] = newNode;
```

### Step 5: Check for Existing Keys

If the bucket already contains an entry, HashMap checks:

1. Whether the stored hash equals the new hash.
2. Whether the keys are the same object using `==`.
3. Whether the keys are logically equal using `equals()`.

Conceptually:

```java
if (storedHash == newHash &&
    (storedKey == newKey || newKey.equals(storedKey))) {

    // Update existing value
}
```

If an equal key already exists, HashMap replaces its value.

### Step 6: Handle Collision

If the bucket contains a different key, HashMap must store another entry in the same bucket.

This is called a **Hash Collision**.

In Java 8 and later, entries are stored in a linked list until treeification conditions are met.

### Overall Flow

```text
put(key, value)
       |
       v
Calculate hashCode()
       |
       v
Calculate internal hash
       |
       v
Calculate bucket index
       |
       v
Is bucket empty?
       |
   +---+---+
   |       |
  Yes      No
   |       |
   v       v
Create   Compare existing
Node     hash and keys
           |
       +---+---+
       |       |
     Equal   Different
       |       |
       v       v
    Update   Handle
    Value    Collision
```

---

# 10. What Is Hash Collision?

A hash collision happens when two different keys produce the same bucket index.

For example:

```java
String key1 = "FB";
String key2 = "Ea";

System.out.println(key1.hashCode());
System.out.println(key2.hashCode());
```

Output:

```text
2236
2236
```

Both keys have the same hash code.

They therefore also produce the same bucket index for the same table capacity.

But remember:

```java
key1.equals(key2);
```

returns:

```text
false
```

So HashMap must store both entries.

### How Does HashMap Handle Collision?

Java 8 and later use two main structures:

1. Linked List
2. Red-Black Tree

Initially, colliding entries are stored in a linked list.

```text
Bucket 5
   |
   v
+-------+
| FB=10 |
+-------+
   |
   v
+-------+
| Ea=20 |
+-------+
   |
   v
+-------+
| Ab=30 |
+-------+
```

When another key maps to the same bucket, HashMap checks the entries in that bucket.

If the key already exists, its value is updated.

Otherwise, a new node is added.

---

# 11. Linked List to Red-Black Tree Conversion

One of the important improvements introduced in Java 8 is treeification.

If a bucket contains too many colliding entries, HashMap may convert the linked list into a Red-Black Tree.

A Red-Black Tree is a self-balancing binary search tree.

It helps keep lookup efficient even when many keys land in the same bucket.

### Important Constants

Java 8 and later use constants similar to:

```java
static final int TREEIFY_THRESHOLD = 8;

static final int UNTREEIFY_THRESHOLD = 6;

static final int MIN_TREEIFY_CAPACITY = 64;
```

### TREEIFY_THRESHOLD

The threshold is 8.

When adding a node causes a bucket's list to reach the treeification threshold, HashMap may attempt to convert it into a tree.

However, treeification is not automatic in every situation.

If the table capacity is smaller than 64, HashMap prefers resizing the table instead.

### MIN_TREEIFY_CAPACITY

The minimum table capacity for treeification is 64.

If the capacity is below 64, HashMap generally resizes instead of creating a tree.

### UNTREEIFY_THRESHOLD

When a tree bin becomes sufficiently small during resizing, it can be converted back into a linked list.

The threshold is 6.

**Important:** These are implementation details of OpenJDK HashMap, not public API guarantees. Exact conversion behavior also depends on the insertion or resizing operation.

### Linked List vs Red-Black Tree

| Structure | Lookup Complexity |
|---|---|
| Linked List | O(k) |
| Balanced Red-Black Tree | O(log k) |

Here, `k` is the number of entries in that bucket.

For ordinary well-distributed keys, most buckets contain very few entries.

---

# 12. What Is a Red-Black Tree?

A Red-Black Tree is a self-balancing binary search tree.

It maintains specific coloring and balancing rules so that its height remains logarithmic.

Important rules include:

1. Every node is either red or black.
2. The root is black.
3. Red nodes cannot have red children.
4. Every path from a node to its descendant null leaves has the same number of black nodes.
5. New nodes are inserted and balanced according to Red-Black Tree rules.

HashMap uses tree bins to improve performance in heavily collided buckets.

### Why Not Use a Normal Binary Search Tree?

A normal binary search tree can become unbalanced.

For example:

```text
10
  \
   20
     \
      30
        \
         40
```

Searching this tree can take O(n).

A balanced tree keeps its height O(log n), improving lookup performance.

**Important HashMap detail:** Tree-bin lookup is not universally O(log n) for every possible key type. Comparable keys with consistent ordering are especially helpful. For non-comparable keys or certain ambiguous ordering situations, lookup can require additional searching.

---

# 13. Internal Working of get() Method

Consider:

```java
map.get("Java");
```

How does HashMap retrieve the value?

### Step 1: Calculate the Hash

```java
"Java".hashCode();
```

### Step 2: Calculate the Internal Hash

```java
hash = h ^ (h >>> 16);
```

### Step 3: Calculate the Bucket Index

```java
index = (n - 1) & hash;
```

### Step 4: Access the Bucket

HashMap directly accesses the corresponding array position.

```java
table[index];
```

### Step 5: Compare the Key

If the bucket contains one node, HashMap checks the stored hash and key equality.

For a linked list, it checks entries one by one.

For a tree bin, it searches the tree structure.

### Step 6: Return the Value

When the matching key is found, its value is returned.

If no matching key exists:

```java
map.get("Unknown");
```

The result is:

```text
null
```

### get() Flow

```text
get(key)
   |
   v
Calculate hash
   |
   v
Calculate bucket index
   |
   v
Access bucket
   |
   v
Compare hash and key
   |
   v
Found?
   |
 +---+---+
 |       |
Yes      No
 |       |
 v       v
Return  Return
Value   null
```

---

# 14. Internal Working of remove() Method

When we call:

```java
map.remove("Java");
```

HashMap:

1. Calculates the key's hash.
2. Finds the bucket index.
3. Searches the bucket for the matching key.
4. Removes the matching node.
5. Repairs the linked list or tree structure if necessary.
6. Returns the removed value.

Example:

```java
Map<String, Integer> map = new HashMap<>();

map.put("Java", 10);
map.put("Spring", 20);

Integer removedValue = map.remove("Java");

System.out.println(removedValue);
System.out.println(map);
```

Output:

```text
10
{Spring=20}
```

Average Time Complexity: O(1)

Worst-case behavior depends on collisions and the bucket structure.

---

# 15. What Is the Initial Capacity of HashMap?

When we create:

```java
Map<String, Integer> map = new HashMap<>();
```

The default initial capacity is 16.

However, there is an important implementation detail:

**The internal bucket array is allocated lazily.**

It is generally not allocated immediately when the HashMap object is created.

The first insertion triggers table allocation.

Default values in Java 8 and later:

```java
static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;

static final float DEFAULT_LOAD_FACTOR = 0.75f;
```

Therefore:

```text
Initial Capacity = 16
Load Factor = 0.75
```

---

# 16. What Is Load Factor?

The load factor determines how full the HashMap table is allowed to become before resizing.

Formula:

```text
Threshold = Capacity × Load Factor
```

For default values:

```text
Capacity = 16

Load Factor = 0.75

Threshold = 16 × 0.75

Threshold = 12
```

When the number of mappings exceeds the threshold, HashMap resizes the internal table.

Example:

```java
Map<Integer, String> map = new HashMap<>(16, 0.75f);
```

Here:

- Initial capacity = 16
- Load factor = 0.75

### Why Is Load Factor Important?

If the load factor is too high:

- More entries may share buckets.
- Collisions may increase.
- Lookup can become slower.

If the load factor is too low:

- More memory is used.
- The table may contain many empty buckets.

The default value of 0.75 provides a practical balance.

---

# 17. What Happens During HashMap Resizing?

When the number of mappings exceeds the threshold, HashMap increases its capacity.

For example:

```text
Old Capacity = 16
Old Threshold = 12

New Capacity = 32
New Threshold = 24
```

In normal growth, the capacity doubles.

### Why Does HashMap Resize?

As the number of elements increases, more keys may map to the same buckets.

Increasing capacity provides more buckets and can improve distribution.

### How Are Existing Entries Moved?

Java 8 introduced an efficient resizing technique.

Because the capacity doubles, each existing entry either:

- Stays at its old bucket index.
- Moves to its old index plus the old capacity.

The decision depends on one additional bit of the entry's hash.

Conceptually:

```java
if ((hash & oldCapacity) == 0) {
    // Stay at the old index.
} else {
    // Move to old index + oldCapacity.
}
```

### Example

Suppose:

```text
Old Capacity = 16
New Capacity = 32
```

An entry previously stored at index 5 can:

```text
Remain at index 5

OR

Move to index 5 + 16 = 21
```

This avoids recalculating the complete modulo operation for every entry.

### Important

Resizing is expensive because entries must be redistributed.

However, resizing happens occasionally, so insertion is still amortized O(1) under normal assumptions.

---

# 18. Why Are hashCode() and equals() Important for HashMap?

This is one of the most frequently asked Java interview questions.

Consider a custom Employee class.

```java
class Employee {

    private int id;
    private String name;

    public Employee(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
```

Now create two objects:

```java
Employee e1 = new Employee(101, "Danish");

Employee e2 = new Employee(101, "Danish");
```

Although both objects contain the same data:

```java
System.out.println(e1 == e2);
```

Output:

```text
false
```

Because they are two different objects in memory.

Without overriding `equals()` and `hashCode()`, they inherit the default identity-based behavior from `Object`.

Now try:

```java
Map<Employee, String> map = new HashMap<>();

map.put(e1, "Developer");

System.out.println(map.get(e2));
```

The result is generally:

```text
null
```

Why?

Because the default implementations do not treat these two distinct Employee instances as equal.

### Solution: Override equals() and hashCode()

```java
import java.util.Objects;

class Employee {

    private int id;
    private String name;

    public Employee(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Employee)) {
            return false;
        }

        Employee other = (Employee) obj;

        return id == other.id
                && Objects.equals(name, other.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name);
    }
}
```

Now:

```java
Employee e1 = new Employee(101, "Danish");

Employee e2 = new Employee(101, "Danish");

Map<Employee, String> map = new HashMap<>();

map.put(e1, "Developer");

System.out.println(map.get(e2));
```

Output:

```text
Developer
```

Because:

```java
e1.equals(e2) == true
```

and:

```java
e1.hashCode() == e2.hashCode()
```

Both objects are treated as the same logical key.

---

# 19. What Happens If We Override equals() but Not hashCode()?

This is a very common interview question.

Suppose:

```java
class Employee {

    private int id;

    Employee(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Employee)) {
            return false;
        }

        Employee other = (Employee) obj;

        return this.id == other.id;
    }
}
```

We have overridden `equals()`, but not `hashCode()`.

Now:

```java
Employee e1 = new Employee(101);

Employee e2 = new Employee(101);

System.out.println(e1.equals(e2));
```

Output:

```text
true
```

But their inherited identity-based hash codes may differ.

Now:

```java
Map<Employee, String> map = new HashMap<>();

map.put(e1, "Developer");

System.out.println(map.get(e2));
```

It may return:

```text
null
```

Why?

HashMap first uses the hash code to identify the bucket.

If the two equal objects produce different hash codes, they can be assigned to different buckets.

HashMap may never compare them using `equals()`.

**Interview rule:**

Whenever you override `equals()`, you should also override `hashCode()`.

---

# 20. What Happens If We Override hashCode() but Not equals()?

Consider:

```java
class Employee {

    private int id;

    Employee(int id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
```

Now:

```java
Employee e1 = new Employee(101);

Employee e2 = new Employee(101);

Map<Employee, String> map = new HashMap<>();

map.put(e1, "Developer");

System.out.println(map.get(e2));
```

The result is:

```text
null
```

Why?

Both objects produce the same hash code, but the inherited `equals()` method still compares object identity.

HashMap finds the same bucket but determines that the keys are not equal.

Therefore, it does not return the existing value.

**Remember:**

- Same hash code does not mean equal objects.
- Equal objects must have the same hash code.
- Unequal objects may have the same hash code.

---

# 21. What Happens If a Mutable Object Is Used as a HashMap Key?

This is another important senior-level interview question.

Consider:

```java
class Employee {

    int id;

    Employee(int id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Employee)) {
            return false;
        }

        Employee other = (Employee) obj;

        return this.id == other.id;
    }
}
```

Usage:

```java
Employee employee = new Employee(101);

Map<Employee, String> map = new HashMap<>();

map.put(employee, "Developer");

System.out.println(map.get(employee)); // Developer

employee.id = 102;

System.out.println(map.get(employee)); // May return null
```

### Why Does This Happen?

When the employee is inserted:

```text
employee.id = 101

hashCode() = 101
```

HashMap calculates the bucket index and stores the entry there.

Later, the ID changes:

```text
employee.id = 102

hashCode() = 102
```

When retrieving the value, HashMap calculates a new bucket index.

It may look in a different bucket from where the entry was originally stored.

Therefore, the lookup fails.

### Best Practice

Use immutable objects as HashMap keys whenever possible.

For example:

- String
- Integer
- Long
- UUID
- Immutable custom objects

If custom objects are used as keys, fields participating in `equals()` and `hashCode()` should not change while the object is stored in the map.

---

# 22. Can HashMap Store Null Keys and Null Values?

Yes.

Java HashMap supports:

- One null key.
- Multiple null values.

Example:

```java
Map<String, String> map = new HashMap<>();

map.put(null, "Unknown");
map.put("Java", null);
map.put("Spring", null);

System.out.println(map.get(null));
System.out.println(map.get("Java"));
```

Output:

```text
Unknown
null
```

### How Is Null Key Handled Internally?

In Java 8 and later, HashMap handles a null key specially.

Conceptually:

```java
hash = (key == null) ? 0 : hash(key);
```

Therefore, a null key uses hash value zero and is stored in bucket zero when the table is non-empty.

### Important: get() Returning Null

Consider:

```java
map.put("Java", null);

System.out.println(map.get("Java"));
```

Output:

```text
null
```

Now consider a key that does not exist:

```java
System.out.println(map.get("Unknown"));
```

It also returns:

```text
null
```

How do we distinguish these cases?

Use:

```java
map.containsKey("Java");
```

This returns `true`, while:

```java
map.containsKey("Unknown");
```

returns `false`.

---

# 23. HashMap vs Hashtable vs ConcurrentHashMap

These are commonly compared in interviews.

| Feature | HashMap | Hashtable | ConcurrentHashMap |
|---|---|---|---|
| Thread-safe | No | Yes | Yes |
| Null key | One allowed | Not allowed | Not allowed |
| Null values | Allowed | Not allowed | Not allowed |
| Synchronization | None by default | Synchronized methods | Concurrent coordination |
| Performance under concurrency | Unsafe without external coordination | Often limited by broad locking | Designed for concurrent access |
| Introduced | Java 1.2 | Java 1.0 | Java 1.5 |
| Iterator behavior | Fail-fast, best effort | Legacy Enumeration and fail-fast iterators | Weakly consistent |

### HashMap

Use when:

- Thread safety is not required.
- You need ordinary key-value storage.
- You want low-overhead map operations.

### Hashtable

An older synchronized map implementation.

It synchronizes individual methods, but broad synchronization can limit concurrency.

It generally should not be chosen for new concurrent applications.

### ConcurrentHashMap

Use when multiple threads access and update a map concurrently.

It supports concurrent operations with finer-grained coordination rather than synchronizing every operation on the entire map.

Example:

```java
Map<String, Integer> map = new ConcurrentHashMap<>();

map.put("Java", 10);
```

Important: ConcurrentHashMap does not allow null keys or null values.

---

# 24. HashMap vs LinkedHashMap vs TreeMap

| Feature | HashMap | LinkedHashMap | TreeMap |
|---|---|---|---|
| Ordering | No guaranteed order | Insertion/access order | Sorted key order |
| Internal structure | Hash table | Hash table + linked list | Red-Black Tree |
| Null key | One allowed | One allowed | Depends on comparator; natural ordering does not support null |
| Basic lookup | O(1) average | O(1) average | O(log n) |
| Basic insertion | O(1) average | O(1) average | O(log n) |
| Use case | Fast lookup | Predictable iteration order | Sorted keys and range operations |

### HashMap

Does not guarantee insertion order.

### LinkedHashMap

Maintains insertion order by default.

It can also maintain access order, making it useful for implementing LRU caches.

### TreeMap

Stores keys in sorted order using natural ordering or a supplied comparator.

---

# 25. Time Complexity of HashMap

| Operation | Average Complexity | Worst-Case Consideration |
|---|---|---|
| put() | O(1) | O(n) in general; tree bins can improve suitable lookups/inserts |
| get() | O(1) | O(n) for list bins; typically O(log n) for tree-bin searches with suitable key ordering |
| remove() | O(1) | O(n) in general; tree-bin behavior depends on key ordering |
| containsKey() | O(1) | Similar to get() |
| containsValue() | O(n) | O(n) |
| Iteration | O(n + capacity) | Depends on table capacity and size |
| Resize | O(n) | Occasional operation |

### Why Is HashMap O(1) on Average?

Because a good hash distribution spreads entries across buckets.

Most buckets contain few entries, so HashMap can quickly locate the relevant key.

### Why Can It Become O(n)?

If many keys collide into the same bucket, a linked list may need to be traversed.

Treeification can improve collision-heavy bucket behavior, but it does not eliminate all possible linear-time cases.

---

# 26. Implementing a Simple HashMap from Scratch

Understanding a basic implementation helps explain the internal working.

The following example uses:

- An array of buckets.
- Linked-list nodes.
- A hash function.
- Bucket index calculation.
- `put()`, `get()`, and `remove()`.

This is an educational implementation, not a complete replacement for Java's HashMap. It does not include resizing, treeification, fail-fast iteration, or all Map interface behavior.

```java
import java.util.Objects;

public class CustomHashMap<K, V> {

    private static final int CAPACITY = 16;

    private static class Node<K, V> {

        K key;
        V value;
        Node<K, V> next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private final Node<K, V>[] buckets;

    @SuppressWarnings("unchecked")
    public CustomHashMap() {
        buckets = (Node<K, V>[]) new Node[CAPACITY];
    }

    private int getIndex(K key) {

        int hash = (key == null)
                ? 0
                : key.hashCode();

        // Ensure a non-negative index.
        return (hash & 0x7fffffff) % CAPACITY;
    }

    public void put(K key, V value) {

        int index = getIndex(key);

        Node<K, V> current = buckets[index];

        // Check whether the key already exists.
        while (current != null) {

            if (Objects.equals(current.key, key)) {
                current.value = value;
                return;
            }

            current = current.next;
        }

        // Insert new node at the beginning.
        Node<K, V> newNode = new Node<>(key, value);

        newNode.next = buckets[index];

        buckets[index] = newNode;
    }

    public V get(K key) {

        int index = getIndex(key);

        Node<K, V> current = buckets[index];

        while (current != null) {

            if (Objects.equals(current.key, key)) {
                return current.value;
            }

            current = current.next;
        }

        return null;
    }

    public V remove(K key) {

        int index = getIndex(key);

        Node<K, V> current = buckets[index];

        Node<K, V> previous = null;

        while (current != null) {

            if (Objects.equals(current.key, key)) {

                if (previous == null) {
                    buckets[index] = current.next;
                } else {
                    previous.next = current.next;
                }

                return current.value;
            }

            previous = current;

            current = current.next;
        }

        return null;
    }

    public static void main(String[] args) {

        CustomHashMap<String, Integer> map =
                new CustomHashMap<>();

        map.put("Java", 10);
        map.put("Spring", 20);
        map.put("Python", 30);

        System.out.println(map.get("Java"));

        map.put("Java", 100);

        System.out.println(map.get("Java"));

        System.out.println(map.remove("Spring"));

        System.out.println(map.get("Spring"));
    }
}
```

Output:

```text
10
100
20
null
```

### What Does This Implementation Teach?

1. A key is converted into a hash.
2. The hash determines the bucket index.
3. Multiple entries can exist in one bucket.
4. We use `equals()` to identify an existing key.
5. Existing keys have their values updated.
6. New entries are added to the bucket's linked list.

### How Is It Different from Java HashMap?

Our implementation:

- Uses fixed capacity.
- Does not resize.
- Does not implement Red-Black Tree bins.
- Does not implement the full Map interface.
- Uses a simplified bucket calculation.
- Does not implement all Java HashMap optimizations.

The real Java implementation is significantly more sophisticated.

---

# 27. Important HashMap Internal Constants

The following are implementation details commonly found in OpenJDK Java 8 and later.

| Constant | Default Value | Purpose |
|---|---:|---|
| DEFAULT_INITIAL_CAPACITY | 16 | Initial table capacity |
| MAXIMUM_CAPACITY | 2³⁰ | Maximum table capacity |
| DEFAULT_LOAD_FACTOR | 0.75 | Default load factor |
| TREEIFY_THRESHOLD | 8 | Threshold associated with treeification |
| UNTREEIFY_THRESHOLD | 6 | Threshold used when splitting tree bins during resizing |
| MIN_TREEIFY_CAPACITY | 64 | Minimum table capacity for treeification |

**Interview tip:** Know these values, but also understand their purpose. They are implementation details and can vary between Java versions or implementations.

---

# 28. Common HashMap Mistakes

1. Assuming HashMap preserves insertion order.
2. Assuming different keys always have different hash codes.
3. Overriding equals() without overriding hashCode().
4. Assuming the same hash code means two keys are equal.
5. Modifying key fields after inserting the key into a HashMap.
6. Assuming HashMap is thread-safe.
7. Assuming get() returning null always means the key does not exist.
8. Assuming every collision immediately creates a Red-Black Tree.
9. Assuming HashMap allocates its full bucket array immediately in its default constructor.
10. Assuming HashMap lookup is always O(1), regardless of collisions.

---

# 29. Senior-Level Interview Questions and Answers

### Q1. Explain the internal working of HashMap in Java 8.

HashMap uses an internal array of buckets. Each bucket contains a node, a linked list, or a Red-Black Tree.

For `put()`:

1. It calculates the key's hash code.
2. It applies hash spreading.
3. It calculates the bucket index using `(n - 1) & hash`.
4. It checks whether the bucket is empty.
5. If occupied, it compares hashes and keys using identity and `equals()`.
6. It updates an existing key or inserts a new node.
7. It may resize the table or treeify a bucket when necessary.

For `get()`, it calculates the same bucket index and searches that bucket.

### Q2. Why does HashMap use both hashCode() and equals()?

`hashCode()` helps locate the bucket, while `equals()` confirms whether two keys represent the same logical object.

Hash codes can collide, so hashCode() alone is insufficient.

### Q3. What happens when two different keys have the same hash code?

They may be placed in the same bucket. HashMap stores both entries and distinguishes them using key equality.

### Q4. What is the difference between hash code and bucket index?

Hash code is an integer returned by the key's hashing logic. Bucket index is calculated from the internal spread hash and current table capacity.

### Q5. Why does HashMap use `h ^ (h >>> 16)`?

It mixes higher-order bits into lower-order bits, improving distribution when the bucket index uses only a limited number of lower bits.

### Q6. Why is HashMap capacity a power of two?

It enables efficient index calculation using:

```java
(n - 1) & hash
```

It also makes redistribution during resizing efficient.

### Q7. What is the default capacity and load factor?

In Java 8 and later:

```text
Default capacity = 16
Default load factor = 0.75
```

The internal table is generally allocated on the first insertion.

### Q8. When does HashMap resize?

When the number of mappings exceeds the current resize threshold, which is generally capacity multiplied by load factor.

### Q9. Why is resizing expensive?

Because existing entries need to be redistributed into the new, larger table. A resize can take O(n) time.

### Q10. Why is put() O(1) on average?

A good hash distribution allows HashMap to access a small bucket and perform a limited number of comparisons.

### Q11. What happens if all keys have the same hash code?

They all collide into the same bucket.

The bucket can be represented as a linked list or, when treeification conditions are satisfied, a Red-Black Tree.

### Q12. When does a linked list become a Red-Black Tree?

The treeification threshold is 8, and the table capacity must be at least 64. If the table is smaller, HashMap generally resizes instead.

Exact behavior depends on the insertion path and implementation details.

### Q13. Why does HashMap convert a tree back into a linked list?

When a tree bin becomes sufficiently small during resizing, maintaining the tree structure may no longer be worthwhile. The untreeification threshold is 6.

### Q14. What happens if two different objects have the same hashCode() and equals() returns true?

HashMap treats them as the same logical key. Inserting the second object replaces the value associated with the existing mapping.

### Q15. Can two unequal objects have the same hashCode()?

Yes. This is called a hash collision.

### Q16. Can two equal objects have different hash codes?

No. That would violate the Java hashCode() contract.

### Q17. What happens if we override equals() but not hashCode()?

The objects may be considered equal but produce different hash codes. HashMap may place them in different buckets, causing lookups with an equivalent key to fail.

### Q18. Can HashMap have multiple null keys?

No. HashMap allows only one null key because keys are unique. Inserting another null key replaces the existing null-key value.

### Q19. Is HashMap thread-safe?

No. Multiple threads can access it safely for certain read-only scenarios after safe publication if it is not modified, but concurrent structural modification without proper synchronization is not supported.

Use ConcurrentHashMap or external synchronization for concurrent updates.

### Q20. What is the difference between HashMap and ConcurrentHashMap?

HashMap is not synchronized. ConcurrentHashMap supports concurrent access and updates using internal coordination mechanisms designed for concurrency.

### Q21. Why should HashMap keys be immutable?

If fields involved in hashCode() or equals() change after insertion, the object may no longer be found in the bucket where it was originally stored.

### Q22. Does HashMap maintain insertion order?

No. Use LinkedHashMap if insertion order or access order is required.

### Q23. What happens if the initial capacity is specified as 20?

HashMap rounds the requested capacity up to an appropriate power of two when allocating the internal table. For a requested capacity of 20, the resulting table capacity is normally 32.

### Q24. How does HashMap optimize resizing in Java 8?

When capacity doubles, an entry either stays at its old index or moves to its old index plus the old capacity. The decision is made using the old-capacity bit of the hash.

### Q25. Is the HashMap hash function guaranteed to prevent collisions?

No. Hash codes are finite integers, so collisions are always possible. HashMap tries to distribute keys efficiently and handles collisions internally.

---

# 30. Practical Coding Interview Problems

### Beginner

1. Count the frequency of each character in a string.
2. Count the frequency of each number in an array.
3. Find duplicate elements in an array.
4. Find the first non-repeating character in a string.
5. Find whether two arrays contain the same elements with the same frequencies.

### Intermediate

6. Two Sum problem.
7. Group Anagrams.
8. Find the intersection of two arrays.
9. Find the longest consecutive sequence.
10. Find the top K frequent elements.
11. Find the first unique character in a stream.
12. Find pairs with a given sum.

### Advanced

13. Implement an LRU Cache using LinkedHashMap.
14. Implement an LRU Cache using HashMap and Doubly Linked List.
15. Design a custom HashMap with resizing.
16. Design a thread-safe key-value store.
17. Find the minimum window substring.
18. Implement a frequency-based ranking system.

---

# 31. Quick Revision Cheat Sheet

| Concept | Key Point |
|---|---|
| HashMap | Stores key-value pairs |
| Internal structure | Array of buckets |
| Bucket contents | Node, linked list, or Red-Black Tree |
| hashCode() | Helps determine bucket location |
| equals() | Determines logical key equality |
| Hash spreading | `h ^ (h >>> 16)` |
| Bucket index | `(n - 1) & hash` |
| Default capacity | 16 |
| Default load factor | 0.75 |
| Default threshold | 12 for capacity 16 |
| Resize | Usually doubles capacity |
| Treeification threshold | 8 |
| Minimum treeify capacity | 64 |
| Untreeify threshold | 6 |
| Average get() | O(1) |
| Average put() | O(1) |
| Search in list bin | O(k) |
| Search in tree bin | Typically O(log k) with suitable ordering |
| Null keys | One allowed |
| Null values | Multiple allowed |
| Thread-safe | No |
| Insertion order | Not guaranteed |

## Final Things to Remember for Interviews

1. HashMap uses an array of buckets internally.
2. A key's `hashCode()` is transformed before calculating the bucket index.
3. The bucket index is calculated using `(n - 1) & hash`.
4. `hashCode()` identifies the likely bucket; `equals()` identifies the actual key.
5. Multiple keys can have the same hash code.
6. Collisions are handled using linked lists and Red-Black Trees.
7. Default capacity is 16 and default load factor is 0.75.
8. Resizing generally doubles the capacity.
9. Overriding `equals()` requires overriding `hashCode()`.
10. Mutable keys can cause lookup failures.
11. HashMap is not thread-safe.
12. Average lookup is O(1), but worst-case behavior depends on collision patterns and key ordering.

**Recommended learning order:**

1. Hashing fundamentals.
2. `hashCode()` and `equals()` contract.
3. HashMap bucket calculation.
4. Internal implementation of `put()` and `get()`.
5. Collision handling.
6. Linked List to Red-Black Tree conversion.
7. Load factor and resizing.
8. ConcurrentHashMap.
9. Custom HashMap implementation.
10. HashMap-based coding interview problems.