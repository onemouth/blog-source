---
title: 設計 Golang 的 error
author: LT
summary: how to design error in golang
tags: golang, error_handling
toc: true
---

在 Go 裡面，主要用來處理 error 的方法，是利用在 Go 1.13 時新引入的這兩個函式：[errors.Is](https://pkg.go.dev/errors#Is) 和 [errors.As](https://pkg.go.dev/errors#As)。在本文，想要探討如何設計好的error type，才能和 Go 標準庫提供的這兩個函數配合的天衣無縫。

首先先把error大略分爲兩種，一種是沒有包裝其他error的，是我們的這個package內部產生的error，和其他package無關。
另一種則是有包裝的，是在我們呼叫其他package的時候產生的error，因此必須保留這個上下文。

以下來分別看一下兩種error的設計。

#  package 內部產生的 error

## Sentinel Error

這是最常見的，也在許多知名的package被廣泛使用。

```go
package example

var (
  ErrExample = erros.New("test")
)

// Error Handling
err := example.DoSomething()
if errors.Is(err, example.ErrExmaple) {
    fmt.Println("do error handling)
}
```

sentinal error的好處是簡單，定義快速，又一目瞭然。缺點是定義了全域變數，還有在runtime才初始化。但這種形式是如此的常見，我想這些缺點不是大問題才是。

## 定義一個 error type

另一種常見的方法是定義一個 error type，這個type 會實作 `Error() string`。
我們再利用這個 type，定義一些 const error。


```go
type InvalidFormatError string

func (e InvalidFormatError) Error() string {
	return string(e)
}

const (
	ErrNoDdata         = InvalidFormatError("no data")
	ErrLengthTooLong   = InvalidFormatError("length too long")
)


// Error Handling
if errors.Is(err, ErrLengthTooLong) {
    fmt.Println("do error handling") 
}

var badRequest InvalidFormatError
if errors.As(err, &badRequest) {
    fmt.Println("do error handling")
}
```

這種寫法的好處除了也支援 `errros.Is` 以外，我們還可以利用 `errors.As` 去判斷同一類的 error。

例如在這個例子，ErrNoData 和 ErrLengthTooLong 都屬於 InvalidFormatError 這個type，所以假如只想知道這個 error 屬於
InvalidFormatError 這一類的話，就可以利用 errros.As 來做判斷。

其他好處包括了不用定義全域變數，不用在runtime初始化。缺點則是寫法比較複雜。

# 需要包裝(Wrap)其他error 

在其他情況，我們的error需要包裝其他package的error。最簡單的方法是利用 `fmt.Errorf("%w", err)` 直接包裝。
但我們也可以利用定義 struct 的方法，讓error的架構變得更清晰。

## 定義一個 struct

```go
type myDBPkgError struct {
	value string
	err   error
}

func (bv *myDBPkgError) Error() string {
	return fmt.Sprintf("bad value %v", bv.value)
}

func (bv *myDBPkgError) Unwrap() error {
	return bv.err
}

func (bv *myDBPkgError) Is(target error) bool {
    if target == sql.ErrNoRows {
        return true
    }
    return false
}

func testErr() error {
    v := 3
    err := processDB(v)
    if err != nil {
        return badValueError{value: v, err: err}
    }
    return nil
}
```

這個例子的重點是我們的struct定義了 `Unwrap()` 函式，利用這個函式，`erros.Is` 和 `errors.As` 才能正常運作，不只是嘗試match `badValueError{value: v, err: err}`這個eror，
還會不斷的往下尋找是否有其他能對應的error。

另一個我們可以定義的是`Is(target error) bool` 這個函式，他可以讓 `errors.Is` 直接呼叫，表示我們定義的這個error，其實跟另外某個error是相等的。


# 結語

在這篇文章中，探討了我在 Go 中常使用的error handling技巧，在1.13之後，Go本身提供的error handling機制，的確是更加成熟了。





