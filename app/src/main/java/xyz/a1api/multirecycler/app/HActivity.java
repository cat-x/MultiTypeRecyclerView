package xyz.a1api.multirecycler.app;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import xyz.a1api.multirecycler.BaseMultiAdapter;
import xyz.a1api.multirecycler.BaseViewHolder;
import xyz.a1api.multirecycler.Binder;
import xyz.a1api.multirecycler.layoutmanager.MultiLayoutManger;
import xyz.a1api.multirecycler.multi.ClassLinker;

/**
 * Created by Cat-x on 2018/6/29.
 * For MultiTypeRecyclerView
 * Cat-x All Rights Reserved
 */
public class HActivity extends AppCompatActivity {

    MyAdapter adapter;
    GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_h);
        RecyclerView recyclerView = findViewById(R.id.myRecyclerView);
        gridLayoutManager = MultiLayoutManger.bindGridLayoutManager(recyclerView, 2);
        adapter = new MyAdapter().bindToRecyclerView(recyclerView);
        List<LoadingItem> data = new ArrayList<LoadingItem>();
        data.add(new LoadingItem("1", false));
        data.add(new LoadingItem("2", true));
        adapter.addData(data);
    }

    public void demo() {
        new BaseMultiAdapter().register(String.class).to(new Binder<String, HBaseViewHolder>() {
            @Override
            public int getLayoutId() {
                return 0;
            }

            @Override
            public void convert(@NonNull HBaseViewHolder holder, @NonNull String item, int position) {

            }


        }, new Binder<String, BaseViewHolder>() {
            @Override
            public int getLayoutId() {
                return 0;
            }

            @Override
            public void convert(@NonNull BaseViewHolder holder, @NonNull String item, int position) {

            }

        }).withClassLinker(new ClassLinker<String>() {
            @NonNull
            @Override
            public Class<? extends Binder<? extends BaseViewHolder, ?>> index(int position, @NonNull String s) {
                return null;
            }
        });
    }
}
