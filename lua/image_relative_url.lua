PANDOC_VERSION:must_be_at_least '2.12'

local path = require 'pandoc.path'

function ImagePathToRelative(image)
    if path.is_absolute(image.src) then
        image.src = ".." .. image.src
    end
    
    return image
end

return {
    { Image = ImagePathToRelative }
}