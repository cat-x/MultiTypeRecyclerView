package xyz.a1api.multirecycler

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.widget.LinearLayout

/**
 * Created by Cat-x on 2019/3/26.
 * For MultiTypeRecyclerView
 * Cat-x All Rights Reserved
 */

fun <T : RecyclerView.LayoutManager> RecyclerView.bindLayoutManger(layoutManager: T): RecyclerView {
    setLayoutManager(layoutManager)
    return this
}

fun <T : GridLayoutManager> RecyclerView.bindGridLayoutManager(spanCount: Int, layoutManager: T = GridLayoutManager(context, spanCount) as T): RecyclerView {
    setLayoutManager(layoutManager)
    return this
}

fun <T : LinearLayoutManager> RecyclerView.bindHLinearLayoutManager(layoutManager: T = LinearLayoutManager(context).also { it.orientation = LinearLayout.HORIZONTAL } as T): RecyclerView {
    setLayoutManager(layoutManager)
    return this
}

fun <T : LinearLayoutManager> RecyclerView.bindVLinearLayoutManager(layoutManager: T = LinearLayoutManager(context).also { it.orientation = LinearLayout.VERTICAL } as T): RecyclerView {
    setLayoutManager(layoutManager)
    return this
}

fun <T : LinearLayoutManager> RecyclerView.bindLinearLayoutManager(layoutManager: T): RecyclerView {
    setLayoutManager(layoutManager)
    return this
}

fun <T : StaggeredGridLayoutManager> RecyclerView.bindStaggeredLayoutManager(spanCount: Int, @RecyclerView.Orientation orientation: Int, layoutManager: T = StaggeredGridLayoutManager(spanCount, orientation) as T): RecyclerView {
    setLayoutManager(layoutManager)
    return this
}