---
title: Spacemacs
tags: editor
---

![](../images/spacemacs.png "spacemacs logo")

現在用 [spacemacs](https://github.com/syl20bnr/spacemacs) 來開發以及個人工作記錄。

這是一個 “just work” 的 Emacs 配置檔，
融合了 Vim 高效率的的編輯操作和 Emacs 強大的 plugin。
十分適合已經熟悉 Vim ，又想輕鬆投靠 Emacs 的人。  

Emacs 厲害的 plugin 實在太多了，所以 spacemacs 把一些很好用但不是每個人都會用到的 plugin 依照功能
放在特定的一個一個資料夾中，這些一個一個的資料夾被稱為 ’layer‘, 可以依照每個人的需求在設定檔中去啟動不同的 layer。
另外當然高手也可以寫自己的 layer!

以下就來舉幾個我有用到的 layer 吧！

## xkcd

直接看 xkcd 的 layer，用 SPACE-a-x 啟動，
會像這樣：

![](../images/xkcd_layer.png "xkcd")

進入 xkcd 模式後，按 `e` 可以看到這篇漫畫梗的詳解，
按 `r` 可以隨機到另一則漫畫，按 `t` 看這篇漫畫的摘要，
按 `q` 則是離開這個模式。

## auto-completion

auto-completion 這個 layer, 我的配置是

~~~ {.clojure}
(auto-completion :variables
                 auto-completion-tab-key-behavior 'cycle
                 auto-completion-complete-key-sequence "jk"
                 auto-completion-enable-company-help-tooltip t)
~~~

表示按下 `tab` 會自動補齊並出現選單顯示其他候選字，快速的按下 `jk` 這兩個鍵也會啟動自動補齊。

這個 layer 也包含了 Yasnippet，按下 `SPACE-i-s` 會顯示所有可用的 snippets,
或是在打了一些字之後, 直接使用 `M-/` 來補齊 snippets

# eyebrowse

將 emacs 分成許多 workspace，就像是作業系統中的虛擬桌面一樣, 或像是 vim 的 Tab（分頁）。

`SPACE-W-[1-9]` 切換不同的 workspace

`SPACE-W-c` 關掉目前的 workspace

`gt` 到下一個 workspace

`gT` 到上一個 workspace

# OSX

可以在 spacemacs 裡使用 OSX 的快速鍵，我通常只用到貼上： `cmd-v` 而已。

# Dash

![](../images/dash.png "dash logo")

![](../images/zeal.png "zeal logo")

Dash 是 Mac 上十分好用的 API 查詢工具，有了它就不用在瀏覽器開一堆分頁查 API 怎麼用了！

![](../images/dash-in-use.png "Dash in Use")

其他平臺雖然沒有 Dash 可用, 但有一個 open source clone, 就是 Zeal，Zeal 可以在 Linux 和
Windows 上使用。  

Dash Layer 會自動判斷我們系統上安裝的是 Zeal 還是 Dash, 不需要我們手動設定。

相關的指令有

- `SPACE-d-d` 在 Dash 或 Zeal 視窗中查詢游標停留的字

- `SPACE-d-D` 指定 docset 後，在 Dash 或 Zeal 視窗中查詢游標停留的字

- `SPACE-d-h` 在 Spacemacs 視窗中查詢游標停留的字

- `SPACE-d-H` 指定 docset 後，在 Spacemacs 視窗中查詢游標停留的字

最後兩個指令要告訴 spacemacs, Dash 或 Zeal docset 的目錄位置才可以使用。
像是這樣

~~~ {.clojure}
(dash :variables dash-helm-dash-docset-path "/home/Zeal/Zeal/docsets")
~~~


# git

git layer 十分強大！

包含了 `magit`, `git-timemachine`, `helm-gitignore` 等套件。

我通常就用 `SPACE-g-s` 叫出 git status, 然後看一下提示就知道大概有哪些功能能用XD

# spell-checking

檢查拼字正確的 layer, 系統要安裝 `aspell` 這個指令才有效果喔。

# syntax-checking

檢查程式語言語法是否有錯誤的 layer，安裝的是 [flycheck](https://github.com/flycheck/flycheck) 這個套件，
[支援的語言十分多](http://www.flycheck.org/manual/latest/Supported-languages.html#Supported-languages)，有的語言，例如 Python 或是 Ruby 要額外安裝指令才能和 `flycheck` 配合。

# 其他

其他我還有用的 layer 包括 c-c++, clojure, github, org（工作記錄就靠它了！） 等等。
最後附上我的 .spacesmacs 檔[連結](https://github.com/onemouth/dot-files/blob/master/spacemacs)XD
