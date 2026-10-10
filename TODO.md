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
  - `https://lt.ichiban.day/archive`
  - Every generated post using its extensionless `/posts/*` public URL
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

- [x] Use shared `site/config` in `src/site.clj` for:
  - Site name
  - Root URL
  - Default author
  - Default language
  - Homepage description
- [x] Continue passing the relevant configuration to Atom generation.
- [x] Pass these values to every Pandoc page build:
  - Absolute page URL
  - Page description
  - Page type (`website` or `article`)
  - Page language
  - ISO publication dates; unknown modification dates deferred to P3
- [x] Keep the human-readable post date separate from the ISO date used by metadata.

Relevant files: `src/site.clj`, `src/main.clj`, `src/compiler/pandoc.clj`.

### 5. Add absolute canonical URLs

Cloudflare Pages redirects `.html` URLs to extensionless URLs. Canonical, sitemap, Atom links, navigation, and post lists now use extensionless public URLs; generated files remain `.html`. Existing Atom entry IDs are preserved to avoid duplicate posts in feed readers.


- [x] Add exactly one `<link rel="canonical">` to the common `<head>`.
- [x] Use these canonical targets:
  - Homepage: `https://lt.ichiban.day/`
  - Archive: `https://lt.ichiban.day/archive`
  - Posts: their absolute extensionless `/posts/*` URLs
- [x] Ensure canonical URLs are absolute and do not redirect.

Relevant files: `src/template/default.clj`, `src/main.clj`.

### 6. Add Open Graph metadata

- [x] Add common properties:
  - `og:site_name`
  - `og:title`
  - `og:description`
  - `og:url`
  - `og:type`
  - `og:locale`
- [x] Use `website` for homepage/archive and `article` for posts.
- [x] Add article-specific properties:
  - `article:published_time`
  - Omit `article:modified_time` until reliable modification dates exist (P3)
  - Optional `article:author` deferred until an author profile URL is selected
  - Optional repeated `article:tag` deferred
- [x] Use absolute URLs.
- [x] Do not add `og:image` until a suitable, stable social image exists.

Relevant files: `src/template/default.clj`, `src/main.clj`.

### 7. Add Twitter Card metadata

- [x] Add `twitter:card` with `summary`.
- [x] Add `twitter:title` and `twitter:description`.
- [x] Add image/account properties only when real corresponding assets/accounts are selected.
- [x] Keep Twitter values consistent with Open Graph values.

Relevant file: `src/template/default.clj`.

## P1 — Language and heading structure

### 8. Make document language page-specific

- [x] Replace the hard-coded `lang="en"` in `src/template/default.clj` with page metadata.
- [x] Support a `lang` frontmatter value for posts.
- [x] Document a fallback language, likely `zh-Hant` for this predominantly Traditional Chinese blog.
- [x] Explicitly mark genuinely English posts as `en`; `posts/2023-03-20-new-blog-system.md` is an obvious candidate.
- [x] Decide the homepage language based on its final visible copy; it is currently mostly English.
- [x] Map HTML language values to suitable OG locales, such as `zh_TW` and `en_US`.
- Inline Japanese language annotations are deferred to P3.

Relevant files: `src/template/default.clj`, `src/main.clj`, all `posts/*.md`.

P1-A validation: `bb build` passed; all 18 pages passed metadata, language, and URL consistency checks; HTML escaping tested with quotes, ampersands, and angle brackets; sitemap/Atom XML and stable Atom IDs verified. Wrangler local preview passed with compatibility date `2026-07-01`; all 18 selected canonical targets return HTTP 200 directly on production. Production verification passed for all 18 pages: canonical/social metadata, descriptions, language, publication dates, Atom discovery, and clean links; all selected URLs return HTTP 200 without redirects. Robots, sitemap, and Atom checks also passed. Heading normalization is tracked in P1-B below.

### 9. Add a homepage H1

- [x] Use “About Me” as the sole homepage H1 in `src/template/index.clj` (user preference).
- [x] Keep “Recent Posts” as H2.
- [x] Correct “Recently Posts” to “Recent Posts”.
- [x] Build and verify the homepage heading hierarchy: H1 “About Me”, then H2 “Recent Posts”.

### 10. Normalize article heading hierarchy

The article template already renders the article title as H1. Markdown body sections should therefore begin at H2.

- [x] Change top-level Markdown sections from `#` to `##` and shift child headings consistently in:
  - `posts/2022-03-10-japanese-plugins.md`
  - `posts/2022-09-20-golang-errors.md`
  - `posts/2023-01-02-consistent-hash.md`
  - `posts/2023-03-20-new-blog-system.md`
  - `posts/2023-09-24-jp-okayama-trip.md`
  - `posts/2025-01-04-unicode-intro.md`
  - `posts/2025-01-11-unicode-flag.md`
  - `posts/2026-07-10-browser-plugins.md`
- [x] Do not shift posts that already correctly begin at H2.
- [x] Increase TOC depth to 3 so H2 sections and H3 subsections remain listed.
- [x] Test `--number-sections` with H2-first content and prevent numbering such as `0.1`.
- [x] Confirm every generated article has exactly one H1 and no skipped heading levels.

