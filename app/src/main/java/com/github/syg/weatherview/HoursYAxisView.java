package com.github.syg.weatherview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * Created by lenove on 2017/12/19.
 */

public class HoursYAxisView extends View {
    private HoursView mHoursView;
    private Paint paint;
    private int mViewPadding = Utils.dp2px(getContext(),10);
    public HoursYAxisView(Context context) {
        this(context,null);
    }

    public HoursYAxisView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public HoursYAxisView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(20);
    }

    public void setmHoursView(HoursView mHoursView){
        this.mHoursView = mHoursView;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int computeWidth =resolveSize(Utils.dp2px(getContext(),30),widthMeasureSpec);
        int computeHeight = resolveSize(Utils.dp2px(getContext(),200),heightMeasureSpec);
        setMeasuredDimension(computeWidth,computeHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mHoursView == null){
            return;
        }

        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        canvas.drawText("空气",mViewPadding,HoursView.mAirBottomY-mViewPadding,paint);
        canvas.drawText("风力",mViewPadding,HoursView.mWindBottomY-mViewPadding,paint);
        canvas.drawText(HoursView.mTempMax+"°",mViewPadding,HoursView.mTempMaxY,paint);
        canvas.drawText(HoursView.mTempMin+"°",mViewPadding,HoursView.mTempMinY,paint);
    }

//    private String getYText(int i) {
//        if (mChartView.isBigModeChart()) {
//            switch (i) {
//                case 0:
//                    return "0";
//                case 1:
//                    return "250";
//                case 2:
//                    return "500";
//            }
//        } else {
//            switch (i) {
//                case 0:
//                    return "0";
//                case 1:
//                    return "150";
//                case 2:
//                    return "300";
//            }
//        }
//        return "";
//    }


    private void drawGradeAxis(Canvas canvas) {

    }
}
