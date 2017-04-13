package com.sumavision.talktv2.ui.widget;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sharpay on 16-6-24.
 */
public class WidthDropAnimator {

    public void animateOpen(final View view,int width) {
        ValueAnimator animator = createDropAnimator(
                view,
                0,
                width);
        animator.start();
    }

    public void animateClose(final View view) {
        int origWidth = view.getWidth();
        ValueAnimator animator = createDropAnimator(view, origWidth, 0);
        animator.start();
    }

    public ValueAnimator createDropAnimator(final View view, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        int value = (Integer) valueAnimator.getAnimatedValue();
                        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                        layoutParams.width = value;
                        view.setLayoutParams(layoutParams);
                    }
                });
        return animator;
    }
}
