package me.codego.example;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by mengxn on 16-9-9.
 */
public class SlideView extends FrameLayout {

    private ViewGroup mContentView;
    private ViewGroup mHolderView;
    private int mHolderWidth = 120;
    private int mContentWidth;

    private int mLastX = 0;
    private int mLastY = 0;
    private static final int TAN = 2;
    private float mParallax;
    private int AnimDuration = 300;

    private OnSlideListener onSlideListener;

    public interface OnSlideListener {        // SlideView的三种状态：开始滑动，打开，关闭
        int SLIDE_STATUS_OFF = 0;
        int SLIDE_STATUS_START_SCROLL = 1;
        int SLIDE_STATUS_ON = 2;

        /**
         * @param view current SlideView
         * @param status SLIDE_STATUS_ON, SLIDE_STATUS_OFF or SLIDE_STATUS_START_SCROLL
         */
        void onSlide(View view, int status);
    }


    public SlideView(Context context) {
        this(context, null);
        View.inflate(context, R.layout.layout_slide_item, this);
        mContentView = (FrameLayout) findViewById(R.id.content);
        mHolderView = (ViewGroup) findViewById(R.id.holder);
    }

    public SlideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mParallax = 0.3f;
    }

    public void setContentView(View view) {
        mContentView.addView(view);
    }

    public void setParallax(float parallax) {
        if (parallax < 0 || parallax > 1) {
            throw new IllegalArgumentException("parallax is bettwon 0 and 1");
        }
        mParallax = parallax;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View contentView = findViewById(R.id.content);
        if (contentView == null || !(contentView instanceof ViewGroup)) {
            throw new IllegalArgumentException("please check id #content");
        }
        mContentView = (ViewGroup) contentView;

        View holderView = findViewById(R.id.holder);
        if (holderView == null || !(holderView instanceof ViewGroup)) {
            throw new IllegalArgumentException("please check id #holder");
        }
        mHolderView = (ViewGroup) holderView;
    }

    public void setOnSlideListener(OnSlideListener onSlideListener) {
        this.onSlideListener = onSlideListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        float scrollX = (int) mContentView.getTranslationX();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                abortAnimation();
                mHolderWidth = mHolderView.getMeasuredWidth();
                mContentWidth = mContentView.getMeasuredWidth();
                if (onSlideListener != null) {
                    onSlideListener.onSlide(this, OnSlideListener.SLIDE_STATUS_START_SCROLL);
                }
                return true;
            case MotionEvent.ACTION_MOVE: {
                int deltaX = x - mLastX;
                int deltaY = y - mLastY;
                if (Math.abs(deltaX) < Math.abs(deltaY) * TAN) {
                    break;
                }
                getParent().requestDisallowInterceptTouchEvent(true);
                float newScrollX = scrollX + deltaX;
                if (deltaX != 0) {
                    if (newScrollX > 0) {
                        newScrollX = 0;
                    } else if (newScrollX < -mHolderWidth) {
                        newScrollX = -mHolderWidth;
                    }
                    translationX(newScrollX, mParallax);
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                getParent().requestDisallowInterceptTouchEvent(false);
                int newScrollX = 0;
                if (scrollX < -mHolderWidth * 0.75) {
                    newScrollX = -mHolderWidth;
                }
                this.smoothScrollTo(newScrollX, 0);

                if (onSlideListener != null) {
                    onSlideListener.onSlide(this, newScrollX == 0 ? OnSlideListener.SLIDE_STATUS_OFF : OnSlideListener.SLIDE_STATUS_ON);
                }
                break;
            }
        }
        mLastX = x;
        mLastY = y;

        return super.onTouchEvent(event);
    }

    private void translationX(float translationX, float parallax) {
        float newTranslationX = translationX;
        if (parallax > 0) {
            newTranslationX = -mHolderWidth * parallax + translationX * (1-parallax);
        }
        mHolderView.setTranslationX(mContentWidth + newTranslationX);
        mContentView.setTranslationX(translationX);
    }

    public void shrink() {
        if (mContentView.getTranslationX() != 0) {
            this.smoothScrollTo(0, 0);
        }
    }

    private void smoothScrollTo(int destX, int destY) {
        // 缓慢滚动到指定位置
        mHolderView.animate().translationX(mContentWidth + destX).setDuration(AnimDuration).start();
        mContentView.animate().translationX(destX).setDuration(AnimDuration).start();
    }

    private void abortAnimation() {
        mHolderView.animate().cancel();
        mContentView.animate().cancel();
    }

}
