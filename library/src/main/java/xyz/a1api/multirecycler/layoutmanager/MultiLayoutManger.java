package xyz.a1api.multirecycler.layoutmanager;

import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.widget.LinearLayout;

import xyz.a1api.multirecycler.BaseQuickAdapter;

/**
 * Created by Cat-x on 2018/7/3.
 * For MultiTypeRecyclerView
 * Cat-x All Rights Reserved
 */
public class MultiLayoutManger {

    public interface ISpanSize {
        int getSpanSize(Object data);
    }

    public static GridLayoutManager bindGridLayoutManager(final RecyclerView recyclerView, int spanCount) {
        return bindGridLayoutManager(recyclerView, spanCount, null);
    }

    public static GridLayoutManager bindGridLayoutManager(final RecyclerView recyclerView, int spanCount, @Nullable final ISpanSize iSpanSize) {
        GridLayoutManager layoutManager = new GridLayoutManager(recyclerView.getContext(), spanCount);
        if (iSpanSize != null) {
            GridLayoutManager.SpanSizeLookup spanSizeLookup = new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    Object item = ((BaseQuickAdapter) recyclerView.getAdapter()).getItem(position);
                    return iSpanSize.getSpanSize(item);
                }
            };
            layoutManager.setSpanSizeLookup(spanSizeLookup);
        }
        recyclerView.setLayoutManager(layoutManager);
        return layoutManager;
    }

    public static LinearLayoutManager bindHLinearLayoutManager(final RecyclerView recyclerView) {
        return bindLinearLayoutManager(recyclerView, LinearLayout.HORIZONTAL);
    }

    public static LinearLayoutManager bindVLinearLayoutManager(final RecyclerView recyclerView) {
        return bindLinearLayoutManager(recyclerView, LinearLayout.VERTICAL);
    }

    public static LinearLayoutManager bindLinearLayoutManager(final RecyclerView recyclerView, @RecyclerView.Orientation int orientation) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
        layoutManager.setOrientation(orientation);
        recyclerView.setLayoutManager(layoutManager);
        return layoutManager;
    }

    public static StaggeredGridLayoutManager bindStaggeredGridLayoutManager(final RecyclerView recyclerView, int spanCount, @RecyclerView.Orientation int orientation) {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(spanCount, orientation);
        recyclerView.setLayoutManager(layoutManager);
        return layoutManager;
    }

    public static <LM extends RecyclerView.LayoutManager> LM setReverseLayout(LM layoutManager, boolean reverseLayout) {
        if (layoutManager instanceof LinearLayoutManager) {
            ((LinearLayoutManager) layoutManager).setReverseLayout(reverseLayout);
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            ((StaggeredGridLayoutManager) layoutManager).setReverseLayout(reverseLayout);
        } else {
            Log.e("MultiLayoutManger", "setReverseLayout(layoutManager,reverseLayout) had error," +
                    "don't find layoutManager setReverseLayout Method");
        }
        return layoutManager;
    }

}
