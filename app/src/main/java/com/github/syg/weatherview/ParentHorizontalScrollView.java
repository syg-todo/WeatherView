package com.github.syg.weatherview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

/**
 * Created by lenove on 2017/12/18.
 */

public class ParentHorizontalScrollView extends HorizontalScrollView{
    private OnScrollListener mListener;

    public ParentHorizontalScrollView(Context context) {
        super(context);
    }

    public ParentHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ParentHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
    }

    public void setOnScrollListener(OnScrollListener listener) {
        mListener = listener;
    }

    /**
     * @param l    Current horizontal scroll origin.
     * @param t    Current vertical scroll origin.
     * @param oldl Previous horizontal scroll origin.
     * @param oldt Previous vertical scroll origin.
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mListener != null) {
            mListener.onScrollChanged(l, t, oldl, oldt);
        }
    }

    public interface OnScrollListener {
        void onScrollChanged(int x, int y, int oldX, int oldY);
    }
}
