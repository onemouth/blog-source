---
title: 如何在Hakyll中產生slide shows
author: LT
summary: how to produce html slides by Hakyll
tags: hakyll
---

我們不只可以讓Hykyll產生HTML網頁，得利於Hakyll背後的[Pandoc](https://pandoc.org/)的強大能力，只要是Pandoc支援的格式，其實都可以輸出。

Pandoc支援許多種Slide格式，像是Slidy，reveal.js，DZSlides 等等。這篇blog主要會介紹如何讓Hakyll產生reveal.js的格式。

在開始之前，我們要先看一下，在Hakyll裡面，一個Item的pandoc compile的流程大致是怎麼的。

## Pandoc Compile 流程

利用下面的函式，我們可以把一個Item，經過Pandoc之後，轉換成HTML格式。

```haskell
-- 讀取檔案內容
getResourceBody :: Compiler (Item String)

-- 將檔案內容轉換為Pandoc內部資料結構
readPandoc :: Item String  -> Compiler (Item Pandoc) 

-- 從Pandoc內部資料結構轉換為HTML格式
writePandoc :: Item Pandoc 
            -> Item String 

```

也就是可以組合成下面的 snippet：

```haskell
compile $ do
          getResourceBody
          >>= readPandoc
          >>= writePandoc
          -- ....
```

因為這個套路蠻固定的，所以Hakyll直接提供了一個`pandocCompiler`，等同於上面三個的組合

```haskell
-- 讀取檔案內容，經由Pandoc處理後輸出。
pandocCompiler :: Compiler (Item String)
```

這邊要注意的是，`writePandoc` 必定是產生HTML格式，也就是說，它呼叫的是Pandoc的[writeHtml5String](https://hackage.haskell.org/package/pandoc-2.2/docs/Text-Pandoc-Writers-HTML.html#v:writeHtml5String)這個函式。但因為我們想要產生reveal.js格式，所以應該要使用[writeRevealJs](https://hackage.haskell.org/package/pandoc-2.2/docs/Text-Pandoc-Writers-HTML.html#v:writeRevealJs)函式才對。

## 定義自己的 writePandoc

也就是說，我們不能使用預設的`writePandoc`函式，而應該自定義一個`writePandocToRevealJs`，並在裡面呼叫Pandoc的[writeRevealJs](https://hackage.haskell.org/package/pandoc-2.2/docs/Text-Pandoc-Writers-HTML.html#v:writeRevealJs)函式。


```haskell
writePandocToRevealJs :: Item Pandoc -> Compiler (Item String)
writePandocToRevealJs = traverse $ \pandoc ->
  case runPure (PandocWriter.writeRevealJs slidesWriterOptions pandoc) of
    Left err -> fail $ show err
    Right x -> return (T.unpack x)


slidesWriterOptions :: WriterOptions
slidesWriterOptions =
  defaultHakyllWriterOptions
    { writerHTMLMathMethod = MathJax "" -- 使用MathJax
    }
```

而整個Rule monad就會像下列這樣：
```haskell
  match "slides/*" $
    do
      route $ setExtension "html"
      compile $
        do getResourceBody
          >>= readPandoc
          >>= writePandocToRevealJs
          >>= loadAndApplyTemplate "templates/revealjs.html" postCtx
          >>= relativizeUrls


```