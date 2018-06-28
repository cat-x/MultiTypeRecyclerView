package xyz.a1api.multirecycler.core;

import android.support.annotation.Nullable;

import java.util.List;

import xyz.a1api.multirecycler.base.BaseQuickAdapter;

/**
 * Created by Cat-x on 2018/6/28.
 * For MultiTypeRecyclerView
 * Cat-x All Rights Reserved
 */
public abstract class MultiAdapter extends BaseQuickAdapter {
    public MultiAdapter(int layoutResId, @Nullable List data) {
        super(layoutResId, data);
    }

    public MultiAdapter(@Nullable List data) {
        super(data);
    }

    public MultiAdapter(int layoutResId) {
        super(layoutResId);
    }
}
