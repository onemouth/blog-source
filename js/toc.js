// Scroll-spy: highlight the Table-of-Contents entry for the section currently
// being read. Pure vanilla JS, no dependencies. Runs only on pages with a .toc.
(function () {
  "use strict";

  var toc = document.querySelector(".toc");
  if (!toc) return;

  var links = Array.prototype.slice.call(toc.querySelectorAll('a[href^="#"]'));
  if (!links.length) return;

  // Pair each TOC link with its heading element (in document order).
  var linkFor = {};
  var headings = [];
  links.forEach(function (link) {
    var id = decodeURIComponent(link.getAttribute("href").slice(1));
    var heading = document.getElementById(id);
    if (heading) {
      linkFor[id] = link;
      headings.push(heading);
    }
  });
  if (!headings.length) return;

  var active = null;
  function setActive(link) {
    if (link === active) return;
    if (active) active.classList.remove("active");
    if (link) link.classList.add("active");
    active = link;
  }

  // The section whose heading last crossed an activation line near the top of
  // the viewport is the one being read.
  function update() {
    var line = window.innerHeight * 0.2;
    var current = headings[0];
    for (var i = 0; i < headings.length; i++) {
      if (headings[i].getBoundingClientRect().top <= line) {
        current = headings[i];
      } else {
        break;
      }
    }
    setActive(linkFor[current.id]);
  }

  var ticking = false;
  function onScroll() {
    if (ticking) return;
    ticking = true;
    window.requestAnimationFrame(function () {
      update();
      ticking = false;
    });
  }

  window.addEventListener("scroll", onScroll, { passive: true });
  window.addEventListener("resize", onScroll, { passive: true });
  update();
})();
