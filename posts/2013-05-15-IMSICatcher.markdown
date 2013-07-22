---
title: IMSI Catcher, GNU Radio, 還有USRP
---

起源是因為昨天看了這篇[網誌](http://blog.cryptographyengineering.com/2013/05/a-few-thoughts-on-cellular-encryption.html)。

裡面提到了GSM的弱點：

> GSM phones authenticate to the tower, but the tower doesn't authenticate back. This means that anyone can create a 'fake' tower that your phone will connect to.
> 
> ... 
> 
> ...
> 
> For example, if all you want to do is determine which devices area in an area, you simply present yourself as a valid tower -- and see which phones connect to you (by sending their IMSI values). This is the approach taken by [IMSI-catchers](http://en.wikipedia.org/wiki/IMSI-catcher) like Stingray.

也就是說，在GSM的架構下，手機不會去要求認證基地台的身份，因此我們可以輕易的架設假的基地台，收到一大堆手機傳來的資訊。雖然現在3G普及了，但是當3G網路塞車的時候，我們的手機仍然會改用GSM來通訊。（我現在還在用2G手機...）

然後我就想說，要怎麼樣才能自己做一個基地台，查了一下，有一個不錯的library: [GNU radio](http://gnuradio.org/redmine/projects/gnuradio/wiki)，可以幫我們做這些無線電波訊號的處理。

但是！我們還需要發送和接收無線電波的硬體，也就是USRP(Universal Software Radio Peripheral)，使用USRP搭配GNU radio，一個<s>（假）</s>基地台就指日可待了！

USRP好像也不難買，網路上就有[賣](http://www.ni.com/usrp/zht/)了，但是看到價錢，大吃一驚，要十幾萬塊阿，這這，只能等哪天發了再來玩看看了。
