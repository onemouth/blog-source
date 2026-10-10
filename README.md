# Blog

## Dependencies

  - [babashka](https://babashka.org): fast native Clojure scripting runtime
  - [pandoc](https://pandoc.org): convert files from one markup format into another
  - [htmlq](https://github.com/mgdm/htmlq): like jq, but for HTML


## Commmands

- bb build: rebuild site
- bb serve: run local http server

## Writing posts

See [the post drafting and frontmatter reference](docs/authoring.md) for a copyable template, metadata fields, and a publishing checklist.

## SEO metadata and URLs

- Shared site configuration lives in `src/site.clj`.
- Set `lang: zh-Hant` for Traditional Chinese posts or `lang: en` for English posts. Posts without `lang` default to `zh-Hant`.
- Public URLs omit `.html` to match Cloudflare Pages redirects; output filenames still include `.html`.
- Canonical, Open Graph, and Twitter metadata use the same page URL and description. Unknown modification dates and unselected social images are omitted.
- Atom entry links use extensionless URLs, while existing entry IDs stay unchanged to avoid duplicate posts in feed readers.
- `bb serve` is a basic file server and does not resolve extensionless links. For a production-like preview, use `npx wrangler pages dev _site --port 8002 --compatibility-date 2026-07-01`.

## deploy

npx wrangler pages deploy _site

