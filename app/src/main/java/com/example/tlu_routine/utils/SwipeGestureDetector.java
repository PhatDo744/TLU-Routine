package com.example.tlu_routine.utils;

import android.view.GestureDetector;
import android.view.MotionEvent;

public class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {

    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;

    public interface OnSwipeListener {
        void onSwipeLeft();

        void onSwipeRight();
    }

    private OnSwipeListener listener;

    public SwipeGestureDetector(OnSwipeListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e1 == null || e2 == null)
            return false;

        float diffY = e2.getY() - e1.getY();
        float diffX = e2.getX() - e1.getX();

        if (Math.abs(diffX) > Math.abs(diffY)) {
            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    // Swipe right - previous week
                    listener.onSwipeRight();
                } else {
                    // Swipe left - next week
                    listener.onSwipeLeft();
                }
                return true;
            }
        }
        return false;
    }
}