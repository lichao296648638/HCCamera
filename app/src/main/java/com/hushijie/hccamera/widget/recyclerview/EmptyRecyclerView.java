package com.hushijie.hccamera.widget.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.hushijie.hccamera.widget.refreshlayout.RefreshLayout;


/**
 * 可控制空视图的rv
 * Created by zhangkun on 2017/8/21.
 */

public class EmptyRecyclerView extends RecyclerView {

    private View emptyView;

    private RefreshLayout refreshLayout;

    private OnLoadDataListener loadDataListener;

    private boolean isAlmostBottom = false;

    private int search_page = 1;

    //默认的最大页面
    private int MAX_PAGE = 10;


    private RefreshLayout.OnRefreshListener refreshListener = new RefreshLayout.OnRefreshListener() {
        @Override
        public void onHeaderRefresh() {
            if (loadDataListener != null) {
                loadDataListener.set();
                search_page = 1;
            }
        }

        @Override
        public void onFooterRefresh() {
            if (loadDataListener != null) {
                search_page++;
                loadDataListener.add(search_page);
            }
        }
    };

    final private AdapterDataObserver observer = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            checkIfEmpty();
        }
    };

    public EmptyRecyclerView(Context context) {
        this(context, null);
    }

    public EmptyRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptyRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
//        addOnScrollListener(new OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                //当前RecyclerView显示出来的最后一个的item的position
//                int lastPosition = -1;
//
//                //当前状态为停止滑动状态SCROLL_STATE_IDLE时
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
//
//                    if (layoutManager instanceof GridLayoutManager) {
//                        //通过LayoutManager找到当前显示的最后的item的position
//                        lastPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
//                    } else if (layoutManager instanceof LinearLayoutManager) {
//                        lastPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
//                    } else if (layoutManager instanceof StaggeredGridLayoutManager) {
//                        //因为StaggeredGridLayoutManager的特殊性可能导致最后显示的item存在多个，所以这里取到的是一个数组
//                        //得到这个数组后再取到数组中position值最大的那个就是最后显示的position值了
//                        int[] lastPositions = new int[((StaggeredGridLayoutManager) layoutManager).setSpanCount()];
//                        ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(lastPositions);
//                        lastPosition = findMax(lastPositions);
//                    }
//
//                    //时判断界面显示的最后item的position是否等于itemCount总数-1也就是最后一个item的position
//                    //如果相等则说明已经滑动到最后了
//                    isAlmostBottom = lastPosition == recyclerView.getLayoutManager().getItemCount() - 1;
//                }
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
////                boolean canScroll = recyclerView.getLayoutManager().getChildCount() != recyclerView.getLayoutManager().getItemCount();
////                if (isAlmostBottom && refreshLayout != null && !refreshLayout.isRefreshing() && refreshLayout.isAlmostBottom() && search_page < MAX_PAGE) {
////
////                    refreshLayout.setFooterRefreshing(true);
////                    refreshListener.onFooterRefresh();
////                }
//            }
//        });
    }

    @Override
    public void setAdapter(Adapter adapter) {
        final Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(observer);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }

        checkIfEmpty();
    }

    public void setRefresh(RefreshLayout refreshLayout,OnLoadDataListener listener){
        this.refreshLayout = refreshLayout;
        this.loadDataListener = listener;
        refreshLayout.setOnRefreshListener(refreshListener);

    }

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }

    public RefreshLayout getRefreshLayout() {
        return refreshLayout;
    }

    private void checkIfEmpty() {
        if (emptyView != null && getAdapter() != null) {
            int itemCount = getAdapter().getItemCount();
            boolean emptyViewVisible = itemCount == 0;
            emptyView.setVisibility(emptyViewVisible ? VISIBLE : GONE);
            setVisibility(emptyViewVisible ? GONE : VISIBLE);
        }
    }

    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    public void setSearchPage(int search_page) {
        this.search_page = search_page;
    }

    public interface OnLoadDataListener {
        void set();

        void add(int pager);
    }
}