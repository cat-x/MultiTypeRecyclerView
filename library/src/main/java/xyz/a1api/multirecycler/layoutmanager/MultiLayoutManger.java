package xyz.a1api.multirecycler.layoutmanager;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.widget.LinearLayout;

/**
 * Created by Cat-x on 2018/7/3.
 * For MultiTypeRecyclerView
 * Cat-x All Rights Reserved
 */
public class MultiLayoutManger {

    public static GridLayoutManager bindGridLayoutManager(final RecyclerView recyclerView, int spanCount) {
        GridLayoutManager layoutManager = new GridLayoutManager(recyclerView.getContext(), spanCount);
        return bindLayoutManager(recyclerView, layoutManager);
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
        return bindLayoutManager(recyclerView, layoutManager);
    }

    public static StaggeredGridLayoutManager bindStaggeredLayoutManager(final RecyclerView recyclerView, int spanCount, @RecyclerView.Orientation int orientation) {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(spanCount, orientation);
        return bindLayoutManager(recyclerView, layoutManager);
    }

    public static <LM extends GridLayoutManager> LM bindLayoutManager(final RecyclerView recyclerView, LM layoutManager) {
        recyclerView.setLayoutManager(layoutManager);
        return layoutManager;
    }

    public static <LM extends LinearLayoutManager> LM bindLayoutManager(final RecyclerView recyclerView, LM layoutManager) {
        recyclerView.setLayoutManager(layoutManager);
        return layoutManager;
    }

    public static <LM extends StaggeredGridLayoutManager> LM bindLayoutManager(final RecyclerView recyclerView, LM layoutManager) {
        recyclerView.setLayoutManager(layoutManager);
        return layoutManager;
    }

    public static <LM extends RecyclerView.LayoutManager> LM bindLayoutManager(final RecyclerView recyclerView, LM layoutManager) {
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
