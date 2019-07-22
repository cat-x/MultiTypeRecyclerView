/*
 * Copyright 2013 Joan Zapata
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xyz.a1api.multirecycler;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutParams;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import xyz.a1api.multirecycler.animation.AlphaInAnimation;
import xyz.a1api.multirecycler.animation.BaseAnimation;
import xyz.a1api.multirecycler.animation.ScaleInAnimation;
import xyz.a1api.multirecycler.animation.SlideInBottomAnimation;
import xyz.a1api.multirecycler.animation.SlideInLeftAnimation;
import xyz.a1api.multirecycler.animation.SlideInRightAnimation;
import xyz.a1api.multirecycler.entity.IExpandable;
import xyz.a1api.multirecycler.loadmore.LoadMoreView;
import xyz.a1api.multirecycler.loadmore.SimpleLoadMoreView;
import xyz.a1api.multirecycler.multi.BinderNotFoundException;
import xyz.a1api.multirecycler.multi.DefaultLinker;
import xyz.a1api.multirecycler.multi.Linker;
import xyz.a1api.multirecycler.multi.MultiTypePool;
import xyz.a1api.multirecycler.multi.OneToManyFlow;
import xyz.a1api.multirecycler.multi.Preconditions;
import xyz.a1api.multirecycler.multi.TypePool;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;


/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public class BaseMultiAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    //load more
    private boolean mNextLoadEnable = false;
    private boolean mLoadMoreEnable = false;
    private boolean mLoading = false;
    protected LoadMoreView mLoadMoreView = new SimpleLoadMoreView();
    private RequestLoadMoreListener mRequestLoadMoreListener;
    private boolean mEnableLoadMoreEndClick = false;

    //Animation
    /**
     * Use with {@link #openLoadAnimation}
     */
    public static final int ALPHAIN = 0x00000001;
    /**
     * Use with {@link #openLoadAnimation}
     */
    public static final int SCALEIN = 0x00000002;
    /**
     * Use with {@link #openLoadAnimation}
     */
    public static final int SLIDEIN_BOTTOM = 0x00000003;
    /**
     * Use with {@link #openLoadAnimation}
     */
    public static final int SLIDEIN_LEFT = 0x00000004;
    /**
     * Use with {@link #openLoadAnimation}
     */
    public static final int SLIDEIN_RIGHT = 0x00000005;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    private OnItemChildClickListener mOnItemChildClickListener;
    private OnItemChildLongClickListener mOnItemChildLongClickListener;

    @IntDef({ALPHAIN, SCALEIN, SLIDEIN_BOTTOM, SLIDEIN_LEFT, SLIDEIN_RIGHT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AnimationType {
    }

    private boolean mFirstOnlyEnable = true;
    private boolean mOpenAnimationEnable = false;
    private Interpolator mInterpolator = new LinearInterpolator();
    private int mDuration = 300;
    private int mLastPosition = -1;

    private BaseAnimation mCustomAnimation;
    private BaseAnimation mSelectAnimation = new AlphaInAnimation();
    //header footer
    private LinearLayout mHeaderLayout;
    private LinearLayout mFooterLayout;
    //empty
    private FrameLayout mEmptyLayout;
    private boolean mIsUseEmpty = true;
    private boolean mHeadAndEmptyEnable;
    private boolean mFootAndEmptyEnable;

    protected static final String TAG = BaseMultiAdapter.class.getSimpleName();
    protected Context mContext;
    protected LayoutInflater mLayoutInflater;

    protected @NonNull
    List<Object> mData = new LinkedList<>();

    protected int getDataSize() {
        return mData.size();
    }

    public static final int HEADER_VIEW = 0x00000111;
    public static final int LOADING_VIEW = 0x00000222;
    public static final int FOOTER_VIEW = 0x00000333;
    public static final int EMPTY_VIEW = 0x00000555;

    private RecyclerView mRecyclerView;

    protected final RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    protected final void setRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    private void checkRecyclerViewNotNull() {
        if (getRecyclerView() == null) {
            throw new RuntimeException("please bind recyclerView first!");
        }
    }

    /**
     * same as recyclerView.setAdapter(), and save the instance of recyclerView
     */
    @CheckResult
    public <BA extends BaseMultiAdapter> BA bindToRecyclerView(RecyclerView recyclerView) {
        if (getRecyclerView() != null) {
            throw new RuntimeException("Don't bind twice");
        }
        setRecyclerView(recyclerView);
        getRecyclerView().setAdapter(this);
        return (BA) this;
    }

    /**
     * @see #setOnLoadMoreListener(RequestLoadMoreListener, RecyclerView)
     * @deprecated This method is because it can lead to crash: always call this method while RecyclerView is computing a layout or scrolling.
     * Please use {@link #setOnLoadMoreListener(RequestLoadMoreListener, RecyclerView)}
     */
    @Deprecated
    public void setOnLoadMoreListener(RequestLoadMoreListener requestLoadMoreListener) {
        openLoadMore(requestLoadMoreListener);
    }

    private void openLoadMore(RequestLoadMoreListener requestLoadMoreListener) {
        this.mRequestLoadMoreListener = requestLoadMoreListener;
        mNextLoadEnable = true;
        mLoadMoreEnable = true;
        mLoading = false;
    }

    public void setOnLoadMoreListener(RequestLoadMoreListener requestLoadMoreListener, RecyclerView recyclerView) {
        openLoadMore(requestLoadMoreListener);
        if (getRecyclerView() == null) {
            setRecyclerView(recyclerView);
        }
    }

    /**
     * bind recyclerView {@link #bindToRecyclerView(RecyclerView)} before use!
     *
     * @see #disableLoadMoreIfNotFullPage(RecyclerView)
     */
    public void disableLoadMoreIfNotFullPage() {
        checkRecyclerViewNotNull();
        disableLoadMoreIfNotFullPage(getRecyclerView());
    }

    /**
     * check if full page after {@link #setData(List)}, if full, it will enable load more again.
     * <p>
     * 不是配置项！！
     * <p>
     * 这个方法是用来检查是否满一屏的，所以只推荐在 {@link #setData(List)} 之后使用
     * 原理很简单，先关闭 load more，检查完了再决定是否开启
     * <p>
     * 不是配置项！！
     *
     * @param recyclerView your recyclerView
     * @see #setData(List)
     */
    public void disableLoadMoreIfNotFullPage(RecyclerView recyclerView) {
        setEnableLoadMore(false);
        if (recyclerView == null) return;
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager == null) return;
        if (manager instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) manager;
            recyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isFullScreen(linearLayoutManager)) {
                        setEnableLoadMore(true);
                    }
                }
            }, 50);
        } else if (manager instanceof StaggeredGridLayoutManager) {
            final StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) manager;
            recyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    final int[] positions = new int[staggeredGridLayoutManager.getSpanCount()];
                    staggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(positions);
                    int pos = getTheBiggestNumber(positions) + 1;
                    if (pos != getItemCount()) {
                        setEnableLoadMore(true);
                    }
                }
            }, 50);
        }
    }

    private boolean isFullScreen(LinearLayoutManager llm) {
        return (llm.findLastCompletelyVisibleItemPosition() + 1) != getItemCount() ||
                llm.findFirstCompletelyVisibleItemPosition() != 0;
    }

    private int getTheBiggestNumber(int[] numbers) {
        int tmp = -1;
        if (numbers == null || numbers.length == 0) {
            return tmp;
        }
        for (int num : numbers) {
            if (num > tmp) {
                tmp = num;
            }
        }
        return tmp;
    }

    /**
     * up fetch start
     */
    private boolean mUpFetchEnable;
    private boolean mUpFetching;
    private UpFetchListener mUpFetchListener;

    public void setUpFetchEnable(boolean upFetch) {
        this.mUpFetchEnable = upFetch;
    }

    public boolean isUpFetchEnable() {
        return mUpFetchEnable;
    }

    /**
     * start up fetch position, default is 1.
     */
    private int mStartUpFetchPosition = 1;

    public void setStartUpFetchPosition(int startUpFetchPosition) {
        mStartUpFetchPosition = startUpFetchPosition;
    }

    private void autoUpFetch(int positions) {
        if (!isUpFetchEnable() || isUpFetching()) {
            return;
        }
        if (positions <= mStartUpFetchPosition && mUpFetchListener != null) {
            mUpFetchListener.onUpFetch();
        }
    }

    public boolean isUpFetching() {
        return mUpFetching;
    }

    public void setUpFetching(boolean upFetching) {
        this.mUpFetching = upFetching;
    }

    public void setUpFetchListener(UpFetchListener upFetchListener) {
        mUpFetchListener = upFetchListener;
    }

    public interface UpFetchListener {
        void onUpFetch();
    }

    /**
     * up fetch end
     */
    public void setNotDoAnimationCount(int count) {
        mLastPosition = count;
    }

    /**
     * Set custom load more
     *
     * @param loadingView 加载视图
     */
    public void setLoadMoreView(LoadMoreView loadingView) {
        this.mLoadMoreView = loadingView;
    }

    /**
     * Load more view count
     *
     * @return 0 or 1
     */
    public int getLoadMoreViewCount() {
        if (mRequestLoadMoreListener == null || !mLoadMoreEnable) {
            return 0;
        }
        if (!mNextLoadEnable && mLoadMoreView.isLoadEndMoreGone()) {
            return 0;
        }
        if (mData.size() == 0) {
            return 0;
        }
        return 1;
    }

    /**
     * Gets to load more locations
     *
     * @return
     */
    public int getLoadMoreViewPosition() {
        return getHeaderLayoutCount() + mData.size() + getFooterLayoutCount();
    }

    /**
     * @return Whether the Adapter is actively showing load
     * progress.
     */
    public boolean isLoading() {
        return mLoading;
    }


    /**
     * Refresh end, no more data
     */
    public void loadMoreEnd() {
        loadMoreEnd(false);
    }

    /**
     * Refresh end, no more data
     *
     * @param gone if true gone the load more view
     */
    public void loadMoreEnd(boolean gone) {
        if (getLoadMoreViewCount() == 0) {
            return;
        }
        mLoading = false;
        mNextLoadEnable = false;
        mLoadMoreView.setLoadMoreEndGone(gone);
        if (gone) {
            notifyItemRemoved(getLoadMoreViewPosition());
        } else {
            mLoadMoreView.setLoadMoreStatus(LoadMoreView.STATUS_END);
            notifyItemChanged(getLoadMoreViewPosition());
        }
    }

    /**
     * Refresh complete
     */
    public void loadMoreComplete() {
        if (getLoadMoreViewCount() == 0) {
            return;
        }
        mLoading = false;
        mNextLoadEnable = true;
        mLoadMoreView.setLoadMoreStatus(LoadMoreView.STATUS_DEFAULT);
        notifyItemChanged(getLoadMoreViewPosition());
    }

    /**
     * Refresh failed
     */
    public void loadMoreFail() {
        if (getLoadMoreViewCount() == 0) {
            return;
        }
        mLoading = false;
        mLoadMoreView.setLoadMoreStatus(LoadMoreView.STATUS_FAIL);
        notifyItemChanged(getLoadMoreViewPosition());
    }

    /**
     * Set the enabled state of load more.
     *
     * @param enable True if load more is enabled, false otherwise.
     */
    public void setEnableLoadMore(boolean enable) {
        int oldLoadMoreCount = getLoadMoreViewCount();
        mLoadMoreEnable = enable;
        int newLoadMoreCount = getLoadMoreViewCount();

        if (oldLoadMoreCount == 1) {
            if (newLoadMoreCount == 0) {
                notifyItemRemoved(getLoadMoreViewPosition());
            }
        } else {
            if (newLoadMoreCount == 1) {
                mLoadMoreView.setLoadMoreStatus(LoadMoreView.STATUS_DEFAULT);
                notifyItemInserted(getLoadMoreViewPosition());
            }
        }
    }

    /**
     * Returns the enabled status for load more.
     *
     * @return True if load more is enabled, false otherwise.
     */
    public boolean isLoadMoreEnable() {
        return mLoadMoreEnable;
    }

    /**
     * Sets the duration of the animation.
     *
     * @param duration The length of the animation, in milliseconds.
     */
    public void setDuration(int duration) {
        mDuration = duration;
    }

    /**
     * If you have added headeview, the notification view refreshes.
     * Do not need to care about the number of headview, only need to pass in the position of the final view
     *
     * @param position
     */
    public final void refreshNotifyItemChanged(int position) {
        notifyItemChanged(position + getHeaderLayoutCount());
    }


    public BaseMultiAdapter(@Nullable List<Object> data, @NonNull TypePool pool) {
        this.mData = data == null ? new LinkedList<>() : data;
        this.typePool = pool;
    }

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data and an initial capacity of TypePool..
     *
     * @param data            A new list is created out of this one to avoid mutable list
     * @param initialCapacity the initial capacity of TypePool
     */
    public BaseMultiAdapter(@Nullable List<Object> data, int initialCapacity) {
        this(data, new MultiTypePool(initialCapacity));
    }

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public BaseMultiAdapter(@Nullable List<Object> data) {
        this(data, new MultiTypePool());
    }


    public BaseMultiAdapter() {
        this(null);
    }

    /**
     * setting up a new instance to data;
     *
     * @param data
     */
    public void setData(@Nullable List<Object> data) {
        this.mData = data == null ? new LinkedList<>() : data;
        if (mRequestLoadMoreListener != null) {
            mNextLoadEnable = true;
            mLoadMoreEnable = true;
            mLoading = false;
            mLoadMoreView.setLoadMoreStatus(LoadMoreView.STATUS_DEFAULT);
        }
        mLastPosition = -1;
        notifyDataSetChanged();
    }


    /**
     * insert  a item associated with the specified position of adapter
     *
     * @param position
     * @param item
     * @deprecated use {@link #addData(int, Object)} instead
     */
    @Deprecated
    public void add(@IntRange(from = 0) int position, @NonNull Object item) {
        addData(position, item);
    }

    /**
     * add one new data in to certain location
     *
     * @param position
     */
    public void addData(@IntRange(from = 0) int position, @NonNull Object data) {
        mData.add(position, data);
        notifyItemInserted(position + getHeaderLayoutCount());
        compatibilityDataSizeChanged(1);
    }

    /**
     * add one new data
     */
    public void addData(@NonNull Object data) {
        mData.add(data);
        notifyItemInserted(mData.size() + getHeaderLayoutCount());
        compatibilityDataSizeChanged(1);
    }

    /**
     * remove the item associated with the specified position of adapter
     *
     * @param position
     */
    public void remove(@IntRange(from = 0) int position) {
        mData.remove(position);
        int internalPosition = position + getHeaderLayoutCount();
        notifyItemRemoved(internalPosition);
        compatibilityDataSizeChanged(0);
        notifyItemRangeChanged(internalPosition, mData.size() - internalPosition);
    }

    /**
     * change data
     */
    public void setData(@IntRange(from = 0) int index, @NonNull Object data) {
        mData.set(index, data);
        notifyItemChanged(index + getHeaderLayoutCount());
    }

    /**
     * add new data in to certain location
     *
     * @param position the insert position
     * @param newData  the new data collection
     */
    public void addData(@IntRange(from = 0) int position, @NonNull Collection<? extends Object> newData) {
        mData.addAll(position, newData);
        notifyItemRangeInserted(position + getHeaderLayoutCount(), newData.size());
        compatibilityDataSizeChanged(newData.size());
    }

    /**
     * add new data to the end of mData
     *
     * @param newData the new data collection
     */
    public void addData(@NonNull Collection<? extends Object> newData) {
        mData.addAll(newData);
        notifyItemRangeInserted(mData.size() - newData.size() + getHeaderLayoutCount(), newData.size());
        compatibilityDataSizeChanged(newData.size());
    }

    /**
     * use data to replace all item in mData. this method is different {@link #setData(List)},
     * it doesn't change the mData reference
     *
     * @param data data collection
     */
    public void replaceData(@NonNull Collection<? extends Object> data) {
        // 不是同一个引用才清空列表
        if (data != mData) {
            mData.clear();
            mData.addAll(data);
        }
        notifyDataSetChanged();
    }

    public void clearData() {
        mData.clear();
        notifyDataSetChanged();
    }

    /**
     * compatible getLoadMoreViewCount and getEmptyViewCount may change
     *
     * @param size Need compatible data size
     */
    private void compatibilityDataSizeChanged(int size) {
        final int dataSize = mData == null ? 0 : mData.size();
        if (dataSize == size) {
            notifyDataSetChanged();
        }
    }

    /**
     * Get the data of list
     *
     * @return 列表数据
     */
    @NonNull
    public List<Object> getData() {
        return mData;
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T getItem(@IntRange(from = 0) int position) {
        if (position >= 0 && position < mData.size())
            return (T) mData.get(position);
        else
            return null;
    }

    /**
     * if setHeadView will be return 1 if not will be return 0.
     * notice: Deprecated! Use {@link ViewGroup#getChildCount()} of {@link #getHeaderLayout()} to replace.
     *
     * @return
     */
    @Deprecated
    public int getHeaderViewsCount() {
        return getHeaderLayoutCount();
    }

    /**
     * if mFooterLayout will be return 1 or not will be return 0.
     * notice: Deprecated! Use {@link ViewGroup#getChildCount()} of {@link #getFooterLayout()} to replace.
     *
     * @return
     */
    @Deprecated
    public int getFooterViewsCount() {
        return getFooterLayoutCount();
    }

    /**
     * if addHeaderView will be return 1, if not will be return 0
     */
    public int getHeaderLayoutCount() {
        if (mHeaderLayout == null || mHeaderLayout.getChildCount() == 0) {
            return 0;
        }
        return 1;
    }

    /**
     * if addFooterView will be return 1, if not will be return 0
     */
    public int getFooterLayoutCount() {
        if (mFooterLayout == null || mFooterLayout.getChildCount() == 0) {
            return 0;
        }
        return 1;
    }

    /**
     * if show empty view will be return 1 or not will be return 0
     *
     * @return
     */
    public int getEmptyViewCount() {
        if (mEmptyLayout == null || mEmptyLayout.getChildCount() == 0) {
            return 0;
        }
        if (!mIsUseEmpty) {
            return 0;
        }
        if (mData.size() != 0) {
            return 0;
        }
        return 1;
    }

    @Override
    public int getItemCount() {
        int count;
        if (getEmptyViewCount() == 1) {
            count = 1;
            if (mHeadAndEmptyEnable && getHeaderLayoutCount() != 0) {
                count++;
            }
            if (mFootAndEmptyEnable && getFooterLayoutCount() != 0) {
                count++;
            }
        } else {
            count = getHeaderLayoutCount() + getDataSize() + getFooterLayoutCount() + getLoadMoreViewCount();
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (getEmptyViewCount() == 1) {
            boolean header = mHeadAndEmptyEnable && getHeaderLayoutCount() != 0;
            switch (position) {
                case 0:
                    if (header) {
                        return HEADER_VIEW;
                    } else {
                        return EMPTY_VIEW;
                    }
                case 1:
                    if (header) {
                        return EMPTY_VIEW;
                    } else {
                        return FOOTER_VIEW;
                    }
                case 2:
                    return FOOTER_VIEW;
                default:
                    return EMPTY_VIEW;
            }
        }
        int numHeaders = getHeaderLayoutCount();
        if (position < numHeaders) {
            return HEADER_VIEW;
        } else {
            int adjPosition = position - numHeaders;
            int adapterCount = getDataSize();
            if (adjPosition < adapterCount) {
                return getDefItemViewType(adjPosition);
            } else {
                adjPosition = adjPosition - adapterCount;
                int numFooters = getFooterLayoutCount();
                if (adjPosition < numFooters) {
                    return FOOTER_VIEW;
                } else {
                    return LOADING_VIEW;
                }
            }
        }
    }

    protected int getDefItemViewType(int position) {
        Object item = mData.get(position);
        return indexInTypesOf(position, item);
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BaseViewHolder baseViewHolder = null;
        this.mContext = parent.getContext();
        this.mLayoutInflater = LayoutInflater.from(mContext);
        switch (viewType) {
            case LOADING_VIEW:
                baseViewHolder = getLoadingView(parent);
                break;
            case HEADER_VIEW:
                baseViewHolder = new BaseViewHolder(mHeaderLayout);
                break;
            case EMPTY_VIEW:
                baseViewHolder = new BaseViewHolder(mEmptyLayout);
                break;
            case FOOTER_VIEW:
                baseViewHolder = new BaseViewHolder(mFooterLayout);
                break;
            default:
                Binder<?, ?> binder = typePool.getItemViewBinder(viewType);
                baseViewHolder = binder.getViewHolder(this, getItemView(binder.getLayoutId(), parent));
                bindViewClickListener(baseViewHolder, binder);
                break;
        }
        baseViewHolder.setAdapter(this);
        return baseViewHolder;

    }

    @SuppressWarnings("unchecked")
    private BaseViewHolder getLoadingView(ViewGroup parent) {
        View view = getItemView(mLoadMoreView.getLayoutId(), parent);
        BaseViewHolder holder = new BaseViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLoadMoreView.getLoadMoreStatus() == LoadMoreView.STATUS_FAIL) {
                    notifyLoadMoreToLoading();
                }
                if (mEnableLoadMoreEndClick && mLoadMoreView.getLoadMoreStatus() == LoadMoreView.STATUS_END) {
                    notifyLoadMoreToLoading();
                }
            }
        });
        return holder;
    }

    /**
     * The notification starts the callback and loads more
     */
    public void notifyLoadMoreToLoading() {
        if (mLoadMoreView.getLoadMoreStatus() == LoadMoreView.STATUS_LOADING) {
            return;
        }
        mLoadMoreView.setLoadMoreStatus(LoadMoreView.STATUS_DEFAULT);
        notifyItemChanged(getLoadMoreViewPosition());
    }

    /**
     * Load more without data when settings are clicked loaded
     *
     * @param enable
     */
    public void enableLoadMoreEndClick(boolean enable) {
        mEnableLoadMoreEndClick = enable;
    }

    /**
     * Called when a view created by this adapter has been attached to a window.
     * simple to solve item will layout using all
     * {@link #setFullSpan(RecyclerView.ViewHolder)}
     *
     * @param holder
     */
    @Override
    public void onViewAttachedToWindow(BaseViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        int type = holder.getItemViewType();
        if (type == EMPTY_VIEW || type == HEADER_VIEW || type == FOOTER_VIEW || type == LOADING_VIEW) {
            setFullSpan(holder);
        } else {
            addAnimation(holder);
        }
    }

    /**
     * When set to true, the item will layout using all span area. That means, if orientation
     * is vertical, the view will have full width; if orientation is horizontal, the view will
     * have full height.
     * if the hold view use StaggeredGridLayoutManager they should using all span area
     *
     * @param holder True if this item should traverse all spans.
     */
    protected void setFullSpan(RecyclerView.ViewHolder holder) {
        if (holder.itemView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) holder
                    .itemView.getLayoutParams();
            params.setFullSpan(true);
        }
    }

    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int type = getItemViewType(position);
                    if (type == HEADER_VIEW && isHeaderViewAsFlow()) {
                        return 1;
                    }
                    if (type == FOOTER_VIEW && isFooterViewAsFlow()) {
                        return 1;
                    }
                    if (mSpanSizeLookup == null) {
                        return isFixedViewType(type) ? gridManager.getSpanCount() : typePool.getItemViewBinder(type).getSpanSize();

                    } else {
                        return (isFixedViewType(type)) ? gridManager.getSpanCount() : mSpanSizeLookup.getSpanSize(gridManager,
                                position - getHeaderLayoutCount());
                    }
                }


            });
        }
    }

    protected boolean isFixedViewType(int type) {
        return type == EMPTY_VIEW || type == HEADER_VIEW || type == FOOTER_VIEW || type ==
                LOADING_VIEW;
    }

    /**
     * if asFlow is true, footer/header will arrange like normal item view.
     * only works when use {@link GridLayoutManager},and it will ignore span size.
     */
    private boolean headerViewAsFlow, footerViewAsFlow;

    public void setHeaderViewAsFlow(boolean headerViewAsFlow) {
        this.headerViewAsFlow = headerViewAsFlow;
    }

    public boolean isHeaderViewAsFlow() {
        return headerViewAsFlow;
    }

    public void setFooterViewAsFlow(boolean footerViewAsFlow) {
        this.footerViewAsFlow = footerViewAsFlow;
    }

    public boolean isFooterViewAsFlow() {
        return footerViewAsFlow;
    }

    private SpanSizeLookup mSpanSizeLookup;

    public interface SpanSizeLookup {
        int getSpanSize(GridLayoutManager gridLayoutManager, int position);
    }

    /**
     * @param spanSizeLookup instance to be used to query number of spans occupied by each item
     */
    public void setSpanSizeLookup(SpanSizeLookup spanSizeLookup) {
        this.mSpanSizeLookup = spanSizeLookup;
    }

    /**
     * To bind different types of holder and solve different the bind events
     *
     * @param holder
     * @param position
     * @see #getDefItemViewType(int)
     */
    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        //Add up fetch logic, almost like load more, but simpler.
        autoUpFetch(position);
        //Do not move position, need to change before LoadMoreView binding
        autoLoadMore(position);
        int viewType = holder.getItemViewType();

        switch (viewType) {
            case LOADING_VIEW:
                mLoadMoreView.convert(holder);
                break;
            case HEADER_VIEW:
                break;
            case EMPTY_VIEW:
                break;
            case FOOTER_VIEW:
                break;
            default:
                typePool.getItemViewBinder(viewType).bind(holder, getItem(position - getHeaderLayoutCount()), position);
                break;
        }
    }

    private void bindViewClickListener(final BaseViewHolder baseViewHolder, final Binder<?, ?> binder) {
        if (baseViewHolder == null) {
            return;
        }
        final View view = baseViewHolder.itemView;
        if (view == null) {
            return;
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = baseViewHolder.getLayoutPosition() - getHeaderLayoutCount();
                binder.click(false, v, getItem(position), position);
                if (getOnItemClickListener() != null) {
                    performItemClick(v, position);
                }
            }
        });


        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = baseViewHolder.getLayoutPosition() - getHeaderLayoutCount();
                binder.click(true, v, getItem(position), position);
                if (getOnItemLongClickListener() != null) {
                    return performItemLongClick(v, position);
                } else {
                    return false;
                }
            }
        });

    }

    /**
     * override this method if you want to override click event logic
     *
     * @param v
     * @param position
     */
    public void performItemClick(View v, int position) {
        getOnItemClickListener().onItemClick(BaseMultiAdapter.this, v, position);
    }

    /**
     * override this method if you want to override longClick event logic
     *
     * @param v
     * @param position
     * @return
     */
    public boolean performItemLongClick(View v, int position) {
        return getOnItemLongClickListener().onItemLongClick(BaseMultiAdapter.this, v, position);
    }


    /**
     * try to create Generic K instance
     *
     * @param z
     * @param view
     * @return
     */
    @SuppressWarnings("unchecked")
    private BaseViewHolder createGenericKInstance(Class z, View view) {
        try {
            Constructor constructor;
            // inner and unstatic class
            if (z.isMemberClass() && !Modifier.isStatic(z.getModifiers())) {
                constructor = z.getDeclaredConstructor(getClass(), View.class);
                constructor.setAccessible(true);
                return (BaseViewHolder) constructor.newInstance(this, view);
            } else {
                constructor = z.getDeclaredConstructor(View.class);
                constructor.setAccessible(true);
                return (BaseViewHolder) constructor.newInstance(view);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * get generic parameter K
     *
     * @param z
     * @return
     */
    private Class getInstancedGenericKClass(Class z) {
        Type type = z.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            for (Type temp : types) {
                if (temp instanceof Class) {
                    Class tempClass = (Class) temp;
                    if (BaseViewHolder.class.isAssignableFrom(tempClass)) {
                        return tempClass;
                    }
                } else if (temp instanceof ParameterizedType) {
                    Type rawType = ((ParameterizedType) temp).getRawType();
                    if (rawType instanceof Class && BaseViewHolder.class.isAssignableFrom((Class<Object>) rawType)) {
                        return (Class<Object>) rawType;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Return root layout of header
     */

    public LinearLayout getHeaderLayout() {
        return mHeaderLayout;
    }

    /**
     * Return root layout of footer
     */
    public LinearLayout getFooterLayout() {
        return mFooterLayout;
    }

    /**
     * Append header to the rear of the mHeaderLayout.
     *
     * @param header
     */
    public int addHeaderView(View header) {
        return addHeaderView(header, null);
    }

    public int addHeaderView(View header, @Nullable LinearLayout.LayoutParams layoutParams) {
        return addHeaderView(header, -1, layoutParams);
    }

    /**
     * Add header view to mHeaderLayout and set header view position in mHeaderLayout.
     * When index = -1 or index >= child count in mHeaderLayout,
     * the effect of this method is the same as that of {@link #addHeaderView(View)}.
     *
     * @param header
     * @param index  the position in mHeaderLayout of this header.
     *               When index = -1 or index >= child count in mHeaderLayout,
     *               the effect of this method is the same as that of {@link #addHeaderView(View)}.
     */
    public int addHeaderView(View header, int index) {
        return addHeaderView(header, index, LinearLayout.VERTICAL);
    }

    public int addHeaderView(View header, int index, @Nullable LinearLayout.LayoutParams layoutParams) {
        return addHeaderView(header, index, LinearLayout.VERTICAL, layoutParams);
    }

    public int addHeaderView(View header, int index, int orientation) {
        return addHeaderView(header, index, orientation, null);
    }

    /**
     * @param header
     * @param index
     * @param orientation
     */
    public int addHeaderView(View header, int index, int orientation, @Nullable LinearLayout.LayoutParams layoutParams) {
        if (mHeaderLayout == null) {
            mHeaderLayout = new LinearLayout(header.getContext());
            if (orientation == LinearLayout.VERTICAL) {
                mHeaderLayout.setOrientation(LinearLayout.VERTICAL);
                mHeaderLayout.setLayoutParams(new LayoutParams(MATCH_PARENT, WRAP_CONTENT));
            } else {
                mHeaderLayout.setOrientation(LinearLayout.HORIZONTAL);
                mHeaderLayout.setLayoutParams(new LayoutParams(WRAP_CONTENT, MATCH_PARENT));
            }
        }
        final int childCount = mHeaderLayout.getChildCount();
        if (index < 0 || index > childCount) {
            index = childCount;
        }
        if (layoutParams != null) {
            mHeaderLayout.addView(header, index, layoutParams);
        } else {
            mHeaderLayout.addView(header, index);
        }
        if (mHeaderLayout.getChildCount() == 1) {
            int position = getHeaderViewPosition();
            if (position != -1) {
                notifyItemInserted(position);
            }
        }
        return index;
    }

    public int setHeaderView(View header) {
        return setHeaderView(header, 0, LinearLayout.VERTICAL,null);
    }

    public int setHeaderView(View header,@Nullable LinearLayout.LayoutParams layoutParams) {
        return setHeaderView(header, 0, LinearLayout.VERTICAL,layoutParams);
    }

    public int setHeaderView(View header, int index) {
        return setHeaderView(header, index, LinearLayout.VERTICAL,null);
    }

    public int setHeaderView(View header, int index,@Nullable LinearLayout.LayoutParams layoutParams) {
        return setHeaderView(header, index, LinearLayout.VERTICAL,layoutParams);
    }

    public int setHeaderView(View header, int index, int orientation,@Nullable LinearLayout.LayoutParams layoutParams) {
        if (mHeaderLayout == null || mHeaderLayout.getChildCount() <= index) {
            return addHeaderView(header, index, orientation,layoutParams);
        } else {
            mHeaderLayout.removeViewAt(index);
            if (layoutParams == null){
                mHeaderLayout.addView(header, index);
            }else {
                mHeaderLayout.addView(header, index,layoutParams);
            }
            return index;
        }
    }

    /**
     * Append footer to the rear of the mFooterLayout.
     *
     * @param footer
     */
    public int addFooterView(View footer) {
        return addFooterView(footer, -1, LinearLayout.VERTICAL);
    }

    public int addFooterView(View footer, int index) {
        return addFooterView(footer, index, LinearLayout.VERTICAL);
    }

    /**
     * Add footer view to mFooterLayout and set footer view position in mFooterLayout.
     * When index = -1 or index >= child count in mFooterLayout,
     * the effect of this method is the same as that of {@link #addFooterView(View)}.
     *
     * @param footer
     * @param index  the position in mFooterLayout of this footer.
     *               When index = -1 or index >= child count in mFooterLayout,
     *               the effect of this method is the same as that of {@link #addFooterView(View)}.
     */
    public int addFooterView(View footer, int index, int orientation) {
        if (mFooterLayout == null) {
            mFooterLayout = new LinearLayout(footer.getContext());
            if (orientation == LinearLayout.VERTICAL) {
                mFooterLayout.setOrientation(LinearLayout.VERTICAL);
                mFooterLayout.setLayoutParams(new LayoutParams(MATCH_PARENT, WRAP_CONTENT));
            } else {
                mFooterLayout.setOrientation(LinearLayout.HORIZONTAL);
                mFooterLayout.setLayoutParams(new LayoutParams(WRAP_CONTENT, MATCH_PARENT));
            }
        }
        final int childCount = mFooterLayout.getChildCount();
        if (index < 0 || index > childCount) {
            index = childCount;
        }
        mFooterLayout.addView(footer, index);
        if (mFooterLayout.getChildCount() == 1) {
            int position = getFooterViewPosition();
            if (position != -1) {
                notifyItemInserted(position);
            }
        }
        return index;
    }

    public int setFooterView(View header) {
        return setFooterView(header, 0, LinearLayout.VERTICAL);
    }

    public int setFooterView(View header, int index) {
        return setFooterView(header, index, LinearLayout.VERTICAL);
    }

    public int setFooterView(View header, int index, int orientation) {
        if (mFooterLayout == null || mFooterLayout.getChildCount() <= index) {
            return addFooterView(header, index, orientation);
        } else {
            mFooterLayout.removeViewAt(index);
            mFooterLayout.addView(header, index);
            return index;
        }
    }

    /**
     * remove header view from mHeaderLayout.
     * When the child count of mHeaderLayout is 0, mHeaderLayout will be set to null.
     *
     * @param header
     */
    public void removeHeaderView(View header) {
        if (getHeaderLayoutCount() == 0) return;

        mHeaderLayout.removeView(header);
        if (mHeaderLayout.getChildCount() == 0) {
            int position = getHeaderViewPosition();
            if (position != -1) {
                notifyItemRemoved(position);
            }
        }
    }

    /**
     * remove footer view from mFooterLayout,
     * When the child count of mFooterLayout is 0, mFooterLayout will be set to null.
     *
     * @param footer
     */
    public void removeFooterView(View footer) {
        if (getFooterLayoutCount() == 0) return;

        mFooterLayout.removeView(footer);
        if (mFooterLayout.getChildCount() == 0) {
            int position = getFooterViewPosition();
            if (position != -1) {
                notifyItemRemoved(position);
            }
        }
    }

    /**
     * remove all header view from mHeaderLayout and set null to mHeaderLayout
     */
    public void removeAllHeaderView() {
        if (getHeaderLayoutCount() == 0) return;

        mHeaderLayout.removeAllViews();
        int position = getHeaderViewPosition();
        if (position != -1) {
            notifyItemRemoved(position);
        }
    }

    /**
     * remove all footer view from mFooterLayout and set null to mFooterLayout
     */
    public void removeAllFooterView() {
        if (getFooterLayoutCount() == 0) return;

        mFooterLayout.removeAllViews();
        int position = getFooterViewPosition();
        if (position != -1) {
            notifyItemRemoved(position);
        }
    }

    private int getHeaderViewPosition() {
        //Return to header view notify position
        if (getEmptyViewCount() == 1) {
            if (mHeadAndEmptyEnable) {
                return 0;
            }
        } else {
            return 0;
        }
        return -1;
    }

    private int getFooterViewPosition() {
        //Return to footer view notify position
        if (getEmptyViewCount() == 1) {
            int position = 1;
            if (mHeadAndEmptyEnable && getHeaderLayoutCount() != 0) {
                position++;
            }
            if (mFootAndEmptyEnable) {
                return position;
            }
        } else {
            return getHeaderLayoutCount() + mData.size();
        }
        return -1;
    }


    public void setEmptyView(int layoutResId, ViewGroup viewGroup) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutResId, viewGroup, false);
        setEmptyView(view);
    }

    /**
     * bind recyclerView {@link #bindToRecyclerView(RecyclerView)} before use!
     * Recommend you to use {@link #setEmptyView(int layoutResId, ViewGroup viewGroup)}
     *
     * @see #bindToRecyclerView(RecyclerView)
     */
    @Deprecated
    public void setEmptyView(int layoutResId) {
        checkRecyclerViewNotNull();
        setEmptyView(layoutResId, getRecyclerView());
    }

    public void setEmptyView(View emptyView) {
        boolean insert = false;
        if (mEmptyLayout == null) {
            mEmptyLayout = new FrameLayout(emptyView.getContext());
            final LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            final ViewGroup.LayoutParams lp = emptyView.getLayoutParams();
            if (lp != null) {
                layoutParams.width = lp.width;
                layoutParams.height = lp.height;
            }
            mEmptyLayout.setLayoutParams(layoutParams);
            insert = true;
        }
        mEmptyLayout.removeAllViews();
        mEmptyLayout.addView(emptyView);
        mIsUseEmpty = true;
        if (insert) {
            if (getEmptyViewCount() == 1) {
                int position = 0;
                if (mHeadAndEmptyEnable && getHeaderLayoutCount() != 0) {
                    position++;
                }
                notifyItemInserted(position);
            }
        }
    }

    /**
     * Call before {@link RecyclerView#setAdapter(RecyclerView.Adapter)}
     *
     * @param isHeadAndEmpty false will not show headView if the data is empty true will show emptyView and headView
     */
    public void setHeaderAndEmpty(boolean isHeadAndEmpty) {
        setHeaderFooterEmpty(isHeadAndEmpty, false);
    }

    /**
     * set emptyView show if adapter is empty and want to show headview and footview
     * Call before {@link RecyclerView#setAdapter(RecyclerView.Adapter)}
     *
     * @param isHeadAndEmpty
     * @param isFootAndEmpty
     */
    public void setHeaderFooterEmpty(boolean isHeadAndEmpty, boolean isFootAndEmpty) {
        mHeadAndEmptyEnable = isHeadAndEmpty;
        mFootAndEmptyEnable = isFootAndEmpty;
    }

    /**
     * Set whether to use empty view
     *
     * @param isUseEmpty
     */
    public void isUseEmpty(boolean isUseEmpty) {
        mIsUseEmpty = isUseEmpty;
    }

    /**
     * When the current adapter is empty, the BaseMultiAdapter can display a special view
     * called the empty view. The empty view is used to provide feedback to the user
     * that no data is available in this AdapterView.
     *
     * @return The view to show if the adapter is empty.
     */
    public View getEmptyView() {
        return mEmptyLayout;
    }

    private int mPreLoadNumber = 1;

    @Deprecated
    public void setAutoLoadMoreSize(int preLoadNumber) {
        setPreLoadNumber(preLoadNumber);
    }

    public void setPreLoadNumber(int preLoadNumber) {
        if (preLoadNumber > 1) {
            mPreLoadNumber = preLoadNumber;
        }
    }

    private void autoLoadMore(int position) {
        if (getLoadMoreViewCount() == 0) {
            return;
        }
        if (position < getItemCount() - mPreLoadNumber) {
            return;
        }
        if (mLoadMoreView.getLoadMoreStatus() != LoadMoreView.STATUS_DEFAULT) {
            return;
        }
        mLoadMoreView.setLoadMoreStatus(LoadMoreView.STATUS_LOADING);
        if (!mLoading) {
            mLoading = true;
            if (getRecyclerView() != null) {
                getRecyclerView().post(new Runnable() {
                    @Override
                    public void run() {
                        mRequestLoadMoreListener.onLoadMoreRequested();
                    }
                });
            } else {
                mRequestLoadMoreListener.onLoadMoreRequested();
            }
        }
    }


    /**
     * add animation when you want to show time
     *
     * @param holder
     */
    private void addAnimation(RecyclerView.ViewHolder holder) {
        if (mOpenAnimationEnable) {
            if (!mFirstOnlyEnable || holder.getLayoutPosition() > mLastPosition) {
                BaseAnimation animation = null;
                if (mCustomAnimation != null) {
                    animation = mCustomAnimation;
                } else {
                    animation = mSelectAnimation;
                }
                for (Animator anim : animation.getAnimators(holder.itemView)) {
                    startAnim(anim, holder.getLayoutPosition());
                }
                mLastPosition = holder.getLayoutPosition();
            }
        }
    }

    /**
     * set anim to start when loading
     *
     * @param anim
     * @param index
     */
    protected void startAnim(Animator anim, int index) {
        anim.setDuration(mDuration).start();
        anim.setInterpolator(mInterpolator);
    }

    /**
     * @param layoutResId ID for an XML layout resource to load
     * @param parent      Optional view to be the parent of the generated hierarchy or else simply an object that
     *                    provides a set of LayoutParams values for root of the returned
     *                    hierarchy
     * @return view will be return
     */
    protected View getItemView(@LayoutRes int layoutResId, ViewGroup parent) {
        return mLayoutInflater.inflate(layoutResId, parent, false);
    }


    public interface RequestLoadMoreListener {

        void onLoadMoreRequested();

    }


    /**
     * Set the view animation type.
     *
     * @param animationType One of {@link #ALPHAIN}, {@link #SCALEIN}, {@link #SLIDEIN_BOTTOM},
     *                      {@link #SLIDEIN_LEFT}, {@link #SLIDEIN_RIGHT}.
     */
    public void openLoadAnimation(@AnimationType int animationType) {
        this.mOpenAnimationEnable = true;
        mCustomAnimation = null;
        switch (animationType) {
            case ALPHAIN:
                mSelectAnimation = new AlphaInAnimation();
                break;
            case SCALEIN:
                mSelectAnimation = new ScaleInAnimation();
                break;
            case SLIDEIN_BOTTOM:
                mSelectAnimation = new SlideInBottomAnimation();
                break;
            case SLIDEIN_LEFT:
                mSelectAnimation = new SlideInLeftAnimation();
                break;
            case SLIDEIN_RIGHT:
                mSelectAnimation = new SlideInRightAnimation();
                break;
            default:
                break;
        }
    }

    /**
     * Set Custom ObjectAnimator
     *
     * @param animation ObjectAnimator
     */
    public void openLoadAnimation(BaseAnimation animation) {
        this.mOpenAnimationEnable = true;
        this.mCustomAnimation = animation;
    }

    /**
     * To open the animation when loading
     */
    public void openLoadAnimation() {
        this.mOpenAnimationEnable = true;
    }

    /**
     * To close the animation when loading
     */
    public void closeLoadAnimation() {
        this.mOpenAnimationEnable = false;
    }

    /**
     * {@link #addAnimation(RecyclerView.ViewHolder)}
     *
     * @param firstOnly true just show anim when first loading false show anim when load the data every time
     */
    public void isFirstOnly(boolean firstOnly) {
        this.mFirstOnlyEnable = firstOnly;
    }

    /**
     * get the specific view by position,e.g. getViewByPosition(2, R.id.textView)
     * <p>
     * bind recyclerView {@link #bindToRecyclerView(RecyclerView)} before use!
     *
     * @see #bindToRecyclerView(RecyclerView)
     */
    @Nullable
    public View getViewByPosition(int position, @IdRes int viewId) {
        checkRecyclerViewNotNull();
        return getViewByPosition(getRecyclerView(), position, viewId);
    }

    @Nullable
    public View getViewByPosition(RecyclerView recyclerView, int position, @IdRes int viewId) {
        if (recyclerView == null) {
            return null;
        }
        BaseViewHolder viewHolder = (BaseViewHolder) recyclerView.findViewHolderForLayoutPosition(position);
        if (viewHolder == null) {
            return null;
        }
        return viewHolder.getView(viewId);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        Object item = mData.get(position);
        int itemViewType = getItemViewType(position);
        Binder binder = typePool.getItemViewBinder(itemViewType);
        long id = binder.getItemId(item);
        if (id == RecyclerView.NO_ID) {
            return position;
        }
        return id;
    }

    @SuppressWarnings("unchecked")
    private int recursiveExpand(int position, @NonNull List list) {
        int count = list.size();
        int pos = position + list.size() - 1;
        for (int i = list.size() - 1; i >= 0; i--, pos--) {
            if (list.get(i) instanceof IExpandable) {
                IExpandable item = (IExpandable) list.get(i);
                if (item.isExpanded() && hasSubItems(item)) {
                    List subList = item.getSubItems();
                    mData.addAll(pos + 1, subList);
                    int subItemCount = recursiveExpand(pos + 1, subList);
                    count += subItemCount;
                }
            }
        }
        return count;

    }

    /**
     * Expand an expandable item
     *
     * @param position     position of the item
     * @param animate      expand items with animation
     * @param shouldNotify notify the RecyclerView to rebind items, <strong>false</strong> if you want to do it
     *                     yourself.
     * @return the number of items that have been added.
     */
    @SuppressWarnings("unchecked")
    public int expand(@IntRange(from = 0) int position, boolean animate, boolean shouldNotify) {
        position -= getHeaderLayoutCount();

        IExpandable expandable = getExpandableItem(position);
        if (expandable == null) {
            return 0;
        }
        if (!hasSubItems(expandable)) {
            expandable.setExpanded(true);
            notifyItemChanged(position);
            return 0;
        }
        int subItemCount = 0;
        if (!expandable.isExpanded()) {
            List list = expandable.getSubItems();
            mData.addAll(position + 1, list);
            subItemCount += recursiveExpand(position + 1, list);

            expandable.setExpanded(true);
//            subItemCount += list.size();
        }
        int parentPos = position + getHeaderLayoutCount();
        if (shouldNotify) {
            if (animate) {
                notifyItemChanged(parentPos);
                notifyItemRangeInserted(parentPos + 1, subItemCount);
            } else {
                notifyDataSetChanged();
            }
        }
        return subItemCount;
    }

    /**
     * Expand an expandable item
     *
     * @param position position of the item, which includes the header layout count.
     * @param animate  expand items with animation
     * @return the number of items that have been added.
     */
    public int expand(@IntRange(from = 0) int position, boolean animate) {
        return expand(position, animate, true);
    }

    /**
     * Expand an expandable item with animation.
     *
     * @param position position of the item, which includes the header layout count.
     * @return the number of items that have been added.
     */
    public int expand(@IntRange(from = 0) int position) {
        return expand(position, true, true);
    }

    public int expandAll(int position, boolean animate, boolean notify) {
        position -= getHeaderLayoutCount();

        Object endItem = null;
        if (position + 1 < this.mData.size()) {
            endItem = getItem(position + 1);
        }

        IExpandable expandable = getExpandableItem(position);
        if (expandable == null) {
            return 0;
        }

        if (!hasSubItems(expandable)) {
            expandable.setExpanded(true);
            notifyItemChanged(position);
            return 0;
        }

        int count = expand(position + getHeaderLayoutCount(), false, false);
        for (int i = position + 1; i < this.mData.size(); i++) {
            Object item = getItem(i);

            if (item == endItem) {
                break;
            }
            if (isExpandable(item)) {
                count += expand(i + getHeaderLayoutCount(), false, false);
            }
        }

        if (notify) {
            if (animate) {
                notifyItemRangeInserted(position + getHeaderLayoutCount() + 1, count);
            } else {
                notifyDataSetChanged();
            }
        }
        return count;
    }

    /**
     * expand the item and all its subItems
     *
     * @param position position of the item, which includes the header layout count.
     * @param init     whether you are initializing the recyclerView or not.
     *                 if <strong>true</strong>, it won't notify recyclerView to redraw UI.
     * @return the number of items that have been added to the adapter.
     */
    public int expandAll(int position, boolean init) {
        return expandAll(position, true, !init);
    }

    public void expandAll() {

        for (int i = mData.size() - 1 + getHeaderLayoutCount(); i >= getHeaderLayoutCount(); i--) {
            expandAll(i, false, false);
        }
    }

    @SuppressWarnings("unchecked")
    private int recursiveCollapse(@IntRange(from = 0) int position) {
        Object item = getItem(position);
        if (!isExpandable(item)) {
            return 0;
        }
        IExpandable expandable = (IExpandable) item;
        int subItemCount = 0;
        if (expandable.isExpanded()) {
            List<Object> subItems = expandable.getSubItems();
            if (null == subItems) return 0;

            for (int i = subItems.size() - 1; i >= 0; i--) {
                Object subItem = subItems.get(i);
                int pos = getItemPosition(subItem);
                if (pos < 0) {
                    continue;
                }
                if (subItem instanceof IExpandable) {
                    subItemCount += recursiveCollapse(pos);
                }
                mData.remove(pos);
                subItemCount++;
            }
        }
        return subItemCount;
    }

    /**
     * Collapse an expandable item that has been expanded..
     *
     * @param position the position of the item, which includes the header layout count.
     * @param animate  collapse with animation or not.
     * @param notify   notify the recyclerView refresh UI or not.
     * @return the number of subItems collapsed.
     */
    public int collapse(@IntRange(from = 0) int position, boolean animate, boolean notify) {
        position -= getHeaderLayoutCount();

        IExpandable expandable = getExpandableItem(position);
        if (expandable == null) {
            return 0;
        }
        int subItemCount = recursiveCollapse(position);
        expandable.setExpanded(false);
        int parentPos = position + getHeaderLayoutCount();
        if (notify) {
            if (animate) {
                notifyItemChanged(parentPos);
                notifyItemRangeRemoved(parentPos + 1, subItemCount);
            } else {
                notifyDataSetChanged();
            }
        }
        return subItemCount;
    }

    /**
     * Collapse an expandable item that has been expanded..
     *
     * @param position the position of the item, which includes the header layout count.
     * @return the number of subItems collapsed.
     */
    public int collapse(@IntRange(from = 0) int position) {
        return collapse(position, true, true);
    }

    /**
     * Collapse an expandable item that has been expanded..
     *
     * @param position the position of the item, which includes the header layout count.
     * @return the number of subItems collapsed.
     */
    public int collapse(@IntRange(from = 0) int position, boolean animate) {
        return collapse(position, animate, true);
    }

    private int getItemPosition(Object item) {
        return item != null && mData != null && !mData.isEmpty() ? mData.indexOf(item) : -1;
    }

    private boolean hasSubItems(IExpandable item) {
        if (item == null) {
            return false;
        }
        List list = item.getSubItems();
        return list != null && list.size() > 0;
    }

    public boolean isExpandable(Object item) {
        return item != null && item instanceof IExpandable;
    }

    private IExpandable getExpandableItem(int position) {
        Object item = getItem(position);
        if (isExpandable(item)) {
            return (IExpandable) item;
        } else {
            return null;
        }
    }

    /**
     * Get the parent item position of the IExpandable item
     *
     * @return return the closest parent item position of the IExpandable.
     * if the IExpandable item's level is 0, return itself position.
     * if the item's level is negative which mean do not implement this, return a negative
     * if the item is not exist in the data list, return a negative.
     */
    public int getParentPosition(@NonNull Object item) {
        int position = getItemPosition(item);
        if (position == -1) {
            return -1;
        }

        // if the item is IExpandable, return a closest IExpandable item position whose level smaller than this.
        // if it is not, return the closest IExpandable item position whose level is not negative
        int level;
        if (item instanceof IExpandable) {
            level = ((IExpandable) item).getLevel();
        } else {
            level = Integer.MAX_VALUE;
        }
        if (level == 0) {
            return position;
        } else if (level == -1) {
            return -1;
        }

        for (int i = position; i >= 0; i--) {
            Object temp = mData.get(i);
            if (temp instanceof IExpandable) {
                IExpandable expandable = (IExpandable) temp;
                if (expandable.getLevel() >= 0 && expandable.getLevel() < level) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Interface definition for a callback to be invoked when an itemchild in this
     * view has been clicked
     */
    public interface OnItemChildClickListener {
        /**
         * callback method to be invoked when an item in this view has been
         * click and held
         *
         * @param view     The view whihin the ItemView that was clicked
         * @param position The position of the view int the adapter
         */
        void onItemChildClick(BaseMultiAdapter adapter, View view, int position);
    }


    /**
     * Interface definition for a callback to be invoked when an childView in this
     * view has been clicked and held.
     */
    public interface OnItemChildLongClickListener {
        /**
         * callback method to be invoked when an item in this view has been
         * click and held
         *
         * @param view     The childView whihin the itemView that was clicked and held.
         * @param position The position of the view int the adapter
         * @return true if the callback consumed the long click ,false otherwise
         */
        boolean onItemChildLongClick(BaseMultiAdapter adapter, View view, int position);
    }

    /**
     * Interface definition for a callback to be invoked when an item in this
     * view has been clicked and held.
     */
    public interface OnItemLongClickListener {
        /**
         * callback method to be invoked when an item in this view has been
         * click and held
         *
         * @param adapter  the adpater
         * @param view     The view whihin the RecyclerView that was clicked and held.
         * @param position The position of the view int the adapter
         * @return true if the callback consumed the long click ,false otherwise
         */
        boolean onItemLongClick(BaseMultiAdapter adapter, View view, int position);
    }


    /**
     * Interface definition for a callback to be invoked when an item in this
     * RecyclerView itemView has been clicked.
     */
    public interface OnItemClickListener {

        /**
         * Callback method to be invoked when an item in this RecyclerView has
         * been clicked.
         *
         * @param adapter  the adpater
         * @param view     The itemView within the RecyclerView that was clicked (this
         *                 will be a view provided by the adapter)
         * @param position The position of the view in the adapter.
         */
        void onItemClick(BaseMultiAdapter adapter, View view, int position);
    }

    /**
     * Register a callback to be invoked when an item in this RecyclerView has
     * been clicked.
     *
     * @param listener The callback that will be invoked.
     */
    public void setOnItemClickListener(@Nullable OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    /**
     * Register a callback to be invoked when an itemchild in View has
     * been  clicked
     *
     * @param listener The callback that will run
     */
    public void setOnItemChildClickListener(OnItemChildClickListener listener) {
        mOnItemChildClickListener = listener;
    }

    /**
     * Register a callback to be invoked when an item in this RecyclerView has
     * been long clicked and held
     *
     * @param listener The callback that will run
     */
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mOnItemLongClickListener = listener;
    }

    /**
     * Register a callback to be invoked when an itemchild  in this View has
     * been long clicked and held
     *
     * @param listener The callback that will run
     */
    public void setOnItemChildLongClickListener(OnItemChildLongClickListener listener) {
        mOnItemChildLongClickListener = listener;
    }


    /**
     * @return The callback to be invoked with an item in this RecyclerView has
     * been long clicked and held, or null id no callback as been set.
     */
    public final OnItemLongClickListener getOnItemLongClickListener() {
        return mOnItemLongClickListener;
    }

    /**
     * @return The callback to be invoked with an item in this RecyclerView has
     * been clicked and held, or null id no callback as been set.
     */
    public final OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    /**
     * @return The callback to be invoked with an itemchild in this RecyclerView has
     * been clicked, or null id no callback has been set.
     */
    @Nullable
    public final OnItemChildClickListener getOnItemChildClickListener() {
        return mOnItemChildClickListener;
    }

    /**
     * @return The callback to be invoked with an itemChild in this RecyclerView has
     * been long clicked, or null id no callback has been set.
     */
    @Nullable
    public final OnItemChildLongClickListener getOnItemChildLongClickListener() {
        return mOnItemChildLongClickListener;
    }

    private @NonNull
    TypePool typePool;

    /**
     * Registers a type class and its item view binder. If you have registered the class,
     * it will override the original binder(s). Note that the method is non-thread-safe
     * so that you should not use it in concurrent operation.
     * <p>
     * Note that the method should not be called after
     * {@link RecyclerView#setAdapter(RecyclerView.Adapter)}, or you have to call the setAdapter
     * again.
     * </p>
     *
     * @param clazz  the class of a item
     * @param binder the item view binder
     * @param <T>    the item data type
     */
    public <T, VH extends BaseViewHolder> BaseMultiAdapter register(@NonNull Class<? extends T> clazz, @NonNull Binder<T, VH> binder) {
        Preconditions.checkNotNull(clazz);
        Preconditions.checkNotNull(binder);
        checkAndRemoveAllTypesIfNeeded(clazz);
        register(clazz, binder, new DefaultLinker<T>());
        return this;
    }

    public <T, VH extends BaseViewHolder> BaseMultiAdapter register(@NonNull Class<? extends T> clazzs[], @NonNull Binder<T, VH> binder) {
        Preconditions.checkNotNull(clazzs);
        Preconditions.checkNotNull(binder);
        for (Class<? extends T> clazz : clazzs) {
            checkAndRemoveAllTypesIfNeeded(clazz);
            register(clazz, binder, new DefaultLinker<T>());
        }
        return this;
    }


    <T, VH extends BaseViewHolder> BaseMultiAdapter register(@NonNull Class<? extends T> clazz, @NonNull Binder<T, VH> binder, @NonNull Linker<T> linker) {
        typePool.register(clazz, binder, linker);
        binder.adapter = this;
        return this;
    }

    /**
     * Registers a type class to multiple item view binders. If you have registered the
     * class, it will override the original binder(s). Note that the method is non-thread-safe
     * so that you should not use it in concurrent operation.
     * <p>
     * Note that the method should not be called after
     * {@link RecyclerView#setAdapter(RecyclerView.Adapter)}, or you have to call the setAdapter
     * again.
     * </p>
     *
     * @param clazz the class of a item
     * @param <T>   the item data type
     * @return {@link OneToManyFlow} for setting the binders
     * @see #register(Class, Binder)
     */
    @CheckResult
    public @NonNull
    <T> OneToManyFlow<T> register(@NonNull Class<? extends T> clazz) {
        Preconditions.checkNotNull(clazz);
        checkAndRemoveAllTypesIfNeeded(clazz);
        return new OneToManyBuilder<>(this, clazz);
    }


    /**
     * Registers all of the contents in the specified type pool. If you have registered a
     * class, it will override the original binder(s). Note that the method is non-thread-safe
     * so that you should not use it in concurrent operation.
     * <p>
     * Note that the method should not be called after
     * {@link RecyclerView#setAdapter(RecyclerView.Adapter)}, or you have to call the setAdapter
     * again.
     * </p>
     *
     * @param pool type pool containing contents to be added to this adapter inner pool
     * @see #register(Class, Binder)
     * @see #register(Class)
     */
    public void registerAll(@NonNull final TypePool pool) {
        Preconditions.checkNotNull(pool);
        final int size = pool.size();
        for (int i = 0; i < size; i++) {
            registerWithoutChecking(
                    pool.getClass(i),
                    pool.getItemViewBinder(i),
                    pool.getLinker(i)
            );
        }
    }

    /**
     * Set the TypePool to hold the types and view binders.
     *
     * @param typePool the TypePool implementation
     */
    public void setTypePool(@NonNull TypePool typePool) {
        Preconditions.checkNotNull(typePool);
        this.typePool = typePool;
    }


    public @NonNull
    TypePool getTypePool() {
        return typePool;
    }

    protected final int indexInTypesOf(int position, @NonNull Object item) throws BinderNotFoundException {
        int index = typePool.firstIndexOf(item.getClass());
        if (index != -1) {
            @SuppressWarnings("unchecked")
            Linker<Object> linker = (Linker<Object>) typePool.getLinker(index);
            return index + linker.index(position, item);
        }
        throw new BinderNotFoundException(item.getClass());
    }


    private void checkAndRemoveAllTypesIfNeeded(@NonNull Class<?> clazz) {
        if (typePool.unregister(clazz)) {
            Log.w(TAG, "You have registered the " + clazz.getSimpleName() + " type. " +
                    "It will override the original binder(s).");
        }
    }


    /**
     * A safe register method base on the TypePool's safety for TypePool.
     */
    @SuppressWarnings("unchecked")
    private void registerWithoutChecking(@NonNull Class clazz, @NonNull Binder binder, @NonNull Linker linker) {
        checkAndRemoveAllTypesIfNeeded(clazz);
        register(clazz, binder, linker);
    }

}
