---
title: Boost.Python: C++ 與 Python 的橋樑
tags: python, c++, boost
---

# 前言

有些程式, 因為種種的原因, 是以 C 或 C++ 完成, 假如能有個方便的管道, 讓我們的 Python 可以直接呼叫 C++ function, 那就太好了. 

Survey 了一陣子, 有各式各樣的方法, 例如硬派的從 `<Python.h>` 直接從頭做起, 如[官方文件](http://docs.python.org/2/extending/extending.html)的說明。 

偷懶一點的話, Python Standard Library 中 有個 [ctypes](http://docs.python.org/2/library/ctypes.html) module, 可以 parse 一個 .so 檔, 並呼叫裡面的 function, 但是我們還是得寫一層 interface, 處理掉 C/C++ 到 Python 的轉換, 我覺得這層 interface 用 ctypes 來完成還是稍繁瑣了。(當然若要呼叫的C/C++ function本身的輸入輸出很單純, 沒什麼複雜的 pointer 或 struct 要費心處理, 那 ctypes 就很夠用了)

後來我發現了 [Boost.Python](http://www.boost.org/doc/libs/1_55_0/libs/python/doc/index.html), 試用之後發現真的蠻好用的, Boost 幫我們處理掉很多細節, 以下會從一個例子開始說明, 從 C++ code 到 Makefile 再到 Python import 的過程 .

# C++ code

我們的 C++ code 是下面這個 function:

~~~{.cpp}
#include <sign.h>

using std::string;

bool sign_your_string(string & result, const string & input ){
    if (input == "from_bad_guy"){
        return false;
    }   
    else{
        result = "I enndorse you.";
        return true;
    }   
}
~~~

這個 function 會根據使用者的 input, 來決定要不要給予簽章; 若是決定要簽章, 會return true並把簽章的結果放在第一個參數。
若是不要, 則會 return false


# Boost.Python

我們利用 Boost.Python 提供的工具, 可以將上面那個 function 包裝起來, 像下面這樣, 根據**return值**和**第一個參數**,
傳回 **tuple**

~~~{.cpp}
#include <string>

#include <sign.h>
#include <boost/python.hpp>

using std::string;

boost::python::tuple sign(const string & input){
    string result;
    bool ret = sign_your_string(result, input);
    if (ret){
        return boost::python::make_tuple(true, result);
    }
    else
        return boost::python::make_tuple(false, "");
}


BOOST_PYTHON_MODULE(sign_ext)
{
    using namespace boost::python;
    def("sign", sign);
}


~~~

# Makefile

make 其實很簡單, 可以參考下面的 Makefile 例子, 要注意的是, 因為我們 export 出去的 module 叫 **sign_ext**, 所以產生出來的檔, 就得叫 **sign_ext.so**才行。

因為Python在import的時候, 會根據 so 檔名去找對應的**__init__**, 假如對不起來, 就會抱怨找不到 **__init__** 而 造成import error 


~~~{.makefile}
INCLUDE=-Iinclude/  -I/usr/include/python2.7/
COMPILER=g++

CPP_FILES=sign.cpp Python.cpp
OBJ_FILES=$(notdir $(CPP_FILES:.cpp=.o))

%.o: src/%.cpp
        ${COMPILER}  -fPIC -c  ${INCLUDE}  $< 

sign_ext.so: $(OBJ_FILES)
        ${COMPILER} -shared -Wl,--export-dynamic ${OBJ_FILES} -lboost_python -o sign_ext.so


~~~


# Python

Python使用起來和一般的module沒有兩樣

~~~{.python}

>>> from sign_ext import sign

>>> sign("Hello World!")
(True, 'I enndorse you.')

sign("from_bad_guy")
>>> (False, '')
~~~

也可以把我們做好的so檔, 放到系統路徑下, 例如 Ubuntu的話, 可能就是 **/usr/local/lib/python2.7/dist-packages/**


# 結論

以上只是初步的使用! 更進一步的話應該要和 Python 的 [setuptools](https://pypi.python.org/pypi/setuptools)做結合, 才能更符合我們使用Python module的習慣。




 
