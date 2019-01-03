package xyz.a1api.multirecycler


import android.support.v7.widget.RecyclerView
import xyz.a1api.multirecycler.multi.Linker
import xyz.a1api.multirecycler.multi.OneToManyEndpoint
import xyz.a1api.multirecycler.multi.OneToManyFlow
import xyz.a1api.multirecycler.multi.TypePool
import kotlin.reflect.KClass

/**
 * @author drakeet Cat-x
 */
inline fun <reified T : Any> BaseMultiAdapter.setAnyData(data: List<T>) {
    setData(data)
}

inline fun <reified T : Any> BaseMultiAdapter.addAnyData(data: List<T>) {
    addData(data)
}

inline fun <reified T : Any> BaseMultiAdapter.addAnyData(position: Int, data: List<T>) {
    addData(position, data)
}

inline fun <reified T : BaseMultiAdapter> RecyclerView.bindAdapter(multiAdapter: T): T {
    return multiAdapter.bindToRecyclerView(this)
}

fun RecyclerView.bindSimpleMultiAdapter(): BaseMultiAdapter {
    return BaseMultiAdapter().bindToRecyclerView(this)
}

fun <T : Any, VH : BaseViewHolder> BaseMultiAdapter.register(clazz: T, binder: Binder<T, VH>): BaseMultiAdapter {
    return register(clazz::class.java, binder)
}

fun <T : Any, VH : BaseViewHolder> BaseMultiAdapter.register(clazz: KClass<out T>, binder: Binder<T, VH>): BaseMultiAdapter {
    return register(clazz.java, binder)
}


inline fun <reified T : Any, VH : BaseViewHolder> BaseMultiAdapter.register(binder: Binder<T, VH>): BaseMultiAdapter {
    return register(T::class.java, binder)
}

fun <T : Any> BaseMultiAdapter.register(clazz: T): OneToManyFlow<T> {
    return register(clazz::class.java)
}

fun <T : Any> BaseMultiAdapter.register(clazz: KClass<out T>): OneToManyFlow<T> {
    return register(clazz.java)
}

fun <T : Any, VH : BaseViewHolder> TypePool.register(clazz: T, binder: Binder<T, VH>, linker: Linker<T>) {
    register(clazz::class.java, binder, linker)
}

fun <T : Any, VH : BaseViewHolder> TypePool.register(clazz: KClass<out T>, binder: Binder<T, VH>, linker: Linker<T>) {
    register(clazz.java, binder, linker)
}


fun <T : Any> TypePool.unregister(clazz: KClass<out T>): Boolean {
    return unregister(clazz.java)
}


fun <T : Any> TypePool.firstIndexOf(clazz: KClass<out T>): Int {
    return firstIndexOf(clazz.java)
}


fun <T> OneToManyEndpoint<T>.withKClassLinker(classLinker: KClassLinker<T>): BaseMultiAdapter {
    return withClassLinker { position, t -> classLinker.index(position, t).java }
}


fun <T> OneToManyEndpoint<T>.withKClassLinker(classLinker: (position: Int, t: T) -> KClass<out Binder<out BaseViewHolder, *>>): BaseMultiAdapter {
    return withClassLinker { position, t -> classLinker(position, t).java }
}