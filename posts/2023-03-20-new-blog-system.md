---
title: Write myself a static site generator
author: LT
summary: Write myself a static site generator
tags: blog,babashka,pandoc
enable:
  toc: true
---

Originally this blog was generated using [Hakyll](https://jaspervdj.be/hakyll), but after using it for a while, although I liked its concise and simple design, I still found some aspects didn't quite suit my needs. Recently, I happened to discover [Babashka](https://babashka.org), and I thought I could use Babashka and Pandoc CLI to implement the functionalities I used in Hakyll. This blog post mainly introduces my implementation. My implementation repository can be found [here](https://github.com/onemouth/blog-source).

# Hakyll

First, I'd like to explain how Hakyll, the tool I used to use, works. Hakyll is a library written in Haskell that provides helper functions for creating static websites. It uses another library called [Pandoc](https://hackage.haskell.org/package/pandoc) to generate HTML files, and you can check out the Hakyll website (https://jaspervdj.be/hakyll/) for more information.

What I love most about Hakyll is how straightforward it is. Essentially, all you need are some HTML templates, some CSS files, some markdown files, and a set of rules that allow Pandoc to generate HTML files from Markdown. Users are free to mix other Haskell libraries with Hakyll to create their ideal website, without feeling limited by the framework provided by the static site generator.

However, there were still two aspects that I found difficult to work with. The first was my own unfamiliarity with the Haskell programming language, which meant that I had to spend a lot of time researching how to implement the features I wanted.

The other issue was that, compared to directly using the Pandoc Library, I preferred to use the [Pandoc command-line interface (CLI)](https://pandoc.org/#). Pandoc is already a complex software, with a user manual of about 150 pages, and the functionality provided by the CLI alone is already quite complex. If I were to use the library instead, I would also have to deal with the complexity of Pandoc's internal implementation and the Haskell programming language. So, for me, using only the CLI to convert formats is much simpler.


# Babashka 

In fact, several years ago when I began learning Clojure, I had the idea that a powerful dynamic language like Clojure would be great as a script language, capable of replacing Python or Bash for some quick tasks. However, in practice, because Clojure is a dialect running on the JVM, its startup speed is quite slow, making it not very suitable for writing CLI tools.

Until recently, I discovered [Babashka](https://babashka.org), also known as "Fast native Clojure scripting runtime". From the introduction on the official website, Babashka is designed to replace Bash scripts with a new designed runtime. It uses GraalVM native image technology, which makes its startup speed comparable to that of Python 3. When I found out about Babashka, I thought that using it along with other Clojure libraries would be a good way to quickly implement most of the functionality of Hakyll.

My plan is to replace the HTML templates in Hakyll with [Hicuup](https://github.com/weavejester/hiccup). I will also use the [process library](https://github.com/babashka/process) built into Babashka to call the Pandoc program. Additionally, I will use Babashka's [task runner](https://book.babashka.org/#tasks) to implement features such as `build` and `serve`.

The whole process was much easier than I expected and very interesting. Currently, I have removed the use of Hakyll completely from the source code. Of course, I haven't fully implemented all of Hakyll's features, but for now it meets my needs. Since I built it myself, I have a better understanding of the entire website building process and have more control over it than before.

# Conclustion

If you use a static site generator, you may find yourself creating your own generator. That's what happened to me - I ended up building my own one.

