package xyz.a1api.paging;

import android.arch.paging.PagedList;
import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.recyclerview.extensions.AsyncDifferConfig;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.Collection;
import java.util.List;

import xyz.a1api.multirecycler.BaseMultiAdapter;
import xyz.a1api.multirecycler.BaseViewHolder;

/**
 * Created by Cat-x on 2018/7/27.
 * For MultiTypeRecyclerView
 * Cat-x All Rights Reserved
 */
public class PagedMultiAdapter extends BaseMultiAdapter {

    private ProxyAdapter proxyAdapter;

    public ProxyAdapter getAdapter() {
        return proxyAdapter;
    }

    public PagedMultiAdapter(@NonNull DiffUtil.ItemCallback<Object> diffCallback) {
        super();
        this.proxyAdapter = new ProxyAdapter(diffCallback);
    }

    public PagedMultiAdapter(@NonNull AsyncDifferConfig<Object> config) {
        super();
        this.proxyAdapter = new ProxyAdapter(config);
    }

    public PagedMultiAdapter(@Nullable PagedList<Object> pagedList, @NonNull DiffUtil.ItemCallback<Object> diffCallback) {
        this(diffCallback);
        submitList(pagedList);
    }

    public PagedMultiAdapter(@Nullable PagedList<Object> pagedList, @NonNull AsyncDifferConfig<Object> config) {
        this(config);
        submitList(pagedList);
    }


    @Override
    public <BA extends BaseMultiAdapter> BA bindToRecyclerView(RecyclerView recyclerView) {
        if (getRecyclerView() != null) {
            throw new RuntimeException("Don't bind twice");
        }
        setRecyclerView(recyclerView);
        getRecyclerView().setAdapter(getAdapter());
        return (BA) this;
    }

    public void submitList(PagedList<Object> pagedList) {
        proxyAdapter.submitList(pagedList);
    }

    @Nullable
    public PagedList<Object> getCurrentList() {
        return proxyAdapter.getCurrentList();
    }

    @Override
    protected int getDataSize() {
        return proxyAdapter.getOriginalItemCount();
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public Object getItem(int position) {
        return proxyAdapter.getItem(position);
    }

    @Override
    protected int getDefItemViewType(int position) {
        return indexInTypesOf(position, getItem(position));
    }

    public void onCurrentListChanged(@Nullable PagedList<Object> currentList) {

    }


    class ProxyAdapter extends PagedListAdapter<Object, BaseViewHolder> {

        protected ProxyAdapter(@NonNull DiffUtil.ItemCallback<Object> diffCallback) {
            super(diffCallback);
        }

        protected ProxyAdapter(@NonNull AsyncDifferConfig<Object> config) {
            super(config);
        }


        @Nullable
        @Override
        public Object getItem(int position) {
            return super.getItem(position);
        }

        @Override
        public int getItemCount() {
            return PagedMultiAdapter.this.getItemCount();
        }

        public int getOriginalItemCount() {
            return super.getItemCount();
        }

        @Override
        public int getItemViewType(int position) {
            return PagedMultiAdapter.this.getItemViewType(position);
        }

        @NonNull
        @Override
        public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return PagedMultiAdapter.this.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
            PagedMultiAdapter.this.onBindViewHolder(holder, position);
        }

        @Override
        public long getItemId(int position) {
            return PagedMultiAdapter.this.getItemId(position);
        }

        @Override
        public void onViewAttachedToWindow(BaseViewHolder holder) {
            PagedMultiAdapter.this.onViewAttachedToWindow(holder);
        }

        @Override
        public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
            PagedMultiAdapter.this.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public void onCurrentListChanged(@Nullable PagedList<Object> currentList) {
            PagedMultiAdapter.this.onCurrentListChanged(currentList);
        }
    }


    @Override
    @Deprecated
    public void setData(@Nullable List<Object> data) {
        throw new RuntimeException("in PagedListAdapter ,you should use submitList()");
    }

    @Override
    @Deprecated
    public void add(int position, @NonNull Object item) {
        throw new RuntimeException("in PagedListAdapter ,you should use submitList()");
    }

    @Override
    @Deprecated
    public void addData(int position, @NonNull Object data) {
        throw new RuntimeException("in PagedListAdapter ,you should use submitList()");
    }

    @Override
    @Deprecated
    public void addData(@NonNull Object data) {
        throw new RuntimeException("in PagedListAdapter ,you should use submitList()");
    }

    @Override
    @Deprecated
    public void remove(int position) {
        throw new RuntimeException("in PagedListAdapter ,you should use submitList()");
    }

    @Override
    @Deprecated
    public void setData(int index, @NonNull Object data) {
        throw new RuntimeException("in PagedListAdapter ,you should use submitList()");
    }

    @Override
    @Deprecated
    public void addData(int position, @NonNull Collection<?> newData) {
        throw new RuntimeException("in PagedListAdapter ,you should use submitList()");
    }

    @Override
    @Deprecated
    public void addData(@NonNull Collection<?> newData) {
        throw new RuntimeException("in PagedListAdapter ,you should use submitList()");
    }

    @Override
    @Deprecated
    public void replaceData(@NonNull Collection<?> data) {
        throw new RuntimeException("in PagedListAdapter ,you should use submitList()");
    }

    @Override
    @Deprecated
    public void clearData() {
        throw new RuntimeException("in PagedListAdapter ,you should use submitList()");
    }

    @NonNull
    @Override
    @Deprecated
    public List<Object> getData() {
        throw new RuntimeException("in PagedListAdapter ,you should use getCurrentList()");
    }

}
