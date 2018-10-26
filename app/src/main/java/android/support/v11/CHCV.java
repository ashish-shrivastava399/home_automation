package android.support.v11;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;

@CoordinatorLayout.DefaultBehavior(HideBehavior.class)
public class CHCV extends CardView {
    public CHCV(Context context) {
        super(context);
    }

    public CHCV(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CHCV(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
