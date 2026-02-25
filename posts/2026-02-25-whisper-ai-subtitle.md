---
title: 使用 openai-whisper 和 ffmpeg 產生影片字幕
summary: how to use openai-whisper and ffmpeg to generate subtitle for video
tags: whisper,ffmpeg,srt
---

有一些外文的影片是沒有字幕的，這讓我們要嘗試理解影片的意思會十分困難，因為有字幕的話，還可以查字典找意思，
沒字幕的話，聽力也不行就完全沒辦法了。

但是現在！我們有AI，用AI的話，不論是Podcast，Youtube，還是其他多媒體內容，只要能下載到本機，就可以進行語音辨識，產生字幕檔。

我們主要會使用到的工具:

 -  [openai-whisper](https://github.com/openai/whisper)
 -  ffmpeg
 -  [uv](https://docs.astral.sh/uv/)

因為我是用macOS實驗的，所以接下來的指令，也都是基於mac的終端機的。
首先先安裝 ffmpeg 和 uv:
```bash
brew install ffmpeg uv
```


接下來假設我們手邊有一個影片檔 `video.mp4`，我們先利用 `ffmpeg` 取出 audio 的部分：
```bash
ffmpeg -i video.mp4 -b:a 192k -vn audio.mp3
```

接下來，就可以用 whisper 產生字幕檔了！
```bash
uvx --from openai-whisper whisper --output_format srt audio.mp3
```

用參數 `--model` 去設定model，預設的model是 `turbo`，我覺得就很夠用了。

全部支援的 model, 它們的參數由小到大分別是： `tiny`, `base`, `small`, `medium`, `turbo`, `large`。
另外 turbo 有對inference speed做最佳化。

一般來說，whisper會自動去抓音檔的語音，但有時候可能會判斷錯誤，
這時候我們就可以用 `--language` 去指定語言，例如 `--language ja`。

最後，對某些音檔，whisper會出現幻聽(Hallucination)循環，當 Whisper 遇到背景音樂太強、人聲太小、或是開頭有長時間沉默時，它會陷入一種死循環，不斷重複同一句話
這時候，就可以使用這個參數 `--condition_on_previous_text False`，讓 whisper不要因為上一段的辨識錯誤而一直影響到之後的辨識。

最近看到一個說法，不要覺得很多東西很多人寫過了，就不去寫了。
因為自己驗證過別人的文章，再把它寫出來，也是一種貢獻，幫助擴散正確的概念。
所以雖然網路上很多相關的文章，但我想還是值得分享我目前的成果。

