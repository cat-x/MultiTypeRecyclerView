package xyz.ciyuan.quick

import android.arch.lifecycle.LiveData
import android.arch.paging.*
import android.support.annotation.WorkerThread
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable

/**
 * Created by Cat-x on 2018/7/30.
 * For C_Comic
 * Cat-x All Rights Reserved
 */
abstract class QuickLivePositionalList<Value> {
    private var mConfig: PagedList.Config

    constructor(config: PagedList.Config) {
        mConfig = config
    }

    constructor(pageSize: Int, enablePlaceholders: Boolean = false)
            : this(PagedList.Config.Builder().setPageSize(pageSize).setEnablePlaceholders(enablePlaceholders).build())


    /**
     * Load initial list data.
     *
     *
     * This method is called to load the initial page(s) from the DataSource.
     *
     *
     * Result list must be a multiple of pageSize to enable efficient tiling.
     *
     * @param params Parameters for initial load, including requested start position, load size, and
     * page size.
     * @param callback Callback that receives initial load data, including
     * position and total data set size.
     */
    @WorkerThread
    protected abstract fun loadInitial(
            params: PositionalDataSource.LoadInitialParams,
            callback: PositionalDataSource.LoadInitialCallback<Value>)

    private fun makeDataSource(): DataSource.Factory<Int, Value> {
        return object : DataSource.Factory<Int, Value>() {
            override fun create(): DataSource<Int, Value> {
                return object : PositionalDataSource<Value>() {
                    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Value>) {
                        this@QuickLivePositionalList.loadRange(params, callback)
                    }

                    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Value>) {
                        this@QuickLivePositionalList.loadInitial(params, callback)
                    }
                }
            }
        }
    }

    /**
     * Called to load a range of data from the DataSource.
     *
     *
     * This method is called to load additional pages from the DataSource after the
     * LoadInitialCallback passed to dispatchLoadInitial has initialized a PagedList.
     *
     *
     * Unlike [.loadInitial], this method must return
     * the number of items requested, at the position requested.
     *
     * @param params Parameters for load, including start position and load size.
     * @param callback Callback that receives loaded data.
     */
    @WorkerThread
    protected abstract fun loadRange(params: PositionalDataSource.LoadRangeParams,
                                     callback: PositionalDataSource.LoadRangeCallback<Value>)

    fun build(): LiveData<PagedList<Value>> {
        return LivePagedListBuilder(makeDataSource(), this@QuickLivePositionalList.mConfig).build()
    }

    fun buildFlowable(backpressureStrategy: BackpressureStrategy): Flowable<PagedList<Value>> {
        return RxPagedListBuilder(makeDataSource(), this@QuickLivePositionalList.mConfig).buildFlowable(backpressureStrategy)
    }


    fun buildObservable(): Observable<PagedList<Value>> {
        return RxPagedListBuilder(makeDataSource(), this@QuickLivePositionalList.mConfig).buildObservable()
    }

}