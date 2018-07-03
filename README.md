## 更快捷的RecyclerView适配库

这是一个还正在开发中的库，不承担任何风险  
本库计划从网络上流行的众多RecyclerView适配库中整合创建出一个跟高效快捷的RecyclerView适配库。  
目前已初步整合  
me.drakeet.multitype:multitype,  
com.github.CymChad:BaseRecyclerViewAdapterHelper 
计划引入  
[ru.noties:scrollable](https://github.com/noties/Scrollable)  
[rouchuan.viewpagerlayoutmanager:viewpagerlayoutmanager](https://github.com/leochuan/ViewPagerLayoutManager)  

## Getting started

In your root path  `build.gradle`:

```groovy
allprojects {
        repositories {
            maven { url "https://jitpack.io" }
        }
    }
```
In your Application Dir `build.gradle`:
```groovy
dependencies {
    implementation 'com.github.cat-x:MultiTypeRecyclerView:0.03'
}
```

## Usage

##  Thanks
* [https://github.com/drakeet/MultiType](https://github.com/drakeet/MultiType)
* [https://github.com/CymChad/BaseRecyclerViewAdapterHelper](https://github.com/CymChad/BaseRecyclerViewAdapterHelper)

## License


>引用库代码Copyright属于原作者


~~~
    Copyright cat-x.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
~~~