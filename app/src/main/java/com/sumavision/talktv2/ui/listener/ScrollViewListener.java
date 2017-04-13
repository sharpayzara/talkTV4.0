package com.sumavision.talktv2.ui.listener;

import com.sumavision.talktv2.ui.widget.ObservableScrollView;

/**
 * Created by sharpay on 16-7-17.
 */
public interface ScrollViewListener {
    void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy);
}
