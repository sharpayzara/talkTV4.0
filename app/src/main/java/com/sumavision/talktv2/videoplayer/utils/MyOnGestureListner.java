package com.sumavision.talktv2.videoplayer.utils;

import android.view.GestureDetector;
import android.view.MotionEvent;

import com.sumavision.talktv2.common.ShareElement;
import com.sumavision.talktv2.videoplayer.view.BaseVideoView;

/**
 * Created by Administrator on 2016/6/12.
 */
public class MyOnGestureListner implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener  {

    private BaseVideoView videoView;
    private boolean firstScroll = false;// 每次触摸屏幕后，第一次scroll的标志
    private int GESTURE_FLAG = 0;// 1,调节进度，2，调节音量
    private static final int GESTURE_MODIFY_PROGRESS = 1;
    private static final int GESTURE_MODIFY_VOLUME_BRIGHRT = 2;
    private boolean isLocked = false;
    private boolean isHalfScreen = true;

    public MyOnGestureListner(BaseVideoView videoView) {
        this.videoView = videoView;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        firstScroll = true;
        if(onSingleTapUpListener != null) {
            onSingleTapUpListener.down(e);
        }
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
//        if(onSingleTapUpListener != null)
//            onSingleTapUpListener.onSingleUp();
        return false;
    }

    /**
     * 处理亮度，声音调节等
     */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if(isHalfScreen || isLocked)
            return false;
        float mOldX = e1.getX(), mOldY = e1.getY();
        int y = (int) e2.getRawY();
        int x = (int) e2.getRawX();

        if (firstScroll) {
            if (Math.abs(distanceX) >= Math.abs(distanceY)) {
                GESTURE_FLAG = GESTURE_MODIFY_PROGRESS;
            } else {
                GESTURE_FLAG = GESTURE_MODIFY_VOLUME_BRIGHRT;
            }
        }
        if (GESTURE_FLAG == GESTURE_MODIFY_VOLUME_BRIGHRT ) {
            if (Math.abs(distanceY) > Math.abs(distanceX)) {
                if (mOldX > videoView.videoWidth() * 1 / 2)// 右边滑动
                    videoView.onVolumeSlide((mOldY - y) / videoView.videoHeight());
                else if (mOldX < videoView.videoWidth() / 2)// 左边滑动
                    videoView.onBrightnessSlide((mOldY - y) / videoView.videoHeight());
            }
        }
        else if(GESTURE_FLAG == GESTURE_MODIFY_PROGRESS) {
            if(onSingleTapUpListener != null) {
                onSingleTapUpListener.scroll(e1,e2,distanceX,distanceY);
            }
        }
        firstScroll = false;
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if(onSingleTapUpListener != null)
            onSingleTapUpListener.onSingleUp();
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        if(onSingleTapUpListener != null)
            onSingleTapUpListener.onDoubleTap();
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    public interface OnSingleTapUpListener {
       void onSingleUp();
        void down(MotionEvent e);
        void scroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY);
        void onDoubleTap();
    }
    private OnSingleTapUpListener onSingleTapUpListener;
    public void setOnSingleTapUpListener(OnSingleTapUpListener onSingleTapUpListener) {
        this.onSingleTapUpListener = onSingleTapUpListener;
    }

    public void isLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    public void isHalfScreen(int screenState) {
        if(screenState == ShareElement.PORTRAIT)
            isHalfScreen = true;
        else
            isHalfScreen = false;
    }
}
