---
title: 如何在Hakyll中設定Atom/RSS Feed
author: LT
summary: how to produce an atom/rss feed by Hakyll
tags: hakyll
---

首先這是產生Atom Feed 的function：

```haskell
renderAtom :: FeedConfiguration
           -> Context String
           -> [Item String]
           -> Compiler (Item String)
```
從function signature 可以猜出來，第一個參數是關於Configuration，第二個參數我們先不管它，第三個參數是`[Item String]`，也就是我們要產生Feed的主要內容。
因為`renderAtom`的返回值是一個`Compiler (Item String)`，所以我們可以直接傳給compiler function：

```haskell

  match "posts/*" $ do
    route $ setExtension "html"
    compile $ pandocCompiler
        >>= loadAndApplyTemplate "templates/post.html"    postCtx
        >>= saveSnapshot "content"
        >>= loadAndApplyTemplate "templates/default.html" postCtx
        >>= relativizeUrls

  create ["atom.xml"] $ do
    route idRoute
    compile $ do
      posts <- fmap (take 15) . recentFirst =<< loadAllSnapshots "posts/*" "content"
      renderAtom feedConfiguration ? posts

 feedConfiguration :: FeedConfiguration
 feedConfiguration =
   FeedConfiguration
   { feedTitle = "Put some ink into the inkpot",
     feedDescription = "Put some ink into the inkpot - a personal blog",
     feedAuthorName = "LT Tsai",
     feedAuthorEmail = "lt@ltt.pw",
     feedRoot = "https://onemouth.github.io"
   }
```

那我們現在就只剩下第二個參數`Context String`了，首先我們要知道，在Hakyll中，`Context`代表的是很多key-value所形成的一個集合。此外，
`Context`是一個Monoid，所以用`mappend`就可以新增key-value到`Conxtext`裡面。而當template需要某一個key的值，就可以從`Context`中獲得。

```haskell
postCtx :: Context String
postCtx =
  dateField "date" "%B %e, %Y"
    `mappend` defaultContext

feedCtx :: Context String
feedCtx = postCtx <> bodyField "description" 
```

defaultContext裡面的key包括:

  - `$body$`: Item 的內容
  - `$url$`: Item 的URL
  - `$path$`: Item 的原始檔案路徑
  - `$foo$`: 任何包括在Metadata中的資料

defaultContext 並不包含date的資訊，所以我們用`dateField`把寫在檔案路徑的日期parse出來，並放在`postCtx`裡面。

另外，在feed的預設template裡面，需要一個description的key，而在目前大部分RSS的網站實作中，description都是直接放文章的完整內容，
所以這邊我們用`bodyField`，把description指向body的內容。

最後，我們產生Feed的程式碼就像這樣：
```haskell
  create ["atom.xml"] $ do
    route idRoute
    compile $ do
      posts <- fmap (take 15) . recentFirst =<< loadAllSnapshots "posts/*" "content"
      renderAtom feedConfiguration feedCtx posts
```