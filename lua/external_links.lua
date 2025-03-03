PANDOC_VERSION:must_be_at_least '2.12'

function Link(el)
  -- Check if the link is external (starts with http:// or https://)
  local url = el.target
  if url:match("^https?://") then
    -- Add external-link class to the link
    el.classes = el.classes or {}
    table.insert(el.classes, "external-link")
  end
  return el
end 

return {
  { Link = Link }
}