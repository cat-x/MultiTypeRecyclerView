package xyz.a1api.multirecycler.itemdecoration;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Cat-x on 2018/7/3.
 * For MultiTypeRecyclerView
 * Cat-x All Rights Reserved
 */
public class MultiItemDecoration {

    public static <ID extends RecyclerView.ItemDecoration> ID bindItemDecoration(final RecyclerView recyclerView, ID itemDecoration) {
        recyclerView.addItemDecoration(itemDecoration);
        return itemDecoration;
    }

    public static <ID extends RecyclerView.ItemDecoration> ID bindItemDecoration(final RecyclerView recyclerView, ID itemDecoration, int index) {
        recyclerView.addItemDecoration(itemDecoration, index);
        return itemDecoration;
    }

    public static DividerItemDecoration bindHDividerItemDecoration(final RecyclerView recyclerView) {
        return bindDividerItemDecoration(recyclerView, DividerItemDecoration.HORIZONTAL);
    }

    public static DividerItemDecoration bindVDividerItemDecoration(final RecyclerView recyclerView) {
        return bindDividerItemDecoration(recyclerView, DividerItemDecoration.VERTICAL);
    }

    public static DividerItemDecoration bindDividerItemDecoration(final RecyclerView recyclerView, @RecyclerView.Orientation int orientation) {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), orientation);
        recyclerView.addItemDecoration(dividerItemDecoration);
        return dividerItemDecoration;
    }

}
