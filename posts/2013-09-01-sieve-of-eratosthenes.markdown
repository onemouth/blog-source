---
title: 有趣的Sieve of Eratosthenes (2)
---

我們想要的是，既能保有laziness，不需要先給出上限N，又可以保有原來sieve algorithm的效率，能不能做到呢？當然可以！

這個實現的想法是這樣的：利用一個dictionary or map的資料結構，當我們遇到一個數p的時候，假如p在這個map查找不到，我們就令p為質數，並在這個map裡加入一筆(key,value)=(p+p, p)。
下一次，假如我們遇到了(p+p)，它在我們的map裡可以查找到，我們便知道(p+p)不是質數，在map中對應(p+p)的，會是一串的質數，我們再對每一個質數q，加入(p＋q, q)到map中。

這個方法的重點在於把sieve algorithm中，p的倍數這個列表，不一次做出來，而只是先存了2p到map裡，然後遇到了，再存3p到map裡，然後依此類推...

看code會更清楚！

<script src="https://gist.github.com/onemouth/6405402.js"></script>

<script src="https://gist.github.com/onemouth/6405584.js"></script>