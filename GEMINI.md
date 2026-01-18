# Project Gemini Analysis

## Overview
This project is a static site/blog generator using **Babashka (Clojure)** for build scripting and **TailwindCSS** for styling. It uses **Cloudflare Pages** (Wrangler) for deployment.

## Prerequisites
Ensure you have the following installed:
- [Babashka](https://babashka.org) (`bb`)
- [Node.js](https://nodejs.org) & npm
- [Pandoc](https://pandoc.org) (for markdown conversion)
- [htmlq](https://github.com/mgdm/htmlq) (for HTML processing)

## Project Structure
- **src/**: Source code for the build system (Clojure).
- **posts/**: Markdown source files for blog posts.
- **templates/**: Hiccup/HTML templates.
- **css/**: Output compiled CSS.
- **_site/**: Generated static site (output directory).
- **bb.edn**: Babashka task definitions.

## workflow

### 2. Build Site
Clean and rebuild the static site:
```bash
bb build
```
This runs `rm -r _site`, `rm -r _cache`, generates templates, and runs `main/build-v2`.

### 3. Local Development (Serve)
Serve the `_site` directory on http://localhost:8002:
```bash
bb serve
```

### 4. Deployment
Deploy to Cloudflare Pages:
```bash
npx wrangler pages deploy _site
```
