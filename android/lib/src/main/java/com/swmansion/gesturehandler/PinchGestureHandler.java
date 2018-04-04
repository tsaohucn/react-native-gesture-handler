package com.swmansion.gesturehandler;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewConfiguration;
import android.util.Log;
import com.facebook.react.common.ReactConstants;

public class PinchGestureHandler extends GestureHandler<PinchGestureHandler> {

  private ScaleGestureDetector mScaleGestureDetector;
  private double mLastScaleFactor;
  private int numberOfTouches;

  private ScaleGestureDetector.OnScaleGestureListener mGestureListener =
          new ScaleGestureDetector.OnScaleGestureListener() {

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
      mLastScaleFactor = detector.getScaleFactor();
      if (getState() == STATE_BEGAN) {
        activate();
      }
      return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
      detector.setQuickScaleEnabled(false);
      detector.setStylusScaleEnabled(false);
      return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
      // ScaleGestureDetector thinks that when fingers are 27mm away that's a sufficiently good
      // reason to trigger this method giving us no other choice but to ignore it completely.
    }
  };

  public PinchGestureHandler() {
    setShouldCancelWhenOutside(false);
  }

  @Override
  protected void onHandle(MotionEvent event) {
    if (getState() == STATE_UNDETERMINED) {
      Context context = getView().getContext();
      mLastScaleFactor = 1f;
      mScaleGestureDetector = new ScaleGestureDetector(context, mGestureListener);
      ViewConfiguration configuration = ViewConfiguration.get(context);

      begin();
    }

    if (mScaleGestureDetector != null) {
      mScaleGestureDetector.onTouchEvent(event);
    }

    int activePointers = event.getPointerCount();
    if (event.getActionMasked() == MotionEvent.ACTION_POINTER_UP) {
      activePointers -= 1;
    }

    if (event.getActionMasked() == MotionEvent.ACTION_UP) {
      end();
    } else if (getState() == STATE_ACTIVE && activePointers < 2) {
      mLastScaleFactor = 1f;
    }
    numberOfTouches = activePointers;
  }

  @Override
  protected void onReset() {
    mScaleGestureDetector = null;
    mLastScaleFactor = 1f;
  }

  public int getNumberOfTouches() {
    return numberOfTouches;
  }

  public double getScale() {
    return mLastScaleFactor;
  }

  public float getFocalPointX() {
    if (mScaleGestureDetector == null) {
      return Float.NaN;
    }
    return mScaleGestureDetector.getFocusX();
  }

  public float getFocalPointY() {
    if (mScaleGestureDetector == null) {
      return Float.NaN;
    }
    return mScaleGestureDetector.getFocusY();
  }
}
