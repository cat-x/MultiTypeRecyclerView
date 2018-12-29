package xyz.a1api.multirecycler

import android.support.v7.recyclerview.extensions.AsyncDifferConfig
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import xyz.a1api.multirecycler.multi.OneToManyFlow
import xyz.a1api.paging.PagedMultiAdapter
import kotlin.reflect.KClass

/**
 * Created by Cat-x on 2018/8/10.
 * For MultiTypeRecyclerView
 * Cat-x All Rights Reserved
 */
fun RecyclerView.bindPagedMultiAdapter(diffCallback: DiffUtil.ItemCallback<Any>): PagedMultiAdapter {
    return PagedMultiAdapter(diffCallback).bindToRecyclerView(this)
}

fun RecyclerView.bindPagedMultiAdapter(config: AsyncDifferConfig<Any>): PagedMultiAdapter {
    return PagedMultiAdapter(config).bindToRecyclerView(this)
}

fun <T : Any, VH : BaseViewHolder> PagedMultiAdapter.register(clazz: KClass<out T>, binder: Binder<T, VH>): PagedMultiAdapter {
    register(clazz.java, binder)
    return this
}


inline fun <reified T : Any, VH : BaseViewHolder> PagedMultiAdapter.register(binder: Binder<T, VH>): PagedMultiAdapter {
    register(T::class.java, binder)
    return this
}


fun <T : Any> PagedMultiAdapter.register(clazz: KClass<out T>): OneToManyFlow<T> {
    return register(clazz.java)
}