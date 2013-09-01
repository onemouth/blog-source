---
title: 有趣的Sieve of Eratosthenes (1)
---

這幾篇主要會根據這篇[paper](http://www.cs.tufts.edu/~nr/cs257/archive/melissa-oneill/Sieve-JFP.pdf)，還有一些我自己的延伸~

[Sieve of Eratosthenes](http://en.wikipedia.org/wiki/Sieve_of_Eratosthenes), 是一個可以用來列出質數的演算法。(以下簡稱sieve algorithm，或篩法)

演算法:
首先列出從2到N的全部正整數為一個表，然後重覆以下步驟:

1. 找到表中的最小正整數p，宣告p為一個質數
2. 刪掉表中為p倍數的數


有一個經典的haskell實現，如以下程式碼：

<script src="https://gist.github.com/onemouth/5677760.js"></script>

我也寫了一個對應的python版本，如下:

<script src="https://gist.github.com/onemouth/6349845.js"></script>


這個方法利用[lazy evaluation](http://en.wikipedia.org/wiki/Lazy_evaluation)成功的把sieve algorithm應用到無限長的list上，乍看之下，可以說是優雅又簡潔!

但可惜的是，這個版本的實現違反了sieve algorithm的根本精神，因為在sieve algorithm的第二步: `刪掉表中為p倍數的數`，應該是直接刪掉p, 2p, 3p, 4p,...這樣一系列的數，但是我們的code卻是一個數一個數去除，除到餘數為0的，才把這個數從表中剔除。顯而易見的，速度會慢了不少!

所以接下來，我們會試著改code，讓它可以符合sieve algorithm的精神，又可以依然應用在無限長的list上。