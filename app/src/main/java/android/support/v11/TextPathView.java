package android.support.v11;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.iot.app.R;


public abstract class TextPathView extends PathView {

    TextPaint mTextPaint;

    Path mFontPath = new Path();

    float mTextWidth = 0;
    float mTextHeight = 0;

    String mText;
    boolean mAutoStart = false;
    boolean mShowInStart = false;
    private int mTextSize = 108;
    private boolean mTextInCenter = false;
    private boolean mFillColor = false;

    private Typeface mTypeface = null;

    private RectF mPathBounds = new RectF();

    private boolean wrapWidth = false, wrapHeight = false;


    public TextPathView(Context context) {
        super(context);
    }

    public TextPathView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
    }

    public TextPathView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
    }

    @Override
    void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextPathView);
        mText = typedArray.getString(R.styleable.TextPathView_text);
        if (mText == null) {
            mText = "Soul Escort";
        }

//        Log.e("initAttr","a1 "+R.styleable.TextPathView_textSize+" mTextSize "+typedArray.getDimension(R.styleable.TextPathView_textSize, mTextSize));
//        mTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,R.styleable.TextPathView_textSize, getResources().getDisplayMetrics());
//        mTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, typedArray.getDimension(R.styleable.TextPathView_textSize, mTextSize), getResources().getDisplayMetrics());

        mTextSize = typedArray.getDimensionPixelSize(R.styleable.TextPathView_textSize, mTextSize);

//        Log.e("initAttr","mTextSize "+mTextSize);

        mDuration = typedArray.getInteger(R.styleable.TextPathView_duration, mDuration);
        showPainter = typedArray.getBoolean(R.styleable.TextPathView_showPainter, showPainter);
        showPainterActually = typedArray.getBoolean(R.styleable.TextPathView_showPainterActually, showPainterActually);
        mPathStrokeWidth = typedArray.getDimensionPixelOffset(R.styleable.TextPathView_pathStrokeWidth, mPathStrokeWidth);
        mTextStrokeColor = typedArray.getColor(R.styleable.TextPathView_pathStrokeColor, mTextStrokeColor);
        mPaintStrokeWidth = typedArray.getDimensionPixelOffset(R.styleable.TextPathView_paintStrokeWidth, mPaintStrokeWidth);
        mPaintStrokeColor = typedArray.getColor(R.styleable.TextPathView_paintStrokeColor, mPaintStrokeColor);
        mAutoStart = typedArray.getBoolean(R.styleable.TextPathView_autoStart, mAutoStart);
        mTextInCenter = typedArray.getBoolean(R.styleable.TextPathView_textInCenter, mTextInCenter);
        mShowInStart = typedArray.getBoolean(R.styleable.TextPathView_showInStart, mShowInStart);
        mRepeatStyle = typedArray.getInt(R.styleable.TextPathView_repeat, 0);
        typedArray.recycle();
    }


    @Override
    void initPaint() {
        super.initPaint();
        mTextPaint = new TextPaint();
//        TextPaint textPaint = new TextPaint(mTextPaint);
//        mTextPaint.setTypeface();

        mTextPaint.setTextSize(mTextSize);

        if (mTextInCenter) {
            mDrawPaint.setTextAlign(Paint.Align.CENTER);
        }
        if (mTypeface != null) {
            mTextPaint.setTypeface(mTypeface);
        }

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

//        int hSpeSize = MeasureSpec.getSize(heightMeasureSpec);
//        int hSpeMode = MeasureSpec.getMode(heightMeasureSpec);
//        int wSpeSize = MeasureSpec.getSize(widthMeasureSpec);
//        int wSpeMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);

//        mTextWidth = TextUtil.getTextWidth(mTextPaint,mText);

        if (mTextWidth > width) {
            handleNewLines(width);
            mTextWidth = width;
        }

        if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            if (mTextWidth <= width) {
                width = (int) mTextWidth + 1;
            } else {
                handleNewLines(width);
                mTextWidth = width;
            }
            wrapWidth = true;
        } else {
            wrapWidth = false;
        }

        if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            height = (int) mTextHeight + 1;
            wrapHeight = true;
        } else {
            wrapHeight = false;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mFontPath.computeBounds(mPathBounds, true);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (mTextInCenter) {
            if (!wrapWidth) {
                canvas.translate((getWidth() - mPathBounds.width()) / 2, 0);
            }
            if (!wrapHeight) {
                canvas.translate(0, (getHeight() - mPathBounds.height()) / 2);
            }
        }

        if (showPainterActually) {
            canvas.drawPath(mPaintPath, mPaint);
        }

        if (mAnimatorValue < 1) {
            canvas.drawPath(mDst, mDrawPaint);
        } else {
            canvas.drawPath(mFontPath, mDrawPaint);
        }

    }


    private void handleNewLines(float outerWidth) {
        float[] widths = new float[mText.length()];
        mTextPaint.getTextWidths(mText, widths);

        float widthSum = 0;
        Paint.FontMetrics metrics = mTextPaint.getFontMetrics();
        float ascent = -mTextPaint.getFontMetrics().ascent;
        float height = metrics.descent + ascent;
        int start = 0, count = 0;
        mFontPath.reset();
        for (int i = 0; i < widths.length; i++) {
            float width = widths[i];
            widthSum += width;
//            Log.d(TAG, "handleNewLines: width " + width + " i: " + i);
            if (widthSum > outerWidth) {
                String text = mText.substring(start, i);
                widthSum = width;
                start = i;
                Path path = new Path();
                mTextPaint.getTextPath(text, 0, text.length(), 0, ascent, path);
                mFontPath.addPath(path, 0, height * count);
//                Log.d(TAG, "handleNewLines text: " + text);
                count++;
            }
        }
        if (start < widths.length) {
            String text = mText.substring(start, widths.length);
//            Log.d(TAG, "handleNewLines text: " + text);
            Path path = new Path();
            mTextPaint.getTextPath(text, 0, text.length(), 0, ascent, path);
            mFontPath.addPath(path, 0, height * count);
        }
        mTextHeight = height * ++count;
    }

    public void showFillColorText() {
        mFillColor = true;
        mDrawPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        drawPath(1);
    }

    protected void checkFill(float progress) {
        if (progress != 1 && mFillColor) {
            mFillColor = false;
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
