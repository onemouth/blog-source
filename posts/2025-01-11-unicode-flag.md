---
title: emoji 裡的旗子符號
summary: Unicode Flag Symbols
tags: emoji
enable:
  toc: true
---


emoji 裡我們有各式各樣的旗子可以用，例如美國國旗🇺🇸，法國國旗🇫🇷，貝里斯國旗🇧🇿等等。這些旗子是怎麼定義出來的呢？

基本上Unicode 本身是不收錄國旗本身的圖樣的，畢竟國旗是有著政治意義上的象徵，直接放入Unicode可能會引起一些爭議。而且隨著時間更替，同一個地區的國旗可能會發生改變，假如要把一個地區從古至今全部用過的國旗都放入Unicode，似乎又太浪費Unicode碼位。

所以，Unicode提供了[regional indicator symbols](https://en.wikipedia.org/wiki/Regional_indicator_symbol)(區域指示符), 和[emoji tag sequence](https://emojipedia.org/emoji-tag-sequence), 這兩個方法來定義country code，至於要怎麼顯示country code, 則可以由各家廠商自行決定。

# 區域指示符

區域指示符總共有26個，範圍是從 `U+1F1E6` 到 `U+1F1FF`，代表的就是英文數字的A到Z。
然後使用者就可以用這些區域指示符組出[country code](https://en.wikipedia.org/wiki/Country_code)，例如 US, TW 等等。
可以用這個範例程式看一下效果：
```clojure
(def A 0x1F1E6)
(def B 0x1F1E7)
(def C 0x1F1E8)
(def D 0x1F1E9)
(def E 0x1F1EA)
(def F 0x1F1EB)
(def G 0x1F1EC)
(def H 0x1F1ED)
(def I 0x1F1EE)
(def J 0x1F1EF)
(def K 0x1F1F0)
(def L 0x1F1F1)
(def M 0x1F1F2)
(def N 0x1F1F3)
(def O 0x1F1F4)
(def P 0x1F1F5)
(def Q 0x1F1F6)
(def R 0x1F1F7)
(def S 0x1F1F8)
(def T 0x1F1F9)
(def U 0x1F1FA)
(def V 0x1F1FB)
(def W 0x1F1FC)
(def X 0x1F1FD)
(def Y 0x1F1FE)
(def Z 0x1F1FF)

(defn merge-to-chars [code-points]
  (let [char-arrays (map #(Character/toChars %) code-points)]
    (String. (char-array (apply concat char-arrays)))))

(defn show [& code-point-groups]
  (doseq [group code-point-groups]
    (print (merge-to-chars group)))
  (println))

(show [U] [S])
(show [T] [W])
(show [A] [B])
```

假如拼出來的country code 是無效的，通常會直接顯示字母在畫面上。

# Emoji Tag Sequence
上述的區域指示符用來產生國家(region)的旗幟。後來Unicode又定義了Emoji tag sequence，用來顯示國家內部的地區(subregion)的旗幟。

Emoji tag sequence 是利用Unicode的`U+E0061` 到 `U+E007A`的這個範圍，代表不可見的26個小寫的拉丁字母，這個範圍被稱為Tag Latin Small Letter。
然後Emoji tag sequence用以下格式表示旗幟：
```txt
🏴（U+1F3F4）+(Tag Latin Small Letter的sequence)+Cancel Tag(U+E007F)
```

例如UK的三個王國，England, Scotland, 和 Wales 就可以用下面的順序表示

- 🏴󠁧󠁢󠁥󠁮󠁧󠁿 England = 🏴+(gbeng)+cancel tag
- 🏴󠁧󠁢󠁳󠁣󠁴󠁿 Scotland = 🏴+(gbsct)+cancel tag
- 🏴󠁧󠁢󠁷󠁬󠁳󠁿 Wales =  🏴+(gbwls)+cancel tag
	 
其他國家的subregion，例如美國的各州，也可以用這個方法表示。假設要表示德州：

- 🏴󠁵󠁳󠁴󠁸󠁿 Texas (US-TX) = 🏴+(ustx)+cancel tag

但實際上，目前只有英國的3個地區，被各大平台所支援，用tag sequence來表示美國各州，雖然是合法的Unicode flag sequence，卻無法被顯示出來, 只會出現沒有圖樣的🏴󠁵󠁳󠁴󠁸󠁿。

範例程式：
```clojure
(def flag 0x1F3F4)
(def cancel 0xE007F)

(def gbeng [0xE0067 0xE0062 0xE0065 0xE006E 0xE0067])
(def gbsct [0xE0067 0xE0062 0xE0073 0xE0063 0xE0074])
(def gbwls [0xE0067 0xE0062 0xE0077 0xE006C 0xE0073])
(def ustx [0xE0075 0xE0073 0xE0074 0xE0078])

(defn merge-to-chars [code-points]
  (let [char-arrays (map #(Character/toChars %) code-points)]
    (String. (char-array (apply concat char-arrays)))))

(defn show [& code-point-groups]
  (doseq [group code-point-groups]
    (print (merge-to-chars group)))
  (println))

(show [flag] gbeng [cancel])
(show [flag] gbsct [cancel])
(show [flag] gbwls [cancel])
(show [flag] ustx [cancel])

```
# Reference
1. 关于 Emoji 你不知道的事 , [https://juejin.cn/post/7225074892357173308](https://juejin.cn/post/7225074892357173308)
2. 45.Kerning Panic·字谈字串（三）🎙😂🤓🤑😌， [https://pan.icu/45](https://pan.icu/45)