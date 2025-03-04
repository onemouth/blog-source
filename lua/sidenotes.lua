local counter = 0

-- Equivalent to Haskell's NoteType
local NoteType = {
  SIDE_NOTE = "sidenote",
  MARGIN_NOTE = "marginnote",
  FOOT_NOTE = "footnote"
}

-- Convert blocks to inline content (similar to coerceToInline)
local function blocks_to_inline(blocks)
  local result = {}
  for _, block in ipairs(blocks) do
    if block.t == "Plain" then
      for _, inline in ipairs(block.content) do
        table.insert(result, inline)
      end
    elseif block.t == "Para" then
      for _, inline in ipairs(block.content) do
        table.insert(result, inline)
      end
      -- Add double linebreak after paragraphs
      table.insert(result, pandoc.LineBreak())
      table.insert(result, pandoc.LineBreak())
    elseif block.t == "LineBlock" then
      for i, line in ipairs(block.content) do
        for _, inline in ipairs(line) do
          table.insert(result, inline)
        end
        if i < #block.content then
          table.insert(result, pandoc.LineBreak())
        end
      end
      table.insert(result, pandoc.LineBreak())
      table.insert(result, pandoc.LineBreak())
    elseif block.t == "RawBlock" then
      table.insert(result, pandoc.RawInline(block.format, block.text))
    end
  end
  return result
end

-- Get note type from first block (similar to getFirstStr)
local function get_note_type(blocks)
  if #blocks == 0 then
    return NoteType.SIDE_NOTE, blocks
  end

  local first_block = blocks[1]
  local content = first_block.content

  if not content or #content == 0 then
    return NoteType.SIDE_NOTE, blocks
  end

  local function check_marker(content)
    if #content >= 2 and
       content[1].t == "Str" and
       content[2].t == "Space" then
      if content[1].text == "{-}" then
        return NoteType.MARGIN_NOTE, table.move(content, 3, #content, 1, {})
      elseif content[1].text == "{.}" then
        return NoteType.FOOT_NOTE, table.move(content, 3, #content, 1, {})
      end
    end
    return nil
  end

  local note_type, new_content = check_marker(content)
  if note_type then
    if first_block.t == "Plain" then
      blocks[1] = pandoc.Plain(new_content)
    elseif first_block.t == "Para" then
      blocks[1] = pandoc.Para(new_content)
    elseif first_block.t == "LineBlock" then
      if #first_block.content > 0 then
        first_block.content[1] = new_content
      end
    end
    return note_type, blocks
  end

  return NoteType.SIDE_NOTE, blocks
end

-- Process a Note block and convert it to a sidenote or marginnote
function Note(note)
  -- Increment counter for unique IDs
  counter = counter + 1
  local note_id = string.format("sn-%d", counter)

  -- Get note type and process blocks
  local note_type, blocks = get_note_type(note.content)
  
  -- If it's a footnote, return the original note
  if note_type == NoteType.FOOT_NOTE then
    return note
  end

  -- Convert blocks to inline content
  local inline_content = blocks_to_inline(blocks)

  -- Create the label HTML
  local is_margin_note = note_type == NoteType.MARGIN_NOTE
  local label_class = "margin-toggle" .. (is_margin_note and "" or " sidenote-number")
  local label_symbol = is_margin_note and "&#8853;" or ""
  local label_html = string.format(
    '<label for="%s" class="%s">%s</label>',
    note_id, label_class, label_symbol
  )

  -- Create the input HTML
  local input_html = string.format(
    '<input type="checkbox" id="%s" class="margin-toggle"/>',
    note_id
  )

  -- Create the note wrapper
  local note_html = string.format(
    '<span class="%s">%s</span>',
    note_type,
    pandoc.utils.stringify(inline_content)
  )

  -- Return the complete HTML structure
  return pandoc.RawInline(
    'html',
    string.format(
      '<span class="sidenote-wrapper">%s%s%s</span>',
      label_html,
      input_html,
      note_html
    )
  )
end
