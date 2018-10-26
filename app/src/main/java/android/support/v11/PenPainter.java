package android.support.v11;

import android.graphics.Path;
import android.util.Log;

public class PenPainter {
    private static final float r_nib = 30, r_pen = 100;

    public void onDrawPaintPath(float x, float y, Path paintPath) {
        paintPath.addCircle(x, y, 3, Path.Direction.CCW);
        paintPath.moveTo(x, y);
        paintPath.lineTo(x + r_nib, y);
        paintPath.lineTo(x, y - r_nib);
        paintPath.lineTo(x, y);
        paintPath.moveTo(x + r_nib, y);
        paintPath.lineTo(x + r_nib + r_pen, y - r_pen);
        paintPath.lineTo(x + r_pen, y - r_pen - r_nib);
        paintPath.lineTo(x, y - r_nib);
    }

    public void onStartAnimation() {
        Log.e("PenPainter", "onStartAnimation");
    }
}
