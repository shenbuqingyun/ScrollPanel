package com.chinstyle.scrollpanel;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;

/**
 * 作者    Chin_style
 * 时间    2018/12/1
 * 文件    ScrollPanel
 * 描述    ①MotionEvent.ACTION_MASK 在Android中是应用于多点触摸操作 加上之后就可以处理多点触控的操作；
 *        ②利用Scroll类实现自定义view滑动的标准写法 scrollTo(0, (int) (getScrollY() + offset)); 后者是发动机（首次滑动） 前者是存储动力源（持续存储滑动值）
 *        ③关闭动画mOpenAnimator = ValueAnimator.ofInt(getScrollY(), scrollTo); 打开动画mOpenAnimator = ValueAnimator.ofInt(getScrollY(), scrollTo);
 *        都是利用getScrollY()获取滑动偏移量，第二个参数要么是起始值0，要么是固定值- 按照一定比例取view的Height；
 *        ④核心思想：move时滑动在move中定义好；滑动结束时，操作在up事件中定义好，利用动画实现对应的逻辑。
 *        ⑤动画使用的插值器为 OvershootInterpolator
 *        ⑥onTouchEvent中move事件，使用Scroll类来实现滑动效果，亦可以使用Scroller类进行优化 实现有过渡的滑动效果。
 *        ⑦setDrawingCacheEnabled(true)用以优化，提高绘图速度，防止卡顿 获取cache时会占有一定的内存在不用的时候要设为false。
 * 致谢    Thank you for your advice.
 */
public class ScrollPanel extends FrameLayout {
    private static final int SCROLL_TOP = 0;
    private static final float DEFAULT_SLIDE_DISTANCE = 200;
    public float ratio = (float) 0.382;
    private static int DEFAULT_CLOSE_DURATION = 250;
    private static int DEFAULT_OPEN_DURATION = 300;

    private boolean isOpened = false;
    private boolean isDragging = false;
    private float mLastEventY = 0;
    private float mFirstEventY = 0;

    private ValueAnimator mOpenAnimator;
    private ObjectAnimator mFadeOutAnimator;
    private AnimatorSet mAnimatorOpenSet;

    private ValueAnimator mCloseAnimator;
    private AnimatorSet mAnimatorCloseSet;
    private ObjectAnimator mFadeInAnimator;
    private SimplePanelListener mSimplePanelListener;

    private int height;
    public ScrollPanel(Context context) {
        super(context);
    }

    public ScrollPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollPanel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float eventY = event.getY();

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_MOVE:
                if (isOpened && (getHeight() - getScrollY()) < eventY) {
                    return true;
                }

                if (isDragging) {
                    float offset = 0;
                    if (mLastEventY != 0) offset = mLastEventY - eventY;
                    setDrawingCacheEnabled(true);
                    scrollTo(0, (int) (getScrollY() + offset)); //offset值较小 用于首次启动滑动；getScrollY()值较大 存储了offset和值
                    mLastEventY = eventY;
                } else {
                    mLastEventY = 0;
                }

                setAlpha(0.85f);
                isDragging = true;
                break;

            case MotionEvent.ACTION_DOWN:
                if (isOpened && (getHeight() - getScrollY()) < eventY) {
                    return false;
                } else {
                    mFirstEventY = eventY;
                }
                break;


            case MotionEvent.ACTION_UP:
                if (!isDragging) return false;

                if (mFirstEventY > eventY && Math.abs(mFirstEventY - eventY) > DEFAULT_SLIDE_DISTANCE) { //判断手指上滑，同时滑动大于一个域值
                    open(); //触发条件 移动到指定位置 - 动画结束时要停在指定区域 动画接收参数为手离开屏幕是的纵坐标 第二个长参数是个负值 即为ViewPager的高度
                } else {
                    close(); //滑动不符合要求 回到原始区域 - 动画结束时返回原始状态 动画接收参数为手离开屏幕是的纵坐标 第二个长参数是0
                }

                clearDragging();
                break;
        }

        return true;
    }

    private void clearDragging() {
        mLastEventY = 0;
        mFirstEventY = 0;
        isDragging = false;
        setDrawingCacheEnabled(false);
    }

    public void open() {
        setAnimationCacheEnabled(true);
        setAnimatorOpenSet().start();
    }

    public void close() {
        setAnimationCacheEnabled(true);
        setAnimatorCloseSet().start();
    }

    private AnimatorSet setAnimatorOpenSet() {
        height = getResources().getDisplayMetrics().heightPixels ;
        int scrollTo = (int) (height * ratio);
        mOpenAnimator = ValueAnimator.ofInt(getScrollY(), scrollTo);

        mOpenAnimator.setInterpolator(new OvershootInterpolator());
        mOpenAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int height = (Integer) valueAnimator.getAnimatedValue();
                scrollTo(0, height);
            }
        });
        //在自定义View中使用ObjectAnimator属性动画时，第一个参数传入this即可
        mFadeOutAnimator = ObjectAnimator.ofFloat(this, "alpha", getAlpha(), 0.85f);

        mAnimatorOpenSet = new AnimatorSet();
        mAnimatorOpenSet.setDuration(DEFAULT_OPEN_DURATION);
        mAnimatorOpenSet.play(mOpenAnimator).with(mFadeOutAnimator);
        mAnimatorOpenSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (mSimplePanelListener != null) {
                    mSimplePanelListener.onOpened();
                }
                isOpened = true;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        return mAnimatorOpenSet;
    }

    private AnimatorSet setAnimatorCloseSet() {
        mCloseAnimator = ValueAnimator.ofInt(getScrollY(), SCROLL_TOP);
        mCloseAnimator.setInterpolator(new OvershootInterpolator());
        mCloseAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int height = (Integer) valueAnimator.getAnimatedValue();
                scrollTo(0, height);
            }
        });

        mFadeInAnimator = ObjectAnimator.ofFloat(this, "alpha", getAlpha(), 1f);

        mAnimatorCloseSet = new AnimatorSet();
        mAnimatorCloseSet.setDuration(DEFAULT_CLOSE_DURATION);
        mAnimatorCloseSet.play(mCloseAnimator).with(mFadeInAnimator);
        mAnimatorCloseSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (mSimplePanelListener != null) {
                    mSimplePanelListener.onClosed();
                }
                isOpened = false;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        return mAnimatorCloseSet;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void setSlideRatio(float r) {
        this.ratio = r;
    }

    public float getSlideRatio() {
        return ratio;
    }

    public void addSimplePanelListener(SimplePanelListener listener) {
        mSimplePanelListener = listener;
    }

    public interface SimplePanelListener {
        abstract public void onOpened();

        abstract public void onClosed();
    }
}

