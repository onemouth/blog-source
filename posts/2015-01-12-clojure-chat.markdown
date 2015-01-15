---
title: Clojure 閒聊 
tags: clojure, nodejs, clojurescript
---

最近試着開始寫 [Clojure](http://clojure.org/), 還蠻好玩的,
之前完全沒想過有一天會開始寫 Lisp 風格的程式, 後來發現一堆括號排得整整齊齊居然會有莫名的成就感。
<span class="sidenote"> <a href="http://xkcd.com/297/" target="_blank"> xkcd: Lisp Cycles </a> </span>

Clojure 通常跑在 JVM 上面, 所以還是要對 Java 的生態系有一定的瞭解, 對 Java 不熟的人像我, 就會覺得比較麻煩一些。
<span class="sidenote"> 而且 JVM 又啓動的很慢 </span>

幸好 Clojure 很容易被移植, 除了被編譯成 Java Bytecode, 它現在還可以被編譯成 [CLR Bytecode](https://github.com/clojure/clojure-clr) 和 [Javascript](https://github.com/clojure/clojurescript)!

想到 Javascript, 又會想到近幾年很火的 [Node.js](http://nodejs.org/), 也許可以試着把它們結合, ClojureScript on Node.js 取代 Clojure on JVM?
網路上有一些 ClojureScript on Node.js 要如何設定的資料, 當時也花了一番功夫查這些資料, 拼湊要怎麼做最好, 不過現在不用那麼麻煩, 只要用 @swannodette 的 [mies-node-template](https://github.com/swannodette/mies-node-template) 就 OK 了。

[npm](https://www.npmjs.com/) 上有不少從 C/C++ 移植過去的 module, 對我來說, 這些 module 也比 Java 的更容易上手, 因此目前用起來蠻愉快的~