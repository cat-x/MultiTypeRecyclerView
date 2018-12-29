package xyz.ciyuan.quick

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
abstract class QuickLivePagedKeyList<Key, Value> {
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
     * [LoadInitialParams.requestedLoadSize] is a hint, not a requirement, so it may be may be
     * altered or ignored.
     *
     * @param params Parameters for initial load, including requested load size.
     * @param callback Callback that receives initial load data.
     */
    protected abstract fun loadInitial(params: PageKeyedDataSource.LoadInitialParams<Key>,
                                       callback: PageKeyedDataSource.LoadInitialCallback<Key, Value>)

    /**
     * Prepend page with the key specified by [LoadParams.key].
     *
     *
     * It's valid to return a different list size than the page size if it's easier, e.g. if your
     * backend defines page sizes. It is generally safer to increase the number loaded than reduce.
     *
     *
     * Data may be passed synchronously during the load method, or deferred and called at a
     * later time. Further loads going down will be blocked until the callback is called.
     *
     *
     * If data cannot be loaded (for example, if the request is invalid, or the data would be stale
     * and inconsistent, it is valid to call [.invalidate] to invalidate the data source,
     * and prevent further loading.
     *
     * @param params Parameters for the load, including the key for the new page, and requested load
     * size.
     * @param callback Callback that receives loaded data.
     */
    protected abstract fun loadBefore(params: PageKeyedDataSource.LoadParams<Key>,
                                      callback: PageKeyedDataSource.LoadCallback<Key, Value>)

    /**
     * Append page with the key specified by [LoadParams.key].
     *
     *
     * It's valid to return a different list size than the page size if it's easier, e.g. if your
     * backend defines page sizes. It is generally safer to increase the number loaded than reduce.
     *
     *
     * Data may be passed synchronously during the load method, or deferred and called at a
     * later time. Further loads going down will be blocked until the callback is called.
     *
     *
     * If data cannot be loaded (for example, if the request is invalid, or the data would be stale
     * and inconsistent, it is valid to call [.invalidate] to invalidate the data source,
     * and prevent further loading.
     *
     * @param params Parameters for the load, including the key for the new page, and requested load
     * size.
     * @param callback Callback that receives loaded data.
     */
    protected abstract fun loadAfter(params: PageKeyedDataSource.LoadParams<Key>,
                                     callback: PageKeyedDataSource.LoadCallback<Key, Value>)

    private fun makeDataSource(): DataSource.Factory<Key, Value> {
        return object : DataSource.Factory<Key, Value>() {
            override fun create(): DataSource<Key, Value> {
                return object : PageKeyedDataSource<Key, Value>() {
                    override fun loadInitial(params: LoadInitialParams<Key>, callback: LoadInitialCallback<Key, Value>) {
                        this@QuickLivePagedKeyList.loadInitial(params, callback)
                    }

                    override fun loadAfter(params: LoadParams<Key>, callback: LoadCallback<Key, Value>) {
                        this@QuickLivePagedKeyList.loadAfter(params, callback)
                    }

                    override fun loadBefore(params: LoadParams<Key>, callback: LoadCallback<Key, Value>) {
                        this@QuickLivePagedKeyList.loadBefore(params, callback)
                    }
                }
            }
        }
    }

    fun build(): LiveData<PagedList<Value>> {
        return LivePagedListBuilder(makeDataSource(), this@QuickLivePagedKeyList.mConfig).build()
    }

    fun buildFlowable(backpressureStrategy: BackpressureStrategy): Flowable<PagedList<Value>> {
        return RxPagedListBuilder(makeDataSource(), this@QuickLivePagedKeyList.mConfig).buildFlowable(backpressureStrategy)
    }

    fun buildObservable(): Observable<PagedList<Value>> {
        return RxPagedListBuilder(makeDataSource(), this@QuickLivePagedKeyList.mConfig).buildObservable()
    }

}