package xyz.a1api.multirecycler.snaphelper;

import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Cat-x on 2018/7/3.
 * For MultiTypeRecyclerView
 * Cat-x All Rights Reserved
 */
public class MultiSnapHelper {

    public static LinearSnapHelper bindLinearSnapHelper(RecyclerView recyclerView) {
        LinearSnapHelper linearSnapHelper = new LinearSnapHelper();
        linearSnapHelper.attachToRecyclerView(recyclerView);
        return linearSnapHelper;
    }

    public static PagerSnapHelper bindPagerSnapHelper(RecyclerView recyclerView) {
        PagerSnapHelper linearSnapHelper = new PagerSnapHelper();
        linearSnapHelper.attachToRecyclerView(recyclerView);
        return linearSnapHelper;
    }
}
