package me.codego.slideview;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by mengxn on 16-9-9.
 */
public class SlideView extends FrameLayout {

    private static final String TAG = "SlideView";

    private ViewGroup mContentView;
    private ViewGroup mHolderView;
    private int mHolderWidth;
    private int mContentWidth;
    private GestureDetectorCompat mGestureDetector;

    private int mLastX = 0;
    private int mLastY = 0;
    private static final int TAN = 2;
    private float mParallax;

    private static final float AUTO_EXPAND_PERCENT = 0.5f;
    private static final int DEFAULT_ANIM_DURATION = 300;
    private static final int FLING_MIN_DISTANCE = 500;

    public SlideView(Context context) {
        this(context, null);
        View.inflate(context, R.layout.layout_slide_item, this);
        mContentView = findViewById(R.id.content);
        mHolderView = findViewById(R.id.holder);
    }

    public SlideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mParallax = 0.0f;
        mGestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (Math.abs(velocityX) > FLING_MIN_DISTANCE) {
                    handleHolder(velocityX < 0);
                    return true;
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                float scrollX = (int) mContentView.getTranslationX();
                if (distanceX != 0) {
                    float newScrollX = scrollX - distanceX;
                    if (newScrollX > 0) {
                        newScrollX = 0;
                    } else if (newScrollX < -mHolderWidth) {
                        newScrollX = -mHolderWidth;
                    }
                    translationX(newScrollX, mParallax);
                }
                return true;
            }
        });
    }

    public void setContentView(View view) {
        mContentView.removeAllViews();
        mContentView.addView(view);
    }

    public void setParallax(float parallax) {
        if (parallax < 0 || parallax > 1) {
            throw new IllegalArgumentException("parallax is between 0 and 1");
        }
        mParallax = parallax;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 2) {
            throw new IllegalArgumentException("SlideView should have two child view");
        }
        mHolderView = (ViewGroup) getChildAt(0);
        mContentView = (ViewGroup) getChildAt(1);
        mContentView.setFocusable(true);
        mContentView.setClickable(true);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mHolderView.setTranslationX(mContentView.getMeasuredWidth());
        mContentView.setTranslationX(0);
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mHolderWidth = mHolderView.getMeasuredWidth();
                mContentWidth = mContentView.getMeasuredWidth();

                mLastX = x;
                mLastY = y;

                shrinkOther();
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = x - mLastX;
                int deltaY = y - mLastY;
                // 如果用户在做水平滑动，拦截事件
                if (Math.abs(deltaX) > Math.abs(deltaY) * TAN) {
                    abortAnimation();
                    getParent().requestDisallowInterceptTouchEvent(true);
                    return true;
                }
                break;
            default:
                break;
        }
        return super.onInterceptTouchEvent(event);
    }

    private void shrinkOther() {
        if (getParent() instanceof ViewGroup) {
            final ViewGroup viewGroup = (ViewGroup) getParent();
            final int count = viewGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                final View childView = viewGroup.getChildAt(i);
                if (childView == this) {
                    continue;
                }
                if (childView instanceof SlideView) {
                    ((SlideView) childView).shrink();
                }
            }
        }
    }

    public void shrink() {
        if (mContentView.getTranslationX() != 0) {
            smoothScrollTo(0);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                handleHolder(mContentView.getTranslationX() < -mHolderWidth * AUTO_EXPAND_PERCENT);
                break;
            }
            default:
                break;
        }
        mLastX = x;
        mLastY = y;

        return mGestureDetector.onTouchEvent(event);
    }

    /**
     * 处理 Holder View
     *
     * @param expand 开 or 关
     */
    private void handleHolder(boolean expand) {
        getParent().requestDisallowInterceptTouchEvent(false);
        if (expand) {
            smoothScrollTo(-mHolderWidth);
        } else {
            smoothScrollTo(0);
        }
    }

    private void translationX(float translationX, float parallax) {
        mContentView.setTranslationX(translationX);
        if (parallax > 0) {
            translationX = -mHolderWidth * parallax + translationX * (1 - parallax);
        }
        mHolderView.setTranslationX(mContentWidth + translationX);
    }

    private void smoothScrollTo(int destX) {
        // 缓慢滚动到指定位置
        mHolderView.animate().translationX(mContentWidth + destX).setDuration(DEFAULT_ANIM_DURATION).start();
        mContentView.animate().translationX(destX).setDuration(DEFAULT_ANIM_DURATION).start();
    }

    private void abortAnimation() {
        mHolderView.animate().cancel();
        mContentView.animate().cancel();
    }

    public void clearOptions() {
        mHolderView.removeAllViews();
    }

    public Option newOption(String text, OnClickListener listener) {
        final Option option = new Option();
        option.setText(text);
        option.setOnClickListener(listener);
        return option;
    }

    public Option newOption(String text, int background, OnClickListener listener) {
        final Option option = new Option();
        option.setText(text);
        option.setBackground(background);
        option.setOnClickListener(listener);
        return option;
    }

    public void addOption(Option option) {
        mHolderView.addView(createView(option));
    }

    private View createView(Option option) {
        final TextView textView = new TextView(getContext());
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        int padding = (int) toPx(option.padding);
        textView.setPadding(padding, 0, padding, 0);
        textView.setText(option.text);
        textView.setTextSize(option.textSize);
        textView.setTextColor(option.textColor);
        textView.setBackgroundColor(option.background);
        textView.setOnClickListener(option.listener);
        textView.setGravity(Gravity.CENTER);
        textView.setMinWidth((int) toPx(option.minWidth));
        return textView;
    }

    private float toPx(int value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }

    public static class Option {

        private String text;
        private float textSize;
        private int textColor;
        private int background;
        private OnClickListener listener;
        private int minWidth;
        private int padding;

        private Option() {
            text = "";
            textSize = 16f;
            textColor = Color.WHITE;
            background = Color.BLACK;
            minWidth = 90;
            padding = 20;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public float getTextSize() {
            return textSize;
        }

        public void setTextSize(float textSize) {
            this.textSize = textSize;
        }

        public int getTextColor() {
            return textColor;
        }

        public void setTextColor(int textColor) {
            this.textColor = textColor;
        }

        public int getBackground() {
            return background;
        }

        public void setBackground(int background) {
            this.background = background;
        }

        public OnClickListener getOnClickListener() {
            return listener;
        }

        public void setOnClickListener(OnClickListener listener) {
            this.listener = listener;
        }
    }

}
