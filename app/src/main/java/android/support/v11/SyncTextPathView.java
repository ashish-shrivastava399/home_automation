package android.support.v11;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.util.AttributeSet;


public class SyncTextPathView extends TextPathView {
    private PenPainter mPainter;
    private float mLengthSum = 0;

    public SyncTextPathView(Context context) {
        super(context);
        init();
    }

    public SyncTextPathView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SyncTextPathView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        setLayerType(LAYER_TYPE_SOFTWARE, null);


        initPaint();


        initPath();


        if (mAutoStart) {
            startAnimation(0, 1);
        }


        if (mShowInStart) {
            drawPath(1);
        }
    }

    @Override
    protected void initPath() {
        mDst.reset();
        mFontPath.reset();

        mTextWidth = Layout.getDesiredWidth(mText, mTextPaint);
        Paint.FontMetrics metrics = mTextPaint.getFontMetrics();
        mTextHeight = metrics.bottom - metrics.top;

        mTextPaint.getTextPath(mText, 0, mText.length(), 0, -metrics.ascent, mFontPath);
        mPathMeasure.setPath(mFontPath, false);
        mLengthSum = mPathMeasure.getLength();

        while (mPathMeasure.nextContour()) {
            mLengthSum += mPathMeasure.getLength();
        }

    }


    @Override
    protected void drawPath(float progress) {
        if (isProgressValid(progress)) {
            return;
        }
        mAnimatorValue = progress;
        mStop = mLengthSum * progress;

        checkFill(progress);


        mPathMeasure.setPath(mFontPath, false);
        mDst.reset();
        mPaintPath.reset();


        while (mStop > mPathMeasure.getLength()) {
            mStop = mStop - mPathMeasure.getLength();
            mPathMeasure.getSegment(0, mPathMeasure.getLength(), mDst, true);
            if (!mPathMeasure.nextContour()) {
                break;
            }
        }
        mPathMeasure.getSegment(0, mStop, mDst, true);


        if (showPainterActually) {
            mPathMeasure.getPosTan(mStop, mCurPos, null);
            drawPaintPath(mCurPos[0], mCurPos[1], mPaintPath);
        }


        postInvalidate();
    }


    private void drawPaintPath(float x, float y, Path paintPath) {
        if (mPainter != null) {
            mPainter.onDrawPaintPath(x, y, paintPath);
        }
    }

    @Override
    public void startAnimation(float start, float end, int animationStyle, int repeatCount) {
        super.startAnimation(start, end, animationStyle, repeatCount);
        if (mPainter != null) {
            mPainter.onStartAnimation();
        }
    }


    public void setPathPainter(PenPainter listener) {
        this.mPainter = listener;
    }

}