Relevant files: listed posts, `src/main.clj`, possibly `src/template/post.clj`.

### Archive heading

- [x] Change archive “Articles” from H2 to H1 in `src/template/archive.clj`.

P1-B local validation passed: all 18 pages have exactly one H1 and no skipped heading levels; heading text, anchor IDs, section numbering, and TOC coverage match the pre-change output. All eight TOC articles passed simulated scroll/resize highlighting checks using the unchanged `js/toc.js`. Metadata, sitemap, and Atom checks passed; local Cloudflare preview served all 18 pages and supporting assets. Cloudflare preview verification passed: all 18 deployed preview pages matched the validated local build. Production deployment was reported complete by the user; a separate production verification remains pending.

## P2 — Image accessibility

### 11. Add meaningful alt text

- [x] Add descriptive alt text to four images in `posts/2022-03-10-japanese-plugins.md`.
- [x] Add descriptive alt text to three diagrams in `posts/2023-01-02-consistent-hash.md`.
- [x] Add `alt` attributes to three raw HTML images in `posts/2023-09-24-jp-okayama-trip.md`.
- [x] Add suitable alt text to `/images/student.png` in `src/template/index.clj`, or `alt=""` if it is purely decorative.
- [x] Describe each image's purpose or information rather than its filename.
- [x] Confirm every generated `<img>` has an `alt` attribute.
- [x] Preserve the URL-rewriting behavior in `lua/image_relative_url.lua`.

## P2 — Structured data

### 12. Add homepage `WebSite` JSON-LD

- [x] Emit one `application/ld+json` block on the homepage.
- [x] Include:
  - `@context`
  - `@type: WebSite`
  - `name`
  - `url`
  - `description`
  - Author or publisher represented as a `Person`
- [x] Use the canonical homepage URL.
- [x] Do not add a search action because the site has no site search.

### 13. Add article `BlogPosting` JSON-LD

- [x] Emit one `application/ld+json` block on every article.
- [x] Include:
  - `@context`
  - `@type: BlogPosting`
  - `headline`
  - `description`
  - `url`
  - `mainEntityOfPage`
  - `author`
  - `datePublished`
  - Omit unknown `dateModified` until P3
  - `inLanguage`
- [x] Use ISO 8601 dates rather than display dates such as `Jan 02, 2023`.
- [x] Fall back to the configured site author when post frontmatter omits `author`.
- [x] Omit unknown `dateModified`; do not substitute the publication date. Reliable modification metadata is deferred to P3.
- [x] Only include `image` where a representative image has been deliberately selected.
- [x] Generate JSON with a serializer rather than manual string concatenation.
- [x] Ensure Pandoc interpolation does not HTML-escape the JSON into invalid content.

Relevant files: `src/main.clj`, `src/compiler/pandoc.clj`, `src/template/default.clj`, and optionally a new SEO helper namespace.

P2 local validation passed: `bb build` and `bb test` (3 tests, 28 assertions); all 17 JSON-LD blocks parse and match page metadata, author defaults, canonical URLs, and dates; all 11 page images have appropriate alt attributes. Implicit figures are disabled so alt text does not create visible captions. HTML/script escaping is tested through Pandoc, including embedded `</script>` content. H1, TOC anchors, social metadata, sitemap, and stable Atom IDs passed regression checks. Local Cloudflare preview served all 18 pages. External Schema.org/Google tooling and deployed P2 verification remain pending.

## P3 — Inline language annotations

- [x] Review substantial Japanese passages in otherwise Chinese articles.
- [x] Add inline `lang="ja"` attributes where useful for screen-reader pronunciation.
- [x] Preserve each page's primary document language and existing content.

Review found short Japanese names and phrases rather than long passages. Added five Japanese spans in `posts/2023-09-24-jp-okayama-trip.md`: three restaurant headings, the menu flavor 濃厚貝出汁, and レトロ. Kept Chinese narration, Chinese place names, and mixed-language image descriptions in the page's primary language. Pandoc also preserves annotations in the TOC and Atom content; ruby pronunciation remains intact.

Validation passed: `bb build`; `bb test` (4 tests, 39 assertions); all 18 pages retain their document language, text, heading IDs/numbering, links, images, and SEO metadata. Sitemap/robots remain unchanged; Atom annotations, text, dates, and stable IDs are verified. Authoring syntax is documented in `docs/authoring.md`. No commit or deployment yet.

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
- [x] Confirm every image has an `alt` attribute.
- [ ] Confirm nested posts advertise `/atom.xml`.
- [x] Confirm every JSON-LD block parses as JSON.
- [ ] Inspect homepage, archive, one TOC post, and one non-TOC post with `bb serve`.
- [ ] Validate representative pages with Schema.org or Google Rich Results tooling.
- [x] After deployment, verify `/robots.txt` and `/sitemap.xml` return their actual content and suitable content types rather than homepage HTML.
