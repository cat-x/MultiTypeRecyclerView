## 更快捷的RecyclerView适配库

现在MultiTypeRecyclerView库已经完成初步释放，后续优化功能仍会增加，使MultiTypeRecyclerView库更加强大。
完全支持Kotlin调用
本库计划从网络上流行的众多RecyclerView适配库中整合创建出一个跟高效快捷的RecyclerView适配库。  
目前已整合  
[me.drakeet.multitype:multitype](https://github.com/drakeet/MultiType) (2018年12月29日版本 multitype:3.4.4)   
[com.github.CymChad:BaseRecyclerViewAdapterHelper](https://github.com/CymChad/BaseRecyclerViewAdapterHelper) (2018年12月29日版本 v2.9.44)   
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
    //全部（包含基础库、android paging分页库、 kotlin支持）
    implementation 'com.github.cat-x:MultiTypeRecyclerView:0.21'
    //如果你已经引用上面的全部库，则无需引用下面的的单独的库
    
    
    //基础库（不包含kotlin支持，减小编译体积）
    implementation 'com.github.cat-x.MultiTypeRecyclerView:library:0.21'
    //android paging分页库（不包含kotlin支持，减小编译体积）；必须引用基础库
    implementation 'com.github.cat-x.MultiTypeRecyclerView:paging-support:0.21'
    //library库的kotlin支持；必须引用需要基础库；
    implementation 'com.github.cat-x.MultiTypeRecyclerView:library-support-kotlin:0.21'
    //paging分页库的kotlin支持；必须引用需要基础库和paging分页库；
    implementation 'com.github.cat-x.MultiTypeRecyclerView:paging-support-kotlin:0.21'
    
}
```


## Sample


## Usage
把你需要显示的的数据类(比如说下面的这个)或者基本类型String、Int等等
```java
public class LoadingItem {
    String title;
    Boolean isOk;

    public LoadingItem(String title, Boolean isOk) {
        this.title = title;
        this.isOk = isOk;
    }

    public void onClick() {
        Log.i("MultiAdapter", "yes onClick");
    }
}
```
在BaseMultiAdapter中或者BaseMultiAdapter的子类中调用register()方法注册即可
你存入BaseMultiAdapter的数据需要是一个集合，例如：
```java
//绑定recyclerView
MyAdapter adapter = new MyAdapter().bindToRecyclerView(recyclerView);
List<Object> data = new ArrayList<Object>();
//你可以放入String类型的数据
data.add("String数据")
//你可以放入Int类型的数据
data.add(1)
//你可以放入LoadingItem类型的数据
data.add(new LoadingItem("LoadingItem数据", false));
//添加到adapter即可
adapter.addData(data);
```

最后的显示效果只和你得List数据的顺序有关。
其中BaseMultiAdapter需要实现Binder
* 必须实现 getLayoutId()方法 是提供布局Id
* 必须实现 convert(holder,item,position)方法 让你快速绑定布局和数据。
> BaseViewHolder 中提供了许多方法，可以让你快速完成数据绑定。
> 例如setText(id,value) 亦或者setImageUrl(@IdRes int viewId, String url)等等
* 快捷实现 onClick( v,item,position)方法 是一个点击本View的回调函数，你可以复写它来快速满足你的点击事件的需求
* 快捷实现 onClick( v,item,position)方法 是一个长按本View的回调函数，你可以复写它来快速满足你的长按事件的需求
* 快捷实现 getSpanSize()方法 是当你在使用GridLayoutManager的时候，你想自定义SpanSize尺寸的时候，你直接复写对应注册类型的getSpanSize即可，无需额外设置。
```java
public class MyAdapter extends BaseMultiAdapter {
    public MyAdapter() {
        super();
        //注册String类型
         register(String.class, new Binder<String, BaseViewHolder>() {
                //提供布局Id
                @Override
                public int getLayoutId() {
                    //TODO("not implemented")
                }
    
                //绑定布局和数据
                @Override
                public void convert(@NonNull BaseViewHolder holder, @NonNull String item, int position) {
                    //TODO("not implemented")
                }
         });
         
         //注册LoadingItem类型
        register(LoadingItem.class, new Binder<LoadingItem, BaseViewHolder>() {
            @Override
            public int getLayoutId() {
                return R.layout.loading_item;
            }

            @Override
            public void convert(@NonNull BaseViewHolder holder, @NonNull LoadingItem item, int position) {
                holder.setText(R.id.text,item.title);
            }

            @Override
            public void onClick(View v, @NonNull LoadingItem item, @NonNull int position) {
                item.onClick();
            }

            @Override
            public void onLongClick(View view, @NonNull LoadingItem item, @NonNull int position) {
              if (item.isOk){
                  Log.i("MultiAdapter","item.isOk");
              }
            }

            @Override
            public int getSpanSize() {
                return super.getSpanSize();
            }
        });
    }
}
```
你可以使用MultiLayoutManger类中的方法来快速绑定相对应的LayoutManager
例如   
```java
//绑定GridLayoutManager
MultiLayoutManger.bindGridLayoutManager(recyclerView, 2);
//绑定垂直LinearLayoutManager
  MultiLayoutManger.bindVLinearLayoutManager(recyclerView);
//绑定水平LinearLayoutManager
  MultiLayoutManger.bindLinearLayoutManager(recyclerView);
```

你可以使用Kotlin来进行代码编写，和上面一样。
```kotlin
BaseMultiAdapter().register(String::class.java, object : Binder<String, HBaseViewHolder>() {
            override fun getLayoutId(): Int {
                TODO("not implemented") 
            }
            override fun convert(holder: HBaseViewHolder, item: String, position: Int) {
                TODO("not implemented") 
            }
        }
```

适配android Jetpack Paging分页库
```java
    public PagedMultiAdapter(@NonNull DiffUtil.ItemCallback<Object> diffCallback) {
    }

    public PagedMultiAdapter(@NonNull AsyncDifferConfig<Object> config) {
    }
    //添加数据你必须要使用PagedListAdapter规定的submitList(PagedList<Object> pagedList) 函数。
    //操作数据方面的其他函数均已废弃，必须使用submitList(PagedList<Object> pagedList) 函数。
    //对于绑定Adapter，如果你不使用bindToRecyclerView()函数，你需要 getRecyclerView().setAdapter(PagedMultiAdapter.getAdapter());这样绑定
```

kotlin支持
```kotlin
//Class
QuickLiveItemKeyList
QuickLivePagedKeyList
QuickLivePositionalList
//Ex
BaseMultiAdapter.setAnyData(data: List<T>)
BaseMultiAdapter.addAnyData(data: List<T>)
BaseMultiAdapter.addAnyData(position: Int, data: List<T>)
RecyclerView.bindAdapter(multiAdapter: T): T
RecyclerView.bindSimpleMultiAdapter(): BaseMultiAdapter
BaseMultiAdapter.register(clazz: KClass<out T>, binder: Binder<T, VH>): BaseMultiAdapter 
 BaseMultiAdapter.register(binder: Binder<T, VH>): BaseMultiAdapter
//...more
```

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