package com.hushijie.hccamera.widget.recyclerview;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import java.util.List;


/**
 * callback
 * Created by zhangkun on 2016/12/26.
 */

public class MyCallback extends ItemTouchHelper.SimpleCallback {
    private static final String TAG = "MyCallback";

    //view偏移距离
    private int mTranslationY;
    //view缩小量
    private float mScale;
    //显示的view数量
    private int mMaxShowCount;


    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private List mData;

    public MyCallback(RecyclerView rv, RecyclerView.Adapter adapter, List datas, int trY, float scale, int max) {
        this(0, ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT,
                rv, adapter, datas, trY, scale, max);
    }

    public MyCallback(int dragDirs, int swipeDirs
            , RecyclerView rv, RecyclerView.Adapter adapter, List datas, int trY, float scale, int max) {
        super(dragDirs, swipeDirs);
        mRecyclerView = rv;
        mAdapter = adapter;
        mData = datas;
        mTranslationY = trY;
        mScale =scale;
        mMaxShowCount = max;
    }


    //水平方向是否可以被回收掉的阈值
    public float getThreshold(RecyclerView.ViewHolder viewHolder) {
        return mRecyclerView.getWidth() * getSwipeThreshold(viewHolder);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        //这里处理swipe之后的操作，即删除第一条数据把它加到list的最后，目的是循环展示数据
        Object remove = mData.remove(viewHolder.getLayoutPosition());
        mData.add(remove);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        //先根据滑动的dx dy算出拖动的位移
        double swipValue = Math.sqrt(dX * dX + dY * dY);
        //根据阈值和拖动的位移 算出现在动画的比例系数fraction
        double fraction = swipValue / getThreshold(viewHolder);
        //边界修正 最大为1
        if (fraction > 1) {
            fraction = 1;
        }
        //对每个ChildView进行缩放 位移
        int childCount = recyclerView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = recyclerView.getChildAt(i);

            //算出当前view是 除去顶层的以外视图的层数
            int level = childCount-i-1;
//            if (level==2){
//                child.findViewById(R.id.tv_test).setBackgroundColor(Color.GREEN);
//            }
            if (level > 0) {
                child.setScaleX((float) (1 - mScale * level + fraction * mScale));

                if (level < mMaxShowCount ) {
                    child.setScaleY((float) (1 - mScale * level + fraction * mScale));
                    child.setTranslationY((float) (mTranslationY * level - fraction * mTranslationY));
                }
            }

        }
    }
}
