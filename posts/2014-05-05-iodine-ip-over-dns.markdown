---
title: IP-Over-Dns 和 iodine
tags: dns
---

[原理簡介](http://dnstunnel.de/)

你正在街上, 很無聊想要上網, 於是你隨便連上一個 wifi 訊號,
在瀏覽器輸入網址: blog.ink-pot.co,
接著, 你被導到了一個登入頁面, 要求你必須輸入帳號密碼, 才能使用這個 wifi。

很常見的情景, 這告訴了我們DNS query 並沒有被擋住。(若DNS查詢失敗, 瀏覽器就不會顯示任何網頁, 我們也不會看到登入畫面)

所以一個簡單的想法, 控制一個 DNS server, 我們假裝向他查詢, 其實是把 IP 封包編碼後丟給它,
也就是藉由這個 DNS server, 瞞天過海的上網!

想要讓電腦向我們的 DNS server 查詢, 首先要先控制一個 subdomain, 然後再指定這個 subdomain 的 DNS server 為我們偽裝好的, 可以幫我們上網的 server。

因為根據 DNS query的流程, 電腦要解析這個 subdomain 的任一個 domain name, 勢必要詢問我們的 DNS server。

需要的工具: 

* [iodine](http://code.kryo.se/iodine/): server端 和 client端

* domain name (例如去godaddy買)

詳細配置請上網搜尋 "通過iodine實現DNS tunnel(DNS隧道)" XD









