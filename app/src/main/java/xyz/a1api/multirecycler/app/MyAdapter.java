package xyz.a1api.multirecycler.app;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import xyz.a1api.multirecycler.BaseMultiAdapter;
import xyz.a1api.multirecycler.BaseViewHolder;
import xyz.a1api.multirecycler.Binder;

/**
 * Created by Cat-x on 2018/7/18.
 * For MultiTypeRecyclerView
 * Cat-x All Rights Reserved
 */
public class MyAdapter extends BaseMultiAdapter {
    public MyAdapter() {
        super();
        register(String.class, new Binder<String, BaseViewHolder>() {
            @Override
            public int getLayoutId() {
                //TODO("not implemented")
                return R.layout.loading_item;
            }

            @Override
            public void convert(@NonNull BaseViewHolder holder, @NonNull String item, int position) {
                //TODO("not implemented")
            }
        });
        register(LoadingItem.class, new Binder<LoadingItem, BaseViewHolder>() {
            @Override
            public int getLayoutId() {
                return R.layout.loading_item;
            }

            @Override
            public void convert(@NonNull BaseViewHolder holder, @NonNull LoadingItem item, int position) {
                holder.setText(R.id.text, item.title);
            }

            @Override
            public void onClick(View v, @NonNull LoadingItem item, @NonNull int position) {
                item.onClick();
            }

            @Override
            public void onLongClick(View view, @NonNull LoadingItem item, @NonNull int position) {
                if (item.isOk) {
                    Log.i("MultiAdapter", "item.isOk");
                }
            }

            @Override
            public int getSpanSize() {
                return super.getSpanSize();
            }
        });

    }
}
