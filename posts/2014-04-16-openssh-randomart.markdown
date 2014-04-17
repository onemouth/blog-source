---
title: OpenSSH 的 randomart
tags: ssh
---

![](../images/randomart.png "randomart examples")

~~~

~~~

OpenSSH 的 randomart 是希望用圖形的方式, 幫助人類辨別 server 的 public key. 

可以用

> ssh-keygen -lv -f ~/.ssh/known_hosts

看現在所有存起來的 public key, 以及它們的 ramdomart


然後每次在使用 ssh client 的時候, 都使用

> ssh -o VisualHostKey=yes   


或是直接在設定檔: ~/.ssh/config, 加入

> VisualHostKey=yes   

就會每次在 ssh 連線的時候, 先秀出 server 的 ramdomart.


這樣的好處是什麼呢, 就是當你日複一日, 每天都上同一個 server 的時候,
就會不知不覺把它的 randomart 植入腦海。

然後！假如有一天 randomart 改變了, 你就會馬上注意到, 想著, 我記憶中的圖不是這樣的阿!
可能就發現了黑客想要攻擊你!

不過, 不同的 ssh 版本顯示的 randomart 會不太一樣, 所以假如換了 OS, 然後發現同一個 server 顯示的 randomart 跟著變了, 也不用太驚訝...


