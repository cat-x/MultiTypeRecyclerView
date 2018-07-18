package xyz.a1api.multirecycler.listener;

import android.view.View;

import xyz.a1api.multirecycler.BaseMultiAdapter;

/**
 * create by: allen on 16/8/3.
 */

public abstract class OnItemLongClickListener extends SimpleClickListener {
    @Override
    public void onItemClick(BaseMultiAdapter adapter, View view, int position) {

    }

    @Override
    public void onItemLongClick(BaseMultiAdapter adapter, View view, int position) {
        onSimpleItemLongClick(adapter, view, position);
    }

    @Override
    public void onItemChildClick(BaseMultiAdapter adapter, View view, int position) {

    }

    @Override
    public void onItemChildLongClick(BaseMultiAdapter adapter, View view, int position) {
    }

    public abstract void onSimpleItemLongClick(BaseMultiAdapter adapter, View view, int position);
}
