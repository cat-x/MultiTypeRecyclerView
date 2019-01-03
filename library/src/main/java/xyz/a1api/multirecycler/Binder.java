package xyz.a1api.multirecycler;

/**
 * Created by Cat-x on 2018/6/29.
 * For MultiTypeRecyclerView
 * Cat-x All Rights Reserved
 */

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class Binder<T, VH extends BaseViewHolder> {

    BaseMultiAdapter adapter;

    @SuppressWarnings("WeakerAccess")
    protected VH viewHolder;

    public BaseMultiAdapter getAdapter() {
        return adapter;
    }

    public VH getViewHolder() {
        return viewHolder;
    }

    public abstract @LayoutRes
    int getLayoutId();


//    public abstract void convert(VH holder, T item);

    public abstract void convert(@NonNull VH holder, @NonNull T item, int position);

    void bind(@NonNull BaseViewHolder holder, @NonNull Object item, int position) {
        convert((VH) holder, (T) item, position);
    }

    final void click(boolean isLong, View v, @NonNull Object item, @NonNull int position) {
        if (isLong) {
            onLongClick(v, (T) item, position);
        } else {
            onClick(v, (T) item, position);
        }
    }

    public void onClick(View v, @NonNull T item, @NonNull int position) {

    }

    public void onLongClick(View view, @NonNull T item, @NonNull int position) {

    }

    public int getSpanSize() {
        return 1;
    }

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


    public VH getViewHolder(BaseMultiAdapter adapter, View view) {
        Class temp = getClass();
        Class z = null;
        while (z == null && null != temp) {
            z = ClassUtil.getInstancedGenericKClass(temp);
            temp = temp.getSuperclass();
        }
        VH vh;
        // 泛型擦除会导致z为null
        if (z == null) {
            vh = (VH) new BaseViewHolder(view);
        } else {
            vh = ClassUtil.createGenericKInstance(adapter, z, view);
        }
        viewHolder = vh != null ? vh : (VH) new BaseViewHolder(view);
        return viewHolder;
    }


}
