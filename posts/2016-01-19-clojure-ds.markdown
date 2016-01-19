---
title: Clojure 的 Map 和 Vector
tags: clojure, data structure
---

# Maps

分為 3 種, ArrayMap, Sorted Map 和 HashMap

## ArrayMap

使用 Array 實作的 Map, Map 長度小於等於 16 的時候會預設使用。
[當 Map 長度大於 16, 會自動轉成 HashMap](https://github.com/clojure/clojure/blob/ae7acfeecda1e70cdba96bfa189b451ec999de2e/src/jvm/clojure/lang/PersistentArrayMap.java#L180)

## Sorted Map

用 Red-Block Tree 實作, 時間複雜度為 $O(\log_{2} n)$

- 必須有 compare 的方法可供呼叫

- 在找的過程中，需要一直呼叫 compare, compare 的 cost 可能很高

## Hash Map
### Ideas

#### Trie (DFA)
  + 用 lookup table 實作
  + lookup table 可能很多層，沒用到的部分造成空間浪費
  + Time complexity: $O(\log_{m} n)$, m 為 symbol 個數
  
#### BST + TRIE = Ternary Search Tree (TST)
  + Time complexity: $O(\log_{2} n)$
  + 不會浪費空間
  
#### Array Mapped Trie (AMT)
  + 用 bitmap + dynamic array 改進 Trie 的 lookup table
  + Time complexity: $O(\log_{m} n)$
  + 不會浪費空間
  + 無法做到 Objects as keys
  
### HAML 

#### Hashing + AMT

- 使用一個“好”的 hash function, 產生一個 32-bit 的整數 (`hasheq`)。
  看起來是用 [Murmur3](https://github.com/clojure/clojure/blob/bc186508ab98514780efbbddb002bf6fd2938aee/src/jvm/clojure/lang/Murmur3.java) 實作的

- 整數每 5 個 bit 為一組, 作為 AMT 的 symbol 使用

#### Node Polymorphism
  + ArrayNode (32 個 symbol 都用到的 lookup table)
  + BitmapIndexedNode (只用到部分 symbol 的 lookup table)
  + HashCollisionNode (放 collision 的地方)

#### 為何不用傳統的 hash table?
  tree 的架構能滿足 functional data structure 對 structure sharing 的要求

### Other Maps

- HashMaps 在 merge 上效率較低
- [data.int-map](https://github.com/clojure/data.int-map) 是一個對 integer 為 key 做優化的 map

# Vectors

- 以 index 作為 AMT 的 key

- 只使用 ArrayNode 

- 有做 tail optimization, 所以 insert back 很快

## Problems
 + do not concat efficiently
 + [subvec](https://github.com/clojure/clojure/blob/ae7acfeecda1e70cdba96bfa189b451ec999de2e/src/jvm/clojure/lang/APersistentVector.java#L549)
   只是原來 vector 的 view, 假如原先 vector 很大, 可能有佔據記憶體的問題
 + 可考慮 core.rrb-vector 


### Reference

1. [What Lies Beneath - A Deep Dive Into Clojure's Data Structures - Mohit Thatte ](https://www.youtube.com/watch?v=7BFF50BHPPo) 

2. [Clojure source code](https://github.com/clojure/clojure)
