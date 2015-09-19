package com.liuqiang.xatulibrary.common;

import android.app.ActionBar;
import android.content.Context;
import android.view.animation.Animation;


public class ShowHideOnScroll extends ScrollDetector implements Animation.AnimationListener {

    private final ActionBar actionBar;
    private final Context context;

    public ShowHideOnScroll(Context context, ActionBar actionBar) {
        super(context);
        this.context = context;
        this.actionBar = actionBar;
    }

    @Override
    public void onScrollDown() {
        if (!areViewsVisible()) {
            actionBar.show();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onScrollUp() {
        if (areViewsVisible()) {

            actionBar.hide();
        }
    }

    private boolean areViewsVisible() {
        return actionBar.isShowing();
    }


    @Override
    public void onAnimationStart(Animation animation) {
        // Nada
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAnimationEnd(Animation animation) {
        setIgnore(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAnimationRepeat(Animation animation) {
        // Nada
    }
}