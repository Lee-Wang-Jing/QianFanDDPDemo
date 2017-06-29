package com.qianfan.qianfanddpdemo.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qianfan.qianfanddpdemo.MyApplication;
import com.qianfan.qianfanddpdemo.R;
import com.qianfan.qianfanddpdemo.entity.FooterEntity;


/**
 * BaseAdapter
 *
 * @author WangJing on 2016/11/28 0028 14:04
 * @e-mail wangjinggm@gmail.com
 * @see [相关类/方法](可选)
 */

public abstract class BaseAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final int VIEW_TYPE_LOAD_MORE_FOOTER = 100;
//    protected boolean isLoadMoreFooterShown;

    private int footer_state;

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_LOAD_MORE_FOOTER) {
            return onCreateLoadMoreFooterViewHolder(parent);
        }
        return onCreateNormalViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
//        if (isLoadMoreFooterShown && position == getItemCount() - 1) {
        if (position == getItemCount() - 1) {
            if (holder.itemView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
                params.setFullSpan(true);
            }
        }
        holder.onBindViewHolder(position);
    }

    @Override
    public int getItemCount() {
//        return getDataCount() + (isLoadMoreFooterShown ? 1 : 0);
        return getDataCount() + 1;
    }

    @Override
    public int getItemViewType(int position) {
//        if (isLoadMoreFooterShown && position == getItemCount() - 1) {
        if (position == getItemCount() - 1) {//position从0开始的所以要-1
            return VIEW_TYPE_LOAD_MORE_FOOTER;
        }
        return getDataViewType(position);
    }

    protected abstract int getDataCount();

    protected abstract BaseViewHolder onCreateNormalViewHolder(ViewGroup parent, int viewType);


    private BaseViewHolder onCreateLoadMoreFooterViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.widget_pull_to_refresh_footer, parent, false);
        return new LoadMoreFooterViewHolder(view);
    }

    protected int getDataViewType(int position) {
        return 0;
    }

    public void onLoadMoreStateChanged(boolean isShown) {
//        this.isLoadMoreFooterShown = isShown;
//        if (isShown) {
//            notifyItemInserted(getItemCount());
//        } else {
//            notifyItemRemoved(getItemCount());
//        }
    }

    public boolean isLoadMoreFooter(int position) {
//        return isLoadMoreFooterShown && position == getItemCount() - 1;
        return position == getItemCount() - 1;
    }

    public boolean isSectionHeader(int position) {
        return false;
    }

    private class LoadMoreFooterViewHolder extends BaseViewHolder {

        TextView tv_footer;
        ProgressBar progressBar;

        public LoadMoreFooterViewHolder(View view) {
            super(view);
            tv_footer = (TextView) view.findViewById(R.id.tv_footer);
            progressBar= (ProgressBar) view.findViewById(R.id.progressBar);
        }

        @Override
        public void onBindViewHolder(int position) {
            progressBar.setVisibility(View.GONE);
            switch (footer_state) {
                case 0:
                    tv_footer.setText("" + MyApplication.getmContext().getString(R.string.footer_loadmore));
                    break;
                case 1:
                    progressBar.setVisibility(View.VISIBLE);
                    tv_footer.setText("" + MyApplication.getmContext().getString(R.string.footer_loading));
                    break;
                case 2:
                    tv_footer.setText("" + MyApplication.getmContext().getString(R.string.footer_nomore));
                    break;
                case 3:
                    tv_footer.setText("" + MyApplication.getmContext().getString(R.string.footer_again));
                    break;
                case 4:
                    tv_footer.setText("" );
                    break;
            }
        }

        @Override
        public void onItemClick(View view, int position) {
            switch (footer_state) {
                case 0://点击查看更多
//                    ToastUtil.TShort(view.getContext(), "" + MyApplication.getmContext().getString(R.string.footer_loadmore));
                    //TODO 此处需要添加点击查看更多逻辑
                    if (mOnClickItemListener!=null){
                        mOnClickItemListener.OnClick(0);
                    }
                    break;
                case 1://正在努力加载
//                    ToastUtil.TShort(view.getContext(), "" + MyApplication.getmContext().getString(R.string.footer_loading));
                    break;
                case 2://已显示全部
//                    ToastUtil.TShort(view.getContext(), "" + MyApplication.getmContext().getString(R.string.footer_nomore));
                    break;
                case 3://加载失败，点击重新加载
//                    ToastUtil.TShort(view.getContext(), "" + MyApplication.getmContext().getString(R.string.footer_again));
                    //TODO 此处需要添加重新请求逻辑
                    if (mOnClickItemListener!=null){
                        mOnClickItemListener.OnClick(3);
                    }
                    break;
                case 4:
                    break;
            }
        }
    }

    public void notifyFooterState(FooterEntity footerEntity) {
        this.footer_state = footerEntity.getType();
        notifyItemChanged(getItemCount() - 1);
    }

    public interface OnClickItemListener{
        void OnClick(int index);
    }

    private OnClickItemListener mOnClickItemListener;
    public void setOnClickItemListener(OnClickItemListener mOnClickItemListener){
        this.mOnClickItemListener = mOnClickItemListener;
    }
}
