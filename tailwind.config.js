/** @type {import('tailwindcss').Config} */

const defaultTheme = require('tailwindcss/defaultTheme')

module.exports = {
  content: [
    "./src/template/*.clj"
  ],
  safelist: [
    ".header-section-number",
    ".toc-section-number"
  ],
  theme: {
    extend: {
      fontFamily: {
        'sans': ["HunInn", ...defaultTheme.fontFamily.sans],
      },
    },
  },
  plugins: [],
}
