--------------------------------------------------------------------------------
{-# LANGUAGE OverloadedStrings #-}

import Data.Monoid (mappend)
import qualified Data.Text as T
import Hakyll
  ( Compiler,
    Configuration (deployCommand, previewPort),
    Context,
    FeedConfiguration (FeedConfiguration, feedAuthorEmail, feedAuthorName, feedDescription, feedRoot, feedTitle),
    Item,
    applyAsTemplate,
    bodyField,
    compile,
    compressCssCompiler,
    constField,
    copyFileCompiler,
    create,
    dateField,
    defaultConfiguration,
    defaultContext,
    defaultHakyllReaderOptions,
    defaultHakyllWriterOptions,
    fromList,
    getResourceBody,
    hakyll,
    hakyllWith,
    idRoute,
    listField,
    loadAll,
    loadAllSnapshots,
    loadAndApplyTemplate,
    makeItem,
    match,
    pandocCompiler,
    pandocCompilerWith,
    readPandoc,
    recentFirst,
    relativizeUrls,
    renderAtom,
    route,
    saveSnapshot,
    setExtension,
    templateBodyCompiler,
  )
import Hakyll.Web.Feed (FeedConfiguration)
import Text.Pandoc
import Text.Pandoc.App (Opt (optSelfContained))
import Text.Pandoc.Writers as PandocWriter
import qualified Text.Pandoc.Writers.HTML as PandocWriter

--------------------------------------------------------------------------------
main :: IO ()
main = hakyllWith config $ do
  match "images/*" $ do
    route idRoute
    compile copyFileCompiler

  match "css/*" $ do
    route idRoute
    compile compressCssCompiler

  match (fromList ["about.rst", "contact.markdown"]) $ do
    route $ setExtension "html"
    compile $
      pandocCompiler
        >>= loadAndApplyTemplate "templates/default.html" defaultContext
        >>= relativizeUrls

  match "posts/*" $ do
    route $ setExtension "html"
    compile $
      pandocCompiler
        >>= loadAndApplyTemplate "templates/post.html" postCtx
        >>= saveSnapshot "content"
        >>= loadAndApplyTemplate "templates/default.html" postCtx
        >>= relativizeUrls

  match "slides/*" $
    do
      route $ setExtension "html"
      compile $
        do getResourceBody
          >>= readPandoc
          >>= Main.writeRevealJS
          >>= loadAndApplyTemplate "templates/revealjs.html" postCtx
          >>= relativizeUrls

  create ["archive.html"] $ do
    route idRoute
    compile $ do
      posts <- recentFirst =<< loadAll "posts/*"
      let archiveCtx =
            listField "posts" postCtx (return posts)
              `mappend` constField "title" "Archives"
              `mappend` defaultContext

      makeItem ""
        >>= loadAndApplyTemplate "templates/archive.html" archiveCtx
        >>= loadAndApplyTemplate "templates/default.html" archiveCtx
        >>= relativizeUrls

  create ["atom.xml"] $ do
    route idRoute
    compile $ do
      posts <- fmap (take 15) . recentFirst =<< loadAllSnapshots "posts/*" "content"
      renderAtom feedConfiguration feedCtx posts

  match "index.html" $ do
    route idRoute
    compile $ do
      posts <- recentFirst =<< loadAll "posts/*"
      let indexCtx =
            listField "posts" postCtx (return posts)
              `mappend` defaultContext

      getResourceBody
        >>= applyAsTemplate indexCtx
        >>= loadAndApplyTemplate "templates/default.html" indexCtx
        >>= relativizeUrls

  match "templates/*" $ compile templateBodyCompiler

--------------------------------------------------------------------------------
postCtx :: Context String
postCtx =
  dateField "date" "%B %e, %Y"
    `mappend` defaultContext

feedCtx :: Context String
feedCtx = postCtx <> bodyField "description"

config :: Configuration
config =
  defaultConfiguration
    { deployCommand = "cp -r _site/* ../onemouth.github.io/"
    }

feedConfiguration :: FeedConfiguration
feedConfiguration =
  FeedConfiguration
    { feedTitle = "Put some ink into the inkpot",
      feedDescription = "Put some ink into the inkpot - a personal blog",
      feedAuthorName = "LT Tsai",
      feedAuthorEmail = "and.liting@gmail.com",
      feedRoot = "https://onemouth.github.io"
    }

slidesWriterOptions :: WriterOptions
slidesWriterOptions =
  defaultHakyllWriterOptions
    { writerHTMLMathMethod = MathJax ""
    }

writeRevealJS :: Item Pandoc -> Compiler (Item String)
writeRevealJS = traverse $ \pandoc ->
  case runPure (PandocWriter.writeRevealJs slidesWriterOptions pandoc) of
    Left err -> fail $ show err
    Right x -> return (T.unpack x)
