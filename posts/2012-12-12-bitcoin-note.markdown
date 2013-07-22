---
title: Bitcoin 筆記和心得
---

Bitcoin是一種虛擬貨幣，有著十分有趣的原理，我也嘗試想要理解它，這篇[blog](http://blog.codingnow.com/2011/05/bitcoin.html)就解釋的很好。之後我去了Bitcoin的[官網](http://bitcoin.org/en/)，裡面有最一開始Satoshi Nakamoto寫的[paper](http://bitcoin.org/bitcoin.pdf)，這篇blog就是我閱讀後的筆記和心得。

Satoshi Nakamoto ，不知何許人也，亦不詳其真實姓名，他在2008年發表的paper，是最早提出bitcoin概念的人，他也實作了[Bitcoin-Qt](http://bitcoin.org/bitcoin.pdf)。

在虛擬貨幣的系統中，最需要防範的就是`double-spending`，畢竟數位的東西，複製是很容易的。典型的虛擬貨幣系統，會有三個角色，銀行、消費者和商店，基本上的流程和我們日常生活去銀行領錢很像。消費者向銀行領出虛擬硬幣，之後向商店買東西，商店再把虛擬硬幣存回銀行。在上述的過程中，必須要有機制防範消費者用同一個虛擬貨幣消費了兩次以上，也要防範商店存錢存了兩次以上。另一方面，匿名性也很重要，從一個虛擬貨幣是不能對應回去消費者本身的。以下<s>(抄課本)</s>列出五點虛擬貨幣需要的特性：

1. 安全性： 錢不能被偷，不能被假冒使用。
2. 不能被複製並使用： 也就是要防範`double-spending`
3. 離線付款： 指的是在銀行離線的的情況下，買賣雙方也要能完成交易。
4. 轉移性： 虛擬貨幣可以自由轉移給別人。
5. 分割性： 一個虛擬貨幣可以分割成數個小單位的虛擬貨幣。
6. 隱私：無論是銀行或是商店，都不能從虛擬貨幣中追蹤到消費者。

接著我們看看bitcoin的演算法，並注意它是怎麼達到以上要求的。


## 帳戶 ##
帳戶就是一對的公鑰和私鑰，一個使用者可擁有任意個帳戶，雖然在bitcoin的系統中，任何一個帳戶的收入支出情形，都可以被查的清清楚楚，但是因為帳戶對應到哪個使用者是無法知道的，另一方面，使用者也可以創造多個帳戶從事不同用途，基於這樣的原因，bitcoin可說是保障了隱私。

但是這樣程度的保護隱私，近來有許多研究顯示是不夠的，透過一些統計分析的方法，還是有可能追蹤到帳戶彼此間的關係。這方面可以參考[zerocoin](http://blog.cryptographyengineering.com/2013/04/zerocoin-making-bitcoin-anonymous.html)的研究，或是[這篇](http://eprint.iacr.org/2012/584.pdf)及[這篇](http://eprint.iacr.org/2012/596.pdf)

## 交易（Transactions）  ##
一個硬幣(coin)被視為一堆交易的鏈結(chain)，鏈結上的每一環，代表了一次硬幣的移轉，也就是一次交易。這些鏈結都是公開的。付款者用自己的私鑰簽署**(前一次交易+收款者的公鑰)**，這樣代表了硬幣從付款者轉移到了收款者，收款者可以依此類推，繼續轉移這枚硬幣給別人。交易的先後順序是很重要的，若我們可以確定交易的順序，就可以明確的知道是否有人意圖要`double-spending`某一枚coin，為了解決這個問題，bitcoin進一步引入了區塊。

## 區塊（Blocks） ##
區塊也形成了一個鏈結，每個區塊包含了3個部分，一是hash值，二是一個nonce，三是數個交易(transactions)。假設hash function為`H`，每個區塊的hash值即為

> `H( 上一個區塊的hash值 | nonce | transactions )`

這些hash值都必須滿足一個條件，就是hash值的前n個bit必須為0。n的值會隨系統的增長而調節，n越大就越難（因為要搜尋很多很多次nonce才能找到滿足的）。因為每個區塊都跟前一個區塊相關，要做出一個區塊又需要耗費很多的CPU資源，所以當區塊越來越長的時候，就越不可能偽造交易的歷史紀錄。
區塊的增長由P2P網路的大多數node來決定，因此，只要誠實的node的計算能力大於攻擊者的計算能力，區塊的紀錄就是安全的。

## 網路（Network） ##
參照原來的paper，整個流程是這樣子的：

> New transactions are broadcast to all nodes.

> 新的交易廣播到其他的nodes。

> Each node collects new transactions into a block.

> 每個node將收到的交易包入一個區塊。
  
> Each node works on finding a difficult proof-of-work for its block.

> 每個node開始搜尋這個區塊的nonce值。

> When a node finds a proof-of-work, it broadcasts the block to all nodes.

> 當一個node找到了符合條件的nonce值，它廣播這個區塊給其他nodes

> Nodes accept the block only if all transactions in it are valid and not already spent.
Nodes express their acceptance of the block by working on creating the next block in the chain, using the hash of the accepted block as the previous hash.

> 其他nodes收到了這個區塊，它會先驗證這個區塊裡包含的所有交易都是有效的，若他們決定要接受這個區塊，它們要計算的下一個區塊就會基於這個收到的區塊的hash值產生出來。

nodes永遠會以最長的鏈結當做正確的鏈結。

## 動機（Incentive）##
為什麼其他node要耗費自己的CPU幫其他人背書？因為在每個區塊所包含的第一個交易，是一種特殊的形式，這筆交易聲明了這個區塊的創造者，獲得了一筆收入，例如50個coin。因此每個node都希望能幫別人的交易合入區塊鏈結，好獲得收入。
所以bitcoin並不是由銀行或政府發出的，而是由演算法和使用者所產生，這也是它最有特色的地方，演算法會調節產生bitcoin的速度，最後達到上限之後，就不會產生新的bitcoin，而動機會改用交易費來取代，也就是付款者會付一筆費用給成功合成區塊的人。

事實上，這也鼓勵擁有強大計算能力的人，不去破壞，而是照著遊戲規則走，因為他可以利用強大的計算能力，賺取不少的bitcoin。


我的筆記大致是這樣，希望也來研究code看看!

