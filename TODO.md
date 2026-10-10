# TODO — SEO implementation plan

SEO audit implementation checklist for `https://lt.ichiban.day`.

> Templates under `templates/` are generated. Edit the Hiccup sources in `src/template/` and run `bb gen-template` or `bb build`; do not edit generated HTML directly.

## P0 — Crawling, indexing, and URL correctness

### 1. Add `/robots.txt`

- [x] Generate `_site/robots.txt` during `main/build-v2` in `src/main.clj`.
- [x] Use the following content:

  ```text
  User-agent: *
  Allow: /
  Sitemap: https://lt.ichiban.day/sitemap.xml
  ```

- [x] Confirm the deployed URL returns plain text rather than homepage HTML.

### 2. Add `/sitemap.xml`

- [x] Add a sitemap renderer, preferably `src/template/sitemap.clj`.
- [x] Generate the sitemap from the post metadata already collected in the build context.
- [x] Include absolute URLs for:
  - `https://lt.ichiban.day/`
  - `https://lt.ichiban.day/archive.html`
  - Every generated `/posts/*.html` page
- [x] Omit `<lastmod>` initially: filename dates represent publication, not necessarily the last substantive modification. Track reliable modification dates in P3.
- [x] XML-escape all generated values.
- [x] Do not include assets, cache files, or the feed.
- [x] Write the result to `_site/sitemap.xml` and validate it as XML.

Relevant files: `src/main.clj`, new `src/template/sitemap.clj`.

### 3. Fix Atom discovery and labeling

- [x] Change the discovery link in `src/template/default.clj` from `atom.xml` to `/atom.xml` or its absolute URL.
- [x] Keep `type="application/atom+xml"`.
- [x] Rename the navigation label from `RSS` to `Atom`.
- [x] Only retain the label `RSS` if a separate RSS 2.0 feed is implemented.
- [x] Confirm nested posts resolve the discovery link to `/atom.xml`, not `/posts/atom.xml`.

## P1 — Canonical and social metadata

### 4. Establish shared site and page metadata

- [ ] Generalize the current `rss-config` in `src/main.clj` into site-wide configuration containing:
  - Site name
  - Root URL
  - Default author
  - Default language
  - Homepage description
- [ ] Continue passing the relevant configuration to Atom generation.
- [ ] Pass these values to every Pandoc page build:
  - Absolute page URL
  - Page description
  - Page type (`website` or `article`)
  - Page language
  - ISO publication/modification dates where applicable
- [ ] Keep the human-readable post date separate from the ISO date used by metadata.

Relevant files: `src/main.clj`, `src/compiler/pandoc.clj`.

### 5. Add absolute canonical URLs

- [ ] Add exactly one `<link rel="canonical">` to the common `<head>`.
- [ ] Use these canonical targets:
  - Homepage: `https://lt.ichiban.day/`
  - Archive: `https://lt.ichiban.day/archive.html`
  - Posts: their absolute `/posts/*.html` URLs
- [ ] Ensure canonical URLs are absolute and do not redirect.

Relevant files: `src/template/default.clj`, `src/main.clj`.

### 6. Add Open Graph metadata

- [ ] Add common properties:
  - `og:site_name`
  - `og:title`
  - `og:description`
  - `og:url`
  - `og:type`
  - `og:locale`
- [ ] Use `website` for homepage/archive and `article` for posts.
- [ ] Add article-specific properties:
  - `article:published_time`
  - `article:modified_time`
  - Optionally `article:author`
  - Optionally repeated `article:tag`
- [ ] Use absolute URLs.
- [ ] Do not add `og:image` until a suitable, stable social image exists.

Relevant files: `src/template/default.clj`, `src/main.clj`.

### 7. Add Twitter Card metadata

- [ ] Add `twitter:card` with `summary`.
- [ ] Add `twitter:title` and `twitter:description`.
- [ ] Add image/account properties only when real corresponding assets/accounts are selected.
- [ ] Keep Twitter values consistent with Open Graph values.

Relevant file: `src/template/default.clj`.

## P1 — Language and heading structure

### 8. Make document language page-specific

- [ ] Replace the hard-coded `lang="en"` in `src/template/default.clj` with page metadata.
- [ ] Support a `lang` frontmatter value for posts.
- [ ] Document a fallback language, likely `zh-Hant` for this predominantly Traditional Chinese blog.
- [ ] Explicitly mark genuinely English posts as `en`; `posts/2023-03-20-new-blog-system.md` is an obvious candidate.
- [ ] Decide the homepage language based on its final visible copy; it is currently mostly English.
- [ ] Map HTML language values to suitable OG locales, such as `zh_TW` and `en_US`.
- [ ] Use inline `lang` attributes for substantial Japanese or other-language passages where useful.

Relevant files: `src/template/default.clj`, `src/main.clj`, all `posts/*.md`.

### 9. Add a descriptive homepage H1

- [ ] Add exactly one descriptive H1 in `src/template/index.clj`.
- [ ] Keep “About Me” and the recent-posts heading as H2.
- [ ] Correct “Recently Posts” to “Recent Posts”.
- [ ] Confirm the homepage heading hierarchy is H1 followed by H2 sections.

### 10. Normalize article heading hierarchy

The article template already renders the article title as H1. Markdown body sections should therefore begin at H2.

