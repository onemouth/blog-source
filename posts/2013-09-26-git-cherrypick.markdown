---
title:  第一次使用 git cherry-pick
---

最近工作上把相近的專案分成好幾個 repository 來維護，有時候A專案改了，B專案也需要改，這時候`git cherry-pick`似乎可以派上用場。

假設A專案是我們主要在開發的 repository，B專案比較少改，但因為從同一個base分支出來，所以有時候必須把A專案改的東西，抓回B專案。我用以下的做法達成。

* 切到B repository    
 `$ cd ~/Projects/B`                 
* 把A的repository加到B的remote中    
 `$ git remote add A-ref [url]`       
* 抓下remote的所有東西    
 `$ git fetch --all`               

這時候我們在local已經可以看到A的全部commits了，假設 `0001`、`0002`這兩個commit是我們想要放到B的，我們按commit的時間順序，下以下指令:

> `$ git cherry-pick 0001` 

> `$ git cherry-pick 0002`

假如沒有conflict發生，這樣就大功告成了!
