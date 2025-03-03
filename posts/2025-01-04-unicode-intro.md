---
title: 又一篇Unicode的入門介紹
summary: Unicode Introduction
tags: unicode,utf-8,emoji
enable:
  toc: true
---

一直以來，我對於Unicode總是處於一知半解的情況，剛好最近在上[CS50](https://learning.edx.org/course/course-v1:HarvardX+CS50+X/home "CS50")，在第0講就提到了如何用0，1編碼表達文字，也從ASCII講到了Unicode，我也趁這個機會從頭學習了Unicode，以下就是我從CS50還有其他網路資源學習到的內容。

# Unicode 簡介
Unicode的目標是提供一個文字編碼的方案，可以包含所有人類想要數位化的文字符號。

Unicode將每個*字*對應到一個Code point。Code point 可以簡單理解為一個數字，為了方便起見，慣例用16進位表示這個數字。例如😂 對應到的是U+1F602這個數字。

因為可包含的文字符號很多，Unicode把可以編碼的文字符號劃分為17個Code Plane，每個 Plane 又包含了65536個Code Point。
所以 Unicode 可以囊括的文字上限為 65536 \* 17 =1,114,112 個文字。

## Code planes
Plane 的編號從0到16，共17個。其中最重要的是*Plane 0*，也稱為**BMP** (Basic Multilingual Plane)。大部分語言的常用字符，都包含在BMP裡面，常用的CJK文字也在BMP裡面。BMP的範圍是從0000到FFFF，也就是能用2個byte表示的範圍。

其他的Plane大致可稱為 Supplementary Planes，詳細的劃分可以參考[維基頁面](https://en.wikipedia.org/wiki/Plane_(Unicode) "Plane (Unicode)")。

### BMP’s Surrogate area 
在BMP中有一塊範圍從 U+D800 到U+DFFF，這個範圍被稱為Surrogate area。
Surrogate area 是一塊保留區，不會有任何字符被指定到這個區域，它的主要功能是讓UTF-16 可以用這塊區域去編碼超過2byte 大小的code point。

Surrogate area 可以再分為High Surrogate 和 Low Surrogate。
High Surrogate 的範圍是從 `U+D800` 到 `U+DBFF`，而 Low Surrogate的範圍是從`U+DC00` 到 `U+DFFF` 。 不難看出, High Surrogate 和 Low Surrogate 的大小都是 4 \* 256，所以當我們把 High Surrogate  和 Low Surrogate 組合起來的話，就可以得到4\*256*4\*256 種可能，等於 16\*65536，也就是 Plane 1 到 Plane 16所需要的個數！

# Encoding
雖然我們定義了Code point，但Code point 只是一個數字，接下來，我們還需要編碼這個數字，讓電腦可以讀取。編碼的方法有很多種，也各自有優缺點，現在最常見的3種是UTF-32, UTF-16, 和UTF-8。

## UTF-32
UTF-32 簡單暴力，因為每個Unicode code point 其實只需要21個bit就可以表示出來，UTF-32直接用32個bit表示，多出來的11個bit就直接補0。

好處是有兩個，第一個好處是code point 和 binary number 直接對應，不需要額外的複雜encoding 規則。延伸出來的第二個好處就是UTF-32是一個固定長度的編碼，所以我們要找到某個字串的第N個字，假如字串是UTF-32編碼，就可以在常數時間找到，但要注意的是，現在的Unicode編碼有像Skin tone modifier, Zero-width joiner 等修飾字符，所以這個常數時間定位，也未必能跟眼睛所見的、螢幕上顯示的字符做一對一的定位。

壞處也很顯而易見，就是空間的浪費，尤其我們之前提到，常用字都在BMP，只需要2 bytes 就可以表示，UTF-32卻一律都使用了4 bytes。另外一個問題是可能需要BOM(Byte Order Mark)[^bom]，來確定位元順序是little-endian 還是 big-endian。因為空間的浪費，所以UTF-32在實務上比起UTF-16，UTF-8來說很少使用。

另外，在有些文獻可能會提到UCF-4這個詞，簡單來說UTF-32跟UCF-4是一樣的東西，只是名稱上的差異。

## UTF-16
UTF-16 用兩個byte編碼BMP，對於其他plane，則用上述提到過的Surrogate area來編碼，也就是說，在BMP之內的字符，會是2個bytes長，BMP之外的字符，則會是4個bytes 長。

好處是常用字符(BMP)都只需要2個bytes，節省空間。壞處是不兼容ASCII, 原先用ASCII表示的話，只需要1 byte 的英文字母，用UTF-16則需要2 bytes。 另外也需要 BOM 來確定位元順序。

在有些地方可以看到USC-2這個詞，可以把USC-2編碼視為不支持Surrogate area的UTF-16,也就是說，USC-2 只會有2 bytes而且只能處理BMP有的字符。 

## UTF-8
UTF-8[^utf8]的特色就是相容[ASCII](https://zh.wikipedia.org/zh-tw/ASCII)，所以只需要1 byte就能表示英文字母，對於相容只支援ASCII的舊系統也相當方便。 另外，UTF-8在設計上不需要BOM。
缺點是對CJK的編碼需要3 bytes。

### 編碼規則
1 byte的情況：1 byte 完全相容 ASCII，為 `0xxxxxxx` 格式，只要byte的第1個 bit 是 0, 就一定是 ASCII。

多個bytes 的情況: 第一個byte的開始有連續幾個1，就是代表這組總共有幾個bytes。 且第一個 byte 之後同一組的byte，都會是 `10xxxxxx ` 格式。

- 2 bytes: `110xxxxx 10xxxxxx` 
- 3 bytes: `1110xxxx 10xxxxxx 10xxxxxx`
- 4 bytes: `11110xxx 10xxxxxx 10xxxxxx 10xxxxxx`

簡單算一下，2 bytes 可以包含 2^11 =2,048 個 code points。
3 bytes 可以包含 2^16=65,536 個 code points (1 個 plane 的大小)
4 bytes 可以包含 2^21=2,097,152  個 code points (整個 unicode 的大小)

### MySQL 的故事
MySQL 裡面有 utfmb3 編碼跟 utfmb4 編碼，基本上utfmb3 是一個實作上的錯誤，它雖然是utf-8編碼卻最多只能有3個byte，無法正確存放所有Unicode。也因此，在使用 Mysql 的utf 編碼時，務必要使用utfmb4 編碼。

# Emoji
Unicode也包含了大家喜歡的表情符號emoji，例如😂😭等等，是在網路上與人交流不可或缺的工具。因為emoji太好用了，所以大家就想加越來越多的emoji到Unicode裡，像是各種膚色的emoji 👍 👍🏻，或是帶著筆電的男人👨🏽‍💻，或是有著紅頭髮的女人👩🏻‍🦰......等等。這些emoji的膚色或攜帶物品或髮型，可以有各種組合，假如每個組合都是一個獨立的emoji，那我們的Unicode就不夠用了，為了避免這個問題，我們就有了Skin tone modifier, ZWJ 等特殊字元，來解決emoji的組合問題。

## Skin tone modifier
Skin tone modifier 總共有5個，它可以改變emoji的膚色，總共有5個，分別是

- `U+1F3FB` 🏻
- `U+1F3FC` 🏼
- `U+1F3FD` 🏽
- `U+1F3FE` 🏾
- `U+1F3FF` 🏿

把它加在原來emoji的後面，就可以改變emoji的膚色。例如 `U+1F44D` 是預設黃色的👍，`U+1F44D U+1F3FB` 就會變成淺皮膚色的👍🏻。

## Zero-width joiner (ZWJ)
ZWJ(`U+200D`)可以結合多個emoji而產生出一個新的emoji，利用ZWJ，我們可以用原有的emoji為基礎，而定義出新的emoji。例如當我們想要有一個emoji是紅頭髮的淺皮膚女性，就可以有以下Unicode順序：`U+1F469 U+1F3FB U+200D U+1F9B0 `

- `U+1F469 U+1F3FB `: 👩🏻
- `U+200D`: ZWJ
- `U+1F9B0`: 🦰 

所以合起來就會得到： 👩🏻‍🦰

##  變體選擇符VS-15 和 VS-16
在Unicode中有所謂的變體選擇符(Variation Selector)，其中的VS-15 (`U+FE0E`)和VS-16(`U+FE0F`)是專屬於emoji使用的。
VS-15表示要用單色的方式顯示emoji，VS-16則表示要用彩色的方式來顯示emoji。以下是一個例子：(不同平台呈現的效果可能不同)

- ☕ (`U+2615`)
- ☕︎ (`U+2615 U+FE0E`)
- ☕️ (`U+2615 U+FE0F`)

## 範例程式
寫了一個簡單程式，可以demo上面的例子，大家也可以自行修改來玩玩，只要裝[Babashka](https://babashka.org/) 就可以執行了。

```clojure
(def light-skin 0x1F3FB)
(def medium-light-skin 0x1F3FC)
(def medium-skin 0x1F3FD)
(def medium-dark-skin 0x1F3FE)
(def dark-skin 0x1F3FF)
(def thumb 0x1F44D)
(def thumb-light-skin [thumb light-skin])
(def man-with-computer [0x1F468 0x1F3FD 0x200D 0x1F4BB])
(def woman-with-light-skin [0x1F469 0x1F3FB])
(def woman-with-redhair [0x1F469 0x1F3FB 0x200D 0x1F9B0])
(def coffee 0x2615)

(defn merge-to-chars [code-points]
  (let [char-arrays (map #(Character/toChars %) code-points)]
    (String. (char-array (apply concat char-arrays)))))

(defn show [& code-point-groups]
  (doseq [group code-point-groups]
      (print (merge-to-chars group)))
  (println))

(show [thumb] thumb-light-skin)
(show man-with-computer woman-with-light-skin woman-with-redhair)
(show [light-skin medium-light-skin medium-skin medium-dark-skin dark-skin])
(show [coffee] [coffee 0xFE0E] [coffee 0xFE0F])
```

# Reference 

1. [從 Unicode 到 Emoji ](https://medium.com/@angus258963/%E5%BE%9Eunicode%E5%88%B0emoji-8cab765f55d9)

[^bom]: BOM (Byte Order Mark) 是一個特殊的Unicode字符（U+FEFF），通常放在文件開頭。在big-endian系統中，它會被存儲為`FE FF`，而在little-endian系統中，它會被存儲為`FF FE`。這樣系統就能通過讀取這個標記來確定後續字節的解讀順序。不過，在UTF-8中因為其設計特性，不需要使用BOM。

[^utf8]: UTF-8是由Ken Thompson和Rob Pike在1992年設計的。他們在紐澤西的一家餐廳用一個下午就設計出了這個編碼方案。UTF-8最巧妙的設計在於它完全向後兼容ASCII，這意味著所有的ASCII文本自動就是有效的UTF-8文本，這大大簡化了從ASCII到Unicode的過渡。


