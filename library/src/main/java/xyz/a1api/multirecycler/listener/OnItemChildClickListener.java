package xyz.a1api.multirecycler.listener;

import android.view.View;

import xyz.a1api.multirecycler.BaseMultiAdapter;

/**
 * Created by AllenCoder on 2016/8/03.
 * A convenience class to extend when you only want to OnItemChildClickListener for a subset
 * of all the SimpleClickListener. This implements all methods in the
 * {@link SimpleClickListener}
 **/

public abstract class OnItemChildClickListener extends SimpleClickListener {
    @Override
    public void onItemClick(BaseMultiAdapter adapter, View view, int position) {

    }

    @Override
    public void onItemLongClick(BaseMultiAdapter adapter, View view, int position) {

    }

    @Override
    public void onItemChildClick(BaseMultiAdapter adapter, View view, int position) {
        onSimpleItemChildClick(adapter, view, position);
    }

    @Override
    public void onItemChildLongClick(BaseMultiAdapter adapter, View view, int position) {

    }

    public abstract void onSimpleItemChildClick(BaseMultiAdapter adapter, View view, int position);
}
