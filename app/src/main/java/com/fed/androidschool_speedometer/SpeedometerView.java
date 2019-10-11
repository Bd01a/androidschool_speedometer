package com.fed.androidschool_speedometer;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.TextViewCompat;

import java.nio.MappedByteBuffer;

public class SpeedometerView extends View {


    private static final int TEXT_SIZE_NUMBER_PAINT = 55;
    private static final int TEXT_SIZE_MARKING_PAINT = 60;
    private static final int STROKE_WIDTH_LINE_PAINT = 16;
    private static final float ARROW_RADIUS = 180;
    private static final float OVAl_RADIUS = 60;
    private static final float MARKING_NUMBER_RADIUS = 250;
    private static final int LINE_COLOR = Color.BLACK;
    private static final int MARKING_COLOR = Color.BLACK;
    private static final int BACKGROUND_COLOR = Color.WHITE;

    private static final int VIEW_X = 400;
    private static final int VIEW_Y = 400;


    private int mSpeed;
    private int mMaxSpeed;
    private int mLowSpeedColor;
    private int mMiddleSpeedColor;
    private int mHighSpeedColor;
    private int mArrowColor;

    private Paint mNumberPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mMarkingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Path mArrowPath = new Path();

    private RectF mNumberRectf = new RectF(-OVAl_RADIUS,-OVAl_RADIUS,OVAl_RADIUS,OVAl_RADIUS);

//    private TextView mTextView;
//    private Rect mTextBounds = new Rect();


    public SpeedometerView(Context context) {
        super(context);
        init(context, null);
    }

