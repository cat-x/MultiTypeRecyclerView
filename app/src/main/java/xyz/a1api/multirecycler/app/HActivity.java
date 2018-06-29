package xyz.a1api.multirecycler.app;

import android.os.Bundle;
import android.support.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import xyz.a1api.multirecycler.base.BaseQuickAdapter;
import xyz.a1api.multirecycler.base.BaseViewHolder;
import xyz.a1api.multirecycler.base.Binder;

/**
 * Created by Cat-x on 2018/6/29.
 * For MultiTypeRecyclerView
 * Cat-x All Rights Reserved
 */
public class HActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new BaseQuickAdapter().register(String.class).to(new Binder<String, HBaseViewHolder>() {

            @Override
            public int getLayoutId() {
                return 0;
            }

            @Override
            public <K extends BaseViewHolder> void convert(@NonNull K holder, @NonNull Object item) {

            }

        })
    }
}
