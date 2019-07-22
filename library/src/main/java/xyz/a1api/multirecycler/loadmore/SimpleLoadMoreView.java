package xyz.a1api.multirecycler.loadmore;


import xyz.a1api.multirecycler.R;

/**
 * Created by BlingBling on 2016/10/11.
 */

public final class SimpleLoadMoreView extends LoadMoreView {

    @Override
    public int getLayoutId() {
        return R.layout.brvah_quick_view_load_more;
    }

    @Override
    protected int getLoadingViewId() {
        return R.id.load_more_loading_view;
    }

    @Override
    protected int getLoadFailViewId() {
        return R.id.load_more_load_fail_view;
    }

    @Override
    protected int getLoadEndViewId() {
        return R.id.load_more_load_end_view;
    }

    public int getLoadingTextViewId() {
        return R.id.loading_text;
    }

    public int getLoadFailedTextViewId() {
        return R.id.load_failed_text;
    }

    public int getLoadEndTextViewId() {
        return R.id.load_end_text;
    }
}
