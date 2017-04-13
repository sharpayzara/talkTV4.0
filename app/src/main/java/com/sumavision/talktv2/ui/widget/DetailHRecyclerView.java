package com.sumavision.talktv2.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by zjx on 2016/7/4.
 */
public class DetailHRecyclerView extends RecyclerView {
    private boolean isScrollingToBottom = true;
    private boolean isScrollingToTop = true;
    private LoadMoreListener listener;

    public DetailHRecyclerView(Context context) {
        super(context);
    }

    public DetailHRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public DetailHRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setLoadMoreListener(LoadMoreListener loadMoreListener){
        this.listener = loadMoreListener;
    }


    @Override
    public void onScrolled(int dx, int dy) {
        isScrollingToBottom = dx > 0;
        isScrollingToTop = dx <= 0;
    }

    @Override
    public void onScrollStateChanged(int state) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) getLayoutManager();
        if (state == RecyclerView.SCROLL_STATE_IDLE || state == RecyclerView.SCROLL_STATE_DRAGGING) {
            int lastVisibleItem = layoutManager.findLastCompletelyVisibleItemPosition();
            int firstVisiableItem = layoutManager.findFirstCompletelyVisibleItemPosition();
            int totalItemCount = layoutManager.getItemCount();
            if (lastVisibleItem == (totalItemCount - 1) && isScrollingToBottom) {
                if (listener != null)
                    listener.loadMore();
            }
            else if (firstVisiableItem == 0 && isScrollingToTop) {
                if (listener != null)
                    listener.loadLast();
            }
        }
    }

    public interface LoadMoreListener {
        void loadMore();
        void loadLast();
    }
}
