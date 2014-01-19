---
title: Dual_EC_DRBG
tags: crypto
---

惡名昭彰、臭名遠播的 [Dual_EC_DRBG](http://en.wikipedia.org/wiki/Dual_EC_DRBG), 原來是這麼回事啊, 看了這篇 [blog](http://blog.cloudflare.com/how-the-nsa-may-have-put-a-backdoor-in-rsas-cryptography-a-technical-primer) 後才大概知道了後門是怎麼被製作出來的。 


> $P$, $Q$ : two points on an elliptic curve  
> $n$: *secret state* resides in PRNG  
>   
> output a pseudorandom number: [$nP$].x_coordinate  
> update *secret state*: $n$ = [$nQ$].x_coordiante   


假如說 $Q$ 其實等於 $sP$, 而且我們知道$s$的值,那也就是說, 當我們觀測到 Dual_EC_DRBG 所 output 的一個 random 值後, 把它轉回 elliptic cureve 上的一個點, 然後再默默乘以 $s$, 因為 $$snP=nsP=nQ,$$ 就能得到應該是秘密的 *secret state* $n$ 了!

所以, Dual_EC_DRBG 要安全, $s$不可以被任何人知道, $P$和$Q$ 應該要是隨機選取的。

但是! NIST建議的 $P$ 和 $Q$ 真的是隨機嗎, 還是 $s$是多少他們根本就知道嗎？



