/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/template/*.clj"
  ],
  safelist: [
    ".header-section-number",
    ".toc-section-number"
  ],
  theme: {
    extend: {},
  },
  plugins: [],
}
