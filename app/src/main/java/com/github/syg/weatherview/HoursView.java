package com.github.syg.weatherview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * Created by lenove on 2017/12/18
 */

public class HoursView extends View {
    private static final int HOURS = 24;
    private static final float MAX_AIR_VALUE = 500;
    private int mCubeAirHeight;
    private int mCubeWidth;//一个小方块的宽度
    private int mRoundRadius;//小方块圆角半径
    private int mCubePadding;//小方块的间距
    private int mYAxisWidth = Utils.dp2px(getContext(), 30);//Y轴宽度
    private Path mPathTempLine = new Path();
    private Path mPathWeatherRect = new Path();
    private PathMeasure pathMeasure;

    private int mTextWeatherX;
    private int mTextWeatherY;

    private int mColorWind;
    private int mCubeWindHeight;
    private int mCurrentHour;//当前显示的时间
    private int mViewPadding = Utils.dp2px(getContext(), 10);
    private int mMarginLeft = Utils.dp2px(getContext(), 20);
    private int mMarginRight = Utils.dp2px(getContext(), 40);
    private int mScreenWidth;
    private int mTotalWidth;
    private int mVisibleWidth;
    private int offset = 0;
    private float scale;
    private float mScrollX;
    private Rect mRectHourText = new Rect();
    private Rect mRectAirText = new Rect();

    public static int mWindBottomY;
    public static int mAirBottomY;
    private int mTempBottomY;
    public static int mTempMaxY;
    public static int mTempMinY;
    public static int mTempMax;
    public static int mTempMin;
    private List<HourWeather> mDataList = new ArrayList<>();
    private List<ChartRect> mAirRectList = new ArrayList();
    private List<Integer> mAirList = new ArrayList<>();
    private List<ChartRect> mWindRectList = new ArrayList<>();
    private List<Hour> mHourList = new ArrayList<>();
    private List<Integer> mTempPointY = new ArrayList<>();
    private List<LongChart> mWindLongList = new ArrayList<>();
    private List<LongChart> mWeatherLongList = new ArrayList<>();
    private List<ChartRect> mWeatherRectList = new ArrayList<>();
    private List<Integer> mTempList = new ArrayList<>();
    private List<Integer> mWeatherList = new ArrayList<>();
    private List<Integer> mWindList = new ArrayList<>();
    private Paint mPaintRect = new Paint();
    private Paint mPaintText;
    private Paint mPaintPath;
    private Paint mPaintPathWeatherRect;
    private Paint mPaintTextTemp;
    //View宽高
    private int mHeight;

    public HoursView(Context context) {
        this(context, null);
    }