    public SpeedometerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attr) {
        extractsAttr(context, attr);

        mNumberPaint.setTextSize(TEXT_SIZE_NUMBER_PAINT);

        mLinePaint.setColor(LINE_COLOR);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(STROKE_WIDTH_LINE_PAINT);

        mBackgroundPaint.setColor(BACKGROUND_COLOR);
        mBackgroundPaint.setStyle(Paint.Style.FILL);

        mMarkingPaint.setColor(MARKING_COLOR);
        mMarkingPaint.setTextSize(TEXT_SIZE_MARKING_PAINT);

//        mTextView = new TextView(context);
//        TextViewCompat.setTextAppearance(mTextView, R.style.TextAppearance_AppCompat_Display1);


    }

    private void changeColorNumberPaint() {
        if(mSpeed>mMaxSpeed/3*2) {
            mNumberPaint.setColor(mHighSpeedColor);
        }
        else if(mSpeed<mMaxSpeed/3.0*2 && mSpeed>mMaxSpeed/3){
            mNumberPaint.setColor(mMiddleSpeedColor);
        }
        else {
            mNumberPaint.setColor(mLowSpeedColor);
        }
    }

    private void extractsAttr(@NonNull Context context, @Nullable AttributeSet attr) {
        final Resources.Theme theme= context.getTheme();
        final TypedArray typedArray = theme.obtainStyledAttributes(attr, R.styleable.SpeedometerView,
                R.attr.speedometerView,0);
        try {
            mSpeed = typedArray.getInteger(R.styleable.SpeedometerView_speed,getResources().getInteger(R.integer.default_speed_speedometer_view));
            mMaxSpeed = typedArray.getInteger(R.styleable.SpeedometerView_max_speed, 0);
            mLowSpeedColor = typedArray.getColor(R.styleable.SpeedometerView_low_speed_color, 0);
            mMiddleSpeedColor = typedArray.getColor(R.styleable.SpeedometerView_middle_speed_color, 0);
            mHighSpeedColor = typedArray.getColor(R.styleable.SpeedometerView_high_speed_color, 0);
            mArrowColor = typedArray.getColor(R.styleable.SpeedometerView_arrow_color, 0);
        }
        finally {
            typedArray.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        changeColorNumberPaint();
        canvas.translate(VIEW_X,VIEW_Y);
        drawArrow(canvas);
        drawSpeedNumber(canvas);
        drawMarking(canvas);
    }

    private void drawMarking(Canvas canvas) {
        for (float speed = 0; speed < mMaxSpeed; speed += mMaxSpeed / 8f) {
            String speedMarking = String.valueOf((int)speed);
            float speedTextWidth = mMarkingPaint.measureText(speedMarking);

            double markingAngle = (2 * Math.PI) / mMaxSpeed * speed + Math.PI / 2;
            float speedX =  - speedTextWidth/2 + MARKING_NUMBER_RADIUS * (float) Math.cos(markingAngle);
            float speedY =  - (mNumberPaint.ascent()+mNumberPaint.descent())/2 + MARKING_NUMBER_RADIUS * (float) Math.sin(markingAngle);
            canvas.drawText(speedMarking, speedX, speedY, mMarkingPaint);
        }
    }

    private void drawArrow(Canvas canvas) {
        double arrowAngle = (2 * Math.PI) / mMaxSpeed * mSpeed + Math.PI / 2;
        float arrowX = ARROW_RADIUS * (float) Math.cos(arrowAngle);
        float arrowY = ARROW_RADIUS * (float) Math.sin(arrowAngle);

        float line1X = (float) Math.cos(arrowAngle - Math.PI / 2) * OVAl_RADIUS;
        float line1Y = (float) Math.sin(arrowAngle - Math.PI / 2) * OVAl_RADIUS;

        float line2X = (float) Math.cos(arrowAngle + Math.PI / 2) * OVAl_RADIUS;
        float line2Y = (float) Math.sin(arrowAngle + Math.PI / 2) * OVAl_RADIUS;

        mArrowPath.moveTo(line1X, line1Y);
        mArrowPath.lineTo(arrowX, arrowY);
        mArrowPath.lineTo(line2X,line2Y);

        canvas.drawPath(mArrowPath, mLinePaint);
        canvas.drawOval(mNumberRectf, mBackgroundPaint);
        canvas.drawOval(mNumberRectf, mLinePaint);

    }

    private void drawSpeedNumber(Canvas canvas) {
        final String speedNumber = String.valueOf(mSpeed);
//        mTextView.getPaint().getTextBounds(speedNumber, 0, speedNumber.length(), mTextBounds);
//        float x = mNumberRectf.width() / 2f - mTextBounds.width() / 2f - mTextBounds.left;
//        float y = mNumberRectf.height() / 2f + mTextBounds.height() / 2f - mTextBounds.bottom;

        final float textWidth = mNumberPaint.measureText(speedNumber);
        float textX = -textWidth / 2;
        float textY = -(mNumberPaint.ascent() + mNumberPaint.descent()) / 2;
        canvas.drawText(speedNumber, textX, textY, mNumberPaint);
    }

    public int getSpeed() {
        return mSpeed;
    }

    public void setSpeed(int mSpeed) {
        this.mSpeed = mSpeed;
    }

    public int getMaxSpeed() {
        return mMaxSpeed;
    }

    public void setMaxSpeed(int mMaxSpeed) {
        this.mMaxSpeed = mMaxSpeed;
    }

    public int getLowSpeedColor() {
        return mLowSpeedColor;
    }

    public void setLowSpeedColor(int mLowSpeedColor) {
        this.mLowSpeedColor = mLowSpeedColor;
    }

    public int getMiddleSpeedColor() {
        return mMiddleSpeedColor;
    }

    public void setMiddleSpeedColor(int mMiddleSpeedColor) {
        this.mMiddleSpeedColor = mMiddleSpeedColor;
    }

    public int getHighSpeedColor() {
        return mHighSpeedColor;
    }

    public void setHighSpeedColor(int mHighSpeedColor) {
        this.mHighSpeedColor = mHighSpeedColor;
    }

    public int getArrowColor() {
        return mArrowColor;
    }

    public void setArrowColor(int mArrowColor) {
        this.mArrowColor = mArrowColor;
    }
}
