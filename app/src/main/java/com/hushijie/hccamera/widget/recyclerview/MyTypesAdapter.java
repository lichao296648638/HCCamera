package com.hushijie.hccamera.widget.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 多布局的适配器
 * Created by zkywalker on 2016/12/25.
 * package:org.zky.zky.recyclerview
 */

public abstract class MyTypesAdapter<T> extends RecyclerView.Adapter<ViewHolder> {
    protected Context mContext;

    protected List<T> mDatas;

    protected LayoutInflater mInflater;

    public ViewGroup mRv;

    private OnItemClickListener mOnItemClickListener;

    public MyTypesAdapter setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
        return this;
    }

    public OnItemClickListener getmOnItemClickListener() {
        return this.mOnItemClickListener;
    }

    public MyTypesAdapter(Context context, List<T> datas) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mDatas = datas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder = ViewHolder.get(this.mContext, (View) null, parent, getLayoutRes(mDatas.get(viewType)));
        if (null == this.mRv) {
            this.mRv = parent;
        }

        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
            return position;
    }

    protected int getPosition(RecyclerView.ViewHolder viewHolder) {
        return viewHolder.getAdapterPosition();
    }

    protected boolean isEnabled(int viewType) {
        return true;
    }

    @Deprecated
    protected void setListener(final ViewGroup parent, final ViewHolder viewHolder, int viewType) {
        if (this.isEnabled(viewType)) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (MyTypesAdapter.this.mOnItemClickListener != null) {
                        int position = MyTypesAdapter.this.getPosition(viewHolder);
                        MyTypesAdapter.this.mOnItemClickListener.onItemClick(parent, v, MyTypesAdapter.this.mDatas.get(position), position);
                    }

                }
            });
            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View v) {
                    if (MyTypesAdapter.this.mOnItemClickListener != null) {
                        int position = MyTypesAdapter.this.getPosition(viewHolder);
                        return MyTypesAdapter.this.mOnItemClickListener.onItemLongClick(parent, v, MyTypesAdapter.this.mDatas.get(position), position);
                    } else {
                        return false;
                    }
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        this.setListener(position, holder);
        this.convert(holder, this.mDatas.get(position));
    }

    protected void setListener(final int position, final ViewHolder viewHolder) {
        if (this.isEnabled(this.getItemViewType(position))) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (MyTypesAdapter.this.mOnItemClickListener != null) {
                        MyTypesAdapter.this.mOnItemClickListener.onItemClick(MyTypesAdapter.this.mRv, v, MyTypesAdapter.this.mDatas.get(position), position);
                    }

                }
            });
            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View v) {
                    if (MyTypesAdapter.this.mOnItemClickListener != null) {
                        int position = MyTypesAdapter.this.getPosition(viewHolder);
                        return MyTypesAdapter.this.mOnItemClickListener.onItemLongClick(MyTypesAdapter.this.mRv, v, MyTypesAdapter.this.mDatas.get(position), position);
                    } else {
                        return false;
                    }
                }
            });
        }
    }

    public abstract int getLayoutRes(T var1);

    public abstract void convert(ViewHolder var1, T var2);

    public int getItemCount() {
        return this.mDatas != null ? this.mDatas.size() : 0;
    }

    public void setDatas(List<T> list) {
        if (this.mDatas != null) {
            if (null != list) {
                ArrayList temp = new ArrayList();
                temp.addAll(list);
                this.mDatas.clear();
                this.mDatas.addAll(temp);
            } else {
                this.mDatas.clear();
            }
        } else {
            this.mDatas = list;
        }

        this.notifyDataSetChanged();
    }

    public void remove(int i) {
        if (null != this.mDatas && this.mDatas.size() > i && i > -1) {
            this.mDatas.remove(i);
            this.notifyDataSetChanged();
        }
    }

    public void addDatas(List<T> list) {
        if (null != list) {
            ArrayList temp = new ArrayList();
            temp.addAll(list);
            if (this.mDatas != null) {
                this.mDatas.addAll(temp);
            } else {
                this.mDatas = temp;
            }

            this.notifyDataSetChanged();
        }

    }

    public List<T> getDatas() {
        return this.mDatas;
    }

    public T getItem(int position) {
        return position > -1 && null != this.mDatas && this.mDatas.size() > position ? this.mDatas.get(position) : null;
    }


    public interface OnItemClickListener<T> {
        void onItemClick(ViewGroup var1, View var2, T var3, int var4);

        boolean onItemLongClick(ViewGroup var1, View var2, T var3, int var4);
    }
}
