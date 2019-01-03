package xyz.a1api.multirecycler

import android.view.View
import kotlinx.android.extensions.LayoutContainer

/**
 * Created by Cat-x on 2019/1/3.
 * For MultiTypeRecyclerView
 * Cat-x All Rights Reserved
 */
abstract class KBaseViewHolder(override val containerView: View?) : BaseViewHolder(containerView), LayoutContainer {

}