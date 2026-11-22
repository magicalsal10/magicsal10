---
name: web-design-best-practices
description: Pull the latest Google web best-practice guidance from GitHub before building or redesigning any website, web page, landing page, HTML/CSS template or web artifact. Use at the start of every web build.
---

# Check Google's web best practices before building

Do this **before writing any HTML/CSS** for a new site or page, every time. Check for updates; don't rely on memory.

## 1. Check for updates to Google's HTML/CSS Style Guide (primary source)

Repo: https://github.com/google/styleguide, file `htmlcssguide.md` (actively maintained, auto-exported from Google's internal guides).

```bash
D=$(mktemp -d)
git clone -q --filter=blob:none --no-checkout https://github.com/google/styleguide "$D/styleguide"
git -C "$D/styleguide" log -5 --format='%h %ad %s' --date=short -- htmlcssguide.md
curl -sL https://raw.githubusercontent.com/google/styleguide/gh-pages/htmlcssguide.md -o "$D/htmlcssguide.md"
```

- Read the fetched `htmlcssguide.md` in full and follow it. If `.claude/skills/web-design-best-practices/last-seen.txt` exists, compare its commit hash with the latest and tell the user in one line what changed since last time. Then write the new hash there.
- If the default branch isn't `gh-pages`, try `master`/`main`.

## 2. Check web.dev for current guidance

Google's older repos `google/WebFundamentals` and `GoogleChrome/web.dev` are **archived** (read-only). Their content lives on:
- https://web.dev/learn (HTML, CSS, accessibility, responsive design, images, forms, performance)
- https://web.dev/articles/vitals (Core Web Vitals: LCP, INP, CLS)
- https://developer.chrome.com/docs/lighthouse (Lighthouse audits; source at https://github.com/GoogleChrome/lighthouse)

Fetch the relevant pages for what you're building. If the network blocks them, say so and go on with the style guide.

## 3. Apply while building

At minimum, follow these points from the sources:
- Valid HTML5 with semantic elements, `lang` attribute, `<meta charset="utf-8">`, and a viewport meta tag.
- Accessibility: alt text, labelled form fields, keyboard focus styles, sufficient contrast.
- Separate structure (HTML), presentation (CSS) and behaviour (JS). Use lowercase and 2-space indentation, and don't add `type` attributes on CSS/JS.
- Responsive, mobile-first layout. Use modern image formats with explicit width and height to avoid CLS.
- Aim for good Core Web Vitals. When you can, run Lighthouse (`npx lighthouse <url> --chrome-flags="--headless --no-sandbox"`) on the result.

## 4. Report

Start your first build message with a line like: "Checked google/styleguide (latest: <hash> <date>) and web.dev". Mention anything new.
