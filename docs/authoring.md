# Writing a blog post

## Start with this template

Create `posts/YYYY-MM-DD-short-slug.md` and put YAML frontmatter at the very beginning:

```markdown
---
title: "文章標題"
summary: "用一兩句話介紹文章內容，讓讀者知道這篇文章會談什麼。"
lang: zh-Hant
author: LT
tags: programming, golang
enable:
  toc: true
---

文章開場段落。

## 第一個章節

內容。

### 子章節

內容。
```

For English posts, use `lang: en` and write the title and summary in English.

## Frontmatter reference

| Field | Set it? | Format / example | Used for |
| --- | --- | --- | --- |
| `title` | Required for a complete post | `title: "設計 Golang 的 error"` | Page title, article H1, post lists, feed title, social title, and JSON-LD headline. |
| `summary` | Recommended for every post | A concise plain-text sentence or two | Search description, social descriptions, and Atom summary. HTML SEO metadata and JSON-LD fall back to the title if omitted; Atom does not. |
| `lang` | Recommended explicitly | `zh-Hant` or `en` | Document language, social locale, and JSON-LD language. Defaults to `zh-Hant` when omitted. |
| `updated_date` | Optional | `updated_date: 2026-07-12` (quoted or unquoted) | Validated modification date for sitemap `lastmod`, JSON-LD `dateModified`, Open Graph `article:modified_time`, and Atom entry `updated`. Must not precede the filename publication date. |
| `author` | Optional | `author: LT` | Visible article byline and JSON-LD author. If omitted, no visible byline is shown; JSON-LD falls back to the configured site author. Atom uses the site-wide author. |
| `tags` | Optional | `tags: programming, golang` | HTML keywords metadata. Currently does not generate visible tags or tag pages. |
| `enable.toc` | Optional | Nested YAML: `enable:` followed by `  toc: true` | Enables the table of contents, section numbering, and scroll highlighting. Omit for short posts. |

Use plain text for `title` and `summary`, not HTML or Markdown formatting. Quote YAML values containing special syntax such as `: ` or `#`; double-quoted values must escape embedded double quotes as `\"`.

## You do not need to set these

The build supplies:

- **Publication date:** parsed from the filename, not a frontmatter `date` field.
- **Public URL:** derived from the filename; for example, `posts/2026-07-10-browser-plugins.md` becomes `https://lt.ichiban.day/posts/2026-07-10-browser-plugins`.
- **Canonical URL and social URL:** use that public URL automatically.
- **Site name, root URL, feed author, and timezone:** configured in `src/site.clj`.
- **Open Graph page type:** `article` for posts.
- **Twitter Card type:** `summary`.
- **Social locale:** currently `zh_TW` for `zh-Hant`, or `en_US` for `en`.

- **JSON-LD:** the build automatically adds `BlogPosting` structured data to every article and `WebSite` data to the homepage. It reuses the fields above; do not write a separate JSON block. Changing the frontmatter and rebuilding updates it automatically.

Modification dates are supplied only through optional `updated_date`; omit it when the last substantive update is unknown. No date is inferred from build time, filesystem timestamps, or publication dates. Social-image fields are not implemented yet.

## Recording a substantive update

Add or advance `updated_date` when you meaningfully change the article: corrected explanations, new sections, or updated recommendations. Use an actual calendar date in exactly `YYYY-MM-DD` format:

```yaml
updated_date: 2026-07-12
```

Do not advance it just for a rebuild, layout/style change, or language/alt-text annotations. Invalid dates, timestamps, and dates earlier than publication fail the build with the post path and field name. Same-day publication/update is allowed. If omitted or null, SEO modification fields remain absent; the visible publication date and post ordering do not change.

Atom requires full timestamps, so an explicit update uses the feed\'s existing date-only convention: noon in the configured timezone (`+08:00`). Without `updated_date`, entry `updated` continues to equal `published`. Entry IDs remain unchanged.

## Inline language changes

Keep frontmatter `lang` as the article's primary language. Mark Japanese names or phrases inside Chinese text with Pandoc bracketed spans:

```markdown
很有懷舊([レトロ]{lang=ja})氣氛。

## [倉式珈琲店]{lang=ja}

[<ruby>隠岐 <rt>おき</rt></ruby>の島ラーメン]{lang=ja}
```

These render as `<span lang="ja">…</span>`, helping screen readers select Japanese pronunciation without changing the text. Heading annotations are also copied into the TOC. Annotate actual Japanese text, not entire mixed-language paragraphs, Chinese place names, or borrowed words indiscriminately. Keep image alt text as plain text; it cannot contain inline language markup.

## Before publishing

- [ ] Choose the filename date deliberately. Every `posts/*.md` is published by the build, including future-dated files; there is no draft/scheduling filter.
- [ ] Set `title`, a meaningful `summary`, and the correct `lang`.
- [ ] Add `author` if you want a visible byline.
- [ ] Use `##` for body sections and `###` for subsections. The template already creates the article title H1; do not add another `#` heading.
- [ ] Give informative images meaningful alt text: `![圖片說明](/images/example.png)`. For raw HTML images, set `alt="圖片說明"`. Use `alt=""` only for decoration. Markdown alt text does not create a visible caption; add captions separately if needed.
- [ ] Run `bb build`.
- [ ] Preview with `npx wrangler pages dev _site --port 8002 --compatibility-date 2026-07-01` and check the post, links, and optional TOC.

The TOC includes H2 sections and H3 subsections (`--toc-depth=3`). H4 headings stay in the article body but are not listed in the TOC. Check section coverage and numbering in the preview.
