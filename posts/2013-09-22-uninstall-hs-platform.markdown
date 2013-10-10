---
title: 移除haskell platform
tags: haskell
---
<p>
終於受不了我macbook上的cabal hell了。
</p>

<p>
發現可能是haskell-platform上一版沒移除乾淨，結果這一版`cabal install` 一直有問題。
</p>

<p>
決定移除乾淨，再重新安裝。 這次不由官網上抓dmg檔來裝了，改用[homebrew](http://brew.sh/)，應該會比較好管理。
</p>

<p>
另外，除了真的很廣泛的套件外，一律用`cabal-dev`來安裝，昨天先用`cabal-dev`裝了`hakyll`，應該沒什麼問題，相依性也不會衝到。        
</p>
 