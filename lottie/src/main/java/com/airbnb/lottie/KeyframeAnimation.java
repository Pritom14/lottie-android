package com.airbnb.lottie;

import android.util.Log;

import java.util.List;

public abstract class KeyframeAnimation<T> extends BaseKeyframeAnimation<T, T> {
  private static final String TAG = KeyframeAnimation.class.getSimpleName();

  KeyframeAnimation(List<? extends Keyframe<T>> keyframes) {
    super(keyframes);
    if (L.DBG) {
      Log.d(TAG, "Created with " + keyframes.size() + " keyframes");
    }
  }
}
