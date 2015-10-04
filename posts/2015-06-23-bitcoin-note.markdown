---
title: 一些關於 bitcoin 的事實
tags: bitcoin, crypto
---

# block (區塊)

- 1個 block 設計成約花整個 bitcoin 網路10分鐘完成

- 每完成2016個 block (約兩個禮拜), 根據真正花費的時間, 重新調整 proof-of-work 的難度,

- 難度爲 SHA256 計算出來的值需要有幾個連續0來決定。

- 爲了激勵大家完成 block chain, bitcoin 設有獎勵機制,
  初始爲50個 bitcoin, 每210000個block過後(約4年), 獎勵減半。
  估計到2140年, 將不再提供獎勵，而完全由交易費取代。

- 最小的 bitcoin 單位爲 satoshi, 1 satoshi = $10^{-8}$ bitcoin

- 預設要多6個 block (約一小時)才認定一個 transaction 是有效的.

- 獎勵要多100個 block 之後, 才能開始使用。

# bitcoin scripts

- transaction 使用的語言

- 可於 [testnet](https://en.bitcoin.it/wiki/Testnet) 上測試
