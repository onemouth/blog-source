---
title: 筆記：如何利用OpenSSL製作憑證
---

當我們需要利用ssl來確保連線安全的時候，會需要利用OpenSSL提供的工具來製作憑證，最近研究了好久，參考各大blog，終於整理好一份製作憑證的recipe。

在以下的例子，我們會有三個角色：CA, Server 和 Client。CA會負責簽署Server和Client的憑證。

### Key ###

首先，我們先製作好，三個角色各自的private key。

`openssl ecparam -out ec_param -name prime256v1`

`openssl ecparam -genkey -in ec_param -out ca.key`

`openssl ecparam -genkey -in ec_param -out server.key`

`openssl ecparam -genkey -in ec_param -out client.key`

這樣便會產生好3把256-bit的private key。
我們用的private key屬於ECC(Elliptic Curve Cryptography)，在相同的bit數下，比RSA更安全，256-bit的ECC，其安全性相當於3072-bit的RSA！


### CA ###


`openssl req -new  -days 365 -x509 -out ca.crt -key ca.key`

這行指令輸入後會要求輸入一些關於憑證的資訊，關於這些內容的詳細資料，可參考[X.509](http://en.wikipedia.org/wiki/X.509)的說明。
都完成後，會出現一個crt檔，這便是我們CA的憑證，這個憑證由CA自己的private key所簽署。


### Server ###

`openssl req -new -key server.key -days 365 -out server.csr`

這行指令會產生好一個server.csr檔，也就是還沒經過CA簽署的憑證，我們再經由下面指令，讓CA簽署。

`openssl x509 -req -days 365 -in server.csr -CA ca.crt -CAkey ca.key -out server.crt -set_serial 3`


### Client ###

`openssl req -new -key client.key -days 365 -out client.csr`

`openssl x509 -req -days 365 -in client.csr -CA ca.crt -CAkey ca.key -out client.crt -set_serial 7`


### Verify ###

可以利用以下指令，驗證整個憑證路徑的正確性：

`openssl verify -CAfile ca.crt server.crt`

### Conclusion ###

藉由憑證和ssl，可以方便的製作出secure channel，在寫一些需要安全通信的程式的時候，可以好好利用。
另外，用我們自己產生的CA憑證，系統是不認識的，所以真的要用的時候，可以把這個憑證import到自己系統裡，或是在自己的程式內部，指定相信這個CA憑證(就像我們在上面verify指令做的事)。


