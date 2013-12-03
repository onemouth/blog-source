---
title: Rainbow Table (2)
tags: hash, crypto
---

在這篇，我們要做一些稍微formal的定義[^1][^2]～

假設我們有一個hash function $H$，然後密碼的集合是$P$，對於任何$p \in P$, $p$就是其中某一組密碼, $H(p)=h$，我們的目標是：給定一個$h$，我們要反推回$p$。

從上篇，我們知道可以用暴力法(需要好久好久的時間)，也可以用查表法(需要好多好多的空間)，所以！有人想到了一個新方法：Precomputed hash chains，這個方法經過改良後，進一步變成了Rainbow Table，所以先來看看這個方法的原理吧！

首先我們定義一個 reduction function $R$; $R$的功用是把hash值轉成集合$P$中的一個值。然後我們就可以利用$R$和$H$來做出hash chain。假設$P$是任意四個小寫英文字母所成的集合，hash值為32-bit的長度。那麼一個hash chain看起來會像是：

$$aaaa \xrightarrow{H} b9f4082a \xrightarrow{R} abcd \xrightarrow{H} bcd1a567 \xrightarrow{R} ccdd \xrightarrow{H} 90abff12 $$

對於hash chain我們只存頭尾，所以只剩下：

$$aaaa \xrightarrow{...} 90abff12$$

可以看出只存頭尾可以省下大量空間！

那要怎麼查呢？利用以下演算法：

### 演算法
   
**Input: 給定的某hash值**  

    查詢這個hash值是否出現在hash chain尾   

    是：
         這個chain是我們要的，從chain頭開始做，可以找到要的答案

    不是：

        利用 reduce function R 將hash值轉回某個plaintext，再對新的palintext做出新的hash值。然後回到第一步。

   

[^1]: [Rainbow table in Wikipedia](http://en.wikipedia.org/wiki/Rainbow_table)

[^2]: [How Rainbow Tables work](http://kestas.kuliukas.com/RainbowTables/)