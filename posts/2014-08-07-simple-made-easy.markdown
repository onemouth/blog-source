---
title: Simple Made Easy
tags: programming, presentation
---

[Simple Made Easy by Rich Hickey](http://www.infoq.com/presentations/Simple-Made-Easy)

> `Simplicity is prerequisite for reliability` ~ Edsger W. Dijkstra

Rich Hickey 認為, 身為一個 programmer, 應該先追求 Simple, 假如我們在軟體開發的過程, 每一步都確實的踩在 Simple 的道路上,
最終會得到獎勵 —— 好維護又很可靠的系統。

但是 programmer 往往被 Easy 所迷惑, Easy 不是 Simple, Easy 指的是那些垂手可得的東西, 我們熟悉的工具(`apt-get`, `Eclipse`), 我們熟悉的演算法, 或我們熟悉的程式語言。Easy 是相對的, 我們熟悉的東西不代表別人也熟悉。Easy 也不一定是 Simple, 現在貪一時方便引入的東西, 常造成日後的維護困難。

當面臨問題, 面臨抉擇的時候, 先停下來, 不要急著用最省力的做法, 而是瞭解問題後, 用一個「好」的做法來解決。要怎麼判斷「好」? Simple 是我們的準則。

Simple 是一維的, 單一的概念, 單一的任務; 它很單純, 它不容易壞, 而且它恰好滿足我們的需要而不引進多餘的複雜度。

例如要操作資料庫, 該使用 [ORM](http://en.wikipedia.org/wiki/Object-relational_mapping) 或是 [SQL](http://zh.wikipedia.org/wiki/SQL)? ORM 對 programmer 來說很好用, 它提供了多一層的抽象, 讓 programmer 操作資料庫 像在操作一般的物件一樣, 不需要再煩惱背後 SQL 的語法, 但假如出現難解的 bug, 或是效能上需要改進, 那就麻煩了, 得 trace 進 ORM, 一步步看它究竟把物件操作轉成怎樣的 SQL 指令, 我們必須瞭解這樣的便利背後的複雜度代價。

在遇到 concurrency 的時候, thread & lock 幾乎是每個語言都會提供, 用起來也很容易, 但 thread & lock 造成非常多難以 debug 的問題, 因此, 不少語言或函式庫往往提供其他不一樣的 concurrency model, 例如 [STM](http://en.wikipedia.org/wiki/Software_transactional_memory), [Actor](http://en.wikipedia.org/wiki/Actor_model),  Event-driven concurrency 等。在捨棄 thread & lock 的同時, 這些不同的 model, 我們必須從中抉擇, 一個對於我們要解決的問題, 直接且 simple 的 model。

選擇 Easy 的開發, 最後產生出一個複製難用的系統; 選擇 Simple 的開發, 最後產生出一個簡單好用的系統。

這是一個很好的演講, 不同的人會從中得到不同的東西,這篇 [blog](http://blog.8thlight.com/uncle-bob/2011/10/20/Simple-Hickey.html) 從演講中得到了很多關於 TDD, Scrum 敏捷開發的省思, 也值得一讀。

