---
title: bookmarklet(小書籤)心得
tags: javascript, bookmarklet
---

我手上剛好有一個網站需要測試，想要自訂一下 javascript 做一些壞事，看這個網站能不能通過考驗。

想要做這樣的網頁 addon 有很多種方法，例如 [greasemonkey](http://en.wikipedia.org/wiki/Greasemonkey), 或 [Chrome extension](http://developer.chrome.com/extensions/index.html)，但總覺得有點笨重，我需要的只是快速輕巧的跑幾行 javascript 而已。

因此後來發現了 bookmarklet 很符合我的需求，詳細的介紹我覺得[這篇](http://www.ruanyifeng.com/blog/2011/06/a_guide_for_writing_bookmarklet.html)寫的很好，我只額外講一下我開發的過程，我利用 Chrome 開發工具的 Console 先試驗我要跑的 javascript，再利用這篇的方法把這幾行 code 合成一個 bookmarklet ，當然也可以用 Firefox 上的 firebug來輔助開發，值得注意的是 Chrome 開發工具的方便程度居然已經跟 firebug 不相上下，甚至某些方面還超越了! 這對刻板印象是 firebug 比較強的我，覺得還蠻驚訝的。

成果因為是跟工作有關，就不放上來了 :p, 若只是需要一個輕量的 addon, bookmarklet 的開發真的很方便。







