package xyz.a1api.paging


import android.arch.lifecycle.LiveData
import android.arch.paging.*
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable

/**
 * Created by Cat-x on 2018/7/30.
 * For C_Comic
 * Cat-x All Rights Reserved
 */
abstract class QuickLiveItemKeyList<Key, Value> {
    private var mConfig: PagedList.Config

    constructor(config: PagedList.Config) {
        mConfig = config
    }

    constructor(pageSize: Int, enablePlaceholders: Boolean = false)
            : this(PagedList.Config.Builder().setPageSize(pageSize).setEnablePlaceholders(enablePlaceholders).build())

    /**
     * Load initial data.
     *
     *
     * This method is called first to initialize a PagedList with data. If it's possible to count
     * the items that can be loaded by the DataSource, it's recommended to pass the loaded data to
     * the callback via the three-parameter
     * [LoadInitialCallback.onResult]. This enables PagedLists
     * presenting data from this source to display placeholders to represent unloaded items.
     *
     *
     * [LoadInitialParams.requestedInitialKey] and [LoadInitialParams.requestedLoadSize]
     * are hints, not requirements, so they may be altered or ignored. Note that ignoring the
     * `requestedInitialKey` can prevent subsequent PagedList/DataSource pairs from
     * initializing at the same location. If your data source never invalidates (for example,
     * loading from the network without the network ever signalling that old data must be reloaded),
     * it's fine to ignore the `initialLoadKey` and always start from the beginning of the
     * data set.
     *
     * @param params Parameters for initial load, including initial key and requested size.
     * @param callback Callback that receives initial load data.
     */
    protected abstract fun loadInitial(params: ItemKeyedDataSource.LoadInitialParams<Key>,
                                       callback: ItemKeyedDataSource.LoadInitialCallback<Value>)

    /**
     * Load list data after the key specified in [LoadParams.key].
     *
     *
     * It's valid to return a different list size than the page size if it's easier, e.g. if your
     * backend defines page sizes. It is generally safer to increase the number loaded than reduce.
     *
     *
     * Data may be passed synchronously during the loadAfter method, or deferred and called at a
     * later time. Further loads going down will be blocked until the callback is called.
     *
     *
     * If data cannot be loaded (for example, if the request is invalid, or the data would be stale
     * and inconsistent, it is valid to call [.invalidate] to invalidate the data source,
     * and prevent further loading.
     *
     * @param params Parameters for the load, including the key to load after, and requested size.
     * @param callback Callback that receives loaded data.
     */
    protected abstract fun loadAfter(params: ItemKeyedDataSource.LoadParams<Key>, callback: ItemKeyedDataSource.LoadCallback<Value>)

    /**
     * Load list data before the key specified in [LoadParams.key].
     *
     *
     * It's valid to return a different list size than the page size if it's easier, e.g. if your
     * backend defines page sizes. It is generally safer to increase the number loaded than reduce.
     *
     *
     *
     * **Note:** Data returned will be prepended just before the key
     * passed, so if you vary size, ensure that the last item is adjacent to the passed key.
     *
     *
     * Data may be passed synchronously during the loadBefore method, or deferred and called at a
     * later time. Further loads going up will be blocked until the callback is called.
     *
     *
     * If data cannot be loaded (for example, if the request is invalid, or the data would be stale
     * and inconsistent, it is valid to call [.invalidate] to invalidate the data source,
     * and prevent further loading.
     *
     * @param params Parameters for the load, including the key to load before, and requested size.
     * @param callback Callback that receives loaded data.
     */
    protected abstract fun loadBefore(params: ItemKeyedDataSource.LoadParams<Key>, callback: ItemKeyedDataSource.LoadCallback<Value>)

    /**
     * Return a key associated with the given item.
     *
     *
     * If your ItemKeyedDataSource is loading from a source that is sorted and loaded by a unique
     * integer ID, you would return `item.getID()` here. This key can then be passed to
     * [.loadBefore] or
     * [.loadAfter] to load additional items adjacent to the item
     * passed to this function.
     *
     *
     * If your key is more complex, such as when you're sorting by name, then resolving collisions
     * with integer ID, you'll need to return both. In such a case you would use a wrapper class,
     * such as `Pair<String, Integer>` or, in Kotlin,
     * `data class Key(val name: String, val id: Int)`
     *
     * @param item Item to get the key from.
     * @return Key associated with given item.
     */
    protected abstract fun getKey(item: Value): Key


    private fun makeDataSource(): DataSource.Factory<Key, Value> {
        return object : DataSource.Factory<Key, Value>() {
            override fun create(): DataSource<Key, Value> {
                return object : ItemKeyedDataSource<Key, Value>() {
                    override fun loadInitial(params: LoadInitialParams<Key>, callback: LoadInitialCallback<Value>) {
                        this@QuickLiveItemKeyList.loadInitial(params, callback)
                    }

                    override fun loadAfter(params: LoadParams<Key>, callback: LoadCallback<Value>) {
                        this@QuickLiveItemKeyList.loadAfter(params, callback)
                    }

                    override fun loadBefore(params: LoadParams<Key>, callback: LoadCallback<Value>) {
                        this@QuickLiveItemKeyList.loadBefore(params, callback)
                    }

                    override fun getKey(item: Value): Key {
                        return this@QuickLiveItemKeyList.getKey(item)
                    }
                }
            }
        }
    }

    fun build(): LiveData<PagedList<Value>> {
        return LivePagedListBuilder(makeDataSource(), this@QuickLiveItemKeyList.mConfig).build()
    }

    fun buildObservable(backpressureStrategy: BackpressureStrategy): Flowable<PagedList<Value>> {
        return RxPagedListBuilder(makeDataSource(), this@QuickLiveItemKeyList.mConfig).buildFlowable(backpressureStrategy)
    }

    fun buildObservable(): Observable<PagedList<Value>> {
        return RxPagedListBuilder(makeDataSource(), this@QuickLiveItemKeyList.mConfig).buildObservable()
    }
}