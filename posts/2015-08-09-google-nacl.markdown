---
title: 關於 Google NaCl 和 Pepper API
tags: web
---

# 前言
弄了 NaCl 這東西也快一個月了，中間一度卡住放棄，終於在颱風天有點進展，趕快把一些瞭解的部分寫一下...

# 什麼是 NaCl
目前的瀏覽器十分強大，不止是瀏覽網頁，已經是一個可執行許多 app 的平臺，
但 Javascript 在優化上有所限制，因此人們往往希望可以在瀏覽器上也直接跑 native code,
NaCl(Native Client) 就是用來執行編譯過後的 C/C++ 程式的一個 sandbox 環境。

這個 sandbox 環境是可以獨立被執行的，可參考 [Can I run Google's Native Client outside of the browser?](http://stackoverflow.com/questions/4690526/can-i-run-googles-native-client-outside-of-the-browser/14037396#14037396)
我試了上面 link 講的 sel_ldr.py 來跑 [naclports](https://code.google.com/p/naclports/) 提供的 [python2](https://chromium.googlesource.com/external/naclports/+/master/ports/python), 發現執行 `os.system`會傳回 error code, 也找不到 socket 模組，這是因爲在這個環境裡, system call 都是被先擋住的,
sandbox 會先確定外在環境有可信任的 API, 才會將 system call 轉去外面執行。而在絕大部分的情況下，
NaCl 都會在 Chrome 的內部執行。

# ppapi (Pepper API)

ppapi 是 Chrome OS 提供給 NaCl 程式呼叫的函式庫，包含了 TCP socket, File IO, Graphics 和 Audio 等等。
這些 API 是被 sandbox 環境信任的, 因此我們可以用這些 API 來取代被 sandbox 擋住的部分, 便可以將原來的 C/C++ 程式
porting 到 NaCl 上了!

# nacl_io

但是每個跟 system call 有關的 API 都要改的話要花費不少力氣, 因此 Google 提供了 [nacl_io](https://developer.chrome.com/native-client/devguide/coding/nacl_io) 函式庫,
它將一部分和 IO 有關的常用 API, 如 POSIX I/O (stdio.h) 以及 BSD sockets (sys/socket.h),
直接用 ppapi 實現，這樣我們在移植上就方便許多了!

# 後記
這技術用的人貌似不多...雖然已經很多東西被移植到 NaCl 上了，像是 git, vim, python, curl 等等。
但之後也許會轉向 [WebAssembly](https://github.com/WebAssembly/design/blob/master/HighLevelGoals.md)？
然後這些 C/C++ code 直接 compile 到 WebAssembly 上就好XD
