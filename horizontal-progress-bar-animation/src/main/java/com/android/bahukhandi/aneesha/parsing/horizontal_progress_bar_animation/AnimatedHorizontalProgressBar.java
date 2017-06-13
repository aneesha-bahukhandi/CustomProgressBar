package com.android.bahukhandi.aneesha.parsing.horizontal_progress_bar_animation;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.Px;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

public class AnimatedHorizontalProgressBar extends ProgressBar{

    private List<Integer> mColors = new ArrayList<>();
    private List<Integer> mIntervals = new ArrayList<>();
    private int mBackgroundColor;
    @Px private int mProgressBarRadius;
    private int mPrevColor;
    private long mAnimationDuration;

    private ValueAnimator mProgressAnimator;
    private ValueAnimator mMaxAnimator;
    private boolean isAnimating = false;

    private static final int DEFAULT_REFERENCE_ID = -1;
    private static final int DEFAULT_ANIMATION_DURATION = 2000;
    private static final float DEFAULT_CORNER_RADIUS = 0F;

    public AnimatedHorizontalProgressBar(Context context) {
        this(context, null, 0);
    }

    public AnimatedHorizontalProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimatedHorizontalProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initStyleableProperties(context, attrs);

        mProgressAnimator = new ValueAnimator();
        mMaxAnimator = new ValueAnimator();

        initValueAnimator(mProgressAnimator, true);
        initValueAnimator(mMaxAnimator, false);

        initDrawables();
    }

    private void initStyleableProperties(Context context, AttributeSet attrs){
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.animated_horizontal_progress_bar);

        int progressColorsId = typedArray.getResourceId(R.styleable.animated_horizontal_progress_bar_progressColors, DEFAULT_REFERENCE_ID);
        if (progressColorsId != DEFAULT_REFERENCE_ID){
            int[] progressColors = context.getResources().getIntArray(progressColorsId);
            for (int color : progressColors){
                mColors.add(color);
            }
        } else {
            mColors.add(ContextCompat.getColor(context, R.color.colorProgress));
        }

        int intervalsId = typedArray.getResourceId(R.styleable.animated_horizontal_progress_bar_intervalSlotEndPoints, DEFAULT_REFERENCE_ID);
        if (intervalsId != DEFAULT_REFERENCE_ID){
            int[] intervalSlots = context.getResources().getIntArray(intervalsId);
            for (int color : intervalSlots){
                mIntervals.add(color);
            }
        } else {
            mIntervals.add(getMax());
        }

        mBackgroundColor = typedArray.getColor(R.styleable.animated_horizontal_progress_bar_backgroundColor,
                ContextCompat.getColor(context, R.color.colorBackground));

        mProgressBarRadius = (int) typedArray.getDimension(R.styleable.animated_horizontal_progress_bar_cornerRadius,
                DEFAULT_CORNER_RADIUS);

        mAnimationDuration = (long)typedArray.getInteger(R.styleable.animated_horizontal_progress_bar_animationDuration,
                DEFAULT_ANIMATION_DURATION);

        typedArray.recycle();
    }

    private void initValueAnimator(ValueAnimator animator, final boolean isProgress){
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (isProgress) {
                    if (mPrevColor != getColor((int)animation.getAnimatedValue())){
                        initDrawables();
                    }
                    AnimatedHorizontalProgressBar.super.setProgress((Integer) animation.getAnimatedValue());
                } else {
                    AnimatedHorizontalProgressBar.super.setMax((Integer) animation.getAnimatedValue());
                }
            }
        });

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimating = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        animator.setDuration(mAnimationDuration);
    }

    private void initDrawables(){
        ClipDrawable progressBar;
        Drawable[] progressBarDrawables;
        int color = getColor(getProgress());
        this.mPrevColor = color;
        progressBar = new ClipDrawable (createGradientDrawable(color), Gravity.START, ClipDrawable.HORIZONTAL);
        progressBarDrawables = new Drawable[] {createGradientDrawable(mBackgroundColor), progressBar};

        LayerDrawable progressLayerDrawable = new LayerDrawable(progressBarDrawables);
        progressLayerDrawable.setId(0, android.R.id.background);
        progressLayerDrawable.setId(1, android.R.id.progress);

        super.setProgressDrawable(progressLayerDrawable);
    }

    private GradientDrawable createGradientDrawable(int color){
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setColor(color);
        gradientDrawable.setCornerRadius(mProgressBarRadius);
        return gradientDrawable;
    }

    private int getColor(int val){
        if (mIntervals != null) {
            for (int i = 0; i < mIntervals.size(); i++) {
                if (val <= mIntervals.get(i)) {
                    return mColors.get(i);
                }
            }
        }
        return mColors.get(mColors.size() > 0 ? mColors.size() - 1 : 0);
    }

    public void setProgressWithAnimation(int progress){
        if (!isAnimating){
            if(mProgressAnimator == null){
                mProgressAnimator = new ValueAnimator();
                initValueAnimator(mProgressAnimator, true);
            }
            mProgressAnimator.setIntValues(getProgress(), progress);
            mProgressAnimator.start();
        }
    }

    public void setMaximumWithAnimation(int max){
        if (!isAnimating){
            if(mMaxAnimator == null){
                mMaxAnimator = new ValueAnimator();
                initValueAnimator(mMaxAnimator, false);
            }
            mMaxAnimator.setIntValues(getMax(), max);
            mMaxAnimator.start();
        }
    }

    public void setAnimationDuration(long duration){
        if (mMaxAnimator != null){
            mMaxAnimator.setDuration(duration);
        }
        if (mProgressAnimator != null){
            mProgressAnimator.setDuration(duration);
        }
    }

    public void setColorScheme(List<Integer> colors, List<Integer> intervals) {
        if (colors != null && intervals != null && !colors.isEmpty() && colors.size() == intervals.size()) {
            this.mColors.clear();
            this.mColors.addAll(colors);
            this.mIntervals.clear();
            this.mIntervals.addAll(intervals);
        } else {
            throw new IllegalArgumentException("Color list and Intervals list should have at least one value. " +
                    "These lists should also have the same number of elements.");
        }
        invalidate();
    }

    public void setBackgroundColor(int backgroundColor) {
        this.mBackgroundColor = backgroundColor;
        invalidate();
    }

    public void setProgressBarRadius(int barRadius) {
        this.mProgressBarRadius = barRadius;
        invalidate();
    }

    @Override
    public synchronized void setProgress(int progress) {
        if (!isAnimating) {
            super.setProgress(progress);
        }
    }

    @Override
    public synchronized void setMax(int max) {
        if (!isAnimating) {
            super.setMax(max);
        }
    }

}