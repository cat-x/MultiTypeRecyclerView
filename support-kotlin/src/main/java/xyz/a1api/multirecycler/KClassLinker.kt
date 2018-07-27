package xyz.a1api.multirecycler

import kotlin.reflect.KClass

/**
 * An interface to link the items and binders by the classes of binders.
 *
 * @author drakeet Cat-x
 */
interface KClassLinker<T> {

    /**
     * Returns the class of your registered binders for your item.
     *
     * @param position The position in items
     * @param t The item
     * @return The index of your registered binders
     * @see OneToManyEndpoint.withClassLinker
     */
    fun index(position: Int, t: T): KClass<out Binder<out BaseViewHolder, *>>
}
