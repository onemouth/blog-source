---
title: "[筆記] Go：Nil Map"
author: LT
summary: the bahavior of a nil map in golang
tags: golang
---


```go
package main

import (
	"fmt"
)

func main() {
	var m map[string]int
	fmt.Println(m["test"])
	m["test"]++
	fmt.Println(m["test"])
}
```

上面這段code的輸出為0，然後panic。

在這個例子，m是一個nil map。在Go中，對nil map的有一些操作是"nil safe"的：

  - `len(m)`: 0
  - `m[key]`
    - return the zero value for the value type if key is not in the map
    - return the value assoiated with the key if key is in the map

利用第二個性質，假如是empty map的話，我們可以有像是python的[defaultdict](https://docs.python.org/3/library/collections.html#collections.defaultdict)的應用。

```go
package main

import (
	"fmt"
)

func main() {
	m := map[string]int{}
	fmt.Println(m["test"])
	m["test"]++
	fmt.Println(m["test"])
}
```

除了nil map以外，像是nil slice, nil channel也都有一些"nil safe"的操作。

對nil slice可以用`len`取長度，也可以做`append`操作。

也可以對nil channel做receive value或send value的動作。(雖然這樣會永久block住)


## Reference
1. [Go Brain Teasers](https://pragprog.com/titles/d-gobrain/go-brain-teasers/)