    public HoursView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public HoursView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        TypedArray ta = getResources().obtainAttributes(attrs, R.styleable.HoursView);
        mCubeWidth = Utils.dp2px(context, ta.getDimension(R.styleable.HoursView_eachCubeWidth, 30));
        mCubeAirHeight = Utils.dp2px(context, ta.getDimension(R.styleable.HoursView_cubeAirHeight, 10));
        mCubePadding = Utils.dp2px(context, ta.getDimension(R.styleable.HoursView_cubePadding, 1));
        mRoundRadius = Utils.dp2px(context, ta.getDimension(R.styleable.HoursView_roundRadius, 4));
        mCubeWindHeight = Utils.dp2px(context, ta.getDimension(R.styleable.HoursView_cubeWindHeight, 20));
        mColorWind = ta.getColor(R.styleable.HoursView_colorWind, Color.GRAY);
        ta.recycle();
        initPaint();
    }


    private void initPaint() {

        mPaintText = new Paint();
        mPaintText.setColor(Color.WHITE);
        mPaintText.setTextSize(20);

        mPaintPath = new Paint();
        mPaintPath.setStrokeWidth(2);
        mPaintPath.setColor(Color.WHITE);
        PathEffect pathEffect = new CornerPathEffect(20);
        mPaintPath.setPathEffect(pathEffect);
        mPaintPath.setStyle(Paint.Style.STROKE);

        mPaintTextTemp = new Paint();
        mPaintTextTemp.setColor(Color.BLACK);
        mPaintTextTemp.setTextSize(30);

        mPaintPathWeatherRect = new Paint();
        mPaintPathWeatherRect.setStyle(Paint.Style.FILL);
        mPaintPathWeatherRect.setColor(Color.WHITE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mTotalWidth = getTotalChartWidth() + mMarginLeft + mMarginRight + mViewPadding;
        int computeWidth = resolveSize(mTotalWidth, widthMeasureSpec);
        int computeHeight = resolveSize(Utils.dp2px(getContext(), 200), heightMeasureSpec);
        setMeasuredDimension(computeWidth, computeHeight);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawHourText(canvas);
        drawWindRectAndText(canvas);
        drawAirRectAndText(canvas);
        drawTempLine(canvas);
        drawWeatherRect(canvas);
        drawWeatherBitmap(canvas);
    }

    private void drawTempLine(Canvas canvas) {
        canvas.drawPath(mPathTempLine, mPaintPath);
    }


    private void drawWeatherRect(Canvas canvas) {


        int temp = mTempList.get(mCurrentHour);
        int weather = mWeatherList.get(mCurrentHour);
        String text;

        switch (weather) {
            case 1:
                text = temp + "°晴";
                break;
            case 2:
                text = temp + "°多云";
                break;
            case 3:
                text = temp + "°小雨";
                break;
            default:
                text = temp + "°大雨";
                break;
        }
        calculateWeatherRectPath(text);
        canvas.drawPath(mPathWeatherRect, mPaintPathWeatherRect);
        canvas.drawText(text, mTextWeatherX, mTextWeatherY, mPaintTextTemp);

        for (ChartRect chartRect : mWeatherRectList) {
            int top = (int) chartRect.rectChart.top;
            int left = (int) chartRect.rectChart.left;
            int bottom = (int) chartRect.rectChart.bottom;
            int right = (int) chartRect.rectChart.right;

            Rect rect = new Rect(left, top, right, bottom);
            canvas.drawRect(rect, mPaintRect);
        }
    }

    private void drawWeatherBitmap(Canvas canvas) {
        Bitmap bitmap;
        int bitmapWidth = 40;
        for (int i = 0; i < mWeatherRectList.size(); i++) {
            ChartRect chartRect = mWeatherRectList.get(i);
            switch (mWeatherLongList.get(i).num) {
                case 1:
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sunny);
                    break;
                case 2:
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cloudy);
                    break;
                case 3:
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.light_rain);
                    break;
                default:
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.heavy_rain);
                    break;
            }
            int mMinVisibleCoord = (int) (mScrollX - mMarginLeft);//最小可见坐标

            int mMaxVisibleCoord = mMinVisibleCoord + mVisibleWidth;//最大可见坐标


            int length = (int) (chartRect.rectChart.right - chartRect.rectChart.left);
            int middle = (int) (chartRect.rectChart.left + (length - bitmapWidth) / 2);
            int left;
            if (mMinVisibleCoord > chartRect.rectChart.left && mMinVisibleCoord < chartRect.rectChart.right - bitmapWidth) {
                left = (int) (mMinVisibleCoord + (chartRect.rectChart.right - mMinVisibleCoord - bitmapWidth) / 2);
            } else if (mMinVisibleCoord >= chartRect.rectChart.right - bitmapWidth) {
                left = (int) (chartRect.rectChart.right - bitmapWidth);
            } else if (mMinVisibleCoord <= chartRect.rectChart.left && (mMaxVisibleCoord) >= chartRect.rectChart.right) {
                left = middle;
            } else if (mMaxVisibleCoord <= chartRect.rectChart.left + bitmapWidth) {
                left = (int) chartRect.rectChart.left;
            } else {
                left = (int) (chartRect.rectChart.left + (mMaxVisibleCoord - chartRect.rectChart.left - bitmapWidth) / 2);
            }

            int right = left + bitmapWidth;
            int top = (int) chartRect.rectChart.top;
            int bottom = (int) chartRect.rectChart.bottom;
            Rect rect = new Rect(left, top, right, bottom);
            canvas.drawBitmap(bitmap, null, rect, mPaintPath);

        }

    }

    private void drawWindRectAndText(Canvas canvas) {

        for (int i = 0; i < mWindLongList.size(); i++) {
            drawChartRect(canvas, i, mWindRectList);

            Rect bound = new Rect();
            String text = mWindLongList.get(i).num + getContext().getString(R.string.wind_level);
            mPaintText.getTextBounds(text, 0, text.length(), bound);
            int textHeight = bound.bottom - bound.top;
            int textWidth = bound.right - bound.left;
            int rectWidth = (int) (mWindRectList.get(i).rectChart.right - mWindRectList.get(i).rectChart.left);
            int x = (int) (mWindRectList.get(i).rectChart.left + (rectWidth - textWidth) / 2);
            int y = (int) ((mWindRectList.get(i).rectChart.bottom - mRoundRadius) - (mCubeWindHeight - mRoundRadius - textHeight) / 2);

            canvas.drawText(text, x, y, mPaintText);

        }


    }

    private void drawHourText(Canvas canvas) {
        for (int i = 0; i < HOURS / 2; i++) {
            canvas.drawText(mHourList.get(i).text, mHourList.get(i).x, mHourList.get(i).y, mPaintText);
        }
        mPaintText.getTextBounds(mHourList.get(0).text, 0, mHourList.get(0).text.length(), mRectHourText);

    }

    private void drawAirRectAndText(Canvas canvas) {
        for (int i = 0; i < HOURS; i++) {
            drawChartRect(canvas, i, mAirRectList);
        }
        drawAirText(canvas, mCurrentHour, offset);

    }

    private void drawAirText(Canvas canvas, int i, int offset) {
        RectF rectF = mAirRectList.get(i).rectChart;
        String text = mAirList.get(i).toString();
        canvas.drawText(text, offset + mViewPadding, rectF.top - 5, mPaintText);
        mPaintText.getTextBounds(text, 0, text.length(), mRectAirText);
    }

    private void drawChartRect(Canvas canvas, int i, List<ChartRect> charList) {
        ChartRect chartRect = charList.get(i);
        canvas.save();
        mPaintRect.setColor(chartRect.color);
        RectF rectF = chartRect.rectChart;
        canvas.clipRect(rectF.left, rectF.top, rectF.right, rectF.bottom - mRoundRadius);

        canvas.drawRoundRect(rectF, mRoundRadius, mRoundRadius, mPaintRect);
        canvas.restore();
    }


    public void onHoursScrollChanged(int x, int y, int oldX, int oldY) {
        if (mDataList.isEmpty()) {
            return;
        }
        scale = getScaleByX(x);
        mScrollX = x;
        offset = (int) (scale * getTotalChartWidth());
        mCurrentHour = offset / (getItemWidthWithSpace()) == 24 ? 23 : offset / (getItemWidthWithSpace());
        invalidate();

    }

    private float getScaleByX(int x) {
        return (float) x / (float) (mTotalWidth - mVisibleWidth);
    }

    private int getItemWidthWithSpace() {
        return mCubeWidth + mCubePadding;
    }

    public int getScreenWidth() {
        if (mScreenWidth != 0) {
            return mScreenWidth;
        }
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        if (wm != null && wm.getDefaultDisplay() != null) {
            DisplayMetrics outMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(outMetrics);
            mScreenWidth = outMetrics.widthPixels;
            return mScreenWidth;
        } else {
            return 720;
        }
    }

    private int getTotalChartWidth() {
        return HOURS * getItemWidthWithSpace();
    }

    public void fillData(List<HourWeather> dataList) {
        mDataList = dataList;
        calculateHourText();
        calculateWindDot();
        calculateAir();
        calculateTemp();
        calculateWeather();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void calculateWeatherRectPath(String text) {
        pathMeasure = new PathMeasure(mPathTempLine, false);
        float mlength = pathMeasure.getLength();
        float[] pos = new float[2];
        float[] tan = new float[2];
        pathMeasure.getPosTan(mlength * scale, pos, tan);
        Rect bound = new Rect();
        mPaintTextTemp.getTextBounds(text, 0, text.length(), bound);
        int textHeight = bound.bottom - bound.top;
        int textWidth = bound.right - bound.left;
        int padding = Utils.dp2px(getContext(), 5);
        int pathHeight = textHeight + padding * 2;
        int left = offset + mViewPadding;
        int bottom = (int) pos[1];
        int top = bottom - pathHeight;
        mPathWeatherRect.setFillType(Path.FillType.WINDING);
        mPathWeatherRect.reset();
        mPathWeatherRect.moveTo(left, bottom);
        mPathWeatherRect.lineTo(left + textWidth, bottom);
        mPathWeatherRect.arcTo(left + textWidth - pathHeight / 2, top, left + textWidth + pathHeight / 2, bottom, 90, -180, true);
        mPathWeatherRect.lineTo(left + pathHeight / 2, top);
        mPathWeatherRect.arcTo(left, top, left + pathHeight, bottom, -90, -90, false);
        mPathWeatherRect.lineTo(left, bottom);
        mTextWeatherX = left + padding;
        mTextWeatherY = bottom - padding;
    }

    private void calculateWeather() {
        mWeatherRectList.clear();
        mWeatherLongList.clear();
        for (int i = 0; i < HOURS; i++) {
            mWeatherList.add(mDataList.get(i).getWeather());
        }

        mWeatherLongList = getLongListViaArray(mWeatherList);
        calculateLongChartPot(mWeatherLongList, mWeatherRectList, mTempBottomY, mCubeWindHeight, Color.GRAY);

    }

    private List<LongChart> getLongListViaArray(List<Integer> list) {
        int count = 1;
        List<LongChart> longChartList = new ArrayList<>();
        longChartList.clear();
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i).equals(list.get(i + 1))) {
                count++;
                continue;
            }

            while (count > 10) {
                LongChart longChart = new LongChart();
                longChart.num = list.get(i);
                longChart.count = 10;
                longChartList.add(longChart);
                count -= 10;
            }
            LongChart longChart = new LongChart();
            longChart.num = list.get(i);
            longChart.count = count;
            longChartList.add(longChart);

            count = 1;
        }

        if (list.get(list.size() - 1) != list.get(list.size() - 2)) {
            LongChart longChart = new LongChart();
            longChart.count = 1;
            longChart.num = list.get(list.size() - 1);
            longChartList.add(longChart);
        }
        return longChartList;


    }

    private void calculateLongChartPot(List<LongChart> srcList, List<ChartRect> dstList, int bottomY, int height, int color) {
        int sum = 0;
        for (int i = 0; i < srcList.size(); i++) {

            sum += srcList.get(i).count;
            ChartRect chartRect = new ChartRect();
            float left = mViewPadding + getItemWidthWithSpace() * (sum - srcList.get(i).count);
            float top = bottomY - height;
            float right = left + getItemWidthWithSpace() * srcList.get(i).count - mCubePadding;
            chartRect.rectChart = new RectF(left, top, right, bottomY);
            chartRect.color = color;
            dstList.add(chartRect);
        }
    }

    private void calculateWindDot() {
        mWindBottomY = mHeight - (mRectHourText.bottom - mRectHourText.top) - mViewPadding * 2;

        mWindRectList.clear();
        mWindLongList.clear();
        mWindList.clear();
        for (int i = 0; i < HOURS; i++) {
            mWindList.add(mDataList.get(i).getWind());
        }
        mWindLongList = getLongListViaArray(mWindList);
        calculateLongChartPot(mWindLongList, mWindRectList, mWindBottomY, mCubeWindHeight, mColorWind);

    }

    private void calculateHourText() {
        mHourList.clear();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -4);
        calendar.set(Calendar.MINUTE, 0);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");

        for (int i = 0; i < HOURS / 2; i++) {
            Hour hour = new Hour();
            calendar.add(Calendar.HOUR, 2);
            hour.text = format.format(calendar.getTime());
            hour.x = mViewPadding + i * 2 * getItemWidthWithSpace();
            hour.y = mHeight - mViewPadding;

            mHourList.add(hour);
        }
    }

    private void calculateAir() {
        mAirBottomY = mWindBottomY - mCubeWindHeight - mViewPadding + mRoundRadius;
        mAirRectList.clear();
        mAirList.clear();
        for (int i = 0; i < HOURS; i++) {
            HourWeather hourWeather = mDataList.get(i);
            ChartRect chartRect = new ChartRect();
            int air = hourWeather.getAir();
            float left = i * mCubePadding + i * mCubeWidth + mViewPadding;
            float bottom = mAirBottomY;
            float top = bottom - (1 + air / MAX_AIR_VALUE) * mCubeAirHeight - mRoundRadius;
            float right = left + mCubeWidth;

            chartRect.rectChart = new RectF(left, top, right, bottom);
            if (air <= 50) {
                chartRect.color = Color.parseColor("#6bcd07");
            } else if (air <= 100) {
                chartRect.color = Color.parseColor("#fbd029");
            } else if (air <= 150) {
                chartRect.color = Color.parseColor("#fe8800");
            } else if (air <= 200) {
                chartRect.color = Color.parseColor("#fe0000");
            } else if (air <= 300) {
                chartRect.color = Color.parseColor("#970454");
            } else {
                chartRect.color = Color.parseColor("#62001e");
            }
            mAirRectList.add(chartRect);
            mAirList.add(air);
        }
    }

    private void calculateTemp() {
        mTempBottomY = mAirBottomY + mRoundRadius - 5 - mCubeAirHeight * 2 - (getTextHeight(mPaintText)) - mViewPadding;//考虑最大污染度
        mTempMinY = mTempBottomY - mViewPadding * 3;
        mTempMaxY = mTempMinY - mViewPadding * 5;
        mTempList.clear();
        for (int i = 0; i < HOURS; i++) {
            mTempList.add(mDataList.get(i).getTemp());
        }
        mTempMax = Collections.max(mTempList);

        mTempMin = Collections.min(mTempList);

        for (int i = 0; i < HOURS; i++) {
            mTempPointY.add(mTempMinY - (mTempMinY - mTempMaxY) * (mTempList.get(i) - mTempMin) / (mTempMax - mTempMin));
        }
        mPathTempLine.moveTo(mViewPadding, mTempPointY.get(0));
        for (int i = 0; i < HOURS; i++) {
            mPathTempLine.lineTo(mViewPadding + getItemWidthWithSpace() * (i + 1), mTempPointY.get(i));
        }
    }

    private int getTextHeight(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (int) Math.ceil(fm.descent - fm.top) + 2;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = h;
        mVisibleWidth = getScreenWidth() - mYAxisWidth;//可以看见的距离

    }


    private static class ChartRect {
        /**
         * 柱状图矩区域
         */
        RectF rectChart;
        /**
         * 柱子颜色
         */
        @ColorInt
        int color;
    }

    private static class LongChart {
        int num;
        int count;
    }

    private static class Hour {
        String text;
        int x;
        int y;
    }
}
