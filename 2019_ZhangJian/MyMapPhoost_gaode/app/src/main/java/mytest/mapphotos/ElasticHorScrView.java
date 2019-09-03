package mytest.mapphotos;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.HorizontalScrollView;

public class ElasticHorScrView extends HorizontalScrollView {
    private View inner;
    private Rect normal = new Rect();
    private float x;

    public ElasticHorScrView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ElasticHorScrView(Context context) {
        super(context);
    }

    @Override
    protected void onFinishInflate() {
        if (getChildCount() > 0) {
            inner = getChildAt(0);
        }
        super.onFinishInflate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev == null) {
            return super.onTouchEvent(ev);
        } else {
            commOnTouchEvent(ev);
        }
        return super.onTouchEvent(ev);
    }
//处理点击，移动事件
    private void commOnTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
        case MotionEvent.ACTION_DOWN:
            x = ev.getX();
            break;
        case MotionEvent.ACTION_UP:
            if (isNeedAnimation()) {
                animation();
            }
            break;
        case MotionEvent.ACTION_MOVE:
            final float preX = x;
            float nowX = ev.getX();
            int distanceX = (int) (preX - nowX);
            //滑动
            scrollBy(distanceX, 0);
            x = nowX;
            // 当滚动到最上或最下时就不会再滚动，这是移动布局
            if (isNeedMove()) {
                if (normal.isEmpty()) {
                    // 保存正常的布局位置
                    normal.set(inner.getLeft(), inner.getTop(), inner.getRight(), inner.getBottom());
                }
                //移动布局
                inner.layout(inner.getLeft() - distanceX, inner.getTop(), inner.getRight() - distanceX, inner.getBottom());
            }

            break;

        default:
            break;
        }
    }

    private void animation() {
        // 开启移动动画  
        TranslateAnimation mTranslateAnimation = new TranslateAnimation(inner.getLeft(), 0, normal.left, 0);
        mTranslateAnimation.setDuration(50);
        inner.setAnimation(mTranslateAnimation);
        //设置回到正常的布局位置
        inner.layout(normal.left, normal.top, normal.right, normal.bottom);
        normal.setEmpty();

    }

    /**
     * @return 是否要开启动画
     */
    private boolean isNeedAnimation() {
        return !normal.isEmpty();
    }

    /**
     * @return 是否需要移动布局
     */
    private boolean isNeedMove() {
        int offset = inner.getMeasuredWidth() - getWidth();
        int scrollX = getScrollX();
        if (scrollX == 0 || offset == scrollX)
            return true;
        return false;
    }
}