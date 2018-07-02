package xyz.a1api.multirecycler.app;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import xyz.a1api.multirecycler.BaseQuickAdapter;
import xyz.a1api.multirecycler.BaseViewHolder;
import xyz.a1api.multirecycler.Binder;
import xyz.a1api.multirecycler.multi.ClassLinker;
import xyz.a1api.multirecycler.multi.Linker;

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
            public void convert(@NonNull HBaseViewHolder holder, @NonNull Object item) {

            }
        }, new Binder<String, BaseViewHolder>() {
            @Override
            public int getLayoutId() {
                return 0;
            }

            @Override
            public void convert(@NonNull BaseViewHolder holder, @NonNull Object item) {

            }
        }).withClassLinker(new ClassLinker<String>() {
            @NonNull
            @Override
            public Class<? extends Binder<? extends BaseViewHolder, ?>> index(int position, @NonNull String s) {
                return null;
            }
        });

        new BaseQuickAdapter().register(String.class).to(new Binder<String, HBaseViewHolder>() {
            @Override
            public int getLayoutId() {
                return 0;
            }

            @Override
            public void convert(@NonNull HBaseViewHolder holder, @NonNull Object item) {

            }
        }, new Binder<String, BaseViewHolder>() {
            @Override
            public int getLayoutId() {
                return 0;
            }

            @Override
            public void convert(@NonNull BaseViewHolder holder, @NonNull Object item) {

            }
        }).withLinker(new Linker<String>() {
            @Override
            public int index(int position, @NonNull String s) {
                return 0;
            }
        });
    }
}
