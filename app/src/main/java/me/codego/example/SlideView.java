package me.codego.example;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Scroller;

import static me.codego.example.R.id.holder;

/**
 * Created by mengxn on 16-9-9.
 */
public class SlideView extends LinearLayout {

    private FrameLayout mViewContent;
    private ViewGroup mContentView;
    private ViewGroup mHolderView;
    private int mHolderWidth = 120;

    private int mLastX = 0;
    private int mLastY = 0;
    private static final int TAN = 2;

    private Scroller mScroller;
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
    }

    public SlideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(getContext());
        setOrientation(HORIZONTAL);
        /*View.inflate(context, R.layout.layout_slide_item, this);
        mViewContent = (FrameLayout) findViewById(R.id.content);*/

       /* mHolderWidth = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, mHolderWidth, getResources()
                        .getDisplayMetrics()));*/

    }

    public void setContentView(View view) {
        mViewContent.addView(view);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View contentView = findViewById(R.id.content);
        if (contentView == null || !(contentView instanceof ViewGroup)) {
            throw new IllegalArgumentException("please check id #content");
        }
        mContentView = (ViewGroup) contentView;

        View holderView = findViewById(holder);
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
        int scrollX = getScrollX();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }

                if (onSlideListener != null) {
                    onSlideListener.onSlide(this, OnSlideListener.SLIDE_STATUS_START_SCROLL);
                }
                mHolderWidth = mHolderView.getMeasuredWidth();
                return true;
            case MotionEvent.ACTION_MOVE: {
                int deltaX = x - mLastX;
                int deltaY = y - mLastY;
                if (Math.abs(deltaX) < Math.abs(deltaY) * TAN) {
                    break;
                }
                getParent().requestDisallowInterceptTouchEvent(true);
                int newScrollX = scrollX - deltaX;
                if (deltaX != 0) {
                    if (newScrollX < 0) {
                        newScrollX = 0;
                    } else if (newScrollX > mHolderWidth) {
                        newScrollX = mHolderWidth;
                    }
                    this.scrollTo(newScrollX, 0);
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                getParent().requestDisallowInterceptTouchEvent(false);
                int newScrollX = 0;
                if (scrollX - mHolderWidth * 0.75 > 0) {
                    newScrollX = mHolderWidth;
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

    public void shrink() {
        if (getScrollX() != 0) {
            this.smoothScrollTo(0, 0);
        }
    }

    private void smoothScrollTo(int destX, int destY) {
        // 缓慢滚动到指定位置
        int scrollX = getScrollX();
        int delta = destX - scrollX;
        mScroller.startScroll(scrollX, 0, delta, 0, Math.abs(delta) * 3);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }
}
