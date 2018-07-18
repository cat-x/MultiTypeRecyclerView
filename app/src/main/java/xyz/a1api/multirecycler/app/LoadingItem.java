package xyz.a1api.multirecycler.app;

import android.util.Log;

/**
 * Created by Cat-x on 2018/7/18.
 * For MultiTypeRecyclerView
 * Cat-x All Rights Reserved
 */
public class LoadingItem {
    String title;
    Boolean isOk;

    public LoadingItem(String title, Boolean isOk) {
        this.title = title;
        this.isOk = isOk;
    }

    public void onClick() {
        Log.i("MultiAdapter", "yes onClick");
    }
}