- [ ] Change top-level Markdown sections from `#` to `##` and shift child headings consistently in:
  - `posts/2022-03-10-japanese-plugins.md`
  - `posts/2022-09-20-golang-errors.md`
  - `posts/2023-01-02-consistent-hash.md`
  - `posts/2023-03-20-new-blog-system.md`
  - `posts/2023-09-24-jp-okayama-trip.md`
  - `posts/2025-01-04-unicode-intro.md`
  - `posts/2025-01-11-unicode-flag.md`
  - `posts/2026-07-10-browser-plugins.md`
- [ ] Do not shift posts that already correctly begin at H2.
- [ ] Revisit `--toc-depth=2`; increase it if H3 sections should remain in the TOC.
- [ ] Test `--number-sections` with H2-first content and prevent numbering such as `0.1`.
- [ ] Confirm every generated article has exactly one H1 and no skipped heading levels.

Relevant files: listed posts, `src/main.clj`, possibly `src/template/post.clj`.

## P2 — Image accessibility

### 11. Add meaningful alt text

- [ ] Add descriptive alt text to four images in `posts/2022-03-10-japanese-plugins.md`.
- [ ] Add descriptive alt text to three diagrams in `posts/2023-01-02-consistent-hash.md`.
- [ ] Add `alt` attributes to three raw HTML images in `posts/2023-09-24-jp-okayama-trip.md`.
- [ ] Add suitable alt text to `/images/student.png` in `src/template/index.clj`, or `alt=""` if it is purely decorative.
- [ ] Describe each image's purpose or information rather than its filename.
- [ ] Confirm every generated `<img>` has an `alt` attribute.
- [ ] Preserve the URL-rewriting behavior in `lua/image_relative_url.lua`.

## P2 — Structured data

### 12. Add homepage `WebSite` JSON-LD

- [ ] Emit one `application/ld+json` block on the homepage.
- [ ] Include:
  - `@context`
  - `@type: WebSite`
  - `name`
  - `url`
  - `description`
  - Author or publisher represented as a `Person`
- [ ] Use the canonical homepage URL.
- [ ] Do not add a search action because the site has no site search.

### 13. Add article `BlogPosting` JSON-LD

- [ ] Emit one `application/ld+json` block on every article.
- [ ] Include:
  - `@context`
  - `@type: BlogPosting`
  - `headline`
  - `description`
  - `url`
  - `mainEntityOfPage`
  - `author`
  - `datePublished`
  - `dateModified`
  - `inLanguage`
- [ ] Use ISO 8601 dates rather than display dates such as `Jan 02, 2023`.
- [ ] Fall back to the configured site author when post frontmatter omits `author`.
- [ ] Initially use the publication date as `dateModified` unless real modification metadata is added.
- [ ] Only include `image` where a representative image has been deliberately selected.
- [ ] Generate JSON with a serializer rather than manual string concatenation.
- [ ] Ensure Pandoc interpolation does not HTML-escape the JSON into invalid content.

Relevant files: `src/main.clj`, `src/compiler/pandoc.clj`, `src/template/default.clj`, and optionally a new SEO helper namespace.

## P3 — Reliable modification dates

- [ ] Add an optional `modified` frontmatter field with an ISO 8601 date for substantive article updates.
- [ ] Parse and validate it in `src/compiler/pandoc.clj`; reject invalid dates or dates before publication.
- [ ] Use explicit modification metadata for sitemap `<lastmod>`; omit it when unknown.
- [ ] Reuse it for JSON-LD `dateModified` and Open Graph `article:modified_time`.
- [ ] Do not infer modification dates from build timestamps or filesystem modification times.
- [ ] Document when authors should update `modified` (meaningful content changes, not every rebuild or cosmetic edit).

Relevant files: `posts/*.md`, `src/compiler/pandoc.clj`, `src/main.clj`, sitemap and SEO renderers.

## Build and verification

P0 local validation completed: `bb build`; 18 sitemap URLs (homepage, archive, 16 posts); XML parsing and escaping; Atom discovery and labels on all 18 pages; `bb serve` returned real robots/sitemap content with suitable content types. Live deployment checks also passed: robots.txt and sitemap.xml return 200 with suitable content types; sitemap and Atom parse as XML; all 18 listed pages load and advertise the correct Atom discovery link and label. Python's default HTTP client received 403 while browser-style requests succeeded; review Cloudflare crawler access separately. P1/P2 checks below are not yet complete.

- [ ] Run `bb gen-template`.
- [ ] Inspect generated `templates/*.html`, but do not edit them directly.
- [ ] Run `bb build`.
- [ ] Confirm `_site/robots.txt`, `_site/sitemap.xml`, and `_site/atom.xml` exist.
- [ ] Parse `sitemap.xml` and `atom.xml` as XML.
- [ ] Confirm every sitemap URL maps to a generated page.
- [ ] Confirm each HTML document has one valid `lang` value.
- [ ] Confirm each indexable page has exactly one absolute canonical link.
- [ ] Confirm every page has exactly one H1.
- [ ] Confirm every image has an `alt` attribute.
- [ ] Confirm nested posts advertise `/atom.xml`.
- [ ] Confirm every JSON-LD block parses as JSON.
- [ ] Inspect homepage, archive, one TOC post, and one non-TOC post with `bb serve`.
- [ ] Validate representative pages with Schema.org or Google Rich Results tooling.
- [x] After deployment, verify `/robots.txt` and `/sitemap.xml` return their actual content and suitable content types rather than homepage HTML.
