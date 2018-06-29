package xyz.a1api.multirecycler.base;

/**
 * Created by Cat-x on 2018/6/29.
 * For MultiTypeRecyclerView
 * Cat-x All Rights Reserved
 */

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

public abstract class Binder<T, VH extends BaseViewHolder> {

    BaseQuickAdapter adapter;

    public BaseQuickAdapter getAdapter() {
        return adapter;
    }

    public abstract @LayoutRes
    int getLayoutId();


//    public abstract void convert(VH holder, T item);

    public abstract void convert(@NonNull VH holder, @NonNull Object item);

    /**
     * Return the stable ID for the <code>item</code>. If {@link RecyclerView.Adapter#hasStableIds()}
     * would return false this method should return {@link RecyclerView#NO_ID}. The default
     * implementation of this method returns {@link RecyclerView#NO_ID}.
     *
     * @param item The item within the MultiTypeAdapter's items data set to query
     * @return the stable ID of the item
     * @see RecyclerView.Adapter#setHasStableIds(boolean)
     * @since v3.2.0
     */
    public long getItemId(@NonNull T item) {
        return RecyclerView.NO_ID;
    }


}
