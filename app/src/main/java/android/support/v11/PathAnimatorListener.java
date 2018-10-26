package android.support.v11;

import android.animation.Animator;


public class PathAnimatorListener implements Animator.AnimatorListener {
    protected boolean isCancel = false;
    private PathView mPathView;

    void setTarget(PathView pathView) {
        this.mPathView = pathView;
    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    @Override
    public void onAnimationStart(Animator animation) {
        isCancel = false;
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        mPathView.setShowPainterActually(false);
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        isCancel = true;
    }
}
