# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

A static blog ("Put some ink into the inkpot", https://lt.ichiban.day) built with Babashka (Clojure) scripts that drive Pandoc, deployed to Cloudflare Pages. Posts are mostly written in Traditional Chinese.

Required tools: `bb` (Babashka), `pandoc`, `htmlq` (used for RSS content extraction), Node.js/npm (wrangler only).

## Commands

- `bb build` — full rebuild: deletes `_site/` and `_cache/`, regenerates Pandoc templates from Hiccup, then runs `main/build-v2`
- `bb serve` — serve `_site/` at http://localhost:8002 (`--port` to override)
- `bb gen-template` — regenerate `templates/*.html` from the Hiccup sources only
- `npx wrangler pages deploy _site` — deploy

There are no tests or linter tasks (clj-kondo config exists for editor use).

## Architecture

The build pipeline (`src/main.clj`, entry point `build-v2`) is a sequence of `process` multimethod calls dispatched on `:action`:

1. `:copy` — copies `images/`, `css/`, and `js/` into `_site/`
2. `:pandoc-posts` — converts each `posts/*.md` to HTML via Pandoc, sorted newest-first; collects post metadata into a shared `context` atom
3. `:create-file` — dumps that metadata to `_cache/allposts.yaml` and `_cache/recentposts.yaml`
4. `:pandoc-embed` — renders `archive.html` and `index.html` by feeding those YAML files to Pandoc as `--metadata-file` (the templates loop over `$posts$`)
5. `build-rss` — extracts each post's `<main>` content with `htmlq` and writes `atom.xml` (`src/template/rss.clj`)

### Templates are generated — edit the Clojure, not the HTML

`templates/*.html` are Pandoc templates **generated** from Hiccup sources in `src/template/` (`post.clj`, `index.clj`, `archive.clj`, `intro.clj`, wrapped by `default.clj`). The Hiccup embeds Pandoc template syntax (`$body$`, `$if(tags)$` …) as string literals. To change page structure, edit the `.clj` files and rerun `bb gen-template` (or `bb build`, which does it automatically).

### Posts

- Filename convention `posts/YYYY-MM-DD-slug.md` — the date is parsed **from the filename**, not the frontmatter (`src/compiler/pandoc.clj`)
- YAML frontmatter: `title`, `summary`, `tags`; add `enable:\n  toc: true` to get a table of contents (switches to `templates/post-toc.html` with `--toc --number-sections --toc-depth=2`)
- Pandoc input format is `markdown+east_asian_line_breaks+footnotes` with MathJax enabled
- Lua filters in `lua/` run on every post: `image_relative_url.lua`, `external_links.lua`, `sidenotes.lua`

### Styling

Plain CSS in `css/` (`default.css`, `table.css`, `highlight.css`). Tailwind is no longer used; `default.css` contains hand-written replacements for former Tailwind classes.

### Client-side JS

Vanilla, dependency-free scripts live in `js/` (copied verbatim by the `:copy` step). `js/toc.js` is a scroll-spy that adds `.active` to the `.toc` link of the section currently in view; it's injected via `<script src="/js/toc.js" defer>` only on TOC posts (see the `enable-toc` branch in `src/template/post.clj`).

### RSS/site config

Site title, author, root URL, and timezone live in the `rss-config` def in `src/main.clj`.
