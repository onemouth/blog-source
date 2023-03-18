---
title: Write myself a static side generator
author: LT
summary: Write myself a static side generator
tags: blog,babashka,pandoc
enable:
  toc: true
---

Originally this blog was generated using [Hakyll](https://jaspervdj.be/hakyll), but after using it for a while, although I liked its concise and simple design, I still found some aspects didn't quite suit my needs. Recently, I happened to discover [Babashka](https://babashka.org), and I thought I could use Babashka and Pandoc CLI to implement the functionalities I used in Hakyll. This blog post mainly introduces my implementation. My implementation repository can be found [here](https://github.com/onemouth/blog-source).

# Hakyll

First, I'd like to explain how Hakyll, the tool I used to use, works. Hakyll is a library written in Haskell that provides helper functions for creating static websites. It uses another library called [Pandoc](https://hackage.haskell.org/package/pandoc) to generate HTML files, and you can check out the Hakyll website (https://jaspervdj.be/hakyll/) for more information.

What I love most about Hakyll is how straightforward it is. Essentially, all you need are some HTML templates, some CSS files, some markdown files, and a set of rules that allow Pandoc to generate HTML files from Markdown. Users are free to mix other Haskell libraries with Hakyll to create their ideal website, without feeling limited by the framework provided by the static site generator.

However, there were still two aspects that I found difficult to work with. The first was my own unfamiliarity with the Haskell programming language, which meant that I had to spend a lot of time researching how to implement the features I wanted.

The other issue was that, compared to using the Pandoc Library, I preferred to use the Pandoc command-line interface (CLI). Pandoc is already a complex software, with a user manual of about 150 pages, and the functionality provided by the CLI alone is already quite complex. If I were to use the library instead, I would also have to deal with the complexity of Pandoc's internal implementation and the Haskell programming language. So, for me, using only the CLI to convert formats is much simpler.


# Babashka + Pandoc

