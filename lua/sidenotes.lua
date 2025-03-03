local counter = 0

-- Process a Note block and convert it to a sidenote or marginnote
function Note(note)
  -- Increment counter for unique IDs
  counter = counter + 1
  local note_id = string.format("sn-%d", counter)
  
  -- Convert the note content to HTML
  local note_html = pandoc.write(pandoc.Pandoc({note}), 'html')
  -- Clean up the HTML
  note_html = note_html:gsub("<p>", ""):gsub("</p>", ""):gsub("\n", " "):gsub("^%s+", ""):gsub("%s+$", "")
  
  -- Check if it's a margin note (starts with {-})
  local is_margin_note = true
  local label_class = is_margin_note and "margin-toggle" or "margin-toggle sidenote-number"
  local label_symbol = is_margin_note and "<sup>†</sup>" or tostring(counter)
  local note_type_class = is_margin_note and "marginnote" or "sidenote"
  
  -- Remove {-} prefix if present
  if is_margin_note then
    note_html = note_html:gsub("^{%-}%s*", "")
  end
  
  -- For debugging
  io.stderr:write(string.format("Converting note %d: %s\n", counter, note_html))
  
  return pandoc.RawInline('html', string.format(
    '<label for="%s" class="%s">%s</label>' ..
    '%s',
    note_id, label_class, label_symbol, note_html
  ))
end
