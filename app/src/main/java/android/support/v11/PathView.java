package android.support.v11;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.iot.app.R;

public abstract class PathView extends View {
    private static final int NONE = 0;
    private static final int RESTART = 1;
    private static final int REVERSE = 2;
    int mRepeatStyle = NONE;
    Paint mDrawPaint;
    Paint mPaint;
    Path mDst = new Path();
    Path mPaintPath = new Path();
    float mAnimatorValue = 0;
    float mStop = 0;
    boolean showPainter = true;
    boolean showPainterActually = false;
    float[] mCurPos = new float[2];
    int mDuration = 6000;
    PathMeasure mPathMeasure = new PathMeasure();
    int mPathStrokeWidth = 5;
    int mPaintStrokeWidth = 3;
    int mTextStrokeColor = Color.BLACK;
    int mPaintStrokeColor = Color.BLACK;
    private ValueAnimator mAnimator;
    private boolean mShouldFill = false;

    private PathAnimatorListener mAnimatorListener;

    public PathView(Context context) {
        super(context);
    }


    public PathView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
    }

    public PathView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
    }

    void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PathView);
        mDuration = typedArray.getInteger(R.styleable.PathView_duration, mDuration);
        showPainter = typedArray.getBoolean(R.styleable.PathView_showPainter, showPainter);
        showPainterActually = typedArray.getBoolean(R.styleable.PathView_showPainterActually, showPainterActually);
        mPathStrokeWidth = typedArray.getDimensionPixelOffset(R.styleable.PathView_pathStrokeWidth, mPathStrokeWidth);
        mTextStrokeColor = typedArray.getColor(R.styleable.PathView_pathStrokeColor, mTextStrokeColor);
        mPaintStrokeWidth = typedArray.getDimensionPixelOffset(R.styleable.PathView_paintStrokeWidth, mPaintStrokeWidth);
        mPaintStrokeColor = typedArray.getColor(R.styleable.PathView_paintStrokeColor, mPaintStrokeColor);
        mRepeatStyle = typedArray.getInt(R.styleable.PathView_repeat, mRepeatStyle);
        typedArray.recycle();
    }

    void initPaint() {

        mDrawPaint = new Paint();
        mDrawPaint.setAntiAlias(true);
        mDrawPaint.setColor(mTextStrokeColor);
        mDrawPaint.setStrokeWidth(mPathStrokeWidth);
        mDrawPaint.setStyle(Paint.Style.STROKE);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mPaintStrokeColor);
        mPaint.setStrokeWidth(mPaintStrokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    private void initAnimator(float start, float end, int animationStyle, int repeatCount) {
        mAnimator = ValueAnimator.ofFloat(start, end);

        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mAnimatorValue = (float) valueAnimator.getAnimatedValue();
                drawPath(mAnimatorValue);
            }
        });
        if (mAnimatorListener == null) {
            mAnimatorListener = new PathAnimatorListener();
            mAnimatorListener.setTarget(this);
        }
        mAnimator.removeAllListeners();
        mAnimator.addListener(mAnimatorListener);

        mAnimator.setDuration(mDuration);
        mAnimator.setInterpolator(new LinearInterpolator());
        if (animationStyle == RESTART) {
            mAnimator.setRepeatMode(ValueAnimator.RESTART);
            mAnimator.setRepeatCount(repeatCount);
        } else if (animationStyle == REVERSE) {
            mAnimator.setRepeatMode(ValueAnimator.REVERSE);
            mAnimator.setRepeatCount(repeatCount);
        }
    }

    public void startAnimation(float start, float end) {
        startAnimation(start, end, mRepeatStyle, ValueAnimator.INFINITE);
    }

    void startAnimation(float start, float end, int animationStyle, int repeatCount) {
        if (isProgressValid(start) || isProgressValid(end)) {
            return;
        }
        if (mAnimator != null) {
            mAnimator.cancel();
        }
        initAnimator(start, end, animationStyle, repeatCount);
        showPainterActually = showPainter;
        mAnimator.start();
    }

    protected abstract void drawPath(float progress);

    protected abstract void initPath();

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (showPainterActually) {
            canvas.drawPath(mPaintPath, mPaint);
        }

        canvas.drawPath(mDst, mDrawPaint);

    }

    public void setShowPainterActually(boolean showPainterActually) {
        this.showPainterActually = showPainterActually;
    }

    public void setAnimatorListener(PathAnimatorListener animatorListener) {
        mAnimatorListener = animatorListener;
        mAnimatorListener.setTarget(this);
        if (mAnimator != null) {
            mAnimator.removeAllListeners();
            mAnimator.addListener(mAnimatorListener);
        }
    }

    protected void checkFill(float progress) {
        if (progress != 1 && mShouldFill) {
            mShouldFill = false;
            mDrawPaint.setStyle(Paint.Style.STROKE);
        }
    }

    boolean isProgressValid(float progress) {
        if (progress < 0 || progress > 1) {
            try {
                throw new Exception("Progress is invalid!");
            } catch (Exception e) {
                e.printStackTrace();
                return true;
            }
        }
        return false;
    }
}
