package com.jclick.anim;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
public class CoolProgress extends View {
	private Paint mPaint; 
	private static final int DEFAULT_DURATION = 1800;
	private static final String[] COLOR_ARR = {"#1674bc", "#00a396", "#81c540", "#f5b52e", "#ed5b35", "#c22286", "#612e8d"};
	private ColorLine[] mColorLineArr;
	private int mViewSize;
	private Point mCenter = new Point();
	private int minRadius;
	private float strokeWidth;
	private int animateDuration = DEFAULT_DURATION;
	
	private long mStartTime;
	
	private boolean isRoating = false;
	
	private static final float MAX_STRECH_ANGLE = 30;
	private static final int MAX_ABS = 6;
	
	public CoolProgress(Context context) {
        super(context);
        init(null, 0);
    }

    public CoolProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CoolProgress(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    @TargetApi(11)
    private void init(AttributeSet attrs, int defStyle) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
		mPaint.setAntiAlias(true);
		mPaint.setStrokeCap(Paint.Cap.SQUARE);
		if(Build.VERSION.SDK_INT >= 11){
			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int defaultSize = getResources().getDimensionPixelSize(R.dimen.default_circle_view_size);
        int width = getDefaultSize(defaultSize, widthMeasureSpec);
        int height = getDefaultSize(defaultSize, heightMeasureSpec);
        minRadius = width / (COLOR_ARR.length + 3);
        mViewSize = Math.min(width, height);
        setMeasuredDimension(mViewSize, mViewSize);
        mCenter.set(mViewSize / 2, mViewSize / 2);
        initLines();
    }

	private void initLines() {
		mColorLineArr = new ColorLine[COLOR_ARR.length];
		strokeWidth = (mViewSize / 2 - minRadius) / ((COLOR_ARR.length - 1) * 2);
		for(int i = 0; i < COLOR_ARR.length; i ++){
			mColorLineArr[i] = new ColorLine();
			mColorLineArr[i].color = Color.parseColor(COLOR_ARR[i]);
			mColorLineArr[i].initAngle = (i * (((float)360) / (COLOR_ARR.length -1)) + 135) % 360;
			mColorLineArr[i].startAngle = (i * (((float)360) / (COLOR_ARR.length - 1)) + 135) % 360;
			mColorLineArr[i].sweepAngle = 180;
			mColorLineArr[i].innerRadius = minRadius + (strokeWidth) * i * 2 - strokeWidth;
			mColorLineArr[i].outerRadius = minRadius + (strokeWidth) * i * 2;
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		for(ColorLine cl : mColorLineArr){
			drawColorLine(canvas, mColorLineArr[0].speed, cl);
		}
		if(mStartTime == 0){
			mStartTime = System.currentTimeMillis();
		}
		rotateLines();
		if(isRoating){
			postInvalidate();
		}
	}
	
	private void drawColorLine(Canvas canvas, float speed, ColorLine line){
		mPaint.setColor(line.color);
		if(line.speed > speed){
			float k = line.speed / speed;
			if(k > MAX_ABS){
				k = MAX_ABS;
			}
			float angle = MAX_STRECH_ANGLE / MAX_ABS * k;
			drawArcSegment(canvas, mCenter.x, mCenter.y, line.innerRadius, line.outerRadius, line.startAngle, -angle, mPaint, true);
			drawArcSegment(canvas, mCenter.x, mCenter.y, line.innerRadius, line.outerRadius, line.startAngle, line.sweepAngle, mPaint, false);
			drawArcSegment(canvas, mCenter.x, mCenter.y, line.innerRadius, line.outerRadius, line.startAngle + line.sweepAngle, angle, mPaint, true);
		}else{
			drawArcSegment(canvas, mCenter.x, mCenter.y, line.innerRadius, line.outerRadius, line.startAngle, line.sweepAngle, mPaint, false);
		}
	}
	
	private static final float CIRCLE_LIMIT = 359.9999f;
	public void drawArcSegment(Canvas canvas, float cx, float cy, float rInn, float rOut, float startAngle,
	        float sweepAngle, Paint fill, boolean isGradient) {
	    if (sweepAngle > CIRCLE_LIMIT) {
	        sweepAngle = CIRCLE_LIMIT;
	    }
	    if (sweepAngle < -CIRCLE_LIMIT) {
	        sweepAngle = -CIRCLE_LIMIT;
	    }

	    RectF outerRect = new RectF(cx - rOut, cy - rOut, cx + rOut, cy + rOut);
	    RectF innerRect = new RectF(cx - rInn, cy - rInn, cx + rInn, cy + rInn);

	    Path segmentPath = new Path();
	    double start = Math.toRadians(startAngle);
	    segmentPath.moveTo((float)(cx + rInn * Math.cos(start)), (float)(cy + rInn * Math.sin(start)));
	    segmentPath.lineTo((float)(cx + rOut * Math.cos(start)), (float)(cy + rOut * Math.sin(start)));
	    segmentPath.arcTo(outerRect, startAngle, sweepAngle);
	    double end = Math.toRadians(startAngle + sweepAngle);
	    segmentPath.lineTo((float)(cx + rInn * Math.cos(end)), (float)(cy + rInn * Math.sin(end)));
	    segmentPath.arcTo(innerRect, startAngle + sweepAngle, -sweepAngle);
	    float center = (rInn + rOut) / 2f;
	    float startX = (float)(cx + center * Math.cos(start));
	    float startY = (float)(cx + center * Math.sin(start));
	    float endX = (float)(cx + center * Math.cos(end));
	    float endY = (float)(cx + center * Math.sin(end));
	    if(isGradient){
	    	LinearGradient gradient = new LinearGradient(startX, startY, endX, endY, fill.getColor(), Color.WHITE, TileMode.CLAMP);
		    fill.setShader(gradient);
	    }else{
	    	fill.setShader(null);
	    	fill.setMaskFilter(null);
	    }
	    if (fill != null) {
	    	fill.setStyle(Style.FILL);
	        canvas.drawPath(segmentPath, fill);
	    }
	}
	
	private void rotateLines(){
		for(int i = 0; i < mColorLineArr.length; i ++){
			float incre = getIncreAngle(i, mColorLineArr[i]);
			float k = incre - (mColorLineArr[i].startAngle - mColorLineArr[i].initAngle);
			mColorLineArr[i].speed = k;
			mColorLineArr[i].startAngle = mColorLineArr[i].initAngle + incre;
		}
	}
	
	public void startPlay(){
		isRoating = true;
		postInvalidate();
	}
	
	public void stopPlay(){
		isRoating = false;
	}
	
	public void setDuration(int duration){
		if(duration > 1000){
			animateDuration = duration;
		}
	}
	
	public void reset(){
		initLines();
		postInvalidate();
	}
	
	private float getRatio(){
		return (System.currentTimeMillis() - mStartTime) / ((float) animateDuration) % 1;
	}
	
	private float getIncreAngle(int index, ColorLine colorLine){
		float increAngle = 0;
		float interpolator = 0;
		switch (index) {
		case 0:
			increAngle = getRatio() * 360;
			break;
		case 1:
			interpolator = (float)(Math.cos((getRatio() * 2) * Math.PI));
			increAngle = -interpolator * 30;
			break;
		case 2:
			if(getRatio() < 0.2){
				interpolator = (float) (- Math.sin(getRatio() / 0.2 * Math.PI));
				increAngle = interpolator * 30;
			}else{
				interpolator = (float) Math.sin(((getRatio() - 0.2) * Math.PI / 0.8 / 2));
				increAngle = interpolator * 360;
			}
			break;
			
		case 3:
		case 4:
			interpolator = (float)(Math.sin(getRatio() * Math.PI  / 2.0f));
			increAngle = interpolator * 360 * 2;
			break;
		case 5:
			interpolator = (float)(Math.sin(getRatio() * Math.PI  / 2.0f));
			increAngle = interpolator * 360 * 4;
			break;
		case 6:
			interpolator = (float)(Math.sin(getRatio() * Math.PI  / 2.0f));
			increAngle = interpolator * 360 * 5;
			break;
		case 7:
			interpolator = (float)(Math.sin(getRatio() * Math.PI  / 2.0f));
			increAngle = interpolator * 360 * 6;
			break;
		default:
			break;
		}
		increAngle = increAngle % 360;
		return increAngle;
	}
	
	private static class ColorLine{
		float initAngle, startAngle, sweepAngle;
		float speed;
		float innerRadius;
		float outerRadius;
		int color;
	}
}
