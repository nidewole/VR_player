package com.asha.md360player4android;

import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by yptian on 2017/3/17.
 */

public class DoubleTapEvent  implements GestureDetector.OnGestureListener{
    public DoubleTapEvent() {

    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }


}
